package com.etex.sunmobilepro.BDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.etex.sunmobilepro.Consumo;
import com.etex.sunmobilepro.Sombrilla;
import com.etex.sunmobilepro.Usb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by javi on 17/07/15.
 */
public class SunBDManager  {

    private SunBD db;

    //private String[] SOMBRILLA_TABLE_COLUMNS = { SunBD.CONSUMO_ID, SunBD.CONSUMO_DATO, SunBD.CONSUMO_IDSOBRILLA, SunBD.CONSUMO_IDUSB, SunBD.CONSUMO_FECHA };

    private SQLiteDatabase database;

    public SunBDManager(Context context) {

        db = SunBD.getInstance(context);

    }

    public void open() throws SQLException {

        database = db.getWritableDatabase();
    }


    public void close() {

        if(database != null && database.isOpen()) database.close();

    }

    public long addSombrilla(Sombrilla s){

        open();
        ContentValues values = new ContentValues();
        values.put(SunBD.SOMBRILLA_IDMAC,s.getId());
        values.put(SunBD.SOMBRILLA_NOMBRE,s.getNombre());
        values.put(SunBD.SOMBRILLA_DIRECCION, s.getDireccion());
        // insert row
        long sombrill_id = database.insert(SunBD.SOMBRILLA,null,values);
        close();
        return sombrill_id;

    }



    public List<Sombrilla> getSombrillas(){

        List<Sombrilla> direcciones = new ArrayList<Sombrilla>();
        open();
        Cursor cursor = database.rawQuery("SELECT * FROM "+SunBD.SOMBRILLA, null);
        if(cursor.moveToFirst()){
            do{
                Sombrilla s = new Sombrilla();
                s.setId(cursor.getString(cursor.getColumnIndex(SunBD.SOMBRILLA_IDMAC)));
                s.setNombre(cursor.getString(cursor.getColumnIndex(SunBD.SOMBRILLA_NOMBRE)));
                s.setDireccion(cursor.getString(cursor.getColumnIndex(SunBD.SOMBRILLA_DIRECCION)));
                direcciones.add(s);
            }while(cursor.moveToNext());
        }
        cursor.close();
        close();

        return direcciones;
    }

    public void addConsumos(Consumo diaConsumo, String fecha){

        String[] args = new String[] {fecha};
        long[]tensiones = diaConsumo.getTensionHoras();
        long[]corrientes = diaConsumo.getCorrienteHoras();

        open();
        Cursor c = database.rawQuery(" SELECT * FROM consumo WHERE fecha=? ", args);

        // Si hay fecha, actualizamos valores
        if(c.getCount()>0){
            c.close();
            for (int i = 0; i<24; i++){

                ContentValues dato = new ContentValues();
                dato.put(SunBD.CONSUMO_DATO_TENSION,tensiones[i]);
                dato.put(SunBD.CONSUMO_DATO_CORRIENTE, corrientes[i]);
                int num = database.update(SunBD.CONSUMO,dato,SunBD.CONSUMO_FECHA+"=? AND "+ SunBD.CONSUMO_HORA+"=?", new String[]{fecha,String.valueOf(i)});
                int altre = num;
                //int altre2 = altre;
            }

            // Si no la insetamos fecha nueva
        }else{

            for (int i = 0; i<24; i++){
                ContentValues dato = new ContentValues();
                dato.put(SunBD.CONSUMO_FECHA, fecha);
                dato.put(SunBD.CONSUMO_HORA,String.valueOf(i));
                dato.put(SunBD.CONSUMO_DATO_TENSION,tensiones[i]);
                dato.put(SunBD.CONSUMO_DATO_CORRIENTE,corrientes[i]);
                database.insert(SunBD.CONSUMO, null, dato);
            }


        }

        close();


    }
    public List<String> getFechasConsumo(){

        open();
        List<String> ret = new ArrayList<String>();
        Cursor cursor = database.rawQuery("SELECT DISTINCT "+SunBD.CONSUMO_FECHA+" FROM "+SunBD.CONSUMO,null);

        if(cursor.moveToFirst()){
            do{
                ret.add(cursor.getString(cursor.getColumnIndex(SunBD.CONSUMO_FECHA)));

            }while(cursor.moveToNext());
        }

        close();
        return ret;

    }

    public Consumo getConsumo(String fecha) {

        //String date = DateFormat.getInstance().format(fecha);
        List consumos = new ArrayList();
        long [] tensiones = new long[24];
        long [] corrientes = new long[24];
        Consumo diaConsumoResturn = new Consumo();
        open();
        //Cursor cursor = database.query(SunBD.CONSUMO, SOMBRILLA_TABLE_COLUMNS, null, null, null, null, null);
        Cursor cursor = database.rawQuery("SELECT * FROM "+SunBD.CONSUMO+" WHERE "+SunBD.CONSUMO_FECHA+"=?",new String[]{fecha});
        int h = 0;
        if(cursor.moveToFirst()){
            do{
                tensiones[h]=cursor.getLong(cursor.getColumnIndex(SunBD.CONSUMO_DATO_TENSION));
                corrientes[h]=cursor.getLong(cursor.getColumnIndex(SunBD.CONSUMO_DATO_CORRIENTE));
                h++;
            }while(cursor.moveToNext());
        }
        diaConsumoResturn.setTensionHoras(tensiones);
        diaConsumoResturn.setCorrienteHoras(corrientes);
        //diaConsumoResturn.setFecha(cursor.getString(cursor.getColumnIndex(SunBD.CONSUMO_FECHA)));
        cursor.close();
        close();

        return diaConsumoResturn;

    }



    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }



}

