package com.iorbit.iorbithealthapp.ui.Activity.Charting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.iorbit.iorbithealthapp.Adapters.CustomAdapter;
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.Helpers.Utils.Utils;
import com.iorbit.iorbithealthapp.Helpers.Utils.VerticalTextView;
import com.iorbit.iorbithealthapp.Models.BodyTempAndSPO2Model;
import com.iorbit.iorbithealthapp.Models.DisplayMeasurements;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BGActivity extends AppCompatActivity implements OnChartGestureListener {
    private static RecyclerView.Adapter adapter;
    private static RecyclerView recyclerView;
    public static ArrayList<BodyTempAndSPO2Model> data;
    private static ArrayList<Integer> removedItems;
    private static String dirpath;
    TickSeekBar s;
    String TAG = "seekbar";
    ArrayList<Entry> bgR;
    ArrayList<Entry> bgF;
    ArrayList<Entry> bgP;
    ArrayList<Entry> bg;
    AlertDialog userDialog = null;
    int duration = 1;
    private LineChart mChart;
    private String patientId;
    private ArrayList<String> xAx;
    private ProgressDialog waitDialog;
    private RecyclerView.LayoutManager layoutManager;
    LinearLayout container;
    //private String dirpath;
    private Menu menu;
    private TextView dateTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bgactivity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mChart = (LineChart) findViewById(R.id.linechart);
        ((VerticalTextView) findViewById(R.id.value)).setText("mg/dl");
        s = findViewById(R.id.listener);
        mChart = (LineChart) findViewById(R.id.linechart);
        //container=(LinearLayout)findViewById(R.id.container) ;
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
        dateTextView = (TextView) findViewById(R.id.textView2);
        Date x = new Date();
        Calendar cl = Calendar.getInstance();
        cl.setTime(x);
        cl.add(Calendar.DATE, 1);
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = mdformat.format(cl.getTime());

        cl.add(Calendar.DATE, -1);
        String dateStr2 = mdformat.format(cl.getTime());

        getMeasurementFromCloud();
        // checkId2(mApplication.getP().getSsid(),dateStr2,dateStr);
        Log.e("Date new = ", dateStr);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<BodyTempAndSPO2Model>();

        removedItems = new ArrayList<Integer>();

        adapter = new CustomAdapter(data, "Blood Glucose", " mg/dl");
        recyclerView.setAdapter(adapter);
    }

    private void getData(DisplayMeasurements obj) {
        setChartValues(obj);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        // ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
//        if (bgF.size() >= 2) {
//            LineDataSet bpChartSet = new LineDataSet(bgF, "Fasting Blood Glucose");
//            bpChartSet.setColor(Color.BLUE);
//            bpChartSet.setCircleColor(Color.BLUE);
//            dataSets.add(bpChartSet);
//        }
//
//        if (bgP.size() >= 2) {
//            LineDataSet bpChartSet2 = new LineDataSet(bgP, "Postprandial Blood Glucose");
//            bpChartSet2.setColor(Color.DKGRAY);
//            bpChartSet2.setCircleColor(Color.DKGRAY);
//            dataSets.add(bpChartSet2);
//        }
//
//        if (bgR.size() >= 2) {
//            LineDataSet bpChartSet2 = new LineDataSet(bgR, "Random Blood Glucose");
//            bpChartSet2.setColor(Color.CYAN);
//            bpChartSet2.setCircleColor(Color.CYAN);
//            dataSets.add(bpChartSet2);
//        }
        if (bg.size() >= 2) {
            LineDataSet bpChartSet = new LineDataSet(bg, "Blood Glucose");
            bpChartSet.setColor(Color.BLUE);
            bpChartSet.setCircleColor(Color.BLUE);
            dataSets.add(bpChartSet);
            Log.d(TAG, "getData: glucose "+bg);
        }


//
//        XAxis xAxis = mChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                int index = (int) value;
//                Log.d("BG INDEX", "index " + index);
//
//                try {
//                    return xAx.get(index);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return "";
//            }
//        });
//        xAxis.setGranularityEnabled(true);
//        LineData data = new LineData(dataSets);
// XAxis xAxis = mChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                int index = (int) value;
//                if (index < xAx.size()) {
//
//                    return xAx.get(index);
//                } else {
//                    return "";
//                }
//            }
//        });
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        Log.d(TAG, "getData: 123455");
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                if (index < xAx.size()) {

                    return xAx.get(index);
                } else {
                    return "";
                }
            }
        });


        xAxis.setGranularityEnabled(true);
        LineData data = new LineData(dataSets);
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf((int) value);
            }
        });
        if (data.getDataSets().size() > 0)
            mChart.setData(data);
    }

    private void setChartValues(DisplayMeasurements obj) {
//        bgR = new ArrayList<>();
//
//        bgF = new ArrayList<>();
//
//        bgP = new ArrayList<>();

        bg = new ArrayList<>();
        try {
            Log.e("BG", obj.toString());
            if (obj.getMeasure().size() == 0) {
                Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_LONG).show();
            } else {
                String visit_time = null;
                for (int i = 0; i < obj.getMeasure().size(); i++) {
                    visit_time = obj.getMeasure().get(i).getMeasuredatetime();
                    String bloodGlucose = obj.getMeasure().get(i).getReadingValues();
                    if (!bloodGlucose.equalsIgnoreCase("")) {
                        bg.add(new Entry(i, Float.parseFloat(bloodGlucose)));
                        SimpleDateFormat format = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy");
                        format.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date date = format.parse(visit_time);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.HOUR, -5);
                        calendar.add(Calendar.MINUTE, -30);
                        date = calendar.getTime();
                        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                        String strDate = dateFormat.format(date);
                        data.add(new BodyTempAndSPO2Model(bloodGlucose, strDate, "BG", R.drawable.bgsym, "19"));
                        adapter.notifyDataSetChanged();
                    }

                    DateFormat dateFormat;
                    if (duration == 1) {
                        dateTextView.setText("Time");
                        dateFormat = new SimpleDateFormat("HH:mm:ss");
                    } else {
                        dateTextView.setText("Date");
                        dateFormat = new SimpleDateFormat("dd MMM");
                    }
                    SimpleDateFormat inputFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy");
                    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = inputFormat.parse(visit_time);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.HOUR, -5);
                    calendar.add(Calendar.MINUTE, -30);
                    date = calendar.getTime();
                    DateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                    displayFormat.setTimeZone(TimeZone.getDefault());
                    String strDate = dateFormat.format(date);
                    xAx.add(strDate.replace("UTC", "-05:30"));
                }
            }
        } catch (Exception e) {
            if (duration == 1)
                dateTextView.setText("Time");
            e.printStackTrace();
        }

        //String gloodGlucose = visitObj.getString("readingValues");
        //visit_time = null;
//                    String gType = visitObj.getString("type");
//                    int tType = visitObj.getInt("TestType");
//                    if (!gloodGlucose.equalsIgnoreCase("")) {
//                        if (tType != -1) {
//                            if (Integer.parseInt(gType) == 17) {
//                                bgF.add(new Entry(i, Float.parseFloat(gloodGlucose)));
//                            } else if (Integer.parseInt(gType) == 18) {
//                                bgP.add(new Entry(i, Float.parseFloat(gloodGlucose)));
//                            } else if (Integer.parseInt(gType) == 19) {
//                                bgR.add(new Entry(i, Float.parseFloat(gloodGlucose)));
//                            }
//                            data.add(new BodyTempAndSPO2Model(gloodGlucose, visit_time, "BG", R.drawable.bgsym, ""));
//                            adapter.notifyDataSetChanged();
//                            DateFormat dateFormat = new SimpleDateFormat("dd");
//                            String strDate = dateFormat.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(visit_time));
//                            xAx.add(strDate);
//                        }
//
//                    }
//
//
//                }
//                container.addView(mChart);
//            }
//            } else {
//                mChart.setNoDataText("No chart data available");
//            }
//        } catch (Exception e) {
//            //   Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }

//                    String gType = visitObj.getString("");
//                    int tType = visitObj.getInt("TestType");
//                  //  if (visitObj.getString("paramName").equalsIgnoreCase("BG")) {
//                    if (!gloodGlucose.equalsIgnoreCase("BG")) {
//                        if (tType != -1) {
//                            if (Integer.parseInt(gType) == 17) {
//                                bgF.add(new Entry(i, Float.parseFloat(gloodGlucose)));
//                            } else if (Integer.parseInt(gType) == 18) {
//                                bgP.add(new Entry(i, Float.parseFloat(gloodGlucose)));
//                            } else if (Integer.parseInt(gType) == 19) {
//                                bgR.add(new Entry(i, Float.parseFloat(gloodGlucose)));
//                            }
//                            if (visitObj.getString("readingValues").equalsIgnoreCase("BG")) {
////                                visit_time = visitObj.getString("measuredatetime");
////                                String bgF_ = visitObj.getString("bgfastingvalue");
////                                String bgP_ = visitObj.getString("bgpostprandialvalue");
////                                String bgR_ = visitObj.getString("bgrandomvalue");
//                                Log.d(TAG, "setChartValues: " + visit_time);
//                                Log.d(TAG, "sys: " + bgF);
////                                Log.d(TAG, "dia: " + bgP);
////                                if (!bgF_.equalsIgnoreCase("")) {
////                                    bgF.add(new Entry(i, Float.parseFloat(bgF_)));
////                                    bgP.add(new Entry(i, Float.parseFloat(bgF_)));
////                                    bgR.add(new Entry(i, Float.parseFloat(bgR_)));
//
//
//
//
//                                    String correctedVisitTime;
//
//
//
//                                    DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
//                                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//                                    Calendar calendar = Calendar.getInstance();
//                                    calendar.setTime(dateFormat.parse(visit_time));
//                                    calendar.add(Calendar.HOUR, -5);
//                                    calendar.add(Calendar.MINUTE, -30);
//                                    Log.d(TAG, "dateformat: " + dateFormat.format(calendar.getTime()));
//                                    SimpleDateFormat correctedFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
//                                    correctedVisitTime = correctedFormat.format(calendar.getTime());
//                                    data.add(new BodyTempAndSPO2Model(gloodGlucose, correctedVisitTime, "BG", R.drawable.bgsym, ""));
//                                    adapter.notifyDataSetChanged();
//                                }
//                            }
//
//                            DateFormat dateFormat;
//                            if (duration == 1) {
//                                dateFormat = new SimpleDateFormat("HH:mm:ss");
//                                dateTextView.setText("Time");
//                            } else {
//                                dateFormat = new SimpleDateFormat("dd MMM");
//                                dateTextView.setText("Date");
//                            }
////                Calendar calendar = Calendar.getInstance();
////                calendar.setTime(dateFormat.parse(visit_time));
////                calendar.add(Calendar.HOUR, -5);
////                calendar.add(Calendar.MINUTE, -30);
////                String str = dateFormat.format(calendar.getTime());
////                xAx.add(str);
//                            SimpleDateFormat inputFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy");
//                            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//                            Date date = inputFormat.parse(visit_time);
//                            Calendar calendar = Calendar.getInstance();
//                            calendar.setTime(date);
//                            calendar.add(Calendar.HOUR, -5);
//                            calendar.add(Calendar.MINUTE, -30);
//                            date = calendar.getTime();
//                            DateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
//                            displayFormat.setTimeZone(TimeZone.getDefault());
//                            String strDate = dateFormat.format(date);
//                            Log.d(TAG, "xax date: " + date);
//                            xAx.add(strDate.replace("UTC", "-05:30"));
//                        }
//                    }
//                } catch (Exception e) {
//                    if (duration == 1)
//                        dateTextView.setText("Time");
//                    e.printStackTrace();
//            closeWaitDialog();
//                }
    }

    private void setUpChart(DisplayMeasurements obj) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        xAx = new ArrayList<String>();


        mChart.setOnChartGestureListener(this);
        mChart.setDrawGridBackground(false);

        // add data
        getData(obj);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        // no description text
        Description d = new Description();
        d.setText("");
        mChart.setDescription(d);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines


        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);


        // limit lines are drawn behind data (and not on top)
        //leftAxis.setDrawLimitLinesBehindData(true);
        mChart.getAxisRight().setEnabled(false);

        mChart.animateX(1000, Easing.EasingOption.EaseInOutQuart);

        LimitLine ll2 = new LimitLine(95f, "BG");
        ll2.setLineWidth(0.1f);
        ll2.setLineColor(Color.RED);
        ll2.enableDashedLine(10f, 0f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);


        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.addLimitLine(ll2);

        // mChart.getAxisRight().setEnabled(false);
        mChart.getAxisLeft().setGranularityEnabled(true);
        mChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        mChart.getAxisRight().setEnabled(false);
        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        //    mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        //  dont forget to refresh the drawing
        mChart.invalidate();
        closeWaitDialog();
    }

    private String getcurrentDate(Calendar calendar) {
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = mdformat.format(calendar.getTime());
        return dateStr;
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }



    private void closeWaitDialog() {
        Utils.closeWaitDialog();
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



    public void getMeasurementFromCloud() {
        RetrofitClient retrofit = new RetrofitClient();
        Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
        if (retrofitClient == null) {
            return;
        }
        Call<DisplayMeasurements> call = retrofitClient.create(ServiceApi.class).getbgMeasure(new SharedPreference(BGActivity.this).getCurrentPAtient().getSsid(),duration);
        call.enqueue(new Callback<DisplayMeasurements>() {
            @Override
            public void onResponse(Call<DisplayMeasurements> call, Response<DisplayMeasurements> response) {
                if(response.isSuccessful()){
                    mChart.clear();
                    mChart.invalidate();
                    try {
                        DisplayMeasurements displayMeasurements = response.body();
                        Log.d(TAG, "onResponse:BP "+response);
                        if (displayMeasurements.getStatusDetails().getMessage().equalsIgnoreCase("Success")) {
                            setUpChart(displayMeasurements);

                        } else if (displayMeasurements.getStatusDetails().getMessage().equalsIgnoreCase("Fail")) {
                            Toast.makeText(getApplicationContext(), "No Records Found.", Toast.LENGTH_LONG).show();
                            closeWaitDialog();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        showDialogMessage("Error", e.getMessage());
                        closeWaitDialog();
                    }
                }else{
                    closeWaitDialog();
                }
            }

            @Override
            public void onFailure(Call<DisplayMeasurements> call, Throwable t) {
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
                    if (data.size() > 0)
                        imageToPDF();
                    else
                        Toast.makeText(getApplicationContext(), "No Data Available", Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void imageToPDF() throws FileNotFoundException {
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
                PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/BG_" + new SharedPreference(BGActivity.this).getCurrentPAtient().getFirstName() + ".pdf")); //  Change pdf's name.
                document.open();

                PdfPTable table = new PdfPTable(3);

                table.addCell(new PdfPCell(new Phrase("Value    " )));
                table.addCell(new PdfPCell(new Phrase("Unit  " )));
                table.addCell(new PdfPCell(new Phrase(" Date  " )));
                //   table.addCell(new PdfPCell(new Phrase("Cell 4")));
                table.completeRow();

                for(int i=0;i<data.size();i++)
                {
                    table.addCell(new PdfPCell(new Phrase(""+data.get(i).value+"/"+data.get(i).value2 )));
                    table.addCell(new PdfPCell(new Phrase()));
                    table.addCell(new PdfPCell(new Phrase(" Date  " )));
                    //   table.addCell(new PdfPCell(new Phrase("Cell 4")));
                    table.completeRow();
                }
                document.add(table);

                document.close();
                Toast.makeText(getApplicationContext(), "PDF Generated successfully!..", Toast.LENGTH_SHORT).show();
            }  } catch(Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}