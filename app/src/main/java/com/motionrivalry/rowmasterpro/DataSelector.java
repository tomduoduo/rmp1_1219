package com.motionrivalry.rowmasterpro;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import Jama.Matrix;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.motionrivalry.rowmasterpro.Dashboard.createPath;
import static com.motionrivalry.rowmasterpro.MainActivity.cookieManager;

public class DataSelector extends AppCompatActivity {

    private List<Map<String, Object>> logList = new ArrayList<>();

    private SimpleAdapter adapter;
    private ListView mListView;
    private String fileLoc = null;
    private String entryData = null;
    private ArrayList<String> fileList = null;
    private ArrayList<String> fileListTrim = null;
    private ArrayList<String> fileAmount = null;
    private ArrayList<String> fileNumber = null;
    private ArrayList<Integer> existL1 = new ArrayList<>();
    private ArrayList<Integer> existL2 = new ArrayList<>();
    private ArrayList<Integer> existL3 = new ArrayList<>();
    private ArrayList<Integer> existL4 = new ArrayList<>();
    private ArrayList<Integer> existR1 = new ArrayList<>();
    private ArrayList<Integer> existR2 = new ArrayList<>();
    private ArrayList<Integer> existR3 = new ArrayList<>();
    private ArrayList<Integer> existR4 = new ArrayList<>();
    private ArrayList<Integer> existBoat = new ArrayList<>();
    private ArrayList<Integer> starList = new ArrayList<>();
    private FileWriter mStarFileWriter = null;
    private CSVWriter mStarCSVWriter = null;
    private List<String[]> allStarList = null;

    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter mAdapter;

    private ArrayList<String> currentPathList = new ArrayList<>();

    private String uploadPATH = "";

    private PopupWindow windowUpload = null;
    private View popupWindowUpload;
    private int currentUpload = 0;
    private int totalUpload = 0;
    private int uploadFailTrigger = 0;

    private TextView totalUploadNum;
    private TextView currentUploadNum;
    private String uploadFileLoc;

    private PopupWindow windowProcess = null;
    private View popupWindowProcess;
    private int currentProcess = 0;
    private int totalProcess = 0;
    private TextView totalProcessNum;
    private TextView currentProcessNum;

    private ArrayList<String> preparationList = null;
    private ArrayList<String> targetFileList = null;
    private List<String[]> phoneData = null;

    private String samplingRate = "30";
    private String ID_P1 = "";
    private String ID_P2 = "";
    private String ID_P3 = "";
    private String ID_P4 = "";
    private String ID_P5 = "";
    private String ID_P6 = "";
    private String ID_P7 = "";
    private String ID_P8 = "";

    private String ID_UDF = "00000000-0000-0000-0000-000000000000";

    private String posStatus_L1 = "0";
    private String posStatus_L2 = "0";
    private String posStatus_L3 = "0";
    private String posStatus_L4 = "0";
    private String posStatus_R1 = "0";
    private String posStatus_R2 = "0";
    private String posStatus_R3 = "0";
    private String posStatus_R4 = "0";

    private String boatType = "UDF";
    private String boatTypeConfirmed = "UDF";

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

    private Thread thread0;
    private int[] sizeArray;
    private int itrMax = 0;
    private int playNodePos = 0;

    private List<String[]> outputMatrix = new ArrayList<>();

    private double[] UI_params_L1 = new double[] {
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0, 0, 0,
            0, 0, 0,
            0, 0, 0, 0, 0, 0 };

    // strokeSpeed[18], featherAngle[19], featherSpeed[20], oarDepth[21],
    // strokeForce[22],
    // strokeWattage[23], featherAngleCache[24];

    private double[] UI_params_L2 = new double[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0 };
    private double[] UI_params_L3 = new double[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0 };
    private double[] UI_params_L4 = new double[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0 };

    private double[] UI_params_R1 = new double[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0 };
    private double[] UI_params_R2 = new double[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0 };
    private double[] UI_params_R3 = new double[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0 };
    private double[] UI_params_R4 = new double[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0,
            0 };
    private int[] HR_matching_POS = new int[] { 0, 2, 4, 6, 1, 3, 5, 7 };

    // {yaw[0], txYaw[1], avMin[2], avMax[3], txDegreeFwd[4], txDegreeBwd[5],
    // Roll[6], Pitch[7], YawMin[8], YawMax[9], YawAbs[10],
    // YawBeg[11], YawEnd[12], YawDuration[13], DynamicData[14],
    // StrokeVelocityLast[15]}
    // StrokeTimeLast[16], instantBoatSpeed[17], boatAcceleration[18]}
    // Dynamic Data: strokeSpeed[10], catchDegree[2], strokeDepth[3]

    // outputMatrix:
    // Sampling Rate, txDegreeFwd[4], txDegreeFwd[5], YawAbs[10] , HeartRate[]
    // strokeSpeed, featherAngle, featherSpeed, oarDepth, strokeForce
    //

    // String[] header = new String[]{
    // String.valueOf(samplingRate), "fwd_degree", "bwd_degree", "total_degree",
    // "total_wattage",
    // "stoke_speed", "feather_angle", "feather_speed", "oar_depth", "stroke_force",
    // "stroke_rate", "boat_speed", "split", "boat_yaw", "boat_roll",
    // "boat_accel", "heart_rate",
    // };

    double boatAngle = 0;
    double correctionLeft = 90;
    double correctionRight = 90;
    double correctionPeddle = 135;
    // double correctionPeddle = 225;
    // original setting for record
    double correctionRightPitch = 0;

    private double correctionLeftSecondary = 0;
    private double correctionRightSecondary = 0;

    private double[] CacheStrokeAV = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    private double[] CacheLengthStrokeAV = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    private double[] CacheLocalAV_0 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0 };

    private double speedSuppressionRatio = 0.6;
    private double depthSuppressionRatio = 0.7;
    private double wattageSuppressionRatio = 0.50;
    private double forceAmplifier = 1;
    private double rightCompensateRatioLow = 1.0;
    private double rightCompensateRatioHigh = 1.35;
    private double rightCompensateRatio = 1.0;

    private double speedNormMax = 3.5;
    private double speedNormMin = 0;
    private double depthNormMax = 10;
    private double depthNormMin = -15;
    private double powerNormMax = 800;
    private double powerNormMin = 0;

    private double p00 = 2.514;
    private double p10 = -2.107;
    private double p01 = -6.25;
    private double p20 = 1.509;
    private double p11 = 2.679;
    private double p02 = 3.826;

    private int T = 1;
    private double[][] A0 = { { 1, T }, { 0, 1 } };
    private double[][] G0 = { { Math.pow(T, 2) / 2 }, { T } };
    private double[][] H0 = { { 1, 0 } };
    private double[][] Xu0 = { { 0 }, { 0 } };
    private double[][] Pu0 = { { 0, 0 }, { 0, 0 } };
    private double[][] I0 = { { 1, 0 }, { 0, 1 } };

    private double[][] Q0 = { { 0.05 } };
    private double[][] R0 = { { 5 } };

    private Matrix Q = new Matrix(Q0);
    private Matrix R1 = new Matrix(R0);

    private Matrix A = new Matrix(A0);
    private Matrix G = new Matrix(G0);
    private Matrix H = new Matrix(H0);
    private Matrix I = new Matrix(I0);

    private Matrix Xu = new Matrix(Xu0);
    private Matrix Pu = new Matrix(Pu0);
    private Matrix Xp = null;
    private Matrix Pp = null;
    private Matrix K = null;

    private ArrayList<Matrix> XuList = new ArrayList<>();
    private ArrayList<Matrix> PuList = new ArrayList<>();
    private ArrayList<Matrix> XpList = new ArrayList<>();
    private ArrayList<Matrix> PpList = new ArrayList<>();
    private ArrayList<Matrix> KList = new ArrayList<>();

    private int forceCacheLength = 10;
    private int[][] forceCacheSizeList = new int[8][1];
    private int[][] forceCachePointerList = new int[8][1];
    private double[][] forceCacheSumList = new double[8][1];
    private double[][] forceCacheSamplesList = new double[8][forceCacheLength];

    private double wattage_p1_4 = -99.08;
    private double wattage_p2_4 = 466.6;
    private double wattage_p3_4 = -581.7;
    private double wattage_p4_4 = 331.6;
    private double wattage_p5_4 = 1.714;

    private double fwdSplitRatio = 0.40;
    private double fwdSplitThreshSpeed = 1.35;
    private double fwdSplitDivideFactor = 50;
    private double fwdSplitRandomSeedDivideFactor = 50;

    private int speedMultiple = 1;

    private Matrix Xu1 = new Matrix(Xu0);
    private Matrix Pu1 = new Matrix(Pu0);
    private Matrix Xp1 = null;
    private Matrix Pp1 = null;
    private Matrix K1 = null;

    private Matrix Xu2 = new Matrix(Xu0);
    private Matrix Pu2 = new Matrix(Pu0);
    private Matrix Xp2 = null;
    private Matrix Pp2 = null;
    private Matrix K2 = null;

    private Matrix Xu3 = new Matrix(Xu0);
    private Matrix Pu3 = new Matrix(Pu0);
    private Matrix Xp3 = null;
    private Matrix Pp3 = null;
    private Matrix K3 = null;

    private Matrix Xu4 = new Matrix(Xu0);
    private Matrix Pu4 = new Matrix(Pu0);
    private Matrix Xp4 = null;
    private Matrix Pp4 = null;
    private Matrix K4 = null;

    private Matrix Xu5 = new Matrix(Xu0);
    private Matrix Pu5 = new Matrix(Pu0);
    private Matrix Xp5 = null;
    private Matrix Pp5 = null;
    private Matrix K5 = null;

    private Matrix Xu6 = new Matrix(Xu0);
    private Matrix Pu6 = new Matrix(Pu0);
    private Matrix Xp6 = null;
    private Matrix Pp6 = null;
    private Matrix K6 = null;

    private Matrix Xu7 = new Matrix(Xu0);
    private Matrix Pu7 = new Matrix(Pu0);
    private Matrix Xp7 = null;
    private Matrix Pp7 = null;
    private Matrix K7 = null;

    private Matrix Xu8 = new Matrix(Xu0);
    private Matrix Pu8 = new Matrix(Pu0);
    private Matrix Xp8 = null;
    private Matrix Pp8 = null;
    private Matrix K8 = null;

    private float pixelDensity;
    private List<HttpCookie> listCookie;
    private String Lang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Lang = getIntent().getStringExtra("lang");

        if (Objects.equals(Lang, "eng")) {

            setContentView(R.layout.activity_data_selector_eng);

        } else {

            setContentView(R.layout.activity_data_selector);
        }

        // mListView = findViewById(R.id.lv_log_list);

        pixelDensity = getResources().getDisplayMetrics().density;

        listCookie = cookieManager.getCookieStore().getCookies();

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

        mRecyclerView = findViewById(R.id.lv_log_list_recycler);

        fileLoc = this.getFilesDir() + "/xsens/";
        String entryDataKeyWord = "entryData_000000000";
        entryData = fileLoc + "entryData_0000000000.csv";

        createPath(fileLoc);
        createEntryDataFile(entryData);
        armStarLogger(entryData);

        uploadFileLoc = this.getFilesDir() + "/upload/";
        createPath(uploadFileLoc);

        fileList = getFileList(fileLoc, "csv");
        fileListTrim = new ArrayList<>();
        fileAmount = new ArrayList<>();
        fileNumber = new ArrayList<>();
        int entryNumber = 0;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (Objects.equals(Lang, "eng")) {

            popupWindowUpload = inflater.inflate(R.layout.popup_upload_eng, null, false);

        } else {

            popupWindowUpload = inflater.inflate(R.layout.popup_upload, null, false);
        }

        windowUpload = new PopupWindow(popupWindowUpload, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        windowUpload.setOutsideTouchable(false);
        windowUpload.setFocusable(false);

        totalUploadNum = popupWindowUpload.findViewById(R.id.upload_total_text);
        currentUploadNum = popupWindowUpload.findViewById(R.id.upload_current_text);

        if (Objects.equals(Lang, "eng")) {

            popupWindowProcess = inflater.inflate(R.layout.popup_processing_eng, null, false);

        } else {

            popupWindowProcess = inflater.inflate(R.layout.popup_processing, null, false);
        }

        windowProcess = new PopupWindow(popupWindowProcess, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);
        windowProcess.setOutsideTouchable(false);
        windowProcess.setFocusable(false);

        totalProcessNum = popupWindowProcess.findViewById(R.id.process_total_text);
        currentProcessNum = popupWindowProcess.findViewById(R.id.process_current_text);

        generateList(entryNumber, entryDataKeyWord);

        String[] from = { "number", "amount", "entry", "l1", "l2", "l3", "l4", "r1", "r2", "r3", "r4", "boat" };
        int[] to = { R.id.log_number, R.id.dot_amount, R.id.log_entry, R.id.dot_l1_ds, R.id.dot_l2_ds, R.id.dot_l3_ds,
                R.id.dot_l4_ds, R.id.dot_r1_ds, R.id.dot_r2_ds, R.id.dot_r3_ds, R.id.dot_r4_ds, R.id.dot_boat_ds };

        mAdapter = new MyRecyclerViewAdapter(logList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter.setOnItemClickListener(new MyRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position, String logTime) {

                if (v.getId() == R.id.button_star_ds) {

                    Toast.makeText(DataSelector.this, "请长按星号以标记", Toast.LENGTH_SHORT).show();

                } else if (v.getId() == R.id.button_trash_ds) {

                    Toast.makeText(DataSelector.this, "请长按删除键以删除", Toast.LENGTH_SHORT).show();

                } else {
                    v.setBackgroundResource(R.drawable.gradient_black);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            Intent intent = new Intent(DataSelector.this, DataPlayback.class);
                            intent.putExtra("logTime", logTime);
                            intent.putExtra("lang", Lang);
                            v.setBackgroundResource(R.drawable.gradient_black_2);
                            Toast.makeText(DataSelector.this, logTime, Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            Looper.loop();
                        }
                    }, 200);

                }
            }

            @Override
            public void onLongClick(View v, int position, String logTime) {

                if (v.getId() == R.id.button_star_ds) {

                    try {
                        mStarFileWriter = new FileWriter(entryData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mStarCSVWriter = new CSVWriter(mStarFileWriter,
                            CSVWriter.DEFAULT_SEPARATOR,
                            CSVWriter.NO_QUOTE_CHARACTER,
                            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                            CSVWriter.RFC4180_LINE_END);

                    int exist = 0;

                    for (int i = 1; i < allStarList.size(); i++) {

                        if (logTime.equals(allStarList.get(i)[0])) {

                            v.setBackgroundResource(R.drawable.star_0);
                            logList.get(position).replace("star", R.drawable.star_0);
                            mAdapter.notifyDataSetChanged();
                            allStarList.remove(i);
                            exist = 1;
                        }
                    }

                    if (exist == 0) {
                        v.setBackgroundResource(R.drawable.star_1);
                        logList.get(position).replace("star", R.drawable.star_1);
                        mAdapter.notifyDataSetChanged();
                        String[] currentLog = { logTime, "0" };
                        allStarList.add(allStarList.size(), currentLog);
                    }

                    mStarCSVWriter.writeAll(allStarList);
                    try {
                        mStarCSVWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (v.getId() == R.id.button_trash_ds) {

                    System.out.println((Integer) R.drawable.star_1);
                    System.out.println(logList.get(position).get("star"));

                    int now = (Integer) logList.get(position).get("star");
                    int then = R.drawable.star_1;
                    int dif = now - then;

                    System.out.println(dif);

                    if (dif == 0) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(DataSelector.this);

                        if (Objects.equals(Lang, "eng")) {

                            builder.setTitle("Marked files cannot be deleted");
                            builder.setMessage("To delete, unmarked first");
                            builder.setPositiveButton("Noted", null);
                        } else {

                            builder.setTitle("无法删除被标星的数据");
                            builder.setMessage("如要删除，请先取消标星");
                            builder.setPositiveButton("返回", null);
                        }

                        builder.show();

                    } else {
                        fileDeleteNotification(v, position, logTime);
                    }

                } else if (v.getId() == R.id.button_upload_ds) {

                    fileUploadNotification(logTime);

                }
            }
        });

        // adapter = new MyAdapter(this, logList, R.layout.simple_entry, from, to);
        // mListView.setAdapter(adapter);

        // mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        // @Override
        // public void onItemClick(AdapterView<?> adapterView, final View view, int i,
        // long l) {
        //
        // HashMap<String,Object > map_item = (HashMap<String,Object
        // >)mListView.getItemAtPosition(i);
        // final String logTime = map_item.get("entry")+"";
        // view.setBackgroundResource(R.drawable.gradient_black);
        //
        //
        // Timer timer = new Timer();
        // timer.schedule(new TimerTask() {
        // @Override
        // public void run() {
        // Looper.prepare();
        // Intent intent = new Intent(DataSelector.this, DataPlayback.class);
        // intent.putExtra("logTime", logTime);
        // Toast.makeText(DataSelector.this, logTime, Toast.LENGTH_SHORT).show();
        // view.setBackgroundResource(R.drawable.gradient_black_2);
        // startActivity(intent);
        // Looper.loop();
        // }
        // },200);
        //
        //
        // }
        //
        // });

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

    public void createEntryDataFile(String fileIn) {

        File file = new File(fileIn);

        if (!file.exists()) {

            try {
                final FileWriter initiateFileWriter = new FileWriter(entryData);
                final CSVWriter initiateCSVWriter = new CSVWriter(initiateFileWriter);
                String[] header = new String[] { "fileName", "Star" };
                String[] fillEmpty = new String[] { "0", "0" };
                initiateCSVWriter.writeNext(header);
                initiateCSVWriter.writeNext(fillEmpty);
                initiateCSVWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("fileCreation", "error");
            }

        }
    }

    public void armStarLogger(String starFile) {

        File file = new File(starFile);
        if (file.exists()) {
            try {
                CSVReader starReader = new CSVReader(new FileReader(starFile));
                allStarList = starReader.readAll();
                starReader.close();

                System.out.println("Arm successful");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (CsvException e) {
                e.printStackTrace();
            }

        }

    }

    private void generateList(int number, String keyWord) {

        for (int i = 0; i < fileList.size(); i++) {

            String name = fileList.get(i).substring(0, 19);

            if (!fileListTrim.contains(name) && !name.equals(keyWord)) {

                number++;
                fileListTrim.add(name);
                fileNumber.add(String.valueOf(number));
            }
        }

        fileListTrim = invertOrderList(fileListTrim);

        for (int k = 0; k < fileListTrim.size(); k++) {

            String name_1 = fileListTrim.get(k);
            // System.out.println(fileListTrim.get(k));
            int dot = 0;
            int[] existList = new int[] { R.drawable.l1_0, R.drawable.l2_0, R.drawable.l3_0, R.drawable.l4_0,
                    R.drawable.r1_0, R.drawable.r2_0, R.drawable.r3_0, R.drawable.r4_0, R.drawable.boat_0,
                    R.drawable.star_0 };
            for (int n = 0; n < fileList.size(); n++) {
                if (fileList.get(n).contains(name_1)) {
                    String name_2 = fileList.get(n).substring(fileList.get(n).length() - 6,
                            fileList.get(n).length() - 4);
                    // System.out.println(name_2);
                    dot++;
                    switch (name_2) {
                        case "L1":
                            existList[0] = R.drawable.l1_1;
                            break;
                        case "L2":
                            existList[1] = R.drawable.l2_1;
                            break;
                        case "L3":
                            existList[2] = R.drawable.l3_1;
                            break;
                        case "L4":
                            existList[3] = R.drawable.l4_1;
                            break;
                        case "R1":
                            existList[4] = R.drawable.r1_1;
                            break;
                        case "R2":
                            existList[5] = R.drawable.r2_1;
                            break;
                        case "R3":
                            existList[6] = R.drawable.r3_1;
                            break;
                        case "R4":
                            existList[7] = R.drawable.r4_1;
                            break;
                        case "BO":
                            existList[8] = R.drawable.boat_1;
                            break;
                    }
                }
            }
            for (int m = 1; m < allStarList.size(); m++) {

                if (name_1.equals(allStarList.get(m)[0])) {

                    existList[9] = R.drawable.star_1;

                }

            }

            fileAmount.add(String.valueOf(dot - 1));
            existL1.add(existList[0]);
            existL2.add(existList[1]);
            existL3.add(existList[2]);
            existL4.add(existList[3]);
            existR1.add(existList[4]);
            existR2.add(existList[5]);
            existR3.add(existList[6]);
            existR4.add(existList[7]);
            existBoat.add(existList[8]);
            starList.add(existList[9]);
        }

        for (int i = 0; i < fileListTrim.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("number", fileNumber.get(i));
            map.put("amount", fileAmount.get(i));
            map.put("entry", fileListTrim.get(i));
            map.put("l1", existL1.get(i));
            map.put("l2", existL2.get(i));
            map.put("l3", existL3.get(i));
            map.put("l4", existL4.get(i));
            map.put("r1", existR1.get(i));
            map.put("r2", existR2.get(i));
            map.put("r3", existR3.get(i));
            map.put("r4", existR4.get(i));
            map.put("boat", existBoat.get(i));
            map.put("star", starList.get(i));
            logList.add(map);
        }

    }

    private void deleteLogs(String fileLogTime) {

        fileList = getFileList(fileLoc, "csv");
        for (int i = 0; i < fileList.size(); i++) {
            String name = fileList.get(i).substring(0, 19);
            if (name.equals(fileLogTime)) {
                File file = new File(fileLoc + fileList.get(i));

                if (file.isFile() && file.exists()) {
                    file.delete();
                }
            }
        }
    }

    private void uploadLogs(String fileLogTime) throws IOException, CsvException {

        preparationList = new ArrayList<>();
        targetFileList = new ArrayList<>();
        totalUpload = 0;

        posStatus_L1 = "0";
        posStatus_L2 = "0";
        posStatus_L3 = "0";
        posStatus_L4 = "0";
        posStatus_R1 = "0";
        posStatus_R2 = "0";
        posStatus_R3 = "0";
        posStatus_R4 = "0";

        String fileLogTimeAlter = fileLogTime.replace("_", "-");
        fileLogTimeAlter = fileLogTimeAlter.replace(" ", "-");

        for (int i = 0; i < fileList.size(); i++) {
            String name = fileList.get(i).substring(0, 19);
            if (name.equals(fileLogTime)) {
                File file = new File(fileLoc + fileList.get(i));
                String affix = fileList.get(i).substring(20, 22);
                System.out.println(affix);
                if (affix.equals("PH")) {

                    try {
                        CSVReader phoneDataReader = new CSVReader(new FileReader(file));
                        phoneData = phoneDataReader.readAll();

                        try {
                            samplingRate = phoneData.get(0)[18];
                            boatType = phoneData.get(0)[27];

                            if (!phoneData.get(0)[19].equals("-")) {
                                ID_P1 = phoneData.get(0)[19];
                            } else {
                                ID_P1 = "00000000-0000-0000-0000-000000000000";
                            }

                            if (!phoneData.get(0)[20].equals("-")) {
                                ID_P2 = phoneData.get(0)[20];
                            } else {
                                ID_P2 = "00000000-0000-0000-0000-000000000000";
                            }

                            if (!phoneData.get(0)[21].equals("-")) {
                                ID_P3 = phoneData.get(0)[21];
                            } else {
                                ID_P3 = "00000000-0000-0000-0000-000000000000";
                            }

                            if (!phoneData.get(0)[22].equals("-")) {
                                ID_P4 = phoneData.get(0)[22];
                            } else {
                                ID_P4 = "00000000-0000-0000-0000-000000000000";
                            }

                            if (!phoneData.get(0)[23].equals("-")) {
                                ID_P5 = phoneData.get(0)[23];
                            } else {
                                ID_P5 = "00000000-0000-0000-0000-000000000000";
                            }

                            if (!phoneData.get(0)[24].equals("-")) {
                                ID_P6 = phoneData.get(0)[24];
                            } else {
                                ID_P6 = "00000000-0000-0000-0000-000000000000";
                            }

                            if (!phoneData.get(0)[25].equals("-")) {
                                ID_P7 = phoneData.get(0)[25];
                            } else {
                                ID_P7 = "00000000-0000-0000-0000-000000000000";
                            }

                            if (!phoneData.get(0)[26].equals("-")) {
                                ID_P8 = phoneData.get(0)[26];
                            } else {
                                ID_P8 = "00000000-0000-0000-0000-000000000000";
                            }

                        } catch (ArrayIndexOutOfBoundsException e) {

                            samplingRate = "20";
                            boatType = "UDF";

                            ID_P1 = "00000000-0000-0000-0000-000000000000";
                            ID_P2 = "00000000-0000-0000-0000-000000000000";
                            ID_P3 = "00000000-0000-0000-0000-000000000000";
                            ID_P4 = "00000000-0000-0000-0000-000000000000";
                            ID_P5 = "00000000-0000-0000-0000-000000000000";
                            ID_P6 = "00000000-0000-0000-0000-000000000000";
                            ID_P7 = "00000000-0000-0000-0000-000000000000";
                            ID_P8 = "00000000-0000-0000-0000-000000000000";

                            // ID_L1 = "00000000-0000-0000-0000-000000000000";

                            System.out.println("assigned sampling Rate is: " + samplingRate);

                        }

                        System.out.println("P1 ID: " + ID_P1);
                        System.out.println("P2 ID: " + ID_P2);
                        System.out.println("P3 ID: " + ID_P3);
                        System.out.println("P4 ID: " + ID_P4);
                        System.out.println("P5 ID: " + ID_P5);
                        System.out.println("P6 ID: " + ID_P6);
                        System.out.println("P7 ID: " + ID_P7);
                        System.out.println("P8 ID: " + ID_P8);

                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (CsvException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (file.isFile() && file.exists()) {
                    totalUpload++;
                }
            }
        }

        System.out.println("boatType:" + boatType);

        for (int i = 0; i < fileList.size(); i++) {
            String name = fileList.get(i).substring(0, 19);
            if (name.equals(fileLogTime)) {
                File file = new File(fileLoc + fileList.get(i));
                String affix_1 = fileList.get(i).substring(20, 22);
                String name_1 = fileList.get(i).substring(0, 22) + ".csv";

                String filename = "";

                if (file.isFile() && file.exists()) {

                    if (affix_1.equals("L1")) {

                        switch (boatType) {
                            case "1x":

                                boatTypeConfirmed = "1x";
                                filename = fileLogTimeAlter + "_" + ID_P1 + "_L1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "2x":

                                boatTypeConfirmed = "2x";
                                filename = fileLogTimeAlter + "_" + ID_P1 + "_L1" + "_" + boatTypeConfirmed + ".csv";

                                break;

                            case "4x":
                            case "4x_front":

                                boatTypeConfirmed = "4x";
                                filename = fileLogTimeAlter + "_" + ID_P1 + "_L1" + "_" + boatTypeConfirmed + ".csv";

                                break;

                            case "2mi":

                                boatTypeConfirmed = "2-";
                                filename = fileLogTimeAlter + "_" + ID_P1 + "_L1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "2miRev":

                                boatTypeConfirmed = "2-";
                                filename = fileLogTimeAlter + "_" + ID_P2 + "_L1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "4mi":

                                boatTypeConfirmed = "4-";
                                filename = fileLogTimeAlter + "_" + ID_P1 + "_L1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "4miRev":

                                boatTypeConfirmed = "4-";
                                filename = fileLogTimeAlter + "_" + ID_P2 + "_L1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8mi":
                            case "8mi_front":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P1 + "_L1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8miRev":
                            case "8mi_frontRev":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P2 + "_L1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "UDF":

                                boatTypeConfirmed = "UDF";
                                filename = fileLogTimeAlter + "_" + ID_UDF + "_L1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                        }

                        preparationList.add(filename);
                        targetFileList.add(name_1);
                        mountData(name_1);

                    } else if (affix_1.equals("R1")) {

                        switch (boatType) {
                            case "1x":

                                boatTypeConfirmed = "1x";
                                filename = fileLogTimeAlter + "_" + ID_P1 + "_R1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "2x":

                                boatTypeConfirmed = "2x";
                                filename = fileLogTimeAlter + "_" + ID_P1 + "_R1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "4x":
                            case "4x_front":

                                boatTypeConfirmed = "4x";
                                filename = fileLogTimeAlter + "_" + ID_P1 + "_R1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "2mi":

                                boatTypeConfirmed = "2-";
                                filename = fileLogTimeAlter + "_" + ID_P2 + "_R1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "2miRev":

                                boatTypeConfirmed = "2-";
                                filename = fileLogTimeAlter + "_" + ID_P1 + "_R1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "4mi":

                                boatTypeConfirmed = "4-";
                                filename = fileLogTimeAlter + "_" + ID_P2 + "_R1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "4miRev":

                                boatTypeConfirmed = "4-";
                                filename = fileLogTimeAlter + "_" + ID_P1 + "_R1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8mi":
                            case "8mi_front":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P2 + "_R1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8miRev":
                            case "8mi_frontRev":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P1 + "_R1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "UDF":

                                boatTypeConfirmed = "UDF";
                                filename = fileLogTimeAlter + "_" + ID_UDF + "_R1" + "_" + boatTypeConfirmed + ".csv";
                                break;

                        }

                        preparationList.add(filename);
                        targetFileList.add(name_1);
                        mountData(name_1);

                    } else if (affix_1.equals("L2")) {

                        switch (boatType) {

                            case "2x":

                                boatTypeConfirmed = "2x";
                                filename = fileLogTimeAlter + "_" + ID_P2 + "_L2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "4x":
                            case "4x_front":

                                boatTypeConfirmed = "4x";
                                filename = fileLogTimeAlter + "_" + ID_P2 + "_L2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "4mi":

                                boatTypeConfirmed = "4-";
                                filename = fileLogTimeAlter + "_" + ID_P3 + "_L2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "4miRev":

                                boatTypeConfirmed = "4-";
                                filename = fileLogTimeAlter + "_" + ID_P4 + "_L2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8mi":
                            case "8mi_front":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P3 + "_L2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8miRev":
                            case "8mi_frontRev":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P4 + "_L2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "UDF":

                                boatTypeConfirmed = "UDF";
                                filename = fileLogTimeAlter + "_" + ID_UDF + "_L2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                        }

                        preparationList.add(filename);
                        targetFileList.add(name_1);
                        mountData(name_1);

                    } else if (affix_1.equals("R2")) {

                        switch (boatType) {

                            case "2x":

                                boatTypeConfirmed = "2x";
                                filename = fileLogTimeAlter + "_" + ID_P2 + "_R2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "4x":
                            case "4x_front":

                                boatTypeConfirmed = "4x";
                                filename = fileLogTimeAlter + "_" + ID_P2 + "_R2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "4mi":

                                boatTypeConfirmed = "4-";
                                filename = fileLogTimeAlter + "_" + ID_P4 + "_R2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "4miRev":

                                boatTypeConfirmed = "4-";
                                filename = fileLogTimeAlter + "_" + ID_P3 + "_R2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8mi":
                            case "8mi_front":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P4 + "_R2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8miRev":
                            case "8mi_frontRev":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P3 + "_R2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "UDF":

                                boatTypeConfirmed = "UDF";
                                filename = fileLogTimeAlter + "_" + ID_UDF + "_R2" + "_" + boatTypeConfirmed + ".csv";
                                break;

                        }

                        preparationList.add(filename);
                        targetFileList.add(name_1);
                        mountData(name_1);

                    } else if (affix_1.equals("L3")) {

                        switch (boatType) {

                            case "4x":
                            case "4x_back":

                                boatTypeConfirmed = "4x";
                                filename = fileLogTimeAlter + "_" + ID_P3 + "_L3" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8mi":
                            case "8mi_back":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P5 + "_L3" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8miRev":
                            case "8mi_frontRev":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P6 + "_L3" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "UDF":

                                boatTypeConfirmed = "UDF";
                                filename = fileLogTimeAlter + "_" + ID_UDF + "_L3" + "_" + boatTypeConfirmed + ".csv";
                                break;

                        }

                        preparationList.add(filename);
                        targetFileList.add(name_1);
                        mountData(name_1);

                    } else if (affix_1.equals("R3")) {

                        switch (boatType) {

                            case "4x":
                            case "4x_back":

                                boatTypeConfirmed = "4x";
                                filename = fileLogTimeAlter + "_" + ID_P3 + "_R3" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8mi":
                            case "8mi_back":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P6 + "_R3" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8miRev":
                            case "8mi_frontRev":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P5 + "_R3" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "UDF":

                                boatTypeConfirmed = "UDF";
                                filename = fileLogTimeAlter + "_" + ID_UDF + "_R3" + "_" + boatTypeConfirmed + ".csv";
                                break;

                        }

                        preparationList.add(filename);
                        targetFileList.add(name_1);
                        mountData(name_1);

                    } else if (affix_1.equals("L4")) {

                        switch (boatType) {

                            case "4x":
                            case "4x_back":

                                boatTypeConfirmed = "4x";
                                filename = fileLogTimeAlter + "_" + ID_P4 + "_L4" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8mi":
                            case "8mi_back":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P8 + "_L4" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8miRev":
                            case "8mi_frontRev":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P7 + "_L4" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "UDF":

                                boatTypeConfirmed = "UDF";
                                filename = fileLogTimeAlter + "_" + ID_UDF + "_L4" + "_" + boatTypeConfirmed + ".csv";
                                break;

                        }

                        preparationList.add(filename);
                        targetFileList.add(name_1);
                        mountData(name_1);

                    } else if (affix_1.equals("R4")) {

                        switch (boatType) {

                            case "4x":
                            case "4x_back":

                                boatTypeConfirmed = "4x";
                                filename = fileLogTimeAlter + "_" + ID_P4 + "_R4" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8mi":
                            case "8mi_back":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P7 + "_R4" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "8miRev":
                            case "8mi_frontRev":

                                boatTypeConfirmed = "8-";
                                filename = fileLogTimeAlter + "_" + ID_P8 + "_R4" + "_" + boatTypeConfirmed + ".csv";
                                break;

                            case "UDF":

                                boatTypeConfirmed = "UDF";
                                filename = fileLogTimeAlter + "_" + ID_UDF + "_R4" + "_" + boatTypeConfirmed + ".csv";
                                break;

                        }

                        preparationList.add(filename);
                        targetFileList.add(name_1);
                        mountData(name_1);

                    } else if (affix_1.equals("PH")) {

                        mountData(name_1);

                    }

                }
            }
        }

        initiate_params();

        for (int i = 0; i < preparationList.size(); i++) {
            System.out.println(preparationList.get(i));
        }

        for (int i = 0; i < targetFileList.size(); i++) {
            System.out.println(targetFileList.get(i));
        }

        switch (boatType) {

            case "1x":
                HR_matching_POS = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
                rightCompensateRatio = 1.35;
                break;

            case "2x":
                HR_matching_POS = new int[] { 0, 1, 0, 0, 0, 1, 0, 0 };
                rightCompensateRatio = 1.35;

                break;

            case "4x":
            case "4x_front":

                HR_matching_POS = new int[] { 0, 1, 2, 3, 0, 1, 2, 3 };
                rightCompensateRatio = 1.35;

                break;

            case "2mi":

                HR_matching_POS = new int[] { 0, 0, 0, 0, 1, 0, 0, 0 };

                break;

            case "2miRev":

                HR_matching_POS = new int[] { 1, 0, 0, 0, 0, 0, 0, 0 };

                break;

            case "4mi":

                HR_matching_POS = new int[] { 0, 2, 0, 0, 1, 3, 0, 0 };

                break;

            case "4miRev":

                HR_matching_POS = new int[] { 1, 3, 0, 0, 0, 2, 0, 0 };

                break;

            case "8mi":
            case "8mi_front":
            case "UDF":

                HR_matching_POS = new int[] { 0, 2, 4, 6, 1, 3, 5, 7 };

                break;

            case "8miRev":
            case "8mi_frontRev":

                HR_matching_POS = new int[] { 1, 3, 5, 7, 0, 2, 4, 6 };

                break;

        }

        totalProcess = targetFileList.size();
        currentProcess = 0;
        totalProcessNum.setText(String.valueOf(totalProcess));
        currentProcessNum.setText(String.valueOf(currentProcess));
        showPopup(windowProcess, (int) (35 * pixelDensity));

        for (int i = 0; i < targetFileList.size(); i++) {

            dataProcessing(targetFileList.get(i), preparationList.get(i), uploadFileLoc);
            currentProcess++;
            currentProcessNum.setText(String.valueOf(currentProcess));

        }

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        windowProcess.dismiss();
                    }
                });
            }
        };
        timer.schedule(task, 3000);

        Timer timer1 = new Timer();
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        for (int i = 0; i < preparationList.size(); i++) {

                            File file = new File(uploadFileLoc + preparationList.get(i));
                            if (file.isFile() && file.exists()) {
                                System.out.println("filename: " + preparationList.get(i).replace(".csv", ""));
                                uploadAction(file, preparationList.get(i).replace(".csv", ""), totalUpload);
                            }

                        }
                    }
                });
            }
        };
        timer1.schedule(task1, 4000);

    }

    private void mountData(String sourceFile) throws IOException, CsvException {

        CSVReader reader;
        File file;
        String endWith = sourceFile.substring(20, 22);
        file = new File(fileLoc + sourceFile);
        reader = new CSVReader(new FileReader(file));
        switch (endWith) {
            case "BO":
                logBO = reader.readAll();
                for (int k = 0; k <= 11; k++) {
                    logBO.remove(0);
                }
                sizeBO = logBO.size();
                break;

            case "L1":
                logL1 = reader.readAll();
                for (int k = 0; k <= 11; k++) {
                    logL1.remove(0);
                }
                sizeL1 = logL1.size();
                break;
            case "L2":
                logL2 = reader.readAll();
                for (int k = 0; k <= 11; k++) {
                    logL2.remove(0);
                }
                sizeL2 = logL2.size();
                break;
            case "L3":
                logL3 = reader.readAll();
                for (int k = 0; k <= 11; k++) {
                    logL3.remove(0);
                }
                sizeL3 = logL3.size();
                break;
            case "L4":
                logL4 = reader.readAll();
                for (int k = 0; k <= 11; k++) {
                    logL4.remove(0);
                }
                sizeL4 = logL4.size();
                break;

            case "R1":
                logR1 = reader.readAll();
                for (int k = 0; k <= 11; k++) {
                    logR1.remove(0);
                }
                sizeR1 = logR1.size();
                break;
            case "R2":
                logR2 = reader.readAll();
                for (int k = 0; k <= 11; k++) {
                    logR2.remove(0);
                }
                sizeR2 = logR2.size();
                break;
            case "R3":
                logR3 = reader.readAll();
                for (int k = 0; k <= 11; k++) {
                    logR3.remove(0);
                }
                sizeR3 = logR3.size();
                break;
            case "R4":
                logR4 = reader.readAll();
                for (int k = 0; k <= 11; k++) {
                    logR4.remove(0);
                }
                sizeR4 = logR4.size();
                break;

            case "PH":

                logPH = reader.readAll();
                logPH.remove(0);
                sizePH = logPH.size();

                break;

        }

    }

    private void uploadAction(File file, String name_1, int total) {

        uploadFailTrigger = 0;
        totalUpload = preparationList.size();
        currentUpload = 0;
        totalUploadNum.setText(String.valueOf(totalUpload));
        currentUploadNum.setText(String.valueOf(currentUpload));

        showPopup(windowUpload, 0);

        // 检查URL是否为空，避免崩溃
        if (uploadPATH == null || uploadPATH.isEmpty()) {
            // 处理上传失败逻辑
            uploadFailTrigger = 1;
            currentUpload++;
            currentUploadNum.setText(String.valueOf(currentUpload));
            if (currentUpload == total) {
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                windowUpload.dismiss();
                            }
                        });
                    }
                };
                timer.schedule(task, 2000);
            }
            return;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        String filename = name_1 + ".csv";

        // System.out.println(filename);

        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"originalData\"; filename=\"" + filename + "\""), fileBody)
                .build();

        // System.out.println("Cookie:" + listCookie.toString());

        Request request = new Request.Builder()
                .url(uploadPATH)
                .post(requestBody)
                .addHeader("Cookie", listCookie.get(0).toString().replace("[", "").replace("]", ""))
                .build();

        System.out.println(listCookie.get(0).toString().replace("[", "").replace("]", ""));

        // System.out.println("Request:" + request);

        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                uploadFailTrigger = 1;
                currentUpload++;
                currentUploadNum.setText(String.valueOf(currentUpload));

                if (currentUpload == total) {
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    windowUpload.dismiss();

                                }
                            });
                        }
                    };
                    timer.schedule(task, 3000);

                    if (uploadFailTrigger == 1) {
                        Log.e("Upload_Result", "Fail");
                        Looper.prepare();
                        if (Objects.equals(Lang, "eng")) {

                            Toast.makeText(DataSelector.this, "Upload failed, please try again", Toast.LENGTH_SHORT)
                                    .show();

                        } else {

                            Toast.makeText(DataSelector.this, "上传失败，请在网络条件良好的情况下重试", Toast.LENGTH_SHORT).show();

                        }

                        Looper.loop();
                    }
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Log.i("text", "success upload!");
                String json = response.body().string();
                // Log.i("success........","成功"+json);
                // Looper.prepare();
                // Toast.makeText(DataSelector.this, "共"+ total +"个文件待上传,"+ name_1 +"上传成功",
                // Toast.LENGTH_SHORT).show();
                // Looper.loop();

                System.out.println(response);
                System.out.println(json);

                currentUpload++;
                currentUploadNum.setText(String.valueOf(currentUpload));

                if (currentUpload == total) {
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    windowUpload.dismiss();

                                }
                            });
                        }
                    };
                    timer.schedule(task, 3000);
                }
            }
        });
    }

    private void fileDeleteNotification(View view, int position, String currentLogTime) {

        String title;
        String content;
        String confirm;
        String cancel;

        if (Objects.equals(Lang, "eng")) {

            title = "Are you sure about deleting the file?";
            content = "Deleted file cannot be recovered";
            confirm = "Yes";
            cancel = "No";

        } else {

            title = "是否确定要删除当前数据条目？";
            content = "被删除的条目将无法恢复";
            confirm = "确认删除";
            cancel = "取消";
        }

        new AlertDialog.Builder(DataSelector.this).setTitle(title)
                .setMessage(content)
                .setPositiveButton(cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).setNegativeButton(confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteLogs(currentLogTime);
                        mAdapter.removeData(position);
                        mAdapter.notifyDataSetChanged();
                    }

                }).show();
    }

    private void fileUploadNotification(String currentLogTime) {

        String title;
        String content;
        String confirm;
        String cancel;

        if (Objects.equals(Lang, "eng")) {

            title = "Do you want to upload file to server?";
            content = "Your data will be updated to server after encryption";
            confirm = "Yes";
            cancel = "No";

        } else {

            title = "是否要上传数据文件？";
            content = "您的数据将被加密传输到服务器";
            confirm = "确认上传";
            cancel = "取消";
        }

        new AlertDialog.Builder(DataSelector.this).setTitle(title)
                .setMessage(content)
                .setPositiveButton(cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                }).setNegativeButton(confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            uploadLogs(currentLogTime);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        } catch (CsvException e) {
                            throw new RuntimeException(e);
                        }

                    }

                }).show();
    }

    private void showPopup(final PopupWindow popupWindow, int y) {

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);

        View parentView = LayoutInflater.from(DataSelector.this).inflate(R.layout.activity_row_monitor, null);
        popupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, y);
        // popupWindow.setFocusable(false);
        // popupWindow.setOutsideTouchable(false);

    }

    private ArrayList<String> invertOrderList(ArrayList<String> L) {

        double convertedTime1;
        double convertedTime2;

        String temp_r;

        for (int i = 0; i < L.size() - 1; i++) {
            for (int j = i + 1; j < L.size(); j++) {

                int year1 = Integer.parseInt(L.get(i).substring(0, 4));
                int year2 = Integer.parseInt(L.get(j).substring(0, 4));
                int month1 = Integer.parseInt(L.get(i).substring(5, 7));
                int month2 = Integer.parseInt(L.get(j).substring(5, 7));
                int day1 = Integer.parseInt(L.get(i).substring(8, 10));
                int day2 = Integer.parseInt(L.get(j).substring(8, 10));
                int hour1 = Integer.parseInt(L.get(i).substring(11, 13));
                int hour2 = Integer.parseInt(L.get(j).substring(11, 13));
                int minute1 = Integer.parseInt(L.get(i).substring(14, 16));
                int minute2 = Integer.parseInt(L.get(j).substring(14, 16));
                int second1 = Integer.parseInt(L.get(i).substring(17, 19));
                int second2 = Integer.parseInt(L.get(j).substring(17, 19));

                convertedTime1 = year1 * 31536000 + month1 * 2626560 + day1 * 86400 + hour1 * 3600 + minute1 * 60
                        + second1;
                convertedTime2 = year2 * 31536000 + month2 * 2626560 + day2 * 86400 + hour2 * 3600 + minute2 * 60
                        + second2;

                if (convertedTime1 < convertedTime2) {
                    // 如果队前日期靠前，调换顺序
                    temp_r = L.get(i);
                    L.set(i, L.get(j));
                    L.set(j, temp_r);
                }
            }
        }

        // for (int i = 0; i < L.size() -1 ; i++){
        //
        // int year1 = Integer.parseInt(L.get(i).substring(0,3));
        // int month1 = Integer.parseInt(L.get(i).substring(5,6));
        // int day1 = Integer.parseInt(L.get(i).substring(8,9));
        // int hour1 = Integer.parseInt(L.get(i).substring(11,12));
        // int minute1 = Integer.parseInt(L.get(i).substring(14,15));
        // int second1 = Integer.parseInt(L.get(i).substring(17,18));
        //
        // double convertedtimeforlog = year1*31536000 + month1 * 2626560 + day1 * 86400
        // + hour1 * 3600 + minute1 * 60 + second1;
        //
        // System.out.println(L.get(i));
        // System.out.println(year1 + "_____" + month1 + "_____" + day1 + "_____" +
        // hour1 + "_____" + minute1 + "_____" + second1);
        //
        //
        //
        // }

        return L;
    }

    private void initiate_params() {

        sizeArray = new int[] { sizeBO, sizeL1, sizeL2, sizeL3, sizeL4, sizeR1, sizeR2, sizeR3, sizeR4, sizePH };
        Arrays.sort(sizeArray);
        itrMax = 0;
        int i = 0;
        while (itrMax <= 10) {
            itrMax = sizeArray[i];
            i++;
        }

        Log.e("itrMax:", String.valueOf(itrMax));
    }

    private void dataCalculation(int i, List<String[]> log, String side, double[] UI_params, int pos) {

        double yawRaw;

        try {
            yawRaw = Double.parseDouble(log.get(i)[4]);

        } catch (Exception e) {
            yawRaw = 0;
        }

        double yawRawCache;

        if (i > 1) {

            try {
                yawRawCache = Double.parseDouble(log.get(i - 1)[4]);

            } catch (Exception e) {
                yawRawCache = 0;
            }

        } else {
            yawRawCache = yawRaw;
        }

        double rollRaw = 0;
        double pitchRaw = 0;
        double strokeSpeed = 0; // 4
        double featherAngle = 0; // 0
        double oarDepth = 0; // 3
        double featherSpeed = 0; // 8
        double strokeForce = 0; // 20
        double strokeWattage = 0; // new

        double velocityLast = UI_params[15];

        try {
            rollRaw = Double.parseDouble(log.get(i)[2]);
            pitchRaw = Double.parseDouble(log.get(i)[3]);
            // angularVelocity = Double.parseDouble(log.get(i)[8]);

        } catch (Exception e) {
            rollRaw = 0;
            pitchRaw = 0;
        }

        try {
            strokeSpeed = Double.parseDouble(log.get(i)[4]);
        } catch (Exception e) {
            strokeSpeed = 0;

        }

        if (side.equals("left")) {

            double roll = -rollRaw + correctionPeddle;
            double pitch = -pitchRaw;
            double yaw = -yawRaw + correctionLeft + boatAngle + correctionLeftSecondary;
            double txYaw = 180 - yawRaw + boatAngle + correctionLeftSecondary;

            double velocityZeroMarker = 0;
            double strokeTimeNow = 0;

            double strokeAV;
            double[] strokeAVK = { 0, 0 };

            double localAv_0;
            double localAv_1;
            double dynamicAV;

            try {
                dynamicAV = Double.parseDouble(log.get(i)[4]);
            } catch (Exception e) {
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

            double velocityNow = Double.parseDouble(samplingRate) * localAv_1;
            // double velocityNow = Double.parseDouble(log.get(i)[10]);
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

            if (roll >= 180) {
                featherAngle = roll - 360;
            } else {
                featherAngle = roll;
            }

            double rollLast = UI_params[19];

            featherSpeed = (featherAngle - rollLast) * Double.parseDouble(samplingRate);

            if (featherSpeed > 1000 || featherSpeed <= -1000) {

                featherSpeed = 0;

            }

            double testNumberLeft = -(strokeSpeed - yawRawCache);

            if (testNumberLeft > 300) {

                strokeAV = testNumberLeft - 360;

            } else if (testNumberLeft < -300) {

                strokeAV = testNumberLeft + 360;

            } else {

                strokeAV = -(strokeSpeed - yawRawCache);

            }

            strokeSpeed = strokeAV;

            oarDepth = -Double.parseDouble(log.get(i)[3]);

            double testNumberLeft_force;

            try {
                testNumberLeft_force = -(Double.parseDouble(log.get(i)[4]) - yawRawCache);
            } catch (Exception e) {
                testNumberLeft_force = 0;
            }

            // add try

            double currentForceData;
            double currentSpeedData;
            double currentDepthData;
            double filteredForceData;

            if (testNumberLeft_force > 300) {
                currentSpeedData = testNumberLeft_force - 360;
            } else if (testNumberLeft_force < -300) {
                currentSpeedData = testNumberLeft_force + 360;
            } else {
                currentSpeedData = testNumberLeft_force;
            }

            currentSpeedData = (currentSpeedData * speedSuppressionRatio - speedNormMin)
                    / (speedNormMax - speedNormMin);
            currentDepthData = (pitch * depthSuppressionRatio - depthNormMin) / (depthNormMax - depthNormMin);

            if (currentSpeedData < 0) {
                currentForceData = 0;
            } else {

                currentForceData = (p00
                        + p10 * currentSpeedData + p01 * currentDepthData
                        + p20 * Math.pow(currentSpeedData, 2)
                        + p11 * currentSpeedData * currentDepthData
                        + p02 * Math.pow(currentDepthData, 2));
                currentForceData = currentForceData * forceAmplifier * (powerNormMax - powerNormMin) + powerNormMin;

            }

            filteredForceData = myKalmanFilter(currentForceData, pos);
            // filteredForceData = currentForceData;

            if (forceCacheSizeList[pos][0] < forceCacheLength) {

                forceCacheSamplesList[pos][forceCachePointerList[pos][0]++] = filteredForceData;
                forceCacheSizeList[pos][0]++;

            } else {

                forceCachePointerList[pos][0] = forceCachePointerList[pos][0] % forceCacheLength;
                forceCacheSumList[pos][0] -= forceCacheSamplesList[pos][forceCachePointerList[pos][0]];
                forceCacheSamplesList[pos][forceCachePointerList[pos][0]++] = filteredForceData;
            }

            double forceDataAvg = doubleArrAverage(forceCacheSamplesList[pos]);
            // double forceDataAvg =
            // myKalmanFilter(doubleArrAverage(forceCacheSamplesList[pos])*forceAmplifier,pos);

            if (forceDataAvg < 0) {
                strokeForce = 0;
            } else {
                strokeForce = forceDataAvg;
            }

            // change gap limitation to frame-based
            // UI_params[12] changed to Last Stroke Begin Count
            // UI_params[16] changed to Current Stroke Begin Count
            // LSB - CSB >= 1.2*sampling rate
            // Current Count - CSB >= 0.3 * sampling rate

            // if (velocityZeroMarker == 1 &&
            // velocityNow < 0 &&
            // strokeTimeNow - UI_params[12] >= 1200 &&
            // strokeTimeNow - UI_params[16] > 300/speedMultiple){

            if (velocityZeroMarker == 1 &&
                    velocityNow < 0 &&
                    i - UI_params[12] >= 1.2 * Double.parseDouble(samplingRate) &&
                    i - UI_params[16] > 0.3 * Double.parseDouble(samplingRate)) {

                UI_params[12] = i;
                UI_params[13] = (UI_params[12] - UI_params[11]) / Double.parseDouble(samplingRate);
                UI_params[16] = i;

                // UI_params[12] = System.currentTimeMillis();
                // UI_params[13] = (UI_params[12] - UI_params[11])/1000;
                // UI_params[16] = System.currentTimeMillis();

                double avgStrokeAV = CacheStrokeAV[pos] / CacheLengthStrokeAV[pos];
                double wattageCurrentStroke = wattage_p1_4 * Math.pow(avgStrokeAV * wattageSuppressionRatio, 4)
                        + wattage_p2_4 * Math.pow(avgStrokeAV * wattageSuppressionRatio, 3)
                        + wattage_p3_4 * Math.pow(avgStrokeAV * wattageSuppressionRatio, 2)
                        + wattage_p4_4 * avgStrokeAV * wattageSuppressionRatio
                        + wattage_p5_4;

                // double wattageCurrentStroke = wattage_p1_1*avgStrokeAV*speedSuppressionRatio
                // + wattage_p2_1;

                CacheStrokeAV[pos] = 0;
                CacheLengthStrokeAV[pos] = 0;

                strokeWattage = wattageCurrentStroke;

                // System.out.println("AV: " + avgStrokeAV*wattageSuppressionRatio + " current
                // wattage " + String.valueOf(pos) + ":" + wattageCurrentStroke);
                // Log.e("position:" + pos, "AV: " + avgStrokeAV * wattageSuppressionRatio +
                // "current wattage: " + wattageCurrentStroke);

                UI_params[8] = yawRaw;
                double UI_10_temp = Math.abs(UI_params[9] - UI_params[8]);

                if (UI_10_temp >= 180) {
                    UI_params[10] = 360 - UI_10_temp;
                    // UI_params[10] = UI_10_temp - 360;

                } else {
                    UI_params[10] = UI_10_temp;
                }

                // new fwd degree calculation
                double randomSeed = (avgStrokeAV * wattageSuppressionRatio - fwdSplitThreshSpeed) / fwdSplitDivideFactor
                        + Math.random() / fwdSplitRandomSeedDivideFactor;
                double simulatedFwdDegree = UI_params[10] * (fwdSplitRatio + randomSeed);
                UI_params[4] = simulatedFwdDegree;
                UI_params[5] = UI_params[10] - UI_params[4];
                // new fwd degree calculation

                // }else if (velocityZeroMarker == 1 &&
                // velocityNow > 0 &&
                // strokeTimeNow - UI_params[11] >= 1200 &&
                // strokeTimeNow - UI_params[16] > 300/speedMultiple) {

            } else if (velocityZeroMarker == 1 &&
                    velocityNow > 0 &&
                    i - UI_params[11] >= 1.2 * Double.parseDouble(samplingRate) &&
                    i - UI_params[16] > 0.3 * Double.parseDouble(samplingRate)) {

                UI_params[11] = i;
                UI_params[16] = i;
                UI_params[13] = (UI_params[12] - UI_params[11]) / Double.parseDouble(samplingRate);

                UI_params[9] = yawRaw;

            }

        } else if (side.equals("right")) {

            double roll = -rollRaw - correctionPeddle;
            double pitch = pitchRaw + correctionRightPitch;
            double yaw = -yawRaw + correctionRight + boatAngle + correctionRightSecondary;
            double txYaw = 180 + yawRaw - boatAngle - correctionRightSecondary;

            double velocityZeroMarker = 0;
            double strokeTimeNow = 0;

            double strokeAV = 0;

            double localAv_0;
            double localAv_1;
            double dynamicAV;

            try {
                dynamicAV = Double.parseDouble(log.get(i)[4]);
            } catch (Exception e) {
                dynamicAV = 0;
            }

            localAv_0 = (dynamicAV - yawRawCache);

            if (localAv_0 > 300) {
                localAv_1 = localAv_0 - 360;
            } else if (localAv_0 < -300) {
                localAv_1 = localAv_0 + 360;
            } else {
                localAv_1 = (dynamicAV - yawRawCache);
                // localAv_1 = localAv_0;
            }

            if (localAv_1 > 0) {
                CacheStrokeAV[pos] = CacheStrokeAV[pos] + localAv_1;
                CacheLengthStrokeAV[pos] = CacheLengthStrokeAV[pos] + 1;
            }

            double velocityNow = Double.parseDouble(samplingRate) * localAv_1;
            // double velocityNow = Double.parseDouble(log.get(i)[10]);
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

            if (-roll >= 180) {
                featherAngle = -roll - 360;
            } else {
                featherAngle = -roll;
            }

            double rollLast = UI_params[19];

            featherSpeed = -(featherAngle - rollLast) * Double.parseDouble(samplingRate);

            if (featherSpeed > 1000 || featherSpeed <= -1000) {

                featherSpeed = 0;

            }

            double testNumberRight = (strokeSpeed - yawRawCache);
            // double testNumberRight = localAv_0;

            if (testNumberRight > 300) {

                strokeAV = testNumberRight - 360;

            } else if (testNumberRight < -300) {

                strokeAV = testNumberRight + 360;

            } else {
                strokeAV = (strokeSpeed - yawRawCache);
            }

            strokeSpeed = strokeAV;

            oarDepth = -Double.parseDouble(log.get(i)[3]);

            double testNumberRight_force;

            try {
                testNumberRight_force = (Double.parseDouble(log.get(i)[4]) - yawRawCache);
            } catch (Exception e) {
                testNumberRight_force = 0;
            }

            double currentForceData;
            double currentSpeedData;
            double currentDepthData;
            double filteredForceData;
            double rightSideCompensate = rightCompensateRatio;

            if (testNumberRight_force > 300) {
                currentSpeedData = testNumberRight_force - 360;
            } else if (testNumberRight_force < -300) {
                currentSpeedData = testNumberRight_force + 360;
            } else {
                currentSpeedData = testNumberRight_force;
            }

            currentSpeedData = (currentSpeedData * speedSuppressionRatio - speedNormMin)
                    / (speedNormMax - speedNormMin);
            currentDepthData = (-pitch * depthSuppressionRatio * rightSideCompensate - depthNormMin)
                    / (depthNormMax - depthNormMin);

            if (currentSpeedData < 0) {

                currentForceData = 0;
            } else {

                currentForceData = (p00
                        + p10 * currentSpeedData + p01 * currentDepthData
                        + p20 * Math.pow(currentSpeedData, 2)
                        + p11 * currentSpeedData * currentDepthData
                        + p02 * Math.pow(currentDepthData, 2));
                currentForceData = currentForceData * forceAmplifier * (powerNormMax - powerNormMin) + powerNormMin;

                filteredForceData = myKalmanFilter(currentForceData, pos);

                if (forceCacheSizeList[pos][0] < forceCacheLength) {

                    forceCacheSamplesList[pos][forceCachePointerList[pos][0]++] = filteredForceData;
                    forceCacheSizeList[pos][0]++;

                } else {

                    forceCachePointerList[pos][0] = forceCachePointerList[pos][0] % forceCacheLength;
                    forceCacheSumList[pos][0] -= forceCacheSamplesList[pos][forceCachePointerList[pos][0]];
                    forceCacheSamplesList[pos][forceCachePointerList[pos][0]++] = filteredForceData;

                }

                double forceDataAvg = doubleArrAverage(forceCacheSamplesList[pos]);

                if (forceDataAvg < 0) {
                    strokeForce = 0;
                } else {
                    strokeForce = forceDataAvg;
                }

            }

            if (velocityZeroMarker == 1 &&
                    velocityNow < 0 &&
                    i - UI_params[12] >= 1.2 * Double.parseDouble(samplingRate) &&
                    i - UI_params[16] > 0.3 * Double.parseDouble(samplingRate)) {

                UI_params[12] = i;
                UI_params[13] = (UI_params[12] - UI_params[11]) / Double.parseDouble(samplingRate);
                UI_params[16] = i;

                double avgStrokeAV = CacheStrokeAV[pos] / CacheLengthStrokeAV[pos];
                double wattageCurrentStroke = wattage_p1_4 * Math.pow(avgStrokeAV * wattageSuppressionRatio, 4)
                        + wattage_p2_4 * Math.pow(avgStrokeAV * wattageSuppressionRatio, 3)
                        + wattage_p3_4 * Math.pow(avgStrokeAV * wattageSuppressionRatio, 2)
                        + wattage_p4_4 * avgStrokeAV * wattageSuppressionRatio
                        + wattage_p5_4;

                CacheStrokeAV[pos] = 0;
                CacheLengthStrokeAV[pos] = 0;

                strokeWattage = wattageCurrentStroke;

                UI_params[8] = yawRaw;
                double UI_10_temp = Math.abs(UI_params[9] - UI_params[8]);
                if (UI_10_temp >= 180) {
                    UI_params[10] = 360 - UI_10_temp;
                } else {
                    UI_params[10] = UI_10_temp;
                }

                double randomSeed = (avgStrokeAV * wattageSuppressionRatio - fwdSplitThreshSpeed) / fwdSplitDivideFactor
                        + Math.random() / fwdSplitRandomSeedDivideFactor;
                double simulatedFwdDegree = UI_params[10] * (fwdSplitRatio + randomSeed);
                UI_params[4] = simulatedFwdDegree;
                UI_params[5] = UI_params[10] - UI_params[4];

            } else if (velocityZeroMarker == 1 &&
                    velocityNow > 0 &&
                    i - UI_params[11] >= 1.2 * Double.parseDouble(samplingRate) &&
                    i - UI_params[16] > 0.3 * Double.parseDouble(samplingRate)) {

                UI_params[11] = i;
                UI_params[16] = i;
                UI_params[13] = (UI_params[12] - UI_params[11]) / Double.parseDouble(samplingRate);

                UI_params[9] = yawRaw;

            }

        }

        UI_params[18] = strokeSpeed;
        UI_params[19] = featherAngle;
        UI_params[20] = featherSpeed;
        UI_params[21] = oarDepth;
        UI_params[22] = strokeForce;
        UI_params[23] = strokeWattage;

    }

    private void dataProcessing(String sourceFile, String outputFile, String outputLocation) {

        String affix = sourceFile.substring(20, 22);
        String side = "left";
        int pos = 0;
        List<String[]> log = null;
        double[] params = new double[0];

        switch (affix) {
            case "L1":
                side = "left";
                pos = 0;
                log = logL1;
                params = UI_params_L1;
                break;
            case "L2":
                side = "left";
                pos = 1;
                log = logL2;
                params = UI_params_L2;
                break;
            case "L3":
                side = "left";
                pos = 2;
                log = logL3;
                params = UI_params_L3;
                break;
            case "L4":
                side = "left";
                pos = 3;
                log = logL4;
                params = UI_params_L4;
                break;
            case "R1":
                side = "right";
                pos = 4;
                log = logR1;
                params = UI_params_R1;
                break;
            case "R2":
                side = "right";
                pos = 5;
                log = logR2;
                params = UI_params_R2;
                break;
            case "R3":
                side = "right";
                pos = 6;
                log = logR3;
                params = UI_params_R3;
                break;
            case "R4":
                side = "right";
                pos = 7;
                log = logR4;
                params = UI_params_R4;
                break;
        }

        UI_params_L1 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        UI_params_L2 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        UI_params_L3 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        UI_params_L4 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        UI_params_R1 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        UI_params_R2 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        UI_params_R3 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        UI_params_R4 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        if (thread0 != null) {
            thread0.interrupt();
        }

        int innerPos = pos;
        String innerSide = side;

        List<String[]> finalLog = log;
        double[] finalParams = params;
        thread0 = new Thread(new Runnable() {

            @Override
            public void run() {

                playNodePos = 0;
                List<String[]> output = new ArrayList<>();

                // String[] outputParams = new String[] {
                // "0","0","0","0","0",
                // "0","0","0","0","0",
                // "0","0","0","0","0",
                // "0", "0"};

                String[] header = new String[] {
                        String.valueOf(samplingRate), "fwd_degree", "bwd_degree", "total_degree", "total_wattage",
                        "stoke_speed", "feather_angle", "feather_speed", "oar_depth", "stroke_force",
                        "stroke_rate", "boat_speed", "split", "boat_yaw", "boat_roll",
                        "boat_accel", "heart_rate", "distance"
                };

                output.add(header);

                try {

                    if (itrMax < 100000000) {

                        for (int i = 0; i < itrMax - 1; i++) {

                            double boatSpeedCache = Double.parseDouble(logPH.get(i)[2]);
                            String Split;
                            if (boatSpeedCache <= 0.7) {
                                Split = "9:59";
                            } else {

                                int splitTime = (int) (500 / boatSpeedCache);
                                int splitTimeSec = splitTime % 60;
                                splitTime = splitTime - splitTimeSec;
                                int splitTimeMin = splitTime / 60;

                                String splitTimeTx;
                                if (splitTimeSec < 10) {
                                    splitTimeTx = splitTimeMin + ":0" + splitTimeSec;
                                } else {
                                    splitTimeTx = splitTimeMin + ":" + splitTimeSec;
                                }
                                Split = splitTimeTx;
                            }

                            dataCalculation(i, finalLog, innerSide, finalParams, innerPos);

                            if (logPH.get(0).length < 17) {

                                String[] outputParams = new String[] {
                                        String.valueOf(i), String.valueOf(finalParams[4]),
                                        String.valueOf(finalParams[5]), String.valueOf(finalParams[10]),
                                        String.valueOf(finalParams[23]),
                                        String.valueOf(finalParams[18]), String.valueOf(finalParams[19]),
                                        String.valueOf(finalParams[20]), String.valueOf(finalParams[21]),
                                        String.valueOf(finalParams[22]),
                                        logPH.get(i)[1], logPH.get(i)[2], Split, logPH.get(i)[3], logPH.get(i)[4],
                                        "0", logPH.get(i)[HR_matching_POS[innerPos] + 5], logPH.get(i)[13] };
                                output.add(outputParams);

                            } else {

                                String[] outputParams = new String[] {
                                        String.valueOf(i), String.valueOf(finalParams[4]),
                                        String.valueOf(finalParams[5]), String.valueOf(finalParams[10]),
                                        String.valueOf(finalParams[23]),
                                        String.valueOf(finalParams[18]), String.valueOf(finalParams[19]),
                                        String.valueOf(finalParams[20]), String.valueOf(finalParams[21]),
                                        String.valueOf(finalParams[22]),
                                        logPH.get(i)[1], logPH.get(i)[2], Split, logPH.get(i)[3], logPH.get(i)[4],
                                        logPH.get(i)[17], logPH.get(i)[HR_matching_POS[innerPos] + 5],
                                        logPH.get(i)[13] };
                                output.add(outputParams);

                            }

                        }

                    }

                } catch (Exception e) {

                    String[] outputParams = new String[] {
                            "0", "0", "0", "0", "0",
                            "0", "0", "0", "0", "0",
                            "0", "0", "0", "0", "0",
                            "0", "0" };
                    output.add(outputParams);

                }

                String processingFile = outputLocation + outputFile;

                try {
                    FileWriter processWriter = new FileWriter(processingFile);
                    CSVWriter processWriterCSV = new CSVWriter(processWriter,
                            CSVWriter.DEFAULT_SEPARATOR,
                            CSVWriter.NO_QUOTE_CHARACTER,
                            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                            CSVWriter.RFC4180_LINE_END);

                    processWriterCSV.writeAll(output);
                    processWriterCSV.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        thread0.start();

    }

    private double myKalmanFilter(double in, int pos) {
        double newOut = 0;
        double[][] newIn = { { in } };
        Matrix s = new Matrix(newIn);

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
        newOut = XuLocal.get(0, 0);

        XpList.set(pos, XpLocal);
        PpList.set(pos, PpLocal);
        KList.set(pos, KLocal);
        XuList.set(pos, XuLocal);
        PuList.set(pos, PuLocal);

        return newOut;
    }

    public double doubleArrAverage(double[] arr) {
        double sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
        }
        return sum / arr.length;
    }

    public double gaussianArrAverage(double[] arr, double[] kernel) {
        double result;
        double sum = 0;

        if (arr.length < kernel.length) {
            for (int i = 0; i < arr.length; i++) {
                sum += arr[i];
            }
            result = sum / arr.length;
            // System.out.println("simple average");

        } else {
            for (int i = 0; i < arr.length; i++) {
                sum += arr[i] * kernel[i];
            }
            result = sum;
            // System.out.println("gaussian average");

        }

        return result;
    }

}