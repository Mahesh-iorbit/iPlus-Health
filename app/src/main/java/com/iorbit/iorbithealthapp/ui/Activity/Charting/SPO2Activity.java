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
import com.iorbit.iorbithealthapp.Models.BodyTempAndSPO2Model;
import com.iorbit.iorbithealthapp.Models.DisplayMeasurements;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.R;
import com.iorbit.iorbithealthapp.Utils.Constants;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SPO2Activity extends AppCompatActivity implements OnChartGestureListener {
    private static RecyclerView.Adapter adapter;
    private static RecyclerView recyclerView;
    private static ArrayList<BodyTempAndSPO2Model> data;
    private static ArrayList<Integer> removedItems;
    TickSeekBar s;
    String TAG = "seekbar";
    ArrayList<Entry> bp;
    int duration = 1;
    AlertDialog userDialog = null;
    private LineChart mChart;
    private String patientId;
    private ArrayList<String> xAx;
    private ProgressDialog waitDialog;
    private String URL = Constants.API_URL + "getSpo2";
    private RecyclerView.LayoutManager layoutManager;
    private String dirpath;
    private Menu menu;
    private TextView dateTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spo2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((TextView) findViewById(R.id.value)).setText("Percentage(%)");
        s = findViewById(R.id.listener);
        mChart = (LineChart) findViewById(R.id.linechart);
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

                    Date x = new Date();
                    Calendar cl = Calendar.getInstance();
                    cl.setTime(x);
                    cl.add(Calendar.DATE, 1);
                    SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd");
                    String dateStr = mdformat.format(cl.getTime());
                    data.clear();
                    adapter.notifyDataSetChanged();
                    getMeasurementFromCloud();
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


        // checkId2(mApplication.getP().getSsid(),dateStr2,dateStr);
        getMeasurementFromCloud();
        Log.e("Date new = ", dateStr);
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        data = new ArrayList<BodyTempAndSPO2Model>();

        removedItems = new ArrayList<Integer>();

        adapter = new CustomAdapter(data, "SPO2", " %");
        recyclerView.setAdapter(adapter);

    }
    private void getData(DisplayMeasurements obj) {
        setChartValues(obj);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        if (bp.size() >= 2) {
            LineDataSet bpChartSet = new LineDataSet(bp, "SPO2");
            bpChartSet.setColor(Color.BLUE);
            bpChartSet.setCircleColor(Color.BLUE);
            dataSets.add(bpChartSet);
        }


        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
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

//    private void setChartValues(JSONObject obj) {
//        bp = new ArrayList<>();
//        try {
//            JSONArray visits = obj.getJSONArray("measure");
//            Log.d(TAG, "setChartValues: "+visits);
//            if (visits.length() == 0) {
//                Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_LONG).show();
//            } else {
//                for (int i = 0; i < visits.length(); i++) {
//                    JSONObject visitObj = visits.getJSONObject(i);
//                    String visit_time = visitObj.getString("measuredatetime");
//                    String oxygenLevel = visitObj.getString("readingValues");
//                    if (!oxygenLevel.equalsIgnoreCase("")) {
//                        bp.add(new Entry(i, Float.parseFloat(oxygenLevel)));
//                        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
//                        String strDate = dateFormat.format(new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy").parse(visit_time));
//                        data.add(new BodyTempAndSPO2Model(oxygenLevel, visit_time.replace("UTC", ""), "SPO2", R.drawable.spo2sym, ""));
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    DateFormat dateFormat;
//                    if(duration==1) {
//                        dateTextView.setText("Time");
//                        dateFormat = new SimpleDateFormat("HH:mm:ss");
//
//                    }
//                    else {
//                        dateTextView.setText("Date");
//                        dateFormat = new SimpleDateFormat("dd MMM");
//                    }
//                    String strDate = dateFormat.format(new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy").parse(visit_time));
//                    xAx.add(strDate.replace("UTC", "-05:30"));
//                }
//            }
//        } catch (Exception e)
//        {if (duration==1)
//            dateTextView.setText("Time");
//            e.printStackTrace();
//        }
//    }



    private void setChartValues(DisplayMeasurements obj) {
        bp = new ArrayList<>();
        try {
            if (obj.getMeasure().size() == 0) {
                Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_LONG).show();
            } else {
                for (int i = 0; i < obj.getMeasure().size(); i++) {
                    String visit_time = obj.getMeasure().get(i).getMeasuredatetime();
                    String oxygenLevel = obj.getMeasure().get(i).getReadingValues();
                    if (!oxygenLevel.equalsIgnoreCase("")) {
                        bp.add(new Entry(i, Float.parseFloat(oxygenLevel)));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        Date date = dateFormat.parse(visit_time);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.HOUR, -5);
                        calendar.add(Calendar.MINUTE, -30);
                        date = calendar.getTime();
                        DateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                        displayFormat.setTimeZone(TimeZone.getDefault());
                        String strDate = displayFormat.format(date);
                        data.add(new BodyTempAndSPO2Model(oxygenLevel, strDate, "SPO2", R.drawable.spo2sym, ""));
                        adapter.notifyDataSetChanged();
                    }

                    DateFormat dateFormat;
                    if(duration==1) {
                        dateTextView.setText("Time");
                        dateFormat = new SimpleDateFormat("HH:mm:ss");
                    }
                    else {
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
            if (duration==1) {
                dateTextView.setText("Time");
            }
            e.printStackTrace();

        }
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
        leftAxis.setDrawLimitLinesBehindData(true);

        LimitLine ll2 = new LimitLine(95f, "");
        ll2.setLineWidth(0.1f);
        ll2.setLineColor(Color.RED);
        ll2.enableDashedLine(10f, 0f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);


        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.addLimitLine(ll2);

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

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

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
        Call<DisplayMeasurements> call = retrofitClient.create(ServiceApi.class).getSPO2Measure(new SharedPreference(SPO2Activity.this).getCurrentPAtient().getSsid(),duration);
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
                PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/SPO2_" + new SharedPreference(getApplicationContext()).getCurrentPAtient().getFirstName() +"_"+Utils.getDateTime() + ".pdf")); //  Change pdf's name.
                document.open();


                Paragraph p1 = new Paragraph("Name : "+new SharedPreference(getApplicationContext()).getCurrentPAtient().getFirstName().toUpperCase()+ "       Date : "+Utils.getDateTime()+"\n Parameter : SPO2"+"\n\n");
                Font paraFont= new Font();
                p1.setAlignment(Paragraph.ALIGN_CENTER);
                p1.setFont(paraFont);
                document.add(p1);

                float[] columnWidths = {2, 6, 3,6};
                PdfPTable table = new PdfPTable(columnWidths);
                PdfPCell slNoCell = new PdfPCell();
                slNoCell.setPhrase(new Phrase(" Sl.No"));
                slNoCell.setVerticalAlignment(Element.ALIGN_CENTER);
                slNoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                PdfPCell dateCell = new PdfPCell(new Phrase(" Date   "));
                dateCell.setVerticalAlignment(Element.ALIGN_CENTER);
                dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                PdfPCell valueCell = new PdfPCell(new Phrase("Value (%) "));
                valueCell.setVerticalAlignment(Element.ALIGN_CENTER);
                valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                PdfPCell conditionCell = new PdfPCell(new Phrase(" Condition      "));
                conditionCell.setVerticalAlignment(Element.ALIGN_CENTER);
                conditionCell.setHorizontalAlignment(Element.ALIGN_CENTER);


                table.addCell(slNoCell);
                table.addCell(dateCell);
                table.addCell(valueCell);
                table.addCell(conditionCell);

                //   table.addCell(new PdfPCell(new Phrase("Cell 4")));
                table.completeRow();
                DateFormat dateFormat;
                for (int i = 0; i < data.size(); i++) {
                    PdfPCell slNoCellValue = new PdfPCell(new Phrase("" + (i + 1)));
                    slNoCellValue.setVerticalAlignment(Element.ALIGN_CENTER);
                    slNoCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(slNoCellValue);

                    dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                    String strDate = dateFormat.format(new SimpleDateFormat("EE MMM dd HH:mm:ss yyyy").parse(data.get(i).getDate()));
                    PdfPCell dateCellValue = new PdfPCell(new Phrase(strDate));
                    dateCellValue.setVerticalAlignment(Element.ALIGN_CENTER);
                    dateCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(dateCellValue);

                    PdfPCell valueCellValue = new PdfPCell(new Phrase("" + data.get(i).value));
                    valueCellValue.setVerticalAlignment(Element.ALIGN_CENTER);
                    valueCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(valueCellValue);

                    PdfPCell tyeCellValue = new PdfPCell(new Phrase(data.get(i).getType()));
                    valueCellValue.setVerticalAlignment(Element.ALIGN_CENTER);
                    valueCellValue.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(tyeCellValue);

                    //   table.addCell(new PdfPCell(new Phrase("Cell 4")));
                    table.completeRow();
                }


                document.add(table);

                document.close();
                Toast.makeText(getApplicationContext(), "PDF Saved into " + dirpath + "/SPO2_" + new SharedPreference(getApplicationContext()).getCurrentPAtient().getFirstName() +"_"+ Utils.getDateTime() + ".pdf", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}