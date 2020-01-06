package com.example.carassistant.Interface;

public interface VehicleInspectionInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void searchInitialSurveyCars(String searchInfo);
    }
}
