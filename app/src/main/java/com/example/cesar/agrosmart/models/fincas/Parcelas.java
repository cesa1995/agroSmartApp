package com.example.cesar.agrosmart.models.fincas;

public class Parcelas {
    private String id;
    private String nombre;
    private String tipo;
    private String finca;
    private String idfinca;

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

    public String getFinca() {
        return finca;
    }

    public void setFinca(String finca) {
        this.finca = finca;
    }

    public String getIdfinca() {
        return idfinca;
    }

    public void setIdfinca(String idfinca) {
        this.idfinca = idfinca;
    }
}
