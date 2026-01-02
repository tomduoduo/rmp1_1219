package com.motionrivalry.rowmasterpro;

import static com.motionrivalry.rowmasterpro.MainActivity.isPad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.motionrivalry.rowmasterpro.utils.TimerManager;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

import com.polar.sdk.api.PolarBleApi;
import com.polar.sdk.api.PolarBleApiDefaultImpl;

//import polar.com.sdk.api.PolarBleApi;
//import polar.com.sdk.api.PolarBleApiDefaultImpl;

public class HeartRateMonitor extends AppCompatActivity {

    private Button mScan;
    private Button mActivate;
    private Button mStart;

    private TextView mPopupName_1;
    private TextView mPopupName_2;
    private TextView mPopupName_3;
    private TextView mPopupName_4;
    private TextView mPopupName_5;
    private TextView mPopupName_6;
    private TextView mPopupName_7;
    private TextView mPopupName_8;
    private TextView mPopupName_9;
    private TextView mPopupName_10;
    private TextView mPopupName_11;
    private TextView mPopupName_12;
    private TextView mPopupName_13;
    private TextView mPopupName_14;
    private TextView mPopupName_15;
    private TextView mPopupName_16;
    private TextView mPopupName_17;
    private TextView mPopupName_18;
    private TextView mPopupName_19;
    private TextView mPopupName_20;
    private TextView mPopupName_21;
    private TextView mPopupName_22;
    private TextView mPopupName_23;
    private TextView mPopupName_24;
    private TextView mPopupName_25;
    private TextView mPopupName_26;
    private TextView mPopupName_27;
    private TextView mPopupName_28;
    private TextView mPopupName_29;
    private TextView mPopupName_30;
    private TextView mPopupName_31;
    private TextView mPopupName_32;
    private ArrayList<TextView> mPopupNameList = new ArrayList<>();

    private TextView mPopupPolarID_1;
    private TextView mPopupPolarID_2;
    private TextView mPopupPolarID_3;
    private TextView mPopupPolarID_4;
    private TextView mPopupPolarID_5;
    private TextView mPopupPolarID_6;
    private TextView mPopupPolarID_7;
    private TextView mPopupPolarID_8;
    private TextView mPopupPolarID_9;
    private TextView mPopupPolarID_10;
    private TextView mPopupPolarID_11;
    private TextView mPopupPolarID_12;
    private TextView mPopupPolarID_13;
    private TextView mPopupPolarID_14;
    private TextView mPopupPolarID_15;
    private TextView mPopupPolarID_16;
    private TextView mPopupPolarID_17;
    private TextView mPopupPolarID_18;
    private TextView mPopupPolarID_19;
    private TextView mPopupPolarID_20;
    private TextView mPopupPolarID_21;
    private TextView mPopupPolarID_22;
    private TextView mPopupPolarID_23;
    private TextView mPopupPolarID_24;
    private TextView mPopupPolarID_25;
    private TextView mPopupPolarID_26;
    private TextView mPopupPolarID_27;
    private TextView mPopupPolarID_28;
    private TextView mPopupPolarID_29;
    private TextView mPopupPolarID_30;
    private TextView mPopupPolarID_31;
    private TextView mPopupPolarID_32;
    private ArrayList<TextView> mPopupPolarIDList = new ArrayList<>();

    private Spinner mSpinnerName_1;
    private Spinner mSpinnerName_2;
    private Spinner mSpinnerName_3;
    private Spinner mSpinnerName_4;
    private Spinner mSpinnerName_5;
    private Spinner mSpinnerName_6;
    private Spinner mSpinnerName_7;
    private Spinner mSpinnerName_8;
    private Spinner mSpinnerName_9;
    private Spinner mSpinnerName_10;
    private Spinner mSpinnerName_11;
    private Spinner mSpinnerName_12;
    private Spinner mSpinnerName_13;
    private Spinner mSpinnerName_14;
    private Spinner mSpinnerName_15;
    private Spinner mSpinnerName_16;
    private Spinner mSpinnerName_17;
    private Spinner mSpinnerName_18;
    private Spinner mSpinnerName_19;
    private Spinner mSpinnerName_20;
    private Spinner mSpinnerName_21;
    private Spinner mSpinnerName_22;
    private Spinner mSpinnerName_23;
    private Spinner mSpinnerName_24;
    private Spinner mSpinnerName_25;
    private Spinner mSpinnerName_26;
    private Spinner mSpinnerName_27;
    private Spinner mSpinnerName_28;
    private Spinner mSpinnerName_29;
    private Spinner mSpinnerName_30;
    private Spinner mSpinnerName_31;
    private Spinner mSpinnerName_32;
    private ArrayList<Spinner> mSpinnerNameList = new ArrayList<>();

    private Spinner mSpinnerBelt_1;
    private Spinner mSpinnerBelt_2;
    private Spinner mSpinnerBelt_3;
    private Spinner mSpinnerBelt_4;
    private Spinner mSpinnerBelt_5;
    private Spinner mSpinnerBelt_6;
    private Spinner mSpinnerBelt_7;
    private Spinner mSpinnerBelt_8;
    private Spinner mSpinnerBelt_9;
    private Spinner mSpinnerBelt_10;
    private Spinner mSpinnerBelt_11;
    private Spinner mSpinnerBelt_12;
    private Spinner mSpinnerBelt_13;
    private Spinner mSpinnerBelt_14;
    private Spinner mSpinnerBelt_15;
    private Spinner mSpinnerBelt_16;
    private Spinner mSpinnerBelt_17;
    private Spinner mSpinnerBelt_18;
    private Spinner mSpinnerBelt_19;
    private Spinner mSpinnerBelt_20;
    private Spinner mSpinnerBelt_21;
    private Spinner mSpinnerBelt_22;
    private Spinner mSpinnerBelt_23;
    private Spinner mSpinnerBelt_24;
    private Spinner mSpinnerBelt_25;
    private Spinner mSpinnerBelt_26;
    private Spinner mSpinnerBelt_27;
    private Spinner mSpinnerBelt_28;
    private Spinner mSpinnerBelt_29;
    private Spinner mSpinnerBelt_30;
    private Spinner mSpinnerBelt_31;
    private Spinner mSpinnerBelt_32;
    private ArrayList<Spinner> mSpinnerBeltList = new ArrayList<>();

    private TextView HR_dp_now_1;
    private TextView HR_dp_now_2;
    private TextView HR_dp_now_3;
    private TextView HR_dp_now_4;
    private TextView HR_dp_now_5;
    private TextView HR_dp_now_6;
    private TextView HR_dp_now_7;
    private TextView HR_dp_now_8;
    private TextView HR_dp_now_9;
    private TextView HR_dp_now_10;
    private TextView HR_dp_now_11;
    private TextView HR_dp_now_12;
    private TextView HR_dp_now_13;
    private TextView HR_dp_now_14;
    private TextView HR_dp_now_15;
    private TextView HR_dp_now_16;
    private TextView HR_dp_now_17;
    private TextView HR_dp_now_18;
    private TextView HR_dp_now_19;
    private TextView HR_dp_now_20;
    private TextView HR_dp_now_21;
    private TextView HR_dp_now_22;
    private TextView HR_dp_now_23;
    private TextView HR_dp_now_24;
    private TextView HR_dp_now_25;
    private TextView HR_dp_now_26;
    private TextView HR_dp_now_27;
    private TextView HR_dp_now_28;
    private TextView HR_dp_now_29;
    private TextView HR_dp_now_30;
    private TextView HR_dp_now_31;
    private TextView HR_dp_now_32;
    private ArrayList<TextView> mHeartRateNowList = new ArrayList<>();

    private TextView HR_dp_avg_1;
    private TextView HR_dp_avg_2;
    private TextView HR_dp_avg_3;
    private TextView HR_dp_avg_4;
    private TextView HR_dp_avg_5;
    private TextView HR_dp_avg_6;
    private TextView HR_dp_avg_7;
    private TextView HR_dp_avg_8;
    private TextView HR_dp_avg_9;
    private TextView HR_dp_avg_10;
    private TextView HR_dp_avg_11;
    private TextView HR_dp_avg_12;
    private TextView HR_dp_avg_13;
    private TextView HR_dp_avg_14;
    private TextView HR_dp_avg_15;
    private TextView HR_dp_avg_16;
    private TextView HR_dp_avg_17;
    private TextView HR_dp_avg_18;
    private TextView HR_dp_avg_19;
    private TextView HR_dp_avg_20;
    private TextView HR_dp_avg_21;
    private TextView HR_dp_avg_22;
    private TextView HR_dp_avg_23;
    private TextView HR_dp_avg_24;
    private TextView HR_dp_avg_25;
    private TextView HR_dp_avg_26;
    private TextView HR_dp_avg_27;
    private TextView HR_dp_avg_28;
    private TextView HR_dp_avg_29;
    private TextView HR_dp_avg_30;
    private TextView HR_dp_avg_31;
    private TextView HR_dp_avg_32;
    private ArrayList<TextView> mHeartRateAvgList = new ArrayList<>();

    private String[] mNameList;
    private String[] mBeltList;

    private ArrayList<BluetoothDevice> mListBelts = new ArrayList<>();

    private Integer[] HR_params_cache = new Integer[32];

    private ArrayList<String> mBeltList_available = new ArrayList<>();
    private ArrayList<String> mBeltList_selected = new ArrayList<>();
    private ArrayList<String> mNameList_available = new ArrayList<>();
    private ArrayList<String> mNameList_passed = new ArrayList<>();
    private ArrayList<String> mNameList_selected = new ArrayList<>();
    private boolean mIsScanning = false;

    private static final String TAG = MainActivity.class.getSimpleName();
    private PolarBleApi api;
    Disposable scanDisposable;
    Disposable broadcastDisposable;

    private int mediator = 0;

    private int zone0 = 134 - mediator;
    private int zone1 = 153 - mediator;
    private int zone2 = 165 - mediator;
    private int zone3 = 170 - mediator;
    private int zone4 = 179 - mediator;
    private int zone5 = 185 - mediator;

    private ArrayList<String> planFileList = null;
    private ArrayList<String> planFileNameList = null;
    private List<String[]> planList = new ArrayList<>();
    private ArrayList<List<String[]>> athletePolarIDPlanList = new ArrayList<>();

    private String planFileLoc = null;
    private String planFileGDMLoc = null;
    private String planFileGDFLoc = null;
    private ArrayList<String> athleteList = null;
    private ArrayList<String> polarIDList = null;
    private String athleteFileLoc = null;
    private ArrayList<String> athleteFileList = null;
    private ArrayList<List<String[]>> athletePolarIDListList = new ArrayList<>();

    private View popupViewPresetList;
    private PopupWindow mWindowPresetList = null;

    private Button mLoadPreset;
    private ListView mPlayerListHRM;
    private ListView mPlanListHRM;

    private ArrayList<String> selectedAthleteList = new ArrayList<>();

    private Button mPopupConfirm;
    private Button mPopupReset;

    private Button mResetSpinner;
    private Button mPopupAddPlan;
    private Button mPopupDeletePlan;

    private CustomChronometer mTotalElapse;
    private int mStartStatus = 0;

    private int tabMode = 0;
    private int iterMax;

    private int lastCheckedPlanNumber = -1;

    private String teamSelected = null;
    private String alterPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (metrics.xdpi <= 250 || isPad(this)) {
            tabMode = 1;
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_heart_rate_monitor_tab);
            iterMax = 32;

        } else {
            tabMode = 0;
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.activity_heart_rate_monitor);
            iterMax = 16;
        }

        api = PolarBleApiDefaultImpl.defaultImplementation(this, PolarBleApi.ALL_FEATURES);
        api.setPolarFilter(false);

        mResetSpinner = findViewById(R.id.btn_clean_spinner_HRM);
        mLoadPreset = findViewById(R.id.load_preset_HRM);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Intent intent = getIntent();
        teamSelected = intent.getStringExtra("teamSelected");

        System.out.println(teamSelected);

        if (tabMode == 1) {
            popupViewPresetList = inflater.inflate(R.layout.popup_select_player_tab, null, false);

        } else {

            popupViewPresetList = inflater.inflate(R.layout.popup_select_player, null, false);

        }

        mWindowPresetList = new PopupWindow(popupViewPresetList, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);

        mPlayerListHRM = popupViewPresetList.findViewById(R.id.hrm_player_list_lv);
        mPlanListHRM = popupViewPresetList.findViewById(R.id.hrm_plan_lv);
        mPopupAddPlan = popupViewPresetList.findViewById(R.id.hrm_btn_add_plan);
        mPopupDeletePlan = popupViewPresetList.findViewById(R.id.hrm_btn_del_plan);

        mPopupConfirm = popupViewPresetList.findViewById(R.id.confirm_plan_HRM);
        mPopupReset = popupViewPresetList.findViewById(R.id.clear_plan_HRM);

        planFileLoc = this.getFilesDir() + "/hrm_plan_list/" + teamSelected + "/";
        System.out.println(planFileLoc);
        planFileNameList = getFileNameList(planFileLoc, "csv");
        planFileList = getFileList(planFileLoc, "csv");

        athleteFileLoc = this.getFilesDir() + "/athlete_list/";
        athleteFileList = getFileList(athleteFileLoc, "csv");

        mScan = findViewById(R.id.scan_belt_HRM);
        mActivate = findViewById(R.id.activate_belt_HRM);
        mStart = findViewById(R.id.start_training_HRM);
        mTotalElapse = findViewById(R.id.section_time_HRM);

        mSpinnerBeltList.add(mSpinnerBelt_1 = findViewById(R.id.belt_spinner_1_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_2 = findViewById(R.id.belt_spinner_2_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_3 = findViewById(R.id.belt_spinner_3_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_4 = findViewById(R.id.belt_spinner_4_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_5 = findViewById(R.id.belt_spinner_5_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_6 = findViewById(R.id.belt_spinner_6_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_7 = findViewById(R.id.belt_spinner_7_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_8 = findViewById(R.id.belt_spinner_8_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_9 = findViewById(R.id.belt_spinner_9_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_10 = findViewById(R.id.belt_spinner_10_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_11 = findViewById(R.id.belt_spinner_11_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_12 = findViewById(R.id.belt_spinner_12_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_13 = findViewById(R.id.belt_spinner_13_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_14 = findViewById(R.id.belt_spinner_14_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_15 = findViewById(R.id.belt_spinner_15_HRM));
        mSpinnerBeltList.add(mSpinnerBelt_16 = findViewById(R.id.belt_spinner_16_HRM));

        if (tabMode == 1) {

            mSpinnerBeltList.add(mSpinnerBelt_17 = findViewById(R.id.belt_spinner_17_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_18 = findViewById(R.id.belt_spinner_18_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_19 = findViewById(R.id.belt_spinner_19_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_20 = findViewById(R.id.belt_spinner_20_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_21 = findViewById(R.id.belt_spinner_21_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_22 = findViewById(R.id.belt_spinner_22_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_23 = findViewById(R.id.belt_spinner_23_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_24 = findViewById(R.id.belt_spinner_24_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_25 = findViewById(R.id.belt_spinner_25_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_26 = findViewById(R.id.belt_spinner_26_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_27 = findViewById(R.id.belt_spinner_27_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_28 = findViewById(R.id.belt_spinner_28_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_29 = findViewById(R.id.belt_spinner_29_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_30 = findViewById(R.id.belt_spinner_30_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_31 = findViewById(R.id.belt_spinner_31_HRM));
            mSpinnerBeltList.add(mSpinnerBelt_32 = findViewById(R.id.belt_spinner_32_HRM));

        }

        mSpinnerNameList.add(mSpinnerName_1 = findViewById(R.id.name_spinner_1_HRM));
        mSpinnerNameList.add(mSpinnerName_2 = findViewById(R.id.name_spinner_2_HRM));
        mSpinnerNameList.add(mSpinnerName_3 = findViewById(R.id.name_spinner_3_HRM));
        mSpinnerNameList.add(mSpinnerName_4 = findViewById(R.id.name_spinner_4_HRM));
        mSpinnerNameList.add(mSpinnerName_5 = findViewById(R.id.name_spinner_5_HRM));
        mSpinnerNameList.add(mSpinnerName_6 = findViewById(R.id.name_spinner_6_HRM));
        mSpinnerNameList.add(mSpinnerName_7 = findViewById(R.id.name_spinner_7_HRM));
        mSpinnerNameList.add(mSpinnerName_8 = findViewById(R.id.name_spinner_8_HRM));
        mSpinnerNameList.add(mSpinnerName_9 = findViewById(R.id.name_spinner_9_HRM));
        mSpinnerNameList.add(mSpinnerName_10 = findViewById(R.id.name_spinner_10_HRM));
        mSpinnerNameList.add(mSpinnerName_11 = findViewById(R.id.name_spinner_11_HRM));
        mSpinnerNameList.add(mSpinnerName_12 = findViewById(R.id.name_spinner_12_HRM));
        mSpinnerNameList.add(mSpinnerName_13 = findViewById(R.id.name_spinner_13_HRM));
        mSpinnerNameList.add(mSpinnerName_14 = findViewById(R.id.name_spinner_14_HRM));
        mSpinnerNameList.add(mSpinnerName_15 = findViewById(R.id.name_spinner_15_HRM));
        mSpinnerNameList.add(mSpinnerName_16 = findViewById(R.id.name_spinner_16_HRM));

        if (tabMode == 1) {

            mSpinnerNameList.add(mSpinnerName_17 = findViewById(R.id.name_spinner_17_HRM));
            mSpinnerNameList.add(mSpinnerName_18 = findViewById(R.id.name_spinner_18_HRM));
            mSpinnerNameList.add(mSpinnerName_19 = findViewById(R.id.name_spinner_19_HRM));
            mSpinnerNameList.add(mSpinnerName_20 = findViewById(R.id.name_spinner_20_HRM));
            mSpinnerNameList.add(mSpinnerName_21 = findViewById(R.id.name_spinner_21_HRM));
            mSpinnerNameList.add(mSpinnerName_22 = findViewById(R.id.name_spinner_22_HRM));
            mSpinnerNameList.add(mSpinnerName_23 = findViewById(R.id.name_spinner_23_HRM));
            mSpinnerNameList.add(mSpinnerName_24 = findViewById(R.id.name_spinner_24_HRM));
            mSpinnerNameList.add(mSpinnerName_25 = findViewById(R.id.name_spinner_25_HRM));
            mSpinnerNameList.add(mSpinnerName_26 = findViewById(R.id.name_spinner_26_HRM));
            mSpinnerNameList.add(mSpinnerName_27 = findViewById(R.id.name_spinner_27_HRM));
            mSpinnerNameList.add(mSpinnerName_28 = findViewById(R.id.name_spinner_28_HRM));
            mSpinnerNameList.add(mSpinnerName_29 = findViewById(R.id.name_spinner_29_HRM));
            mSpinnerNameList.add(mSpinnerName_30 = findViewById(R.id.name_spinner_30_HRM));
            mSpinnerNameList.add(mSpinnerName_31 = findViewById(R.id.name_spinner_31_HRM));
            mSpinnerNameList.add(mSpinnerName_32 = findViewById(R.id.name_spinner_32_HRM));

        }

        mHeartRateAvgList.add(HR_dp_now_1 = findViewById(R.id.hr_avg_display_1_HRM));
        mHeartRateAvgList.add(HR_dp_now_2 = findViewById(R.id.hr_avg_display_2_HRM));
        mHeartRateAvgList.add(HR_dp_now_3 = findViewById(R.id.hr_avg_display_3_HRM));
        mHeartRateAvgList.add(HR_dp_now_4 = findViewById(R.id.hr_avg_display_4_HRM));
        mHeartRateAvgList.add(HR_dp_now_5 = findViewById(R.id.hr_avg_display_5_HRM));
        mHeartRateAvgList.add(HR_dp_now_6 = findViewById(R.id.hr_avg_display_6_HRM));
        mHeartRateAvgList.add(HR_dp_now_7 = findViewById(R.id.hr_avg_display_7_HRM));
        mHeartRateAvgList.add(HR_dp_now_8 = findViewById(R.id.hr_avg_display_8_HRM));
        mHeartRateAvgList.add(HR_dp_now_9 = findViewById(R.id.hr_avg_display_9_HRM));
        mHeartRateAvgList.add(HR_dp_now_10 = findViewById(R.id.hr_avg_display_10_HRM));
        mHeartRateAvgList.add(HR_dp_now_11 = findViewById(R.id.hr_avg_display_11_HRM));
        mHeartRateAvgList.add(HR_dp_now_12 = findViewById(R.id.hr_avg_display_12_HRM));
        mHeartRateAvgList.add(HR_dp_now_13 = findViewById(R.id.hr_avg_display_13_HRM));
        mHeartRateAvgList.add(HR_dp_now_14 = findViewById(R.id.hr_avg_display_14_HRM));
        mHeartRateAvgList.add(HR_dp_now_15 = findViewById(R.id.hr_avg_display_15_HRM));
        mHeartRateAvgList.add(HR_dp_now_16 = findViewById(R.id.hr_avg_display_16_HRM));

        if (tabMode == 1) {

            mHeartRateAvgList.add(HR_dp_now_17 = findViewById(R.id.hr_avg_display_17_HRM));
            mHeartRateAvgList.add(HR_dp_now_18 = findViewById(R.id.hr_avg_display_18_HRM));
            mHeartRateAvgList.add(HR_dp_now_19 = findViewById(R.id.hr_avg_display_19_HRM));
            mHeartRateAvgList.add(HR_dp_now_20 = findViewById(R.id.hr_avg_display_20_HRM));
            mHeartRateAvgList.add(HR_dp_now_21 = findViewById(R.id.hr_avg_display_21_HRM));
            mHeartRateAvgList.add(HR_dp_now_22 = findViewById(R.id.hr_avg_display_22_HRM));
            mHeartRateAvgList.add(HR_dp_now_23 = findViewById(R.id.hr_avg_display_23_HRM));
            mHeartRateAvgList.add(HR_dp_now_24 = findViewById(R.id.hr_avg_display_24_HRM));
            mHeartRateAvgList.add(HR_dp_now_25 = findViewById(R.id.hr_avg_display_25_HRM));
            mHeartRateAvgList.add(HR_dp_now_26 = findViewById(R.id.hr_avg_display_26_HRM));
            mHeartRateAvgList.add(HR_dp_now_27 = findViewById(R.id.hr_avg_display_27_HRM));
            mHeartRateAvgList.add(HR_dp_now_28 = findViewById(R.id.hr_avg_display_28_HRM));
            mHeartRateAvgList.add(HR_dp_now_29 = findViewById(R.id.hr_avg_display_29_HRM));
            mHeartRateAvgList.add(HR_dp_now_30 = findViewById(R.id.hr_avg_display_30_HRM));
            mHeartRateAvgList.add(HR_dp_now_31 = findViewById(R.id.hr_avg_display_31_HRM));
            mHeartRateAvgList.add(HR_dp_now_32 = findViewById(R.id.hr_avg_display_32_HRM));

        }

        mHeartRateNowList.add(HR_dp_now_1 = findViewById(R.id.hr_display_1_HRM));
        mHeartRateNowList.add(HR_dp_now_2 = findViewById(R.id.hr_display_2_HRM));
        mHeartRateNowList.add(HR_dp_now_3 = findViewById(R.id.hr_display_3_HRM));
        mHeartRateNowList.add(HR_dp_now_4 = findViewById(R.id.hr_display_4_HRM));
        mHeartRateNowList.add(HR_dp_now_5 = findViewById(R.id.hr_display_5_HRM));
        mHeartRateNowList.add(HR_dp_now_6 = findViewById(R.id.hr_display_6_HRM));
        mHeartRateNowList.add(HR_dp_now_7 = findViewById(R.id.hr_display_7_HRM));
        mHeartRateNowList.add(HR_dp_now_8 = findViewById(R.id.hr_display_8_HRM));
        mHeartRateNowList.add(HR_dp_now_9 = findViewById(R.id.hr_display_9_HRM));
        mHeartRateNowList.add(HR_dp_now_10 = findViewById(R.id.hr_display_10_HRM));
        mHeartRateNowList.add(HR_dp_now_11 = findViewById(R.id.hr_display_11_HRM));
        mHeartRateNowList.add(HR_dp_now_12 = findViewById(R.id.hr_display_12_HRM));
        mHeartRateNowList.add(HR_dp_now_13 = findViewById(R.id.hr_display_13_HRM));
        mHeartRateNowList.add(HR_dp_now_14 = findViewById(R.id.hr_display_14_HRM));
        mHeartRateNowList.add(HR_dp_now_15 = findViewById(R.id.hr_display_15_HRM));
        mHeartRateNowList.add(HR_dp_now_16 = findViewById(R.id.hr_display_16_HRM));

        if (tabMode == 1) {

            mHeartRateNowList.add(HR_dp_now_17 = findViewById(R.id.hr_display_17_HRM));
            mHeartRateNowList.add(HR_dp_now_18 = findViewById(R.id.hr_display_18_HRM));
            mHeartRateNowList.add(HR_dp_now_19 = findViewById(R.id.hr_display_19_HRM));
            mHeartRateNowList.add(HR_dp_now_20 = findViewById(R.id.hr_display_20_HRM));
            mHeartRateNowList.add(HR_dp_now_21 = findViewById(R.id.hr_display_21_HRM));
            mHeartRateNowList.add(HR_dp_now_22 = findViewById(R.id.hr_display_22_HRM));
            mHeartRateNowList.add(HR_dp_now_23 = findViewById(R.id.hr_display_23_HRM));
            mHeartRateNowList.add(HR_dp_now_24 = findViewById(R.id.hr_display_24_HRM));
            mHeartRateNowList.add(HR_dp_now_25 = findViewById(R.id.hr_display_25_HRM));
            mHeartRateNowList.add(HR_dp_now_26 = findViewById(R.id.hr_display_26_HRM));
            mHeartRateNowList.add(HR_dp_now_27 = findViewById(R.id.hr_display_27_HRM));
            mHeartRateNowList.add(HR_dp_now_28 = findViewById(R.id.hr_display_28_HRM));
            mHeartRateNowList.add(HR_dp_now_29 = findViewById(R.id.hr_display_29_HRM));
            mHeartRateNowList.add(HR_dp_now_30 = findViewById(R.id.hr_display_30_HRM));
            mHeartRateNowList.add(HR_dp_now_31 = findViewById(R.id.hr_display_31_HRM));
            mHeartRateNowList.add(HR_dp_now_32 = findViewById(R.id.hr_display_32_HRM));

        }

        mNameList = getResources().getStringArray(R.array.guest_list);
        mBeltList = getResources().getStringArray(R.array.polarID_empty);

        mPopupPolarIDList.add(mPopupPolarID_1 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_1));
        mPopupPolarIDList.add(mPopupPolarID_2 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_2));
        mPopupPolarIDList.add(mPopupPolarID_3 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_3));
        mPopupPolarIDList.add(mPopupPolarID_4 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_4));
        mPopupPolarIDList.add(mPopupPolarID_5 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_5));
        mPopupPolarIDList.add(mPopupPolarID_6 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_6));
        mPopupPolarIDList.add(mPopupPolarID_7 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_7));
        mPopupPolarIDList.add(mPopupPolarID_8 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_8));
        mPopupPolarIDList.add(mPopupPolarID_9 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_9));
        mPopupPolarIDList.add(mPopupPolarID_10 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_10));
        mPopupPolarIDList.add(mPopupPolarID_11 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_11));
        mPopupPolarIDList.add(mPopupPolarID_12 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_12));
        mPopupPolarIDList.add(mPopupPolarID_13 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_13));
        mPopupPolarIDList.add(mPopupPolarID_14 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_14));
        mPopupPolarIDList.add(mPopupPolarID_15 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_15));
        mPopupPolarIDList.add(mPopupPolarID_16 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_16));

        if (tabMode == 1) {

            mPopupPolarIDList.add(mPopupPolarID_17 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_17));
            mPopupPolarIDList.add(mPopupPolarID_18 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_18));
            mPopupPolarIDList.add(mPopupPolarID_19 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_19));
            mPopupPolarIDList.add(mPopupPolarID_20 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_20));
            mPopupPolarIDList.add(mPopupPolarID_21 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_21));
            mPopupPolarIDList.add(mPopupPolarID_22 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_22));
            mPopupPolarIDList.add(mPopupPolarID_23 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_23));
            mPopupPolarIDList.add(mPopupPolarID_24 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_24));
            mPopupPolarIDList.add(mPopupPolarID_25 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_25));
            mPopupPolarIDList.add(mPopupPolarID_26 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_26));
            mPopupPolarIDList.add(mPopupPolarID_27 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_27));
            mPopupPolarIDList.add(mPopupPolarID_28 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_28));
            mPopupPolarIDList.add(mPopupPolarID_29 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_29));
            mPopupPolarIDList.add(mPopupPolarID_30 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_30));
            mPopupPolarIDList.add(mPopupPolarID_31 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_31));
            mPopupPolarIDList.add(mPopupPolarID_32 = popupViewPresetList.findViewById(R.id.hrm_selected_polarID_32));

        }

        mPopupNameList.add(mPopupName_1 = popupViewPresetList.findViewById(R.id.hrm_selected_name_1));
        mPopupNameList.add(mPopupName_2 = popupViewPresetList.findViewById(R.id.hrm_selected_name_2));
        mPopupNameList.add(mPopupName_3 = popupViewPresetList.findViewById(R.id.hrm_selected_name_3));
        mPopupNameList.add(mPopupName_4 = popupViewPresetList.findViewById(R.id.hrm_selected_name_4));
        mPopupNameList.add(mPopupName_5 = popupViewPresetList.findViewById(R.id.hrm_selected_name_5));
        mPopupNameList.add(mPopupName_6 = popupViewPresetList.findViewById(R.id.hrm_selected_name_6));
        mPopupNameList.add(mPopupName_7 = popupViewPresetList.findViewById(R.id.hrm_selected_name_7));
        mPopupNameList.add(mPopupName_8 = popupViewPresetList.findViewById(R.id.hrm_selected_name_8));
        mPopupNameList.add(mPopupName_9 = popupViewPresetList.findViewById(R.id.hrm_selected_name_9));
        mPopupNameList.add(mPopupName_10 = popupViewPresetList.findViewById(R.id.hrm_selected_name_10));
        mPopupNameList.add(mPopupName_11 = popupViewPresetList.findViewById(R.id.hrm_selected_name_11));
        mPopupNameList.add(mPopupName_12 = popupViewPresetList.findViewById(R.id.hrm_selected_name_12));
        mPopupNameList.add(mPopupName_13 = popupViewPresetList.findViewById(R.id.hrm_selected_name_13));
        mPopupNameList.add(mPopupName_14 = popupViewPresetList.findViewById(R.id.hrm_selected_name_14));
        mPopupNameList.add(mPopupName_15 = popupViewPresetList.findViewById(R.id.hrm_selected_name_15));
        mPopupNameList.add(mPopupName_16 = popupViewPresetList.findViewById(R.id.hrm_selected_name_16));

        if (tabMode == 1) {

            mPopupNameList.add(mPopupName_17 = popupViewPresetList.findViewById(R.id.hrm_selected_name_17));
            mPopupNameList.add(mPopupName_18 = popupViewPresetList.findViewById(R.id.hrm_selected_name_18));
            mPopupNameList.add(mPopupName_19 = popupViewPresetList.findViewById(R.id.hrm_selected_name_19));
            mPopupNameList.add(mPopupName_20 = popupViewPresetList.findViewById(R.id.hrm_selected_name_20));
            mPopupNameList.add(mPopupName_21 = popupViewPresetList.findViewById(R.id.hrm_selected_name_21));
            mPopupNameList.add(mPopupName_22 = popupViewPresetList.findViewById(R.id.hrm_selected_name_22));
            mPopupNameList.add(mPopupName_23 = popupViewPresetList.findViewById(R.id.hrm_selected_name_23));
            mPopupNameList.add(mPopupName_24 = popupViewPresetList.findViewById(R.id.hrm_selected_name_24));
            mPopupNameList.add(mPopupName_25 = popupViewPresetList.findViewById(R.id.hrm_selected_name_25));
            mPopupNameList.add(mPopupName_26 = popupViewPresetList.findViewById(R.id.hrm_selected_name_26));
            mPopupNameList.add(mPopupName_27 = popupViewPresetList.findViewById(R.id.hrm_selected_name_27));
            mPopupNameList.add(mPopupName_28 = popupViewPresetList.findViewById(R.id.hrm_selected_name_28));
            mPopupNameList.add(mPopupName_29 = popupViewPresetList.findViewById(R.id.hrm_selected_name_29));
            mPopupNameList.add(mPopupName_30 = popupViewPresetList.findViewById(R.id.hrm_selected_name_30));
            mPopupNameList.add(mPopupName_31 = popupViewPresetList.findViewById(R.id.hrm_selected_name_31));
            mPopupNameList.add(mPopupName_32 = popupViewPresetList.findViewById(R.id.hrm_selected_name_32));
        }

        ArrayAdapter<String> adapter_belt;

        if (tabMode == 1) {
            adapter_belt = new ArrayAdapter(this, R.layout.my_spinner_box_tab, mBeltList);
            adapter_belt.setDropDownViewResource(R.layout.my_drop_down_red_2_tab);

        } else {

            adapter_belt = new ArrayAdapter(this, R.layout.my_spinner_box, mBeltList);
            adapter_belt.setDropDownViewResource(R.layout.my_drop_down_red_2);
        }

        for (int i = 0; i < mSpinnerBeltList.size(); i++) {
            mBeltList_selected.add("-");
        }

        for (int i = 0; i < mSpinnerNameList.size(); i++) {
            mNameList_selected.add("-");
        }

        for (int i = 0; i < mSpinnerBeltList.size(); i++) {
            mSpinnerBeltList.get(i).setAdapter(adapter_belt);
        }

        for (int i = 0; i < mNameList.length; i++) {
            mNameList_available.add(mNameList[i]);
            System.out.println(mNameList_available.get(i));
        }

        MyAdapter2 adapter_name;

        if (tabMode == 1) {
            adapter_name = new MyAdapter2(this, R.layout.my_spinner_box_tab, mNameList_available);
            adapter_name.setDropDownViewResource(R.layout.my_drop_down_gray_2_tab);

        } else {

            adapter_name = new MyAdapter2(this, R.layout.my_spinner_box, mNameList_available);
            adapter_name.setDropDownViewResource(R.layout.my_drop_down_gray_2);
        }

        for (int i = 0; i < mSpinnerNameList.size(); i++) {
            mSpinnerNameList.get(i).setAdapter(adapter_name);
        }

        for (int i = 0; i < iterMax; i++) {
            selectedAthleteList.add("");
        }

        ArrayList<String> targetNameList = getFileNameList(athleteFileLoc, "csv");

        for (int i = 0; i < athleteFileList.size(); i++) {
            Log.e("athleteFileList", targetNameList.get(i));
            Log.e("teamSelected_______", teamSelected);

            if (targetNameList.get(i).equals(teamSelected)) {
                try {
                    System.out.println("reading target file at:" + athleteFileLoc + athleteFileList.get(i));
                    CSVReader reader = new CSVReader(new FileReader(athleteFileLoc + athleteFileList.get(i)));
                    List<String[]> athleteData = reader.readAll();
                    Log.e("name", athleteData.get(0)[0]);
                    Log.e("polar", athleteData.get(0)[1]);

                    athletePolarIDListList.add(0, athleteData);

                } catch (IOException | CsvException e) {
                    e.printStackTrace();
                }
            }

        }

        List<PlayerHRM> initialListPlayerPolarID;

        if (teamSelected.equals("专用")) {

            initialListPlayerPolarID = createList(athletePolarIDListList.get(0), 2, 2);

        } else {
            initialListPlayerPolarID = createList(athletePolarIDListList.get(0), 2, 1);

        }

        PlayerPolarAdapter adapter = new PlayerPolarAdapter(HeartRateMonitor.this, R.layout.player_hrm_entry,
                initialListPlayerPolarID);
        mPlayerListHRM.setAdapter(adapter);

        mPlayerListHRM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                PlayerHRM item = initialListPlayerPolarID.get(position);

                int emptySpace = 0;
                for (int i = 0; i < iterMax; i++) {
                    if (selectedAthleteList.get(i).equals("")) {

                        emptySpace++;

                    }
                }

                if (item.getSelected()) {

                    item.setSelected(false);

                    for (int i = 0; i < iterMax; i++) {
                        if (selectedAthleteList.get(i).equals(initialListPlayerPolarID.get(position).getName())) {
                            selectedAthleteList.set(i, "");
                            mPopupNameList.get(i).setText("");
                            mPopupPolarIDList.get(i).setText("");
                            mPopupNameList.get(i).setAlpha(0f);
                            mPopupPolarIDList.get(i).setAlpha(0f);
                            break;
                        }
                    }

                } else if (!item.getSelected() && emptySpace > 0) {
                    item.setSelected(true);
                    for (int i = 0; i < iterMax; i++) {
                        if (selectedAthleteList.get(i).equals("")) {
                            selectedAthleteList.set(i, initialListPlayerPolarID.get(position).getName());
                            mPopupNameList.get(i).setText(initialListPlayerPolarID.get(position).getName());
                            mPopupPolarIDList.get(i).setText(initialListPlayerPolarID.get(position).getPolarID());
                            mPopupNameList.get(i).setAlpha(1f);
                            mPopupPolarIDList.get(i).setAlpha(1f);
                            break;
                        }
                    }
                }
                adapter.notifyDataSetInvalidated();
            }
        });

        for (int i = 0; i < planFileNameList.size(); i++) {

            System.out.println("creating plan list");
            try {
                System.out.println(planFileLoc + planFileList.get(i));
                System.out.println(planFileNameList.get(i));
                CSVReader reader = new CSVReader(new FileReader(planFileLoc + planFileList.get(i)));
                List<String[]> planData = reader.readAll();
                athletePolarIDPlanList.add(i, planData);

                int size = 0;
                for (int k = 0; k < planData.size(); k++) {

                    if (!planData.get(k)[0].equals("")) {
                        size++;
                    }
                }

                planList.add(i, new String[] { planFileNameList.get(i), "人数:" + String.valueOf(size) });
                Log.e("plan has been added:", String.valueOf(size));

            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        List<PlayerHRM> initialListPlan = createList(planList, 2, 1);
        PlayerPolarAdapter adapterPlan = new PlayerPolarAdapter(HeartRateMonitor.this, R.layout.player_hrm_entry,
                initialListPlan);
        mPlanListHRM.setAdapter(adapterPlan);

        mPlanListHRM.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                lastCheckedPlanNumber = position;

                List<PlayerHRM> initialListPlan_inner = createList(planList, 2, 1);
                PlayerHRM item = initialListPlan_inner.get(position);

                for (int i = 0; i < initialListPlan_inner.size(); i++) {
                    PlayerHRM TempItem = initialListPlan_inner.get(i);
                    TempItem.setSelected(false);
                }

                if (!item.getSelected()) {
                    item.setSelected(true);
                }

                PlayerPolarAdapter adapterPlan_inner = new PlayerPolarAdapter(HeartRateMonitor.this,
                        R.layout.player_hrm_entry, initialListPlan_inner);
                mPlanListHRM.setAdapter(adapterPlan_inner);
                adapterPlan.notifyDataSetInvalidated();

                selectedAthleteList = new ArrayList<>();
                for (int i = 0; i < iterMax; i++) {
                    selectedAthleteList.add("");
                }

                List<String[]> planData = athletePolarIDPlanList.get(position);

                for (int i = 0; i < initialListPlayerPolarID.size(); i++) {

                    initialListPlayerPolarID.get(i).setSelected(false);

                }

                for (int i = 0; i < iterMax; i++) {
                    mPopupNameList.get(i).setText("");
                    mPopupPolarIDList.get(i).setText("");
                    mPopupNameList.get(i).setAlpha(0f);
                    mPopupPolarIDList.get(i).setAlpha(0f);
                }

                for (int i = 0; i < planData.size(); i++) {

                    if (!planData.get(i)[0].equals("")) {

                        for (int k = 0; k < initialListPlayerPolarID.size(); k++) {
                            // System.out.println("testing:" + planData.get(i)[0] + "vs" +
                            // initialListPlayerPolarID.get(k).getName()+ "///" +
                            // planData.get(i)[0].equals(initialListPlayerPolarID.get(k).getName()));

                            if (StringUtils.equals(planData.get(i)[0].replaceAll("\\p{C}", ""),
                                    initialListPlayerPolarID.get(k).getName())) {
                                initialListPlayerPolarID.get(k).setSelected(true);

                            }
                        }
                        mPopupNameList.get(i).setText(planData.get(i)[0]);
                        mPopupPolarIDList.get(i).setText(planData.get(i)[1]);
                        mPopupNameList.get(i).setAlpha(1f);
                        mPopupPolarIDList.get(i).setAlpha(1f);
                        selectedAthleteList.set(i, planData.get(i)[0]);
                    }
                }
                adapter.notifyDataSetInvalidated();
            }

        });

        mPopupReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                lastCheckedPlanNumber = -1;

                for (int i = 0; i < iterMax; i++) {
                    selectedAthleteList.set(i, "");
                    mPopupNameList.get(i).setText("");
                    mPopupPolarIDList.get(i).setText("");
                    mPopupNameList.get(i).setAlpha(0f);
                    mPopupPolarIDList.get(i).setAlpha(0f);
                }

                for (int i = 0; i < initialListPlayerPolarID.size(); i++) {
                    initialListPlayerPolarID.get(i).setSelected(false);
                }

                adapter.notifyDataSetInvalidated();

                List<PlayerHRM> initialListPlan_inner = createList(planList, 2, 1);
                PlayerPolarAdapter adapterPlan_inner = new PlayerPolarAdapter(HeartRateMonitor.this,
                        R.layout.player_hrm_entry, initialListPlan_inner);
                mPlanListHRM.setAdapter(adapterPlan_inner);

                for (int i = 0; i < initialListPlan_inner.size(); i++) {
                    initialListPlan_inner.get(i).setSelected(false);
                }

                adapterPlan_inner.notifyDataSetInvalidated();
            }
        });

        mPopupConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (teamSelected.equals("专用")) {

                    // mBeltList = getResources().getStringArray(R.array.polarID_GD_Male);

                    mBeltList = new String[athletePolarIDListList.get(0).size() + 1];
                    mBeltList[0] = "-";

                    for (int i = 0; i < athletePolarIDListList.get(0).size() - 1; i++) {
                        System.out.println(athletePolarIDListList.get(0).get(i + 1)[2]);

                        mBeltList[i + 1] = athletePolarIDListList.get(0).get(i + 1)[2];

                    }
                }

                mBeltList_available.clear();

                for (int i = 0; i < mBeltList.length - 1; i++) {

                    mBeltList_available.add(mBeltList[i]);

                }

                MyAdapter adapter_belt;

                if (tabMode == 1) {
                    adapter_belt = new MyAdapter(HeartRateMonitor.this, R.layout.my_spinner_box_tab,
                            mBeltList_available);
                    adapter_belt.setDropDownViewResource(R.layout.my_drop_down_red_2_tab);

                } else {

                    adapter_belt = new MyAdapter(HeartRateMonitor.this, R.layout.my_spinner_box, mBeltList_available);
                    adapter_belt.setDropDownViewResource(R.layout.my_drop_down_red_2);
                }

                if (teamSelected.equals("专用")) {

                    // mNameList = getResources().getStringArray(R.array.AthleteName_GD_Male);

                    mNameList = new String[athletePolarIDListList.get(0).size() + 1];
                    mNameList[0] = "-";

                    for (int i = 0; i < athletePolarIDListList.get(0).size() - 1; i++) {

                        mNameList[i + 1] = athletePolarIDListList.get(0).get(i + 1)[1];
                    }
                }

                mNameList_available.clear();

                for (int i = 0; i < mNameList.length; i++) {
                    mNameList_available.add(mNameList[i]);
                }

                MyAdapter2 adapter_name;
                if (tabMode == 1) {
                    adapter_name = new MyAdapter2(HeartRateMonitor.this, R.layout.my_spinner_box_tab,
                            mNameList_available);
                    adapter_name.setDropDownViewResource(R.layout.my_drop_down_gray_2_tab);

                } else {

                    adapter_name = new MyAdapter2(HeartRateMonitor.this, R.layout.my_spinner_box, mNameList_available);
                    adapter_name.setDropDownViewResource(R.layout.my_drop_down_gray_2);
                }

                mNameList_passed = new ArrayList<>();
                for (int i = 0; i < mPopupNameList.size(); i++) {
                    mNameList_passed.add((String) mPopupNameList.get(i).getText());
                    System.out.println((String) mPopupNameList.get(i).getText());

                }

                System.out.println("still alive");

                for (int i = 0; i < mSpinnerNameList.size(); i++) {

                    mSpinnerBeltList.get(i).setAdapter(adapter_belt);
                    mSpinnerNameList.get(i).setAdapter(adapter_name);

                    for (int j = 0; j < mNameList_available.size(); j++) {

                        // System.out.println("testing:" + mNameList_passed.get(i) + "vs" +
                        // mNameList_available.get(j) + "///" +
                        // mNameList_passed.get(i).equals(mNameList_available.get(j)));

                        if (mNameList_passed.get(i).replaceAll("\\p{C}", "").equals(mNameList_available.get(j))) {

                            mSpinnerNameList.get(i).setSelection(j);
                            mSpinnerBeltList.get(i).setSelection(j);

                        } else if (mNameList_passed.get(i).replaceAll("\\p{C}", "").equals("")) {

                            mSpinnerNameList.get(i).setSelection(0);
                            mSpinnerBeltList.get(i).setSelection(0);

                        }

                    }

                }

                System.out.println("still alive 2");
                resetList();
                mWindowPresetList.dismiss();
            }
        });

        // ArrayAdapter<String> adapter_name;
        // adapter_name = new ArrayAdapter(this, R.layout.my_spinner_box,mNameList);
        // adapter_name.setDropDownViewResource(R.layout.my_drop_down_gray_2);
        // mSpinnerName_1.setAdapter(adapter_name);
        // mSpinnerBelt_1.setAdapter(adapter_belt);

        mLoadPreset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showPopup(mWindowPresetList);
                backgroundAlpha(0.2f);

            }
        });

        mPopupAddPlan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new androidx.appcompat.app.AlertDialog.Builder(HeartRateMonitor.this).setTitle("系统提示")
                        .setMessage("确认要添加新的名单组合吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                createListPlan(adapterPlan);

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).show();

            }
        });

        mPopupDeletePlan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new androidx.appcompat.app.AlertDialog.Builder(HeartRateMonitor.this).setTitle("系统提示")
                        .setMessage("确认要删除当前名单组合吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deleteListPlan(adapterPlan, lastCheckedPlanNumber);

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).show();

            }
        });

        mResetSpinner.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new androidx.appcompat.app.AlertDialog.Builder(HeartRateMonitor.this).setTitle("系统提示")
                        .setMessage("确认清空选择列表吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                resetSpinner();

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).show();
            }
        });

        mStart.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                if (mStartStatus == 0) {

                    mStartStatus = 1;
                    mTotalElapse.setBase(SystemClock.elapsedRealtime());
                    mTotalElapse.start();
                    mStart.setBackgroundResource(R.drawable.button_6);
                    mStart.setText("停止训练");

                    for (int i = 0; i < iterMax; i++) {

                        mSpinnerBeltList.get(i).setEnabled(false);
                        mSpinnerNameList.get(i).setEnabled(false);
                    }

                    mLoadPreset.setEnabled(false);
                    mResetSpinner.setEnabled(false);
                    mScan.setEnabled(false);

                } else {

                    mStartStatus = 0;
                    mTotalElapse.stop();
                    mTotalElapse.setBase(SystemClock.elapsedRealtime());
                    mStart.setBackgroundResource(R.drawable.button_1);
                    mStart.setText("开始训练");

                    for (int i = 0; i < iterMax; i++) {

                        mSpinnerBeltList.get(i).setEnabled(true);
                        mSpinnerNameList.get(i).setEnabled(true);
                    }

                    mLoadPreset.setEnabled(true);
                    mResetSpinner.setEnabled(true);
                    mScan.setEnabled(true);

                }

                return true;
            }
        });

        mScan.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                if (checkBluetoothValid() == 1) {

                    if (!mIsScanning) {
                        mIsScanning = true;
                        // mBeltList_available.clear();
                        // mBeltList_available.add("-");
                        initPolar();
                    } else {
                        mIsScanning = false;
                        initPolar();
                    }
                    mScan.setText(mIsScanning ? "结束扫描" : "开始扫描");
                }
                ;
                return true;
            }
        });

        mActivate.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                resetList();
                mountPolar(1);
                updateUI(1);
                return true;
            }
        });

        for (int i = 0; i < mSpinnerBeltList.size(); i++) {

            int finalI = i;
            mSpinnerBeltList.get(i).setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                        int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    if (mBeltList_available.size() >= 1 && arg2 > 0) {
                        String name = String.valueOf(mSpinnerBeltList.get(finalI).getSelectedItem());
                        mBeltList_selected.set(finalI, name);
                        deleteDuplicate(finalI, name, mSpinnerBeltList, mBeltList_selected, 1);

                    }
                    if (arg2 == 0) {

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }

        for (int i = 0; i < mSpinnerNameList.size(); i++) {

            int finalI = i;
            mSpinnerNameList.get(i).setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                        int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    if (mSpinnerNameList.size() >= 1 && arg2 > 0) {
                        String name = String.valueOf(mSpinnerNameList.get(finalI).getSelectedItem());
                        mNameList_selected.set(finalI, name);
                        Log.e("name", name);
                        deleteDuplicate(finalI, name, mSpinnerNameList, mNameList_selected, 2);
                    }
                    if (arg2 == 0) {

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });
        }

    }

    private void updateUI(int activationStatus) {

        if (activationStatus == 1) {
            TimerManager.getInstance().scheduleAtFixedRate("updateUI_HR", new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUI_sub_HR();
                        }
                    });
                }
            }, 0, 2000);

        } else {
            TimerManager.getInstance().cancelTask("updateUI_HR");
        }

    }

    private void updateUI_sub_HR() {

        for (int i = 0; i < mHeartRateNowList.size(); i++) {

            if (HR_params_cache[i] == null || HR_params_cache[i] == 0) {

                mHeartRateNowList.get(i).setText("-");

            } else {

                mHeartRateNowList.get(i).setText(String.valueOf(HR_params_cache[i]));
                if (HR_params_cache[i] <= zone0) {

                    mHeartRateNowList.get(i).setBackgroundResource(R.color.colorGrayDark);

                } else if (zone0 < HR_params_cache[i] && HR_params_cache[i] <= zone1) {

                    mHeartRateNowList.get(i).setBackgroundResource(R.color.colorGrayDark);

                } else if (zone1 < HR_params_cache[i] && HR_params_cache[i] <= zone2) {

                    mHeartRateNowList.get(i).setBackgroundResource(R.color.colorBlueLight);

                } else if (zone2 < HR_params_cache[i] && HR_params_cache[i] <= zone3) {

                    mHeartRateNowList.get(i).setBackgroundResource(R.color.colorGreenDark);

                } else if (zone3 < HR_params_cache[i] && HR_params_cache[i] <= zone4) {

                    mHeartRateNowList.get(i).setBackgroundResource(R.color.colorOrangeDark);

                } else if (zone4 < HR_params_cache[i] && HR_params_cache[i] <= zone5) {

                    mHeartRateNowList.get(i).setBackgroundResource(R.color.colorRedDark);

                } else if (zone5 < HR_params_cache[i]) {

                    mHeartRateNowList.get(i).setBackgroundResource(R.color.colorRedDark);

                }

            }

        }

    }

    private void initPolar() {

        mBeltList_available.add("-");

        if (scanDisposable == null) {

            scanDisposable = api.searchForDevice()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            polarDeviceInfo -> addHRtoList(polarDeviceInfo.getDeviceId()),
                            throwable -> Log.d(TAG, "" + throwable.getLocalizedMessage()),
                            () -> Log.d(TAG, "complete"));

        } else {
            scanDisposable.dispose();
            scanDisposable = null;

        }

        System.out.println("polar initiation complete");

    }

    private void addHRtoList(String id) {

        if (!mBeltList_available.contains(id) && id != "") {

            System.out.println(id);

            mBeltList_available.add(id);
            // ArrayAdapter<String> adapter_belt;
            //
            // adapter_belt = new ArrayAdapter(getApplicationContext(),
            // R.layout.my_spinner_box, mBeltList_available);
            // adapter_belt.setDropDownViewResource(R.layout.my_drop_down_red_2);

            MyAdapter adapter_belt;

            if (tabMode == 1) {

                adapter_belt = new MyAdapter(getApplicationContext(), R.layout.my_spinner_box_tab, mBeltList_available);
                adapter_belt.setDropDownViewResource(R.layout.my_drop_down_red_2_tab);

            } else {

                adapter_belt = new MyAdapter(getApplicationContext(), R.layout.my_spinner_box, mBeltList_available);
                adapter_belt.setDropDownViewResource(R.layout.my_drop_down_red_2);

            }

            System.out.println("totalSize:" + mSpinnerBeltList.size());

            for (int i = 0; i < mSpinnerBeltList.size(); i++) {

                int selectedPos = mSpinnerBeltList.get(i).getSelectedItemPosition();
                mSpinnerBeltList.get(i).setAdapter(adapter_belt);
                mSpinnerBeltList.get(i).setSelection(selectedPos);

            }

        }

    }

    private void mountPolar(int code) {

        if (code == 1) {

            if (broadcastDisposable == null) {
                broadcastDisposable = api.startListenForPolarHrBroadcasts(null)
                        .subscribe(polarBroadcastData -> polarDataRefresh(
                                polarBroadcastData.getPolarDeviceInfo().getDeviceId(), polarBroadcastData.getHr()),
                                error -> Log.e(TAG, "Broadcast listener failed. Reason " + error),
                                () -> Log.d(TAG, "complete"));
            }

        } else if (code == 0) {

            if (broadcastDisposable != null) {
                broadcastDisposable.dispose();
                broadcastDisposable = null;

            }
        }
    }

    private void polarDataRefresh(String id, int hr) {

        for (int i = 0; i < mBeltList_selected.size(); i++) {
            if (id.equals(mBeltList_selected.get(i))) {
                HR_params_cache[i] = hr;
                System.out.println("_________HR BROADCAST_________:" + i + "____HR:____ " + hr);

            }
        }
    }

    private void deleteDuplicate(int pos, String selected, ArrayList<Spinner> spinnerList, ArrayList<String> targetList,
            int mode) {

        for (int i = 0; i < iterMax; i++) {

            if (i != pos && targetList.get(i) == selected && mode == 1) {
                spinnerList.get(i).setSelection(0);
                mBeltList_selected.set(i, null);
                HR_params_cache[i] = null;
            } else if (i != pos && targetList.get(i) == selected && mode == 2) {
                spinnerList.get(i).setSelection(0);
                mNameList_selected.set(i, null);

            }
        }
    }

    private int checkBluetoothValid() {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        int bluetoothStatus = 0;
        if (adapter == null) {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("错误").setMessage("你的设备不具备蓝牙功能!").create();
            dialog.show();
            bluetoothStatus = 0;
        }

        if (!adapter.isEnabled()) {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("蓝牙设备未打开,请开启此功能后重试!")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            if (ActivityCompat.checkSelfPermission(HeartRateMonitor.this,
                                    android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
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

        for (int i = 0; i < mHeartRateNowList.size(); i++) {

            mHeartRateNowList.get(i).setText("-");
            mHeartRateNowList.get(i).setBackgroundResource(R.drawable.input_box_6);

        }
        System.out.println("still alive 3");

    }

    public class MyAdapter extends ArrayAdapter<ArrayList[]> {

        public MyAdapter(Context context, int textViewResourceId,
                ArrayList objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            LayoutInflater inflater = getLayoutInflater();

            View spinnerItem;
            TextView myText;

            if (tabMode == 1) {
                spinnerItem = inflater.inflate(R.layout.my_drop_down_red_2_tab, null, false);
                myText = spinnerItem.findViewById(R.id.tv_drop_down_red_2_tab);
                myText.setHeight(75);
                myText.setTextSize(15f);

            } else {
                spinnerItem = inflater.inflate(R.layout.my_drop_down_red_2, null, false);
                myText = spinnerItem.findViewById(R.id.tv_drop_down_red_2);
                myText.setHeight(75);
                myText.setTextSize(12f);
            }

            myText.setText(mBeltList_available.get(position));

            for (int i = 0; i < mSpinnerBeltList.size(); i++) {
                int selected = mSpinnerBeltList.get(i).getSelectedItemPosition();
                if (position == selected && position != 0) {
                    spinnerItem.setBackgroundResource(R.drawable.gradient_orange);
                    myText.setTextColor(Color.DKGRAY);
                }

            }

            return spinnerItem;

        }

    }

    public class MyAdapter2 extends ArrayAdapter<ArrayList[]> {

        public MyAdapter2(Context context, int textViewResourceId,
                ArrayList objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
            LayoutInflater inflater = getLayoutInflater();

            View spinnerItem;
            TextView myText;

            if (tabMode == 1) {
                spinnerItem = inflater.inflate(R.layout.my_drop_down_gray_2_tab, null, false);
                myText = spinnerItem.findViewById(R.id.tv_drop_down_gray_2_tab);
                myText.setHeight(75);
                myText.setTextSize(15f);

            } else {
                spinnerItem = inflater.inflate(R.layout.my_drop_down_gray, null, false);
                myText = spinnerItem.findViewById(R.id.tv_drop_down_gray);
                myText.setHeight(75);
                myText.setTextSize(12f);
            }

            myText.setText(mNameList_available.get(position));

            for (int i = 0; i < mSpinnerNameList.size(); i++) {
                int selected = mSpinnerNameList.get(i).getSelectedItemPosition();
                if (position == selected && position != 0) {
                    spinnerItem.setBackgroundResource(R.drawable.gradient_orange);
                    myText.setTextColor(Color.DKGRAY);
                }

            }

            return spinnerItem;

        }

    }

    private ArrayList<String> getFileList(String fileAbsolutePath, String type) {
        ArrayList<String> result = new ArrayList<String>();
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

    private ArrayList<String> getFileNameList(String fileAbsolutePath, String type) {
        ArrayList<String> result = new ArrayList<String>();
        File file = new File(fileAbsolutePath);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (!files[i].isDirectory()) {
                String fileName = files[i].getName();
                if (fileName.trim().toLowerCase().endsWith(type)) {
                    int dot = fileName.lastIndexOf('.');
                    result.add(fileName.substring(0, dot));
                }
            }
        }
        return result;
    }

    private void resetSpinner() {

        mBeltList_available.clear();
        mNameList_available.clear();

        mBeltList = getResources().getStringArray(R.array.polarID_empty);
        mNameList = getResources().getStringArray(R.array.guest_list);

        for (int i = 0; i < mNameList.length; i++) {
            mNameList_available.add(mNameList[i]);
        }

        for (int i = 0; i < mBeltList.length; i++) {
            mBeltList_available.add(mBeltList[i]);

        }

        MyAdapter adapter_belt;
        MyAdapter2 adapter_name;

        if (tabMode == 1) {

            adapter_belt = new MyAdapter(HeartRateMonitor.this, R.layout.my_spinner_box_tab, mBeltList_available);
            adapter_belt.setDropDownViewResource(R.layout.my_drop_down_red_2_tab);

            adapter_name = new MyAdapter2(HeartRateMonitor.this, R.layout.my_spinner_box_tab, mNameList_available);
            adapter_name.setDropDownViewResource(R.layout.my_drop_down_gray_2_tab);

        } else {

            adapter_belt = new MyAdapter(HeartRateMonitor.this, R.layout.my_spinner_box, mBeltList_available);
            adapter_belt.setDropDownViewResource(R.layout.my_drop_down_red_2);

            adapter_name = new MyAdapter2(HeartRateMonitor.this, R.layout.my_spinner_box, mNameList_available);
            adapter_name.setDropDownViewResource(R.layout.my_drop_down_gray_2);

        }

        for (int i = 0; i < mSpinnerNameList.size(); i++) {

            mSpinnerBeltList.get(i).setAdapter(adapter_belt);
            mSpinnerNameList.get(i).setAdapter(adapter_name);
        }

    }

    private void showPopup(final PopupWindow popupWindow) {

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);

        View parentView = LayoutInflater.from(HeartRateMonitor.this).inflate(R.layout.activity_row_monitor, null);
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {

                backgroundAlpha(1f);

            }

        });
    }

    private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }

    private List<PlayerHRM> createList(List<String[]> data, int colNum, int mode) {

        List<PlayerHRM> targetList = new ArrayList<>();

        if (mode == 1) {
            for (int i = 0; i < data.size(); i++) {

                if (colNum == 2) {
                    PlayerHRM entry = new PlayerHRM(R.drawable.input_box_6, data.get(i)[0], data.get(i)[1], false);
                    targetList.add(entry);

                } else {

                    PlayerHRM entry = new PlayerHRM(R.drawable.input_box_6, data.get(i)[0], "", false);
                    targetList.add(entry);

                }

            }

        } else if (mode == 2) {

            for (int i = 1; i < data.size(); i++) {

                if (colNum == 2) {
                    PlayerHRM entry = new PlayerHRM(R.drawable.input_box_6, data.get(i)[1], data.get(i)[2], false);
                    targetList.add(entry);
                } else {

                    PlayerHRM entry = new PlayerHRM(R.drawable.input_box_6, data.get(i)[0], "", false);
                    targetList.add(entry);

                }

            }

        }

        return targetList;
    }

    private List<PlayerHRM> createListPlan(PlayerPolarAdapter adapter) {

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String planFileLoc = this.getFilesDir() + "/hrm_plan_list/" + teamSelected + "/";
        // String filename = "方案" + (planFileList.size()+1) + "_" + month+ "月" + day +
        // "日" +"_" + version + ".csv";
        String filename = month + "月" + day + "_" + hour + "_" + minute + "_" + second + ".csv";

        String writeTarget = planFileLoc + filename;

        try {

            final FileWriter nameListFileWriter = new FileWriter(writeTarget);
            final CSVWriter nameListCSVWriter = new CSVWriter(nameListFileWriter);

            for (int i = 0; i < iterMax; i++) {

                String[] data = new String[] { String.valueOf(mPopupNameList.get(i).getText()),
                        (String) mPopupPolarIDList.get(i).getText() };
                System.out.println(data[0] + data[1]);
                nameListCSVWriter.writeNext(data);
            }

            nameListCSVWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        planList.clear();
        athletePolarIDPlanList = new ArrayList<>();

        planFileNameList = getFileNameList(planFileLoc, "csv");
        planFileList = getFileList(planFileLoc, "csv");

        for (int i = 0; i < planFileNameList.size(); i++) {
            try {

                CSVReader reader = new CSVReader(new FileReader(planFileLoc + planFileList.get(i)));
                List<String[]> planData = reader.readAll();
                athletePolarIDPlanList.add(i, planData);

                int size = 0;
                for (int k = 0; k < planData.size(); k++) {

                    if (!planData.get(k)[0].equals("")) {
                        size++;
                    }
                }

                planList.add(i, new String[] { planFileNameList.get(i), "人数:" + String.valueOf(size) });

            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        lastCheckedPlanNumber = -1;

        List<PlayerHRM> initialListPlan_inner = createList(planList, 2, 1);
        PlayerPolarAdapter adapterPlan_inner = new PlayerPolarAdapter(HeartRateMonitor.this, R.layout.player_hrm_entry,
                initialListPlan_inner);
        mPlanListHRM.setAdapter(adapterPlan_inner);
        adapter.notifyDataSetInvalidated();

        return initialListPlan_inner;

    }

    private List<PlayerHRM> deleteListPlan(PlayerPolarAdapter adapter, int selectedPos) {

        String planFileLoc = this.getFilesDir() + "/hrm_plan_list/" + teamSelected + "/";
        String filename = "";

        if (selectedPos >= 0 && selectedPos < planList.size()) {
            System.out.println("item_selected_number:" + selectedPos);
            System.out.println();
            filename = planList.get(selectedPos)[0] + ".csv";

        }

        String deleteTarget = planFileLoc + filename;
        deleteLog(deleteTarget);

        planList.clear();
        athletePolarIDPlanList = new ArrayList<>();
        planFileNameList = getFileNameList(planFileLoc, "csv");
        planFileList = getFileList(planFileLoc, "csv");

        for (int i = 0; i < planFileNameList.size(); i++) {
            try {

                CSVReader reader = new CSVReader(new FileReader(planFileLoc + planFileList.get(i)));
                List<String[]> planData = reader.readAll();
                athletePolarIDPlanList.add(i, planData);

                int size = 0;
                for (int k = 0; k < planData.size(); k++) {

                    if (!planData.get(k)[0].equals("")) {
                        size++;
                    }
                }

                planList.add(i, new String[] { planFileNameList.get(i), "人数:" + String.valueOf(size) });

            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        List<PlayerHRM> initialListPlan_inner = createList(planList, 2, 1);
        PlayerPolarAdapter adapterPlan_inner = new PlayerPolarAdapter(HeartRateMonitor.this, R.layout.player_hrm_entry,
                initialListPlan_inner);
        mPlanListHRM.setAdapter(adapterPlan_inner);
        adapter.notifyDataSetInvalidated();

        lastCheckedPlanNumber = -1;

        return initialListPlan_inner;

    }

    private void deleteLog(String filePath) {

        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            file.delete();
        }

    }

}
