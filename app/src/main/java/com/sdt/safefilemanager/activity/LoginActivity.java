package com.sdt.safefilemanager.activity;

import android.app.Activity;
import android.app.Instrumentation;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sdt.safefilemanager.activity.BLEService.LocalBinder;
import com.sdt.safefilemanager.R;
import com.sdt.safefilemanager.helper.DAOHandler;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends Activity implements BleWrapperUiCallbacks, View.OnClickListener {

    static String TAG = "LoginActivity";
    private boolean mScanning = false;
    private Button scan_button = null;

    private TextView text_view_pwd0 = null;
    private TextView text_view_pwd1 = null;
    private TextView text_view_pwd2 = null;
    private TextView text_view_pwd3 = null;

    private TextView text_view_tip = null;

    private Button button_1 = null;
    private Button button_2 = null;
    private Button button_3 = null;
    private Button button_4 = null;
    private Button button_5 = null;
    private Button button_6 = null;
    private Button button_7 = null;
    private Button button_8 = null;
    private Button button_9 = null;
    private Button button_0 = null;
    private Button button_point = null;
    private Button button_del = null;

    Timer m_timer = null;
    Handler scanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    scan_button.performClick();
                    break;
                case 2:
                    text_view_tip.setText("扫描成功，登录中..");
                    Intent intent = new Intent(LoginActivity.this, MainHomeActivity.class);
                    LoginActivity.this.startActivity(intent);
                    LoginActivity.this.finish();
                    break;
                case 3:
                    text_view_tip.setText("蓝牙模块不匹配！");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        getApplicationContext().getFilesDir().getPath();

        scan_button = (Button) this.findViewById(R.id.ScanButton);
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginActivity.this.mScanning) {
                    LoginActivity.this.mScanning = false;
                    mService.stopScanning();
                    scan_button.setText("开始扫描");
                    text_view_tip.setText("未扫描到加密蓝牙模块！");
                    text_view_pwd0.setText("");
                    text_view_pwd1.setText("");
                    text_view_pwd2.setText("");
                    text_view_pwd3.setText("");
                }
                else {
                    if (mService.isConnected()){
                        mService.diconnect();
                    }
                    LoginActivity.this.mScanning = true;
                    mService.startScanning();
                    scan_button.setText("停止扫描");
                    text_view_tip.setText("扫描加密蓝牙模块..");
                }
            }
        });
    }

    private void initView() {
        text_view_pwd0 = (TextView)this.findViewById(R.id.text_view_pwd0);
        text_view_pwd0.setInputType(InputType.TYPE_NULL);
        text_view_pwd1 = (TextView)this.findViewById(R.id.text_view_pwd1);
        text_view_pwd1.setInputType(InputType.TYPE_NULL);
        text_view_pwd2 = (TextView)this.findViewById(R.id.text_view_pwd2);
        text_view_pwd2.setInputType(InputType.TYPE_NULL);
        text_view_pwd3 = (TextView)this.findViewById(R.id.text_view_pwd3);
        text_view_pwd3.setInputType(InputType.TYPE_NULL);

        text_view_tip = (TextView)this.findViewById(R.id.text_view_tip);
        text_view_tip.setText("请牢记您当前的登录密码！");

        DAOHandler.getInstance().initHandler(getApplicationContext().getFilesDir().getPath());
        if (DAOHandler.getInstance().getLoginPwd().compareTo("") != 0) {
            text_view_pwd0.setTransformationMethod(PasswordTransformationMethod.getInstance());
            text_view_pwd1.setTransformationMethod(PasswordTransformationMethod.getInstance());
            text_view_pwd2.setTransformationMethod(PasswordTransformationMethod.getInstance());
            text_view_pwd3.setTransformationMethod(PasswordTransformationMethod.getInstance());

            text_view_tip.setText("输 入 密 码");
        }

        button_0=(Button)this.findViewById(R.id.button_0);
        button_1=(Button)this.findViewById(R.id.button_1);
        button_2=(Button)this.findViewById(R.id.button_2);
        button_3=(Button)this.findViewById(R.id.button_3);
        button_4=(Button)this.findViewById(R.id.button_4);
        button_5=(Button)this.findViewById(R.id.button_5);
        button_6=(Button)this.findViewById(R.id.button_6);
        button_7=(Button)this.findViewById(R.id.button_7);
        button_8=(Button)this.findViewById(R.id.button_8);
        button_9=(Button)this.findViewById(R.id.button_9);
        button_point=(Button)this.findViewById(R.id.button_point);
        button_del=(Button)this.findViewById(R.id.button_del);
        button_0.setOnClickListener(this);
        button_1.setOnClickListener(this);
        button_2.setOnClickListener(this);
        button_3.setOnClickListener(this);
        button_4.setOnClickListener(this);
        button_5.setOnClickListener(this);
        button_6.setOnClickListener(this);
        button_7.setOnClickListener(this);
        button_8.setOnClickListener(this);
        button_9.setOnClickListener(this);
        button_point.setOnClickListener(this);
        button_del.setOnClickListener(this);
    }

    private void handleFoundDevice(final BluetoothDevice var1, final int var2,
                                   final byte[] var3) {
        String deviceName = var1.getName();
        if (deviceName!=null) {
            Log.d(TAG, "duchong handleFoundDevice: " + deviceName);
//            if (deviceName.compareTo(new String("Hisense BLE Demo")) == 0) {
            if (deviceName.contains("Hisense BLE Demo")) {

                LoginActivity.this.mScanning = false;
                mService.stopScanning();
                scan_button.setText("开始扫描");
                m_timer.cancel();

                Message msg = new Message();

                //如果数据库中没有蓝牙MAC地址，则保存到数据库
                //如果数据库中有蓝牙MAC地址，则判断MAC是否一致
                String dbMac = DAOHandler.getInstance().getBlueToothMac();
                String deviceMac = var1.getAddress();
                if (DAOHandler.getInstance().getBlueToothMac().compareTo("") == 0) {
                    DAOHandler.getInstance().setBlueToothMac(var1.getAddress());

                    msg.what = 2;
                }
                else {
                    if (DAOHandler.getInstance().getBlueToothMac().compareTo(var1.getAddress()) == 0) {

                        msg.what = 2;
                    }
                    else {
                        msg.what = 3;
                    }
                }
                LoginActivity.this.scanHandler.sendMessage(msg);
            }
        }

        this.runOnUiThread(new Runnable() {
            public void run() {
//                LoginActivity.this.mDevicesListAdapter.addDevice(var1, var2,
//                        var3);
//                LoginActivity.this.mDevicesListAdapter
//                        .notifyDataSetChanged();
//                String deviceName = var1.getName();
//                if (deviceName!=null && deviceName.compareTo(new String("Cordio")) == 0){
//                    Toast.makeText(LoginActivity.this, "BlueTooth Device Found", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }

    private BLEService mService = null;
    private boolean serviceBound = false;
    private Messenger serverMsger;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            //Log.w("component name:", name.getClassName());
            System.out.println("####ScanningActivity ServiceConnection ---- component name:" + name.getClassName());
            //zhj test
            mService = ((LocalBinder) binder).getService();
            //将当前activity添加到接口集合中
            mService.addCallback(LoginActivity.this);

            //serverMsger = new Messenger(binder);
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("####LoginActivity onServiceDisconnected!");
            serverMsger = null;
            serviceBound = false;
        }
    };

    @Override
    protected void onStart() {
        System.out.println("####LoginActivity onStart!----BindService");
        super.onStart();
        Intent serviceIntent = new Intent(LoginActivity.this, BLEService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop(){
        System.out.println("LoginActivity onStop ");
        super.onStop();

        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
            serverMsger = null;
        }
    }

    @Override
    protected void onDestroy()
    {
        System.out.println("LoginActivity  onDestroy");
        super.onDestroy();
    }

    protected void onPause() {
        Log.d("DeviceListDebug","Sc---onPause()");
        super.onPause();
        this.mScanning = false;
        if (mService != null) {
            mService.stopScanning();
        }
    }

    protected void onResume() {
        Log.d("DeviceListDebug","Sc--onResume()");
        super.onResume();

//        if(this.mDevicesListAdapter == null) {
//            this.mDevicesListAdapter = new DeviceListAdapter(this);
//        }
//        this.setListAdapter(this.mDevicesListAdapter);
    }


    @Override
    public void uiAvailableServices(BluetoothGatt paramBluetoothGatt,
                                    BluetoothDevice paramBluetoothDevice,
                                    List<BluetoothGattService> paramList) {
        // TODO Auto-generated method stub
    }

    @Override
    public void uiCharacteristicForService(BluetoothGatt paramBluetoothGatt,
                                           BluetoothDevice paramBluetoothDevice,
                                           BluetoothGattService paramBluetoothGattService,
                                           List<BluetoothGattCharacteristic> paramList) {
        // TODO Auto-generated method stub
    }

    @Override
    public void uiCharacteristicsDetails(BluetoothGatt paramBluetoothGatt,
                                         BluetoothDevice paramBluetoothDevice,
                                         BluetoothGattService paramBluetoothGattService,
                                         BluetoothGattCharacteristic paramBluetoothGattCharacteristic) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiDeviceConnected(BluetoothGatt paramBluetoothGatt,
                                  BluetoothDevice paramBluetoothDevice) {
        // TODO Auto-generated method stub
        mService.stopScanning();

    }

    @Override
    public void uiDeviceDisconnected(BluetoothGatt paramBluetoothGatt,
                                     BluetoothDevice paramBluetoothDevice) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiDeviceFound(BluetoothDevice var1, int var2, byte[] var3) { //zhj test
        LoginActivity.this.handleFoundDevice(var1, var2, var3);
    }

    @Override
    public void uiFailedWrite(BluetoothGatt paramBluetoothGatt,
                              BluetoothDevice paramBluetoothDevice,
                              BluetoothGattService paramBluetoothGattService,
                              BluetoothGattCharacteristic paramBluetoothGattCharacteristic,
                              String paramString) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiGotNotification(BluetoothGatt paramBluetoothGatt,
                                  BluetoothDevice paramBluetoothDevice,
                                  BluetoothGattService paramBluetoothGattService,
                                  BluetoothGattCharacteristic paramBluetoothGattCharacteristic) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiNewRssiAvailable(BluetoothGatt paramBluetoothGatt,
                                   BluetoothDevice paramBluetoothDevice, int paramInt) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiNewValueForCharacteristic(BluetoothGatt paramBluetoothGatt,
                                            BluetoothDevice paramBluetoothDevice,
                                            BluetoothGattService paramBluetoothGattService,
                                            BluetoothGattCharacteristic paramBluetoothGattCharacteristic,
                                            String paramString1, int paramInt, byte[] paramArrayOfByte,
                                            String paramString2) {
        // TODO Auto-generated method stub
    }

    @Override
    public void uiSuccessfulWrite(BluetoothGatt paramBluetoothGatt,
                                  BluetoothDevice paramBluetoothDevice,
                                  BluetoothGattService paramBluetoothGattService,
                                  BluetoothGattCharacteristic paramBluetoothGattCharacteristic,
                                  final String paramString) {
        // TODO Auto-generated method stub
    }

    @Override
    public void uiReceiveUpdate(String nums, String receiveSpeed) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiWriteDataToCharacteristic(byte[] value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void uiReceivedStackMsg(String receivedMsg, String state, byte [] data) {
        // TODO Auto-generated method stub

    }

    //模拟键盘输入
    public void performKeyDown(final int keyCode) {
        new Thread() {
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void handleInput(int key_event_id)
    {
        if (key_event_id != KeyEvent.KEYCODE_DEL && text_view_pwd3.getText().toString().compareTo("") != 0)
            return;
        if (key_event_id >= KeyEvent.KEYCODE_0 && key_event_id <= KeyEvent.KEYCODE_9)
        {
            if (text_view_pwd0.getText().toString().compareTo("") == 0)
            {
                Integer i = new Integer(key_event_id - 7);
                text_view_pwd0.setText(i.toString());
            }
            else if (text_view_pwd1.getText().toString().compareTo("") == 0)
            {
                Integer i = new Integer(key_event_id - 7);
                text_view_pwd1.setText(i.toString());
            }
            else if (text_view_pwd2.getText().toString().compareTo("") == 0)
            {
                Integer i = new Integer(key_event_id - 7);
                text_view_pwd2.setText(i.toString());
            }
            else if (text_view_pwd3.getText().toString().compareTo("") == 0)
            {
                Integer i = new Integer(key_event_id - 7);
                text_view_pwd3.setText(i.toString());
            }
        }
        else if (key_event_id == KeyEvent.KEYCODE_NUMPAD_DOT)
        {
            if (text_view_pwd0.getText().toString().compareTo("") == 0)
            {
                text_view_pwd0.setText(".");
            }
            else if (text_view_pwd1.getText().toString().compareTo("") == 0)
            {
                text_view_pwd1.setText(".");
            }
            else if (text_view_pwd2.getText().toString().compareTo("") == 0)
            {
                text_view_pwd2.setText(".");
            }
            else if (text_view_pwd3.getText().toString().compareTo("") == 0)
            {
                text_view_pwd3.setText(".");
            }
        }
        else if (key_event_id == KeyEvent.KEYCODE_DEL)
        {
            if (text_view_pwd3.getText().toString().compareTo("") != 0)
            {
                text_view_pwd3.setText("");
            }
            else if (text_view_pwd2.getText().toString().compareTo("") != 0)
            {
                text_view_pwd2.setText("");
            }
            else if (text_view_pwd1.getText().toString().compareTo("") != 0)
            {
                text_view_pwd1.setText("");
            }
            else if (text_view_pwd0.getText().toString().compareTo("") != 0)
            {
                text_view_pwd0.setText("");
            }
        }
        //密码输入完成
        if (text_view_pwd3.getText().toString().compareTo("") != 0)
        {
            String str_pwd = new String();
            str_pwd = text_view_pwd0.getText().toString() + text_view_pwd1.getText() + text_view_pwd2.getText() + text_view_pwd3.getText();
            if (DAOHandler.getInstance().getLoginPwd().compareTo("") == 0) {
                DAOHandler.getInstance().setLoginPwd(str_pwd);

                scan_button.performClick();
                m_timer = new Timer();
                m_timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Message msg = new Message();
                        msg.what = 1;
                        LoginActivity.this.scanHandler.sendMessage(msg);
                        m_timer.cancel();
                    }
                }, 10000);
            }
            else {
                if (DAOHandler.getInstance().getLoginPwd().compareTo(str_pwd) == 0)
                {
                    scan_button.performClick();
                    m_timer = new Timer();
                    m_timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Message msg = new Message();
                            msg.what = 1;
                            LoginActivity.this.scanHandler.sendMessage(msg);
                            m_timer.cancel();
                        }
                    }, 10000);
                }
                else
                {
                    text_view_tip.setText("登录密码输入错误！");
                    text_view_pwd0.setText("");
                    text_view_pwd1.setText("");
                    text_view_pwd2.setText("");
                    text_view_pwd3.setText("");
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int key_event_id = 0;
        switch (v.getId()) {
            case R.id.button_0:
                key_event_id = KeyEvent.KEYCODE_0;
                break;
            case R.id.button_1:
                key_event_id = KeyEvent.KEYCODE_1;
                break;
            case R.id.button_2:
                key_event_id = KeyEvent.KEYCODE_2;
                break;
            case R.id.button_3:
                key_event_id = KeyEvent.KEYCODE_3;
                break;
            case R.id.button_4:
                key_event_id = KeyEvent.KEYCODE_4;
                break;
            case R.id.button_5:
                key_event_id = KeyEvent.KEYCODE_5;
                break;
            case R.id.button_6:
                key_event_id = KeyEvent.KEYCODE_6;
                break;
            case R.id.button_7:
                key_event_id = KeyEvent.KEYCODE_7;
                break;
            case R.id.button_8:
                key_event_id = KeyEvent.KEYCODE_8;
                break;
            case R.id.button_9:
                key_event_id = KeyEvent.KEYCODE_9;
                break;
            case R.id.button_point:
                key_event_id = KeyEvent.KEYCODE_NUMPAD_DOT;
                break;
            case R.id.button_del:
                key_event_id = KeyEvent.KEYCODE_DEL;
                break;
            default:
                break;
        }
        handleInput(key_event_id);
    }
}
