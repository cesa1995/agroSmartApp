package com.example.cesar.agrosmart.apiBody.tareas;

public class updateTareaBody {
    private String id;
    private String tarea;
    private String inicio;
    private String fin;
    private String channel;
    private String jwt;

    public updateTareaBody(String id, String tarea, String inicio, String fin, String channel, String jwt) {
        this.id = id;
        this.tarea = tarea;
        this.inicio = inicio;
        this.fin = fin;
        this.channel = channel;
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
