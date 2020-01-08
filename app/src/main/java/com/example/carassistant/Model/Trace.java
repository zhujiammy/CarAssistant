package com.example.carassistant.Model;

public class Trace {

    private String acceptTime;

    private String acceptStation;

    private String processor;

    public Trace() {
    }

    public Trace(String acceptTime, String acceptStation,String processor) {
        this.acceptTime = acceptTime;
        this.acceptStation = acceptStation;
        this.processor = processor;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getAcceptStation() {
        return acceptStation;
    }

    public void setAcceptStation(String acceptStation) {
        this.acceptStation = acceptStation;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }
}
