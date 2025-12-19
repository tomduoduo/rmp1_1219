package com.motionrivalry.rowmasterpro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.kyleduo.switchbutton.SwitchButton;
//import com.motionrivalry.rowmasterpro.Math.jama.Matrix;
//import com.motionrivalry.rowmasterpro.Math.jkalman.JKalman;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import Jama.Matrix;


public class DataPlayback extends AppCompatActivity {

    private String mLogTime;
    private String fileLoc = null;
    private ArrayList<String> fileList = null;
    private ArrayList<String> logList = new ArrayList<>();

    private int samplingRate = 30;

    private FrameLayout mFrame_L1;
    private FrameLayout mFrame_L2;
    private FrameLayout mFrame_L3;
    private FrameLayout mFrame_L4;
    private FrameLayout mFrame_R1;
    private FrameLayout mFrame_R2;
    private FrameLayout mFrame_R3;
    private FrameLayout mFrame_R4;

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

    private double[] UI_params_L1 = new double[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private double[] UI_params_L2 = new double[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private double[] UI_params_L3 = new double[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private double[] UI_params_L4 = new double[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    private double[] UI_params_R1 = new double[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private double[] UI_params_R2 = new double[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private double[] UI_params_R3 = new double[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
    private double[] UI_params_R4 = new double[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

    // {yaw[0], txYaw[1], avMin[2], avMax[3], txDegreeFwd[4], txDegreeBwd[5],
    // Roll[6], Pitch[7], YawMin[8], YawMax[9], YawAbs[10],
    // YawBeg[11], YawEnd[12], YawDuration[13], DynamicData[14], StrokeVelocityLast[15]}
    // StrokeTimeLast[16], instantBoatSpeed[17], boatAcceleration[18]}
    // Dynamic Data: strokeSpeed[10], catchDegree[2], strokeDepth[3]

    private double[] UI_params_boat = new double[] {0,0,0,0};

    // {instantAcceleration[0], instantBoatSpeed[1], PhoneAcceleration[2], PhoneBoatSpeed[3]}


    private String[] UI_params_Secondary_str = new String[] {"0.0","0.0","0"}; //spm, boat speed
    private float[] UI_params_Secondary_float = new float[] {0f,0f}; //boat_yaw, boat_roll
    private Integer[] HR_params_cache = new Integer[] {0,0,0,0,0,0,0,0};


    private List<String[]> logBO = null;
    private List<String[]> logL1 = null;
    private List<String[]> logL2 = null;
    private List<String[]> logL3 = null;
    private List<String[]> logL4 = null;
    private List<String[]> logR1 = null;
    private List<String[]> logR2 = null;
    private List<String[]> logR3 = null;
    private List<String[]> logR4 = null;
    private List<String[]> logPH = null;

    private List<String[]> logHOLDER = new ArrayList<>();

    private ArrayList<List<String[]>> rawLogDataList = new ArrayList<>();

    private int sizeBO = 100000000;
    private int sizeL1 = 100000000;
    private int sizeL2 = 100000000;
    private int sizeL3 = 100000000;
    private int sizeL4 = 100000000;
    private int sizeR1 = 100000000;
    private int sizeR2 = 100000000;
    private int sizeR3 = 100000000;
    private int sizeR4 = 100000000;
    private int sizePH = 100000000;

    private int[] sizeArray;

    private int itrMax = 0;
    private int maxAngularVelocity = 80;
    private int minAngularVelocity = -360;
    double boatAngle = 0;
    double correctionLeft = 90;
    double correctionRight = 90;
    double correctionPeddle = 135;
    // double correctionPeddle = 225;
    // original setting for record
    double correctionRightPitch = 0;

    private Thread thread0;
    private SeekBar mProgressBar;

    private TextView mStrokeRate;
    private TextView mBoatSpeed;
    private ImageView mBoatRoll;
    private ImageView mBoatYaw;
    private TextView mSectionTime;
    private TextView mCurrentTime;

    private boolean suspended = false;
    private int playNodePos = 0;
    private int playNodePosCache = 0;

    private Button mBtnPlay;
    private Button mBtnPause;
    private Button mFF1X;
    private Button mFF2X;
    private Button mFF5X;
    private int mReplayNode = 0;
    private int mEndNode = 0;
    private int speedMultiple = 1;

    private SwitchButton mBoatSensorSwitch;
    private SwitchButton mBoatSpeedSwitch;
    private SwitchButton mMountDegreeSwitch;
    private SwitchButton mRightCompensate;

    private int mBoatSensorSwitchStatus = 0;
    private int mBoatSpeedSwitchStatus = 0;
    private int mMountDegreeSwitchStatus = 0;
    private int mRightCompensateSwitchStatus = 0;

    private int acclCacheLength = 1;
    private int acclCacheSize = 0;
    private int acclCachePointer = 0;
    private double acclCacheSum = 0.0;
    private double[] acclCacheSamples = new double[acclCacheLength];

    private int acclPhoneCacheLength = 1;
    private int acclPhoneCacheSize = 0;
    private int acclPhoneCachePointer = 0;
    private double acclPhoneCacheSum = 0.0;
    private double[] acclPhoneCacheSamples = new double[acclPhoneCacheLength];

    private TextView mBoatSpeedTxUnit;
    private TextView mHR1;
    private TextView mHR2;
    private TextView mHR3;
    private TextView mHR4;
    private TextView mHR5;
    private TextView mHR6;
    private TextView mHR7;
    private TextView mHR8;

    private ArrayList<TextView> mTvListHR = new ArrayList<>();

    private int tabMode;
    private TextView mDistanceTx;
    private LineChart chart;
    protected Typeface tfLight;
    private float[] yaw_params_cache = new float[] {0f,0f,0f,0f,0f,0f,0f,0f};
    private int progressNow = 0;

    private Timer timer;
    private TimerTask timerTask;

    private Button mBtnStrokeVelocity;
    private Button mBtnCatchDegree;
    private Button mBtnStrokeDepth;
    private Button mBtnCatchSpeed;
    private Button mBtnStrokePower;
    private int playBackDataIndex = 4;
    private TextView mTextPlayMode;

    private double[] Euler_Cache = new double[] {0,0,0,0,0,0,0,0};

    private Button mBtnDirectionEast;
    private Button mBtnDirectionSouth;
    private int directionStatus = 1;

    private double correctionLeftSecondary = 0;
    private double correctionRightSecondary = 0;

    private FrameLayout mLineChartLayer;
    private SwitchButton mLineChartSwitch;
    private double currentBoatSpeedCache = 0;

    private int forceCacheLength = 10;
    private int [][] forceCacheSizeList = new int[8][1];
    private int [][] forceCachePointerList = new int[8][1];
    private double [][] forceCacheSumList = new double[8][1];
    private double [][] forceCacheSamplesList = new double[8][forceCacheLength];


    private int forceCacheSize = 0;
    private int forceCachePointer = 0;
    private double forceCacheSum = 0.0;
    private double[] forceCacheSamples = new double[forceCacheLength];

    private double forceSuppressionRatio = 0.6;
    private double speedSuppressionRatio = 0.6;
    private double depthSuppressionRatio = 0.7;
    private double wattageSuppressionRatio = 0.50;
    private double forceAmplifier = 1;
    private double rightCompensateRatioLow = 1.0;
    private double rightCompensateRatioHigh = 1.35;
    private double rightCompensateRatio = 1.0;


//    private double a0 = 164.5;
//    private double a1 = -96.4;
//    private double b1 = -148.1;
//    private double a2 = -31.53;
//    private double b2 = 7.361;
//    private double a3 = -30.7;
//    private double b3 = 9.122;
//    private double w = 0.9717;

//    private double p00 = 15.24;
//    private double p10 = -7.145;
//    private double p01 = -0.3318;
//    private double p20 = 1.129;
//    private double p11 = -6.221;
//    private double p02 = 14.5;

    private double p00 = 2.514;
    private double p10 = -2.107;
    private double p01 = -6.25;
    private double p20 = 1.509;
    private double p11 = 2.679;
    private double p02 = 3.826;

    private double speedNormMax = 3.5;
    private double speedNormMin = 0;
    private double depthNormMax = 10;
    private double depthNormMin = -15;
    private double powerNormMax = 800;
    private double powerNormMin = 0;

    private double wattage_p1_4 = -99.08;
    private double wattage_p2_4 = 466.6;
    private double wattage_p3_4 = -581.7;
    private double wattage_p4_4 = 331.6;
    private double wattage_p5_4 = 1.714;


    private double wattage_p1_1 = 339.3;
    private double wattage_p2_1 = -252.2;

    private double[] CacheStrokeAV = new double[] {0,0,0,0,0,0,0,0};
    private double[] CacheLengthStrokeAV = new double[] {0,0,0,0,0,0,0,0};
    private double[] CacheLocalAV_0 = new double[] {0,0,0,0,0,0,0,0};

    private double fwdSplitRatio = 0.40;
    private double fwdSplitThreshSpeed = 1.35;
    private double fwdSplitDivideFactor = 50;
    private double fwdSplitRandomSeedDivideFactor = 50;


    // f(x,y) = p00 + p10*x + p01*y + p20*x^2 + p11*x*y + p02*y^2 + p30*x^3 + p21*x^2*y + p12*x*y^2

//    private double p00 = -270.7;
//    private double p10 = 38.56;
//    private double p01 = 420.4;
//    private double p20 = 8.239;
//    private double p11 = 0.3059;
//    private double p02 = -11.86;
//    private double p30 = -0.902;
//    private double p21 = -9.217;
//    private double p12 = -11.22;


    private double[] gaussianKernel_10 = new double[]
            {
                0.0098, 0.0336, 0.0849, 0.1574, 0.2143,
                0.2143, 0.1574, 0.0849, 0.0336, 0.0098
            };

    private double[] gaussianKernel_30 = new double[]
            {
                0.0031, 0.0046, 0.0068, 0.0097, 0.0135,
                0.0182, 0.0237, 0.0301, 0.0371, 0.0443,
                0.0514, 0.0579, 0.0633, 0.0672, 0.0692,
                0.0692, 0.0672, 0.0633, 0.0579, 0.0514,
                0.0443, 0.0371, 0.0301, 0.0237, 0.0182,
                0.0135, 0.0097, 0.0068, 0.0046, 0.0031,
            };

    private double[] gaussianKernel_5 = new double[]
            {
                0.0219, 0.2285, 0.4991, 0.2285, 0.0219
            };


    //a0 + a1*cos(x*w) + b1*sin(x*w) + a2*cos(2*x*w) + b2*sin(2*x*w) + a3*cos(3*x*w) + b3*sin(3*x*w)

//    private ArrayList<KalmanFilter> filterList = new ArrayList<>();


//    private KalmanFilter filterL1 = new KalmanFilter();
//    private KalmanFilter filterL2 = new KalmanFilter();
//    private KalmanFilter filterL3 = new KalmanFilter();
//    private KalmanFilter filterL4 = new KalmanFilter();
//    private KalmanFilter filterR1 = new KalmanFilter();
//    private KalmanFilter filterR2 = new KalmanFilter();
//    private KalmanFilter filterR3 = new KalmanFilter();
//    private KalmanFilter filterR4 = new KalmanFilter();

    private int T = 1;
    private double[][] A0 = {{1, T}, {0, 1}};
    private double[][] G0 = {{Math.pow(T,2)/2}, {T}};
    private double[][] H0 = {{1,0}};
    private double[][] Xu0 = {{0}, {0}};
    private double[][] Pu0 = {{0, 0}, {0, 0}};
    private double[][] I0 = {{1, 0}, {0, 1}};


//    private double[][] Q0 = {{0.031622776601684}};
//    private double[][] R0 = {{3.770800439441918}};

    private double[][] Q0 = {{0.05}};
    private double[][] R0 = {{5}};

    private Matrix Q = new Matrix(Q0);
    private Matrix R1 = new Matrix(R0);

    private Matrix A = new Matrix (A0);
    private Matrix G = new Matrix (G0);
    private Matrix H = new Matrix (H0);
    private Matrix I = new Matrix(I0);

    private Matrix Xu = new Matrix (Xu0);
    private Matrix Pu = new Matrix (Pu0);
    private Matrix Xp = null;
    private Matrix Pp = null;
    private Matrix K = null;

    private ArrayList<Matrix> XuList = new ArrayList<>();
    private ArrayList<Matrix> PuList = new ArrayList<>();
    private ArrayList<Matrix> XpList = new ArrayList<>();
    private ArrayList<Matrix> PpList = new ArrayList<>();
    private ArrayList<Matrix> KList = new ArrayList<>();

    private Matrix Xu1 = new Matrix (Xu0);
    private Matrix Pu1 = new Matrix (Pu0);
    private Matrix Xp1 = null;
    private Matrix Pp1 = null;
    private Matrix K1 = null;

    private Matrix Xu2 = new Matrix (Xu0);
    private Matrix Pu2 = new Matrix (Pu0);
    private Matrix Xp2 = null;
    private Matrix Pp2 = null;
    private Matrix K2 = null;

    private Matrix Xu3 = new Matrix (Xu0);
    private Matrix Pu3 = new Matrix (Pu0);
    private Matrix Xp3 = null;
    private Matrix Pp3 = null;
    private Matrix K3 = null;

    private Matrix Xu4 = new Matrix (Xu0);
    private Matrix Pu4 = new Matrix (Pu0);
    private Matrix Xp4 = null;
    private Matrix Pp4 = null;
    private Matrix K4 = null;

    private Matrix Xu5 = new Matrix (Xu0);
    private Matrix Pu5 = new Matrix (Pu0);
    private Matrix Xp5 = null;
    private Matrix Pp5 = null;
    private Matrix K5 = null;

    private Matrix Xu6 = new Matrix (Xu0);
    private Matrix Pu6 = new Matrix (Pu0);
    private Matrix Xp6 = null;
    private Matrix Pp6 = null;
    private Matrix K6 = null;

    private Matrix Xu7 = new Matrix (Xu0);
    private Matrix Pu7 = new Matrix (Pu0);
    private Matrix Xp7 = null;
    private Matrix Pp7 = null;
    private Matrix K7 = null;

    private Matrix Xu8 = new Matrix (Xu0);
    private Matrix Pu8 = new Matrix (Pu0);
    private Matrix Xp8 = null;
    private Matrix Pp8 = null;
    private Matrix K8 = null;

    private String Lang = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Lang = getIntent().getStringExtra("lang");


        if (metrics.xdpi <= 250) {
            tabMode = 1;
            setContentView(R.layout.activity_data_playback_tab);
            chart = findViewById(R.id.chart_pb);
            mBtnStrokeVelocity = findViewById(R.id.btn_mode_change_stroke_speed);
            mBtnCatchDegree = findViewById(R.id.btn_mode_change_catch_degree);
            mBtnStrokeDepth = findViewById(R.id.btn_mode_change_blade_depth);
            mBtnCatchSpeed = findViewById(R.id.btn_mode_change_catch_speed);
            mTextPlayMode = findViewById(R.id.textPlayMode);
            mBtnStrokePower = findViewById(R.id.btn_mode_change_stroke_power);
            chartInit(chart, playBackDataIndex);

        }else{
            tabMode = 0;

            if (Objects.equals(Lang, "eng")){
                setContentView(R.layout.activity_data_playback_eng);

            }else{
                setContentView(R.layout.activity_data_playback);

            }

            chart = findViewById(R.id.chart_pb);
            mBtnStrokeVelocity = findViewById(R.id.btn_mode_change_stroke_speed);
            mBtnCatchDegree = findViewById(R.id.btn_mode_change_catch_degree);
            mBtnStrokeDepth = findViewById(R.id.btn_mode_change_blade_depth);
            mBtnCatchSpeed = findViewById(R.id.btn_mode_change_catch_speed);
            mTextPlayMode = findViewById(R.id.textPlayMode);
            mBtnStrokePower = findViewById(R.id.btn_mode_change_stroke_power);
            chartInit(chart, playBackDataIndex);
            mLineChartSwitch = findViewById(R.id.line_chart_switch_pb);
            mLineChartLayer = findViewById(R.id.line_chart_display_layer_pb);

            mLineChartLayer.setAlpha(0f);
            mLineChartLayer.setEnabled(false);
            mLineChartLayer.scrollBy(0,820);

            mLineChartSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        mLineChartLayer.setAlpha(0.97f);
                        mLineChartLayer.setEnabled(true);
                        mLineChartLayer.scrollTo(0,0);


                    } else {
                        mLineChartLayer.setAlpha(0f);
                        mLineChartLayer.setEnabled(false);
                        mLineChartLayer.scrollBy(0,820);


                    }
                }
            });

        }

        for (int i =0; i<8; i++) {

            forceCacheSizeList[i][0] = 0;
            forceCachePointerList[i][0] = 0;
            forceCacheSumList[i][0] = 0.0;

        }

        mFrame_L1 = findViewById(R.id.frame_L1_PB);
        mFrame_L2 = findViewById(R.id.frame_L2_PB);
        mFrame_L3 = findViewById(R.id.frame_L3_PB);
        mFrame_L4 = findViewById(R.id.frame_L4_PB);
        mFrame_R1 = findViewById(R.id.frame_R1_PB);
        mFrame_R2 = findViewById(R.id.frame_R2_PB);
        mFrame_R3 = findViewById(R.id.frame_R3_PB);
        mFrame_R4 = findViewById(R.id.frame_R4_PB);

        mDegreeL1fwd = findViewById(R.id.degree_L1_fwd_PB);
        mDegreeL2fwd = findViewById(R.id.degree_L2_fwd_PB);
        mDegreeL3fwd = findViewById(R.id.degree_L3_fwd_PB);
        mDegreeL4fwd = findViewById(R.id.degree_L4_fwd_PB);

        mDegreeL1bwd = findViewById(R.id.degree_L1_bwd_PB);
        mDegreeL2bwd = findViewById(R.id.degree_L2_bwd_PB);
        mDegreeL3bwd = findViewById(R.id.degree_L3_bwd_PB);
        mDegreeL4bwd = findViewById(R.id.degree_L4_bwd_PB);

        mDegreeR1fwd = findViewById(R.id.degree_R1_fwd_PB);
        mDegreeR2fwd = findViewById(R.id.degree_R2_fwd_PB);
        mDegreeR3fwd = findViewById(R.id.degree_R3_fwd_PB);
        mDegreeR4fwd = findViewById(R.id.degree_R4_fwd_PB);

        mDegreeR1bwd = findViewById(R.id.degree_R1_bwd_PB);
        mDegreeR2bwd = findViewById(R.id.degree_R2_bwd_PB);
        mDegreeR3bwd = findViewById(R.id.degree_R3_bwd_PB);
        mDegreeR4bwd = findViewById(R.id.degree_R4_bwd_PB);

        mDegreeL1 = findViewById(R.id.degree_L1_PB);
        mDegreeL2 = findViewById(R.id.degree_L2_PB);
        mDegreeL3 = findViewById(R.id.degree_L3_PB);
        mDegreeL4 = findViewById(R.id.degree_L4_PB);

        mDegreeR1= findViewById(R.id.degree_R1_PB);
        mDegreeR2= findViewById(R.id.degree_R2_PB);
        mDegreeR3= findViewById(R.id.degree_R3_PB);
        mDegreeR4= findViewById(R.id.degree_R4_PB);

        mOarL1 = findViewById(R.id.oarL1_PB);
        mOarL2 = findViewById(R.id.oarL2_PB);
        mOarL3 = findViewById(R.id.oarL3_PB);
        mOarL4 = findViewById(R.id.oarL4_PB);
        mOarR1 = findViewById(R.id.oarR1_PB);
        mOarR2 = findViewById(R.id.oarR2_PB);
        mOarR3 = findViewById(R.id.oarR3_PB);
        mOarR4 = findViewById(R.id.oarR4_PB);

        mOarL1_roll = findViewById(R.id.oarL1_roll_PB);
        mOarL2_roll = findViewById(R.id.oarL2_roll_PB);
        mOarL3_roll = findViewById(R.id.oarL3_roll_PB);
        mOarL4_roll = findViewById(R.id.oarL4_roll_PB);
        mOarR1_roll = findViewById(R.id.oarR1_roll_PB);
        mOarR2_roll = findViewById(R.id.oarR2_roll_PB);
        mOarR3_roll = findViewById(R.id.oarR3_roll_PB);
        mOarR4_roll = findViewById(R.id.oarR4_roll_PB);

        mOarL1_pitch = findViewById(R.id.oarL1_pitch_PB);
        mOarL2_pitch = findViewById(R.id.oarL2_pitch_PB);
        mOarL3_pitch = findViewById(R.id.oarL3_pitch_PB);
        mOarL4_pitch = findViewById(R.id.oarL4_pitch_PB);
        mOarR1_pitch = findViewById(R.id.oarR1_pitch_PB);
        mOarR2_pitch = findViewById(R.id.oarR2_pitch_PB);
        mOarR3_pitch = findViewById(R.id.oarR3_pitch_PB);
        mOarR4_pitch = findViewById(R.id.oarR4_pitch_PB);

        mFrame_L1_roll = findViewById(R.id.L1_roll_frame_PB);
        mFrame_L2_roll = findViewById(R.id.L2_roll_frame_PB);
        mFrame_L3_roll = findViewById(R.id.L3_roll_frame_PB);
        mFrame_L4_roll = findViewById(R.id.L4_roll_frame_PB);
        mFrame_R1_roll = findViewById(R.id.R1_roll_frame_PB);
        mFrame_R2_roll = findViewById(R.id.R2_roll_frame_PB);
        mFrame_R3_roll = findViewById(R.id.R3_roll_frame_PB);
        mFrame_R4_roll = findViewById(R.id.R4_roll_frame_PB);

        mStrokeRate = findViewById(R.id.stroke_rate_PB);
        mBoatSpeed = findViewById(R.id.gps_speed_PB);
        mBoatRoll = findViewById(R.id.boat_roll_PB);
        mBoatYaw = findViewById(R.id.boat_yaw_PB);

        mHR1 = findViewById(R.id.hr_display_1_dp);
        mHR2 = findViewById(R.id.hr_display_2_dp);
        mHR3 = findViewById(R.id.hr_display_3_dp);
        mHR4 = findViewById(R.id.hr_display_4_dp);
        mHR5 = findViewById(R.id.hr_display_5_dp);
        mHR6 = findViewById(R.id.hr_display_6_dp);
        mHR7 = findViewById(R.id.hr_display_7_dp);
        mHR8 = findViewById(R.id.hr_display_8_dp);

        mTvListHR.add(mHR1);
        mTvListHR.add(mHR2);
        mTvListHR.add(mHR3);
        mTvListHR.add(mHR4);
        mTvListHR.add(mHR5);
        mTvListHR.add(mHR6);
        mTvListHR.add(mHR7);
        mTvListHR.add(mHR8);

//        filterList.add(filterL1);
//        filterList.add(filterL2);
//        filterList.add(filterL3);
//        filterList.add(filterL4);
//        filterList.add(filterR1);
//        filterList.add(filterR2);
//        filterList.add(filterR3);
//        filterList.add(filterR4);

//        for (int i=0; i<filterList.size();i++){
//            filterList.get(i).initial();
//        }

        XuList.add(Xu1);
        XuList.add(Xu2);
        XuList.add(Xu3);
        XuList.add(Xu4);
        XuList.add(Xu5);
        XuList.add(Xu6);
        XuList.add(Xu7);
        XuList.add(Xu8);

        PuList.add(Pu1);
        PuList.add(Pu2);
        PuList.add(Pu3);
        PuList.add(Pu4);
        PuList.add(Pu5);
        PuList.add(Pu6);
        PuList.add(Pu7);
        PuList.add(Pu8);

        XpList.add(Xp1);
        XpList.add(Xp2);
        XpList.add(Xp3);
        XpList.add(Xp4);
        XpList.add(Xp5);
        XpList.add(Xp6);
        XpList.add(Xp7);
        XpList.add(Xp8);

        PpList.add(Pp1);
        PpList.add(Pp2);
        PpList.add(Pp3);
        PpList.add(Pp4);
        PpList.add(Pp5);
        PpList.add(Pp6);
        PpList.add(Pp7);
        PpList.add(Pp8);

        KList.add(K1);
        KList.add(K2);
        KList.add(K3);
        KList.add(K4);
        KList.add(K5);
        KList.add(K6);
        KList.add(K7);
        KList.add(K8);

        mSectionTime = findViewById(R.id.section_time_PB);
        mCurrentTime = findViewById(R.id.current_time_PB);

        mBoatSpeedTxUnit = findViewById(R.id.boat_speed_tx_unit_pd);
        mDistanceTx = findViewById(R.id.travelled_distance_pb);

        mBoatSensorSwitch = findViewById(R.id.boat_sensor_switch_pb);
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

        mBoatSpeedSwitch = findViewById(R.id.boat_speed_switch_pb);
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

        mMountDegreeSwitch = findViewById(R.id.mount_degree_switch_pb);
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

        mRightCompensate = findViewById(R.id.compensate_btn_pb);
        mRightCompensate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mRightCompensateSwitchStatus = 1;
                    rightCompensateRatio = rightCompensateRatioHigh;
                } else {
                    mRightCompensateSwitchStatus = 0;
                    rightCompensateRatio = rightCompensateRatioLow;

                }
            }
        });

        mBtnPlay = findViewById(R.id.btn_play_PB);
        mBtnPause = findViewById(R.id.btn_pause_PB);
        mFF1X = findViewById(R.id.ff_1x_PB);
        mFF2X = findViewById(R.id.ff_2x_PB);
        mFF5X = findViewById(R.id.ff_5x_PB);


        mProgressBar = findViewById(R.id.progress_control);
        mProgressBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        Intent intent = getIntent();
        mLogTime = intent.getStringExtra("logTime");
        mFF1X.setBackgroundResource(R.drawable.line_orange_round_filled);

        try {
            mountDataFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            e.printStackTrace();
        }

//        trimData();


        try{


        }
        catch (Exception e){

            Log.e("Trim Status", "fail");


        };

        initiate_params();
        updateUI_PB();

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        updateUI_run.run();
                        updateUI();

                    }
                });
            }
        };

        timer.schedule(timerTask,0,Math.round((1000/samplingRate)/speedMultiple));
        System.out.println("Refresh Time is : " + Math.round((1000/samplingRate)/speedMultiple));


        mBtnPlay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                System.out.println(mReplayNode);
//                System.out.println(mEndNode);
//                System.out.println(thread0);

                suspended = false;
//
                if (mReplayNode == 1 && thread0 != null){
                    thread0.interrupt();
                    playNodePos = 0;
                    updateUI_PB();
                    System.out.println("case1");
                }else if (mReplayNode == 0 && mEndNode == 0 && thread0 != null){
                    synchronized(thread0) {
                        thread0.notify();
                    }
                    System.out.println("case2");

                }else if (mReplayNode == 0 && mEndNode == 1 && thread0 != null){
                    thread0.interrupt();
                    updateUI_PB();

                    System.out.println("case3");
                }else{
                    thread0.interrupt();
                    updateUI_PB();
                }
            }
        });

        mBtnPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                suspended = true;

            }
        });

        mFF1X.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                speedMultiple = 1;
                mFF1X.setBackgroundResource(R.drawable.line_orange_round_filled);
                mFF2X.setBackgroundResource(R.drawable.line_orange_round);
                mFF5X.setBackgroundResource(R.drawable.line_orange_round);

            }
        });

        mFF2X.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                speedMultiple = 2;
                mFF1X.setBackgroundResource(R.drawable.line_orange_round);
                mFF2X.setBackgroundResource(R.drawable.line_orange_round_filled);
                mFF5X.setBackgroundResource(R.drawable.line_orange_round);

            }
        });

        mFF5X.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                speedMultiple = 5;
                mFF1X.setBackgroundResource(R.drawable.line_orange_round);
                mFF2X.setBackgroundResource(R.drawable.line_orange_round);
                mFF5X.setBackgroundResource(R.drawable.line_orange_round_filled);

            }
        });

        if (tabMode == 1 || tabMode == 0) {

            mBtnStrokeVelocity.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    playBackDataIndex = 4;

                    if (Objects.equals(Lang, "eng")){
                        mTextPlayMode.setText("Stroke Speed");

                    }else{
                        mTextPlayMode.setText("拉桨速度");

                    }


                    YAxis yTemp = chart.getAxisLeft();
//                    yTemp.setAxisMaximum(5f);
//                    yTemp.setAxisMinimum(-5f);
                    yTemp.setAxisMaximum(8f);
                    yTemp.setAxisMinimum(-8f);
                    chart.getLineData().getDataSetByIndex(8).setVisible(true);

//                    List<LimitLine> listLimitLine = chart.getAxisLeft().getLimitLines();
//                    for (int i = 0; i<listLimitLine.size(); i++){
//                        chart.getAxisLeft().getLimitLines().remove(0);
//                    }

                    chart.getAxisLeft().getLimitLines().clear();

                    LimitLine yLimitLine = new LimitLine(0f);
                    yLimitLine.setLineColor(Color.DKGRAY);
                    yLimitLine.setTextColor(Color.DKGRAY);
                    yLimitLine.setLineWidth(3f);
                    chart.getAxisLeft().addLimitLine(yLimitLine);


                }
            });

            mBtnCatchDegree.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    playBackDataIndex = 0;

                    if (Objects.equals(Lang, "eng")){
                        mTextPlayMode.setText("Feather Angle");

                    }else{
                        mTextPlayMode.setText("转桨角度");

                    }

                    YAxis yTemp = chart.getAxisLeft();
                    yTemp.setAxisMaximum(20f);
                    yTemp.setAxisMinimum(-130f);

                    chart.getLineData().getDataSetByIndex(8).setVisible(false);

//                    List<LimitLine> listLimitLine = chart.getAxisLeft().getLimitLines();
//                    for (int i = 0; i<listLimitLine.size(); i++){
//                        chart.getAxisLeft().getLimitLines().remove(0);
//                    }

                    chart.getAxisLeft().getLimitLines().clear();


                    LimitLine yLimitLine = new LimitLine(0f);
                    yLimitLine.setLineColor(Color.DKGRAY);
                    yLimitLine.setTextColor(Color.DKGRAY);
                    yLimitLine.setLineWidth(3f);
                    chart.getAxisLeft().addLimitLine(yLimitLine);

                    LimitLine waterLimitLine = new LimitLine(-100f);
                    waterLimitLine.setLineColor(Color.GRAY);
                    waterLimitLine.setTextColor(Color.GRAY);
                    waterLimitLine.setLineWidth(2f);
                    waterLimitLine.enableDashedLine(10f,10f,5f);

                    if (Objects.equals(Lang, "eng")){
                        waterLimitLine.setLabel("Pull");

                    }else{
                        waterLimitLine.setLabel("拉桨");

                    }

                    chart.getAxisLeft().addLimitLine(waterLimitLine);

                    LimitLine waterLimitLine2 = new LimitLine(-15f);
                    waterLimitLine2.setLineColor(Color.GRAY);
                    waterLimitLine2.setTextColor(Color.GRAY);
                    waterLimitLine2.setLineWidth(2f);
                    waterLimitLine2.enableDashedLine(10f,10f,5f);

                    if (Objects.equals(Lang, "eng")){
                        waterLimitLine2.setLabel("Recover");

                    }else{
                        waterLimitLine2.setLabel("回桨");

                    }

                    chart.getAxisLeft().addLimitLine(waterLimitLine2);
                    chart.invalidate();

                }
            });

            mBtnStrokeDepth.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    playBackDataIndex = 3;

                    if (Objects.equals(Lang, "eng")){
                        mTextPlayMode.setText("Oar Depth");

                    }else{
                        mTextPlayMode.setText("桨叶深度");

                    }

                    YAxis yTemp = chart.getAxisLeft();
                    yTemp.setAxisMaximum(5f);
                    yTemp.setAxisMinimum(-30f);

                    chart.getLineData().getDataSetByIndex(8).setVisible(false);

//                    List<LimitLine> listLimitLine = chart.getAxisLeft().getLimitLines();
//                    for (int i = 0; i<listLimitLine.size(); i++){
//                        chart.getAxisLeft().getLimitLines().remove(0);
//                    }

                    chart.getAxisLeft().getLimitLines().clear();

                    LimitLine yLimitLine = new LimitLine(0f);
                    yLimitLine.setLineColor(Color.DKGRAY);
                    yLimitLine.setTextColor(Color.DKGRAY);
                    yLimitLine.setLineWidth(3f);
                    chart.getAxisLeft().addLimitLine(yLimitLine);

                    LimitLine waterLimitLine = new LimitLine(-8f);
                    waterLimitLine.setLineColor(Color.GRAY);
                    waterLimitLine.setTextColor(Color.GRAY);
                    waterLimitLine.setLineWidth(3f);
                    waterLimitLine.enableDashedLine(10f,10f,5f);

                    if (Objects.equals(Lang, "eng")){
                        waterLimitLine.setLabel("Waterline");

                    }else{
                        waterLimitLine.setLabel("参考水线");

                    }

                    chart.getAxisLeft().addLimitLine(waterLimitLine);
                    chart.invalidate();

                }
            });

            mBtnCatchSpeed.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    playBackDataIndex = 8;

                    if (Objects.equals(Lang, "eng")){
                        mTextPlayMode.setText("Feather Speed");

                    }else{
                        mTextPlayMode.setText("转桨速度");

                    }

                    YAxis yTemp = chart.getAxisLeft();
                    yTemp.setAxisMaximum(1000f);
                    yTemp.setAxisMinimum(-1000f);
                    chart.getLineData().getDataSetByIndex(8).setVisible(false);

//                    List<LimitLine> listLimitLine = chart.getAxisLeft().getLimitLines();
//                    for (int i = 0; i<listLimitLine.size(); i++){
//                        chart.getAxisLeft().getLimitLines().remove(0);
//                    }
                    chart.getAxisLeft().getLimitLines().clear();


                    LimitLine yLimitLine = new LimitLine(0f);
                    yLimitLine.setLineColor(Color.DKGRAY);
                    yLimitLine.setTextColor(Color.DKGRAY);
                    yLimitLine.setLineWidth(3f);
                    chart.getAxisLeft().addLimitLine(yLimitLine);
                    chart.invalidate();


                }
            });

            mBtnStrokePower.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    playBackDataIndex = 20;

                    if (Objects.equals(Lang, "eng")){
                        mTextPlayMode.setText("Force");

                    }else{
                        mTextPlayMode.setText("拉桨力度");

                    }


                    YAxis yTemp = chart.getAxisLeft();
                    yTemp.setAxisMaximum(2000f);
                    yTemp.setAxisMinimum(-5f);
                    chart.getLineData().getDataSetByIndex(8).setVisible(false);

//                    List<LimitLine> listLimitLine = chart.getAxisLeft().getLimitLines();
//                    for (int i = 0; i<listLimitLine.size(); i++){
//                        chart.getAxisLeft().getLimitLines().remove(0);
//                    }

                    chart.getAxisLeft().getLimitLines().clear();

                    LimitLine yLimitLine = new LimitLine(0f);
                    yLimitLine.setLineColor(Color.DKGRAY);
                    yLimitLine.setTextColor(Color.DKGRAY);
                    yLimitLine.setLineWidth(3f);
                    chart.getAxisLeft().addLimitLine(yLimitLine);
                    chart.invalidate();

                }
            });

        }


    }



    private final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            playNodePosCache = itrMax * progress / 100;
            if (progress >= 99){
                mReplayNode = 1;
                mEndNode = 1;



//                UI_params_L2[11] = System.currentTimeMillis()-1000;
//                UI_params_L2[12] = System.currentTimeMillis()-2000;
//
//                UI_params_L3[11] = System.currentTimeMillis()-1000;
//                UI_params_L3[12] = System.currentTimeMillis()-2000;
//
//                UI_params_L4[11] = System.currentTimeMillis()-1000;
//                UI_params_L4[12] = System.currentTimeMillis()-2000;

                suspended = true;

                acclCacheSize = 0;
                acclCachePointer = 0;
                acclCacheSum = 0.0;
                acclCacheSamples = new double[acclCacheLength];

//                chart.clear();
//                chartInit(chart);
//                chart.moveViewToX(progress);

            }else{
                mReplayNode = 0;

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            suspended = true;

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            suspended = false;
            playNodePos = playNodePosCache;

//            System.out.println("replaynode:" + mReplayNode);
//            System.out.println("playNode:" + playNodePos);

//            UI_params_L1[11] = System.currentTimeMillis();
//            UI_params_L1[12] = System.currentTimeMillis();

            if (tabMode == 1 || tabMode == 0) {
                chart.clear();
                chartInit(chart, playBackDataIndex);
                chart.invalidate();
            }

            synchronized(thread0) {
                thread0.notify();
            }

//            updateUI_PB();

        }
    };

    private void initiate_params() {

        sizeArray = new int[] {sizeBO,sizeL1,sizeL2,sizeL3,sizeL4,sizeR1,sizeR2,sizeR3,sizeR4,sizePH};
//        System.out.println("BO:" + sizeBO);
//        System.out.println("L1:" + sizeL1);
//        System.out.println("L2:" + sizeL2);
//        System.out.println("L3:" + sizeL3);
//        System.out.println("L4:" + sizeL4);
//        System.out.println("R1:" + sizeR1);
//        System.out.println("R2:" +sizeR2);
//        System.out.println("R3:" +sizeR3);
//        System.out.println("R4:" +sizeR4);
//        System.out.println("PH:" +sizePH);
        Arrays.sort(sizeArray);
        itrMax = 0;
        int i = 0;

        while (itrMax <= 10) {
            itrMax = sizeArray[i];
            i++;
        }

        //temporary solution


        mSectionTime.setText(updateClock(itrMax, samplingRate));
        Log.e("itrMax:" , String.valueOf(itrMax));

    }

    private void updateUIParams (int i, double[] UI_params, List<String[]> log, String side, String LogName, int pos){

//        logBO = null; // for testing only

        if (log!=null) {

            double instantBoatSpeed;

            if (mBoatSensorSwitchStatus==1 && logBO != null ) {
                boatAngle = Double.parseDouble(logBO.get(i)[4]);

            }else{
                boatAngle = 0;
            }

            double yawRaw;
            double yawRawCache;
            double rollRawCache;
            double rollRaw = 0;
            double pitchRaw = 0;
            double angularVelocity = 0;
            double dynamicData = 0;
            double velocityLast = UI_params[15];


            try{
                yawRaw = Double.parseDouble(log.get(i)[4]);
                rollRaw = Double.parseDouble(log.get(i)[2]);
                pitchRaw = Double.parseDouble(log.get(i)[3]);
//                angularVelocity = Double.parseDouble(log.get(i)[8]);

            }catch (Exception e){
                yawRaw = 0;
                rollRaw = 0;
                pitchRaw = 0;
                angularVelocity = 0;
            }

            if (i>1) {

                try{
                    yawRawCache = Double.parseDouble(log.get(i-1)[4]);
                    rollRawCache =  Double.parseDouble(log.get(i-1)[2]);

                }catch (Exception e){
                    yawRawCache = 0;
                    rollRawCache = 0;
                }

            }else{
                yawRawCache = yawRaw;
                rollRawCache = rollRaw;
            }


            try{
                dynamicData = Double.parseDouble(log.get(i)[playBackDataIndex]);
            }catch (Exception e){
                dynamicData = 0;

            }


            if (side.equals("left")) {
                double roll = -rollRaw + correctionPeddle;
                double pitch = -pitchRaw;
                double yaw = -yawRaw + correctionLeft + boatAngle + correctionLeftSecondary;
                double txYaw = 180 - yawRaw + boatAngle + correctionLeftSecondary;

                double velocityZeroMarker = 0;
                double strokeTimeNow = 0;

                double strokeAV;
                double[] strokeAVK = {0,0};

                double localAv_0;
                double localAv_1;
                double dynamicAV;

                double featherAngle = 0; //0
                double featherSpeed = 0; //0
                double featherAngleLast = 0;


                try{
                    dynamicAV = Double.parseDouble(log.get(i)[4]);
                }catch (Exception e){
                    dynamicAV = 0;
                }

                localAv_0 = -(dynamicAV - yawRawCache);

                if (localAv_0 > 300){
                    localAv_1 = localAv_0 - 360;
                }else if (localAv_0 < -300){
                    localAv_1 = localAv_0 + 360;
                }else{
                    localAv_1 =  -(dynamicAV - yawRawCache);
                }

                if(localAv_1 > 0){
                    CacheStrokeAV[pos] = CacheStrokeAV[pos] + localAv_1;
                    CacheLengthStrokeAV[pos] = CacheLengthStrokeAV[pos] + 1;
                }


                double velocityNow = samplingRate*localAv_1;
//                double velocityNow = Double.parseDouble(log.get(i)[10]);
                double velocityZeroTest = velocityLast * velocityNow;

                if (velocityZeroTest<0) {
                    velocityZeroMarker=1;
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


                if (roll >= 180) {
                    featherAngle = roll - 360;
                }else{
                    featherAngle = roll;
                }

                featherAngleLast = -rollRawCache + correctionPeddle;

                if (featherAngleLast >= 180){

                    featherAngleLast = featherAngleLast - 360;

                }else {

                    featherAngleLast = featherAngleLast;

                }

                if (playBackDataIndex == 0) {
                    if (roll >= 180) {
                        UI_params[14] = roll - 360;
                    }else{
                        UI_params[14] = roll;
                    }
                }else if (playBackDataIndex == 4) {

                    double testNumberLeft = -(dynamicData - yawRawCache);

//                    System.out.println(testNumberLeft + "    " + localAv_0);

                    if (testNumberLeft>300){

                        strokeAV = testNumberLeft - 360;

                    }else if (testNumberLeft<-300){

                        strokeAV = testNumberLeft + 360;

                    }else{

                        strokeAV =  -(dynamicData - yawRawCache);

                    }

                    UI_params[14] = strokeAV;

//                    System.out.println(strokeAV + "    " + localAv_1);



                } else if (playBackDataIndex == 3) {
                    UI_params[14] = -dynamicData;

                }else if(playBackDataIndex == 20) {

                    double testNumberLeft;

                    try{
                        testNumberLeft = -(Double.parseDouble(log.get(i)[4]) - yawRawCache);
                    }catch (Exception e){
                        testNumberLeft = 0;
                    }

                    //add try

                    double currentForceData;
                    double currentSpeedData;
                    double currentDepthData;
                    double filteredForceData;

                    if (testNumberLeft>300){
                        currentSpeedData = testNumberLeft - 360;
                    }else if (testNumberLeft<-300){
                        currentSpeedData = testNumberLeft +360;
                    }else{
                        currentSpeedData = testNumberLeft;
                    }

                    currentSpeedData = (currentSpeedData*speedSuppressionRatio-speedNormMin)/(speedNormMax - speedNormMin);
                    currentDepthData = (pitch*depthSuppressionRatio-depthNormMin)/(depthNormMax - depthNormMin);

                    if(currentSpeedData<0){
                        currentForceData = 0;
                    }else{

                        //f(x) =  a0 + a1*cos(x*w) + b1*sin(x*w) +
                        //               a2*cos(2*x*w) + b2*sin(2*x*w) + a3*cos(3*x*w) + b3*sin(3*x*w)
//
//                        currentForceData = a0 + a1*Math.cos(currentSpeedData*w) + b1*Math.sin(currentSpeedData*w)
//                                                + a2*Math.cos(2*currentSpeedData*w) + b2*Math.sin(2*currentSpeedData*w)
//                                                + a3*Math.cos(3*currentSpeedData*w) + b3*Math.sin(3*currentSpeedData*w);
//
//                        currentForceData = a0 + a1*Math.cos(currentSpeedData*w) + b1*Math.sin(currentSpeedData*w)
//                                + a2*Math.cos(2*currentSpeedData*w) + b2*Math.sin(2*currentSpeedData*w);

                        // f(x) = a1*exp(-((x-b1)/c1)^2) + a2*exp(-((x-b2)/c2)^2) +
                        //              a3*exp(-((x-b3)/c3)^2)

//                        currentForceData = a1*Math.exp(-Math.pow((currentSpeedData - b1)/c1,2))
//                                            + a2*Math.exp(-Math.pow((currentSpeedData - b2)/c2,2))
//                                            + a3*Math.exp(-Math.pow((currentSpeedData - b3)/c3,2));


                        // f(x,y) = p00 + p10*x + p01*y + p20*x^2 + p11*x*y + p02*y^2

                        currentForceData = (p00
                                + p10*currentSpeedData + p01*currentDepthData
                                + p20*Math.pow(currentSpeedData,2)
                                + p11*currentSpeedData*currentDepthData
                                + p02*Math.pow(currentDepthData,2));
                        currentForceData = currentForceData*forceAmplifier*(powerNormMax - powerNormMin) + powerNormMin;

                    }

                    filteredForceData = myKalmanFilter(currentForceData, pos);
//                    filteredForceData = currentForceData;


                    if (forceCacheSizeList[pos][0] < forceCacheLength) {

                        forceCacheSamplesList[pos][forceCachePointerList[pos][0]++] = filteredForceData;
                        forceCacheSizeList[pos][0] ++;

                    }else{

                        forceCachePointerList[pos][0] = forceCachePointerList[pos][0] % forceCacheLength;
                        forceCacheSumList[pos][0] -= forceCacheSamplesList[pos][forceCachePointerList[pos][0]];
                        forceCacheSamplesList[pos][forceCachePointerList[pos][0]++] = filteredForceData;
                    }


                    double forceDataAvg = doubleArrAverage(forceCacheSamplesList[pos]);
//                    double forceDataAvg = myKalmanFilter(doubleArrAverage(forceCacheSamplesList[pos])*forceAmplifier,pos);


                    if (forceDataAvg<0){
                        UI_params[14] = 0;
                    }else{
                        UI_params[14] = forceDataAvg;
                    }


                    // place holder
                }else{

                    featherSpeed = -(featherAngle - featherAngleLast) * samplingRate;

                    if (featherSpeed > 1000 || featherSpeed <= -1000) {

                        featherSpeed =0;

                    }

                    UI_params[14] = featherSpeed;

                }

                // {yaw[0], txYaw[1], avMin[2], avMax[3], txDegreeFwd[4], txDegreeBwd[5],
                // Roll[6], Pitch[7], YawMin[8], YawMax[9], YawAbs[10],
                // YawBeg[11], YawEnd[12], YawDuration[13], DynamicData[14], StrokeVelocityLast[15]}
                // StrokeTimeLast[16]}
                //Dynamic Data: strokeSpeed[10], catchDegree[2], strokeDepth[3]

                // f(x) = p1*x^3 + p2*x^2 + p3*x + p4


                // ---------------------------- to be evaluated -----------------------------------


                if (velocityZeroMarker == 1 &&
                        velocityNow < 0 &&
                        strokeTimeNow - UI_params[12] >= 1200 &&
                        strokeTimeNow - UI_params[16] > 300/speedMultiple){


                    //old fwd degree calculation


                    UI_params[12] = System.currentTimeMillis();
                    UI_params[13] = (UI_params[12] - UI_params[11])/1000;
                    UI_params[16] = System.currentTimeMillis();

                    double avgStrokeAV = CacheStrokeAV[pos]/CacheLengthStrokeAV[pos];
                    double wattageCurrentStroke = wattage_p1_4 * Math.pow(avgStrokeAV*wattageSuppressionRatio,4)
                                                + wattage_p2_4 * Math.pow(avgStrokeAV*wattageSuppressionRatio,3)
                                                + wattage_p3_4 * Math.pow(avgStrokeAV*wattageSuppressionRatio,2)
                                                + wattage_p4_4 * avgStrokeAV*wattageSuppressionRatio
                                                + wattage_p5_4;

//                    double wattageCurrentStroke = wattage_p1_1*avgStrokeAV*speedSuppressionRatio
//                            + wattage_p2_1;

                    CacheStrokeAV[pos] = 0;
                    CacheLengthStrokeAV[pos] = 0;


//                    System.out.println("AV: " + avgStrokeAV*wattageSuppressionRatio + "  current wattage " + String.valueOf(pos) + ":" + wattageCurrentStroke);
                    Log.e("position:" + pos, "AV: " + avgStrokeAV * wattageSuppressionRatio + "current wattage: " + wattageCurrentStroke);

                    if (mBoatSensorSwitchStatus==1 && logBO != null) {

                        //old fwd degree calculation
                        if (txYaw-90>=0) {
                            UI_params[4] = txYaw-90;
                        }
                        //old fwd degree calculation

                        UI_params[8] = UI_params[4];
                        double UI_10_temp = Math.abs(UI_params[8] + UI_params[9]);
                        if (UI_10_temp>= 180){
                            UI_params[10] = 360 - UI_10_temp;
                        }else{
                            UI_params[10]= UI_10_temp;
                        }

                    } else {
                        UI_params[8] = yawRaw;
                        double UI_10_temp = Math.abs(UI_params[9] - UI_params[8]);

                        if (UI_10_temp >= 180){
                            UI_params[10] = 360 - UI_10_temp;
//                            UI_params[10] = UI_10_temp - 360;

                        }else{
                            UI_params[10]= UI_10_temp;
                        }

                        //new fwd degree calculation
                        double randomSeed = (avgStrokeAV*wattageSuppressionRatio-fwdSplitThreshSpeed)/fwdSplitDivideFactor + Math.random()/fwdSplitRandomSeedDivideFactor;
                        double simulatedFwdDegree = UI_params[10]*(fwdSplitRatio+randomSeed);
                        UI_params[4] = simulatedFwdDegree;
                        UI_params[5] = UI_params[10] - UI_params[4];
                        //new fwd degree calculation
                    }


                }else if (velocityZeroMarker == 1 &&
                        velocityNow > 0 &&
                        strokeTimeNow - UI_params[11] >= 1200 &&
                        strokeTimeNow - UI_params[16] > 300/speedMultiple) {


                    //old fwd degree calculation

                    UI_params[11] = System.currentTimeMillis();
                    UI_params[16] = System.currentTimeMillis();

//                UI_params[13] = UI_params[12] - UI_params[11];
                    if (mBoatSensorSwitchStatus==1 && logBO != null) {
                        //old fwd degree calculation
                        if (90-txYaw>=0) {
                            UI_params[5] = 90-txYaw;
                        }
                        //old fwd degree calculation
                        UI_params[9] = UI_params[5];
                    } else {
                        UI_params[9] = yawRaw;
                    }
                }


            } else if (side.equals("right")) {
                //{yaw[0], txYaw[1], avMin[2], avMax[3], txDegreeFwd[4], txDegreeBwd[5], Roll[6], Pitch[7], YawMin[8], YawMax[9], YawAbs[10], YawBeg[11], YawEnd[12], YawDuration[13]}
                double roll = -rollRaw - correctionPeddle;
                double pitch = pitchRaw + correctionRightPitch;
                double yaw = -yawRaw + correctionRight + boatAngle + correctionRightSecondary;
                double txYaw = 180 + yawRaw - boatAngle - correctionRightSecondary;

                double velocityZeroMarker = 0;
                double strokeTimeNow = 0;

                double strokeAV=0;
                double filteredAV=0;
                double[] strokeAVK = {0,0};

                double localAv_0;
                double localAv_1;
                double dynamicAV;

                double featherAngle = 0; //0
                double featherSpeed = 0; //0
                double featherAngleLast = 0;


                try{
                    dynamicAV = Double.parseDouble(log.get(i)[4]);
                }catch (Exception e){
                    dynamicAV = 0;
                    Log.e("Error Dynamic Data", String.valueOf(dynamicData));
                }

                localAv_0 = (dynamicAV - yawRawCache);

//                if (localAv_0 == 0){
//
//                    localAv_0 = CacheStrokeAV[pos];
//
//                }else{
//
//                    CacheStrokeAV[pos] = localAv_0;
//
//                }


                if (localAv_0 > 300){
                    localAv_1 = localAv_0 - 360;
                }else if (localAv_0 < -300){
                    localAv_1 = localAv_0 + 360;
                }else{
                    localAv_1 =  (dynamicAV - yawRawCache);
//                    localAv_1 =  localAv_0;
                }

                if(localAv_1 > 0){
                    CacheStrokeAV[pos] = CacheStrokeAV[pos] + localAv_1;
                    CacheLengthStrokeAV[pos] = CacheLengthStrokeAV[pos] + 1;
                }

//                System.out.println("LocalAV_0:" + localAv_0);
//                System.out.println("LocalAV_1:" + localAv_1);

//                double velocityNow = samplingRate*(localAv_1);
                double velocityNow = samplingRate*localAv_1;

//                double velocityNow = -Double.parseDouble(log.get(i)[10]);
                double velocityZeroTest = velocityLast * velocityNow;

                if (velocityZeroTest<0) {
                    velocityZeroMarker=1;
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

                if (-roll >= 180) {
                    featherAngle = -roll - 360;
                }else{
                    featherAngle = -roll;
                }

                featherAngleLast = -rollRawCache - correctionPeddle;

                if (-featherAngleLast >= 180){

                    featherAngleLast = -featherAngleLast - 360;

                }else{

                    featherAngleLast = -featherAngleLast;

                }


                if (playBackDataIndex == 0) {
                    if (-roll >= 180) {
                        UI_params[14] = -roll - 360;
                    }else{
                        UI_params[14] = -roll;
                    }
                }else if (playBackDataIndex == 4) {

                    double testNumberRight = (dynamicData - yawRawCache);
//                    double testNumberRight = localAv_0;


                    if (testNumberRight>300){

                        strokeAV = testNumberRight - 360;

                    }else if (testNumberRight<-300){

                        strokeAV = testNumberRight + 360;

                    }else{
                        strokeAV =  (dynamicData - yawRawCache);
                    }

//                    strokeAVK = filterList.get(pos).update(new double[]{playNodePos,strokeAV});
//                    UI_params[14] = strokeAVK[1];

//                    Log.e("Stroke AV", String.valueOf(strokeAV));

                    UI_params[14] = strokeAV;

                } else if (playBackDataIndex == 3) {
                    UI_params[14] = -dynamicData;

                }else if(playBackDataIndex == 20) {

                    //a0 + a1*cos(x*w) + b1*sin(x*w) + a2*cos(2*x*w) + b2*sin(2*x*w) + a3*cos(3*x*w) + b3*sin(3*x*w)

                    double testNumberRight;

                    try{
                        testNumberRight = (Double.parseDouble(log.get(i)[4]) - yawRawCache);
                    }catch (Exception e){
                        testNumberRight = 0;
                    }

                    double currentForceData;
                    double currentSpeedData;
                    double currentDepthData;
                    double filteredForceData;
                    double rightSideCompensate = rightCompensateRatio;

                    if (testNumberRight>300){
                        currentSpeedData = testNumberRight - 360;
                    }else if (testNumberRight<-300){
                        currentSpeedData = testNumberRight + 360;
                    }else{
                        currentSpeedData =  testNumberRight ;
                    }

//                    filteredSpeedData = myKalmanFilter(currentSpeedData, pos);

                    currentSpeedData = (currentSpeedData*speedSuppressionRatio-speedNormMin)/(speedNormMax - speedNormMin);
                    currentDepthData = (-pitch*depthSuppressionRatio*rightSideCompensate-depthNormMin)/(depthNormMax - depthNormMin);


                    if(currentSpeedData<0){

                        currentForceData = 0;
                    }else{

                        //f(x) =  a0 + a1*cos(x*w) + b1*sin(x*w) +
                        //               a2*cos(2*x*w) + b2*sin(2*x*w) + a3*cos(3*x*w) + b3*sin(3*x*w)
//
//                        currentForceData = a0 + a1*Math.cos(currentSpeedData*w) + b1*Math.sin(currentSpeedData*w)
//                                                + a2*Math.cos(2*currentSpeedData*w) + b2*Math.sin(2*currentSpeedData*w)
//                                                + a3*Math.cos(3*currentSpeedData*w) + b3*Math.sin(3*currentSpeedData*w);
//
//                        currentForceData = a0 + a1*Math.cos(currentSpeedData*w) + b1*Math.sin(currentSpeedData*w)
//                                + a2*Math.cos(2*currentSpeedData*w) + b2*Math.sin(2*currentSpeedData*w);

                        // f(x) = a1*exp(-((x-b1)/c1)^2) + a2*exp(-((x-b2)/c2)^2) +
                        //              a3*exp(-((x-b3)/c3)^2)

//                        currentForceData = a1*Math.exp(-Math.pow((currentSpeedData - b1)/c1,2))
//                                            + a2*Math.exp(-Math.pow((currentSpeedData - b2)/c2,2))
//                                            + a3*Math.exp(-Math.pow((currentSpeedData - b3)/c3,2));


                        // f(x,y) = p00 + p10*x + p01*y + p20*x^2 + p11*x*y + p02*y^2

                        currentForceData = (p00
                                + p10*currentSpeedData + p01*currentDepthData
                                + p20*Math.pow(currentSpeedData,2)
                                + p11*currentSpeedData*currentDepthData
                                + p02*Math.pow(currentDepthData,2));
                        currentForceData = currentForceData*forceAmplifier*(powerNormMax - powerNormMin) + powerNormMin;

//                        currentForceData = (p00
//                                + p10*strokeAVK[1]*speedSuppressionRatio + p01*pitch*depthSupreesionRation
//                                + p20*Math.pow(strokeAVK[1]*speedSuppressionRatio,2)
//                                + p11*strokeAVK[1]*speedSuppressionRatio*pitch*depthSupreesionRation
//                                + p02*Math.pow(pitch*depthSupreesionRation,2));

                        // f(x,y) = p00 + p10*x + p01*y
                        // + p20*x^2 + p11*x*y + p02*y^2
                        // + p30*x^3 + p21*x^2*y + p12*x*y^2

//                        currentForceData = (p00
//                                + p10*currentSpeedData + p01*pitch
//                                + p20*Math.pow(currentSpeedData,2) + p11*currentSpeedData*pitch + p02*Math.pow(pitch,2)
//                                + p30*Math.pow(currentSpeedData,3) + p21*Math.pow(currentSpeedData,2)*pitch + p12*currentSpeedData*Math.pow(pitch,2));


                    }


//                           currentForceData = currentSpeedData*speedSuppressionRatio * pitch*forceSuppressionRatio;

                    filteredForceData = myKalmanFilter(currentForceData, pos);
                    double localMax = 0;

                    if (forceCacheSizeList[pos][0] < forceCacheLength) {

                        forceCacheSamplesList[pos][forceCachePointerList[pos][0]++] = filteredForceData;
                        forceCacheSizeList[pos][0] ++;

                    }else{

                        forceCachePointerList[pos][0] = forceCachePointerList[pos][0] % forceCacheLength;
                        forceCacheSumList[pos][0] -= forceCacheSamplesList[pos][forceCachePointerList[pos][0]];
                        forceCacheSamplesList[pos][forceCachePointerList[pos][0]++] = filteredForceData;

                    }

//                    localMax = Arrays.stream(forceCacheSamplesList[pos]).max().getAsDouble();

                    double forceDataAvg = doubleArrAverage(forceCacheSamplesList[pos]);
//                    double forceDataAvg = gaussianArrAverage(forceCacheSamplesList[pos], gaussianKernel_5);

                    if (forceDataAvg<0){
                        UI_params[14] = 0;
                    }else {
                        UI_params[14] = forceDataAvg;
                    }


//                    UI_params[14] = forceDataAvg;


                    // place holder
                }else{

                    featherSpeed = (featherAngle - featherAngleLast) * samplingRate;

                    if (featherSpeed > 1000 || featherSpeed <= -1000) {

                        featherSpeed =0;

                    }

                    UI_params[14] = -featherSpeed;


//                    UI_params[14] = -dynamicData;
                }

                // ---------------------------- to be evaluated -----------------------------------

                if (velocityZeroMarker == 1 &&
                        velocityNow < 0 &&
                        strokeTimeNow - UI_params[12] >= 1200 &&
                        strokeTimeNow - UI_params[16] > 300/speedMultiple){



                    UI_params[12] = System.currentTimeMillis();
                    UI_params[13] = (UI_params[12] - UI_params[11])/1000;
                    UI_params[16] = System.currentTimeMillis();

                    double avgStrokeAV = CacheStrokeAV[pos]/CacheLengthStrokeAV[pos];
                    double wattageCurrentStroke = wattage_p1_4 * Math.pow(avgStrokeAV*wattageSuppressionRatio,4)
                            + wattage_p2_4 * Math.pow(avgStrokeAV*wattageSuppressionRatio,3)
                            + wattage_p3_4 * Math.pow(avgStrokeAV*wattageSuppressionRatio,2)
                            + wattage_p4_4 * avgStrokeAV*wattageSuppressionRatio
                            + wattage_p5_4;

//                    double wattageCurrentStroke = wattage_p1_1*avgStrokeAV*speedSuppressionRatio
//                            + wattage_p2_1;

                    CacheStrokeAV[pos] = 0;
                    CacheLengthStrokeAV[pos] = 0;

//                    Log.e("position:" + pos, "AV: " + avgStrokeAV * wattageSuppressionRatio + "current wattage: " + wattageCurrentStroke);


                    if (mBoatSensorSwitchStatus==1 && logBO != null) {

                        if (txYaw-90>=0) {
                            UI_params[4] = txYaw-90;
                        }

                        UI_params[8] = UI_params[4];
                        double UI_10_temp = Math.abs(UI_params[8] + UI_params[9]);
                        if (UI_10_temp>= 180){
                            UI_params[10] = 360 - UI_10_temp;
                        }else{
                            UI_params[10]= UI_10_temp;
                        }

                    } else {

                        UI_params[8] = yawRaw;
                        double UI_10_temp = Math.abs(UI_params[9] - UI_params[8]);
                        if (UI_10_temp>= 180){
                            UI_params[10] = 360 - UI_10_temp;
                        }else{
                            UI_params[10]= UI_10_temp;
                        }

                        double randomSeed = (avgStrokeAV*wattageSuppressionRatio-fwdSplitThreshSpeed)/fwdSplitDivideFactor + Math.random()/fwdSplitRandomSeedDivideFactor;
                        double simulatedFwdDegree = UI_params[10]*(fwdSplitRatio+randomSeed);
                        UI_params[4] = simulatedFwdDegree;
                        UI_params[5] = UI_params[10] - UI_params[4];

//                        Log.e("randomSeed", pos + ":" + randomSeed);

                    }

                }else if (velocityZeroMarker == 1 &&
                        velocityNow > 0 &&
                        strokeTimeNow - UI_params[11] >= 1200 &&
                        strokeTimeNow - UI_params[16] > 300/speedMultiple) {


                    UI_params[11] = System.currentTimeMillis();
                    UI_params[16] = System.currentTimeMillis();

                    if (mBoatSensorSwitchStatus==1 && logBO != null) {
                        if (90-txYaw>=0) {
                            UI_params[5] = 90-txYaw;
                        }

                        UI_params[9] = UI_params[5];

                    } else {
                        UI_params[9] = yawRaw;
                    }
                }

            }
        }
    }

    private void updateUI_sub(final double[] UI_params,
                              final TextView degreeFwd, final TextView degreeBwd, final TextView degreeOverall,
                              final ImageView yaw, final ImageView roll, final ImageView pitch, final FrameLayout rollFrame,
                              String side){

        double catchFinishRangeLow;
        double catchFinishRangeHigh;

        if ( mMountDegreeSwitchStatus == 0){
            catchFinishRangeLow = 245;
            catchFinishRangeHigh = 285;

        }else {
            catchFinishRangeLow = 70;
            catchFinishRangeHigh = 110;
        }


        if (logBO != null && mBoatSensorSwitchStatus == 1 && UI_params[0]*UI_params[1] != 0 ) {
            yaw.setAlpha(0.7f);
            degreeFwd.setText(String.format("%.0f", UI_params[4]));
            degreeBwd.setText(String.format("%.0f", UI_params[5]));
            yaw.setRotation((float)UI_params[0]);

        }else{

//            degreeFwd.setText(String.format("%.0f", 0f));
//            degreeBwd.setText(String.format("%.0f", 0f));
            degreeFwd.setText(String.format("%.0f", UI_params[4]));
            degreeBwd.setText(String.format("%.0f", UI_params[5]));
            yaw.setAlpha(0f);
        }

        double boatSpeedTest = currentBoatSpeedCache;


        if (boatSpeedTest >= 0 && boatSpeedTest <=1.5){

            if (UI_params[10] > 0 && UI_params[10] < 10){
                degreeOverall.setText(String.format("%.0f", UI_params[10]));

            } else if (UI_params[10] >= 10 && UI_params[10] < 30) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));

            } else if (UI_params[10] >= 30 && UI_params[10] < 60) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));

            } else if (UI_params[10] >= 60 && UI_params[10] < 130) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));
            }

        }else if (boatSpeedTest > 1.5 && boatSpeedTest <= 2.5){

            if (UI_params[10] > 0 && UI_params[10] < 10){

            } else if (UI_params[10] >= 10 && UI_params[10] < 30) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));

            } else if (UI_params[10] >= 30 && UI_params[10] < 60) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));

            } else if (UI_params[10] >= 60 && UI_params[10] < 130) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));
            }

        }else if (boatSpeedTest > 2.5 ){

            if (UI_params[10] > 0 && UI_params[10] < 10){

            } else if (UI_params[10] >= 10 && UI_params[10] < 30) {

            } else if (UI_params[10] >= 30 && UI_params[10] < 60) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));

            } else if (UI_params[10] >= 60 && UI_params[10] < 130) {
                degreeOverall.setText(String.format("%.0f", UI_params[10]));
            }

        }
//
//        if (boatSpeedTest > 1.5 ){
//
//            if (UI_params[10] > 70 && UI_params[10] < 120){
//                degreeOverall.setText(String.format("%.0f", UI_params[10]));
//            }
//
//        }else{
//            degreeOverall.setText(String.format("%.0f", UI_params[10]));
//        }

//        degreeOverall.setText(String.format("%.2f", UI_params[13]/1000));

        roll.setRotation((float)UI_params[6]);
        pitch.setRotation((float)UI_params[7]);

        if (UI_params[6] > catchFinishRangeLow && UI_params[6] < catchFinishRangeHigh && side.equals("left")){
            rollFrame.setBackgroundResource(R.drawable.gradient_line_green_round);

        }else if(UI_params[6] < -catchFinishRangeLow && UI_params[6] >-catchFinishRangeHigh && side.equals("right")){
            rollFrame.setBackgroundResource(R.drawable.gradient_line_red_round);

        }else{
            rollFrame.setBackgroundResource(R.drawable.gradient_line_round);
        }
    }

    private void updateUIParams_secondary(int i, float[] UI_params_float, String[] UI_params_str, List<String[]> log) {
        //private String[] UI_params_Secondary_str = new String[] {"0.0","0.0"}; //spm, boat speed
        //private float[] UI_params_Secondary_float = new float[] {0f,0f}; //boat_yaw, boat_roll

//        System.out.println(i);
        UI_params_str[0] = log.get(i)[1];

        try{
            UI_params_str[2] = log.get(i)[13];
        }catch (ArrayIndexOutOfBoundsException e){
            UI_params_str[2]="0";
        }

        try{
            correctionLeftSecondary =  Double.parseDouble(log.get(i)[14]);
            correctionRightSecondary =  Double.parseDouble(log.get(i)[15]);
        }catch (ArrayIndexOutOfBoundsException e){
            correctionLeftSecondary = 0;
            correctionRightSecondary = 0;
        }

        try{
            for (int k = 0; k<8; k++){
                HR_params_cache[k] = Integer.valueOf(log.get(i)[k+5]);
            }
        }catch (Exception e){
            for (int k = 0; k<8; k++){
                HR_params_cache[k] = 0;
            }

        }


        double boatSpeedCache = Double.parseDouble(log.get(i)[2]);
        currentBoatSpeedCache = boatSpeedCache;

//        if (acclCacheSize < acclCacheLength) {
//
//            acclCacheSamples[acclCachePointer++] = boatSpeedCache;
//            acclCacheSize ++;
//
//        }else{
//
//            acclCachePointer = acclCachePointer % acclCacheLength;
//            acclCacheSum -= acclCacheSamples[acclCachePointer];
//            acclCacheSamples[acclCachePointer++] = boatSpeedCache;
//        }
//
//        double boatSpeedCacheAvg = doubleArrAverage(acclCacheSamples);

        String boatSpeedTxAvg = String.format("%.1f", boatSpeedCache);

        if (mBoatSpeedSwitchStatus == 0){
//            UI_params_str[1] = log.get(i)[2];
            UI_params_str[1] = boatSpeedTxAvg;
        }else{
            if (boatSpeedCache <= 0.7){
                UI_params_str[1] = "9:59";
            }else{

                int splitTime = (int) (500/boatSpeedCache);
                int splitTimeSec = splitTime%60;
                splitTime = splitTime - splitTimeSec;
                int splitTimeMin = splitTime/60;

                String splitTimeTx;
                if (splitTimeSec<10){
                    splitTimeTx = splitTimeMin+":0" + splitTimeSec;
                }else{
                    splitTimeTx = splitTimeMin+":" + splitTimeSec;
                }
                UI_params_str[1] = splitTimeTx;
            }

        }

        UI_params_float[0] = Float.parseFloat(log.get(i)[3]);
        UI_params_float[1] = Float.parseFloat(log.get(i)[4]);

//        System.out.println("numberIs:" + log.get(i)[0] + "  valueIs:" + log.get(i)[1] );

        if (logBO != null) {

            double xAcclLinear = Double.parseDouble(logBO.get(i)[5]);
            double yAcclLinear = Double.parseDouble(logBO.get(i)[6]);
            double zAcclLinear = Double.parseDouble(logBO.get(i)[7]);

            double tiltAngleX = Double.parseDouble(logBO.get(i)[3]) * Math.PI / 180;
            double tiltCosX = Math.cos(tiltAngleX);

            double tiltAngleZ = (90 - Math.abs(Double.parseDouble(logBO.get(i)[3]))) * Math.PI / 180;
            double tiltCosZ = Math.cos(tiltAngleZ);

            double xAcclActual = -xAcclLinear * tiltCosX;
            double zAcclActual = zAcclLinear * tiltCosZ;

            double boatAcclActualNow = xAcclActual + zAcclActual;

            if (acclCacheSize < acclCacheLength) {

                acclCacheSamples[acclCachePointer++] = boatAcclActualNow;
                acclCacheSize ++;

            }else{

                acclCachePointer = acclCachePointer % acclCacheLength;
                acclCacheSum -= acclCacheSamples[acclCachePointer];
                acclCacheSamples[acclCachePointer++] = boatAcclActualNow;
            }

            double boatAcclActualMA = doubleArrAverage(acclCacheSamples);
            double boatAcclActualGA = gaussianArrAverage(acclCacheSamples, gaussianKernel_10);


            UI_params_boat[0] = - boatAcclActualGA;

            double instantBoatSpeed = - boatAcclActualMA + UI_params_boat[1];
            UI_params_boat[1] = instantBoatSpeed;

            try {

                if (acclPhoneCacheSize < acclPhoneCacheLength) {

                    acclPhoneCacheSamples[acclPhoneCachePointer++] = Double.parseDouble(logPH.get(i)[17]);
                    acclPhoneCacheSize ++;

                }else{

                    acclPhoneCachePointer = acclPhoneCachePointer % acclPhoneCacheLength;
                    acclPhoneCacheSum -= acclPhoneCacheSamples[acclPhoneCachePointer];
                    acclPhoneCacheSamples[acclPhoneCachePointer++] = Double.parseDouble(logPH.get(i)[17]);
                }

                double boatAcclPhoneActualMA = doubleArrAverage(acclPhoneCacheSamples);
                double boatAcclPhoneActualGA = gaussianArrAverage(acclPhoneCacheSamples,gaussianKernel_10);



                UI_params_boat[2] = boatAcclPhoneActualMA;
//                UI_params_boat[2] = boatAcclPhoneActualGA;

                double phoneBoatSpeed = Double.parseDouble(logPH.get(i)[17]) + UI_params_boat[3];
                UI_params_boat[3] = phoneBoatSpeed;

            }catch (ArrayIndexOutOfBoundsException e){

                UI_params_boat[2] = UI_params_boat[0];
                UI_params_boat[3] = UI_params_boat[1];
            }

        }else{
            UI_params_boat[0] = 0;
            UI_params_boat[1] = 0;

            try {

                if (acclPhoneCacheSize < acclPhoneCacheLength) {

                    acclPhoneCacheSamples[acclPhoneCachePointer++] = Double.parseDouble(logPH.get(i)[17]);
                    acclPhoneCacheSize ++;

                }else{

                    acclPhoneCachePointer = acclPhoneCachePointer % acclPhoneCacheLength;
                    acclPhoneCacheSum -= acclPhoneCacheSamples[acclPhoneCachePointer];
                    acclPhoneCacheSamples[acclPhoneCachePointer++] = Double.parseDouble(logPH.get(i)[17]);
                }

                double boatAcclPhoneActualMA = doubleArrAverage(acclPhoneCacheSamples);
                double boatAcclPhoneActualGA = gaussianArrAverage(acclPhoneCacheSamples,gaussianKernel_10);


                UI_params_boat[2] = boatAcclPhoneActualMA;
//                UI_params_boat[2] = boatAcclPhoneActualGA;

                double phoneBoatSpeed = Double.parseDouble(logPH.get(i)[17]) + UI_params_boat[3];
                UI_params_boat[3] = phoneBoatSpeed;

            }catch (ArrayIndexOutOfBoundsException e){
                UI_params_boat[2] = UI_params_boat[0];
                UI_params_boat[3] = UI_params_boat[1];
            }

        }

    }

    private String updateClock(int progress, int outputRate) {

        double currentTimeSec = progress/outputRate;
        double currentTimeMin = currentTimeSec/60;
        int tempMin = new Double(currentTimeMin).intValue();
        int tempSec = new Double(currentTimeSec).intValue();
        tempSec = tempSec - 60*tempMin;
        String tempMinStr = "00";
        String tempSecStr = "00";

        if(tempMin<10){
            tempMinStr = "0" + tempMin;
        }else{
            tempMinStr = tempMin+"";
        }
        if(tempSec<10){
            tempSecStr = "0" + tempSec;
        }else{
            tempSecStr = tempSec+"";
        }

        String sectionTime = tempMinStr + ":" + tempSecStr;
        return sectionTime;

    }

    private void updateUI_sub_secondary(float[] UI_params_float, String[] UI_params_str) {
        mStrokeRate.setText(UI_params_str[0]);
        mBoatSpeed.setText(UI_params_str[1]);
        mDistanceTx.setText(UI_params_str[2]);
        mBoatYaw.setRotation(UI_params_float[0]);
        mBoatRoll.setRotation(UI_params_float[1]);

        for (int n=0; n<8; n++){
            mTvListHR.get(n).setText(String.valueOf(HR_params_cache[n]));

            if(HR_params_cache[n] == 0){
                mTvListHR.get(n).setBackgroundResource(R.color.colorGrayDark);
                mTvListHR.get(n).setTextColor(Color.WHITE);
//                mTvListHR.get(n).setText("-");

            }else if(HR_params_cache[n]<=90){
                mTvListHR.get(n).setBackgroundResource(R.drawable.gradient_green);
                mTvListHR.get(n).setTextColor(Color.WHITE);

            }else if (HR_params_cache[n] <= 130){
                mTvListHR.get(n).setBackgroundResource(R.drawable.gradient_orange);
                mTvListHR.get(n).setTextColor(Color.DKGRAY);

            }else {
                mTvListHR.get(n).setBackgroundResource(R.drawable.gradient_red);
                mTvListHR.get(n).setTextColor(Color.WHITE);
            }

        }

    }

    private void updateUI () {

            updateUI_sub(UI_params_L1,mDegreeL1fwd,mDegreeL1bwd,mDegreeL1,mOarL1, mOarL1_roll, mOarL1_pitch, mFrame_L1_roll, "left");
            updateUI_sub(UI_params_L2,mDegreeL2fwd,mDegreeL2bwd,mDegreeL2,mOarL2, mOarL2_roll, mOarL2_pitch, mFrame_L2_roll, "left");
            updateUI_sub(UI_params_L3,mDegreeL3fwd,mDegreeL3bwd,mDegreeL3,mOarL3, mOarL3_roll, mOarL3_pitch, mFrame_L3_roll, "left");
            updateUI_sub(UI_params_L4,mDegreeL4fwd,mDegreeL4bwd,mDegreeL4,mOarL4, mOarL4_roll, mOarL4_pitch, mFrame_L4_roll, "left");
            updateUI_sub(UI_params_R1,mDegreeR1fwd,mDegreeR1bwd,mDegreeR1,mOarR1, mOarR1_roll, mOarR1_pitch, mFrame_R1_roll, "right");
            updateUI_sub(UI_params_R2,mDegreeR2fwd,mDegreeR2bwd,mDegreeR2,mOarR2, mOarR2_roll, mOarR2_pitch, mFrame_R2_roll, "right");
            updateUI_sub(UI_params_R3,mDegreeR3fwd,mDegreeR3bwd,mDegreeR3,mOarR3, mOarR3_roll, mOarR3_pitch, mFrame_R3_roll, "right");
            updateUI_sub(UI_params_R4,mDegreeR4fwd,mDegreeR4bwd,mDegreeR4,mOarR4, mOarR4_roll, mOarR4_pitch, mFrame_R4_roll, "right");
            updateUI_sub_secondary(UI_params_Secondary_float, UI_params_Secondary_str);

            mProgressBar.setProgress(progressNow);
            mCurrentTime.setText(updateClock(playNodePos,samplingRate));


        // pending for test
            if (tabMode ==1 || tabMode ==0 ) {
                myRefreshRunnable refreshChart = new myRefreshRunnable(chart,
                        (float)(UI_params_L1[14]), (float)(UI_params_L2[14]),
                        (float)(UI_params_L3[14]), (float)(UI_params_L4[14]),
                        (float)(UI_params_R1[14]), (float)(UI_params_R2[14]),
                        (float)(UI_params_R3[14]), (float)(UI_params_R4[14]),
                        (float)(UI_params_boat[2]));

                refreshChart.run();

            }

    }

//    Runnable updateUI_run = new Runnable() {
//        @Override
//        public void run() {
//
//            updateUI_sub(UI_params_L1,mDegreeL1fwd,mDegreeL1bwd,mDegreeL1,mOarL1, mOarL1_roll, mOarL1_pitch, mFrame_L1_roll, "left");
//            updateUI_sub(UI_params_L2,mDegreeL2fwd,mDegreeL2bwd,mDegreeL2,mOarL2, mOarL2_roll, mOarL2_pitch, mFrame_L2_roll, "left");
//            updateUI_sub(UI_params_L3,mDegreeL3fwd,mDegreeL3bwd,mDegreeL3,mOarL3, mOarL3_roll, mOarL3_pitch, mFrame_L3_roll, "left");
//            updateUI_sub(UI_params_L4,mDegreeL4fwd,mDegreeL4bwd,mDegreeL4,mOarL4, mOarL4_roll, mOarL4_pitch, mFrame_L4_roll, "left");
//            updateUI_sub(UI_params_R1,mDegreeR1fwd,mDegreeR1bwd,mDegreeR1,mOarR1, mOarR1_roll, mOarR1_pitch, mFrame_R1_roll, "right");
//            updateUI_sub(UI_params_R2,mDegreeR2fwd,mDegreeR2bwd,mDegreeR2,mOarR2, mOarR2_roll, mOarR2_pitch, mFrame_R2_roll, "right");
//            updateUI_sub(UI_params_R3,mDegreeR3fwd,mDegreeR3bwd,mDegreeR3,mOarR3, mOarR3_roll, mOarR3_pitch, mFrame_R3_roll, "right");
//            updateUI_sub(UI_params_R4,mDegreeR4fwd,mDegreeR4bwd,mDegreeR4,mOarR4, mOarR4_roll, mOarR4_pitch, mFrame_R4_roll, "right");
//            updateUI_sub_secondary(UI_params_Secondary_float, UI_params_Secondary_str);
//
//            mProgressBar.setProgress(progressNow);
//            mCurrentTime.setText(updateClock(playNodePos,30));
//
//
//            // pending for test
////            myRefreshRunnable refreshChart = new myRefreshRunnable(chart,
////                    (float)(UI_params_L1[14]), (float)(UI_params_L1[14]),
////                    (float)(UI_params_L1[14]), (float)(UI_params_L1[14]),
////                    (float)(UI_params_L1[14]), (float)(UI_params_L1[14]),
////                    (float)(UI_params_L1[14]), (float)(UI_params_L1[14]));
////
////            refreshChart.run();
//
//        }
//    };

    private void updateUI_PB() {
        if (thread0 != null)
            thread0.interrupt();

        thread0 = new Thread(new Runnable() {

            @Override
            public void run() {
                while (playNodePos < itrMax) {

                    updateUIParams(playNodePos, UI_params_L1, logL1, "left", "L1", 0);
                    updateUIParams(playNodePos, UI_params_L2, logL2, "left", "L2",1);
                    updateUIParams(playNodePos, UI_params_L3, logL3, "left", "L3",2);
                    updateUIParams(playNodePos, UI_params_L4, logL4, "left", "L4",3);
                    updateUIParams(playNodePos, UI_params_R1, logR1, "right", "R1",4);
                    updateUIParams(playNodePos, UI_params_R2, logR2, "right", "R2",5);
                    updateUIParams(playNodePos, UI_params_R3, logR3, "right", "R3",6);
                    updateUIParams(playNodePos, UI_params_R4, logR4, "right", "R4",7);
                    updateUIParams_secondary(playNodePos, UI_params_Secondary_float, UI_params_Secondary_str,logPH);
//                    updateUI();
                    progressNow = (int) (playNodePos/(float)itrMax*100);
//                    System.out.println(progressNow);

                    playNodePos++;

                    synchronized (thread0) {
                        try {
                            if (suspended) {
                                thread0.wait();
                            }
                            Thread.sleep(Math.round((1000/samplingRate)/speedMultiple));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        thread0.start();

    }

    private void mountDataFile() throws IOException, CsvException {

        fileLoc = this.getFilesDir() + "/xsens/";
        fileList = getFileList(fileLoc,"csv");

        logList.add("empty");
        logList.add("empty");
        logList.add("empty");
        logList.add("empty");
        logList.add("empty");
        logList.add("empty");
        logList.add("empty");
        logList.add("empty");
        logList.add("empty");
        logList.add("empty");

        rawLogDataList.add(null);
        rawLogDataList.add(null);
        rawLogDataList.add(null);
        rawLogDataList.add(null);
        rawLogDataList.add(null);
        rawLogDataList.add(null);
        rawLogDataList.add(null);
        rawLogDataList.add(null);
        rawLogDataList.add(null);
        rawLogDataList.add(null);

        for(int i = 0; i < fileList.size(); i++){
            //2021-01-12 16:44:28_BO.csv
            //01234567890123456789012345
            String name = fileList.get(i).substring(0, 19);

            if(name.equals(mLogTime)){
                CSVReader reader;
                String endWith = fileList.get(i).substring(20, 22);
                switch (endWith){
                    case "BO":
                        logList.set(0,(fileLoc+fileList.get(i)));
                        System.out.println(logList.get(0));
                        reader = new CSVReader(new FileReader(logList.get(0)));
                        logBO = reader.readAll();
                        for ( int k = 0; k<=11 ;k++) {
                            logBO.remove(0);
                        }
                        rawLogDataList.set(0,logBO);
                        sizeBO = logBO.size();

//                        System.out.println("LOGBO.GET(0)[0]:" + logBO.get(0)[0]);
                        break;

                    case "L1":
                        logList.set(1,(fileLoc+fileList.get(i)));
                        reader = new CSVReader(new FileReader(logList.get(1)));
                        logL1 = reader.readAll();

                        for ( int k = 0; k<=11 ;k++) {
                            if (k == 5) {

                                try{
                                    String refreshRate = logL1.get(0)[1].substring(0, 2);
                                    samplingRate = Integer.valueOf(refreshRate);
                                    System.out.println(refreshRate);
                                }catch (Exception e){


                                }

                            }
                            logL1.remove(0);
                        }
                        sizeL1 = logL1.size();
                        rawLogDataList.set(1,logL1);
                        mFrame_L1.setAlpha(1f);
                        mOarL1.setAlpha(0.7f);
                        mOarL1_roll.setAlpha(0.7f);
                        mOarL1_pitch.setAlpha(0.7f);
                        break;

                    case "L2":
                        logList.set(2,(fileLoc+fileList.get(i)));
                        reader = new CSVReader(new FileReader(logList.get(2)));
                        logL2 = reader.readAll();

                        for ( int k = 0; k<=11 ;k++) {
                            if (k == 5) {
                                try{
                                    String refreshRate = logL2.get(0)[1].substring(0, 2);
                                    samplingRate = Integer.valueOf(refreshRate);
                                    System.out.println(refreshRate);
                                }catch (Exception e){

                                }
                            }
                            logL2.remove(0);
                        }
                        sizeL2 = logL2.size();
                        rawLogDataList.set(2,logL2);

                        mFrame_L2.setAlpha(1f);
                        mOarL2.setAlpha(0.7f);
                        mOarL2_roll.setAlpha(0.7f);
                        mOarL2_pitch.setAlpha(0.7f);
                        break;

                    case "L3":
                        logList.set(3,(fileLoc+fileList.get(i)));
                        reader = new CSVReader(new FileReader(logList.get(3)));
                        logL3 = reader.readAll();

                        for ( int k = 0; k<=11 ;k++) {
                            if (k == 5) {
                                try{
                                    String refreshRate = logL3.get(0)[1].substring(0, 2);
                                    samplingRate = Integer.valueOf(refreshRate);
                                    System.out.println(refreshRate);
                                }catch (Exception e){
                                }
                            }
                            logL3.remove(0);
                        }
                        sizeL3 = logL3.size();
                        rawLogDataList.set(3,logL3);

                        mFrame_L3.setAlpha(1f);
                        mOarL3.setAlpha(0.7f);
                        mOarL3_roll.setAlpha(0.7f);
                        mOarL3_pitch.setAlpha(0.7f);
                        break;

                    case "L4":
                        logList.set(4,(fileLoc+fileList.get(i)));
                        reader = new CSVReader(new FileReader(logList.get(4)));
                        logL4 = reader.readAll();

                        for ( int k = 0; k<=11 ;k++) {
                            if (k == 5) {
                                try{
                                    String refreshRate = logL4.get(0)[1].substring(0, 2);
                                    samplingRate = Integer.valueOf(refreshRate);
                                    System.out.println(refreshRate);
                                }catch (Exception e){
                                }
                            }
                            logL4.remove(0);
                        }
                        sizeL4 = logL4.size();
                        rawLogDataList.set(4,logL4);

                        mFrame_L4.setAlpha(1f);
                        mOarL4.setAlpha(0.7f);
                        mOarL4_roll.setAlpha(0.7f);
                        mOarL4_pitch.setAlpha(0.7f);
                        break;

                    case "R1":
                        logList.set(5,(fileLoc+fileList.get(i)));
                        reader = new CSVReader(new FileReader(logList.get(5)));
                        logR1 = reader.readAll();
                        for ( int k = 0; k<=11 ;k++) {
                            if (k == 5) {
                                try{
                                    String refreshRate = logR1.get(0)[1].substring(0, 2);
                                    samplingRate = Integer.valueOf(refreshRate);
                                    System.out.println(refreshRate);
                                }catch (Exception e){
                                }
                            }
                            logR1.remove(0);
                        }
                        sizeR1 = logR1.size();
                        rawLogDataList.set(5,logR1);

                        mFrame_R1.setAlpha(1f);
                        mOarR1.setAlpha(0.7f);
                        mOarR1_roll.setAlpha(0.7f);
                        mOarR1_pitch.setAlpha(0.7f);
                        break;

                    case "R2":
                        logList.set(6,(fileLoc+fileList.get(i)));
                        reader = new CSVReader(new FileReader(logList.get(6)));
                        logR2 = reader.readAll();
                        for ( int k = 0; k<=11 ;k++) {
                            if (k == 5) {
                                try{
                                    String refreshRate = logR2.get(0)[1].substring(0, 2);
                                    samplingRate = Integer.valueOf(refreshRate);
                                    System.out.println(refreshRate);
                                }catch (Exception e){
                                }
                            }
                            logR2.remove(0);
                        }
                        rawLogDataList.set(6,logR2);
                        sizeR2 = logR2.size();

                        mFrame_R2.setAlpha(1f);
                        mOarR2.setAlpha(0.7f);
                        mOarR2_roll.setAlpha(0.7f);
                        mOarR2_pitch.setAlpha(0.7f);
                        break;

                    case "R3":
                        logList.set(7,(fileLoc+fileList.get(i)));
                        reader = new CSVReader(new FileReader(logList.get(7)));
                        logR3 = reader.readAll();
                        for ( int k = 0; k<=11 ;k++) {
                            if (k == 5) {
                                try{
                                    String refreshRate = logR3.get(0)[1].substring(0, 2);
                                    samplingRate = Integer.valueOf(refreshRate);
                                    System.out.println(refreshRate);
                                }catch (Exception e){
                                }
                            }
                            logR3.remove(0);
                        }
                        sizeR3 = logR3.size();
                        rawLogDataList.set(7,logR3);

                        mFrame_R3.setAlpha(1f);
                        mOarR3.setAlpha(0.7f);
                        mOarR3_roll.setAlpha(0.7f);
                        mOarR3_pitch.setAlpha(0.7f);
                        break;

                    case "R4":
                        logList.set(8,(fileLoc+fileList.get(i)));
                        reader = new CSVReader(new FileReader(logList.get(8)));
                        logR4 = reader.readAll();
                        for ( int k = 0; k<=11 ;k++) {
                            if (k == 5) {
                                try{
                                    String refreshRate = logR4.get(0)[1].substring(0, 2);
                                    samplingRate = Integer.valueOf(refreshRate);
                                    System.out.println(refreshRate);
                                }catch (Exception e){
                                }
                            }
                            logR4.remove(0);
                        }
                        rawLogDataList.set(8,logR4);
                        sizeR4 = logR4.size();

                        mFrame_R4.setAlpha(1f);
                        mOarR4.setAlpha(0.7f);
                        mOarR4_roll.setAlpha(0.7f);
                        mOarR4_pitch.setAlpha(0.7f);
                        break;

                    case "PH":
                        logList.set(9,(fileLoc+fileList.get(i)));
                        reader = new CSVReader(new FileReader(logList.get(9)));
                        logPH = reader.readAll();
                        try{
                            if (logPH.get(0)[18]!= null){
                                samplingRate = Integer.valueOf(logPH.get(0)[18]);
                            }
                        }catch (ArrayIndexOutOfBoundsException e){

                            samplingRate = 20;

                        }
                        logPH.remove(0);
                        rawLogDataList.set(9,logPH);

                        sizePH = logPH.size();
//                        System.out.println("sizePH: ______________" + sizePH);
//                        System.out.println("logPH: ______________" + logPH);

                        System.out.println("sample Rate is:" + samplingRate);

                        break;

                }

            }
        }

        if (samplingRate == 20) {


            wattageSuppressionRatio = 0.35;


        }

//        samplingRate = 20;
    }

    private void trimData() {

        for (int i = 0; i<logList.size();i++){

            System.out.println("LogList Status:" + logList.get(i));

            for (int a = 0; a<rawLogDataList.size(); a++) {

                System.out.println(rawLogDataList.get(i));

            }

            System.out.println(rawLogDataList.get(i) != null);


            if (!logList.get(i).equals("empty") &&  rawLogDataList.get(i) != null){

                Log.e("FLAG ENTER", "enter trim loop");

                for (int j = 1; j<5; j++){

                    for (int k = 1; k< rawLogDataList.get(i).size()-1; k++){

                        double currentData = Double.parseDouble(rawLogDataList.get(i).get(k)[j]);
                        double lastData = Double.parseDouble(rawLogDataList.get(i).get(k-1)[j]);
                        double nextData = Double.parseDouble(rawLogDataList.get(i).get(k+1)[j]);

                        if (currentData == lastData){
                            rawLogDataList.get(i).get(k)[j] = String.valueOf((lastData + nextData)/2);
                            System.out.println(String.valueOf((lastData + nextData)/2));
                        }else{

                            System.out.println("no need to trim");


                        }

                    }
                }

            }

        }








    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        this.finish();
//
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            suspended = true;

            new AlertDialog.Builder(DataPlayback.this).setTitle("系统提示")
                    .setMessage("确认退出数据回放面板吗？")
                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            if (thread0 != null) {
//                                synchronized (thread0) {
//                                    try {
//                                        thread0.wait();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                            thread0.interrupt();
//                            timerTask.cancel();
                            finish();
                        }
                    }).setNegativeButton("取消",new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }).show();
        }
        return false;
    }




    private ArrayList<String> getFileList(String fileAbsolutePath, String type) {
        ArrayList<String>  result = new ArrayList<String>();
        File file = new File(fileAbsolutePath);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (!files[i].isDirectory()) {
                String fileName = files[i].getName();
                if (fileName.trim().toLowerCase().endsWith(type)) {
                    result.add(fileName);
                }
            }
        }
        return result;
    }

    public double doubleArrAverage(double[] arr) {
        double sum = 0;
        for(int i = 0;i < arr.length; i++) {
            sum += arr[i];
        }
        return sum / arr.length;
    }

    public double gaussianArrAverage(double[] arr, double[] kernel) {
        double result;
        double sum = 0;

        if (arr.length < kernel.length){
            for(int i = 0;i < arr.length; i++) {
                sum += arr[i];
            }
            result = sum / arr.length;
//            System.out.println("simple average");

        }else{
            for (int i = 0; i< arr.length; i++){
                sum += arr[i]*kernel[i];
            }
            result = sum;
//            System.out.println("gaussian average");

        }

        return result;
    }

    private void chartInit (LineChart lineChart, int DataIndex) {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setPinchZoom(true);
        lineChart.setBackgroundColor(Color.TRANSPARENT);

        LineData data_empty = new LineData();
        data_empty.setValueTextColor(Color.GRAY);
        data_empty.setDrawValues(true);

        lineChart.setData(data_empty);
        lineChart.getAxisRight().setEnabled(false);

        Legend legend= lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTypeface(tfLight);
        legend.setTextColor(Color.GRAY);
        legend.setWordWrapEnabled(true);
        legend.setTextSize(11f);

        XAxis xl = lineChart.getXAxis();
        xl.setTypeface(tfLight);
        xl.setTextColor(Color.GRAY);
        xl.setDrawGridLines(true);
        xl.enableGridDashedLine(10f, 10f, 0f);
        xl.setAvoidFirstLastClipping(true);
        xl.setTextSize(1f);
        xl.setEnabled(true);

        YAxis yl = lineChart.getAxisLeft();
        yl.setTypeface(tfLight);
        yl.setTextColor(Color.GRAY);

        YAxis yr = lineChart.getAxisRight();
        yr.setTypeface(tfLight);
        yr.setTextColor(Color.GRAY);
        yr.setAxisMinimum(-7);
        yr.setAxisMaximum(7);

//        lineChart.setVisibleXRangeMaximum(120);
        lineChart.setVisibleXRangeMinimum(120);

        float limitLineY = 0f;

        yl.setDrawGridLines(true);


        LineData dataLive = lineChart.getLineData();

        LineDataSet L1_yaw_speed = createSet("L1", R.color.colorYellow);
        LineDataSet L2_yaw_speed = createSet("L2", R.color.colorOrangeDark);
        LineDataSet L3_yaw_speed = createSet("L3", R.color.colorRedDark);
        LineDataSet L4_yaw_speed = createSet("L4", R.color.colorRedDarker);
        LineDataSet R1_yaw_speed = createSet("R1", R.color.colorGreenLight);
        LineDataSet R2_yaw_speed = createSet("R2", R.color.colorGreenDarker);
        LineDataSet R3_yaw_speed = createSet("R3", R.color.colorBlueLight);
        LineDataSet R4_yaw_speed = createSet("R4", R.color.colorVioletLight);
        LineDataSet Boat_Acceleration;


        if (Objects.equals(Lang, "eng")){
            Boat_Acceleration = createSet("Boat Acceleration", R.color.colorGrayDark);

        }else{
            Boat_Acceleration = createSet("船加速度", R.color.colorGrayDark);

        }


//        L1_yaw_speed.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        L1_yaw_speed.setCubicIntensity(0.001f);

//        LineDataSet L1_yaw_speed = createSet("L1", R.color.colorOrangeDark);
//        LineDataSet L2_yaw_speed = createSet("L2", R.color.colorRedDark);
//        LineDataSet L3_yaw_speed = createSet("L3", R.color.colorGreenDark);
//        LineDataSet L4_yaw_speed = createSet("L4", R.color.colorGreenLight);
//        LineDataSet R1_yaw_speed = createSet("R1", R.color.colorGreenDark);
//        LineDataSet R2_yaw_speed = createSet("R2", R.color.colorVioletLight);
//        LineDataSet R3_yaw_speed = createSet("R3", R.color.colorVioletDark);
//        LineDataSet R4_yaw_speed = createSet("R4", R.color.colorVioletLight);
//        LineDataSet Boat_Acceleration = createSet("Boat", R.color.colorGrayDark);

        Boat_Acceleration.setLineWidth(1f);
        Boat_Acceleration.setDrawFilled(true);
        Boat_Acceleration.setFillAlpha(65);
        Boat_Acceleration.setFillColor(Color.GRAY);
        Boat_Acceleration.setAxisDependency(YAxis.AxisDependency.RIGHT);

        dataLive.addDataSet(L1_yaw_speed);
        dataLive.addDataSet(L2_yaw_speed);
        dataLive.addDataSet(L3_yaw_speed);
        dataLive.addDataSet(L4_yaw_speed);
        dataLive.addDataSet(R1_yaw_speed);
        dataLive.addDataSet(R2_yaw_speed);
        dataLive.addDataSet(R3_yaw_speed);
        dataLive.addDataSet(R4_yaw_speed);
        dataLive.addDataSet(Boat_Acceleration);

        LimitLine yLimitLine = new LimitLine(limitLineY);
        yLimitLine.setLineColor(Color.DKGRAY);
        yLimitLine.setTextColor(Color.DKGRAY);
        yLimitLine.setLineWidth(3f);
        yl.addLimitLine(yLimitLine);

        switch (DataIndex){
            case 4:
                yl.setAxisMaximum(8f);
                yl.setAxisMinimum(-8f);
                Boat_Acceleration.setVisible(true);

                break;
            case 0:
                yl.setAxisMaximum(20f);
                yl.setAxisMinimum(-130f);
                Boat_Acceleration.setVisible(false);

                break;
            case 3:
                yl.setAxisMaximum(5f);
                yl.setAxisMinimum(-30f);
                Boat_Acceleration.setVisible(false);

                break;
            case 8:
                yl.setAxisMaximum(1000f);
                yl.setAxisMinimum(-1000f);
                Boat_Acceleration.setVisible(false);

                break;

            case 20:
                yl.setAxisMaximum(2000f);
                yl.setAxisMinimum(-5f);
                Boat_Acceleration.setVisible(false);


                break;
        }

    }

    private LineDataSet createSet(String string, int color) {

        LineDataSet set = new LineDataSet(null, string);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setValueTextColor(Color.GRAY);
        set.setDrawCircles(false);
        set.setColor(ContextCompat.getColor(this, color));
        set.setDrawValues(false);
        return set;
    }

    private void addEntryChart(LineChart lineChart,
                               float L1_av, float L2_av, float L3_av,float L4_av,
                               float R1_av, float R2_av, float R3_av, float R4_av,
                               float Boat_av){

        LineData data = lineChart.getData();

        if (data != null && !suspended) {
            LineDataSet L1_av_set = (LineDataSet) data.getDataSetByIndex(0);
            LineDataSet L2_av_set = (LineDataSet) data.getDataSetByIndex(1);
            LineDataSet L3_av_set = (LineDataSet) data.getDataSetByIndex(2);
            LineDataSet L4_av_set = (LineDataSet) data.getDataSetByIndex(3);
            LineDataSet R1_av_set = (LineDataSet) data.getDataSetByIndex(4);
            LineDataSet R2_av_set = (LineDataSet) data.getDataSetByIndex(5);
            LineDataSet R3_av_set = (LineDataSet) data.getDataSetByIndex(6);
            LineDataSet R4_av_set = (LineDataSet) data.getDataSetByIndex(7);
            LineDataSet Boat_av_set = (LineDataSet) data.getDataSetByIndex(8);

            data.addEntry(new Entry(L1_av_set.getEntryCount(), L1_av), 0);
            data.addEntry(new Entry(L2_av_set.getEntryCount(), L2_av), 1);
            data.addEntry(new Entry(L3_av_set.getEntryCount(), L3_av), 2);
            data.addEntry(new Entry(L4_av_set.getEntryCount(), L4_av), 3);
            data.addEntry(new Entry(R1_av_set.getEntryCount(), R1_av), 4);
            data.addEntry(new Entry(R2_av_set.getEntryCount(), R2_av), 5);
            data.addEntry(new Entry(R3_av_set.getEntryCount(), R3_av), 6);
            data.addEntry(new Entry(R4_av_set.getEntryCount(), R4_av), 7);
            data.addEntry(new Entry(Boat_av_set.getEntryCount(), Boat_av), 8);

            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();
            lineChart.setVisibleXRangeMaximum(120);
            lineChart.moveViewToX(playNodePos);

        }

    }

    private void removeEntryChart(LineChart lineChart){

        LineData data = lineChart.getData();

        if (data != null) {
            LineDataSet L1_av_set = (LineDataSet) data.getDataSetByIndex(0);
            LineDataSet L2_av_set = (LineDataSet) data.getDataSetByIndex(1);
            LineDataSet L3_av_set = (LineDataSet) data.getDataSetByIndex(2);
            LineDataSet L4_av_set = (LineDataSet) data.getDataSetByIndex(3);
            LineDataSet R1_av_set = (LineDataSet) data.getDataSetByIndex(4);
            LineDataSet R2_av_set = (LineDataSet) data.getDataSetByIndex(5);
            LineDataSet R3_av_set = (LineDataSet) data.getDataSetByIndex(6);
            LineDataSet R4_av_set = (LineDataSet) data.getDataSetByIndex(7);

            data.removeEntry(L1_av_set.getEntryCount()-120, 0);
            data.removeEntry(L2_av_set.getEntryCount()-120, 0);
            data.removeEntry(L3_av_set.getEntryCount()-120, 0);
            data.removeEntry(L4_av_set.getEntryCount()-120, 0);
            data.removeEntry(R1_av_set.getEntryCount()-120, 0);
            data.removeEntry(R2_av_set.getEntryCount()-120, 0);
            data.removeEntry(R3_av_set.getEntryCount()-120, 0);
            data.removeEntry(R4_av_set.getEntryCount()-120, 0);

            data.notifyDataChanged();
            lineChart.notifyDataSetChanged();

        }

    }

    private class myRefreshRunnable implements Runnable {

        private float L1_av_run;
        private float L2_av_run;
        private float L3_av_run;
        private float L4_av_run;
        private float R1_av_run;
        private float R2_av_run;
        private float R3_av_run;
        private float R4_av_run;
        private float Boat_av_run;
        private LineChart lineChart_run;

        public myRefreshRunnable (LineChart _lineChart_run,
                                  float _L1_av_run, float _L2_av_run, float _L3_av_run, float _L4_av_run,
                                  float _R1_av_run, float _R2_av_run, float _R3_av_run, float _R4_av_run,
                                  float _Boat_av_run){
            this.lineChart_run = _lineChart_run;
            this.L1_av_run = _L1_av_run;
            this.L2_av_run = _L2_av_run;
            this.L3_av_run = _L3_av_run;
            this.L4_av_run = _L4_av_run;
            this.R1_av_run = _R1_av_run;
            this.R2_av_run = _R2_av_run;
            this.R3_av_run = _R3_av_run;
            this.R4_av_run = _R4_av_run;
            this.Boat_av_run = _Boat_av_run;

        }

        @Override
        public void run() {

            addEntryChart(
                    lineChart_run,
                    L1_av_run, L2_av_run, L3_av_run, L4_av_run,
                    R1_av_run, R2_av_run, R3_av_run, R4_av_run,
                    Boat_av_run);
//            removeEntryChart(lineChart_run);
        }
    }

    private double getForce (double boatSpeed, double strokeSpeed, double attackAngle, double oarAngle){
        double forceData = 0;

        return forceData;
    }


//    public class KalmanFilter {
//        private JKalman mFilter;
//        private Matrix mPredictValue;
//        private Matrix mCorrectedValue;
//        private Matrix mMeasurementValue;
//
//        private final String TAG = "KalmanFilter";
//
//        public void KalmanFilter(){
//            // empty constructor.
//        }
//
//        public void initial(){
//            try {
//                mFilter = new JKalman(4, 2);
//
//                double x = 0;
//                double y = 0;
//
//                // init
//                mPredictValue = new Matrix(4, 1); // predict state [x, y, dx, dy, dxy]
//                mCorrectedValue = new Matrix(4, 1); // corrected state [x, y, dx, dy, dxy]
//                mMeasurementValue = new Matrix(2, 1); // measurement [x]
//                mMeasurementValue.set(0, 0, x);
//                mMeasurementValue.set(1, 0, y);
//
//                // transitions for x, y, dx, dy
//                double[][] tr = { {1, 0, 1, 0},
//                                  {0, 1, 0, 1},
//                                  {0, 0, 1, 0},
//                                  {0, 0, 0, 1} };
//                mFilter.setTransition_matrix(new Matrix(tr));
//                mFilter.setError_cov_post(mFilter.getError_cov_post().identity());
//
//
//            } catch (Exception ex) {
//                Log.e(TAG, ex.getMessage());
//            }
//        }
//
//        public double[] update(double[] oldValue) {
//            // check state before
//
//            double[] newValue = {0,0};
//            mPredictValue = mFilter.Predict();
//            mMeasurementValue.set(0, 0, oldValue[0]);
//            mMeasurementValue.set(1, 0, oldValue[1]);
//
//            // look better
//            mCorrectedValue = mFilter.Correct(mMeasurementValue);
//
//            newValue[0] = (float)mPredictValue.get(0,0);
//            newValue[1] = (float)mPredictValue.get(1,0);
//            return  newValue;
//        }
//    }

    private double myKalmanFilter (double in, int pos){
        double newOut = 0;
        double[][] newIn = {{in}};
        Matrix s = new Matrix (newIn);

        Matrix XpLocal = XpList.get(pos);
        Matrix PpLocal = PpList.get(pos);
        Matrix KLocal = KList.get(pos);
        Matrix XuLocal = XuList.get(pos);
        Matrix PuLocal = PuList.get(pos);

        XpLocal = A.times(XuLocal);
        PpLocal = A.times(PuLocal).times(A.transpose()).plus(G.times(Q).times(G.transpose()));
        KLocal = PpLocal.times(H.transpose()).times((H.times(PpLocal).times(H.transpose()).plus(R1)).inverse());
        XuLocal = (I.minus(KLocal.times(H))).times(XpLocal).plus(KLocal.times(s));
        PuLocal = (I.minus(KLocal.times(H))).times(PpLocal);
        newOut = XuLocal.get(0,0);

        XpList.set(pos,XpLocal);
        PpList.set(pos,PpLocal);
        KList.set(pos,KLocal);
        XuList.set(pos,XuLocal);
        PuList.set(pos,PuLocal);

        return newOut;
    }




}


