package com.example.notepad;

import android.content.ContentValues;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by 司维 on 2017/4/22.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {//这个adapter是为了将数据和view做适配
    private List<Note> list;
    private static MyItemClickListener mListener;
    private static MyItemLongClickListener mLongClickListener;
    private MyCheckboxChangedListener myCheckboxChangedListener;

    public MyAdapter(List<Note> list){
        this.list=list;
    }
    public void setList(List<Note> list){
        this.list=list;
    }
    public void setmListener(MyItemClickListener mListener) {//成员变量mListener的set方法
        this.mListener = mListener;
    }
    public void setmLongClickListener(MyItemLongClickListener mLongClickListener) {
        this.mLongClickListener = mLongClickListener;
    }
    public void setMyCheckboxChangedListener(MyCheckboxChangedListener myCheckboxChangedListener){
        this.myCheckboxChangedListener=myCheckboxChangedListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {//内部类ViewHolder
        public TextView text1;
        public TextView text2;
        public TextView time;
        public CheckBox checkBox;
        public ViewHolder(final View itemView) {
            super(itemView);
            text1= (TextView) itemView.findViewById(R.id.tv1);
            text2= (TextView) itemView.findViewById(R.id.tv2);
            time= (TextView) itemView.findViewById(R.id.time);
            checkBox=(CheckBox)itemView.findViewById(R.id.checkbox);
        }
    }
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//通过布局创建view，紧接着创建ViewHolder
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);
        final ViewHolder viewHolder=new ViewHolder(view);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position=viewHolder.getAdapterPosition();
                mLongClickListener.onItemLongClick(v, position);
                NoteList.isDeleteMode=true;
                return true;
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    int position = viewHolder.getAdapterPosition();
                    mListener.onItemClick(viewHolder.itemView, position);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( MyAdapter.ViewHolder holder, int position) {//将数据和viewHolder中的子项view进行绑定
        final Note note=list.get(position);
        holder.checkBox.setVisibility(NoteList.isDeleteMode?View.VISIBLE:View.GONE);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ContentValues contentValues=new ContentValues();
                contentValues.put("isChecked",isChecked);
                int id=note.getId();
                DataSupport.update(Note.class,contentValues,id);
                myCheckboxChangedListener.onChanged();
            }
        });
        if(note.getTitle().equals("")){//无标题则取内容的前几个字符
            if(note.getContent().length()<=10)
                holder.text1.setText(note.getContent());
            else
                holder.text1.setText(note.getContent().substring(0,10));
        }
        else {
            holder.text1.setText(note.getTitle());
        }
        if(note.getContent().length()>21){//内容过长，省略部分
            holder.text2.setText(note.getContent().substring(0,21)+"...");
        }
        else {
            holder.text2.setText(note.getContent());
        }
        holder.time.setText(note.getDate());
    }
    @Override
    public int getItemCount() {//获得数据项的大小
        return list.size();
    }
}
