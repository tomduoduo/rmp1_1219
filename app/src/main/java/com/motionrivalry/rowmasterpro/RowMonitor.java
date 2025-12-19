package com.motionrivalry.rowmasterpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.motionrivalry.rowmasterpro.UtilsBle.BleConnect;
import com.motionrivalry.rowmasterpro.UtilsBle.BleScanner;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.polar.sdk.api.PolarBleApi;
import com.polar.sdk.api.PolarBleApiDefaultImpl;
import com.tarek360.instacapture.Instacapture;
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener;
import com.xw.repo.BubbleSeekBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

public class RowMonitor extends AppCompatActivity {

    private ImageView mBoatRoll;
    private ImageView mBoatYaw;
    private ImageView mBoatOrientation;
    private TextView mSpeed;
    private TextView mSplit;
    private TextView mDistance;
    private TextView mStrokeRate;
    private CustomChronometer mTotalElapse;
    private Button mStart;
    private Button mSplitSwitch;
    private TextView mSplitUnit;
    private TextView mStartNotification;
    private TextView mSplitSpeed;

    private CustomNumberPicker mPickerTargetSPM;
    private CustomNumberPicker mPickerTargetMin;
    private CustomNumberPicker mPickerTargetSec;

    private TextView txTargetSPM;
    private TextView txTargetSplit;
    private BubbleSeekBar mSpeedMin;
    private BubbleSeekBar mAvgStrokeRateLength;
    private BubbleSeekBar mGapMin;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mAccelerometerLinear;
    private Sensor mMagnetic;
    private SensorListener mSensorListener;

    private FrameLayout mBanner;
    private TextView mBannerTextLeft;
    private TextView mBannerTextMid;
    private TextView mBannerTextRight;

    private LineChart chartSPM;
    private LineChart chartTimeSPM;
    private LineChart chartTimeSpeed;
    private LineChart chartTimeHR;
    protected Typeface tfLight;

    private double[] UI_params_double = new double[] { 0, 0, 0, 0 };
    private float[] UI_params_float = new float[] { 0f, 0f, 0f };
    // [0]boat_yaw, [1]boat_roll ,[2]boat_orientation
    private String[] UI_params_string = new String[] { "0", "0:00", "0.0", "0", "0" };
    // [0]SPM, [1]Split, [2]speed, [3]hours, [4]distance

    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    private float[] accelerometerLinearValues = new float[3];

    private int acclCacheLength = 30;
    private double[] acclCacheSamples = new double[acclCacheLength];
    private int acclCachePointer = 0;
    private int acclCacheSize = 0;
    private double acclCacheSum = 0.0;

    private int SRCacheLength = 2;
    private double[] SRCacheSamples = new double[SRCacheLength];
    private int SRCachePointer = 0;
    private int SRCacheSize = 0;
    private double SRCacheSum = 0.0;

    private float yawAdjustRatio = 1.4f;
    private int boatYawCacheLength = 20;
    private double[] boatYawCacheSamples = new double[boatYawCacheLength];
    private int boatYawCachePointer = 0;
    private int boatYawCacheSize = 0;
    private double boatYawCacheSum = 0.0;

    private int boatSpeedCacheLength = 10;
    private double[] boatSpeedCacheSamples = new double[boatSpeedCacheLength];
    private int boatSpeedCachePointer = 0;
    private int boatSpeedCacheSize = 0;
    private double boatSpeedCacheSum = 0.0;

    private int compassCacheLength = 1;
    private double[] compassCacheSamples = new double[compassCacheLength];
    private int compassCachePointer = 0;
    private int compassCacheSize = 0;
    private double compassCacheSum = 0.0;

    double strokeCache;
    private double strokeIdleMax = 10000;
    private double minBoatAccl = 1.2;
    private double minStrokeGap = 1200;
    private double strokeCount = 0;
    private double strokeRefreshLowerThresh = -1.2;

    private Location mLocation;
    private LocationManager locationManager;
    private double latitude_0;
    private double longitude_0;
    private double speedLastUpdate;
    private double speedNewUpdate;
    private double traveledDistance = 0;
    private double speedLowLimit = 0.5;
    private double distanceTempHighLimit = 50;
    private double distanceTempLowLimit = 1;
    private double distanceTemp = 0;
    private double speedAvg = 0;
    private double speedMax = 0;
    private int initiateLock = 5;
    private int updateCount = 0;
    private float speedCache = 0;

    private TextView mGpsStatus;
    private int satelliteCount = 0;

    private int START_BUTTON_CASE = 0;
    private TimerTask updateUI_main;
    private int targetSPM = 30;
    private int targetSplit = 140;
    private int targetMin = 2;
    private int targetSec = 10;

    private Button mHRM;
    private Button mConfig;

    private PopupWindow mWindowHRM = null;
    private PopupWindow mWindowConfig = null;
    private PopupWindow mWindowResult = null;
    private PopupWindow mWindowTip = null;

    private View popupViewHRM;
    private View popupViewConfig;
    private View popupViewResult;
    private View PopupViewTip;

    private Button mConnectHRM;
    private Button mScanHRM;
    private Button mCloseHRM;
    private Button mCloseConfig;
    private Button mCloseTip;

    private String[] mItemsHR;
    private ArrayList<String> mSpinnerSelectedBelt = new ArrayList<>();
    private ArrayList<Spinner> mSpinnerListBelt = new ArrayList<>();
    private ArrayList<BluetoothDevice> mSelectedBeltList = new ArrayList<>();

    private BleScanner scannerHR;
    private ArrayList<BluetoothDevice> mDeviceListHR = new ArrayList<>();
    private ArrayList<String> mNameListBelt = new ArrayList<>();

    private Spinner mSpinnerHRM1;
    private Spinner mSpinnerHRM2;
    private Spinner mSpinnerHRM3;
    private Spinner mSpinnerHRM4;

    private BluetoothDevice Belt1;
    private BluetoothDevice Belt2;
    private BluetoothDevice Belt3;
    private BluetoothDevice Belt4;

    private boolean mIsScanning = false;
    private TextView mBeltCount;

    private ArrayList<BleConnect> mListBeltsConnect = new ArrayList<>();

    private TextView mDisplayHRM1;
    private TextView mDisplayHRM2;
    private TextView mDisplayHRM3;
    private TextView mDisplayHRM4;

    private TextView mStatusHRM1;
    private TextView mStatusHRM2;
    private TextView mStatusHRM3;
    private TextView mStatusHRM4;

    private ArrayList<TextView> mListStatusHRM = new ArrayList<>();
    private TextView mTxScanning;

    private int mSuccessConnection = 0;
    private int mIntentConnection = 0;

    private TextView bpmTxHRM1;
    private TextView bpmTxHRM2;
    private TextView bpmTxHRM3;
    private TextView bpmTxHRM4;

    private FrameLayout mMasterBg;
    private String[] mAvailableSPM = {
            "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25",
            "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35",
            "36", "37", "38", "39", "40",
            "41", "42", "43", "44", "45",
            "46", "47", "48", "49", "50",
    };

    private String[] mAvailableSec = {
            "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25",
            "26", "27", "28", "29", "30",
            "31", "32", "33", "34", "35",
            "36", "37", "38", "39", "40",
            "41", "42", "43", "44", "45",
            "46", "47", "48", "49", "50",
            "51", "52", "53", "54", "55",
            "56", "57", "58", "59", "00"

    };

    private String[] mAvailableMin = {
            "1", "2", "3", "4", "5",
    };

    private FileWriter loggerPhone_0 = null;
    private CSVWriter loggerPhone = null;
    private TimerTask loggerPhoneTask;
    private Timer loggerPhoneTimer;
    private double sectionTotalSec = 0;
    private String fileLocPhone = "";

    private String resultDistance = "";
    private String resultSectionTime = "";
    private String resultStrokeCount = "";

    private TextView mTxResultDistance;
    private TextView mTxResultSectionTime;
    private TextView mTxResultStrokeCount;

    private List<String[]> resultLog;

    private Button mResultClose;
    private Button mResultSave;
    private TextView mResultSaveTime;

    private AMapLocationClient mLocationClientGD = null;
    private AMapLocationListener mLocationListenerGD = null;
    private AMapLocationClientOption mLocationOptionGD = null;

    private double latitude_0_GD;
    private double longitude_0_GD;

    private String updatePATH = "";
    private HttpURLConnection httpURLConnectionUpdate;
    private TimerTask updateToDataV;
    private Timer updateToDataVTimer;
    private String userName;

    private TextView mSplitAlert;
    private int strokeRefreshLock = 0;

    private PolarBleApi api;
    Disposable broadcastDisposable;
    Disposable scanDisposable;

    private ArrayList<String> mListBeltsName = new ArrayList<>();
    private String BeltName1 = "-";
    private String BeltName2 = "-";
    private String BeltName3 = "-";
    private String BeltName4 = "-";

    private int[] boatSpeedClassifier = new int[] { 0, 0, 0, 0, 0 };
    private int[] strokeRateClassifier = new int[] { 0, 0, 0, 0, 0 };
    private int[] heartRateClassifier = new int[] { 0, 0, 0, 0, 0 };

    private double boatSpeedZone_0 = 1;
    private double boatSpeedZone_1 = 2;
    private double boatSpeedZone_2 = 3;
    private double boatSpeedZone_3 = 4;

    private double strokeRateZone_0 = 17;
    private double strokeRateZone_1 = 26;
    private double strokeRateZone_2 = 34;
    private double strokeRateZone_3 = 43;

    private double heartRateZone_0 = 80;
    private double heartRateZone_1 = 95;
    private double heartRateZone_2 = 133;
    private double heartRateZone_3 = 152;

    private double sectionCount = 0;

    private TimerTask updateResult;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_row_monitor);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String[] PermissionString = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
                Manifest.permission.INTERNET,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE };

        checkPermission(PermissionString);

        api = PolarBleApiDefaultImpl.defaultImplementation(this, PolarBleApi.ALL_FEATURES);
        api.setPolarFilter(false);

        // int REQUEST_EXTERNAL_STORAGE = 1;
        // String[] PERMISSIONS_STORAGE = {
        // Manifest.permission.READ_EXTERNAL_STORAGE,
        // Manifest.permission.WRITE_EXTERNAL_STORAGE
        // };
        //
        // int permission = ActivityCompat.checkSelfPermission(RowMonitor.this,
        // Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // if (permission != PackageManager.PERMISSION_GRANTED) {
        // ActivityCompat.requestPermissions(
        // RowMonitor.this,
        // PERMISSIONS_STORAGE,
        // REQUEST_EXTERNAL_STORAGE
        // );
        // }

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupViewHRM = inflater.inflate(R.layout.popup_hrm_connect, null, false);
        popupViewConfig = inflater.inflate(R.layout.popup_config, null, false);
        popupViewResult = inflater.inflate(R.layout.popup_result, null, false);
        PopupViewTip = inflater.inflate(R.layout.popup_mounting, null, false);

        mBoatRoll = findViewById(R.id.boat_roll_RM);
        mBoatYaw = findViewById(R.id.boat_yaw_RM);
        mBoatOrientation = findViewById(R.id.compass_RM);
        mStrokeRate = findViewById(R.id.stroke_rate_RM);
        mSpeed = findViewById(R.id.speed_RM);
        mSplit = findViewById(R.id.split_RM);
        mDistance = findViewById(R.id.section_distance_RM);
        mTotalElapse = findViewById(R.id.section_time_RM);
        mMasterBg = findViewById(R.id.master_bg_RM);

        mGpsStatus = findViewById(R.id.gps_status_RM);

        mStart = findViewById(R.id.ready_RM);
        mSplitSwitch = findViewById(R.id.change_split_unit_RM);
        mSplitUnit = findViewById(R.id.split_unit_RM);

        mBanner = findViewById(R.id.row_status_bg_RM);
        mBannerTextLeft = findViewById(R.id.row_status_left_RM);
        mBannerTextMid = findViewById(R.id.row_status_center_RM);
        mBannerTextRight = findViewById(R.id.row_status_right_RM);
        mStartNotification = findViewById(R.id.notification_select_RM);
        mSplitSpeed = findViewById(R.id.split_speed_RM);

        mHRM = findViewById(R.id.HRM_connect_RM);
        mConfig = findViewById(R.id.config_menu_RM);

        mDisplayHRM4 = findViewById(R.id.hr_4_display_RM);
        mDisplayHRM3 = findViewById(R.id.hr_3_display_RM);
        mDisplayHRM2 = findViewById(R.id.hr_2_display_RM);
        mDisplayHRM1 = findViewById(R.id.hr_1_display_RM);

        bpmTxHRM1 = findViewById(R.id.txBpm_hr1_RM);
        bpmTxHRM2 = findViewById(R.id.txBpm_hr2_RM);
        bpmTxHRM3 = findViewById(R.id.txBpm_hr3_RM);
        bpmTxHRM4 = findViewById(R.id.txBpm_hr4_RM);
        chartSPM = findViewById(R.id.chart_SPM_RM);

        txTargetSPM = findViewById(R.id.target_SPM_RM);
        txTargetSplit = findViewById(R.id.target_Split_RM);

        mScanHRM = popupViewHRM.findViewById(R.id.HRM_scan_RM);
        mConnectHRM = popupViewHRM.findViewById(R.id.HRM_connect_RM);
        mCloseHRM = popupViewHRM.findViewById(R.id.HRM_close_RM);
        mScanHRM = popupViewHRM.findViewById(R.id.HRM_scan_RM);

        mSpinnerHRM1 = popupViewHRM.findViewById(R.id.hr_1_spinner_RM);
        mSpinnerHRM2 = popupViewHRM.findViewById(R.id.hr_2_spinner_RM);
        mSpinnerHRM3 = popupViewHRM.findViewById(R.id.hr_3_spinner_RM);
        mSpinnerHRM4 = popupViewHRM.findViewById(R.id.hr_4_spinner_RM);

        mStatusHRM1 = popupViewHRM.findViewById(R.id.HRM1_status_RM);
        mStatusHRM2 = popupViewHRM.findViewById(R.id.HRM2_status_RM);
        mStatusHRM3 = popupViewHRM.findViewById(R.id.HRM3_status_RM);
        mStatusHRM4 = popupViewHRM.findViewById(R.id.HRM4_status_RM);

        mTxScanning = popupViewHRM.findViewById(R.id.scanning_tx_RM);

        mBeltCount = popupViewHRM.findViewById(R.id.belts_count_RM);

        mPickerTargetSPM = popupViewConfig.findViewById(R.id.SPM_select_RM);
        mPickerTargetMin = popupViewConfig.findViewById(R.id.Split_min_select_RM);
        mPickerTargetSec = popupViewConfig.findViewById(R.id.Split_sec_select_RM);

        mCloseConfig = popupViewConfig.findViewById(R.id.config_close_RM);

        mSpeedMin = popupViewConfig.findViewById(R.id.SR_minSpeed_slider_RM);
        mAvgStrokeRateLength = popupViewConfig.findViewById(R.id.SR_minResponse_slider_RM);
        mGapMin = popupViewConfig.findViewById(R.id.SR_minGap_slider_RM);

        mTxResultDistance = popupViewResult.findViewById(R.id.result_distance_RM);
        mTxResultSectionTime = popupViewResult.findViewById(R.id.result_time_RM);
        mTxResultStrokeCount = popupViewResult.findViewById(R.id.result_SR_count_RM);

        chartTimeSPM = popupViewResult.findViewById(R.id.chart_result_SPM_time_RM);
        chartTimeSpeed = popupViewResult.findViewById(R.id.chart_result_speed_time_RM);
        chartTimeHR = popupViewResult.findViewById(R.id.chart_result_HR_time_RM);

        mResultClose = popupViewResult.findViewById(R.id.result_close_RM);
        mResultSave = popupViewResult.findViewById(R.id.result_save_RM);
        mResultSaveTime = popupViewResult.findViewById(R.id.section_save_time_RM);

        mCloseTip = PopupViewTip.findViewById(R.id.mounting_close_RM);

        mSplitAlert = findViewById(R.id.speed_alert_RM);

        mPickerTargetSPM.setMinValue(1);
        mPickerTargetSPM.setMaxValue(mAvailableSPM.length);
        mPickerTargetSPM.setValue(30);
        mPickerTargetSPM.setDisplayedValues(mAvailableSPM);
        mPickerTargetSPM.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        mPickerTargetMin.setMinValue(1);
        mPickerTargetMin.setMaxValue(mAvailableMin.length);
        mPickerTargetMin.setValue(2);
        mPickerTargetMin.setDisplayedValues(mAvailableMin);
        mPickerTargetMin.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        mPickerTargetSec.setMinValue(1);
        mPickerTargetSec.setMaxValue(mAvailableSec.length);
        mPickerTargetSec.setValue(10);
        mPickerTargetSec.setDisplayedValues(mAvailableSec);
        mPickerTargetSec.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        mSpinnerListBelt.add(mSpinnerHRM1);
        mSpinnerListBelt.add(mSpinnerHRM2);
        mSpinnerListBelt.add(mSpinnerHRM3);
        mSpinnerListBelt.add(mSpinnerHRM4);

        mSelectedBeltList.add(Belt1);
        mSelectedBeltList.add(Belt2);
        mSelectedBeltList.add(Belt3);
        mSelectedBeltList.add(Belt4);

        mListStatusHRM.add(mStatusHRM1);
        mListStatusHRM.add(mStatusHRM2);
        mListStatusHRM.add(mStatusHRM3);
        mListStatusHRM.add(mStatusHRM4);

        mListBeltsName.add(BeltName1);
        mListBeltsName.add(BeltName2);
        mListBeltsName.add(BeltName3);
        mListBeltsName.add(BeltName4);

        // Reverse Order

        for (int i = 0; i < 8; i++) {
            mSpinnerSelectedBelt.add("-");
        }

        mItemsHR = getResources().getStringArray(R.array.BeltsName);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.my_spinner_gray, mItemsHR);
        ;
        adapter.setDropDownViewResource(R.layout.my_drop_down_gray);
        mSpinnerHRM1.setAdapter(adapter);
        mSpinnerHRM2.setAdapter(adapter);
        mSpinnerHRM3.setAdapter(adapter);
        mSpinnerHRM4.setAdapter(adapter);

        // initHR();

        mWindowHRM = new PopupWindow(popupViewHRM, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        mWindowConfig = new PopupWindow(popupViewConfig, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        mWindowResult = new PopupWindow(popupViewResult, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true);
        mWindowTip = new PopupWindow(PopupViewTip, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);

        try {
            mLocationClientGD = new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLocationListenerGD = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {

                latitude_0_GD = aMapLocation.getLatitude();
                longitude_0_GD = aMapLocation.getLongitude();

            }

        };

        mLocationClientGD.setLocationListener(mLocationListenerGD);
        mLocationOptionGD = new AMapLocationClientOption();

        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        mLocationOptionGD.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOptionGD.setInterval(2000);
        mLocationClientGD.startLocation();

        mConnectHRM.setEnabled(false);
        mConnectHRM.setAlpha(0);

        mGapMin.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                    boolean fromUser) {
                minStrokeGap = (double) progress * 1000;
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                    boolean fromUser) {
            }
        });

        mSpeedMin.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                    boolean fromUser) {
                minBoatAccl = (double) progress;
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                    boolean fromUser) {
            }
        });

        mAvgStrokeRateLength.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                    boolean fromUser) {
                SRCacheLength = progress;
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                    boolean fromUser) {
            }
        });

        mPickerTargetSPM.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                targetSPM = newVal;
            }
        });

        mPickerTargetMin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                targetMin = newVal;
                targetSplit = targetMin * 60 + targetSec;
            }
        });

        mPickerTargetSec.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                targetSec = newVal;
                targetSplit = targetMin * 60 + targetSec;

            }
        });

        mCloseHRM.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mWindowHRM.dismiss();

            }

        });

        mCloseConfig.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mWindowConfig.dismiss();

            }

        });

        mCloseTip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mWindowTip.dismiss();

            }

        });

        mScanHRM.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (checkBluetoothValid() == 1) {

                    if (!mIsScanning) {

                        initPolar();
                        mountPolar(0);
                        resetList();

                        mBeltCount.setText("0");
                        mNameListBelt.clear();
                        mDeviceListHR.clear();
                        mNameListBelt.add("-");

                        mListBeltsName.clear();
                        mListBeltsName.add("-");
                        mListBeltsName.add("-");
                        mListBeltsName.add("-");
                        mListBeltsName.add("-");

                        // connectDeviceHR(0);
                        mTxScanning.setAlpha(1f);
                        // scannerHR.Start();
                        mIsScanning = true;

                        mConnectHRM.setEnabled(false);
                        mConnectHRM.setAlpha(0);

                    } else {
                        // scannerHR.Stop();
                        mTxScanning.setAlpha(0f);
                        mIsScanning = false;
                        initPolar();

                    }

                    mScanHRM.setText(mIsScanning ? "停止" : "扫描");

                }

            }

        });

        mConnectHRM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mScanHRM.performClick();

                for (int i = 0; i < mListBeltsName.size(); i++) {
                    if (mListBeltsName.get(i) != "-") {
                        mIntentConnection++;
                    }
                }

                if (mIntentConnection < 1) {

                    Toast.makeText(RowMonitor.this, "Select at Least 1 HRM to Connect", Toast.LENGTH_SHORT).show();

                } else {

                    mConnectHRM.setEnabled(false);
                    // mConnectHRM.setTextSize(9);
                    mConnectHRM.setText("已连接");
                    // scannerHR.Stop();
                    mTxScanning.setAlpha(0f);
                    mIsScanning = false;
                    // connectDeviceHR(1);
                    mountPolar(1);

                }

            }
        });

        mSplitSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSplit.getAlpha() == 1) {
                    mSplit.setAlpha(0);
                    mSpeed.setAlpha(1);
                    mSplitUnit.setText("M/S");
                    mSplitSpeed.setText("SPEED");
                } else {
                    mSplit.setAlpha(1);
                    mSpeed.setAlpha(0);
                    mSplitUnit.setText("/500M");
                    mSplitSpeed.setText("SPLIT");

                }
            }

        });

        mHRM.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                showPopup(mWindowHRM);
                backgroundAlpha(0.2f);

            }

        });

        mConfig.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                showPopup(mWindowConfig);
                backgroundAlpha(0.2f);

            }

        });

        mResultSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Instacapture.INSTANCE.capture(RowMonitor.this, new SimpleScreenCapturingListener() {
                    @Override
                    public void onCaptureComplete(Bitmap bitmap) {

                        saveImageToGallery(bitmap, RowMonitor.this);

                    }
                }, mResultSave, mResultClose);

                Toast.makeText(RowMonitor.this, "Screenshot saved in photo album", Toast.LENGTH_SHORT).show();
                mWindowResult.dismiss();

            }

        });

        mResultClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                mWindowResult.dismiss();

            }

        });

        mSpinnerHRM1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (mNameListBelt.size() >= 1 && arg2 > 0) {
                    String name = String.valueOf(mSpinnerHRM1.getSelectedItem());
                    mSpinnerSelectedBelt.set(0, name);
                    Belt1 = getAddress(Belt1, name);
                    mSelectedBeltList.set(0, Belt1);
                    deleteDuplicateBelt(0, name);
                    mListBeltsName.set(0, name);
                    // System.out.println(Belt1.getAddress());

                    for (int i = 0; i < mSelectedBeltList.size(); i++) {

                        System.out.println("Spinner 1 Status:" + mListBeltsName.get(i));

                        if (mListBeltsName.get(i) != "-") {
                            mIntentConnection++;
                        }
                    }

                    if (mIntentConnection > 0) {

                        mConnectHRM.setEnabled(true);
                        mConnectHRM.setAlpha(1);
                        mConnectHRM.setText("连接");
                    }
                }

                if (arg2 == 0) {
                    Belt1 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Belt1 = null;
            }
        });

        mSpinnerHRM2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (mNameListBelt.size() >= 1 && arg2 > 0) {
                    String name = String.valueOf(mSpinnerHRM2.getSelectedItem());
                    mSpinnerSelectedBelt.set(1, name);
                    Belt2 = getAddress(Belt2, name);
                    mSelectedBeltList.set(1, Belt2);
                    deleteDuplicateBelt(1, name);
                    mListBeltsName.set(1, name);
                    for (int i = 0; i < mSelectedBeltList.size(); i++) {
                        if (mListBeltsName.get(i) != "-") {
                            mIntentConnection++;
                        }
                    }

                    if (mIntentConnection > 1) {

                        mConnectHRM.setEnabled(true);
                        mConnectHRM.setAlpha(1);
                        mConnectHRM.setText("连接");
                    }

                    // System.out.println(Belt2.getAddress());
                }
                if (arg2 == 0) {
                    Belt2 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Belt2 = null;
            }
        });

        mSpinnerHRM3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (mNameListBelt.size() >= 1 && arg2 > 0) {
                    String name = String.valueOf(mSpinnerHRM3.getSelectedItem());
                    mSpinnerSelectedBelt.set(2, name);
                    Belt3 = getAddress(Belt3, name);
                    mSelectedBeltList.set(2, Belt3);
                    deleteDuplicateBelt(2, name);
                    mListBeltsName.set(2, name);
                    for (int i = 0; i < mSelectedBeltList.size(); i++) {
                        if (mListBeltsName.get(i) != "-") {
                            mIntentConnection++;
                        }
                    }

                    if (mIntentConnection > 1) {

                        mConnectHRM.setEnabled(true);
                        mConnectHRM.setAlpha(1);
                        mConnectHRM.setText("连接");
                    }
                    // System.out.println(Belt3.getAddress());
                }
                if (arg2 == 0) {
                    Belt3 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Belt3 = null;
            }
        });

        mSpinnerHRM4.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (mNameListBelt.size() >= 1 && arg2 > 0) {
                    String name = String.valueOf(mSpinnerHRM4.getSelectedItem());
                    mSpinnerSelectedBelt.set(3, name);
                    Belt4 = getAddress(Belt4, name);
                    mSelectedBeltList.set(3, Belt4);
                    deleteDuplicateBelt(3, name);
                    mListBeltsName.set(3, name);
                    for (int i = 0; i < mSelectedBeltList.size(); i++) {
                        if (mListBeltsName.get(i) != "-") {
                            mIntentConnection++;
                        }
                    }

                    if (mIntentConnection > 1) {

                        mConnectHRM.setEnabled(true);
                        mConnectHRM.setAlpha(1);
                        mConnectHRM.setText("连接");
                    }
                    // System.out.println(Belt4.getAddress());
                }
                if (arg2 == 0) {
                    Belt4 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Belt4 = null;
            }
        });

        mStart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startButtonAction(START_BUTTON_CASE);
                mSplitAlert.setBackgroundResource(R.color.colorTransparent);
                return true;
            }
        });

        mSensorListener = new SensorListener();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometerLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 500, 3, locationListener);
        locationManager.registerGnssStatusCallback(mGnssStatusCallback);

        chartInit(chartSPM, 0);
        strokeCache = System.currentTimeMillis();

        // Timer timerTip = new Timer();
        // TimerTask showTip = new TimerTask() {
        // @Override
        // public void run() {
        // runOnUiThread(new Runnable() {
        // @Override
        // public void run() {
        //
        // showPopup(mWindowTip);
        //// mWindowTip.setFocusable(false);
        //// mWindowTip.setOutsideTouchable(false);
        // backgroundAlpha(0.2f);
        //
        // }
        // });
        // }
        // };timerTip.schedule(showTip,1000);

        // int second = 3599;
        // mTotalElapse.setBase(SystemClock.elapsedRealtime()-second*1000);
        // mTotalElapse.start();
        // updateUI(1);

    }

    private void startButtonAction(int status) {

        switch (status) {
            case 0:
                mStart.setBackgroundResource(R.drawable.gradient_orange);
                mMasterBg.setBackgroundResource(R.drawable.gradient_orange);
                mStart.setTextColor(Color.BLACK);
                mBanner.setBackgroundResource(R.drawable.gradient_orange);
                mBanner.setVisibility(View.VISIBLE);
                mBanner.setAlpha(1.f);
                mBannerTextLeft.setTextColor(Color.BLACK);
                mBannerTextMid.setTextColor(Color.BLACK);
                mBannerTextRight.setTextColor(Color.BLACK);
                mBannerTextLeft.setText("开");
                mBannerTextMid.setText("始");
                mBannerTextRight.setText("划");

                mStart.setText("取消准备");
                START_BUTTON_CASE = 1;
                break;

            case 1:
                mMasterBg.setBackgroundResource(R.drawable.gradient_green);
                mStart.setBackgroundResource(R.drawable.gradient_green);
                mStart.setTextColor(Color.WHITE);
                mBanner.setAlpha(0f);
                mMasterBg.setBackgroundResource(R.drawable.gradient_black_2);
                mStart.setText("准备");
                START_BUTTON_CASE = 0;
                break;

            case 2:

                sectionCount = 0;
                boatSpeedClassifier = new int[] { 0, 0, 0, 0, 0 };
                strokeRateClassifier = new int[] { 0, 0, 0, 0, 0 };
                heartRateClassifier = new int[] { 0, 0, 0, 0, 0 };

                try {
                    loggerInit();
                    phoneLoggerStandalone();
                    // startConnection();
                    startUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mBanner.setBackgroundResource(R.drawable.gradient_green);
                mMasterBg.setBackgroundResource(R.drawable.gradient_green);
                mBannerTextLeft.setText("开始");
                mBannerTextMid.setText("开始");
                mBannerTextRight.setText("开始");
                mBanner.setAlpha(1f);

                // mSpeedMin.setSecondTrackColor(Color.DKGRAY);
                // mGapMin.setSecondTrackColor(Color.DKGRAY);
                // mAvgStrokeRateLength.setSecondTrackColor(Color.DKGRAY);

                // mSpeedMin.setThumbColor(Color.DKGRAY);
                // mGapMin.setThumbColor(Color.DKGRAY);
                // mAvgStrokeRateLength.setThumbColor(Color.DKGRAY);

                mSpeedMin.setEnabled(false);
                mGapMin.setEnabled(false);
                mAvgStrokeRateLength.setEnabled(false);

                resetUI();

                mBannerTextLeft.setTextColor(Color.WHITE);
                mBannerTextMid.setTextColor(Color.WHITE);
                mBannerTextRight.setTextColor(Color.WHITE);

                Animation fadeout_0 = new AlphaAnimation(1.f, 0.f);
                fadeout_0.setDuration(1000);
                mBanner.startAnimation(fadeout_0);
                mBanner.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBanner.setVisibility(View.GONE);
                    }
                }, 1000);

                mStart.setText("停止");
                mStart.setBackgroundResource(R.drawable.gradient_red);
                mStart.setTextColor(Color.WHITE);
                START_BUTTON_CASE = 3;
                break;

            case 3:

                updateUI(0);

                updateToDataVTimer.cancel();
                updateToDataV.cancel();

                mBanner.setBackgroundResource(R.drawable.gradient_red);
                mMasterBg.setBackgroundResource(R.drawable.gradient_black_2);
                mBannerTextLeft.setText("停止");
                mBannerTextMid.setText("停止");
                mBannerTextRight.setText("停止");

                int[] colors = { getResources().getColor(R.color.colorGreenDark),
                        getResources().getColor(R.color.colorOrangeDark),
                        getResources().getColor(R.color.colorGrayDark) };

                mBanner.setAlpha(1f);

                // mSpeedMin.setThumbColor(colors[0]);
                // mGapMin.setThumbColor(colors[0]);
                // mAvgStrokeRateLength.setThumbColor(colors[0]);

                // mSpeedMin.setSecondTrackColor(colors[0]);
                // mGapMin.setSecondTrackColor(colors[0]);
                // mAvgStrokeRateLength.setSecondTrackColor(colors[0]);

                mSpeedMin.setEnabled(true);
                mGapMin.setEnabled(true);
                mAvgStrokeRateLength.setEnabled(true);

                mBannerTextLeft.setTextColor(Color.WHITE);
                mBannerTextMid.setTextColor(Color.WHITE);
                mBannerTextRight.setTextColor(Color.WHITE);

                chartSPM.clear();
                chartSPM.invalidate();
                chartInit(chartSPM, 0);
                chartInit(chartSPM, 0);

                Animation fadeout_1 = new AlphaAnimation(1.f, 0.f);
                fadeout_1.setDuration(1000);
                mBanner.startAnimation(fadeout_1);
                mBanner.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBanner.setVisibility(View.GONE);
                    }
                }, 1000);

                // Timer timer_1 = new Timer();
                // timer_1.schedule(new TimerTask() {
                // @Override
                // public void run() {
                // Looper.prepare();
                // mBanner.animate().alpha(0f).setDuration(500).setStartDelay(500).start();
                // Looper.loop();
                // }
                // },0);

                mStart.setBackgroundResource(R.drawable.gradient_green);
                mStart.setText("准备");
                mStart.setTextColor(Color.WHITE);

                resultDistance = (String) mDistance.getText();
                resultSectionTime = (String) mTotalElapse.getText();
                resultStrokeCount = String.valueOf((int) strokeCount);

                // resultDistance = "2021";
                // resultSectionTime = "06:10.19";
                // resultStrokeCount = "191";

                // demo pic

                mTxResultDistance.setText(resultDistance);
                mTxResultSectionTime.setText(resultSectionTime);
                mTxResultStrokeCount.setText(resultStrokeCount);
                SimpleDateFormat formatterSplitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                mResultSaveTime.setText(formatterSplitTime.format(System.currentTimeMillis()));

                mTotalElapse.stop();
                mTotalElapse.setBase(SystemClock.elapsedRealtime());

                resetUI();

                START_BUTTON_CASE = 0;

                try {
                    loggerPhone.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                loggerPhoneTask.cancel();
                fileSaveNotification();

                break;

        }

    }

    private void updateResult(int activationStatus) {

        if (activationStatus == 0) {

            updateResult.cancel();

        } else {

            Timer timer = new Timer();
            updateResult = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            };
            timer.schedule(updateResult, 0, 1000);
        }

    }

    private void updateResultParams() {

        double boatSpeedResult = Double.parseDouble(UI_params_string[2]);
        double strokeRateResult = Double.parseDouble(UI_params_string[0]);
        double heartRateResult = UI_params_double[0];

    }

    private void updateUI(int activationStatus) {

        if (activationStatus == 0) {

            updateUI_main.cancel();

        } else {
            Timer timer = new Timer();
            updateUI_main = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            updateUI_main(UI_params_float, UI_params_string, UI_params_double);
                            myRefreshRunnable refreshChart;
                            refreshChart = new myRefreshRunnable(chartSPM, Integer.parseInt(UI_params_string[0]));
                            refreshChart.run();

                        }
                    });
                }
            };
            timer.schedule(updateUI_main, 0, 32);
        }
    }

    private void updateUI_main(final float[] UI_params_flt,
            final String[] UI_params_str,
            final double[] UI_params_dbl) {

        // private float[] UI_params_float = new float[]{0f, 0f};
        // // [0]boat_yaw, [1]boat_roll
        // private String[] UI_params_string = new String[]{"0", "0:00", "0.0", "0",
        // "0"};
        // // [0]SPM, [1]Split, [2]speed, [3]hours, [4]distance

        mStrokeRate.setText(UI_params_str[0]);
        mSplit.setText(UI_params_str[1]);
        mSpeed.setText(UI_params_str[2]);
        mDistance.setText(UI_params_str[4]);
        mBoatYaw.setRotation(UI_params_flt[0]);
        mBoatRoll.setRotation(UI_params_flt[1]);
        mBoatOrientation.setRotation(270 - (UI_params_flt[2]));

        animationDisplayHRM(mDisplayHRM4, bpmTxHRM4, UI_params_dbl[3]);
        animationDisplayHRM(mDisplayHRM3, bpmTxHRM3, UI_params_dbl[2]);
        animationDisplayHRM(mDisplayHRM2, bpmTxHRM2, UI_params_dbl[1]);
        animationDisplayHRM(mDisplayHRM1, bpmTxHRM1, UI_params_dbl[0]);

        // mStrokeRate.setText("28");
        // mSplit.setText("1:34");
        // mSpeed.setText("5.6");
        // mDistance.setText("1988");
        //
        // animationDisplayHRM(mDisplayHRM4, bpmTxHRM4, 141);
        // animationDisplayHRM(mDisplayHRM3, bpmTxHRM3, 125);
        // animationDisplayHRM(mDisplayHRM2, bpmTxHRM2, 159);
        // animationDisplayHRM(mDisplayHRM1, bpmTxHRM1, 99);

        // demo pic

        // DecimalFormat HearRateFormatter = new DecimalFormat("0");

        // if (UI_params_dbl[3]!=0) {
        // mDisplayHRM4.setText(HearRateFormatter.format(UI_params_dbl[3]));
        //
        // }else{
        // mDisplayHRM4.setText("--");
        // }
        //
        // if (UI_params_dbl[2]!=0) {
        // mDisplayHRM3.setText(HearRateFormatter.format(UI_params_dbl[2]));
        // }else{
        // mDisplayHRM3.setText("--");
        // }
        // if (UI_params_dbl[1]!=0) {
        // mDisplayHRM2.setText(HearRateFormatter.format(UI_params_dbl[1]));
        // }else{
        // mDisplayHRM2.setText("--");
        // }
        // if (UI_params_dbl[0]!=0) {
        // mDisplayHRM1.setText(HearRateFormatter.format(UI_params_dbl[0]));
        // }else{
        // mDisplayHRM1.setText("--");
        // }

    }

    private void resetUI() {

        strokeCount = 0;
        sectionTotalSec = 0;
        traveledDistance = 0;
        UI_params_double = new double[] { 0, 0, 0, 0 };
        UI_params_float = new float[] { 0f, 0f, 0f };
        UI_params_string = new String[] { "0", "0:00", "0.0", "0", "0" };

        mStrokeRate.setText(UI_params_string[0]);
        mSplit.setText(UI_params_string[1]);
        mSpeed.setText(UI_params_string[2]);
        mSpeed.setText(UI_params_string[2]);
        mDistance.setText(UI_params_string[4]);
        mBoatYaw.setRotation(UI_params_float[0]);
        mBoatRoll.setRotation(UI_params_float[1]);
        mBoatOrientation.setRotation(UI_params_float[2]);

        animationDisplayHRM(mDisplayHRM4, bpmTxHRM4, UI_params_double[3]);
        animationDisplayHRM(mDisplayHRM3, bpmTxHRM3, UI_params_double[2]);
        animationDisplayHRM(mDisplayHRM2, bpmTxHRM2, UI_params_double[1]);
        animationDisplayHRM(mDisplayHRM1, bpmTxHRM1, UI_params_double[0]);

        acclCacheSamples = new double[acclCacheLength];
        acclCachePointer = 0;
        acclCacheSize = 0;
        acclCacheSum = 0.0;

        SRCacheSamples = new double[SRCacheLength];
        SRCachePointer = 0;
        SRCacheSize = 0;
        SRCacheSum = 0.0;

        boatYawCacheSamples = new double[boatYawCacheLength];
        boatYawCachePointer = 0;
        boatYawCacheSize = 0;
        boatYawCacheSum = 0.0;

        boatSpeedCacheSamples = new double[boatSpeedCacheLength];
        boatSpeedCachePointer = 0;
        boatSpeedCacheSize = 0;
        boatSpeedCacheSum = 0.0;

        // compassCacheSamples = new double[compassCacheLength];
        // compassCachePointer = 0;
        // compassCacheSize = 0;
        // compassCacheSum = 0.0;
    }

    private double doubleArrAverage(double[] arr) {
        double sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum / arr.length;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mAccelerometer, 50000);
        mSensorManager.registerListener(mSensorListener, mMagnetic, 50000);
        mSensorManager.registerListener(mSensorListener, mAccelerometerLinear, 50000);
        locationManager.registerGnssStatusCallback(mGnssStatusCallback);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 500, 3, locationListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // mSensorManager.unregisterListener(mSensorListener);
        // locationManager.unregisterGnssStatusCallback(mGnssStatusCallback);
        // locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mSensorListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locationManager.unregisterGnssStatusCallback(mGnssStatusCallback);
        }

        try {

            locationManager.removeUpdates(locationListener);
            updateToDataV.cancel();
            httpURLConnectionUpdate.disconnect();

        } catch (Exception e) {

        }

    }

    public void checkPermission(String[] PermissionString) {
        int targetSdkVersion = 0;

        try {
            final PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;// 获取应用的Target版本
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e("err", "check_access_err0");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                boolean isAllGranted = checkPermissionAllGranted(PermissionString);
                if (isAllGranted) {
                    Log.e("err", "all access granted");
                    return;
                }
                ActivityCompat.requestPermissions(this,
                        PermissionString, 1);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && false == Environment.isExternalStorageManager()) {
            Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            startActivity(new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri));
            ActivityCompat.requestPermissions(this,
                    PermissionString, 1);
        }
    }

    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission_Status", "access" + permission + "not granted");
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            boolean isAllGranted = true;
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                Log.e("Permission_Status", "all access granted");
            } else {

                Log.e("Permission_Status", "partial access not granted, please grand access");

            }
        }
    }

    class SensorListener implements SensorEventListener {

        private static final String TAG = "Sensor";

        @Override
        public void onSensorChanged(SensorEvent event) {

            // TODO Auto-generated method stub
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = event.values;
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = event.values;
            }

            if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                accelerometerLinearValues = event.values;
            }

            calculateVectorData();

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.i(TAG, "onAccuracyChanged");
        }
    }

    private final GnssStatus.Callback mGnssStatusCallback = new GnssStatus.Callback() {

        public void onFirstFix(int ttffMillis) {

            // mGpsStatus.setBackgroundResource(R.drawable.gradient_orange);
            // Toast.makeText(getApplicationContext(), "fix_time:" +
            // ttffMillis,Toast.LENGTH_SHORT).show();

        }

        public void onSatelliteStatusChanged(GnssStatus status) {

            satelliteCount = status.getSatelliteCount();
            // System.out.println("satelliteCount:" + satelliteCount);
            if (satelliteCount < 5) {
                mGpsStatus.setBackgroundResource(R.drawable.gradient_black_2);
            } else if (satelliteCount >= 5 && satelliteCount <= 25) {
                mGpsStatus.setBackgroundResource(R.drawable.gradient_orange);
            } else {
                mGpsStatus.setBackgroundResource(R.drawable.gradient_green);
            }

        }

    };

    private final LocationListener locationListener = new LocationListener() {

        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.i("GPS Status:", "GPS is available");
                    break;

                case LocationProvider.OUT_OF_SERVICE:
                    Log.i("GPS Status:", "GPS is out of service");
                    break;

                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i("GPS Status:", "GPS is unavailable");
                    break;
            }
        }

        @SuppressLint("MissingPermission")
        public void onProviderEnabled(String provider) {

            mLocation = locationManager.getLastKnownLocation(provider);
            latitude_0 = mLocation.getLatitude();
            longitude_0 = mLocation.getLongitude();
            speedLastUpdate = System.currentTimeMillis();

        }

        public void onProviderDisabled(String provider) {
            mLocation = null;

        }

        public void onLocationChanged(Location location) {
            updateToNewLocation(location);
        }
    };

    private void updateToNewLocation(Location location) {

        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        DecimalFormat decimalFormat_1 = new DecimalFormat("0");
        SimpleDateFormat formatterSplitTime = new SimpleDateFormat("HH:mm:ss");

        speedNewUpdate = System.currentTimeMillis();

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            distanceTemp = 0;
            speedCache = 0;
            updateCount = updateCount + 1;

            if (location.hasSpeed() && location.getSpeed() > speedLowLimit && updateCount > initiateLock) {
                try {
                    distanceTemp = getDistance(latitude_0, longitude_0, latitude, longitude);
                    // boatTravelTarget.setText(String.valueOf(distanceTemp));
                    if (distanceTemp > distanceTempHighLimit || distanceTemp < distanceTempLowLimit) {
                        distanceTemp = 0;
                    }

                    traveledDistance = traveledDistance + distanceTemp;
                    String distanceTx = decimalFormat_1.format(traveledDistance);
                    // mDistance.setText(distanceTx);

                    UI_params_string[4] = distanceTx;

                    latitude_0 = latitude;
                    longitude_0 = longitude;
                    speedLastUpdate = speedNewUpdate;

                } catch (Exception e) {
                    latitude_0 = latitude;
                    longitude_0 = longitude;
                    speedCache = 0;
                    distanceTemp = 0;
                }

                if (distanceTemp != 0 && location.getSpeed() > speedLowLimit) {
                    speedCache = location.getSpeed();

                    if (boatSpeedCacheSize < boatSpeedCacheLength) {

                        boatSpeedCacheSamples[boatSpeedCachePointer++] = speedCache;
                        boatSpeedCacheSize++;

                    } else {

                        boatSpeedCachePointer = boatSpeedCachePointer % boatSpeedCacheLength;
                        boatSpeedCacheSum -= boatSpeedCacheSamples[boatSpeedCachePointer];
                        boatSpeedCacheSamples[boatSpeedCachePointer++] = speedCache;
                    }

                    double boatSpeedAvg = doubleArrAverage(boatSpeedCacheSamples);

                    String splitTimeTx;

                    if (boatSpeedAvg <= 0.77) {

                        splitTimeTx = "9:59";
                        mSplitAlert.setBackgroundResource(R.color.colorTransparent);

                    } else {

                        int splitTime = (int) (500 / boatSpeedAvg);
                        int splitTimeForCompare = splitTime;
                        int splitTimeSec = splitTime % 60;
                        splitTime = splitTime - splitTimeSec;
                        int splitTimeMin = splitTime / 60;

                        if (splitTimeSec < 10) {
                            splitTimeTx = splitTimeMin + ":0" + splitTimeSec;
                        } else {
                            splitTimeTx = splitTimeMin + ":" + splitTimeSec;
                        }

                        if (splitTimeForCompare > targetSplit && mStart.getText().equals("停止")) {

                            mSplitAlert.setBackgroundResource(R.color.colorRedDark);

                        } else {

                            mSplitAlert.setBackgroundResource(R.color.colorTransparent);
                        }
                    }

                    // mSplit.setText(splitTimeTx);
                    UI_params_string[1] = splitTimeTx;

                    // speedAvg = traveledDistance/tempTotalSec;
                    // String speedAvgTx = decimalFormat.format(speedAvg);

                    if (speedCache > speedMax) {

                        speedMax = speedCache;
                        String speedMaxTx = decimalFormat.format(speedMax);

                    }

                    // mSpeed.setText(decimalFormat.format(speedCache));
                    UI_params_string[2] = decimalFormat.format(speedCache);
                    speedCache = 0;
                }

            } else {
                // mSpeed.setText("0.0");
                // mSplit.setText("0:00");
                // [0]boat_yaw, [1]boat_roll
                // [0]SPM, [1]Split, [2]speed, [3]hours, [4]distance
                UI_params_string[1] = "0:00";
                UI_params_string[2] = "0.0";

                latitude_0 = latitude;
                longitude_0 = longitude;

            }
        }
    }

    public double getDistance(double lat1, double lon1,
            double lat2, double lon2) {
        float[] results = new float[1];
        try {
            Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results[0];
    }

    private void calculateVectorData() {

        // [0]boat_yaw, [1]boat_roll
        // [0]SPM, [1]Split, [2]speed, [3]hours, [4]distance

        double systemTimeCheck = System.currentTimeMillis();
        if (systemTimeCheck - speedNewUpdate >= 5000) {
            UI_params_string[2] = "0.0";
            UI_params_string[1] = "0:00";
        }
        // reset speed/split to 0 after GPS idle for 5 secs

        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R, values);

        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);

        UI_params_float[1] = -values[1];
        float tempCompass = values[0];

        // if (compassCacheSize < compassCacheLength) {
        //
        // compassCacheSamples[compassCachePointer++] = tempCompass;
        // compassCacheSize ++;
        //
        // }else{
        //
        // compassCachePointer = compassCachePointer % compassCacheLength;
        // compassCacheSum -= compassCacheSamples[compassCachePointer];
        // compassCacheSamples[compassCachePointer++] = tempCompass;
        // }
        // float tempCompassAvg = (float) doubleArrAverage(compassCacheSamples);

        UI_params_float[2] = tempCompass;

        // UI_params_float[2] = values[0];

        float xAcclLinear = accelerometerLinearValues[0];
        float yAcclLinear = accelerometerLinearValues[1];
        float zAcclLinear = accelerometerLinearValues[2];

        double tiltAngleX = values[2] * Math.PI / 180;
        double tiltCosX = Math.cos(tiltAngleX);

        double tiltAngleZ = (90 - Math.abs(values[2])) * Math.PI / 180;
        double tiltCosZ = Math.cos(tiltAngleZ);

        double xAcclActual = -xAcclLinear * tiltCosX;
        double zAcclActual = zAcclLinear * tiltCosZ;

        double boatAcclActualNow = xAcclActual + zAcclActual;
        double boatYawTan = yAcclLinear / boatAcclActualNow;

        float boatYawAngle = 0f;

        double strokeNow = System.currentTimeMillis();
        double strokeGap = strokeNow - strokeCache;

        int tempHourStrokeRateAvg = 0;
        int tempMinStrokeRateAvg = 0;
        String tempSecStrokeRateAvgString = "";
        int tempSecStrokeRateAvg = 0;
        double tempMilliSecStrokeRateAvg = 0;

        if (mTotalElapse.length() <= 7) {
            tempHourStrokeRateAvg = 0;
            tempMinStrokeRateAvg = Integer.parseInt(mTotalElapse.getText().toString().split(":")[0]);
            tempSecStrokeRateAvgString = mTotalElapse.getText().toString().split(":")[1];
            tempSecStrokeRateAvg = Integer.parseInt(tempSecStrokeRateAvgString.split("\\.")[0]);
            tempMilliSecStrokeRateAvg = Integer.parseInt(tempSecStrokeRateAvgString.split("\\.")[1]);

        } else {
            tempHourStrokeRateAvg = Integer.parseInt(mTotalElapse.getText().toString().split(":")[0]);
            tempMinStrokeRateAvg = Integer.parseInt(mTotalElapse.getText().toString().split(":")[1]);
            tempSecStrokeRateAvgString = mTotalElapse.getText().toString().split(":")[2];
            tempSecStrokeRateAvg = Integer.parseInt(tempSecStrokeRateAvgString.split("\\.")[0]);
            tempMilliSecStrokeRateAvg = Integer.parseInt(tempSecStrokeRateAvgString.split("\\.")[1]);

        }

        double tempTotalSecStrokeRateAvg = tempHourStrokeRateAvg * 3600 + tempMinStrokeRateAvg * 60
                + tempSecStrokeRateAvg + tempMilliSecStrokeRateAvg / 10;
        sectionTotalSec = tempTotalSecStrokeRateAvg;
        // System.out.println(tempTotalSecStrokeRateAvg);

        if (acclCacheSize < acclCacheLength) {

            acclCacheSamples[acclCachePointer++] = boatAcclActualNow;
            acclCacheSize++;

        } else {

            acclCachePointer = acclCachePointer % acclCacheLength;
            acclCacheSum -= acclCacheSamples[acclCachePointer];
            acclCacheSamples[acclCachePointer++] = boatAcclActualNow;
        }

        double boatAcclActual = doubleArrAverage(acclCacheSamples);

        // System.out.println("boatAcclActual:" + boatAcclActual);

        if (strokeGap > strokeIdleMax && Double.valueOf((String) mSpeed.getText()) < 1.2) {
            UI_params_string[0] = "0";
        }

        if (boatAcclActual > minBoatAccl) {

            strokeNow = System.currentTimeMillis();
            strokeGap = strokeNow - strokeCache;

            boatYawAngle = (float) (-Math.atan(boatYawTan) * 180 / Math.PI);

            if (START_BUTTON_CASE == 1) {

                mTotalElapse.setBase(SystemClock.elapsedRealtime());
                mTotalElapse.start();
                updateUI(1);
                START_BUTTON_CASE = 2;
                startButtonAction(START_BUTTON_CASE);
            }

            if (boatYawCacheSize < boatYawCacheLength) {

                boatYawCacheSamples[boatYawCachePointer++] = boatYawAngle;
                boatYawCacheSize++;

            } else {

                boatYawCachePointer = boatYawCachePointer % boatYawCacheLength;
                boatYawCacheSum -= boatYawCacheSamples[boatYawCachePointer];
                boatYawCacheSamples[boatYawCachePointer++] = boatYawAngle;
            }

            UI_params_float[0] = (float) doubleArrAverage(boatYawCacheSamples) * yawAdjustRatio;

            if (strokeGap > minStrokeGap && strokeRefreshLock == 0) {

                System.out.println("StrokeGap:" + strokeGap + "    " + "minStrkeGap:" + minStrokeGap);

                strokeCache = strokeNow;
                double strokeRate = 0;

                if (strokeCount < 1) {
                    strokeRate = 0;
                } else {
                    strokeRate = 60000 / strokeGap;
                }

                if (SRCacheSize < SRCacheLength) {

                    SRCacheSamples[SRCachePointer++] = strokeRate;
                    SRCacheSize++;

                } else {

                    SRCachePointer = SRCachePointer % SRCacheLength;
                    SRCacheSum -= SRCacheSamples[SRCachePointer];
                    SRCacheSamples[SRCachePointer++] = strokeRate;
                }

                double strokeRateActual = doubleArrAverage(SRCacheSamples);

                DecimalFormat StrokeRateFormatter = new DecimalFormat("0");
                String strokeRateTx = StrokeRateFormatter.format(strokeRateActual);

                DecimalFormat StrokeCountFormatter = new DecimalFormat("0");
                String strokeCountTx = StrokeCountFormatter.format(strokeCount);

                strokeCount = strokeCount + 1;

                // boatYawAngle = (float)(-Math.atan(boatYawTan) * 180 / Math.PI);

                UI_params_string[0] = strokeRateTx;

                boatYawCacheSamples = new double[boatYawCacheLength];
                boatYawCachePointer = 0;
                boatYawCacheSize = 0;
                boatYawCacheSum = 0.0;

                strokeRefreshLock = 1;

            }

        } else if (boatAcclActual <= strokeRefreshLowerThresh) {

            strokeRefreshLock = 0;

        }

        // mStrokeRate.setText(UI_params_string[0]);
        // mBoatRoll.setRotation(UI_params_float[1]);
        // mBoatYaw.setRotation(UI_params_float[0]);

        // [0]boat_yaw, [1]boat_roll
        // [0]SPM, [1]Split, [2]speed, [3]hours, [4]distance

    }

    private void polarDataRefresh(String id, int hr) {

        for (int i = 0; i < mListBeltsName.size(); i++) {
            if (id.equals(mListBeltsName.get(i))) {

                UI_params_double[i] = hr;
                System.out.println("_________HR BROADCAST_________:" + id + "____HR:____ " + hr);

            }

        }

    }

    private void addHRtoList(String id) {

        if (!mNameListBelt.contains(id) && id != "") {
            mNameListBelt.add(id);
            ArrayAdapter<String> adapter_hr;

            adapter_hr = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner_gray, mNameListBelt);
            adapter_hr.setDropDownViewResource(R.layout.my_drop_down_gray);

            mSpinnerHRM1.setAdapter(adapter_hr);
            mSpinnerHRM2.setAdapter(adapter_hr);
            mSpinnerHRM3.setAdapter(adapter_hr);
            mSpinnerHRM4.setAdapter(adapter_hr);

            mBeltCount.setText(String.valueOf(mNameListBelt.size() - 1));

        }

    }

    private void initPolar() {

        if (scanDisposable == null) {

            scanDisposable = api.searchForDevice()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            polarDeviceInfo
                            // -> Log.d(TAG, "polar device found id: " + polarDeviceInfo.deviceId + "
                            // address: " + polarDeviceInfo.address + " rssi: " + polarDeviceInfo.rssi + "
                            // name: " + polarDeviceInfo.name + " isConnectable: " +
                            // polarDeviceInfo.isConnectable),
                            -> addHRtoList(polarDeviceInfo.getDeviceId()),
                            throwable -> Log.d(TAG, "" + throwable.getLocalizedMessage()),
                            () -> Log.d(TAG, "complete"));

        } else {
            scanDisposable.dispose();
            scanDisposable = null;

        }

    }

    private void mountPolar(int code) {

        if (code == 1) {

            if (broadcastDisposable == null) {

                System.out.println("polar has been mounted");
                broadcastDisposable = api.startListenForPolarHrBroadcasts(null)
                        .subscribe(polarBroadcastData ->
                        // Log.d(TAG, "HR BROADCAST " +
                        // polarBroadcastData.polarDeviceInfo.deviceId + " HR: " +
                        // polarBroadcastData.hr + " batt: " +
                        // polarBroadcastData.batteryStatus),
                        polarDataRefresh(polarBroadcastData.getPolarDeviceInfo().getDeviceId(),
                                polarBroadcastData.getHr()),
                                // polarDataRefresh(polarBroadcastData.polarDeviceInfo.deviceId,
                                // polarBroadcastData.hr),

                                error -> Log.e(TAG, "Broadcast listener failed. Reason " + error),
                                () -> Log.d(TAG, "complete"));
            }

        } else if (code == 0) {

            if (broadcastDisposable != null) {

                System.out.println("polar has been removed");
                broadcastDisposable.dispose();
                broadcastDisposable = null;

            }

        }

        if (broadcastDisposable != null) {

            System.out.println("polar disposable is ACTIVE");

        } else {

            System.out.println("polar disposable is DEACTIVATED");

        }

        if (api != null) {

            System.out.println("Polar API is ACTIVE");

        } else {

            System.out.println("polar disposable is DEACTIVATED");

        }

        // if (broadcastDisposable == null) {
        //
        // broadcastDisposable = api.startListenForPolarHrBroadcasts(null)
        // .subscribe(polarBroadcastData ->
        //// Log.d(TAG, "HR BROADCAST " +
        //// polarBroadcastData.polarDeviceInfo.deviceId + " HR: " +
        //// polarBroadcastData.hr + " batt: " +
        //// polarBroadcastData.batteryStatus),
        // polarDataRefresh(polarBroadcastData.polarDeviceInfo.deviceId,
        // polarBroadcastData.hr),
        // error -> Log.e(TAG, "Broadcast listener failed. Reason " + error),
        // () -> Log.d(TAG, "complete")
        // );
        // } else {
        // broadcastDisposable.dispose();
        // broadcastDisposable = null;
        // }
    }

    private void chartInit(LineChart lineChart, int DataIndex) {

        int[] colors = { getResources().getColor(R.color.colorGreenDark),
                getResources().getColor(R.color.colorOrangeDark),
                getResources().getColor(R.color.colorGrayDark),
                getResources().getColor(R.color.colorRedLight) };

        int[] colors_hr = { getResources().getColor(R.color.colorRedDark),
                Color.YELLOW,
                getResources().getColor(R.color.colorOrangeDark),
                Color.MAGENTA };

        switch (DataIndex) {
            case 0:

                lineChart.getDescription().setEnabled(false);
                lineChart.setTouchEnabled(false);
                lineChart.setDragEnabled(false);
                lineChart.setScaleEnabled(false);
                lineChart.setDrawGridBackground(false);
                lineChart.setPinchZoom(false);
                lineChart.setBackgroundColor(Color.TRANSPARENT);

                LineData data_empty = new LineData();
                data_empty.setValueTextColor(Color.GRAY);
                data_empty.setDrawValues(true);

                lineChart.setData(data_empty);
                lineChart.getAxisRight().setEnabled(false);
                lineChart.setDrawBorders(false);

                Legend legend = lineChart.getLegend();
                legend.setForm(Legend.LegendForm.LINE);
                legend.setEnabled(false);
                legend.setTypeface(tfLight);
                legend.setTextColor(Color.GRAY);
                legend.setWordWrapEnabled(true);
                legend.setTextSize(11f);

                XAxis xl = lineChart.getXAxis();
                xl.setTypeface(tfLight);
                xl.setTextColor(Color.GRAY);
                xl.setDrawGridLines(false);
                xl.enableGridDashedLine(10f, 10f, 0f);
                xl.setAvoidFirstLastClipping(true);
                xl.setTextSize(1f);
                xl.setDrawLabels(false);
                xl.setAxisLineColor(Color.TRANSPARENT);

                YAxis yl = lineChart.getAxisLeft();
                yl.setTypeface(tfLight);
                yl.setTextColor(Color.GRAY);

                List<LimitLine> listLimitLine = chartSPM.getAxisLeft().getLimitLines();
                System.out.println("limitline Size:" + listLimitLine.size());

                for (int i = 0; i < listLimitLine.size(); i++) {
                    chartSPM.getAxisLeft().getLimitLines().remove(i);
                    System.out.println("itr:" + i);
                }

                yl.setAxisLineColor(Color.TRANSPARENT);
                yl.setAxisMaximum(60f);
                yl.setAxisMinimum(0f);
                yl.setDrawGridLines(false);
                yl.setDrawLabels(false);
                LimitLine yLimitLine = new LimitLine(targetSPM);
                yLimitLine.enableDashedLine(10f, 10f, 5f);
                yLimitLine.setLineColor(colors[2]);
                yLimitLine.setLineWidth(8f);
                yl.addLimitLine(yLimitLine);
                LineData dataLive = lineChart.getLineData();
                LineDataSet data = createSet("data", R.color.colorGreenDark);
                dataLive.addDataSet(data);
                break;

            case 1:

                lineChart.getDescription().setEnabled(false);
                lineChart.setTouchEnabled(false);
                lineChart.setDragEnabled(false);
                lineChart.setScaleEnabled(false);
                lineChart.setDrawGridBackground(false);
                lineChart.setPinchZoom(false);
                lineChart.setBackgroundColor(Color.TRANSPARENT);
                lineChart.getAxisRight().setEnabled(false);
                lineChart.setDrawBorders(false);

                Legend legend_1 = lineChart.getLegend();
                legend_1.setForm(Legend.LegendForm.CIRCLE);
                legend_1.setEnabled(false);
                legend_1.setTypeface(tfLight);
                legend_1.setTextColor(Color.GRAY);
                legend_1.setWordWrapEnabled(true);
                legend_1.setTextSize(10f);
                legend_1.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

                XAxis xl_1 = lineChart.getXAxis();
                xl_1.setTypeface(tfLight);
                xl_1.setTextColor(Color.GRAY);
                xl_1.setDrawGridLines(false);
                xl_1.enableGridDashedLine(10f, 10f, 10f);
                xl_1.setAvoidFirstLastClipping(true);
                xl_1.setTextSize(0);
                xl_1.setDrawLabels(false);
                xl_1.setAxisLineColor(Color.GRAY);
                xl_1.setDrawAxisLine(false);

                YAxis yl_1 = lineChart.getAxisLeft();
                yl_1.setTypeface(tfLight);
                yl_1.setTextColor(Color.GRAY);
                yl_1.setAxisLineColor(Color.GRAY);
                yl_1.setAxisMaximum(60f);
                yl_1.setAxisMinimum(0f);
                yl_1.setTextSize(8);
                yl_1.setDrawGridLines(true);
                yl_1.setGridColor(Color.DKGRAY);
                yl_1.setDrawLabels(true);

                List<Entry> list_SPM_time = new ArrayList<>();

                for (int i = 0; i < resultLog.size(); i++) {

                    list_SPM_time.add(
                            new Entry(Float.parseFloat(resultLog.get(i)[0]), Float.parseFloat(resultLog.get(i)[2])));

                }

                LineDataSet lineDataSet_SPM_time = new LineDataSet(list_SPM_time, "SPM");
                lineDataSet_SPM_time.setFillColor(colors[1]);
                lineDataSet_SPM_time.setDrawFilled(true);
                lineDataSet_SPM_time.setFillAlpha(90);
                lineDataSet_SPM_time.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSet_SPM_time.setLineWidth(2f);
                lineDataSet_SPM_time.setValueTextColor(Color.WHITE);
                lineDataSet_SPM_time.setDrawCircles(false);
                lineDataSet_SPM_time.setColor(colors[1]);
                lineDataSet_SPM_time.setDrawValues(false);
                LineData lineData_SPM_time = new LineData(lineDataSet_SPM_time);

                lineChart.setData(lineData_SPM_time);

                break;

            case 2:

                lineChart.getDescription().setEnabled(false);
                lineChart.setTouchEnabled(false);
                lineChart.setDragEnabled(false);
                lineChart.setScaleEnabled(false);
                lineChart.setDrawGridBackground(false);
                lineChart.setPinchZoom(false);
                lineChart.setBackgroundColor(Color.TRANSPARENT);
                lineChart.getAxisRight().setEnabled(false);
                lineChart.setDrawBorders(false);

                Legend legend_2 = lineChart.getLegend();
                legend_2.setForm(Legend.LegendForm.CIRCLE);
                legend_2.setEnabled(false);
                legend_2.setTypeface(tfLight);
                legend_2.setTextColor(Color.GRAY);
                legend_2.setWordWrapEnabled(true);
                legend_2.setTextSize(10f);
                legend_2.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

                XAxis xl_2 = lineChart.getXAxis();
                xl_2.setTypeface(tfLight);
                xl_2.setTextColor(Color.GRAY);
                xl_2.setDrawGridLines(false);
                xl_2.enableGridDashedLine(10f, 10f, 10f);
                xl_2.setAvoidFirstLastClipping(true);
                xl_2.setTextSize(0);
                xl_2.setDrawLabels(false);
                xl_2.setAxisLineColor(Color.GRAY);
                xl_2.setDrawAxisLine(false);

                YAxis yl_2 = lineChart.getAxisLeft();
                yl_2.setTypeface(tfLight);
                yl_2.setTextColor(Color.GRAY);
                yl_2.setAxisLineColor(Color.GRAY);
                yl_2.setAxisMaximum(10f);
                yl_2.setAxisMinimum(0f);
                yl_2.setTextSize(8);
                yl_2.setGridColor(Color.DKGRAY);
                yl_2.setDrawGridLines(true);
                yl_2.setDrawLabels(true);

                List<Entry> list_speed_time = new ArrayList<>();

                for (int i = 0; i < resultLog.size(); i++) {

                    list_speed_time.add(
                            new Entry(Float.parseFloat(resultLog.get(i)[0]), Float.parseFloat(resultLog.get(i)[3])));

                }

                LineDataSet lineDataSet_speed_time = new LineDataSet(list_speed_time, "SPEED");
                lineDataSet_speed_time.setFillColor(colors[0]);
                lineDataSet_speed_time.setDrawFilled(true);
                lineDataSet_speed_time.setFillAlpha(95);
                lineDataSet_speed_time.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSet_speed_time.setLineWidth(2f);
                lineDataSet_speed_time.setValueTextColor(Color.WHITE);
                lineDataSet_speed_time.setDrawCircles(false);
                lineDataSet_speed_time.setColor(colors[0]);
                lineDataSet_speed_time.setDrawValues(false);
                LineData lineData_speed_time = new LineData(lineDataSet_speed_time);
                lineChart.setData(lineData_speed_time);

                break;

            case 3:

                lineChart.getDescription().setEnabled(false);
                lineChart.setTouchEnabled(false);
                lineChart.setDragEnabled(false);
                lineChart.setScaleEnabled(false);
                lineChart.setDrawGridBackground(false);
                lineChart.setPinchZoom(false);
                lineChart.setBackgroundColor(Color.TRANSPARENT);
                lineChart.getAxisRight().setEnabled(false);
                lineChart.setDrawBorders(false);

                Legend legend_3 = lineChart.getLegend();
                legend_3.setForm(Legend.LegendForm.CIRCLE);
                legend_3.setEnabled(true);
                legend_3.setTypeface(tfLight);
                legend_3.setTextColor(Color.GRAY);
                legend_3.setWordWrapEnabled(true);
                legend_3.setTextSize(10f);
                legend_3.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

                XAxis xl_3 = lineChart.getXAxis();
                xl_3.setTypeface(tfLight);
                xl_3.setTextColor(Color.GRAY);
                xl_3.setDrawGridLines(false);
                xl_3.enableGridDashedLine(10f, 10f, 10f);
                xl_3.setAvoidFirstLastClipping(true);
                xl_3.setTextSize(0);
                xl_3.setDrawLabels(false);
                xl_3.setAxisLineColor(Color.GRAY);
                xl_3.setDrawAxisLine(false);

                YAxis yl_3 = lineChart.getAxisLeft();
                yl_3.setTypeface(tfLight);
                yl_3.setTextColor(Color.GRAY);
                yl_3.setAxisLineColor(Color.DKGRAY);
                yl_3.setAxisMaximum(200f);
                yl_3.setAxisMinimum(0f);
                yl_3.setTextSize(8);
                yl_3.setGridColor(Color.DKGRAY);
                yl_3.setDrawGridLines(true);
                yl_3.setDrawLabels(true);

                List<Entry> list_HR_time_1 = new ArrayList<>();
                List<Entry> list_HR_time_2 = new ArrayList<>();
                List<Entry> list_HR_time_3 = new ArrayList<>();
                List<Entry> list_HR_time_4 = new ArrayList<>();

                for (int i = 0; i < resultLog.size(); i++) {

                    list_HR_time_1.add(
                            new Entry(Float.parseFloat(resultLog.get(i)[0]), Float.parseFloat(resultLog.get(i)[6])));
                    list_HR_time_2.add(
                            new Entry(Float.parseFloat(resultLog.get(i)[0]), Float.parseFloat(resultLog.get(i)[7])));
                    list_HR_time_3.add(
                            new Entry(Float.parseFloat(resultLog.get(i)[0]), Float.parseFloat(resultLog.get(i)[8])));
                    list_HR_time_4.add(
                            new Entry(Float.parseFloat(resultLog.get(i)[0]), Float.parseFloat(resultLog.get(i)[9])));

                }

                LineDataSet lineDataSet_HR_time_1 = new LineDataSet(list_HR_time_1, "HR1");
                lineDataSet_HR_time_1.setFillColor(colors_hr[0]);
                lineDataSet_HR_time_1.setDrawFilled(true);
                lineDataSet_HR_time_1.setFillAlpha(95);
                lineDataSet_HR_time_1.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSet_HR_time_1.setLineWidth(2f);
                lineDataSet_HR_time_1.setValueTextColor(Color.WHITE);
                lineDataSet_HR_time_1.setDrawCircles(false);
                lineDataSet_HR_time_1.setColor(colors_hr[0]);
                lineDataSet_HR_time_1.setDrawValues(false);

                LineDataSet lineDataSet_HR_time_2 = new LineDataSet(list_HR_time_2, "HR2");
                lineDataSet_HR_time_2.setFillColor(colors_hr[1]);
                lineDataSet_HR_time_2.setDrawFilled(true);
                lineDataSet_HR_time_2.setFillAlpha(95);
                lineDataSet_HR_time_2.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSet_HR_time_2.setLineWidth(2f);
                lineDataSet_HR_time_2.setValueTextColor(Color.WHITE);
                lineDataSet_HR_time_2.setDrawCircles(false);
                lineDataSet_HR_time_2.setColor(colors_hr[1]);
                lineDataSet_HR_time_2.setDrawValues(false);

                LineDataSet lineDataSet_HR_time_3 = new LineDataSet(list_HR_time_3, "HR3");
                lineDataSet_HR_time_3.setFillColor(colors_hr[2]);
                lineDataSet_HR_time_3.setDrawFilled(true);
                lineDataSet_HR_time_3.setFillAlpha(95);
                lineDataSet_HR_time_3.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSet_HR_time_3.setLineWidth(2f);
                lineDataSet_HR_time_3.setValueTextColor(Color.WHITE);
                lineDataSet_HR_time_3.setDrawCircles(false);
                lineDataSet_HR_time_3.setColor(colors_hr[2]);
                lineDataSet_HR_time_3.setDrawValues(false);

                LineDataSet lineDataSet_HR_time_4 = new LineDataSet(list_HR_time_4, "HR4");
                lineDataSet_HR_time_4.setFillColor(colors_hr[3]);
                lineDataSet_HR_time_4.setDrawFilled(true);
                lineDataSet_HR_time_4.setFillAlpha(95);
                lineDataSet_HR_time_4.setAxisDependency(YAxis.AxisDependency.LEFT);
                lineDataSet_HR_time_4.setLineWidth(2f);
                lineDataSet_HR_time_4.setValueTextColor(Color.WHITE);
                lineDataSet_HR_time_4.setDrawCircles(false);
                lineDataSet_HR_time_4.setColor(colors_hr[3]);
                lineDataSet_HR_time_4.setDrawValues(false);

                LineData lineData_HR_time = new LineData(lineDataSet_HR_time_1, lineDataSet_HR_time_2,
                        lineDataSet_HR_time_3, lineDataSet_HR_time_4);
                lineChart.setData(lineData_HR_time);

                break;

        }

    }

    private LineDataSet createSet(String string, int color) {

        LineDataSet set = new LineDataSet(null, string);

        int[] colors = { getResources().getColor(R.color.colorGreenDark),
                getResources().getColor(R.color.colorOrangeDark) };

        set.setFillColor(colors[0]);
        set.setDrawFilled(true);
        set.setFillAlpha(70);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(1f);
        set.setValueTextColor(Color.GRAY);
        set.setDrawCircles(false);
        set.setColor(ContextCompat.getColor(this, color));
        set.setDrawValues(false);
        return set;
    }

    private void addEntryChart(LineChart lineChart,
            float data_in) {

        LineData data = lineChart.getData();

        if (data != null) {
            LineDataSet data_av_set = (LineDataSet) data.getDataSetByIndex(0);
            data.addEntry(new Entry(data_av_set.getEntryCount(), data_in), 0);

            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(120);
            lineChart.moveViewToX(data_av_set.getEntryCount());

        }

    }

    private void removeEntryChart(LineChart lineChart) {

        LineData data = lineChart.getData();

        if (data != null) {
            LineDataSet data_av_set = (LineDataSet) data.getDataSetByIndex(0);
            data.removeEntry(data_av_set.getEntryCount() - 240, 0);

            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();

        }

    }

    private class myRefreshRunnable implements Runnable {

        private float data_av_run;

        private LineChart lineChart_run;

        public myRefreshRunnable(LineChart _lineChart_run,
                float _data_av_run) {
            this.lineChart_run = _lineChart_run;
            this.data_av_run = _data_av_run;

        }

        @Override
        public void run() {

            addEntryChart(
                    lineChart_run,
                    data_av_run);
            // removeEntryChart(lineChart_run);
        }
    }

    private void showPopup(final PopupWindow popupWindow) {

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);

        View parentView = LayoutInflater.from(RowMonitor.this).inflate(R.layout.activity_row_monitor, null);
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        popupWindow.setFocusable(true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {

                backgroundAlpha(1f);

                if (popupWindow == mWindowConfig) {

                    int[] colors = { getResources().getColor(R.color.colorGreenDark),
                            getResources().getColor(R.color.colorOrangeDark),
                            getResources().getColor(R.color.colorGrayDark) };

                    List<LimitLine> listLimitLine = chartSPM.getAxisLeft().getLimitLines();
                    System.out.println("limitline Size:" + listLimitLine.size());

                    for (int i = 0; i < listLimitLine.size(); i++) {
                        chartSPM.getAxisLeft().getLimitLines().remove(i);
                        System.out.println("itr:" + i);
                    }

                    chartSPM.invalidate();

                    LimitLine yLimitLine = new LimitLine(targetSPM);
                    yLimitLine.enableDashedLine(10f, 10f, 5f);
                    yLimitLine.setLineColor(colors[2]);
                    yLimitLine.setLineWidth(8f);

                    chartSPM.getAxisLeft().addLimitLine(yLimitLine);
                    chartSPM.invalidate();
                    txTargetSPM.setText(String.valueOf(targetSPM));
                    txTargetSplit.setText(String.valueOf(targetMin) + ":" + String.valueOf(targetSec));

                }

            }

        });
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    private void initHR() {

        scannerHR = new BleScanner();
        mDeviceListHR = new ArrayList<>();
        mNameListBelt = new ArrayList<>();

        LiveData<ArrayList<BluetoothDevice>> bluetoothDevices = scannerHR.getBluetoothState();
        bluetoothDevices.observe(RowMonitor.this, new Observer<ArrayList<BluetoothDevice>>() {
            @Override
            public void onChanged(ArrayList<BluetoothDevice> bluetoothDevices) {

                if (bluetoothDevices.size() == 0) {

                    if (mDeviceListHR.size() != 0)
                        mDeviceListHR.clear();
                    System.out.println("no device found");

                } else {
                    mDeviceListHR = bluetoothDevices;
                    mBeltCount.setText(String.valueOf(mDeviceListHR.size()));
                    for (int i = 0; i < mDeviceListHR.size(); ++i) {

                        String addressBelt = bluetoothDevices.get(i).getAddress();
                        String nameBelt = getDeviceName(addressBelt);

                        if (!mNameListBelt.contains(nameBelt)) {
                            mNameListBelt.add(nameBelt);

                            ArrayAdapter<String> adapter_hr;

                            adapter_hr = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner_gray,
                                    mNameListBelt);
                            adapter_hr.setDropDownViewResource(R.layout.my_drop_down_gray);

                            mSpinnerHRM1.setAdapter(adapter_hr);
                            mSpinnerHRM2.setAdapter(adapter_hr);
                            mSpinnerHRM3.setAdapter(adapter_hr);
                            mSpinnerHRM4.setAdapter(adapter_hr);

                        }
                    }
                }

            }
        });
    }

    private void deleteDuplicateBelt(int pos, String selected) {

        for (int i = 0; i < 4; i++) {
            if (i != pos && mSpinnerSelectedBelt.get(i) == selected) {
                mSpinnerListBelt.get(i).setSelection(0);
                mSelectedBeltList.set(i, null);
                System.out.println(mSpinnerSelectedBelt.get(i));
                // crucial change very important
            }
        }
    }

    private String getDeviceName(String address) {

        String name_1 = address.substring(address.length() - 2);
        String name_0 = address.substring(address.length() - 5, address.length() - 3);
        String name = name_0 + name_1;
        return name;
    }

    private BluetoothDevice getAddress(BluetoothDevice belt, String selectedName) {

        for (int i = 0; i < mDeviceListHR.size(); i++) {
            String address = mDeviceListHR.get(i).getAddress();
            String name_1 = address.substring(address.length() - 2);
            String name_0 = address.substring(address.length() - 5, address.length() - 3);
            String name = name_0 + name_1;
            if (name.equals(selectedName)) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                belt = adapter.getRemoteDevice(address);
            }
        }
        return belt;
    }

    private int checkBluetoothValid() {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        int bluetoothStatus = 0;
        if (adapter == null) {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Error")
                    .setMessage("Device is not BLE supported!").create();
            dialog.show();
            bluetoothStatus = 0;
        }

        if (!adapter.isEnabled()) {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Notice")
                    .setMessage("Please turn on Bluetooth and try again.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            if (ActivityCompat.checkSelfPermission(RowMonitor.this,
                                    Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                // ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                // int[] grantResults)
                                // to handle the case where the user grants the permission. See the
                                // documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            startActivityForResult(mIntent, 1);

                        }
                    })
                    .create();
            dialog.show();
            bluetoothStatus = 0;
        }

        if (adapter.isEnabled()) {
            bluetoothStatus = 1;
        }

        return bluetoothStatus;
    }

    private void resetList() {

        for (int i = 0; i < 4; i++) {
            mSelectedBeltList.set(i, null);
            mSpinnerListBelt.get(i).setSelection(0);
        }

    }

    private void connectDeviceHR(int status) {

        if (status == 1) {
            mListBeltsConnect = new ArrayList<>();
            for (int i = 0; i < mSelectedBeltList.size(); ++i) {
                if (mSelectedBeltList.get(i) != null) {
                    BleConnect localConnect = new BleConnect(RowMonitor.this, mSelectedBeltList.get(i));
                    localConnect.Connect();
                    mListBeltsConnect.add(localConnect);
                    initLiveDataObservers(localConnect, i);
                }
            }
        } else {
            System.out.println("total connected HRM:" + mListBeltsConnect.size());
            for (int i = 0; i < mListBeltsConnect.size(); i++) {
                System.out.println("connected HRM:" + mListBeltsConnect.get(i));
                BleConnect localConnect = mListBeltsConnect.get(i);
                localConnect.Disconnect();

            }

        }
    }

    private void initLiveDataObservers(final BleConnect localConnect, final int i) {
        LiveData<String> connectionState = localConnect.getConnectionState();
        connectionState.observe(RowMonitor.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s) {
                    case "STATE_CONNECTED":
                        // Toast.makeText(RowMonitor.this, "Device Connected",
                        // Toast.LENGTH_SHORT).show();
                        mListStatusHRM.get(i).setBackgroundResource(R.drawable.gradient_green);
                        mSuccessConnection++;

                        switch (i) {
                            case 3:
                                mSpinnerHRM4.setEnabled(false);
                                break;
                            case 2:
                                mSpinnerHRM3.setEnabled(false);
                                break;
                            case 1:
                                mSpinnerHRM2.setEnabled(false);
                                break;
                            case 0:
                                mSpinnerHRM1.setEnabled(false);
                                break;
                        }

                        if (mSuccessConnection == mIntentConnection) {
                            mConnectHRM.setBackgroundResource(R.drawable.input_box_6);
                            mConnectHRM.setTextColor(Color.DKGRAY);
                            mConnectHRM.setText("COMPLETE");
                            mConnectHRM.setTextSize(10);
                            mScanHRM.setEnabled(false);
                            mScanHRM.setAlpha(0f);
                            mSpinnerHRM1.setEnabled(false);
                            mSpinnerHRM2.setEnabled(false);
                            mSpinnerHRM3.setEnabled(false);
                            mSpinnerHRM4.setEnabled(false);
                            mConnectHRM.setEnabled(false);

                        }
                        break;
                    case "STATE_CONNECTING":
                        // Toast.makeText(RowMonitor.this, "Device Connecting",
                        // Toast.LENGTH_SHORT).show();
                        mListStatusHRM.get(i).setBackgroundResource(R.drawable.gradient_orange);
                        break;
                    case "STATE_DISCONNECTED":
                        // Toast.makeText(Dashboard.this, "Device Disconnected",
                        // Toast.LENGTH_SHORT).show();
                        mListStatusHRM.get(i).setBackgroundResource(R.color.colorGrayLight);

                        break;
                }
            }
        });

        LiveData<Integer> readingLiveData = localConnect.getReadingLiveData();
        readingLiveData.observe(RowMonitor.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integers) {
                UI_params_double[i] = integers;

            }
        });

    }

    private void animationDisplayHRM(TextView view, TextView bpmView, double hr) {

        DecimalFormat HearRateFormatter = new DecimalFormat("0");

        if (hr == 0) {
            view.setText("--");
            view.setBackgroundResource(R.drawable.input_box);
            view.setTextColor(Color.BLACK);
            bpmView.setTextColor(Color.BLACK);

        } else if (hr < 100 && hr > 0) {

            view.setText(HearRateFormatter.format(hr));
            view.setBackgroundResource(R.drawable.gradient_green);
            view.setTextColor(Color.WHITE);
            bpmView.setTextColor(Color.WHITE);

        } else if (hr < 150 && hr >= 100) {
            view.setText(HearRateFormatter.format(hr));
            view.setBackgroundResource(R.drawable.gradient_orange);
            view.setTextColor(Color.BLACK);
            bpmView.setTextColor(Color.BLACK);

        } else if (hr >= 150) {
            view.setText(HearRateFormatter.format(hr));
            view.setBackgroundResource(R.drawable.gradient_red);
            view.setTextColor(Color.WHITE);
            bpmView.setTextColor(Color.WHITE);

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new androidx.appcompat.app.AlertDialog.Builder(RowMonitor.this).setTitle("是否要退出当前界面?")
                    .setMessage("退出时将结束当前训练。")
                    .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // connectDeviceHR(0);

                            if (updateToDataVTimer != null) {
                                updateToDataVTimer.cancel();
                            }

                            if (updateToDataV != null) {
                                updateToDataV.cancel();
                            }

                            try {

                                mSensorManager.unregisterListener(mSensorListener);
                                mLocationClientGD.stopLocation();
                                mLocationClientGD.onDestroy();

                                if (httpURLConnectionUpdate != null) {
                                    httpURLConnectionUpdate.disconnect();
                                    httpURLConnectionUpdate.getOutputStream().close();

                                }

                            } catch (Exception e) {

                            }

                            finish();
                        }
                    }).setNegativeButton("不退出", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).show();
        }
        return false;
    }

    private void loggerInit() throws IOException {

        String fileLoc = this.getFilesDir() + "/log_RM/";
        createPath(fileLoc);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        double logBegTime = System.currentTimeMillis();
        String refTime = simpleDateFormat.format(logBegTime);
        fileLocPhone = fileLoc + refTime + "_RM.csv";

        loggerPhone_0 = new FileWriter(fileLocPhone);
        loggerPhone = new CSVWriter(loggerPhone_0,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.RFC4180_LINE_END);

        loggerPhone.writeNext(new String[] {
                "SectionTime", "Distance", "StrokeRate", "BoatSpeed",
                "BoatYaw", "BoatRoll",
                "HR1", "HR2", "HR3", "HR4"
        });
    }

    public void phoneLoggerStandalone() {

        // "SectionTime", "Distance", "StrokeRate", "BoatSpeed",
        // "BoatYaw", "BoatRoll",
        // "HR1", "HR2", "HR3", "HR4"

        // private float[] UI_params_float = new float[]{0f, 0f};
        // [0]boat_yaw, [1]boat_roll
        // private String[] UI_params_string = new String[]{"0", "0:00", "0.0", "0",
        // "0"};
        // [0]SPM, [1]Split, [2]speed, [3]hours, [4]distance

        loggerPhoneTimer = new Timer();
        loggerPhoneTask = new TimerTask() {
            @Override
            public void run() {

                if (loggerPhone != null) {

                    try {
                        loggerPhone.writeNext(new String[] {
                                String.valueOf(sectionTotalSec),
                                (String) mDistance.getText(),
                                (String) mStrokeRate.getText(),
                                (String) mSpeed.getText(),
                                String.valueOf(UI_params_float[0]),
                                String.valueOf(UI_params_float[1]),
                                String.valueOf(UI_params_double[0]),
                                String.valueOf(UI_params_double[1]),
                                String.valueOf(UI_params_double[2]),
                                String.valueOf(UI_params_double[3])
                        });

                    } catch (Exception e) {
                        loggerPhone.writeNext(new String[] {
                                String.valueOf(sectionTotalSec),
                                String.valueOf(0),
                                String.valueOf(0),
                                String.valueOf(0),
                                String.valueOf(0),
                                String.valueOf(0),
                                String.valueOf(0),
                                String.valueOf(0),
                                String.valueOf(0),
                                String.valueOf(0)
                        });
                    }

                }
            }

        };

        loggerPhoneTimer.schedule(loggerPhoneTask, 0, 3000);

    }

    public static void createPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private void fileSaveNotification() {

        new androidx.appcompat.app.AlertDialog.Builder(RowMonitor.this).setTitle("你希望保存这次训练的数据吗?")
                .setMessage("未被保存的数据将被删除。")
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        showPopup(mWindowResult);

                        try {
                            resultLog = dataExtraction();
                            chartTimeSPM.clear();
                            chartTimeSpeed.clear();
                            chartTimeHR.clear();
                            chartInit(chartTimeSPM, 1);
                            chartInit(chartTimeSpeed, 2);
                            chartInit(chartTimeHR, 3);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (CsvException e) {
                            e.printStackTrace();
                        }
                        backgroundAlpha(0.2f);

                        return;
                    }
                }).setNegativeButton("不保存", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // showPopup(mWindowResult);
                        // backgroundAlpha(0.2f);

                        // try {
                        // resultLog = dataExtraction();
                        // SimpleDateFormat formatterSplitTime = new SimpleDateFormat("yyyy-MM-dd
                        // HH:mm:ss");
                        // mResultSaveTime.setText(formatterSplitTime.format(System.currentTimeMillis()));
                        // chartTimeSPM.clear();
                        // chartTimeSpeed.clear();
                        // chartTimeHR.clear();
                        // chartInit(chartTimeSPM,1);
                        // chartInit(chartTimeSpeed,2);
                        // chartInit(chartTimeHR,3);
                        //
                        // } catch (IOException e) {
                        // e.printStackTrace();
                        // } catch (CsvException e) {
                        // e.printStackTrace();
                        // }

                        deleteLog(fileLocPhone);
                    }
                }).show();
    }

    private void deleteLog(String fileLocation) {

        File file = new File(fileLocation);
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

    private List<String[]> dataExtraction() throws IOException, CsvException {

        List<String[]> logRM;
        CSVReader reader;
        reader = new CSVReader(new FileReader(fileLocPhone));
        // reader = new CSVReader(new FileReader(this.getFilesDir() + "/log_RM/" +
        // "2021-07-12 11_59_59" + "_RM.csv"));

        // demo pic

        logRM = reader.readAll();
        logRM.remove(0);
        // System.out.println("logRM: ______________" + logRM.get(2)[0]);

        return logRM;

    }

    private static void saveImageToGallery(Bitmap bmp, Activity context) {

        File appDir = new File(getDCIM());
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + getDCIM())));
    }

    private static String getDCIM() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return "";
        }
        String path = Environment.getExternalStorageDirectory().getPath() + "/dcim/";
        if (new File(path).exists()) {
            return path;
        }
        path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/";
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                return "";
            }
        }
        return path;
    }

    public void startConnection() throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 检查URL是否为空，避免崩溃
                    if (updatePATH == null || updatePATH.isEmpty()) {
                        return;
                    }
                    URL updateUrl = new URL(updatePATH);
                    // System.out.println(updateUrl);
                    httpURLConnectionUpdate = (HttpURLConnection) updateUrl.openConnection();
                    httpURLConnectionUpdate.setRequestMethod("POST");
                    httpURLConnectionUpdate.setConnectTimeout(3000);
                    httpURLConnectionUpdate.setDoOutput(true);
                    httpURLConnectionUpdate.setDoInput(true);
                    httpURLConnectionUpdate.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    httpURLConnectionUpdate.setRequestProperty("accept", "application/json");
                    // httpURLConnectionUpdate.setChunkedStreamingMode(0);

                    Map<String, Object> Data = new HashMap<>();
                    Data.put("FieldName", "CacheData");
                    Data.put("Type", "1");
                    Data.put("userName", 0);
                    Data.put("sectionTime", 0);
                    Data.put("SPM", 0);
                    Data.put("boatSpeed", 0);
                    Data.put("actualDistance", 0);
                    Data.put("latitude", 0);
                    Data.put("longitude", 0);
                    Data.put("sectionType", 0);
                    Data.put("playerType", 0);
                    Data.put("boatType", 0);
                    Data.put("targetDistance", 0);
                    Data.put("HR1", 0);
                    Data.put("HR2", 0);
                    Data.put("HR3", 0);
                    Data.put("HR4", 0);
                    Data.put("split", "0:00");

                    String paramsJson = JSON.toJSONString(Data);
                    httpURLConnectionUpdate.setRequestProperty("Content-Length", String.valueOf(paramsJson.length()));

                    OutputStream outputStream = httpURLConnectionUpdate.getOutputStream();
                    outputStream.write(paramsJson.getBytes());

                    InputStream inputStream = httpURLConnectionUpdate.getInputStream();
                    final Map<String, Object> inputStreamMap = JSON.parseObject(inputStream, Map.class);
                    int responseCode = httpURLConnectionUpdate.getResponseCode();
                    // System.out.println("response code HTTP: " + responseCode);
                    // System.out.println("response code API:" + inputStreamMap.get("code"));
                    // System.out.println("response success:" + inputStreamMap.get("success"));
                    // System.out.println("response data:" + inputStreamMap.get("data"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void startUpdate() throws Exception {

        updateToDataV = new TimerTask() {

            @Override
            public void run() {

                try {
                    // 检查URL是否为空，避免崩溃
                    if (updatePATH == null || updatePATH.isEmpty()) {
                        return;
                    }
                    URL updateUrl = new URL(updatePATH);
                    // System.out.println(updateUrl);
                    httpURLConnectionUpdate = (HttpURLConnection) updateUrl.openConnection();
                    httpURLConnectionUpdate.setRequestMethod("POST");
                    httpURLConnectionUpdate.setConnectTimeout(3000);
                    httpURLConnectionUpdate.setDoOutput(true);
                    httpURLConnectionUpdate.setDoInput(true);
                    httpURLConnectionUpdate.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    httpURLConnectionUpdate.setRequestProperty("accept", "application/json");
                    // httpURLConnectionUpdate.setChunkedStreamingMode(0);

                    Map<String, Object> Data = new HashMap<>();
                    Data.put("FieldName", "CacheData");
                    Data.put("Type", "1");
                    Data.put("userName", userName);
                    Data.put("sectionTime", String.valueOf(sectionTotalSec));
                    Data.put("displayTime", mTotalElapse.getText());
                    Data.put("SPM", mStrokeRate.getText());
                    Data.put("boatSpeed", mSpeed.getText());
                    Data.put("actualDistance", mDistance.getText());
                    Data.put("latitude", BigDecimal.valueOf(latitude_0_GD));
                    Data.put("longitude", BigDecimal.valueOf(longitude_0_GD));
                    Data.put("sectionType", "Training");
                    Data.put("playerType", "PRO");
                    Data.put("boatType", "--");
                    Data.put("targetDistance", String.valueOf(20000));
                    Data.put("HR1", UI_params_double[0]);
                    Data.put("HR2", UI_params_double[1]);
                    Data.put("HR3", UI_params_double[2]);
                    Data.put("HR4", UI_params_double[3]);
                    Data.put("split", mSplit.getText());

                    System.out.println(mStrokeRate.getText());

                    String paramsJson = JSON.toJSONString(Data);

                    // System.out.println("sectionTime：" + sectionTimeTX);
                    // System.out.println("displayTime："+ mDisplayTimeTx);

                    int lengthLocal = paramsJson.getBytes().length;
                    httpURLConnectionUpdate.setRequestProperty("Content-Length", String.valueOf(lengthLocal));
                    OutputStream outputStream = httpURLConnectionUpdate.getOutputStream();
                    outputStream.write(paramsJson.getBytes());

                    InputStream inputStream = httpURLConnectionUpdate.getInputStream();
                    final Map<String, Object> inputStreamMap = JSON.parseObject(inputStream, Map.class);
                    int responseCode = httpURLConnectionUpdate.getResponseCode();
                    // System.out.println("response code HTTP: " + responseCode);
                    // System.out.println("response code API:" + inputStreamMap.get("code"));
                    // System.out.println("response success:" + inputStreamMap.get("success"));
                    System.out.println("response data:" + inputStreamMap.get("data"));

                } catch (Exception e) {

                    e.printStackTrace();
                }

            }
        };

        updateToDataVTimer = new Timer();
        updateToDataVTimer.schedule(updateToDataV, 5000, 2000);

    }

}
