package com.example.carassistant.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.carassistant.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import java.util.ArrayList;
import java.util.List;

public class RetrieveVehicleAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    public static final int VIEW_TYPE_ITEM = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    private Context context;
    private JsonArray model;
    public RetrieveVehicleAdapter(Context context,JsonArray model){
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
        baseView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.retrievevehicle_data, viewGroup, false);
        BodyViewHolder bodyViewHolder = new BodyViewHolder(baseView);
        baseView.setOnClickListener(this);
        return bodyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof BodyViewHolder){
            JsonObject object =model.get(i).getAsJsonObject();
            if(!object.get("plateNumberNo").isJsonNull()){
                ((BodyViewHolder) viewHolder).plateNumberNo.setText(object.get("plateNumberNo").getAsString());
            }else {
                ((BodyViewHolder) viewHolder).plateNumberNo.setText("无");
            }
            if(!object.get("plateNumberColour").isJsonNull()){
                if(object.get("plateNumberColour").getAsString().equals("蓝色")){
                    ((BodyViewHolder) viewHolder).plateNumberNo.setBackgroundColor(context.getResources().getColor(R.color.blue));
                    ((BodyViewHolder) viewHolder).plateNumberNo.setTextColor(context.getResources().getColor(R.color.white));
                }
                if(object.get("plateNumberColour").getAsString().equals("白色")){
                    ((BodyViewHolder) viewHolder).plateNumberNo.setBackgroundColor(context.getResources().getColor(R.color.selector_grey));
                    ((BodyViewHolder) viewHolder).plateNumberNo.setTextColor(context.getResources().getColor(R.color.black));
                }
                if(object.get("plateNumberColour").getAsString().equals("绿色")){
                    ((BodyViewHolder) viewHolder).plateNumberNo.setBackgroundColor(context.getResources().getColor(R.color.colorBlue));
                    ((BodyViewHolder) viewHolder).plateNumberNo.setTextColor(context.getResources().getColor(R.color.white));
                }
                if(object.get("plateNumberColour").getAsString().equals("黄色")){
                    ((BodyViewHolder) viewHolder).plateNumberNo.setBackgroundColor(context.getResources().getColor(R.color.bg));
                    ((BodyViewHolder) viewHolder).plateNumberNo.setTextColor(context.getResources().getColor(R.color.white));
                }
                if(object.get("plateNumberColour").getAsString().equals("黑色")){
                    ((BodyViewHolder) viewHolder).plateNumberNo.setBackgroundColor(context.getResources().getColor(R.color.black));
                    ((BodyViewHolder) viewHolder).plateNumberNo.setTextColor(context.getResources().getColor(R.color.white));
                }
            }
            if(!object.get("plateNumberColour").isJsonNull()){
                ((BodyViewHolder) viewHolder).plateNumberColour.setText(object.get("plateNumberColour").getAsString());
            }else {
                ((BodyViewHolder) viewHolder).plateNumberColour.setText("无");
            }

            if(!object.get("carCode").isJsonNull()){
                ((BodyViewHolder) viewHolder).carCode.setText(object.get("carCode").getAsString());
            }else {
                ((BodyViewHolder) viewHolder).carCode.setText("无");
            }
            if(!object.get("carType").isJsonNull()){
                ((BodyViewHolder) viewHolder).carInfo.setText(object.get("carType").getAsString());
            }else {
                ((BodyViewHolder) viewHolder).carInfo.setText("无");
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

        private TextView plateNumberNo;
        private TextView plateNumberColour;
        private TextView carCode;
        private TextView carInfo;


        public BodyViewHolder(View itemView) {
            super(itemView);
            plateNumberNo = (TextView) itemView.findViewById(R.id.plateNumberNo);
            plateNumberColour = (TextView) itemView.findViewById(R.id.plateNumberColour);
            carCode = (TextView) itemView.findViewById(R.id.carCode);
            carInfo = (TextView) itemView.findViewById(R.id.carInfo);
        }

    }
}