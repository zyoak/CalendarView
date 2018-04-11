# CalendarView
## Android日历选择控件，支持左右滑动切换月份，点击选中某天功能，使用简单方便。可以在ScrollView中使用，亦可添加到ListView头部来使用，日历的高度根据当前月份动态改变。该控件是根据公司需求定制的，如在使用过程中不满足公司需求，可以通过改写MonthView月历卡片来满足自己的需求。
<div>
<img width="280" height="460" src="https://github.com/zyoak/CalendarView/blob/master/img/in_listview.jpg"/>
<img width="280" height="460" src="https://github.com/zyoak/CalendarView/blob/master/img/in_scrollview.jpg"/>
</div>

## 1.添加依赖   
1).在项目的根目录build.gradle文件中添加如下代码：
```
    allprojects {
	       ...
	       maven { url 'https://jitpack.io' }
     }
```
2).在项目的build.gradle中添加如下依赖：
```
    dependencies {
              ...
              compile 'com.github.zyoak:CalendarView:v1.1'
    }
```
## 2.示例代码
```
      CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
      calendarView.setOnDateChangeAndPickedListener(new CalendarView.OnDateChangeAndPickedListener() {
            @Override
            public void onDateChanged(int year, int month) {   //左右切换月历回调的方法
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
            public void onDatePicked(int year, int month, int day) {   //点击选中某天回调的方法
                Toast.makeText(ScrollViewDemoActivity.this , year + "-" + month + "-" +day, Toast.LENGTH_SHORT).show();
            }
        });
        
```


