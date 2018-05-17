package com.example.soring.bandccare_banduser;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.soring.bandccare_banduser.Retrofit.Model.Response_MaxIndex;
import com.example.soring.bandccare_banduser.Retrofit.Model.Response_Sensor;
import com.example.soring.bandccare_banduser.Retrofit.RetroCallback;
import com.example.soring.bandccare_banduser.Retrofit.RetroClient;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    public static MainActivity instance;
    private DrawerLayout mDrawerLayout;
    // RetroClient retroClient;

    public static final long ref = System.currentTimeMillis();
    SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
    long mNow;
    public static Date mDate;
    public Response_MaxIndex data;
    public static int maxIndex;
    public static int startIndex;

    RetroClient retroClient;
    public int xindexstart = 0;
    Double xindex = 1.0;
    LineChart lineChart;
    XAxis xAxis;
    List<Entry> entries;

    TextView dataview;
    TextView timeTextView;
    int result;
    String time_result;
    TextView today_tv;
    //ServiceThread thread;

    public static MainActivity getInstance() {
        if (instance == null)
            instance = new MainActivity();
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        retroClient = RetroClient.getInstance().createBaseApi();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                int id = menuItem.getItemId();
                Intent intent;
                switch (id) {
                    case R.id.navigation_myPage:
                        Toast.makeText(MainActivity.this, "App user info", Toast.LENGTH_LONG).show();
                        intent = new Intent(getApplicationContext(), InfoActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra("info", 1);
                        startActivity(intent);
                        break;

                    case R.id.navigation_logout:
                        intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP

                        );
                        startActivity(intent);
                        break;

                }

                return true;
            }
        });

        dataview = findViewById(R.id.dataview);
        lineChart = findViewById(R.id.linechart);
        ImageView rabbit = findViewById(R.id.heart_img);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(rabbit);
        Glide.with(this).load(R.drawable.heart).into(gifImage);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
        String getTime = sdf.format(date);
        today_tv = findViewById(R.id.today_tv);
        today_tv.setText(getTime);
        DataThread thread = new DataThread();
        thread.setDaemon(true);
        thread.start();

        retroClient.GetMaxIndex(new RetroCallback() {
            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                data = (Response_MaxIndex) receivedData;
                maxIndex = data.getMax();
                startIndex = maxIndex - 3;
            }

            @Override
            public void onFailure(int code) {
            }
        });

        entries = new ArrayList<>();
        //entries.add(new Entry(0,0));

        LineDataSet lineDataSet = new LineDataSet(entries, "심박수(heart rate)");
        lineDataSet.setLineWidth(1);
        lineDataSet.setCircleRadius(4);
        lineDataSet.setCircleColor(Color.parseColor("#FFEC6253"));
        lineDataSet.setCircleColorHole(Color.WHITE);
        lineDataSet.setColor(Color.parseColor("#FFEF450C"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(true);
        lineDataSet.setDrawHighlightIndicators(true);
        lineDataSet.setDrawValues(false);
        lineDataSet.setDrawFilled(false);
        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        IAxisValueFormatter myformat = new HourAxisValueFormatter();

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setAvoidFirstLastClipping(true);
        //xAxis.setAxisMaximum(200);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisMinimum(0);
        xAxis.setValueFormatter(myformat);
        xAxis.setAxisMinimum(maxIndex);
        xAxis.enableGridDashedLine(15, 100, 5);

        LimitLine min = new LimitLine(50, " 최소심박수");
        LimitLine max = new LimitLine(170, "최대심박수");
        min.setLineColor(Color.GRAY);
        min.setTextColor(Color.BLACK);
        max.setLineColor(Color.GRAY);
        max.setTextColor(Color.BLACK);
        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setAxisMaximum(200);
        yLAxis.setTextColor(Color.BLACK);
        yLAxis.addLimitLine(min);
        yLAxis.addLimitLine(max);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        Description description = new Description();
        description.setText("현재시간(분:초)");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setAutoScaleMinMaxEnabled(true);
        lineChart.notifyDataSetChanged();
        lineChart.setDescription(description);
        lineChart.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        lineChart.animateY(2000, Easing.EasingOption.EaseInElastic);
        lineChart.zoom((float) 1.2, 1, 0, 0);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_119:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:119")));
                break;
            case R.id.action_call:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:010-1234-1234")));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void StartgetData() {
        retroClient.GetSensor(startIndex, new RetroCallback() {
            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                Response_Sensor data = (Response_Sensor) receivedData;
                Log.e("심박테이블 데이터 ->", String.valueOf(data.getSensor_data()));
            }

            @Override
            public void onFailure(int code) {
            }
        });
    }

    public int getData() {
        retroClient.GetSensor(startIndex, new RetroCallback() {
            Response_Sensor data;

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onSuccess(int code, Object receivedData) {
                data = (Response_Sensor) receivedData;
                result = data.getSensor_data();
            }

            @Override
            public void onFailure(int code) {
            }
        });
        return result;
    }


    public void chartUpdate(int x) {
        entries.add(new Entry(0 + xindexstart, getData()));
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
        xAxis.setAxisMaximum((float) (maxIndex + xindex));
        xAxis.setAxisMinimum(0);
        dataview.setText(String.valueOf(getData()));
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0 ){
                StartgetData();
                chartUpdate(startIndex);

                Log.e("심박테이블 Index ->", String.valueOf(startIndex));
                xindex++;
                startIndex++;
                xindexstart++;
            }
        }
    };
    class DataThread extends Thread{
        @Override
        public void run() {
            while(true){
                handler.sendEmptyMessage(0);
                try{
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return dateFormat.format(mDate);
    }



    public class HourAxisValueFormatter implements IAxisValueFormatter {

        private DateFormat mDataFormat;
        public Date mDate;

        public HourAxisValueFormatter() {
            this.mDataFormat = new SimpleDateFormat("mm:ss", Locale.KOREAN);
            //this.mDataFormat = DateFormat.getTimeInstance();
            this.mDate = new Date();
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // convertedTimestamp = originalTimestamp - referenceTimestamp
            long originalTimestamp = ref + (long) value * 2000;
            mDate.setTime(originalTimestamp);
            // Retrieve original timestamp
            // Convert timestamp to hour:minute
            return mDataFormat.format(mDate);
        }
    }


}
