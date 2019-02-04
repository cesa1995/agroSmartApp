package com.example.cesar.agrosmart.apiBody.asociar;

public class AddAsociarUsuarioANDEquipoBody {
    private String idfp;
    private String ide;
    private String estado;
    private String jwt;

    public AddAsociarUsuarioANDEquipoBody(String idfp, String ide, String estado, String jwt) {
        this.idfp = idfp;
        this.ide = ide;
        this.estado = estado;
        this.jwt = jwt;
    }

    public String getIdfp() {
        return idfp;
    }

    public void setIdfp(String idfp) {
        this.idfp = idfp;
    }

    public String getIde() {
        return ide;
    }

    public void setIde(String ide) {
        this.ide = ide;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
