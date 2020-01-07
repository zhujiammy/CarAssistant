package com.example.carassistant.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
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
import android.widget.RelativeLayout;
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
import com.example.carassistant.utils.CarKeyboardUtil;
import com.example.carassistant.utils.ImageUtils;
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

public class VehicleEntryActivity extends AppCompatActivity implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener, View.OnTouchListener{

    @BindView(R.id.takephoto)
    ImageView takephoto;//拍摄车
   // @BindView(R.id.Placenames)
    //Spinner Placenames;//车牌号地名选择器
    private TakePhoto takePhoto;
    public InvokeParam invokeParam;
    private String filePath;
    private String plateNumberColour;//车牌颜色
    @BindView(R.id.plateNumberColour_spinner)
    Spinner plateNumberColour_spinner;//车牌颜色
    private ProgressDialog progressDialog;
    @BindView(R.id.etPlateNumber)
    EditText etPlateNumber;
    private CarKeyboardUtil keyboardUtil;
    @BindView(R.id.imgIcon)
    ImageView imgIcon;//新能源icon 车牌号8位显示  默认隐藏
    @BindView(R.id.rlPlateNumber)
    RelativeLayout rlPlateNumber;
    List<AllData> dicts1= new ArrayList<AllData>();
    ArrayAdapter<AllData> arrAdapterpay1;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    Bitmap bitmap;
    @BindView(R.id.plate)
    TextView plate;//自动识别车牌号
    private int tp = 0;
    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 1000L;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.vehicleentry);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        initEvent();
        CAR_PLATE_NUMBER_COLOR();//车牌颜色
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
            long nowTime = System.currentTimeMillis();
            if (nowTime - mLastClickTime > TIME_INTERVAL) {
                // do something
                if(filePath==null){
                    Toast.makeText(getApplicationContext(),"车牌号照片必须上传",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(etPlateNumber.getText().toString())) {
                    Toast.makeText(getApplicationContext(),"车牌号不能为空!",Toast.LENGTH_SHORT).show();
                }else if(etPlateNumber.length()<7) {
                    Toast.makeText(getApplicationContext(),"请填写完整的车牌号!",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(plateNumberColour)){
                    Toast.makeText(getApplicationContext(),"车牌颜色必须选择",Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog = new ProgressDialog(VehicleEntryActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                         progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
                    progressDialog.setMessage("正在提交....");
                    progressDialog.show();
                    final File file = new File(filePath);
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).entranceCar(Utils.getShared2(getApplicationContext(),"token"),etPlateNumber.getText().toString(),plateNumberColour,part);
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
                            Toast.makeText(getApplicationContext(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
                mLastClickTime = nowTime;
            } else {
                Toast.makeText(getApplicationContext(), "不要重复点击", Toast.LENGTH_SHORT).show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initEvent(){
        if(getIntent().getStringExtra("carNature").equals("2")){
            plate.setVisibility(View.VISIBLE);
        }else if (getIntent().getStringExtra("carNature").equals("1")){
            plate.setVisibility(View.GONE);
        }
        plate.setOnClickListener(this);
        takephoto.setOnClickListener(this);
        plateNumberColour_spinner.setOnItemSelectedListener(listener1);
        keyboardUtil = new CarKeyboardUtil(VehicleEntryActivity.this, etPlateNumber);
        etPlateNumber.setOnTouchListener(this);
        etPlateNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                if (s.length() == 8){
                    rlPlateNumber.setBackgroundResource(R.drawable.btn_round_blue);
                    imgIcon.setVisibility(View.VISIBLE);
                }else{
                    rlPlateNumber.setBackgroundResource(R.drawable.btn_round_green);
                    imgIcon.setVisibility(View.GONE);
                }
                if (text.contains("港") || text.contains("澳") || text.contains("学") ){
                    etPlateNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
                }else{
                    etPlateNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    Spinner.OnItemSelectedListener listener1=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            plateNumberColour = ((AllData)plateNumberColour_spinner.getSelectedItem()).getText();
            Log.e("TAG", "onItemSelected: "+ plateNumberColour);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent){
        }
    };
    private void CAR_PLATE_NUMBER_COLOR(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).CAR_PLATE_NUMBER_COLOR(Utils.getShared2(getApplicationContext(),"token"));
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
                                plateNumberColour_spinner.setAdapter(arrAdapterpay1);
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
                Toast.makeText(getApplicationContext(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        if(v == takephoto){
            tp = 1;
            takephoto();
        }
        if(v == plate){
            tp = 2;
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
    @Override
    public void takeSuccess(TResult result) {
        if(tp == 1){
            filePath = result.getImage().getCompressPath();
            takephoto.setRotation(90);
            Glide.with(this).load(filePath).into(takephoto);
        }
        if(tp == 2){
            progressDialog = new ProgressDialog(VehicleEntryActivity.this,
                    R.style.AppTheme_Dark_Dialog);
                 progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
            progressDialog.setMessage("正在识别请稍等....");
            progressDialog.show();
            final File file = new File(result.getImage().getCompressPath());
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).licensePlate(Utils.getShared2(getApplicationContext(),"token"),part);
            call.enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                    if(response.body()!=null){
                        try {
                            String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                            JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                            if(jsonObject.get("status").getAsInt() == 0){
                                Toast.makeText(getApplicationContext(),"识别成功",Toast.LENGTH_SHORT).show();
                                etPlateNumber.setText(jsonObject.get("data").getAsString());
                                progressDialog.dismiss();
                            }else {
                                Toast.makeText(getApplicationContext(),"识别失败，手动输入或再试一次",Toast.LENGTH_SHORT).show();
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.etPlateNumber:
                keyboardUtil.hideSystemKeyBroad();
                keyboardUtil.hideSoftInputMethod();
                if (!keyboardUtil.isShow())
                    keyboardUtil.showKeyboard();
                break;
            default:
                if (keyboardUtil.isShow())
                    keyboardUtil.hideKeyboard();
                break;
        }

        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (keyboardUtil.isShow()) {
            keyboardUtil.hideKeyboard();
        }
        return super.onTouchEvent(event);
    }
}
