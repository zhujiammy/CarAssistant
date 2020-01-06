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

//车源过磅
public class VehicleWeighingActivity extends AppCompatActivity implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener {

    @BindView(R.id.takephoto)
    ImageView takephoto;//拍摄照片
    @BindView(R.id.staging_btn)
    Button staging_btn;//暂存
    @BindView(R.id.ok_btn)
    Button ok_btn;//完成
    @BindView(R.id.weight)
    EditText weight;//车重
    @BindView(R.id.weightOdd)
    EditText weightOdd;//过磅单号
    private TakePhoto takePhoto;
    public InvokeParam invokeParam;
    private String filePath;
    private String imgpath;
    private ProgressDialog progressDialog;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicleweighing);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);


        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        initEvent();
        initData();//获取暂存数据
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
    private void initData(){
        //获取拓号图片
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).searchWeighCarsDetail(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            if(!jsonObject.get("data").getAsJsonObject().get("attachUrl").getAsString().equals("")){
                                Glide.with(getApplicationContext()).load(jsonObject.get("data").getAsJsonObject().get("attachUrl").getAsString()).into(takephoto);
                                imgpath = jsonObject.get("data").getAsJsonObject().get("attachUrl").getAsString();
                            }else {
                                takephoto.setImageDrawable(getResources().getDrawable(R.drawable.plate));
                            }
                            Log.e("TAG", "onResponse: "+jsonObject.get("data").getAsJsonObject().get("weight").getAsString() );
                            if(!jsonObject.get("data").getAsJsonObject().get("weight").getAsString().equals("")){
                                weight.setText(jsonObject.get("data").getAsJsonObject().get("weight").getAsString());
                            }else {
                                weight.setText("");
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
            //完成
            printNoCars(1);
        }
        if(v==staging_btn){
            //暂存
            printNoCars(0);
        }

    }

    private void printNoCars(int type){
        if(imgpath != null){
            progressDialog = new ProgressDialog(VehicleWeighingActivity.this,
                    R.style.AppTheme_Dark_Dialog);
                 progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
            progressDialog.setMessage("正在提交....");
            progressDialog.show();
            MultipartBody.Part part = MultipartBody.Part.createFormData("","");
            if(getIntent().getStringExtra("type").equals("1")){
                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).weighCar(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type,Float.parseFloat(weight.getText().toString()),weightOdd.getText().toString(),part);
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
                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).testcarweighCar(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type, Float.parseFloat(weight.getText().toString()),weightOdd.getText().toString(),part);
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
        if(filePath==null){
            Toast.makeText(getApplicationContext(),"磅单那照片必须上传",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(weightOdd.getText().toString())){
            Toast.makeText(getApplicationContext(),"请输入过磅单号",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(weight.getText().toString())){
            Toast.makeText(getApplicationContext(),"请输入车辆质量",Toast.LENGTH_SHORT).show();
        } else {
            progressDialog = new ProgressDialog(VehicleWeighingActivity.this,
                    R.style.AppTheme_Dark_Dialog);
                 progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
            progressDialog.setMessage("正在提交....");
            progressDialog.show();
            final File file = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            if(getIntent().getStringExtra("type").equals("1")){
                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).weighCar(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type, Float.parseFloat(weight.getText().toString()),weightOdd.getText().toString(),part);
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
                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).testcarweighCar(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type, Float.parseFloat(weight.getText().toString()),weightOdd.getText().toString(),part);
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
        takePhoto.onEnableCompress(new CompressConfig.Builder().setMaxSize(100 * 1024).create(), true);
        return takePhoto;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
