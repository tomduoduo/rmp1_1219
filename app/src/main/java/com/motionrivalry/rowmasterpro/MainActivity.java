package com.motionrivalry.rowmasterpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gigamole.library.ShadowLayout;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.motionrivalry.rowmasterpro.config.AppConfig;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText_userName;
    private EditText mEditText_password;
    private TextView mTextView_result;
    private String PATH = AppConfig.LOGIN_PATH;
    private String PATH_LIST = AppConfig.ATHLETE_LIST_PATH;
    private String username;
    private String password;
    private String addressMAC;
    // private String token = "da2de30b-86c4-4142-a928-b743d59d583b";
    public static CookieManager cookieManager = new CookieManager();
    private String loginInfoLoc = null;
    private String loginFile;
    private int tabMode;

    private String athleteListLoc = null;
    private String hrmPlanListLoc = null;
    private String hrmPlanListGDMLoc = null;
    private String hrmPlanListGDFLoc = null;
    private String hrmPlanListGuestLoc = null;
    private String hrmPlanListPrivateLoc = null;

    private Button mBtnUpdateApp = null;
    private TextView mVersion;
    private BufferedReader read;

    private Button mDownloadTest;

    private String athleteProfileFile;
    private String athleteProfileFile_guest;

    private String Lang = "chn";
    private Button changeLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        if (metrics.xdpi <= 250 || isPad(this)) {
            tabMode = 1;
            setContentView(R.layout.activity_main_tab);

        } else {
            tabMode = 0;
            if (Objects.equals(Lang, "eng")) {
                setContentView(R.layout.activity_main_eng);

            } else {
                setContentView(R.layout.activity_main);

            }
        }

        changeLang = findViewById(R.id.lang_change);

        Button mBtnLogin = findViewById(R.id.btn_login);
        Button mMstBtnLogin = findViewById(R.id.master_btn_login);
        mEditText_userName = findViewById(R.id.username);
        mEditText_password = findViewById(R.id.password);
        mBtnUpdateApp = findViewById(R.id.btn_update);
        final Intent intent = new Intent(MainActivity.this, ModeSelect.class);
        mVersion = findViewById(R.id.version);
        mVersion.setText("Version: " + getResources().getString(R.string.version));
        mDownloadTest = findViewById(R.id.download_test);

        String[] PermissionString = AppConfig.REQUIRED_PERMISSIONS;

        loginInfoLoc = this.getFilesDir() + "/loginInfo/";
        loginFile = loginInfoLoc + "data.csv";

        athleteListLoc = this.getFilesDir() + "/athlete_list/";
        hrmPlanListLoc = this.getFilesDir() + "/hrm_plan_list/";
        hrmPlanListGuestLoc = hrmPlanListLoc + "/游客/";
        hrmPlanListPrivateLoc = hrmPlanListLoc + "/专用/";
        athleteProfileFile = athleteListLoc + "专用.csv";

        CookieHandler.setDefault(cookieManager);
        checkPermission(PermissionString);
        checkPermissionAllGranted(PermissionString);

        createFile(loginInfoLoc, 1);
        createFile(athleteListLoc, 2);
        createFile(hrmPlanListLoc, 2);
        // createFile(hrmPlanListGDMLoc,2);
        // createFile(hrmPlanListGDFLoc,2);
        createFile(hrmPlanListGuestLoc, 2);
        createFile(hrmPlanListPrivateLoc, 2);

        nameListFileCreation();

        try {
            CSVReader reader = new CSVReader(new FileReader(loginFile));
            List<String[]> userData = reader.readAll();
            username = userData.get(1)[0];
            password = userData.get(1)[1];
            Lang = (userData.get(1)[2] != null && !userData.get(1)[2].isEmpty())
                    ? userData.get(1)[2]
                    : AppConfig.DEFAULT_LANGUAGE;
            mEditText_userName.setText(username);
            mEditText_password.setText(password);

            if (Objects.equals(Lang, "eng")) {

                changeLang.setText("中文");
                mBtnLogin.setText("Login");
                mBtnUpdateApp.setText("Long Press\nto Update");
                mEditText_userName.setHint("Username");
                mEditText_password.setHint("Password");

            } else {

                changeLang.setText("eng");
                mBtnLogin.setText("登录");
                mBtnUpdateApp.setText("长按更新软件");
                mEditText_userName.setHint("用户名");
                mEditText_password.setHint("密码");

            }

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }

        changeLang.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Objects.equals(Lang, "eng")) {

                    Lang = "chn";
                    changeLang.setText("eng");
                    mBtnLogin.setText("登录");
                    mBtnUpdateApp.setText("长按更新软件");
                    mEditText_userName.setHint("用户名");
                    mEditText_password.setHint("密码");

                } else {

                    Lang = "eng";
                    changeLang.setText("中文");
                    mBtnLogin.setText("Login");
                    mBtnUpdateApp.setText("Long Press\nto Update");
                    mEditText_userName.setHint("Username");
                    mEditText_password.setHint("Password");

                }

            }
        });

        mBtnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                username = mEditText_userName.getText().toString();
                password = mEditText_password.getText().toString();
                addressMAC = getMacFromHardware();

                try {
                    CSVReader reader2 = new CSVReader(new FileReader(loginFile));
                    List<String[]> allElements = reader2.readAll();
                    reader2.close();
                    System.out.println(allElements);
                    allElements.remove(1);
                    String[] fillActual = new String[] { username, password, Lang };
                    allElements.add(1, fillActual);
                    FileWriter alterFileWriter2 = new FileWriter(loginFile);
                    CSVWriter alterCSVWriter2 = new CSVWriter(alterFileWriter2);
                    alterCSVWriter2.writeAll(allElements);
                    alterCSVWriter2.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (CsvException e) {
                    e.printStackTrace();
                }

                // Toast.makeText(MainActivity.this, addressMAC, Toast.LENGTH_LONG).show();
                postStart(intent);
                // finish();
                // startActivity(intent);
            }
        });

        mDownloadTest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                getNameListCSV();
            }
        });

        mBtnUpdateApp.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {

                openLink("");

                return true;
            }
        });

        mMstBtnLogin.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {

                intent.putExtra("lang", Lang);
                startActivity(intent);

                return true;
            }
        });

        // final ShadowLayout shadowLayout1 = findViewById(R.id.sl_main_activity);
        // shadowLayout1.setIsShadowed(true);
        // shadowLayout1.setShadowAngle(45);
        // shadowLayout1.setShadowRadius(10);
        // shadowLayout1.setShadowDistance(5);
        // shadowLayout1.setShadowColor(Color.DKGRAY);

    }

    public void getNameListCSV() {

        final String serverPath = PATH_LIST;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 检查URL是否为空，避免崩溃
                    if (serverPath == null || serverPath.isEmpty()) {
                        return;
                    }
                    URL url = new URL(serverPath);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setReadTimeout(10000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setRequestProperty("Connection", "keep-alive");
                    InputStream inputStream = httpURLConnection.getInputStream();
                    read = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();

                    String line;

                    FileWriter profileFileWriter = new FileWriter(athleteProfileFile);
                    CSVWriter profileCSVWriter = new CSVWriter(profileFileWriter);

                    while ((line = read.readLine()) != null) {
                        String[] RowData = line.split(",");
                        System.out.println(line);
                        profileCSVWriter.writeNext(RowData);
                    }

                    profileCSVWriter.close();
                    System.out.println("writer closed");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void postStart(final Intent intent) {

        final String serverPath = PATH;

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(MainActivity.this, "请输入账号密码", Toast.LENGTH_LONG).show();
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

                        // 检查URL是否为空，避免崩溃
                        if (serverPath == null || serverPath.isEmpty()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (Objects.equals(Lang, "eng")) {
                                        Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            return;
                        }
                        URL url = new URL(serverPath);
                        System.out.println(url);

                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setConnectTimeout(AppConfig.NETWORK_CONNECT_TIMEOUT_MS);
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

                            if (String.valueOf(inputStreamMap.get("code")).equals(AppConfig.RESPONSE_CODE_SUCCESS)) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Toast.makeText(MainActivity.this, "Code:" +
                                        // String.valueOf(inputStreamMap.get("Code")), Toast.LENGTH_LONG).show();

                                        if (Objects.equals(Lang, "eng")) {

                                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG)
                                                    .show();

                                        } else {
                                            Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_LONG).show();

                                        }

                                        System.out.println(responseMsg);
                                        getNameListCSV();

                                        intent.putExtra("userName", username);
                                        intent.putExtra("password", password);
                                        intent.putExtra("lang", Lang);
                                        startActivity(intent);

                                    }
                                });

                            } else if (String.valueOf(inputStreamMap.get("code"))
                                    .equals(AppConfig.RESPONSE_CODE_EMPTY)) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (Objects.equals(Lang, "eng")) {

                                            Toast.makeText(MainActivity.this, "Please Fill Username/Password",
                                                    Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(MainActivity.this, "账号密码不能为空", Toast.LENGTH_LONG).show();

                                        }
                                        // Toast.makeText(MainActivity.this, "Code:" +
                                        // String.valueOf(inputStreamMap.get("Code")), Toast.LENGTH_LONG).show();
                                        System.out.println(responseMsg);

                                    }
                                });

                            } else if (String.valueOf(inputStreamMap.get("code"))
                                    .equals(AppConfig.RESPONSE_CODE_WRONG)) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (Objects.equals(Lang, "eng")) {

                                            Toast.makeText(MainActivity.this, "Wrong Username/Password",
                                                    Toast.LENGTH_LONG).show();

                                        } else {
                                            Toast.makeText(MainActivity.this, "账号或密码错误", Toast.LENGTH_LONG).show();

                                        }
                                        // Toast.makeText(MainActivity.this, "Code:" +
                                        // String.valueOf(inputStreamMap.get("Code")), Toast.LENGTH_LONG).show();

                                        System.out.println(responseMsg);

                                    }
                                });

                            } else if (String.valueOf(inputStreamMap.get("code"))
                                    .equals(AppConfig.RESPONSE_CODE_FAIL)) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Toast.makeText(MainActivity.this, "Code:" +
                                        // String.valueOf(inputStreamMap.get("Code")), Toast.LENGTH_LONG).show();
                                        Toast.makeText(MainActivity.this, "登录失败，请联系管理员", Toast.LENGTH_LONG).show();
                                        System.out.println(responseMsg);

                                    }
                                });

                            } else if (String.valueOf(inputStreamMap.get("code"))
                                    .equals(AppConfig.RESPONSE_CODE_DEVICE_CONFLICT)) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Toast.makeText(MainActivity.this, "Code:" +
                                        // String.valueOf(inputStreamMap.get("Code")), Toast.LENGTH_LONG).show();
                                        Toast.makeText(MainActivity.this, "设备已与其他账号绑定，请重置", Toast.LENGTH_LONG).show();
                                        System.out.println(responseMsg);

                                    }
                                });

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this,
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
                    return AppConfig.DEFAULT_MAC_ADDRESS;
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
                return AppConfig.DEFAULT_MAC_ADDRESS;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AppConfig.DEFAULT_MAC_ADDRESS;
    }

    public void checkPermission(String[] PermissionString) {
        int targetSdkVersion = 0;

        try {
            final PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;// 获取应用的Target版本
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // Log.e("err", "检查权限_err0");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Build.VERSION.SDK_INT是获取当前手机版本 Build.VERSION_CODES.M为6.0系统
            // 如果系统>=6.0
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // 第 1 步: 检查是否有相应的权限
                boolean isAllGranted = checkPermissionAllGranted(PermissionString);
                if (isAllGranted) {
                    Log.e("err", "所有权限已经授权！");
                    return;
                }
                // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
                ActivityCompat.requestPermissions(this,
                        PermissionString, 1);
            }
        }
    }

    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                Log.e("Permission_Status", "权限" + permission + "没有授权");
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
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                // 所有的权限都授予了
                Log.e("Permission_Status", "权限都授权了");
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                // 容易判断错
                Log.e("Permission_Status", "某些权限未开启,请手动开启");

            }
        }
    }

    public void createFile(String path, int mode) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
            if (mode == 1) {
                try {
                    final FileWriter initiateFileWriter = new FileWriter(loginFile);
                    final CSVWriter initiateCSVWriter = new CSVWriter(initiateFileWriter);
                    String[] header = new String[] { "userName", "password", "lang" };
                    String[] fillEmpty = new String[] { "username", "password", "lang" };
                    initiateCSVWriter.writeNext(header);
                    initiateCSVWriter.writeNext(fillEmpty);

                    initiateCSVWriter.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private void nameListFileCreation() {

        String planFileLoc = this.getFilesDir() + "/hrm_plan_list/游客/";
        String athleteFileLoc = this.getFilesDir() + "/athlete_list/";
        String planFileLocCreate = planFileLoc + "方案1.csv";
        String athleteFileLocCreate = athleteFileLoc + "游客.csv";

        String planFilePrivateLoc = this.getFilesDir() + "/hrm_plan_list/专用/";
        String planFilePrivateLocCreate = planFilePrivateLoc + "方案1.csv";

        createFile(planFileLoc, 2);
        createFile(athleteFileLoc, 2);

        String[] mNameList = getResources().getStringArray(R.array.guest_list);
        String[] mBeltList = getResources().getStringArray(R.array.guest_polar_list);

        try {

            final FileWriter nameListFileWriter = new FileWriter(athleteFileLocCreate);
            final CSVWriter nameListCSVWriter = new CSVWriter(nameListFileWriter);

            for (int i = 1; i < mNameList.length; i++) {

                String[] data = new String[] { mNameList[i], mBeltList[i] };
                nameListCSVWriter.writeNext(data);
            }

            nameListCSVWriter.close();

            final FileWriter planFileWriter = new FileWriter(planFileLocCreate);
            final CSVWriter planCSVWriter = new CSVWriter(planFileWriter);

            for (int i = 1; i <= 16; i++) {

                String[] data = new String[] { mNameList[i], mBeltList[i] };
                planCSVWriter.writeNext(data);
            }

            planCSVWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // try {
        //
        // final FileWriter planFileWriter = new FileWriter(planFilePrivateLocCreate);
        // final CSVWriter planCSVWriter = new CSVWriter(planFileWriter);
        //
        // for (int i = 1; i<= 16;i++){
        //
        // String[] data = new String[]{mNameList[i], mBeltList[i]};
        // planCSVWriter.writeNext(data);
        // }
        //
        // planCSVWriter.close();
        //
        //
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

    }

    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

}

class StreamTool {
    public static String getString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int length;
        while ((length = inputStream.read(buf)) > 0) {
            byteArrayOutputStream.write(buf, 0, length);
        }
        byte[] stringBytes = byteArrayOutputStream.toByteArray();
        String str = new String(stringBytes);
        return str;
    }

}
