package com.hspforzheim.arduinoc;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Diese Klasse beinhaltet alle Bluethooth Funktionalitäten.
 * Es werden Berechtigungen angefordert und Geräte verbunden.
 */
public class findArduino extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private TextView tv;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;

    /**
     * Registrieren eines Bluetooth-Recievers
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                tv.append(getString(R.string.findArduino));
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_arduino);

        mNavigationView = findViewById(R.id.navgigation_bar_find);

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
}
