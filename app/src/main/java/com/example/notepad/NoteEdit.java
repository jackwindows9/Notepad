package com.example.notepad;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.notepad.R.id.save;

public class NoteEdit extends AppCompatActivity {
    private EditText text1;
    private EditText text2;
    private MenuItem item_settings;
    private String editMode;//从上一个activity获得的参数，区分update和create
    private String saveMode = "";//区分直接结束活动保存和点击保存按钮保存
    private int newId;//存放新建并已保存的note的id
    private Boolean updateIsEmpty = false;
    private static String noSave = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_edit);
        setSupportActionBar(toolbar);
        text1 = (EditText) findViewById(R.id.edittext1);
        text2 = (EditText) findViewById(R.id.edittext2);
        Intent intent = getIntent();
        editMode = intent.getStringExtra("mode");
        if (editMode.equals("update")) {
            int id = intent.getIntExtra("id", 0);
            Note note = DataSupport.find(Note.class, id);
            text1.setText(note.getTitle());
            text2.setText(note.getContent());
        }
    }

    private void newNote() {
        Note note = new Note();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String title = text1.getText().toString();
        String content = text2.getText().toString();
        if (title.equals("") && content.equals("")) {
            noSave = "no";
            return;
        }
        note.setTitle(text1.getText().toString());
        note.setContent(text2.getText().toString());
        note.setDate(df.format(new Date()));
        note.save();
        newId=note.getId();
    }

    private void updateNote(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", text1.getText().toString());
        contentValues.put("content", text2.getText().toString());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        contentValues.put("date",df.format(new Date()));
        DataSupport.update(Note.class, contentValues, id);
        if (text1.getText().toString().equals("") && text2.getText().toString().equals("")) {
            DataSupport.delete(Note.class, id);//如果更新之后，标题和内容都为空，那么删除
            updateIsEmpty = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        switch (saveMode){
            case "save":
                //主动保存
                break;
            case "delete":
                //删除
                Log.d("delete","successful");
                break;
            default:
                Intent intent = getIntent();
                editMode = intent.getStringExtra("mode");
                switch (editMode) {
                    case "new":
                        newNote();
                        if (!noSave.equals("no")) {
                            Toast.makeText(NoteEdit.this, "Saved By default", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "update":
                        //更新数据，与新建不同在于显示数据,并且id是不变的
                        int id = intent.getIntExtra("id", 0);
                        updateNote(id);
                        if (updateIsEmpty)
                            ;//编辑后为空，这条记录已经被删除，那么活动的声生命周期走到onPause的时候则不会给出Toast，因为并未保存
                        else {
                            Toast.makeText(NoteEdit.this, "Saved By default", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        item_settings = menu.findItem(R.id.action_settings);
        item_settings.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == save) {
            Intent intent = getIntent();
            editMode = intent.getStringExtra("mode");
            switch (editMode) {
                case "new":
                    newNote();
                    break;
                case "update":
                    //更新数据，与新建不同在于显示数据,并且id是不变的
                    int ids = intent.getIntExtra("id", 0);
                    updateNote(ids);
                    break;
                default:
                    break;
            }
            saveMode = "save";
            item.setVisible(false);
            item_settings.setVisible(true);

            return true;
        }
        if (id == R.id.menuitem_delete) {
            Intent intent = getIntent();
            int ids = intent.getIntExtra("id", -1);
            if(ids==-1){
                //默认-1，表示新建的note，并未从NoteList这个活动中获取id
                ids=newId;
                Log.d("delete",""+ids);
            }
            DataSupport.delete(Note.class, ids);
            saveMode = "delete";
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}
