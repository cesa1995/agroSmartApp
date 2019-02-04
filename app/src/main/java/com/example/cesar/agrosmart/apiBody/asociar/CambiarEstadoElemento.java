package com.example.cesar.agrosmart.apiBody.asociar;

public class CambiarEstadoElemento {

    private String id;
    private String estado;
    private String jwt;

    public CambiarEstadoElemento(String id, String estado, String jwt) {
        this.id = id;
        this.estado = estado;
        this.jwt = jwt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
