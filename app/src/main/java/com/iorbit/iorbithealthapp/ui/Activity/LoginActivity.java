package com.iorbit.iorbithealthapp.ui.Activity;



import static maes.tech.intentanim.CustomIntent.customType;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hbb20.CountryCodePicker;
import com.iorbit.iorbithealthapp.Helpers.DataBaseManager.DatabaseHelper;
import com.iorbit.iorbithealthapp.Helpers.Interface.OnRetryClickListener;
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SecurePreferences;
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.Helpers.Utils.Utils;
import com.iorbit.iorbithealthapp.Models.LoginUserModel;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.R;
import com.iorbit.iorbithealthapp.databinding.ActivityLoginBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements OnRetryClickListener{

    static ActivityLoginBinding lb;
    SecurePreferences sf;
    public String globalUserID = "",username,password,Mobilecode;
    DatabaseHelper databaseHelper;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lb = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = lb.getRoot();
        setContentView(view);
        sf = new SecurePreferences(getApplicationContext(), "rememberpass", "ssmi", true);

        if (sf.getString("remember") != null)
            if (!sf.getString("remember").equalsIgnoreCase("")) {
                lb.remember.setChecked(true);
                lb.editTextUserPassword.setText(sf.getString("remember"));
                lb.editTextUserId.setText(sf.getString("rememberPhone"));
            } else
                lb.remember.setChecked(false);
        globalUserID = lb.MobileNumberCode.getSelectedCountryCodeWithPlus().toString().trim() + "" +lb.editTextUserPassword.getText().toString();
        lb.remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    sf.put("remember", lb.editTextUserPassword.getText().toString().trim());
                    sf.put("rememberPhone",lb.editTextUserId.getText().toString().trim());
                } else
                    sf.clear();
            }
        });

        initApp();

        lb.buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidPhoneNumber() && isValidPassword()){
                   LoginUser();
                }
            }
        });

        lb.textViewUserSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });


//        lb.textViewUserForgotPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(),ForgotPasswordActivity.class));
//            }
//        });
    }

    public boolean isValidPhoneNumber() {
        String phoneNumber = lb.editTextUserId.getText().toString().trim();
        boolean isValid = isValidPNO(phoneNumber,lb.MobileNumberCode.getSelectedCountryNameCode());
        if (phoneNumber.isEmpty()) {
            lb.textInputMobilenumber.setError("Phone number is required");
            return false;
        } else if (!isValid) {
            lb.textInputMobilenumber.setError("Invalid phone number");
            return false;
        } else {
            lb.textInputMobilenumber.setError(null);
            return true;
        }
    }

    public boolean isValidPNO(String phoneNumber, String countryCode) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(phoneNumber, countryCode);
            return phoneUtil.isValidNumber(number);
        } catch (NumberParseException e) {
            return false;
        }
    }

    public boolean isValidPassword() {
        String Password = lb.editTextUserPassword.getText().toString().trim();
        if (Password.isEmpty()) {
            lb.textInputpassword.setError("Password is required");
            return false;
        } else {
            lb.textInputpassword.setError(null);
            return true;
        }
    }


    private void signUpUser() {
        Intent registerActivity = new Intent(this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(registerActivity);
        finish();
        customType(LoginActivity.this,"fadein-to-fadeout");

    }


    private void LoginUser() {
        Utils.showLoaderDialog(this);
        lb.textInputpassword.setError(null);
        lb.textInputMobilenumber.setError(null);
        lb.editTextUserPassword.clearFocus();
        final CountryCodePicker ccp = (CountryCodePicker) findViewById(R.id.MobileNumberCode);
        Mobilecode = ccp.getSelectedCountryCodeWithPlus().toString().trim();
        username = lb.editTextUserId.getText().toString().replace("(", "").replace(")", "").replace(" ", "").replace("-", "").trim();
        password = lb.editTextUserPassword.getText().toString().trim();
        LoginUserModel loginUser = new LoginUserModel();
        loginUser.setUserId(Mobilecode + "" + username);
        loginUser.setUserPassword(password);
        if(Utils.isConnected(this)){
            if (lb.remember.isChecked()) {
                sf.put("remember", lb.editTextUserPassword.getText().toString().trim());
                sf.put("rememberPhone", lb.editTextUserId.getText().toString().trim());
            } else
                sf.clear();
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
                    if (lb.remember.isChecked()) {
                        sf.put("remember", lb.editTextUserPassword.getText().toString().trim());
                        sf.put("rememberPhone", lb.editTextUserId.getText().toString().trim());
                    } else
                        sf.clear();

                    try {
                        if (response.isSuccessful()) {
                            LoginUserModel loginUser1 = response.body();
                            if (loginUser1.getStatusdet().getCode().equalsIgnoreCase("200 OK")) {
                                if (!globalUserID.equalsIgnoreCase(loginUser1.getUserId())) {
                                    databaseHelper.deletePatients();
                                    new SharedPreference(getApplicationContext()).clearCurrentPatient();
                                }
                                new SharedPreference(LoginActivity.this).saveUserID(loginUser1.getUuid());
                                new SharedPreference(LoginActivity.this).saveUserName(loginUser1.getUserName());
                                Intent in = new Intent(getApplicationContext(), DashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(in);
                                customType(LoginActivity.this,"fadein-to-fadeout");
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



    // initialize app
    private void initApp() {
        lb.editTextUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lb.textInputMobilenumber.setError(null);

            }
        });

        lb.editTextUserPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lb.textInputpassword.setError(null);

            }
        });
    }


    @Override
    public void onRetryClick() {
        LoginUser();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}