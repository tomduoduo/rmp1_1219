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
// CSV操作已被TrainingSessionManager中的DataRecorder替代
import com.polar.sdk.api.PolarBleApi;
import com.polar.sdk.api.PolarBleApiDefaultImpl;
import com.tarek360.instacapture.Instacapture;
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener;
import com.xw.repo.BubbleSeekBar;
import com.motionrivalry.rowmasterpro.utils.TimerManager;
import com.motionrivalry.rowmasterpro.permission.PermissionManager;
import com.motionrivalry.rowmasterpro.ui.RowMonitorViewModel;
import com.motionrivalry.rowmasterpro.training.TrainingSessionManager;
import com.motionrivalry.rowmasterpro.training.TrainingProgress;
import com.motionrivalry.rowmasterpro.training.TrainingSessionData;
import com.motionrivalry.rowmasterpro.sensor.RowingState;
import com.motionrivalry.rowmasterpro.bluetooth.BluetoothDeviceManager;
import androidx.lifecycle.ViewModelProvider;
import com.motionrivalry.rowmasterpro.sensor.SensorData;
import com.motionrivalry.rowmasterpro.sensor.ProcessedData;
import com.motionrivalry.rowmasterpro.sensor.RowingState;
import com.motionrivalry.rowmasterpro.training.TrainingProgress;

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

    // ViewModel
    private RowMonitorViewModel viewModel;

    // 训练会话管理器
    private TrainingSessionManager trainingSessionManager;

    // 蓝牙设备管理器
    private BluetoothDeviceManager bluetoothDeviceManager;

    // UI组件
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

    // CSV记录已被TrainingSessionManager中的DataRecorder替代
    // private FileWriter loggerPhone_0 = null;
    // private CSVWriter loggerPhone = null;
    private double sectionTotalSec = 0;
    // private String fileLocPhone = "";

    private String resultDistance = "";
    private String resultSectionTime = "";
    private String resultStrokeCount = "";

    private TextView mTxResultDistance;
    private TextView mTxResultSectionTime;
    private TextView mTxResultStrokeCount;

    // 数据记录已由TrainingSessionManager自动处理，无需手动管理resultLog

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

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_row_monitor);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        PermissionManager.getInstance().checkDefaultPermissions(this, new PermissionManager.PermissionCallback() {
            @Override
            public void onAllPermissionsGranted() {
                Log.e("Permission_Status", "all access granted");
                // 权限已授予，继续初始化
                initializeAfterPermissions();
            }

            @Override
            public void onPermissionsDenied(List<String> deniedPermissions) {
                Log.e("Permission_Status", "partial access not granted, please grant access: " + deniedPermissions);
                // 处理被拒绝的权限
                handleDeniedPermissions(deniedPermissions);
            }
        });

        // 延迟初始化需要权限的组件
        // api = PolarBleApiDefaultImpl.defaultImplementation(this,
        // PolarBleApi.ALL_FEATURES);
        // api.setPolarFilter(false);

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
                        // 使用BluetoothDeviceManager开始扫描
                        bluetoothDeviceManager.startScan(10000); // 扫描10秒

                        // 重置UI状态
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

                        mTxScanning.setAlpha(1f);
                        mConnectHRM.setEnabled(false);
                        mConnectHRM.setAlpha(0);

                    } else {
                        // 停止扫描
                        bluetoothDeviceManager.stopScan();
                        mTxScanning.setAlpha(0f);
                    }
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
                    // 使用BluetoothDeviceManager连接选中的设备
                    connectSelectedHeartRateDevices();

                    mConnectHRM.setEnabled(false);
                    mConnectHRM.setText("已连接");
                    mTxScanning.setAlpha(0f);
                    mIsScanning = false;
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
                // 准备状态 - 使用TrainingSessionManager开始会话
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
                // 取消准备状态
                mMasterBg.setBackgroundResource(R.drawable.gradient_green);
                mStart.setBackgroundResource(R.drawable.gradient_green);
                mStart.setTextColor(Color.WHITE);
                mBanner.setAlpha(0f);
                mMasterBg.setBackgroundResource(R.drawable.gradient_black_2);
                mStart.setText("准备");
                START_BUTTON_CASE = 0;
                break;

            case 2:
                // 开始训练 - 使用TrainingSessionManager
                sectionCount = 0;
                boatSpeedClassifier = new int[] { 0, 0, 0, 0, 0 };
                strokeRateClassifier = new int[] { 0, 0, 0, 0, 0 };
                heartRateClassifier = new int[] { 0, 0, 0, 0, 0 };

                try {
                    // 使用TrainingSessionManager开始训练会话
                    String sessionName = "训练_" + System.currentTimeMillis();
                    if (trainingSessionManager.startSession(sessionName)) {
                        // 设置训练目标
                        trainingSessionManager.setTargetStrokeRate(targetSPM);
                        trainingSessionManager.setTargetDuration(targetMin * 60 + targetSec);

                        // 开始传感器处理
                        viewModel.startSensorProcessing();

                        // 重置计时器
                        mTotalElapse.setBase(SystemClock.elapsedRealtime());
                        mTotalElapse.start();
                    } else {
                        Log.e("TrainingSession", "开始训练会话失败");
                        return;
                    }
                } catch (Exception e) {
                    Log.e("TrainingSession", "开始训练会话异常", e);
                    return;
                }

                mBanner.setBackgroundResource(R.drawable.gradient_green);
                mMasterBg.setBackgroundResource(R.drawable.gradient_green);
                mBannerTextLeft.setText("开始");
                mBannerTextMid.setText("开始");
                mBannerTextRight.setText("开始");
                mBanner.setAlpha(1f);

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

                updateToDataV.cancel();
                TimerManager.getInstance().cancelTask("updateToDataV");
                TimerManager.getInstance().cancelTask("updateToDataV");

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

                // 训练结果将通过TrainingSessionManager的回调自动处理
                // 无需手动设置result变量，结果会在onSessionCompleted中显示

                // 使用TrainingSessionManager完成训练会话
                if (trainingSessionManager != null && trainingSessionManager.isSessionActive()) {
                    trainingSessionManager.completeSession();
                }

                mTotalElapse.stop();
                mTotalElapse.setBase(SystemClock.elapsedRealtime());

                resetUI();

                START_BUTTON_CASE = 0;

                break;

        }

    }

    private void updateResult(int activationStatus) {

        if (activationStatus == 0) {

            TimerManager.getInstance().cancelTask("updateResult");

        } else {

            TimerManager.getInstance().scheduleAtFixedRate("updateResult", new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 原updateResult任务内容
                        }
                    });
                }
            }, 0, 1000);

        }

    }

    private void updateResultParams() {

        double boatSpeedResult = Double.parseDouble(UI_params_string[2]);
        double strokeRateResult = Double.parseDouble(UI_params_string[0]);
        double heartRateResult = UI_params_double[0];

    }

    private void updateUI(int activationStatus) {

        if (activationStatus == 0) {

            TimerManager.getInstance().cancelTask("updateUI_main");

        } else {
            TimerManager.getInstance().scheduleAtFixedRate("updateUI_main", new Runnable() {
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
            }, 0, 32);
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

        // 使用新的架构管理传感器
        if (viewModel != null) {
            viewModel.startSensorProcessing();
        }

        // 注册GPS监听器（如果已初始化）
        if (locationManager != null && locationListener != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 3, locationListener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mGnssStatusCallback != null) {
                locationManager.registerGnssStatusCallback(mGnssStatusCallback);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 暂停传感器处理
        if (viewModel != null) {
            viewModel.stopSensorProcessing();
        }

        // 移除GPS监听器
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mGnssStatusCallback != null) {
                locationManager.unregisterGnssStatusCallback(mGnssStatusCallback);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 清理ViewModel资源
        if (viewModel != null) {
            viewModel.cleanup();
        }

        // 清理训练会话管理器
        if (trainingSessionManager != null && trainingSessionManager.isSessionActive()) {
            trainingSessionManager.cancelSession();
        }

        // 清理GPS资源
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mGnssStatusCallback != null) {
            locationManager.unregisterGnssStatusCallback(mGnssStatusCallback);
        }

        // 清理其他资源
        try {
            if (updateToDataV != null) {
                updateToDataV.cancel();
            }
            if (httpURLConnectionUpdate != null) {
                httpURLConnectionUpdate.disconnect();
            }
        } catch (Exception e) {
            Log.e("RowMonitor", "清理资源时出错: " + e.getMessage());
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
        // 委托给PermissionManager处理
        PermissionManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 权限授予后的初始化方法
     */
    private void initializeAfterPermissions() {
        // 初始化Polar API
        api = PolarBleApiDefaultImpl.defaultImplementation(this, PolarBleApi.ALL_FEATURES);
        api.setPolarFilter(false);

        // 继续其他初始化工作
        initializeComponents();

        Log.e("Permission_Status", "Application initialized after permissions granted");
    }

    /**
     * 处理被拒绝的权限
     * 
     * @param deniedPermissions 被拒绝的权限列表
     */
    private void handleDeniedPermissions(List<String> deniedPermissions) {
        Log.e("Permission_Status", "Handling denied permissions: " + deniedPermissions);

        // 显示权限被拒绝的提示
        if (!deniedPermissions.isEmpty()) {
            // 可以在这里显示对话框提示用户权限的重要性
            // 或者跳转到应用设置页面

            // 对于关键权限，可能需要终止应用或限制功能
            boolean hasCriticalPermissionDenied = false;
            for (String permission : deniedPermissions) {
                if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) ||
                        permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    hasCriticalPermissionDenied = true;
                    break;
                }
            }

            if (hasCriticalPermissionDenied) {
                // 显示关键权限被拒绝的警告
                showPermissionDeniedWarning(deniedPermissions);
            }
        }
    }

    /**
     * 显示权限被拒绝的警告
     * 
     * @param deniedPermissions 被拒绝的权限列表
     */
    private void showPermissionDeniedWarning(List<String> deniedPermissions) {
        // 这里可以实现显示对话框的逻辑
        // 提示用户这些权限对应用功能的重要性
        Log.e("Permission_Status", "Critical permissions denied: " + deniedPermissions);
    }

    /**
     * 初始化组件（在权限检查之后）
     */
    private void initializeComponents() {
        // 初始化ViewModel
        viewModel = new ViewModelProvider(this).get(RowMonitorViewModel.class);
        viewModel.initialize(this);

        // 初始化训练会话管理器
        trainingSessionManager = new TrainingSessionManager(this, viewModel.getSensorDataProcessor());

        // 初始化蓝牙设备管理器
        bluetoothDeviceManager = new BluetoothDeviceManager(this);

        // 设置蓝牙设备回调
        bluetoothDeviceManager.addCallback(new BluetoothDeviceManager.BluetoothDeviceCallback() {
            @Override
            public void onDeviceDiscovered(BluetoothDeviceManager.DeviceInfo device) {
                Log.i("BluetoothDevice", "发现设备: " + device.deviceName + " (" + device.deviceAddress + ")");

                // 更新UI显示发现的设备
                if (device.deviceType == BluetoothDeviceManager.DeviceType.HEART_RATE_MONITOR) {
                    // 可以在这里更新心率设备列表
                    updateHeartRateDeviceList(device);
                }
            }

            @Override
            public void onDeviceConnected(BluetoothDeviceManager.DeviceInfo device) {
                Log.i("BluetoothDevice", "设备已连接: " + device.deviceName);

                // 更新连接状态UI
                updateHeartRateConnectionStatus(device.deviceAddress, true);
            }

            @Override
            public void onDeviceDisconnected(BluetoothDeviceManager.DeviceInfo device) {
                Log.i("BluetoothDevice", "设备已断开: " + device.deviceName);

                // 更新连接状态UI
                updateHeartRateConnectionStatus(device.deviceAddress, false);
            }

            @Override
            public void onDeviceConnectionFailed(BluetoothDeviceManager.DeviceInfo device, String error) {
                Log.e("BluetoothDevice", "设备连接失败: " + device.deviceName + ", 错误: " + error);

                // 显示连接失败提示
                showErrorMessage("心率设备连接失败: " + error);
            }

            @Override
            public void onHeartRateDataReceived(String deviceId, int heartRate) {
                Log.d("BluetoothDevice", "心率数据: " + heartRate + " bpm from " + deviceId);

                // 处理心率数据
                viewModel.processHeartRateData(deviceId, heartRate);
            }

            @Override
            public void onBatteryLevelReceived(String deviceId, int batteryLevel) {
                Log.d("BluetoothDevice", "电池电量: " + batteryLevel + "% from " + deviceId);

                // 可以显示电池电量
                updateHeartRateBatteryLevel(deviceId, batteryLevel);
            }

            @Override
            public void onSensorDataReceived(String deviceId, String sensorType, Object data) {
                Log.d("BluetoothDevice", "传感器数据: " + sensorType + " from " + deviceId);
            }

            @Override
            public void onScanStarted() {
                Log.i("BluetoothDevice", "蓝牙扫描开始");
                mIsScanning = true;
                mScanHRM.setText("停止");
            }

            @Override
            public void onScanStopped() {
                Log.i("BluetoothDevice", "蓝牙扫描停止");
                mIsScanning = false;
                mScanHRM.setText("扫描");
            }

            @Override
            public void onScanFailed(String error) {
                Log.e("BluetoothDevice", "扫描失败: " + error);
                showErrorMessage("蓝牙扫描失败: " + error);
                mIsScanning = false;
                mScanHRM.setText("扫描");
            }

            @Override
            public void onBluetoothStateChanged(boolean enabled) {
                Log.i("BluetoothDevice", "蓝牙状态改变: " + (enabled ? "启用" : "禁用"));

                if (!enabled) {
                    showErrorMessage("蓝牙已禁用，请启用蓝牙");
                }
            }
        });

        // 设置训练会话回调
        trainingSessionManager.setCallback(new TrainingSessionManager.TrainingSessionCallback() {
            @Override
            public void onSessionStarted(String sessionId) {
                Log.i("TrainingSession", "训练会话已开始: " + sessionId);
                // 可以在这里添加训练开始后的UI更新
            }

            @Override
            public void onSessionPaused() {
                Log.i("TrainingSession", "训练会话已暂停");
                // 更新UI显示暂停状态
            }

            @Override
            public void onSessionResumed() {
                Log.i("TrainingSession", "训练会话已恢复");
                // 更新UI显示恢复状态
            }

            @Override
            public void onSessionCompleted(TrainingSessionData data) {
                Log.i("TrainingSession", "训练会话已完成");
                // 显示训练结果
                showTrainingResults(data);
            }

            @Override
            public void onSessionCancelled() {
                Log.i("TrainingSession", "训练会话已取消");
                // 重置UI状态
                resetTrainingUI();
            }

            @Override
            public void onProgressUpdate(TrainingProgress progress) {
                // 更新训练进度到ViewModel
                viewModel.updateTrainingProgress(progress);
            }

            @Override
            public void onTargetReached(String targetType) {
                Log.i("TrainingSession", "目标已达成: " + targetType);
                // 可以显示目标达成的提示
                showTargetReachedMessage(targetType);
            }

            @Override
            public void onError(String error) {
                Log.e("TrainingSession", "训练会话错误: " + error);
                showErrorMessage(error);
            }
        });

        // 设置UI观察者
        setupLiveDataObservers();

        // 初始化传感器
        initializeSensors();

        // 初始化UI组件
        initializeUIComponents();

        Log.e("Permission_Status", "Initializing components after permissions check");
    }

    /**
     * 设置LiveData观察者
     */
    private void setupLiveDataObservers() {
        // 观察传感器数据
        viewModel.getSensorData().observe(this, sensorData -> {
            // 更新UI显示
            updateSensorDataUI(sensorData);
        });

        // 观察处理后的数据
        viewModel.getProcessedData().observe(this, processedData -> {
            // 更新图表和统计数据
            updateProcessedDataUI(processedData);
        });

        // 观察训练进度
        viewModel.getTrainingProgress().observe(this, trainingProgress -> {
            // 更新训练相关UI
            updateTrainingProgressUI(trainingProgress);
        });

        // 观察划船状态
        viewModel.getRowingState().observe(this, rowingState -> {
            // 更新状态指示器
            updateRowingStateUI(rowingState);
        });

        // 观察错误消息
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // 显示错误提示
                showErrorMessage(errorMessage);
            }
        });
    }

    /**
     * 更新传感器数据UI
     * 
     * @param sensorData 传感器数据
     */
    private void updateSensorDataUI(SensorData sensorData) {
        // 更新原始传感器数据显示
        // 这里可以添加调试用的传感器数据显示
        if (sensorData != null) {
            // 更新加速度计数据
            if (sensorData.accelerometerRaw != null) {
                // 可以在这里显示原始加速度数据
            }

            // 更新磁力计数据
            if (sensorData.magnetometer != null) {
                // 可以在这里显示磁力计数据
            }

            // 更新线性加速度数据
            if (sensorData.linearAcceleration != null) {
                // 可以在这里显示线性加速度数据
            }
        }
    }

    /**
     * 更新处理后数据UI
     * 
     * @param processedData 处理后的数据
     */
    private void updateProcessedDataUI(ProcessedData processedData) {
        // 更新处理后的传感器数据显示
        if (processedData != null) {
            // 更新桨频显示
            if (processedData.processedStrokeRate >= 0) {
                mStrokeRate.setText(String.format("%.0f", processedData.processedStrokeRate));
            }

            // 更新船体姿态
            if (processedData.processedBoatYaw != 0) {
                mBoatYaw.setRotation((float) processedData.processedBoatYaw);
            }

            // 更新速度显示
            if (processedData.processedBoatSpeed >= 0) {
                mSpeed.setText(String.format("%.1f", processedData.processedBoatSpeed));
            }

            // 更新图表数据
            if (processedData.processedStrokeRate >= 0) {
                updateChartData(processedData.processedStrokeRate);
            }
        }
    }

    /**
     * 更新图表数据
     * 
     * @param strokeRate 桨频数据
     */
    private void updateChartData(double strokeRate) {
        // 这里实现图表数据更新逻辑
        // 可以调用现有的addEntryChart方法
        if (chartSPM != null) {
            addEntryChart(chartSPM, (float) strokeRate);
        }
    }

    /**
     * 更新训练进度UI
     * 
     * @param trainingProgress 训练进度
     */
    private void updateTrainingProgressUI(TrainingProgress trainingProgress) {
        // 更新训练进度显示
        if (trainingProgress != null) {
            // 更新总时间
            if (trainingProgress.elapsedTime > 0) {
                long hours = trainingProgress.elapsedTime / 3600000;
                long minutes = (trainingProgress.elapsedTime % 3600000) / 60000;
                long seconds = ((trainingProgress.elapsedTime % 3600000) % 60000) / 1000;

                if (hours > 0) {
                    mTotalElapse.setText(String.format("%d:%02d:%02d", hours, minutes, seconds));
                } else {
                    mTotalElapse.setText(String.format("%02d:%02d", minutes, seconds));
                }
            }

            // 更新总距离
            if (trainingProgress.totalDistance >= 0) {
                mDistance.setText(String.format("%.1f", trainingProgress.totalDistance));
            }

            // 更新平均速度
            if (trainingProgress.averageSpeed >= 0) {
                // 可以显示平均速度
            }

            // 更新平均桨频
            if (trainingProgress.averageStrokeRate >= 0) {
                // 可以显示平均桨频
            }

            // 更新进度条或目标指示器
            if (trainingProgress.hasTargetTime || trainingProgress.hasTargetDistance) {
                // 更新目标进度显示
            }
        }
    }

    /**
     * 更新划船状态UI
     * 
     * @param rowingState 划船状态
     */
    private void updateRowingStateUI(RowingState rowingState) {
        // 更新划船状态显示
        if (rowingState != null) {
            switch (rowingState) {
                case IDLE:
                    // 空闲状态
                    mStartNotification.setText("准备开始划船");
                    break;
                case ROWING:
                    // 划船中
                    mStartNotification.setText("正在划船");
                    break;
                case PAUSED:
                    // 暂停状态
                    mStartNotification.setText("训练已暂停");
                    break;
                case COMPLETED:
                    // 完成状态
                    mStartNotification.setText("训练已完成");
                    break;
                case ERROR:
                    // 错误状态
                    mStartNotification.setText("传感器错误");
                    break;
            }

            // 可以更新状态指示器的颜色或图标
            updateStateIndicator(rowingState);
        }
    }

    /**
     * 更新状态指示器
     * 
     * @param state 划船状态
     */
    private void updateStateIndicator(RowingState state) {
        // 根据状态更新UI指示器
        // 例如改变背景色、图标等
        if (state != null) {
            switch (state) {
                case IDLE:
                    // 绿色背景表示准备就绪
                    break;
                case ROWING:
                    // 蓝色背景表示活跃状态
                    break;
                case PAUSED:
                    // 黄色背景表示暂停
                    break;
                case COMPLETED:
                    // 紫色背景表示完成
                    break;
                case ERROR:
                    // 红色背景表示错误
                    break;
            }
        }
    }

    /**
     * 显示错误消息
     * 
     * @param errorMessage 错误消息
     */
    private void showErrorMessage(String errorMessage) {
        // 这里实现错误消息显示逻辑
        // 例如显示Toast或对话框
        Log.e("RowMonitor", "Error: " + errorMessage);
    }

    /**
     * 初始化传感器
     */
    private void initializeSensors() {
        // 使用ViewModel的传感器处理器
        viewModel.startSensorProcessing();

        // 初始化GPS
        initializeGPS();

        // 初始化蓝牙心率监测
        initializeBluetoothHeartRate();
    }

    /**
     * 初始化GPS
     */
    @SuppressLint("MissingPermission")
    private void initializeGPS() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 请求位置更新（使用现有的监听器）
        if (locationManager != null && locationListener != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 3, locationListener);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && mGnssStatusCallback != null) {
                locationManager.registerGnssStatusCallback(mGnssStatusCallback);
            }
        }
    }

    /**
     * 初始化蓝牙心率监测
     */
    private void initializeBluetoothHeartRate() {
        // 初始化Polar API
        api = PolarBleApiDefaultImpl.defaultImplementation(this, PolarBleApi.ALL_FEATURES);
        api.setPolarFilter(false);

        // 初始化心率设备列表
        mNameListBelt = new ArrayList<>();
        mNameListBelt.add("None");

        // 初始化心率监听器
        initPolar();
    }

    /**
     * 初始化UI组件
     */
    private void initializeUIComponents() {
        // 这里实现UI组件初始化逻辑
        // 可以逐步迁移现有的UI初始化代码
    }

    /**
     * 显示训练结果
     * 
     * @param data 训练会话数据
     */
    private void showTrainingResults(TrainingSessionData data) {
        if (data != null) {
            // 显示训练结果弹窗
            showPopup(mWindowResult);
            backgroundAlpha(0.2f);

            // 更新结果数据
            mTxResultDistance.setText(String.format("%.1f", data.totalDistance / 1000.0)); // 转换为公里
            mTxResultSectionTime.setText(formatDuration(data.totalDuration));
            mTxResultStrokeCount.setText(String.valueOf(data.strokeCount));

            // 可以添加更多统计信息的显示
        }
    }

    /**
     * 重置训练UI
     */
    private void resetTrainingUI() {
        // 重置UI状态到初始状态
        START_BUTTON_CASE = 0;
        mStart.setText("准备");
        mStart.setBackgroundResource(R.drawable.gradient_orange);
        mStart.setTextColor(Color.BLACK);
        mMasterBg.setBackgroundResource(R.drawable.gradient_black_2);
        mBanner.setVisibility(View.GONE);

        // 重置计时器
        mTotalElapse.setBase(SystemClock.elapsedRealtime());

        // 清空图表
        if (chartSPM != null) {
            chartSPM.clear();
            chartSPM.invalidate();
        }
    }

    /**
     * 显示目标达成消息
     * 
     * @param targetType 目标类型
     */
    private void showTargetReachedMessage(String targetType) {
        String message = "";
        switch (targetType) {
            case "duration":
                message = "目标时间已达成！";
                break;
            case "distance":
                message = "目标距离已达成！";
                break;
            case "strokeRate":
                message = "目标桨频已达成！";
                break;
            default:
                message = "目标已达成！";
                break;
        }

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 格式化持续时间
     * 
     * @param duration 持续时间（毫秒）
     * @return 格式化的时间字符串
     */
    private String formatDuration(long duration) {
        long hours = duration / 3600000;
        long minutes = (duration % 3600000) / 60000;
        long seconds = ((duration % 3600000) % 60000) / 1000;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    /**
     * 更新心率设备列表
     * 
     * @param device 蓝牙设备信息
     */
    private void updateHeartRateDeviceList(BluetoothDeviceManager.DeviceInfo device) {
        // 这里可以实现更新心率设备列表的逻辑
        // 可以更新Spinner或其他UI组件
        Log.d("HeartRateDevice", "发现心率设备: " + device.deviceName);
    }

    /**
     * 更新心率设备连接状态
     * 
     * @param deviceAddress 设备地址
     * @param isConnected   是否已连接
     */
    private void updateHeartRateConnectionStatus(String deviceAddress, boolean isConnected) {
        // 这里可以实现更新连接状态UI的逻辑
        Log.d("HeartRateDevice", "设备连接状态改变: " + deviceAddress + " - " + (isConnected ? "已连接" : "已断开"));
    }

    /**
     * 更新心率设备电池电量
     * 
     * @param deviceId     设备ID
     * @param batteryLevel 电池电量
     */
    private void updateHeartRateBatteryLevel(String deviceId, int batteryLevel) {
        // 这里可以实现显示电池电量的逻辑
        Log.d("HeartRateDevice", "设备电池电量: " + deviceId + " - " + batteryLevel + "%");
    }

    /**
     * 连接选中的心率设备
     * 使用BluetoothDeviceManager连接用户选中的心率设备
     */
    private void connectSelectedHeartRateDevices() {
        Log.i("BluetoothDevice", "开始连接选中的心率设备");

        // 重置连接状态
        mSuccessConnection = 0;
        mIntentConnection = 0;
        mListBeltsConnect = new ArrayList<>();

        // 首先计算需要连接的设备数量
        for (int i = 0; i < mSelectedBeltList.size(); i++) {
            if (mSelectedBeltList.get(i) != null) {
                mIntentConnection++;
            }
        }

        if (mIntentConnection == 0) {
            Log.w("BluetoothDevice", "没有选中任何心率设备");
            return;
        }

        // 设置BluetoothDeviceManager回调
        bluetoothDeviceManager.addCallback(new BluetoothDeviceManager.BluetoothDeviceCallback() {
            @Override
            public void onDeviceDiscovered(BluetoothDeviceManager.DeviceInfo device) {
                // 扫描发现设备时调用，这里不需要处理
            }

            @Override
            public void onDeviceConnected(BluetoothDeviceManager.DeviceInfo device) {
                Log.i("BluetoothDevice", "设备已连接: " + device.deviceName + " (" + device.deviceAddress + ")");

                // 更新UI状态
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateConnectedDeviceUI(device);
                    }
                });
            }

            @Override
            public void onDeviceDisconnected(BluetoothDeviceManager.DeviceInfo device) {
                Log.i("BluetoothDevice", "设备已断开: " + device.deviceName + " (" + device.deviceAddress + ")");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDisconnectedDeviceUI(device);
                    }
                });
            }

            @Override
            public void onDeviceConnectionFailed(BluetoothDeviceManager.DeviceInfo device, String error) {
                Log.e("BluetoothDevice", "设备连接失败: " + device.deviceName + " (" + device.deviceAddress + ") - " + error);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateConnectionFailedUI(device);
                    }
                });
            }

            @Override
            public void onHeartRateDataReceived(String deviceId, int heartRate) {
                Log.d("BluetoothDevice", "收到心率数据 - 设备: " + deviceId + ", 心率: " + heartRate);

                // 更新心率数据到UI_params_double数组
                updateHeartRateData(deviceId, heartRate);
            }

            @Override
            public void onBatteryLevelReceived(String deviceId, int batteryLevel) {
                Log.d("BluetoothDevice", "收到电池电量 - 设备: " + deviceId + ", 电量: " + batteryLevel);
            }

            @Override
            public void onSensorDataReceived(String deviceId, String sensorType, Object data) {
                // 其他传感器数据，这里不需要处理
            }

            @Override
            public void onScanStarted() {
                // 扫描开始，这里不需要处理
            }

            @Override
            public void onScanStopped() {
                // 扫描停止，这里不需要处理
            }

            @Override
            public void onScanFailed(String error) {
                Log.e("BluetoothDevice", "扫描失败: " + error);
            }

            @Override
            public void onBluetoothStateChanged(boolean enabled) {
                Log.i("BluetoothDevice", "蓝牙状态改变: " + (enabled ? "启用" : "禁用"));
            }
        });

        // 连接选中的设备
        for (int i = 0; i < mSelectedBeltList.size(); i++) {
            BluetoothDevice device = mSelectedBeltList.get(i);
            if (device != null) {
                Log.i("BluetoothDevice",
                        "连接设备 " + (i + 1) + ": " + device.getName() + " (" + device.getAddress() + ")");
                bluetoothDeviceManager.connectToDevice(device.getAddress());
            }
        }
    }

    /**
     * 更新心率数据
     * 将接收到的心率数据更新到UI_params_double数组中
     */
    private void updateHeartRateData(String deviceAddress, int heartRate) {
        // 根据设备地址找到对应的索引
        for (int i = 0; i < mSelectedBeltList.size(); i++) {
            if (mSelectedBeltList.get(i) != null &&
                    mSelectedBeltList.get(i).getAddress().equals(deviceAddress)) {
                UI_params_double[i] = heartRate;
                Log.d("BluetoothDevice", "更新心率数据 - 设备索引: " + i + ", 心率: " + heartRate);
                break;
            }
        }
    }

    /**
     * 更新已连接设备的UI状态
     */
    private void updateConnectedDeviceUI(BluetoothDeviceManager.DeviceInfo device) {
        // 根据设备地址找到对应的索引
        for (int i = 0; i < mSelectedBeltList.size(); i++) {
            if (mSelectedBeltList.get(i) != null &&
                    mSelectedBeltList.get(i).getAddress().equals(device.deviceAddress)) {

                // 更新状态指示器
                mListStatusHRM.get(i).setBackgroundResource(R.drawable.gradient_green);
                mSuccessConnection++;

                // 禁用对应的下拉选择器
                switch (i) {
                    case 0:
                        mSpinnerHRM1.setEnabled(false);
                        break;
                    case 1:
                        mSpinnerHRM2.setEnabled(false);
                        break;
                    case 2:
                        mSpinnerHRM3.setEnabled(false);
                        break;
                    case 3:
                        mSpinnerHRM4.setEnabled(false);
                        break;
                }

                // 如果所有设备都已连接，更新连接按钮状态
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

                Log.i("BluetoothDevice", "设备 " + (i + 1) + " 连接成功，当前成功连接数: " + mSuccessConnection);
                break;
            }
        }
    }

    /**
     * 更新断开连接设备的UI状态
     */
    private void updateDisconnectedDeviceUI(BluetoothDeviceManager.DeviceInfo device) {
        for (int i = 0; i < mSelectedBeltList.size(); i++) {
            if (mSelectedBeltList.get(i) != null &&
                    mSelectedBeltList.get(i).getAddress().equals(device.deviceAddress)) {

                // 更新状态指示器为灰色
                mListStatusHRM.get(i).setBackgroundResource(R.color.colorGrayLight);

                Log.i("BluetoothDevice", "设备 " + (i + 1) + " 已断开连接");
                break;
            }
        }
    }

    /**
     * 更新连接失败的UI状态
     */
    private void updateConnectionFailedUI(BluetoothDeviceManager.DeviceInfo device) {
        for (int i = 0; i < mSelectedBeltList.size(); i++) {
            if (mSelectedBeltList.get(i) != null &&
                    mSelectedBeltList.get(i).getAddress().equals(device.deviceAddress)) {

                // 更新状态指示器为橙色（表示连接失败）
                mListStatusHRM.get(i).setBackgroundResource(R.drawable.gradient_orange);

                Log.i("BluetoothDevice", "设备 " + (i + 1) + " 连接失败");
                break;
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
            // 通过ViewModel处理位置数据
            if (viewModel != null) {
                viewModel.processLocationData(location);
            }
            // 保留现有的位置处理逻辑
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
        // 通过ViewModel处理心率数据
        if (viewModel != null) {
            viewModel.processHeartRateData(id, hr);
        }

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
                // 简化图表初始化，移除CSV数据依赖
                // 后续可通过TrainingSessionManager获取历史数据进行图表展示
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

                // 暂时使用空数据，后续从TrainingSessionManager获取历史数据
                LineData data_empty_spm = new LineData();
                lineChart.setData(data_empty_spm);

                break;

            case 2:
                // 简化图表初始化，移除CSV数据依赖
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

                // 暂时使用空数据，后续从TrainingSessionManager获取历史数据
                LineData data_empty_speed = new LineData();
                lineChart.setData(data_empty_speed);

                break;

            case 3:
                // 简化图表初始化，移除CSV数据依赖
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

                // 暂时使用空数据，后续从TrainingSessionManager获取历史数据
                LineData data_empty_hr = new LineData();
                lineChart.setData(data_empty_hr);

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

                            if (updateToDataV != null) {
                                updateToDataV.cancel();
                            }
                            TimerManager.getInstance().cancelTask("updateToDataV");

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

    // CSV数据记录已被TrainingSessionManager中的DataRecorder替代
    // 所有传感器数据将自动记录，无需手动管理CSV文件

    public static void createPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    // 数据保存通知已被TrainingSessionManager替代
    // TrainingSessionManager会自动处理数据记录和保存
    private void fileSaveNotification() {
        // 直接显示训练结果，无需手动处理CSV文件
        showPopup(mWindowResult);
        backgroundAlpha(0.2f);

        // 初始化结果图表
        chartTimeSPM.clear();
        chartTimeSpeed.clear();
        chartTimeHR.clear();
        chartInit(chartTimeSPM, 1);
        chartInit(chartTimeSpeed, 2);
        chartInit(chartTimeHR, 3);
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

        TimerManager.getInstance().scheduleAtFixedRate("updateToDataV", updateToDataV, 5000, 2000);

    }

}
