package com.iorbit.iorbithealthapp.Devices.Buletooth;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.Models.SaveMeasureModel;
import com.iorbit.iorbithealthapp.Models.StatusResponseModel;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.Utils.StatusDialog;

import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BPDrTrust1 {


    private BluetoothGatt mGatt;
    private BluetoothGattCharacteristic mBPDeviceWrite;
    private final Context mContext;

    private final UUID BPServiceUUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    private final UUID BPDevNotifyUUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private final UUID BPDeviceWriteUUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb");

    private static final byte POWER_STATUS = 5;
    private static final byte DEVICE_TIME = 8;
    private static final byte BP_CURRENT_POWER_PRESSURE = 3;
    private static final byte MEASURE_RESULTS = 4;
    private static final byte MEASURE_EXCEPTION = 2;
    private int systolic;
    private int dia;
    boolean discOnnected = false;
    private boolean measureSave = false;
    private int pulse;
    androidx.appcompat.app.AlertDialog userDialog = null;

    public BPDrTrust1(Context context) {
        mContext = context;
    }

    @SuppressLint("MissingPermission")
    public void connectDevice(String deviceName) {
        try {
            BluetoothManager btManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter btAdapter = btManager.getAdapter();
            BluetoothDevice device = btAdapter.getRemoteDevice(deviceName);
            mGatt = device.connectGatt(mContext, false, mGattCallback);
            StatusDialog.showDialogMessage(mContext,"Measurement Status", "Connecting..");
            StatusDialog.setOnNegativeButtonClickedListener("Cancel", new StatusDialog.OnNegativeButtonClickedListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onNegativeButtonClicked() {
                    mGatt.disconnect();
                    mGatt.close();
                    discOnnected = true;
                    StatusDialog.close();
                }
            });
            StatusDialog.setOnPositiveButtonClickedListener("Close", new StatusDialog.OnPositiveButtonClickedListener() {
                @Override
                public void onPositiveButtonClicked() {
                    mGatt.disconnect();
                    mGatt.close();
                    discOnnected = true;
                    Toast.makeText(mContext,"Nothing to save, Try after completing the measurement",Toast.LENGTH_LONG ).show();
                }
            });

        } catch (Exception exp) {
            Log.i("BLE Exception", exp.getMessage());
        }
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (measureSave) {
                StatusDialog.setMessage("Measurement Saved Successfully");
            } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                StatusDialog.setMessage("Connected!!");
                StatusDialog.setOnNegativeButtonClickedListener("Cancel", new StatusDialog.OnNegativeButtonClickedListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onNegativeButtonClicked() {
                        mGatt.disconnect();
                        mGatt.close();
                        discOnnected = true;
                        StatusDialog.close();
                    }
                });
                mGatt.discoverServices();
                measureSave = false;
            } else {
                StatusDialog.setMessage("Not Connected");
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (measureSave) {

                stopMeasurement();
                mGatt.disconnect();
                mGatt.close();
            } else if (status == BluetoothGatt.GATT_SUCCESS && !measureSave) {
                BluetoothGattService mBPService = gatt.getService(BPServiceUUID);
                BluetoothGattCharacteristic mBPDevNotify = mBPService.getCharacteristic(BPDevNotifyUUID);
                mBPDeviceWrite = mBPService.getCharacteristic(BPDeviceWriteUUID);
                gatt.setCharacteristicNotification(mBPDevNotify, true);
                //Utils.showSnackbar(null, "Starting Measurement", com.sensesemi.hospital.Bluetooth.Constants.HIGH, Snackbar.LENGTH_INDEFINITE);
                StatusDialog.setMessage("Measuring..");
                startMeasurement();
            } else {
//               stopMeasurement();
                mGatt.disconnect();
                mGatt.close();

            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                byte command_id = data[0];
                if (command_id == POWER_STATUS) {
                    //Power status
                } else if (command_id == DEVICE_TIME) {
                    //Device Time
                } else if (command_id == BP_CURRENT_POWER_PRESSURE) {
                    //BP Current Power,Pressure
                } else if (command_id == MEASURE_RESULTS) {
                    systolic = ( int) data[2]&0xff;
                    dia = (int) data[3];
                    pulse = (int) data[4];
                    StatusDialog.setMessage("Sys:" + systolic + " Dia :" + dia + " Pulse :" + pulse);
                    StatusDialog.setOnPositiveButtonClickedListener("Save", new StatusDialog.OnPositiveButtonClickedListener() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onPositiveButtonClicked() {
                            stopMeasurement();
                            gatt.disconnect();
                            gatt.close();
                            discOnnected = true;
                            saveMeasurement();
                            measureSave = true;
                            StatusDialog.close();
                        }
                    });

                } else if (command_id == MEASURE_EXCEPTION) {
                    //Measure exception
                    //Utils.showSnackbar(null, "Measuring Failed", com.sensesemi.hospital.Bluetooth.Constants.HIGH,Snackbar.LENGTH_INDEFINITE);
                    StatusDialog.setMessage("Measurement Failed");
                }
            }
        }
    };

    @SuppressLint("MissingPermission")
    public void startMeasurement() {
        byte[] CS_START = new byte[]{0, 2, 3, 64, 69};
        mBPDeviceWrite.setValue(CS_START);
        mGatt.writeCharacteristic(mBPDeviceWrite);
    }

    @SuppressLint("MissingPermission")
    public void stopMeasurement() {
        byte[] CS_STOP = new byte[]{0, 2, 3, 68, 73};
        mBPDeviceWrite.setValue(CS_STOP);
        mGatt.writeCharacteristic(mBPDeviceWrite);
    }

    @SuppressLint("MissingPermission")
    public void sendACK() {
        byte[] CR_ACK = new byte[]{0, 1, 1, 2};
        mBPDeviceWrite.setValue(CR_ACK);
        mGatt.writeCharacteristic(mBPDeviceWrite);
    }

    @SuppressLint("MissingPermission")
    public void sendNACK() {
        byte[] CR_NAK = new byte[]{0, 1, 0, 1};
        mBPDeviceWrite.setValue(CR_NAK);
        mGatt.writeCharacteristic(mBPDeviceWrite);
    }


    private void showDialogSave(String title, String body) {

        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        final androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        builder.setTitle(title).setMessage(body).setNeutralButton("Save", new DialogInterface.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    saveMeasurement();
                    measureSave = true;

                    Log.d(TAG, "onClick: " + measureSave);
                    // showDialogMessage("Saved Successfully","");
                    mGatt.disconnect();
                    stopMeasurement();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder2
                .setTitle(title).setMessage(body).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        mGatt.disconnect();
                    }
                });
        userDialog = builder.create();
        userDialog.show();

    }




    public void saveMeasurement() {

        int[] intVal = {dia, systolic, pulse};
        String[] BpFraction = {"BPDia", "BPSys", ""};
        String[] BpName = {"BP", "BP", "BPM"};
        RetrofitClient retrofit = new RetrofitClient();
        Retrofit retrofitClient = retrofit.getRetrofitInstance(mContext);
        if (retrofitClient == null) {
            return;
        }

        for (int i = 0; i < intVal.length; i++) {
            SaveMeasureModel measure = new SaveMeasureModel();
            measure.setParamName(BpName[i]);
            measure.setParamFraction(BpFraction[i]);
            measure.setDevmodelId("2ab90e73-99c5-11eb-853f-e9af88721123");
            measure.setDevId("852a2034-c8dd-11eb-a396-755a8569ff4d");
            measure.setIntVal(String.valueOf(intVal[i]));
            measure.setPatientId(new SharedPreference(mContext).getCurrentPAtient().getSsid());
            Call<StatusResponseModel> call = retrofitClient.create(ServiceApi.class).saveMeasure(new SharedPreference(mContext).getCurrentPAtient().getSsid(),measure);
            call.enqueue(new Callback<StatusResponseModel>() {
                @Override
                public void onResponse(Call<StatusResponseModel> call, Response<StatusResponseModel> response) {
                    if(response.isSuccessful()) {
                        StatusResponseModel saveMeasureModel = response.body();
                        if(saveMeasureModel.getStatus().getMessage().equalsIgnoreCase("Success")){
                            Toast.makeText(mContext, saveMeasureModel.getStatus().getDetails(), Toast.LENGTH_SHORT).show();
                            StatusDialog.close();
                        }else {
                            Toast.makeText(mContext, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(mContext, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<StatusResponseModel> call, Throwable t) {
                    Toast.makeText(mContext, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }
            });

        }


        }

    }


