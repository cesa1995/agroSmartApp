package com.example.cesar.agrosmart.apiBody.comentarios;

public class createComentariosBody {

    private String jwt;
    private String comentario;
    private String idparcela;
    private String usuario;
    private String fecha;

    public createComentariosBody(String jwt, String comentario, String idparcela, String usuario, String fecha) {
        this.jwt = jwt;
        this.comentario = comentario;
        this.idparcela = idparcela;
        this.usuario = usuario;
        this.fecha = fecha;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getIdparcela() {
        return idparcela;
    }

    public void setIdparcela(String idparcela) {
        this.idparcela = idparcela;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
