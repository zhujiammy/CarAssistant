package com.example.carassistant.Interface;

public interface TestVehicleDestroyedInterface {
    interface View{
        void succeed();
        void failed();
        void onRefresh();
        void onLoadMore();
        void onNothingData();
    }
    //presenter
    interface Presenter{
        void testcarsearchDestroyCars(String searchInfo);
    }
}
