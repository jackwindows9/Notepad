package com.example.notepad;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoteEdit extends AppCompatActivity {
    private EditText text1;
    private EditText text2;
    private MenuItem item_settings;
    private String editMode;//从上一个activity获得的参数，区分update和create
    private String saveMode = "";//区分直接结束活动保存和点击保存按钮保存
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
    }

    private void updateNote(int id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", text1.getText().toString());
        contentValues.put("content", text2.getText().toString());
        DataSupport.update(Note.class, contentValues, id);
        if (text1.getText().toString().equals("") && text2.getText().toString().equals("")) {
            DataSupport.delete(Note.class, id);//如果更新之后，标题和内容都为空，那么删除
            updateIsEmpty = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (saveMode.equals("save")) {//主动保存
//            if(!noSave.equals("no"))
//            Toast.makeText(NoteEdit.this,"已保存",Toast.LENGTH_SHORT).show();
        } else {
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_edit, menu);
        item_settings = menu.findItem(R.id.about);
        item_settings.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.save) {
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
        }

        return super.onOptionsItemSelected(item);

    }
}
