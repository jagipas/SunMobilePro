package com.etex.sunmobilepro;

/**
 * Created by javi on 6/07/15.
 */
public interface OnFragmentSelectedListener {
    //void onEstadoClicked();
    void onSwitchClicked(int puerto, int estado);
    void onTemporizarClicked( int m);
    //void onConsultarClicked();
    //void onBorrarClicked(int num);
    void onEnviarFechaClicked();
    void onModificarClicked(char[] cont, int id);
    //void onPedirDatosClicked();
}
