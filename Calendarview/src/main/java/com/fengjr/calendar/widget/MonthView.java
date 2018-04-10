package com.fengjr.calendar.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.fengjr.calendar.pojo.DPInfo;
import com.fengjr.calendar.util.DPCManager;
import com.fengjr.calendar.util.DPUtil;
import java.util.HashSet;
import java.util.Set;
/**
 * 月历卡片
 * Created by zengyong on 2018/3/29
 */
public class MonthView extends View {

    public static final int MONTH_COLUMN = 7;  //日历总共有七列

    private Region[][] MONTH_REGIONS_6;  //将每个月份的每天分区
    private DPCManager mCManager;   // 日历管理器
    private DPInfo[][] info;   //日期数组
    private int year , month;  //当前年月
    private Paint mPaint;
    private float ratio;   // 宽高比
    private int width;    // 控件宽度
    private int height;  // 控件高度
    private int cellWidth;  // 每列宽度
    private int cellHeight; // 每列高度
    private int currentRow; // 当前月份总共分为几行
    private int lastPointX, lastPointY;

    private int textSize;  // 日期文字大小
    private int dotColor;  // 小点颜色
    private int bigDotColor; //点击当前日期显示圆形颜色
    private int whiteColor; //白色
    private int blackColor;
    private int dotRadius;

    private Set<String> needPayment;  //待回款
    private Set<String> paymented;  //已回款
    private String pickedDate;  //点击选中的日期
    private OnDatePickedListener onDatePickedListener;


    public MonthView(Context context) {
        this(context , null);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public MonthView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        ratio = 1.0f;
        needPayment = new HashSet<>();
        paymented = new HashSet<>();
        blackColor = Color.parseColor("#111111"); //黑色
        dotColor = Color.parseColor("#D9D9D9");  //灰色
        bigDotColor = Color.parseColor("#FF6633");  //橙色
        whiteColor = Color.WHITE;
        mCManager = DPCManager.getInstance();
        textSize = DPUtil.dip2px(getContext() , 18);
        dotRadius = DPUtil.dip2px(getContext() , 3);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.LINEAR_TEXT_FLAG);
        mPaint.setTextSize(textSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        cellWidth = width / MONTH_COLUMN;
        cellHeight = (int) (cellWidth * ratio);
        height = (int) (cellWidth * currentRow * ratio);
        setMeasuredDimension(width , height);
        fillRegion();
    }

    /**
     * region分区坐标
     */
    private void fillRegion(){
        if(MONTH_REGIONS_6 == null){
            MONTH_REGIONS_6 = new Region[6][7];
            for(int i=0;i<MONTH_REGIONS_6.length;i++){
                for(int j=0;j<MONTH_REGIONS_6[i].length;j++){
                    Region region = new Region();
                    region.set(j * cellWidth , i * cellHeight,
                            (j + 1) * cellWidth, (i + 1) * cellHeight);
                    MONTH_REGIONS_6[i][j] = region;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);//设置背景为白色
        if(info == null)
            return;
        mPaint.setColor(blackColor);
//        //画分割线 竖线
//        for(int i=0;i< MONTH_COLUMN;i++){
//            canvas.drawLine(cellWidth * i , 0 , cellWidth * i , height , mPaint);
//        }
//        //画分割线 横线
//        for(int i=0;i<=currentRow;i++){
//            canvas.drawLine(0 , cellHeight * i , width , cellHeight * i , mPaint);
//        }

        //画日期
        for(int i=0;i<currentRow;i++){
            for(int j=0;j<MONTH_COLUMN;j++){
                Rect rect = MONTH_REGIONS_6[i][j].getBounds();
                DPInfo dpInfo = info[i][j];
                String date = year + "-" + month + "-" + dpInfo.strG;  //当前日期
                Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                float offY = (fontMetrics.descent - fontMetrics.ascent)/4;
                String text = dpInfo.isToday ? "今" : dpInfo.strG;
                if(TextUtils.equals(pickedDate , date)){  //点击选中的日期
                    mPaint.setColor(bigDotColor);
                    canvas.drawCircle(rect.centerX() , rect.centerY() - offY , cellWidth /3 , mPaint);
                    mPaint.setColor(whiteColor);
                    canvas.drawText(text , rect.centerX() , rect.centerY() , mPaint);
                }else if(needPayment.contains(date)){  // 有待回款
                    mPaint.setColor(blackColor);
                    canvas.drawText(text , rect.centerX() , rect.centerY() , mPaint);
                    mPaint.setColor(dotColor);
                    canvas.drawCircle(rect.centerX() , rect.centerY() + 2*offY , dotRadius ,mPaint);
                }else if(paymented.contains(date)){  //已回款
                    mPaint.setColor(bigDotColor);
                    canvas.drawText(text , rect.centerX() , rect.centerY() , mPaint);
                    mPaint.setColor(dotColor);
                    canvas.drawCircle(rect.centerX() , rect.centerY() + 2*offY , dotRadius ,mPaint);
                }else if(dpInfo.isWeekend){
                    mPaint.setColor(dotColor);
                    canvas.drawText(text , rect.centerX() , rect.centerY() , mPaint);
                }else{
                    mPaint.setColor(blackColor);
                    canvas.drawText(text , rect.centerX() , rect.centerY() , mPaint);
                }

            }
        }

    }


    /**
     * 设置日期
     */
    public void setDate(int year , int month) {
        this.year = year;
        this.month = month;
        this.pickedDate = null;
        info = mCManager.obtainDPInfo(year, month);
        if (TextUtils.isEmpty(info[4][0].strG)) {
            currentRow = 4;
        } else if (TextUtils.isEmpty(info[5][0].strG)) {
            currentRow = 5;
        } else {
            currentRow = 6;
        }
        reDraw();
    }


    /**
     * 设置待回款和已回款日期
     * @param needPayment   日期格式 2018-3-29
     * @param paymented
     */
    public void setPaymentDate(@NonNull Set<String> needPayment , @NonNull Set<String> paymented){
        this.paymented = paymented;
        this.needPayment = needPayment;
        reDraw();
    }


    private void reDraw(){
        invalidate();
        requestLayout();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastPointX = (int) event.getX();
                lastPointY = (int) event.getY();
                Log.e("MonthView" , "action_down: " + lastPointX);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                Log.e("MonthView" , "action_up: ");
                if(Math.abs(event.getX() - lastPointX) < 50 && Math.abs(event.getY() - lastPointY) < 50){
                    defineRegion((int) event.getX(), (int) event.getY());
                }
                break;
        }
        return true;
    }


    private void defineRegion(int x, int y) {
        for (int i = 0; i < currentRow ; i++) {
            for (int j = 0; j < MONTH_COLUMN; j++) {
                Region region = MONTH_REGIONS_6[i][j];
                if (TextUtils.isEmpty(info[i][j].strG)) {
                    continue;
                }
                if (region.contains(x, y)) {
                    pickedDate = year + "-" + month + "-" + info[i][j].strG;
                    reDraw();
                    if (null != onDatePickedListener) {
                        onDatePickedListener.onDatePicked(year , month , Integer.valueOf(info[i][j].strG));
                    }
                }
            }
        }
    }

    public void setOnDatePickedListener(OnDatePickedListener listener){
        this.onDatePickedListener = listener;
    }

    public interface OnDatePickedListener{
        void onDatePicked(int year, int month, int day);
    }


}