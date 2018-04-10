package com.fengjr.simple.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_list).setOnClickListener(this);
        findViewById(R.id.btn_scrollview).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_list){
            Intent intent = new Intent(this , ListDemoActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.btn_scrollview){
            Intent intent = new Intent(this , ScrollViewDemoActivity.class);
            startActivity(intent);
        }
    }
}
