package com.example.soring.bandccare_banduser.Retrofit.Model;

import com.google.gson.annotations.SerializedName;

public class Response_MaxIndex {
    @SerializedName("max(id)")
    public int max;

    public Response_MaxIndex(int max) {
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
