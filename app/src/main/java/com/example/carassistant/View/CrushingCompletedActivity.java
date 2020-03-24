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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.APP.CarApp;
import com.example.carassistant.R;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.GlideImageLoader;
import com.example.carassistant.utils.ImagePickerLoader;
import com.example.carassistant.utils.Utils;
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

public class CrushingCompletedActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ninegridview)
    NineGridView mNineGridView;
    @BindView(R.id.ninegridview1)
    NineGridView mNineGridView1;
    @BindView(R.id.ninegridview2)
    NineGridView mNineGridView2;
    private final int REQUEST_CODE_PICKER1 = 100;
    private final int REQUEST_CODE_PICKER2 = 101;
    private final int REQUEST_CODE_PICKER3= 102;
    private List<File>  files1 = new ArrayList<>();
    private List<File>  files2 = new ArrayList<>();
    private List<File>  files3 = new ArrayList<>();
    @BindView(R.id.btn)
    Button btn;//破碎完成

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crushingcompletedactivity);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        ButterKnife.bind(this);
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
        Log.e("TAG", "onCreate: "+getIntent().getStringExtra("crushListId") );
        initUI();

    }

    private void initUI(){
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
        mNineGridView.setMaxNum(3);
        //设置图片显示间隔大小，默认3dp
        mNineGridView.setSpcaeSize(4);
        //设置删除图片
        //        mNineGridView.setIcDeleteResId(R.drawable.ic_block_black_24dp);
        //设置删除图片与父视图的大小比例，默认0.25f
        mNineGridView.setRatioOfDeleteIcon(0.3f);
        //设置“+”号的图片
        mNineGridView.setIcAddMoreResId(R.drawable.ic_ngv_add_pic);
        //设置各类点击监听
        mNineGridView.setOnItemClickListener(listener);

        //设置图片加载器，这个是必须的，不然图片无法显示
        mNineGridView2.setImageLoader(new GlideImageLoader());
        //设置显示列数，默认3列
        mNineGridView2.setColumnCount(3);
        //设置是否为编辑模式，默认为false
        mNineGridView2.setIsEditMode(true);
        //设置单张图片显示时的尺寸，默认100dp
        mNineGridView2.setSingleImageSize(150);
        //设置单张图片显示时的宽高比，默认1.0f
        mNineGridView2.setSingleImageRatio(0.8f);
        //设置最大显示数量，默认9张
        mNineGridView2.setMaxNum(3);
        //设置图片显示间隔大小，默认3dp
        mNineGridView2.setSpcaeSize(4);
        //设置删除图片
        //        mNineGridView.setIcDeleteResId(R.drawable.ic_block_black_24dp);
        //设置删除图片与父视图的大小比例，默认0.25f
        mNineGridView2.setRatioOfDeleteIcon(0.3f);
        //设置“+”号的图片
        mNineGridView2.setIcAddMoreResId(R.drawable.ic_ngv_add_pic);
        //设置各类点击监听
        mNineGridView2.setOnItemClickListener(listener2);

        //设置图片加载器，这个是必须的，不然图片无法显示
        mNineGridView1.setImageLoader(new GlideImageLoader());
        //设置显示列数，默认3列
        mNineGridView1.setColumnCount(3);
        //设置是否为编辑模式，默认为false
        mNineGridView1.setIsEditMode(true);
        //设置单张图片显示时的尺寸，默认100dp
        mNineGridView1.setSingleImageSize(150);
        //设置单张图片显示时的宽高比，默认1.0f
        mNineGridView1.setSingleImageRatio(0.8f);
        //设置最大显示数量，默认9张
        mNineGridView1.setMaxNum(3);
        //设置图片显示间隔大小，默认3dp
        mNineGridView1.setSpcaeSize(4);
        //设置删除图片
        //        mNineGridView.setIcDeleteResId(R.drawable.ic_block_black_24dp);
        //设置删除图片与父视图的大小比例，默认0.25f
        mNineGridView1.setRatioOfDeleteIcon(0.3f);
        //设置“+”号的图片
        mNineGridView1.setIcAddMoreResId(R.drawable.ic_ngv_add_pic);
        //设置各类点击监听
        mNineGridView1.setOnItemClickListener(listener1);
        btn.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v == btn){
            //破碎完成
            final AlertDialog.Builder alterDiaglog = new AlertDialog.Builder(CrushingCompletedActivity.this);
            alterDiaglog.setTitle("提示");//文字
            alterDiaglog.setMessage("确定破碎完成吗？");//提示消息
            alterDiaglog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog = new ProgressDialog(CrushingCompletedActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("正在提交....");
                    progressDialog.show();

                    if(files1.size() != 0 && files2.size()!= 0 && files3.size()!= 0){
                        MultipartBody.Part[] parts = new MultipartBody.Part[files1.size()];
                        MultipartBody.Part[] parts1 = new MultipartBody.Part[files2.size()];
                        MultipartBody.Part[] parts2 = new MultipartBody.Part[files3.size()];
                        int index = 0;
                        int index1 = 0;
                        int index2 = 0;
                        for (File file : files1) {
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                            MultipartBody.Part filePart = MultipartBody.Part.createFormData("beforeDestroyPics", file.getName(), requestBody);
                            parts[index] = filePart;
                            index++;
                        }

                        for (File file : files2) {
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                            MultipartBody.Part filePart = MultipartBody.Part.createFormData("destroyingPics", file.getName(), requestBody);
                            parts1[index1] = filePart;
                            index1++;
                        }

                        for (File file : files3) {
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                            MultipartBody.Part filePart = MultipartBody.Part.createFormData("afterDestroyPics", file.getName(), requestBody);
                            parts2[index2] = filePart;
                            index2++;
                        }
                        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).doCrush(Utils.getShared2(getApplicationContext(),"token"),getIntent().getStringExtra("crushListId"),parts,parts1,parts2);
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
                        Toast.makeText(getApplicationContext(),"毁型前、毁型中、毁型后3个类型每个至少传一张图片",Toast.LENGTH_SHORT).show();
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
    }

    NineGridView.onItemClickListener listener = new NineGridView.onItemClickListener() {
        @Override
        public void onNineGirdAddMoreClick(int cha) {
            //编辑模式下，图片展示数量尚未达到最大数量时，会显示一个“+”号，点击后回调这里
            new ImagePicker()
                    .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .pickType(ImagePickType.MULTI)
                    .displayer(new ImagePickerLoader())
                    .maxNum(cha)
                    .start(CrushingCompletedActivity.this,REQUEST_CODE_PICKER1);
        }

        @Override
        public void onNineGirdItemClick(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {

        }

        @Override
        public void onNineGirdItemDeleted(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {

        }
    };

    NineGridView.onItemClickListener listener1 = new NineGridView.onItemClickListener() {
        @Override
        public void onNineGirdAddMoreClick(int cha) {
            //编辑模式下，图片展示数量尚未达到最大数量时，会显示一个“+”号，点击后回调这里
            new ImagePicker()
                    .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .pickType(ImagePickType.MULTI)
                    .displayer(new ImagePickerLoader())
                    .maxNum(cha)
                    .start(CrushingCompletedActivity.this,REQUEST_CODE_PICKER2);
        }

        @Override
        public void onNineGirdItemClick(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {

        }

        @Override
        public void onNineGirdItemDeleted(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {

        }
    };


    NineGridView.onItemClickListener listener2 = new NineGridView.onItemClickListener() {
        @Override
        public void onNineGirdAddMoreClick(int cha) {
            //编辑模式下，图片展示数量尚未达到最大数量时，会显示一个“+”号，点击后回调这里
            new ImagePicker()
                    .cachePath(Environment.getExternalStorageDirectory().getAbsolutePath())
                    .pickType(ImagePickType.MULTI)
                    .displayer(new ImagePickerLoader())
                    .maxNum(cha)
                    .start(CrushingCompletedActivity.this,REQUEST_CODE_PICKER3);
        }

        @Override
        public void onNineGirdItemClick(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {

        }

        @Override
        public void onNineGirdItemDeleted(int position, NineGridBean gridBean, NineGirdImageContainer imageContainer) {

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER1 && resultCode == RESULT_OK && data != null)
        {
            List<ImageBean> list = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            List<NineGridBean> resultList = new ArrayList<>();

            for (ImageBean imageBean : list)
            {
                NineGridBean nineGirdData = new NineGridBean(imageBean.getImagePath());
                File file = new File(imageBean.getImagePath());
                File newFile = CompressHelper.getDefault(this).compressToFile(file);
                files1.add(newFile);
                resultList.add(nineGirdData);

            }
            mNineGridView.addDataList(resultList);
        }

        if (requestCode == REQUEST_CODE_PICKER2 && resultCode == RESULT_OK && data != null)
        {
            List<ImageBean> list = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            List<NineGridBean> resultList = new ArrayList<>();

            for (ImageBean imageBean : list)
            {
                NineGridBean nineGirdData = new NineGridBean(imageBean.getImagePath());
                File file = new File(imageBean.getImagePath());
                File newFile = CompressHelper.getDefault(this).compressToFile(file);
                files2.add(newFile);
                resultList.add(nineGirdData);

            }
            mNineGridView1.addDataList(resultList);
        }

        if (requestCode == REQUEST_CODE_PICKER3 && resultCode == RESULT_OK && data != null)
        {
            List<ImageBean> list = data.getParcelableArrayListExtra(ImagePicker.INTENT_RESULT_DATA);
            List<NineGridBean> resultList = new ArrayList<>();

            for (ImageBean imageBean : list)
            {
                NineGridBean nineGirdData = new NineGridBean(imageBean.getImagePath());
                File file = new File(imageBean.getImagePath());
                File newFile = CompressHelper.getDefault(this).compressToFile(file);
                files3.add(newFile);
                resultList.add(nineGirdData);

            }
            mNineGridView2.addDataList(resultList);
        }
    }




}
