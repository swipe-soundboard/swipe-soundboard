package com.kimballleavitt.swipe_soundboard.util;

public class PathStripper {
    public static String strip(String path) {
        String[] s = path.split("/");
        return s[s.length - 1].replaceAll("(.*)\\.(.*)", "$1");
    }
}
