package com.iorbit.iorbithealthapp.Utils;

import java.util.UUID;


public interface Constants {

    String TAG = "Arduino - Android";
    int REQUEST_ENABLE_BT = 1;

    // message types sent from the BluetoothChatService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_SNACKBAR = 4;
    int CONNECTION_LOST = 10;
    // Constants that indicate the current connection state
    int STATE_NONE = 0;       // we're doing nothing
    int STATE_ERROR = 1;
    int STATE_CONNECTING = 2; // now initiating an outgoing connection
    int STATE_CONNECTED = 3;  // now connected to a remote device


    UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    // Key names received from the BluetoothChatService Handler
    String COMMUNICATION_ERROR_NACK = "FF";
    String EXTRA_DEVICE  = "EXTRA_DEVICE";
    String SNACKBAR = "toast";
    String TIME_SYNC="C072XXXX";
    String SNAPSHOT = "C073XXXXC00000";
    String OFFLINESNAPSHOT = "C074XXXXC00000";
    String NACK = "C0FFXXXXC00000";
    String BP = "C010XXXXC00000";
    String BG = "C011XXXXC00000";
    String ECG = "C012XXXXC00000";
    String ECG12 = "C016XXXXC00000";
    String SPO2 = "C014XXXXC00000";
    String BODYTEMP = "C015XXXXC00000";
    String ACK = "C000XXXXC00000";
    String PID_REG_CMD = "C071XXXX";
    String PID_DE_REG_CMD = "C071XXXX01";
    String PID_PT_REG_CMD = "C071XX00X";
    String EOS = "C0";
    String DEVICE_CONFIG= "C077XXXX";
    String CALIBRATION = "C018XXXXC00000";
    String NACKCOMMAND = "C0FFXXXXC00000";
    String INVALIDBYTE = "";

    String VERSION_REQUEST = "C061XXXXC00000";
    String ERROR_REQUEST = "C041XXXXC00000";
    String FIRMWARE_UPDATE_REQUEST = "C070XXXXC00000";
    String OFFLINE_REQUEST = "C017XXXXC00000";
    String REQUESTED_DATA_NOT_SUPPORTED_NACK = "FD";
    String DATA_NOT_AVAILABLE_NACK = "FE";
    String ACK_BYTE = "00";

    String START_RECORD = "C001";
    String CONTINUE_RECORD = "C002";
    String END_RECORD = "C003";
    String END_RECORD_BYTE = "03";
    String END = "C00000";


    String TEST_TYPE_BP = "00";
    String TEST_TYPE_BG_F = "08";
    String TEST_TYPE_BG_R = "01";
    String TEST_TYPE_BG_P = "09";
    String TEST_TYPE_SPO2 = "06";
    String TEST_TYPE_BODY_TEMP = "07";

    String TEST_TYPE_ECG = "03";
    String TEST_TYPE_HEIGHT = "04";
    String TEST_TYPE_WEIGHT = "05";
    String TEST_TYPE_WAIST = "02";
    String TEST_TYPE_CHEST = "10";
    String TEST_TYPE_DIET = "11";
    String HOSPITAL_NO_CALIBRATION = "00";
    String HOSPITAL_FACTOR_CALIBRATION = "02";
    String HOSPITAL_STD_BP_CALIBRATION = "04";


    String TYPE_BP = "12";
    String TYPE_BG = "13";
    String TYPE_BT = "14";

    String SELFDIAGNOSTIC = "C080XXXXC00000";
    String FULLDIAGNOSTIC = "C079XXXXC00000";

    int HIGH = 2;
    int LOW = 1;
    int INFO = 0;

    String AWS_DEVICE_SYNC_BUCKET = "testing-device-data-sync";
    String AWS_HOSPITAL_REG_CERTIFICATE = "testing-hospital-registration-certificate";
    String AWS_HOSPITAL_EST_CERTIFICATE = "testing-hospital-establishment-certificate";
    String AWS_HOSPITAL_DOCTOR_CERTIFICATE = "testing-doctor-certificate";
    String AWS_MANUAL_ENTRY_BUCKET = "testing-manual-entry";
    String AWS_ECG_IMAGE_BUCKET = "testing-sense-ecg-images";
    String AWS_PRESCRIPTION_IMAGE_BUCKET = "testing-sense-prescription-images";
    String AWS_OTHER_REPORT_IMAGE_BUCKET = "testing-sense-other-reports-images";
    String AWS_IDENTITY_POOL_ID = "ap-south-1:5ae380a7-ac7f-412b-8593-73204916ff2a";
    String AWS_FIRMWARE = "testing-sense-firmware";
    String AWS_INTERNAL_TESTING_FIRMWARE = "internal-testing-firmware";

    String API_URL="https://ptr2mufjcg.execute-api.ap-south-1.amazonaws.com/api/";
  //  String API_URL_ITHINGS="http://178.128.165.237:8090/api/j1/";
    String API_URL_ITHINGS="http://178.128.165.237:8095/api/j2/";

    String AWS_ECG_IMAGE = "00";
    String AWS_PRESCRIPTION_IMAGE = "01";
    String AWS_OTHER_REPORT_IMAGE = "02";
    int BT_ENABLE_REQUEST = 607;




}
