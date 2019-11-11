package com.kimballleavitt.swipe_soundboard.model;

import android.net.Uri;

import com.andrognito.patternlockview.PatternLockView;
import com.kimballleavitt.swipe_soundboard.exception.MappingExistsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SoundMappings {
    private static SoundMappings soundMappings = new SoundMappings();

    private SoundMappings() {
    }

    public static SoundMappings getInstance() {
        return soundMappings;
    }

    private Map<StoragePattern, Uri> patternsToSounds = new HashMap<>();

    public int size() {
        return patternsToSounds.size();
    }

    public List<StoragePattern> keys() {
        return new ArrayList<>(patternsToSounds.keySet());
    }

    public List<Uri> values() {
        return new ArrayList<>(patternsToSounds.values());
    }

    public Uri getSoundPath(List<PatternLockView.Dot> pattern) throws IndexOutOfBoundsException {
        if (!patternsToSounds.containsKey(new StoragePattern(pattern))) {
            throw new IndexOutOfBoundsException("Key Not Found");
        } else {
            return patternsToSounds.get(new StoragePattern(pattern));
        }
    }

    public void removeMapping(List<PatternLockView.Dot> pattern) {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMapping(List<PatternLockView.Dot> pattern, Uri fileUri, boolean replace) throws MappingExistsException {
        assert fileUri != null;
        boolean exists;
        Uri existingUri = null;
        try {
            existingUri = getSoundPath(pattern);
            exists = existingUri != null;
        } catch (IndexOutOfBoundsException e) {
            exists = false;
        }
        if (exists && !replace) {
            throw new MappingExistsException("Pattern exists", existingUri);
        }
        patternsToSounds.put(new StoragePattern(pattern), fileUri);

    }

    public class StoragePattern {
        private List<Integer> x;
        private List<Integer> y;

        StoragePattern(List<PatternLockView.Dot> originalPatter) {
            x = new ArrayList<>();
            y = new ArrayList<>();
            for (PatternLockView.Dot d : originalPatter) {
                x.add(d.getRow());
                y.add(d.getColumn());
            }
        }

        StoragePattern(List<Integer> x, List<Integer> y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (!(o.getClass() == this.getClass())) {
                return false;
            }
            StoragePattern sp = (StoragePattern) o;
            if (sp.x.size() != this.x.size()) {
                return false;
            }
            for (int i = 0; i < x.size(); i++) {
                if (!sp.x.get(i).equals(this.x.get(i))) {
                    return false;
                }
                if (!sp.y.get(i).equals(this.y.get(i))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 0;
            for (int i = 0; i < x.size(); i++) {
                hash += x.get(i) + 1;
                hash *= 10;
                hash += y.get(i) + 1;
                hash *= 10;
            }
            return hash / 10;
        }
    }
}
