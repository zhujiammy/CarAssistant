package com.example.carassistant.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.carassistant.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.List;

public class FractureAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    private Context context;
    private JsonArray model;
    public FractureAdapter(Context context,JsonArray model){
        this.context = context;
        this.model = model;

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
        baseView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fracture_data, viewGroup, false);
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
            if(!object.get("remark").isJsonNull()){
                ((BodyViewHolder) viewHolder).remark.setText(object.get("remark").getAsString());
            }else {
                ((BodyViewHolder) viewHolder).remark.setText("无");
            }

            if(!object.get("createPerson").isJsonNull()){
                ((BodyViewHolder) viewHolder).createPerson.setText(object.get("createPerson").getAsString());
            }
            else {
                ((BodyViewHolder) viewHolder).createPerson.setText("无");
            }

            if(!object.get("createTime").isJsonNull()){
                ((BodyViewHolder) viewHolder).createTime.setText(object.get("createTime").getAsString());
            }
            else {
                ((BodyViewHolder) viewHolder).createTime.setText("无");
            }


            if(object.get("partName") != null){
                if(!object.get("partName").isJsonNull()){
                    ((BodyViewHolder) viewHolder).partName.setText(object.get("partName").getAsString());
                }
                else {
                    ((BodyViewHolder) viewHolder).partName.setText("无");
                }
            }else {
                ((BodyViewHolder) viewHolder).partName_lin.setVisibility(View.GONE);
            }
            if(object.get("partCode")!= null){
                if(!object.get("partCode").isJsonNull()){
                    ((BodyViewHolder) viewHolder).partCode.setText(object.get("partCode").getAsString());
                }
                else {
                    ((BodyViewHolder) viewHolder).partCode.setText("无");
                }
            }else {
                ((BodyViewHolder) viewHolder).partCode_lin.setVisibility(View.GONE);
            }

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
        private TextView remark;
        private TextView createPerson;
        private TextView createTime;
        private TextView partName;
        private TextView partCode;
        private LinearLayout partName_lin,partCode_lin;


        public BodyViewHolder(View itemView) {
            super(itemView);
            partName_lin = (LinearLayout) itemView.findViewById(R.id.partName_lin);
            partCode_lin = (LinearLayout) itemView.findViewById(R.id.partCode_lin);
            crushListCode = (TextView) itemView.findViewById(R.id.crushListCode);
            remark = (TextView) itemView.findViewById(R.id.remark);
            createPerson = (TextView) itemView.findViewById(R.id.createPerson);
            createTime = (TextView) itemView.findViewById(R.id.createTime);
            partName = (TextView) itemView.findViewById(R.id.partName);
            partCode = (TextView) itemView.findViewById(R.id.partCode);

        }

    }
}
