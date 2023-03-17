package com.iorbit.iorbithealthapp.ui.Activity.Charting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.LinearLayout;
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
import com.iorbit.iorbithealthapp.Helpers.SessionManager.SharedPreference;
import com.iorbit.iorbithealthapp.Helpers.Utils.Utils;
import com.iorbit.iorbithealthapp.Helpers.Utils.VerticalTextView;
import com.iorbit.iorbithealthapp.Models.BodyTempAndSPO2Model;
import com.iorbit.iorbithealthapp.Models.ECGDetailResponse;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EcgDetailActivity extends AppCompatActivity implements OnChartGestureListener {
    AlertDialog userDialog = null;
    private String measurementId="";
    private ArrayList<String> xAx;
    ArrayList<Entry> bp;
    private static ArrayList<BodyTempAndSPO2Model> data;
    LinearLayout linearLayout;
    VerticalTextView verticalTextView[];
    LineChart chartLine[];
    private int globalIteration;
    Menu menu;
    private String dirpath;
    private PdfPCell cell;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        data = new ArrayList<BodyTempAndSPO2Model>();
        linearLayout=(LinearLayout) findViewById(R.id.container);
        verticalTextView=new VerticalTextView[6];
        chartLine=new LineChart[6];
        Bundle extras = getIntent().getExtras();
        if (extras.containsKey("measurementId"))
        {
            measurementId = extras.getString("measurementId");
        }
        getMeasurementFromCloud();
    }

    public void getMeasurementFromCloud() {

        RetrofitClient retrofit = new RetrofitClient();
        Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
        if (retrofitClient == null) {
            return;
        }
        Call<ECGDetailResponse> call = retrofitClient.create(ServiceApi.class).getEcgMeasureDetail(new SharedPreference(EcgDetailActivity.this).getCurrentPAtient().getSsid(), measurementId);
        call.enqueue(new Callback<ECGDetailResponse>() {
            @Override
            public void onResponse(Call<ECGDetailResponse> call, Response<ECGDetailResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        ECGDetailResponse detailResponse = response.body();
                        if (detailResponse.getStatusDetails().getMessage().equalsIgnoreCase("Success")) {

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        showDialogMessage("Error", e.getMessage());
                        closeWaitDialog();
                    }
                } else {
                    closeWaitDialog();
                }
            }

            @Override
            public void onFailure(Call<ECGDetailResponse> call, Throwable t) {
                closeWaitDialog();
            }
        });
    }


        private void closeWaitDialog () {
            Utils.closeWaitDialog();
        }

        private void showDialogMessage (String title, String body){

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


    private void getData(List<String> obj){
        setChartValues(obj);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        if(bp.size()>=2) {
            int count=globalIteration+1;
            String label="";
            if(count==1)
                label="I";
            else if(count==2)
                label="II";
            else if(count==3)
                label="III";
            else if(count==4)
                label="AVR";
            else if(count==5)
                label="AVL";
            else if(count==6)
                label="AVF";

            LineDataSet bpChartSet = new LineDataSet(bp, label);
            bpChartSet.setColor(Color.BLUE);
            bpChartSet.setCircleColor(Color.BLUE);
            bpChartSet.setDrawCircles(false);
            dataSets.add(bpChartSet);
        }




        XAxis xAxis = chartLine[globalIteration].getXAxis();
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
            chartLine[globalIteration].setData(data);
    }

    private void setChartValues(List<String> obj){
        bp = new ArrayList<>();
        try {
            //  JSONArray visits = obj.getJSONArray("measure");
            if (obj.size() == 0) {
                Toast.makeText(getApplicationContext(),"No Data Found",Toast.LENGTH_LONG).show();
            } else {
                for (int i = 0; i < obj.size(); i++) {
                    //  JSONObject visitObj = ;
                    String visit_time = String.valueOf(i);
                    String oxygenLevel = obj.get(i);
                    Log.d("adfsdgf","parsedva "+Float.parseFloat(oxygenLevel));
                    if(!oxygenLevel.equalsIgnoreCase("")) {
                        bp.add(new Entry(i, Float.parseFloat(oxygenLevel)));
                        data.add(new BodyTempAndSPO2Model(oxygenLevel,visit_time,"SPO2",R.drawable.spo2sym,""));
                        //adapter.notifyDataSetChanged();
                    }

//                    DateFormat dateFormat = new SimpleDateFormat("dd");
//                    String strDate = dateFormat.format(new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy").parse(visit_time));
                    xAx.add(String.valueOf(i));
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setUpChart(List<String> obj) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        xAx = new ArrayList<String>();





        chartLine[globalIteration].setOnChartGestureListener(this);
        chartLine[globalIteration].setDrawGridBackground(false);

        // add data
        getData(obj);

        // get the legend (only possible after setting data)
        Legend l = chartLine[globalIteration].getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        // no description text
        Description d = new Description();
        d.setText("");
        chartLine[globalIteration].setDescription(d);


        // enable touch gestures
        chartLine[globalIteration].setTouchEnabled(true);

        // enable scaling and dragging
        chartLine[globalIteration].setDragEnabled(true);
        chartLine[globalIteration].setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);





        YAxis leftAxis = chartLine[globalIteration].getAxisLeft();
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

        chartLine[globalIteration].getAxisLeft().setGranularityEnabled(true);
        chartLine[globalIteration].getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) Math.floor(value));
            }
        });
        chartLine[globalIteration].getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        chartLine[globalIteration].animateX(2500, Easing.EasingOption.EaseInOutQuart);

        //  dont forget to refresh the drawing
        chartLine[globalIteration].invalidate();
        closeWaitDialog();
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
                    layoutToImage();
                    try {
                        imageToPDF();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void layoutToImage( )
    { Bitmap returnedBitmap = Bitmap.createBitmap(linearLayout.getWidth(), linearLayout.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =linearLayout.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        linearLayout.draw(canvas);

        // Bitmap bm = returnedBitmap;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        returnedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                dirpath =  Environment.getExternalStorageDirectory() + File.separator + "Jivaah";
                String filePath=dirpath + "/ECG_" + new SharedPreference(getApplicationContext()).getCurrentPAtient().getFirstName() +"_"+Utils.getDateTime() + ".pdf";
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                PdfPTable table = new PdfPTable(3);
                table.addCell(new PdfPCell(new Phrase("Name   : " + new SharedPreference(EcgDetailActivity.this).getCurrentPAtient().getFirstName())));
                table.addCell(new PdfPCell(new Phrase("ECG ID : " + measurementId)));
             //   table.addCell(new PdfPCell(new Phrase("ECG Test Date : " + ecgDetailResponse.getMeasure().get(0).getMeasTimeStamp())));
                //   table.addCell(new PdfPCell(new Phrase("Cell 4")));
                table.completeRow();


                document.add(table);
                Image img = Image.getInstance(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
                img.scaleToFit(PageSize.A4.getWidth(), 700f);
                float x = (PageSize.A4.getWidth() - img.getScaledWidth()) / 2;
                float y = (PageSize.A4.getHeight() - img.getScaledHeight()) / 2;
                img.setAbsolutePosition(x, y);
                img.setAlignment(Image.ALIGN_CENTER);

                // img.scaleAbsolute(PageSize.A4.rotate());
                //  img.setAbsolutePosition(0,0);
                document.add(img);
                document.close();
                Toast.makeText(this, "PDF Saved into ...."+filePath, Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


}