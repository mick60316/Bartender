package com.example.bletest;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements View.OnTouchListener {
    private BleService mBle;
    private Intent BLEServerIntent;
    private Handler mHandler;
    private List<String> DEVICE_NAMES = new ArrayList<>(Arrays.asList("hahahahahaha"));
    private Button [] RelayBts=new Button[8];
    private Button testButton;
    private  final String RalayCode = "r";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new MyHandler(this);
        testButton=(Button)findViewById(R.id.testButton);
        RelayBts[0]=(Button)findViewById(R.id.button1);
        RelayBts[1]=(Button)findViewById(R.id.button2);
        RelayBts[2]=(Button)findViewById(R.id.button3);
        RelayBts[3]=(Button)findViewById(R.id.button4);
        RelayBts[4]=(Button)findViewById(R.id.button5);
        RelayBts[5]=(Button)findViewById(R.id.button6);
        RelayBts[6]=(Button)findViewById(R.id.button7);
        RelayBts[7]=(Button)findViewById(R.id.button8);
        for (int i =0;i<RelayBts.length;i++)
        {
            RelayBts[i].setOnTouchListener(this);
            RelayBts[i].setTag(""+i);
        }
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendBleMsg(DEVICE_NAMES.get(0)+",a01111110");
            }
        });
        prepareBLE();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            SendBleMsg(DEVICE_NAMES.get(0)+",r"+v.getTag()+"1000000");
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            SendBleMsg(DEVICE_NAMES.get(0)+",r"+v.getTag()+"0000000");
        }
        return false ;
    }

    private void SendBleMsg(String _msg){
        String[] msgs = _msg.split(",");
        if(mBle!=null)
            mBle.writeCharacteristic(msgs[0],msgs[1]);
    }


    private void prepareBLE() {
        //region 請求權限 android 6.0+
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            //判断是否需要 向用户解释，为什么要申请该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {

            }
        }
        //endregion

        //region 綁定service
        BLEServerIntent = new Intent(this, BleService.class);
        bindService(BLEServerIntent, BLEConnection, Context.BIND_AUTO_CREATE);
        //endregion
    }
    public ServiceConnection BLEConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("Mike", "onServiceConnected");
            //取得service的實體
            mBle = ((BleService.LocalBinder) iBinder).getService();
            //設定BLE Device name
            mBle.setBleDeviceNames(DEVICE_NAMES);
            //取得service的callback，在這邊是顯示接收BLE的資訊
            mBle.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String BleString = (String) msg.obj;
                    System.out.println("GET BLE Message "+BleString);

                    //mActivity.get().textView1.setText(BleString);
                    break;
                default:
                    break;
            }
        }
    }
}
