package com.nlp.restservice.pojo.request;

public class Request
{
    private String text;

    public void setText(String text){
        this.text = text;
    }
    public String getText(){
        return this.text;
    }
}


