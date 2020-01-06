package com.example.carassistant.View;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import com.example.carassistant.utils.GlideImageLoader;
import com.example.carassistant.utils.ImagePickerLoader;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lwkandroid.imagepicker.ImagePicker;
import com.lwkandroid.imagepicker.data.ImageBean;
import com.lwkandroid.imagepicker.data.ImagePickType;
import com.lwkandroid.widget.ninegridview.NineGirdImageContainer;
import com.lwkandroid.widget.ninegridview.NineGridBean;
import com.lwkandroid.widget.ninegridview.NineGridView;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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

//车辆毁型明细
public class VehicleDestroyeDetailActivity extends AppCompatActivity implements View.OnClickListener,NineGridView.onItemClickListener {

    @BindView(R.id.iv_1)
    ImageView iv_1;
    @BindView(R.id.iv_2)
    ImageView iv_2;
    @BindView(R.id.iv_3)
    ImageView iv_3;
    @BindView(R.id.staging_btn)
    Button staging_btn;//暂存
    @BindView(R.id.ok_btn)
    Button ok_btn;//毁型完成
    @BindView(R.id.plateNumberNo)
    TextView plateNumberNo;//车牌号
    @BindView(R.id.plateNumberColour)
    TextView plateNumberColour;//车牌颜色
    @BindView(R.id.carCode)
    TextView carCode;//车辆编号
    @BindView(R.id.enterTime)
    TextView enterTime;//入场时间
    private ProgressDialog progressDialog;

    @BindView(R.id.ninegridview)
    NineGridView mNineGridView;
    private final int REQUEST_CODE_PICKER = 100;
    private List<File>  files = new ArrayList<>();
    private List<String>picAttachIds;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicledestroyedetail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        initEvent();
        searchDestroyCarsDetails();


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
        ok_btn.setOnClickListener(this);
        staging_btn.setOnClickListener(this);
        //设置图片加载器，这个是必须的，不然图片无法显示
        mNineGridView.setImageLoader(new GlideImageLoader());
        //设置显示列数，默认3列
        mNineGridView.setColumnCount(3);
        //设置是否为编辑模式，默认为false
        mNineGridView.setIsEditMode(true);
        //设置单张图片显示时的尺寸，默认100dp
        mNineGridView.setSingleImageSize(150);
        //设置单张图片显示时的宽高比，默认1.0f
        mNineGridView.setSingleImageRatio(0.8f);
        //设置最大显示数量，默认9张
        mNineGridView.setMaxNum(6);
        //设置图片显示间隔大小，默认3dp
        mNineGridView.setSpcaeSize(4);
        //设置删除图片
        //        mNineGridView.setIcDeleteResId(R.drawable.ic_block_black_24dp);
        //设置删除图片与父视图的大小比例，默认0.25f
        mNineGridView.setRatioOfDeleteIcon(0.3f);
        //设置“+”号的图片
        mNineGridView.setIcAddMoreResId(R.drawable.ic_ngv_add_pic);
        //设置各类点击监听
        mNineGridView.setOnItemClickListener(this);
    }

    private void searchDestroyCarsDetails(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).searchDestroyCarsDetails(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                                JsonObject data = jsonObject.get("data").getAsJsonObject();
                                JsonArray img = data.get("img").getAsJsonArray();
                                JsonArray destroyImg = data.get("destroyImg").getAsJsonArray();
                                if(img.size()!=0){
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
                                if(destroyImg.size() != 0){
                                    List<NineGridBean> resultList = new ArrayList<>();
                                    picAttachIds = new ArrayList<>();
                                    for(int i = 0;i<destroyImg.size();i++){
                                        NineGridBean nineGirdData = new NineGridBean(destroyImg.get(i).getAsJsonObject().get("attachUrl").getAsString());
                                        picAttachIds.add(destroyImg.get(i).getAsJsonObject().get("picAttachId").getAsString());
                                        resultList.add(nineGirdData);
                                    }
                                    mNineGridView.addDataList(resultList);
                                }
                            plateNumberColour.setText(getIntent().getStringExtra("plateNumberColour"));
                            carCode.setText(getIntent().getStringExtra("carCode"));
                            enterTime.setText(getIntent().getStringExtra("erterTime"));
                            plateNumberNo.setText(getIntent().getStringExtra("plateNumberNo"));
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

        if(v == ok_btn){
            //毁型完成
            showdilgo(1,"毁型完成");
        }
        if(v == staging_btn){
            //暂存
            showdilgo(0,"暂存");
        }
    }

    private void showdilgo(int type,String title){
        final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(VehicleDestroyeDetailActivity.this);
        alterDiaglog.setTitle("提示");//文字
        alterDiaglog.setMessage("确定"+title+"吗？");//提示消息
        alterDiaglog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog = new ProgressDialog(VehicleDestroyeDetailActivity.this,
                        R.style.AppTheme_Dark_Dialog);
                     progressDialog.setIndeterminate(true);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setCancelable(false);
                progressDialog.setMessage("正在提交....");
                progressDialog.show();

                if(files.size() != 0){
                    MultipartBody.Part[] parts = new MultipartBody.Part[files.size()];
                    int index = 0;
                    for (File file : files) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("files", file.getName(), requestBody);
                        parts[index] = filePart;
                        index++;
                        Log.e("TAG", "onClick: "+files.get(0).length() );
                    }
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).saveDestroyCars(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type,picAttachIds,parts);
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
                    MultipartBody.Part[] parts = new MultipartBody.Part[1];
                    MultipartBody.Part filePart = MultipartBody.Part.createFormData("","");
                    parts[0] = filePart;
                    Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).saveDestroyCars(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("carDetailId"),type,picAttachIds,parts);
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
    public void onNineGirdAddMoreClick(int cha)
    {
        //编辑模式下，图片展示数量尚未达到最大数量时，会显示一个“+”号，点击后回调这里
        new ImagePicker()
                .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                .pickType(ImagePickType.MULTI)
                .displayer(new ImagePickerLoader())
                .maxNum(cha)
                .start(this, REQUEST_CODE_PICKER);
    }

    @Override
    public void onNineGirdItemClick(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer)
    {
        //点击图片的监听

    }

    @Override
    public void onNineGirdItemDeleted(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer)
    {
        //编辑模式下，某张图片被删除后回调这里
        picAttachIds.remove(position);
        Log.e("TAG", "onNineGirdItemClick: "+picAttachIds.size() );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && resultCode == RESULT_OK && data != null)
        {
            List<ImageBean> list = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            List<NineGridBean> resultList = new ArrayList<>();

            for (ImageBean imageBean : list)
            {
                NineGridBean nineGirdData = new NineGridBean(imageBean.getImagePath());
                File file = new File(imageBean.getImagePath());
                File newFile = CompressHelper.getDefault(this).compressToFile(file);
                files.add(newFile);
                resultList.add(nineGirdData);

            }
            mNineGridView.addDataList(resultList);
        }
    }
}
