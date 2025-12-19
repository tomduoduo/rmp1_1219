
package com.motionrivalry.rowmasterpro;

import static com.motionrivalry.rowmasterpro.MainActivity.isPad;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.AMapWrapper;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.kyleduo.switchbutton.SwitchButton;
import com.motionrivalry.rowmasterpro.UtilsBle.BleConnect;
import com.opencsv.CSVWriter;
import com.xsens.dot.android.sdk.XsensDotSdk;
import com.xsens.dot.android.sdk.events.XsensDotData;
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCallback;
import com.xsens.dot.android.sdk.models.FilterProfileInfo;
import com.xsens.dot.android.sdk.models.XsensDotDevice;
import com.xsens.dot.android.sdk.models.XsensDotPayload;
import com.xsens.dot.android.sdk.utils.XsensDotLogger;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import io.reactivex.rxjava3.disposables.Disposable;
import ng.max.slideview.SlideView;

import com.polar.sdk.api.PolarBleApi;
import com.polar.sdk.api.PolarBleApiDefaultImpl;

//import polar.com.sdk.api.PolarBleApi;
//import polar.com.sdk.api.PolarBleApiDefaultImpl;

public class Dashboard extends AppCompatActivity implements XsensDotDeviceCallback {

    private int loadControl = 2;
    private int samplingRate = 20;
    private int samplingRateLowRefresh = 20;

    private Thread threadConnectDots;
    private int mXsensDeviceListIndex = 0;
    private boolean suspended = false;
    private Thread threadConnectHRM;
    private int HeartRateMonitorListIndex = 0;
    private boolean suspendedHRM = false;
    private int mHRfirstConnect = 0;

    private BluetoothDevice DotL1;
    private BluetoothDevice DotL2;
    private BluetoothDevice DotL3;
    private BluetoothDevice DotL4;
    private BluetoothDevice DotR1;
    private BluetoothDevice DotR2;
    private BluetoothDevice DotR3;
    private BluetoothDevice DotR4;
    private BluetoothDevice DotBOAT;

    private TextView mDotL1;
    private TextView mDotL2;
    private TextView mDotL3;
    private TextView mDotL4;
    private TextView mDotR1;
    private TextView mDotR2;
    private TextView mDotR3;
    private TextView mDotR4;
    private TextView mDotBOAT;

    private FrameLayout mFrame_L1;
    private FrameLayout mFrame_L2;
    private FrameLayout mFrame_L3;
    private FrameLayout mFrame_L4;
    private FrameLayout mFrame_R1;
    private FrameLayout mFrame_R2;
    private FrameLayout mFrame_R3;
    private FrameLayout mFrame_R4;

    private int numOfSelectedDots = 0;
    private int numOfConnectedDots = 0;
    private Button mBtnReconnect;
    private Button mBtnResetHeading;
    private Button mBtnActivate;
    private Button mBtnDeactivate;

    private ArrayList<BluetoothDevice> mPassThroughList = new ArrayList<>();
    private ArrayList<TextView> mTvPassThroughNameList = new ArrayList<>();
    private ArrayList<XsensDotDevice> mXsensDeviceList = new ArrayList<>();
    private ArrayList<String> mPassThroughListStr = new ArrayList<>();

    private XsensDotDevice xsensDotL1;
    private XsensDotDevice xsensDotL2;
    private XsensDotDevice xsensDotL3;
    private XsensDotDevice xsensDotL4;
    private XsensDotDevice xsensDotR1;
    private XsensDotDevice xsensDotR2;
    private XsensDotDevice xsensDotR3;
    private XsensDotDevice xsensDotR4;
    private XsensDotDevice xsensDotBOAT;

    private int activationCode = 1;
    private int activationStatus = 0;
    private int HeavyPayloadMode = XsensDotPayload.PAYLOAD_TYPE_CUSTOM_MODE_1;
    private int LightPayloadMode = XsensDotPayload.PAYLOAD_TYPE_ORIENTATION_EULER;
    private int payloadMode;

    private ImageView mOarL1;
    private ImageView mOarL2;
    private ImageView mOarL3;
    private ImageView mOarL4;

    private ImageView mOarR1;
    private ImageView mOarR2;
    private ImageView mOarR3;
    private ImageView mOarR4;

    private ImageView mOarL1_roll;
    private ImageView mOarL2_roll;
    private ImageView mOarL3_roll;
    private ImageView mOarL4_roll;

    private ImageView mOarR1_roll;
    private ImageView mOarR2_roll;
    private ImageView mOarR3_roll;
    private ImageView mOarR4_roll;

    private ImageView mOarL1_pitch;
    private ImageView mOarL2_pitch;
    private ImageView mOarL3_pitch;
    private ImageView mOarL4_pitch;

    private ImageView mOarR1_pitch;
    private ImageView mOarR2_pitch;
    private ImageView mOarR3_pitch;
    private ImageView mOarR4_pitch;

    private FrameLayout mFrame_L1_roll;
    private FrameLayout mFrame_L2_roll;
    private FrameLayout mFrame_L3_roll;
    private FrameLayout mFrame_L4_roll;
    private FrameLayout mFrame_R1_roll;
    private FrameLayout mFrame_R2_roll;
    private FrameLayout mFrame_R3_roll;
    private FrameLayout mFrame_R4_roll;

    double boatAngle = 0;
    double correctionLeft = 90;
    double correctionRight = 90;
    double correctionPeddle = 135;
    double correctionRightPitch = 0;

    private TextView mDegreeL1fwd;
    private TextView mDegreeL2fwd;
    private TextView mDegreeL3fwd;
    private TextView mDegreeL4fwd;

    private TextView mDegreeL1bwd;
    private TextView mDegreeL2bwd;
    private TextView mDegreeL3bwd;
    private TextView mDegreeL4bwd;

    private TextView mDegreeR1fwd;
    private TextView mDegreeR2fwd;
    private TextView mDegreeR3fwd;
    private TextView mDegreeR4fwd;

    private TextView mDegreeR1bwd;
    private TextView mDegreeR2bwd;
    private TextView mDegreeR3bwd;
    private TextView mDegreeR4bwd;

    private TextView mDegreeL1;
    private TextView mDegreeL2;
    private TextView mDegreeL3;
    private TextView mDegreeL4;

    private TextView mDegreeR1;
    private TextView mDegreeR2;
    private TextView mDegreeR3;
    private TextView mDegreeR4;

    private FrameLayout mBottomBar;

    private int maxAngularVelocity = 80;
    private int minAngularVelocity = -360;

    private double[] UI_params_L1 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0 };
    private double[] UI_params_L2 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0 };
    private double[] UI_params_L3 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0 };
    private double[] UI_params_L4 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0 };

    private double[] UI_params_R1 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0 };
    private double[] UI_params_R2 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0 };
    private double[] UI_params_R3 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0 };
    private double[] UI_params_R4 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0 };

    private ArrayList<double[]> UI_params_set = new ArrayList<>();

    // {yaw[0], txYaw[1], avMin[2], avMax[3], txDegreeFwd[4], txDegreeBwd[5],
    // Roll[6], Pitch[7], YawMin[8], YawMax[9], YawAbs[10],
    // YawBeg[11], YawEnd[12], YawDuration[13], DynamicData[14],
    // StrokeVelocityLast[15]}
    // StrokeTimeLast[16], instantBoatSpeed[17], autoSplitCorrect[18], wattage[19]}
    // original Euler_X, original Euler_Y, original Euler_Z

    private float[] acclSensorValues = new float[3];
    private float[] magSensorValues = new float[3];
    private float[] acclLinearSensorValues = new float[3];

    private SensorManager mSensorManager;
    private Sensor mAcclSensor;
    private Sensor mAcclLinearSensor;
    private Sensor mMagSensor;
    private phoneSensorListener mSensorListener;

    private Button mStart;
    private Button mStop;
    private TextView mStrokeRate;
    private TextView mBoatSpeed;

    private double minStrokeGap = 1200;
    // private double minBoatAccl = 1.4;
    private double minBoatAccl = 1.2;
    private double strokeCount = 0;
    private double strokeRateAvg = 0;
    private double strokeIdleMax = 10000;
    private double strokeRefreshLowerThresh = -1.2;

    double strokeCache;
    private String mDisplayTimeTx;

    private Chronometer totalElapse;

    private int acclCacheLength = 10;
    private double[] acclCacheSamples = new double[acclCacheLength];
    private int acclCachePointer = 0;
    private int acclCacheSize = 0;
    private double acclCacheSum = 0.0;

    private int SRCacheLength = 4;
    private double[] SRCacheSamples = new double[SRCacheLength];
    private int SRCachePointer = 0;
    private int SRCacheSize = 0;
    private double SRCacheSum = 0.0;

    private int boatSpeedCacheLength = 10;
    private double[] boatSpeedCacheSamples = new double[boatSpeedCacheLength];
    private int boatSpeedCachePointer = 0;
    private int boatSpeedCacheSize = 0;
    private double boatSpeedCacheSum = 0.0;

    private float yawAdjustRatio = 1.4f;
    private int boatYawCacheLength = 20;
    private double[] boatYawCacheSamples = new double[boatYawCacheLength];
    private int boatYawCachePointer = 0;
    private int boatYawCacheSize = 0;
    private double boatYawCacheSum = 0.0;

    private int boatRollCacheLength = 20;
    private double[] boatRollCacheSamples = new double[boatYawCacheLength];
    private int boatRollCachePointer = 0;
    private int boatRollCacheSize = 0;
    private double boatRollCacheSum = 0.0;

    private String[] UI_params_Secondary_str = new String[] { "0.0", "0:00" }; // spm, boat speed
    private float[] UI_params_Secondary_float = new float[] { 0f, 0f }; // boat_yaw, boat_roll
    private Integer[] HR_params_cache = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0 };

    private int mStartStatus = 0;

    private ImageView mBoatRoll;
    private ImageView mBoatYaw;

    // private GifImageView mRowingAnimation;

    private AMapLocationClient mLocationClientGD = null;
    private AMapLocationListener mLocationListenerGD = null;
    private AMapLocationClientOption mLocationOptionGD = null;

    private double latitude_0_GD;
    private double longitude_0_GD;
    private double latitude_0_GD_last;
    private double longitude_0_GD_last;
    private float boatSpeed_0_GD;
    private float mDistance = 0;
    private String mDistanceTx = "0";

    private String uploadPATH = "";
    private String updatePATH = "";

    private HttpURLConnection httpURLConnectionUpdate;
    private TimerTask updateTask;
    private Timer updateTimer;
    private String sectionTimeTX = "0.0";
    private String fileLoc = null;

    private XsensDotLogger loggerL1 = null;
    private XsensDotLogger loggerL2 = null;
    private XsensDotLogger loggerL3 = null;
    private XsensDotLogger loggerL4 = null;
    private XsensDotLogger loggerR1 = null;
    private XsensDotLogger loggerR2 = null;
    private XsensDotLogger loggerR3 = null;
    private XsensDotLogger loggerR4 = null;
    private XsensDotLogger loggerBoat = null;

    private FileWriter loggerPhone_0 = null;
    private CSVWriter loggerPhone = null;

    private FileWriter loggerL1_sub_FileWriter = null;
    private CSVWriter loggerL1_sub_CsvWriter = null;

    private FileWriter loggerL2_sub_FileWriter = null;
    private CSVWriter loggerL2_sub_CsvWriter = null;

    private FileWriter loggerL3_sub_FileWriter = null;
    private CSVWriter loggerL3_sub_CsvWriter = null;

    private FileWriter loggerL4_sub_FileWriter = null;
    private CSVWriter loggerL4_sub_CsvWriter = null;

    private FileWriter loggerR1_sub_FileWriter = null;
    private CSVWriter loggerR1_sub_CsvWriter = null;

    private FileWriter loggerR2_sub_FileWriter = null;
    private CSVWriter loggerR2_sub_CsvWriter = null;

    private FileWriter loggerR3_sub_FileWriter = null;
    private CSVWriter loggerR3_sub_CsvWriter = null;

    private FileWriter loggerR4_sub_FileWriter = null;
    private CSVWriter loggerR4_sub_CsvWriter = null;

    private FileWriter loggerBoat_sub_FileWriter = null;
    private CSVWriter loggerBoat_sub_CsvWriter = null;

    private ArrayList<CSVWriter> loggerList_sub = new ArrayList<>();
    private ArrayList<XsensDotLogger> loggerList = new ArrayList<>();
    private ArrayList<String> loggerPathList = new ArrayList<>();
    private int packetCounter = 0;
    private TimerTask loggerPhoneTask;
    private Timer loggerPhoneTimer;

    private BleConnect bleConnectHR;
    private BluetoothDevice deviceHR;
    private TextView mHR_L1;

    private BluetoothDevice Belt1;
    private BluetoothDevice Belt2;
    private BluetoothDevice Belt3;
    private BluetoothDevice Belt4;
    private BluetoothDevice Belt5;
    private BluetoothDevice Belt6;
    private BluetoothDevice Belt7;
    private BluetoothDevice Belt8;

    private BleConnect Belt1Connect;
    private BleConnect Belt2Connect;
    private BleConnect Belt3Connect;
    private BleConnect Belt4Connect;
    private BleConnect Belt5Connect;
    private BleConnect Belt6Connect;
    private BleConnect Belt7Connect;
    private BleConnect Belt8Connect;

    private ArrayList<BluetoothDevice> mListBelts = new ArrayList<>();
    private ArrayList<BleConnect> mListBeltsConnect = new ArrayList<>();
    private ArrayList<String> mListBeltsName = new ArrayList<>();

    private TextView mHR1;
    private TextView mHR2;
    private TextView mHR3;
    private TextView mHR4;
    private TextView mHR5;
    private TextView mHR6;
    private TextView mHR7;
    private TextView mHR8;

    private ArrayList<TextView> mTvListHR = new ArrayList<>();

    private SwitchButton mBoatSensorSwitch;
    private SwitchButton mBoatSpeedSwitch;
    private SwitchButton mMountDegreeSwitch;

    private int mBoatSensorSwitchStatus = 0;
    private int mBoatSpeedSwitchStatus = 0;
    private int mMountDegreeSwitchStatus = 0;

    private TextView mBoatSpeedTxUnit;
    private String splitTimeTx = "9:59";
    private String boatSpeedForLog = "0.0";

    private double[] UI_params_yaw_cache = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    private int tabMode;

    private MyWebView webView;
    private AMapWrapper aMapWrapper;
    private Marker mGPSMarker;
    private MarkerOptions markOptions;

    private AMap aMap;
    private TextView distanceTab;
    private MyLocationStyle myLocationStyle = new MyLocationStyle();
    private int reverseMode = 0;

    private Button mBtnDirectionEast;
    private Button mBtnDirectionSouth;
    private int directionStatus = 1;
    private SlideView mSliderResetSecondary;

    private double correctionLeftSecondary = 0;
    private double correctionRightSecondary = 0;
    private double calibrateBegTime = 0;
    private double postCalibrateDuration = 0;
    private double splitRatioBwdDefault = 0.633;
    private double marginOfDifference = 0.35;

    private int packetCounterCache = 0;

    private double mLoggerStartTime = 0;
    private double mLoggerLengthCap = 480000;
    // private double mLoggerLengthCap = 20000;
    private int READY_TO_LOG_NEW = 0;
    private double mBoatAccelerationForLog = 0;

    private double speedNewUpdate;
    private Button mButtonHide;
    private Button mButtonShow;
    private Boolean[] mButtonStatus = new Boolean[] {};
    // mStart, mStop, mSliderRecovery, mSliderSpeed, mSliderAngle, mSliderPrecision

    private double[] CacheStrokeAV = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    private double[] CacheLengthStrokeAV = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };

    private double wattage_p1_1 = 339.3;
    private double wattage_p2_1 = -252.2;

    private double wattage_p1_4 = -99.08;
    private double wattage_p2_4 = 466.6;
    private double wattage_p3_4 = -581.7;
    private double wattage_p4_4 = 331.6;
    private double wattage_p5_4 = 1.714;

    private double wattageSuppressionRatio = 0.35;

    private double fwdSplitRatio = 0.40;
    private double fwdSplitThreshSpeed = 1.35;
    private double fwdSplitDivideFactor = 50;
    private double fwdSplitRandomSeedDivideFactor = 50;

    private int DotsSelected = 0;
    private double[] euler_cache_for_lightLoad_0 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    private double[] euler_cache_for_lightLoad_1 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    private double[] euler_cache_for_lightLoad_2 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    private TextView payloadMonitor;

    private int mConnectedHRMs = 0;

    private double[] connectionStart = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private double[] connectionTimeElapsed = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private double connectionTimeout = 10000;

    private TimerTask connectionEnhance;
    private Timer connectionEnhanceTimer;
    private int connectionMode;

    private TimerTask updateSecondaryDataTask;
    private Timer updateSecondaryTimer;

    private String BeltName1;
    private String BeltName2;
    private String BeltName3;
    private String BeltName4;
    private String BeltName5;
    private String BeltName6;
    private String BeltName7;
    private String BeltName8;

    private static final String TAG = MainActivity.class.getSimpleName();
    private PolarBleApi api;
    Disposable broadcastDisposable;

    private double[] dotsLastUpdateTime = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private int dynamicReconnect = 0;
    private int currentReconnectPos = -1;
    private int reconnectingInProcess = 0;

    private int strokeRefreshLock = 0;
    private double[] packetCounterSet = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private double[] packetCounterSetCache = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    private double packetCounterPhone = 0;
    private double packetCounterPhoneCache = 0;

    private TimerTask watchmanTask;
    private Timer watchmanTimer;

    private int reconnectLoop = 3;
    private int reconnectNumber = 0;

    private int HRMpassThrough = 0;

    private String PATH = "";
    private String username;
    private String password;
    private String addressMAC;

    public CookieManager cookieManager = new CookieManager();

    private int selectedBeltAmount = 0;
    private double lastLoginTime = 0;
    private double timeFromLastAutoConnect = 0;

    private ArrayList<String> athleteNameList = new ArrayList<>();
    private ArrayList<String> athleteIDList = new ArrayList<>();

    private String boatType;

    private int[] boatSpeedClassifier = new int[] { 0, 0, 0, 0, 0 };
    private int[] strokeRateClassifier = new int[] { 0, 0, 0, 0, 0 };
    private int[] heartRateClassifier_1 = new int[] { 0, 0, 0, 0, 0 };
    private int[] heartRateClassifier_2 = new int[] { 0, 0, 0, 0, 0 };
    private int[] heartRateClassifier_3 = new int[] { 0, 0, 0, 0, 0 };
    private int[] heartRateClassifier_4 = new int[] { 0, 0, 0, 0, 0 };
    private int[] heartRateClassifier_5 = new int[] { 0, 0, 0, 0, 0 };
    private int[] heartRateClassifier_6 = new int[] { 0, 0, 0, 0, 0 };
    private int[] heartRateClassifier_7 = new int[] { 0, 0, 0, 0, 0 };
    private int[] heartRateClassifier_8 = new int[] { 0, 0, 0, 0, 0 };

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
    private String Lang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        reverseMode = getIntent().getIntExtra("reverse", 0);
        Lang = getIntent().getStringExtra("lang");

        if (metrics.xdpi <= 250 || isPad(this)) {
            tabMode = 1;
            if (reverseMode == 1) {
                setContentView(R.layout.activity_dashboard_tab_reverse);
            } else {
                setContentView(R.layout.activity_dashboard_tab);
                mBtnDirectionEast = findViewById(R.id.btn_direction_east);
                mBtnDirectionSouth = findViewById(R.id.btn_direction_south);
                mBtnDirectionSouth.setTextColor(Color.WHITE);
                mBtnDirectionSouth.setBackgroundResource(R.drawable.gradient_green_round);
            }

        } else {
            tabMode = 0;

            if (Objects.equals(Lang, "eng")) {
                setContentView(R.layout.activity_dashboard_eng);

            } else {
                setContentView(R.layout.activity_dashboard);

            }
        }

        mBottomBar = findViewById(R.id.bottom_bar_DB);

        mBtnReconnect = findViewById(R.id.btn_reconnect_DB);
        mBtnResetHeading = findViewById(R.id.btn_reset_heading_DB);
        mBtnActivate = findViewById(R.id.btn_activate_DB);

        mBoatRoll = findViewById(R.id.boat_roll_DB);
        mBoatYaw = findViewById(R.id.boat_yaw_DB);

        DotL1 = getIntent().getExtras().getParcelable("L1");
        DotL2 = getIntent().getExtras().getParcelable("L2");
        DotL3 = getIntent().getExtras().getParcelable("L3");
        DotL4 = getIntent().getExtras().getParcelable("L4");
        DotR1 = getIntent().getExtras().getParcelable("R1");
        DotR2 = getIntent().getExtras().getParcelable("R2");
        DotR3 = getIntent().getExtras().getParcelable("R3");
        DotR4 = getIntent().getExtras().getParcelable("R4");
        DotBOAT = getIntent().getExtras().getParcelable("BOAT");
        Belt1 = getIntent().getExtras().getParcelable("Belt1");
        Belt2 = getIntent().getExtras().getParcelable("Belt2");
        Belt3 = getIntent().getExtras().getParcelable("Belt3");
        Belt4 = getIntent().getExtras().getParcelable("Belt4");
        Belt5 = getIntent().getExtras().getParcelable("Belt5");
        Belt6 = getIntent().getExtras().getParcelable("Belt6");
        Belt7 = getIntent().getExtras().getParcelable("Belt7");
        Belt8 = getIntent().getExtras().getParcelable("Belt8");
        connectionMode = getIntent().getIntExtra("mode", 1);
        selectedBeltAmount = getIntent().getIntExtra("selectedBeltAmount", 0);

        BeltName1 = getIntent().getStringExtra("BeltName1");
        BeltName2 = getIntent().getStringExtra("BeltName2");
        BeltName3 = getIntent().getStringExtra("BeltName3");
        BeltName4 = getIntent().getStringExtra("BeltName4");
        BeltName5 = getIntent().getStringExtra("BeltName5");
        BeltName6 = getIntent().getStringExtra("BeltName6");
        BeltName7 = getIntent().getStringExtra("BeltName7");
        BeltName8 = getIntent().getStringExtra("BeltName8");

        boatType = getIntent().getStringExtra("boatType");

        athleteNameList.add(getIntent().getStringExtra("AthleteName1"));
        athleteNameList.add(getIntent().getStringExtra("AthleteName2"));
        athleteNameList.add(getIntent().getStringExtra("AthleteName3"));
        athleteNameList.add(getIntent().getStringExtra("AthleteName4"));
        athleteNameList.add(getIntent().getStringExtra("AthleteName5"));
        athleteNameList.add(getIntent().getStringExtra("AthleteName6"));
        athleteNameList.add(getIntent().getStringExtra("AthleteName7"));
        athleteNameList.add(getIntent().getStringExtra("AthleteName8"));

        athleteIDList.add(getIntent().getStringExtra("AthleteID1"));
        athleteIDList.add(getIntent().getStringExtra("AthleteID2"));
        athleteIDList.add(getIntent().getStringExtra("AthleteID3"));
        athleteIDList.add(getIntent().getStringExtra("AthleteID4"));
        athleteIDList.add(getIntent().getStringExtra("AthleteID5"));
        athleteIDList.add(getIntent().getStringExtra("AthleteID6"));
        athleteIDList.add(getIntent().getStringExtra("AthleteID7"));
        athleteIDList.add(getIntent().getStringExtra("AthleteID8"));

        System.out.println("Dashboard Loaded");
        System.out.println(athleteNameList);
        System.out.println(athleteIDList);

        // System.out.println("this is parser: " + Belt1);

        mDotL1 = findViewById(R.id.dot_L1);
        mDotL2 = findViewById(R.id.dot_L2);
        mDotL3 = findViewById(R.id.dot_L3);
        mDotL4 = findViewById(R.id.dot_L4);
        mDotR1 = findViewById(R.id.dot_R1);
        mDotR2 = findViewById(R.id.dot_R2);
        mDotR3 = findViewById(R.id.dot_R3);
        mDotR4 = findViewById(R.id.dot_R4);
        mDotBOAT = findViewById(R.id.dot_BOAT);

        mPassThroughList.add(DotL1);
        mPassThroughList.add(DotL2);
        mPassThroughList.add(DotL3);
        mPassThroughList.add(DotL4);
        mPassThroughList.add(DotR1);
        mPassThroughList.add(DotR2);
        mPassThroughList.add(DotR3);
        mPassThroughList.add(DotR4);
        mPassThroughList.add(DotBOAT);

        mPassThroughListStr.add(String.valueOf(DotL1));
        mPassThroughListStr.add(String.valueOf(DotL2));
        mPassThroughListStr.add(String.valueOf(DotL3));
        mPassThroughListStr.add(String.valueOf(DotL4));
        mPassThroughListStr.add(String.valueOf(DotR1));
        mPassThroughListStr.add(String.valueOf(DotR2));
        mPassThroughListStr.add(String.valueOf(DotR3));
        mPassThroughListStr.add(String.valueOf(DotR4));
        mPassThroughListStr.add(String.valueOf(DotBOAT));

        mListBelts.add(Belt1);
        mListBelts.add(Belt2);
        mListBelts.add(Belt3);
        mListBelts.add(Belt4);
        mListBelts.add(Belt5);
        mListBelts.add(Belt6);
        mListBelts.add(Belt7);
        mListBelts.add(Belt8);

        if (selectedBeltAmount > 0) {

            HRMpassThrough = 1;

        }

        //
        // for (int i = 0; i<mListBelts.size(); i++){
        //
        // Log.e("HRM:" , String.valueOf(mListBelts.get(i)));
        // Log.e("HRM loop:", String.valueOf(i));
        //
        // if (String.valueOf(mListBelts.get(i)) != "null"){
        // System.out.println("THIS IS WHERE POLAR FUCKS:" +
        // String.valueOf(mListBelts.get(i)));
        // HRMpassThrough = 1;
        // break;
        // }
        //
        // }

        mListBeltsName.add(BeltName1);
        mListBeltsName.add(BeltName2);
        mListBeltsName.add(BeltName3);
        mListBeltsName.add(BeltName4);
        mListBeltsName.add(BeltName5);
        mListBeltsName.add(BeltName6);
        mListBeltsName.add(BeltName7);
        mListBeltsName.add(BeltName8);

        UI_params_set.add(UI_params_L1);
        UI_params_set.add(UI_params_L2);
        UI_params_set.add(UI_params_L3);
        UI_params_set.add(UI_params_L4);
        UI_params_set.add(UI_params_R1);
        UI_params_set.add(UI_params_R2);
        UI_params_set.add(UI_params_R3);
        UI_params_set.add(UI_params_R4);

        // mListBeltsConnect.add(Belt1Connect);
        // mListBeltsConnect.add(Belt2Connect);
        // mListBeltsConnect.add(Belt3Connect);
        // mListBeltsConnect.add(Belt4Connect);
        // mListBeltsConnect.add(Belt5Connect);
        // mListBeltsConnect.add(Belt6Connect);
        // mListBeltsConnect.add(Belt7Connect);
        // mListBeltsConnect.add(Belt8Connect);

        mHR1 = findViewById(R.id.hr_display_1);
        mHR2 = findViewById(R.id.hr_display_2);
        mHR3 = findViewById(R.id.hr_display_3);
        mHR4 = findViewById(R.id.hr_display_4);
        mHR5 = findViewById(R.id.hr_display_5);
        mHR6 = findViewById(R.id.hr_display_6);
        mHR7 = findViewById(R.id.hr_display_7);
        mHR8 = findViewById(R.id.hr_display_8);

        mTvListHR.add(mHR1);
        mTvListHR.add(mHR2);
        mTvListHR.add(mHR3);
        mTvListHR.add(mHR4);
        mTvListHR.add(mHR5);
        mTvListHR.add(mHR6);
        mTvListHR.add(mHR7);
        mTvListHR.add(mHR8);

        mTvPassThroughNameList.add(mDotL1);
        mTvPassThroughNameList.add(mDotL2);
        mTvPassThroughNameList.add(mDotL3);
        mTvPassThroughNameList.add(mDotL4);
        mTvPassThroughNameList.add(mDotR1);
        mTvPassThroughNameList.add(mDotR2);
        mTvPassThroughNameList.add(mDotR3);
        mTvPassThroughNameList.add(mDotR4);
        mTvPassThroughNameList.add(mDotBOAT);

        mFrame_L1 = findViewById(R.id.frame_L1);
        mFrame_L2 = findViewById(R.id.frame_L2);
        mFrame_L3 = findViewById(R.id.frame_L3);
        mFrame_L4 = findViewById(R.id.frame_L4);
        mFrame_R1 = findViewById(R.id.frame_R1);
        mFrame_R2 = findViewById(R.id.frame_R2);
        mFrame_R3 = findViewById(R.id.frame_R3);
        mFrame_R4 = findViewById(R.id.frame_R4);

        mDegreeL1fwd = findViewById(R.id.degree_L1_fwd);
        mDegreeL2fwd = findViewById(R.id.degree_L2_fwd);
        mDegreeL3fwd = findViewById(R.id.degree_L3_fwd);
        mDegreeL4fwd = findViewById(R.id.degree_L4_fwd);

        mDegreeL1bwd = findViewById(R.id.degree_L1_bwd);
        mDegreeL2bwd = findViewById(R.id.degree_L2_bwd);
        mDegreeL3bwd = findViewById(R.id.degree_L3_bwd);
        mDegreeL4bwd = findViewById(R.id.degree_L4_bwd);

        mDegreeR1fwd = findViewById(R.id.degree_R1_fwd);
        mDegreeR2fwd = findViewById(R.id.degree_R2_fwd);
        mDegreeR3fwd = findViewById(R.id.degree_R3_fwd);
        mDegreeR4fwd = findViewById(R.id.degree_R4_fwd);

        mDegreeR1bwd = findViewById(R.id.degree_R1_bwd);
        mDegreeR2bwd = findViewById(R.id.degree_R2_bwd);
        mDegreeR3bwd = findViewById(R.id.degree_R3_bwd);
        mDegreeR4bwd = findViewById(R.id.degree_R4_bwd);

        mDegreeL1 = findViewById(R.id.degree_L1);
        mDegreeL2 = findViewById(R.id.degree_L2);
        mDegreeL3 = findViewById(R.id.degree_L3);
        mDegreeL4 = findViewById(R.id.degree_L4);

        mDegreeR1 = findViewById(R.id.degree_R1);
        mDegreeR2 = findViewById(R.id.degree_R2);
        mDegreeR3 = findViewById(R.id.degree_R3);
        mDegreeR4 = findViewById(R.id.degree_R4);

        mOarL1 = findViewById(R.id.oarL1);
        mOarL2 = findViewById(R.id.oarL2);
        mOarL3 = findViewById(R.id.oarL3);
        mOarL4 = findViewById(R.id.oarL4);
        mOarR1 = findViewById(R.id.oarR1);
        mOarR2 = findViewById(R.id.oarR2);
        mOarR3 = findViewById(R.id.oarR3);
        mOarR4 = findViewById(R.id.oarR4);

        mOarL1_roll = findViewById(R.id.oarL1_roll);
        mOarL2_roll = findViewById(R.id.oarL2_roll);
        mOarL3_roll = findViewById(R.id.oarL3_roll);
        mOarL4_roll = findViewById(R.id.oarL4_roll);
        mOarR1_roll = findViewById(R.id.oarR1_roll);
        mOarR2_roll = findViewById(R.id.oarR2_roll);
        mOarR3_roll = findViewById(R.id.oarR3_roll);
        mOarR4_roll = findViewById(R.id.oarR4_roll);

        mOarL1_pitch = findViewById(R.id.oarL1_pitch);
        mOarL2_pitch = findViewById(R.id.oarL2_pitch);
        mOarL3_pitch = findViewById(R.id.oarL3_pitch);
        mOarL4_pitch = findViewById(R.id.oarL4_pitch);
        mOarR1_pitch = findViewById(R.id.oarR1_pitch);
        mOarR2_pitch = findViewById(R.id.oarR2_pitch);
        mOarR3_pitch = findViewById(R.id.oarR3_pitch);
        mOarR4_pitch = findViewById(R.id.oarR4_pitch);

        mFrame_L1_roll = findViewById(R.id.L1_roll_frame);
        mFrame_L2_roll = findViewById(R.id.L2_roll_frame);
        mFrame_L3_roll = findViewById(R.id.L3_roll_frame);
        mFrame_L4_roll = findViewById(R.id.L4_roll_frame);
        mFrame_R1_roll = findViewById(R.id.R1_roll_frame);
        mFrame_R2_roll = findViewById(R.id.R2_roll_frame);
        mFrame_R3_roll = findViewById(R.id.R3_roll_frame);
        mFrame_R4_roll = findViewById(R.id.R4_roll_frame);

        payloadMonitor = findViewById(R.id.load_status);

        // mRowingAnimation = findViewById(R.id.rowing_animation);

        mSensorListener = new phoneSensorListener();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcclSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAcclLinearSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mStart = findViewById(R.id.btn_start_section);
        mStop = findViewById(R.id.btn_stop_section);
        mStrokeRate = findViewById(R.id.stroke_rate_DB);
        mBoatSpeed = findViewById(R.id.gps_speed_DB);
        totalElapse = findViewById(R.id.section_time_DB);

        distanceTab = findViewById(R.id.travelled_distance);

        mBoatSpeedTxUnit = findViewById(R.id.boat_speed_tx_unit_dashboard);

        mSliderResetSecondary = findViewById(R.id.slide_to_reset);

        mBoatSensorSwitch = findViewById(R.id.boat_sensor_switch_dashboard);
        mBoatSensorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mBoatSensorSwitchStatus = 1;
                } else {
                    mBoatSensorSwitchStatus = 0;
                }
            }
        });

        mBoatSpeedSwitch = findViewById(R.id.boat_speed_switch_dashboard);
        mBoatSpeedSwitch.setChecked(true);

        mBoatSpeedSwitchStatus = 1;
        mBoatSpeedTxUnit.setText("/500M");

        mBoatSpeedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mBoatSpeedSwitchStatus = 1;
                    mBoatSpeedTxUnit.setText("/500M");
                } else {
                    mBoatSpeedSwitchStatus = 0;
                    mBoatSpeedTxUnit.setText("M/S");
                }
            }
        });

        mMountDegreeSwitch = findViewById(R.id.mount_degree_switch_dashboard);
        mMountDegreeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mMountDegreeSwitchStatus = 1;
                    correctionPeddle = 225;
                } else {
                    mMountDegreeSwitchStatus = 0;
                    correctionPeddle = 135;

                }
            }
        });

        mBtnResetHeading.setAlpha(0);
        mBtnActivate.setAlpha(0);
        mStart.setAlpha(0);
        mSliderResetSecondary.setAlpha(0);

        mStart.setEnabled(false);
        mStop.setEnabled(false);
        mSliderResetSecondary.setEnabled(false);

        mButtonStatus = new Boolean[] {
                mStart.isEnabled(), mStop.isEnabled(),
                mSliderResetSecondary.isEnabled(), mBoatSensorSwitch.isEnabled(),
                mBoatSpeedSwitch.isEnabled(), mMountDegreeSwitch.isEnabled() };

        mSliderResetSecondary.setOnSlideCompleteListener(new SlideView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideView slideView) {

                resetHeading();
                calibrateBegTime = System.currentTimeMillis();
                correctionLeftSecondary = -90;
                correctionRightSecondary = 90;
                Toast.makeText(Dashboard.this, "校准完成", Toast.LENGTH_LONG).show();

            }
        });

        Intent intent = getIntent();
        username = intent.getStringExtra("userName");
        password = intent.getStringExtra("password");
        System.out.println(username + password);

        addressMAC = getMacFromHardware();

        fileLoc = this.getFilesDir() + "/xsens/";
        createPath(fileLoc);

        // XsensDotSdk.setDebugEnabled(false);
        // XsensDotSdk.setReconnectEnabled(true);

        refreshBotList();

        if (tabMode == 1) {
            webView = findViewById(R.id.my_webview);
            MAWebViewWrapper webViewWrapper = new MAWebViewWrapper(webView);
            aMapWrapper = new AMapWrapper(this, webViewWrapper);
            aMapWrapper.onCreate();
            aMapWrapper.getMapAsyn(new AMap.OnMapReadyListener() {
                @Override
                public void onMapReady(AMap map) {
                    aMap = map;
                }
            });
        }

        mBtnReconnect.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (mStartStatus == 1) {

                    Toast.makeText(Dashboard.this, "请停止记记录后再重新连接传感器", Toast.LENGTH_SHORT).show();

                } else {

                    // try {
                    //
                    // watchman(0);
                    //
                    // }catch (Exception e){
                    //
                    //
                    // }
                    numOfConnectedDots = 0;
                    mHRfirstConnect = 0;
                    mConnectedHRMs = 0;
                    threadConnectDots.interrupt();
                    activateDots(0);
                    connectionEnhance.cancel();
                    connectionEnhanceTimer.cancel();

                    connectionGuard();
                    mountPolar(0);

                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            disconnectDots();
                            Looper.loop();
                        }
                    }, 200);

                    Timer timer2 = new Timer();
                    timer2.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            connectDots();
                            Looper.loop();
                        }
                    }, 1000);
                }
                return true;

            }
        });

        mBtnActivate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                activateDots(activationCode);
                System.out.println("CURRENT SYSTEM ACTIVATION STATUS:++++++++" + activationStatus);
                updateUI(activationStatus);
                // watchman(activationStatus);
                // connectDeviceHR(1);
                mountPolar(1);
                autoPostStart();

                try {
                    connectionEnhance.cancel();
                    connectionEnhanceTimer.cancel();
                    threadConnectDots.interrupt();
                } catch (Exception e) {

                }

                return true;
            }
        });

        mBtnResetHeading.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                resetHeading();
                return true;
            }
        });

        if (tabMode == 0) {

            mButtonHide = findViewById(R.id.btn_arrow_down_DB);
            mButtonShow = findViewById(R.id.btn_arrow_up_DB);

            mButtonShow.setAlpha(0);
            mButtonShow.setEnabled(false);

            mButtonHide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    FrameLayout.LayoutParams fp_invisible = (FrameLayout.LayoutParams) mBottomBar.getLayoutParams();
                    fp_invisible.bottomMargin = 2000;
                    mBottomBar.setLayoutParams(fp_invisible);
                    mButtonShow.setAlpha(0.5f);
                    mButtonShow.setEnabled(true);

                    mStart.setEnabled(mButtonStatus[0]);
                    mStop.setEnabled(mButtonStatus[1]);
                    mSliderResetSecondary.setEnabled(mButtonStatus[2]);
                    mBoatSensorSwitch.setEnabled(mButtonStatus[3]);
                    mBoatSpeedSwitch.setEnabled(mButtonStatus[4]);
                    mMountDegreeSwitch.setEnabled(mButtonStatus[5]);

                }
            });

            mButtonShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mButtonShow.setAlpha(0);
                    mButtonShow.setEnabled(false);

                    FrameLayout.LayoutParams fp_invisible = (FrameLayout.LayoutParams) mBottomBar.getLayoutParams();
                    fp_invisible.bottomMargin = 0;
                    mBottomBar.setLayoutParams(fp_invisible);

                    // mStart, mStop, mSliderRecovery, mSliderPrecision, mSliderSpeed, mSliderAngle
                    mButtonStatus = new Boolean[] {
                            mStart.isEnabled(), mStop.isEnabled(),
                            mSliderResetSecondary.isEnabled(), mBoatSensorSwitch.isEnabled(),
                            mBoatSpeedSwitch.isEnabled(), mMountDegreeSwitch.isEnabled() };

                    mStart.setEnabled(false);
                    mStop.setEnabled(false);
                    mSliderResetSecondary.setEnabled(false);
                    mBoatSensorSwitch.setEnabled(false);
                    mBoatSpeedSwitch.setEnabled(false);
                    mMountDegreeSwitch.setEnabled(false);

                }
            });
        }

        mStart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                mStartStatus = 1;
                mLocationClientGD.startLocation();
                mLoggerStartTime = System.currentTimeMillis();
                lastLoginTime = System.currentTimeMillis();

                sectionCount = 0;
                boatSpeedClassifier = new int[] { 0, 0, 0, 0, 0 };
                strokeRateClassifier = new int[] { 0, 0, 0, 0, 0 };

                heartRateClassifier_1 = new int[] { 0, 0, 0, 0, 0 };
                heartRateClassifier_2 = new int[] { 0, 0, 0, 0, 0 };
                heartRateClassifier_3 = new int[] { 0, 0, 0, 0, 0 };
                heartRateClassifier_4 = new int[] { 0, 0, 0, 0, 0 };
                heartRateClassifier_5 = new int[] { 0, 0, 0, 0, 0 };
                heartRateClassifier_6 = new int[] { 0, 0, 0, 0, 0 };
                heartRateClassifier_7 = new int[] { 0, 0, 0, 0, 0 };
                heartRateClassifier_8 = new int[] { 0, 0, 0, 0, 0 };

                try {
                    updateTimer.cancel();
                    updateTask.cancel();

                    if (httpURLConnectionUpdate != null) {
                        httpURLConnectionUpdate.disconnect();
                        httpURLConnectionUpdate.getOutputStream().close();
                    }
                    // phoneLoggerStandalone(mStartStatus);
                    // watchman(mStartStatus);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    // startConnection();
                    loggerStart();
                    // phoneLoggerStandalone(mStartStatus);
                    // watchman(mStartStatus);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    startUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    updateSecondaryData();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (tabMode == 1) {
                    FrameLayout.LayoutParams fp_visible = (FrameLayout.LayoutParams) mBottomBar.getLayoutParams();
                    fp_visible.bottomMargin = -200;
                    mBottomBar.setLayoutParams(fp_visible);
                }

                totalElapse.setBase(SystemClock.elapsedRealtime());
                totalElapse.start();

                mStart.setEnabled(false);
                mStop.setEnabled(true);
                mBtnActivate.setEnabled(false);

                mButtonStatus[0] = false;
                mButtonStatus[1] = true;

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        // mRowingAnimation.animate().alpha(0.35f).setDuration(200).start();
                        mStop.animate().alpha(1f).setDuration(200).start();
                        mStart.animate().alpha(0f).setDuration(200).start();
                        totalElapse.animate().alpha(1f).setDuration(200).start();
                        Looper.loop();
                    }
                }, 10);
                return true;
            }
        });

        mStop.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                mStartStatus = 0;
                mLocationClientGD.stopLocation();

                try {
                    updateTimer.cancel();
                    updateTask.cancel();

                    connectionEnhance.cancel();
                    connectionEnhanceTimer.cancel();

                    if (httpURLConnectionUpdate != null) {
                        httpURLConnectionUpdate.disconnect();
                        httpURLConnectionUpdate.getOutputStream().close();
                    }
                    // loggerStop();
                    // phoneLoggerStandalone(mStartStatus);
                    // watchman(mStartStatus);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    loggerStop();
                    // phoneLoggerStandalone(mStartStatus);
                    // watchman(mStartStatus);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                secondaryDataInputReset();
                secondaryUIReset();

                if (tabMode == 1) {

                    FrameLayout.LayoutParams fp_visible = (FrameLayout.LayoutParams) mBottomBar.getLayoutParams();
                    fp_visible.bottomMargin = 0;
                    mBottomBar.setLayoutParams(fp_visible);

                }

                totalElapse.setBase(SystemClock.elapsedRealtime());
                totalElapse.stop();

                mStart.setEnabled(true);
                mStop.setEnabled(false);
                mBtnActivate.setEnabled(true);

                mButtonStatus[0] = true;
                mButtonStatus[1] = false;

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        // mRowingAnimation.animate().alpha(0f).setDuration(200).start();
                        mStop.animate().alpha(0f).setDuration(200).start();
                        mStart.animate().alpha(1f).setDuration(200).start();
                        totalElapse.animate().alpha(0f).setDuration(200).start();
                        Looper.loop();
                    }
                }, 10);

                fileSaveNotification();
                return true;
            }
        });

        if (tabMode == 1 && reverseMode == 0) {

            mBtnDirectionEast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    directionStatus = 2;
                    mBtnDirectionEast.setTextColor(Color.WHITE);
                    mBtnDirectionEast.setBackgroundResource(R.drawable.gradient_green_round);
                    mBtnDirectionSouth.setTextColor(Color.DKGRAY);
                    mBtnDirectionSouth.setBackgroundResource(R.drawable.gradient_line_round);
                    System.out.println("directionStatus:" + directionStatus);

                }
            });

            mBtnDirectionSouth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    directionStatus = 1;
                    mBtnDirectionSouth.setTextColor(Color.WHITE);
                    mBtnDirectionSouth.setBackgroundResource(R.drawable.gradient_green_round);
                    mBtnDirectionEast.setTextColor(Color.DKGRAY);
                    mBtnDirectionEast.setBackgroundResource(R.drawable.gradient_line_round);
                    System.out.println("directionStatus:" + directionStatus);

                }
            });

        }

        // Initialization

        if (connectionMode == 1 || connectionMode == 2 || connectionMode == 4) {

            updateXsensDevice();

            // try {
            //
            // watchman(0);
            //
            // }catch (Exception e){
            //
            //
            // }
            numOfConnectedDots = 0;
            mConnectedHRMs = 0;
            activateDots(0);
            connectionGuard();

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Looper.prepare();
                    disconnectDots();
                    Looper.loop();
                }
            }, 500);

            Timer timer2 = new Timer();
            timer2.schedule(new TimerTask() {
                @Override
                public void run() {
                    Looper.prepare();
                    connectDots();
                    Looper.loop();
                }
            }, 2000);

            // mountPolar(1);
            // updateXsensDevice();
            // connectDots();
            // connectionGuard();

        } else if (connectionMode == 3) {

            mBtnActivate.performLongClick();
            mButtonHide.performClick();
        }

        locationServiceInit();
    }

    private void getSecondaryData() {

        if (mStartStatus == 1) {

            float[] values = new float[3];
            float[] R = new float[9];
            SensorManager.getRotationMatrix(R, null, acclSensorValues,
                    magSensorValues);

            SensorManager.getOrientation(R, values);

            values[0] = (float) Math.toDegrees(values[0]);
            values[1] = (float) Math.toDegrees(values[1]);
            values[2] = (float) Math.toDegrees(values[2]);

            double systemTimeCheck = System.currentTimeMillis();
            if (systemTimeCheck - speedNewUpdate >= 5000) {
                boatSpeedForLog = "0.0";
            }

            if (boatRollCacheSize < boatRollCacheLength) {

                boatRollCacheSamples[boatRollCachePointer++] = -values[1];
                boatRollCacheSize++;

            } else {

                boatRollCachePointer = boatRollCachePointer % boatRollCacheLength;
                boatRollCacheSum -= boatRollCacheSamples[boatRollCachePointer];
                boatRollCacheSamples[boatRollCachePointer++] = -values[1];

            }

            UI_params_Secondary_float[1] = (float) doubleArrAverage(boatRollCacheSamples);

            float xAcclLinear = acclLinearSensorValues[0];
            float yAcclLinear = acclLinearSensorValues[1];
            float zAcclLinear = acclLinearSensorValues[2];

            double tiltAngleX = values[2] * Math.PI / 180;
            double tiltCosX = Math.cos(tiltAngleX);

            double tiltAngleZ = (90 - Math.abs(values[2])) * Math.PI / 180;
            double tiltCosZ = Math.cos(tiltAngleZ);

            double xAcclActual = -xAcclLinear * tiltCosX;
            double zAcclActual = zAcclLinear * tiltCosZ;

            double boatAcclActualNow;
            double boatYawTan = 0;

            if (reverseMode == 1) {
                boatAcclActualNow = -xAcclActual - zAcclActual;
                boatYawTan = yAcclLinear / boatAcclActualNow;
            } else if (directionStatus == 2) {
                boatAcclActualNow = -yAcclLinear;
                boatYawTan = -xAcclLinear / boatAcclActualNow;
            } else {
                boatAcclActualNow = xAcclActual + zAcclActual;
                boatYawTan = yAcclLinear / boatAcclActualNow;
            }

            mBoatAccelerationForLog = boatAcclActualNow;

            float boatYawAngle = 0f;

            double strokeNow = System.currentTimeMillis();
            double strokeGap = strokeNow - strokeCache;

            int tempHourStrokeRateAvg = 0;
            int tempMinStrokeRateAvg = 0;
            int tempSecStrokeRateAvg = 0;

            if (totalElapse.length() <= 5) {
                tempHourStrokeRateAvg = 0;
                tempMinStrokeRateAvg = Integer.parseInt(totalElapse.getText().toString().split(":")[0]);
                tempSecStrokeRateAvg = Integer.parseInt(totalElapse.getText().toString().split(":")[1]);

            } else {
                tempHourStrokeRateAvg = Integer.parseInt(totalElapse.getText().toString().split(":")[0]);
                tempMinStrokeRateAvg = Integer.parseInt(totalElapse.getText().toString().split(":")[1]);
                tempSecStrokeRateAvg = Integer.parseInt(totalElapse.getText().toString().split(":")[2]);
            }
            mDisplayTimeTx = tempHourStrokeRateAvg + ":" + tempMinStrokeRateAvg + ":" + tempSecStrokeRateAvg;
            double tempTotalSecStrokeRateAvg = tempHourStrokeRateAvg * 3600 + tempMinStrokeRateAvg * 60
                    + tempSecStrokeRateAvg;

            // int tempMinStrokeRateAvg =
            // Integer.parseInt(totalElapse.getText().toString().split(":")[0]);
            // int tempSecStrokeRateAvg =
            // Integer.parseInt(totalElapse.getText().toString().split(":")[1]);
            // mDisplayTimeTx = tempMinStrokeRateAvg + ":" + tempSecStrokeRateAvg;
            // double tempTotalSecStrokeRateAvg =
            // tempMinStrokeRateAvg*60+tempSecStrokeRateAvg;

            sectionTimeTX = String.valueOf(tempTotalSecStrokeRateAvg);

            if (acclCacheSize < acclCacheLength) {

                acclCacheSamples[acclCachePointer++] = boatAcclActualNow;
                acclCacheSize++;

            } else {

                acclCachePointer = acclCachePointer % acclCacheLength;
                acclCacheSum -= acclCacheSamples[acclCachePointer];
                acclCacheSamples[acclCachePointer++] = boatAcclActualNow;
            }

            double boatAcclActual = doubleArrAverage(acclCacheSamples);

            if (strokeGap > strokeIdleMax && Double.parseDouble(boatSpeedForLog) < 1.2) {
                String strokeRateTx = "0.0";
                UI_params_Secondary_str[0] = strokeRateTx;
                UI_params_Secondary_float[0] = boatYawAngle;
            }

            if (boatAcclActual > minBoatAccl) {

                strokeNow = System.currentTimeMillis();
                strokeGap = strokeNow - strokeCache;

                if (reverseMode == 1) {
                    boatYawAngle = -(float) (-Math.atan(boatYawTan) * 180 / Math.PI);
                } else {
                    boatYawAngle = (float) (-Math.atan(boatYawTan) * 180 / Math.PI);
                }

                if (boatYawCacheSize < boatYawCacheLength) {

                    boatYawCacheSamples[boatYawCachePointer++] = boatYawAngle;
                    boatYawCacheSize++;

                } else {

                    boatYawCachePointer = boatYawCachePointer % boatYawCacheLength;
                    boatYawCacheSum -= boatYawCacheSamples[boatYawCachePointer];
                    boatYawCacheSamples[boatYawCachePointer++] = boatYawAngle;
                }

                UI_params_Secondary_float[0] = (float) doubleArrAverage(boatYawCacheSamples) * yawAdjustRatio;

                if (strokeGap > minStrokeGap && strokeRefreshLock == 0) {

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

                    DecimalFormat StrokeRateFormatter = new DecimalFormat("0.0");
                    String strokeRateTx = StrokeRateFormatter.format(strokeRateActual);

                    DecimalFormat StrokeCountFormatter = new DecimalFormat("0");
                    String strokeCountTx = StrokeCountFormatter.format(strokeCount);

                    strokeCount = strokeCount + 1;

                    UI_params_Secondary_str[0] = strokeRateTx;

                    strokeRefreshLock = 1;

                }

            } else if (boatAcclActual <= strokeRefreshLowerThresh) {

                strokeRefreshLock = 0;

            }

        }
    }

    private void secondaryDataInputReset() {

        UI_params_yaw_cache = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        HR_params_cache = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0 };

        strokeCache = System.currentTimeMillis();
        strokeCount = 0;

        acclCacheSamples = new double[acclCacheLength];
        acclCachePointer = 0;
        acclCacheSize = 0;
        acclCacheSum = 0.0;

        SRCacheSamples = new double[SRCacheLength];
        SRCachePointer = 0;
        SRCacheSize = 0;
        SRCacheSum = 0.0;

        boatSpeedCacheSamples = new double[boatSpeedCacheLength];
        boatSpeedCachePointer = 0;
        boatSpeedCacheSize = 0;
        boatSpeedCacheSum = 0.0;

        boatYawCacheSamples = new double[boatYawCacheLength];
        boatYawCachePointer = 0;
        boatYawCacheSize = 0;
        boatYawCacheSum = 0.0;

        boatRollCacheSamples = new double[boatYawCacheLength];
        boatRollCachePointer = 0;
        boatRollCacheSize = 0;
        boatRollCacheSum = 0.0;

        UI_params_Secondary_str = new String[] { "0.0", "0:00" };
        UI_params_Secondary_float = new float[] { 0f, 0f };

        mDistance = 0;
        mDistanceTx = "0";
        sectionTimeTX = "0";

        packetCounter = 0;
        packetCounterPhone = 0;
        packetCounterCache = 0;
        packetCounterSet = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        packetCounterSetCache = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        euler_cache_for_lightLoad_0 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        euler_cache_for_lightLoad_1 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        euler_cache_for_lightLoad_2 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };

    }

    private void secondaryUIReset() {

        mBoatSpeed.setText("0.0");
        mStrokeRate.setText("0.0");
        distanceTab.setText("0");
        mBoatRoll.setRotation(0);
        mBoatYaw.setRotation(0);

    }

    private void updateUI_sub(final double[] UI_params,
            final TextView degreeFwd, final TextView degreeBwd, final TextView degreeOverall,
            final ImageView yaw, final ImageView roll, final ImageView pitch, final FrameLayout rollFrame,
            String side) {

        double catchFinishRangeLow;
        double catchFinishRangeHigh;

        if (mMountDegreeSwitchStatus == 0) {
            catchFinishRangeLow = 245;
            catchFinishRangeHigh = 285;

        } else {
            catchFinishRangeLow = 70;
            catchFinishRangeHigh = 110;
        }

        if (xsensDotBOAT != null && mBoatSensorSwitchStatus == 1 && UI_params[0] * UI_params[1] != 0) {
            yaw.setAlpha(0.7f);

            // degreeFwd.setText(String.format("%.0f", UI_params[4]));
            // degreeBwd.setText(String.format("%.0f", UI_params[5]));
            yaw.setRotation((float) UI_params[0]);

        } else {
            // degreeFwd.setText(String.format("%.0f", UI_params[4]));
            // degreeBwd.setText(String.format("%.0f", UI_params[5]));
            yaw.setAlpha(0f);
        }

        double boatSpeedTest = Double.parseDouble(boatSpeedForLog);

        if (boatSpeedTest >= 0 && boatSpeedTest <= 1.5) {

            if (UI_params[10] > 0 && UI_params[10] < 10) {
                // degreeOverall.setText(String.format("%.0f", UI_params[10]));

            } else if (UI_params[10] >= 10 && UI_params[10] < 30) {
                // degreeOverall.setText(String.format("%.0f", UI_params[10]));

            } else if (UI_params[10] >= 30 && UI_params[10] < 60) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));
                degreeFwd.setText(String.format("%.0f", UI_params[4]));
                degreeBwd.setText(String.format("%.0f", UI_params[5]));

            } else if (UI_params[10] >= 60 && UI_params[10] < 140) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));
                degreeFwd.setText(String.format("%.0f", UI_params[4]));
                degreeBwd.setText(String.format("%.0f", UI_params[5]));
            }

        } else if (boatSpeedTest > 1.5 && boatSpeedTest <= 2.5) {

            if (UI_params[10] > 0 && UI_params[10] < 10) {

            } else if (UI_params[10] >= 10 && UI_params[10] < 30) {
                // degreeOverall.setText(String.format("%.0f", UI_params[10]));

            } else if (UI_params[10] >= 30 && UI_params[10] < 60) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));
                degreeFwd.setText(String.format("%.0f", UI_params[4]));
                degreeBwd.setText(String.format("%.0f", UI_params[5]));

            } else if (UI_params[10] >= 60 && UI_params[10] < 140) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));
                degreeFwd.setText(String.format("%.0f", UI_params[4]));
                degreeBwd.setText(String.format("%.0f", UI_params[5]));
            }

        } else if (boatSpeedTest > 2.5) {

            if (UI_params[10] > 0 && UI_params[10] < 10) {

            } else if (UI_params[10] >= 10 && UI_params[10] < 30) {

            } else if (UI_params[10] >= 30 && UI_params[10] < 60) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));
                degreeFwd.setText(String.format("%.0f", UI_params[4]));
                degreeBwd.setText(String.format("%.0f", UI_params[5]));

            } else if (UI_params[10] >= 60 && UI_params[10] < 140) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));
                degreeFwd.setText(String.format("%.0f", UI_params[4]));
                degreeBwd.setText(String.format("%.0f", UI_params[5]));
            }

        }

        // if (boatSpeedTest > 2.5 ){
        //
        // if (UI_params[10] > 70 && UI_params[10] < 120){
        // degreeOverall.setText(String.format("%.0f", UI_params[10]));
        // }
        //
        // }else{
        // degreeOverall.setText(String.format("%.0f", UI_params[10]));
        // }

        // degreeOverall.setText(String.format("%.0f", UI_params[10]));
        // degreeOverall.setText(String.format("%.2f", UI_params[13]/1000));

        roll.setRotation((float) UI_params[6]);

        if (reverseMode == 1) {
            pitch.setRotation(-(float) UI_params[7]);
        } else {
            pitch.setRotation((float) UI_params[7]);
        }

        if (UI_params[6] > catchFinishRangeLow && UI_params[6] < catchFinishRangeHigh && side.equals("left")) {
            rollFrame.setBackgroundResource(R.drawable.gradient_line_green_round);

        } else if (UI_params[6] < -catchFinishRangeLow && UI_params[6] > -catchFinishRangeHigh
                && side.equals("right")) {
            rollFrame.setBackgroundResource(R.drawable.gradient_line_red_round);

        } else {
            rollFrame.setBackgroundResource(R.drawable.gradient_line_round);
        }
    }

    private void updateUI_secondary_sub(final float[] UI_params_float, final String[] UI_params_str) {

        mStrokeRate.setText(UI_params_str[0]);
        mBoatYaw.setRotation(UI_params_float[0]);
        mBoatRoll.setRotation(UI_params_float[1]);
        mBoatSpeed.setText(UI_params_str[1]);

    }

    private void updateUI(int code) {

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        updateUI_sub(UI_params_L1, mDegreeL1fwd, mDegreeL1bwd, mDegreeL1, mOarL1, mOarL1_roll,
                                mOarL1_pitch, mFrame_L1_roll, "left");
                        updateUI_sub(UI_params_L2, mDegreeL2fwd, mDegreeL2bwd, mDegreeL2, mOarL2, mOarL2_roll,
                                mOarL2_pitch, mFrame_L2_roll, "left");
                        updateUI_sub(UI_params_L3, mDegreeL3fwd, mDegreeL3bwd, mDegreeL3, mOarL3, mOarL3_roll,
                                mOarL3_pitch, mFrame_L3_roll, "left");
                        updateUI_sub(UI_params_L4, mDegreeL4fwd, mDegreeL4bwd, mDegreeL4, mOarL4, mOarL4_roll,
                                mOarL4_pitch, mFrame_L4_roll, "left");

                        updateUI_sub(UI_params_R1, mDegreeR1fwd, mDegreeR1bwd, mDegreeR1, mOarR1, mOarR1_roll,
                                mOarR1_pitch, mFrame_R1_roll, "right");
                        updateUI_sub(UI_params_R2, mDegreeR2fwd, mDegreeR2bwd, mDegreeR2, mOarR2, mOarR2_roll,
                                mOarR2_pitch, mFrame_R2_roll, "right");
                        updateUI_sub(UI_params_R3, mDegreeR3fwd, mDegreeR3bwd, mDegreeR3, mOarR3, mOarR3_roll,
                                mOarR3_pitch, mFrame_R3_roll, "right");
                        updateUI_sub(UI_params_R4, mDegreeR4fwd, mDegreeR4bwd, mDegreeR4, mOarR4, mOarR4_roll,
                                mOarR4_pitch, mFrame_R4_roll, "right");
                        updateUI_secondary_sub(UI_params_Secondary_float, UI_params_Secondary_str);
                        updateUI_sub_HR();

                        // Log.e("status","OK");

                        if (mStartStatus == 1 && lastLoginTime != 0) {

                            timeFromLastAutoConnect = System.currentTimeMillis() - lastLoginTime;

                            if (timeFromLastAutoConnect > 200000) {

                                autoPostStart();
                                lastLoginTime = System.currentTimeMillis();
                            }

                        }

                        double mSectionElapsed;

                        if (mStartStatus == 0) {

                            mSectionElapsed = 0;

                        } else {

                            double mCurrentLoggerTaskTime = System.currentTimeMillis();
                            mSectionElapsed = mCurrentLoggerTaskTime - mLoggerStartTime;
                            // System.out.println("Elapsed:" + mSectionElapsed);
                        }

                        if (READY_TO_LOG_NEW == 1 && mStartStatus == 1) {
                            try {
                                loggerStart();
                                // phoneLoggerStandalone(1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            READY_TO_LOG_NEW = 0;
                            mLoggerStartTime = System.currentTimeMillis();
                        }

                        // loggerUpdate();

                        if (mSectionElapsed > mLoggerLengthCap && mStartStatus == 1) {
                            READY_TO_LOG_NEW = 1;
                            // phoneLoggerStandalone(0);
                            loggerStop();

                        }

                    }
                });
            }
        };
        if (code == 1) {
            System.out.println("value is:" + Math.round(1000 / samplingRate));
            timer.schedule(task, 0, Math.round(1000 / samplingRate));

        } else {
            task.cancel();
        }

    }

    private void updateUIParams(double[] UI_params, double boatAngle,
            double yawRaw, double rollRaw, double pitchRaw,
            double angularVelocity, double angularVelocityZ,
            String side, int pos) {

        double yawRawCache;
        yawRawCache = UI_params_yaw_cache[pos];
        double velocityLast = UI_params[15];

        UI_params[20] = rollRaw;
        UI_params[21] = pitchRaw;
        UI_params[22] = yawRaw;

        if (side.equals("left")) {

            // {yaw[0], txYaw[1], avMin[2], avMax[3], txDegreeFwd[4], txDegreeBwd[5],
            // Roll[6], Pitch[7], YawMin[8], YawMax[9], YawAbs[10],
            // YawBeg[11], YawEnd[12], YawDuration[13], DynamicData[14],
            // StrokeVelocityLast[15]}
            // StrokeTimeLast[16], instantBoatSpeed[17], autoSplitCorrect[18], wattage[19]}

            double roll = -rollRaw + correctionPeddle;
            double pitch = -pitchRaw;
            double yaw = -yawRaw + correctionLeft + boatAngle + correctionLeftSecondary;
            double txYaw = 180 - yawRaw + boatAngle + correctionLeftSecondary;

            double velocityZeroMarker = 0;
            double strokeTimeNow = 0;

            double localAv_0;
            double localAv_1;
            double dynamicAV;

            try {
                dynamicAV = yawRaw;
            } catch (ArrayIndexOutOfBoundsException e) {
                dynamicAV = 0;
            }

            localAv_0 = -(dynamicAV - yawRawCache);

            if (localAv_0 > 300) {
                localAv_1 = localAv_0 - 360;
            } else if (localAv_0 < -300) {
                localAv_1 = localAv_0 + 360;
            } else {
                localAv_1 = -(dynamicAV - yawRawCache);
            }

            if (localAv_1 > 0) {
                CacheStrokeAV[pos] = CacheStrokeAV[pos] + localAv_1;
                CacheLengthStrokeAV[pos] = CacheLengthStrokeAV[pos] + 1;
            }

            double velocityNow;

            if (payloadMode == HeavyPayloadMode) {

                velocityNow = angularVelocityZ;

            } else {

                velocityNow = samplingRate * localAv_1;
            }

            double velocityZeroTest = velocityLast * velocityNow;

            if (velocityZeroTest < 0) {
                velocityZeroMarker = 1;
                strokeTimeNow = System.currentTimeMillis();
            }

            if (txYaw > 360) {
                txYaw = txYaw - 360;
            }
            if (txYaw < 0) {
                txYaw = txYaw + 360;
            }

            UI_params[0] = yaw;
            UI_params[1] = txYaw;
            UI_params[6] = roll;
            UI_params[7] = pitch;
            UI_params[15] = velocityNow;

            if (velocityZeroMarker == 1 &&
                    velocityNow < 0 &&
                    strokeTimeNow - UI_params[12] >= 1200
                    && strokeTimeNow - UI_params[16] > 300) {

                UI_params[2] = angularVelocity;
                UI_params[3] = 0;

                UI_params[12] = System.currentTimeMillis();
                UI_params[13] = (UI_params[12] - UI_params[11]) / 1000;
                UI_params[16] = System.currentTimeMillis();
                // System.out.println("durationTime:" + UI_params[13]);

                double avgStrokeAV = CacheStrokeAV[pos] / CacheLengthStrokeAV[pos];
                // double wattageCurrentStroke = wattage_p1_1*avgStrokeAV*speedSuppressionRatio
                // + wattage_p2_1;

                double wattageCurrentStroke = wattage_p1_4 * Math.pow(avgStrokeAV * wattageSuppressionRatio, 4)
                        + wattage_p2_4 * Math.pow(avgStrokeAV * wattageSuppressionRatio, 3)
                        + wattage_p3_4 * Math.pow(avgStrokeAV * wattageSuppressionRatio, 2)
                        + wattage_p4_4 * avgStrokeAV * wattageSuppressionRatio
                        + wattage_p5_4;

                if (wattageCurrentStroke < 0) {
                    wattageCurrentStroke = 0;
                } else if (wattageCurrentStroke > 600) {
                    wattageCurrentStroke = 600 + Math.random() * 10;
                }

                CacheStrokeAV[pos] = 0;
                CacheLengthStrokeAV[pos] = 0;
                // System.out.println("AV: " + avgStrokeAV + " current wattage " +
                // String.valueOf(pos) + ":" + wattageCurrentStroke);
                UI_params[19] = wattageCurrentStroke;

                if (mBoatSensorSwitchStatus == 1 && xsensDotBOAT != null) {

                    if (txYaw - 90 >= 0) {
                        UI_params[4] = txYaw - 90;
                    }

                    UI_params[8] = UI_params[4];

                    double UI_10_temp = Math.abs(UI_params[8] + UI_params[9]);
                    if (UI_10_temp >= 180) {
                        UI_params[10] = 360 - UI_10_temp;
                    } else {
                        UI_params[10] = UI_10_temp;
                    }

                } else {
                    UI_params[8] = yawRaw;
                    double UI_10_temp = Math.abs(UI_params[9] - UI_params[8]);
                    if (UI_10_temp >= 180) {
                        UI_params[10] = 360 - UI_10_temp;
                    } else {
                        UI_params[10] = UI_10_temp;
                    }

                    // new fwd degree calculation
                    double randomSeed = (avgStrokeAV * wattageSuppressionRatio - fwdSplitThreshSpeed)
                            / fwdSplitDivideFactor + Math.random() / fwdSplitRandomSeedDivideFactor;
                    double simulatedFwdDegree = UI_params[10] * (fwdSplitRatio + randomSeed);
                    UI_params[4] = simulatedFwdDegree;
                    UI_params[5] = UI_params[10] - UI_params[4];
                    // new fwd degree calculation
                }

            } else if (velocityZeroMarker == 1 &&
                    velocityNow > 0 &&
                    strokeTimeNow - UI_params[11] >= 1200 &&
                    strokeTimeNow - UI_params[16] > 300) {

                UI_params[3] = angularVelocity;
                UI_params[2] = 0;
                UI_params[11] = System.currentTimeMillis();
                UI_params[16] = System.currentTimeMillis();

                // UI_params[13] = UI_params[12] - UI_params[11];
                if (mBoatSensorSwitchStatus == 1 && xsensDotBOAT != null) {
                    if (90 - txYaw >= 0) {
                        UI_params[5] = 90 - txYaw;
                    }
                    UI_params[9] = UI_params[5];
                    // UI_params[10] = Math.abs(UI_params[8]+UI_params[9]);
                } else {
                    UI_params[9] = yawRaw;
                    // UI_params[10] = Math.abs(UI_params[9]-UI_params[8]);

                }

                // UI_params[5] = 90-txYaw;
            }

        } else if (side.equals("right")) {
            // {yaw[0], txYaw[1], avMin[2], avMax[3], txDegreeFwd[4], txDegreeBwd[5],
            // Roll[6], Pitch[7], YawMin[8], YawMax[9], YawAbs[10], YawBeg[11], YawEnd[12],
            // YawDuration[13]}
            double roll = -rollRaw - correctionPeddle;
            double pitch = pitchRaw + correctionRightPitch;
            double yaw = -yawRaw + correctionRight + boatAngle + correctionRightSecondary;
            double txYaw = 180 + yawRaw - boatAngle - correctionRightSecondary;

            double velocityZeroMarker = 0;
            double strokeTimeNow = 0;

            double localAv_0;
            double localAv_1;
            double dynamicAV;

            try {
                dynamicAV = yawRaw;
            } catch (ArrayIndexOutOfBoundsException e) {
                dynamicAV = 0;
            }

            localAv_0 = (dynamicAV - yawRawCache);

            if (localAv_0 > 300) {
                localAv_1 = localAv_0 - 360;
            } else if (localAv_0 < -300) {
                localAv_1 = localAv_0 + 360;
            } else {
                localAv_1 = (dynamicAV - yawRawCache);
            }

            if (localAv_1 > 0) {
                CacheStrokeAV[pos] = CacheStrokeAV[pos] + localAv_1;
                CacheLengthStrokeAV[pos] = CacheLengthStrokeAV[pos] + 1;
            }

            double velocityNow;

            if (payloadMode == HeavyPayloadMode) {
                velocityNow = -angularVelocityZ;
            } else {
                velocityNow = samplingRate * (localAv_1);
            }

            double velocityZeroTest = velocityLast * velocityNow;

            if (velocityZeroTest < 0) {
                velocityZeroMarker = 1;
                strokeTimeNow = System.currentTimeMillis();
            }

            if (txYaw > 360) {
                txYaw = txYaw - 360;
            }
            if (txYaw < 0) {
                txYaw = txYaw + 360;
            }

            UI_params[0] = yaw;
            UI_params[1] = txYaw;
            UI_params[6] = roll;
            UI_params[7] = pitch;
            UI_params[15] = velocityNow;

            if (velocityZeroMarker == 1 &&
                    velocityNow < 0 &&
                    strokeTimeNow - UI_params[12] >= 1200 &&
                    strokeTimeNow - UI_params[16] > 300) {

                UI_params[2] = angularVelocity;
                UI_params[3] = 0;

                UI_params[12] = System.currentTimeMillis();
                UI_params[13] = (UI_params[12] - UI_params[11]) / 1000;
                UI_params[16] = System.currentTimeMillis();

                double avgStrokeAV = CacheStrokeAV[pos] / CacheLengthStrokeAV[pos];
                double wattageCurrentStroke = wattage_p1_4 * Math.pow(avgStrokeAV * wattageSuppressionRatio, 4)
                        + wattage_p2_4 * Math.pow(avgStrokeAV * wattageSuppressionRatio, 3)
                        + wattage_p3_4 * Math.pow(avgStrokeAV * wattageSuppressionRatio, 2)
                        + wattage_p4_4 * avgStrokeAV * wattageSuppressionRatio
                        + wattage_p5_4;

                // double wattageCurrentStroke = wattage_p1_1*avgStrokeAV*speedSuppressionRatio
                // + wattage_p2_1;

                if (wattageCurrentStroke < 0) {
                    wattageCurrentStroke = 0;
                } else if (wattageCurrentStroke > 600) {
                    wattageCurrentStroke = 600 + Math.random() * 10;
                }

                CacheStrokeAV[pos] = 0;
                CacheLengthStrokeAV[pos] = 0;

                // System.out.println("AV: " + avgStrokeAV + " current wattage " +
                // String.valueOf(pos) + ":" + wattageCurrentStroke);
                UI_params[19] = wattageCurrentStroke;

                // System.out.println("durationTime:" + UI_params[13]);
                if (mBoatSensorSwitchStatus == 1 && xsensDotBOAT != null) {

                    if (txYaw - 90 >= 0) {
                        UI_params[4] = txYaw - 90;
                    }

                    UI_params[8] = UI_params[4];
                    double UI_10_temp = Math.abs(UI_params[8] + UI_params[9]);
                    if (UI_10_temp >= 180) {
                        UI_params[10] = 360 - UI_10_temp;
                    } else {
                        UI_params[10] = UI_10_temp;
                    }

                } else {
                    UI_params[8] = yawRaw;
                    double UI_10_temp = Math.abs(UI_params[9] - UI_params[8]);
                    if (UI_10_temp >= 180) {
                        UI_params[10] = 360 - UI_10_temp;
                    } else {
                        UI_params[10] = UI_10_temp;
                    }

                    double randomSeed = (avgStrokeAV * wattageSuppressionRatio - fwdSplitThreshSpeed)
                            / fwdSplitDivideFactor + Math.random() / fwdSplitRandomSeedDivideFactor;
                    double simulatedFwdDegree = UI_params[10] * (fwdSplitRatio + randomSeed);
                    UI_params[4] = simulatedFwdDegree;
                    UI_params[5] = UI_params[10] - UI_params[4];

                }

            } else if (velocityZeroMarker == 1 &&
                    velocityNow > 0 &&
                    strokeTimeNow - UI_params[11] >= 1200 &&
                    strokeTimeNow - UI_params[16] > 300) {
                UI_params[3] = angularVelocity;
                UI_params[2] = 0;

                if (90 - txYaw >= 0) {
                    UI_params[5] = 90 - txYaw;
                }

                UI_params[11] = System.currentTimeMillis();
                UI_params[16] = System.currentTimeMillis();

                // UI_params[13] = UI_params[12] - UI_params[11];
                if (mBoatSensorSwitchStatus == 1 && xsensDotBOAT != null) {
                    UI_params[9] = UI_params[5];
                    // UI_params[10] = Math.abs(UI_params[8]+UI_params[9]);

                } else {
                    UI_params[9] = yawRaw;
                    // UI_params[10] = Math.abs(UI_params[9]-UI_params[8]);

                }
            }

        }

        UI_params_yaw_cache[pos] = yawRaw;
    }

    public void phoneLoggerStandalone(int startStatus) {

        // loggerPhoneTimer = new Timer();
        // loggerPhoneTask = new TimerTask() {
        // @Override
        // public void run() {
        //
        // if (loggerPhone!=null){
        //
        // if (packetCounterCache != packetCounter){
        //
        // try {
        // loggerPhone.writeNext(new String[]{
        // String.valueOf(packetCounter),
        // (String) mStrokeRate.getText(),
        // (String) boatSpeedForLog,
        // String.valueOf(mBoatYaw.getRotation()),
        // String.valueOf(mBoatRoll.getRotation()),
        // String.valueOf(HR_params_cache[0]),
        // String.valueOf(HR_params_cache[1]),
        // String.valueOf(HR_params_cache[2]),
        // String.valueOf(HR_params_cache[3]),
        // String.valueOf(HR_params_cache[4]),
        // String.valueOf(HR_params_cache[5]),
        // String.valueOf(HR_params_cache[6]),
        // String.valueOf(HR_params_cache[7]),
        // (String) distanceTab.getText(),
        // String.valueOf(correctionLeftSecondary),
        // String.valueOf(correctionRightSecondary),
        // });
        //
        // }catch (Exception e) {
        // loggerPhone.writeNext(new String[]{
        // String.valueOf(packetCounter),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // String.valueOf(0),
        // });
        // }
        //
        // packetCounterCache = packetCounter;
        //
        // }
        // }
        // }
        // };
        //
        // if (startStatus == 1){
        // loggerPhoneTimer.schedule(loggerPhoneTask,0,31);
        //
        // }else{
        // loggerPhoneTask.cancel();
        // }

    }

    @Override
    public void onXsensDotDataChanged(String s, XsensDotData xsensDotData) {

        if (activationStatus == 1) {

            int route = -1;
            String side = "";

            double[] eulerAngles = xsensDotData.getEuler();
            double[] angularVelocity = new double[] { 0, 0, 0 };
            packetCounter = xsensDotData.getPacketCounter();
            // System.out.println("PacketCounter________________________" + packetCounter);

            for (int i = 0; i < 8; i++) {
                if (s.equals(String.valueOf(mPassThroughList.get(i)))) {
                    route = i;
                    if (route < 4) {
                        side = "left";
                    } else {
                        side = "right";
                    }

                    if (payloadMode == HeavyPayloadMode) {
                        angularVelocity = xsensDotData.getGyr();
                    }
                    // else{
                    // angularVelocity[0] = -samplingRate*(eulerAngles[0] -
                    // euler_cache_for_lightLoad_0[route]);
                    // angularVelocity[1] = -samplingRate*(eulerAngles[1] -
                    // euler_cache_for_lightLoad_1[route]);
                    // angularVelocity[2] = -samplingRate*(eulerAngles[2] -
                    // euler_cache_for_lightLoad_2[route]);
                    // euler_cache_for_lightLoad_0[route] = eulerAngles[0];
                    // euler_cache_for_lightLoad_1[route] = eulerAngles[1];
                    // euler_cache_for_lightLoad_2[route] = eulerAngles[2];
                    // }
                    // dotsLastUpdateTime[route] = System.currentTimeMillis();
                    // packetCounterSet[route] = packetCounter;

                    updateUIParams(UI_params_set.get(route), boatAngle,
                            eulerAngles[2], eulerAngles[0], eulerAngles[1],
                            angularVelocity[0], angularVelocity[2], side, route);

                    // if (packetCounter > packetCounterCache && mStartStatus ==1 &&
                    // loggerList_sub.get(route) != null) {
                    // packetCounterCache = packetCounter;
                    //
                    // try {
                    // loggerList_sub.get(route).writeNext(new String[]{
                    // String.valueOf(packetCounter), String.valueOf(System.currentTimeMillis()),
                    // String.valueOf(eulerAngles[0]), String.valueOf(eulerAngles[1]),
                    // String.valueOf(eulerAngles[2])
                    // });
                    // }catch (Exception e){
                    //
                    // loggerList_sub.get(route).writeNext(new String[]{
                    // String.valueOf(packetCounter), String.valueOf(0),
                    // String.valueOf(0), String.valueOf(0), String.valueOf(0)
                    // });
                    //
                    // }
                    // }

                    try {
                        if (mStartStatus == 1) {
                            loggerList.get(route).update(xsensDotData);
                        }
                    } catch (Exception ignored) {

                    }
                    break;
                }
            }

            if (s.equals(String.valueOf(mPassThroughList.get(8)))) {
                boatAngle = eulerAngles[2];
                try {
                    if (mStartStatus == 1) {
                        loggerList.get(8).update(xsensDotData);
                    }

                } catch (Exception ignored) {

                }
            }

            if (mStartStatus == 1) {

                if (loggerPhone != null) {

                    if (packetCounter > packetCounterCache) {
                        packetCounterCache = packetCounter;

                        try {
                            loggerPhone.writeNext(new String[] {
                                    String.valueOf(packetCounter),
                                    (String) mStrokeRate.getText(),
                                    (String) boatSpeedForLog,
                                    String.valueOf(mBoatYaw.getRotation()),
                                    String.valueOf(mBoatRoll.getRotation()),
                                    String.valueOf(HR_params_cache[0]),
                                    String.valueOf(HR_params_cache[1]),
                                    String.valueOf(HR_params_cache[2]),
                                    String.valueOf(HR_params_cache[3]),
                                    String.valueOf(HR_params_cache[4]),
                                    String.valueOf(HR_params_cache[5]),
                                    String.valueOf(HR_params_cache[6]),
                                    String.valueOf(HR_params_cache[7]),
                                    (String) distanceTab.getText(),
                                    String.valueOf(correctionLeftSecondary),
                                    String.valueOf(correctionRightSecondary),
                                    String.valueOf(0),
                                    String.valueOf(mBoatAccelerationForLog),
                            });

                        } catch (Exception e) {
                            loggerPhone.writeNext(new String[] {
                                    String.valueOf(packetCounter),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                                    String.valueOf(0),
                            });

                        }
                    }
                }

            }

            //
            // if (s.equals(String.valueOf(mPassThroughList.get(0)))) {
            //
            // if (payloadMode == LightPayloadMode){
            // angularVelocity[0] = -30*(eulerAngles[0] - euler_cache_for_lightLoad_0[0]);
            // angularVelocity[1] = -30*(eulerAngles[1] - euler_cache_for_lightLoad_1[0]);
            // angularVelocity[2] = -30*(eulerAngles[2] - euler_cache_for_lightLoad_2[0]);
            // euler_cache_for_lightLoad_0[0] = eulerAngles[0];
            // euler_cache_for_lightLoad_1[0] = eulerAngles[1];
            // euler_cache_for_lightLoad_2[0] = eulerAngles[2];
            // }
            //
            // updateUIParams (UI_params_L1, boatAngle,
            // eulerAngles[2], eulerAngles[0], eulerAngles[1],
            // angularVelocity[0], angularVelocity[2],"left", 0);
            // try {
            // if (mStartStatus ==1 ){
            // loggerL1.update(xsensDotData);
            //
            // }
            // }catch (Exception ignored) {
            //
            // }
            // }
            //
            // if (s.equals(String.valueOf(mPassThroughList.get(1)))) {
            //
            // if (payloadMode == LightPayloadMode){
            // angularVelocity[0] = -30*(eulerAngles[0] - euler_cache_for_lightLoad_0[1]);
            // angularVelocity[1] = -30*(eulerAngles[1] - euler_cache_for_lightLoad_1[1]);
            // angularVelocity[2] = -30*(eulerAngles[2] - euler_cache_for_lightLoad_2[1]);
            // euler_cache_for_lightLoad_0[1] = eulerAngles[0];
            // euler_cache_for_lightLoad_1[1] = eulerAngles[1];
            // euler_cache_for_lightLoad_2[1] = eulerAngles[2];
            // }
            //
            // updateUIParams (UI_params_L2, boatAngle,
            // eulerAngles[2], eulerAngles[0], eulerAngles[1],
            // angularVelocity[0], angularVelocity[2],"left", 1);
            // try {
            // if (mStartStatus ==1 ){
            // loggerL2.update(xsensDotData);
            //
            // }
            // }catch (Exception ignored) {
            //
            // }
            // }
            // if (s.equals(String.valueOf(mPassThroughList.get(2)))) {
            //
            // if (payloadMode == LightPayloadMode){
            // angularVelocity[0] = -30*(eulerAngles[0] - euler_cache_for_lightLoad_0[2]);
            // angularVelocity[1] = -30*(eulerAngles[1] - euler_cache_for_lightLoad_1[2]);
            // angularVelocity[2] = -30*(eulerAngles[2] - euler_cache_for_lightLoad_2[2]);
            // euler_cache_for_lightLoad_0[2] = eulerAngles[0];
            // euler_cache_for_lightLoad_1[2] = eulerAngles[1];
            // euler_cache_for_lightLoad_2[2] = eulerAngles[2];
            // }
            //
            // updateUIParams (UI_params_L3, boatAngle,
            // eulerAngles[2], eulerAngles[0], eulerAngles[1],
            // angularVelocity[0], angularVelocity[2],"left", 2);
            // try {
            // if (mStartStatus ==1 ){
            // loggerL3.update(xsensDotData);
            //
            // }
            // }catch (Exception ignored) {
            //
            // }
            // }
            // if (s.equals(String.valueOf(mPassThroughList.get(3)))) {
            //
            // if (payloadMode == LightPayloadMode){
            // angularVelocity[0] = -30*(eulerAngles[0] - euler_cache_for_lightLoad_0[3]);
            // angularVelocity[1] = -30*(eulerAngles[1] - euler_cache_for_lightLoad_1[3]);
            // angularVelocity[2] = -30*(eulerAngles[2] - euler_cache_for_lightLoad_2[3]);
            // euler_cache_for_lightLoad_0[3] = eulerAngles[0];
            // euler_cache_for_lightLoad_1[3] = eulerAngles[1];
            // euler_cache_for_lightLoad_2[3] = eulerAngles[2];
            // }
            //
            // updateUIParams (UI_params_L4, boatAngle,
            // eulerAngles[2], eulerAngles[0], eulerAngles[1],
            // angularVelocity[0], angularVelocity[2],"left", 3);
            // try {
            // if (mStartStatus ==1 ){
            // loggerL4.update(xsensDotData);
            // }
            // }catch (Exception ignored) {
            //
            // }
            // }
            // if (s.equals(String.valueOf(mPassThroughList.get(4)))) {
            //
            // if (payloadMode == LightPayloadMode){
            // angularVelocity[0] = -30*(eulerAngles[0] - euler_cache_for_lightLoad_0[4]);
            // angularVelocity[1] = -30*(eulerAngles[1] - euler_cache_for_lightLoad_1[4]);
            // angularVelocity[2] = -30*(eulerAngles[2] - euler_cache_for_lightLoad_2[4]);
            // euler_cache_for_lightLoad_0[4] = eulerAngles[0];
            // euler_cache_for_lightLoad_1[4] = eulerAngles[1];
            // euler_cache_for_lightLoad_2[4] = eulerAngles[2];
            // }
            //
            // updateUIParams (UI_params_R1, boatAngle,
            // eulerAngles[2], eulerAngles[0], eulerAngles[1],
            // angularVelocity[0], angularVelocity[2],"right", 4);
            // try {
            // if (mStartStatus ==1 ){
            // loggerR1.update(xsensDotData);
            // }
            // }catch (Exception ignored) {
            //
            // }
            // }
            // if (s.equals(String.valueOf(mPassThroughList.get(5)))) {
            //
            // if (payloadMode == LightPayloadMode){
            // angularVelocity[0] = -30*(eulerAngles[0] - euler_cache_for_lightLoad_0[5]);
            // angularVelocity[1] = -30*(eulerAngles[1] - euler_cache_for_lightLoad_1[5]);
            // angularVelocity[2] = -30*(eulerAngles[2] - euler_cache_for_lightLoad_2[5]);
            // euler_cache_for_lightLoad_0[5] = eulerAngles[0];
            // euler_cache_for_lightLoad_1[5] = eulerAngles[1];
            // euler_cache_for_lightLoad_2[5] = eulerAngles[2];
            // }
            //
            // updateUIParams (UI_params_R2, boatAngle,
            // eulerAngles[2], eulerAngles[0], eulerAngles[1],
            // angularVelocity[0], angularVelocity[2],"right", 5);
            // try {
            // if (mStartStatus ==1 ){
            // loggerR2.update(xsensDotData);
            // }
            // }catch (Exception ignored) {
            //
            // }
            // }
            // if (s.equals(String.valueOf(mPassThroughList.get(6)))) {
            //
            // if (payloadMode == LightPayloadMode){
            // angularVelocity[0] = -30*(eulerAngles[0] - euler_cache_for_lightLoad_0[6]);
            // angularVelocity[1] = -30*(eulerAngles[1] - euler_cache_for_lightLoad_1[6]);
            // angularVelocity[2] = -30*(eulerAngles[2] - euler_cache_for_lightLoad_2[6]);
            // euler_cache_for_lightLoad_0[6] = eulerAngles[0];
            // euler_cache_for_lightLoad_1[6] = eulerAngles[1];
            // euler_cache_for_lightLoad_2[6] = eulerAngles[2];
            // }
            //
            // updateUIParams (UI_params_R3, boatAngle,
            // eulerAngles[2], eulerAngles[0], eulerAngles[1],
            // angularVelocity[0], angularVelocity[2],"right", 6);
            // try {
            // if (mStartStatus ==1 ){
            // loggerR3.update(xsensDotData);
            // }
            // }catch (Exception ignored) {
            //
            // }
            // }
            // if (s.equals(String.valueOf(mPassThroughList.get(7)))) {
            // if (payloadMode == LightPayloadMode){
            // angularVelocity[0] = -30*(eulerAngles[0] - euler_cache_for_lightLoad_0[7]);
            // angularVelocity[1] = -30*(eulerAngles[1] - euler_cache_for_lightLoad_1[7]);
            // angularVelocity[2] = -30*(eulerAngles[2] - euler_cache_for_lightLoad_2[7]);
            // euler_cache_for_lightLoad_0[7] = eulerAngles[0];
            // euler_cache_for_lightLoad_1[7] = eulerAngles[1];
            // euler_cache_for_lightLoad_2[7] = eulerAngles[2];
            // }
            //
            // updateUIParams (UI_params_R4, boatAngle,
            // eulerAngles[2], eulerAngles[0], eulerAngles[1],
            // angularVelocity[0], angularVelocity[2],"right", 7);
            // try {
            // if (mStartStatus ==1 ){
            // loggerR4.update(xsensDotData);
            // }
            // }catch (Exception ignored) {
            //
            // }
            // }
        }
    }

    private void updateXsensDevice() {

        for (int i = 0; i < 9; i++) {

            if (mPassThroughList.get(i) != null) {
                numOfSelectedDots++;
                switch (i) {
                    case 0:
                        xsensDotL1 = new XsensDotDevice(getApplicationContext(), mPassThroughList.get(i),
                                Dashboard.this);
                        mXsensDeviceList.add(xsensDotL1);
                        break;

                    case 1:
                        xsensDotL2 = new XsensDotDevice(getApplicationContext(), mPassThroughList.get(i),
                                Dashboard.this);
                        mXsensDeviceList.add(xsensDotL2);
                        break;

                    case 2:
                        xsensDotL3 = new XsensDotDevice(getApplicationContext(), mPassThroughList.get(i),
                                Dashboard.this);
                        mXsensDeviceList.add(xsensDotL3);
                        break;

                    case 3:
                        xsensDotL4 = new XsensDotDevice(getApplicationContext(), mPassThroughList.get(i),
                                Dashboard.this);
                        mXsensDeviceList.add(xsensDotL4);
                        break;

                    case 4:
                        xsensDotR1 = new XsensDotDevice(getApplicationContext(), mPassThroughList.get(i),
                                Dashboard.this);
                        mXsensDeviceList.add(xsensDotR1);
                        break;

                    case 5:
                        xsensDotR2 = new XsensDotDevice(getApplicationContext(), mPassThroughList.get(i),
                                Dashboard.this);
                        mXsensDeviceList.add(xsensDotR2);
                        break;

                    case 6:
                        xsensDotR3 = new XsensDotDevice(getApplicationContext(), mPassThroughList.get(i),
                                Dashboard.this);
                        mXsensDeviceList.add(xsensDotR3);
                        break;

                    case 7:
                        xsensDotR4 = new XsensDotDevice(getApplicationContext(), mPassThroughList.get(i),
                                Dashboard.this);
                        mXsensDeviceList.add(xsensDotR4);
                        break;

                    case 8:
                        xsensDotBOAT = new XsensDotDevice(getApplicationContext(), mPassThroughList.get(i),
                                Dashboard.this);
                        mXsensDeviceList.add(xsensDotBOAT);
                        break;

                }

            } else {

                mXsensDeviceList.add(null);

            }

        }

    }

    private void connectDots() {
        activateDots(0);
        // mStart.setAlpha(1f);

        numOfConnectedDots = 0;
        mXsensDeviceListIndex = 0;

        if (threadConnectDots != null) {
            threadConnectDots.interrupt();
        }

        threadConnectDots = new Thread(new Runnable() {
            @Override
            public void run() {

                while (mXsensDeviceListIndex < mXsensDeviceList.size()) {

                    if (mXsensDeviceList.get(mXsensDeviceListIndex) != null) {
                        mXsensDeviceList.get(mXsensDeviceListIndex).connect();
                        connectionStart[mXsensDeviceListIndex] = System.currentTimeMillis();
                        suspended = true;
                    } else {

                        mXsensDeviceListIndex++;
                        suspended = false;
                    }

                    synchronized (threadConnectDots) {
                        try {
                            if (suspended) {
                                threadConnectDots.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            threadConnectDots.interrupt();
                            break;
                        }
                    }

                }

                threadConnectDots.interrupt();

            }
        });

        threadConnectDots.start();

        // if (xsensDotL1 != null){
        // xsensDotL1.connect();
        // }
        // if (xsensDotL2 != null){
        // xsensDotL2.connect();
        // connectionStart[1] = System.currentTimeMillis();
        //
        // }
        // if (xsensDotL3 != null){
        // xsensDotL3.connect();
        // connectionStart[2] = System.currentTimeMillis();
        //
        // }
        // if (xsensDotL4 != null){
        // xsensDotL4.connect();
        // connectionStart[3] = System.currentTimeMillis();
        //
        // }
        // if (xsensDotR1 != null){
        // xsensDotR1.connect();
        // connectionStart[4] = System.currentTimeMillis();
        //
        // }
        // if (xsensDotR2 != null){
        // xsensDotR2.connect();
        // connectionStart[5] = System.currentTimeMillis();
        //
        // }
        // if (xsensDotR3 != null){
        // xsensDotR3.connect();
        // connectionStart[6] = System.currentTimeMillis();
        //
        // }
        // if (xsensDotR4 != null){
        // xsensDotR4.connect();
        // connectionStart[7] = System.currentTimeMillis();
        //
        // }
        // if (xsensDotBOAT != null){
        // xsensDotBOAT.connect();
        // connectionStart[8] = System.currentTimeMillis();
        //
        // }

    }

    private void disconnectDots() {

        mBtnActivate.setBackgroundResource(R.drawable.button_11);

        if (Objects.equals(Lang, "eng")) {
            mBtnActivate.setText("Activate");

        } else {
            mBtnActivate.setText("激活传感器");

        }

        if (xsensDotL1 != null) {
            xsensDotL1.disconnect();
        }
        if (xsensDotL2 != null) {
            xsensDotL2.disconnect();
        }
        if (xsensDotL3 != null) {
            xsensDotL3.disconnect();
        }
        if (xsensDotL4 != null) {
            xsensDotL4.disconnect();
        }
        if (xsensDotR1 != null) {
            xsensDotR1.disconnect();
        }
        if (xsensDotR2 != null) {
            xsensDotR2.disconnect();
        }
        if (xsensDotR3 != null) {
            xsensDotR3.disconnect();
        }
        if (xsensDotR4 != null) {
            xsensDotR4.disconnect();
        }
        if (xsensDotBOAT != null) {
            xsensDotBOAT.disconnect();
        }
    }

    private void resetHeading() {

        correctionLeftSecondary = 0;
        correctionRightSecondary = 0;

        if (xsensDotL1 != null) {
            xsensDotL1.resetHeading();
        }
        if (xsensDotL2 != null) {
            xsensDotL2.resetHeading();
        }
        if (xsensDotL3 != null) {
            xsensDotL3.resetHeading();
        }
        if (xsensDotL4 != null) {
            xsensDotL4.resetHeading();
        }
        if (xsensDotR1 != null) {
            xsensDotR1.resetHeading();
        }
        if (xsensDotR2 != null) {
            xsensDotR2.resetHeading();
        }
        if (xsensDotR3 != null) {
            xsensDotR3.resetHeading();
        }
        if (xsensDotR4 != null) {
            xsensDotR4.resetHeading();
        }
        if (xsensDotBOAT != null) {
            xsensDotBOAT.resetHeading();
        }
    }

    private void activateDots(int code) {

        if (code == 1) {

            double startTime = System.currentTimeMillis();
            dotsLastUpdateTime = new double[] { startTime, startTime, startTime, startTime, startTime, startTime,
                    startTime, startTime, startTime };

            if (payloadMode == LightPayloadMode) {
                payloadMonitor.setText("OE");
            } else {
                payloadMonitor.setText("CM1");
            }

            activationCode = 0;
            activationStatus = 1;

            if (xsensDotL1 != null) {
                xsensDotL1.startMeasuring();
                // xsensDotL1.setMeasurementMode(payloadMode);

            }
            if (xsensDotL2 != null) {
                xsensDotL2.startMeasuring();
                // xsensDotL2.setMeasurementMode(payloadMode);

            }
            if (xsensDotL3 != null) {
                xsensDotL3.startMeasuring();
                // xsensDotL3.setMeasurementMode(payloadMode);

            }
            if (xsensDotL4 != null) {
                xsensDotL4.startMeasuring();
                // xsensDotL4.setMeasurementMode(payloadMode);

            }
            if (xsensDotR1 != null) {
                xsensDotR1.startMeasuring();
                // xsensDotR1.setMeasurementMode(payloadMode);

            }
            if (xsensDotR2 != null) {
                xsensDotR2.startMeasuring();
                // xsensDotR2.setMeasurementMode(payloadMode);

            }
            if (xsensDotR3 != null) {
                xsensDotR3.startMeasuring();
                // xsensDotR3.setMeasurementMode(payloadMode);

            }
            if (xsensDotR4 != null) {
                xsensDotR4.startMeasuring();
                // xsensDotR4.setMeasurementMode(payloadMode);

            }
            if (xsensDotBOAT != null) {
                xsensDotBOAT.startMeasuring();
                // xsensDotBOAT.setMeasurementMode(payloadMode);

            }

            mStart.setEnabled(true);
            mSliderResetSecondary.setEnabled(true);

            mButtonStatus[0] = true;
            mButtonStatus[2] = true;

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Looper.prepare();
                    mBtnActivate.setBackgroundResource(R.drawable.button_12);

                    if (Objects.equals(Lang, "eng")) {
                        mBtnActivate.setText("Deactivate");

                    } else {
                        mBtnActivate.setText("传感器休眠");

                    }
                    mBtnResetHeading.animate().alpha(1f).setDuration(200).start();
                    mStart.animate().alpha(1f).setDuration(200).start();
                    mSliderResetSecondary.animate().alpha(1f).setDuration(200).start();
                    Looper.loop();
                }
            }, 10);

        }

        if (code == 0) {

            activationCode = 1;
            activationStatus = 0;

            if (xsensDotL1 != null) {
                xsensDotL1.stopMeasuring();
            }
            if (xsensDotL2 != null) {
                xsensDotL2.stopMeasuring();
            }
            if (xsensDotL3 != null) {
                xsensDotL3.stopMeasuring();
            }
            if (xsensDotL4 != null) {
                xsensDotL4.stopMeasuring();
            }
            if (xsensDotR1 != null) {
                xsensDotR1.stopMeasuring();
            }
            if (xsensDotR2 != null) {
                xsensDotR2.stopMeasuring();
            }
            if (xsensDotR3 != null) {
                xsensDotR3.stopMeasuring();
            }
            if (xsensDotR4 != null) {
                xsensDotR4.stopMeasuring();
            }
            if (xsensDotBOAT != null) {
                xsensDotBOAT.stopMeasuring();
            }

            mStart.setEnabled(false);
            mSliderResetSecondary.setEnabled(false);

            mButtonStatus[0] = false;
            mButtonStatus[2] = false;

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Looper.prepare();
                    mBtnActivate.setBackgroundResource(R.drawable.button_11);

                    if (Objects.equals(Lang, "eng")) {
                        mBtnActivate.setText("Activate");

                    } else {
                        mBtnActivate.setText("激活传感器");

                    }
                    mBtnResetHeading.animate().alpha(0f).setDuration(200).start();
                    mStart.animate().alpha(0f).setDuration(200).start();
                    mSliderResetSecondary.animate().alpha(0f).setDuration(200).start();

                    Looper.loop();
                }
            }, 10);

        }
    }

    private void updateSecondaryData() {

        updateSecondaryDataTask = new TimerTask() {
            @Override
            public void run() {

                if (mStartStatus == 1) {
                    getSecondaryData();
                }

            }
        };

        updateSecondaryTimer = new Timer();
        updateSecondaryTimer.schedule(connectionEnhance, 0, 20);
    }

    private void connectionGuard() {

        connectionEnhance = new TimerTask() {

            @Override
            public void run() {

                // if (mHRfirstConnect == 1){
                // try{
                // for (int i=0; i< mListBelts.size(); i++){
                //
                // if (mListBelts.get(i)!=null) {
                // BleConnect localConnect = new BleConnect(Dashboard.this, mListBelts.get(i));
                //
                // if (localConnect.getConnectionState().getValue().equals("STATE_CONNECTED")){
                //
                // }else{
                // mConnectedHRMs --;
                // localConnect.Connect();
                // }
                // }
                // }
                //
                // }catch (Exception e) {
                // }
                // }

                try {

                    numOfConnectedDots = 0;

                    for (int i = 0; i < mXsensDeviceList.size(); i++) {

                        if (mXsensDeviceList.get(i) != null) {

                            // if (mXsensDeviceList.get(i).getConnectionState() ==
                            // XsensDotDevice.CONN_STATE_CONNECTING) {
                            //
                            // mTvPassThroughNameList.get(i).setBackgroundResource(R.drawable.gradient_red);
                            //
                            // connectionTimeElapsed[i] = System.currentTimeMillis() - connectionStart[i];
                            // System.out.println("dot serial:" + i + "__elpased:" +
                            // connectionTimeElapsed[i]);
                            //
                            // if (connectionTimeElapsed[i] >= connectionTimeout){
                            //
                            // System.out.println("initialization timeout:" + i + "__elpased:" +
                            // connectionTimeElapsed[i]);
                            // mXsensDeviceList.get(i).cancelReconnecting();
                            //// mXsensDeviceList.get(i).disconnect();
                            //
                            // }
                            //
                            // }else if (mXsensDeviceList.get(i).getConnectionState() ==
                            // XsensDotDevice.CONN_STATE_DISCONNECTED){
                            //
                            // System.out.println("reconnect start:" + i);
                            //
                            // connectionTimeElapsed[i] = 0;
                            // connectionStart[i] = System.currentTimeMillis();
                            // mXsensDeviceList.get(i).connect();
                            //
                            // }else if (mXsensDeviceList.get(i).isInitDone()){
                            //
                            // System.out.println("initialization done:" +
                            // mXsensDeviceList.get(i).getAddress());
                            //
                            // numOfConnectedDots ++;
                            //
                            // }

                            if (mXsensDeviceList.get(i).isInitDone()) {

                                // System.out.println("initialization done:" +
                                // mXsensDeviceList.get(i).getAddress());

                                numOfConnectedDots++;

                            }
                        }
                    }

                    if (numOfConnectedDots == numOfSelectedDots) {

                        // if (reconnectNumber<reconnectLoop){
                        // mBtnReconnect.performLongClick();
                        // reconnectNumber++;
                        // }else{
                        //
                        // reconnectNumber = 0;
                        //
                        // }

                        mBtnActivate.setAlpha(1f);
                        // if (mStartStatus == 1){
                        //// watchman(1);
                        // }
                        // System.out.println("ACTIVATION BUTTON");
                        // System.out.println("connectedDots:" + numOfConnectedDots);
                        // System.out.println("selectedDots:" + numOfSelectedDots);

                    } else {

                        mBtnResetHeading.setAlpha(0f);
                        mBtnActivate.setAlpha(0f);
                        // System.out.println("ACTIVATION BUTTON OFF");
                        // System.out.println("connectedDots 2nd loop:" + numOfConnectedDots);
                        // System.out.println("selectedDots 2nd loop:" + numOfSelectedDots);

                    }

                    // System.out.println("Time Reference:" + System.currentTimeMillis());

                } catch (Exception e) {
                    System.out.println("connectedDots:" + "Error");
                    e.printStackTrace();
                }

            }
        };

        connectionEnhanceTimer = new Timer();
        connectionEnhanceTimer.schedule(connectionEnhance, 1000, 2000);

    }

    private void refreshBotList() {

        for (int i = 0; i < 9; i++) {
            String name = getDotName(String.valueOf(mPassThroughList.get(i)));
            if (name != null) {
                DotsSelected++;
                mTvPassThroughNameList.get(i).setText(name);
            }
        }

        if (DotsSelected > loadControl) {
            payloadMode = LightPayloadMode;
            samplingRate = samplingRateLowRefresh;
            // payloadMonitor.setText("OE");
        } else {
            payloadMode = HeavyPayloadMode;
            // payloadMonitor.setText("CM1");
        }

        System.out.println("total number of DOT connection: " + DotsSelected);

    }

    @Nullable
    private String getDotName(String address) {

        if (address != "null") {
            String name_1 = address.substring(address.length() - 2);
            String name_0 = address.substring(address.length() - 5, address.length() - 3);
            String name = name_0 + name_1;
            return name;

        }
        return null;
    }

    @Override
    public void onXsensDotConnectionChanged(String s, int state) {

        // if (state == XsensDotDevice.CONN_STATE_CONNECTING){
        //
        // if(xsensDotL1 != null) {
        // if (s.equals(xsensDotL1.getAddress())) {
        // mTvPassThroughNameList.get(0).setBackgroundResource(R.drawable.gradient_red);
        // mFrame_L1.setAlpha(1f);
        // mOarL1.setAlpha(0.7f);
        // mOarL1_roll.setAlpha(0.7f);
        // mOarL1_pitch.setAlpha(0.7f);
        //
        // }
        // }
        // if(xsensDotL2 != null){
        // if (s.equals(xsensDotL2.getAddress())){
        // mTvPassThroughNameList.get(1).setBackgroundResource(R.drawable.gradient_red);
        // mFrame_L2.setAlpha(1f);
        // mOarL2.setAlpha(0.7f);
        // mOarL2_roll.setAlpha(0.7f);
        // mOarL2_pitch.setAlpha(0.7f);
        // }
        // }
        // if(xsensDotL3 != null){
        // if (s.equals(xsensDotL3.getAddress())){
        // mTvPassThroughNameList.get(2).setBackgroundResource(R.drawable.gradient_red);
        // mFrame_L3.setAlpha(1f);
        // mOarL3.setAlpha(0.7f);
        // mOarL3_roll.setAlpha(0.7f);
        // mOarL3_pitch.setAlpha(0.7f);
        // }
        // }
        // if(xsensDotL4 != null) {
        // if (s.equals(xsensDotL4.getAddress())) {
        // mTvPassThroughNameList.get(3).setBackgroundResource(R.drawable.gradient_red);
        // mFrame_L4.setAlpha(1f);
        // mOarL4.setAlpha(0.7f);
        // mOarL4_roll.setAlpha(0.7f);
        // mOarL4_pitch.setAlpha(0.7f);
        // }
        // }
        // if(xsensDotR1 != null) {
        // if (s.equals(xsensDotR1.getAddress())) {
        // mTvPassThroughNameList.get(4).setBackgroundResource(R.drawable.gradient_red);
        // mFrame_R1.setAlpha(1f);
        // mOarR1.setAlpha(0.7f);
        // mOarR1_roll.setAlpha(0.7f);
        // mOarR1_pitch.setAlpha(0.7f);
        // }
        // }
        // if(xsensDotR2 != null){
        // if (s.equals(xsensDotR2.getAddress())){
        // mTvPassThroughNameList.get(5).setBackgroundResource(R.drawable.gradient_red);
        // mFrame_R2.setAlpha(1f);
        // mOarR2.setAlpha(0.7f);
        // mOarR2_roll.setAlpha(0.7f);
        // mOarR2_pitch.setAlpha(0.7f);
        // }
        // }
        // if(xsensDotR3 != null){
        // if (s.equals(xsensDotR3.getAddress())){
        // mTvPassThroughNameList.get(6).setBackgroundResource(R.drawable.gradient_red);
        // mFrame_R3.setAlpha(1f);
        // mOarR3.setAlpha(0.7f);
        // mOarR3_roll.setAlpha(0.7f);
        // mOarR3_pitch.setAlpha(0.7f);
        // }
        // }
        // if(xsensDotR4 != null) {
        // if (s.equals(xsensDotR4.getAddress())) {
        // mTvPassThroughNameList.get(7).setBackgroundResource(R.drawable.gradient_red);
        // mFrame_R4.setAlpha(1f);
        // mOarR4.setAlpha(0.7f);
        // mOarR4_roll.setAlpha(0.7f);
        // mOarR4_pitch.setAlpha(0.7f);
        // }
        // }
        // if(xsensDotBOAT != null) {
        // if (s.equals(xsensDotBOAT.getAddress())) {
        // mTvPassThroughNameList.get(8).setBackgroundResource(R.drawable.gradient_red);
        //
        // }
        // }
        // }

        if (state == XsensDotDevice.CONN_STATE_CONNECTED) {

            if (xsensDotL1 != null) {
                if (s.equals(xsensDotL1.getAddress())) {
                    mTvPassThroughNameList.get(0).setBackgroundResource(R.drawable.gradient_orange);
                    mFrame_L1.setAlpha(1f);
                    mOarL1.setAlpha(0.7f);
                    mOarL1_roll.setAlpha(0.7f);
                    mOarL1_pitch.setAlpha(0.7f);

                }
            }
            if (xsensDotL2 != null) {
                if (s.equals(xsensDotL2.getAddress())) {
                    mTvPassThroughNameList.get(1).setBackgroundResource(R.drawable.gradient_orange);
                    mFrame_L2.setAlpha(1f);
                    mOarL2.setAlpha(0.7f);
                    mOarL2_roll.setAlpha(0.7f);
                    mOarL2_pitch.setAlpha(0.7f);
                }
            }
            if (xsensDotL3 != null) {
                if (s.equals(xsensDotL3.getAddress())) {
                    mTvPassThroughNameList.get(2).setBackgroundResource(R.drawable.gradient_orange);
                    mFrame_L3.setAlpha(1f);
                    mOarL3.setAlpha(0.7f);
                    mOarL3_roll.setAlpha(0.7f);
                    mOarL3_pitch.setAlpha(0.7f);
                }
            }
            if (xsensDotL4 != null) {
                if (s.equals(xsensDotL4.getAddress())) {
                    mTvPassThroughNameList.get(3).setBackgroundResource(R.drawable.gradient_orange);
                    mFrame_L4.setAlpha(1f);
                    mOarL4.setAlpha(0.7f);
                    mOarL4_roll.setAlpha(0.7f);
                    mOarL4_pitch.setAlpha(0.7f);
                }
            }
            if (xsensDotR1 != null) {
                if (s.equals(xsensDotR1.getAddress())) {
                    mTvPassThroughNameList.get(4).setBackgroundResource(R.drawable.gradient_orange);
                    mFrame_R1.setAlpha(1f);
                    mOarR1.setAlpha(0.7f);
                    mOarR1_roll.setAlpha(0.7f);
                    mOarR1_pitch.setAlpha(0.7f);
                }
            }
            if (xsensDotR2 != null) {
                if (s.equals(xsensDotR2.getAddress())) {
                    mTvPassThroughNameList.get(5).setBackgroundResource(R.drawable.gradient_orange);
                    mFrame_R2.setAlpha(1f);
                    mOarR2.setAlpha(0.7f);
                    mOarR2_roll.setAlpha(0.7f);
                    mOarR2_pitch.setAlpha(0.7f);
                }
            }
            if (xsensDotR3 != null) {
                if (s.equals(xsensDotR3.getAddress())) {
                    mTvPassThroughNameList.get(6).setBackgroundResource(R.drawable.gradient_orange);
                    mFrame_R3.setAlpha(1f);
                    mOarR3.setAlpha(0.7f);
                    mOarR3_roll.setAlpha(0.7f);
                    mOarR3_pitch.setAlpha(0.7f);
                }
            }
            if (xsensDotR4 != null) {
                if (s.equals(xsensDotR4.getAddress())) {
                    mTvPassThroughNameList.get(7).setBackgroundResource(R.drawable.gradient_orange);
                    mFrame_R4.setAlpha(1f);
                    mOarR4.setAlpha(0.7f);
                    mOarR4_roll.setAlpha(0.7f);
                    mOarR4_pitch.setAlpha(0.7f);
                }
            }
            if (xsensDotBOAT != null) {
                if (s.equals(xsensDotBOAT.getAddress())) {
                    mTvPassThroughNameList.get(8).setBackgroundResource(R.drawable.gradient_orange);

                }
            }
        }

        if (state == XsensDotDevice.CONN_STATE_DISCONNECTED) {
            if (xsensDotL1 != null) {
                if (s.equals(xsensDotL1.getAddress())) {
                    mTvPassThroughNameList.get(0).setBackgroundResource(R.drawable.gradient_light);
                    numOfConnectedDots--;
                    mFrame_L1.setAlpha(0f);
                    mOarL1.setAlpha(0.0f);
                    mOarL1_roll.setAlpha(0f);
                    mOarL1_pitch.setAlpha(0f);
                }
            }
            if (xsensDotL2 != null) {
                if (s.equals(xsensDotL2.getAddress())) {
                    mTvPassThroughNameList.get(1).setBackgroundResource(R.drawable.gradient_light);
                    numOfConnectedDots--;
                    mFrame_L2.setAlpha(0f);
                    mOarL2.setAlpha(0.0f);
                    mOarL2_roll.setAlpha(0f);
                    mOarL2_pitch.setAlpha(0f);
                }
            }
            if (xsensDotL3 != null) {
                if (s.equals(xsensDotL3.getAddress())) {
                    mTvPassThroughNameList.get(2).setBackgroundResource(R.drawable.gradient_light);
                    numOfConnectedDots--;
                    mFrame_L3.setAlpha(0f);
                    mOarL3.setAlpha(0.0f);
                    mOarL3_roll.setAlpha(0f);
                    mOarL3_pitch.setAlpha(0f);
                }
            }
            if (xsensDotL4 != null) {
                if (s.equals(xsensDotL4.getAddress())) {
                    mTvPassThroughNameList.get(3).setBackgroundResource(R.drawable.gradient_light);
                    numOfConnectedDots--;
                    mFrame_L4.setAlpha(0f);
                    mOarL4.setAlpha(0.0f);
                    mOarL4_roll.setAlpha(0f);
                    mOarL4_pitch.setAlpha(0f);
                }
            }
            if (xsensDotR1 != null) {
                if (s.equals(xsensDotR1.getAddress())) {
                    mTvPassThroughNameList.get(4).setBackgroundResource(R.drawable.gradient_light);
                    numOfConnectedDots--;
                    mFrame_R1.setAlpha(0f);
                    mOarR1.setAlpha(0.0f);
                    mOarR1_roll.setAlpha(0f);
                    mOarR1_pitch.setAlpha(0f);

                }
            }
            if (xsensDotR2 != null) {
                if (s.equals(xsensDotR2.getAddress())) {
                    mTvPassThroughNameList.get(5).setBackgroundResource(R.drawable.gradient_light);
                    numOfConnectedDots--;
                    mFrame_R2.setAlpha(0f);
                    mOarR2.setAlpha(0.0f);
                    mOarR2_roll.setAlpha(0f);
                    mOarR2_pitch.setAlpha(0f);

                }
            }

            if (xsensDotR3 != null) {
                if (s.equals(xsensDotR3.getAddress())) {
                    mTvPassThroughNameList.get(6).setBackgroundResource(R.drawable.gradient_light);
                    numOfConnectedDots--;
                    mFrame_R3.setAlpha(0f);
                    mOarR3.setAlpha(0.0f);
                    mOarR3_roll.setAlpha(0f);
                    mOarR3_pitch.setAlpha(0f);

                }
            }
            if (xsensDotR4 != null) {
                if (s.equals(xsensDotR4.getAddress())) {
                    mTvPassThroughNameList.get(7).setBackgroundResource(R.drawable.gradient_light);
                    numOfConnectedDots--;
                    mFrame_R4.setAlpha(0f);
                    mOarR4.setAlpha(0.0f);
                    mOarR4_roll.setAlpha(0f);
                    mOarR4_pitch.setAlpha(0f);
                }
            }

            if (xsensDotBOAT != null) {
                if (s.equals(xsensDotBOAT.getAddress())) {
                    mTvPassThroughNameList.get(8).setBackgroundResource(R.drawable.gradient_light);
                    numOfConnectedDots--;

                }
            }

            // if(numOfSelectedDots != numOfConnectedDots){
            // mBtnResetHeading.setAlpha(0f);
            // mBtnActivate.setAlpha(0f);
            // }
        }
    }

    @Override
    public void onXsensDotInitDone(String s) {

        XsensDotSdk.setReconnectEnabled(false);
        suspended = false;
        mXsensDeviceListIndex++;

        synchronized (threadConnectDots) {
            threadConnectDots.notify();
        }

        if (xsensDotL1 != null) {
            if (s.equals(xsensDotL1.getAddress())) {
                mTvPassThroughNameList.get(0).setBackgroundResource(R.drawable.gradient_green);
                xsensDotL1.setMeasurementMode(payloadMode);
                xsensDotL1.setOutputRate(samplingRate);
                xsensDotL1.setFilterProfile(1);
                // numOfConnectedDots ++;
            }
        }
        if (xsensDotL2 != null) {
            if (s.equals(xsensDotL2.getAddress())) {
                mTvPassThroughNameList.get(1).setBackgroundResource(R.drawable.gradient_green);
                xsensDotL2.setMeasurementMode(payloadMode);
                xsensDotL2.setOutputRate(samplingRate);
                xsensDotL2.setFilterProfile(1);
                // numOfConnectedDots ++;

            }
        }
        if (xsensDotL3 != null) {
            if (s.equals(xsensDotL3.getAddress())) {
                mTvPassThroughNameList.get(2).setBackgroundResource(R.drawable.gradient_green);
                xsensDotL3.setMeasurementMode(payloadMode);
                xsensDotL3.setOutputRate(samplingRate);
                xsensDotL3.setFilterProfile(1);
                // numOfConnectedDots ++;

            }
        }
        if (xsensDotL4 != null) {
            if (s.equals(xsensDotL4.getAddress())) {
                mTvPassThroughNameList.get(3).setBackgroundResource(R.drawable.gradient_green);
                xsensDotL4.setMeasurementMode(payloadMode);
                xsensDotL4.setOutputRate(samplingRate);
                xsensDotL4.setFilterProfile(1);
                // numOfConnectedDots ++;

            }
        }
        if (xsensDotR1 != null) {
            if (s.equals(xsensDotR1.getAddress())) {
                mTvPassThroughNameList.get(4).setBackgroundResource(R.drawable.gradient_green);
                xsensDotR1.setMeasurementMode(payloadMode);
                xsensDotR1.setOutputRate(samplingRate);
                xsensDotR1.setFilterProfile(1);
                // numOfConnectedDots ++;

            }
        }
        if (xsensDotR2 != null) {
            if (s.equals(xsensDotR2.getAddress())) {
                mTvPassThroughNameList.get(5).setBackgroundResource(R.drawable.gradient_green);
                xsensDotR2.setMeasurementMode(payloadMode);
                xsensDotR2.setOutputRate(samplingRate);
                xsensDotR2.setFilterProfile(1);
                // numOfConnectedDots ++;

            }
        }
        if (xsensDotR3 != null) {
            if (s.equals(xsensDotR3.getAddress())) {
                mTvPassThroughNameList.get(6).setBackgroundResource(R.drawable.gradient_green);
                xsensDotR3.setMeasurementMode(payloadMode);
                xsensDotR3.setOutputRate(samplingRate);
                xsensDotR3.setFilterProfile(1);
                // numOfConnectedDots ++;

            }
        }
        if (xsensDotR4 != null) {
            if (s.equals(xsensDotR4.getAddress())) {
                mTvPassThroughNameList.get(7).setBackgroundResource(R.drawable.gradient_green);
                xsensDotR4.setMeasurementMode(payloadMode);
                xsensDotR4.setOutputRate(samplingRate);
                xsensDotR4.setFilterProfile(1);
                // numOfConnectedDots ++;

            }
        }
        if (xsensDotBOAT != null) {
            if (s.equals(xsensDotBOAT.getAddress())) {
                mTvPassThroughNameList.get(8).setBackgroundResource(R.drawable.gradient_green);
                xsensDotBOAT.setMeasurementMode(HeavyPayloadMode);
                xsensDotBOAT.setOutputRate(samplingRate);
                xsensDotBOAT.setFilterProfile(1);
                // numOfConnectedDots ++;

            }
        }

    }

    class phoneSensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                acclSensorValues = event.values;
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magSensorValues = event.values;
            }

            if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                acclLinearSensorValues = event.values;
            }

            if (mStartStatus == 1) {
                getSecondaryData();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.e("status", "AccuracyChanged");
        }
    }

    private void locationServiceInit() {

        mLocationClientGD = new AMapLocationClient(getApplicationContext());

        mLocationListenerGD = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {

                speedNewUpdate = System.currentTimeMillis();

                latitude_0_GD = aMapLocation.getLatitude();
                longitude_0_GD = aMapLocation.getLongitude();

                LatLng latLngStart = new LatLng(latitude_0_GD_last, longitude_0_GD_last);
                LatLng latLngEnd = new LatLng(latitude_0_GD, longitude_0_GD);

                latitude_0_GD_last = latitude_0_GD;
                longitude_0_GD_last = longitude_0_GD;

                if (tabMode == 1) {

                    aMap.clear();
                    // aMap.setMyLocationStyle(myLocationStyle);
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLngEnd));
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                    aMap.addMarker(new MarkerOptions().position(latLngEnd).title(username));

                }

                float distanceNow = AMapUtils.calculateLineDistance(latLngStart, latLngEnd);

                if (distanceNow > 100f) {

                    distanceNow = 0;
                }

                mDistance = mDistance + distanceNow;

                DecimalFormat FormatterDistance = new DecimalFormat("0");
                mDistanceTx = FormatterDistance.format(mDistance);
                distanceTab.setText(mDistanceTx);

                boatSpeed_0_GD = aMapLocation.getSpeed();

                if (boatSpeedCacheSize < boatSpeedCacheLength) {

                    boatSpeedCacheSamples[boatSpeedCachePointer++] = boatSpeed_0_GD;
                    boatSpeedCacheSize++;

                } else {

                    boatSpeedCachePointer = boatSpeedCachePointer % boatSpeedCacheLength;
                    boatSpeedCacheSum -= boatSpeedCacheSamples[boatSpeedCachePointer];
                    boatSpeedCacheSamples[boatSpeedCachePointer++] = boatSpeed_0_GD;
                }

                double boatSpeedAvg = doubleArrAverage(boatSpeedCacheSamples);

                DecimalFormat Formatter = new DecimalFormat("0.0");
                String boatSpeed_0_GD_Tx = Formatter.format(boatSpeedAvg);
                boatSpeedForLog = boatSpeed_0_GD_Tx;

                if (mBoatSpeedSwitchStatus == 0) {
                    UI_params_Secondary_str[1] = boatSpeed_0_GD_Tx;
                    int splitTime = (int) (500 / boatSpeedAvg);
                    int splitTimeSec = splitTime % 60;
                    splitTime = splitTime - splitTimeSec;
                    int splitTimeMin = splitTime / 60;

                    if (splitTimeSec < 10) {
                        splitTimeTx = splitTimeMin + ":0" + splitTimeSec;
                    } else {
                        splitTimeTx = splitTimeMin + ":" + splitTimeSec;
                    }

                } else {
                    if (boatSpeedAvg <= 0.77) {
                        UI_params_Secondary_str[1] = "9:59";
                    } else {

                        int splitTime = (int) (500 / boatSpeedAvg);
                        int splitTimeSec = splitTime % 60;
                        splitTime = splitTime - splitTimeSec;
                        int splitTimeMin = splitTime / 60;

                        if (splitTimeSec < 10) {
                            splitTimeTx = splitTimeMin + ":0" + splitTimeSec;
                        } else {
                            splitTimeTx = splitTimeMin + ":" + splitTimeSec;
                        }
                        UI_params_Secondary_str[1] = splitTimeTx;
                    }

                }

            }

        };

        mLocationClientGD.setLocationListener(mLocationListenerGD);
        mLocationOptionGD = new AMapLocationClientOption();

        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);
        mLocationOptionGD.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOptionGD.setInterval(1000);

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
                    Data.put("L1_overall", 0);
                    Data.put("L2_overall", 0);
                    Data.put("L3_overall", 0);
                    Data.put("L4_overall", 0);
                    Data.put("R1_overall", 0);
                    Data.put("R2_overall", 0);
                    Data.put("R3_overall", 0);
                    Data.put("R4_overall", 0);
                    Data.put("L1_fwd", 0);
                    Data.put("L2_fwd", 0);
                    Data.put("L3_fwd", 0);
                    Data.put("L4_fwd", 0);
                    Data.put("R1_fwd", 0);
                    Data.put("R2_fwd", 0);
                    Data.put("R3_fwd", 0);
                    Data.put("R4_fwd", 0);
                    Data.put("L1_bwd", 0);
                    Data.put("L2_bwd", 0);
                    Data.put("L3_bwd", 0);
                    Data.put("L4_bwd", 0);
                    Data.put("R1_bwd", 0);
                    Data.put("R2_bwd", 0);
                    Data.put("R3_bwd", 0);
                    Data.put("R4_bwd", 0);
                    Data.put("L1_duration_stroke", 0);
                    Data.put("L2_duration_stroke", 0);
                    Data.put("L3_duration_stroke", 0);
                    Data.put("L4_duration_stroke", 0);
                    Data.put("R1_duration_stroke", 0);
                    Data.put("R2_duration_stroke", 0);
                    Data.put("R3_duration_stroke", 0);
                    Data.put("R4_duration_stroke", 0);
                    Data.put("boat_pitch_L", 0);
                    Data.put("boat_pitch_R", 0);
                    Data.put("boat_yaw", 0);
                    Data.put("boat_roll", 0);
                    Data.put("HR1", 0);
                    Data.put("HR2", 0);
                    Data.put("HR3", 0);
                    Data.put("HR4", 0);
                    Data.put("HR5", 0);
                    Data.put("HR6", 0);
                    Data.put("HR7", 0);
                    Data.put("HR8", 0);
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

        updateTask = new TimerTask() {

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

                    if (connectionMode == 1 || connectionMode == 4) {

                        Data.put("FieldName", "CacheData");
                        Data.put("Type", "1");
                        Data.put("userName", username);
                        Data.put("sectionTime", sectionTimeTX);
                        Data.put("displayTime", mDisplayTimeTx);
                        Data.put("SPM", mStrokeRate.getText());
                        Data.put("boatSpeed", boatSpeedForLog);
                        Data.put("actualDistance", mDistanceTx);
                        Data.put("latitude", BigDecimal.valueOf(latitude_0_GD));
                        Data.put("longitude", BigDecimal.valueOf(longitude_0_GD));
                        Data.put("sectionType", "专业模式");
                        Data.put("playerType", "-");
                        Data.put("boatType", "-");
                        Data.put("targetDistance", String.valueOf(20000));
                        Data.put("L1_overall", mDegreeL1.getText());
                        Data.put("L2_overall", mDegreeL2.getText());
                        Data.put("L3_overall", mDegreeL3.getText());
                        Data.put("L4_overall", mDegreeL4.getText());
                        Data.put("R1_overall", mDegreeR1.getText());
                        Data.put("R2_overall", mDegreeR2.getText());
                        Data.put("R3_overall", mDegreeR3.getText());
                        Data.put("R4_overall", mDegreeR4.getText());
                        Data.put("L1_fwd", UI_params_L1[4]);
                        Data.put("L2_fwd", UI_params_L2[4]);
                        Data.put("L3_fwd", UI_params_L3[4]);
                        Data.put("L4_fwd", UI_params_L4[4]);
                        Data.put("R1_fwd", UI_params_R1[4]);
                        Data.put("R2_fwd", UI_params_R2[4]);
                        Data.put("R3_fwd", UI_params_R3[4]);
                        Data.put("R4_fwd", UI_params_R4[4]);
                        Data.put("L1_bwd", UI_params_L1[5]);
                        Data.put("L2_bwd", UI_params_L2[5]);
                        Data.put("L3_bwd", UI_params_L3[5]);
                        Data.put("L4_bwd", UI_params_L4[5]);
                        Data.put("R1_bwd", UI_params_R1[5]);
                        Data.put("R2_bwd", UI_params_R2[5]);
                        Data.put("R3_bwd", UI_params_R3[5]);
                        Data.put("R4_bwd", UI_params_R4[5]);
                        Data.put("L1_duration_stroke", UI_params_L1[19]);
                        Data.put("L2_duration_stroke", UI_params_L2[19]);
                        Data.put("L3_duration_stroke", UI_params_L3[19]);
                        Data.put("L4_duration_stroke", UI_params_L4[19]);
                        Data.put("R1_duration_stroke", UI_params_R1[19]);
                        Data.put("R2_duration_stroke", UI_params_R2[19]);
                        Data.put("R3_duration_stroke", UI_params_R3[19]);
                        Data.put("R4_duration_stroke", UI_params_R4[19]);
                        Data.put("boat_pitch_L", 0);
                        Data.put("boat_pitch_R", 0);
                        // boat_yaw, boat_roll
                        Data.put("boat_yaw", UI_params_Secondary_float[0]);
                        Data.put("boat_roll", UI_params_Secondary_float[1]);
                        Data.put("HR1", HR_params_cache[0]);
                        Data.put("HR2", HR_params_cache[1]);
                        Data.put("HR3", HR_params_cache[2]);
                        Data.put("HR4", HR_params_cache[3]);
                        Data.put("HR5", HR_params_cache[4]);
                        Data.put("HR6", HR_params_cache[5]);
                        Data.put("HR7", HR_params_cache[6]);
                        Data.put("HR8", HR_params_cache[7]);
                        Data.put("split", splitTimeTx);

                    } else if (connectionMode == 2) {

                        Data.put("FieldName", "CacheData");
                        Data.put("Type", "1");
                        Data.put("userName", username);
                        Data.put("sectionTime", sectionTimeTX);
                        Data.put("displayTime", mDisplayTimeTx);
                        Data.put("SPM", mStrokeRate.getText());
                        Data.put("boatSpeed", boatSpeedForLog);
                        Data.put("actualDistance", mDistanceTx);
                        Data.put("latitude", BigDecimal.valueOf(latitude_0_GD));
                        Data.put("longitude", BigDecimal.valueOf(longitude_0_GD));
                        Data.put("sectionType", "专业模式");
                        Data.put("playerType", "-");
                        Data.put("boatType", "-");
                        Data.put("targetDistance", String.valueOf(20000));
                        Data.put("L1_overall", mDegreeL1.getText());
                        Data.put("L2_overall", mDegreeL2.getText());
                        Data.put("L3_overall", mDegreeL3.getText());
                        Data.put("L4_overall", mDegreeL4.getText());
                        Data.put("R1_overall", mDegreeR1.getText());
                        Data.put("R2_overall", mDegreeR2.getText());
                        Data.put("R3_overall", mDegreeR3.getText());
                        Data.put("R4_overall", mDegreeR4.getText());
                        Data.put("L1_fwd", UI_params_L1[4]);
                        Data.put("L2_fwd", UI_params_L2[4]);
                        Data.put("L3_fwd", UI_params_L3[4]);
                        Data.put("L4_fwd", UI_params_L4[4]);
                        Data.put("R1_fwd", UI_params_R1[4]);
                        Data.put("R2_fwd", UI_params_R2[4]);
                        Data.put("R3_fwd", UI_params_R3[4]);
                        Data.put("R4_fwd", UI_params_R4[4]);
                        Data.put("L1_bwd", UI_params_L1[5]);
                        Data.put("L2_bwd", UI_params_L2[5]);
                        Data.put("L3_bwd", UI_params_L3[5]);
                        Data.put("L4_bwd", UI_params_L4[5]);
                        Data.put("R1_bwd", UI_params_R1[5]);
                        Data.put("R2_bwd", UI_params_R2[5]);
                        Data.put("R3_bwd", UI_params_R3[5]);
                        Data.put("R4_bwd", UI_params_R4[5]);
                        Data.put("L1_duration_stroke", UI_params_L1[19]);
                        Data.put("L2_duration_stroke", UI_params_L2[19]);
                        Data.put("L3_duration_stroke", UI_params_L3[19]);
                        Data.put("L4_duration_stroke", UI_params_L4[19]);
                        Data.put("R1_duration_stroke", UI_params_R1[19]);
                        Data.put("R2_duration_stroke", UI_params_R2[19]);
                        Data.put("R3_duration_stroke", UI_params_R3[19]);
                        Data.put("R4_duration_stroke", UI_params_R4[19]);
                        Data.put("boat_pitch_L", 0);
                        Data.put("boat_pitch_R", 0);
                        // boat_yaw, boat_roll
                        Data.put("boat_yaw", UI_params_Secondary_float[0]);
                        Data.put("boat_roll", UI_params_Secondary_float[1]);
                        Data.put("HR1", HR_params_cache[0]);
                        Data.put("HR2", HR_params_cache[1]);
                        Data.put("HR3", HR_params_cache[2]);
                        Data.put("HR4", HR_params_cache[3]);
                        Data.put("HR5", HR_params_cache[4]);
                        Data.put("HR6", HR_params_cache[5]);
                        Data.put("HR7", HR_params_cache[6]);
                        Data.put("HR8", HR_params_cache[7]);
                        Data.put("split", splitTimeTx);

                    } else if (connectionMode == 3) {
                        Data.put("FieldName", "CacheData");
                        Data.put("Type", "1");
                        Data.put("userName", username);
                        Data.put("sectionTime", sectionTimeTX);
                        Data.put("displayTime", mDisplayTimeTx);
                        Data.put("SPM", mStrokeRate.getText());
                        Data.put("boatSpeed", boatSpeedForLog);
                        Data.put("actualDistance", mDistanceTx);
                        Data.put("latitude", BigDecimal.valueOf(latitude_0_GD));
                        Data.put("longitude", BigDecimal.valueOf(longitude_0_GD));
                        Data.put("sectionType", "专业模式");
                        Data.put("playerType", "-");
                        Data.put("boatType", "-");
                        Data.put("targetDistance", String.valueOf(20000));
                        Data.put("HR1", HR_params_cache[0]);
                        Data.put("HR2", HR_params_cache[1]);
                        Data.put("HR3", HR_params_cache[2]);
                        Data.put("HR4", HR_params_cache[3]);
                        Data.put("HR5", HR_params_cache[4]);
                        Data.put("HR6", HR_params_cache[5]);
                        Data.put("HR7", HR_params_cache[6]);
                        Data.put("HR8", HR_params_cache[7]);

                    }

                    String paramsJson = JSON.toJSONString(Data);
                    // System.out.println("senddata:" + paramsJson);
                    // System.out.println("ad:" + mDistanceTx);

                    int lengthLocal = paramsJson.getBytes().length;
                    httpURLConnectionUpdate.setRequestProperty("Content-Length", String.valueOf(lengthLocal));

                    try {
                        OutputStream outputStream = httpURLConnectionUpdate.getOutputStream();
                        outputStream.write(paramsJson.getBytes());

                        InputStream inputStream = httpURLConnectionUpdate.getInputStream();
                        final Map<String, Object> inputStreamMap = JSON.parseObject(inputStream, Map.class);

                    } catch (Exception e) {

                    }

                    int responseCode = httpURLConnectionUpdate.getResponseCode();
                    // System.out.println("response code HTTP: " + responseCode);
                    // System.out.println("response code API:" + inputStreamMap.get("code"));
                    // System.out.println("response success:" + inputStreamMap.get("success"));
                    // System.out.println("response data:" + inputStreamMap.get("data"));

                } catch (Exception e) {

                    // e.printStackTrace();
                }

            }
        };

        updateTimer = new Timer();
        updateTimer.schedule(updateTask, 3000, 2000);

    }

    private double doubleArrAverage(double[] arr) {
        double sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum / arr.length;
    }

    private void loggerStart() throws IOException {

        loggerPathList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        double logBegTime = System.currentTimeMillis();
        String refTime_0 = simpleDateFormat.format(logBegTime);
        String refTime = refTime_0.replaceAll(":", "_");
        packetCounterPhoneCache = 0;
        packetCounterSetCache = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        String fileLocPhone = fileLoc + refTime + "_PH.csv";
        String placeHolderID = "00000000-0000-0000-0000-000000000000";
        loggerPhone_0 = new FileWriter(fileLocPhone);
        loggerPhone = new CSVWriter(loggerPhone_0,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.RFC4180_LINE_END);
        loggerPhone.writeNext(new String[] { "PacketCounter", "StrokeRate", "BoatSpeed", "BoatYaw", "BoatRoll",
                "HR1", "HR2", "HR3", "HR4", "HR5", "HR6", "HR7", "HR8", "Distance",
                "CorrectionLeft2nd", "CorrectionRight2nd",
                "InstantBoatSpeed", "InstantBoatAcceleration", String.valueOf(samplingRate),
                athleteIDList.get(0), athleteIDList.get(1), athleteIDList.get(2), athleteIDList.get(3),
                athleteIDList.get(4), athleteIDList.get(5), athleteIDList.get(6), athleteIDList.get(7),
                boatType });

        loggerPathList.add(fileLocPhone);

        for (int i = 0; i < 9; i++) {

            if (mPassThroughList.get(i) != null) {
                // numOfSelectedDots ++;
                switch (i) {
                    case 0:
                        // Context context, int type, int mode, String filename, String tagName, String
                        // firmwareVersion, boolean isSynced, int outputRate, String filterProfileName,
                        // String appVersion
                        String fileLocL1 = fileLoc + refTime + "_L1.csv";
                        loggerL1 = new XsensDotLogger(Dashboard.this, 1, payloadMode, fileLocL1, "data", "2.2.1", false,
                                samplingRate, "dynamic", "1.0", (long) logBegTime);

                        // loggerL1_sub_FileWriter = new FileWriter(fileLocL1);
                        // loggerL1_sub_CsvWriter = new CSVWriter(loggerL1_sub_FileWriter,
                        // CSVWriter.DEFAULT_SEPARATOR,
                        // CSVWriter.NO_QUOTE_CHARACTER,
                        // CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        // CSVWriter.RFC4180_LINE_END);
                        // loggerL1_sub_CsvWriter.writeNext(new String[] {"DeviceTag:",
                        // xsensDotL1.getTag()});
                        // loggerL1_sub_CsvWriter.writeNext(new String[] {"FirmwareVersion:",
                        // xsensDotL1.getFirmwareVersion()});
                        // loggerL1_sub_CsvWriter.writeNext(new String[] {"AppVersion:", "1"});
                        // loggerL1_sub_CsvWriter.writeNext(new String[] {"SyncStatus:", "Un-synced"});
                        // loggerL1_sub_CsvWriter.writeNext(new String[] {"OutputRate:",
                        // String.valueOf(xsensDotL1.getCurrentOutputRate())});
                        // loggerL1_sub_CsvWriter.writeNext(new String[] {"FilterProfile:", "dynamic"});
                        // loggerL1_sub_CsvWriter.writeNext(new String[] {"Measurement Mode:",
                        // String.valueOf(xsensDotL1.getMeasurementMode())});
                        // loggerL1_sub_CsvWriter.writeNext(new String[] {"StarTime:",
                        // String.valueOf(System.currentTimeMillis())});
                        // loggerL1_sub_CsvWriter.writeNext(new String[] {"POS:" + "L1"});
                        // loggerL1_sub_CsvWriter.writeNext(new String[] {"PlaceHolder"});
                        // loggerL1_sub_CsvWriter.writeNext(new String[] {"PacketCounter",
                        // "SampleTimeFine", "Euler_X","Euler_Y", "Euler_Z"});
                        //
                        loggerPathList.add(fileLocL1);
                        break;

                    case 1:
                        String fileLocL2 = fileLoc + refTime + "_L2.csv";
                        loggerL2 = new XsensDotLogger(Dashboard.this, 1, payloadMode, fileLocL2, "data", "2.2.1", false,
                                samplingRate, "dynamic", "1.0", (long) logBegTime);

                        // loggerL2_sub_FileWriter = new FileWriter(fileLocL2);
                        // loggerL2_sub_CsvWriter = new CSVWriter(loggerL2_sub_FileWriter,
                        // CSVWriter.DEFAULT_SEPARATOR,
                        // CSVWriter.NO_QUOTE_CHARACTER,
                        // CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        // CSVWriter.RFC4180_LINE_END);
                        // loggerL2_sub_CsvWriter.writeNext(new String[] {"DeviceTag:",
                        // xsensDotL2.getTag()});
                        // loggerL2_sub_CsvWriter.writeNext(new String[] {"FirmwareVersion:",
                        // xsensDotL2.getFirmwareVersion()});
                        // loggerL2_sub_CsvWriter.writeNext(new String[] {"AppVersion:", "1"});
                        // loggerL2_sub_CsvWriter.writeNext(new String[] {"SyncStatus:", "Un-synced"});
                        // loggerL2_sub_CsvWriter.writeNext(new String[] {"OutputRate:",
                        // String.valueOf(xsensDotL2.getCurrentOutputRate())});
                        // loggerL2_sub_CsvWriter.writeNext(new String[] {"FilterProfile:", "dynamic"});
                        // loggerL2_sub_CsvWriter.writeNext(new String[] {"Measurement Mode:",
                        // String.valueOf(xsensDotL2.getMeasurementMode())});
                        // loggerL2_sub_CsvWriter.writeNext(new String[] {"StarTime:",
                        // String.valueOf(System.currentTimeMillis())});
                        // loggerL2_sub_CsvWriter.writeNext(new String[] {"POS:" + "L2"});
                        // loggerL2_sub_CsvWriter.writeNext(new String[] {"PlaceHolder"});
                        // loggerL2_sub_CsvWriter.writeNext(new String[] {"PacketCounter",
                        // "SampleTimeFine", "Euler_X","Euler_Y", "Euler_Z"});

                        loggerPathList.add(fileLocL2);
                        break;

                    case 2:
                        String fileLocL3 = fileLoc + refTime + "_L3.csv";
                        loggerL3 = new XsensDotLogger(Dashboard.this, 1, payloadMode, fileLocL3, "data", "2.2.1", false,
                                samplingRate, "dynamic", "1.0", (long) logBegTime);
                        // loggerL3_sub_FileWriter = new FileWriter(fileLocL3);
                        // loggerL3_sub_CsvWriter = new CSVWriter(loggerL3_sub_FileWriter,
                        // CSVWriter.DEFAULT_SEPARATOR,
                        // CSVWriter.NO_QUOTE_CHARACTER,
                        // CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        // CSVWriter.RFC4180_LINE_END);
                        // loggerL3_sub_CsvWriter.writeNext(new String[] {"DeviceTag:",
                        // xsensDotL3.getTag()});
                        // loggerL3_sub_CsvWriter.writeNext(new String[] {"FirmwareVersion:",
                        // xsensDotL3.getFirmwareVersion()});
                        // loggerL3_sub_CsvWriter.writeNext(new String[] {"AppVersion:", "1"});
                        // loggerL3_sub_CsvWriter.writeNext(new String[] {"SyncStatus:", "Un-synced"});
                        // loggerL3_sub_CsvWriter.writeNext(new String[] {"OutputRate:",
                        // String.valueOf(xsensDotL3.getCurrentOutputRate())});
                        // loggerL3_sub_CsvWriter.writeNext(new String[] {"FilterProfile:", "dynamic"});
                        // loggerL3_sub_CsvWriter.writeNext(new String[] {"Measurement Mode:",
                        // String.valueOf(xsensDotL3.getMeasurementMode())});
                        // loggerL3_sub_CsvWriter.writeNext(new String[] {"StarTime:",
                        // String.valueOf(System.currentTimeMillis())});
                        // loggerL3_sub_CsvWriter.writeNext(new String[] {"POS:" + "L3"});
                        // loggerL3_sub_CsvWriter.writeNext(new String[] {"PlaceHolder"});
                        // loggerL3_sub_CsvWriter.writeNext(new String[] {"PacketCounter",
                        // "SampleTimeFine", "Euler_X","Euler_Y", "Euler_Z"});

                        loggerPathList.add(fileLocL3);
                        break;

                    case 3:
                        String fileLocL4 = fileLoc + refTime + "_L4.csv";
                        loggerL4 = new XsensDotLogger(Dashboard.this, 1, payloadMode, fileLocL4, "data", "2.2.1", false,
                                samplingRate, "dynamic", "1.0", (long) logBegTime);
                        // loggerL4_sub_FileWriter = new FileWriter(fileLocL4);
                        // loggerL4_sub_CsvWriter = new CSVWriter(loggerL4_sub_FileWriter,
                        // CSVWriter.DEFAULT_SEPARATOR,
                        // CSVWriter.NO_QUOTE_CHARACTER,
                        // CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        // CSVWriter.RFC4180_LINE_END);
                        // loggerL4_sub_CsvWriter.writeNext(new String[] {"DeviceTag:",
                        // xsensDotL4.getTag()});
                        // loggerL4_sub_CsvWriter.writeNext(new String[] {"FirmwareVersion:",
                        // xsensDotL4.getFirmwareVersion()});
                        // loggerL4_sub_CsvWriter.writeNext(new String[] {"AppVersion:", "1"});
                        // loggerL4_sub_CsvWriter.writeNext(new String[] {"SyncStatus:", "Un-synced"});
                        // loggerL4_sub_CsvWriter.writeNext(new String[] {"OutputRate:",
                        // String.valueOf(xsensDotL4.getCurrentOutputRate())});
                        // loggerL4_sub_CsvWriter.writeNext(new String[] {"FilterProfile:", "dynamic"});
                        // loggerL4_sub_CsvWriter.writeNext(new String[] {"Measurement Mode:",
                        // String.valueOf(xsensDotL4.getMeasurementMode())});
                        // loggerL4_sub_CsvWriter.writeNext(new String[] {"StarTime:",
                        // String.valueOf(System.currentTimeMillis())});
                        // loggerL4_sub_CsvWriter.writeNext(new String[] {"POS:" + "L4"});
                        // loggerL4_sub_CsvWriter.writeNext(new String[] {"PlaceHolder"});
                        // loggerL4_sub_CsvWriter.writeNext(new String[] {"PacketCounter",
                        // "SampleTimeFine", "Euler_X","Euler_Y", "Euler_Z"});

                        loggerPathList.add(fileLocL4);
                        break;

                    case 4:
                        String fileLocR1 = fileLoc + refTime + "_R1.csv";
                        loggerR1 = new XsensDotLogger(Dashboard.this, 1, payloadMode, fileLocR1, "data", "2.2.1", false,
                                samplingRate, "dynamic", "1.0", (long) logBegTime);
                        // loggerR1_sub_FileWriter = new FileWriter(fileLocR1);
                        // loggerR1_sub_CsvWriter = new CSVWriter(loggerR1_sub_FileWriter,
                        // CSVWriter.DEFAULT_SEPARATOR,
                        // CSVWriter.NO_QUOTE_CHARACTER,
                        // CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        // CSVWriter.RFC4180_LINE_END);
                        // loggerR1_sub_CsvWriter.writeNext(new String[] {"DeviceTag:",
                        // xsensDotR1.getTag()});
                        // loggerR1_sub_CsvWriter.writeNext(new String[] {"FirmwareVersion:",
                        // xsensDotR1.getFirmwareVersion()});
                        // loggerR1_sub_CsvWriter.writeNext(new String[] {"AppVersion:", "1"});
                        // loggerR1_sub_CsvWriter.writeNext(new String[] {"SyncStatus:", "Un-synced"});
                        // loggerR1_sub_CsvWriter.writeNext(new String[] {"OutputRate:",
                        // String.valueOf(xsensDotR1.getCurrentOutputRate())});
                        // loggerR1_sub_CsvWriter.writeNext(new String[] {"FilterProfile:", "dynamic"});
                        // loggerR1_sub_CsvWriter.writeNext(new String[] {"Measurement Mode:",
                        // String.valueOf(xsensDotR1.getMeasurementMode())});
                        // loggerR1_sub_CsvWriter.writeNext(new String[] {"StarTime:",
                        // String.valueOf(System.currentTimeMillis())});
                        // loggerR1_sub_CsvWriter.writeNext(new String[] {"POS:" + "R1"});
                        // loggerR1_sub_CsvWriter.writeNext(new String[] {"PlaceHolder"});
                        // loggerR1_sub_CsvWriter.writeNext(new String[] {"PacketCounter",
                        // "SampleTimeFine", "Euler_X","Euler_Y", "Euler_Z"});
                        //
                        loggerPathList.add(fileLocR1);
                        break;

                    case 5:
                        String fileLocR2 = fileLoc + refTime + "_R2.csv";
                        loggerR2 = new XsensDotLogger(Dashboard.this, 1, payloadMode, fileLocR2, "data", "2.2.1", false,
                                samplingRate, "dynamic", "1.0", (long) logBegTime);
                        // loggerR2_sub_FileWriter = new FileWriter(fileLocR2);
                        // loggerR2_sub_CsvWriter = new CSVWriter(loggerR2_sub_FileWriter,
                        // CSVWriter.DEFAULT_SEPARATOR,
                        // CSVWriter.NO_QUOTE_CHARACTER,
                        // CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        // CSVWriter.RFC4180_LINE_END);
                        // loggerR2_sub_CsvWriter.writeNext(new String[] {"DeviceTag:",
                        // xsensDotR2.getTag()});
                        // loggerR2_sub_CsvWriter.writeNext(new String[] {"FirmwareVersion:",
                        // xsensDotR2.getFirmwareVersion()});
                        // loggerR2_sub_CsvWriter.writeNext(new String[] {"AppVersion:", "1"});
                        // loggerR2_sub_CsvWriter.writeNext(new String[] {"SyncStatus:", "Un-synced"});
                        // loggerR2_sub_CsvWriter.writeNext(new String[] {"OutputRate:",
                        // String.valueOf(xsensDotR2.getCurrentOutputRate())});
                        // loggerR2_sub_CsvWriter.writeNext(new String[] {"FilterProfile:", "dynamic"});
                        // loggerR2_sub_CsvWriter.writeNext(new String[] {"Measurement Mode:",
                        // String.valueOf(xsensDotR2.getMeasurementMode())});
                        // loggerR2_sub_CsvWriter.writeNext(new String[] {"StarTime:",
                        // String.valueOf(System.currentTimeMillis())});
                        // loggerR2_sub_CsvWriter.writeNext(new String[] {"POS:" + "R2"});
                        // loggerR2_sub_CsvWriter.writeNext(new String[] {"PlaceHolder"});
                        // loggerR2_sub_CsvWriter.writeNext(new String[] {"PacketCounter",
                        // "SampleTimeFine", "Euler_X","Euler_Y", "Euler_Z"});

                        loggerPathList.add(fileLocR2);
                        break;

                    case 6:
                        String fileLocR3 = fileLoc + refTime + "_R3.csv";
                        loggerR3 = new XsensDotLogger(Dashboard.this, 1, payloadMode, fileLocR3, "data", "2.2.1", false,
                                samplingRate, "dynamic", "1.0", (long) logBegTime);
                        // loggerR3_sub_FileWriter = new FileWriter(fileLocR3);
                        // loggerR3_sub_CsvWriter = new CSVWriter(loggerR3_sub_FileWriter,
                        // CSVWriter.DEFAULT_SEPARATOR,
                        // CSVWriter.NO_QUOTE_CHARACTER,
                        // CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        // CSVWriter.RFC4180_LINE_END);
                        // loggerR3_sub_CsvWriter.writeNext(new String[] {"DeviceTag:",
                        // xsensDotR3.getTag()});
                        // loggerR3_sub_CsvWriter.writeNext(new String[] {"FirmwareVersion:",
                        // xsensDotR3.getFirmwareVersion()});
                        // loggerR3_sub_CsvWriter.writeNext(new String[] {"AppVersion:", "1"});
                        // loggerR3_sub_CsvWriter.writeNext(new String[] {"SyncStatus:", "Un-synced"});
                        // loggerR3_sub_CsvWriter.writeNext(new String[] {"OutputRate:",
                        // String.valueOf(xsensDotR3.getCurrentOutputRate())});
                        // loggerR3_sub_CsvWriter.writeNext(new String[] {"FilterProfile:", "dynamic"});
                        // loggerR3_sub_CsvWriter.writeNext(new String[] {"Measurement Mode:",
                        // String.valueOf(xsensDotR3.getMeasurementMode())});
                        // loggerR3_sub_CsvWriter.writeNext(new String[] {"StarTime:",
                        // String.valueOf(System.currentTimeMillis())});
                        // loggerR3_sub_CsvWriter.writeNext(new String[] {"POS:" + "R3"});
                        // loggerR3_sub_CsvWriter.writeNext(new String[] {"PlaceHolder"});
                        // loggerR3_sub_CsvWriter.writeNext(new String[] {"PacketCounter",
                        // "SampleTimeFine", "Euler_X","Euler_Y", "Euler_Z"});

                        loggerPathList.add(fileLocR3);
                        break;

                    case 7:
                        String fileLocR4 = fileLoc + refTime + "_R4.csv";
                        loggerR4 = new XsensDotLogger(Dashboard.this, 1, payloadMode, fileLocR4, "data", "2.2.1", false,
                                samplingRate, "dynamic", "1.0", (long) logBegTime);
                        //
                        // loggerR4_sub_FileWriter = new FileWriter(fileLocR4);
                        // loggerR4_sub_CsvWriter = new CSVWriter(loggerR4_sub_FileWriter,
                        // CSVWriter.DEFAULT_SEPARATOR,
                        // CSVWriter.NO_QUOTE_CHARACTER,
                        // CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        // CSVWriter.RFC4180_LINE_END);
                        // loggerR4_sub_CsvWriter.writeNext(new String[] {"DeviceTag:",
                        // xsensDotR4.getTag()});
                        // loggerR4_sub_CsvWriter.writeNext(new String[] {"FirmwareVersion:",
                        // xsensDotR4.getFirmwareVersion()});
                        // loggerR4_sub_CsvWriter.writeNext(new String[] {"AppVersion:", "1"});
                        // loggerR4_sub_CsvWriter.writeNext(new String[] {"SyncStatus:", "Un-synced"});
                        // loggerR4_sub_CsvWriter.writeNext(new String[] {"OutputRate:",
                        // String.valueOf(xsensDotR4.getCurrentOutputRate())});
                        // loggerR4_sub_CsvWriter.writeNext(new String[] {"FilterProfile:", "dynamic"});
                        // loggerR4_sub_CsvWriter.writeNext(new String[] {"Measurement Mode:",
                        // String.valueOf(xsensDotR4.getMeasurementMode())});
                        // loggerR4_sub_CsvWriter.writeNext(new String[] {"StarTime:",
                        // String.valueOf(System.currentTimeMillis())});
                        // loggerR4_sub_CsvWriter.writeNext(new String[] {"POS:" + "R4"});
                        // loggerR4_sub_CsvWriter.writeNext(new String[] {"PlaceHolder"});
                        // loggerR4_sub_CsvWriter.writeNext(new String[] {"PacketCounter",
                        // "SampleTimeFine", "Euler_X","Euler_Y", "Euler_Z"});

                        loggerPathList.add(fileLocR4);
                        break;

                    case 8:
                        String fileLocBoat = fileLoc + refTime + "_BO.csv";
                        loggerBoat = new XsensDotLogger(Dashboard.this, 1, payloadMode, fileLocBoat, "data", "2.2.1",
                                false, samplingRate, "dynamic", "1.0", (long) logBegTime);

                        // loggerBoat_sub_FileWriter = new FileWriter(fileLocBoat);
                        // loggerBoat_sub_CsvWriter = new CSVWriter(loggerBoat_sub_FileWriter,
                        // CSVWriter.DEFAULT_SEPARATOR,
                        // CSVWriter.NO_QUOTE_CHARACTER,
                        // CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                        // CSVWriter.RFC4180_LINE_END);
                        // loggerBoat_sub_CsvWriter.writeNext(new String[] {"DeviceTag:",
                        // xsensDotBOAT.getTag()});
                        // loggerBoat_sub_CsvWriter.writeNext(new String[] {"FirmwareVersion:",
                        // xsensDotBOAT.getFirmwareVersion()});
                        // loggerBoat_sub_CsvWriter.writeNext(new String[] {"AppVersion:", "1"});
                        // loggerBoat_sub_CsvWriter.writeNext(new String[] {"SyncStatus:",
                        // "Un-synced"});
                        // loggerBoat_sub_CsvWriter.writeNext(new String[] {"OutputRate:",
                        // String.valueOf(xsensDotBOAT.getCurrentOutputRate())});
                        // loggerBoat_sub_CsvWriter.writeNext(new String[] {"FilterProfile:",
                        // "dynamic"});
                        // loggerBoat_sub_CsvWriter.writeNext(new String[] {"Measurement Mode:",
                        // String.valueOf(xsensDotBOAT.getMeasurementMode())});
                        // loggerBoat_sub_CsvWriter.writeNext(new String[] {"StarTime:",
                        // String.valueOf(System.currentTimeMillis())});
                        // loggerBoat_sub_CsvWriter.writeNext(new String[] {"POS:" + "BOAT"});
                        // loggerBoat_sub_CsvWriter.writeNext(new String[] {"PlaceHolder"});
                        // loggerBoat_sub_CsvWriter.writeNext(new String[] {"PacketCounter",
                        // "SampleTimeFine", "Euler_X","Euler_Y", "Euler_Z"});
                        //
                        loggerPathList.add(fileLocBoat);
                        break;

                }

            }

        }

        loggerList.add(loggerL1);
        loggerList.add(loggerL2);
        loggerList.add(loggerL3);
        loggerList.add(loggerL4);
        loggerList.add(loggerR1);
        loggerList.add(loggerR2);
        loggerList.add(loggerR3);
        loggerList.add(loggerR4);
        loggerList.add(loggerBoat);

        // loggerList_sub.add(loggerL1_sub_CsvWriter);
        // loggerList_sub.add(loggerL2_sub_CsvWriter);
        // loggerList_sub.add(loggerL3_sub_CsvWriter);
        // loggerList_sub.add(loggerL4_sub_CsvWriter);
        // loggerList_sub.add(loggerR1_sub_CsvWriter);
        // loggerList_sub.add(loggerR2_sub_CsvWriter);
        // loggerList_sub.add(loggerR3_sub_CsvWriter);
        // loggerList_sub.add(loggerR4_sub_CsvWriter);
        // loggerList_sub.add(loggerBoat_sub_CsvWriter);

    }

    private void loggerUpdate() {

        for (int i = 0; i <= 8; i++) {

            if (mStartStatus == 1 && loggerList_sub.get(i) != null) {
                // if (packetCounterSet[i] > packetCounterSetCache[i] && mStartStatus == 1 &&
                // loggerList_sub.get(i) != null) {

                try {
                    loggerList_sub.get(i).writeNext(new String[] {
                            String.valueOf(packetCounterSet[i]), String.valueOf(System.currentTimeMillis()),
                            String.valueOf(UI_params_set.get(i)[20]), String.valueOf(UI_params_set.get(i)[21]),
                            String.valueOf(UI_params_set.get(i)[22])
                    });
                } catch (Exception e) {

                    loggerList_sub.get(i).writeNext(new String[] {
                            String.valueOf(packetCounterSet[i]), String.valueOf(0),
                            String.valueOf(0), String.valueOf(0), String.valueOf(0)
                    });

                }
            }
            packetCounterSetCache[i] = packetCounterSet[i];
        }

        packetCounterPhone++;

        if (mStartStatus == 1 && loggerPhone != null) {
            // if(packetCounterPhone > packetCounterPhoneCache && mStartStatus ==1 &&
            // loggerPhone!=null) {

            try {
                loggerPhone.writeNext(new String[] {
                        String.valueOf(packetCounter),
                        (String) mStrokeRate.getText(), (String) boatSpeedForLog,
                        String.valueOf(mBoatYaw.getRotation()), String.valueOf(mBoatRoll.getRotation()),
                        String.valueOf(HR_params_cache[0]), String.valueOf(HR_params_cache[1]),
                        String.valueOf(HR_params_cache[2]), String.valueOf(HR_params_cache[3]),
                        String.valueOf(HR_params_cache[4]), String.valueOf(HR_params_cache[5]),
                        String.valueOf(HR_params_cache[6]), String.valueOf(HR_params_cache[7]),
                        (String) distanceTab.getText(), String.valueOf(correctionLeftSecondary),
                        String.valueOf(correctionRightSecondary), String.valueOf(0),
                        String.valueOf(mBoatAccelerationForLog),
                });

            } catch (Exception e) {
                loggerPhone.writeNext(new String[] {
                        String.valueOf(packetCounter),
                        String.valueOf(0), String.valueOf(0), String.valueOf(0), String.valueOf(0), String.valueOf(0),
                        String.valueOf(0),
                        String.valueOf(0), String.valueOf(0), String.valueOf(0), String.valueOf(0), String.valueOf(0),
                        String.valueOf(0),
                        String.valueOf(0), String.valueOf(0), String.valueOf(0), String.valueOf(0), String.valueOf(0),
                });
            }

            packetCounterPhoneCache = packetCounterPhone;
        }

    }

    private void loggerStop() {

        try {
            loggerPhone.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(Dashboard.this, "Close Writer Failed", Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i < 9; i++) {

            if (loggerList.get(i) != null) {
                loggerList.get(i).stop();
            }
        }

        loggerList = new ArrayList<>();

        // for(int i = 0; i< 9; i++){
        //
        // if (loggerList_sub.get(i) != null) {
        // try {
        // loggerList_sub.get(i).close();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // }
        // }

        // loggerList_sub = new ArrayList<>();

        // if (loggerL1 != null){
        // loggerL1.stop();
        // }
        // if (loggerL2 != null){
        // loggerL2.stop();
        // }
        // if (loggerL3 != null){
        // loggerL3.stop();
        // }
        // if (loggerL4 != null){
        // loggerL4.stop();
        // }
        // if (loggerR1 != null){
        // loggerR1.stop();
        // }
        // if (loggerR2 != null){
        // loggerR2.stop();
        // }
        // if (loggerR3 != null){
        // loggerR3.stop();
        // }
        // if (loggerR4 != null){
        // loggerR4.stop();
        // }
        // if (loggerBoat != null){
        // loggerBoat.stop();
        // }

    }

    private void watchman(int code) {

        if (mXsensDeviceList.size() != 0) {

            watchmanTask = new TimerTask() {
                @Override
                public void run() {

                    for (int i = 0; i < 9; i++) {

                        if (mXsensDeviceList.get(i) != null) {

                            double updateGap = System.currentTimeMillis() - dotsLastUpdateTime[i];
                            if (updateGap > 5000 && dynamicReconnect == 0 && mStartStatus == 1) {
                                mXsensDeviceList.get(i).disconnect();
                                // capture dead object
                                dynamicReconnect = 1;
                                currentReconnectPos = i;
                                System.out.println(i + "---current Lag:" + updateGap);

                            } else {
                                System.out.println(i + "++++is fine:" + updateGap);
                            }

                            if (dynamicReconnect == 1 &&
                                    mXsensDeviceList.get(currentReconnectPos)
                                            .getConnectionState() == XsensDotDevice.CONN_STATE_DISCONNECTED
                                    &&
                                    reconnectingInProcess != 1) {
                                mXsensDeviceList.get(i).connect();
                                reconnectingInProcess = 1;
                                System.out.println("Reconnecting");

                            }

                            if (currentReconnectPos != -1) {
                                if (mXsensDeviceList.get(currentReconnectPos).isInitDone()) {
                                    packetCounterSetCache[currentReconnectPos] = 0;
                                    dotsLastUpdateTime[currentReconnectPos] = System.currentTimeMillis();
                                    mXsensDeviceList.get(currentReconnectPos).startMeasuring();
                                    reconnectingInProcess = 0;
                                    currentReconnectPos = -1;
                                    dynamicReconnect = 0;

                                    System.out.println("Measuring");

                                }
                            }
                            // System.out.println("Connection Status of " + i + " :" +
                            // mXsensDeviceList.get(i).getConnectionState());;
                            // System.out.println("Measurement Status of " + i + " :" +
                            // mXsensDeviceList.get(i).getMeasurementState());;

                        }
                    }

                }
            };

            if (code == 1) {

                double startTime = System.currentTimeMillis();
                dotsLastUpdateTime = new double[] { startTime, startTime, startTime, startTime, startTime, startTime,
                        startTime, startTime, startTime };
                watchmanTimer = new Timer();
                watchmanTimer.schedule(watchmanTask, 1000, 3000);

            } else if (watchmanTimer != null && watchmanTask != null) {

                watchmanTimer.cancel();
                watchmanTask.cancel();
            }

        }

    }

    private void deleteLogs(ArrayList<String> filePathList) {

        for (int i = 0; i < filePathList.size(); i++) {
            File file = new File(filePathList.get(i));
            if (file.isFile() && file.exists()) {
                file.delete();
            }
        }
    }

    private void fileSaveNotification() {

        String notice;
        String confirm;
        String title;
        String deny;

        if (Objects.equals(Lang, "eng")) {

            notice = "Do You Want to Save Data for Section";
            confirm = "Yes";
            deny = "No";
            title = "Notification";

        } else {
            notice = "是否保存此次划行的数据？";
            confirm = "保存";
            deny = "不保存";
            title = "系统提示";
        }

        new AlertDialog.Builder(Dashboard.this).setTitle(title)
                .setMessage(notice)
                .setPositiveButton(confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 保存并上传数据
                        uploadCSVFiles(loggerPathList);
                    }
                }).setNegativeButton(deny, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteLogs(loggerPathList);
                    }

                }).show();
    }

    /**
     * 上传CSV文件到服务器
     * 
     * @param filePaths 要上传的文件路径列表
     */
    private void uploadCSVFiles(final ArrayList<String> filePaths) {
        // 创建OkHttpClient实例
        OkHttpClient okHttpClient = new OkHttpClient();

        // 遍历所有文件路径
        for (final String filePath : filePaths) {
            // 获取文件名
            File file = new File(filePath);
            final String filename = file.getName();

            // 检查文件是否存在
            if (!file.exists()) {
                Log.e("uploadCSV", "File not found: " + filePath);
                continue;
            }

            // 创建文件请求体
            RequestBody fileBody = RequestBody.create(
                    MediaType.parse("application/octet-stream"),
                    file);

            // 创建表单请求体
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addPart(Headers.of(
                            "Content-Disposition",
                            "form-data; name=\"originalData\"; filename=\"" + filename + "\""), fileBody)
                    .build();

            // 创建请求
            Request request = new Request.Builder()
                    .url(uploadPATH)
                    .post(requestBody)
                    .build();

            // 发送请求
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("uploadCSV", "Failed to upload file: " + filename + ", error: " + e.getMessage());

                    // 在UI线程显示提示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Objects.equals(Lang, "eng")) {
                                Toast.makeText(Dashboard.this, "Failed to upload " + filename, Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(Dashboard.this, "上传" + filename + "失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.i("uploadCSV", "Successfully uploaded file: " + filename + ", response: " + responseBody);

                        // 在UI线程显示提示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Objects.equals(Lang, "eng")) {
                                    Toast.makeText(Dashboard.this, "Successfully uploaded " + filename,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Dashboard.this, "上传" + filename + "成功", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Log.e("uploadCSV",
                                "Failed to upload file: " + filename + ", response code: " + response.code());

                        // 在UI线程显示提示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (Objects.equals(Lang, "eng")) {
                                    Toast.makeText(Dashboard.this, "Failed to upload " + filename, Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Toast.makeText(Dashboard.this, "上传" + filename + "失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            String notice;
            String confirm;
            String title;
            String deny;

            if (Objects.equals(Lang, "eng")) {

                notice = "Do You Want to End Section";
                confirm = "Yes";
                deny = "No";
                title = "Notification";

            } else {
                notice = "确认退出实时监控面板吗？";
                confirm = "确认";
                deny = "取消";
                title = "系统提示";
            }

            new AlertDialog.Builder(Dashboard.this).setTitle(title)
                    .setMessage(notice)
                    .setPositiveButton(confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            disconnectDots();

                            if (updateTimer != null) {

                                updateTimer.cancel();

                            }

                            if (updateTask != null) {
                                updateTask.cancel();
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
                    }).setNegativeButton(deny, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    }).show();
        }
        return false;
    }

    public static void createPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mSensorManager.registerListener(mSensorListener, mAcclSensor, 50000);
        mSensorManager.registerListener(mSensorListener, mMagSensor, 50000);
        mSensorManager.registerListener(mSensorListener, mAcclLinearSensor, 50000);
        // connectDeviceHR(1);

        if (tabMode == 1) {
            webView.onResume();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // mSensorManager.unregisterListener(mSensorListener);
        // connectDeviceHR(0);
        // mLocationClientGD.stopLocation();
        if (tabMode == 1) {
            webView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (updateTimer != null) {

            updateTimer.cancel();

        }

        if (updateTask != null) {
            updateTask.cancel();
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

        // connectDeviceHR(0);
    }

    @Override
    public void onXsensDotServicesDiscovered(String s, int i) {

    }

    @Override
    public void onXsensDotFirmwareVersionRead(String s, String s1) {

    }

    @Override
    public void onXsensDotTagChanged(String s, String s1) {

    }

    @Override
    public void onXsensDotBatteryChanged(String s, int i, int i1) {

    }

    @Override
    public void onXsensDotButtonClicked(String s, long l) {

    }

    @Override
    public void onXsensDotPowerSavingTriggered(String s) {

    }

    @Override
    public void onReadRemoteRssi(String s, int i) {

    }

    @Override
    public void onXsensDotOutputRateUpdate(String s, int i) {

    }

    @Override
    public void onXsensDotFilterProfileUpdate(String s, int i) {

    }

    @Override
    public void onXsensDotGetFilterProfileInfo(String s, ArrayList<FilterProfileInfo> arrayList) {

    }

    @Override
    public void onSyncStatusUpdate(String s, boolean b) {

    }

    private void connectDeviceHR(int status) {

        try {

            mConnectedHRMs = 0;
            if (status == 1) {
                mHRfirstConnect = 0;
                for (int i = 0; i < mListBelts.size(); ++i) {
                    if (mListBelts.get(i) != null) {
                        BleConnect localConnect = new BleConnect(Dashboard.this, mListBelts.get(i));
                        localConnect.Connect();

                        // Timer timer = new Timer();
                        // timer.schedule(new TimerTask() {
                        // @Override
                        // public void run() {
                        // Looper.prepare();
                        // localConnect.Connect();
                        // Looper.loop();
                        // }
                        // },2000);

                        initLiveDataObservers(localConnect, i);
                        mListBeltsConnect.add(localConnect);
                    }
                }
                mHRfirstConnect = 1;
            } else {
                for (int i = 0; i < mListBeltsConnect.size(); ++i) {
                    if (mListBelts.get(i) != null) {
                        mListBeltsConnect.get(i).Disconnect();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("connect_HR_Fail");
        }

        // HeartRateMonitorListIndex = 0;
        // mConnectedHRMs = 0;
        //
        // if (threadConnectHRM != null) {
        // threadConnectHRM.interrupt();
        // }
        //
        // threadConnectHRM = new Thread(new Runnable() {
        // @Override
        // public void run() {
        //
        // try {
        // if (status == 1){
        // while (HeartRateMonitorListIndex < mListBelts.size()){
        // if (mListBelts.get(HeartRateMonitorListIndex)!=null) {
        // BleConnect localConnect = new BleConnect(Dashboard.this,
        // mListBelts.get(HeartRateMonitorListIndex));
        // localConnect.Connect();
        //// initLiveDataObservers(localConnect, HeartRateMonitorListIndex);
        // suspendedHRM = true;
        // System.out.println("connecting HRM:" + HeartRateMonitorListIndex);
        // }else{
        // HeartRateMonitorListIndex++;
        // suspendedHRM = false;
        // }
        // synchronized (threadConnectHRM) {
        // try {
        // if (suspendedHRM) {
        // threadConnectHRM.wait();
        // }
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        // }
        // }else {
        // for (int i = 0; i<mListBeltsConnect.size(); ++i){
        // if (mListBelts.get(i)!=null) {
        // mListBeltsConnect.get(i).Disconnect();
        // }
        // }
        // }
        //
        // } catch (Exception e) {
        // e.printStackTrace();
        // System.out.println("connect_HR_Fail");
        // }
        //
        // }
        // });
        //
        // threadConnectHRM.start();

    }

    private void initLiveDataObservers(final BleConnect localConnect, final int i) {
        LiveData<String> connectionState = localConnect.getConnectionState();
        connectionState.observe(Dashboard.this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s) {
                    case "STATE_CONNECTED":
                        mConnectedHRMs++;

                        // HeartRateMonitorListIndex++;
                        // mListBeltsConnect.add(localConnect);
                        // suspendedHRM = false;
                        //
                        // synchronized(threadConnectHRM) {
                        // threadConnectHRM.notify();
                        // }

                        Toast.makeText(Dashboard.this, "HRM:" + (i + 1), Toast.LENGTH_SHORT).show();
                        break;
                    case "STATE_CONNECTING":
                        // Toast.makeText(Dashboard.this, "Device Connecting",
                        // Toast.LENGTH_SHORT).show();
                        break;
                    case "STATE_DISCONNECTED":
                        // localConnect.Connect();
                        // Toast.makeText(Dashboard.this, "Device Disconnected",
                        // Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        LiveData<Integer> readingLiveData = localConnect.getReadingLiveData();
        readingLiveData.observe(Dashboard.this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integers) {
                HR_params_cache[i] = integers;

                // mTvListHR.get(i).setText(String.valueOf(integers));
                // if(integers<=90){
                // mTvListHR.get(i).setBackgroundResource(R.drawable.gradient_green);
                // mTvListHR.get(i).setTextColor(Color.WHITE);
                //
                // }else if (integers <= 130){
                // mTvListHR.get(i).setBackgroundResource(R.drawable.gradient_orange);
                // mTvListHR.get(i).setTextColor(Color.DKGRAY);
                //
                // }else {
                // mTvListHR.get(i).setBackgroundResource(R.drawable.gradient_red);
                // mTvListHR.get(i).setTextColor(Color.WHITE);
                // }

            }
        });
    }

    private void updateUI_sub_HR() {

        for (int i = 0; i < mListBelts.size(); i++) {
            mTvListHR.get(i).setText(String.valueOf(HR_params_cache[i]));

            if (HR_params_cache[i] <= 90) {
                mTvListHR.get(i).setBackgroundResource(R.drawable.gradient_green);
                mTvListHR.get(i).setTextColor(Color.WHITE);

            } else if (HR_params_cache[i] <= 150) {
                mTvListHR.get(i).setBackgroundResource(R.drawable.gradient_orange);
                mTvListHR.get(i).setTextColor(Color.DKGRAY);

            } else {
                mTvListHR.get(i).setBackgroundResource(R.drawable.gradient_red);
                mTvListHR.get(i).setTextColor(Color.WHITE);
            }

        }

    }

    private double strokeSplit(double sectionStart) {

        double splitRatio = 0;
        postCalibrateDuration = System.currentTimeMillis() - calibrateBegTime;

        if (postCalibrateDuration <= 60000) {

        }

        return splitRatio;
    }

    private void setMarker(LatLng latLng, String title, String content) {
        if (mGPSMarker != null) {
            mGPSMarker.remove();
        }
        // 获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        int width = (wm.getDefaultDisplay().getWidth()) / 2;
        int height = ((wm.getDefaultDisplay().getHeight()) / 2) - 80;
        markOptions = new MarkerOptions();
        // markOptions.draggable(true);//设置Marker可拖动
        markOptions
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gradient_green_round)))
                .anchor(0.5f, 0.7f);
        // 设置一个角标
        mGPSMarker = aMap.addMarker(markOptions);
        // 设置marker在屏幕的像素坐标
        mGPSMarker.setPosition(latLng);
        // mGPSMarker.setTitle(title);
        // mGPSMarker.setSnippet(content);
        // 设置像素坐标
        // mGPSMarker.setPositionByPixels(width, height);
        // if (!TextUtils.isEmpty(content)) {
        // mGPSMarker.showInfoWindow();
        // }
        // webView.invalidate();
    }

    private void mountPolar(int code) {

        System.out.println("selected HRM:" + HRMpassThrough);

        if (code == 1 && HRMpassThrough == 1) {

            api = PolarBleApiDefaultImpl.defaultImplementation(this, PolarBleApi.ALL_FEATURES);
            api.setPolarFilter(false);

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

    private void polarDataRefresh(String id, int hr) {

        for (int i = 0; i < mListBeltsName.size(); i++) {
            if (id.equals(mListBeltsName.get(i))) {

                HR_params_cache[i] = hr;
                System.out.println("_________HR BROADCAST_________:" + id + "____HR:____ " + hr);

            }

        }

    }

    public void autoPostStart() {

        final String serverPath = PATH;

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(Dashboard.this, "请输入账号密码", Toast.LENGTH_LONG).show();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        Map<String, Object> map = new HashMap<>();
                        map.put("pwd", password);
                        map.put("name", username);
                        map.put("MAC", addressMAC);
                        String paramsJson = JSON.toJSONString(map);
                        String data = "pwd:" + password + ",name:" + username;

                        System.out.println(data);
                        System.out.println(paramsJson);

                        URL url = new URL(serverPath);
                        System.out.println(url);

                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setConnectTimeout(3000);
                        httpURLConnection.setDoOutput(true);// 打开输出流，以便向服务器提交数据
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                        httpURLConnection.setRequestProperty("accept", "application/json");
                        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(paramsJson.length()));
                        OutputStream outputStream = httpURLConnection.getOutputStream();
                        outputStream.write(paramsJson.getBytes());

                        int responseCode = httpURLConnection.getResponseCode();
                        System.out.println(responseCode);

                        if (200 == responseCode) {

                            InputStream inputStream = httpURLConnection.getInputStream();
                            final Map<String, Object> inputStreamMap = JSON.parseObject(inputStream, Map.class);
                            Log.i("inputStream", "success:" + inputStreamMap.get("success"));
                            Log.i("inputStream", "Code:" + inputStreamMap.get("code"));

                            List<HttpCookie> listCookie = cookieManager.getCookieStore().getCookies();
                            Log.i("cookie", "cookie:" + listCookie);

                            final String responseMsg = StreamTool.getString(inputStream);

                            if (String.valueOf(inputStreamMap.get("code")).equals("u200")) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Toast.makeText(MainActivity.this, "Code:" +
                                        // String.valueOf(inputStreamMap.get("Code")), Toast.LENGTH_LONG).show();
                                        // Toast.makeText(Dashboard.this, "登录成功", Toast.LENGTH_LONG).show();
                                        // System.out.println(responseMsg);
                                        // intent.putExtra("userName",username);
                                        // intent.putExtra("password", password);
                                        // Toast.makeText(Dashboard.this, "成功连接到服务器", Toast.LENGTH_LONG).show();

                                    }
                                });

                            } else if (String.valueOf(inputStreamMap.get("code")).equals("u410")) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Toast.makeText(MainActivity.this, "Code:" +
                                        // String.valueOf(inputStreamMap.get("Code")), Toast.LENGTH_LONG).show();
                                        Toast.makeText(Dashboard.this, "账号密码不能为空", Toast.LENGTH_LONG).show();
                                        System.out.println(responseMsg);

                                    }
                                });

                            } else if (String.valueOf(inputStreamMap.get("code")).equals("u411")) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Toast.makeText(MainActivity.this, "Code:" +
                                        // String.valueOf(inputStreamMap.get("Code")), Toast.LENGTH_LONG).show();
                                        Toast.makeText(Dashboard.this, "账号或密码错误", Toast.LENGTH_LONG).show();
                                        System.out.println(responseMsg);

                                    }
                                });

                            } else if (String.valueOf(inputStreamMap.get("code")).equals("u412")) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Toast.makeText(MainActivity.this, "Code:" +
                                        // String.valueOf(inputStreamMap.get("Code")), Toast.LENGTH_LONG).show();
                                        Toast.makeText(Dashboard.this, "登录失败，请联系管理员", Toast.LENGTH_LONG).show();
                                        System.out.println(responseMsg);

                                    }
                                });

                            } else if (String.valueOf(inputStreamMap.get("code")).equals("u413")) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Toast.makeText(MainActivity.this, "Code:" +
                                        // String.valueOf(inputStreamMap.get("Code")), Toast.LENGTH_LONG).show();
                                        Toast.makeText(Dashboard.this, "设备已与其他账号绑定，请重置", Toast.LENGTH_LONG).show();
                                        System.out.println(responseMsg);

                                    }
                                });

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(Dashboard.this,
                                                "code:" + String.valueOf(inputStreamMap.get("code")), Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });
                            }

                        } else {
                            System.out.println("responseCode = " + responseCode);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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

                            updateResultParams();

                        }
                    });
                }
            };
            timer.schedule(updateResult, 0, 1000);
        }
    }

    private void updateResultParams() {

        double boatSpeedResult = boatSpeed_0_GD;
        double strokeRateResult = Double.parseDouble((String) mStrokeRate.getText());

        double[] heartRateResultArray = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };

        double boatSpeedLevel = 0;
        double strokeRateLevel = 0;

        if (boatSpeedResult <= 1) {

            boatSpeedLevel = 1;

        } else if (boatSpeedResult > 1 && boatSpeedResult <= 2) {

            boatSpeedLevel = 2;

        } else if (boatSpeedResult > 2 && boatSpeedResult <= 3) {

            boatSpeedLevel = 3;

        } else if (boatSpeedResult > 3 && boatSpeedResult <= 4) {

            boatSpeedLevel = 4;

        } else if (boatSpeedResult > 4) {

            boatSpeedLevel = 5;

        }

        if (strokeRateResult <= 1) {

            strokeRateLevel = 1;

        } else if (strokeRateResult > 1 && strokeRateResult <= 2) {

            strokeRateLevel = 2;

        } else if (strokeRateResult > 2 && strokeRateResult <= 3) {

            strokeRateLevel = 3;

        } else if (strokeRateResult > 3 && strokeRateResult <= 4) {

            strokeRateLevel = 4;

        } else if (strokeRateResult > 4) {

            strokeRateLevel = 5;

        }

        strokeCount = strokeCount + 1;

    }

    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            System.out.println(all);

            for (NetworkInterface nif : all) {
                // System.out.println(nif.getName());

                if (!nif.getName().equalsIgnoreCase("wlan0"))
                    continue;
                // if (!nif.getName().equalsIgnoreCase("rmnet_data1")) continue;

                byte[] macBytes = nif.getHardwareAddress();

                if (macBytes == null) {
                    return "02:00:00:00:00:00";
                    // forced assignment of MAC
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                // return res1.toString();
                return "02:00:00:00:00:00";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }
}
