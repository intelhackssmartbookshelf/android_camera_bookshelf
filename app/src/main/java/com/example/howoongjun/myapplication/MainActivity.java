
package com.example.howoongjun.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.widget.EditText;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class MainActivity extends AppCompatActivity {

    // 사용자 정의 함수로 블루투스 활성 상태의 변경 결과를 App으로 알려줄때 식별자로 사용됨(0보다 커야함)
    static final int REQUEST_ENABLE_BT = 10;

    // 폰의 블루투스 모듈을 사용하기 위한 오브젝트.
    BluetoothDevice bluetoothDevice;

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    Button mButtonSend;
    Button mButtonStop;
    Button mButtonStart;
    BluetoothGatt bluetoothGatt;
    ImageView iv = null;
    HttpClient httpclient = HttpClients.createDefault();

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            bluetoothDevice = device;
        }
    };

    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // 170725 Added
            int distVal = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, 1);
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            Log.d("ble", "ddd");
            for(BluetoothGattService gattService : gatt.getServices()){
                if(gattService == null)
                    continue;
                Log.d("ble", gattService.getUuid().toString());
            }
        }
    };

    // 170725 Added
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        iv.setImageURI(data.getData());

        //MS API
        try
        {
            URIBuilder builder = new URIBuilder("https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/ocr");

            builder.setParameter("language", "en");
            builder.setParameter("detectOrientation ", "true");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", "c038525346344e78b3dbe75994653853");

            // Request body
            StringEntity reqEntity = new StringEntity(data.getData().toString());
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null)
            {
                System.out.println(EntityUtils.toString(entity));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);


                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                        ) {
                    // All Permissions Granted

                    // Permission Denied
                    Toast.makeText(MainActivity.this, "All Permission GRANTED !! Thank You :)", Toast.LENGTH_SHORT)
                            .show();


                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "One or More Permissions are DENIED Exiting App :(", Toast.LENGTH_SHORT)
                            .show();

                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void fuckMarshMallow() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("Show Location");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {

                // Need Rationale
                String message = "App need access to " + permissionsNeeded.get(0);

                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        Toast.makeText(MainActivity.this, "No new Permission Required- Launching App .You are Awesome!!", Toast.LENGTH_SHORT)
                .show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {

        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+ Permission APIs
            fuckMarshMallow();
        }

        mButtonSend = (Button)findViewById(R.id.btnSend);
        mButtonStop = (Button)findViewById(R.id.btnStop);
        mButtonStart = (Button)findViewById(R.id.btnStart);

        mButtonSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("test", bluetoothGatt.getServices().toString());
                BluetoothGattService btdevice = bluetoothGatt.getService(UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1214"));
                BluetoothGattCharacteristic chara = btdevice.getCharacteristic(UUID.fromString("19B10001-E8F2-537E-4F6C-D104768A1214"));
                chara.setValue(new byte[] {(byte) 0x01});
                bluetoothGatt.writeCharacteristic(chara);
            }
        });

        BluetoothManager btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);

        final BluetoothAdapter btAdapter = btManager.getAdapter();
        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }

        mButtonStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try{
                    btAdapter.startLeScan(leScanCallback);
                }
                catch(Exception e){
                    Log.d("err", e.toString());
                }
            }
        });

        mButtonStop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try{
                    btAdapter.stopLeScan(leScanCallback);

                    bluetoothGatt = bluetoothDevice.connectGatt(getBaseContext(), false, btleGattCallback);
                    bluetoothGatt.connect();
                    bluetoothGatt.discoverServices();
                    Log.d("ble", bluetoothGatt.getDevice().toString());

                }
                catch(Exception e){
                    Log.e("ble", "abc", e);
                    Log.d("err", e.toString());
                }
            }
        });
    }

    public class NetworkTask extends AsyncTask<Void, Void, String>{
        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }

        protected String doInBackground(Void... params){
            String result;
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url,values);
            return result;
        }

        protected void onPostExecute(String s){
            super.onPostExecute(s);
        }
    }


//
//
//    Button.OnClickListener mClickListener = new View.OnClickListener(){
//        public void onClick(View v){
//            HttpClient httpclient = HttpClients.createDefault();
//
//            try
//            {
//                URIBuilder builder = new URIBuilder("https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/ocr");
//
//                builder.setParameter("language", "unk");
//                builder.setParameter("detectOrientation ", "true");
//
//                URI uri = builder.build();
//                HttpPost request = new HttpPost(uri);
//                request.setHeader("Content-Type", "application/json");
//                request.setHeader("Ocp-Apim-Subscription-Key", "c038525346344e78b3dbe75994653853");
//
//
//                // Request body
//                StringEntity reqEntity = new StringEntity("http://cfile25.uf.tistory.com/image/243983425903E78A1A684C");
//                request.setEntity(reqEntity);
//
//                HttpResponse response = httpclient.execute(request);
//                HttpEntity entity = response.getEntity();
//
//                if (entity != null)
//                {
//                    System.out.println(EntityUtils.toString(entity));
//                }
//            }
//            catch (Exception e)
//            {
//                System.out.println(e.getMessage());
//            }
//        }
//    };


}
