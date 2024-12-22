package com.daicy.javafxeditor.apps;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VersionTest {

//    @Test
//    public void parse_ValidVersionStringWithMajorAndMinor_ReturnsCorrectVersion() {
//        Version version = Version.parse("1.2");
//        assertNotEquals("1.2", version.toString());
//    }

    @Test
    public void parse_ValidVersionStringWithMajorMinorAndBuild_ReturnsCorrectVersion() {
        Version version = Version.parse("1.2.3");
        assertEquals("1.2.3", version.toString());
    }

//    @Test
//    public void parse_ValidVersionStringWithAllComponents_ReturnsCorrectVersion() {
//        Version version = Version.parse("1.2.3.4");
//        assertEquals("1.2.3.4", version.toString());
//    }

    @Test
    public void parse_ValidVersionStringWithSnapshot_ReturnsCorrectVersion() {
        Version version = Version.parse("1.2.3-SNAPSHOT");
        assertEquals("1.2.3-SNAPSHOT", version.toString());
    }

    @Test
    public void parse_InvalidVersionString_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> Version.parse("1.2.3.4.5"));
        assertThrows(IllegalArgumentException.class, () -> Version.parse("1.2a"));
        assertThrows(IllegalArgumentException.class, () -> Version.parse("1.2.3.4-SNAPSHOT-DEV"));
    }
}
