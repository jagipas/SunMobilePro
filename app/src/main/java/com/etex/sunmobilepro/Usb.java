package com.etex.sunmobilepro;

/**
 * Created by javi on 17/07/15.
 */
public class Usb {
    private String idUsb;

    private int estado;
    private int tiempo;   // dos horas por defecto

    public Usb(){}

    public Usb(String idUsb) {
        this.idUsb = idUsb;

    }

    public Usb(String idUsb, int estado) {
        this.idUsb = idUsb;
        this.estado = estado;
        this.tiempo = 0;
    }

    public String getIdUsb() {
        return idUsb;
    }

    public void setIdUsb(String idUsb) {
        this.idUsb = idUsb;
    }


    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }
}
