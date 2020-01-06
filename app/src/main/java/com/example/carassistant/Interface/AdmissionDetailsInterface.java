package com.example.carassistant.Interface;

public interface AdmissionDetailsInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void getDisCarsDetails(String carsId,String condition);
    }
}
