package com.berhasil.imgretrofit;

import com.google.gson.annotations.SerializedName;

public class imageClass {
    @SerializedName("title")
    private String Title;
    @SerializedName("image")
    private String Image;
    @SerializedName("response")
    private String response;

    public String getResponse() {
        return response;
    }
}
