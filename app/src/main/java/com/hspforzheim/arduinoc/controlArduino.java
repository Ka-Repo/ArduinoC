package com.hspforzheim.arduinoc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;


/**
 * Diese Klasse implementiert das Steuern des per Bluetooth verbundenen Geräts.
 */
public class controlArduino extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private Button left, right, up, down;
    private static final findArduino sendingInstance = new findArduino();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        mNavigationView = findViewById(R.id.navgigation_bar_control);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        up = findViewById(R.id.up);
        down = findViewById(R.id.down);

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
        mDrawerLayout = findViewById(R.id.control_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        left.setOnClickListener(v -> {
            sendingInstance.sendByte(1);
            //finish();
        });

        right.setOnClickListener(v -> {
            sendingInstance.sendByte(2);
            //finish();
        });

        up.setOnClickListener(v -> {
            sendingInstance.sendByte(3);
            //finish();
        });

        up.setOnClickListener(v -> {
            sendingInstance.sendByte(4);
            //finish();
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
                Intent i = new Intent(controlArduino.this, findArduino.class);
                startActivity(i);
                break;
            case R.id.control:
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
