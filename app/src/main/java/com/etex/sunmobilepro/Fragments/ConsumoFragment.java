package com.etex.sunmobilepro.Fragments;

/**
 * Created by javi on 23/06/15.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import com.etex.sunmobilepro.Consumo;
import com.etex.sunmobilepro.MainActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


import com.etex.sunmobilepro.OnFragmentSelectedListener;
import com.etex.sunmobilepro.R;

import java.util.ArrayList;
import java.util.List;

public class ConsumoFragment extends Fragment  {

    OnFragmentSelectedListener mCallback;
    //private LineChart puertosChar;
    private LineChart totalChar;
    private LineChart corrienteChar;
    private LineChart potenciaChar;
    private Button buttonMonitorizar;

    // Spinner element
    Spinner spinnerFechas;

    private ArrayList<Float> datos = new ArrayList<Float>();

    // newInstance constructor for creating fragment with arguments
    public static ConsumoFragment newInstance(char [] datos) {
        ConsumoFragment consumoF = new ConsumoFragment();
        Bundle args = new Bundle();
        args.putCharArray("datosP", datos);
        consumoF.setArguments(args);
        return consumoF;
    }


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

        View rootView = inflater.inflate(R.layout.activity_control_consumo, container, false);


        return rootView;
    }

    public void onActivityCreated(Bundle guardarEstado) {
        super.onActivityCreated(guardarEstado);

        // instanciamos componente interficie
        buttonMonitorizar = (Button)getActivity().findViewById(R.id.button_monitorizar);
        buttonMonitorizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onEnviarFechaClicked();
            }
        });

        spinnerFechas =(Spinner)getActivity().findViewById(R.id.spinner_fechas);
        spinnerFechas.setOnItemSelectedListener((MainActivity)getContext());


        // Instanciamos el grafico de lineas para total
        totalChar = (LineChart)getActivity().findViewById(R.id.chart_general);
        totalChar.setDescription("");
        totalChar.setNoDataText("No hay datos de consumo");
        totalChar.setDrawGridBackground(false);
        // Eje horizontal
        XAxis xAxisT = totalChar.getXAxis();
        xAxisT.setPosition(XAxis.XAxisPosition.BOTTOM);
        // Eje vertical
        YAxis yAxisT = totalChar.getAxisRight();
        yAxisT.setEnabled(false);

        // Instanciamos el grafico corriente
        corrienteChar = (LineChart)getActivity().findViewById(R.id.chart_corriente);
        corrienteChar.setDescription("");
        corrienteChar.setNoDataText("No hay datos de consumo");
        corrienteChar.setDrawGridBackground(false);
        // Eje horizontal
        XAxis xAxisC = totalChar.getXAxis();
        xAxisC.setPosition(XAxis.XAxisPosition.BOTTOM);
        // Eje vertical
        YAxis yAxisC = totalChar.getAxisRight();
        yAxisC.setEnabled(false);

        // Instanciamos el grafico corriente
        potenciaChar = (LineChart)getActivity().findViewById(R.id.chart_potencia);
        potenciaChar.setDescription("");
        //potenciaChar.setDescriptionPosition(100,100);
        potenciaChar.setNoDataText("No hay datos de consumo");
        potenciaChar.setDrawGridBackground(false);
        // Eje horizontal
        XAxis xAxisP = totalChar.getXAxis();
        xAxisP.setPosition(XAxis.XAxisPosition.BOTTOM);
        // Eje vertical
        YAxis yAxisP = totalChar.getAxisRight();
        yAxisP.setEnabled(false);
    }



    public void setSpinnerData(List<String> fechas){

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, fechas);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinnerFechas.setAdapter(dataAdapter);

    }

    public void setConsumo(Consumo consumo, String fecha){

        totalChar.setDescription(fecha);
        // creamos listad de datos entry de los diferentes puerto, entry como wraper del dato
        ArrayList<Entry> valsTension = new ArrayList<Entry>();
        ArrayList<Entry> valsCorrient = new ArrayList<Entry>();
        ArrayList<Entry> valsPotencia = new ArrayList<Entry>();


        // bucle para repartir datos del parametro
        long [] tensiones = consumo.getTensionHoras();
        for(int i=0; i<24; i++){
            Entry tensionE = new Entry((float) tensiones[i],i);
            valsTension.add(tensionE);
        }

        long [] corrientes = consumo.getCorrienteHoras();
        for(int i=0; i<24; i++){
            Entry corrientesE = new Entry((float) corrientes[i]*10,i);
            valsCorrient.add(corrientesE);
        }

        for(int i=0; i<24; i++){
            Entry hora1usbC = new Entry((float) corrientes[i]*tensiones[i],i); //datos.get(i)
            valsPotencia.add(hora1usbC);
        }
        // creamoso los dataSets a partir de las listar de entry, cada data set es una linea en el grafico
        LineDataSet setTensiones = new LineDataSet(valsTension, "tensión "+fecha);
        setTensiones.setAxisDependency(YAxis.AxisDependency.LEFT);
        setTensiones.setFillColor(Color.BLUE);
        setTensiones.setColor(Color.BLUE);
        LineDataSet setCorrientes = new LineDataSet(valsCorrient, "corriente "+fecha);
        setCorrientes.setAxisDependency(YAxis.AxisDependency.LEFT);
        setCorrientes.setFillColor(Color.GREEN);
        setCorrientes.setColor(Color.GREEN);
        LineDataSet setPotencia = new LineDataSet(valsPotencia, "potencia "+fecha);
        setPotencia.setAxisDependency(YAxis.AxisDependency.LEFT);
        setPotencia.setFillColor(Color.RED);
        setPotencia.setColor(Color.RED);


        // añadimos los dataSets de los distintos usb a una lista
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setTensiones);
        //dataSets.add(setCorrientes);
        //dataSets.add(setPotencia);

        // array para los titulos del eje X
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("1h"); xVals.add("2h"); xVals.add("3h"); xVals.add("4h");xVals.add("5h"); xVals.add("6h"); xVals.add("7h"); xVals.add("8h");
        xVals.add("9h"); xVals.add("10h"); xVals.add("11h"); xVals.add("12h");xVals.add("13h"); xVals.add("14h"); xVals.add("15h"); xVals.add("16h");
        xVals.add("17h"); xVals.add("18h"); xVals.add("19h"); xVals.add("20h");xVals.add("21h"); xVals.add("22h"); xVals.add("23h"); xVals.add("24h");

        // pasamos los datos al grafico
        LineData data = new LineData(xVals, dataSets);
        LineData dataC = new LineData(xVals,setCorrientes);
        LineData dataP = new LineData(xVals,setPotencia);

        totalChar.setData(data);
        corrienteChar.setData(dataC);
        potenciaChar.setData(dataP);
        totalChar.invalidate();
        corrienteChar.invalidate();
        potenciaChar.invalidate();// se actualiza grafico
    }

    /*private void addEntry(){
        LineData data = totalChar.getData();
        if(data != null){
            LineDataSet set = data.getDataSetByIndex(0);

            if (set==null){
                //lo creamos si es null
                set = createSet();
                data.addDataSet(set);
            }

            // añadir el nuevo valor random
            data.addXValue("");
            data.addEntry(new Entry((float)(Math.random()*75)+60f, set.getEntryCount()),0);

            // notificar al char cambio de datos
            totalChar.notifyDataSetChanged();


        }
    }*/

    //metodo para crear el set
    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, "SPL Db");
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        return set;
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(110.000f, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(40.000f, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(60.000f, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(30.000f, 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(90.000f, 4); // May
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(100.000f, 5); // Jun
        valueSet1.add(v1e6);

        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        BarEntry v2e1 = new BarEntry(150.000f, 0); // Jan
        valueSet2.add(v2e1);
        BarEntry v2e2 = new BarEntry(90.000f, 1); // Feb
        valueSet2.add(v2e2);
        BarEntry v2e3 = new BarEntry(120.000f, 2); // Mar
        valueSet2.add(v2e3);
        BarEntry v2e4 = new BarEntry(60.000f, 3); // Apr
        valueSet2.add(v2e4);
        BarEntry v2e5 = new BarEntry(20.000f, 4); // May
        valueSet2.add(v2e5);
        BarEntry v2e6 = new BarEntry(80.000f, 5); // Jun
        valueSet2.add(v2e6);

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Brand 1");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Brand 2");
        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);

        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        return xAxis;
    }



}