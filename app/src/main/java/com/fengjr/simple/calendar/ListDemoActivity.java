package com.fengjr.simple.calendar;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.fengjr.calendar.widget.CalendarView;
import java.util.HashSet;
import java.util.Set;
/**
 * Created by zengyong on 2018/4/10
 */
public class ListDemoActivity extends Activity {


    private ListView lv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_demo);

        lv = (ListView) findViewById(R.id.lv);

        View headView = View.inflate(this , R.layout.layout_head , null);
        CalendarView calendarView = (CalendarView) headView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeAndPickedListener(new CalendarView.OnDateChangeAndPickedListener() {
            @Override
            public void onDateChanged(int year, int month) {
                Toast.makeText(ListDemoActivity.this , year + "-" + month , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDatePicked(int year, int month, int day) {
                Toast.makeText(ListDemoActivity.this , year + "-" + month + "-" +day, Toast.LENGTH_SHORT).show();
            }
        });

        Set<String> paymented = new HashSet<>();
        paymented.add("2018-4-4");
        paymented.add("2018-4-15");
        paymented.add("2018-4-28");

        Set<String> needPayment = new HashSet<>();
        needPayment.add("2018-4-8");
        needPayment.add("2018-4-10");
        needPayment.add("2018-4-19");

        calendarView.setPaymentDate(needPayment , paymented);

        lv.addHeaderView(headView);
        lv.setAdapter(new MyAdapter());
    }


    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 50;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = new TextView(parent.getContext());
            }
            TextView tv = (TextView) convertView;
            tv.setText(String.valueOf(position));
            return convertView;
        }

    }

}
