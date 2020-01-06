package com.example.carassistant.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;
import android.widget.ScrollView;

import androidx.core.widget.NestedScrollView;

import com.example.carassistant.Interface.ISmartScrollChangedListener;

public class MyScrollView extends ScrollView {

    private int left;
    private int top;
    private int right;
    private int bottom;

    private boolean isNoIntercept = false;

    private int adjustHeight;
    private int adjustWidth;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置修正值，位置修正
     * View的getTop并不能代表触摸的getX，按需修正
     * @param width
     * @param height
     */
    public void setAdjustDistance(int width, int height){
        adjustWidth = width;
        adjustHeight = height;
    }

    /**
     * 这个控件为了解决ScrollView和ListView的滑动冲突，所以传入ListView，如果有其他需要可以自行修改
     * @param lv
     */
    public void setListView(ListView lv){
        if (lv == null){
            top = 0;
            left = 0;
            right = 0;
            bottom = 0;
        }else {
            top = adjustHeight + lv.getTop();
            bottom = adjustHeight + lv.getBottom();
            left = adjustWidth + lv.getLeft();
            right = adjustWidth + lv.getRight();
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        //抬起结束触摸，事件重新交给ScrollView
        if (ev.getAction() == MotionEvent.ACTION_UP){
            isNoIntercept = false;
        }

        //为true则事件交给子View消费
        if (isNoIntercept){
            return false;
        }

        float x = ev.getX();
        float y = ev.getY();
        if (ev.getAction() == MotionEvent.ACTION_DOWN){
            if (x>left && x<right && y>top && y<bottom){
                //按下时选中ListView区域，则事件交给子View处理
                isNoIntercept = true;
                return false;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

}

