package com.example.carassistant.Interface;

public interface VehicleWeigInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void searchWeighCars(String searchInfo);
    }
}
