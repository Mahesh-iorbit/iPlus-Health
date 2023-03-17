package com.iorbit.iorbithealthapp.Helpers.SessionManager;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.iorbit.iorbithealthapp.Models.PatientModel;

public class SharedPreference {
    public static String USERPREFERENCE = "User_Preference";
    public static String USERUNIQUEID = "User_unique_Id";
    public static String DEVICEUNIQUEID = "Device_unique_Id";
    public static String USERUNIQUENAME = "User_unique_name";
    public static String PATIENT = "Patient_unique";
    public static String FIRST_LOGIN = "initial_login";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharedPreference(Context context) {
            this.sharedPreferences = context.getSharedPreferences(USERPREFERENCE, MODE_PRIVATE);
            this.editor = sharedPreferences.edit();
        }

    public void saveUserID(String userID) {
        editor.putString(USERUNIQUEID, userID);
        editor.commit();
    }

    public void saveDeviceID(String deviceId) {
        editor.putString(DEVICEUNIQUEID, deviceId);
        editor.commit();
    }

    public void saveUserName(String userName) {
        editor.putString(USERUNIQUENAME, userName);
        editor.commit();
    }

    public void saveLoginStatus(boolean isFirst) {
        editor.putBoolean(FIRST_LOGIN, isFirst);
        editor.commit();
    }
    public boolean getFirstLogin() {
        return sharedPreferences.getBoolean(FIRST_LOGIN, false);
    }
    public String getUserID() {
        Log.d("dsfjdjgbh",sharedPreferences.getString(USERUNIQUEID, ""));
        return sharedPreferences.getString(USERUNIQUEID, "");
    }

    public String getDeviceID() {
        return sharedPreferences.getString(DEVICEUNIQUEID, "");
    }

    public String getUserName() {
        return sharedPreferences.getString(USERUNIQUENAME, "");
    }

    public void saveCurrentPAtient(PatientModel patients) {
        Gson gson = new Gson();
        String json = gson.toJson(patients);
        editor.putString(PATIENT, json);
        editor.commit();
    }

    public PatientModel getCurrentPAtient() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PATIENT, "");
        PatientModel patients = gson.fromJson(json, PatientModel.class);
        return patients;
    }

    public void clearCurrentPatient()
    {
       editor.remove(PATIENT);
       editor.commit();
    }

}
