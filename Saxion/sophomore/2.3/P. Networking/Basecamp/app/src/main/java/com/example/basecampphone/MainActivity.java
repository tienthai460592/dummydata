package com.example.basecampphone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null){
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Bluetooth available", Toast.LENGTH_SHORT).show();

        }
    }

    public void turnOnBt(View view) {
        Toast.makeText(this,"Turning bluetooth on",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BT);
    }

    public void turnOffBt(View view) {
        if(bluetoothAdapter.isEnabled()){
            bluetoothAdapter.disable();
        }
            Toast.makeText(this,"Turning bluetooth on",Toast.LENGTH_SHORT).show();

    }

    public void getPairedDevices(View view) {
        Intent intent = new Intent(this, DeviceScanActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK){
                    Toast.makeText(this, "Bluetooth ON", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(this, "Bluetooth couldn't turn on", Toast.LENGTH_SHORT).show();

        }
    }
}
