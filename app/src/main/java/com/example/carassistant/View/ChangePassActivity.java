package com.example.carassistant.View;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.R;
import com.example.carassistant.retrofit.HttpHelper;
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

public class ChangePassActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.oldPassword)
    EditText oldPassword;//旧密码
    @BindView(R.id.newPassword)
    EditText newPassword;//新密码
    @BindView(R.id.confirmPassword)
    EditText confirmPassword;//确认密码
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.changepass);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        initEvent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        if (id == R.id.sumbit){
            //提交修改密码
            if(TextUtils.isEmpty(oldPassword.getText().toString())){
                Toast.makeText(getApplicationContext(),"旧密码不能为空哦！",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(newPassword.getText().toString())){
                Toast.makeText(getApplicationContext(),"新密码不能为空哦！",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(confirmPassword.getText().toString())){
                Toast.makeText(getApplicationContext(),"确认密码不能为空哦！",Toast.LENGTH_SHORT).show();
            }else if(!confirmPassword.getText().toString().equals(newPassword.getText().toString())){
                Toast.makeText(getApplicationContext(),"两次密码输入不一致哦！",Toast.LENGTH_SHORT).show();
            }else {
                progressDialog = new ProgressDialog(this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("正在提交....");
                progressDialog.show();
                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).changePassword(Utils.getShared2(getApplicationContext(),"token"),Utils.getShared2(getApplicationContext(),"userId"),oldPassword.getText().toString(),newPassword.getText().toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.body()!=null){
                            try {
                                String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                                JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                                if(jsonObject.get("status").getAsInt() == 0){
                                    Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    Utils.ClearData(ChangePassActivity.this);
                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Toast.makeText(getApplicationContext(),jsonObject.get("msg").getAsString(),Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private void initEvent(){


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_pass_lin:
                //修改密码

                break;

        }
    }
}
