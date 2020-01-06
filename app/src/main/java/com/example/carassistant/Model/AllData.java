package com.example.carassistant.Model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class AllData implements Serializable {
    private String str;
    private String text;

    public AllData() {
    }

    public AllData(String str, String text) {
        super();
        this.str = str;
        this.text = text;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * 为什么要重写toString()呢？
     *
     * 因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
     */
    @Override
    public String toString() {
        return text;

    }


}