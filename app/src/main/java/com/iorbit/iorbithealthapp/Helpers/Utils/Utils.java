package com.iorbit.iorbithealthapp.Helpers.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.iorbit.iorbithealthapp.Helpers.Interface.OnRetryClickListener;
import com.iorbit.iorbithealthapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class Utils {

    private static boolean doubleBackToExitPressedOnce = false;
    private static long DOUBLE_PRESS_TIME_THRESHOLD = 2000;
    private static long lastBackPressTime = 0;
    static Dialog dialog;

    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void requestPermissions(Context context) {
        List<String> permissionToRequest = new ArrayList<>();
        if (!hasReadExternalStoragePermission(context))
            permissionToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!hasWriteExternalStoragePermission(context))
            permissionToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!hasManageExternalStoragePermission(context))
            permissionToRequest.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        if (!hasBluetoothPermission(context))
            permissionToRequest.add(Manifest.permission.BLUETOOTH);
        if (!hasBluetoothAdminPermission(context))
            permissionToRequest.add(Manifest.permission.BLUETOOTH_ADMIN);
        if (!hasAccessFineLocationPermission(context))
            permissionToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (!hasBluetoothScanPermission(context))
            permissionToRequest.add(Manifest.permission.BLUETOOTH_SCAN);
        if (!hasBluetoothConnectPermission(context))
            permissionToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
        if (!hasBluetoothPrivilegedPermission(context))
            permissionToRequest.add(Manifest.permission.BLUETOOTH_PRIVILEGED);
        if (!hasCameraPermission(context))
            permissionToRequest.add(Manifest.permission.CAMERA);
        if (!permissionToRequest.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, permissionToRequest.toArray(new String[0]), 0);
        }
    }

    private static boolean hasReadExternalStoragePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasWriteExternalStoragePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasManageExternalStoragePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.MANAGE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasBluetoothPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasBluetoothAdminPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasAccessFineLocationPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasBluetoothScanPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasBluetoothConnectPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasBluetoothPrivilegedPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_PRIVILEGED) == PackageManager.PERMISSION_GRANTED;
    }

    private static boolean hasCameraPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static void showDoubleBackPressExitSnackbar(Activity activity) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressTime < DOUBLE_PRESS_TIME_THRESHOLD) {
            activity.finish();
        } else {
            doubleBackToExitPressedOnce = true;
            lastBackPressTime = currentTime;

            View view = activity.findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(view, "Press back again to exit", Snackbar.LENGTH_SHORT);
            View snackbarView = snackbar.getView();
            snackbarView.setBackground(ContextCompat.getDrawable(activity, R.drawable.snackbar_bg)); // Set the background to the drawable resource file
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            snackbarView.setLayoutParams(params);
            snackbar.show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, DOUBLE_PRESS_TIME_THRESHOLD);
        }
    }

    public static void showSnackbar(View view, String message, int duration) {
        Snackbar.make(view, message, duration).show();
    }

    public static boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public static String getDateTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return df.format(c);
    }

    public static void showNoInternetDialog(Context context, OnRetryClickListener listener){
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.network_dailog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        Button Reload = dialog.findViewById(R.id.retry);
        Reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    dialog.dismiss();
                    listener.onRetryClick();
                }


            }
        });
    }

    public static void showLoaderDialog(Context context){
        dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_loader_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public static void closeLoaderDialog(){
        dialog.dismiss();

    }




    public static AlertDialog waitDialog;
    public static void showWaitDialog(Context context,String message) {
        closeWaitDialog();
        waitDialog = new SpotsDialog.Builder().setContext(context).build();
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    public static void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        }
        catch (Exception e) {
            //
        }
    }


    private static void showSettingsDialog(final Context context) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Need Permissions");
        builder.setCancelable(false);
        builder.setMessage("You have denied app permissions multiple times. \n\nPlease turn on permissions at [Setting] > [Permission]");
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings(context);
            }
        });
        builder.show();

    }

    private static void openSettings(final Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);

    }

    public static void showAlertDialogMessage(final Activity context,String msg,final boolean finish){
        final androidx.appcompat.app.AlertDialog.Builder builder =new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(context, com.google.android.material.R.style.Widget_AppCompat_ButtonBar_AlertDialog));
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (finish) {
                    dialogInterface.dismiss();
                    context.finish();
                } else {
                    dialogInterface.dismiss();
                }

            }
        });
        builder.show();

    }



}
