package com.example.carassistant.Interface;

import java.util.Map;

public interface LoginInterface {
    //view
    interface View{
        void succeed();
        void failed();
    }
    //presenter
    interface Presenter{
        void login(Map<String,Object> map);
    }
}
