package com.example.carassistant.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.carassistant.API.CarAssistantAPI;
import com.example.carassistant.R;
import com.example.carassistant.View.ProductionApprovalActivity;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.Utils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class productionApproveAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    private ProductionApprovalActivity context;
    private List<String> checkBoxIDList;

    private JsonArray model;
    int type = 1;
    public productionApproveAdapter(ProductionApprovalActivity context,JsonArray model,int type){
        this.context = context;
        this.model = model;
        this.type= type;
        checkBoxIDList= new ArrayList<>();
    }

    //get set
    public List<String> getCheckBoxIDList() {
        return checkBoxIDList;
    }

    public void setCheckBoxIDList(List<String> checkBoxIDList) {
        this.checkBoxIDList = checkBoxIDList;
    }


    private OnitemClickListener onitemClickListener=null;


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //在onCreateViewHolder方法中，我们要根据不同的ViewType来返回不同的ViewHolder
        if (viewType == VIEW_TYPE_EMPTY) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_view_tab, viewGroup, false);
            return new RecyclerView.ViewHolder(view) {
            };
        }

        View baseView;
        baseView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.right_data, viewGroup, false);
        BodyViewHolder bodyViewHolder = new BodyViewHolder(baseView);
        baseView.setOnClickListener(this);
        return bodyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof BodyViewHolder){
            JsonObject object =model.get(i).getAsJsonObject();
            if(!object.get("carType").isJsonNull()){
                ((BodyViewHolder) viewHolder).carInfo.setText(object.get("carType").getAsString());
            }else {
                ((BodyViewHolder) viewHolder).carInfo.setText("无");
            }
            if(!object.get("enterTime").isJsonNull()){
                ((BodyViewHolder) viewHolder).erterTime.setText(object.get("enterTime").getAsString());
            }
            else {
                ((BodyViewHolder) viewHolder).erterTime.setText("无");
            }
            if(!object.get("carCode").isJsonNull()){
                ((BodyViewHolder) viewHolder).carCode.setText(object.get("carCode").getAsString());
            }
            else {
                ((BodyViewHolder) viewHolder).carCode.setText("无");
            }


            if(!object.get("testMainEnginePlantsNum").isJsonNull()){
                ((BodyViewHolder) viewHolder).testMainEnginePlantsNum.setText(object.get("testMainEnginePlantsNum").getAsString());
            }
            else {
                ((BodyViewHolder) viewHolder).testMainEnginePlantsNum.setText("无");
            }

            if(!object.get("testMainEnginePlants").isJsonNull()){
                ((BodyViewHolder) viewHolder).testMainEnginePlants.setText(object.get("testMainEnginePlants").getAsString());
            }
            else {
                ((BodyViewHolder) viewHolder).testMainEnginePlants.setText("无");
            }
            ((BodyViewHolder) viewHolder).carDetailId.setText(object.get("carDetailId").getAsString());


            //设置checkBox的值
            //获取复选框选中状态改变事件进行增删改
            ((BodyViewHolder) viewHolder).checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    /*
                     * isChecked=选中状态
                     * if isChecked = true 将值添加至checkBoxIDList
                     * if isChecked = false 将值从checkBoxIDList移除
                     * */
                    if (isChecked) {
                        checkBoxIDList.add(object.get("carDetailId").getAsString());
                    } else {
                        checkBoxIDList.remove(object.get("carDetailId").getAsString());

                    }
                }
            });



        }
        viewHolder.itemView.setTag(i);
    }




    @Override
    public int getItemCount() {
        if (model.size() == 0) {
            return 1;
        }
        return model.size();
    }

    /**
     *
     * 复用getItemViewType方法，根据位置返回相应的ViewType
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        //如果是0，就是头，否则则是其他的item

        if (model.size() == 0) {
            return VIEW_TYPE_EMPTY;
        }
        //如果有数据，则使用ITEM的布局
        return VIEW_TYPE_ITEM;
    }

    @Override
    public void onClick(View view) {
        if(onitemClickListener!=null){
            onitemClickListener.onItemClick(view,(int)view.getTag());
        }


    }

    public void setOnitemClickListener(OnitemClickListener onitemClickListener) {
        this.onitemClickListener = onitemClickListener;
    }

    public static interface OnitemClickListener{
        void onItemClick(View view, int position);
    }

    /**
     * 给GridView中的条目用的ViewHolder，里面只有一个TextView
     */
    public class BodyViewHolder extends RecyclerView.ViewHolder {


        private TextView carInfo;
        private TextView carCode;
        private TextView erterTime;
        private TextView testMainEnginePlantsNum;
        private TextView testMainEnginePlants,carDetailId;
        private CheckBox checkbox;
        private LinearLayout line_et;



        public BodyViewHolder(View itemView) {
            super(itemView);
            carInfo = (TextView) itemView.findViewById(R.id.carInfo);
            carCode = (TextView) itemView.findViewById(R.id.carCode);
            erterTime = (TextView) itemView.findViewById(R.id.erterTime);
            testMainEnginePlantsNum = (TextView) itemView.findViewById(R.id.testMainEnginePlantsNum);
            testMainEnginePlants = (TextView) itemView.findViewById(R.id.testMainEnginePlants);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            carDetailId = (TextView) itemView.findViewById(R.id.carDetailId);
            line_et = (LinearLayout) itemView.findViewById(R.id.line_et);

        }

    }
}
