package com.hafros.bookproj;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class DataModel {

    @Nullable
    String name;
    @Nullable String image;
    @Nullable String url;

    public DataModel(@NonNull JSONObject jsonObject) throws JSONException {

        this.name = jsonObject.getString("name");
        this.image = jsonObject.getString("image");
        this.url = jsonObject.getString("url");

    }

}
