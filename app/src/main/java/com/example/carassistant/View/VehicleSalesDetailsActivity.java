package com.example.carassistant.View;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.R;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//监销车辆详情
public class VehicleSalesDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.iv_1)
    ImageView iv_1;
    @BindView(R.id.iv_2)
    ImageView iv_2;
    @BindView(R.id.iv_3)
    ImageView iv_3;
    @BindView(R.id.carVin)
    TextView carVin;//车架号
    @BindView(R.id.plateNumberNo)
    TextView plateNumberNo;//车牌号
    @BindView(R.id.doSupervise)
    TextView doSupervise;//是否监销
    @BindView(R.id.plateNumberColour)
    TextView plateNumberColour;//车牌颜色
    @BindView(R.id.engineNo)
    TextView engineNo;//发动机号
    @BindView(R.id.carDisplacement)
    TextView carDisplacement;//排量
    @BindView(R.id.carCode)
    TextView carCode;//车辆编号
    @BindView(R.id.newOldLevel)
    TextView newOldLevel;//新旧程度
    @BindView(R.id.registerTime)
    TextView registerTime;//注册日期
    @BindView(R.id.enterTime)
    TextView enterTime;//入场时间
    @BindView(R.id.useProperty)
    TextView useProperty;//使用性质
    @BindView(R.id.warehouse)
    TextView warehouse;//仓库位置
    @BindView(R.id.remark)
    TextView remark;//备注
    @BindView(R.id.outbound_btn)
    Button outbound_btn;//出库
    private ProgressDialog progressDialog;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehiclesalesdetails);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        initEvent();
        getChooseDismantleTypeCarsDetails();


    }

    private void initEvent(){

        outbound_btn.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    private void getChooseDismantleTypeCarsDetails(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).superviseDestroysearchCarsDetails(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            if(!jsonObject.get("data").isJsonNull()){
                                JsonObject data = jsonObject.get("data").getAsJsonObject();
                                if(!data.get("carVin").isJsonNull()){
                                    carVin.setText(data.get("carVin").getAsString());
                                }else {
                                    carVin.setText("无");
                                }
                                if(!data.get("engineNo").isJsonNull()){
                                    engineNo.setText(data.get("engineNo").getAsString());
                                }else {
                                    engineNo.setText("无");
                                }
                                if(!data.get("carDisplacement").isJsonNull()){
                                    carDisplacement.setText(data.get("carDisplacement").getAsString());
                                }else {
                                    carDisplacement.setText("无");
                                }
                                if(!data.get("carCode").isJsonNull()){
                                    carCode.setText(data.get("carCode").getAsString());
                                }else {
                                    carCode.setText("无");
                                }
                                if(!data.get("newOldLevel").isJsonNull()){
                                    newOldLevel.setText(data.get("newOldLevel").getAsString());
                                }else {
                                    newOldLevel.setText("无");
                                }
                                if(!data.get("registerTime").isJsonNull()){
                                    registerTime.setText(data.get("registerTime").getAsString());
                                }else {
                                    registerTime.setText("无");
                                }
                                if(!data.get("enterTime").isJsonNull()){
                                    enterTime.setText(data.get("enterTime").getAsString());
                                }else {
                                    enterTime.setText("无");
                                }
                                if(!data.get("useProperty").isJsonNull()){
                                    useProperty.setText(data.get("useProperty").getAsString());
                                }else {
                                    useProperty.setText("无");
                                }
                                if(!data.get("remark").isJsonNull()){
                                    remark.setText(data.get("remark").getAsString());
                                }else {
                                    remark.setText("无");
                                }
                                if(!data.get("warehouse").isJsonNull()){
                                    warehouse.setText(data.get("warehouse").getAsString());
                                }else {
                                    warehouse.setText("无");
                                }
                                if(!data.get("plateNumberNo").isJsonNull()){
                                    plateNumberNo.setText(data.get("plateNumberNo").getAsString());
                                }else {
                                    plateNumberNo.setText("无");
                                }
                                if(!data.get("plateNumberColour").isJsonNull()){
                                    plateNumberColour.setText(data.get("plateNumberColour").getAsString());
                                }else {
                                    plateNumberColour.setText("无");
                                }
                                if(getIntent().getStringExtra("plateNumberColour").equals("蓝色")){
                                    plateNumberNo.setBackgroundColor(getResources().getColor(R.color.blue));
                                    plateNumberNo.setTextColor(getResources().getColor(R.color.white));
                                }
                                if(getIntent().getStringExtra("plateNumberColour").equals("白色")){
                                    plateNumberNo.setBackgroundColor(getResources().getColor(R.color.selector_grey));
                                    plateNumberNo.setTextColor(getResources().getColor(R.color.black));
                                }
                                if(getIntent().getStringExtra("plateNumberColour").equals("绿色")){
                                    plateNumberNo.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                                    plateNumberNo.setTextColor(getResources().getColor(R.color.white));
                                }
                                if(getIntent().getStringExtra("plateNumberColour").equals("黄色")){
                                    plateNumberNo.setBackgroundColor(getResources().getColor(R.color.bg));
                                    plateNumberNo.setTextColor(getResources().getColor(R.color.white));
                                }
                                if(getIntent().getStringExtra("plateNumberColour").equals("黑色")){
                                    plateNumberNo.setBackgroundColor(getResources().getColor(R.color.black));
                                    plateNumberNo.setTextColor(getResources().getColor(R.color.white));
                                }

                                if(!data.get("doSupervise").isJsonNull()){
                                    if(data.get("doSupervise").getAsInt()==1){
                                        doSupervise.setText("监销");
                                        doSupervise.setTextColor(getResources().getColor(R.color.black));
                                    }else {
                                        doSupervise.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        doSupervise.setText("非监销");
                                    }

                                }else {
                                    doSupervise.setText("无");
                                }
                                JsonArray img = data.get("img").getAsJsonArray();
                                if(!img.get(0).getAsJsonObject().get("attachUrl").getAsString().equals("")){
                                    Glide.with(getApplicationContext()).load(img.get(0).getAsJsonObject().get("attachUrl").getAsString()).into(iv_1);
                                }
                                if(!img.get(1).getAsJsonObject().get("attachUrl").getAsString().equals("")){
                                    Glide.with(getApplicationContext()).load(img.get(1).getAsJsonObject().get("attachUrl").getAsString()).into(iv_2);
                                }
                                if(!img.get(2).getAsJsonObject().get("attachUrl").getAsString().equals("")){
                                    Glide.with(getApplicationContext()).load(img.get(2).getAsJsonObject().get("attachUrl").getAsString()).into(iv_3);
                                }





                            }


                        }else {
                            Toast.makeText(getApplicationContext(),jsonObject.get("msg").getAsString(),Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        if(v == outbound_btn){
            //出库
            showdilgo();
        }
    }

    private void showdilgo(){
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(VehicleSalesDetailsActivity.this);
        alterDiaglog.setTitle("提示");//文字
        alterDiaglog.setMessage("确定监销吗？");//提示消息
        alterDiaglog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //退出登录

                progressDialog = new ProgressDialog(VehicleSalesDetailsActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                     progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
                progressDialog.setMessage("正在提交....");
                progressDialog.show();
                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).superviseDestroyCar(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"));
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.body()!=null){
                            try {
                                String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                                JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                                if(jsonObject.get("status").getAsInt() == 0){
                                    Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
                                    CarApp carApp =(CarApp)getApplication();
                                    carApp.setRefresh(true);
                                    progressDialog.dismiss();
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
                        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
        alterDiaglog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //显示
        alterDiaglog.show();
    }

}
