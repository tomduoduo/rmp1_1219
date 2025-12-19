package com.motionrivalry.rowmasterpro;

import static com.motionrivalry.rowmasterpro.MainActivity.isPad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanSettings;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.motionrivalry.rowmasterpro.UtilsBle.BleConnect;
import com.motionrivalry.rowmasterpro.UtilsBle.BleScanner;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.xsens.dot.android.sdk.events.XsensDotData;
import com.xsens.dot.android.sdk.interfaces.XsensDotDeviceCallback;
import com.xsens.dot.android.sdk.interfaces.XsensDotScannerCallback;
import com.xsens.dot.android.sdk.models.FilterProfileInfo;
import com.xsens.dot.android.sdk.models.XsensDotDevice;
import com.xsens.dot.android.sdk.utils.XsensDotScanner;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

import com.polar.sdk.api.PolarBleApi;
import com.polar.sdk.api.PolarBleApiDefaultImpl;

//import polar.com.sdk.api.PolarBleApi;
//import polar.com.sdk.api.PolarBleApiDefaultImpl;

public class BleConnector extends AppCompatActivity implements XsensDotScannerCallback, XsensDotDeviceCallback {

    private Spinner spinnerL1;
    private Spinner spinnerL2;
    private Spinner spinnerL3;
    private Spinner spinnerL4;
    private Spinner spinnerR1;
    private Spinner spinnerR2;
    private Spinner spinnerR3;
    private Spinner spinnerR4;
    private Spinner spinnerBOAT;

    private String[] mItems;
    private XsensDotScanner mXsDotScanner;
    private Button mScan;
    private Button mConnect;
    private Button mPowerOff;

    private ArrayList<BluetoothDevice> mScannedSensorList = new ArrayList<>();
    private ArrayList<String> mNameList = new ArrayList<>();
    private ArrayList<TextView> mTvNameList = new ArrayList<>();
    private ArrayList<TextView> mTvPosList = new ArrayList<>();
    private ArrayList<String> mSpinnerSelected = new ArrayList<>();
    private ArrayList<Spinner> mSpinnerList = new ArrayList<>();

    private ArrayList<BluetoothDevice> mSelectedSensorList = new ArrayList<>();

    private boolean mIsScanning = false;
    private TextView mDotsCount;
    private TextView mDot1;
    private TextView mDot2;
    private TextView mDot3;
    private TextView mDot4;
    private TextView mDot5;
    private TextView mDot6;
    private TextView mDot7;
    private TextView mDot8;
    private TextView mDot9;
    private TextView mDot10;

    private TextView mDot1Pos;
    private TextView mDot2Pos;
    private TextView mDot3Pos;
    private TextView mDot4Pos;
    private TextView mDot5Pos;
    private TextView mDot6Pos;
    private TextView mDot7Pos;
    private TextView mDot8Pos;
    private TextView mDot9Pos;
    private TextView mDot10Pos;


    private BluetoothDevice DotL1;
    private BluetoothDevice DotL2;
    private BluetoothDevice DotL3;
    private BluetoothDevice DotL4;
    private BluetoothDevice DotR1;
    private BluetoothDevice DotR2;
    private BluetoothDevice DotR3;
    private BluetoothDevice DotR4;
    private BluetoothDevice DotBOAT;

    private String username;
    private String password;

    private ArrayList<XsensDotDevice> mDeviceList = new ArrayList<>();

    private BleScanner scannerHR;
    private ArrayList<BluetoothDevice> mDeviceListHR = new ArrayList<>();
    private ArrayList<String> mNameListBelt = new ArrayList<>();

    private Spinner spinnerHR1;
    private Spinner spinnerHR2;
    private Spinner spinnerHR3;
    private Spinner spinnerHR4;
    private Spinner spinnerHR5;
    private Spinner spinnerHR6;
    private Spinner spinnerHR7;
    private Spinner spinnerHR8;

    private BluetoothDevice Belt1;
    private BluetoothDevice Belt2;
    private BluetoothDevice Belt3;
    private BluetoothDevice Belt4;
    private BluetoothDevice Belt5;
    private BluetoothDevice Belt6;
    private BluetoothDevice Belt7;
    private BluetoothDevice Belt8;

    private String BeltName1;
    private String BeltName2;
    private String BeltName3;
    private String BeltName4;
    private String BeltName5;
    private String BeltName6;
    private String BeltName7;
    private String BeltName8;
    private ArrayList<String> BeltNameList = new ArrayList<>();


    private TextView mBelt1Pos;
    private TextView mBelt2Pos;
    private TextView mBelt3Pos;
    private TextView mBelt4Pos;
    private TextView mBelt5Pos;
    private TextView mBelt6Pos;
    private TextView mBelt7Pos;
    private TextView mBelt8Pos;

    private ArrayList<TextView> mTvBeltPosList = new ArrayList<>();

    private TextView mBeltCount;

    private String[] mItemsHR;
    private ArrayList<String> mSpinnerSelectedBelt = new ArrayList<>();
    private ArrayList<Spinner> mSpinnerListBelt = new ArrayList<>();
    private ArrayList<BluetoothDevice> mSelectedBeltList = new ArrayList<>();
    private ArrayList<String> mSelectedBeltNameList = new ArrayList<>();

    private int tabMode;
    private SwitchButton reverseButton;
    private int reverseMode = 0;
    private int mSelectedDots = 0;
    private int mSelectedBelts = 0;

    private static final String TAG = MainActivity.class.getSimpleName();
    private PolarBleApi api;
    Disposable scanDisposable;

    private Spinner spinner1_name;
    private Spinner spinner2_name;
    private Spinner spinner3_name;
    private Spinner spinner4_name;
    private Spinner spinner5_name;
    private Spinner spinner6_name;
    private Spinner spinner7_name;
    private Spinner spinner8_name;
    private SwitchButton name_hrm_switch;

    private ArrayList<Spinner> mSpinnerListName = new ArrayList<>();

    private ArrayList<String> mSelectedAtheleteName = new ArrayList<>();
    private ArrayList<String> mSelectedAtheleteID = new ArrayList<>();

    private ArrayList<String> mSpinnerSelectedList = new ArrayList<>();


    private FrameLayout mContainer1_hrm;
    private FrameLayout mContainer2_hrm;
    private FrameLayout mContainer3_hrm;
    private FrameLayout mContainer4_hrm;
    private FrameLayout mContainer5_hrm;
    private FrameLayout mContainer6_hrm;
    private FrameLayout mContainer7_hrm;
    private FrameLayout mContainer8_hrm;

    private FrameLayout mContainer1_name;
    private FrameLayout mContainer2_name;
    private FrameLayout mContainer3_name;
    private FrameLayout mContainer4_name;
    private FrameLayout mContainer5_name;
    private FrameLayout mContainer6_name;
    private FrameLayout mContainer7_name;
    private FrameLayout mContainer8_name;

    private FrameLayout mContainer_L1;
    private FrameLayout mContainer_L2;
    private FrameLayout mContainer_L3;
    private FrameLayout mContainer_L4;
    private FrameLayout mContainer_R1;
    private FrameLayout mContainer_R2;
    private FrameLayout mContainer_R3;
    private FrameLayout mContainer_R4;


    private ArrayList<String> athleteNameList = new ArrayList<>();
    private ArrayList<String> athleteIDList = new ArrayList<>();

    private ArrayList<String> athleteHRMList = new ArrayList<>();

    private String athleteNamePath;
    private String boatType;

    private SwitchButton sideSwitchButton;

    private String sideReverse = "";

    private String Lang = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        Intent intent = getIntent();
        boatType = intent.getStringExtra("boatType");
        Lang = intent.getStringExtra("lang");
        System.out.println(Lang);

        final float density = getResources().getDisplayMetrics().density;


        if (metrics.xdpi <= 250 || isPad(this)) {
            tabMode = 1;
            setContentView(R.layout.activity_ble_connector_tab);
            reverseButton = findViewById(R.id.reverse_mode_switch);

            reverseButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        reverseMode = 1;
                    } else {
                        reverseMode = 0;
                    }
                }
            });

        } else {
            tabMode = 0;

            switch (boatType){
                case "1x":

                    if (Objects.equals(Lang, "eng")){

                        setContentView(R.layout.activity_ble_connector_1x_eng);

                    }else{

                        setContentView(R.layout.activity_ble_connector_1x);
                    }

                    break;

                case "2x":

                    if (Objects.equals(Lang, "eng")){

                        setContentView(R.layout.activity_ble_connector_2x_eng);

                    }else{

                        setContentView(R.layout.activity_ble_connector_2x);
                    }


                    break;

                case "4x":

                    if (Objects.equals(Lang, "eng")){

                        setContentView(R.layout.activity_ble_connector_4x_eng);

                    }else{

                        setContentView(R.layout.activity_ble_connector_4x);
                    }

                    break;

                case "4x_front":

                    if (Objects.equals(Lang, "eng")){

                        setContentView(R.layout.activity_ble_connector_4x_front_eng);

                    }else{

                        setContentView(R.layout.activity_ble_connector_4x_front);
                    }

                    break;

                case "4x_back":

                    if (Objects.equals(Lang, "eng")){

                        setContentView(R.layout.activity_ble_connector_4x_back_eng);

                    }else{

                        setContentView(R.layout.activity_ble_connector_4x_back);
                    }
                    break;

                case "2mi":

                    if (Objects.equals(Lang, "eng")){

                        setContentView(R.layout.activity_ble_connector_2mi_eng);

                    }else{

                        setContentView(R.layout.activity_ble_connector_2mi);
                    }

                    break;

                case "4mi":

                    if (Objects.equals(Lang, "eng")){

                        setContentView(R.layout.activity_ble_connector_4mi_eng);

                    }else{

                        setContentView(R.layout.activity_ble_connector_4mi);
                    }

                    break;

                case "8mi":

                    if (Objects.equals(Lang, "eng")){

                        setContentView(R.layout.activity_ble_connector_8mi_eng);

                    }else{

                        setContentView(R.layout.activity_ble_connector_8mi);
                    }

                    break;

                case "8mi_front":

                    if (Objects.equals(Lang, "eng")){

                        setContentView(R.layout.activity_ble_connector_8mi_front_eng);

                    }else{

                        setContentView(R.layout.activity_ble_connector_8mi_front);
                    }
                    break;

                case "8mi_back":

                    if (Objects.equals(Lang, "eng")){

                        setContentView(R.layout.activity_ble_connector_8mi_back_eng);

                    }else{

                        setContentView(R.layout.activity_ble_connector_8mi_back);
                    }
                    break;

                case "UDF":

                    if (Objects.equals(Lang, "eng")){

                        setContentView(R.layout.activity_ble_connector_eng);

                    }else{

                        setContentView(R.layout.activity_ble_connector);
                    }
                    break;

            }

        }
        athleteNamePath =  this.getFilesDir() + "/athlete_list/专用.csv";

        spinnerL1 = findViewById(R.id.spinner_L1);
        spinnerL2 = findViewById(R.id.spinner_L2);
        spinnerL3 = findViewById(R.id.spinner_L3);
        spinnerL4 = findViewById(R.id.spinner_L4);
        spinnerR1 = findViewById(R.id.spinner_R1);
        spinnerR2 = findViewById(R.id.spinner_R2);
        spinnerR3 = findViewById(R.id.spinner_R3);
        spinnerR4 = findViewById(R.id.spinner_R4);
        spinnerBOAT = findViewById(R.id.spinner_Boat);

        spinnerHR1 = findViewById(R.id.hr_1_spinner);
        spinnerHR2 = findViewById(R.id.hr_2_spinner);
        spinnerHR3 = findViewById(R.id.hr_3_spinner);
        spinnerHR4 = findViewById(R.id.hr_4_spinner);
        spinnerHR5 = findViewById(R.id.hr_5_spinner);
        spinnerHR6 = findViewById(R.id.hr_6_spinner);
        spinnerHR7 = findViewById(R.id.hr_7_spinner);
        spinnerHR8 = findViewById(R.id.hr_8_spinner);

        spinner1_name = findViewById(R.id.name_1_spinner);
        spinner2_name = findViewById(R.id.name_2_spinner);
        spinner3_name = findViewById(R.id.name_3_spinner);
        spinner4_name = findViewById(R.id.name_4_spinner);
        spinner5_name = findViewById(R.id.name_5_spinner);
        spinner6_name = findViewById(R.id.name_6_spinner);
        spinner7_name = findViewById(R.id.name_7_spinner);
        spinner8_name = findViewById(R.id.name_8_spinner);

        name_hrm_switch = findViewById(R.id.name_hr_switch);

        sideSwitchButton = findViewById(R.id.side_switch);

        mSpinnerListName.add(spinner1_name);
        mSpinnerListName.add(spinner2_name);
        mSpinnerListName.add(spinner3_name);
        mSpinnerListName.add(spinner4_name);
        mSpinnerListName.add(spinner5_name);
        mSpinnerListName.add(spinner6_name);
        mSpinnerListName.add(spinner7_name);
        mSpinnerListName.add(spinner8_name);

        mSpinnerList.add(spinnerL1);
        mSpinnerList.add(spinnerL2);
        mSpinnerList.add(spinnerL3);
        mSpinnerList.add(spinnerL4);
        mSpinnerList.add(spinnerR1);
        mSpinnerList.add(spinnerR2);
        mSpinnerList.add(spinnerR3);
        mSpinnerList.add(spinnerR4);
        mSpinnerList.add(spinnerBOAT);

        mSpinnerListBelt.add(spinnerHR1);
        mSpinnerListBelt.add(spinnerHR2);
        mSpinnerListBelt.add(spinnerHR3);
        mSpinnerListBelt.add(spinnerHR4);
        mSpinnerListBelt.add(spinnerHR5);
        mSpinnerListBelt.add(spinnerHR6);
        mSpinnerListBelt.add(spinnerHR7);
        mSpinnerListBelt.add(spinnerHR8);

        mContainer1_hrm = findViewById(R.id.hrm_container_1);
        mContainer2_hrm = findViewById(R.id.hrm_container_2);
        mContainer3_hrm = findViewById(R.id.hrm_container_3);
        mContainer4_hrm = findViewById(R.id.hrm_container_4);
        mContainer5_hrm = findViewById(R.id.hrm_container_5);
        mContainer6_hrm = findViewById(R.id.hrm_container_6);
        mContainer7_hrm = findViewById(R.id.hrm_container_7);
        mContainer8_hrm = findViewById(R.id.hrm_container_8);

        mContainer1_name = findViewById(R.id.name_container_1);
        mContainer2_name = findViewById(R.id.name_container_2);
        mContainer3_name = findViewById(R.id.name_container_3);
        mContainer4_name = findViewById(R.id.name_container_4);
        mContainer5_name = findViewById(R.id.name_container_5);
        mContainer6_name = findViewById(R.id.name_container_6);
        mContainer7_name = findViewById(R.id.name_container_7);
        mContainer8_name = findViewById(R.id.name_container_8);

        mContainer_L1 = findViewById(R.id.oar_container_L1);
        mContainer_L2 = findViewById(R.id.oar_container_L2);
        mContainer_L3 = findViewById(R.id.oar_container_L3);
        mContainer_L4 = findViewById(R.id.oar_container_L4);

        mContainer_R1 = findViewById(R.id.oar_container_R1);
        mContainer_R2 = findViewById(R.id.oar_container_R2);
        mContainer_R3 = findViewById(R.id.oar_container_R3);
        mContainer_R4 = findViewById(R.id.oar_container_R4);




        api = PolarBleApiDefaultImpl.defaultImplementation(this, PolarBleApi.ALL_FEATURES);
        api.setPolarFilter(false);

        name_hrm_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    FrameLayout.LayoutParams fp_L1 = (FrameLayout.LayoutParams) mContainer1_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_L2 = (FrameLayout.LayoutParams) mContainer2_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_L3 = (FrameLayout.LayoutParams) mContainer3_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_L4 = (FrameLayout.LayoutParams) mContainer4_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_R1 = (FrameLayout.LayoutParams) mContainer5_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_R2 = (FrameLayout.LayoutParams) mContainer6_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_R3 = (FrameLayout.LayoutParams) mContainer7_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_R4 = (FrameLayout.LayoutParams) mContainer8_hrm.getLayoutParams();

                    FrameLayout.LayoutParams fp_1 = (FrameLayout.LayoutParams) mContainer1_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_2 = (FrameLayout.LayoutParams) mContainer2_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_3 = (FrameLayout.LayoutParams) mContainer3_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_4 = (FrameLayout.LayoutParams) mContainer4_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_5 = (FrameLayout.LayoutParams) mContainer5_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_6 = (FrameLayout.LayoutParams) mContainer6_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_7 = (FrameLayout.LayoutParams) mContainer7_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_8 = (FrameLayout.LayoutParams) mContainer8_name.getLayoutParams();

                    fp_L1.rightMargin = -5000;
                    fp_L2.rightMargin = -5000;
                    fp_L3.rightMargin = -5000;
                    fp_L4.rightMargin = -5000;
                    fp_R1.rightMargin = -5000;
                    fp_R2.rightMargin = -5000;
                    fp_R3.rightMargin = -5000;
                    fp_R4.rightMargin = -5000;

                    fp_1.rightMargin = 0;
                    fp_2.rightMargin = 0;
                    fp_3.rightMargin = 0;
                    fp_4.rightMargin = 0;
                    fp_5.rightMargin = 0;
                    fp_6.rightMargin = 0;
                    fp_7.rightMargin = 0;
                    fp_8.rightMargin = 0;

                    mContainer1_hrm.setLayoutParams(fp_L1);
                    mContainer2_hrm.setLayoutParams(fp_L2);
                    mContainer3_hrm.setLayoutParams(fp_L3);
                    mContainer4_hrm.setLayoutParams(fp_L4);
                    mContainer5_hrm.setLayoutParams(fp_R1);
                    mContainer6_hrm.setLayoutParams(fp_R2);
                    mContainer7_hrm.setLayoutParams(fp_R3);
                    mContainer8_hrm.setLayoutParams(fp_R4);

                    mContainer1_name.setLayoutParams(fp_1);
                    mContainer2_name.setLayoutParams(fp_2);
                    mContainer3_name.setLayoutParams(fp_3);
                    mContainer4_name.setLayoutParams(fp_4);
                    mContainer5_name.setLayoutParams(fp_5);
                    mContainer6_name.setLayoutParams(fp_6);
                    mContainer7_name.setLayoutParams(fp_7);
                    mContainer8_name.setLayoutParams(fp_8);

                } else {
                    FrameLayout.LayoutParams fp_L1 = (FrameLayout.LayoutParams) mContainer1_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_L2 = (FrameLayout.LayoutParams) mContainer2_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_L3 = (FrameLayout.LayoutParams) mContainer3_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_L4 = (FrameLayout.LayoutParams) mContainer4_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_R1 = (FrameLayout.LayoutParams) mContainer5_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_R2 = (FrameLayout.LayoutParams) mContainer6_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_R3 = (FrameLayout.LayoutParams) mContainer7_hrm.getLayoutParams();
                    FrameLayout.LayoutParams fp_R4 = (FrameLayout.LayoutParams) mContainer8_hrm.getLayoutParams();

                    FrameLayout.LayoutParams fp_1 = (FrameLayout.LayoutParams) mContainer1_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_2 = (FrameLayout.LayoutParams) mContainer2_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_3 = (FrameLayout.LayoutParams) mContainer3_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_4 = (FrameLayout.LayoutParams) mContainer4_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_5 = (FrameLayout.LayoutParams) mContainer5_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_6 = (FrameLayout.LayoutParams) mContainer6_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_7 = (FrameLayout.LayoutParams) mContainer7_name.getLayoutParams();
                    FrameLayout.LayoutParams fp_8 = (FrameLayout.LayoutParams) mContainer8_name.getLayoutParams();

                    fp_L1.rightMargin = 0;
                    fp_L2.rightMargin = 0;
                    fp_L3.rightMargin = 0;
                    fp_L4.rightMargin = 0;
                    fp_R1.rightMargin = 0;
                    fp_R2.rightMargin = 0;
                    fp_R3.rightMargin = 0;
                    fp_R4.rightMargin = 0;

                    fp_1.rightMargin = 5000;
                    fp_2.rightMargin = 5000;
                    fp_3.rightMargin = 5000;
                    fp_4.rightMargin = 5000;
                    fp_5.rightMargin = 5000;
                    fp_6.rightMargin = 5000;
                    fp_7.rightMargin = 5000;
                    fp_8.rightMargin = 5000;

                    mContainer1_hrm.setLayoutParams(fp_L1);
                    mContainer2_hrm.setLayoutParams(fp_L2);
                    mContainer3_hrm.setLayoutParams(fp_L3);
                    mContainer4_hrm.setLayoutParams(fp_L4);
                    mContainer5_hrm.setLayoutParams(fp_R1);
                    mContainer6_hrm.setLayoutParams(fp_R2);
                    mContainer7_hrm.setLayoutParams(fp_R3);
                    mContainer8_hrm.setLayoutParams(fp_R4);

                    mContainer1_name.setLayoutParams(fp_1);
                    mContainer2_name.setLayoutParams(fp_2);
                    mContainer3_name.setLayoutParams(fp_3);
                    mContainer4_name.setLayoutParams(fp_4);
                    mContainer5_name.setLayoutParams(fp_5);
                    mContainer6_name.setLayoutParams(fp_6);
                    mContainer7_name.setLayoutParams(fp_7);
                    mContainer8_name.setLayoutParams(fp_8);

                }
            }
        });

        sideSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    sideReverse = "Rev";

                    FrameLayout.LayoutParams fp_L1_oar = (FrameLayout.LayoutParams) mContainer_L1.getLayoutParams();
                    FrameLayout.LayoutParams fp_L2_oar = (FrameLayout.LayoutParams) mContainer_L2.getLayoutParams();
                    FrameLayout.LayoutParams fp_L3_oar = (FrameLayout.LayoutParams) mContainer_L3.getLayoutParams();
                    FrameLayout.LayoutParams fp_L4_oar = (FrameLayout.LayoutParams) mContainer_L4.getLayoutParams();
                    FrameLayout.LayoutParams fp_R1_oar = (FrameLayout.LayoutParams) mContainer_R1.getLayoutParams();
                    FrameLayout.LayoutParams fp_R2_oar = (FrameLayout.LayoutParams) mContainer_R2.getLayoutParams();
                    FrameLayout.LayoutParams fp_R3_oar = (FrameLayout.LayoutParams) mContainer_R3.getLayoutParams();
                    FrameLayout.LayoutParams fp_R4_oar = (FrameLayout.LayoutParams) mContainer_R4.getLayoutParams();


                    switch (boatType){
                        case "2mi":
                            fp_L1_oar.topMargin = (int) (275 * density);
                            fp_R1_oar.topMargin = (int) (375 * density);

                            mContainer_L1.setLayoutParams(fp_L1_oar);
                            mContainer_R1.setLayoutParams(fp_R1_oar);
                            break;

                        case "4mi":

                            fp_L1_oar.topMargin = (int) (275 * density);
                            fp_R1_oar.topMargin = (int) (375 * density);
                            fp_L2_oar.topMargin = (int) (75 * density);
                            fp_R2_oar.topMargin = (int) (175 * density);

                            mContainer_L1.setLayoutParams(fp_L1_oar);
                            mContainer_R1.setLayoutParams(fp_R1_oar);
                            mContainer_L2.setLayoutParams(fp_L2_oar);
                            mContainer_R2.setLayoutParams(fp_R2_oar);

                            break;

                        case "8mi":

                            fp_L1_oar.topMargin = (int) (375 * density);
                            fp_R1_oar.topMargin = (int) (425 * density);
                            fp_L2_oar.topMargin = (int) (275 * density);
                            fp_R2_oar.topMargin = (int) (325 * density);
                            fp_L3_oar.topMargin = (int) (175 * density);
                            fp_R3_oar.topMargin = (int) (225 * density);
                            fp_L4_oar.topMargin = (int) (75 * density);
                            fp_R4_oar.topMargin = (int) (125 * density);

                            mContainer_L1.setLayoutParams(fp_L1_oar);
                            mContainer_R1.setLayoutParams(fp_R1_oar);
                            mContainer_L2.setLayoutParams(fp_L2_oar);
                            mContainer_R2.setLayoutParams(fp_R2_oar);
                            mContainer_L3.setLayoutParams(fp_L3_oar);
                            mContainer_R3.setLayoutParams(fp_R3_oar);
                            mContainer_L4.setLayoutParams(fp_L4_oar);
                            mContainer_R4.setLayoutParams(fp_R4_oar);

                            break;

                        case "8mi_front":

                            fp_L1_oar.topMargin = (int) (375 * density);
                            fp_R1_oar.topMargin = (int) (425 * density);
                            fp_L2_oar.topMargin = (int) (275 * density);
                            fp_R2_oar.topMargin = (int) (325 * density);


                            mContainer_L1.setLayoutParams(fp_L1_oar);
                            mContainer_R1.setLayoutParams(fp_R1_oar);
                            mContainer_L2.setLayoutParams(fp_L2_oar);
                            mContainer_R2.setLayoutParams(fp_R2_oar);


                            break;

                        case "8mi_back":

                            fp_L3_oar.topMargin = (int) (175 * density);
                            fp_R3_oar.topMargin = (int) (225 * density);
                            fp_L4_oar.topMargin = (int) (75 * density);
                            fp_R4_oar.topMargin = (int) (125 * density);

                            mContainer_L3.setLayoutParams(fp_L3_oar);
                            mContainer_R3.setLayoutParams(fp_R3_oar);
                            mContainer_L4.setLayoutParams(fp_L4_oar);
                            mContainer_R4.setLayoutParams(fp_R4_oar);

                            break;

                    }



                } else {

                    sideReverse = "";

                    FrameLayout.LayoutParams fp_L1_oar = (FrameLayout.LayoutParams) mContainer_L1.getLayoutParams();
                    FrameLayout.LayoutParams fp_L2_oar = (FrameLayout.LayoutParams) mContainer_L2.getLayoutParams();
                    FrameLayout.LayoutParams fp_L3_oar = (FrameLayout.LayoutParams) mContainer_L3.getLayoutParams();
                    FrameLayout.LayoutParams fp_L4_oar = (FrameLayout.LayoutParams) mContainer_L4.getLayoutParams();
                    FrameLayout.LayoutParams fp_R1_oar = (FrameLayout.LayoutParams) mContainer_R1.getLayoutParams();
                    FrameLayout.LayoutParams fp_R2_oar = (FrameLayout.LayoutParams) mContainer_R2.getLayoutParams();
                    FrameLayout.LayoutParams fp_R3_oar = (FrameLayout.LayoutParams) mContainer_R3.getLayoutParams();
                    FrameLayout.LayoutParams fp_R4_oar = (FrameLayout.LayoutParams) mContainer_R4.getLayoutParams();



                    switch (boatType){
                        case "2mi":

                            fp_L1_oar.topMargin = (int) (375 * density);
                            fp_R1_oar.topMargin = (int) (275 * density);

                            mContainer_L1.setLayoutParams(fp_L1_oar);
                            mContainer_R1.setLayoutParams(fp_R1_oar);
                            break;

                        case "4mi":

                            fp_L1_oar.topMargin = (int) (375 * density);
                            fp_R1_oar.topMargin = (int) (275 * density);
                            fp_L2_oar.topMargin = (int) (175 * density);
                            fp_R2_oar.topMargin = (int) (75 * density);

                            mContainer_L1.setLayoutParams(fp_L1_oar);
                            mContainer_R1.setLayoutParams(fp_R1_oar);

                            break;

                        case "8mi":

                            fp_R1_oar.topMargin = (int) (375 * density);
                            fp_L1_oar.topMargin = (int) (425 * density);
                            fp_R2_oar.topMargin = (int) (275 * density);
                            fp_L2_oar.topMargin = (int) (325 * density);
                            fp_R3_oar.topMargin = (int) (175 * density);
                            fp_L3_oar.topMargin = (int) (225 * density);
                            fp_R4_oar.topMargin = (int) (75 * density);
                            fp_L4_oar.topMargin = (int) (125 * density);

                            mContainer_L1.setLayoutParams(fp_L1_oar);
                            mContainer_R1.setLayoutParams(fp_R1_oar);
                            mContainer_L2.setLayoutParams(fp_L2_oar);
                            mContainer_R2.setLayoutParams(fp_R2_oar);
                            mContainer_L3.setLayoutParams(fp_L3_oar);
                            mContainer_R3.setLayoutParams(fp_R3_oar);
                            mContainer_L4.setLayoutParams(fp_L4_oar);
                            mContainer_R4.setLayoutParams(fp_R4_oar);

                            break;

                        case "8mi_front":

                            fp_R1_oar.topMargin = (int) (375 * density);
                            fp_L1_oar.topMargin = (int) (425 * density);
                            fp_R2_oar.topMargin = (int) (275 * density);
                            fp_L2_oar.topMargin = (int) (325 * density);

                            mContainer_L1.setLayoutParams(fp_L1_oar);
                            mContainer_R1.setLayoutParams(fp_R1_oar);
                            mContainer_L2.setLayoutParams(fp_L2_oar);
                            mContainer_R2.setLayoutParams(fp_R2_oar);

                            break;

                        case "8mi_back":

                            fp_R3_oar.topMargin = (int) (175 * density);
                            fp_L3_oar.topMargin = (int) (225 * density);
                            fp_R4_oar.topMargin = (int) (75 * density);
                            fp_L4_oar.topMargin = (int) (125 * density);

                            mContainer_L3.setLayoutParams(fp_L3_oar);
                            mContainer_R3.setLayoutParams(fp_R3_oar);
                            mContainer_L4.setLayoutParams(fp_L4_oar);
                            mContainer_R4.setLayoutParams(fp_R4_oar);

                            break;

                    }


                }
            }
        });


        mItems = getResources().getStringArray(R.array.DotsName);
        ArrayAdapter<String> adapter;

        if (tabMode == 1) {
            adapter = new ArrayAdapter(this, R.layout.my_spinner_2_tab, mItems);
            adapter.setDropDownViewResource(R.layout.my_drop_down_2_tab);
        } else {
            adapter = new ArrayAdapter(this, R.layout.my_spinner_2, mItems);
            adapter.setDropDownViewResource(R.layout.my_drop_down_2);
        }

        mItemsHR = getResources().getStringArray(R.array.BeltsName);
        ArrayAdapter<String> adapter_hr;

        if (tabMode == 1) {
            adapter_hr = new ArrayAdapter(this, R.layout.my_spinner_red_tab, mItemsHR);
            adapter_hr.setDropDownViewResource(R.layout.my_drop_down_red_tab);
        } else {
            adapter_hr = new ArrayAdapter(this, R.layout.my_spinner_red, mItemsHR);
            adapter_hr.setDropDownViewResource(R.layout.my_drop_down_red);
        }

        spinnerL1.setAdapter(adapter);
        spinnerL2.setAdapter(adapter);
        spinnerL3.setAdapter(adapter);
        spinnerL4.setAdapter(adapter);
        spinnerR1.setAdapter(adapter);
        spinnerR2.setAdapter(adapter);
        spinnerR3.setAdapter(adapter);
        spinnerR4.setAdapter(adapter);
        spinnerBOAT.setAdapter(adapter);

        spinnerHR1.setAdapter(adapter_hr);
        spinnerHR2.setAdapter(adapter_hr);
        spinnerHR3.setAdapter(adapter_hr);
        spinnerHR4.setAdapter(adapter_hr);
        spinnerHR5.setAdapter(adapter_hr);
        spinnerHR6.setAdapter(adapter_hr);
        spinnerHR7.setAdapter(adapter_hr);
        spinnerHR8.setAdapter(adapter_hr);

        spinner1_name.setAdapter(adapter_hr);
        spinner2_name.setAdapter(adapter_hr);
        spinner3_name.setAdapter(adapter_hr);
        spinner4_name.setAdapter(adapter_hr);
        spinner5_name.setAdapter(adapter_hr);
        spinner6_name.setAdapter(adapter_hr);
        spinner7_name.setAdapter(adapter_hr);
        spinner8_name.setAdapter(adapter_hr);

        getAthleteNameIDList(athleteNamePath);

        mXsDotScanner = new XsensDotScanner(getApplicationContext(), this);
        mXsDotScanner.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        mScan = findViewById(R.id.btn_scan);
        mConnect = findViewById(R.id.btn_connect);
        mPowerOff = findViewById(R.id.btn_poweroff);

        mConnect.setAlpha(0f);
        mPowerOff.setAlpha(0f);

        mDotsCount = findViewById(R.id.dots_count);

        mDot1 = findViewById(R.id.dot_01);
        mDot2 = findViewById(R.id.dot_02);
        mDot3 = findViewById(R.id.dot_03);
        mDot4 = findViewById(R.id.dot_04);
        mDot5 = findViewById(R.id.dot_05);
        mDot6 = findViewById(R.id.dot_06);
        mDot7 = findViewById(R.id.dot_07);
        mDot8 = findViewById(R.id.dot_08);
        mDot9 = findViewById(R.id.dot_09);
        mDot10 = findViewById(R.id.dot_10);

        mTvNameList.add(mDot1);
        mTvNameList.add(mDot2);
        mTvNameList.add(mDot3);
        mTvNameList.add(mDot4);
        mTvNameList.add(mDot5);
        mTvNameList.add(mDot6);
        mTvNameList.add(mDot7);
        mTvNameList.add(mDot8);
        mTvNameList.add(mDot9);
        mTvNameList.add(mDot10);

        mDot1Pos = findViewById(R.id.dot_01_pos);
        mDot2Pos = findViewById(R.id.dot_02_pos);
        mDot3Pos = findViewById(R.id.dot_03_pos);
        mDot4Pos = findViewById(R.id.dot_04_pos);
        mDot5Pos = findViewById(R.id.dot_05_pos);
        mDot6Pos = findViewById(R.id.dot_06_pos);
        mDot7Pos = findViewById(R.id.dot_07_pos);
        mDot8Pos = findViewById(R.id.dot_08_pos);
        mDot9Pos = findViewById(R.id.dot_09_pos);
        mDot10Pos = findViewById(R.id.dot_10_pos);

        mTvPosList.add(mDot1Pos);
        mTvPosList.add(mDot2Pos);
        mTvPosList.add(mDot3Pos);
        mTvPosList.add(mDot4Pos);
        mTvPosList.add(mDot5Pos);
        mTvPosList.add(mDot6Pos);
        mTvPosList.add(mDot7Pos);
        mTvPosList.add(mDot8Pos);
        mTvPosList.add(mDot9Pos);
        mTvPosList.add(mDot10Pos);

        mSelectedSensorList.add(DotL1);
        mSelectedSensorList.add(DotL2);
        mSelectedSensorList.add(DotL3);
        mSelectedSensorList.add(DotL4);
        mSelectedSensorList.add(DotR1);
        mSelectedSensorList.add(DotR2);
        mSelectedSensorList.add(DotR3);
        mSelectedSensorList.add(DotR4);
        mSelectedSensorList.add(DotBOAT);

        mSelectedBeltList.add(Belt1);
        mSelectedBeltList.add(Belt2);
        mSelectedBeltList.add(Belt3);
        mSelectedBeltList.add(Belt4);
        mSelectedBeltList.add(Belt5);
        mSelectedBeltList.add(Belt6);
        mSelectedBeltList.add(Belt7);
        mSelectedBeltList.add(Belt8);

        mSelectedBeltNameList.add(BeltName1);
        mSelectedBeltNameList.add(BeltName2);
        mSelectedBeltNameList.add(BeltName3);
        mSelectedBeltNameList.add(BeltName4);
        mSelectedBeltNameList.add(BeltName5);
        mSelectedBeltNameList.add(BeltName6);
        mSelectedBeltNameList.add(BeltName7);
        mSelectedBeltNameList.add(BeltName8);



        mBelt1Pos = findViewById(R.id.hr_1_connector);
        mBelt2Pos = findViewById(R.id.hr_2_connector);
        mBelt3Pos = findViewById(R.id.hr_3_connector);
        mBelt4Pos = findViewById(R.id.hr_4_connector);
        mBelt5Pos = findViewById(R.id.hr_5_connector);
        mBelt6Pos = findViewById(R.id.hr_6_connector);
        mBelt7Pos = findViewById(R.id.hr_7_connector);
        mBelt8Pos = findViewById(R.id.hr_8_connector);

        mTvBeltPosList.add(mBelt1Pos);
        mTvBeltPosList.add(mBelt2Pos);
        mTvBeltPosList.add(mBelt3Pos);
        mTvBeltPosList.add(mBelt4Pos);
        mTvBeltPosList.add(mBelt5Pos);
        mTvBeltPosList.add(mBelt6Pos);
        mTvBeltPosList.add(mBelt7Pos);
        mTvBeltPosList.add(mBelt8Pos);

        mDeviceListHR = new ArrayList<>();
        mNameListBelt = new ArrayList<>();

        mBeltCount = findViewById(R.id.belts_count);

        for (int i = 0; i < 10; i++) {
            mSpinnerSelected.add("-");
        }

        for (int i = 0; i < 8; i++) {
            mSpinnerSelectedBelt.add("-");
        }

        for (int i = 0; i < 8; i++) {
            mSpinnerSelectedList.add("-");
        }

        for (int i = 0; i < 8; i++) {
            mSelectedAtheleteName.add("-");
        }

        for (int i = 0; i < 8; i++) {
            mSelectedAtheleteID.add("-");
        }

        username = intent.getStringExtra("userName");
        password = intent.getStringExtra("password");
//        initHR();

        mConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BleConnector.this, Dashboard.class);
//                scannerHR.Stop();

                if (checkSelected() == 1) {

                    Toast.makeText(BleConnector.this, "混合模式", Toast.LENGTH_SHORT).show();

                    intent.putExtra("L1", DotL1);
                    intent.putExtra("L2", DotL2);
                    intent.putExtra("L3", DotL3);
                    intent.putExtra("L4", DotL4);
                    intent.putExtra("R1", DotR1);
                    intent.putExtra("R2", DotR2);
                    intent.putExtra("R3", DotR3);
                    intent.putExtra("R4", DotR4);
                    intent.putExtra("BOAT", DotBOAT);
                    intent.putExtra("userName", username);
                    intent.putExtra("password", password);
                    intent.putExtra("Belt1", Belt1);
                    intent.putExtra("Belt2", Belt2);
                    intent.putExtra("Belt3", Belt3);
                    intent.putExtra("Belt4", Belt4);
                    intent.putExtra("Belt5", Belt5);
                    intent.putExtra("Belt6", Belt6);
                    intent.putExtra("Belt7", Belt7);
                    intent.putExtra("Belt8", Belt8);
                    intent.putExtra("BeltName1", BeltName1);
                    intent.putExtra("BeltName2", BeltName2);
                    intent.putExtra("BeltName3", BeltName3);
                    intent.putExtra("BeltName4", BeltName4);
                    intent.putExtra("BeltName5", BeltName5);
                    intent.putExtra("BeltName6", BeltName6);
                    intent.putExtra("BeltName7", BeltName7);
                    intent.putExtra("BeltName8", BeltName8);
                    intent.putExtra("reverse", reverseMode);
                    intent.putExtra("selectedDotsAmount", mSelectedDots);
                    intent.putExtra("selectedBeltAmount", mSelectedBelts);
                    intent.putExtra("mode", checkSelected());

                    intent.putExtra("AthleteName1", mSelectedAtheleteName.get(0));
                    intent.putExtra("AthleteName2", mSelectedAtheleteName.get(1));
                    intent.putExtra("AthleteName3", mSelectedAtheleteName.get(2));
                    intent.putExtra("AthleteName4", mSelectedAtheleteName.get(3));
                    intent.putExtra("AthleteName5", mSelectedAtheleteName.get(4));
                    intent.putExtra("AthleteName6", mSelectedAtheleteName.get(5));
                    intent.putExtra("AthleteName7", mSelectedAtheleteName.get(6));
                    intent.putExtra("AthleteName8", mSelectedAtheleteName.get(7));
                    intent.putExtra("AthleteID1", mSelectedAtheleteID.get(0));
                    intent.putExtra("AthleteID2", mSelectedAtheleteID.get(1));
                    intent.putExtra("AthleteID3", mSelectedAtheleteID.get(2));
                    intent.putExtra("AthleteID4", mSelectedAtheleteID.get(3));
                    intent.putExtra("AthleteID5", mSelectedAtheleteID.get(4));
                    intent.putExtra("AthleteID6", mSelectedAtheleteID.get(5));
                    intent.putExtra("AthleteID7", mSelectedAtheleteID.get(6));
                    intent.putExtra("AthleteID8", mSelectedAtheleteID.get(7));

                    intent.putExtra("boatType",boatType+sideReverse);
                    intent.putExtra("lang",Lang);


//                    System.out.println(Belt1);
//                    System.out.println(Belt2);
//                    System.out.println(Belt3);
//                    System.out.println(intent.getExtras());
                    startActivity(intent);

                } else if (checkSelected() == 2) {
                    Toast.makeText(BleConnector.this, "传感器模式", Toast.LENGTH_SHORT).show();

                    intent.putExtra("L1", DotL1);
                    intent.putExtra("L2", DotL2);
                    intent.putExtra("L3", DotL3);
                    intent.putExtra("L4", DotL4);
                    intent.putExtra("R1", DotR1);
                    intent.putExtra("R2", DotR2);
                    intent.putExtra("R3", DotR3);
                    intent.putExtra("R4", DotR4);
                    intent.putExtra("BOAT", DotBOAT);
                    intent.putExtra("userName", username);
                    intent.putExtra("password", password);
                    intent.putExtra("Belt1", Belt1);
                    intent.putExtra("Belt2", Belt2);
                    intent.putExtra("Belt3", Belt3);
                    intent.putExtra("Belt4", Belt4);
                    intent.putExtra("Belt5", Belt5);
                    intent.putExtra("Belt6", Belt6);
                    intent.putExtra("Belt7", Belt7);
                    intent.putExtra("Belt8", Belt8);
                    intent.putExtra("BeltName1", BeltName1);
                    intent.putExtra("BeltName2", BeltName2);
                    intent.putExtra("BeltName3", BeltName3);
                    intent.putExtra("BeltName4", BeltName4);
                    intent.putExtra("BeltName5", BeltName5);
                    intent.putExtra("BeltName6", BeltName6);
                    intent.putExtra("BeltName7", BeltName7);
                    intent.putExtra("BeltName8", BeltName8);
                    intent.putExtra("reverse", reverseMode);
                    intent.putExtra("selectedBeltAmount", mSelectedBelts);
                    intent.putExtra("mode", checkSelected());

                    intent.putExtra("AthleteName1", mSelectedAtheleteName.get(0));
                    intent.putExtra("AthleteName2", mSelectedAtheleteName.get(1));
                    intent.putExtra("AthleteName3", mSelectedAtheleteName.get(2));
                    intent.putExtra("AthleteName4", mSelectedAtheleteName.get(3));
                    intent.putExtra("AthleteName5", mSelectedAtheleteName.get(4));
                    intent.putExtra("AthleteName6", mSelectedAtheleteName.get(5));
                    intent.putExtra("AthleteName7", mSelectedAtheleteName.get(6));
                    intent.putExtra("AthleteName8", mSelectedAtheleteName.get(7));
                    intent.putExtra("AthleteID1", mSelectedAtheleteID.get(0));
                    intent.putExtra("AthleteID2", mSelectedAtheleteID.get(1));
                    intent.putExtra("AthleteID3", mSelectedAtheleteID.get(2));
                    intent.putExtra("AthleteID4", mSelectedAtheleteID.get(3));
                    intent.putExtra("AthleteID5", mSelectedAtheleteID.get(4));
                    intent.putExtra("AthleteID6", mSelectedAtheleteID.get(5));
                    intent.putExtra("AthleteID7", mSelectedAtheleteID.get(6));
                    intent.putExtra("AthleteID8", mSelectedAtheleteID.get(7));

                    intent.putExtra("boatType",boatType+sideReverse);
                    intent.putExtra("lang",Lang);



                    System.out.println(intent.getExtras());
                    startActivity(intent);

                } else if (checkSelected() == 3) {

                    System.out.println(mSelectedSensorList);
                    Toast.makeText(BleConnector.this, "心率带模式", Toast.LENGTH_SHORT).show();

                    intent.putExtra("L1", DotL1);
                    intent.putExtra("L2", DotL2);
                    intent.putExtra("L3", DotL3);
                    intent.putExtra("L4", DotL4);
                    intent.putExtra("R1", DotR1);
                    intent.putExtra("R2", DotR2);
                    intent.putExtra("R3", DotR3);
                    intent.putExtra("R4", DotR4);
                    intent.putExtra("BOAT", DotBOAT);
                    intent.putExtra("userName", username);
                    intent.putExtra("password", password);
                    intent.putExtra("Belt1", Belt1);
                    intent.putExtra("Belt2", Belt2);
                    intent.putExtra("Belt3", Belt3);
                    intent.putExtra("Belt4", Belt4);
                    intent.putExtra("Belt5", Belt5);
                    intent.putExtra("Belt6", Belt6);
                    intent.putExtra("Belt7", Belt7);
                    intent.putExtra("Belt8", Belt8);
                    intent.putExtra("BeltName1", BeltName1);
                    intent.putExtra("BeltName2", BeltName2);
                    intent.putExtra("BeltName3", BeltName3);
                    intent.putExtra("BeltName4", BeltName4);
                    intent.putExtra("BeltName5", BeltName5);
                    intent.putExtra("BeltName6", BeltName6);
                    intent.putExtra("BeltName7", BeltName7);
                    intent.putExtra("BeltName8", BeltName8);
                    intent.putExtra("reverse", reverseMode);
                    intent.putExtra("selectedBeltAmount", mSelectedBelts);
                    intent.putExtra("mode", checkSelected());

                    intent.putExtra("AthleteName1", mSelectedAtheleteName.get(0));
                    intent.putExtra("AthleteName2", mSelectedAtheleteName.get(1));
                    intent.putExtra("AthleteName3", mSelectedAtheleteName.get(2));
                    intent.putExtra("AthleteName4", mSelectedAtheleteName.get(3));
                    intent.putExtra("AthleteName5", mSelectedAtheleteName.get(4));
                    intent.putExtra("AthleteName6", mSelectedAtheleteName.get(5));
                    intent.putExtra("AthleteName7", mSelectedAtheleteName.get(6));
                    intent.putExtra("AthleteName8", mSelectedAtheleteName.get(7));
                    intent.putExtra("AthleteID1", mSelectedAtheleteID.get(0));
                    intent.putExtra("AthleteID2", mSelectedAtheleteID.get(1));
                    intent.putExtra("AthleteID3", mSelectedAtheleteID.get(2));
                    intent.putExtra("AthleteID4", mSelectedAtheleteID.get(3));
                    intent.putExtra("AthleteID5", mSelectedAtheleteID.get(4));
                    intent.putExtra("AthleteID6", mSelectedAtheleteID.get(5));
                    intent.putExtra("AthleteID7", mSelectedAtheleteID.get(6));
                    intent.putExtra("AthleteID8", mSelectedAtheleteID.get(7));

                    intent.putExtra("boatType",boatType+sideReverse);
                    intent.putExtra("lang",Lang);


                    System.out.println(intent.getExtras());
                    startActivity(intent);


                } else {

                    System.out.println(mSelectedSensorList);
                    Toast.makeText(BleConnector.this, "请选择至少一个传感器/心率带", Toast.LENGTH_SHORT).show();

                    intent.putExtra("L1", DotL1);
                    intent.putExtra("L2", DotL2);
                    intent.putExtra("L3", DotL3);
                    intent.putExtra("L4", DotL4);
                    intent.putExtra("R1", DotR1);
                    intent.putExtra("R2", DotR2);
                    intent.putExtra("R3", DotR3);
                    intent.putExtra("R4", DotR4);
                    intent.putExtra("BOAT", DotBOAT);
                    intent.putExtra("userName", username);
                    intent.putExtra("password", password);
                    intent.putExtra("Belt1", Belt1);
                    intent.putExtra("Belt2", Belt2);
                    intent.putExtra("Belt3", Belt3);
                    intent.putExtra("Belt4", Belt4);
                    intent.putExtra("Belt5", Belt5);
                    intent.putExtra("Belt6", Belt6);
                    intent.putExtra("Belt7", Belt7);
                    intent.putExtra("Belt8", Belt8);
                    intent.putExtra("BeltName1", BeltName1);
                    intent.putExtra("BeltName2", BeltName2);
                    intent.putExtra("BeltName3", BeltName3);
                    intent.putExtra("BeltName4", BeltName4);
                    intent.putExtra("BeltName5", BeltName5);
                    intent.putExtra("BeltName6", BeltName6);
                    intent.putExtra("BeltName7", BeltName7);
                    intent.putExtra("BeltName8", BeltName8);
                    intent.putExtra("reverse", reverseMode);
                    intent.putExtra("selectedBeltAmount", mSelectedBelts);
                    intent.putExtra("mode", checkSelected());

                    intent.putExtra("AthleteName1", mSelectedAtheleteName.get(0));
                    intent.putExtra("AthleteName2", mSelectedAtheleteName.get(1));
                    intent.putExtra("AthleteName3", mSelectedAtheleteName.get(2));
                    intent.putExtra("AthleteName4", mSelectedAtheleteName.get(3));
                    intent.putExtra("AthleteName5", mSelectedAtheleteName.get(4));
                    intent.putExtra("AthleteName6", mSelectedAtheleteName.get(5));
                    intent.putExtra("AthleteName7", mSelectedAtheleteName.get(6));
                    intent.putExtra("AthleteName8", mSelectedAtheleteName.get(7));
                    intent.putExtra("AthleteID1", mSelectedAtheleteID.get(0));
                    intent.putExtra("AthleteID2", mSelectedAtheleteID.get(1));
                    intent.putExtra("AthleteID3", mSelectedAtheleteID.get(2));
                    intent.putExtra("AthleteID4", mSelectedAtheleteID.get(3));
                    intent.putExtra("AthleteID5", mSelectedAtheleteID.get(4));
                    intent.putExtra("AthleteID6", mSelectedAtheleteID.get(5));
                    intent.putExtra("AthleteID7", mSelectedAtheleteID.get(6));
                    intent.putExtra("AthleteID8", mSelectedAtheleteID.get(7));

                    intent.putExtra("boatType",boatType+sideReverse);
                    intent.putExtra("lang",Lang);


                    System.out.println(intent.getExtras());
                    startActivity(intent);


                }

            }
        });

        spinnerL1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                BluetoothAdapter adapterL1 = BluetoothAdapter.getDefaultAdapter();
                if (mNameList.size() >= 1 && arg2 > 0) {
                    DotL1 = adapterL1.getRemoteDevice(String.valueOf(mScannedSensorList.get(arg2 - 1)));
                    refreshPosList(arg2 - 1, "L1");
                    System.out.println(mScannedSensorList.get(arg2 - 1));
                    System.out.println(DotL1);
                    System.out.println(spinnerL1.getSelectedItem());
                    mSelectedSensorList.set(0, mScannedSensorList.get(arg2 - 1));
                    mSpinnerSelected.set(0, (String) spinnerL1.getSelectedItem());
                    deleteDuplicate(0, (String) spinnerL1.getSelectedItem());

                }
                if (arg2 == 0) {
                    refreshPosList(-1, "L1");
                    DotL1 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mDot1Pos.setText("-");
                mDot1Pos.setBackgroundResource(R.drawable.input_box);
                DotL1 = null;
            }
        });

        spinnerL2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                BluetoothAdapter adapterL2 = BluetoothAdapter.getDefaultAdapter();
                if (mNameList.size() >= 1 && arg2 > 0) {
                    DotL2 = adapterL2.getRemoteDevice(String.valueOf(mScannedSensorList.get(arg2 - 1)));
                    refreshPosList(arg2 - 1, "L2");
                    System.out.println(mScannedSensorList.get(arg2 - 1));
                    System.out.println(DotL2);
                    System.out.println(spinnerL2.getSelectedItem());
                    mSelectedSensorList.set(1, mScannedSensorList.get(arg2 - 1));
                    mSpinnerSelected.set(1, (String) spinnerL2.getSelectedItem());
                    deleteDuplicate(1, (String) spinnerL2.getSelectedItem());


                }
                if (arg2 == 0) {
                    refreshPosList(-1, "L2");
                    DotL2 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mDot2Pos.setText("-");
                mDot2Pos.setBackgroundResource(R.drawable.input_box);
                DotL2 = null;
            }
        });

        spinnerL3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                BluetoothAdapter adapterL3 = BluetoothAdapter.getDefaultAdapter();
                if (mNameList.size() >= 1 && arg2 > 0) {
                    DotL3 = adapterL3.getRemoteDevice(String.valueOf(mScannedSensorList.get(arg2 - 1)));
                    refreshPosList(arg2 - 1, "L3");
                    System.out.println(mScannedSensorList.get(arg2 - 1));
                    System.out.println(DotL3);
                    System.out.println(spinnerL3.getSelectedItem());
                    mSelectedSensorList.set(2, mScannedSensorList.get(arg2 - 1));
                    mSpinnerSelected.set(2, (String) spinnerL3.getSelectedItem());
                    deleteDuplicate(2, (String) spinnerL3.getSelectedItem());

                }
                if (arg2 == 0) {
                    refreshPosList(-1, "L3");
                    DotL3 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mDot3Pos.setText("-");
                mDot3Pos.setBackgroundResource(R.drawable.input_box);
                DotL3 = null;
            }
        });

        spinnerL4.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                BluetoothAdapter adapterL4 = BluetoothAdapter.getDefaultAdapter();
                if (mNameList.size() >= 1 && arg2 > 0) {
                    DotL4 = adapterL4.getRemoteDevice(String.valueOf(mScannedSensorList.get(arg2 - 1)));
                    refreshPosList(arg2 - 1, "L4");
                    System.out.println(mScannedSensorList.get(arg2 - 1));
                    System.out.println(DotL4);
                    System.out.println(spinnerL4.getSelectedItem());
                    mSelectedSensorList.set(3, mScannedSensorList.get(arg2 - 1));
                    mSpinnerSelected.set(3, (String) spinnerL4.getSelectedItem());
                    deleteDuplicate(3, (String) spinnerL4.getSelectedItem());

                }
                if (arg2 == 0) {
                    refreshPosList(-1, "L4");
                    DotL4 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mDot4Pos.setText("-");
                mDot4Pos.setBackgroundResource(R.drawable.input_box);
                DotL4 = null;
            }
        });

        spinnerR1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                BluetoothAdapter adapterR1 = BluetoothAdapter.getDefaultAdapter();
                if (mNameList.size() >= 1 && arg2 > 0) {
                    DotR1 = adapterR1.getRemoteDevice(String.valueOf(mScannedSensorList.get(arg2 - 1)));
                    refreshPosList(arg2 - 1, "R1");
                    System.out.println(mScannedSensorList.get(arg2 - 1));
                    System.out.println(DotR1);
                    System.out.println(spinnerR1.getSelectedItem());
                    mSelectedSensorList.set(4, mScannedSensorList.get(arg2 - 1));
                    mSpinnerSelected.set(4, (String) spinnerR1.getSelectedItem());
                    deleteDuplicate(4, (String) spinnerR1.getSelectedItem());

                }
                if (arg2 == 0) {
                    refreshPosList(-1, "R1");
                    DotR1 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mDot5Pos.setText("-");
                mDot5Pos.setBackgroundResource(R.drawable.input_box);
                DotR1 = null;
            }
        });

        spinnerR2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                BluetoothAdapter adapterR2 = BluetoothAdapter.getDefaultAdapter();
                if (mNameList.size() >= 1 && arg2 > 0) {
                    DotR2 = adapterR2.getRemoteDevice(String.valueOf(mScannedSensorList.get(arg2 - 1)));
                    refreshPosList(arg2 - 1, "R2");
                    System.out.println(mScannedSensorList.get(arg2 - 1));
                    System.out.println(DotR2);
                    System.out.println(spinnerR2.getSelectedItem());
                    mSelectedSensorList.set(5, mScannedSensorList.get(arg2 - 1));
                    mSpinnerSelected.set(5, (String) spinnerR2.getSelectedItem());
                    deleteDuplicate(5, (String) spinnerR2.getSelectedItem());

                }
                if (arg2 == 0) {
                    refreshPosList(-1, "R2");
                    DotR2 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mDot6Pos.setText("-");
                mDot6Pos.setBackgroundResource(R.drawable.input_box);
                DotR2 = null;
            }
        });

        spinnerR3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                BluetoothAdapter adapterR3 = BluetoothAdapter.getDefaultAdapter();
                if (mNameList.size() >= 1 && arg2 > 0) {
                    DotR3 = adapterR3.getRemoteDevice(String.valueOf(mScannedSensorList.get(arg2 - 1)));
                    refreshPosList(arg2 - 1, "R3");
                    System.out.println(mScannedSensorList.get(arg2 - 1));
                    System.out.println(DotR3);
                    System.out.println(spinnerR3.getSelectedItem());
                    mSelectedSensorList.set(6, mScannedSensorList.get(arg2 - 1));
                    mSpinnerSelected.set(6, (String) spinnerR3.getSelectedItem());
                    deleteDuplicate(6, (String) spinnerR3.getSelectedItem());

                }
                if (arg2 == 0) {
                    refreshPosList(-1, "R3");
                    DotR3 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mDot7Pos.setText("-");
                mDot7Pos.setBackgroundResource(R.drawable.input_box);
                DotR3 = null;
            }
        });

        spinnerR4.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                BluetoothAdapter adapterR4 = BluetoothAdapter.getDefaultAdapter();
                if (mNameList.size() >= 1 && arg2 > 0) {
                    DotR4 = adapterR4.getRemoteDevice(String.valueOf(mScannedSensorList.get(arg2 - 1)));
                    refreshPosList(arg2 - 1, "R4");
                    System.out.println(mScannedSensorList.get(arg2 - 1));
                    System.out.println(DotR4);
                    System.out.println(spinnerR4.getSelectedItem());
                    mSelectedSensorList.set(7, mScannedSensorList.get(arg2 - 1));
                    mSpinnerSelected.set(7, (String) spinnerR4.getSelectedItem());
                    deleteDuplicate(7, (String) spinnerR4.getSelectedItem());

                }
                if (arg2 == 0) {
                    refreshPosList(-1, "R4");
                    DotR4 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mDot8Pos.setText("-");
                mDot8Pos.setBackgroundResource(R.drawable.input_box);
                DotR4 = null;
            }
        });

        spinnerBOAT.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                BluetoothAdapter adapterBOAT = BluetoothAdapter.getDefaultAdapter();
                if (mNameList.size() >= 1 && arg2 > 0) {
                    DotBOAT = adapterBOAT.getRemoteDevice(String.valueOf(mScannedSensorList.get(arg2 - 1)));
                    refreshPosList(arg2 - 1, "BOAT");
                    System.out.println(mScannedSensorList.get(arg2 - 1));
                    System.out.println(DotBOAT);
                    System.out.println(spinnerBOAT.getSelectedItem());
                    mSelectedSensorList.set(8, mScannedSensorList.get(arg2 - 1));
                    mSpinnerSelected.set(8, (String) spinnerBOAT.getSelectedItem());
                    deleteDuplicate(8, (String) spinnerBOAT.getSelectedItem());

                }
                if (arg2 == 0) {
                    refreshPosList(-1, "BOAT");
                    DotBOAT = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mDot9Pos.setText("-");
                mDot9Pos.setBackgroundResource(R.drawable.input_box);
                DotBOAT = null;
            }
        });

        spinner1_name.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (athleteNameList.size() >= 1 && arg2 > 0) {

                    String name = String.valueOf(athleteNameList.get(arg2));
                    String id = String.valueOf(athleteIDList.get(arg2));
                    mSelectedAtheleteName.set(0, name);
                    mSelectedAtheleteID.set(0, id);
                    System.out.println(name + ":" + id);
                    deleteDuplicateNameID(0, name);

                }

                if (arg2 == 0) {

                    mSelectedAtheleteName.set(0, "-");
                    mSelectedAtheleteID.set(0, "-");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner2_name.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (athleteNameList.size() >= 1 && arg2 > 0) {

                    String name = String.valueOf(athleteNameList.get(arg2));
                    String id = String.valueOf(athleteIDList.get(arg2));
                    mSelectedAtheleteName.set(1, name);
                    mSelectedAtheleteID.set(1, id);
                    System.out.println(name + ":" + id);
                    deleteDuplicateNameID(1, name);

                }

                if (arg2 == 0) {

                    mSelectedAtheleteName.set(1, "-");
                    mSelectedAtheleteID.set(1, "-");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner3_name.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (athleteNameList.size() >= 1 && arg2 > 0) {

                    String name = String.valueOf(athleteNameList.get(arg2));
                    String id = String.valueOf(athleteIDList.get(arg2));
                    mSelectedAtheleteName.set(2, name);
                    mSelectedAtheleteID.set(2, id);
                    System.out.println(name + ":" + id);
                    deleteDuplicateNameID(2, name);

                }

                if (arg2 == 0) {

                    mSelectedAtheleteName.set(2, "-");
                    mSelectedAtheleteID.set(2, "-");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner4_name.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (athleteNameList.size() >= 1 && arg2 > 0) {

                    String name = String.valueOf(athleteNameList.get(arg2));
                    String id = String.valueOf(athleteIDList.get(arg2));
                    mSelectedAtheleteName.set(3, name);
                    mSelectedAtheleteID.set(3, id);
                    System.out.println(name + ":" + id);
                    deleteDuplicateNameID(3, name);

                }

                if (arg2 == 0) {

                    mSelectedAtheleteName.set(3, "-");
                    mSelectedAtheleteID.set(3, "-");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner5_name.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (athleteNameList.size() >= 1 && arg2 > 0) {

                    String name = String.valueOf(athleteNameList.get(arg2));
                    String id = String.valueOf(athleteIDList.get(arg2));
                    mSelectedAtheleteName.set(4, name);
                    mSelectedAtheleteID.set(4, id);
                    System.out.println(name + ":" + id);
                    deleteDuplicateNameID(4, name);

                }

                if (arg2 == 0) {

                    mSelectedAtheleteName.set(4, "-");
                    mSelectedAtheleteID.set(4, "-");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner6_name.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (athleteNameList.size() >= 1 && arg2 > 0) {

                    String name = String.valueOf(athleteNameList.get(arg2));
                    String id = String.valueOf(athleteIDList.get(arg2));
                    mSelectedAtheleteName.set(5, name);
                    mSelectedAtheleteID.set(5, id);
                    System.out.println(name + ":" + id);
                    deleteDuplicateNameID(5, name);

                }

                if (arg2 == 0) {

                    mSelectedAtheleteName.set(5, "-");
                    mSelectedAtheleteID.set(5, "-");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner7_name.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (athleteNameList.size() >= 1 && arg2 > 0) {

                    String name = String.valueOf(athleteNameList.get(arg2));
                    String id = String.valueOf(athleteIDList.get(arg2));
                    mSelectedAtheleteName.set(6, name);
                    mSelectedAtheleteID.set(6, id);
                    System.out.println(name + ":" + id);
                    deleteDuplicateNameID(6, name);

                }

                if (arg2 == 0) {

                    mSelectedAtheleteName.set(6, "-");
                    mSelectedAtheleteID.set(6, "-");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner8_name.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (athleteNameList.size() >= 1 && arg2 > 0) {

                    String name = String.valueOf(athleteNameList.get(arg2));
                    String id = String.valueOf(athleteIDList.get(arg2));
                    mSelectedAtheleteName.set(7, name);
                    mSelectedAtheleteID.set(7, id);
                    System.out.println(name + ":" + id);
                    deleteDuplicateNameID(7, name);

                }

                if (arg2 == 0) {

                    mSelectedAtheleteName.set(7, "-");
                    mSelectedAtheleteID.set(7, "-");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        spinnerHR1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (mNameListBelt.size() >= 1 && arg2 > 0) {
                    String name = String.valueOf(spinnerHR1.getSelectedItem());
                    mSpinnerSelectedBelt.set(0, name);
                    Belt1 = getAddress(Belt1, name);
                    BeltName1 = name;
                    mBelt1Pos.setText((String) name);
                    mSelectedBeltList.set(0, Belt1);
                    mSelectedBeltNameList.set(0, BeltName1);
                    mBelt1Pos.setBackgroundResource(R.drawable.gradient_red);
                    mBelt1Pos.setTextColor(Color.WHITE);
                    deleteDuplicateBelt(0, name);
//                    System.out.println(Belt1.getAddress());
                }
                if (arg2 == 0) {
                    mBelt1Pos.setText("P1");
                    mBelt1Pos.setBackgroundResource(R.drawable.input_box);
                    mBelt1Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                    Belt1 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mBelt1Pos.setText("P1");
                mBelt1Pos.setBackgroundResource(R.drawable.input_box);
                mBelt1Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                Belt1 = null;
            }
        });

        spinnerHR2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (mNameListBelt.size() >= 1 && arg2 > 0) {
                    String name = String.valueOf(spinnerHR2.getSelectedItem());
                    mSpinnerSelectedBelt.set(1, name);
                    Belt2 = getAddress(Belt2, name);
                    BeltName2 = name;
                    mSelectedBeltList.set(1, Belt2);
                    mSelectedBeltNameList.set(1, BeltName2);
                    mBelt2Pos.setText((String) name);
                    mBelt2Pos.setBackgroundResource(R.drawable.gradient_red);
                    mBelt2Pos.setTextColor(Color.WHITE);
                    deleteDuplicateBelt(1, name);
//                    System.out.println(Belt2.getAddress());
                }
                if (arg2 == 0) {
                    mBelt2Pos.setText("P2");
                    mBelt2Pos.setBackgroundResource(R.drawable.input_box);
                    mBelt2Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                    Belt2 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mBelt2Pos.setText("P2");
                mBelt2Pos.setBackgroundResource(R.drawable.input_box);
                mBelt2Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                Belt2 = null;
            }
        });

        spinnerHR3.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (mNameListBelt.size() >= 1 && arg2 > 0) {
                    String name = String.valueOf(spinnerHR3.getSelectedItem());
                    mSpinnerSelectedBelt.set(2, name);
                    Belt3 = getAddress(Belt3, name);
                    BeltName3 = name;
                    mSelectedBeltList.set(2, Belt3);
                    mSelectedBeltNameList.set(2, BeltName3);
                    mBelt3Pos.setText((String) name);
                    mBelt3Pos.setBackgroundResource(R.drawable.gradient_red);
                    mBelt3Pos.setTextColor(Color.WHITE);
                    deleteDuplicateBelt(2, name);
//                    System.out.println(Belt3.getAddress());
                }
                if (arg2 == 0) {
                    mBelt3Pos.setText("P3");
                    mBelt3Pos.setBackgroundResource(R.drawable.input_box);
                    mBelt3Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                    Belt3 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mBelt3Pos.setText("P3");
                mBelt3Pos.setBackgroundResource(R.drawable.input_box);
                mBelt3Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                Belt3 = null;
            }
        });

        spinnerHR4.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (mNameListBelt.size() >= 1 && arg2 > 0) {
                    String name = String.valueOf(spinnerHR4.getSelectedItem());
                    mSpinnerSelectedBelt.set(3, name);
                    Belt4 = getAddress(Belt4, name);
                    BeltName4 = name;
                    mSelectedBeltList.set(3, Belt4);
                    mSelectedBeltNameList.set(3, BeltName4);
                    mBelt4Pos.setText((String) name);
                    mBelt4Pos.setBackgroundResource(R.drawable.gradient_red);
                    mBelt4Pos.setTextColor(Color.WHITE);
                    deleteDuplicateBelt(3, name);
//                    System.out.println(Belt4.getAddress());
                }
                if (arg2 == 0) {
                    mBelt4Pos.setText("P4");
                    mBelt4Pos.setBackgroundResource(R.drawable.input_box);
                    mBelt4Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                    Belt4 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mBelt4Pos.setText("P4");
                mBelt4Pos.setBackgroundResource(R.drawable.input_box);
                mBelt4Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                Belt4 = null;
            }
        });

        spinnerHR5.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (mNameListBelt.size() >= 1 && arg2 > 0) {
                    String name = String.valueOf(spinnerHR5.getSelectedItem());
                    mSpinnerSelectedBelt.set(4, name);
                    Belt5 = getAddress(Belt5, name);
                    BeltName5 = name;
                    mSelectedBeltList.set(4, Belt5);
                    mSelectedBeltNameList.set(4, BeltName5);
                    mBelt5Pos.setText((String) name);
                    mBelt5Pos.setBackgroundResource(R.drawable.gradient_red);
                    mBelt5Pos.setTextColor(Color.WHITE);
                    deleteDuplicateBelt(4, name);
//                    System.out.println(Belt5.getAddress());
                }
                if (arg2 == 0) {
                    mBelt5Pos.setText("P5");
                    mBelt5Pos.setBackgroundResource(R.drawable.input_box);
                    mBelt5Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                    Belt5 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mBelt5Pos.setText("P5");
                mBelt5Pos.setBackgroundResource(R.drawable.input_box);
                mBelt5Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                Belt5 = null;
            }
        });

        spinnerHR6.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (mNameListBelt.size() >= 1 && arg2 > 0) {
                    String name = String.valueOf(spinnerHR6.getSelectedItem());
                    mSpinnerSelectedBelt.set(5, name);
                    Belt6 = getAddress(Belt6, name);
                    BeltName6 = name;
                    mSelectedBeltList.set(5, Belt6);
                    mSelectedBeltNameList.set(5, BeltName6);
                    mBelt6Pos.setText((String) name);
                    mBelt6Pos.setBackgroundResource(R.drawable.gradient_red);
                    mBelt6Pos.setTextColor(Color.WHITE);
                    deleteDuplicateBelt(5, name);
//                    System.out.println(Belt6.getAddress());
                }
                if (arg2 == 0) {
                    mBelt6Pos.setText("P6");
                    mBelt6Pos.setBackgroundResource(R.drawable.input_box);
                    mBelt6Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                    Belt6 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mBelt6Pos.setText("P6");
                mBelt6Pos.setBackgroundResource(R.drawable.input_box);
                mBelt6Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                Belt6 = null;
            }
        });

        spinnerHR7.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (mNameListBelt.size() >= 1 && arg2 > 0) {
                    String name = String.valueOf(spinnerHR7.getSelectedItem());
                    mSpinnerSelectedBelt.set(6, name);
                    Belt7 = getAddress(Belt7, name);
                    BeltName7 = name;
                    mSelectedBeltList.set(6, Belt7);
                    mSelectedBeltNameList.set(6, BeltName7);
                    mBelt7Pos.setText((String) name);
                    mBelt7Pos.setBackgroundResource(R.drawable.gradient_red);
                    mBelt7Pos.setTextColor(Color.WHITE);
                    deleteDuplicateBelt(6, name);
//                    System.out.println(Belt7.getAddress());
                }
                if (arg2 == 0) {
                    mBelt7Pos.setText("P7");
                    mBelt7Pos.setBackgroundResource(R.drawable.input_box);
                    mBelt7Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                    Belt7 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mBelt7Pos.setText("P7");
                mBelt7Pos.setBackgroundResource(R.drawable.input_box);
                mBelt7Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                Belt7 = null;
            }
        });

        spinnerHR8.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (mNameListBelt.size() >= 1 && arg2 > 0) {
                    String name = String.valueOf(spinnerHR8.getSelectedItem());
                    mSpinnerSelectedBelt.set(7, name);
                    Belt8 = getAddress(Belt8, name);
                    BeltName8 = name;
                    mSelectedBeltList.set(7, Belt8);
                    mSelectedBeltNameList.set(7, BeltName8);
                    mBelt8Pos.setText((String) name);
                    mBelt8Pos.setBackgroundResource(R.drawable.gradient_red);
                    mBelt8Pos.setTextColor(Color.WHITE);
                    deleteDuplicateBelt(7, name);
//                    System.out.println(Belt8.getAddress());
                }
                if (arg2 == 0) {
                    mBelt8Pos.setText("P8");
                    mBelt8Pos.setBackgroundResource(R.drawable.input_box);
                    mBelt8Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                    Belt8 = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                mBelt8Pos.setText("P8");
                mBelt8Pos.setBackgroundResource(R.drawable.input_box);
                mBelt8Pos.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));
                Belt8 = null;
            }
        });


        mScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (checkBluetoothValid() == 1) {

                    initXsDotScanner();

                    if (!mIsScanning) {
                        mSelectedDots = 0;
                        mSelectedBelts = 0;
                        mDotsCount.setText("0");
                        mBeltCount.setText("0");
                        mScannedSensorList.clear();
                        mNameList.clear();
                        mNameList.add("-");
                        mNameListBelt.clear();
                        mDeviceListHR.clear();
                        mNameListBelt.add("-");


                        resetList();
                        mIsScanning = mXsDotScanner.startScan();
                        initPolar(1);
//                        scannerHR.Start();


                    } else {
                        mIsScanning = !mXsDotScanner.stopScan();
//                        scannerHR.Stop();
                        initPolar(0);

                    }

                    if (Objects.equals(Lang, "eng")){

                        mScan.setText(mIsScanning ? "Stop" : "Scan");

                    }else{

                        mScan.setText(mIsScanning ? "结束扫描" : "开始扫描");
                    }

                    mConnect.setAlpha(mIsScanning ? 0f : 1f);
                    mPowerOff.setAlpha(mIsScanning ? 0f : 1f);

                }
                ;


            }
        });

        mPowerOff.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                for (int i = 0; i < mDeviceList.size(); i++) {
                    mDeviceList.get(i).connect();
                }
                return true;
            }
        });

    }

    private void getAthleteNameIDList (String path) {
        File file = new File(path);
        if (file.exists()) {
            try{
                CSVReader reader = new CSVReader(new FileReader(path));
                List<String[]> nameData = reader.readAll();
                int iter = nameData.size();
                System.out.println(iter);

                athleteNameList.add("-");
                athleteIDList.add("-");

                for (int i = 0; i<iter-1;i++){

                    athleteNameList.add(nameData.get(i+1)[1]);
                    athleteIDList.add(nameData.get(i+1)[0]);
                    athleteHRMList.add(nameData.get(i+1)[2]);

                }

            }catch (IOException | CsvException e){

                e.printStackTrace();

            }

        }else{

            athleteNameList.add("-");
            athleteIDList.add("-");
        }

        System.out.println(athleteNameList);

        ArrayAdapter<String> adapter_name;
        adapter_name = new ArrayAdapter<>(this, R.layout.my_spinner_red, athleteNameList);
        adapter_name.setDropDownViewResource(R.layout.my_drop_down_red);


        for (int i = 0; i<8; i++){

            mSpinnerListName.get(i).setAdapter(adapter_name);
        }

    }
    private void initXsDotScanner() {
        if (mXsDotScanner == null) {
            mXsDotScanner = new XsensDotScanner(getApplicationContext(), (XsensDotScannerCallback) this);
            mXsDotScanner.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        }
    }
    private void deleteDuplicate(int pos, String selected) {

        for (int i = 0; i < 10; i++) {
            if (i != pos && mSpinnerSelected.get(i) == selected) {
                mSpinnerList.get(i).setSelection(0);
            }
        }
    }
    private int checkSelected() {

        for (int i = 0; i < 9; i++) {
            if (mSelectedSensorList.get(i) != null && mSelectedSensorList.get(i).getAddress() != "-") {
                mSelectedDots++;
            }
        }

//        for (int i = 0; i < 8; i++){
//            if (mSelectedBeltList.get(i) != null && mSelectedBeltList.get(i).getAddress() != "-" ){
//                mSelectedBelts ++;
//
//            }
//        }

        for (int i = 0; i < 8; i++) {
            if (mSelectedBeltNameList.get(i) != null && mSelectedBeltNameList.get(i) != "") {
                mSelectedBelts++;
            }
        }


        if (mSelectedDots >= 1 && mSelectedBelts >= 1) {
            return 1;
        } else if (mSelectedDots >= 1) {
            return 2;
        } else if (mSelectedBelts >= 1) {
            return 3;
        } else {
            return 4;
        }
    }
    private String getDotName(String address) {

        String name_1 = address.substring(address.length() - 2);
        String name_0 = address.substring(address.length() - 5, address.length() - 3);
        String name = name_0 + name_1;
        return name;
    }
    private void resetList() {
        for (int i = 0; i < mTvPosList.size(); i++) {

            mTvPosList.get(i).setText("-");
            mTvNameList.get(i).setText("-");
            mTvPosList.get(i).setBackgroundResource(R.drawable.input_box);

        }

        for (int i = 0; i < 9; i++) {
            mSelectedSensorList.set(i, null);
        }

        for (int i = 0; i < mTvBeltPosList.size(); i++) {

            String beltName = "";

            if (Objects.equals(Lang, "eng")){

                beltName = "P" + String.valueOf(i + 1) ;

            }else{

                beltName = String.valueOf(i + 1) + "号位";
            }

            mTvBeltPosList.get(i).setText(beltName);
            mTvBeltPosList.get(i).setBackgroundResource(R.drawable.input_box);
            mTvBeltPosList.get(i).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorGrayDark));

        }

        for (int i = 0; i < 8; i++) {
            mSelectedBeltList.set(i, null);
            mSpinnerListBelt.get(i).setSelection(0);
        }

    }
    private void refreshPosList(int newPos, String pos) {

        if (newPos == -1) {

            for (int i = 0; i < mTvPosList.size(); i++) {

                if (mTvPosList.get(i).getText() == pos) {
                    mTvPosList.get(i).setText("-");
                    mTvPosList.get(i).setBackgroundResource(R.drawable.input_box);
                }
            }

        } else {

            for (int i = 0; i < mTvPosList.size(); i++) {

                if (i == newPos) {
                    mTvPosList.get(i).setText(pos);
                    mTvPosList.get(i).setBackgroundResource(R.drawable.gradient_green);

                } else if (mTvPosList.get(i).getText() == pos) {
                    mTvPosList.get(i).setText("-");
                    mTvPosList.get(i).setBackgroundResource(R.drawable.input_box);
                }
            }
        }
    }

    @Override
    public void onXsensDotScanned(BluetoothDevice bluetoothDevice, int i) {

        if (!mScannedSensorList.contains(bluetoothDevice)) {
//            System.out.println(bluetoothDevice);
            String address = bluetoothDevice.getAddress();

            String name = getDotName(address);
            mNameList.add(name);
            mScannedSensorList.add(bluetoothDevice);
            System.out.println("mScannedSensorList Size:" + mScannedSensorList.size());

            ArrayAdapter<String> adapter;
            if (tabMode == 1) {
                adapter = new ArrayAdapter(this, R.layout.my_spinner_2_tab, mNameList);
                adapter.setDropDownViewResource(R.layout.my_drop_down_2_tab);
            } else {
                adapter = new ArrayAdapter(this, R.layout.my_spinner_2, mNameList);
                adapter.setDropDownViewResource(R.layout.my_drop_down_2);
            }

            spinnerL1.setAdapter(adapter);
            spinnerL2.setAdapter(adapter);
            spinnerL3.setAdapter(adapter);
            spinnerL4.setAdapter(adapter);
            spinnerR1.setAdapter(adapter);
            spinnerR2.setAdapter(adapter);
            spinnerR3.setAdapter(adapter);
            spinnerR4.setAdapter(adapter);
            spinnerBOAT.setAdapter(adapter);

            mDotsCount.setText(String.valueOf(mScannedSensorList.size()));

            if (mScannedSensorList.size() <= mTvNameList.size()) {
                mTvNameList.get(mScannedSensorList.size() - 1).setText(mNameList.get(mScannedSensorList.size()));
            }

            //odd

            XsensDotDevice mDeviceNew = new XsensDotDevice(getApplicationContext(), bluetoothDevice, BleConnector.this);
            mDeviceList.add(mDeviceNew);

        }
    }

    @Override
    public void onXsensDotConnectionChanged(String s, int i) {

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
    public void onXsensDotDataChanged(String s, XsensDotData xsensDotData) {

    }

    @Override
    public void onXsensDotInitDone(String s) {

        for (int i = 0; i < mDeviceList.size(); i++) {
            if (s.equals(mDeviceList.get(i).getAddress())) {
                mDeviceList.get(i).powerOffDevice();
            }
        }
        resetList();
        mIsScanning = mXsDotScanner.startScan();

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

    private void initPolar(int i) {

        if (scanDisposable == null && i == 1) {

            scanDisposable = api.searchForDevice()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            polarDeviceInfo
//                                    -> Log.d(TAG, "polar device found id: " + polarDeviceInfo.deviceId + " address: " + polarDeviceInfo.address + " rssi: " + polarDeviceInfo.rssi + " name: " + polarDeviceInfo.name + " isConnectable: " + polarDeviceInfo.isConnectable),
                                    -> addHRtoList(polarDeviceInfo.getDeviceId()),
                            throwable
                                    -> Log.d(TAG, "" + throwable.getLocalizedMessage()),
                            ()
                                    -> Log.d(TAG, "complete"));

        } else {
            scanDisposable.dispose();
            scanDisposable = null;

        }
    }


    private void addHRtoList(String id) {

        if (!mNameListBelt.contains(id) && id != "") {
            mNameListBelt.add(id);
            ArrayAdapter<String> adapter_hr;

            if (tabMode == 1) {
                adapter_hr = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner_red_tab, mNameListBelt);
                adapter_hr.setDropDownViewResource(R.layout.my_drop_down_red_tab);
            } else {
                adapter_hr = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner_red, mNameListBelt);
                adapter_hr.setDropDownViewResource(R.layout.my_drop_down_red);
            }

            spinnerHR1.setAdapter(adapter_hr);
            spinnerHR2.setAdapter(adapter_hr);
            spinnerHR3.setAdapter(adapter_hr);
            spinnerHR4.setAdapter(adapter_hr);
            spinnerHR5.setAdapter(adapter_hr);
            spinnerHR6.setAdapter(adapter_hr);
            spinnerHR7.setAdapter(adapter_hr);
            spinnerHR8.setAdapter(adapter_hr);

            mBeltCount.setText(String.valueOf(mNameListBelt.size() - 1));


        }

    }

    private void initHR() {

        scannerHR = new BleScanner();
        mDeviceListHR = new ArrayList<>();
        mNameListBelt = new ArrayList<>();

        LiveData<ArrayList<BluetoothDevice>> bluetoothDevices = scannerHR.getBluetoothState();
        bluetoothDevices.observe(BleConnector.this, new Observer<ArrayList<BluetoothDevice>>() {
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
                        String nameBelt = getDotName(addressBelt);

                        if (!mNameListBelt.contains(nameBelt)) {
                            mNameListBelt.add(nameBelt);

                            ArrayAdapter<String> adapter_hr;
                            if (tabMode == 1) {
                                adapter_hr = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner_red_tab, mNameListBelt);
                                adapter_hr.setDropDownViewResource(R.layout.my_drop_down_red_tab);
                            } else {
                                adapter_hr = new ArrayAdapter(getApplicationContext(), R.layout.my_spinner_red, mNameListBelt);
                                adapter_hr.setDropDownViewResource(R.layout.my_drop_down_red);
                            }

                            spinnerHR1.setAdapter(adapter_hr);
                            spinnerHR2.setAdapter(adapter_hr);
                            spinnerHR3.setAdapter(adapter_hr);
                            spinnerHR4.setAdapter(adapter_hr);
                            spinnerHR5.setAdapter(adapter_hr);
                            spinnerHR6.setAdapter(adapter_hr);
                            spinnerHR7.setAdapter(adapter_hr);
                            spinnerHR8.setAdapter(adapter_hr);

                        }
//
//                        beltL1 = mDeviceListHR.get(i);
//                        System.out.println("BleDevice:" + i + "----" + mDeviceListHR.get(i));
                    }
                }

            }
        });
    }

    private void deleteDuplicateBelt(int pos, String selected) {

        for (int i = 0; i < 8; i++) {
            if (i != pos && mSpinnerSelectedBelt.get(i) == selected) {
                mSpinnerListBelt.get(i).setSelection(0);
//                System.out.println(mSpinnerSelectedBelt.get(i));
            }
        }
    }

    private void deleteDuplicateNameID(int pos, String selected) {

        for (int i = 0; i < 8; i++) {
            if (i != pos && mSelectedAtheleteName.get(i) == selected) {
                mSpinnerListName.get(i).setSelection(0);
                mSelectedAtheleteName.set(i,"-");
                mSelectedAtheleteID.set(i,"-");


//                System.out.println(mSpinnerSelectedBelt.get(i));
            }
        }

        System.out.println(mSelectedAtheleteName);
        System.out.println(mSelectedAtheleteID);
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
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("错误").setMessage("你的设备不具备蓝牙功能!").create();
            dialog.show();
            bluetoothStatus = 0;
        }

        String notice;
        String confirm;
        String title;

        if (Objects.equals(Lang, "eng")){

            notice = "Please Turn On Bluetooth for Scanning";
            confirm = "Yes";
            title = "Notification";

        }else{
            notice = "蓝牙设备未打开,请开启此功能后重试!";
            confirm = "确认";
            title = "提示";
        }

        if (!adapter.isEnabled()) {
            AlertDialog dialog = new AlertDialog.Builder(this).setTitle(title)
                    .setMessage(notice)
                    .setPositiveButton(confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            if (ActivityCompat.checkSelfPermission(BleConnector.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
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

        if(adapter.isEnabled()) {
            bluetoothStatus = 1;
        }

        return bluetoothStatus;
    }
}