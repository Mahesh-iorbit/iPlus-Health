package com.iorbit.iorbithealthapp.ui.Activity;

import static com.iorbit.iorbithealthapp.Helpers.Utils.Utils.closeWaitDialog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;
import com.hbb20.CountryCodePicker;
import com.iorbit.iorbithealthapp.Helpers.DataBaseManager.DatabaseHelper;
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.Helpers.Utils.Utils;
import com.iorbit.iorbithealthapp.Models.PatientModel;
import com.iorbit.iorbithealthapp.Models.StatusResponse;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.R;
import com.iorbit.iorbithealthapp.databinding.ActivityAddPatientBinding;

import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddPatientActivity extends AppCompatActivity {

    ActivityAddPatientBinding activityAddPatientBinding;
    DatabaseHelper databaseHelper;
    Map<String, Integer> a = new HashMap<>();
    RadioButton rbgender;
    String gender,mPrefix,Mobilecode,userName;
    private androidx.appcompat.app.AlertDialog userDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddPatientBinding = ActivityAddPatientBinding.inflate(getLayoutInflater());
        View view = activityAddPatientBinding.getRoot();
        setContentView(view);

        initView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        activityAddPatientBinding.prefix.setText(new SharedPreference(AddPatientActivity.this).getUserID().substring(0, 3));
        mPrefix = new SharedPreference(AddPatientActivity.this).getUserID().substring(0, 3);

        activityAddPatientBinding.etcalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showAlertDialogMessage(AddPatientActivity.this, "BP calibration is necessary to minimize measurement uncertainty by ensuring the accuracy of Sense-H device.", false);
            }
        });

        activityAddPatientBinding.generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityAddPatientBinding.etPID.setText(databaseHelper.generateUniquePID());
            }
        });


        activityAddPatientBinding.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activityAddPatientBinding.moreInfo.getVisibility() == View.GONE) {
                    activityAddPatientBinding.more.setText("- Less Information");
                    activityAddPatientBinding.moreInfo.setVisibility(View.VISIBLE);
                } else {
                    activityAddPatientBinding.more.setText("+ More Information");
                    activityAddPatientBinding.moreInfo.setVisibility(View.GONE);
                }
            }
        });



        activityAddPatientBinding.bSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValidated = true;
                if (activityAddPatientBinding.etFirstName.getText().toString().equalsIgnoreCase("")) {
                    activityAddPatientBinding.textFirstName.setError(" ");
                    isValidated = false;

                }
                if (activityAddPatientBinding.etLastName.getText().toString().equalsIgnoreCase("")) {
                    activityAddPatientBinding.textLastName.setError(" ");
                    isValidated = false;

                }
                if (activityAddPatientBinding.etAge.getText().toString().equalsIgnoreCase("")) {
                    activityAddPatientBinding.textInputAge.setError(" ");
                    isValidated = false;

                }
                if (activityAddPatientBinding.etContactNo.getText().toString().equalsIgnoreCase("")) {
                    activityAddPatientBinding.textInputConatctnumber.setError(" ");
                    isValidated = false;

                }
                if (activityAddPatientBinding.etContactNo.getText().toString().trim().length() < 10) {
                    activityAddPatientBinding.textInputConatctnumber.setError("Enter valid Mobile Number");
                    isValidated = false;

                }
                if (activityAddPatientBinding.etCity.getText().toString().equalsIgnoreCase("")) {
                    activityAddPatientBinding.textInputCity.setError(" ");
                    isValidated = false;

                }
                if (activityAddPatientBinding.etPID.getText().toString().equalsIgnoreCase("")) {
                    activityAddPatientBinding.etPID.setError(" ");
                    isValidated = false;

                }
                if (isValidated)
                    save();
            }
        });

    }

    private void initView() {
        a.put(" ", 0);
        a.put("O +ve", 1);
        a.put("O -ve", 2);
        a.put("A +ve", 3);
        a.put("A -ve", 4);
        a.put("B +ve", 5);
        a.put("B -ve", 6);
        a.put("AB +ve", 7);
        a.put("AB -ve", 8);
        a.put("", -1);
    }


    private void save() {
        Utils.showWaitDialog(AddPatientActivity.this, "");
        int selectedId = activityAddPatientBinding.rggender.getCheckedRadioButtonId();
        rbgender = findViewById(selectedId);
        gender = rbgender.getText().toString().trim();
        int len = activityAddPatientBinding.etPID.getText().toString().trim().length();
        String pidx = activityAddPatientBinding.etPID.getText().toString().trim();
        for (int x = 0; x < 4 - len; x++) {
            pidx = "0" + pidx;
        }
        pidx = mPrefix + pidx;
        PatientModel p = new PatientModel();
        final CountryCodePicker ccp = findViewById(R.id.MobileNumberCode);
        Mobilecode = ccp.getSelectedCountryCodeWithPlus().trim();
        p.addPatients(pidx, new SharedPreference(AddPatientActivity.this).getUserID(), activityAddPatientBinding.etemail.getText().toString(),activityAddPatientBinding.etCity.getText().toString(),activityAddPatientBinding.etFirstName.getText().toString(),activityAddPatientBinding.etLastName.getText().toString(),activityAddPatientBinding.etAge.getText().toString(), gender, Mobilecode + activityAddPatientBinding.etContactNo.getText().toString());
        p.setCreatedBy(new SharedPreference(AddPatientActivity.this).getUserID());
        p.setCreatedByRole("H");
        String heightStr = activityAddPatientBinding.editTextHieght.getText().toString();
        String weightStr = activityAddPatientBinding.editTextWieght.getText().toString();
        p.setHeight(heightStr);
        p.setWeight(weightStr);
        p.setBloodGroup(activityAddPatientBinding.editTextOPositive.getSelectedItem().toString());

        if (activityAddPatientBinding.rgdiabetic.getCheckedRadioButtonId() == R.id.yesdiabetic) {
            p.setDiabetic("1");
        } else if (activityAddPatientBinding.rgdiabetic.getCheckedRadioButtonId() == R.id.nodiabetic) {
            p.setDiabetic("2");
        }


        if (activityAddPatientBinding.rgBp1.getCheckedRadioButtonId() == R.id.low) {
            p.setHighBP("1");
        } else if (activityAddPatientBinding.rgBp1.getCheckedRadioButtonId() == R.id.normal) {
            p.setHighBP("2");
        }

        if (activityAddPatientBinding.smoking.getCheckedRadioButtonId() == R.id.Neversmoke) {
            p.setSmoke("1");
        } else if (activityAddPatientBinding.smoking.getCheckedRadioButtonId() == R.id.Ocassionalsmoke) {
            p.setSmoke("2");
        }

        if (activityAddPatientBinding.drinking.getCheckedRadioButtonId() == R.id.Neverdrink) {
            p.setAlcohol("1");
        } else if (activityAddPatientBinding.drinking.getCheckedRadioButtonId() == R.id.Ocassionaldrink) {
            p.setAlcohol("2");
        }

        p.setStdSBP(activityAddPatientBinding.etsbp.getText().toString().trim());
        p.setStdDBP(activityAddPatientBinding.etdbp.getText().toString().trim());
        if (Utils.isConnected(getApplicationContext())) {
            checkPatientAlreadyExists(p);
            closeWaitDialog();
        } else {
            closeWaitDialog();
            savePatientToMobileDB(p);
        }


    }

    private void checkPatientAlreadyExists(PatientModel patients) {

        RetrofitClient retrofit = new RetrofitClient();
        Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
        if (retrofitClient == null) {
            return;
        }
        Call<StatusResponse> call = retrofitClient.create(ServiceApi.class).CheckPateint(patients.getContactNo());
        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {

                if(response.isSuccessful()){
                    StatusResponse s = response.body();
                    if(s.getMessage().equalsIgnoreCase("User Already Exists")){
                        Toast.makeText(AddPatientActivity.this, s.getMessage(), Toast.LENGTH_SHORT).show();
                    }else if(s.getMessage().equalsIgnoreCase("User Not Found")){
                        savePatientToCloud(patients);
                    }
                }

            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {

            }
        });

    }


    private void savePatientToCloud(final PatientModel pat) {
        String userID = new SharedPreference(AddPatientActivity.this).getUserID();
        RetrofitClient retrofit = new RetrofitClient();
        Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
        if (retrofitClient == null) {
            return;
        }
        Call<StatusResponse> call = retrofitClient.create(ServiceApi.class).addPatient(userID,pat);
        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful()) {
                    StatusResponse statusResponse = new StatusResponse();
                    statusResponse = response.body();
                    if (statusResponse.getCode().equalsIgnoreCase("200 OK")) {
                        savePatientToMobileDB(pat);
                        if(new SharedPreference(AddPatientActivity.this).getCurrentPAtient()==null)
                            new SharedPreference(AddPatientActivity.this).saveCurrentPAtient(pat);
                        Utils.closeWaitDialog();
                        showDialogMessage("Success!", pat.getContactNo() + " has been registered!", true);

                    } else {
                        Utils.closeWaitDialog();
                        Toast.makeText(getApplicationContext(),"Patient Already Registered..!!!",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(AddPatientActivity.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Toast.makeText(AddPatientActivity.this, "Something went wrong !!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void savePatientToMobileDB(PatientModel pat) {

        try {
            if (databaseHelper.addPatients(pat, Utils.isConnected(getApplicationContext()))) {
                Utils.showWaitDialog(AddPatientActivity.this, "");
                //showSuccess();
            } else {
                //Utils.showException("Error in adding member");
            }
        } catch (Exception e) {
            //Utils.showException(e.getMessage());
        }

    }

    private void showDialogMessage(String title, String body, final boolean exitActivity) {
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if (exitActivity) {
                        exit();
                    }
                } catch (Exception e) {
                    exit();
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void exit() {
        Intent intent = new Intent();
        if (userName == null)
            userName = "";
        intent.putExtra("name", userName);
        setResult(RESULT_OK, intent);
        finish();
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