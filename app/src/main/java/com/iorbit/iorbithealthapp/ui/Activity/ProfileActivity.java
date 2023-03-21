package com.iorbit.iorbithealthapp.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iorbit.iorbithealthapp.Helpers.DataBaseManager.DatabaseHelper;
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.Helpers.Utils.Utils;
import com.iorbit.iorbithealthapp.Models.PatientModel;
import com.iorbit.iorbithealthapp.Models.RegisterUserResponse;
import com.iorbit.iorbithealthapp.Network.Connectivity;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.R;
import com.iorbit.iorbithealthapp.databinding.ActivityProfileBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileActivity extends AppCompatActivity {

    private PatientModel patients;
    ActivityProfileBinding pb;
    String mPrefix,gender,Mobilecode;
    Map<String, Integer> a = new HashMap<>();
    RadioButton rbgender;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pb = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = pb.getRoot();
        setContentView(view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Bundle extras = getIntent().getExtras();
        if (new SharedPreference(ProfileActivity.this).getCurrentPAtient() != null) {
            patients = new SharedPreference(ProfileActivity.this).getCurrentPAtient();
            setPatientDetails(patients);
        }

        databaseHelper = new DatabaseHelper(this);

        pb.etcalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showAlertDialogMessage(ProfileActivity.this, "BP calibration is necessary to minimize measurement uncertainty by ensuring the accuracy of Sense-H device.", false);
            }
        });

        pb.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pb.moreInfo.getVisibility() == View.GONE) {
                    pb.more.setText("- Less Information");
                    pb.moreInfo.setVisibility(View.VISIBLE);
                } else {
                    pb.more.setText("+ More Information");
                    pb.moreInfo.setVisibility(View.GONE);
                }
            }
        });

        mPrefix = new SharedPreference(ProfileActivity.this).getUserID().substring(0, 3);
        pb.bSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pb.etFirstName.getText().toString().equalsIgnoreCase("")) {
                    pb.textFirstName.setError("required");
                    return;
                } else if (pb.etLastName.getText().toString().equalsIgnoreCase("")) {
                    pb.textLastName.setError("required");
                    return;
                } else if (pb.etAge.getText().toString().equalsIgnoreCase("")) {
                    pb.textInputAge.setError("required");
                    return;
                } else if (pb.etContactNo.getText().toString().equalsIgnoreCase("")) {
                    pb.textInputConatctnumber.setError("required");
                    return;
                } else if (pb.etCity.getText().toString().equalsIgnoreCase("")) {
                    pb.textInputCity.setError("required");
                    return;
                } else if (pb.etPID.getText().toString().equalsIgnoreCase("")) {
                    pb.etPID.setError("required");
                    return;
                } else {
                    save();
                }
            }
        });

        a.put("O +ve", 0);
        a.put("O -ve", 1);
        a.put("A +ve", 2);
        a.put("A -ve", 3);
        a.put("B +ve", 4);
        a.put("B -ve", 5);
        a.put("AB +ve", 6);
        a.put("AB -ve", 7);
        a.put("", -1);

        if (extras != null) {
            if(extras.containsKey("PatientEditDetailsDetails"))
            {
                patients = new Gson().fromJson(extras.getString("PatientEditDetailsDetails"), PatientModel.class);
                setPatientDetails(patients);
            }
        }

    }

    private void setPatientDetails(PatientModel patientDetails) {
        pb.etFirstName.setText(patientDetails.getFirstName());
        pb.etLastName.setText(patientDetails.getLastName());
        pb.etemail.setText(patientDetails.getEmailID());
        pb.etPID.setText(patientDetails.getSsid());
        pb.etPID.setEnabled(false);
        pb.etAge.setText(patientDetails.getAge().trim());
        pb.MobileNumberCode.setText(patientDetails.getContactNo().substring(1, 3));
        pb.MobileNumberCode.setEnabled(false);
        pb.etContactNo.setText(patientDetails.getContactNo().substring(3, 13));
        pb.etContactNo.setEnabled(false);


        if (patientDetails.getGender().equalsIgnoreCase("Male"))
            pb.rggender.check(R.id.rbMale);
        else if (patientDetails.getGender().equalsIgnoreCase("Female"))
            pb.rggender.check(R.id.rbFemale);

        pb.etdbp.setText(patientDetails.getStdDBP());
        pb.etCity.setText(patientDetails.getCity());
        pb.editTextHieght.setText(patientDetails.getHeight());
        pb.editTextWieght.setText(patientDetails.getWeight());

        String[] strings = (getResources().getStringArray(R.array.bloodgroup));
        if (patientDetails.getBloodGroup().equalsIgnoreCase(" "))
            pb.editTextOPositive.setSelection(Arrays.asList(strings).indexOf("O +ve"));
        else
            pb.editTextOPositive.setSelection(Arrays.asList(strings).indexOf(patientDetails.getBloodGroup()));

        if (patientDetails.getDiabetic().equalsIgnoreCase("1"))
            pb.rgdiabetic.check(R.id.yesdiabetic);
        else if (patientDetails.getDiabetic().equalsIgnoreCase("2"))
            pb.rgdiabetic.check(R.id.nodiabetic);
        else if (patientDetails.getSmoke().equalsIgnoreCase("2"))
            pb.smoking.check(R.id.Ocassionalsmoke);

        if (patientDetails.getHighBP().equalsIgnoreCase("1"))
            pb.rgBp1.check(R.id.low);
        else if (patientDetails.getHighBP().equalsIgnoreCase("2"))
            pb.rgBp1.check(R.id.normal);


        if (patientDetails.getAlcohol().equalsIgnoreCase("1"))
            pb.drinking.check(R.id.Neverdrink);
        else if (patientDetails.getAlcohol().equalsIgnoreCase("2"))
            pb.drinking.check(R.id.Ocassionaldrink);

        pb.etsbp.setText(patientDetails.getStdSBP());
        pb.etdbp.setText(patientDetails.getStdDBP());
    }

    private void save() {
        Utils.showWaitDialog(ProfileActivity.this, "");
        int selectedId = pb.rggender.getCheckedRadioButtonId();
        rbgender = findViewById(selectedId);
        gender = rbgender.getText().toString().trim();
        int len = pb.etPID.getText().toString().trim().length();
        String pidx = pb.etPID.getText().toString().trim();
        for (int x = 0; x < 4 - len; x++) {
            pidx = "0" + pidx;
        }
        PatientModel p = new PatientModel();

        Mobilecode = "+"+pb.MobileNumberCode.getText().toString();
        p.addPatients(pidx, new SharedPreference(ProfileActivity.this).getUserID(), pb.etemail.getText().toString(), pb.etCity.getText().toString(), pb.etFirstName.getText().toString(), pb.etLastName.getText().toString(), pb.etAge.getText().toString(), gender,Mobilecode+ pb.etContactNo.getText().toString());
        p.setCreatedBy(new SharedPreference(ProfileActivity.this).getUserID());
        p.setCreatedByRole("H");
        String heightStr = pb.editTextHieght.getText().toString();
        String weightStr = pb.editTextWieght.getText().toString();
        p.setHeight(heightStr);
        p.setWeight(weightStr);
        //  Log.d("sfgkdjhgjkdf",""+editTextOPositive.getSelectedItem().toString());
        p.setBloodGroup(pb.editTextOPositive.getSelectedItem().toString());

        if (pb.rgdiabetic.getCheckedRadioButtonId() == R.id.yesdiabetic) {
            p.setDiabetic("1");
        } else if (pb.rgdiabetic.getCheckedRadioButtonId() == R.id.nodiabetic) {
            p.setDiabetic("2");
        }


        if (pb.rgBp1.getCheckedRadioButtonId() == R.id.low) {
            p.setHighBP("1");
        } else if (pb.rgBp1.getCheckedRadioButtonId() == R.id.normal) {
            p.setHighBP("2");
        }
//                    else if(rgBp1.getCheckedRadioButtonId()==R.id.high){
//                        p.setHighBP("3");
//                    }

        if (pb.smoking.getCheckedRadioButtonId() == R.id.Neversmoke) {
            p.setSmoke("1");
        } else if (pb.smoking.getCheckedRadioButtonId() == R.id.Ocassionalsmoke) {
            p.setSmoke("2");
        }
//                    else if(smoking.getCheckedRadioButtonId()==R.id.Regularsomke){
//                        p.setSmoke("3");
//                    }


        if (pb.drinking.getCheckedRadioButtonId() == R.id.Neverdrink) {
            p.setAlcohol("1");
        } else if (pb.drinking.getCheckedRadioButtonId() == R.id.Ocassionaldrink) {
            p.setAlcohol("2");
        }
//                    else if(drinking.getCheckedRadioButtonId()==R.id.Regulardrink){
//                        p.setAlcohol("3");
//                    }

        p.setStdSBP(pb.etsbp.getText().toString().trim());
        p.setStdDBP(pb.etdbp.getText().toString().trim());
        if (Connectivity.isConnected(getApplicationContext())) {
            Utils.closeWaitDialog();
            String userID = new SharedPreference(ProfileActivity.this).getUserID();
//            String url = RetrofitClient.BASE_URL+ "physicianId/" + userID + "/patientSSID/"+p.getSsid()+"/updatepatient";
//           new UpdatePatient(p).execute(url);
            updatePatient(p);
        } else {
            Utils.closeWaitDialog();
            savePatientToMobileDB(p);
        }
    }


    private void savePatientToMobileDB(PatientModel pat) {

        try {
            if (databaseHelper.addPatients(pat, Connectivity.isConnected(getApplicationContext()))) {
                Utils.showWaitDialog(ProfileActivity.this, "");
                showSuccess();
            } else {
                Toast.makeText(this, "Error in adding member", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void showSuccess() {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(ProfileActivity.this, com.hbb20.R.style.Widget_AppCompat_ButtonBar_AlertDialog));
        builder.setMessage("Success");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
                Intent intent = new Intent(ProfileActivity.this,PatientSearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, 15);
            }
        });
        builder.show();

    }


    private void updatePatient(PatientModel p) {
        String userID = new SharedPreference(ProfileActivity.this).getUserID();
        RetrofitClient retrofit = new RetrofitClient();
        Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
        if (retrofitClient == null) {
            return;
        }
        Call<RegisterUserResponse> call = retrofitClient.create(ServiceApi.class).updatePatient(userID,p.getSsid(),p);
        call.enqueue(new Callback<RegisterUserResponse>() {
            @Override
            public void onResponse(Call<RegisterUserResponse> call, Response<RegisterUserResponse> response) {

            }

            @Override
            public void onFailure(Call<RegisterUserResponse> call, Throwable t) {

            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}