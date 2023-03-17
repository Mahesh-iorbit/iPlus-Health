package com.iorbit.iorbithealthapp.ui.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.R;
import com.iorbit.iorbithealthapp.ui.Activity.AddPatientActivity;
import com.iorbit.iorbithealthapp.ui.Activity.Charting.BGActivity;
import com.iorbit.iorbithealthapp.ui.Activity.Charting.BPActivity;
import com.iorbit.iorbithealthapp.ui.Activity.Charting.BPMActivity;
import com.iorbit.iorbithealthapp.ui.Activity.Charting.BodyTempActivity;
import com.iorbit.iorbithealthapp.ui.Activity.Charting.ECGListActivity;
import com.iorbit.iorbithealthapp.ui.Activity.Charting.ReportActivity;
import com.iorbit.iorbithealthapp.ui.Activity.Charting.SPO2Activity;
import com.iorbit.iorbithealthapp.ui.Activity.PatientSearchActivity;
import java.util.Calendar;
import java.util.Date;



public class DashboardFragment extends Fragment implements View.OnClickListener{

    BottomNavigationView navView;
    TextView BgCount, BpCount, EcgCount, Spo2Count, TempCount;
    LinearLayout EcgLayout, BpLayout, BgLayout, oxygenLayout, tempLayout, bpmLayout,data_sysnc_layout;
    private LinearLayout healthReportLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        navView = v.findViewById(R.id.bottom_navigation);
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        navView.getMenu().getItem(0).setCheckable(false);
        BgCount = (TextView) v.findViewById(R.id.BG_count);
        BpCount = (TextView) v.findViewById(R.id.BP_count);
        EcgCount = (TextView) v.findViewById(R.id.ECG_count);
        Spo2Count = (TextView) v.findViewById(R.id.SPO2_count);
        TempCount = (TextView) v.findViewById(R.id.Temp_count);
        EcgLayout = (LinearLayout) v.findViewById(R.id.ecg_layout);
        BpLayout = (LinearLayout) v.findViewById(R.id.bp_layout);
        BgLayout = (LinearLayout) v.findViewById(R.id.bg_layout);
        oxygenLayout = (LinearLayout) v.findViewById(R.id.oxygen_layout);
        bpmLayout = (LinearLayout) v.findViewById(R.id.bpm_layout);
        data_sysnc_layout = (LinearLayout) v.findViewById(R.id.data_sync_layout);
        tempLayout = (LinearLayout) v.findViewById(R.id.temp_layout);
        healthReportLayout = (LinearLayout) v.findViewById(R.id.health_report_layout);
        EcgLayout.setOnClickListener(this);
        BpLayout.setOnClickListener(this);
        BgLayout.setOnClickListener(this);
        oxygenLayout.setOnClickListener(this);
        data_sysnc_layout.setOnClickListener(this);
        healthReportLayout.setOnClickListener(this);
        tempLayout.setOnClickListener(this);
        bpmLayout.setOnClickListener(this);


        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        dt = c.getTime();
        Date x = new Date();
        Calendar cl = Calendar.getInstance();
        cl.setTime(x);
        cl.add(Calendar.DATE, 1);
        return v;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            =new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search_patient:
                    startActivity(new Intent(getActivity(), PatientSearchActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return true;
                case R.id.navigation_addpatient:
                    startActivity(new Intent(getActivity(), AddPatientActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    return true;

            }
            return false;
        }
    };


    @Override
    public void onClick(View v) {
        Intent intent;
        if (new SharedPreference(getContext()).getCurrentPAtient() != null) {
            if (new SharedPreference(getContext()).getCurrentPAtient().getSsid().equalsIgnoreCase(""))
                Toast.makeText(getActivity(), "Add a Member", Toast.LENGTH_SHORT).show();
            else {
                switch (v.getId()) {
                    case R.id.ecg_layout:
                        intent = new Intent(getContext(),ECGListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.bp_layout:
                        intent = new Intent(getContext(),BPActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        break;
                    case R.id.bg_layout:
                        intent = new Intent(getContext(), BGActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("title", "Blood Glucose");
                        startActivity(intent);
                        break;
                    case R.id.oxygen_layout:
                        intent = new Intent(getContext(),SPO2Activity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("title", "SPO2");
                        startActivity(intent);
                        break;
                    case R.id.bpm_layout:
                        intent = new Intent(getContext(),BPMActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("title", "BPM");
                        startActivity(intent);
                        break;
                    case R.id.temp_layout:
                        intent = new Intent(getContext(),BodyTempActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("title", "Temperature");
                        try {
                            //intent.putExtra("user", user.toString().trim());
                        } catch (Exception e) {
                        }
                        startActivity(intent);
                        break;
                    case R.id.health_report_layout:
                        intent = new Intent(getContext(), ReportActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        // getAllMeasurementsFromCloud();
                        break;
                    case R.id.data_sync_layout:
                        break;

                }
            }
        }
    }
}