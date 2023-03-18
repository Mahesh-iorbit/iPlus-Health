package com.iorbit.iorbithealthapp.Helpers.DataBaseManager;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.iorbit.iorbithealthapp.Helpers.Utils.EncryptModel;
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.Models.PatientModel;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_PATIENT = "patients";
    public static final String TABLE_SYNC = "Sync";
    public static final String TABLE_INSTANT_REPORT = "instant_report";
    public static final String TABLE_SIGNATURE = "signature";
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "iorbitHealth";
    private Context context;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PATIENT_TABLE = "CREATE TABLE IF NOT EXISTS patients (id INTEGER PRIMARY KEY autoincrement , CreatedByRole TEXT, age TEXT, CreatedBy TEXT, honorific TEXT, FirstName TEXT, LastName TEXT, SSID TEXT, EmailID TEXT, AlternateEmailID TEXT, ContactNo TEXT, AlternateContactNo TEXT, DateOfBirth DATE, Gender TEXT, Address TEXT, ZIP TEXT, City TEXT, Country TEXT, TimeZone TEXT, BloodGroup TEXT, Height TEXT, Weight TEXT, Diabetic INTEGER, HighBP INTEGER, Smoke INTEGER, Alcohol INTEGER, OtherDetails TEXT, DeviceID TEXT,isSynced INTEGER,HospitalID TEXT,stdSBP TEXT,stdDBP TEXT,factorSBP TEXT,factorDBP TEXT)";
        String CREATE_SYNC_TABLE = "CREATE TABLE IF NOT EXISTS Sync (id INTEGER PRIMARY KEY autoincrement , fileName TEXT, data TEXT)";
        String CREATE_INSTANT_REPORT_TABLE = "CREATE TABLE IF NOT EXISTS instant_report (id INTEGER PRIMARY KEY autoincrement , date DATE, patientID TEXT, data TEXT)";
        String CREATE_SIGNATURE_TABLE = "CREATE TABLE IF NOT EXISTS Signature (id INTEGER PRIMARY KEY autoincrement , DoctorID TEXT, DoctorName TEXT,Signature TEXT)";
        db.execSQL(CREATE_PATIENT_TABLE);
        db.execSQL(CREATE_SYNC_TABLE);
        db.execSQL(CREATE_INSTANT_REPORT_TABLE);
        db.execSQL(CREATE_SIGNATURE_TABLE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SYNC);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIGNATURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTANT_REPORT);
        onCreate(db);
    }


    @SuppressLint("Range")
    public List<PatientModel> getAllPatients() {
        ArrayList<String> exemptList = new ArrayList<>();
        //  exemptList.add("getSSID");
        exemptList.add("getContactNo");
        List<PatientModel> patientsList = new ArrayList<PatientModel>();
        // query = EncryptModel.getEncryptedString(context,"KEY",query);
        String selectQuery = "SELECT  * FROM " + TABLE_PATIENT;
        Log.d("Patient", "getAllPatients: "+selectQuery);
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PatientModel patients = new PatientModel();
                patients.setCreatedByRole(cursor.getString(cursor.getColumnIndex("CreatedByRole")));
                patients.setAge(cursor.getString(cursor.getColumnIndex("age")));
                patients.setCreatedBy(cursor.getString(cursor.getColumnIndex("CreatedBy")));
                patients.setHonorific(cursor.getString(cursor.getColumnIndex("honorific")));
                patients.setFirstName(cursor.getString(cursor.getColumnIndex("FirstName")));
                patients.setLastName(cursor.getString(cursor.getColumnIndex("LastName")));
                patients.setSsid(cursor.getString(cursor.getColumnIndex("SSID")));
                patients.setEmailID(cursor.getString(cursor.getColumnIndex("EmailID")));
                patients.setAlternateEmailID(cursor.getString(cursor.getColumnIndex("AlternateEmailID")));
                patients.setContactNo(cursor.getString(cursor.getColumnIndex("ContactNo")));
                patients.setAlternateContactNo(cursor.getString(cursor.getColumnIndex("AlternateContactNo")));
                patients.setDateOfBirth(cursor.getString(cursor.getColumnIndex("DateOfBirth")));
                patients.setGender(cursor.getString(cursor.getColumnIndex("Gender")));
                patients.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
                patients.setZip(cursor.getString(cursor.getColumnIndex("ZIP")));
                patients.setCity(cursor.getString(cursor.getColumnIndex("City")));
                patients.setCountry(cursor.getString(cursor.getColumnIndex("Country")));
                patients.setTimeZone(cursor.getString(cursor.getColumnIndex("TimeZone")));
                patients.setBloodGroup(cursor.getString(cursor.getColumnIndex("BloodGroup")));
                patients.setHeight(cursor.getString(cursor.getColumnIndex("Height")));
                patients.setWeight(cursor.getString(cursor.getColumnIndex("Weight")));
                patients.setDiabetic(cursor.getString(cursor.getColumnIndex("Diabetic")));
                patients.setHighBP(cursor.getString(cursor.getColumnIndex("HighBP")));
                patients.setSmoke(cursor.getString(cursor.getColumnIndex("Smoke")));
                patients.setAlcohol(cursor.getString(cursor.getColumnIndex("Alcohol")));
                patients.setOtherDetails(cursor.getString(cursor.getColumnIndex("OtherDetails")));
                patients.setDeviceID(cursor.getString(cursor.getColumnIndex("DeviceID")));
                patients.setStdSBP(cursor.getString(cursor.getColumnIndex("stdSBP")));
                patients.setStdDBP(cursor.getString(cursor.getColumnIndex("stdDBP")));
                patients.setFactorSBP(cursor.getString(cursor.getColumnIndex("factorSBP")));
                patients.setFactorDBP(cursor.getString(cursor.getColumnIndex("factorDBP")));
                //patients.setSynced(cursor.getInt(cursor.getColumnIndex("isSynced")));

                patientsList.add((PatientModel) EncryptModel.getDecryptedModel(context, "KEY", patients, exemptList));
            } while (cursor.moveToNext());
        }
        db.close();
        return patientsList;
    }

    public void deletePatients() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //deleting rows
        sqLiteDatabase.delete(TABLE_PATIENT, null, null);
        sqLiteDatabase.close();
    }

    public String generateUniquePID() {
        int counter = 1;
        String generatedPID = generatePID(counter);
        PatientModel p = getPatientsByID(new SharedPreference(context).getUserID().substring(0, 3) + generatedPID);
        while (p != null) {
            counter++;
            generatedPID = generatePID(counter);
            if (counter == 2 && generatedPID.equalsIgnoreCase("0001"))
                return generatedPID;
            else
                p = getPatientsByID(new SharedPreference(context).getUserID().substring(0, 3) + generatedPID);
        }
        return generatedPID;
    }

    @SuppressLint("Range")
    public String generatePID(int iteration) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT id FROM " + TABLE_PATIENT + " WHERE CreatedBy='" + EncryptModel.getEncryptedString(context, "KEY", new SharedPreference(context).getUserID()) + "' ORDER BY id DESC LIMIT 1";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
           String PID = (cursor.getInt(cursor.getColumnIndex("id")) + iteration) + "";
            int len = PID.trim().length();
            if (len >= 5) {
                db.close();
                return "";
            }
            String pidx = PID.trim();
            for (int x = 0; x < 4 - len; x++) {
                pidx = "0" + pidx;
            }
            db.close();
            return pidx;
        }
        db.close();
        return "0001";
    }

    @SuppressLint("Range")
    public PatientModel getPatientsByID(String ssid) {
        ArrayList<String> exemptList = new ArrayList<>();
        exemptList.add("getSSID");
        exemptList.add("getContactNo");
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PATIENT + " where ( SSID LIKE '%" + EncryptModel.getEncryptedString(context, "KEY", ssid) + "%') ";
        Cursor cursor = db.rawQuery(selectQuery, null);
        PatientModel patients = null;
        if (cursor.moveToFirst()) {
            patients = new PatientModel();
            patients.setCreatedByRole(cursor.getString(cursor.getColumnIndex("CreatedByRole")));
            patients.setAge(cursor.getString(cursor.getColumnIndex("age")));
            patients.setCreatedBy(cursor.getString(cursor.getColumnIndex("CreatedBy")));
            patients.setHonorific(cursor.getString(cursor.getColumnIndex("honorific")));
            patients.setFirstName(cursor.getString(cursor.getColumnIndex("FirstName")));
            patients.setLastName(cursor.getString(cursor.getColumnIndex("LastName")));
            patients.setSsid(cursor.getString(cursor.getColumnIndex("SSID")));
            patients.setEmailID(cursor.getString(cursor.getColumnIndex("EmailID")));
            patients.setAlternateEmailID(cursor.getString(cursor.getColumnIndex("AlternateEmailID")));
            patients.setContactNo(cursor.getString(cursor.getColumnIndex("ContactNo")));
            patients.setAlternateContactNo(cursor.getString(cursor.getColumnIndex("AlternateContactNo")));
            patients.setDateOfBirth(cursor.getString(cursor.getColumnIndex("DateOfBirth")));
            patients.setGender(cursor.getString(cursor.getColumnIndex("Gender")));
            patients.setAddress(cursor.getString(cursor.getColumnIndex("Address")));
            patients.setZip(cursor.getString(cursor.getColumnIndex("ZIP")));
            patients.setCity(cursor.getString(cursor.getColumnIndex("City")));
            patients.setCountry(cursor.getString(cursor.getColumnIndex("Country")));
            patients.setTimeZone(cursor.getString(cursor.getColumnIndex("TimeZone")));
            patients.setBloodGroup(cursor.getString(cursor.getColumnIndex("BloodGroup")));
            patients.setHeight(cursor.getString(cursor.getColumnIndex("Height")));
            patients.setWeight(cursor.getString(cursor.getColumnIndex("Weight")));
            patients.setDiabetic(cursor.getString(cursor.getColumnIndex("Diabetic")));
            patients.setHighBP(cursor.getString(cursor.getColumnIndex("HighBP")));
            patients.setSmoke(cursor.getString(cursor.getColumnIndex("Smoke")));
            patients.setAlcohol(cursor.getString(cursor.getColumnIndex("Alcohol")));
            patients.setOtherDetails(cursor.getString(cursor.getColumnIndex("OtherDetails")));
            patients.setDeviceID(cursor.getString(cursor.getColumnIndex("DeviceID")));
            patients.setStdSBP(cursor.getString(cursor.getColumnIndex("stdSBP")));
            patients.setStdDBP(cursor.getString(cursor.getColumnIndex("stdDBP")));
            patients.setFactorSBP(cursor.getString(cursor.getColumnIndex("factorSBP")));
            patients.setFactorDBP(cursor.getString(cursor.getColumnIndex("factorDBP")));
            //patients.setSynced(cursor.getInt(cursor.getColumnIndex("isSynced")));
            //  patientsList.add((Patients) EncryptModel.getDecryptedModel(context,"KEY",patients,exemptList));
        }
        db.close();
        if (patients != null)
            return EncryptModel.getDecryptedModel(context, "KEY", patients, exemptList);
        else
            return null;
    }
    public boolean addPatients(PatientModel patients, boolean isNetworkConnected) throws Exception {
        ArrayList<String> exemptList = new ArrayList<>();
        exemptList.add("getSSID");
        exemptList.add("getContactNo");
        patients = EncryptModel.getEncryptedModel(context, "KEY", patients, exemptList);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CreatedByRole", patients.getCreatedByRole());
        values.put("age", patients.getAge());
        values.put("CreatedBy", patients.getCreatedBy());
        values.put("honorific", patients.getHonorific());
        values.put("FirstName", patients.getFirstName());
        values.put("LastName", patients.getLastName());
        values.put("SSID", patients.getSsid());
        values.put("EmailID", patients.getEmailID());
        values.put("AlternateEmailID", patients.getAlternateEmailID());
        values.put("ContactNo", patients.getContactNo());
        values.put("AlternateContactNo", patients.getAlternateContactNo());
        values.put("DateOfBirth", patients.getDateOfBirth());
        values.put("Gender", patients.getGender());
        values.put("Address", patients.getAddress());
        values.put("ZIP", patients.getZip());
        values.put("City", patients.getCity());
        values.put("Country", patients.getCountry());
        values.put("TimeZone", patients.getTimeZone());
        values.put("BloodGroup", patients.getBloodGroup());
        values.put("Height", patients.getHeight());
        values.put("Weight", patients.getWeight());
        values.put("Diabetic", patients.getDiabetic());
        values.put("HighBP", patients.getHighBP());
        values.put("Smoke", patients.getSmoke());
        values.put("Alcohol", patients.getAlcohol());
        values.put("OtherDetails", patients.getOtherDetails());
        values.put("DeviceID", patients.getDeviceID());
        values.put("HospitalID", "");
        values.put("stdSBP", patients.getStdSBP());
        values.put("stdDBP", patients.getStdDBP());
        values.put("factorSBP", patients.getFactorSBP());
        values.put("factorDBP", patients.getFactorDBP());
        if (isNetworkConnected)
            values.put("isSynced", 1);
        else
            values.put("isSynced", 0);


        long res;
        if (getPatientsByID(patients.getSsid()) == null) {
            db = getWritableDatabase();
            res = db.insert(TABLE_PATIENT, null, values);
        } else {
            db.close();
            throw new Exception("Member ID already exists");
        }
        db.close();
        return res >= 0;
    }


    public boolean updatePatients(PatientModel patients, boolean isNetworkConnected) throws Exception {
        ArrayList<String> exemptList = new ArrayList<>();
        exemptList.add("getSSID");
        exemptList.add("getContactNo");
        patients = EncryptModel.getEncryptedModel(context, "KEY", patients, exemptList);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("CreatedByRole", patients.getCreatedByRole());
        values.put("age", patients.getAge());
        values.put("CreatedBy", patients.getCreatedBy());
        values.put("honorific", patients.getHonorific());
        values.put("FirstName", patients.getFirstName());
        values.put("LastName", patients.getLastName());
        values.put("SSID", patients.getSsid());
        values.put("EmailID", patients.getEmailID());
        values.put("AlternateEmailID", patients.getAlternateEmailID());
        values.put("ContactNo", patients.getContactNo());
        values.put("AlternateContactNo", patients.getAlternateContactNo());
        values.put("DateOfBirth", patients.getDateOfBirth());
        values.put("Gender", patients.getGender());
        values.put("Address", patients.getAddress());
        values.put("ZIP", patients.getZip());
        values.put("City", patients.getCity());
        values.put("Country", patients.getCountry());
        values.put("TimeZone", patients.getTimeZone());
        values.put("BloodGroup", patients.getBloodGroup());
        values.put("Height", patients.getHeight());
        values.put("Weight", patients.getWeight());
        values.put("Diabetic", patients.getDiabetic());
        values.put("HighBP", patients.getHighBP());
        values.put("Smoke", patients.getSmoke());
        values.put("Alcohol", patients.getAlcohol());
        values.put("OtherDetails", patients.getOtherDetails());
        values.put("DeviceID", patients.getDeviceID());
        values.put("HospitalID", "");
        values.put("stdSBP", patients.getStdSBP());
        values.put("stdDBP", patients.getStdDBP());
        values.put("factorSBP", patients.getFactorSBP());
        values.put("factorDBP", patients.getFactorDBP());
        if (isNetworkConnected)
            values.put("isSynced", 1);
        else
            values.put("isSynced", 0);

        db = getWritableDatabase();
        long res = db.update(TABLE_PATIENT, values, "SSID = ?", new String[]{patients.getSsid()});
        db.close();
        return res >= 0;
    }

    public void deletePatient(String ssid) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM " + TABLE_PATIENT + " WHERE (SSID LIKE '%" + EncryptModel.getEncryptedString(context, "KEY", ssid) + "%')";
        db.execSQL(deleteQuery);
        db.close();
    }

}