package com.example.carassistant.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carassistant.MainActivity;
import com.example.carassistant.R;
import com.example.carassistant.utils.Utils;
import com.tencent.bugly.Bugly;

public class WelcomScreenActivity extends AppCompatActivity {


    private TimeCount time;
    private String flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcomescreen);
        Bugly.init(getApplicationContext(), "501afac536", false);
        flag = Utils.getShared2(getApplicationContext(),"flag");
        time = new TimeCount(3000, 1000);//构造CountDownTimer对象
        time.start();

    }


    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            Intent intent;
            if(flag.equals("1")){
                intent = new Intent(WelcomScreenActivity.this, MainActivity.class);
                startActivity(intent);
            }else {
                intent = new Intent(WelcomScreenActivity.this,LoginActivity.class);
                startActivity(intent);
            }

            finish();
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示

        }
    }
}
