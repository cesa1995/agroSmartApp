package com.example.cesar.agrosmart.apiBody;

public class deleteBody {
    private String id;
    private String jwt;

    public deleteBody(String id, String jwt) {
        this.id = id;
        this.jwt = jwt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
