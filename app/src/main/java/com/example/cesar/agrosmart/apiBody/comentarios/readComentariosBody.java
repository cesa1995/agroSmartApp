package com.example.cesar.agrosmart.apiBody.comentarios;

public class readComentariosBody {

    private String jwt;
    private String idparcela;
    private String intervalo;
    private String pag;

    public readComentariosBody(String jwt, String idparcela, String intervalo, String pag) {
        this.jwt = jwt;
        this.idparcela = idparcela;
        this.intervalo = intervalo;
        this.pag = pag;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getIdparcela() {
        return idparcela;
    }

    public void setIdparcela(String idparcela) {
        this.idparcela = idparcela;
    }

    public String getIntervalo() {
        return intervalo;
    }

    public void setIntervalo(String intervalo) {
        this.intervalo = intervalo;
    }

    public String getPag() {
        return pag;
    }

    public void setPag(String pag) {
        this.pag = pag;
    }
}
