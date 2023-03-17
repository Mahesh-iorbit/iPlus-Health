package com.iorbit.iorbithealthapp.ui.Activity.Charting;

import static com.iorbit.iorbithealthapp.Helpers.Utils.Utils.closeWaitDialog;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.Helpers.Utils.Utils;
import com.iorbit.iorbithealthapp.Models.MeasurementResponse;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.R;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.warkiz.tickseekbar.OnSeekChangeListener;
import com.warkiz.tickseekbar.SeekParams;
import com.warkiz.tickseekbar.TickSeekBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReportActivity extends AppCompatActivity {
    ProgressDialog mProgressBar;
    String TAG = "seekbar";
    TickSeekBar s;
    int duration = 1;
    TextView NoDataAvailableTextview;
    LinearLayout linearLayout;
    private TableLayout mTableLayout;
    private List<MeasurementResponse.Measurement> measurementList = new ArrayList<>();
    private String dirpath;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NoDataAvailableTextview = (TextView) findViewById(R.id.no_data_textview);
        linearLayout = (LinearLayout) findViewById(R.id.ll_country);
        mProgressBar = new ProgressDialog(this);
        mTableLayout = (TableLayout) findViewById(R.id.measurement_table);
        mTableLayout.setStretchAllColumns(true);
        getAllMeasurementsFromCloud();
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
                            duration = 1;
                            dt = c.getTime();
                            Log.e(TAG, dt.toString());
                            break;
                        case "1 Week":
                            duration = 2;
                            c.add(Calendar.DATE, -7);
                            dt = c.getTime();
                            Log.e(TAG, dt.toString());
                            break;
                        case "3 Months":
                            duration = 3;
                            c.add(Calendar.MONTH, -3);
                            dt = c.getTime();
                            Log.e(TAG, dt.toString());
                            break;
                        case "6 Months":
                            duration = 4;
                            c.add(Calendar.MONTH, -6);
                            dt = c.getTime();
                            Log.e(TAG, dt.toString());
                            break;
                        case "1 Year":
                            duration = 5;
                            c.add(Calendar.YEAR, -1);
                            dt = c.getTime();
                            Log.e(TAG, dt.toString());
                            break;

                    }
                }
                getAllMeasurementsFromCloud();

            }
        });
    }

    void fillCountryTable() throws ParseException {
        boolean isFirst = true;
        mTableLayout.removeAllViews();
        TableRow row;
        TextView serialNum, t1, t2, t3, t4;
        //Converting to dip unit
        int dip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 1, getResources().getDisplayMetrics());

        ArrayList<MeasurementResponse.Measurement> bpList = new ArrayList<>();
        ArrayList<MeasurementResponse.Measurement> ecgList = new ArrayList<>();
        ArrayList<MeasurementResponse.Measurement> tempList = new ArrayList<>();
        ArrayList<MeasurementResponse.Measurement> spo2List = new ArrayList<>();
        ArrayList<ArrayList<MeasurementResponse.Measurement>> allList = new ArrayList<>();

        for (int current = 0; current < measurementList.size(); current++) {
            if (measurementList.get(current).getParamName().equalsIgnoreCase("oxygen_level"))

                spo2List.add(measurementList.get(current));
            else if (measurementList.get(current).getParamName().equalsIgnoreCase("temperature"))
                tempList.add(measurementList.get(current));
            else if (measurementList.get(current).getParamName().equalsIgnoreCase("ECG"))
                ecgList.add(measurementList.get(current));
            else if (measurementList.get(current).getParamName().equalsIgnoreCase("BP"))
                bpList.add(measurementList.get(current));


        }
        allList.add(bpList);
        allList.add(tempList);
        allList.add(ecgList);
        allList.add(spo2List);
        DateFormat dateFormat;
        int count = 0;
        for (int current = 0; current < 4; current++) {
            for (MeasurementResponse.Measurement measurement : allList.get(current)) {
                row = new TableRow(ReportActivity.this);

                serialNum = new TextView(ReportActivity.this);
                serialNum.setTextColor(getResources().getColor(R.color.BLACK));
                t1 = new TextView(ReportActivity.this);
                t1.setTextColor(getResources().getColor(R.color.BLACK));
                t2 = new TextView(ReportActivity.this);
                t2.setTextColor(getResources().getColor(R.color.BLACK));
                t1.setGravity(Gravity.CENTER);
                t2.setGravity(Gravity.CENTER);
                serialNum.setGravity(Gravity.CENTER);


                if (isFirst) {

                    t2.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20);

                    t1.setAllCaps(true);
                    t2.setAllCaps(true);
                    serialNum.setAllCaps(true);
                    t1.setPadding(0, 0, 0, 5 * dip);
                    t2.setPadding(0, 0, 0, 5 * dip);
                    serialNum.setPadding(0, 0, 0, 5);
                    serialNum.setText("Sl.No");
                    t1.setText("Parameter");

                    t2.setText("Date");
                    t1.setTextSize(11f);
                    t2.setTextSize(11f);
                    serialNum.setTextSize(11f);
                    t1.setTypeface(null, Typeface.BOLD);
                    t2.setTypeface(null, Typeface.BOLD);
                    serialNum.setTypeface(null, Typeface.BOLD);
                } else {
                    t1.setTypeface(null, Typeface.NORMAL);
                    t2.setTypeface(null, Typeface.NORMAL);
                    serialNum.setTypeface(null, Typeface.NORMAL);
                    t1.setTextSize(11f);
                    t2.setTextSize(11f);
                    serialNum.setTextSize(11f);
                    serialNum.setText(" " + count);
                    if (measurement.getParamName().equalsIgnoreCase("oxygen_level"))
                        t1.setText("SPO2");
                    else if (measurement.getParamName().equalsIgnoreCase("temperature"))
                        t1.setText("Body Temperature");
                    else if (measurement.getParamName().equalsIgnoreCase("ECG"))
                        t1.setText("BPM");

                    else
                        t1.setText(measurement.getParamName());

                    dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                    String strDate = dateFormat.format(new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy").parse(measurement.getMeasTimeStamp()));
                    t2.setText(strDate);
                }


                t1.setWidth(65 * dip);
                t2.setWidth(90 * dip);
                t1.setPadding(20 * dip, 5 * dip, 0, 5 * dip);
                t2.setPadding(20 * dip, 5 * dip, 0, 5 * dip);

                t3 = new TextView(ReportActivity.this);
                t3.setTextColor(getResources().getColor(R.color.BLACK));
                t3.setGravity(Gravity.CENTER);
                t4 = new TextView(ReportActivity.this);
                t4.setTextColor(getResources().getColor(R.color.BLACK));
                t4.setGravity(Gravity.CENTER);
                if (isFirst) {
                    t3.setAllCaps(true);
                    t3.setTextSize(11f);
                    t3.setText("Value");

                } else {
                    t3.setTextSize(11f);
                    if (measurement.getParamName().equalsIgnoreCase("oxygen_level"))
                        t3.setText(measurement.getReadingValues() + " %");
                    else if (measurement.getParamName().equalsIgnoreCase("temperature"))
                        t3.setText(measurement.getReadingValues() + (char) 0x00B0 + " F");
                    else if (measurement.getParamName().equalsIgnoreCase("ECG"))
                        t3.setText(measurement.getReadingValues() + " BPM");
                    else if (measurement.getParamName().equalsIgnoreCase("BP"))
                        t3.setText(measurement.getReadingValues() + " mmhg");
                }


                String condition = "";
                String[] parts;
                if (measurement.getParamName().equalsIgnoreCase("BP")) {
                    parts = measurement.getReadingValues().split("/");
                    //    if (parts[0] != null && parts[1] != null)
                    if (parts.length >= 2 && parts[0] != null && parts[1] != null)
                        condition = getType("BP", parts[0], parts[1]);
                    else
                        condition = "Error";
                    if (isFirst) {
                        t4.setTextSize(11f);
                        t4.setAllCaps(true);
                        t4.setText("Condition");
                        t3.setTypeface(null, Typeface.BOLD);
                        t4.setTypeface(null, Typeface.BOLD);

                    } else {
                        t3.setTypeface(null, Typeface.NORMAL);
                        t4.setTypeface(null, Typeface.NORMAL);
                        t4.setTextSize(11f);
                        t4.setText(condition);
                    }
                } else if (!measurement.getParamName().equalsIgnoreCase("ECG") || measurement.getParamName().equalsIgnoreCase("ECG")) {
                    condition = getType(measurement.getParamName(), measurement.getReadingValues(), "");
                    if (isFirst) {
                        t3.setTypeface(null, Typeface.BOLD);
                        t4.setTypeface(null, Typeface.BOLD);
                        t4.setTextSize(11f);
                        t4.setAllCaps(true);
                        t4.setText("Condition");

                    } else {
                        t3.setTypeface(null, Typeface.NORMAL);
                        t4.setTypeface(null, Typeface.NORMAL);
                        t4.setTextSize(11f);
                        t4.setText(condition);
                    }

                }


                t3.setWidth(90 * dip);
                t4.setWidth(90 * dip);
                t3.setPadding(20 * dip, 5 * dip, 0, 8 * dip);

                serialNum.setWidth(20 * dip);
                serialNum.setPadding(20 * dip, 5 * dip, 0, 5 * dip);

                row.addView(serialNum);
                row.addView(t1);
                row.addView(t2);
                row.addView(t3);
                row.addView(t4);

                mTableLayout.addView(row, new TableLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                mTableLayout.invalidate();
                mTableLayout.refreshDrawableState();
                isFirst = false;
                count++;
            }
        }
    }

    public String getType(String type, String value, String value2) {
        Log.d("sdfsdbfhvjhfbhj", "type " + type + " value " + value + " value2 " + value2);
        float v = 0f;
        if (!type.equalsIgnoreCase("FALL")) {

            if (value != null)
                v = Float.parseFloat(value);
            else
                Log.d("fsdfhsdhgf", "dsfdsf " + type);
            if (type.equals("ECG")) {
                if (v < 95)
                    return "Low";
                else if (v >= 95 && v <= 114)
                    return "Very light";
                else if (v > 114 && v <= 133)
                    return "Light";
                else if (v > 133 && v <= 152)
                    return "Moderate";
                else if (v > 152 && v <= 171)
                    return "Hard";
                else if (v > 171 && v <= 190)
                    return "Very Hard";

            } else if (type.equalsIgnoreCase("temperature")) {
                if (v <= 95) {
                    return "Hypothermia";
                } else if (v > 95 && v <= 99) {
                    return "Normal";
                } else if (v > 99 && v <= 104) {
                    return "Fever";
                } else if (v > 104) {
                    return "Hyperpyrexia";
                }
            } else if (type.equals("oxygen_level")) {
                if (v < 75) {
                    return "Hypoxemia";
                } else if (v >= 75 && v <= 89) {
                    return "Hypoxemia";
                } else if (v >= 90 && v <= 94) {
                    return "Hypoxemia";
                } else if (v >= 95) {
                    return "Normal";
                }
            } else if (type.equals("BP")) {
                int v2 = Integer.parseInt(value2);

                if (v == 0 || v2 == 0)
                    return "Error_Record";
                else if (v < 120 && v2 <= 80)
                    return "Normal";
                else if (v >= 120 && v <= 129 && v2 <= 80)
                    return "Elevated";
                else if ((v >= 130 && v <= 139) || (v2 >= 80 && v2 <= 89))
                    return "Hypertension State-I";
                else if ((v >= 140 && v < 180) || (v2 >= 90 && v2 < 120))
                    return "Hypertension State-II";
                else if (v >= 180 || v2 >= 120)
                    return "Severe Hypertension";
                else
                    return "Elevated";

            } else if (type.equals("BG")) {
                if (value.equals("19"))
                    return "Random Blood Sugar";
                else if (value.equals("18"))
                    return "Postprandial";
                else if (value.equals("17"))
                    return "Fasting Blood Sugar";

            }
        } else
            return "zgvxfg-dfg-hhdh";
        return "NoneError";
    }

    public void getAllMeasurementsFromCloud() {
        mProgressBar.setMessage("Fetching measurements, please wait.");
        mProgressBar.show();
        RetrofitClient retrofit = new RetrofitClient();
        Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
        if (retrofitClient == null) {
            return;
        }
        Call<MeasurementResponse> call = retrofitClient.create(ServiceApi.class).getReportHistory(new SharedPreference(ReportActivity.this).getCurrentPAtient().getSsid(), duration);
        call.enqueue(new Callback<MeasurementResponse>() {
            @Override
            public void onResponse(Call<MeasurementResponse> call, Response<MeasurementResponse> response) {
                if (response.isSuccessful()) {
                    mProgressBar.hide();
                    MeasurementResponse displayPatients = response.body();
                    measurementList = displayPatients.getMeasurement();
                    if (measurementList != null && measurementList.size() > 0) {

                        linearLayout.setVisibility(View.VISIBLE);
                        NoDataAvailableTextview.setVisibility(View.GONE);
                        try {
                            fillCountryTable();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        closeWaitDialog();
                    } else {
                        linearLayout.setVisibility(View.GONE);

                        NoDataAvailableTextview.setVisibility(View.VISIBLE);
                        NoDataAvailableTextview.setText("No Data Available");
                    }
                }
            }

            @Override
            public void onFailure(Call<MeasurementResponse> call, Throwable t) {
                mProgressBar.hide();
                closeWaitDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.pdf, menu);
        this.menu = menu;

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.pdf:
                try {
                    if (measurementList != null || measurementList.size() > 0)
                        imageToPDF(measurementList);
                    else
                        Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void imageToPDF(List<MeasurementResponse.Measurement> measurement) throws FileNotFoundException {
        if (measurement.size() > 0) {
            try {
                File folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "Jivaah");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {
                    Document document = new Document(PageSize.A4, 20f, 20f, 15f, 10f);
                    dirpath = Environment.getExternalStorageDirectory() + File.separator + "Jivaah";
                    PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/Health_Report" + new SharedPreference(getApplicationContext()).getCurrentPAtient().getFirstName() + "_" + Utils.getDateTime() + ".pdf")); //  Change pdf's name.
                    document.open();

                    Paragraph p1 = new Paragraph("Name : " + new SharedPreference(getApplicationContext()).getCurrentPAtient().getFirstName().toUpperCase() + "       Date : " + Utils.getDateTime() + "\n\n");
                    Font paraFont = new Font();
                    paraFont.setColor(new BaseColor(0, 0, 0, 68));
                    paraFont.setSize(20f);
                    paraFont.setColor(15, 12, 11);
                    paraFont.setStyle(Font.BOLD);
                    p1.setAlignment(Paragraph.ALIGN_CENTER);
                    p1.setFont(paraFont);
                    document.add(p1);

                    PdfPTable table = new PdfPTable(4);
                    table.addCell(new PdfPCell(new Phrase(" Parameter")));
                    table.addCell(new PdfPCell(new Phrase(" Date   ")));
                    table.addCell(new PdfPCell(new Phrase("Value  ")));
                    table.addCell(new PdfPCell(new Phrase("Condition  ")));


                    //   table.addCell(new PdfPCell(new Phrase("Cell 4")));
                    table.completeRow();
                    String condition = "";
                    String[] parts;
                    DateFormat dateFormat;


                    ArrayList<MeasurementResponse.Measurement> bpList = new ArrayList<>();
                    ArrayList<MeasurementResponse.Measurement> ecgList = new ArrayList<>();
                    ArrayList<MeasurementResponse.Measurement> tempList = new ArrayList<>();
                    ArrayList<MeasurementResponse.Measurement> spo2List = new ArrayList<>();
                    ArrayList<ArrayList<MeasurementResponse.Measurement>> allList = new ArrayList<>();

                    for (int current = 0; current < measurement.size(); current++) {

                        if (measurement.get(current).getParamName().equalsIgnoreCase("oxygen_level"))

                            spo2List.add(measurement.get(current));
                        else if (measurement.get(current).getParamName().equalsIgnoreCase("temperature"))
                            tempList.add(measurement.get(current));
                        else if (measurement.get(current).getParamName().equalsIgnoreCase("ECG"))
                            ecgList.add(measurement.get(current));
                        else if (measurement.get(current).getParamName().equalsIgnoreCase("BP"))
                            bpList.add(measurement.get(current));


                    }
                    allList.add(bpList);
                    allList.add(tempList);
                    allList.add(ecgList);
                    allList.add(spo2List);


                    for (int i = 0; i < 4; i++) {

                        for (MeasurementResponse.Measurement measurements : allList.get(i)) {
                            if (measurements.getParamName().equalsIgnoreCase("oxygen_level"))
                                table.addCell(new PdfPCell(new Phrase("SPO2")));
                            else if (measurements.getParamName().equalsIgnoreCase("temperature"))
                                table.addCell(new PdfPCell(new Phrase("Body Temperature")));
                            else if (measurements.getParamName().equalsIgnoreCase("ECG"))
                                table.addCell(new PdfPCell(new Phrase("BPM")));
                            else
                                table.addCell(new PdfPCell(new Phrase(new Phrase(measurements.getParamName()))));


                            dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String strDate = dateFormat.format(new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy").parse(measurements.getMeasTimeStamp()));
                            table.addCell(new PdfPCell(new Phrase(strDate)));

                            if (measurements.getParamName().equalsIgnoreCase("oxygen_level"))
                                table.addCell(new PdfPCell(new Phrase(measurements.getReadingValues() + " %")));

                            else if (measurements.getParamName().equalsIgnoreCase("temperature"))
                                table.addCell(new PdfPCell(new Phrase(measurements.getReadingValues() + (char) 0x00B0 + " F")));

                            else if (measurements.getParamName().equalsIgnoreCase("ECG"))
                                table.addCell(new PdfPCell(new Phrase(measurements.getReadingValues() + " BPM")));

                            else if (measurements.getParamName().equalsIgnoreCase("BP"))
                                table.addCell(new PdfPCell(new Phrase(measurements.getReadingValues() + " mmhg")));


                            if (measurements.getParamName().equalsIgnoreCase("BP")) {
                                parts = measurements.getReadingValues().split("/");
                                condition = getType("BP", parts[0], parts[1]);
                                table.addCell(new PdfPCell(new Phrase(condition)));

                            } else if (!measurements.getParamName().equalsIgnoreCase("ECG") || measurements.getParamName().equalsIgnoreCase("ECG")) {
                                condition = getType(measurements.getParamName(), measurements.getReadingValues(), "");
                                table.addCell(new PdfPCell(new Phrase(condition)));
                            }

                            //   table.addCell(new PdfPCell(new Phrase("Cell 4")));
                            table.completeRow();
                        }
                    }
                    document.add(table);

                    document.close();
                    Toast.makeText(getApplicationContext(), "PDF Saved into " + dirpath + "/Health_Report_" + new SharedPreference(getApplicationContext()).getCurrentPAtient().getFirstName() + "_" + Utils.getDateTime() + ".pdf", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_SHORT).show();
    }
}