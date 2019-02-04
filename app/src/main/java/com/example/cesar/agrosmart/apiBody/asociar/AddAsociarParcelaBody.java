package com.example.cesar.agrosmart.apiBody.asociar;

public class AddAsociarParcelaBody {
    private String idfinca;
    private String idparcela;
    private String jwt;

    public AddAsociarParcelaBody(String idfinca, String idparcela, String jwt) {
        this.idfinca = idfinca;
        this.idparcela = idparcela;
        this.jwt = jwt;
    }

    public String getIdfinca() {
        return idfinca;
    }

    public void setIdfinca(String idfinca) {
        this.idfinca = idfinca;
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
