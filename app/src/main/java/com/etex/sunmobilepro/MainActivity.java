package com.etex.sunmobilepro;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.etex.sunmobilepro.Adaptadores.TabsPagerAdapter;
import com.etex.sunmobilepro.BDatos.SunBDManager;
import com.etex.sunmobilepro.Fragments.ConsumoFragment;
import com.etex.sunmobilepro.Fragments.ContraseñasFragment;
import com.etex.sunmobilepro.Fragments.ControlPuertosFragment;
import com.etex.sunmobilepro.logger.*;
import com.github.mikephil.charting.charts.LineChart;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener, OnFragmentSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    int num = 0;
    public static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    // Intent request codes
    private static final int REQUEST_LOGIN = 1;                // para lanzar activity login y recoger datos
    private static final int REQUEST_CONNECT_DEVICE = 2;       //para lanzar activiti listadispositivos
    private static final int REQUEST_ENABLE_BT = 3;

    //Nombre del dispositivo conectado
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    // Parametros ultimo SunMob al que nos hemos conectado
    private String mDeviceName;
    private String mDeviceAddress;

    private boolean mConnected = false;  // estado de conexión

    // Servicio Bluetooth
    private BluetoothLeService mBluetoothLeService;

    private BluetoothGattService mMLDPService;
    private BluetoothGattCharacteristic mDataMDLP;   // Characteristica que se escribe para enviar datos
    private BluetoothGattCharacteristic mControlMLDP;
    private BluetoothGattCharacteristic deviceNameCharact;

    String mensajeRecibido;
    ArrayList<Byte> bytesMensajeRecibido = new ArrayList<Byte>();
    byte[] bytesMensaje = new byte[200];
    ArrayList<Byte> mensajeRecibidoAnterior = new ArrayList<Byte>();
    char letraAnterior;

    // Adaptador local Bluetooth
    private BluetoothAdapter adaptadorBT = null;

    Menu menu;
    Toolbar toolbar;
    ViewPager pager;
    public TabsPagerAdapter pagesAdapter;
    private TabLayout tabLayout;
    CharSequence titulos[] = {"Puertos", "Consumo", "Contras."};
    int numTabs = 3;

    private NavigationView mNavDrawer;
    private DrawerLayout drawerLayout;

    private static SunBDManager sunBDmanager;   // base de datos
    private Sombrilla mSombrilla;

    byte[] postEstadoPBytes;

    private int diaglobal;
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {

                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected");
            mBluetoothLeService = null;
        }
    };


    /*public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
        //logWrapper.setNext(logFragment.getLogView());
        logFragment.getLogView().setTextAppearance(this, R.style.Log);
        logFragment.getLogView().setBackgroundColor(Color.WHITE);
    }*/

    /**
     * Metodos ciclo de vida main activity
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //initializeLogging();
        Log.d(TAG, "onCreate");

        // Inicializamos la Toolbar y se pone como actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializamos NavigationView
        mNavDrawer = (NavigationView) findViewById(R.id.nvView);
        mNavDrawer.setNavigationItemSelectedListener(this);

        // Inicializamos Drawer Layout i el boton toggle de la actionbar
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        // Vinculamos el boton Toggle on el Drawer layout
        drawerLayout.setDrawerListener(actionDrawerToggle);

        // Llamamos syncState para que el icono se muestre
        actionDrawerToggle.syncState();

        pager = (ViewPager) findViewById(R.id.pager);
        pagesAdapter = new TabsPagerAdapter(getSupportFragmentManager(), titulos, numTabs);
        pagesAdapter.addFragment(new ControlPuertosFragment());
        pagesAdapter.addFragment(new ConsumoFragment());
        pagesAdapter.addFragment(new ContraseñasFragment());
        pager.setAdapter(pagesAdapter);
        pager.setOffscreenPageLimit(2);

        //Configuramos el tab Layout
        setupTablayout();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Pages adapter count " + pager.getCurrentItem(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //pagesAdapter.getCount();
                if (pager.getCurrentItem() == 0) {
                    sendMessage(MensajeBt.MESSAGE_ESTADO, null);
                } else if (pager.getCurrentItem() == 1) {

                    diaglobal = 1;
                    byte [] dia = {(byte)diaglobal};
                    ConsumoFragment consumoF = (ConsumoFragment)pagesAdapter.getFragment(1);
                    List<String> fechas =sunBDmanager.getFechasConsumo();
                            /*new ArrayList<String>();
                    fechas.add("10-2-2016");
                    fechas.add("11-2-2016");
                    fechas.add("12-2-2016");*/
                    consumoF.setSpinnerData(fechas);
                    sendMessage(MensajeBt.MESSAGE_CONSUMO, dia);
                } else if (pager.getCurrentItem() == 2) {
                    sendMessage(MensajeBt.MESSAGE_CONSULTA_CONT, null);
                }
            }
        });

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);   //Get the BluetoothManager
        adaptadorBT = bluetoothManager.getAdapter();

        // comprobamos si el bluetooth esta disponible en el dispositivo
        if (adaptadorBT == null) {
            Toast.makeText(this, "Bluetooth no disponible", Toast.LENGTH_LONG).show();
            finish();   // si no disponible termina el programa
            return;
        }

        // Comprobamos soporte para Bluetooth LE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Activamos servicio bluetoothgatt
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }

        // instanciamos dbmanager
        sunBDmanager = new SunBDManager(this);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter()); // Se registran las actions a recibir por el broadcastReceiver
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume()");
        super.onResume();

        // If BT is not on, request that it be enabled.
        if (adaptadorBT == null || !adaptadorBT.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        // Comprobamos si se ha perdido la conexión mietras la actividad a estado en pausa
        if (mConnected && mBluetoothLeService.getmConnectionState() == mBluetoothLeService.STATE_DISCONNECTED) {
            mConnected = false;
            invalidateOptionsMenu();
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause()");
        super.onPause();

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop()");
        super.onStop();
        /*if (mBluetoothLeService.getmConnectionState() != mBluetoothLeService.STATE_DISCONNECTED) {
            mBluetoothLeService.disconnect();
            mConnected = false;
            updateConnectionState(R.string.disconnected);
            invalidateOptionsMenu();
        }*/
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        //unregisterReceiver(mGattUpdateReceiver);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem estado = menu.findItem(R.id.estado_conexion);

        if (mConnected /*mBluetoothLeService.getmConnectionState() == BluetoothLeService.STATE_CONNECTED*/) {
            estado.setTitle(R.string.connected);
        } else {
            estado.setTitle(R.string.disconnected);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // Item boton conectar a un dispositivo bluetooth
        if (id == R.id.action_log) {
            //View log = findViewById(R.id.log_fragment);
            //log.setVisibility(View.GONE);


        }

        if (id == R.id.estado_conexion) {

            if (mConnected) {

                if (mBluetoothLeService != null)
                    mBluetoothLeService.disconnect();
                mConnected = false;
            } else {
                Intent conectarIntent = new Intent(this, ScanLEActivity.class);
                startActivityForResult(conectarIntent, REQUEST_CONNECT_DEVICE);
                //setDeviceList();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Make sure this is the method with just `Bundle` as the signature
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        //Checking if the item is in checked state or not, if not make it in checked state
        if (menuItem.isChecked()) menuItem.setChecked(false);
        else menuItem.setChecked(true);

        //Closing drawer on item click
        drawerLayout.closeDrawers();

        //Check to see which item was being clicked and perform appropriate action
        switch (menuItem.getItemId()) {
            //return true;
        }
        return true;
    }

    private void setupTablayout() {

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        if (tabLayout == null)
            return;

        tabLayout.setTabTextColors(Color.WHITE, Color.parseColor("#00a5c8"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(pager);
        //tabLayout.setVisibility(View.GONE);

    }

    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // se actualizan los switch en el fragment
                ControlPuertosFragment cpf = (ControlPuertosFragment) pagesAdapter.getFragment(0);
                cpf.actualizarSwitchs(mConnected, mSombrilla.getUSBs());
                cpf.actualizarV(mSombrilla.getVin(), mSombrilla.getVbat());

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult()");
        switch (requestCode) {

            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    //Log.d(TAG, "Reques connect device, address:  " + data.getStringExtra(EXTRAS_DEVICE_ADDRESS) + " name: " + data.getStringExtra(EXTRAS_DEVICE_NAME));
                    conectDispBt(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    Toast.makeText(this, "BT activado",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // User did not enable Bluetooth or an error occurred

                    Toast.makeText(this, "BT no disponible en el dispositivo",
                            Toast.LENGTH_SHORT).show();
                    this.finish();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    private void conectDispBt(Intent data) {
        //Log.d(TAG, "conectDispBt()");
        // Get the device MAC address
        mDeviceAddress = data.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mDeviceName = data.getStringExtra(EXTRAS_DEVICE_NAME);

        Log.d(TAG, "conectDispBt(), dirección: " + mDeviceAddress);
        mConnected = mBluetoothLeService.connect(mDeviceAddress);

        // Creamos objeto sombrilla para guardar estado
        mSombrilla = new Sombrilla("conectando", mDeviceAddress);

        Usb usbA = new Usb("A", 0);  // Usb en etado dos, aun no consultados
        Usb usbB = new Usb("B", 0);
        Usb usbC = new Usb("C", 0);
        List<Usb> listUsb = new ArrayList<>();
        listUsb.add(usbA);
        listUsb.add(usbB);
        listUsb.add(usbC);
        mSombrilla.setUSBs(listUsb);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_WRITTEN);
        return intentFilter;
    }

    /**
     * Instanciamos un BroadcastReceiver que se encargara de detectar si el estado
     * del Bluetooth del dispositivo ha cambiado mediante su handler onReceive, y recibir mensajes broadcast del sevicoBluetothLE
     */

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();

            // Filtramos por la accion. Nos interesa detectar BluetoothAdapter.ACTION_STATE_CHANGED
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {

                // Solicitamos la informacion extra del intent etiquetada como BluetoothAdapter.EXTRA_STATE
                // El segundo parametro indicara el valor por defecto que se obtendra si el dato extra no existe
                final int estado = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (estado) {
                    // Apagado
                    case BluetoothAdapter.STATE_OFF: {
                        if (!adaptadorBT.isEnabled()) {
                            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                        }
                        break;
                    }

                    // Encendido
                    case BluetoothAdapter.STATE_ON: {
                        break;
                    }
                    default:
                        break;
                }
            }

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.d(TAG, "[" + currentDateTimeString + "] Conectado a: " + mDeviceAddress);
                mConnected = true;
                invalidateOptionsMenu();  // Actualizamos estado conexión
                updateUI();

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {

                String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                Log.d(TAG, "[" + currentDateTimeString + "] Desconectado de: " + mDeviceAddress);
                mConnected = false;
                invalidateOptionsMenu();
                updateUI();
                mBluetoothLeService.close();
                //clearUI();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                // displayGattServices(mBluetoothLeService.getSupportedGattServices());
                Log.d(TAG, "mensaje broadcast bluetooth ACTION SERVICES DISCOVERED");
                findMldpGattServices(mBluetoothLeService.getSupportedGattServices());
                //if(deviceNameCharact!=null) mBluetoothLeService.readCharacteristic(deviceNameCharact);

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {    // salta onCharactiristicRead en blservice

                Log.d(TAG, "mensaje broadcast datos disponibles");
                Byte[] txdata = toObjects(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA));
                byte[] curretnBytes = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                String cRecibido = null;
                try {
                    cRecibido = new String(intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA), "US-ASCII");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                // String recibido = null;
                /*try {
                    recibido = new String(txdata, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                //Log.d(TAG, "datos recibidos: " + recibido);

                for (int i = 0; i < txdata.length; i++) {

                    if (cRecibido.charAt(i) == '0' && letraAnterior == 'B') {
                        num = 1;
                        Log.d(TAG, "recibimos byte inicio" + " total: " + num);

                        // bytesMensaje[num]=curretnBytes[i];
                        bytesMensajeRecibido.add(txdata[i]);
                        num++;


                    } else if (cRecibido.charAt(i) == '\n' && letraAnterior == 'z') {


                        Log.d(TAG, "recibimo byte final: " + txdata[i].intValue() + " total: " + num);
                        // bytesMensaje[num]=curretnBytes[i];
                        num = 0;
                        procesarMensaje(bytesMensajeRecibido);

                    } else {

                        Log.d(TAG, "recibimo byte: " + txdata[i].intValue() + " total: " + num);
                        bytesMensajeRecibido.add(txdata[i]);
                        // bytesMensaje[num]=curretnBytes[i];
                        num++;

                    }

                    letraAnterior = cRecibido.charAt(i);
                }


            } else if (BluetoothLeService.ACTION_DATA_WRITTEN.equals(action)) {
                //Log.d(TAG, "salta broadcastrecieve bluetooth data written");
                //Log.d(DeviceControlActivity.TAG, " BroadcastReceiver.onReceive ACTION_DATA_WRITTEN");
                //Toast.makeText(MainActivity.this, "Action data written", Toast.LENGTH_SHORT).show();
            }

        }
    };

    // byte[] to Byte[] transforma array de byte a su clase envolvente para coger integers
    Byte[] toObjects(byte[] bytesPrim) {
        Byte[] bytes = new Byte[bytesPrim.length];

        int i = 0;
        for (byte b : bytesPrim) bytes[i++] = b; // Autoboxing

        return bytes;
    }


    private void findMldpGattServices(List<BluetoothGattService> gattServices) {

        Log.d(TAG, "findMldpServices()");
        if (gattServices == null) {
            Log.d(TAG, "no se han econtrado servicios");    //Verify that list of GATT services is valid
            return;
        }

        String uuid;                                           //String to compare received UUID with desired known UUIDs
        mDataMDLP = null;

        for (BluetoothGattService gattService : gattServices) {
            uuid = gattService.getUuid().toString();                          //Get the string version of the service's UUID

            if (uuid.equals(BluetoothLeService.MLDP_PRIVATE_SERVICE)) {    //See if it matches the UUID of the MLDP service "00035b03-58e6-07dd-021a-08123a000300"
                //mMLDPService = gattService;
                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics(); //If so then get the service's list of characteristics
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {               //Test each characteristic in the list of characteristics
                    uuid = gattCharacteristic.getUuid().toString();                     //Get the string version of the characteristic's UUID
                    if (uuid.equals(BluetoothLeService.MLDP_DATA_PRIVATE_CHAR)) {        // "00035b03-58e6-07dd-021a-08123a000301"
                        mDataMDLP = gattCharacteristic;
                        Log.d(TAG, "encontrado servico MLDP");

                        final int characteristicProperties = gattCharacteristic.getProperties(); //Get the properties of the characteristic

                        if ((characteristicProperties & 0x30) > 0) { //See if the characteristic has the Notify property
                            mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true); //If so then enable notification in the BluetoothGatt
                            Log.d(TAG, "activamos notificaciones mldp");
                        }

                    }/*else if (uuid.equals(BluetoothLeService.MLDP_CONTROL_PRIVATE_CHAR)) {
                    }*/

                }

            }

            /*if (uuid.equals(BluetoothLeService.GENERIC_ACCESS_SERVICE)) {
                List<BluetoothGattCharacteristic> gattCharacteristicsGenericAccess = gattService.getCharacteristics();

                Log.d(TAG, "encontrado servico generic access");
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristicsGenericAccess) {               //Test each characteristic in the list of characteristics
                    uuid = gattCharacteristic.getUuid().toString();                     //Get the string version of the characteristic's UUID
                    if (uuid.equals("00002a00-0000-1000-8000-00805f9b34fb")) {        // "00035b03-58e6-07dd-021a-08123a000301"
                        deviceNameCharact = gattCharacteristic;
                    }

                    final int characteristicProperties = gattCharacteristic.getProperties(); //Get the properties of the characteristic

                }
            }*/

        }

        if (mDataMDLP == null) {
            Toast.makeText(this, "MLDP no soportado", Toast.LENGTH_SHORT).show();
            finish();
        }

    }


    /**
     * Metodos interfaz OnFragmentSelectedListener
     * se llaman desde los fragments para comunicar cambios de estado a la main activity
     */

    public void onSwitchClicked(int puerto, int estado) {

        Log.d(TAG, "onSwitchClicked(), puerto= " + puerto + " estado=" + estado);

        //mSombrilla.setEstadoPuertos(puerto, estado);
        int[] preEstado = mSombrilla.getEstadoPuertos();
        preEstado[puerto] = estado;
        byte[] postEstadoPBytes = {(byte) preEstado[0], (byte) preEstado[1], (byte) preEstado[2]};

        // cambiamos estado switch en el objeto sombrilla
        mSombrilla.setEstadoPuertos(puerto, estado);

        sendMessage(MensajeBt.MESSAGE_HABILITAR_PUERTOS, postEstadoPBytes);

    }

    public void onTemporizarClicked(int m) {

        int t = m;
        Log.d(TAG, "onTemporizarClicked(), minutos: " + m);
        //String men = String.valueOf(t);
        byte[] tiempoBytes = new byte[1];
        tiempoBytes[0] = (byte) m;
        sendMessage(MensajeBt.MESSAGE_TEMPORIZAR, tiempoBytes);
    }

    public void onModificarClicked(char[] cont, int id) {
        Log.d(TAG, "onModificarClicked()");

        byte[] contBytes = new byte[cont.length + 1];

        for (int i = 0; i < cont.length; i++) {
            contBytes[i + 1] = (byte) cont[i];
        }

        contBytes[0] = (byte) (id + 1);

        sendMessage(MensajeBt.MESSAGE_MOD_CONT, contBytes);

    }

    public void onEnviarFechaClicked() {

        Calendar cal = Calendar.getInstance();

        int mes = cal.get(Calendar.MONTH)+1;
        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        byte[] fechaBytes = {(byte) mes, (byte) dia, (byte) hora, (byte) min};
        sendMessage(MensajeBt.MESSAGE_EVIAR_FECHA, fechaBytes);

    }

    /**
     * Metodos para enviar mensajes por bluetooth
     */

    public void sendMessage(int tipo, byte[] m) {

        Log.d(TAG, "sendMessage() tipo: " + tipo);

        // comprobamos conexíon, sino return
        if (mBluetoothLeService.getmConnectionState() != mBluetoothLeService.STATE_CONNECTED) {
            Log.d(TAG, "no hay conexión, estado: " + mBluetoothLeService.getmConnectionState());
            return;
        }

        MensajeBt mensaje;
        byte[] mensajeEnBytes;

        switch (tipo) {

            case MensajeBt.MESSAGE_ESTADO:
            case MensajeBt.MESSAGE_CONSULTA_CONT:

                mensaje = new MensajeBt(mSombrilla.getId(), tipo, null);  //mSombrilla.getId()
                mensajeEnBytes = mensaje.getMensajeEnBytes();
                break;

            case MensajeBt.MESSAGE_CONSUMO:
            case MensajeBt.MESSAGE_HABILITAR_PUERTOS:
            case MensajeBt.MESSAGE_TEMPORIZAR:
            case MensajeBt.MESSAGE_MOD_CONT:
            case MensajeBt.MESSAGE_EVIAR_FECHA:
                mensaje = new MensajeBt(mSombrilla.getId(), tipo, m);
                mensajeEnBytes = mensaje.getMensajeEnBytes();
                break;

            default:
                // mensaje de error
                mensaje = new MensajeBt(mSombrilla.getId(), tipo, null);
                mensajeEnBytes = mensaje.getMensajeEnBytes();
                break;

        }

        mBluetoothLeService.writeRXCharacteristic(mensajeEnBytes);

    }

    /**
     * Metodo procesar mensajes entrantes
     */

    private void procesarMensaje(ArrayList<Byte> mensajeRespuest) {



        if (mensajeRespuest.get(0)!=66 && mensajeRespuest.get(1)!=48){
            Log.d(TAG, "procesarMensaje() mensaje anomalo: " + mensajeRespuest.size());
            mensajeRecibidoAnterior = bytesMensajeRecibido;   // Gurdamso mensaje como anterior
            bytesMensajeRecibido.clear();                     // Vaciamos el array
            return;
        }


        int tipo = mensajeRespuest.get(3).intValue() - 48;
        ArrayList<Byte> estadoRespuesta = new ArrayList<Byte>(mensajeRespuest.subList(4, 6));
        String estado = estadoRespuesta.toString();


        switch (tipo) {
            case MensajeBt.MESSAGE_ESTADO:

                Log.d(TAG, "procesarMensaje() tipo: " + tipo);
                if (estado.equals("ERR")) {
                    Log.d(TAG, "ERR tipo " + tipo);
                    updateUI();

                } else {
                    Log.d(TAG, "AOKK tipo " + tipo);
                    mSombrilla.setEstadoPuertos(0, mensajeRespuest.get(4).intValue());
                    mSombrilla.setEstadoPuertos(1, mensajeRespuest.get(5).intValue());
                    mSombrilla.setEstadoPuertos(2, mensajeRespuest.get(6).intValue());

                    mSombrilla.setTiempoPuerto(0, mensajeRespuest.get(7).intValue());
                    mSombrilla.setTiempoPuerto(1, mensajeRespuest.get(8).intValue());
                    mSombrilla.setTiempoPuerto(2, mensajeRespuest.get(9).intValue());

                    mSombrilla.setVin(mensajeRespuest.get(10).intValue());
                    mSombrilla.setVbat(mensajeRespuest.get(11).intValue());
                    updateUI();
                }
                break;

            case MensajeBt.MESSAGE_HABILITAR_PUERTOS:
                Log.d(TAG, "procesarMensaje() tipo: " + tipo);

                Log.d(TAG, "procesarMensaje() tipo: " + tipo);
                if (estado.equals("ERR")) {
                    Log.d(TAG, "ERR tipo " + tipo);
                    updateUI();

                } else {
                    Log.d(TAG, "AOKK tipo " + tipo);
                }

                break;

            case MensajeBt.MESSAGE_TEMPORIZAR:

                Log.d(TAG, "procesarMensaje() tipo: " + tipo);

                if (estadoRespuesta.equals("ERR")) {
                    Log.d(TAG, "ERR tipo " + tipo);

                } else {
                    Log.d(TAG, "AOKK tipo " + tipo);
                }

                break;

            case MensajeBt.MESSAGE_MOD_CONT:

                Log.d(TAG, "procesarMensaje() tipo: " + tipo);

                if (estadoRespuesta.equals("ERR")) {
                    Log.d(TAG, "ERR tipo " + tipo);

                } else {
                    ContraseñasFragment cf = (ContraseñasFragment) pagesAdapter.getFragment(2);
                    cf.contraseñaActualizadaOk();
                }

                break;

            case MensajeBt.MESSAGE_CONSULTA_CONT:

                Log.d(TAG, "procesarMensaje() tipo: " + tipo);

                if (estadoRespuesta.equals("ERR")) {
                    Log.d(TAG, "ERR tipo " + tipo);

                } else {

                    //if (mensajeRespuest.size() == 84) {
                        //String strCont = mensaje.substring(4);
                        String[] contraseñas = new String[20];
                        int index = 0;
                        for (int i = 4; i <= 80; i = i + 4) { // bucle desde primerera contraseña del mensaje asta la 20 de 4 en 4 bytes
                            contraseñas[index] = String.valueOf(mensajeRespuest.subList(i, i + 4));
                            index++;
                        }
                        ContraseñasFragment fragmentCont = (ContraseñasFragment) pagesAdapter.getFragment(2);    // instanciamos el fragmmento contraseñas
                        fragmentCont.actualizarContraseñas(contraseñas);                                        // llamamos al metodo que refresca las contraseñas, pasandole como parametro la nueva lista
                    //}
                }
                break;

            case MensajeBt.MESSAGE_CONSUMO:

                if (mensajeRespuest.size() <= 6) {
                    Log.d(TAG, "procesarMensaje() no hay dias: " + mensajeRespuest.size());
                    mensajeRecibidoAnterior = bytesMensajeRecibido;   // Gurdamso mensaje como anterior
                    bytesMensajeRecibido.clear();                     // Vaciamos el array
                    return;
                }
                ArrayList<Consumo> consumos = new ArrayList<>();
                Consumo consumo = null;
                long[] tensiones = new long[24];
                long[] corrientes = new long[24];
                int cont = 0;

                consumo = new Consumo(mensajeRespuest.get(4).intValue());
                for (int i = 4; i < 53 - 1; i++) {                         // Bucle desde dia asta ultimo dato de consumo 4cabecera+1dia+24tension+24corriente

                    if (cont < 24) {
                        if (mensajeRespuest.get(i).longValue() == -1) {
                            tensiones[cont] = 0;
                        } else {
                            tensiones[cont] = mensajeRespuest.get(i).longValue();
                        }
                        cont++;
                    } else if (cont >= 24) {
                        if (mensajeRespuest.get(i).longValue() == -1) {
                            corrientes[cont - 24] = 0;
                        } else {
                            corrientes[cont - 24] = mensajeRespuest.get(i).longValue();
                        }
                        cont++;
                    }

                }

                consumo.setTensionHoras(tensiones);
                consumo.setCorrienteHoras(corrientes);

                Log.d(TAG, "fin repartir valores: " + tipo);

                // Guardamos o actualizamos dias en la base de datos

                String fecha = null;
                fecha = getMesAño();
                fecha = String.valueOf(consumo.getDia()) + "-" + fecha;
                sunBDmanager.addConsumos(consumo, fecha);

                // Graficamos dia en fragmente
                ConsumoFragment consumoF = (ConsumoFragment) pagesAdapter.getFragment(1);
                consumoF.setConsumo(consumo, fecha);

                // Comprobamos si se han pedido los 3 dias, sino pedimos el dia siguiente
                if(diaglobal<3){
                    diaglobal++;
                    byte [] m = {(byte)diaglobal};
                    sendMessage(MensajeBt.MESSAGE_CONSUMO,m);
                }
                break;

            default:

        }

        mensajeRecibidoAnterior = bytesMensajeRecibido;   // Gurdamso mensaje como anterior
        bytesMensajeRecibido.clear();                     // Vaciamos el array
    }


    private String getMesAño() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
        ConsumoFragment consumoF = (ConsumoFragment)pagesAdapter.getFragment(1);
        Consumo consumGraf = sunBDmanager.getConsumo(item);
        consumoF.setConsumo(consumGraf,item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
