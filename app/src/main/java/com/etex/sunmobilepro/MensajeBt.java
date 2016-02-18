package com.etex.sunmobilepro;

/**
 * Created by jagipas on 15/01/16.
 */
public class MensajeBt {
    private String idSombrilla;
    private int tipo;
    private byte [] contenido;

    // Id tipos de mensaje a enviar
    //public static final int MESSAGE_ACK = 1;
    public static final int MESSAGE_ESTADO = 1;
    public static final int MESSAGE_HABILITAR_PUERTOS = 2;
    public static final int MESSAGE_TEMPORIZAR = 3;
    public static final int MESSAGE_CONSULTA_CONT = 4;
    public static final int MESSAGE_MOD_CONT = 5;
    public static final int MESSAGE_BORRAR_CONT = 5;
    public static final int MESSAGE_EVIAR_FECHA = 6;
    public static final int MESSAGE_CONSUMO = 7;

    public MensajeBt(String idSombrilla, int tipo, byte [] contenido) {
        this.tipo = tipo;
        this.contenido = contenido;
        this.idSombrilla = idSombrilla;
    }

    public byte [] getContenido() {
        return contenido;
    }

    public void setContenido(byte [] contenido) {
        this.contenido = contenido;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    private char [] getrn(){
        char [] nr = {'\r', '\n' };
        return nr;
    }

    public byte [] getMensajeEnBytes(){

        byte [] mensajeBytes;
        int numBytes;
        char [] rn = getrn();

        if (contenido != null) {
            numBytes = contenido.length+ 4+2;
            mensajeBytes = new byte[numBytes];

            for(int i = 0; i<contenido.length; i++){
                mensajeBytes[i+4]=contenido[i];
            }
        } else {
            numBytes = 4 + 2;
            mensajeBytes = new byte[numBytes];
        }

        // construimos cabecera
        byte [] bytesid = idSombrilla.getBytes();
        mensajeBytes[0]=bytesid[0];
        mensajeBytes[1]=bytesid[1];
        mensajeBytes[2]=(byte)numBytes;
        mensajeBytes[3]=(byte)(tipo+48);

        // final de mensaje
        mensajeBytes[numBytes-2] = (byte)rn[0]; // r
        mensajeBytes[numBytes-1] = (byte)rn[1]; // n

        return mensajeBytes;

    }

    public byte [] getMensajeErrBytes(){

        byte [] mensajeBytes;
        int numBytes = 4+3+2;
        mensajeBytes = new byte[numBytes];
        char [] rn = getrn();

        // construimos cabecera
        byte [] bytesid = idSombrilla.getBytes();
        mensajeBytes[0]=bytesid[0];
        mensajeBytes[1]=bytesid[1];
        mensajeBytes[3]=(byte)numBytes;
        mensajeBytes[4]=(byte)(tipo+48);

        String err = "ERR";
        byte [] berr = err.getBytes();
        for(int i = 0; i<berr.length; i++){
            mensajeBytes[i+4]=berr[i];
        }
        // final de mensaje
        mensajeBytes[numBytes-2] = (byte)rn[0]; // r
        mensajeBytes[numBytes-1] = (byte)rn[1]; // n

        return mensajeBytes;

    }
}
