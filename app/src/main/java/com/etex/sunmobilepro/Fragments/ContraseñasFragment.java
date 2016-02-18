package com.etex.sunmobilepro.Fragments;

/**
 * Created by javi on 24/06/15.
 */
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.etex.sunmobilepro.Adaptadores.ContraseñasAdapter;
import com.etex.sunmobilepro.Adaptadores.DividerItemDecoration;
import com.etex.sunmobilepro.Adaptadores.EntradaListaCont;
import com.etex.sunmobilepro.OnFragmentSelectedListener;
import com.etex.sunmobilepro.R;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.LandingAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ContraseñasFragment extends Fragment {

    private char[] contraMod;
    private  int idMod;
    private ContraseñasAdapter adapter;
    private RecyclerView contRecyclerView = null;

    private View rootView;

    public static OnFragmentSelectedListener mCallback;



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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_control_pass, container, false);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle guardarEstado) {
        super.onActivityCreated(guardarEstado);

        // Instanciamos elementos del layout
        contRecyclerView = (RecyclerView)getActivity().findViewById(R.id.list_contraseñas);

        adapter = new ContraseñasAdapter(getActivity(),getFirstData());
        contRecyclerView.setAdapter(adapter);
        contRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration itemDecoration = new  DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        contRecyclerView.addItemDecoration(itemDecoration);
        contRecyclerView.setItemAnimator(new LandingAnimator());
    }


    public static List<EntradaListaCont> getFirstData(){

        List<EntradaListaCont> dataCont = new ArrayList<>();
        int[] icons = {R.drawable.bola1,R.drawable.bola2,R.drawable.bola3,R.drawable.bola4,R.drawable.bola5,R.drawable.bola6,R.drawable.bola7,R.drawable.bola8,R.drawable.bola9,R.drawable.bola10,R.drawable.bola11,R.drawable.bola12,R.drawable.bola13,R.drawable.bola14,R.drawable.bola15,R.drawable.bola16,R.drawable.bola17,R.drawable.bola18,R.drawable.bola19,R.drawable.bola20};
        String[] emptyTitles = new String[20];
        for (int i = 0; i<emptyTitles.length; i++) emptyTitles[i]="Actualizar contraseña";

        for (int j=0; j<emptyTitles.length; j++){
            EntradaListaCont current = new EntradaListaCont();
            current.iconId = icons[j];
            current.contraseña=emptyTitles[j];
            current.iconBorrar=R.drawable.ic_clear_black_24dp;
            current.iconMod=R.drawable.ic_create_black_24dp;
            dataCont.add(current);
        }

        return dataCont;

    }

    /**
     * Metodos para actualizar la lista entera al consultar del dispositivo (actualizarContraseñas),
     * modificar una contraseña añadida o borrada (añadir), o enviar un contraseña a borrar (enviarContraseña)
     * @param contraseñas
     */
    public  void actualizarContraseñas(String[]contraseñas){     // Es crida

        adapter.actualizarConstraseñas(contraseñas);
    }


    public void enviarContraseña(char[] cont, int id){

        contraMod = cont;
        idMod = id;
        mCallback.onModificarClicked(cont, id);
    }

    public void contraseñaActualizadaOk(){
        adapter.contraseñaModificadaOk(contraMod,idMod);
    }






}