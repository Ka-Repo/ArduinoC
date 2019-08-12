package com.hspforzheim.arduinoc;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import io.github.controlwear.virtual.joystick.android.JoystickView;


/**
 * Diese Klasse beinhaltet alle Bluethooth Funktionalitäten.
 * Es werden Berechtigungen angefordert und Geräte verbunden.
 */
public class findArduino extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    private static final int REQUEST_ENABLE_BT = 123;
    private static final int REQUEST_ACCESS_LOC = 321;
    private static final int REQUEST_ACCESS_UUID = 000;

    private BluetoothAdapter adapter;
    private TextView tv;
    private boolean started;
    private Button connect;
    private Button disconnect;
    private static String mac_adresse; // MAC Adresse des Bluetooth Adapters
    private BluetoothSocket socket = null;

    private static final String TAG = findArduino.class.getSimpleName();
    public static boolean is_connected = false;
    private static final java.util.UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static OutputStream stream_out = null;


    /**
     * Registrieren eines Bluetooth-Receiver
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                tv.append(getString(R.string.template, device.getName(), device.getAddress()));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_arduino);

        mNavigationView = findViewById(R.id.navgigation_bar_find);
        tv = findViewById(R.id.tv);

        /**
         * Herausfinden der Telefon-ID.
         */
        TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        /**
         * Berechtigung zum Ermitteln der UUID einholen.
         */
        if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_ACCESS_UUID);
        } else {
            Toast.makeText(this, R.string.UUID_REQUEST, Toast.LENGTH_LONG);
        }

        // Toast.makeText(this, tManager.getDeviceId(), Toast.LENGTH_LONG).show();
        // UUID = UUID.fromString(insertDashUUID(tManager.getDeviceId()));


         /**
         * Statusmeldung
         */
        Toast.makeText(this, R.string.search, Toast.LENGTH_LONG).show();

        /**
         * Menuleiste erhält einen Listener um auf Klicks zu reagieren
         */
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        /**
         Ab hier wird die Menuleiste definiert.
         Wenn möglich auslagern aus onCreate.
         */
        mDrawerLayout = findViewById(R.id.bluetooth_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * Registierren des Bluetooth Receiver
         */
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        /**
         * On-Click-Listener zu Testzwecken des Sendens von Daten
         */

        Button right = findViewById(R.id.right1);
        Button left = findViewById(R.id.left1);
        Button up = findViewById(R.id.up1);
        Button down = findViewById(R.id.down1);

        right.setOnClickListener(v -> {
            sendByte(1);
            //finish();
        });

        left.setOnClickListener(v -> {
            sendByte(2);
            //finish();
        });

        up.setOnClickListener(v -> {
            sendByte(3);
            //finish();
        });

        down.setOnClickListener(v -> {
            sendByte(4);
            //finish();
        });

        JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);
        ScrollView scroller = new ScrollView(this);
        scroller.findViewById(R.id.scroller);

        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if (is_connected) {
                    Log.d(TAG, "Sende Nachricht: "  + angle + " " + strength);
                    try {
                        stream_out.write(angle + strength);
                    } catch (IOException e) {
                        Log.e(TAG,
                                "Bluetest: Exception beim Senden: " + e.toString());
                    }
                }

                scroller.setEnabled(false);

            }
        });

        scroller.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

    }


    /**
     * Wird benötigt um das Klicken des Menu-Icons zu ermöglichen.
     *
     * @param item
     * @return true if toggled
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Hier werden nun beim Klicken der Menu-Punkte neue Activities aufgerufen,
     * also eine neue Klasse mit einer eigenen oncreate() und in der App einer eigenen Seite.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.bluetooth:
                break;
            case R.id.control:
                Intent i = new Intent(findArduino.this, controlArduino.class);
                startActivity(i);
                break;
            case R.id.camera:
                break;
            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onStart() {
        super.onStart();

        adapter = null;
        started = false;

        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ACCESS_LOC);
        } else {
            if(isBluetoothEnabled()) {
                showDevices();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(started) {
            adapter.cancelDiscovery();
            started = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if((requestCode == REQUEST_ACCESS_LOC) && (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            if(isBluetoothEnabled()) {
                showDevices();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if((resultCode == RESULT_OK) && (requestCode == REQUEST_ENABLE_BT)) {
            showDevices();
        }
    }

    private boolean isBluetoothEnabled() {
        boolean enabled = false;

        adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter != null) {
            enabled = adapter.isEnabled();

            if(!enabled) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        return enabled;
    }

    private void showDevices(){
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.paired));

        Set<BluetoothDevice> devices = adapter.getBondedDevices();

        for(BluetoothDevice device : devices) {
            sb.append(getString(R.string.template, device.getName(), device.getAddress()));
        }

        sb.append("\n");

        if(started) {
            adapter.cancelDiscovery();
        }

        started = adapter.startDiscovery();

        if(started) {
            sb.append(getString(R.string.others));
        }

        tv.setText(sb.toString());
    }

    /**
     * Verbinden eines Bluetooth-Gerätes über MAC-Adresse.
     * @param v
     */
    public void verbinden(View v) throws IOException {
        mac_adresse = ((EditText) findViewById(R.id.text_adresse)).getText()
                .toString();
        Log.d(TAG, "Verbinde mit " + mac_adresse);

        BluetoothDevice remote_device = adapter.getRemoteDevice(mac_adresse);

        // Socket erstellen
        try {
            socket = remote_device
                    .createInsecureRfcommSocketToServiceRecord(MY_UUID);
            Log.d(TAG, "Socket erstellt");
        } catch (Exception e) {
            Log.d(TAG, "Socket Erstellung fehlgeschlagen: " + e.toString());
        }

        adapter.cancelDiscovery();

        // Socket verbinden
        try {
            socket.connect();
            Log.d(TAG, "Socket verbunden");
            is_connected = true;
        } catch (IOException e) {
            is_connected = false;
            Log.e(TAG, "Socket kann nicht verbinden: " + e.toString());
        }

        Button connect = findViewById(R.id.connect);

        // Socket beenden, falls nicht verbunden werden konnte
        if (!is_connected) {
            try {
                socket.close();
            } catch (Exception e) {
                Log.d(TAG,
                        "Socket kann nicht beendet werden: " + e.toString());
            }
            connect.setBackgroundColor(Color.RED);

        } else {

            connect.setBackgroundColor(Color.GREEN);
        }

        // Outputstream erstellen:
        try {
            stream_out = socket.getOutputStream();
            Log.d(TAG, "OutputStream erstellt");
        } catch (IOException e) {
            Log.e(TAG, "OutputStream Fehler: " + e.toString());
            is_connected = false;
        }
    }

    public void trennen(View v) {
        if (is_connected) {
            is_connected = false;
            ((Button) findViewById(R.id.connect))
                    .setBackgroundColor(Color.RED);
            Log.d(TAG, "Trennen: Beende Verbindung");
            try {
                stream_out.flush();
                socket.close();
            } catch (IOException e) {
                Log.e(TAG,
                        "Fehler beim beenden des Streams und schliessen des Sockets: "
                                + e.toString());
            }
        } else
           Log.d(TAG, "Trennen: Keine Verbindung zum beenden");
    }

    public void senden(View v) {
        String message = ((EditText) findViewById(R.id.text_adresse)).getText()
                .toString();
        byte[] msgBuffer = message.getBytes();
        if (is_connected) {
            Log.d(TAG, "Sende Nachricht: " + message);
            try {
                stream_out.write(msgBuffer);
            } catch (IOException e) {
                Log.e(TAG,
                        "Bluetest: Exception beim Senden: " + e.toString());
            }
        }
    }

    public void sendByte(int direction) {
        if (is_connected) {
            Log.d(TAG, "Sende Nachricht: " + direction);
            try {
                stream_out.write(1);
            } catch (IOException e) {
                Log.e(TAG,
                        "Bluetest: Exception beim Senden: " + e.toString());
            }
        }
    }

/*    public static String insertDashUUID(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(13, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(18, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(23, "-");

        return sb.toString();
    }*/
}
