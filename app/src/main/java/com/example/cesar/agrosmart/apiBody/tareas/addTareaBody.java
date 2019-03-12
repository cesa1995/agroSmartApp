package com.example.cesar.agrosmart.apiBody.tareas;

public class addTareaBody {
    private String tarea;
    private String idparcela;
    private String inicio;
    private String fin;
    private String estado;
    private String channel;
    private String jwt;

    public addTareaBody(String tarea, String idparcela, String inicio, String fin, String estado, String channel, String jwt) {
        this.tarea = tarea;
        this.idparcela = idparcela;
        this.inicio = inicio;
        this.fin = fin;
        this.estado = estado;
        this.channel = channel;
        this.jwt = jwt;
    }

    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public String getIdparcela() {
        return idparcela;
    }

    public void setIdparcela(String idparcela) {
        this.idparcela = idparcela;
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
