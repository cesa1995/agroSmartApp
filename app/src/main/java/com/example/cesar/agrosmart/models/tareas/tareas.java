package com.example.cesar.agrosmart.models.tareas;

public class tareas {

    private String id;
    private String tarea;
    private String inicio;
    private String fin;
    private String estado;

    public tareas(String id, String tarea, String inicio, String fin, String estado) {
        this.id = id;
        this.tarea = tarea;
        this.inicio = inicio;
        this.fin = fin;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getFin() {
        return fin;
    }

    public void setFin(String fin) {
        this.fin = fin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
