package com.kimballleavitt.swipe_soundboard.exception;

import android.net.Uri;

public class MappingExistsException extends Throwable {
    private Uri fileUri;
    public MappingExistsException(String pattern) {
    }
    public MappingExistsException(String pattern, Uri fileUri) {
        this.fileUri = fileUri;
    }
    public Uri getFileUri() {
        return fileUri;
    }
}
