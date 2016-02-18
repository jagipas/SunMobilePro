package com.etex.sunmobilepro;

/**
 * Created by jagipas on 21/01/16.
 */
public class Consumo {

    private int dia;
    private int mes;
    private int año;
    private String fecha;

    private long [] tensionHoras;
    private long [] corrienteHoras;

    public Consumo(){}

    public Consumo(int dia){
        this.dia = dia;
    }
    public Consumo(int dia, int mes, int año, long[] tensionHoras, long[] corrienteHoras) {
        this.dia = dia;
        this.mes = mes;
        this.año = año;
        this.tensionHoras = tensionHoras;
        this.corrienteHoras = corrienteHoras;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public long[] getTensionHoras() {
        return tensionHoras;
    }

    public void setTensionHoras(long[] tensionHoras) {
        this.tensionHoras = tensionHoras;
    }

    public long[] getCorrienteHoras() {
        return corrienteHoras;
    }

    public void setCorrienteHoras(long[] corrienteHoras) {
        this.corrienteHoras = corrienteHoras;
    }
}
