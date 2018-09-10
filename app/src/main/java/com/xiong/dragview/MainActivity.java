package com.xiong.dragview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xiong.DragView;

public class MainActivity extends AppCompatActivity {

    private DragView mDragView;
    private boolean welt = true;
    private boolean bounce = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDragView = findViewById(R.id.drag);
        mDragView.onClickListener(new DragView.OnClickListener() {
            @Override
            public void onClick() {
                Toast.makeText(MainActivity.this,"Touch",Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                welt = !welt;
                mDragView.setWelt(welt);
            }
        });

        findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bounce = !bounce;
                mDragView.setBounce(bounce);
            }
        });

        findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int time = Integer.valueOf(((EditText)findViewById(R.id.et1)).getText().toString());
                mDragView.setAnimationTime(time);
            }
        });
    }
}
