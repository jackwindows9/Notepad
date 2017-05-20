package com.example.notepad;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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
    private List<Note> datalist;//数据列表
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private MenuItem delete_checkbox;
    private MenuItem delete_undo;
    public static boolean checkboxIsVisible=false;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);

        SQLiteDatabase db=Connector.getDatabase();//创建数据库和表
        datalist=new ArrayList<Note>();

        //Add data here in the List from database
        refresh();//将当前的数据库中的所有项重新导入显示列表

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter=new MyAdapter(datalist);
        recyclerView.setAdapter(myAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(myAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);


        myAdapter.setmListener(new MyItemClickListener() {//这里只需创造接口类对象，并且实现其中的方法，这是一种简写
            //不省略的写法是需要创造一个类实现MyItemClickListener接口，然后父类对象指向子类实例(父类为接口)，然后这里放入父类对象
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(NoteList.this,NoteEdit.class);
                intent.putExtra("mode","update");
                Note note=datalist.get(position);
                intent.putExtra("id",note.getId());
                startActivityForResult(intent,2);
            }
        });
        myAdapter.setmLongClickListener(new MyItemLongClickListener() {
            @Override
            public void onItemLongClick(View view,int position) {
                delete_checkbox.setVisible(true);
                delete_undo.setVisible(true);
                toolbar.setTitle("");
                //更新当前的checkbox的选中
                MyAdapter.ViewHolder viewHolder=new MyAdapter.ViewHolder(view);
                viewHolder.checkBox.setChecked(true);
                //更新当前选中的条目的isChecked选项
                ContentValues contentValues=new ContentValues();
                contentValues.put("isChecked",true);
                int id=datalist.get(position).getId();
                DataSupport.update(Note.class,contentValues,id);

                datalist=DataSupport.findAll(Note.class);
                myAdapter.notifyDataSetChanged();
                checkboxIsVisible=true;
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkboxIsVisible) {
                    Intent intent = new Intent(NoteList.this, NoteEdit.class);
                    intent.putExtra("mode", "new");
                    startActivityForResult(intent, 1);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(recyclerView.SCROLL_STATE_IDLE==newState){
                    //暂停移动
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    fab.hide();
                }
                else{
                    fab.show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        delete_undo=menu.findItem(R.id.menuitem_delete_main);
        delete_undo.setVisible(false);

        delete_checkbox= menu.findItem(R.id.menuitem_delete_checkbox);
        delete_checkbox.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        if(id==R.id.menuitem_delete_checkbox){
            datalist=DataSupport.findAll(Note.class);
            for(int i=0;i<datalist.size();i++){
                Log.d("data",i+"");
                if(datalist.get(i).isChecked()){
                    Log.d("data",datalist.get(i).getId()+"");
                    DataSupport.delete(Note.class,datalist.get(i).getId());
                }
            }
            refresh();
            myAdapter.setList(datalist);
            recyclerView.setAdapter(myAdapter);
            delete_undo.setVisible(false);
            delete_checkbox.setVisible(false);
            toolbar.setTitle("Notepad");
            checkboxIsVisible=false;
            MyAdapter.isDeleteMode=false;
            myAdapter.notifyDataSetChanged();
            return true;
        }
        if(id==R.id.menuitem_delete_main){
            delete_undo.setVisible(false);
            delete_checkbox.setVisible(false);
            ContentValues contentValues=new ContentValues();
            contentValues.put("isChecked",false);
            datalist=DataSupport.findAll(Note.class);
            for(int i=0;i<datalist.size();i++) {
                int ids = datalist.get(i).getId();
                DataSupport.update(Note.class, contentValues, ids);
            }
            checkboxIsVisible=false;
            toolbar.setTitle("Notepad");
            MyAdapter.isDeleteMode=false;
            myAdapter.notifyDataSetChanged();
            datalist=DataSupport.findAll(Note.class);

            return true;

        }

        return super.onOptionsItemSelected(item);
    }
    private void refresh(){
        datalist= DataSupport.findAll(Note.class);
        Collections.sort(datalist);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                Log.d("NoteList","done");
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

//    @Override
//    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
//
//    }
}
