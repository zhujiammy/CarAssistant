package com.example.carassistant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.View.DestroyVehicle;
import com.example.carassistant.View.ExperimentalVehicle;
import com.example.carassistant.View.MyTask;
import com.example.carassistant.View.SettingActivity;
import com.example.carassistant.View.SocialCarDetailsActivity;
import com.example.carassistant.View.TestCarDetailsActivity;
import com.example.carassistant.View.VehicleManagement;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.item.NormalItemView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.title)
    TextView title;//标题
    @BindView(R.id.qr_lin)
    LinearLayout qr_lin;//扫一扫
    @BindView(R.id.setting_lin)
    LinearLayout setting_lin;//设置

    private static  int REQUEST_CODE = 1001;
    private FragmentManager manager;
    private NavigationController navigationController;
    public MyTask myTask;//我的任务
    public VehicleManagement vehicleManagement;//车辆管理
    public DestroyVehicle destroyVehicle;//毁型管理
    public ExperimentalVehicle experimentalVehicle;//试验车

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //必须在super 之前调用,不然无效。因为那时候fragment已经被恢复了。
        if (savedInstanceState != null) {
            // FRAGMENTS_TAG
            savedInstanceState.remove("android:support:fragments");
            savedInstanceState.remove("android:fragments");
        }
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        qr_lin.setOnClickListener(this);
        setting_lin.setOnClickListener(this);

        initUI();//初始化界面
    }

    private void initUI(){
        Log.e("TAG", "initUI: "+ Utils.getShared2(getApplicationContext(),"userName"));
        Log.e("TAG", "initUI: "+ Utils.getShared2(getApplicationContext(),"token"));
        title.setText("我的任务");
        PageBottomTabLayout tab = (PageBottomTabLayout) findViewById(R.id.tab);
        navigationController =tab.custom()
                .addItem(newItem(R.mipmap.task_n,R.mipmap.task_s,"我的任务"))
                .addItem(newItem(R.mipmap.vehicle_n,R.mipmap.vehicle_s,"车辆管理"))
                .addItem(newItem(R.mipmap.destroy_n,R.mipmap.destroy_s,"毁型管理"))
                .addItem(newItem(R.mipmap.experimentalvehicle_n,R.mipmap.experimentalvehicle_s,"试验车"))
                .build();
        navigationController.addTabItemSelectedListener(listener);

        myTask = new MyTask();
        vehicleManagement = new VehicleManagement();
        destroyVehicle = new DestroyVehicle();
        experimentalVehicle = new ExperimentalVehicle();
        manager = getSupportFragmentManager();
        //初次登陆显示首页
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.add(R.id.main_content,myTask);
        transaction.add(R.id.main_content,vehicleManagement);
        transaction.add(R.id.main_content,destroyVehicle);
        transaction.add(R.id.main_content,experimentalVehicle);

        transaction.show(myTask);
        transaction.hide(vehicleManagement);
        transaction.hide(destroyVehicle);
        transaction.hide(experimentalVehicle);
        transaction.commit();
    }

    OnTabItemSelectedListener listener=new OnTabItemSelectedListener() {
        @Override
        public void onSelected(int index, int old) {
            FragmentTransaction transaction=manager.beginTransaction();
            switch (index){
                //当选中首页id时，显示framelayout加载首页fragment
                case 0:
                    transaction.show(myTask);
                    transaction.hide(vehicleManagement);
                    transaction.hide(destroyVehicle);
                    transaction.hide(experimentalVehicle);
                    title.setText("我的任务");
                    transaction.commit();
                    break;

                case 1:
                    transaction.show(vehicleManagement);
                    transaction.hide(myTask);
                    transaction.hide(destroyVehicle);
                    transaction.hide(experimentalVehicle);
                    title.setText("车辆管理");
                    transaction.commit();
                    break;
                case 2:
                    transaction.show(destroyVehicle);
                    transaction.hide(vehicleManagement);
                    transaction.hide(myTask);
                    transaction.hide(experimentalVehicle);
                    title.setText("毁型管理");
                    transaction.commit();
                    break;
                case 3:
                    transaction.show(experimentalVehicle);
                    transaction.hide(destroyVehicle);
                    transaction.hide(vehicleManagement);
                    transaction.hide(myTask);
                    title.setText("试验车");
                    transaction.commit();
                    break;


            }
        }

        @Override
        public void onRepeat(int index) {

        }
    };


    private BaseTabItem newItem(int drawable, int checkedDrawable, String text){
        NormalItemView normalItemView =new NormalItemView(this);
        normalItemView.initialize(drawable,checkedDrawable,text);
        normalItemView.setTextDefaultColor(Color.GRAY);
        normalItemView.setTextCheckedColor(getResources().getColor(R.color.black));
        return  normalItemView;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(null != data){
                Bundle bundle = data.getExtras();
                if(bundle == null){
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    cartype(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(MainActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

private void cartype(String result){
    Log.e("TAG", "cartype: "+result );
    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).carModelType(Utils.getShared2(getApplicationContext(),"token"),result);
    call.enqueue(new Callback<ResponseBody>() {
        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            if(response.body()!=null){
                try {
                    String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                    JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                    if(jsonObject.get("status").getAsInt() == 0){
                        if(jsonObject.get("data").getAsInt() ==2){
                            //解析社会车详情
                            Intent intent = new Intent(MainActivity.this, SocialCarDetailsActivity.class);
                            intent.putExtra("carDetailId",result);
                            startActivity(intent);
                        }else {
                            //解析试验车详情
                            Intent intent = new Intent(MainActivity.this, TestCarDetailsActivity.class);
                            intent.putExtra("carDetailId",result);
                            startActivity(intent);
                        }

                    }else {
                        Toast.makeText(MainActivity.this,jsonObject.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
            Log.e("TAG", "onResponse: "+t.getMessage());
        }
    });
}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qr_lin:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 没有权限，申请权限。
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},1);

                }else{
                    Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                    startActivityForResult(intent,REQUEST_CODE);
                }


                break;
            case R.id.setting_lin:
                //设置
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     *
     * @param requestCode
     * @param permissions 请求的权限
     * @param grantResults 请求权限返回的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        }

    }



}
