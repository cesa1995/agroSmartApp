package com.example.cesar.agrosmart.apiBody.tareas;

public class rmBody {

    private String id;
    private String channel;
    private String jwt;

    public rmBody(String id, String channel, String jwt) {
        this.id = id;
        this.channel = channel;
        this.jwt = jwt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
