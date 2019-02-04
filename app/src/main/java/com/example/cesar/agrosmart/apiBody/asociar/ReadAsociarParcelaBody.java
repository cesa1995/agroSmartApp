package com.example.cesar.agrosmart.apiBody.asociar;

public class ReadAsociarParcelaBody {
    private String idfinca;
    private String jwt;

    public ReadAsociarParcelaBody(String idfinca, String jwt) {
        this.idfinca = idfinca;
        this.jwt = jwt;
    }

    public String getIdfinca() {
        return idfinca;
    }

    public void setIdfinca(String idfinca) {
        this.idfinca = idfinca;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
