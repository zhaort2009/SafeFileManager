package com.sdt.safefilemanager.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;

public abstract interface BleWrapperUiCallbacks
{
  public abstract void uiAvailableServices(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, List<BluetoothGattService> paramList);

  public abstract void uiCharacteristicForService(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, BluetoothGattService paramBluetoothGattService, List<BluetoothGattCharacteristic> paramList);

  public abstract void uiCharacteristicsDetails(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, BluetoothGattService paramBluetoothGattService, BluetoothGattCharacteristic paramBluetoothGattCharacteristic);

  public abstract void uiDeviceConnected(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice);

  public abstract void uiDeviceDisconnected(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice);

  public abstract void uiDeviceFound(BluetoothDevice paramBluetoothDevice, int paramInt, byte[] paramArrayOfByte);

  public abstract void uiFailedWrite(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, BluetoothGattService paramBluetoothGattService, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, String paramString);

  public abstract void uiGotNotification(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, BluetoothGattService paramBluetoothGattService, BluetoothGattCharacteristic paramBluetoothGattCharacteristic);

  public abstract void uiNewRssiAvailable(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, int paramInt);

  public abstract void uiNewValueForCharacteristic(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, BluetoothGattService paramBluetoothGattService, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, String paramString1, int paramInt, byte[] paramArrayOfByte, String paramString2);

  public abstract void uiSuccessfulWrite(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, BluetoothGattService paramBluetoothGattService, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, String paramString);

  public abstract void uiReceiveUpdate(String nums, String receiveSpeed);
  public abstract void uiWriteDataToCharacteristic(byte[] value);
  public abstract void uiReceivedStackMsg(String receivedMsg, String state, byte[] data);

  
  public static class Null
    implements BleWrapperUiCallbacks
  {
    public void uiAvailableServices(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, List<BluetoothGattService> paramList)
    {
    }

    public void uiCharacteristicForService(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, BluetoothGattService paramBluetoothGattService, List<BluetoothGattCharacteristic> paramList)
    {
    }

    public void uiCharacteristicsDetails(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, BluetoothGattService paramBluetoothGattService, BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
    {
    }

    public void uiDeviceConnected(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice)
    {
    }

    public void uiDeviceDisconnected(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice)
    {
    }

    public void uiDeviceFound(BluetoothDevice paramBluetoothDevice, int paramInt, byte[] paramArrayOfByte)
    {
    }

    public void uiFailedWrite(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, BluetoothGattService paramBluetoothGattService, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, String paramString)
    {
    }

    public void uiGotNotification(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, BluetoothGattService paramBluetoothGattService, BluetoothGattCharacteristic paramBluetoothGattCharacteristic)
    {
    }

    public void uiNewRssiAvailable(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, int paramInt)
    {
    }

    public void uiNewValueForCharacteristic(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, BluetoothGattService paramBluetoothGattService, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, String paramString1, int paramInt, byte[] paramArrayOfByte, String paramString2)
    {
    }

    public void uiSuccessfulWrite(BluetoothGatt paramBluetoothGatt, BluetoothDevice paramBluetoothDevice, BluetoothGattService paramBluetoothGattService, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, String paramString)
    {
    }
    public void  uiReceiveUpdate(String nums , String receiveSpeed)
    {	
    }
    public void uiWriteDataToCharacteristic(byte[] value)
    {
    }
    public void uiReceivedStackMsg(String receivedMsg , String state, byte [] data)
    {    	
    }
  }
}

/* Location:           F:\android\com.wutl.ble.tools_BLE调试助手\classes_dex2jar.jar
 * Qualified Name:     com.wutl.ble.tools.BleWrapperUiCallbacks
 * JD-Core Version:    0.6.0
 */