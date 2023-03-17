package com.iorbit.iorbithealthapp.Helpers.SessionManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.view.ContextThemeWrapper;

import com.google.android.material.snackbar.Snackbar;
import com.iorbit.iorbithealthapp.R;
import com.iorbit.iorbithealthapp.Utils.App;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class Utils {
    public static AlertDialog waitDialog;
    private static Snackbar snackBar;
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

    public static void requestPerm(final Activity context){
        Dexter.withActivity(context)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (!report.areAllPermissionsGranted()) {
                            showSettingsDialog(context);
                        }else if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog(context);
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
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
        App.getContext().startActivityForResult(intent, 101);
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



}
