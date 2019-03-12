package com.example.cesar.agrosmart.apiBody.tareas;

public class readTareasBody {
    private String idparcela;
    private String estado;
    private String now;
    private String jwt;

    public readTareasBody(String idparcela, String estado, String now, String jwt) {
        this.idparcela = idparcela;
        this.estado = estado;
        this.now = now;
        this.jwt = jwt;
    }

    public String getIdparcela() {
        return idparcela;
    }

    public void setIdparcela(String idparcela) {
        this.idparcela = idparcela;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
