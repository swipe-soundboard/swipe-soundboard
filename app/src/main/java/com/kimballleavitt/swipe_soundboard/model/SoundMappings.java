package com.kimballleavitt.swipe_soundboard.model;

import android.content.Context;
import android.net.Uri;

import com.andrognito.patternlockview.PatternLockView;
import com.kimballleavitt.swipe_soundboard.exception.MappingExistsException;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SoundMappings {
    private static SoundMappings soundMappings = new SoundMappings();
    private PatternSoundMappings patternsToSounds = new PatternSoundMappings();

    private SoundMappings() {
        this.initializeMappings();
    }

    public static SoundMappings getInstance() {
        return soundMappings;
    }

    public int size() {
        return patternsToSounds.size();
    }

    public List<StoragePattern> keys() {
        return new ArrayList<>(patternsToSounds.keySet());
    }

    public List<Uri> values() {
        return new ArrayList<>(patternsToSounds.values());
    }

    private void initializeMappings(){
        File file = new File("config.json");
        if (file.exists() && !file.isDirectory()){
            try {
                System.out.println("config.json exists");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            System.out.println("config.json does not exist");
        }
        System.out.println(file.getAbsolutePath());
    }

    public void saveMappings(){
        File file = new File("config.json");
        try {
            if (file.createNewFile()) {
                System.out.println("Creating config.json");
            }
            else {
                System.out.println("writing over old file");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //Returns the URI of the sound bite corresponding to the pattern swiped
    public Uri getSoundPath(List<PatternLockView.Dot> pattern) throws IndexOutOfBoundsException {
        if (!patternsToSounds.containsKey(new StoragePattern(pattern))) {
            throw new IndexOutOfBoundsException("Key Not Found");
        } else {
            return patternsToSounds.get(new StoragePattern(pattern));
        }
    }
    //Remove the mapping from our list
    public void removeMapping(List<PatternLockView.Dot> pattern) {
        try {
            patternsToSounds.remove(new StoragePattern(pattern));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Check if our list contains a mapping for the pattern that was swiped
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
        addMapping(pattern, Uri.parse("android.resource://" + c.getPackageName() + '/' + resourceID ), replace);
    }
    //Check if the mapping exists, add it if it does not
    public void addMapping(List<PatternLockView.Dot> pattern, Uri fileUri, boolean replace) throws MappingExistsException {
        assert fileUri != null;
        boolean exists;
        Uri existingUri = null;
        //If the URI returned by getSoundPath is not null, then that pattern already exists in our
        //map, and therefore should not be changed unless the user wants to replace it
        try {
            existingUri = getSoundPath(pattern);
            exists = existingUri != null;
        } catch (IndexOutOfBoundsException e) {
            exists = false;
        }
        if (exists && !replace) {
            throw new MappingExistsException("Pattern exists already", existingUri);
        }
        patternsToSounds.put(new StoragePattern(pattern), fileUri);
        this.saveMappings();
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
