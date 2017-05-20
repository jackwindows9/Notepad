package com.example.notepad;

import android.support.annotation.NonNull;

import org.litepal.crud.DataSupport;

/**
 * Created by 司维 on 2017/4/20.
 */

public class Note extends DataSupport implements Comparable<Note>{
    private int id;
    private String title;
    private String content;
    private String date;
    private boolean isChecked=false;

    public int getId() {
        return id;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
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


    @Override
    public int compareTo(@NonNull Note o) {
        int c=o.getDate().compareTo(this.getDate());
        return c;
    }
}
