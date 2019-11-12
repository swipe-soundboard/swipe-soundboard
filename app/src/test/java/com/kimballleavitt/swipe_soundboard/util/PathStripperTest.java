package com.kimballleavitt.swipe_soundboard.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class PathStripperTest {
    @Test
    public void testStrip() {
        String s = "/home/someone/has/a/file.mp3";
        String stripped = PathStripper.strip(s);
        assertEquals("file", stripped);

        s = "file.mp3";
        stripped = PathStripper.strip(s);
        assertEquals("file", stripped);

        s = "a/file.mp3";
        stripped = PathStripper.strip(s);
        assertEquals("file", stripped);

        s = "file.a.mp3";
        stripped = PathStripper.strip(s);
        assertEquals("file.a", stripped);

        s = "directory/chicken.a/file.mp3";
        stripped = PathStripper.strip(s);
        assertEquals("file", stripped);
    }
}