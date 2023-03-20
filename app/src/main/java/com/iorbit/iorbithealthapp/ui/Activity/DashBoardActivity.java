package com.iorbit.iorbithealthapp.ui.Activity;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.iorbit.iorbithealthapp.Adapters.BluetoothDevicesAdapter;
import com.iorbit.iorbithealthapp.Adapters.ScanDeviceListAdapter;
import com.iorbit.iorbithealthapp.Devices.Bluetooth.BPDrTrust1;
import com.iorbit.iorbithealthapp.Devices.CommonDataArea;
import com.iorbit.iorbithealthapp.Devices.Bluetooth.SPO2ControlD;
import com.iorbit.iorbithealthapp.Devices.OCR.TextScanner.GlucoMeterScanner;
import com.iorbit.iorbithealthapp.Devices.OCR.TextScanner.SpO2Scanner;
import com.iorbit.iorbithealthapp.Helpers.DataBaseManager.DatabaseHelper;
import com.iorbit.iorbithealthapp.Helpers.Interface.OnRetryClickListener;
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.Helpers.Utils.Utils;
import com.iorbit.iorbithealthapp.Models.GetPatientModel;
import com.iorbit.iorbithealthapp.Models.PatientModel;
import com.iorbit.iorbithealthapp.Models.ScannerDeviceModel;
import com.iorbit.iorbithealthapp.Network.Connectivity;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.R;
import com.iorbit.iorbithealthapp.ui.Fragment.DashboardFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class DashBoardActivity extends AppCompatActivity implements OnRetryClickListener{

    DatabaseHelper databaseHelper;
    ImageView BleIcon,ScanIcon;
    private BluetoothAdapter bluetoothAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        Utils.requestPerm(this);
        getPatientFromCloud();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Toolbar toolbar = findViewById(R.id.toolbar_admin_vitals);
        if (new SharedPreference(DashBoardActivity.this).getCurrentPAtient() != null)
            toolbar.setTitle(new SharedPreference(DashBoardActivity.this).getCurrentPAtient().getFirstName());
        else
            toolbar.setTitle(new SharedPreference(DashBoardActivity.this).getUserName());

        databaseHelper = new DatabaseHelper(this);
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout_vitals_admin);
        NavigationView navigationView = findViewById(R.id.admin_nav_right);
        BleIcon = findViewById(R.id.ble_icon);
        ScanIcon = findViewById(R.id.scan_icon);

        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.adminHopitalName)).setText(new SharedPreference(getApplicationContext()).getUserName());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        DashboardFragment llf = new DashboardFragment();
        ft.replace(R.id.root_layout, llf);
        ft.commit();


        BleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetoothIntent, 1);

                        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

                        // Check if background location permission is needed
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            // Check if the app already has background location permission
                            if (ContextCompat.checkSelfPermission(DashBoardActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                                // Background location permission not granted, request it
                                permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION};
                            }
                        }

                        // Request location permissions
                        ActivityCompat.requestPermissions(DashBoardActivity.this, permissions, 2);

                } else {
                    showBtConnectPopUp();
                }
            }
        });

        ScanIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showScanConnectPopUp();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_logout:
                        new SharedPreference(getApplicationContext()).clearCurrentPatient();
                        databaseHelper.deletePatients();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                        break;
                    case R.id.contact_us:
                        Intent intent1 = new Intent(DashBoardActivity.this, ContactUsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);
                        break;

                    case R.id.profile:
                        Intent intent = new Intent(DashBoardActivity.this, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;

                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_vitals_admin);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private AlertDialog showScanConnectPopUp() {
        List<ScannerDeviceModel> deviceModelList = new ArrayList<>();
        deviceModelList.add(new ScannerDeviceModel(R.drawable.pulse_oximeter64, "Pulse Oximeter","(Control D)"));
        deviceModelList.add(new ScannerDeviceModel(R.drawable.glucometer64, "Blood Glucometer","(Contour plus ELITE)"));
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_bluetooth_list, null);
        final ImageView refresh = view.findViewById(R.id.refresh);
        final TextView empty = view.findViewById(R.id.empty);
        empty.setVisibility(View.GONE);
        refresh.setVisibility(View.GONE);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ListView devicesListView = (ListView) view.findViewById(R.id.devices_list_view);

        ScanDeviceListAdapter scanDeviceListAdapter = new ScanDeviceListAdapter(getApplicationContext(),deviceModelList);
        devicesListView.setAdapter(scanDeviceListAdapter);
        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundColor(Color.parseColor("#E6F0FC"));
                dialog.dismiss();
                Intent intent;
                switch (position) {
                    case 0:
                        intent = new Intent(DashBoardActivity.this, SpO2Scanner.class)
                                .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        break;
                    case 1:
                        intent = new Intent(DashBoardActivity.this, GlucoMeterScanner.class)
                                .addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + position);
                }
                DashBoardActivity.this.startActivity(intent);}
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

        return dialog;
        }

    @SuppressLint("MissingPermission")
    public AlertDialog showBtConnectPopUp() {

        final BluetoothDevicesAdapter bluetoothDevicesAdapter = new BluetoothDevicesAdapter(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_bluetooth_list, null);
        final ImageView refresh = view.findViewById(R.id.refresh);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ListView devicesListView = (ListView) view.findViewById(R.id.devices_list_view);
        TextView emptyText = (TextView) view.findViewById(R.id.empty);
        devicesListView.setAdapter(bluetoothDevicesAdapter);
        devicesListView.setEmptyView(emptyText);

        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                try {
                    String action = intent.getAction();
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        if (bluetoothDevicesAdapter.getPosition(device) == -1) {
                            // -1 is returned when the item is not in the adapter
                            String devName = device.getName();
                            if (devName != null)
                                if (devName.startsWith(CommonDataArea.SUPPORTED_DEVICES_BP1) || devName.startsWith(CommonDataArea.SUPPORTED_DEVICES_SPO21) || devName.startsWith("JVH")) {
                                    bluetoothDevicesAdapter.add(device);
                                    bluetoothDevicesAdapter.notifyDataSetChanged();
                                }
                        }
                    } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                        try {
                            Glide.with(context).load((Bitmap) null).into(refresh);
                            refresh.setImageResource(R.drawable.whitebluettothnew);
                            context.unregisterReceiver(this);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                break;
                        }
                    }
                } catch (Exception exp) {
                    // LogWriter.writeLog("Bluetooth scan","Exception->"+exp.getMessage());
                }
            }
        };


        if (bluetoothDevicesAdapter == null) {
            // Log.e(Constants.TAG, "Device has no bluetooth");
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("No Bluetooth")
                    .setMessage("Your device has no bluetooth")
                    .setPositiveButton("Close app", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }


        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice devicee = bluetoothDevicesAdapter.getItem(position);
                bluetoothAdapter.cancelDiscovery();
                view.setBackgroundColor(Color.parseColor("#E6F0FC"));
                if ((devicee.getName() != null) && devicee.getName().contains(CommonDataArea.SUPPORTED_DEVICES_BP1)) {
                    CommonDataArea.bpDrTrust1 = new BPDrTrust1(DashBoardActivity.this);
                    CommonDataArea.bpDrTrust1.connectDevice(devicee.getAddress());
                } else if ((devicee.getName() != null) && devicee.getName().contains(CommonDataArea.SUPPORTED_DEVICES_SPO21)) {
                    CommonDataArea.spO2ControlD = new SPO2ControlD(DashBoardActivity.this);
                    CommonDataArea.spO2ControlD.connectDevice(devicee.getAddress());
                }
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bluetoothAdapter.isEnabled()) {
                    bluetoothDevicesAdapter.clear();
                    bluetoothDevicesAdapter.notifyDataSetChanged();
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(BluetoothDevice.ACTION_FOUND);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                    getApplicationContext().registerReceiver(mReceiver, filter);
                    bluetoothAdapter.startDiscovery();
                    Glide.with(DashBoardActivity.this).load(R.drawable.whitebluettothnew).into(refresh);
                }
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mReceiver, filter);
        dialog.setCancelable(true);
        if (bluetoothAdapter.isEnabled()) {
            Log.e("startearch", "true");
            bluetoothDevicesAdapter.clear();
            bluetoothDevicesAdapter.notifyDataSetChanged();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            this.registerReceiver(mReceiver, filter);
            if (!bluetoothAdapter.startDiscovery()) {
                // LogWriter.writeLog("Bluetooth", " Failed to start discovery");
            }
            Glide.with(this).load(R.drawable.whitebluettothnew).into(refresh);
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }

        return dialog;
    }

    public void getPatientFromCloud() {
        Utils.showLoaderDialog(this);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        if(Utils.isConnected(this)){
                RetrofitClient retrofit = new RetrofitClient();
                Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
                if (retrofitClient == null) {
                    return;
                }

                Call<GetPatientModel> call = retrofitClient.create(ServiceApi.class).getPatient(new SharedPreference(this).getUserID());
                call.enqueue(new Callback<GetPatientModel>() {
                    @Override
                    public void onResponse(Call<GetPatientModel> call, Response<GetPatientModel> response) {
                        if (response.isSuccessful()) {
                            databaseHelper.deletePatients();
                            GetPatientModel displayPatients = response.body();
                            List<PatientModel> patientsList = displayPatients.getPatientschema();
                            if (patientsList != null) {
                                if (patientsList.size() > 0) {

                                    try {
                                        for (PatientModel patients : patientsList) {
                                            if (databaseHelper.getPatientsByID(patients.getSsid()) == null) {
                                                if(new SharedPreference(getApplicationContext()).getCurrentPAtient()==null)
                                                    new SharedPreference(getApplicationContext()).saveCurrentPAtient(patientsList.get(0));
                                                databaseHelper.addPatients(patients, Connectivity.isConnected(getApplicationContext()));

                                            }
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Utils.closeLoaderDialog();
                                        Utils.showSnackbar(findViewById(android.R.id.content),"Something went wrong!!", Snackbar.LENGTH_SHORT);
                                    }
                                    if (new SharedPreference(getApplicationContext()).getCurrentPAtient() == null) {
                                        List<PatientModel> patients = databaseHelper.getAllPatients();
                                        if (patients.size() > 0) {
                                            if(new SharedPreference(getApplicationContext()).getCurrentPAtient()==null)
                                                new SharedPreference(getApplicationContext()).saveCurrentPAtient(patients.get(0));
                                        }
                                    }
                                }
                                Utils.closeLoaderDialog();
                            }
                        }else{
                            Utils.closeLoaderDialog();
                            Utils.showSnackbar(findViewById(android.R.id.content),"Something went wrong!!", Snackbar.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void onFailure(Call<GetPatientModel> call, Throwable t) {
                        Utils.closeLoaderDialog();
                        Utils.showSnackbar(findViewById(android.R.id.content),"Something went wrong!!", Snackbar.LENGTH_SHORT);
                    }
                });

        }else{
            Utils.closeLoaderDialog();
            Utils.showNoInternetDialog(this, (OnRetryClickListener) this);
        }


    }

    @Override
    public void onBackPressed() {
        Utils.showDoubleBackPressExitSnackbar(this);
    }

    @Override
    public void onRetryClick() {
        getPatientFromCloud();
    }

}