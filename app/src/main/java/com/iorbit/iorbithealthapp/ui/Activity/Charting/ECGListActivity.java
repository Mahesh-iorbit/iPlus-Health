package com.iorbit.iorbithealthapp.ui.Activity.Charting;

import static com.iorbit.iorbithealthapp.Helpers.Utils.Utils.closeWaitDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iorbit.iorbithealthapp.Adapters.ECGAdapter;
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.Models.ECGListResponse;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.R;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ECGListActivity extends AppCompatActivity {
    private static ECGAdapter adapter;
    private static RecyclerView recyclerView;
    private static ArrayList<ECGListResponse.Ecgschema> data;
    TickSeekBar s;
    private RecyclerView.LayoutManager layoutManager;
    private TextView textViewNoData;
    int duration = 1;
    String TAG = "seekbar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecglist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        textViewNoData=findViewById(R.id.empty_view);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //   recyclerView.set

        data = new ArrayList<ECGListResponse.Ecgschema>();

        s = findViewById(R.id.listener);
        s.customTickTexts(new String[]{"Today", "1 Week", "3 Months", "6 Months", "1 Year"});
        s.setOnSeekChangeListener(new OnSeekChangeListener() {
            SeekParams seekParams = null;

            @Override
            public void onSeeking(SeekParams seekParams) {
                this.seekParams = seekParams;
            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {
                if (seekParams != null) {
                    Date dt = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(dt);
                    switch (seekParams.tickText) {
                        case "Today":
                            duration=1;
                            dt = c.getTime();
                            Log.e(TAG, dt.toString());
                            break;
                        case "1 Week":
                            duration=2;
                            c.add(Calendar.DATE, -7);
                            dt = c.getTime();
                            Log.e(TAG, dt.toString());
                            break;
                        case "3 Months":
                            duration=3;
                            c.add(Calendar.MONTH, -3);
                            dt = c.getTime();
                            Log.e(TAG, dt.toString());
                            break;
                        case "6 Months":
                            duration=4;
                            c.add(Calendar.MONTH, -6);
                            dt = c.getTime();
                            Log.e(TAG, dt.toString());
                            break;
                        case "1 Year":
                            duration=5;
                            c.add(Calendar.YEAR, -1);
                            dt = c.getTime();
                            Log.e(TAG, dt.toString());
                            break;
                    }

                    Date x = new Date();
                    Calendar cl = Calendar.getInstance();
                    cl.setTime(x);
                    cl.add(Calendar.DATE, 1);
                    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
                    String dateStr = mdformat.format(cl.getTime());
                    data.clear();
                    adapter.notifyDataSetChanged();
                    getMeasurementFromCloud();
                    // checkId2(mApplication.getP().getSsid(),getcurrentDate(c),dateStr);
                }
            }

        });

        adapter = new ECGAdapter(data);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new ECGAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(ECGListActivity.this, EcgDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("measurementId", String.valueOf(data.get(position).getMeasurementId()));
                startActivityForResult(intent, 10);
            }

            @Override
            public void onEditItemClick(int position, View v) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getMeasurementFromCloud();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public void getMeasurementFromCloud() {
        RetrofitClient retrofit = new RetrofitClient();
        Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
        if (retrofitClient == null) {
            return;
        }
        Call<ECGListResponse> call = retrofitClient.create(ServiceApi.class).getEcgMeasure(new SharedPreference(ECGListActivity.this).getCurrentPAtient().getSsid(),duration);
        call.enqueue(new Callback<ECGListResponse>() {
            @Override
            public void onResponse(Call<ECGListResponse> call, Response<ECGListResponse> response) {
                if(response.isSuccessful()){
                    ECGListResponse ecgListResponse = response.body();
                    data.clear();
                    if (ecgListResponse.getEcgschema().size() > 0) {
                        for (ECGListResponse.Ecgschema ecgschema : ecgListResponse.getEcgschema()) {

                            data.add(ecgschema);
                            if(data==null) {
                                recyclerView.setVisibility(View.GONE);
                                textViewNoData.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                recyclerView.setVisibility(View.VISIBLE);
                                textViewNoData.setVisibility(View.GONE);
                            }

                            adapter.notifyDataSetChanged();
                        }

                    }
                    else
                    {
                        recyclerView.setVisibility(View.GONE);
                        textViewNoData.setVisibility(View.VISIBLE);
                    }
                }else{
                    closeWaitDialog();
                }
            }

            @Override
            public void onFailure(Call<ECGListResponse> call, Throwable t) {
                closeWaitDialog();
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
