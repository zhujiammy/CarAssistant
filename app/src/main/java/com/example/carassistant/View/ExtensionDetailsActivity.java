package com.example.carassistant.View;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.R;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.MyBottomSheetDialog;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoImpl;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.InvokeParam;
import org.devio.takephoto.model.TContextWrap;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.permission.InvokeListener;
import org.devio.takephoto.permission.PermissionManager;
import org.devio.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//车辆拓号
public class ExtensionDetailsActivity extends AppCompatActivity implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener {

    @BindView(R.id.carInfo)
    TextView carInfo;//车辆型号
    @BindView(R.id.carCode)
    TextView carCode;//编号
    @BindView(R.id.erterTime)
    TextView erterTime;//入场时间
    @BindView(R.id.plateNumberNo)
    TextView plateNumberNo;//车牌号
    @BindView(R.id.takephoto)
    ImageView takephoto;//拍摄拓号纸
    @BindView(R.id.staging_btn)
    Button staging_btn;//暂存
    @BindView(R.id.ok_btn)
    Button ok_btn;//拓号完成
    private TakePhoto takePhoto;
    public InvokeParam invokeParam;
    private String filePath;
    private String imgpath;
    private ProgressDialog progressDialog;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.testMainEnginePlants_lin)
    LinearLayout testMainEnginePlants_lin;
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
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.extensiondetails);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        loadData();//获取车辆拓号信息，和车辆信息
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

    private void loadData(){
        if(getIntent().getStringExtra("type").equals("2")){
            testMainEnginePlants_lin.setVisibility(View.GONE);
            line4.setVisibility(View.VISIBLE);
            testMainEnginePlantsNum.setText(getIntent().getStringExtra("testMainEnginePlantsNum"));
            testState.setText(getIntent().getStringExtra("testState"));
            testMainEnginePlants.setText(getIntent().getStringExtra("testMainEnginePlants"));
        }
        if(!getIntent().getStringExtra("plateNumberNo").equals("null")){
            plateNumberNo.setText(getIntent().getStringExtra("plateNumberNo"));
        }else {
            plateNumberNo.setText("无");
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

        if(!getIntent().getStringExtra("carType").equals("null")){
            carInfo.setText(getIntent().getStringExtra("carType"));
        }else {
            carInfo.setText("无");
        }

        if(!getIntent().getStringExtra("erterTime").equals("null")){
            erterTime.setText(getIntent().getStringExtra("erterTime"));
        }else {
            erterTime.setText("无");
        }
        if(!getIntent().getStringExtra("carCode").equals("null")){
            carCode.setText(getIntent().getStringExtra("carCode"));
        }else {
            carCode.setText("无");
        }


        //获取拓号图片
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).searchNeedPrintNoCarsDetail(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                                if(!jsonObject.get("data").getAsString().equals("")){
                                    Glide.with(getApplicationContext()).load(jsonObject.get("data").getAsString()).into(takephoto);
                                    imgpath = jsonObject.get("data").getAsString();
                                }else {
                                    takephoto.setImageDrawable(getResources().getDrawable(R.drawable.plate));
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
    private void initEvent(){
        takephoto.setOnClickListener(this);
        ok_btn.setOnClickListener(this);
        staging_btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == takephoto){
            final MyBottomSheetDialog dialog = new MyBottomSheetDialog(this);
            View box_view = LayoutInflater.from(this).inflate(R.layout.takephoto,null);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  //←重点在这里，来，都记下笔记
            dialog.setContentView(box_view);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            TextView camera = (TextView) box_view.findViewById(R.id.camera);
            TextView Album = (TextView) box_view.findViewById(R.id.Album);
            TextView cancel_btn = (TextView) box_view.findViewById(R.id.cancel_btn);
            View.OnClickListener listener = new View.OnClickListener() {
                public void onClick(View v) {
                    File file = new File(getExternalCacheDir(), System.currentTimeMillis() + ".png");
                    Uri uri = Uri.fromFile(file);
                    int size = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels);
                    CropOptions cropOptions = new CropOptions.Builder().setOutputX(600).setOutputY(600).setWithOwnCrop(true).create();
                    switch (v.getId()) {
                        //相机
                        case R.id.camera:
                              takePhoto.onPickFromCapture(uri);
                            break;
                        //相册
                        case R.id.Album:
                            //相机获取照片并剪裁
                            takePhoto.onPickFromGallery();
                            break;
                        //取消
                        case R.id.cancel_btn:
                            dialog.dismiss();
                            break;

                    }
                    dialog.dismiss();
                }
            };

            cancel_btn.setOnClickListener(listener);
            camera.setOnClickListener(listener);
            Album.setOnClickListener(listener);
        }

        if(v==ok_btn){
            //拓号完成
            printNoCars(1);
        }
        if(v==staging_btn){
            //暂存拓号
            printNoCars(0);
        }
    }

    private void printNoCars(int type){
        if(imgpath !=null&& filePath == null){
            progressDialog = new ProgressDialog(ExtensionDetailsActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("正在提交....");
            progressDialog.show();
            MultipartBody.Part part = MultipartBody.Part.createFormData("","");
            if(getIntent().getStringExtra("type").equals("1")){
                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).printNoCars(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type,part);
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
            }else {
                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).testcarprintNoCars(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type,part);
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

        }else {
            if(filePath==null){
                Toast.makeText(getApplicationContext(),"车辆拓号纸必须上传",Toast.LENGTH_SHORT).show();
            }else {
                progressDialog = new ProgressDialog(ExtensionDetailsActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("正在提交....");
                progressDialog.show();
                final File file = new File(filePath);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                if(getIntent().getStringExtra("type").equals("1")){
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).printNoCars(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type,part);
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
                        public void onFailure(Call<ResponseBody> call, Throwable t){
                            Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }else {
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).testcarprintNoCars(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type,part);
                    call.enqueue(new Callback<ResponseBody>(){
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
        }

    }

    @Override
    public void takeSuccess(TResult result) {
        filePath = result.getImage().getCompressPath();
        takephoto.setRotation(90);
        Glide.with(this).load(filePath).into(takephoto);
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
            this.invokeParam=invokeParam;
        }
        return type;
    }

    /** * 获取TakePhoto实例 * @return */
    public TakePhoto getTakePhoto(){
        if (takePhoto==null){
            takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
        }
        //设置压缩规则，最大500kb
        takePhoto.onEnableCompress(new CompressConfig.Builder().setMaxSize(500 * 1024).create(), true);
        return takePhoto;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
