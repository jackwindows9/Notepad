package com.example.notepad;

import org.litepal.crud.DataSupport;

/**
 * Created by 司维 on 2017/4/20.
 */

public class Note extends DataSupport {
    private int id;
    private String title;
    private String content;
    private String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate(){
        return this.date;
    }
    public void setDate(String date){
        this.date=date;
    }
}
