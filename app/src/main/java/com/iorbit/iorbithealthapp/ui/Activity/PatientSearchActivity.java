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
import android.widget.Toast;

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
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PatientSearchActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    private static RecyclerView recyclerView;
    static ImageView loading2;
    private PatientListAdapter adapter;
    private List<PatientModel> patientList = new ArrayList<>();
    private List<PatientModel> patientListFiltered = new ArrayList<>();
    EditText patientId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        loading2 = findViewById(R.id.loading2);
        recyclerView = findViewById(R.id.listView);
        patientId = findViewById(R.id.patid);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new PatientListAdapter(PatientSearchActivity.this, patientListFiltered, new PatientListAdapter.ClickListener() {
            @Override
            public void onItemclick(PatientModel patientModel) {
                getCurrentPatient(databaseHelper.getPatientsByID(patientModel.getSsid()));
                finish();
            }

            @Override
            public void onDeleteclick(PatientModel patientModel) {
                deletePatientFromDb(patientModel);
            }


            @Override
            public void onEditclick(PatientModel patientModel) {
                getCurrentPatient(databaseHelper.getPatientsByID(patientModel.getSsid()));
                Intent intent = new Intent(PatientSearchActivity.this, ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        new GetAllPatientsTask(this).execute();

        patientId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loading2.setVisibility(View.VISIBLE);
                new FilterPatientListTask(PatientSearchActivity.this).execute(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void deletePatientFromDb(PatientModel patientModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PatientSearchActivity.this);
        builder.setMessage("Are you sure you want to delete this member?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RetrofitClient retrofit = new RetrofitClient();
                        Retrofit retrofitClient = retrofit.getRetrofitInstance(getApplicationContext());
                        if (retrofitClient == null) {
                            return;
                        }

                        Call<GetPatientModel> call = retrofitClient.create(ServiceApi.class).deletePatient(patientModel.getSsid());
                        call.enqueue(new Callback<GetPatientModel>() {
                            @Override
                            public void onResponse(Call<GetPatientModel> call, Response<GetPatientModel> response) {
                                if (response.isSuccessful()) {
                                    try {
                                       databaseHelper.deletePatient(patientModel.getSsid());
                                            int position = patientListFiltered.indexOf(patientModel);
                                            patientListFiltered.remove(position);
                                            adapter.notifyItemRemoved(position);
                                            adapter.notifyItemRangeChanged(position,patientListFiltered.size());
                                            Snackbar.make(recyclerView, "Patient deleted", Snackbar.LENGTH_LONG).show();

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


    private static class GetAllPatientsTask extends AsyncTask<Void, Void, List<PatientModel>> {

        private WeakReference<PatientSearchActivity> activityWeakReference;

        GetAllPatientsTask(PatientSearchActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PatientSearchActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.loading2.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<PatientModel> doInBackground(Void... voids) {
            PatientSearchActivity activity = activityWeakReference.get();
            if (activity != null) {
                return activity.databaseHelper.getAllPatients();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<PatientModel> patients) {
            super.onPostExecute(patients);
            PatientSearchActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.loading2.setVisibility(View.GONE);
                if (patients != null) {
                    activity.patientList.clear();
                    activity.patientList.addAll(patients);
                    activity.patientListFiltered.addAll(patients);
                    activity.adapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(activity.recyclerView, "Failed to fetch patients", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }

    private static class FilterPatientListTask extends AsyncTask<String, Void, List<PatientModel>> {

        private WeakReference<PatientSearchActivity> activityWeakReference;

        FilterPatientListTask(PatientSearchActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected List<PatientModel> doInBackground(String... strings) {
            PatientSearchActivity activity = activityWeakReference.get();
            if (activity != null) {
                String input = strings[0].toLowerCase();
                List<PatientModel> filteredList = new ArrayList<>();
                for (PatientModel patient : activity.patientList) {
                    if (patient.getFirstName().toLowerCase().contains(input)
                            || patient.getSsid().toLowerCase().contains(input)) {
                        filteredList.add(patient);
                    }
                }
                return filteredList;
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<PatientModel> filteredList) {
            super.onPostExecute(filteredList);
            PatientSearchActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.patientListFiltered.clear();
                if (filteredList != null) {
                    activity.patientListFiltered.addAll(filteredList);
                }
                activity.adapter.notifyDataSetChanged();
                loading2.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void getCurrentPatient(PatientModel patients) {
        if (patients != null) {
            new SharedPreference(PatientSearchActivity.this).saveCurrentPAtient(patients);

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