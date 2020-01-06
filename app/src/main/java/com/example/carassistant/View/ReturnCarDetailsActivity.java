package com.example.carassistant.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.gson.JsonArray;
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

//退车管理
public class ReturnCarDetailsActivity extends AppCompatActivity implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener {

    @BindView(R.id.carVin)
    TextView carVin;//车架号
    @BindView(R.id.engineNo)
    TextView engineNo;//发动机号
    @BindView(R.id.carDisplacement)
    TextView carDisplacement;//排量
    @BindView(R.id.carCode)
    TextView carCode;//车辆编号
    @BindView(R.id.warehouse)
    TextView warehouse;//仓库位置
    @BindView(R.id.plateNumberNo)
    TextView plateNumberNo;//车牌号
    @BindView(R.id.plateNumberColour)
    TextView plateNumberColour;//车牌颜色
    @BindView(R.id.takephoto)
    ImageView takephoto;//拍摄照片
    @BindView(R.id.staging_btn)
    Button staging_btn;//手续已退
    @BindView(R.id.ok_btn)
    Button ok_btn;//车辆已退
    @BindView(R.id.retreat_reason)
    EditText retreat_reason;//退车原因
    private TakePhoto takePhoto;
    public InvokeParam invokeParam;
    private String filePath;
    private ProgressDialog progressDialog;
    private int doCarProcedure;
    private int doCar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.returncardetails);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initEvent();
        searchRetreatCarDetail();
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
        takephoto.setOnClickListener(this);
        ok_btn.setOnClickListener(this);
        staging_btn.setOnClickListener(this);
    }

    private void searchRetreatCarDetail(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).searchRetreatCarDetail(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"));
        Log.e("TAG", "searchRetreatCarDetail: "+getIntent().getStringExtra("carDetailId") );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonObject data = jsonObject.get("data").getAsJsonObject();
                            if(!data.get("img").isJsonNull()){
                                Glide.with(getApplicationContext()).load(data.get("img").getAsString()).into(takephoto);
                            }
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
                            if(!data.get("carCode").isJsonNull()){
                                carCode.setText(data.get("carCode").getAsString());
                            }else {
                                carCode.setText("无");
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

                            if(!data.get("carDisplacement").isJsonNull()){
                                carDisplacement.setText(data.get("carDisplacement").getAsString());
                            }else {
                                carDisplacement.setText("无");
                            }
                            if(!data.get("warehouse").isJsonNull()){
                                warehouse.setText(data.get("warehouse").getAsString());
                            }else {
                                warehouse.setText("无");
                            }

                            if(!data.get("retreatReason").isJsonNull()){
                                retreat_reason.setText(data.get("retreatReason").getAsString());
                            }else {
                                retreat_reason.setText("");
                            }
                            if(data.get("doCarProcedure").getAsInt() == 0){
                                doCarProcedure = 0;
                                staging_btn.setEnabled(true);
                                staging_btn.setBackground(getResources().getDrawable(R.drawable.login_btn_backgroud_unfocus));
                                staging_btn.setText("领取车辆手续");
                            }else {
                                doCarProcedure = 1;
                                staging_btn.setEnabled(false);
                                staging_btn.setBackground(getResources().getDrawable(R.drawable.login_btn_backgroud));
                            }
                            if(data.get("doCar").getAsInt() == 0){
                                doCar = 0;
                                ok_btn.setEnabled(true);
                                ok_btn.setBackground(getResources().getDrawable(R.drawable.login_btn_backgroud_unfocus));
                                ok_btn.setText("领取车辆");
                            }else {
                                doCar = 1;
                                ok_btn.setEnabled(false);
                                ok_btn.setBackground(getResources().getDrawable(R.drawable.login_btn_backgroud));
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
            confirmDoCar();
        }
        if(v==staging_btn){
            confirmDoCarProcedure();
        }
    }
    private void shootBackCarPicture(){
        final File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).shootBackCarPicture(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),part);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
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
                progressDialog.dismiss();
            }
        });
    }

    private void confirmDoCar(){
        if(TextUtils.isEmpty(retreat_reason.getText().toString())){
            Toast.makeText(getApplicationContext(),"请输入退车原因",Toast.LENGTH_SHORT).show();
        } else {
            progressDialog = new ProgressDialog(ReturnCarDetailsActivity.this,
                    R.style.AppTheme_Dark_Dialog);
                 progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
            progressDialog.setMessage("正在提交....");
            progressDialog.show();
            Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).confirmDoCar(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"), retreat_reason.getText().toString());
            Log.e("TAG", "printNoCars: "+doCarProcedure );
            Log.e("TAG", "printNoCars: "+doCar );
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

    private void confirmDoCarProcedure(){
        if(TextUtils.isEmpty(retreat_reason.getText().toString())){
            Toast.makeText(getApplicationContext(),"请输入退车原因",Toast.LENGTH_SHORT).show();
        } else {
            progressDialog = new ProgressDialog(ReturnCarDetailsActivity.this,
                    R.style.AppTheme_Dark_Dialog);
                 progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
            progressDialog.setMessage("正在提交....");
            progressDialog.show();
            Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).confirmDoCarProcedure(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"), retreat_reason.getText().toString());
            Log.e("TAG", "printNoCars: "+doCarProcedure );
            Log.e("TAG", "printNoCars: "+doCar );
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

    @Override
    public void takeSuccess(TResult result) {
        filePath = result.getImage().getCompressPath();
        takephoto.setRotation(90);
        Glide.with(this).load(filePath).into(takephoto);
        shootBackCarPicture();
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
        getTakePhoto().onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode,data);
    }
}
