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

public class VehicleDismantledAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    private Context context;
    private JsonArray model;
    private String disType;
    public VehicleDismantledAdapter(Context context,JsonArray model,String disType){
        this.context = context;
        this.model = model;
        this.disType = disType;

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
        baseView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vehicledismantled_data, viewGroup, false);
        BodyViewHolder bodyViewHolder = new BodyViewHolder(baseView);
        baseView.setOnClickListener(this);
        return bodyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof BodyViewHolder){
            JsonObject object =model.get(i).getAsJsonObject();



            if(!object.get("listCode").isJsonNull()){
                ((BodyViewHolder) viewHolder).listCode.setText(object.get("listCode").getAsString());
            }else {
                ((BodyViewHolder) viewHolder).listCode.setText("无");
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

            if(!object.get("createDate").isJsonNull()){
                ((BodyViewHolder) viewHolder).createDate.setText(object.get("createDate").getAsString());
            }
            else {
                ((BodyViewHolder) viewHolder).createDate.setText("无");
            }

            if(disType.equals("0")){
                if(!object.get("carSource").isJsonNull()){
                    ((BodyViewHolder) viewHolder).carSource.setText(object.get("carSource").getAsString());
                }
                else {
                    ((BodyViewHolder) viewHolder).carSource.setText("无");
                }
            }else {
                ((BodyViewHolder) viewHolder).carSource_lin.setVisibility(View.GONE);
            }

            if(disType.equals("0")){
                if(!object.get("carType").isJsonNull()){
                    ((BodyViewHolder) viewHolder).carType.setText(object.get("carType").getAsString());
                }
                else {
                    ((BodyViewHolder) viewHolder).carType.setText("无");
                }
            }else {
                ((BodyViewHolder) viewHolder).carType_lin.setVisibility(View.GONE);
            }


            if(disType.equals("0")){
                if(!object.get("carCode").isJsonNull()){
                    ((BodyViewHolder) viewHolder).carCode.setText(object.get("carCode").getAsString());
                }
                else {
                    ((BodyViewHolder) viewHolder).carCode.setText("无");
                }
            }else {
                ((BodyViewHolder) viewHolder).carcode_lin.setVisibility(View.GONE);
            }

            if(disType.equals("0")){
                if(!object.get("isDestroy").isJsonNull()){
                    ((BodyViewHolder) viewHolder).isDestroy.setText(object.get("isDestroy").getAsString());
                }
                else {
                    ((BodyViewHolder) viewHolder).isDestroy.setText("无");
                }
            }else {
                ((BodyViewHolder) viewHolder).isDestroy_lin.setVisibility(View.GONE);
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

        private TextView listCode;
        private TextView remark;
        private TextView createPerson;
        private TextView createDate;
        private TextView partName;
        private TextView partCode;
        private TextView carSource;
        private TextView carType;
        private TextView carCode;
        private TextView isDestroy;
        private LinearLayout partName_lin,partCode_lin,carcode_lin,carSource_lin,carType_lin,isDestroy_lin;


        public BodyViewHolder(View itemView) {
            super(itemView);
            partName_lin = (LinearLayout) itemView.findViewById(R.id.partName_lin);
            partCode_lin = (LinearLayout) itemView.findViewById(R.id.partCode_lin);
            carcode_lin = (LinearLayout) itemView.findViewById(R.id.carcode_lin);
            carSource_lin = (LinearLayout) itemView.findViewById(R.id.carSource_lin);
            isDestroy_lin = (LinearLayout) itemView.findViewById(R.id.isDestroy_lin);
            carType_lin = (LinearLayout) itemView.findViewById(R.id.carType_lin);
            listCode = (TextView) itemView.findViewById(R.id.listCode);
            remark = (TextView) itemView.findViewById(R.id.remark);
            createPerson = (TextView) itemView.findViewById(R.id.createPerson);
            createDate = (TextView) itemView.findViewById(R.id.createDate);
            partName = (TextView) itemView.findViewById(R.id.partName);
            partCode = (TextView) itemView.findViewById(R.id.partCode);
            carSource = (TextView) itemView.findViewById(R.id.carSource);
            carType = (TextView) itemView.findViewById(R.id.carType);
            carCode = (TextView) itemView.findViewById(R.id.carCode);
            isDestroy = (TextView) itemView.findViewById(R.id.isDestroy);

        }

    }
}
