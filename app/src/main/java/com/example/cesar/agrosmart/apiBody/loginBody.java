package com.example.cesar.agrosmart.apiBody;

import com.google.gson.annotations.SerializedName;

public class loginBody {
    @SerializedName("email")
    private String email;
    private String password;

    public loginBody(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
