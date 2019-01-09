package com.example.cesar.agrosmart.apiBody.update;

public class updateEquipoBody {
    private String id;
    private String nombre;
    private String devicetype;
    private String descripcion;
    private String jwt;

    public updateEquipoBody(String id, String nombre, String devicetype, String descripcion, String jwt) {
        this.id = id;
        this.nombre = nombre;
        this.devicetype = devicetype;
        this.descripcion = descripcion;
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

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
