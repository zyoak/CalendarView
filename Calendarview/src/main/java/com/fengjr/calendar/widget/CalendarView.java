package com.fengjr.calendar.widget;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fengjr.calendar.R;
import com.fengjr.calendar.pojo.DPInfo;
import com.fengjr.calendar.util.DPCManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
/**
 * 日历控件
 * Created by zengyong on 2018/3/30
 */
public class CalendarView extends FrameLayout implements View.OnClickListener {

    private final String TAG = "CalendarView";
    private static final int INDEX_POSITION = 1000;  //当前设置日期对应viewpager对位置

    private ViewPager viewpager;
    private TextView tv_date;    // 年月指示标题
    private TextView tv_currentDate; // 返回本月
    private DPCManager dpcManager;   //日期管理器
    private CalendarViewAdapter adapter;
    private int positionYear , positionMonth;  //viewpager指定位置对应对年月，作为标点
    private OnDateChangeAndPickedListener onDateChangeAndPickListener;
    private int calenderHeight;

    public CalendarView(@NonNull Context context) {
        this(context , null);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public CalendarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        setListener();
    }

    private void init(){
        dpcManager = DPCManager.getInstance();
        View contentView = View.inflate(getContext() , R.layout.layout_calendar_view , null);
        tv_date = (TextView) contentView.findViewById(R.id.tv_date);
        tv_currentDate = (TextView) contentView.findViewById(R.id.tv_currentDate);
        tv_currentDate.setOnClickListener(this);
        contentView.findViewById(R.id.iv_leftArrow).setOnClickListener(this);
        contentView.findViewById(R.id.iv_rightArrow).setOnClickListener(this);
        viewpager = (ViewPager) contentView.findViewById(R.id.viewpager);
        //获取当天年月
        Calendar calendar = Calendar.getInstance();
        positionYear = calendar.get(Calendar.YEAR);
        positionMonth = calendar.get(Calendar.MONTH) + 1;
        setTitleDate(positionYear , positionMonth);

        calenderHeight = getCalendarViewHeight(positionYear , positionMonth);

        adapter = new CalendarViewAdapter(positionYear , positionMonth);
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(INDEX_POSITION);
        viewpager.setOffscreenPageLimit(1);

        setViewPagerHeight();

        addView(contentView);
    }


    private void setViewPagerHeight(){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewpager.getLayoutParams();
        params.height = getCalendarViewHeight();
        viewpager.setLayoutParams(params);
    }


    private void setTitleDate(int year , int month){
        tv_date.setText(year + "年" + month + "月");
    }

    /**
     * 是否显示返回本月按钮
     */
    private void showOrHideResetMonthButton(boolean isShow){
        tv_currentDate.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void setListener(){
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int offPosition = position - INDEX_POSITION;
                int newMonth = (positionMonth + offPosition - 1) % 12 + 1;
                int newYear = positionYear + (positionMonth + offPosition -1) / 12;
                if(newMonth <= 0){
                    newMonth = 12 + newMonth;
                    newYear = newYear -1;
                }
                setTitleDate(newYear , newMonth);
                showOrHideResetMonthButton(position != INDEX_POSITION);
                adapter.setDate(newYear, newMonth);
                calenderHeight = getCalendarViewHeight(newYear , newMonth);
                if(onDateChangeAndPickListener != null){
                    onDateChangeAndPickListener.onDateChanged(newYear , newMonth);
                }
                setViewPagerHeight();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    public int getCalendarViewHeight(){
        return calenderHeight;
    }

    /**
     * 根据年月获取控件的高度
     * @param year
     * @param month
     * @return
     */
    private int getCalendarViewHeight(int year , int month){
        DPInfo[][] info = dpcManager.obtainDPInfo(year, month);
        int calendarRow = 0;
        if (TextUtils.isEmpty(info[4][0].strG)) {
            calendarRow = 4;
        } else if (TextUtils.isEmpty(info[5][0].strG)) {
            calendarRow = 5;
        } else {
            calendarRow = 6;
        }
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.widthPixels / MonthView.MONTH_COLUMN * calendarRow;
        return height;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_leftArrow){
            viewpager.setCurrentItem(viewpager.getCurrentItem() - 1);
        }else if(v.getId() == R.id.iv_rightArrow){
            viewpager.setCurrentItem(viewpager.getCurrentItem() + 1);
        }else if(v.getId() == R.id.tv_currentDate){
            if(viewpager.getCurrentItem() != INDEX_POSITION){
                viewpager.setCurrentItem(INDEX_POSITION , false);
                showOrHideResetMonthButton(false);
                if(onDateChangeAndPickListener != null){
                    onDateChangeAndPickListener.onDateChanged(positionYear , positionMonth);
                }
            }
        }
    }

    /**
     * 设置待回款和已回款日期
     * @param needPayment   日期格式 2018-3-29
     * @param paymented
     */
    public void setPaymentDate(@NonNull Set<String> needPayment , @NonNull Set<String> paymented){
        adapter.setPaymentDate(needPayment , paymented);
    }


    private class CalendarViewAdapter extends PagerAdapter {

        private ArrayList<MonthView> calendars = new ArrayList<>();

        /**
         * @param currentMonth  1-12
         */
        public CalendarViewAdapter(int currentYear , int currentMonth){
            for(int i=0;i<3;i++){
                MonthView monthView = new MonthView(getContext());
                monthView.setDate(currentYear,currentMonth);
                monthView.setOnDatePickedListener(new MonthView.OnDatePickedListener() {
                    @Override
                    public void onDatePicked(int year, int month, int day) {
                        if(onDateChangeAndPickListener != null){
                            onDateChangeAndPickListener.onDatePicked(year , month , day);
                        }
                    }
                });
                calendars.add(monthView);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MonthView calendar = calendars.get(position % calendars.size());
            ViewGroup parent = (ViewGroup) calendar.getParent();
            if(parent != null){
                parent.removeView(calendar);
            }
            Log.e(TAG , "position: " + position);
            container.addView(calendar);
            return calendar;
        }

        /**
         * 要显示的设置年月
         * @param year
         * @param month
         */
        public void setDate(int year , int month){
            MonthView monthView = calendars.get(viewpager.getCurrentItem() % calendars.size());
            monthView.setDate(year , month);
        }

        /**
         * @param needPayment 待回款，
         * @param paymented  未回款
         */
        public void setPaymentDate(@NonNull Set<String> needPayment , @NonNull Set<String> paymented){
            MonthView monthView = calendars.get(viewpager.getCurrentItem() % calendars.size());
            monthView.setPaymentDate(needPayment , paymented);
        }

        @Override
        public int getCount() {
            return 2000;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(container);
        }

    }

    public void setOnDateChangeAndPickedListener(OnDateChangeAndPickedListener listener){
        this.onDateChangeAndPickListener = listener;
    }

    public interface OnDateChangeAndPickedListener{

        void onDateChanged(int year, int month);

        void onDatePicked(int year, int month, int day);

    }

}
