package com.example.carassistant.Interface;

public interface CarSourceAdmissionInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void getDisCarsList(String condition);
    }
}
