package com.kimballleavitt.swipe_soundboard.model;

import android.content.Context;
import android.net.Uri;

import com.andrognito.patternlockview.PatternLockView;
import com.google.common.collect.HashBiMap;
import com.google.gson.reflect.TypeToken;
import com.kimballleavitt.swipe_soundboard.exception.MappingExistsException;
import com.google.common.collect.*;

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
    private Map<StoragePattern, Uri> patternsToSounds = new HashMap<>();
    private BiMap<String, StoragePattern> namePatternBiMap = HashBiMap.create();

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

    public int size() {
        return patternsToSounds.size();
    }

    public List<StoragePattern> pattern2Soundkeys() {
        return new ArrayList<>(patternsToSounds.keySet());
    }

    public List<String> file2PatternKeys() {
        return new ArrayList<>(namePatternBiMap.keySet());
    }

    public List<StoragePattern> file2PatternValues(){
        return new ArrayList<>(namePatternBiMap.values());
    }

    public List<Uri> pattern2SoundValues() {
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
            if (patternsToSounds.containsKey(toRemove)) {
                patternsToSounds.remove(toRemove);
                namePatternBiMap.inverse().remove(toRemove);
            } else {
                System.out.println("That pattern isn't mapped to anything!");
                return;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public boolean contains(List <PatternLockView.Dot> pattern) {
        boolean exists;
        Uri existingUri = null;
        try {
            existingUri = getSoundPath(pattern);
            exists = existingUri != null;
        } catch (IndexOutOfBoundsException e) {
            exists = false;
        }
        return exists;
    }
    public void addMapping(List<PatternLockView.Dot> pattern, Context c, int resourceID, boolean replace) throws  MappingExistsException{
        String filename = c.getResources().getResourceEntryName(resourceID);
        addMapping(pattern, Uri.parse("android.resource://" + c.getPackageName() + '/' + resourceID ),filename, replace);
    }
    public void addMapping(List<PatternLockView.Dot> pattern, Uri fileUri, String filename, boolean replace) throws MappingExistsException {
        assert fileUri != null;
        System.out.println(filename);
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
        else {
            StoragePattern newPattern = new StoragePattern(pattern);
            patternsToSounds.put(newPattern, fileUri);
            namePatternBiMap.put(filename, newPattern);
        }
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
