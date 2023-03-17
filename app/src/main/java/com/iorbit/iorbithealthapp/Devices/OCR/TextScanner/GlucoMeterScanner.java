package com.iorbit.iorbithealthapp.Devices.OCR.TextScanner;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.ContextThemeWrapper;


import com.google.gson.Gson;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.iorbit.iorbithealthapp.Devices.CommonDataArea;
import com.iorbit.iorbithealthapp.Devices.OCR.camera.AnalyzeResult;
import com.iorbit.iorbithealthapp.Devices.OCR.camera.analyze.Analyzer;
import com.iorbit.iorbithealthapp.Devices.OCR.text.TextCameraScanActivity;
import com.iorbit.iorbithealthapp.Devices.OCR.text.ViewfinderView;
import com.iorbit.iorbithealthapp.Devices.OCR.text.analyze.TextRecognitionAnalyzer;
import com.iorbit.iorbithealthapp.Models.SaveMeasureModel;
import com.iorbit.iorbithealthapp.Models.StatusResponseModel;
import com.iorbit.iorbithealthapp.Network.RetrofitClient;
import com.iorbit.iorbithealthapp.Network.ServiceApi;
import com.iorbit.iorbithealthapp.R;
import com.king.app.dialog.AppDialog;
import com.king.app.dialog.AppDialogConfig;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class GlucoMeterScanner extends TextCameraScanActivity {
        private int glucoseValue;
        private String glucoseUnit;
        private boolean measurementSaved = false;
        private boolean glucoseLevelRecognized = false;
        private final Handler handler = new Handler();
        private AppDialogConfig config = null;
    private ProgressDialog progressDialog;
    private boolean isScanning = false;
    private final boolean stopScanRunnableCanceled = false;
    protected ViewfinderView viewfinderView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler.postDelayed(stopScanRunnable, 30000); // 15 seconds
        isScanning = true;


    }

    private final Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            if (glucoseLevelRecognized) {
                return;
            }
            if (!measurementSaved) {


                showSuccess("Tips:Make sure the background is clear","Timeout-Measure Not Found");

            }
            isScanning = false;
        }
    };

    @Override
    public void onScanResultCallback(AnalyzeResult<Text> result) {
        if (!isScanning) {
            return;
        }
            if (glucoseLevelRecognized) {
                return;
            }

            if (result.getResult().getText().isEmpty()) {
                return;
            }

            if (config == null && !measurementSaved) {
                showResultDialog();
            }
            updateDialogContent(result.getResult().getText());

            if (measurementSaved) {
                return;
            }

            Button btnRescan = config.getView(R.id.btnRescan);
            String recognizedText = result.getResult().getText();
            Log.d("TAG", "onScanResultCallback: "+recognizedText);
         //   Pattern pattern = Pattern.compile("Contour plus ELITE\\s+\\S+\\s+\\S+\\s+([1-9]\\d+\\.?\\d*)");
           // Pattern pattern = Pattern.compile("Contour plus ELITE\\s+\\S+\\s+\\S+\\s+(?!0*(?:[1-2][0-9]|[1-9]))\\d+(\\.\\d+)?");
              Pattern pattern = Pattern.compile(CommonDataArea.SUPPORTED_PATTERN_GlUCOMETER);


            Matcher matcher = pattern.matcher(recognizedText);

            if (matcher.find()) {
                String glucoseValueString = matcher.group(1);
                try {
                    glucoseValue = Integer.parseInt(glucoseValueString);
                } catch (NumberFormatException e) {
                    // Handle the case where the string is not a valid integer
                    Log.e("TAG", "Invalid glucose value: " + glucoseValueString);
                    return;
                }
                glucoseUnit = "mg/dl";
                Log.d("TAG", "Glucose measurement: " + glucoseValue + " " + glucoseUnit);
                showGlucoseMeasurementResult(btnRescan);
                glucoseLevelRecognized = false;
                getCameraScan().setAnalyzeImage(false);
            }
        }

        private void showResultDialog() {
            config = new AppDialogConfig(GlucoMeterScanner.this, R.layout.text_result_dialog);
            config.setOnClickCancel(v -> {
                AppDialog.INSTANCE.dismissDialog();
                handler.removeCallbacksAndMessages(null);
                GlucoMeterScanner.this.finish();

            });
            config.setOnClickConfirm(v -> {
              saveMeasurement(glucoseValue);
                measurementSaved = true;
            });
            AppDialog.INSTANCE.showDialog(config, false);


        }

        private void updateDialogContent(String text) {
            if (config == null) {
                return;
            }
            viewfinderView=config.getView(R.id.viewfinderView);
          //  gif = config.getView(R.id.loading_indicator);
           // Glide.with(getApplicationContext()).load(R.drawable.scanner).into(gif);
            TextView tvDialogContent = config.getView(R.id.tvDialogContent);
            tvDialogContent.setText(text);
           // tvDialogContent.setVisibility(View.INVISIBLE);
            tvDialogContent.setMovementMethod(ScrollingMovementMethod.getInstance());

        }

        private void showGlucoseMeasurementResult(Button btnRescan) {
            TextView tvDialogTitle = config.getView(R.id.tvDialogTitle);
            tvDialogTitle.setText("Blood Glucose Measurement");
            TextView tvDialogContent = config.getView(R.id.tvDialogContent);
           // tvDialogContent.setVisibility(View.VISIBLE);
            tvDialogContent.setText("Glucose Level: " + glucoseValue + " " + glucoseUnit);
            btnRescan.setVisibility(View.VISIBLE);
            Button btnConfirm=  config.getView(R.id.btnDialogConfirm);
            btnConfirm.setVisibility(View.VISIBLE);
            Button btnCancel=  config.getView(R.id.btnDialogCancel);
            btnCancel.setVisibility(View.INVISIBLE);
           // gif.setVisibility(View.GONE);
            viewfinderView.setVisibility(View.GONE);

            btnRescan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    glucoseLevelRecognized = false;
                    getCameraScan().setAnalyzeImage(true);
                    TextView tvDialogTitle = config.getView(R.id.tvDialogTitle);
                    tvDialogTitle.setText("Rescanning for Measurement");
                    btnRescan.setVisibility(View.GONE);
                    Button btnConfirm=  config.getView(R.id.btnDialogConfirm);
                    btnConfirm.setVisibility(View.GONE);
                    Button btnCancel=  config.getView(R.id.btnDialogCancel);
                    btnCancel.setVisibility(View.VISIBLE);
                   // gif.setVisibility(View.VISIBLE);
                    viewfinderView.setVisibility(View.VISIBLE);

                }
            });

            handler.removeCallbacks(stopScanRunnable);
            handler.postDelayed(stopScanRunnable, 30000);
        }

        @Override
        public Analyzer<Text> createAnalyzer() {
            return new TextRecognitionAnalyzer(new TextRecognizerOptions.Builder().build());
        }

    public void saveMeasurement(int glucoseValue) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        int intVal = glucoseValue;
        String BpName = "BG";
        RetrofitClient retrofit = new RetrofitClient();
        Retrofit retrofitClient = retrofit.getRetrofitInstance(this);
        if (retrofitClient == null) {
            return;
        }

            SaveMeasureModel measure = new SaveMeasureModel();
            measure.setParamName(BpName);
            measure.setParamFraction("");
            measure.setDevmodelId("2ab90e73-99c5-11eb-853f-e9af88721123");
            measure.setDevId("852a2034-c8dd-11eb-a396-755a8569ff4d");
            measure.setIntVal(String.valueOf(intVal));
            measure.setPatientId("1aa0001");
            Call<StatusResponseModel> call = retrofitClient.create(ServiceApi.class).saveMeasure("1aa0001", measure);
            call.enqueue(new Callback<StatusResponseModel>() {
                @Override
                public void onResponse(Call<StatusResponseModel> call, Response<StatusResponseModel> response) {
                    if (response.isSuccessful()) {
                        StatusResponseModel saveMeasureModel = new StatusResponseModel();
                        saveMeasureModel = response.body();
                        if (saveMeasureModel.getStatus().getMessage().equalsIgnoreCase("Success")) {
                            showSuccess("Measurement has been saved successfully","Measure Saved");
                        } else {
                            Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<StatusResponseModel> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
                }
            });


        }


    private void showSuccess(String msg, String title){
//Base_ThemeOverlay_AppCompat_Dialog_Alert {-White Color}
        //Base_Theme_MaterialComponents_Dialog_Alert {-Black}
        AlertDialog.Builder builder =new AlertDialog.Builder(new ContextThemeWrapper(GlucoMeterScanner.this, com.hbb20.R.style.Widget_AppCompat_ButtonBar_AlertDialog));
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialogInterface, i) ->
                finish());
        AppDialog.INSTANCE.dismissDialog();
        builder.show();

    }
}