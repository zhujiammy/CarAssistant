package com.example.carassistant.Interface;

public interface VehicleSaleInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void superviseDestroysearchCars(String searchInfo);
    }
}
