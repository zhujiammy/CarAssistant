package com.example.carassistant.Interface;

public interface VehicleHandInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void searchCarPretreatment(String searchInfo);
    }
}
