package com.hafros.bookproj;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class DataModel {

    @Nullable
    String name;
    @Nullable String image;
    @Nullable String url;
    @Nullable String description;

    public DataModel(@NonNull JSONObject jsonObject) throws JSONException {

        this.name = jsonObject.getString("name");
        this.image = jsonObject.getString("image");
        this.url = jsonObject.getString("url");

        if (jsonObject.has("description")){

            this.description = jsonObject.getString("description");

            Log.d("JSON",""+jsonObject.toString());
        }

    }

    public boolean hasDescription(){

        if (description == null || description.length() == 0){
            return false;
        }

        return true;

    }

}
