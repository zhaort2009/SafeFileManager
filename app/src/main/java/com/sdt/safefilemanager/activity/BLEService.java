package com.sdt.safefilemanager.activity;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.sdt.safefilemanager.activity.BleWrapperUiCallbacks.Null;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class BLEService extends Service {

	public static final String ACTION = "com.hs2800.receive.intent.action.BLEReceiver";
	private static final String TAG = "BLE";
	private static final BleWrapperUiCallbacks NULL_CALLBACK = new Null();

	private final BluetoothGattCallback mBleCallback = new BluetoothGattCallback() {
		public void onCharacteristicChanged(BluetoothGatt gatt,
											BluetoothGattCharacteristic characteristic) {
			BLEService.this.getCharacteristicValue(characteristic);
			BLEService.this.mUiCallback.uiGotNotification(
					BLEService.this.mBluetoothGatt,
					BLEService.this.mBluetoothDevice,
					BLEService.this.mBluetoothSelectedService, characteristic);
		}

		public void onCharacteristicRead(BluetoothGatt gatt,
										 BluetoothGattCharacteristic characteristic,
										 int status) {
			//读取到值，在这里读数据
			if (status == BluetoothGatt.GATT_SUCCESS) {
				BLEService.this.getCharacteristicValue(characteristic);
			}

		}

		public void onCharacteristicWrite(BluetoothGatt gatt,
										  BluetoothGattCharacteristic characteristic,
										  int status) {
			String var4 = gatt.getDevice().getName();
			String var5 = characteristic.getService().getUuid().toString().toLowerCase(Locale.getDefault());
			String var6 = characteristic.getUuid().toString().toLowerCase(Locale.getDefault());
			String var7 = "Device: " + var4 + " Service: " + var5
					+ " Characteristic: " + var6;
			if (status == BluetoothGatt.GATT_SUCCESS) {
				BLEService.this.mUiCallback.uiSuccessfulWrite(
						BLEService.this.mBluetoothGatt,
						BLEService.this.mBluetoothDevice,
						BLEService.this.mBluetoothSelectedService, characteristic, var7);
			} else {
				BLEService.this.mUiCallback.uiFailedWrite(
						BLEService.this.mBluetoothGatt,
						BLEService.this.mBluetoothDevice,
						BLEService.this.mBluetoothSelectedService, characteristic, var7
								+ " STATUS = " + status);
			}
		}

		public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
			//收到设备notify值 （设备上报值）
			if (newState == BluetoothProfile.STATE_CONNECTED) {
				BLEService.this.mConnected = true;
				BLEService.this.mUiCallback.uiDeviceConnected( //在需要的页面进行回调函数的实现或者改用发广播的方式通知页面更新
						BLEService.this.mBluetoothGatt,
						BLEService.this.mBluetoothDevice);
				Log.i(TAG, "Connected to GATT server.");
				BLEService.this.mBluetoothGatt.readRemoteRssi();
				BLEService.this.startServicesDiscovery(); // Attempts to discover services after successful connection.
				Log.i(TAG, "Attempting to start service discovery:" +  mBluetoothGatt.discoverServices());
			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				BLEService.this.mConnected = false;
				if (BLEService.this.mBluetoothGatt != null) {
					BLEService.this.mBluetoothGatt.disconnect();
					refreshDeviceCache(BLEService.this.mBluetoothGatt);
					BLEService.this.mBluetoothGatt.close();
					BLEService.this.mBluetoothGatt = null;
				}

				BLEService.this.mUiCallback.uiDeviceDisconnected( //在需要的页面进行回调函数的实现或者改用发广播的方式通知页面更新
						BLEService.this.mBluetoothGatt,
						BLEService.this.mBluetoothDevice);
				Log.i(TAG, "Disconnected from GATT server.");
				return;
			}

		}

		public void onReadRemoteRssi(BluetoothGatt var1, int var2, int var3) {
			if (var3 == 0) {
				BLEService.this.mUiCallback.uiNewRssiAvailable(
						BLEService.this.mBluetoothGatt,
						BLEService.this.mBluetoothDevice, var2);
			}

		}

		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				BLEService.this.getSupportedServices();
			}
			else{
				Log.w(TAG, "onServicesDiscovered received: " + status);
				System.out.println("onServicesDiscovered received: " + status);
			}
		}
	};
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothDevice mBluetoothDevice = null;
	private BluetoothGatt mBluetoothGatt = null;
	private List<BluetoothGattService> mBluetoothGattServices = null;
	private BluetoothGattService mBluetoothSelectedService = null;
	private boolean mConnected = false;
	private String mDeviceAddress = "";
	private LeScanCallback mDeviceFoundCallback = new LeScanCallback() {
		public void onLeScan(BluetoothDevice var1, int var2, byte[] var3) {
			BLEService.this.mUiCallback.uiDeviceFound(var1, var2, var3);
		}
	};
	private Activity mParent = null;
	private boolean mTimerEnabled = false;
	private Handler mTimerHandler = new Handler();
	private BleWrapperUiCallbacks mUiCallback = null;

	public void addCallback(BleWrapperUiCallbacks callback) {
		System.out.println("addCallback: " + callback);
		mUiCallback = callback;
	}

	public boolean checkBleHardwareAvailable() {
		BluetoothManager var1 = (BluetoothManager) (BluetoothManager) this.getSystemService(BLUETOOTH_SERVICE);
		return var1 != null && var1.getAdapter() != null ? BLEService.this
				.getPackageManager().hasSystemFeature(
						"android.hardware.bluetooth_le") : false;
	}

	public void close() {
		if (this.mBluetoothGatt != null) {
			this.mBluetoothGatt.close();
		}

		this.mBluetoothGatt = null;
	}

	public boolean connect(String var1) {
		if (this.mBluetoothAdapter != null && var1 != null) {
			this.mDeviceAddress = var1;
			if (this.mBluetoothGatt != null
					&& this.mBluetoothGatt.getDevice().getAddress()
					.equals(var1)) {
				return this.mBluetoothGatt.connect();
			}

			this.mBluetoothDevice = this.mBluetoothAdapter
					.getRemoteDevice(this.mDeviceAddress);
			if (this.mBluetoothDevice != null) {
				this.mBluetoothGatt = this.mBluetoothDevice.connectGatt(
						this, false, this.mBleCallback); //this.mParent
				return true;
			}
		}

		return false;
	}

	public void diconnect() {
		if (this.mBluetoothGatt != null) {
			this.mBluetoothGatt.disconnect();
			refreshDeviceCache(this.mBluetoothGatt);
			this.mBluetoothGatt.close();
			this.mBluetoothGatt = null;
		}

		this.mUiCallback.uiDeviceDisconnected(this.mBluetoothGatt,
				this.mBluetoothDevice);

	}

	/**
	 * 清理本地的BluetoothGatt 的缓存，以保证在蓝牙连接设备的时候，设备的服务、特征是最新的
	 * @param gatt
	 * @return
	 */
	public boolean refreshDeviceCache(BluetoothGatt gatt) {
		if(null != gatt){
			try {
				BluetoothGatt localBluetoothGatt = gatt;
				Method localMethod = localBluetoothGatt.getClass().getMethod( "refresh", new Class[0]);
				if (localMethod != null) {
					boolean bool = ((Boolean) localMethod.invoke(
							localBluetoothGatt, new Object[0])).booleanValue();
					return bool;
				}
			} catch (Exception localException) {
				localException.printStackTrace();
			}
		}
		return false;
	}

	public BluetoothAdapter getAdapter() {
		return this.mBluetoothAdapter;
	}

	public BluetoothGattService getCachedService() {
		return this.mBluetoothSelectedService;
	}

	public List<BluetoothGattService> getCachedServices() {
		return this.mBluetoothGattServices;
	}

	public void getCharacteristicValue(
			BluetoothGattCharacteristic characteristic) {
		Log.w(TAG, "getCharacteristicValue received!");
		if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null
				&& characteristic != null) {
			byte[] bytes = characteristic.getValue();
			characteristic.getUuid();
			int value = 0;
			if (bytes.length > 0)
				value = bytes[0];
			if (bytes.length > 1)
				value += bytes[1] << 8;
			if (bytes.length > 2)
				value += bytes[2] << 16;
			if (bytes.length > 3)
				value += bytes[3] << 32;
			Log.d(TAG,"getCharacteristicValue length="+Integer.toString(bytes.length));

			mUiCallback.uiNewValueForCharacteristic(mBluetoothGatt,
					mBluetoothDevice, mBluetoothSelectedService,
					characteristic, new String(bytes), value, bytes,
					dateFormat.format(new Date()));
		}
	}

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy.MM.dd HH:mm:ss.SSS");

	public void getCharacteristicsForService(BluetoothGattService var1) {
		if (var1 != null) {
			List var2 = var1.getCharacteristics();
			this.mUiCallback.uiCharacteristicForService(this.mBluetoothGatt,
					this.mBluetoothDevice, var1, var2);
			this.mBluetoothSelectedService = var1;
		}
	}

	public BluetoothDevice getDevice() {
		return this.mBluetoothDevice;
	}
	/*zhh add*/
	public int getBondState(){
		return this.mBluetoothDevice.getBondState();
	}

	public BluetoothGatt getGatt() {
		return this.mBluetoothGatt;
	}

	public void getSupportedServices() {
		if (this.mBluetoothGattServices != null
				&& this.mBluetoothGattServices.size() > 0) {
			this.mBluetoothGattServices.clear();
		}

		if (this.mBluetoothGatt != null) {
			this.mBluetoothGattServices = this.mBluetoothGatt.getServices();
		}

		this.mUiCallback.uiAvailableServices(this.mBluetoothGatt,
				this.mBluetoothDevice, this.mBluetoothGattServices);
	}

	public int getValueFormat(BluetoothGattCharacteristic var1) {
		int var2 = var1.getProperties();
		return (var2 & 52) != 0 ? 52 : ((var2 & 50) != 0 ? 50
				: ((var2 & 34) != 0 ? 34 : ((var2 & 36) != 0 ? 36
				: ((var2 & 33) != 0 ? 33 : ((var2 & 18) != 0 ? 18
				: ((var2 & 20) != 0 ? 20
				: ((var2 & 17) != 0 ? 17 : 0)))))));
	}

	public boolean initialize() {
		if(!this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
			Toast.makeText(this, "Ble is not be supported!", Toast.LENGTH_LONG).show();
			System.out.println("Ble is not be supported!");
			//finish();
		}

		final BluetoothManager mBluetoothManager = (BluetoothManager) this.getSystemService(BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();

		if(this.mBluetoothAdapter ==  null){
			Toast.makeText(this, "Bluetooth is not be supported!", Toast.LENGTH_LONG).show();
			System.out.println("Bluetooth is not be supported!");
			//finish();
		}
		else
		{
			if (!mBluetoothAdapter.isEnabled())
			{
				System.out.println("Open the Bluetooth!");
				Toast.makeText(this, "Open the Bluetooth!", Toast.LENGTH_LONG).show();
				mBluetoothAdapter.enable();
			}
		}

		return this.mBluetoothAdapter != null;
	}

	public boolean isBtEnabled() {
		BluetoothManager var1 = (BluetoothManager) this.getSystemService(BLUETOOTH_SERVICE);
		if (var1 != null) {
			BluetoothAdapter var2 = var1.getAdapter();
			if (var2 != null) {
				return var2.isEnabled();
			}
		}

		return false;
	}

	public boolean isConnected() {
		return this.mConnected;
	}

	public void readPeriodicalyRssiValue(boolean var1) {
		this.mTimerEnabled = var1;
		if (this.mConnected && this.mBluetoothGatt != null
				&& this.mTimerEnabled) {
			this.mTimerHandler.postDelayed(new Runnable() {
				public void run() {
					if (BLEService.this.mBluetoothGatt != null
							&& BLEService.this.mBluetoothAdapter != null
							&& BLEService.this.mConnected) {
						BLEService.this.mBluetoothGatt.readRemoteRssi();
						BLEService.this
								.readPeriodicalyRssiValue(BLEService.this.mTimerEnabled);
					} else {
						BLEService.this.mTimerEnabled = false;
					}
				}
			}, 1500L);
		} else {
			this.mTimerEnabled = false;
		}
	}

	public void requestCharacteristicValue(BluetoothGattCharacteristic characteristic) {
		if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
			this.mBluetoothGatt.readCharacteristic(characteristic);
		}else{
			Log.w(TAG, "BluetoothAdapter not initialized");
		}
	}

	public void setNotificationForCharacteristic(
            BluetoothGattCharacteristic var1, boolean var2) {
		Log.d(TAG, "setNotificationForCharacteristic!");
		if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
			if (!this.mBluetoothGatt.setCharacteristicNotification(var1, var2)) {
				Log.e("------",
						"Seting proper notification status for characteristic failed!");
			}

			BluetoothGattDescriptor var3 = var1.getDescriptor(UUID
					.fromString("00002902-0000-1000-8000-00805f9b34fb"));
			if (var3 != null) {
				byte[] var4;
				if (var2) {
					var4 = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
				} else {
					var4 = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
				}

				var3.setValue(var4);
				this.mBluetoothGatt.writeDescriptor(var3);
				return;
			}
		}

	}

	public void startMonitoringRssiValue() {
		this.readPeriodicalyRssiValue(true);
	}

	public void startScanning() {
		Log.d(TAG,"startScanning...");
		this.mBluetoothAdapter.startLeScan(this.mDeviceFoundCallback);
	}

	public void startServicesDiscovery() {
		if (this.mBluetoothGatt != null) {
			this.mBluetoothGatt.discoverServices();
		}

	}

	public void stopMonitoringRssiValue() {
		this.readPeriodicalyRssiValue(false);
	}

	public void stopScanning() {
		Log.d(TAG,"stopScanning...");
		this.mBluetoothAdapter.stopLeScan(this.mDeviceFoundCallback);
	}

	public void writeDataToCharacteristic(BluetoothGattCharacteristic characteristic,
										  byte[] value) {
		Log.w(TAG, "writeDataToCharacteristic");
		String str =  characteristic.getUuid().toString();
		if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null
				&& characteristic != null) {
			Log.w(TAG, "write...");
			characteristic.setValue(value);
			this.mBluetoothGatt.writeCharacteristic(characteristic);
		}else{
			Log.w(TAG, "BluetoothAdapter not initialized");
		}
	}

	private BluetoothGattCharacteristic mwCharacteristic = null;
	public void setCurrentWriteCharacteristics(BluetoothGattCharacteristic characteristic) {
		this.mwCharacteristic = characteristic;
	}

	public BluetoothGattCharacteristic getCurrentWriteCharacteristics() {
		return this.mwCharacteristic;
	}


	public final class LocalBinder extends Binder {
		public BLEService getService() {
			return BLEService.this;
		}
	}

	@Override
	public void onCreate(){
		System.out.println("*****Service onCreate");
		super.onCreate();
		initialize();
		System.out.println("*****Service onCreate end");
	}

	public BLEService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		//throw new UnsupportedOperationException("Not yet implemented");
		System.out.println("*****Service onBind");
		return new LocalBinder();
	}

	@Override
	public void onRebind(Intent intent){
		System.out.println("*****Service onRebind() Intent="+intent);
		super.onRebind(intent);
	}
	@Override
	public boolean onUnbind(Intent intent){
		System.out.println("*****Service onUnbind() Intent="+intent);
		return super.onUnbind(intent);
	}
	@Override
	public void onDestroy(){
		System.out.println("*****Service onDestroy");
		super.onDestroy();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("*****Service onStart()-->Intent=" + intent + ",startId=" + startId);
		return Service.START_STICKY;
	}


}
