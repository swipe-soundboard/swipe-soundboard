package com.kimballleavitt.swipe_soundboard.model;

import android.net.Uri;

import com.andrognito.patternlockview.PatternLockView;
import com.google.gson.reflect.TypeToken;
import com.kimballleavitt.swipe_soundboard.exception.MappingExistsException;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.*;

public class SoundMappings {
    private static SoundMappings soundMappings = new SoundMappings();
    private Gson serializer;

    private SoundMappings() {
        serializer = new Gson();
        Type patternSoundMapType = new TypeToken<Map<StoragePattern, Uri>>() {}.getType();
        File config = new File("config.json");
        if(config.exists() && !config.isDirectory()) {
            try {
               String text = new Scanner(config).useDelimiter("\\A").next();
               this.patternsToSounds = serializer.fromJson(text, patternSoundMapType);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            StoragePattern toRemove = new StoragePattern(pattern);
            if(patternsToSounds.containsKey(toRemove)) {
                patternsToSounds.remove(toRemove);
            }
            else {
                System.out.println("That pattern isn't mapped to anything!");
                return;
            }
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

    public void saveMappings() {
        if(serializer == null){
            serializer = new Gson();
        }
        String saveConfig = serializer.toJson(patternsToSounds);
        File config = new File("config.json");
        try {
            Writer writer = new FileWriter(config);
            writer.write(saveConfig);
            writer.close();
        }
        catch(Exception e){
            System.out.println("Something went wrong in the saving of the config file");
            e.printStackTrace();
        }
    }

    public class StoragePattern implements Serializable {
        private List<Integer> x;
        private List<Integer> y;
        private List<PatternLockView.Dot> originalPattern;

        StoragePattern(List<PatternLockView.Dot> originalPattern) {
            x = new ArrayList<>();
            y = new ArrayList<>();
            this.originalPattern = new ArrayList<>(originalPattern);
            for (PatternLockView.Dot d : originalPattern) {
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

        public List<PatternLockView.Dot> getOriginalPattern() {
            return originalPattern;
        }
    }
}
