package com.hspforzheim.arduinoc;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Diese Klasse beinhaltet alle Bluethooth Funktionalitäten.
 * Es werden Berechtigungen angefordert und Geräte verbunden.
 */
public class findArduino extends AppCompatActivity {

    private TextView tv;

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

    }
}
