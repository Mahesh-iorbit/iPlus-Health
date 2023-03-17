package com.iorbit.iorbithealthapp.ui.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.snackbar.Snackbar;
import com.iorbit.iorbithealthapp.Adapters.PatientListAdapter;
import com.iorbit.iorbithealthapp.Helpers.DataBaseManager.DatabaseHelper;
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.Helpers.Utils.Utils;
import com.iorbit.iorbithealthapp.Models.GetPatientModel;
import com.iorbit.iorbithealthapp.Models.PatientModel;
import com.iorbit.iorbithealthapp.Network.Connectivity;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PatientSearchActivity extends AppCompatActivity {
    public static String ssid_global = "";
    static DatabaseHelper databaseHelper;
    private static RecyclerView recyclerView;
    ProgressBar loading2;
    AlertDialog userDialog = null;
    private ArrayList<String> ssids;
    private ArrayList<String> name;
    private EditText patientId;
    private PatientListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        loading2 = findViewById(R.id.loading2);
        recyclerView = findViewById(R.id.listView);
        ssids = new ArrayList<>();
        name = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new PatientListAdapter(PatientSearchActivity.this,ssids, name);

        patientId = findViewById(R.id.patid);
        patientId.requestFocus();

        patientId.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;
                return false;
            }
        });


        adapter.setOnItemClickListener(new PatientListAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                ssid_global = ssids.get(position);
                if (!ssid_global.equalsIgnoreCase("No match found..!!")) {
                    int len = ssids.get(position).length();
                    for (int x = 0; x < 10 - len; x++) {
                        ssid_global = "0" + ssid_global;
                    }
                    getCurrentPatient(databaseHelper.getPatientsByID(ssids.get(position)));
                    Intent in = new Intent(getApplicationContext(), DashBoardActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Utils.closeWaitDialog();
                    startActivity(in);
                    finish();
                }
            }

            @Override
            public void onEditItemClick(int position, View v) {
                Intent intent = new Intent(PatientSearchActivity.this,ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("PatientEditDetailsDetails",databaseHelper.getPatientsByID(ssids.get(position)).toString());
                startActivityForResult(intent, 15);
            }

            @Override
            public void onDeleteItemClick(final int position, View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchActivity.this);
                    builder.setMessage("Are you sure you want to delete this member?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    RetrofitClient retrofit = new RetrofitClient();
                                    Retrofit retrofitClient = retrofit.getRetrofitInstance(getApplicationContext());
                                    if (retrofitClient == null) {
                                        return;
                                    }

                                    Call<GetPatientModel> call = retrofitClient.create(ServiceApi.class).deletePatient(ssids.get(position));
                                    call.enqueue(new Callback<GetPatientModel>() {
                                        @Override
                                        public void onResponse(Call<GetPatientModel> call, Response<GetPatientModel> response) {
                                            if (response.isSuccessful()) {
                                                try {
                                                        // handler.deletePatient(ssids.get(position));
                                                        //ssids.clear();
                                                        // name.clear();
                                                        adapter.notifyItemRemoved(position);
                                                        // handler.getAllPatients();
                                                        loading2.setVisibility(View.INVISIBLE);
                                                        Intent intent = new Intent(PatientSearchActivity.this, DashBoardActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivityForResult(intent, 15);
                                                    Utils.showSnackbar(findViewById(android.R.id.content),"Member deleted successfully!", Snackbar.LENGTH_SHORT);
                                                    } catch (Exception e) {
                                                    Utils.showSnackbar(findViewById(android.R.id.content),"Something went wrong!!", Snackbar.LENGTH_SHORT);
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

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }


        });


        recyclerView.setAdapter(adapter);
        patientId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.e("event = ","beforeTextChanged");

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                loading2.setVisibility(View.VISIBLE);
                String query = patientId.getText().toString();
                ssids.clear();
                adapter.notifyDataSetChanged();
                if (query.trim().length() >= 1) {
                    searchPIDInDatabase(query);
                } else {
                    new GetPatientInDatabase(PatientSearchActivity.this).execute();
                    //  loading2.setVisibility(View.INVISIBLE);
                }
            }
        });

        //showSyncSnackBar();
    }

    public void getCurrentPatient(PatientModel patients) {
        if (patients != null) {
            new SharedPreference(PatientSearchActivity.this).saveCurrentPAtient(patients);

        }
    }

    private void searchPIDInDatabase(final String query) {
        new SearchPatientInDatabase(PatientSearchActivity.this).execute(query);

    }

    public String replaceLeadingZeros(String s) {
        s = s.replaceAll("^[0]+", "");
        if (s.equals("")) {
            return "0";
        }

        return s;
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetPatientInDatabase(PatientSearchActivity.this).execute();
    }





    public String convert(String str) {
        int len = str.length();
        if (len < 10)
            for (int i = 0; i < 10 - len; i++) {
                str = "0" + str;
            }
        String pid = "";
        for (int i = 0; i < str.length(); i++) {
            pid += Integer.toHexString((int) str.charAt(i));
        }
        return pid;
    }

    private void showDialogMessage(String title, String body) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private String fixString(String s) {
        if (s.length() == 1) {
            return "0" + s;
        } else {
            return s;
        }
    }









    private class SearchPatientInDatabase extends AsyncTask<String, Void, List<PatientModel>> {

        private WeakReference<PatientSearchActivity> activityReference;

        SearchPatientInDatabase(PatientSearchActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<PatientModel> doInBackground(String... params) {
            List<PatientModel> patientsListTemp = databaseHelper.getAllPatients();
            List<PatientModel> patientsList = new ArrayList<>();
            String tempName2 = "";
            String tempName1 = "";
            if (patientsListTemp != null) {
                if (patientsListTemp.size() > 0) {
                    for (PatientModel p : patientsListTemp) {
                        if(p.getFirstName().toLowerCase().startsWith(params[0].toLowerCase())||p.getLastName().toLowerCase().startsWith(params[0].toLowerCase()))
                            patientsList.add(p);

                    }
                }
            }


            return patientsList;
        }

        @Override
        protected void onPostExecute(List<PatientModel> pats) {

            // get a reference to the activity if it is still there
            PatientSearchActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            // modify the activity's UI
            ssids.clear();
            name.clear();
            for (PatientModel p : pats) {
                ssids.add(p.getSsid());
                name.add(p.getFirstName() + " " + p.getLastName());
                adapter.notifyDataSetChanged();
                loading2.setVisibility(ImageView.VISIBLE);
            }

            if (pats.isEmpty()) { //if not found in database, search from cloud..
                if (!Connectivity.isConnected(PatientSearchActivity.this)) {
                    ssids.add("No match found..!!");
                    adapter.notifyDataSetChanged();
                } else {
                    ssids.clear();
                    //   searchPIDInCloud(patientId.getText().toString().trim());
                }
            }

            loading2.setVisibility(View.INVISIBLE);
        }
    }

    private class GetPatientInDatabase extends AsyncTask<Void, Void, List<PatientModel>> {

        private WeakReference<PatientSearchActivity> activityReference;

        GetPatientInDatabase(PatientSearchActivity context) {
            activityReference = new WeakReference<>(context);
        }


        @Override
        protected List<PatientModel> doInBackground(Void... voids) {
            return databaseHelper.getAllPatients();
        }

        @Override
        protected void onPostExecute(List<PatientModel> pats) {

            // get a reference to the activity if it is still there
            PatientSearchActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            // modify the activity's UI
            ssids.clear();

            for (PatientModel p : pats) {
                ssids.add(p.getSsid());
                name.add(p.getFirstName() + " " + p.getLastName());
                adapter.notifyDataSetChanged();
                loading2.setVisibility(ImageView.VISIBLE);
            }

            if (pats.isEmpty()) { //if not found in database, search from cloud..
                if (!Connectivity.isConnected(PatientSearchActivity.this)) {
                    ssids.add("No match found..!!");
                    adapter.notifyDataSetChanged();
                } else {
                    ssids.clear();
                    //   searchPIDInCloud(patientId.getText().toString().trim());
                }
            }

            loading2.setVisibility(View.INVISIBLE);
        }
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