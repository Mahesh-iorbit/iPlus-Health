package com.iorbit.iorbithealthapp.ui.Activity;

import static maes.tech.intentanim.CustomIntent.customType;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.iorbit.iorbithealthapp.Helpers.DataBaseManager.DatabaseHelper;
import com.iorbit.iorbithealthapp.Helpers.Interface.OnRetryClickListener;
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.Helpers.Utils.Utils;
import com.iorbit.iorbithealthapp.Models.LoginUserModel;
import com.iorbit.iorbithealthapp.Models.RegisterUserModel;
import com.iorbit.iorbithealthapp.Models.RegisterUserResponse;
import com.iorbit.iorbithealthapp.Models.StatusResponse;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.R;
import com.ybs.passwordstrengthmeter.PasswordStrength;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity implements OnRetryClickListener{

    private EditText username;
    private EditText password;
    private EditText email;
    private String code, Mobilecode;
    private Button signUp;
    private AlertDialog userDialog;
    private TextInputLayout textInputMobilenumber, textInputpassword, textInputEmail, textInputName, textInputConfirmPassword, textInputLastName;
    private String usernameInput;
    private String userPasswd;
    private TextInputEditText hospitalName, LastName;
    private TextInputEditText verifyconfirmpassword;
    private CountryCodePicker ccp;
    private ConstraintLayout constraintlayout;
    ProgressBar progressBar;
    TextView strengthView;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_HINT = 1000;
    private static int SPLASH_TIME_OUT = 1000;
    DatabaseHelper databaseHelper;
    public String globalUserID = "";


    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        databaseHelper = new DatabaseHelper(this);
        constraintlayout = findViewById(R.id.main_layout);
        ccp = findViewById(R.id.MobileNumberCode);
        textInputMobilenumber = findViewById(R.id.textInputMobilenumber);
        textInputpassword = findViewById(R.id.textInputpassword);
        textInputEmail = findViewById(R.id.textInputhospitalEmail);
        hospitalName = findViewById(R.id.hospitalName);
        LastName = findViewById(R.id.lastName);
        textInputName = findViewById(R.id.textInputName);
        textInputLastName = findViewById(R.id.textInputLastName);
        verifyconfirmpassword = findViewById(R.id.hospitalConfirmPassword);
        password = findViewById(R.id.hospitalPassword);
        textInputConfirmPassword = findViewById(R.id.textInputConfirmPassword);
        progressBar = findViewById(R.id.progressBar);
        strengthView = findViewById(R.id.password_strength);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.CREDENTIALS_API)
                .build();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Create a hint request to get the user's phone number
                HintRequest hintRequest = new HintRequest.Builder()
                        .setPhoneNumberIdentifierSupported(true)
                        .build();
                PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(
                        mGoogleApiClient, hintRequest);
                try {
                    startIntentSenderForResult(intent.getIntentSender(),
                            RC_HINT, null, 0, 0, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, SPLASH_TIME_OUT);
        // Create a Google API client

        ccp =  findViewById(R.id.MobileNumberCode);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                progressBar.setVisibility(View.VISIBLE);
                strengthView.setVisibility(View.VISIBLE);
                updatePasswordStrengthView(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                textInputpassword.setError(null);
                textInputConfirmPassword.setError(null);
                if (password.getText().toString().equals(verifyconfirmpassword.getText().toString()))
                    textInputConfirmPassword.setError(null);
            }
        });

        verifyconfirmpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (password.getText().toString().equals(verifyconfirmpassword.getText().toString()))
                    textInputConfirmPassword.setError(null);
                else
                    textInputConfirmPassword.setError("Password do not match");
            }
        });
        init();
    }

    private void updatePasswordStrengthView(String password) {
        if (TextView.VISIBLE != strengthView.getVisibility())
            return;
        if (password.isEmpty()) {
            strengthView.setText("");
            progressBar.setProgress(0);
            return;
        }
        PasswordStrength str = PasswordStrength.calculateStrength(password);
        strengthView.setText(str.getText(this));
        strengthView.setTextColor(str.getColor());

        progressBar.getProgressDrawable().setColorFilter(str.getColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        if (str.getText(this).equals("Weak")) {
            progressBar.setProgress(25);
        } else if (str.getText(this).equals("Medium")) {
            progressBar.setProgress(50);
        } else if (str.getText(this).equals("Strong")) {
            progressBar.setProgress(75);
        } else {
            progressBar.setProgress(100);
        }
    }

    private void init() {

        hospitalName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textInputName.setError(null);
                if (hospitalName.getText().toString().trim().equalsIgnoreCase("")) {
                    textInputName.setError("First name cannot be empty");
                }
            }
        });

        LastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textInputLastName.setError(null);
                if (LastName.getText().toString().trim().equalsIgnoreCase("")) {
                    textInputLastName.setError("Last name cannot be empty");
                }
            }
        });

        username = findViewById(R.id.hospitalUser);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textInputMobilenumber.setError(null);
            }
        });


        email = findViewById(R.id.hospitalEmail);


        signUp = findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mobilecode = ccp.getSelectedCountryCodeWithPlus().toString().trim();
                usernameInput = username.getText().toString().replace("(", "").replace(")", "").replace(" ", "").replace("-", "").trim();
                code = Mobilecode + usernameInput;

                if (hospitalName.getText().toString().trim().equalsIgnoreCase("")) {
                    textInputName.setError("First name cannot be empty");
                    return;
                }
                if (LastName.getText().toString().trim().equalsIgnoreCase("")) {
                    textInputLastName.setError("Last name cannot be empty");
                    return;
                }

                if (usernameInput == null || usernameInput.isEmpty() || usernameInput.length() < 10 || usernameInput.length() > 10) {
                    textInputMobilenumber.setError("Please enter valid number");

                    return;
                }

                String userpasswordInput = password.getText().toString().trim();
                userPasswd = userpasswordInput;
                if (userpasswordInput == null || userpasswordInput.isEmpty()) {
                    textInputpassword.setError("Cannot be empty");

                    return;
                } else if (userPasswd.length() < 8) {
                    textInputpassword.setError("Password must be minimum of 8 characters");
                    return;
                }

                if (!password.getText().toString().equals(verifyconfirmpassword.getText().toString())) {
                    textInputConfirmPassword.setError("Password do not match");
                    return;
                }

                checkPatientAlreadyExists();

            }
        });
    }




    private void checkPatientAlreadyExists() {
        Utils.showLoaderDialog(this);
        if (Utils.isConnected(this)) {
            RetrofitClient retrofit = new RetrofitClient();
            Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
            if (retrofitClient == null) {
                return;
            }

            Call<StatusResponse> call = retrofitClient.create(ServiceApi.class).UserCheck(code);
            call.enqueue(new Callback<StatusResponse>() {
                @Override
                public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                    if (response.isSuccessful()) {
                        StatusResponse statusResponse = response.body();
                        if (statusResponse.getMessage().equalsIgnoreCase("User Already Exists")) {
                            Utils.closeLoaderDialog();
                            showSnackBar(statusResponse.getMessage());
                        } else if (statusResponse.getMessage().equalsIgnoreCase("User Not Found")) {
                            confirmSignUp();
                        }
                    }else{
                        showSnackBar("Something went wrong!!");
                        Utils.closeLoaderDialog();
                    }
                }

                @Override
                public void onFailure(Call<StatusResponse> call, Throwable t) {
                    showSnackBar("Something went wrong!!");
                    Utils.closeLoaderDialog();
                }
            });
        }else{
            Utils.closeLoaderDialog();
            Utils.showNoInternetDialog(this, (OnRetryClickListener) this);
        }

    }

    private void showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(constraintlayout, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void confirmSignUp() {
        globalUserID = ccp.getSelectedCountryCodeWithPlus().toString().trim() + username.getText().toString().trim();
        RegisterUserModel registerUserModel = new RegisterUserModel();
        registerUserModel.setHospitalName(hospitalName.getText().toString().trim());
        registerUserModel.setUserEmail(email.getText().toString().trim());
        registerUserModel.setLastName(LastName.getText().toString().trim());
        registerUserModel.setHospitalID(globalUserID);
        saveHospitalToCloud(registerUserModel);
    }

    private void saveHospitalToCloud(RegisterUserModel registerUserModel) {
        registerUserModel.setStatus(-1);
        registerUserModel.setUserId(registerUserModel.getHospitalID());
        registerUserModel.setUserName(registerUserModel.getHospitalName());
        registerUserModel.setUserPassword(userPasswd);
        RetrofitClient retrofit = new RetrofitClient();
        Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
        if (retrofitClient == null) {
            return;
        }

        Call<RegisterUserResponse> call = retrofitClient.create(ServiceApi.class).UserRegister(registerUserModel);
        call.enqueue(new Callback<RegisterUserResponse>() {
            @Override
            public void onResponse(Call<RegisterUserResponse> call, Response<RegisterUserResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        RegisterUserResponse userResponse = response.body();
                        if (userResponse.getStatusdet().getCode().equalsIgnoreCase("200 OK")) {
                            LoginUser();
                            //showDialogMessage("Success!", "Registration Completed", true);
                        } else {
                            if (userResponse.getStatusdet().getMessage().equalsIgnoreCase("Error")
                                    && userResponse.getStatusdet().getDetails().equalsIgnoreCase("User Already Exist")) {
                                Utils.closeLoaderDialog();
                                showSnackBar("UserAlready Exist");
                                //9d344910-a394-11ed-abb9-59b2cc805d2d
                                //Toast.makeText(getApplicationContext(), "User Already Exist", Toast.LENGTH_LONG).show();
                            } else {
                                Utils.closeLoaderDialog();
                                showSnackBar("Registration Failed");
                                // Toast.makeText(getApplicationContext(), "Failed..!!!", Toast.LENGTH_LONG).show();

                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.closeLoaderDialog();
                    showSnackBar("Registration Failed");
                }


            }

            @Override
            public void onFailure(Call<RegisterUserResponse> call, Throwable t) {
                showSnackBar("Registration Failed");
                Utils.closeLoaderDialog();
            }
        });

    }

    private void LoginUser() {
        final CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.MobileNumberCode);
        Mobilecode = ccp.getSelectedCountryCodeWithPlus().toString().trim();
        LoginUserModel loginUser = new LoginUserModel();
        loginUser.setUserId(Mobilecode + "" + usernameInput);
        loginUser.setUserPassword(userPasswd);
        if(Utils.isConnected(this)){
            databaseHelper = new DatabaseHelper(getApplicationContext());
            RetrofitClient retrofit = new RetrofitClient();
            Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
            if (retrofitClient == null) {
                return;
            }
            Call<LoginUserModel> call = retrofitClient.create(ServiceApi.class).UserLogin(loginUser);
            call.enqueue(new Callback<LoginUserModel>() {
                @Override
                public void onResponse(Call<LoginUserModel> call, Response<LoginUserModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            LoginUserModel loginUser1 = response.body();
                            if (loginUser1.getStatusdet().getCode().equalsIgnoreCase("200 OK")) {
                                    databaseHelper.deletePatients();
                                    new SharedPreference(getApplicationContext()).clearCurrentPatient();
                                new SharedPreference(RegisterActivity.this).saveUserID(loginUser1.getUuid());
                                new SharedPreference(RegisterActivity.this).saveUserName(loginUser1.getUserName());
                                Intent in = new Intent(getApplicationContext(), DashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(in);
                                customType(RegisterActivity.this,"fadein-to-fadeout");
                                finish();
                                //showDialogMessage("Success!", userName + " has been confirmed!", true);
                            }
                        } else {
                            com.iorbit.iorbithealthapp.Helpers.Utils.Utils.showSnackbar(findViewById(android.R.id.content),"User not found!!", Snackbar.LENGTH_SHORT);
                            com.iorbit.iorbithealthapp.Helpers.Utils.Utils.closeLoaderDialog();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        com.iorbit.iorbithealthapp.Helpers.Utils.Utils.closeLoaderDialog();
                    }
                    com.iorbit.iorbithealthapp.Helpers.Utils.Utils.closeLoaderDialog();
                }

                @Override
                public void onFailure(Call<LoginUserModel> call, Throwable t) {
                    com.iorbit.iorbithealthapp.Helpers.Utils.Utils.closeLoaderDialog();
                    Utils.showSnackbar(findViewById(android.R.id.content),"Something went wrong!!",Snackbar.LENGTH_SHORT);
                }
            });
        }else{
            Utils.closeLoaderDialog();
            Utils.showNoInternetDialog(this, (OnRetryClickListener) this);
        }


    }


    private void exit(String uname) {
        exit(uname, null);
    }

    private void exit(String uname, String password) {
        Intent intent = new Intent();
        if (uname == null) {
            uname = "";
        }
        if (password == null) {
            password = "";
        }
        intent.putExtra("name", uname);
        intent.putExtra("password", password);
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        customType(RegisterActivity.this, "fadein-to-fadeout");
        finish();
    }

    @Override
    public void onRetryClick() {
        checkPatientAlreadyExists();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_HINT && resultCode == RESULT_OK) {
                try {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    ccp.setCountryForPhoneCode(Integer.parseInt(credential.getId().substring(1, 3)));
                    username.setText(credential.getId().substring(3));
                } catch (Exception e){
                    Utils.showSnackbar(findViewById(android.R.id.content),"Something went wrong!!",Snackbar.LENGTH_SHORT);
                }
            }
        }

}