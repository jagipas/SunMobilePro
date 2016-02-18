package com.etex.sunmobilepro.BDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by javi on 16/07/15.
 */
public class SunBD extends SQLiteOpenHelper {

    private static SunBD sInstance;
    private static final int DATABASE_VERSION = 1;  //versión base de datos, para futuras actualizaciones

    // nombres de las tablas
    public static final String SOMBRILLA = "sombrilla";
    public static final String CONSUMO = "consumo";
    // tabla sombrillas nombres columnas
    public static final String SOMBRILLA_ID = "_id";
    public static final String SOMBRILLA_IDMAC = "idmac";
    public static final String SOMBRILLA_NOMBRE = "nombre";
    public static final String SOMBRILLA_DIRECCION ="direccion";
    //public static final String SOMBRILLA_FECHA = "fecha";
    // tabla consumo nombrescolumnas
    public static final String CONSUMO_ID = "_id";
    public static final String CONSUMO_DATO_TENSION = "dato_tension";
    public static final String CONSUMO_DATO_CORRIENTE = "dato_corriente";
    public static final String CONSUMO_IDSOBRILLA = "idsombrilla";
    public static final String CONSUMO_IDUSB = "idusb";
    public static final String CONSUMO_FECHA = "fecha";
    public static final String CONSUMO_HORA = "hora";
    // nombre base de datos
    private static final String DATABASE_NAME = "Sundb.db";


    public static synchronized SunBD getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new SunBD(context.getApplicationContext());
        }
        return sInstance;
    }

    public SunBD(Context contexto) {

        super(contexto, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase bd) {
        bd.execSQL("CREATE TABLE "+SOMBRILLA+" ("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                SOMBRILLA_IDMAC+" TEXT, "+
                SOMBRILLA_NOMBRE+" TEXT, "+
                SOMBRILLA_DIRECCION+" TEXT)");

        bd.execSQL("CREATE TABLE "+CONSUMO+" ("+
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                CONSUMO_IDSOBRILLA+" TEXT, "+
                CONSUMO_FECHA+" TEXT, "+
                CONSUMO_HORA+" INTEGER, "+
                CONSUMO_DATO_TENSION+" REAL, "+
                CONSUMO_DATO_CORRIENTE+" REAL)");

        /*bd.execSQL("INSERT INTO lugares VALUES (null, 'Al de siempre', "+
                "'P.Industrial Junto Molí Nou - 46722, Benifla (Valencia)', " +
                " -0.190642, 38.925857, " +  TipoLugar.BAR.ordinal() + ", '', 636472405, '', "+
                "'No te pierdas el arroz en calabaza.', " +
                System.currentTimeMillis() +", 3.0)");

       */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


}
