package com.example.cesar.agrosmart.apiBody.tareas;

public class updateStateBody {

    private String id;
    private String estado;
    private String channel;
    private String jwt;

    public updateStateBody(String id, String estado, String channel, String jwt) {
        this.id = id;
        this.estado = estado;
        this.channel = channel;
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
