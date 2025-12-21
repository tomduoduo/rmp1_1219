package com.motionrivalry.rowmasterpro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.kyleduo.switchbutton.SwitchButton;
import com.opencsv.CSVWriter;
import com.tarek360.instacapture.Instacapture;
import com.tarek360.instacapture.listener.SimpleScreenCapturingListener;
import com.xw.repo.BubbleSeekBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.iwgang.countdownview.CountdownView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Speedometer extends AppCompatActivity {

    private static final String TAG = "SensorTest";
    private static final int REQUEST_PERMISSION_LOCATION = 2048;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1024;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mAccelerometerLinear;
    private Sensor mMagnetic;
    private Sensor mAllData;
    private SensorProcessor sensorProcessor;

    private ImageView boatRoll;
    private ImageView boatYaw;

    private Button mSelectDistance;
    private Button mStart;
    private int mStartStatus = 0;

    private String[] mItemsType;
    private String[] mWeightType;
    private String[] mTrainType;

    private ArrayList<String> distanceSections = new ArrayList<String>();

    private PopupWindow windowDistance = null;
    private PopupWindow windowCountdown = null;
    private PopupWindow windowResult = null;
    private PopupWindow windowUploadResult = null;
    private BubbleSeekBar mDistanceSlider;

    private View popupWindowView;
    private View popupCountdownView;
    private View popupResultView;
    private View popupUploadResultView;

    private String selectedType = "1x";
    private String selectedWeight = "H";
    private String selectedTrain = "常规训练";
    private int selectedDistance = 2000;

    private TextView boatTravelTarget;

    private Chronometer totalElapse;
    private TextView halfKmElapse;

    private SwitchButton mCountDownSwitch;
    private int mCountDownSwitchStatus;
    private int mStartTerminate = 0;

    private Spinner spinnerTypeSelect;
    private Spinner spinnerWeightSelect;
    private Spinner spinnerTrainSelect;

    private float speed = 0;
    private Location mLocation;
    private LocationManager locationManager;
    private TextView mSpeed;
    private TextView mDistance;
    private TextView mSpeedAvg;
    private TextView mSpeedMax;
    private TextView mStrokeRate;
    private TextView mStrokeCount;
    private TextView mStrokeRateAvg;
    private ImageView mBoatYaw;
    private String strokeRateAvgTx = "0";
    private String speedTx = "0";

    private TextView mDateResult;
    private TextView mDistanceResult;
    private TextView mAvgSPMResult;
    private TextView mStrokeCountResult;
    private TextView mSectionTimeResult;
    private TextView mAvgBoatSpeed;
    private TextView mMaxBoatSpeed;
    private TextView mUserName;
    private Button mSaveExit;
    private Button mNoSaveExit;

    // ========== LocationTracker集成 ==========
    private LocationTracker locationTracker;

    private double latitude_0;
    private double longitude_0;
    private double speedAvg = 0;
    private double speedMax = 0;
    private double traveledDistance = 0;

    private double strokeRateAvg = 0;

    private String mDistanceResultTx = "0";
    private String mAvgSPMResultTx = "0";
    private String mStrokeCountResultTx = "0";
    private String mSectionTimeResultTx = "0";
    private String mAvgBoatSpeedTx = "0";
    private String mMaxBoatSpeedTx = "0";

    private String addressRowData = null;
    private String addressRowDataLive = null;
    private DataLogger dataLogger = new DataLogger();
    private FileWriter customDataLive;
    private CSVWriter writerCustomDataLive;
    private int logCreated = 0;
    private String uploadPATH = "";
    private String updatePATH = "";
    private String strBegTime = "";
    private String strokeRateTx = "0";
    private double logBeginTime = 0;

    private int uploadStatus = 0;
    private int timeCorrectSec = 0;
    private int timeCorrectMin = 0;

    // ========== StrokeDetector集成 ==========
    private StrokeDetector strokeDetector;
    private boolean isStrokeDisplayReset = false; // 桨频显示重置标志

    private HttpURLConnection httpURLConnectionUpdate;
    private Timer timer;
    private TimerTask task;
    private NetworkManager networkManager;
    private String sectionTimeTX = "0.0";
    private String userName;
    private String mDistanceTx;
    private String mDisplayTimeTx;

    // 时间相关字段（测试用）
    private long startTime = 0;
    private long currentTime = 0;

    private UIManager uiManager;

    private AMapLocationClient mLocationClientGD = null;
    private AMapLocationListener mLocationListenerGD = null;
    private AMapLocationClientOption mLocationOptionGD = null;

    private double latitude_0_GD;
    private double longitude_0_GD;

    private int elapsedHour = 0;
    private int hourMarker = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_speedometer);

        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(Speedometer.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    Speedometer.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }

        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        System.out.println(userName);

        boatRoll = findViewById(R.id.img_roll_boat);
        boatYaw = findViewById(R.id.img_yaw_boat);

        mSpeedAvg = findViewById(R.id.boat_speed_average);
        mSpeedMax = findViewById(R.id.boat_speed_max);
        mStrokeRate = findViewById(R.id.stroke_rate);
        mStrokeCount = findViewById(R.id.stroke_count);
        mStrokeRateAvg = findViewById(R.id.stroke_rate_average);
        mBoatYaw = findViewById(R.id.img_yaw_boat);

        mSpeed = findViewById(R.id.gps_speed);
        mDistance = findViewById(R.id.boat_travel_distance_actual);
        String serviceName = this.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(serviceName);

        // ========== LocationTracker初始化 ==========
        locationTracker = new LocationTracker(this, 500, 3);
        locationTracker.setLocationUpdateListener(new LocationTracker.LocationUpdateListener() {
            @Override
            public void onLocationUpdate(LocationTracker.LocationUpdate update) {
                // 更新UI显示
                speedTx = uiManager.updateSpeed(update.speed);
                uiManager.updateMaxSpeed(update.maxSpeed);
                uiManager.updateAvgSpeed(update.avgSpeed);
                uiManager.updateDistance(update.totalDistance);
                uiManager.updateSplitTime(update.splitTime);

                // 更新内部状态变量
                speedAvg = update.avgSpeed;
                speedMax = update.maxSpeed;
                traveledDistance = update.totalDistance;
            }
        });

        mSelectDistance = findViewById(R.id.distance_select_speedometer);
        mStart = findViewById(R.id.start_measure_speedometer);

        mCountDownSwitch = findViewById(R.id.countdown_switch);
        mCountDownSwitch.setCheckedNoEvent(false);
        mCountDownSwitchStatus = 0;

        mCountDownSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mCountDownSwitchStatus = 1;
                } else {
                    mCountDownSwitchStatus = 0;
                }
            }
        });

        mItemsType = getResources().getStringArray(R.array.ItemType);
        mWeightType = getResources().getStringArray(R.array.ItemWeight);
        mTrainType = getResources().getStringArray(R.array.ItemTrain);

        boatTravelTarget = findViewById(R.id.boat_travel_distance);

        totalElapse = (Chronometer) this.findViewById(R.id.boat_travel_total_time);
        // totalElapse.setFormat("0"+String.valueOf(hour)+":%s");

        halfKmElapse = findViewById(R.id.boat_travel_split_time);

        // 初始化UIManager
        uiManager = new UIManager(mSpeed, mSpeedAvg, mSpeedMax, mDistance,
                mStrokeRate, mStrokeCount, mStrokeRateAvg, halfKmElapse,
                boatRoll, mBoatYaw);

        // 初始化NetworkManager
        networkManager = new NetworkManager(uploadPATH, updatePATH);

        spinnerTypeSelect = findViewById(R.id.spinner_type_select);
        spinnerWeightSelect = findViewById(R.id.spinner_weight_select);
        spinnerTrainSelect = findViewById(R.id.spinner_train_select);

        ArrayAdapter<String> adapterType = new ArrayAdapter(this, R.layout.my_spinner, mItemsType);
        ArrayAdapter<String> adapterWeight = new ArrayAdapter(this, R.layout.my_spinner, mWeightType);
        ArrayAdapter<String> adapterTrain = new ArrayAdapter(this, R.layout.my_spinner, mTrainType);

        adapterType.setDropDownViewResource(R.layout.my_drop_down);
        adapterWeight.setDropDownViewResource(R.layout.my_drop_down);
        adapterTrain.setDropDownViewResource(R.layout.my_drop_down);

        spinnerTypeSelect.setAdapter(adapterType);
        spinnerWeightSelect.setAdapter(adapterWeight);
        spinnerTrainSelect.setAdapter(adapterTrain);

        spinnerTypeSelect.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                selectedType = arg0.getItemAtPosition(arg2).toString();
                System.out.println(selectedType);

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        spinnerWeightSelect.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                selectedWeight = arg0.getItemAtPosition(arg2).toString();
                System.out.println(selectedWeight);

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        spinnerTrainSelect.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                selectedTrain = arg0.getItemAtPosition(arg2).toString();
                System.out.println(selectedTrain);

            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupWindowView = inflater.inflate(R.layout.popup_distance, null, false);
        popupCountdownView = inflater.inflate(R.layout.popup_countdown, null, false);
        popupResultView = inflater.inflate(R.layout.popup_result_old, null, false);
        popupUploadResultView = inflater.inflate(R.layout.popup_upload, null, false);

        mDistanceSlider = popupWindowView.findViewById(R.id.distance_slider);

        mDistanceSlider.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                    boolean fromUser) {
                selectedDistance = progress;
                String selectedDistanceTx = selectedDistance + "M";
                mSelectDistance.setText(selectedDistanceTx);
                System.out.println(selectedDistance);
                boatTravelTarget.setText(selectedDistanceTx);
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat,
                    boolean fromUser) {
            }
        });

        sensorProcessor = new SensorProcessor();
        sensorProcessor.setListener(new SensorProcessor.SensorDataListener() {
            @Override
            public void onSensorDataUpdated(SensorProcessor.SensorData data) {
                // 更新当前时间（测试用）
                currentTime = System.currentTimeMillis();

                // 更新船只姿态显示
                uiManager.updateBoatRoll(data.roll);

                // 传递给StrokeDetector进行桨频检测
                if (logCreated == 1 && mStartStatus == 1) {
                    StrokeDetector.StrokeResult result = strokeDetector.detectStroke(
                            data.boatAcceleration, System.currentTimeMillis());

                    if (result.isNewStroke) {
                        strokeRateTx = uiManager.updateStrokeRate(result.strokeRate);
                        uiManager.updateStrokeCount(result.strokeCount);

                        // 更新平均桨频
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

                        double tempTotalSecStrokeRateAvg = tempHourStrokeRateAvg * 3600 + tempMinStrokeRateAvg * 60
                                + tempSecStrokeRateAvg;
                        double totalElapsedMin = tempTotalSecStrokeRateAvg / 60;
                        double strokeCountDouble = result.strokeCount;

                        if (strokeCountDouble < 2) {
                            strokeRateAvg = 0;
                        } else {
                            strokeRateAvg = strokeCountDouble / totalElapsedMin;
                        }

                        uiManager.updateAvgStrokeRate(strokeRateAvg);
                        uiManager.updateBoatYaw(data.boatYawAngle);
                    }
                } else {
                    // 当检测器未运行时，仅在首次执行重置
                    if (!isStrokeDisplayReset) {
                        mStrokeRate.setText("0.0");
                        strokeRateTx = "0.0";
                        mBoatYaw.setRotation(0);
                        isStrokeDisplayReset = true; // 标记已重置
                    }
                }

                // ========== 新增：CSV 数据记录 ==========
                if (logCreated == 1) {
                    // 获取当前时间信息
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

                    // 更新时间相关变量
                    mDisplayTimeTx = tempHourStrokeRateAvg + ":" + tempMinStrokeRateAvg + ":" + tempSecStrokeRateAvg;
                    double tempTotalSecStrokeRateAvg = tempHourStrokeRateAvg * 3600 + tempMinStrokeRateAvg * 60
                            + tempSecStrokeRateAvg;
                    sectionTimeTX = String.valueOf(tempTotalSecStrokeRateAvg);

                    // 写入CSV数据
                    dataLogger.logData(new DataLogger.RowingData(
                            traveledDistance, strokeRateTx, speedTx,
                            data.boatAcceleration, data.roll, data.boatYawAngle));
                }
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometerLinear = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        mLocationClientGD = new AMapLocationClient(getApplicationContext());
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

        mSelectDistance.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showWindowDistance();
                backgroundAlpha(0.2f);

            }

        });

        final TextView mGo = popupCountdownView.findViewById(R.id.go_mark);

        mStart.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {

                if (mStartStatus == 1) {

                    shutdownAlert();

                } else {

                    // ========== StrokeDetector集成 - 启动检测器 ==========
                    strokeDetector.start();

                    isStrokeDisplayReset = false; // 重置标志，允许下次停止时重置UI

                    strokeRateTx = "0";

                    // mBoatYaw.setRotation(0);
                    // timeCorrectSec = 0;
                    // timeCorrectMin = 0;

                    mStartStatus = 1;
                    mGo.setAlpha(0);
                    mGo.animate().setStartDelay(5050).alpha(1).setDuration(1).start();
                    mStart.setBackgroundResource(R.drawable.button_6);
                    mStart.setText("结束");
                    mStart.setTextColor(Color.WHITE);

                    spinnerTypeSelect.setEnabled(false);
                    spinnerWeightSelect.setEnabled(false);
                    spinnerTrainSelect.setEnabled(false);
                    mSelectDistance.setEnabled(false);

                    locationTracker.startTracking();

                    if (mCountDownSwitchStatus == 0) {

                        showCountdown();
                        try {
                            startTimer(5500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        backgroundAlpha(0.3f);

                    } else {

                        try {
                            startTimer(10);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

        });

        if ((ActivityCompat.checkSelfPermission(Speedometer.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(Speedometer.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {

            return;
        }

        // locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 200, 1,
        // locationListener);

        // ========== StrokeDetector初始化 ==========
        strokeDetector = new StrokeDetector(1400, 1.3, 5000);

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

    private void shutdownAlert() {

        new AlertDialog.Builder(Speedometer.this).setTitle("系统提示")// 设置对话框标题

                .setMessage("确认结束此次划行吗？")// 设置显示的内容
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {// 添加确定按钮

                    @Override

                    public void onClick(DialogInterface dialog, int which) {// 确定按钮的响应事件
                        mStartStatus = 0;

                        // ========== StrokeDetector集成 - 停止检测器 ==========
                        strokeDetector.stop();

                        DecimalFormat decimalFormat = new DecimalFormat("0.0");
                        DecimalFormat decimalFormat_1 = new DecimalFormat("0");
                        String distanceTx = decimalFormat_1.format(traveledDistance);
                        // String speedTx = decimalFormat.format(speed);
                        String speedAvgTx = decimalFormat.format(speedAvg);
                        String speedMaxTx = decimalFormat.format(speedMax);
                        strokeRateAvgTx = decimalFormat.format(strokeRateAvg);

                        int tempHourResult = 0;
                        int tempMinResult = 0;
                        int tempSecResult = 0;

                        if (totalElapse.length() <= 5) {

                            tempHourResult = 0;
                            tempMinResult = Integer.parseInt(totalElapse.getText().toString().split(":")[0]);
                            tempSecResult = Integer.parseInt(totalElapse.getText().toString().split(":")[1]);

                        } else {
                            tempHourResult = Integer.parseInt(totalElapse.getText().toString().split(":")[0]);
                            tempMinResult = Integer.parseInt(totalElapse.getText().toString().split(":")[1]);
                            tempSecResult = Integer.parseInt(totalElapse.getText().toString().split(":")[2]);

                        }

                        mSectionTimeResultTx = tempHourResult + ":" + tempMinResult + ":" + tempSecResult;

                        mDistanceResultTx = distanceTx;
                        mAvgSPMResultTx = strokeRateAvgTx;
                        mStrokeCountResultTx = String.valueOf(strokeDetector.getStrokeCount());
                        mAvgBoatSpeedTx = speedAvgTx;
                        mMaxBoatSpeedTx = speedMaxTx;

                        mStart.setBackgroundResource(R.drawable.button_4);
                        mStart.setText("开始");
                        mStart.setTextColor(Color.parseColor("#6C6C6C"));

                        spinnerTypeSelect.setEnabled(true);
                        spinnerWeightSelect.setEnabled(true);
                        spinnerTrainSelect.setEnabled(true);
                        mSelectDistance.setEnabled(true);

                        totalElapse.setBase(SystemClock.elapsedRealtime());
                        totalElapse.stop();

                        elapsedHour = 0;
                        hourMarker = 0;

                        traveledDistance = 0;
                        speed = 0;
                        speedAvg = 0;
                        speedMax = 0;

                        // 重置LocationTracker状态
                        locationTracker.reset();

                        mBoatYaw.setRotation(0);
                        // timeCorrectSec = 0;
                        // timeCorrectMin = 0;

                        uiManager.resetAllDisplays();

                        locationTracker.stopTracking();

                        try {
                            dataLogger.close();
                            logCreated = 0;

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(Speedometer.this, "关闭日志失败", Toast.LENGTH_SHORT).show();
                        }
                        showResult();

                    }

                }).setNegativeButton("返回", new DialogInterface.OnClickListener() {// 添加返回按钮

                    @Override

                    public void onClick(DialogInterface dialog, int which) {// 响应事件
                        return;
                    }

                }).show();// 在按键响应事件中显示此对话框

    }

    private void showResult() {

        backgroundAlpha(0.2f);
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;

        // windowResult = new
        // PopupWindow(popupResultView,width*65/100,height*85/100,true);
        windowResult = new PopupWindow(popupResultView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true);

        // windowResult.update();

        View parentView = LayoutInflater.from(Speedometer.this).inflate(R.layout.activity_speedometer, null);
        windowResult.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        // windowResult.setBackgroundDrawable(new ColorDrawable(0x00000000));
        // windowResult.setFocusable(false);
        // windowResult.setOutsideTouchable(false);

        mDateResult = popupResultView.findViewById(R.id.log_time);
        mDistanceResult = popupResultView.findViewById(R.id.result_distance_actual);
        mAvgSPMResult = popupResultView.findViewById(R.id.result_SPM_average);
        mStrokeCountResult = popupResultView.findViewById(R.id.result_stroke_count);
        mSectionTimeResult = popupResultView.findViewById(R.id.result_section_time);
        mAvgBoatSpeed = popupResultView.findViewById(R.id.result_boatspeed_avg);
        mMaxBoatSpeed = popupResultView.findViewById(R.id.result_boatspeed_max);
        mSaveExit = popupResultView.findViewById(R.id.result_save);
        mNoSaveExit = popupResultView.findViewById(R.id.result_no_save);
        mUserName = popupResultView.findViewById(R.id.user_id);

        SimpleDateFormat simpleDateFormatResult = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date dateResult = new Date(System.currentTimeMillis());
        mDateResult.setText(simpleDateFormatResult.format(dateResult));

        mDistanceResult.setText(mDistanceResultTx);
        mAvgSPMResult.setText(mAvgSPMResultTx);
        mStrokeCountResult.setText(mStrokeCountResultTx);
        mSectionTimeResult.setText(mSectionTimeResultTx);
        mAvgBoatSpeed.setText(mAvgBoatSpeedTx);
        mMaxBoatSpeed.setText(mMaxBoatSpeedTx);
        mUserName.setText(userName);

        mSaveExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Instacapture.INSTANCE.capture(Speedometer.this, new SimpleScreenCapturingListener() {
                    @Override
                    public void onCaptureComplete(Bitmap bitmap) {

                        saveImageToGallery(bitmap, Speedometer.this);

                    }
                }, mSaveExit, mNoSaveExit);

                timer.cancel();
                task.cancel();
                sectionTimeTX = "0.0";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        try {
                            upload();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }).start();

                windowResult.dismiss();
            }

        });

        mNoSaveExit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                timer.cancel();
                task.cancel();
                sectionTimeTX = "0.0";
                windowResult.dismiss();
                backgroundAlpha(1f);

            }

        });

        windowResult.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {

                backgroundAlpha(1f);

            }

        });

    }

    private void showWindowUploadResult() {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;

        windowUploadResult = new PopupWindow(popupUploadResultView, width * 20 / 100, width * 20 / 100, false);
        View parentView = LayoutInflater.from(Speedometer.this).inflate(R.layout.activity_speedometer, null);
        windowUploadResult.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        windowUploadResult.setFocusable(false);

    }

    private void showWindowDistance() {

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;

        windowDistance = new PopupWindow(popupWindowView, width * 75 / 100, height * 85 / 100, true);
        View parentView = LayoutInflater.from(Speedometer.this).inflate(R.layout.activity_speedometer, null);
        windowDistance.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        windowDistance.setFocusable(true);

        windowDistance.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {

                backgroundAlpha(1f);

            }

        });
    }

    private void showCountdown() {

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;

        windowCountdown = new PopupWindow(popupCountdownView, width * 30 / 100, width * 30 / 100, true);
        View parentView = LayoutInflater.from(Speedometer.this).inflate(R.layout.activity_speedometer, null);
        windowCountdown.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        windowCountdown.setFocusable(true);

        windowCountdown.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onDismiss() {

                if (mStartTerminate == 1) {

                    mStartStatus = 0;
                    mStart.setBackgroundResource(R.drawable.button_4);
                    mStart.setText("开始");
                    mStart.setTextColor(Color.parseColor("#6C6C6C"));

                    spinnerTypeSelect.setEnabled(true);
                    spinnerWeightSelect.setEnabled(true);
                    spinnerTrainSelect.setEnabled(true);
                    mSelectDistance.setEnabled(true);

                    totalElapse.setBase(SystemClock.elapsedRealtime());
                    totalElapse.stop();

                    traveledDistance = 0;
                    speed = 0;
                    speedAvg = 0;
                    speedMax = 0;

                    // 重置LocationTracker状态
                    locationTracker.reset();

                    // 使用UIManager统一重置所有UI显示
                    uiManager.resetAllDisplays();
                    speedTx = "0.0";

                    locationTracker.stopTracking();

                }

                backgroundAlpha(1f);

            }

        });

    }

    private void startTimer(long delayed) throws Exception {

        SimpleDateFormat simpleDateFormatCache = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        logBeginTime = System.currentTimeMillis();
        strBegTime = simpleDateFormatCache.format(logBeginTime);
        addressRowData = this.getFilesDir() + "/" + strBegTime + ".csv";
        addressRowDataLive = this.getFilesDir() + "/rowDataLive.csv";

        networkManager.initConnection();

        try {
            dataLogger.createLog(addressRowData);
            logCreated = 1;

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(Speedometer.this, "文件创建失败", Toast.LENGTH_SHORT).show();
        }

        task = new TimerTask() {
            public void run() {
                NetworkManager.UpdateData d = new NetworkManager.UpdateData();
                d.userName = userName;
                d.sectionTime = sectionTimeTX;
                d.displayTime = mDisplayTimeTx;
                d.spm = mStrokeRate.getText().toString();
                d.boatSpeed = mSpeed.getText().toString();
                d.actualDistance = mDistance.getText().toString();
                d.latitude = BigDecimal.valueOf(latitude_0_GD);
                d.longitude = BigDecimal.valueOf(longitude_0_GD);
                d.sectionType = selectedTrain;
                d.playerType = selectedWeight;
                d.boatType = selectedType;
                d.targetDistance = String.valueOf(selectedDistance);
                networkManager.sendUpdate(d);
            }
        };
        timer = new Timer();
        timer.schedule(task, 5000, 2000);

        mStartTerminate = 1;
        CountdownView mCvCountdownView = popupCountdownView.findViewById(R.id.countdown_view);
        mCvCountdownView.start(delayed); // Millisecond

        // mCvCountdownView.setOnCountdownIntervalListener(100, new
        // CountdownView.OnCountdownIntervalListener() {
        // @Override
        // public void onInterval(CountdownView cv, long remainTime) {
        //
        // }
        // });

        mCvCountdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {

            @Override
            public void onEnd(CountdownView cv) {

                mStartTerminate = 0;
                totalElapse.setBase(SystemClock.elapsedRealtime());
                // totalElapse.setBase(SystemClock.elapsedRealtime() - 3590*1000);
                totalElapse.start();

                if (mCountDownSwitchStatus == 0) {
                    windowCountdown.dismiss();
                }

            }
        });

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

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册传感器监听函数
        mSensorManager.registerListener(sensorProcessor, mAccelerometer, 50000);
        mSensorManager.registerListener(sensorProcessor, mMagnetic, 50000);
        mSensorManager.registerListener(sensorProcessor, mAccelerometerLinear, 50000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // 注销监听函数
        mSensorManager.unregisterListener(sensorProcessor);
    }

    // calculateVectorData方法已被SensorProcessor替代

    public void upload() {
        networkManager.uploadFile(addressRowData, strBegTime + ".csv", new NetworkManager.UploadCallback() {
            public void onSuccess() {
                android.os.Looper.prepare();
                Toast.makeText(Speedometer.this, "上传成功", Toast.LENGTH_SHORT).show();
                android.os.Looper.loop();
            }

            public void onFailure(Exception e) {
                android.os.Looper.prepare();
                Toast.makeText(Speedometer.this, "上传失败", Toast.LENGTH_SHORT).show();
                android.os.Looper.loop();
            }
        });
    }

    // public Call file_submit(String filepath, String url, String filename){
    // OkHttpClient client = new OkHttpClient();
    // File file = new File(filepath);
    // Log.i("text",filepath);
    // RequestBody fileBody =
    // RequestBody.create(MediaType.parse("application/octet-stream"), file);
    // //请求体
    // RequestBody requestBody = new MultipartBody.Builder()
    // .setType(MultipartBody.FORM)
    //// .addPart(Headers.of(
    //// "Content-Disposition",
    //// "form-data; name=\"filename\""),
    //// RequestBody.create(null, "lzr"))//这里是携带上传的其他数据
    // .addPart(Headers.of(
    // "Content-Disposition",
    // "form-data; name=\"mFile\"; filename=\"" + filename + "\""), fileBody)
    // .build();
    // //请求的地址
    // Request request = new Request.Builder()
    // .url(url)
    // .post(requestBody)
    // .build();
    //
    // System.out.println(client.newCall(request));
    // return client.newCall(request);
    // }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    // TestSensorListener类已被SensorProcessor替代

    // public static class Utils {
    //
    // public static boolean isBluetoothAdapterEnabled(Context context) {
    // BluetoothManager bluetoothManager = (BluetoothManager)
    // context.getSystemService(Context.BLUETOOTH_SERVICE);
    //
    // if (bluetoothManager != null) {
    // BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
    //
    // if (bluetoothAdapter != null) {
    // return bluetoothAdapter.isEnabled();
    // }
    // }
    //
    // return false;
    // }
    //
    // }
    //
    //
    // private boolean hasLocationPermission() {
    // return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
    // PackageManager.PERMISSION_GRANTED;
    // }
    //
    // private void requestLocationPermission() {
    // requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
    // REQUEST_PERMISSION_LOCATION);
    // }
    //
    // private void requestEnableBluetooth() {
    // Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    // startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
    // }
    //
    // private void checkBluetoothPermissions() {
    // boolean isBluetoothAdapterEnabled = Utils.isBluetoothAdapterEnabled(this);
    // boolean hasLocationPermission = hasLocationPermission();
    //
    // if (isBluetoothAdapterEnabled) {
    // if (!hasLocationPermission) {
    // requestLocationPermission();
    // }
    // } else {
    // requestEnableBluetooth();
    // }
    //
    // Log.d(TAG, "isBluetoothAdapterEnabled " + isBluetoothAdapterEnabled + ",
    // hasLocationPermission " + hasLocationPermission);
    //
    // }
    //
    // @Override
    // public void onRequestPermissionsResult(int requestCode, @NonNull String[]
    // permissions, @NonNull int[] grantResults) {
    // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //
    // Log.d(TAG, "onRequestPermissionsResult() - requestCode = " + requestCode);
    //
    // if (requestCode == REQUEST_PERMISSION_LOCATION) {
    //
    // for (int i = 0; i < grantResults.length; i++) {
    //
    // if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
    // if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
    // checkBluetoothPermissions();
    // } else {
    // Toast.makeText(this, "Please allow location permission to use your
    // trackers.", Toast.LENGTH_LONG).show();
    // }
    // }
    // }
    // }
    // }

    // ========== 测试辅助方法 ==========
    /**
     * 获取CSV写入器（测试用）
     * 
     * @return CSV写入器实例
     */
    public CSVWriter getCSVWriter() {
        return null; // 保持接口兼容，实际使用DataLogger
    }

    /**
     * 获取CSV文件路径（测试用）
     * 
     * @return CSV文件路径
     */
    public String getCSVFilePath() {
        return dataLogger.getFilePath();
    }

    /**
     * 获取数据记录器（测试用）
     * 
     * @return DataLogger实例
     */
    public DataLogger getDataLogger() {
        return dataLogger;
    }

    /**
     * 获取开始状态（测试用）
     * 
     * @return 开始状态值
     */
    public int getStartStatus() {
        return mStartStatus;
    }

    /**
     * 获取日志创建状态（测试用）
     * 
     * @return 日志创建状态
     */
    public int getLogCreated() {
        return logCreated;
    }

    /**
     * 获取日志开始时间（测试用）
     * 
     * @return 日志开始时间
     */
    public long getLogBeginTime() {
        return (long) logBeginTime;
    }

    /**
     * 获取UI重置标志（测试用）
     * 
     * @return UI重置标志状态
     */
    public boolean isStrokeDisplayReset() {
        return isStrokeDisplayReset;
    }

    /**
     * 获取开始按钮（测试用）
     * 
     * @return 开始按钮实例
     */
    public Button getStartButton() {
        return mStart;
    }

    /**
     * 获取SensorProcessor实例（测试用）
     * 
     * @return SensorProcessor实例
     */
    public SensorProcessor getSensorProcessor() {
        return sensorProcessor;
    }

    /**
     * 获取StrokeDetector实例（测试用）
     * 
     * @return StrokeDetector实例
     */
    public StrokeDetector getStrokeDetector() {
        return strokeDetector;
    }

    /**
     * 设置日志创建状态（测试用）
     * 
     * @param status 日志创建状态
     */
    public void setLogCreated(int status) {
        logCreated = status;
    }

    /**
     * 设置开始状态（测试用）
     * 
     * @param status 开始状态
     */
    public void setStartStatus(int status) {
        mStartStatus = status;
    }

    /**
     * 获取速度显示TextView（测试用）
     * 
     * @return 速度显示TextView实例
     */
    public TextView getSpeedTextView() {
        return mSpeed;
    }

    /**
     * 获取距离显示TextView（测试用）
     * 
     * @return 距离显示TextView实例
     */
    public TextView getDistanceTextView() {
        return mDistance;
    }

    /**
     * 获取桨频显示TextView（测试用）
     * 
     * @return 桨频显示TextView实例
     */
    public TextView getStrokeRateTextView() {
        return mStrokeRate;
    }

    /**
     * 获取船只横滚角ImageView（测试用）
     * 
     * @return 船只横滚角ImageView实例
     */
    public ImageView getBoatRollImageView() {
        return boatRoll;
    }

    /**
     * 获取开始时间（测试用）
     * 
     * @return 开始时间
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * 获取当前时间（测试用）
     * 
     * @return 当前时间
     */
    public long getCurrentTime() {
        return currentTime;
    }

}