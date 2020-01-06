package com.example.carassistant.View;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.MainActivity;
import com.example.carassistant.Model.AllData;
import com.example.carassistant.R;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//车辆入库明细
public class WarehouseCarsDetailsActivity extends AppCompatActivity implements View.OnClickListener {

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

    private String warehouseId ;//位置id
    private ProgressDialog progressDialog;
    @BindView(R.id.spinner1)
    AppCompatSpinner spinner1;
    @BindView(R.id.spinner2)
    AppCompatSpinner spinner2;
    @BindView(R.id.spinner3)
    AppCompatSpinner spinner3;
    @BindView(R.id.spinner4)
    AppCompatSpinner spinner4;
    List<AllData> dicts1= new ArrayList<AllData>();
    ArrayAdapter<AllData> arrAdapterpay1;
    List<AllData> dicts2= new ArrayList<AllData>();
    ArrayAdapter<AllData> arrAdapterpay2;
    List<AllData> dicts3= new ArrayList<AllData>();
    ArrayAdapter<AllData> arrAdapterpay3;
    List<AllData> dicts4= new ArrayList<AllData>();
    ArrayAdapter<AllData> arrAdapterpay4;
    JsonArray disWarehouses;
    JsonArray children;
    JsonArray children1;
    JsonArray children2;
    ArrayAdapter<AllData> arrAdapterpay2_A;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private static  int REQUEST_CODE = 1001;
    @BindView(R.id.plateNumberNo_lin)
    LinearLayout plateNumberNo_lin;
    @BindView(R.id.carVin_lin)
    LinearLayout carVin_lin;
    @BindView(R.id.line4)
    LinearLayout line4;
    @BindView(R.id.testMainEnginePlantsNum)
    TextView testMainEnginePlantsNum;
    @BindView(R.id.testState)
    TextView testState;
    @BindView(R.id.testMainEnginePlants)
    TextView testMainEnginePlants;
    @BindView(R.id.location_lin)
    LinearLayout location_lin;//仓库位置
    @BindView(R.id.ok_btn)
    Button ok_btn;//保存
    @BindView(R.id.warehouse_lin)
    LinearLayout warehouse_lin;//仓库位置
    @BindView(R.id.chaijie)
    LinearLayout chaijie;//拆解方式
    @BindView(R.id.dismantleType)
    TextView dismantleType;//拆解方式字段

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.warehousecarsdetails);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        initEvent();
        getChooseDismantleTypeCarsDetails();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.qrmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        if (id == R.id.qr) {
            //扫描仓库位置二维码
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // 没有权限，申请权限。
                ActivityCompat.requestPermissions(WarehouseCarsDetailsActivity.this,new String[]{Manifest.permission.CAMERA},1);

            }else{
                Intent intent = new Intent(WarehouseCarsDetailsActivity.this, CaptureActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void initEvent(){
        if(getIntent().getStringExtra("type").equals("2")){
            plateNumberNo_lin.setVisibility(View.GONE);
            line4.setVisibility(View.VISIBLE);
            warehouse_lin.setVisibility(View.GONE);
            chaijie.setVisibility(View.VISIBLE);
            carVin_lin.setVisibility(View.GONE);

            testMainEnginePlantsNum.setText(getIntent().getStringExtra("testMainEnginePlantsNum"));
            testState.setText(getIntent().getStringExtra("testState"));
            testMainEnginePlants.setText(getIntent().getStringExtra("testMainEnginePlants"));
            dismantleType.setText(getIntent().getStringExtra("dismantleType"));
        }
        ok_btn.setOnClickListener(this);
        spinner1.setOnItemSelectedListener(listener1);
        spinner2.setOnItemSelectedListener(listener2);
        spinner3.setOnItemSelectedListener(listener3);
        spinner4.setOnItemSelectedListener(listener4);

    }

    //位置一
    Spinner.OnItemSelectedListener listener1=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            dicts2.clear();
            children=disWarehouses.get(position).getAsJsonObject().get("children").getAsJsonArray();
            if(children.size() == 0){
                dicts2.add(new AllData("无","无"));
                arrAdapterpay2 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts2);
                //设置样式
                arrAdapterpay2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner2.setAdapter(arrAdapterpay2);
                children1 = null;

            }else {
                children1 = children;
                for(int i = 0;i<children.size();i++){
                    JsonObject childrenobj = children.get(i).getAsJsonObject();
                    dicts2.add(new AllData(childrenobj.get("warehouseId").getAsString(),childrenobj.get("warehouse").getAsString()));
                    arrAdapterpay2 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts2);
                    //设置样式
                    arrAdapterpay2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner2.setAdapter(arrAdapterpay2);

                }
            }



        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //位置2
    Spinner.OnItemSelectedListener listener2=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            dicts3.clear();
            if(children1 != null){
                children1=children.get(position).getAsJsonObject().get("children").getAsJsonArray();
                if(children1.size() == 0){
                    dicts3.add(new AllData("无","无"));
                    arrAdapterpay3 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts3);
                    //设置样式
                    arrAdapterpay3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner3.setAdapter(arrAdapterpay3);
                }else {
                    for(int i = 0;i<children1.size();i++){
                        JsonObject childrenobj = children1.get(i).getAsJsonObject();
                        dicts3.add(new AllData(childrenobj.get("warehouseId").getAsString(),childrenobj.get("warehouse").getAsString()));
                        arrAdapterpay3 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts3);
                        //设置样式
                        arrAdapterpay3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner3.setAdapter(arrAdapterpay3);
                    }
                }
            }else {
                dicts3.add(new AllData("无","无"));
                arrAdapterpay3 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts3);
                //设置样式
                arrAdapterpay3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner3.setAdapter(arrAdapterpay3);
                children1 = null;
            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    //位置3
    Spinner.OnItemSelectedListener listener3=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            dicts4.clear();
            if(children1 == null ||children1.size() == 0){
                dicts4.add(new AllData("无","无"));
                arrAdapterpay4 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts4);
                //设置样式
                arrAdapterpay4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner4.setAdapter(arrAdapterpay4);
            }else {
                children2=children1.get(position).getAsJsonObject().get("children").getAsJsonArray();
                for(int i = 0;i<children2.size();i++){
                    JsonObject childrenobj = children2.get(i).getAsJsonObject();
                    dicts4.add(new AllData(childrenobj.get("warehouseId").getAsString(),childrenobj.get("warehouse").getAsString()));
                    arrAdapterpay4 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts4);
                    //设置样式
                    arrAdapterpay4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner4.setAdapter(arrAdapterpay4);
                }
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //位置4
    Spinner.OnItemSelectedListener listener4=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            warehouseId=((AllData)spinner4.getSelectedItem()).getStr();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };



    private void getChooseDismantleTypeCarsDetails(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).getWarehouseCarsDetails(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"));
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
                                JsonArray img = data.get("img").getAsJsonArray();
                                disWarehouses = data.get("disWarehouses").getAsJsonArray();
                                Log.e("TAG", "onResponse: "+disWarehouses );
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

                                    if(plateNumberColour.equals("蓝色")){
                                        plateNumberNo.setBackgroundColor(getResources().getColor(R.color.blue));
                                        plateNumberNo.setTextColor(getResources().getColor(R.color.white));
                                    }
                                    if(plateNumberColour.equals("白色")){
                                        plateNumberNo.setBackgroundColor(getResources().getColor(R.color.selector_grey));
                                        plateNumberNo.setTextColor(getResources().getColor(R.color.black));
                                    }
                                    if(plateNumberColour.equals("绿色")){
                                        plateNumberNo.setBackgroundColor(getResources().getColor(R.color.colorBlue));
                                        plateNumberNo.setTextColor(getResources().getColor(R.color.white));
                                    }
                                    if(plateNumberColour.equals("黄色")){
                                        plateNumberNo.setBackgroundColor(getResources().getColor(R.color.bg));
                                        plateNumberNo.setTextColor(getResources().getColor(R.color.white));
                                    }
                                    if(plateNumberColour.equals("黑色")){
                                        plateNumberNo.setBackgroundColor(getResources().getColor(R.color.black));
                                        plateNumberNo.setTextColor(getResources().getColor(R.color.white));
                                    }
                                }else {
                                    plateNumberColour.setText("无");
                                }


                                for(int i = 0;i<img.size();i++){
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
                                for(int i = 0;i<disWarehouses.size();i++){
                                    JsonObject object = disWarehouses.get(i).getAsJsonObject();
                                    dicts1.add(new AllData(object.get("warehouseId").getAsString(),object.get("warehouse").getAsString()));
                                    arrAdapterpay1 = new ArrayAdapter<AllData>(getApplicationContext(), R.layout.simple_spinner_item,dicts1);
                                    //设置样式
                                    arrAdapterpay1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinner1.setAdapter(arrAdapterpay1);
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

        if(v ==ok_btn){
            showdilgo();
        }
    }

    private void showdilgo(){

        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(WarehouseCarsDetailsActivity.this);
        alterDiaglog.setTitle("提示");//文字
        alterDiaglog.setMessage("确定保存吗？");//提示消息
        alterDiaglog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("TAG", "onClick: "+warehouseId );
                    if(warehouseId==null||warehouseId.equals("无")){
                        Toast.makeText(getApplicationContext(),"请选择仓库最后存放位置",Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog = new ProgressDialog(WarehouseCarsDetailsActivity.this,
                                R.style.AppTheme_Dark_Dialog);
                             progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
                        progressDialog.setMessage("正在提交....");
                        progressDialog.show();
                        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).saveWarehouse(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),warehouseId);
                        Log.e("TAG", "warehouseId: "+warehouseId );
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
                    Log.e("TAG", "onActivityResult: "+result );
                    showWareHouseName(result);
                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(WarehouseCarsDetailsActivity.this, "解析二维码失败", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void showWareHouseName(String result){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).showWareHouseName(Utils.getShared2(getApplicationContext(),"token"),result);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonArray data = jsonObject.get("data").getAsJsonArray();
                            if(data != null || data.size()!=0){
                                int k= arrAdapterpay1.getCount();
                                for(int j=0;j<k;j++){
                                    if(data.get(0).getAsJsonObject().get("warehouseId").getAsString().equals(arrAdapterpay1.getItem(j).getStr())){
                                        spinner1.setSelection(j,true);// 默认选中项
                                        break;
                                    }

                                }

                                new Handler().postDelayed(new Runnable(){
                                    public void run(){
                                        //execute the task
                                        int b= arrAdapterpay2.getCount();
                                        for(int j=0;j<b;j++){
                                            if(data.get(1).getAsJsonObject().get("warehouseId").getAsString().equals(arrAdapterpay2.getItem(j).getStr())){
                                                spinner2.setSelection(j,true);// 默认选中项
                                                break;
                                            }
                                        }
                                    }
                                },300);

                                new Handler().postDelayed(new Runnable(){
                                    public void run(){
                                        //execute the task
                                        int b= arrAdapterpay3.getCount();
                                        for(int j=0;j<b;j++){
                                            if(data.size()>2){
                                                if(data.get(2).getAsJsonObject().get("warehouseId").getAsString().equals(arrAdapterpay3.getItem(j).getStr())){
                                                    spinner3.setSelection(j,true);// 默认选中项
                                                    break;
                                                }
                                            }

                                        }
                                    }
                                },400);


                                new Handler().postDelayed(new Runnable(){
                                    public void run(){
                                        //execute the task
                                        int b= arrAdapterpay4.getCount();
                                        for(int j=0;j<b;j++){
                                            if(data.size()>3){
                                                if(data.get(3).getAsJsonObject().get("warehouseId").getAsString().equals(arrAdapterpay4.getItem(j).getStr())){
                                                    spinner4.setSelection(j,true);// 默认选中项
                                                    break;
                                                }
                                            }

                                        }
                                    }
                                },450);





                          /*      int d= arrAdapterpay4.getCount();
                                for(int j=0;j<d;j++){
                                    if(result.equals(arrAdapterpay4.getItem(j).getStr())){
                                        spinner4.setSelection(j,true);// 默认选中项
                                        break;
                                    }
                                }*/


                            }

                        }else {
                            Toast.makeText(WarehouseCarsDetailsActivity.this,jsonObject.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(WarehouseCarsDetailsActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                Log.e("TAG", "onResponse: "+t.getMessage());
            }
        });
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
                Intent intent = new Intent(WarehouseCarsDetailsActivity.this, CaptureActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        }

    }

}
