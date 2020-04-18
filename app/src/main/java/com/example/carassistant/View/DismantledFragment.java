package com.example.carassistant.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class DismantledFragment extends Fragment implements View.OnClickListener, TakePhoto.TakeResultListener, InvokeListener {

    public View view;
    @BindView(R.id.takephoto)
    ImageView takephoto;//拍照
    @BindView(R.id.remark)
    EditText remark;
    @BindView(R.id.partName)
    Spinner partName;//配件名称
    @BindView(R.id.texture)
    Spinner texture;//材质
    @BindView(R.id.color)
    Spinner color;//颜色
    @BindView(R.id.quality)
    Spinner quality;//成色
    @BindView(R.id.weight)
    EditText weight;//重量
    @BindView(R.id.status)
    Spinner status;//状态
    @BindView(R.id.disRemark)
    EditText disRemark;//拆解备注
    @BindView(R.id.btn)
    Button btn;//确认
    private String partNamestr;
    private String disListId;
    private TakePhoto takePhoto;
    public InvokeParam invokeParam;
    private String filePath = "";
    private String picUrl;
    private String disDetailsId;
    private String partRepertoryId;
    private ProgressDialog progressDialog;
    private String colorstr;
    private String qualitystr;
    private String texturestr;
    private String statusstr;

    List<AllData> dicts1= new ArrayList<AllData>();
    ArrayAdapter<AllData> arrAdapterpay1;

    List<AllData> dicts2= new ArrayList<AllData>();
    ArrayAdapter<AllData> arrAdapterpay2;

    List<AllData> dicts3= new ArrayList<AllData>();
    ArrayAdapter<AllData> arrAdapterpay3;

    List<AllData> dicts4= new ArrayList<AllData>();
    ArrayAdapter<AllData> arrAdapterpay4;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dismantledfragment,container,false);
        ButterKnife.bind(this,view);
        Bundle bundle = getArguments();
        assert bundle != null;
        disListId = bundle.getString("disListId");
        showDismantleDetail();

        takephoto.setOnClickListener(this);
        btn.setOnClickListener(this);
        color.setOnItemSelectedListener(listener);
        quality.setOnItemSelectedListener(listener1);
        texture.setOnItemSelectedListener(listener2);
        status.setOnItemSelectedListener(listener3);
        partName.setOnItemSelectedListener(listener4);
        return view;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try{
            if(getUserVisibleHint()){//界面可见时
             showDismantleDetail();
            }else {
                Log.e("TAG", "setUserVisibleHint: "+"不可见" );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //配件名称
    Spinner.OnItemSelectedListener listener4 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            partNamestr = ((AllData)partName.getSelectedItem()).getText();
            if(!partNamestr.equals("无配件")){
                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).showDismantleDetail(disListId,"2");
                call.enqueue(new Callback<ResponseBody>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.body()!=null){
                            try {
                                String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                                JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                                Log.e("TAG", "onResponse: "+jsonStr );

                                if(jsonObject.get("status").getAsInt() == 0){
                                    JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
                                    JsonObject jsonObject1 = jsonArray.get(position).getAsJsonObject();
                                    remark.setText(jsonObject1.get("remark").getAsString());
                                    disDetailsId = jsonObject1.get("disDetailsId").getAsString();
                                    partRepertoryId = jsonObject1.get("partRepertoryId").getAsString();
                                    if(!jsonObject1.get("picUrl").isJsonNull()){
                                        filePath = jsonObject1.get("picUrl").getAsString();
                                        Glide.with(getActivity()).load(filePath).into(takephoto);
                                    }else {
                                        filePath = "";
                                    }
                                    texturestr = jsonObject1.get("texture").getAsString();
                                    colorstr = jsonObject1.get("color").getAsString();
                                    qualitystr = jsonObject1.get("quality").getAsString();
                                    showQualityAndTexture();//材质成色
                                    showColor();//颜色
                                    if(jsonObject1.get("status").getAsString().equals("1")){
                                        status.setSelection(0);
                                    }
                                    if(jsonObject1.get("status").getAsString().equals("2")){
                                        status.setSelection(1);
                                    }
                                    if(jsonObject1.get("status").getAsString().equals("3")){
                                        status.setSelection(2);
                                    }
                                    if(jsonObject1.get("status").getAsString().equals("4")){
                                        status.setSelection(3);
                                    }

                                    if(!jsonObject1.get("weight").isJsonNull()){
                                        weight.setText(jsonObject1.get("weight").getAsString());
                                    }else {
                                        weight.setText("无");
                                    }
                                    if(!jsonObject1.get("disRemark").isJsonNull()){
                                        disRemark.setText(jsonObject1.get("disRemark").getAsString());
                                    }else {
                                        disRemark.setText("无");
                                    }




                                }else {
                                    Toast.makeText(getActivity(),jsonObject.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getActivity(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_LONG).show();
                        Log.e("TAG", "onResponse: "+"连接超时，请检查网络环境，避免影响使用！" );
                    }
                });
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //颜色
    Spinner.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            colorstr = ((AllData)color.getSelectedItem()).getText();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    //成色
    Spinner.OnItemSelectedListener listener1 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            qualitystr = ((AllData)quality.getSelectedItem()).getText();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    //材质
    Spinner.OnItemSelectedListener listener2 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            texturestr = ((AllData)texture.getSelectedItem()).getText();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //状态
    Spinner.OnItemSelectedListener listener3 = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(status.getSelectedItem().toString().equals("待确认")){
                statusstr = "1";
            }
            if(status.getSelectedItem().toString().equals("完好")){
                statusstr = "2";
            }
            if(status.getSelectedItem().toString().equals("毁坏")){
                statusstr = "3";
            }
            if(status.getSelectedItem().toString().equals("无此配件")){
                statusstr = "4";
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

    }

    private void showDismantleDetail(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).showDismantleDetail(disListId,"2");
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        Log.e("TAG", "onResponse: "+jsonStr );

                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonArray jsonArray = jsonObject.get("data").getAsJsonArray();
                            if(jsonArray.size()>0){
                                dicts4.clear();
                                for(int i = 0;i<jsonArray.size();i++){
                                    dicts4.add(new AllData("",jsonArray.get(i).getAsJsonObject().get("partName").getAsString()));
                                    arrAdapterpay4 = new ArrayAdapter<AllData>(getActivity(), R.layout.simple_spinner_item,dicts4);
                                    //设置样式
                                    arrAdapterpay4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    partName.setAdapter(arrAdapterpay4);
                                }
                            }else {
                                dicts4.add(new AllData("","无配件"));
                                arrAdapterpay4 = new ArrayAdapter<AllData>(getActivity(), R.layout.simple_spinner_item,dicts4);
                                //设置样式
                                arrAdapterpay4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                partName.setAdapter(arrAdapterpay4);
                            }

                        }else {
                            Toast.makeText(getActivity(),jsonObject.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_LONG).show();
                Log.e("TAG", "onResponse: "+"连接超时，请检查网络环境，避免影响使用！" );
            }
        });
    }
    private void showQualityAndTexture(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).showQualityAndTexture();
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        Log.e("TAG", "onResponse: "+jsonStr );
                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonObject object = jsonObject.get("data").getAsJsonObject();
                            JsonArray qualitys= object.get("quality").getAsJsonArray();
                            JsonArray textures= object.get("texture").getAsJsonArray();

                            dicts1.clear();
                            for(int i = 0;i<qualitys.size();i++){
                                dicts1.add(new AllData("",qualitys.get(i).getAsString()));
                                arrAdapterpay1 = new ArrayAdapter<AllData>(getActivity(), R.layout.simple_spinner_item,dicts1);
                                //设置样式
                                arrAdapterpay1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                quality.setAdapter(arrAdapterpay1);
                            }

                            dicts2.clear();
                            for(int i = 0;i<textures.size();i++){
                                dicts2.add(new AllData("",textures.get(i).getAsString()));
                                arrAdapterpay2 = new ArrayAdapter<AllData>(getActivity(), R.layout.simple_spinner_item,dicts2);
                                //设置样式
                                arrAdapterpay2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                texture.setAdapter(arrAdapterpay2);
                            }

                            for (int i = 0; i < arrAdapterpay1.getCount(); i++) {
                                if (qualitystr.equals(arrAdapterpay1.getItem(i).getText())) {
                                    quality.setSelection(i, true);
                                }
                            }

                            for (int i = 0; i < arrAdapterpay2.getCount(); i++) {
                                if (texturestr.equals(arrAdapterpay2.getItem(i).getText())) {
                                    texture.setSelection(i, true);
                                }
                            }


                        }else {
                            Toast.makeText(getActivity(),jsonObject.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_LONG).show();
                Log.e("TAG", "onResponse: "+"连接超时，请检查网络环境，避免影响使用！" );
            }
        });
    }
    private void showColor(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).showColor();
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        Log.e("TAG", "onResponse: "+jsonStr );
                        if(jsonObject.get("status").getAsInt() == 0){
                            JsonArray data= jsonObject.get("data").getAsJsonArray();

                            dicts3.clear();
                            for(int i = 0;i<data.size();i++){
                                dicts3.add(new AllData("",data.get(i).getAsString()));
                                arrAdapterpay3 = new ArrayAdapter<AllData>(getActivity(), R.layout.simple_spinner_item,dicts3);
                                //设置样式
                                arrAdapterpay3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                color.setAdapter(arrAdapterpay3);
                            }
                            for (int i = 0; i < arrAdapterpay3.getCount(); i++) {
                                if (colorstr.equals(arrAdapterpay3.getItem(i).getText())) {
                                    color.setSelection(i, true);
                                }
                            }


                        }else {
                            Toast.makeText(getActivity(),jsonObject.get("msg").getAsString(),Toast.LENGTH_LONG).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_LONG).show();
                Log.e("TAG", "onResponse: "+"连接超时，请检查网络环境，避免影响使用！" );
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == takephoto){
            final MyBottomSheetDialog dialog = new MyBottomSheetDialog(getActivity());
            View box_view = LayoutInflater.from(getActivity()).inflate(R.layout.takephoto,null);
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
                    File file = new File(getActivity().getExternalCacheDir(), System.currentTimeMillis() + ".png");
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
        if(v == btn){
            progressDialog = new ProgressDialog(getActivity(),
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("正在提交....");
            progressDialog.show();
            if(filePath.equals("")){
                Toast.makeText(getActivity(),"请上传配件图片",Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }else {
                final File file = new File(filePath);
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
                MultipartBody.Part part;
                if(file.length() == 0){
                    part = MultipartBody.Part.createFormData("","");
                }else {
                    part = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                }

                Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).dismantleComplete(Utils.getShared2(getActivity(),"token"),disDetailsId,partRepertoryId,partNamestr,
                        filePath,remark.getText().toString(),colorstr,qualitystr,texturestr,weight.getText().toString(),disRemark.getText().toString(),statusstr,part);
                call.enqueue(new Callback<ResponseBody>(){
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response){
                        if(response.body()!=null){
                            try {
                                String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                                JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                                if(jsonObject.get("status").getAsInt() == 0){
                                    Toast.makeText(getActivity(),"提交成功",Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }else {
                                    Toast.makeText(getActivity(),jsonObject.get("msg").getAsString(),Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getActivity(),"连接超时，请检查网络环境，避免影响使用！",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }

        }
    }

    @Override
    public void takeSuccess(TResult result) {
        filePath = result.getImage().getCompressPath();

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


}
