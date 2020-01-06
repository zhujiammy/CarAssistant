package com.example.carassistant.Interface;

public interface RetrieveVehicleInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void searchAwaitEntranceCars(String searchInfo);
    }
}
