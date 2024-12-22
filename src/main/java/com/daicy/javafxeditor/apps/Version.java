package com.daicy.javafxeditor.apps;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author changyadai
 */
public final class Version {

    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)?(-SNAPSHOT)?");

    public static Version parse(String versionString) {
        Matcher matcher = VERSION_PATTERN.matcher(versionString);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Illegal version string: '" + versionString + "'");
        }

        int major = Integer.parseInt(matcher.group(1));
        int minor = Integer.parseInt(matcher.group(2));

        int build = Optional.ofNullable(matcher.group(3)).orElse("0").equals("") ? 0 : Integer.parseInt(matcher.group(3));

        boolean snapshot = matcher.group(4) != null;

        return new Version(major, minor, build, snapshot);
    }

    /**
     * 主版本号，用于表示重大更新或兼容性变化的版本号
     */
    private final int major;

    /**
     * 次版本号，用于表示新增功能或向后兼容的改动
     */
    private final int minor;

    /**
     * 构建版本号，通常用于表示小的更新或修复
     */
    private final int build;


    /**
     * 快照版本标志，true表示当前版本为开发中的快照版本，false表示为稳定版本
     */
    private final boolean snapshot;

    public Version(int major, int minor, int build,boolean snapshot) {
        this.major = major;
        this.minor = minor;
        this.build = build;
        this.snapshot = snapshot;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(major).append(".").append(minor);

        if (build > 0) {
            sb.append(".").append(build);
        }

        if (snapshot) {
            sb.append("-SNAPSHOT");
        }

        return sb.toString();
    }

    // Getters can be added here if needed

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Version)) return false;
        Version version = (Version) o;
        return major == version.major &&
                minor == version.minor &&
                build == version.build &&
                snapshot == version.snapshot;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, build, snapshot);
    }

}
