package com.example.cesar.agrosmart.apiBody.update;

public class updateUsuariopassBody {
    private String id;
    private String pass1;
    private String pass2;
    private String jwt;

    public updateUsuariopassBody(String id, String pass1, String pass2, String jwt) {
        this.id = id;
        this.pass1 = pass1;
        this.pass2 = pass2;
        this.jwt = jwt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPass1() {
        return pass1;
    }

    public void setPass1(String pass1) {
        this.pass1 = pass1;
    }

    public String getPass2() {
        return pass2;
    }

    public void setPass2(String pass2) {
        this.pass2 = pass2;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
