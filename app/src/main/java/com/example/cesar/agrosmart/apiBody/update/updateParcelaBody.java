package com.example.cesar.agrosmart.apiBody.update;

public class updateParcelaBody {
    private String id;
    private String nombre;
    private String tipo;
    private String jwt;

    public updateParcelaBody(String id, String nombre, String tipo, String jwt) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.jwt = jwt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
