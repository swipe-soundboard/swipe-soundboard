package com.kimballleavitt.swipe_soundboard.model;

import android.content.Context;
import android.net.Uri;

import com.andrognito.patternlockview.PatternLockView;
import com.kimballleavitt.swipe_soundboard.exception.MappingExistsException;
import com.kimballleavitt.swipe_soundboard.util.Serializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SoundMappings {
    private static SoundMappings soundMappings = new SoundMappings();
    private PatternSoundMappings patternsToSounds;

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

    // If a config file exists, initialize the mappings from the JSON
    // Otherwise just create a new mappings object
    private void initializeMappings(){
        Serializer serializer = Serializer.getInstance();
        File file = new File("config.json");
        if (file.exists() && !file.isDirectory()){
            try {
                InputStream is = new FileInputStream("config.json");
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();
                StringBuilder sb = new StringBuilder();

                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }

                String JSON = sb.toString();
                patternsToSounds = (PatternSoundMappings) serializer.fromJSON(JSON, PatternSoundMappings.class);
                br.close();
                is.close();
                System.out.println("config.json exists, initializing list from config file");
            }
            catch (IOException e){
                System.out.println("Error while attempting to retrieve config.json");
                e.printStackTrace();
            }
            catch (Exception e) {
                System.out.println("Error during initialization of mappings object");
                e.printStackTrace();
            }
        }
        else {
            patternsToSounds = new PatternSoundMappings();
            System.out.println("config.json does not exist yet");
        }
    }

    public void saveMappings(){
        File file = new File("config.json");
        Serializer serializer = Serializer.getInstance();
        if (file.exists()) {
            try {
                FileOutputStream fos = new FileOutputStream("config.json");
            }
            catch (FileNotFoundException e) {
                System.out.println("Error when attempting to write map into JSON");
                e.printStackTrace();
            }
        }
        else {
            try {
                PrintWriter pw = new PrintWriter("config.json", "UTF-8");
                pw.write(serializer.toJSON(patternsToSounds));
            }
            catch (FileNotFoundException | UnsupportedEncodingException e) {
                System.out.println("Error when attempting to create new config file");
            }
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
