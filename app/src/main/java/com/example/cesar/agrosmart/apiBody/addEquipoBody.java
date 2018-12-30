package com.example.cesar.agrosmart.apiBody;

public class addEquipoBody {
    private String nombre;
    private String devicetype;
    private String descripcion;
    private String jwt;

    public addEquipoBody(String nombre, String devicetype, String descripcion, String jwt) {
        this.nombre = nombre;
        this.devicetype = devicetype;
        this.descripcion = descripcion;
        this.jwt=jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
