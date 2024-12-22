package com.daicy.javafxeditor;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;


public class StyledCodeEditor extends CodeArea {

    private static final Pattern LEADING_SPACE_PATTERN = Pattern.compile("^\\s*");

    private Pattern syntaxPattern = Pattern.compile("");
    private Map<String, Style> styles = new HashMap<>();
    private int descriptorKeyCounter = 0;

    private String tabReplacementString = null;

    public StyledCodeEditor(Duration stylingSleepDuration) {
        setParagraphGraphicFactory(LineNumberFactory.get(this));

        new StyleDaemon(this, stylingSleepDuration).start();
    }

    public StyledCodeEditor() {
        this(Duration.ofMillis(334));
    }

    /**
     * Binds a pattern to a CSS class.
     * <p>
     * Consequently, such class will style code portions matching the pattern.
     * Patterns are applied in registration (FIFO) order, stopping when a pattern matches.
     *
     * @param cssClass     the CSS class used to style text matching the pattern
     * @param regexPattern a regular expression pattern used to match text in the editor
     */
    public void addPattern(String cssClass, String regexPattern) {
        String key = "S" + descriptorKeyCounter;
        descriptorKeyCounter++;

        Style style = new Style(cssClass, regexPattern);
        styles.put(key, style);

        updateSyntaxPattern();
    }

    /**
     * Simplified version of addPattern(), focusing on <em>tokens</em> (for example, keywords)
     *
     * @param cssClass the CSS class used to style the tokens
     * @param tokens   the tokens to style
     */
    public void addTokens(String cssClass, String... tokens) {
        StringBuilder tokensPattern = new StringBuilder();

        for (int i = 0; i < tokens.length; i++) {
            tokensPattern.append("\\b").append(Pattern.quote(tokens[i])).append("\\b");
            if (i < tokens.length - 1) {
                tokensPattern.append("|");
            }
        }

        addPattern(cssClass, tokensPattern.toString());
    }

    private void updateSyntaxPattern() {
        StringBuilder patternStringBuilder = new StringBuilder();

        boolean first = true;
        for (Map.Entry<String, Style> entry : styles.entrySet()) {
            if (!first) {
                patternStringBuilder.append("|");
            }
            patternStringBuilder.append("(?<").append(entry.getKey()).append(">").append(entry.getValue()).append(")");
            first = false;
        }

        String patternString = patternStringBuilder.toString();
        syntaxPattern = Pattern.compile(patternString);
    }
    public void setText(String text) {
        clear();
        replaceText(0, 0, text);
    }

    /**
     * When the user presses ENTER, initial space is added to the new line
     * to keep it aligned with the previous one.
     */
    public void enableIndentedNewline() {
        addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    int currentPosition = getCaretPosition();

                    String previousText = getText(0, currentPosition);
                    int previousNewlineCharPosition = previousText.lastIndexOf('\n');

                    String currentLine = previousText.substring(previousNewlineCharPosition + 1);

                    Matcher leadingSpaceMatcher = LEADING_SPACE_PATTERN.matcher(currentLine);

                    if (leadingSpaceMatcher.find()) {
                        String leadingSpace = leadingSpaceMatcher.group();

                        keyEvent.consume();

                        String totalSpaceToInsert = "\n" + leadingSpace;
                        insertText(currentPosition, totalSpaceToInsert);

                        moveTo(currentPosition + totalSpaceToInsert.length());
                    }
                }
            }
        });
    }

    /**
     * Pressing the "Tab" key will only add the "space" character, for the given number of times
     *
     * @param spaceCharacterCount The number of "space" characters to add in lieu of "\t"
     */
    public void enableDynamicTabs(int spaceCharacterCount) {
        if (spaceCharacterCount < 0) {
            throw new IllegalArgumentException("spaceCharacterCount must be non-negative");
        }

        if (tabReplacementString != null) {
            throw new IllegalStateException("Dynamic tabs already enabled");
        }

        tabReplacementString = " ".repeat(spaceCharacterCount);

        addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.TAB) {
                    keyEvent.consume();

                    int currentPosition = getCaretPosition();

                    insertText(currentPosition, tabReplacementString);

                    moveTo(currentPosition + spaceCharacterCount);
                }
            }
        });
    }

    private boolean stylingEnabled = true;

    /**
     * Stops the background thread employed for styling the editor
     */
    public void stopStyling() {
        stylingEnabled = false;
    }

    private class StyleDaemon extends Thread {
        private final StyledCodeEditor codeEditor;
        private final Duration sleepDuration;
        private final Semaphore guiSemaphore = new Semaphore(0);

        public StyleDaemon(StyledCodeEditor codeEditor, Duration sleepDuration) {
            this.codeEditor = codeEditor;
            this.sleepDuration = sleepDuration;
            setDaemon(true);
        }

        @Override
        public void run() {
            AtomicReference<String> text = new AtomicReference<>("");
            AtomicReference<java.lang.String> latestStyledText = new AtomicReference<>("");;
            while (codeEditor.stylingEnabled) {
                Platform.runLater(() -> {
                    // 更新引用值
                    text.compareAndSet("", codeEditor.getText());
//                    text = codeEditor.getText();
                    guiSemaphore.release();
                });

                try {
                    guiSemaphore.acquire();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (!text.equals(latestStyledText)) {
                    StyleSpans<Collection<String>> styleSpans = computeHighlighting(text.get());

                    Platform.runLater(() -> {
                        try {
                            codeEditor.setStyleSpans(0, styleSpans);
                            // 更新引用值
                            latestStyledText.compareAndSet("", text.get());
//                            latestStyledText = text;
                        } catch (Exception ignored) {
                            // Just do nothing
                        }
                        guiSemaphore.release();
                    });

                    try {
                        guiSemaphore.acquire();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }

                try {
                    Thread.sleep(sleepDuration.toMillis());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private StyleSpans<Collection<String>> computeHighlighting(String text) {
            StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

            Matcher matcher = syntaxPattern.matcher(text);
            int latestMatchEndPosition = 0;

            while (matcher.find()) {
                String styleKey = null;
                for (String key : styles.keySet()) {
                    if (matcher.group(key) != null) {
                        styleKey = key;
                        break;
                    }
                }

                if (styleKey != null) {
                    String styleClass = styles.get(styleKey).getCssClass();

                    spansBuilder.add(Collections.emptyList(), matcher.start() - latestMatchEndPosition);
                    spansBuilder.add(Collections.singletonList(styleClass), matcher.end() - matcher.start());
                    latestMatchEndPosition = matcher.end();
                }
            }
            spansBuilder.add(Collections.emptyList(), text.length() - latestMatchEndPosition);
            return spansBuilder.create();
        }
    }
}
