package com.fengjr.simple.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.fengjr.calendar.widget.CalendarView;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zengyong on 2018/4/10
 */
public class ScrollViewDemoActivity extends Activity {

    private CalendarView calendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrollview_demo);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeAndPickedListener(new CalendarView.OnDateChangeAndPickedListener() {
            @Override
            public void onDateChanged(int year, int month) {
                Set<String> paymented = new HashSet<>();
                paymented.add("2018-3-4");
                paymented.add("2018-3-15");
                paymented.add("2018-3-28");

                Set<String> needPayment = new HashSet<>();
                needPayment.add("2018-3-8");
                needPayment.add("2018-3-10");
                needPayment.add("2018-3-19");

                calendarView.setPaymentDate(needPayment , paymented);

                Toast.makeText(ScrollViewDemoActivity.this , year + "-" + month , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDatePicked(int year, int month, int day) {
                Toast.makeText(ScrollViewDemoActivity.this , year + "-" + month + "-" +day, Toast.LENGTH_SHORT).show();
            }
        });



    }

}
