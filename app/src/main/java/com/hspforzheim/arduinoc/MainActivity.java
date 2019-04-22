package com.hspforzheim.arduinoc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView mNavigationView = findViewById(R.id.navgigation_bar_id);

        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(this);
        }

        /**
        Ab hier wird die Menuleiste definiert.
        Wenn möglich auslagern aus onCreate.
         **/
        mDrawerLayout = findViewById(R.id.drawer_layout);
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
                Intent i = new Intent(MainActivity.this, findArduino.class);
                startActivity(i);
                break;

            default:
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}
