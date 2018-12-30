package com.example.cesar.agrosmart.apiBody;

public class addFincaBody {
    private String nombre;
    private String telefono;
    private String direccion;
    private String jwt;

    public addFincaBody(String nombre, String telefono, String direccion, String jwt) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.direccion = direccion;
        this.jwt = jwt;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
