package com.example.cesar.agrosmart.apiBody.add;

public class addUsuariosBody {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String nivel;
    private String jwt;

    public addUsuariosBody(String nombre, String apellido, String email, String password, String nivel, String jwt){
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.nivel = nivel;
        this.jwt = jwt;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
