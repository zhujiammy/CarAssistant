package com.example.carassistant.View;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.R;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import q.rorbin.badgeview.QBadgeView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//我的任务
public class MyTask extends Fragment {

    public View view;
    @BindView(R.id.gridview)
    GridView gridView;
    GridViewSim gridViewSim;
    JsonArray jsonElements;
    JsonArray jsonElements1;
    //icon封装数组
    private int [] icon = new int[11];
    private String[] iconName = new String[11];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.mytask,container,false);
        ButterKnife.bind(this,view);
        showAppMenus();
        flowCount();
        initUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showAppMenus();
        flowCount();
    }

    private void initUI(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;

                    if(iconName[position].equals("车辆初检")){
                        intent = new Intent(getActivity(),VehicleInspectionActivity.class);
                        startActivity(intent);
                    }
                    if(iconName[position].equals("车辆预处理 ")){
                        intent = new Intent(getActivity(),VehicleHandActivity.class);
                        startActivity(intent);
                    }
                    if(iconName[position].equals("车辆拓号")){
                        intent = new Intent(getActivity(),VehiclesNumberActivity.class);
                        startActivity(intent);
                    }
                    if(iconName[position].equals("拆解方式")){
                        intent = new Intent(getActivity(),VehicleDismantlingActivity.class);
                        startActivity(intent);
                    }
                    if(iconName[position].equals("车辆出库")){
                        intent = new Intent(getActivity(),ForOutboundActivity.class);
                        startActivity(intent);
                    }
                    if(iconName[position].equals("车辆毁型")){
                        intent = new Intent(getActivity(),VehicleDestroyedActivity.class);
                        startActivity(intent);
                    }
                    if(iconName[position].equals("车辆过磅")){
                        intent = new Intent(getActivity(),VehicleWeigActivity.class);
                        startActivity(intent);
                    }
                if(iconName[position].equals("生产审批")){
                    intent = new Intent(getActivity(),ProductionApprovalAty.class);
                    startActivity(intent);
                }

                if(iconName[position].equals("待拆解车")){
                    intent = new Intent(getActivity(),VehicleDismantledActivity.class);
                    intent.putExtra("disType","0");
                    startActivity(intent);
                }
                if(iconName[position].equals("待拆解配件")){
                    intent = new Intent(getActivity(),VehicleDismantledActivity.class);
                    intent.putExtra("disType","1");
                    startActivity(intent);
                }
                if(iconName[position].equals("待拆解破碎")){
                    intent = new Intent(getActivity(),FractureActivity.class);
                    startActivity(intent);
                }


                }


        });
    }

    private void showAppMenus(){
        Log.e("TAG", "token: "+ Utils.getShared2(getActivity(),"token"));
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).showAppMenus(Utils.getShared2(getActivity(),"token"));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){
                            jsonElements1 = jsonObject.getAsJsonArray("data");
                            for(int i = 0;i<jsonElements1.size();i++){
                                JsonObject name = jsonElements1.get(i).getAsJsonObject();
                                if(name.get("name").getAsString().equals("我的任务")){
                                    JsonArray childrenname = name.getAsJsonArray("children").getAsJsonArray();
                                    for(int j = 0;j<childrenname.size();j++){
                                        iconName[j] = childrenname.get(j).getAsJsonObject().get("name").getAsString();
                                        if(childrenname.get(j).getAsJsonObject().get("name").getAsString().equals("车辆初检")){
                                            icon[j] = R.mipmap.menu_clgl_cj2x;
                                        }
                                        if(childrenname.get(j).getAsJsonObject().get("name").getAsString().equals("车辆预处理 ")){
                                            icon[j] = R.mipmap.menu_clgl_ycl2x;
                                        }
                                        if(childrenname.get(j).getAsJsonObject().get("name").getAsString().equals("车辆拓号")){
                                            icon[j] = R.mipmap.menu_clgl_th2x;
                                        }
                                        if(childrenname.get(j).getAsJsonObject().get("name").getAsString().equals("拆解方式")){
                                            icon[j] = R.mipmap.menu_clgl_cjfs2x;
                                        }
                                        if(childrenname.get(j).getAsJsonObject().get("name").getAsString().equals("车辆出库")){
                                            icon[j] = R.mipmap.menu_cjgl_ck2x;
                                        }
                                        if(childrenname.get(j).getAsJsonObject().get("name").getAsString().equals("车辆毁型")){
                                            icon[j] = R.mipmap.menu_cjgl_cj2x;
                                        }
                                        if(childrenname.get(j).getAsJsonObject().get("name").getAsString().equals("车辆过磅")){
                                            icon[j] = R.mipmap.weighing;
                                        }
                                        if(childrenname.get(j).getAsJsonObject().get("name").getAsString().equals("生产审批")){
                                            icon[j] = R.mipmap.shenhe;
                                        }
                                        if(childrenname.get(j).getAsJsonObject().get("name").getAsString().equals("待拆解车")){
                                            icon[j] = R.mipmap.chaijieche;
                                        }
                                        if(childrenname.get(j).getAsJsonObject().get("name").getAsString().equals("待拆解配件")){
                                            icon[j] = R.mipmap.chaijiepeijan;
                                        }
                                        if(childrenname.get(j).getAsJsonObject().get("name").getAsString().equals("待拆解破碎")){
                                            icon[j] = R.mipmap.chaijieposui;
                                        }
                                    }
                                }
                            }
                            Log.e("TAG", "onResponse: "+iconName.length );

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
            }
        });
    }
    private void flowCount(){
        Call<ResponseBody> call = HttpHelper.getInstance().create(CarAssistantAPI.class).flowCount();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    try {
                        String jsonStr = new String(response.body().bytes());//把原始数据转为字符串
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse(jsonStr);
                        if(jsonObject.get("status").getAsInt() == 0){

                            jsonElements = jsonObject.getAsJsonArray("data");
                            //获取数据
                            gridViewSim = new GridViewSim(getActivity(),iconName,icon);
                            //配置适配器
                            gridView.setAdapter(gridViewSim);

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
            }
        });
    }


    class GridViewSim extends BaseAdapter {
        private Context context=null;
        private String data[]=null;
        private int imgId[]=null;


        private class Holder{

            ImageView item_img;
            TextView item_tex;

            public ImageView getItem_img() {
                return item_img;
            }

            public void setItem_img(ImageView item_img) {
                this.item_img = item_img;
            }

            public TextView getItem_tex() {
                return item_tex;
            }

            public void setItem_tex(TextView item_tex) {
                this.item_tex = item_tex;
            }




        }
        //构造方法
        public GridViewSim(Context context, String[] data, int[] imgId) {
            this.context = context;
            this.data = data;
            this.imgId = imgId;
        }


        @Override
        public int getCount() {


            return data.length;

        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            Holder holder;
            if(view==null){
                view=View.inflate(getActivity(),R.layout.item,null);
                holder=new Holder();
                holder.item_img=(ImageView)view.findViewById(R.id.image);
                holder.item_tex=(TextView)view.findViewById(R.id.text);
                view.setTag(holder);
            }else{
                holder=(Holder) view.getTag();
            }
            holder.item_tex.setText(data[position]);
            holder.item_img.setImageResource(imgId[position]);
            for(int i = 0;i<jsonElements.size();i++){
                if(jsonElements.get(i).getAsJsonObject().get("flowName").getAsString().equals(holder.item_tex.getText().toString())){
                    new QBadgeView(getActivity()).bindTarget(holder.item_img).setBadgeNumber(jsonElements.get(i).getAsJsonObject().get("count").getAsInt());
                }
            }


            return view;
        }
    }

}
