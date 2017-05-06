package com.example.notepad;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

public class NoteList extends AppCompatActivity implements OnStartDragListener{
    private List<Note> datalist;//数据列表
    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private ItemTouchHelper mItemTouchHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);

        SQLiteDatabase db=Connector.getDatabase();//创建数据库和表
        datalist=new ArrayList<Note>();

        //Add data here in the List from database
        refresh();//将当前的数据库中的所有项重新导入显示列表

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        myAdapter=new MyAdapter(datalist,this);

        recyclerView.setAdapter(myAdapter);

//
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
            public void onItemLongClick(View view, int position) {
//                Toast.makeText(NoteList.this,"长按",Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NoteList.this,NoteEdit.class);
                intent.putExtra("mode","new");
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void refresh(){
        datalist= DataSupport.findAll(Note.class);
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {

    }
}
