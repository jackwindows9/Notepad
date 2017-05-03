package com.example.notepad;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 司维 on 2017/4/22.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>  {//这个adapter是为了将数据和view做适配
    private List<Note> list;
    private static MyItemClickListener mListener;
    private static MyItemLongClickListener mLongClickListener;

    public MyAdapter(List<Note> list){
        //构造函数，传入数据
        this.list=list;
    }

    public void setList(List<Note> list){
        this.list=list;
    }//成员变量list的set方法

    public void setmListener(MyItemClickListener mListener) {//成员变量mListener的set方法
        this.mListener = mListener;
    }

    public void setmLongClickListener(MyItemLongClickListener mLongClickListener) {//成员变量mLongClickListener的set方法
        this.mLongClickListener = mLongClickListener;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {//内部类ViewHolder
        public static TextView text1;
        public static TextView text2;
        public static TextView time;
        public static TextView tv;
        public static View view;

        public ViewHolder(final View itemView) {
            super(itemView);
            view=itemView;
            text1= (TextView) itemView.findViewById(R.id.tv1);
            text2= (TextView) itemView.findViewById(R.id.tv2);
            time= (TextView) itemView.findViewById(R.id.time);
            //tv=(TextView)itemView.findViewById(R.id.tv);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position=getAdapterPosition();
                    mLongClickListener.onItemLongClick(itemView,position);
                    return true;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=getAdapterPosition();
                    mListener.onItemClick(itemView,position);
                }
            });
        }

    }
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {//通过布局创建view，紧接着创建ViewHolder
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {//将数据和viewHolder中的子项view进行绑定
        Note note=list.get(position);
        holder.text1.setText(note.getTitle());
        holder.text2.setText(note.getContent());
        holder.time.setText(note.getDate());
    }

    @Override
    public int getItemCount() {//获得数据项的大小
        return list.size();
    }


}
