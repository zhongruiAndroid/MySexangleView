package com.test.sexangle;

import android.graphics.Color;
import android.graphics.SweepGradient;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.sexangle.SexangleView;

public class MainActivity extends AppCompatActivity {
    SexangleView sv;
    SeekBar sb;
    TextView tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tv=findViewById(R.id.tv);
        sv=findViewById(R.id.sv);

        sv.setShader(null);
//        SweepGradient  shader = new SweepGradient(0, 0, new int[]{Color.parseColor("#34e8a6"), Color.parseColor("#06C1AE"), Color.parseColor("#34e8a6")}, null);
//        sv.setShader(shader);
        sb=findViewById(R.id.sb);

        sv.setMax(sb.getMax());
        sb.setProgress((int) sv.getProgress());

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//              int progress = seekBar.getProgress();
                sv.setProgress(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        sv.setOnProgressChangeInter(new SexangleView.OnProgressChangeInter() {
            @Override
            public void progress(float scaleProgress, float progress, float max) {
                tv.setText("总进度："+max+",当前进度"+progress+"动画进度："+scaleProgress);
            }
        });

        tv.setText("总进度："+sv.getMax()+",当前进度"+sv.getProgress()+"动画进度："+sv.getProgress());





    }
    public int getTheColor(int resId){
        return ContextCompat.getColor(this,resId);
    }


}
