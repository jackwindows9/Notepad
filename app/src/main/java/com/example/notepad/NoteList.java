package com.example.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NoteList extends AppCompatActivity {
    private final static String TAG="NoteList";
    private List<Note> datalist;//数据列表
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private MenuItem delete_checkbox;
    private MenuItem delete_undo;
    private MenuItem select_all;
    public static boolean isDeleteMode = false;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Connector.getDatabase();//create database and table
        datalist = new ArrayList<Note>();
        refresh();
        //Add data here in the List from database
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter(datalist);
        recyclerView.setAdapter(myAdapter);
        myAdapter.setmListener(new MyItemClickListener() {//这里只需创造接口类对象，并且实现其中的方法，这是一种简写
            //不省略的写法是需要创造一个类实现MyItemClickListener接口，然后父类对象指向子类实例(父类为接口)，然后这里放入父类对象
            @Override
            public void onItemClick(View view, int position) {
                Note note = datalist.get(position);
                if (!NoteList.isDeleteMode) {//当前页面未处于删除模式，才可以删除
                    refresh();
                    Intent intent = new Intent(NoteList.this, NoteEdit.class);
                    note = datalist.get(position);
                    intent.putExtra("mode", "update");//send editmode to edit interface
                    intent.putExtra("id", note.getId());//send note.id to edit interface
                    startActivityForResult(intent, 2);
                } else {
                    MyAdapter.ViewHolder viewHolder = new MyAdapter.ViewHolder(view);
                    viewHolder.checkBox.setChecked(!note.isChecked());
                    note.setIsChecked(!note.isChecked());
                }
                myAdapter.setList(datalist);
            }
        });
        myAdapter.setmLongClickListener(new MyItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                //update the item on toolbar
                changeDeleteMode(true);
                Note note=datalist.get(position);
                note.setIsChecked(true);
                myAdapter.setList(datalist);
                myAdapter.notifyDataSetChanged();
                isDeleteMode = true;
            }
        });
        myAdapter.setMyCheckboxChangedListener(new MyCheckboxChangedListener() {
            @Override
            public void onChanged() {//check wheather all checkbox is selected
                int checkedNum = 0;//the number of checkbox which is selected
                for (int i = 0; i < datalist.size(); i++) {
                    if (datalist.get(i).isChecked()) {
                        checkedNum++;
                    }
                }
                if (select_all != null) {
                    if (checkedNum == datalist.size() && "All".equals(select_all.getTitle())) {
                        //all checkbox is selected
                        select_all.setTitle("No All");
                    }
                    if (checkedNum < datalist.size() && "No All".equals(select_all.getTitle())) {
                        select_all.setTitle("All");
                    }
                }
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDeleteMode) {
                    Intent intent = new Intent(NoteList.this, NoteEdit.class);
                    intent.putExtra("mode", "new");//send editMode to edit interface
                    startActivityForResult(intent, 1);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerView.SCROLL_STATE_IDLE == newState) {
                    //暂停移动
                    fab.show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG,dy+"");
                if (dy > 1||dy<-1) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        delete_undo = menu.findItem(R.id.disappear_delete_mode);
        delete_checkbox = menu.findItem(R.id.delete_note);
        select_all = menu.findItem(R.id.select_all);
        delete_undo.setVisible(false);
        delete_checkbox.setVisible(false);
        select_all.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.about) {
            TextView content = (TextView) getLayoutInflater().inflate(R.layout.about_view, null);
            content.setMovementMethod(LinkMovementMethod.getInstance());
            content.setText(Html.fromHtml(getString(R.string.about)));
            new AlertDialog.Builder(this)
                    .setTitle("about")
                    .setView(content)
                    .setInverseBackgroundForced(true)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
            return true;
        }
        if (id == R.id.delete_note) {
            for (int i = 0; i < datalist.size(); i++) {
                if (datalist.get(i).isChecked()) {
                    DataSupport.delete(Note.class, datalist.get(i).getId());
                }
            }
            refresh();
            myAdapter.setList(datalist);
            recyclerView.setAdapter(myAdapter);
            changeDeleteMode(false);
            isDeleteMode = false;
            myAdapter.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.disappear_delete_mode) {
            changeDeleteMode(false);
            for (int i = 0; i < datalist.size(); i++) {
                datalist.get(i).setIsChecked(false);
            }
            Log.d(TAG,"disappear_delete_mode");
            isDeleteMode = false;
            myAdapter.setList(datalist);
            myAdapter.notifyDataSetChanged();
            return true;
        }
        if (id == R.id.select_all) {

            if ("No All".equals(select_all.getTitle())) {

                for (int i = 0; i < datalist.size(); i++) {
                    datalist.get(i).setIsChecked(false);
                    Log.d(TAG,"select_all");
                    View view = recyclerView.getChildAt(i);
                    if(view!=null) {
                        MyAdapter.ViewHolder viewHolder = new MyAdapter.ViewHolder(view);
                        viewHolder.checkBox.setChecked(false);
                    }
                }
            } else {
                for (int i = 0; i < datalist.size(); i++) {
                    Log.d(TAG,"select_all");
                    datalist.get(i).setIsChecked(true);
                    View view = recyclerView.getChildAt(i);
                    if(view!=null) {
                        MyAdapter.ViewHolder viewHolder = new MyAdapter.ViewHolder(view);
                        viewHolder.checkBox.setChecked(true);
                    }
                }
            }
            myAdapter.setList(datalist);
            myAdapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        datalist = DataSupport.findAll(Note.class);
        Collections.sort(datalist);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                refresh();
                break;
            case 2:
                refresh();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refresh();
        myAdapter.setList(datalist);
        recyclerView.setAdapter(myAdapter);
    }

    private void changeDeleteMode(boolean isDelete) {
        delete_undo.setVisible(isDelete);
        delete_checkbox.setVisible(isDelete);
        select_all.setVisible(isDelete);
        if (isDelete) {
            toolbar.setTitle("");
        } else {
            toolbar.setTitle("Notepad");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(delete_undo!=null){
            //如果在界面中未操作菜单，那么delete_undo就不会被初始化
            //所以在这里需要进行判断
            changeDeleteMode(false);
        }
        for (int i = 0; i < datalist.size(); i++) {
            datalist.get(i).setIsChecked(false);
        }
        Log.d(TAG,"onPause");
        isDeleteMode = false;
        refresh();
        myAdapter.notifyDataSetChanged();
    }
}
