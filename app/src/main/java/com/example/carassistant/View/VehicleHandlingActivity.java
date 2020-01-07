package com.example.carassistant.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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


//车辆预处理
public class VehicleHandlingActivity extends AppCompatActivity implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener {

    @BindView(R.id.iv_1)
    ImageView iv1;
    @BindView(R.id.iv_2)
    ImageView iv2;
    @BindView(R.id.iv_3)
    ImageView iv3;
    @BindView(R.id.iv_4)
    ImageView iv4;
    @BindView(R.id.iv_5)
    ImageView iv5;
    @BindView(R.id.iv_6)
    ImageView iv6;


    @BindView(R.id.staging_btn)
    Button staging_btn;//暂存
    @BindView(R.id.ok_btn)
    Button ok_btn;//保存
    private int imagCode;

    private TakePhoto takePhoto;
    public InvokeParam invokeParam;

    private String filePath;
    private ProgressDialog progressDialog;
    private List<File> files = new ArrayList<>();
    private List<File> imgfiles = new ArrayList<>();
    private File file;
    private File imgfile;

    @BindView(R.id.t3)
    TextView t3;
    @BindView(R.id.t4)
    TextView t4;
    @BindView(R.id.t5)
    TextView t5;
    @BindView(R.id.t6)
    TextView t6;

    @BindView(R.id.carInfo)
    TextView carInfo;//车品牌
    @BindView(R.id.carCode)
    TextView carCode;//车编号
    @BindView(R.id.erterTime)
    TextView erterTime;//入场时间
    @BindView(R.id.plateNumberNo)
    TextView plateNumberNo;//车牌号
    private JsonArray data;
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
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehiclehandling);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        searchCarPretreatmentDetail();//获取待处理明细
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

    private void initEvent(){
        files.add(0,null);
        files.add(1,null);
        files.add(2,null);
        files.add(3,null);
        files.add(4,null);
        files.add(5,null);

        iv1.setOnClickListener(this);
        iv2.setOnClickListener(this);
        iv3.setOnClickListener(this);
        iv4.setOnClickListener(this);
        iv5.setOnClickListener(this);
        iv6.setOnClickListener(this);
        staging_btn.setOnClickListener(this);
        ok_btn.setOnClickListener(this);

        if(getIntent().getStringExtra("type").equals("2")){
            plateNumberNo_lin.setVisibility(View.GONE);
            line4.setVisibility(View.VISIBLE);
            testMainEnginePlantsNum.setText(getIntent().getStringExtra("testMainEnginePlantsNum"));
            testState.setText(getIntent().getStringExtra("testState"));
            testMainEnginePlants.setText(getIntent().getStringExtra("testMainEnginePlants"));
            t3.setText("侧面");
            t4.setText("车架");
            t5.setText("发动机号");
            t6.setText("其他");
        }
        if(!getIntent().getStringExtra("carType").equals("null")){
            carInfo.setText(getIntent().getStringExtra("carType"));
        }else {
            carInfo.setText("无");
        }

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
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.iv_1:
                imagCode = 0;
                opencamera();
                break;
            case R.id.iv_2:
                imagCode = 1;
                opencamera();
                break;
            case R.id.iv_3:
                imagCode =2;
                opencamera();
                break;
            case R.id.iv_4:
                imagCode = 3;
                opencamera();
                break;
            case R.id.iv_5:
                imagCode = 4;
                opencamera();
                break;
            case R.id.iv_6:
                imagCode = 5;
                opencamera();
                break;
            case R.id.ok_btn:
                saveCarPretreatmentDetail(1);
                break;
            case R.id.staging_btn:
                saveCarPretreatmentDetail(0);
                break;

        }

    }

    //获取待处理明细
    private void searchCarPretreatmentDetail(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).searchCarPretreatmentDetail(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"));
        Log.e("TAG", "searchCarPretreatmentDetail: "+ getIntent().getStringExtra("carDetailId"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            if(!jsonObject.get("data").isJsonNull()){
                                data = jsonObject.get("data").getAsJsonArray();
                                if(data.size() != 0){
                                    if(!data.get(0).getAsJsonObject().get("attachUrl").getAsString().equals("")){
                                        Glide.with(getApplicationContext()).load(data.get(0).getAsJsonObject().get("attachUrl").getAsString()).into(iv1);
                                        imgfile = new File(data.get(0).getAsJsonObject().get("attachUrl").getAsString());
                                        imgfiles.add(0,imgfile);
                                    }else {
                                        imgfiles.add(0,null);
                                    }
                                    if(!data.get(1).getAsJsonObject().get("attachUrl").getAsString().equals("")){
                                        Glide.with(getApplicationContext()).load(data.get(1).getAsJsonObject().get("attachUrl").getAsString()).into(iv2);
                                        imgfile = new File(data.get(1).getAsJsonObject().get("attachUrl").getAsString());
                                        imgfiles.add(1,imgfile);
                                    }else {
                                        imgfiles.add(1,null);
                                    }
                                    if(!data.get(2).getAsJsonObject().get("attachUrl").getAsString().equals("")){
                                        Glide.with(getApplicationContext()).load(data.get(2).getAsJsonObject().get("attachUrl").getAsString()).into(iv3);
                                        imgfile = new File(data.get(2).getAsJsonObject().get("attachUrl").getAsString());
                                        imgfiles.add(2,imgfile);
                                    }else {
                                        imgfiles.add(2,null);
                                    }
                                    if(!data.get(3).getAsJsonObject().get("attachUrl").getAsString().equals("")){
                                        Glide.with(getApplicationContext()).load(data.get(3).getAsJsonObject().get("attachUrl").getAsString()).into(iv4);
                                        imgfile = new File(data.get(3).getAsJsonObject().get("attachUrl").getAsString());
                                        imgfiles.add(3,imgfile);
                                    }else {
                                        imgfiles.add(3,null);
                                    }
                                    if(!data.get(4).getAsJsonObject().get("attachUrl").getAsString().equals("")){
                                        Glide.with(getApplicationContext()).load(data.get(4).getAsJsonObject().get("attachUrl").getAsString()).into(iv5);
                                        imgfile = new File(data.get(4).getAsJsonObject().get("attachUrl").getAsString());
                                        imgfiles.add(4,imgfile);
                                    }else {
                                        imgfiles.add(4,null);
                                    }
                                    if(!data.get(5).getAsJsonObject().get("attachUrl").getAsString().equals("")){
                                        Glide.with(getApplicationContext()).load(data.get(5).getAsJsonObject().get("attachUrl").getAsString()).into(iv6);
                                        imgfile = new File(data.get(5).getAsJsonObject().get("attachUrl").getAsString());
                                        imgfiles.add(5,imgfile);
                                    }else {
                                        imgfiles.add(5,null);
                                    }
                                }else {
                                    imgfiles.add(0,null);
                                    imgfiles.add(1,null);
                                    imgfiles.add(2,null);
                                    imgfiles.add(3,null);
                                    imgfiles.add(4,null);
                                    imgfiles.add(5,null);
                                }


                                Log.e("imgfiles", "onResponse: "+imgfiles.size() );

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

    //车辆预处理完成或暂存
    private  void saveCarPretreatmentDetail(int type){
        progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
             progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
        progressDialog.setMessage("正在提交....");
        progressDialog.show();
        if(type == 1){
            if(getIntent().getStringExtra("type").equals("1")){
                Call<ResponseBody> call = null;
                    for(int i = 0;i<imgfiles.size();i++){
                        if(imgfiles.get(i)==null){
                            Toast.makeText(getApplicationContext(),"图片未上传完整",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else if(i == 5){
                            call = HttpHelper.getInstance().create(CarAssistantAPI.class).saveCarPretreatmentDetail(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type,filesToMultipartBodyParts(files));
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




            }else {
                Call<ResponseBody> call=null;
                        if(imgfiles.size()<3){
                            Toast.makeText(getApplicationContext(),"图片未上传完整",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                          else {
                              call = HttpHelper.getInstance().create(CarAssistantAPI.class).testcarsaveCarPretreatmentDetail(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type,filesToMultipartBodyParts(files));
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
        }else {
            if(getIntent().getStringExtra("type").equals("1")){
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).saveCarPretreatmentDetail(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type,filesToMultipartBodyParts(files));
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
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).testcarsaveCarPretreatmentDetail(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type,filesToMultipartBodyParts(files));
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

    public static List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files) {
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for(int i = 1;i<files.size();i++){
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            MultipartBody.Part part;
            if(files.get(i)==null){
                part = MultipartBody.Part.createFormData("file"+i, "");
            }else {
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), files.get(i));
                part = MultipartBody.Part.createFormData("file"+i, files.get(i).getName(), requestBody);
                Log.e("TAG", "filesToMultipartBodyParts: "+files.get(i).getAbsolutePath());
            }

            parts.add(part);
        }

        return parts;
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

    @Override
    public void takeSuccess(TResult result) {
        filePath = result.getImage().getCompressPath();
        if(imagCode == 0){

            iv1.setRotation(90);
            Glide.with(this).load(filePath).into(iv1);
            file = new File(filePath);
            files.add(1,file);
            imgfiles.set(0,file);
        }
        if(imagCode == 1){
            iv2.setRotation(90);
            Glide.with(this).load(filePath).into(iv2);
            file = new File(filePath);
            files.add(2,file);
            imgfiles.set(1,file);
        }
        if(imagCode == 2){
            iv3.setRotation(90);
            Glide.with(this).load(filePath).into(iv3);
            file = new File(filePath);
            files.add(3,file);
            imgfiles.set(2,file);
        }
        if(imagCode == 3){
            iv4.setRotation(90);
            Glide.with(this).load(filePath).into(iv4);
            file = new File(filePath);
            files.add(4,file);
            imgfiles.set(3,file);
        }
        if(imagCode == 4){
            iv5.setRotation(90);
            Glide.with(this).load(filePath).into(iv5);
            file = new File(filePath);
            files.add(5,file);
            imgfiles.set(4,file);
        }
        if(imagCode == 5){
            iv6.setRotation(90);
            Glide.with(this).load(filePath).into(iv6);
            file = new File(filePath);
            files.add(6,file);
            imgfiles.set(5,file);
        }
        Log.e("TAG", "takeSuccess: "+imgfiles.size());

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

    private void opencamera(){
        final MyBottomSheetDialog dialog = new MyBottomSheetDialog(this);
        View box_view = LayoutInflater.from(this).inflate(R.layout.takephoto,null);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  //←重点在这里，来，都记下笔记
        dialog.setContentView(box_view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        TextView camera = (TextView) box_view.findViewById(R.id.camera);
        TextView Album = (TextView) box_view.findViewById(R.id.Album);
        Album.setVisibility(View.GONE);
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
}
