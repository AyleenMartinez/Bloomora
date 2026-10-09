package com.devst.bloomora;

public class Tarea {

    private String asignatura;
    private String nombre;
    private String estado;

    public Tarea(String asignatura, String nombre, String estado) {
        this.asignatura = asignatura;
        this.nombre = nombre;
        this.estado = estado;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}