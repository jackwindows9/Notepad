package com.example.notepad;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private String editMode;//从上一个activity获得的参数，区分update和create
    private String saveMode = "";//区分直接结束活动保存和点击保存按钮保存
    private int updateId = -1;
    private Note note;
    private boolean isSaveDeafult = true;//save by default

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
            updateId = intent.getIntExtra("id", 0);
            note = DataSupport.find(Note.class, updateId);
            text1.setText(note.getTitle());
            text2.setText(note.getContent());
        }
    }

    private void newNote() {
        note = new Note();
        String title = text1.getText().toString();
        String content = text2.getText().toString();
        if (title.equals("") && content.equals("")) {
            isSaveDeafult = false;
            finish();//when save nothing, finish it and don't give users a chance to input without saving button.
            return;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        note.setTitle(text1.getText().toString());
        note.setContent(text2.getText().toString());
        note.setDate(df.format(new Date()));
        note.save();
    }

    private void updateNote(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", text1.getText().toString());
        contentValues.put("content", text2.getText().toString());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        contentValues.put("date", df.format(new Date()));
        DataSupport.update(Note.class, contentValues, id);
        if (text1.getText().toString().equals("") && text2.getText().toString().equals("")) {
            DataSupport.delete(Note.class, id);//by the end, nothing in title and content.so delete it;
            isSaveDeafult=false;
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        switch (saveMode) {
            case "delete":
                //delete the note;
                break;
            case "save":
                //use saving button to save;
                break;
            default:
                switch (editMode) {
                    case "new":
                        newNote();
                        if (isSaveDeafult) {
                            Toast.makeText(NoteEdit.this, "Saved By default", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "update":
                        updateNote(updateId);
                        //It is not existing a condition that nothing is saved,but Toast.show("Saved By default"),
                        //Because when nothing is be saved ,it will be finished.
                        if(isSaveDeafult) {
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == save) {
            switch (editMode) {
                case "new":
                    newNote();
                    break;
                case "update":
                    updateNote(updateId);
                    break;
                default:
                    break;
            }
            saveMode = "save";
            finish();
            return true;
        }
        if (id == R.id.menuitem_delete) {
            if (updateId != -1) {
                //updateId
                DataSupport.delete(Note.class, updateId);
                //delete this updateId
            }
            //if newId,we know newId==-1 now
            //nothing be created
            //nothing we need to do
            saveMode = "delete";//do nothing in onPause
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
