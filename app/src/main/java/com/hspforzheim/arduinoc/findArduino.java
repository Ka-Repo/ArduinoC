package com.hspforzheim.arduinoc;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

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

    private BluetoothAdapter adapter;
    private TextView tv;
    private boolean started;

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
         * Statusmeldung
         */
        Toast.makeText(this, "Bluetooth-Geräte werden gesucht", Toast.LENGTH_LONG).show();

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
                System.out.println("warum komme ich nicht hier hin?");
                break;
            case R.id.control:
                System.out.println("Es wurde die Taste contorl Arduino gedrückt");
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
}
