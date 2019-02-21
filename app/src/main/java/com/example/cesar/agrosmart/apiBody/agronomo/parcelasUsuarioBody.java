package com.example.cesar.agrosmart.apiBody.agronomo;

public class parcelasUsuarioBody {
    private String idusuario;
    private String idfinca;
    private String jwt;

    public parcelasUsuarioBody(String idusuario, String idfinca, String jwt) {
        this.idusuario = idusuario;
        this.idfinca = idfinca;
        this.jwt = jwt;
    }

    public String getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(String idusuario) {
        this.idusuario = idusuario;
    }

    public String getIdfinca() {
        return idfinca;
    }

    public void setIdfinca(String idfinca) {
        this.idfinca = idfinca;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
