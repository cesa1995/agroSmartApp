package com.example.cesar.agrosmart.apiBody.add;

public class addParcelaBody {
    private String nombre;
    private String tipo;
    private String jwt;

    public addParcelaBody(String nombre, String tipo, String jwt){
        this.nombre = nombre;
        this.tipo = tipo;
        this.jwt = jwt;
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
