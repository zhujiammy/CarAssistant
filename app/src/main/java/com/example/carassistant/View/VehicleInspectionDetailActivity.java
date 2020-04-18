package com.example.carassistant.View;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.Model.AllData;
import com.example.carassistant.R;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hyb.library.PreventKeyboardBlockUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//车辆初检
public class VehicleInspectionDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    @BindView(R.id.plateNumber)
    EditText plateNumber;//车牌数量
    @BindView(R.id.old_new_spinner)
    Spinner old_new_spinner;//新旧程度
    private String old_new_spinnerstr;//新旧程度数值
    @BindView(R.id.airConditioningPumpAmount)
    EditText airConditioningPumpAmount;//空调泵
    @BindView(R.id.carBatteryAmount)
    EditText carBatteryAmount;//电池
    @BindView(R.id.carMotorAmount)
    EditText carMotorAmount;//马达
    @BindView(R.id.carDoorAmount)
    EditText carDoorAmount;//车门
    @BindView(R.id.carAluminumRingAmount)
    EditText carAluminumRingAmount;//铝圈数量
    @BindView(R.id.carMotorsAmount)
    EditText carMotorsAmount;//电机数量
    @BindView(R.id.carWeight)
    EditText carWeight;//自重
    @BindView(R.id.carColor)
    Spinner carColor;//车身颜色
    private String strcorcolor;
    @BindView(R.id.carWaterTankAmount)
    EditText carWaterTankAmount;//水箱
    @BindView(R.id.carTyreAmount)
    EditText carTyreAmount;//轮胎
    @BindView(R.id.carSeatAmount)
    EditText carSeatAmount;//座椅
    @BindView(R.id.airConditioningAmount)
    EditText airConditioningAmount;//空调
    @BindView(R.id.threeWayCatalystAmount)
    EditText threeWayCatalystAmount;//三元催化
    @BindView(R.id.remark)
    EditText remark;//备注

    //五大总成
    @BindView(R.id.engine_group)
    RadioGroup engine_group;//发动机
    @BindView(R.id.rb_group)
    RadioGroup rb_group;//是否铝圈
    @BindView(R.id.transmission_group)
    RadioGroup transmission_group;//变速箱
    @BindView(R.id.Bab_group)
    RadioGroup Bab_group;//前后桥
    @BindView(R.id.steeringGearBox_group)
    RadioGroup steeringGearBox_group;//方向机
    @BindView(R.id.frame_group)
    RadioGroup frame_group;//车架

    @BindView(R.id.staging_btn)
    Button staging_btn;//暂存
    @BindView(R.id.ok_btn)
    Button ok_btn;//初检完成

    private int carEngine = 1;//发动机是否在
    private int carGearbox = 1;//变速箱 是否在
    private int frontAndRearAxle = 1;//前后桥是否在
    private int steeringGearBox = 1;//方向机是否在
    private int carFrame = 1;//车架是否在
    private int isCarAluminumRing = 1;//是否铝圈

    List<AllData> dicts1= new ArrayList<AllData>();
    ArrayAdapter<AllData> arrAdapterpay1;

    @BindView(R.id.YRB)
    RadioButton YRB;
    @BindView(R.id.NRB)
    RadioButton NRB;

    @BindView(R.id.engine_r1)
    RadioButton engine_r1;
    @BindView(R.id.engine_r2)
    RadioButton engine_r2;

    @BindView(R.id.transmission_r1)
    RadioButton transmission_r1;
    @BindView(R.id.transmission_r2)
    RadioButton transmission_r2;

    @BindView(R.id.Bab_r1)
    RadioButton Bab_r1;
    @BindView(R.id.Bab_r2)
    RadioButton Bab_r2;

    @BindView(R.id.steeringGearBox_r1)
    RadioButton steeringGearBox_r1;
    @BindView(R.id.steeringGearBox_r2)
    RadioButton steeringGearBox_r2;

    @BindView(R.id.frame_r1)
    RadioButton frame_r1;
    @BindView(R.id.frame_r2)
    RadioButton frame_r2;

    @BindView(R.id.carInfo)
    TextView carInfo;//车品牌
    @BindView(R.id.carCode)
    TextView carCode;//车编号
    @BindView(R.id.erterTime)
    TextView erterTime;//入场时间
    @BindView(R.id.plateNumberNo)
    TextView plateNumberNo;//车牌号
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.plateNumberNo_lin)
    LinearLayout plateNumberNo_lin;
    @BindView(R.id.line4)
    LinearLayout line4;
    @BindView(R.id.testMainEnginePlantsNum)
    TextView testMainEnginePlantsNum;
    @BindView(R.id.testState)
    TextView testState;
    @BindView(R.id.testMainEnginePlants)
    TextView testMainEnginePlants;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicleinspectiondetail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        initEvent();
        COLOUR_TYPE();//车身颜色
        searchInitialSurveyDetail();//获取待初检车辆明细

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    Spinner.OnItemSelectedListener listener1=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            strcorcolor = ((AllData)carColor.getSelectedItem()).getText();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void  initEvent(){

        staging_btn.setOnClickListener(this);
        ok_btn.setOnClickListener(this);
        carColor.setOnItemSelectedListener(listener1);
        if(getIntent().getStringExtra("type").equals("2")){
            plateNumberNo_lin.setVisibility(View.GONE);
            line4.setVisibility(View.VISIBLE);
            testMainEnginePlantsNum.setText(getIntent().getStringExtra("testMainEnginePlantsNum"));
            testState.setText(getIntent().getStringExtra("testState"));
            testMainEnginePlants.setText(getIntent().getStringExtra("testMainEnginePlants"));
        }
        carInfo.setText(getIntent().getStringExtra("carType"));
        carCode.setText(getIntent().getStringExtra("carCode"));
        erterTime.setText(getIntent().getStringExtra("erterTime"));
        if(!getIntent().getStringExtra("plateNumberNo").equals("null")){
            plateNumberNo.setText(getIntent().getStringExtra("plateNumberNo"));
        }else {
            plateNumberNo.setVisibility(View.GONE);
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

        old_new_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                old_new_spinnerstr =getResources().getStringArray(R.array.oldandnew)[position];
                Log.e("TAG", "onItemSelected: "+old_new_spinnerstr );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //是否铝圈
        rb_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.YRB:
                        isCarAluminumRing = 1;
                        break;
                    case R.id.NRB:
                        isCarAluminumRing = 0;
                        break;
                }
            }
        });

        //发动机
        engine_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.engine_r1:
                        carEngine = 1;
                        break;
                    case R.id.engine_r2:
                        carEngine = 0;
                        break;
                }
            }
        });

        //变速箱
        transmission_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.transmission_r1:
                        carGearbox = 1;
                        break;
                    case R.id.transmission_r2:
                        carGearbox = 0;
                        break;
                }
            }
        });
        //前后桥
        Bab_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.Bab_r1:
                        frontAndRearAxle = 1;
                        break;
                    case R.id.Bab_r2:
                        frontAndRearAxle = 0;
                        break;
                }
            }
        });

        //方向机
        steeringGearBox_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.steeringGearBox_r1:
                        steeringGearBox = 1;
                        break;
                    case R.id.steeringGearBox_r2:
                        steeringGearBox = 0;
                        break;
                }
            }
        });

        //车架
        frame_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.frame_r1:
                        carFrame = 1;
                        break;
                    case R.id.frame_r2:
                        carFrame = 0;
                        break;
                }
            }
        });
    }
    private void searchInitialSurveyDetail(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).searchInitialSurveyDetail(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        Log.e("jsonStr", "onResponse: "+jsonStr );
                        if(jsonObject.get("status").getAsInt() == 0){
                            if(!jsonObject.get("data").isJsonNull()){
                                JsonObject data = jsonObject.get("data").getAsJsonObject();
                                plateNumber.setText(data.get("plateNumber").getAsString());
                                if(data.get("newoldLevel").getAsString().equals("正常")){
                                    old_new_spinner.setSelection(0);
                                }
                                if(data.get("newoldLevel").getAsString().equals("火灾")){
                                    old_new_spinner.setSelection(1);
                                }
                                if(data.get("newoldLevel").getAsString().equals("碰撞")){
                                    old_new_spinner.setSelection(2);
                                }
                                if(data.get("newoldLevel").getAsString().equals("水淹")){
                                    old_new_spinner.setSelection(3);
                                }
                                airConditioningPumpAmount.setText(data.get("airConditioningPumpAmount").getAsString());
                                carBatteryAmount.setText(data.get("carBatteryAmount").getAsString());
                                carMotorAmount.setText(data.get("carMotorAmount").getAsString());
                                carDoorAmount.setText(data.get("carDoorAmount").getAsString());
                                carAluminumRingAmount.setText(data.get("carAluminumRingAmount").getAsString());
                                carMotorsAmount.setText(data.get("carMotorsAmount").getAsString());
                                carWaterTankAmount.setText(data.get("carWaterTankAmount").getAsString());
                                carTyreAmount.setText(data.get("carTyreAmount").getAsString());
                                carSeatAmount.setText(data.get("carSeatAmount").getAsString());
                                airConditioningAmount.setText(data.get("airConditioningAmount").getAsString());
                                threeWayCatalystAmount.setText(data.get("threeWayCatalystAmount").getAsString());
                                //carWeight.setText(data.get("carWeight").getAsString());


                                if(data.get("isCarAluminumRing").getAsInt() == 0){
                                    isCarAluminumRing = 0;
                                    NRB.setChecked(true);
                                }
                                if(data.get("isCarAluminumRing").getAsInt() == 1){
                                    isCarAluminumRing = 1;
                                    YRB.setChecked(true);
                                }

                                if(data.get("carEngine").getAsInt() == 0){
                                    carEngine = 0;
                                    engine_r2.setChecked(true);
                                }
                                if(data.get("carEngine").getAsInt() == 1){
                                    carEngine = 1;
                                    engine_r1.setChecked(true);
                                }
                                if(data.get("carGearbox").getAsInt() == 0){
                                    carGearbox = 0;
                                    transmission_r2.setChecked(true);
                                }
                                if(data.get("carGearbox").getAsInt() == 1){
                                    carGearbox = 1;
                                    transmission_r1.setChecked(true);
                                }
                                if(data.get("frontAndRearAxle").getAsInt() == 0){
                                    frontAndRearAxle = 0;
                                    Bab_r2.setChecked(true);
                                }
                                if(data.get("frontAndRearAxle").getAsInt() == 1){
                                    frontAndRearAxle = 1;
                                    Bab_r1.setChecked(true);
                                }
                                if(data.get("steeringGearBox").getAsInt() == 0){
                                    steeringGearBox = 0;
                                    steeringGearBox_r2.setChecked(true);
                                }
                                if(data.get("steeringGearBox").getAsInt() == 1){
                                    steeringGearBox = 1;
                                    steeringGearBox_r1.setChecked(true);
                                }
                                Log.e("carFrame", "carFrame: "+ data.get("carFrame").getAsInt());
                                if(data.get("carFrame").getAsInt() == 0){
                                    carFrame = 0;
                                    frame_r2.setChecked(true);
                                }else if(data.get("carFrame").getAsInt() == 1){
                                    carFrame = 1;
                                    frame_r1.setChecked(true);
                                }
                                remark.setText(data.get("remark").getAsString());
                                int k= arrAdapterpay1.getCount();
                                for(int i=0;i<k;i++){
                                    if(data.get("carColor").getAsString().equals(arrAdapterpay1.getItem(i).getText())){
                                        carColor.setSelection(i,true);// 默认选中项
                                        break;
                                    }
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
                Toast.makeText(getApplicationContext(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void COLOUR_TYPE(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).COLOUR_TYPE(Utils.getShared2(getApplicationContext(),"token"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonArray jsonElements = jsonObject.getAsJsonArray("data");
                            for(int i = 0;i<jsonElements.size();i++){
                                dicts1.add(new AllData("",jsonElements.get(i).getAsString()));
                                arrAdapterpay1 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts1);
                                //设置样式
                                arrAdapterpay1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                carColor.setAdapter(arrAdapterpay1);
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),jsonObject.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_LONG).show();
                Log.e("TAG", "onResponse: "+"连接超时，请检查网络环境，避免影响使用！");
            }
        });
    }

    private void saveInitialSurveyDetail(int type){
        if(TextUtils.isEmpty(plateNumber.getText().toString())){
            Toast.makeText(getApplicationContext(),"车牌数量不能为空",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(airConditioningPumpAmount.getText().toString())){
            Toast.makeText(getApplicationContext(),"空调泵数量不能为空",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(carBatteryAmount.getText().toString())){
            Toast.makeText(getApplicationContext(),"电池数量不能为空",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(carMotorAmount.getText().toString())){
            Toast.makeText(getApplicationContext(),"马达数量不能为空",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(carDoorAmount.getText().toString())){
            Toast.makeText(getApplicationContext(),"车门数量不能为空",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(carAluminumRingAmount.getText().toString())){
            Toast.makeText(getApplicationContext(),"铝圈数量不能为空",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(carMotorsAmount.getText().toString())){
            Toast.makeText(getApplicationContext(),"电机数量不能为空",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(carWaterTankAmount.getText().toString())){
            Toast.makeText(getApplicationContext(),"水箱数量不能为空",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(carTyreAmount.getText().toString())){
            Toast.makeText(getApplicationContext(),"轮胎数量不能为空",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(carSeatAmount.getText().toString())){
            Toast.makeText(getApplicationContext(),"座椅数量不能为空",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(airConditioningAmount.getText().toString())){
            Toast.makeText(getApplicationContext(),"空调数量不能为空",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(threeWayCatalystAmount.getText().toString())){
            Toast.makeText(getApplicationContext(),"三元催化器数量不能为空",Toast.LENGTH_SHORT).show();
        }/*else if(TextUtils.isEmpty(carWeight.getText().toString())){
            Toast.makeText(getApplicationContext(),"自重不能为空",Toast.LENGTH_SHORT).show();
        }*/else {
            Gson gson = new Gson();
            progressDialog = new ProgressDialog(VehicleInspectionDetailActivity.this,
                    R.style.AppTheme_Dark_Dialog);
                 progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
            progressDialog.setMessage("正在提交....");
            progressDialog.show();
            Map<String,Object> map = new HashMap<>();
            map.put("carDetailId",getIntent().getStringExtra("carDetailId"));
            map.put("type",type);
            map.put("plateNumber",plateNumber.getText().toString());
            map.put("newoldLevel",old_new_spinnerstr);
            map.put("airConditioningPumpAmount",airConditioningPumpAmount.getText().toString());
            map.put("carBatteryAmount",carBatteryAmount.getText().toString());
            map.put("carMotorAmount",carMotorAmount.getText().toString());
            map.put("carDoorAmount",carDoorAmount.getText().toString());
            map.put("carAluminumRingAmount",carAluminumRingAmount.getText().toString());
            map.put("carMotorsAmount",carMotorsAmount.getText().toString());
            map.put("carWaterTankAmount",carWaterTankAmount.getText().toString());
            map.put("carTyreAmount",carTyreAmount.getText().toString());
            map.put("carSeatAmount",carSeatAmount.getText().toString());
            map.put("airConditioningAmount",airConditioningAmount.getText().toString());
            map.put("threeWayCatalystAmount",threeWayCatalystAmount.getText().toString());
            map.put("carWeight",carWeight.getText().toString());
            map.put("carColor",strcorcolor);
            map.put("carEngine",carEngine);
            map.put("carGearbox",carGearbox);
            map.put("isCarAluminumRing",isCarAluminumRing);
            map.put("frontAndRearAxle",frontAndRearAxle);
            map.put("steeringGearBox",steeringGearBox);
            map.put("carFrame",carFrame);
            map.put("remark",remark.getText().toString());
            String obj =gson.toJson(map);
            RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),obj);
            Log.e("TAG", "提交的数据: "+obj );
            if(getIntent().getStringExtra("type").equals("1")){
                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).saveInitialSurveyDetail(Utils.getShared2(getApplicationContext(),"token"),body);
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
                        Toast.makeText(getApplicationContext(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }else {
                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).testcarsaveInitialSurveyDetail(Utils.getShared2(getApplicationContext(),"token"),body);
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
                        Toast.makeText(getApplicationContext(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }

        }

    }



    @Override
    public void onClick(View v) {
        if(v == staging_btn){
            //暂存
            saveInitialSurveyDetail(0);
        }
        if(v == ok_btn){
            //初检完成
            saveInitialSurveyDetail(1);

        }

    }
}
