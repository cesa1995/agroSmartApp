package com.example.cesar.agrosmart.agrono.comentarios;

public class Mensaje {
    private String mensaje;
    private String usuario;
    private String hora;


    public Mensaje() {
    }

    public Mensaje(String mensaje, String usuario, String hora) {
        this.mensaje = mensaje;
        this.usuario = usuario;
        this.hora = hora;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
