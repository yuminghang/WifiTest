package com.example.yuminghang.wifitest;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView tv;
    EditText et;
    int count;
    double l1, l2, l3, l4, l5;
    double lab1, lab2, lab3, lab4, lab5;
    double r1, r2, r3, r4, r5;
    int c;
    int time;
    private WifiManager wm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        et = (EditText) findViewById(R.id.et);
        btn = (Button) findViewById(R.id.btn);
        tv = (TextView) findViewById(R.id.tv);
        tv.setText("lab1:" + r1 + "\nlab2:" + r2 + "\nlab3:" + r3 + "\nlab4:" + r4 + "\nHUAWEI--xx:" + r5);

    }

    public void click(View view) {
        time = Integer.parseInt(et.getText().toString());
        initVars();
        waiting();
    }

    public void initVars() {
        c = 0;//计算10次采样结果
        l1 = 0;
        l2 = 0;
        l3 = 0;
        l4 = 0;
        l5 = 0;
    }

    private void scan() {
        wm.startScan();  //开始扫描AP
        List<ScanResult> results = wm.getScanResults();  //拿到扫描的结果
        initlabVars();
        for (ScanResult result : results) {

            if (result.SSID.equals("lab1") && result.level > -100) {
                count++;
                lab1 = result.level;
            } else if (result.SSID.equals("lab2") && result.level > -100) {
                count++;
                lab2 = result.level;
            } else if (result.SSID.equals("lab3") && result.level > -100) {
                count++;
                lab3 = result.level;
            } else if (result.SSID.equals("lab4") && result.level > -100) {
                count++;
                lab4 = result.level;
            } else if (result.SSID.startsWith("HUAWEI") && result.level > -100) {
                count++;
                lab5 = result.level;
            }
            if (count == 5) {
                l1 += lab1;
                l2 += lab2;
                l3 += lab3;
                l4 += lab4;
                l5 += lab5;
                c++;
                break;
            }
        }
    }

    public void initlabVars() {
        count = 0;
        lab1 = 0;
        lab2 = 0;
        lab3 = 0;
        lab4 = 0;
        lab5 = 0;
    }

    private void waiting() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                final Timer timer = new Timer(true);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        scan();
                        if (c == 10) {
                            Object[] objects = new Object[6];
                            objects[0] = l1;
                            objects[1] = l2;
                            objects[2] = l3;
                            objects[3] = l4;
                            objects[4] = l5;
                            objects[5] = timer;
                            c = 0;
                            publishProgress(objects);
                        }
                    }
                }, 10, time);
                return null;
            }

            @Override
            protected void onProgressUpdate(Object[] values) {
                super.onProgressUpdate(values);
                r1 = ((double) values[0] / 10);
                r2 = ((double) values[1] / 10);
                r3 = ((double) values[2] / 10);
                r4 = ((double) values[3] / 10);
                r5 = ((double) values[4] / 10);

                tv.setText("lab1   :" + r1 + "/nlab2   :" + r2 + "/nlab3   :" + r3 + "/nlab4   :" + r4 + "/nlHUAWEI--xx:" + r5);

                ((Timer) values[5]).cancel();
            }
        };
        asyncTask.execute();

    }
}

