package com.example.carassistant.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.carassistant.R;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.change_pass_lin)
    RelativeLayout change_pass_lin;//修改密码
    @BindView(R.id.login_out)
    RelativeLayout login_out;//推出登录
    SharedPreferences spf;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.setting);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        spf = getSharedPreferences("ShareData", Context.MODE_PRIVATE);
        initEvent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initEvent(){
        change_pass_lin.setOnClickListener(this);
        login_out.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_pass_lin:
                //修改密码
                Intent intent = new Intent(getApplicationContext(),ChangePassActivity.class);
                startActivity(intent);
                break;
            case R.id.login_out:
                //退出登录
                final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(SettingActivity.this);
                alterDiaglog.setTitle("提示");//文字
                alterDiaglog.setMessage("确定退出登录？");//提示消息
                alterDiaglog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //退出登录
                                            Utils.ClearData(SettingActivity.this);
                                            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                            startActivity(intent);
                                            finish();

                    }
                });
                alterDiaglog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                //显示
                alterDiaglog.show();
                break;
        }
    }
}
