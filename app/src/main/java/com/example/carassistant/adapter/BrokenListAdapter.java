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
import com.example.carassistant.View.ProductionApprovalAty;
import com.example.carassistant.retrofit.HttpHelper;
import com.example.carassistant.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrokenListAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    private ProductionApprovalAty context;
    private List<String> checkBoxIDList;


    private JsonArray model;
    int type = 1;
    public BrokenListAdapter(ProductionApprovalAty context,JsonArray model,int type){
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
        baseView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.broken_data, viewGroup, false);
        BodyViewHolder bodyViewHolder = new BodyViewHolder(baseView);
        baseView.setOnClickListener(this);
        return bodyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof BodyViewHolder){
            JsonObject object =model.get(i).getAsJsonObject();
            if(!object.get("crushListCode").isJsonNull()){
                ((BodyViewHolder) viewHolder).crushListCode.setText(object.get("crushListCode").getAsString());
            }else {
                ((BodyViewHolder) viewHolder).crushListCode.setText("无");
            }
            if(!object.get("state").isJsonNull()){
                ((BodyViewHolder) viewHolder).state.setText(object.get("state").getAsString());
            }
            else {
                ((BodyViewHolder) viewHolder).state.setText("无");
            }
            if(!object.get("partName").isJsonNull()){
                ((BodyViewHolder) viewHolder).partName.setText(object.get("partName").getAsString());
            }
            else {
                ((BodyViewHolder) viewHolder).partName.setText("无");
            }


            if(!object.get("createTime").isJsonNull()){
                ((BodyViewHolder) viewHolder).createDate.setText(object.get("createTime").getAsString());
            }
            else {
                ((BodyViewHolder) viewHolder).createDate.setText("无");
            }

            if(!object.get("createPerson").isJsonNull()){
                ((BodyViewHolder) viewHolder).createPerson.setText(object.get("createPerson").getAsString());
            }
            else {
                ((BodyViewHolder) viewHolder).createPerson.setText("无");
            }

            ((BodyViewHolder) viewHolder).Agree_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.print("确定审批同意吗？","1",object.get("crushListId").getAsString());
                }
            });

            ((BodyViewHolder) viewHolder).refuse_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.print("确定拒绝审批吗？","2",object.get("crushListId").getAsString());
                }
            });


            //设置checkBox的值
            //获取复选框选中状态改变事件进行增删改
/*            ((BodyViewHolder) viewHolder).checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    *//*
             * isChecked=选中状态
             * if isChecked = true 将值添加至checkBoxIDList
             * if isChecked = false 将值从checkBoxIDList移除
             * *//*
                    if (isChecked) {
                        checkBoxIDList.add(object.get("disListId").getAsString());
                    } else {
                        checkBoxIDList.remove(object.get("disListId").getAsString());

                    }
                }
            });*/



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


        private TextView crushListCode;
        private TextView partName;
        private TextView createDate;
        private TextView createPerson;
        private TextView state;
        private CheckBox checkbox;
        private LinearLayout line_et;
        private Button Agree_btn,refuse_btn;



        public BodyViewHolder(View itemView) {
            super(itemView);
            crushListCode = (TextView) itemView.findViewById(R.id.crushListCode);
            state = (TextView) itemView.findViewById(R.id.state);
            partName = (TextView) itemView.findViewById(R.id.partName);
            createDate = (TextView) itemView.findViewById(R.id.createDate);
            createPerson = (TextView) itemView.findViewById(R.id.createPerson);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            line_et = (LinearLayout) itemView.findViewById(R.id.line_et);
            refuse_btn = (Button) itemView.findViewById(R.id.refuse_btn);
            Agree_btn = (Button) itemView.findViewById(R.id.Agree_btn);

        }

    }
}
