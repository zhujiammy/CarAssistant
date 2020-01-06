package com.example.carassistant.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carassistant.APP.CarApp;
import com.example.carassistant.Interface.IEditTextChangeListener;
import com.example.carassistant.Interface.LoginInterface;
import com.example.carassistant.MainActivity;
import com.example.carassistant.Presenter.LoginPresenter;
import com.example.carassistant.R;
import com.example.carassistant.utils.WorksSizeCheckUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LoginInterface.View {

    @BindView(R.id.username_et)
    EditText username_et;//用户名
    @BindView(R.id.password_et)
    EditText password_et;//密码
    @BindView(R.id.button)
    Button button;//登录
    private ProgressDialog progressDialog;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        loginPresenter = new LoginPresenter(this,this);
        initUI();//初始化
    }

    private void initUI(){
        button.setOnClickListener(this);
        //1.创建工具类对象 把要改变颜色的tv先传过去
        WorksSizeCheckUtil.textChangeListener textChangeListener = new WorksSizeCheckUtil.textChangeListener(button);

        //2.把所有要监听的edittext都添加进去
        textChangeListener.addAllEditText(username_et,password_et);

        //3.接口回调 在这里拿到boolean变量 根据isHasContent的值决定 tv 应该设置什么颜色
        WorksSizeCheckUtil.setChangeListener(new IEditTextChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void textChange(boolean isHasContent) {
                if(isHasContent){
                    button.setEnabled(true);
                    button.setBackground(getResources().getDrawable(R.drawable.login_btn_backgroud_unfocus));
                    button.setTextColor(getResources().getColor(R.color.white));
                }else{
                    button.setEnabled(false);
                    button.setBackground(getResources().getDrawable(R.drawable.login_btn_backgroud));
                    button.setTextColor(getResources().getColor(R.color.black));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                //登录
                progressDialog = new ProgressDialog(LoginActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("登陆中....");
                progressDialog.show();
                Map<String,Object> map = new HashMap<>();
                map.put("userName",username_et.getText().toString());
                map.put("password",password_et.getText().toString());
                loginPresenter.login(map);
                break;
        }
    }

    @Override
    public void succeed() {
       Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void failed() {
        progressDialog.dismiss();
    }
}
