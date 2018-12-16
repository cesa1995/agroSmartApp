package com.example.cesar.agrosmart.models;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;

public class ApiError {
    private String message;

    public ApiError(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static ApiError fromResponseBody(ResponseBody responseBody) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(responseBody.string(), ApiError.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
