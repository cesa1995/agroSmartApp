package com.example.cesar.agrosmart.apiBody.comentarios;

public class deleteComentarioBody {
    private String idparcela;
    private String jwt;

    public deleteComentarioBody(String idparcela, String jwt) {
        this.idparcela = idparcela;
        this.jwt = jwt;
    }

    public String getIdparcela() {
        return idparcela;
    }

    public void setIdparcela(String idparcela) {
        this.idparcela = idparcela;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
