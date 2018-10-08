package com.hafros.bookproj;

import org.json.JSONException;

public interface NetworkHandler {
    void successHandler(String body) throws JSONException;
    void failHandler(String error);
}
