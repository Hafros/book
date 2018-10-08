package com.hafros.bookproj;

import java.util.ArrayList;

public interface APIHandler {
    void successHandler(ArrayList<DataModel> items);
    void failHandler(String error);
}