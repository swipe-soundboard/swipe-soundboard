package com.kimballleavitt.swipe_soundboard.util;

import com.google.gson.*;

public class Serializer {
    private static final Serializer ourInstance = new Serializer();
    private Gson gson;

    public static Serializer getInstance() {
        return ourInstance;
    }

    private Serializer() {
        gson = new Gson();
    }

    public String toJSON(Object toSerialize){
        return gson.toJson(toSerialize);
    }

    public Object fromJSON(String JSON, Class klass){
        return gson.fromJson(JSON, klass);
    }
}
