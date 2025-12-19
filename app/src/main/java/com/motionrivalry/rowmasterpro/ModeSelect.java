package com.motionrivalry.rowmasterpro;

import static com.motionrivalry.rowmasterpro.MainActivity.isPad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.github.mikephil.charting.components.LimitLine;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import android.Manifest;

public class ModeSelect extends AppCompatActivity {

    private Button mSpeedometer;
    private Button mBle;
    private Button mPlayback;
    private Button mHeartRate;
    private String username;
    private String password;
    private int tabMode;

    private PopupMenu teamSelect;

    private PopupWindow mWindowBoatSelect = null;
    private View popupViewBoatSelect;

    private Button mBoat_1x;
    private Button mBoat_2x;
    private Button mBoat_4x;
    private Button mBoat_4x_front;
    private Button mBoat_4x_back;

    private Button mBoat_2mi;
    private Button mBoat_4mi;
    private Button mBoat_8mi;
    private Button mBoat_8mi_front;
    private Button mBoat_8mi_back;

    private Button mBoat_UDF;

    private String Lang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 请求权限
        requestPermissions();

        // 2. 设置默认参数
        username = "guest";
        password = "guest";
        Lang = "chn";

        // 3. 读取语言设置
        readLanguageSetting();

        // 4. 获取Intent参数（如果有）
        Intent intent = getIntent();
        if (intent.getStringExtra("userName") != null) {
            username = intent.getStringExtra("userName");
        }
        if (intent.getStringExtra("password") != null) {
            password = intent.getStringExtra("password");
        }
        if (intent.getStringExtra("lang") != null) {
            Lang = intent.getStringExtra("lang");
        }

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        // Toast.makeText(ModeSelect.this, Lang, Toast.LENGTH_LONG).show();

        if (metrics.xdpi <= 250 || isPad(this)) {
            tabMode = 1;
            setContentView(R.layout.activity_mode_select_tab);

        } else {
            tabMode = 0;

            if (Objects.equals(Lang, "eng")) {

                setContentView(R.layout.activity_mode_select_eng);

            } else {

                setContentView(R.layout.activity_mode_select);
            }

        }

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (Objects.equals(Lang, "eng")) {

            popupViewBoatSelect = inflater.inflate(R.layout.popup_select_boat_eng, null, false);

        } else {

            popupViewBoatSelect = inflater.inflate(R.layout.popup_select_boat, null, false);

        }

        mWindowBoatSelect = new PopupWindow(popupViewBoatSelect, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT, true);

        mBoat_1x = popupViewBoatSelect.findViewById(R.id.button_1x);
        mBoat_2x = popupViewBoatSelect.findViewById(R.id.button_2x);
        mBoat_4x = popupViewBoatSelect.findViewById(R.id.button_4x);
        mBoat_4x_front = popupViewBoatSelect.findViewById(R.id.button_4x_front);
        mBoat_4x_back = popupViewBoatSelect.findViewById(R.id.button_4x_back);

        mBoat_2mi = popupViewBoatSelect.findViewById(R.id.button_2mi);
        mBoat_4mi = popupViewBoatSelect.findViewById(R.id.button_4mi);
        mBoat_8mi = popupViewBoatSelect.findViewById(R.id.button_8mi);
        mBoat_8mi_front = popupViewBoatSelect.findViewById(R.id.button_8mi_front);
        mBoat_8mi_back = popupViewBoatSelect.findViewById(R.id.button_8mi_back);

        mBoat_UDF = popupViewBoatSelect.findViewById(R.id.button_UDF);

        mBoat_UDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, BleConnector.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                intent.putExtra("boatType", "UDF");
                startActivity(intent);
            }
        });

        mBoat_1x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, BleConnector.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                intent.putExtra("boatType", "1x");
                startActivity(intent);
            }
        });

        mBoat_2x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, BleConnector.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                intent.putExtra("boatType", "2x");
                startActivity(intent);
            }
        });

        mBoat_4x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, BleConnector.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                intent.putExtra("boatType", "4x");
                startActivity(intent);
            }
        });

        mBoat_4x_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, BleConnector.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                intent.putExtra("boatType", "4x_front");
                startActivity(intent);
            }
        });

        mBoat_4x_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, BleConnector.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                intent.putExtra("boatType", "4x_back");
                startActivity(intent);
            }
        });

        mBoat_2mi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, BleConnector.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                intent.putExtra("boatType", "2mi");
                startActivity(intent);
            }
        });

        mBoat_4mi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, BleConnector.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                intent.putExtra("boatType", "4mi");
                startActivity(intent);
            }
        });

        mBoat_8mi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, BleConnector.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                intent.putExtra("boatType", "8mi");
                startActivity(intent);
            }
        });

        mBoat_8mi_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, BleConnector.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                intent.putExtra("boatType", "8mi_front");
                startActivity(intent);
            }
        });

        mBoat_8mi_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, BleConnector.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                intent.putExtra("boatType", "8mi_back");
                startActivity(intent);
            }
        });

        // 已经在onCreate开始时获取了Intent参数，此处不再重复获取

        mSpeedometer = findViewById(R.id.speedometer_button);
        mSpeedometer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 修改：跳转到Speedometer而不是RowMonitor
                Intent intent = new Intent(ModeSelect.this, Speedometer.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                startActivity(intent);
            }
        });

        mBle = findViewById(R.id.ble_button);
        mBle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, BleConnector.class);
                intent.putExtra("userName", username);
                intent.putExtra("password", password);
                intent.putExtra("lang", Lang);
                showPopup(mWindowBoatSelect);
                backgroundAlpha(0.2f);

                // startActivity(intent);
            }
        });

        mPlayback = findViewById(R.id.playback_button);
        mPlayback.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ModeSelect.this, DataSelector.class);
                intent.putExtra("lang", Lang);

                startActivity(intent);
            }
        });

        if (tabMode == 1) {

            mHeartRate = findViewById(R.id.heartRate_button_tab);
            mHeartRate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    teamSelect = new PopupMenu(ModeSelect.this, v);
                    teamSelect.getMenuInflater().inflate(R.menu.menu_main, teamSelect.getMenu());
                    teamSelect.setGravity(Gravity.END);
                    teamSelect.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // 当点击时弹出被点击象标题

                            Intent intent = new Intent(ModeSelect.this, HeartRateMonitor.class);
                            intent.putExtra("teamSelected", item.getTitle());
                            Toast.makeText(getBaseContext(), item.getTitle(), Toast.LENGTH_LONG).show();

                            startActivity(intent);

                            return true;
                        }
                    });
                    // 最后调用show方法，显示菜单
                    teamSelect.show();

                    // Toast.makeText(ModeSelect.this, "Heart Rate Mode", Toast.LENGTH_LONG).show();

                }
            });

        } else {
            mHeartRate = findViewById(R.id.heartRate_button);
            mHeartRate.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {

                    teamSelect = new PopupMenu(ModeSelect.this, v);
                    teamSelect.getMenuInflater().inflate(R.menu.menu_main, teamSelect.getMenu());
                    teamSelect.setGravity(Gravity.END);
                    teamSelect.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            // 当点击时弹出被点击象标题

                            Intent intent = new Intent(ModeSelect.this, HeartRateMonitor.class);
                            intent.putExtra("teamSelected", item.getTitle());
                            Toast.makeText(getBaseContext(), item.getTitle(), Toast.LENGTH_LONG).show();

                            startActivity(intent);

                            return true;
                        }
                    });
                    // 最后调用show方法，显示菜单
                    teamSelect.show();

                    // Toast.makeText(ModeSelect.this, "Heart Rate Mode", Toast.LENGTH_LONG).show();

                    return true;

                }
            });
        }
    }

    private void showPopup(final PopupWindow popupWindow) {

        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);

        View parentView = LayoutInflater.from(ModeSelect.this).inflate(R.layout.activity_row_monitor, null);
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
    
    /**
     * 请求应用所需的所有权限
     */
    private void requestPermissions() {
        String[] PermissionString = {
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.INTERNET,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        };
        
        ActivityCompat.requestPermissions(this, PermissionString, 1);
    }
    
    /**
     * 读取本地保存的语言设置
     */
    private void readLanguageSetting() {
        try {
            File loginInfoDir = new File(getFilesDir() + File.separator + "loginInfo");
            if (!loginInfoDir.exists()) {
                loginInfoDir.mkdirs();
            }
            File loginFile = new File(loginInfoDir, "data.csv");
            
            if (loginFile.exists()) {
                CSVReader reader = new CSVReader(new FileReader(loginFile));
                List<String[]> userData = reader.readAll();
                if (userData.size() > 1) {
                    String[] row = userData.get(1);
                    if (row.length >= 3 && row[2] != null) {
                        Lang = row[2];
                    }
                }
                reader.close();
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
            // 如果读取失败，使用默认语言
            Lang = "chn";
        }
    }

}