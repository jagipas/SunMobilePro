package com.etex.sunmobilepro.Fragments;

/**
 * Created by javi on 23/06/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.etex.sunmobilepro.R;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.etex.sunmobilepro.*;

import java.util.List;


public class ControlPuertosFragment extends Fragment {

    private char [] puertos;
    private Switch switchA = null;
    private Switch switchC = null;
    private Switch switchB = null;

    private TextView clockC = null;
    private TextView clockB = null;
    private TextView clockA = null;

    private TextView vin = null;
    private TextView vbat = null;

    private FloatingActionButton fabButtonTmp = null;
    private int minutos;
    private int ultimoSeleccionado;

    OnFragmentSelectedListener mCallback;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        try {
            if(context instanceof Activity){

                mCallback = (OnFragmentSelectedListener) context;

            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null){
            ultimoSeleccionado = savedInstanceState.getInt("minutos");
        }
        else minutos = 0;

        //puertos = getArguments().getCharArray("estadoP");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_control_puertos, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle guardarEstado) {
        super.onActivityCreated(guardarEstado);

        // instanciamos switches
        switchA = (Switch)getActivity().findViewById(R.id.switchA);
        switchC = (Switch)getActivity().findViewById(R.id.switchC);
        switchB = (Switch)getActivity().findViewById(R.id.switchB);

        // instanciamos tiempos
        clockC = (TextView)getActivity().findViewById(R.id.clockC);
        clockB = (TextView)getActivity().findViewById(R.id.clockB);
        clockA = (TextView)getActivity().findViewById(R.id.clockA);

        // instanciamos V
        vin = (TextView)getActivity().findViewById(R.id.Vin);
        vbat = (TextView)getActivity().findViewById(R.id.Vout);

        // instanciamos boton temporizar
        fabButtonTmp = (FloatingActionButton)getActivity().findViewById(R.id.fabTmp);

        switchC.setEnabled(false);
        switchB.setEnabled(false);
        switchA.setEnabled(false);

        if (guardarEstado != null) {
            // Restore last state for checked position.
            /*horas = guardarEstado.getInt("hora");
            minutos = guardarEstado.getInt("minuto");
            button_tmp.setText( horas + ":" + minutos);*/
        }

        switchC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onSwitchClicked(v);

            }
        });
        switchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onSwitchClicked(v);

            }
        });
        switchA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onSwitchClicked(v);

            }
        });

        fabButtonTmp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Seleccionar tiempo para apagado");

                builder.setSingleChoiceItems(R.array.tiempo_arrays, ultimoSeleccionado, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int num = which;
                        switch (num) {
                            case 0:
                                minutos = 0;
                                ultimoSeleccionado = 0;
                                break;
                            case 1:
                                minutos = 30;
                                ultimoSeleccionado = 1;
                                break;
                            case 2:
                                minutos = 60;
                                ultimoSeleccionado = 2;
                                break;
                            case 3:
                                minutos = 90;
                                ultimoSeleccionado = 3;
                                break;
                            case 4:
                                minutos = 120;
                                ultimoSeleccionado = 4;
                                break;
                            default:
                                break;
                        }

                        clockA.setText(String.valueOf(minutos));
                        clockB.setText(String.valueOf(minutos));
                        clockC.setText(String.valueOf(minutos));


                    }

                });
                builder.setPositiveButton("Elegir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCallback.onTemporizarClicked(minutos);
                    }
                });

                builder.show();

            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle guardarEstado) {

        super.onSaveInstanceState(guardarEstado);
        guardarEstado.putInt("minutos", minutos);
        guardarEstado.putInt("ultimo",ultimoSeleccionado);

    }

    public void onSwitchClicked(View v){
        switch(v.getId()){
            case R.id.switchA:

                if(switchA.isChecked()){
                    mCallback.onSwitchClicked(0,1); //pasar estados a chars
                }
                else mCallback.onSwitchClicked(0,0);

                break;
            case R.id.switchC:

                if(switchC.isChecked()){
                    mCallback.onSwitchClicked(2,1);
                }
                else mCallback.onSwitchClicked(2,0);
                break;
            case R.id.switchB:

                if(switchB.isChecked()){
                    mCallback.onSwitchClicked(1,1);
                }
                else mCallback.onSwitchClicked(1,0);
                break;
        }

    }


    public void actualizarSwitchs(Boolean conexion, List<Usb> Usb ){
        if(conexion) {

            // activamos switchs
            switchC.setEnabled(true);
            switchB.setEnabled(true);
            switchA.setEnabled(true);

            switchA.setChecked(1 == Usb.get(0).getEstado());
            switchB.setChecked(1 == Usb.get(1).getEstado());
            switchC.setChecked(1 == Usb.get(2).getEstado());

            clockA.setText(String.valueOf(minutos-Usb.get(0).getTiempo()));
            clockB.setText(String.valueOf(minutos-Usb.get(1).getTiempo()));
            clockC.setText(String.valueOf(minutos-Usb.get(2).getTiempo()));

            //minutos = Usb.get(0).getTiempo();

            //clockC.s

        }else{
            switchC.setEnabled(false);
            switchB.setEnabled(false);
            switchA.setEnabled(false);
        }
    }

    public void actualizarV(int Vin, int Vbat){
        vin.setText(String.valueOf(Vin*10));
        vbat.setText(String.valueOf(Vbat));
    }


}