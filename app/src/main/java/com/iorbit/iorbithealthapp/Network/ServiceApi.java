package com.iorbit.iorbithealthapp.Network;

import static com.iorbit.iorbithealthapp.Network.RetrofitClient.TOKEN;

import com.iorbit.iorbithealthapp.Models.DisplayMeasurements;
import com.iorbit.iorbithealthapp.Models.ECGDetailResponse;
import com.iorbit.iorbithealthapp.Models.ECGListResponse;
import com.iorbit.iorbithealthapp.Models.GetPatientModel;
import com.iorbit.iorbithealthapp.Models.LoginUserModel;
import com.iorbit.iorbithealthapp.Models.MeasurementResponse;
import com.iorbit.iorbithealthapp.Models.PatientModel;
import com.iorbit.iorbithealthapp.Models.RegisterUserModel;
import com.iorbit.iorbithealthapp.Models.RegisterUserResponse;
import com.iorbit.iorbithealthapp.Models.SaveMeasureModel;
import com.iorbit.iorbithealthapp.Models.StatusResponse;
import com.iorbit.iorbithealthapp.Models.StatusResponseModel;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface ServiceApi {


    @Headers({"Authorization: Bearer "+ TOKEN})
    @POST("usersignin")
    Call<LoginUserModel>UserLogin(@Body LoginUserModel loginUserModel);


    @Headers({"Authorization: Bearer "+ TOKEN})
    @GET("CheckUserExist/{phoneNumber}")
    Call<StatusResponse>UserCheck(@Path(value="phoneNumber")
                                     String phoneNumber);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @POST("User/registration")
    Call<RegisterUserResponse>UserRegister(@Body RegisterUserModel registerUserModel);


    @Headers({"Authorization: Bearer "+ TOKEN})
    @GET("CheckPatientExist/{phoneNumber}")
    Call<StatusResponse>CheckPateint(@Path(value = "phoneNumber") String phoneNumber);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @POST("physician/{userID}/savepatient")
    Call<StatusResponse>addPatient(@Path(value = "userID")
                                   String userID,
                                   @Body PatientModel patientModel);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @POST("patientId/{patientId}/savemeasurement")
    Call<StatusResponseModel>saveMeasure(@Path(value="patientId")
                                                 String patientId,
                                         @Body SaveMeasureModel measureModel);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @GET("physician/{userid}/getallPatientDetails")
    Call<GetPatientModel>getPatient(@Path(value="userid")
                                          String userid);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @DELETE("patients/{ssid}")
    Call<GetPatientModel>deletePatient(@Path(value="ssid")
                                            String ssid);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @GET("patientSSid/{ssid}/datestatus/{duration}/getecglist")
    Call<ECGListResponse>getEcgMeasure(@Path(value="ssid")
                                            String ssid,
                                       @Path(value="duration")
                                               Integer duration);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @GET("patientSSid/{ssid}/parameter/ECG/measurementid/{measurementid}/getmeasurement")
    Call<ECGDetailResponse>getEcgMeasureDetail(@Path(value="ssid")
                                               String ssid,
                                               @Path(value="measurementid")
                                               String measurementid);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @GET("patientSSid/{ssid}/parameter/BP/datestatus/{duration}/getmeasurement")
    Call<DisplayMeasurements>getBPMeasure(@Path(value="ssid")
                                               String ssid,
                                @Path(value="duration")
                                               Integer duration);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @GET("patientSSid/{ssid}/parameter/BPM/datestatus/{duration}/getmeasurement")
    Call<DisplayMeasurements>getBPMMeasure(@Path(value="ssid")
                                                  String ssid,
                                          @Path(value="duration")
                                                  Integer duration);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @GET("patientSSid/{ssid}/parameter/oxygen_level/datestatus/{duration}/getmeasurement")
    Call<DisplayMeasurements>getSPO2Measure(@Path(value="ssid")
                                                   String ssid,
                                           @Path(value="duration")
                                                   Integer duration);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @GET("patientSSid/{ssid}/parameter/temperature/datestatus/{duration}/getmeasurement")
    Call<DisplayMeasurements>getTempMeasure(@Path(value="ssid")
                                                    String ssid,
                                            @Path(value="duration")
                                                    Integer duration);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @GET("patientSSid/{ssid}/parameter/BG/datestatus/{duration}/getmeasurement")
    Call<DisplayMeasurements>getbgMeasure(@Path(value="ssid")
                                                    String ssid,
                                            @Path(value="duration")
                                                    Integer duration);

    @Headers({"Authorization: Bearer "+ TOKEN})
    @GET("getpm/{ssid}/datestatus/{duration}/measurements")
    Call<MeasurementResponse>getReportHistory(@Path(value="ssid")
                                                  String ssid,
                                              @Path(value="duration")
                                                  Integer duration);


}
