package com.kimballleavitt.swipe_soundboard.model;

import android.net.Uri;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PatternSoundMappings {

    private Map<SoundMappings.StoragePattern, Uri> patternsToSounds;

    public PatternSoundMappings() {
        patternsToSounds = new HashMap<>();
    }

    public Uri get(SoundMappings.StoragePattern pattern) {
        return patternsToSounds.get(pattern);
    }

    public void remove(SoundMappings.StoragePattern pattern) {
        patternsToSounds.remove(pattern);
    }

    public boolean containsKey(SoundMappings.StoragePattern pattern) {
        return patternsToSounds.containsKey(pattern);
    }

    public Collection<Uri> values() {
        return patternsToSounds.values();
    }

    public Collection<SoundMappings.StoragePattern> keySet() {
        return patternsToSounds.keySet();
    }

    public int size() {
        return patternsToSounds.size();
    }

    public void put(SoundMappings.StoragePattern key, Uri value) {
        patternsToSounds.put(key, value);
    }
}
