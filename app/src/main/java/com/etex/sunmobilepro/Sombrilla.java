package com.etex.sunmobilepro;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by javi on 17/07/15.
 */
public class Sombrilla {

    private String id;
    private String nombre;
    private String direccion;
    //
    private List contraseñas = new ArrayList<String>();
    private List<Usb> USBs = new ArrayList<>();

    private int vin;
    private int vbat;

    // Variables para almacenar consumo
    //private int[] consumo;
    private String fechaInicioMoni;

    // constructor
    public Sombrilla(){}

    public Sombrilla(String n, String d){
        nombre=n;
        direccion=d;
        char idc [] = {direccion.charAt(direccion.length()-2), direccion.charAt(direccion.length()-1)};
        id = new String(idc);
        vin = 0;
        vbat=0;
    }

    public Sombrilla(String id, String n, String d){
        this.id = id;
        nombre=n;
        direccion=d;
    }

    public List<Usb> getUSBs() {
        return USBs;
    }

    public void setUSBs(List<Usb> USBs) {
        this.USBs = USBs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List getContraseñas() {
        return contraseñas;
    }

    public void setContraseñas(List contraseñas) {
        this.contraseñas = contraseñas;
    }

    public int getVin() {
        return vin;
    }

    public void setVin(int vin) {
        this.vin = vin;
    }

    public int getVbat() {
        return vbat;
    }

    public void setVbat(int vbat) {
        this.vbat = vbat;
    }

    public String getFechaInicioMoni() {
        return fechaInicioMoni;
    }

    public void setFechaInicioMoni(String fechaInicioMoni) {
        this.fechaInicioMoni = fechaInicioMoni;
    }

    public void addCont(String c){
        if(contraseñas.size()<20){
            contraseñas.add(c);
        }
    }

    public String getCont(int n){
        if(contraseñas.isEmpty()){return null;}
        else return (String)contraseñas.get(n-1);
    }


    public void setEstadoPuertos(int puerto, int estado){
        Usb cambiado = USBs.get(puerto);
        cambiado.setEstado(estado);
        USBs.set(puerto, cambiado);
    }

    public int [] getEstadoPuertos() {
        int [] puertos = {USBs.get(0).getEstado(), USBs.get(1).getEstado(), USBs.get(2).getEstado()};
        return puertos;
    }

    public void setTiempoPuerto(int puerto, int tiempo){
        Usb cambiado = USBs.get(puerto);
        cambiado.setTiempo(tiempo);
        USBs.set(puerto, cambiado);
    }

    public int [] getTiempoPuerto() {
        int [] tiempos = {USBs.get(0).getTiempo(), USBs.get(1).getTiempo(), USBs.get(2).getTiempo()};
        return tiempos;
    }

    public void setTimePuerto (int puerto, char tiempo){

    }


}
