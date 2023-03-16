package bd.com.expresshub.posx.print;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import bd.com.expresshub.posx.R;

public class BluetoothCheck extends AppCompatActivity implements Runnable {

    private ListView lstvw;
    private ArrayList<DeviceModel> arrayList;
    private ArrayAdapter aAdapter;
    private BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
    String deviceName = "", deviceMac = "";


    BluetoothDevice mBluetoothDevice;
    private BluetoothSocket mBluetoothSocket;
    private UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String TAG = "Print Error";
    private static OutputStream outputStream, outputStream1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_check);

        if (bAdapter == null) {
            Toast.makeText(getApplicationContext(), "Bluetooth Not Supported", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    ActivityCompat.requestPermissions(BluetoothCheck.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                    return;
                }
                //        return;
            }
            lstvw = (ListView) findViewById(R.id.deviceList);


            getbluetoothdevice();
        }

        lstvw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                deviceName = arrayList.get(position).getName();
                deviceMac = arrayList.get(position).getMac();
                PrintPaySlip printPaySlip = new PrintPaySlip();
                printPaySlip.execute();
            }
        });


        findViewById(R.id.btn_new_printer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOpenBluetoothSettings = new Intent();
                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intentOpenBluetoothSettings);
            }
        });

        findViewById(R.id.btn_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getbluetoothdevice();
            }
        });
    }

    private void getbluetoothdevice() {
        arrayList = new ArrayList<DeviceModel>();

        Set<BluetoothDevice> pairedDevices = bAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String devicename = device.getName();
                String macAddress = device.getAddress();
                DeviceModel deviceModel = new DeviceModel(devicename, macAddress);
                arrayList.add(deviceModel);

            }

            aAdapter = new DeviceAdapter(arrayList, getApplicationContext());
            lstvw.setAdapter(aAdapter);


        }
        aAdapter = new DeviceAdapter(arrayList, getApplicationContext());
        lstvw.setAdapter(aAdapter);
        lstvw.deferNotifyDataSetChanged();
        TextView tv = findViewById(R.id.tvhead);
        if (arrayList.size() > 0) {
            tv.setText("Please tap on the device name to print the bill");
        } else {
            tv.setText("Please Pair printer by tapping on 'ADD NEW PRINTER'");
        }
    }

    @Override
    public void run() {

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //mBluetoothConnectProgressDialog.dismiss();
            Toast.makeText(BluetoothCheck.this, "Device Connected", Toast.LENGTH_SHORT).show();
        }
    };

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    class PrintPaySlip extends AsyncTask<Void, Void, String> {
        private Activity context;
        private ProgressDialog pd;
        private String TAG = "bluetooth print";


        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(BluetoothCheck.this, "Print Is Processing",
                    "Please wait...");
        }

        @SuppressLint("MissingPermission")
        @Override
        protected String doInBackground(Void... params) {
            String mDeviceAddress = deviceMac;
            android.util.Log.v(TAG, "Device address " + mDeviceAddress);
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            mBluetoothDevice = mBluetoothAdapter
                    .getRemoteDevice(mDeviceAddress);


            try {

                mBluetoothSocket = mBluetoothDevice
                        .createRfcommSocketToServiceRecord(applicationUUID);
                mBluetoothSocket.connect();
                mHandler.sendEmptyMessage(0);
            } catch (IOException eConnectException) {
                android.util.Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
                closeSocket(mBluetoothSocket);

            }


            Bitmap bitmap = null;
            PrintPic printPic = PrintPic.getInstance();
            try {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.govt_logo);


            } catch (Exception e) {
                // handle exception
                Log.e("bitmap", e.toString());
            }


            try {

                outputStream = mBluetoothSocket.getOutputStream();
                outputStream1 = mBluetoothSocket.getOutputStream();
                String header = "\nGOVERNMENT OF THE PEOPLE REPUBLIC \nOF BANGLADESH \nNATIONAL BOARD OF REVENUE\n";
//                String header = "\nBANGLADESH POWER DEVELOPMENT \nBOARD (BPDB)\nPenalty Bill\n";
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                byte[] command = Utils.decodeBitmap(bitmap);

                outputStream.write(command);
                Thread.sleep(2000);
                outputStream.write(PrinterCommands.ESC_ALIGN_CENTER);
                outputStream.write(header.getBytes());
                Thread.sleep(1000);
                String BILL = "\n";

//                if(INS_AMT == "") {
                   /** BILL = BILL + "--------------------------------\n";
                    BILL = BILL + "LOCATION---: " + LOCATION_CODE + "-" + LOCATION_NAME + "\n";
                    BILL = BILL + "--------------------------------\n";

                    BILL = BILL + "CONSUMER NO: " + CUSTOMER_NUM + "\n";
                    BILL = BILL + "NAME-------: " + CUST_NAME + "\n";
                    BILL = BILL + "ADDRESS----: " + CUST_ADDRESS + "\n";
                    BILL = BILL + "MONTH------: " + BILL_MONTH + "\n";
                    BILL = BILL + "BOOK NUM---: " + AREA + "\n";

                    BILL = BILL + "BILL GROUP-: " + BILL_GROUP + "\n";
                    BILL = BILL + "WALK ORDER-: " + WALK_ORDER + "\n";
                    BILL = BILL + "TARIFF-----: " + TARIFF + "\n";
                    BILL = BILL + "BILL NO----: " + BILL_NUM + "-" + BILL_NUM_CHK_DIGIT + "\n";
                    BILL = BILL + "ISSUE DATE-: " + BILL_ISSUE_DATE + "\n";
                    BILL = BILL + "DUE DATE---: " + DUE_DATE + "\n";
                    BILL = BILL + "--------------------------------\n";
                    BILL = BILL + "METER NUM--: " + METER_NUM + "\n";
                    BILL = BILL + "METER TYPE-: " + METER_TYPE + "\n";
                    BILL = BILL + "METER COND-: " + METER_COND + "\n";
                    BILL = BILL + "PENALTY UNI: " + PENALTY_UNIT + "\n";
                    BILL = BILL + "--------------------------------\n";
                    BILL = BILL + "CUS MOBILE-: " + PHONE + "\n";
                    BILL = BILL + "NID--------: " + NID + "\n";
                    BILL = BILL + "IMPOSED BY-: " + IMPOSED_BY_DESC + "\n";
                    BILL = BILL + "REASON-----: " + BILL_REASON_DESC + "\n";
                    BILL = BILL + "--------------------------------\n";
                    BILL = BILL + "TOTAL PRIN-: " + TOT_PRINCPAL_AMOUNT + "\n";
                    BILL = BILL + "TOTAL VAT--: " + TOT_VAT_AMOUNT + "\n";
                    BILL = BILL + "VAT PERCEN-: " + VAT_PERCENT + "%\n";
                    BILL = BILL + "TOTAL BILL-: " + TOTAL_BILL_AMOUNT + "\n"; **/
//                } else {
//                    BILL = BILL + "--------------------------------\n";
//                    BILL = BILL + "LOCATION---: " + LOCATION_CODE + "-" + LOCATION_NAME + "\n";
//                    BILL = BILL + "--------------------------------\n";
//
//                    BILL = BILL + "CONSUMER NO: " + CUSTOMER_NUM + "\n";
//                    BILL = BILL + "NAME-------: " + CUST_NAME + "\n";
//                    BILL = BILL + "ADDRESS----: " + CUST_ADDRESS + "\n";
//                    BILL = BILL + "MONTH------: " + BILL_MONTH + "\n";
//                    BILL = BILL + "BOOK NUM---: " + AREA + "\n";
//
//                    BILL = BILL + "BILL GROUP-: " + BILL_GROUP + "\n";
//                    BILL = BILL + "WALK ORDER-: " + WALK_ORDER + "\n";
//                    BILL = BILL + "TARIFF-----: " + TARIFF + "\n";
//                    BILL = BILL + "BILL NO----: " + BILL_NUM + "-" + BILL_NUM_CHK_DIGIT + "\n";
//                    BILL = BILL + "ISSUE DATE-: " + BILL_ISSUE_DATE + "\n";
//                    BILL = BILL + "DUE DATE---: " + DUE_DATE + "\n";
//                    BILL = BILL + "--------------------------------\n";
//                    BILL = BILL + "METER NUM--: " + METER_NUM + "\n";
//                    BILL = BILL + "METER TYPE-: " + METER_TYPE + "\n";
//                    BILL = BILL + "METER COND-: " + METER_COND + "\n";
//                    BILL = BILL + "PENALTY UNI: " + PENALTY_UNIT + "\n";
//                    BILL = BILL + "--------------------------------\n";
//                    BILL = BILL + "CUS MOBILE-: " + PHONE + "\n";
//                    BILL = BILL + "NID--------: " + NID + "\n";
//                    BILL = BILL + "IMPOSED BY-: " + IMPOSED_BY_DESC + "\n";
//                    BILL = BILL + "REASON-----: " + BILL_REASON_DESC + "\n";
//                    BILL = BILL + "--------------------------------\n";
//                    BILL = BILL + INS_AMT;
//                    BILL = BILL + "--------------------------------\n";
//                    BILL = BILL + "TOTAL PRIN-: " + TOT_PRINCPAL_AMOUNT + "\n";
//                    BILL = BILL + "TOTAL VAT--: " + TOT_VAT_AMOUNT + "\n";
//                    BILL = BILL + "VAT PERCEN-: " + VAT_PERCENT + "%\n";
//                    BILL = BILL + "TOTAL BILL-: " + TOTAL_BILL_AMOUNT + "\n";
//                }

//                outputStream1.write(PrinterCommands.ESC_ALIGN_LEFT);
//                outputStream1.write(PrinterCommands.FEED_LINE);
//                outputStream1.write(PrinterCommands.ESC_ALIGN_LEFT);
//                outputStream1.write(BILL.getBytes());
//                Thread.sleep(1000);
                outputStream1.write(PrinterCommands.ESC_ALIGN_CENTER);
                outputStream1.write(PrinterCommands.FEED_LINE);
                outputStream1.write(PrinterCommands.FEED_LINE);

//                String qr_data = BILL_NUM + BILL_NUM_CHK_DIGIT + "-" + CUSTOMER_NUM + "-" + CUST_NAME + "-" + TOTAL_BILL_AMOUNT;
//                Bitmap bm = QRCode.from(qr_data).withSize(250, 250).bitmap();
//                byte[] cmd = Utils.decodeBitmap(bm);
//                outputStream.write(cmd);
//                Thread.sleep(1000);
//                outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
//                outputStream.write(PrinterCommands.FEED_LINE);
//                outputStream.write(PrinterCommands.FEED_LINE);
//                outputStream.write(PrinterCommands.ESC_ALIGN_LEFT);
//                Thread.sleep(1000);


                if (mBluetoothSocket != null) {
                    outputStream.close();
                    outputStream1.close();
                    closeSocket(mBluetoothSocket);
                }
            } catch (Exception e) {
                Log.e("MainActivity", "Exe ", e);
                if (mBluetoothSocket != null)
                    closeSocket(mBluetoothSocket);

            }

            return mDeviceAddress;
        }

        @Override
        protected void onPostExecute(String result) {
            if (pd != null) {
                pd.dismiss();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}