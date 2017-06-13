package com.yunqitechbj.clientandroid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;
import android.widget.Toast;

import com.yunqitechbj.clientandroid.R;
import com.yunqitechbj.clientandroid.entity.OfflineStorage;

/**
 * Created by mengwei on 2016/11/3.
 */

public class CalendarViewActivity extends AppCompatActivity {
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dsadasda);

        calendarView = (CalendarView) findViewById(R.id.cv_rili);

        calendarView.setShowWeekNumber(false);
//        calendarView.setMaxDate();



        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String date = year + "年" + month + "月" + dayOfMonth + "日";
                Toast.makeText(getApplicationContext(), date, Toast.LENGTH_LONG).show();
            }
        });

        OfflineStorage offlineStorage = new OfflineStorage();
        offlineStorage.setVehicleName("");
        offlineStorage.save();


    }

    public static void invoke(Context context){
        Intent intent = new Intent(context,CalendarViewActivity.class);
        context.startActivity(intent);
    }
}
