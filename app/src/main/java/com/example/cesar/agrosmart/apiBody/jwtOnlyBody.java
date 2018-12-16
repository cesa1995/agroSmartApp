package com.example.cesar.agrosmart.apiBody;

import com.google.gson.annotations.SerializedName;

public class jwtOnlyBody {
    @SerializedName("jwt")
    private String jwt;

    public jwtOnlyBody(String jwt){
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
