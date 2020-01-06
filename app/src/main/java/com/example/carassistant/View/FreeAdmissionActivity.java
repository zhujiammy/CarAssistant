package com.example.carassistant.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.Model.AllData;
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
import org.devio.takephoto.model.TakePhotoOptions;
import org.devio.takephoto.permission.InvokeListener;
import org.devio.takephoto.permission.PermissionManager;
import org.devio.takephoto.permission.TakePhotoInvocationHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FreeAdmissionActivity extends AppCompatActivity implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener, View.OnTouchListener {

    @BindView(R.id.takephoto)
    ImageView takephoto;//拍摄车
    private TakePhoto takePhoto;
    public InvokeParam invokeParam;
    private String filePath;
    private String testStatestr;//车状态
    @BindView(R.id.carType)
    EditText carType;//车型
    @BindView(R.id.testMainEnginePlantsNum)
    EditText testMainEnginePlantsNum;//主机厂编号
    @BindView(R.id.testState)
    Spinner testState;//车状态
    private ProgressDialog progressDialog;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    List<AllData> dicts1= new ArrayList<AllData>();
    ArrayAdapter<AllData> arrAdapterpay1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.freeadmission);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        initEvent();
        TEST_CAR_STATE();

    }
    private void initEvent(){
        if(getIntent().getStringExtra("type").equals("2")){
            testMainEnginePlantsNum.setText(getIntent().getStringExtra("testMainEnginePlantsNum"));
            carType.setText(getIntent().getStringExtra("carType"));
        }
        takephoto.setOnClickListener(this);
        testState.setOnItemSelectedListener(listener1);

    }


    private void TEST_CAR_STATE(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).TEST_CAR_STATE(Utils.getShared2(getApplicationContext(),"token"));
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
                                arrAdapterpay1 = new ArrayAdapter<AllData >(getApplicationContext(),R.layout.simple_spinner_item,dicts1);
                                arrAdapterpay1.setDropDownViewResource(R.layout.simple_spinner_item);
                                testState.setAdapter(arrAdapterpay1);
                            }
                            if(getIntent().getStringExtra("type").equals("2")) {
                                for (int i = 0; i < arrAdapterpay1.getCount(); i++) {
                                    if (getIntent().getStringExtra("testState").equals(arrAdapterpay1.getItem(i).getText())) {
                                        testState.setSelection(i, true);
                                    }
                                }
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),jsonObject.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        if (id == R.id.sumbit) {

            if(filePath==null){
                Toast.makeText(getApplicationContext(),"车辆照片必须上传",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(carType.getText().toString())) {
                Toast.makeText(getApplicationContext(),"车型不能为空!",Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(testMainEnginePlantsNum.getText().toString())) {
                Toast.makeText(getApplicationContext(),"主机厂编号不能为空!",Toast.LENGTH_SHORT).show();
            } else if(TextUtils.isEmpty(testStatestr)){
                Toast.makeText(getApplicationContext(),"车状态必须选择",Toast.LENGTH_SHORT).show();
            }else {
                progressDialog = new ProgressDialog(FreeAdmissionActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                     progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
                progressDialog.setMessage("正在提交....");
                progressDialog.show();
                final File file = new File(filePath);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                if(getIntent().getStringExtra("type").equals("1")){
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).testcarentranceCar(Utils.getShared2(getApplicationContext(),"token"),testMainEnginePlantsNum.getText().toString(),testStatestr,carType.getText().toString(),part);
                    call.enqueue(new Callback<ResponseBody>(){
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                            if(response.body()!=null){
                                try {
                                    String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                                    JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                                    if(jsonObject.get("status").getAsInt() == 0){
                                        Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
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
                    Log.e("TAG", "onOptionsItemSelected: "+getIntent().getStringExtra("carDetailId") );
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).testcarentranceCar1(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),testMainEnginePlantsNum.getText().toString(),testStatestr,carType.getText().toString(),part);
                    call.enqueue(new Callback<ResponseBody>(){
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                            if(response.body()!=null){
                                try {
                                    String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                                    JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                                    if(jsonObject.get("status").getAsInt() == 0){
                                        Toast.makeText(getApplicationContext(),"提交成功",Toast.LENGTH_SHORT).show();
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v == takephoto){
            takephoto();
        }
    }

    private void takephoto(){
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
        View.OnClickListener listener = new View.OnClickListener(){
            public void onClick(View v){
                File file = new File(getExternalCacheDir(),System.currentTimeMillis()+".png");
                Uri uri = Uri.fromFile(file);
                CropOptions cropOptions  = new CropOptions.Builder().setAspectX(1).setAspectY(2).setWithOwnCrop(true).create();
                switch (v.getId()){
                    case R.id.camera:
                        //相机
                        takePhoto.onPickFromCapture(uri);
                        break;
                    case R.id.Album:
                        //相册获取照片并剪裁
                        takePhoto.onPickFromGallery();
                        break;
                    case R.id.cancel_btn:
                        //取消
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


    Spinner.OnItemSelectedListener listener1=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            testStatestr = ((AllData)testState.getSelectedItem()).getText();
            Log.e("TAG", "onItemSelected: "+ testStatestr);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent){
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
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

    /** * 获取TakePhoto实例 * @return */
    public TakePhoto getTakePhoto(){
        if (takePhoto==null){
            takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
        }
        //设置压缩规则，最大500kb
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        builder.setCorrectImage(true);
        takePhoto.setTakePhotoOptions(builder.create());
        CompressConfig compressConfig=new CompressConfig.Builder().setMaxSize(500*1024).create();
        takePhoto.onEnableCompress(compressConfig,true);
        return takePhoto;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
            this.invokeParam=invokeParam;
        }
        return type;
    }
}
