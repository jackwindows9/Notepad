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

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements ItemToucheHelperAdapter{//这个adapter是为了将数据和view做适配
    private List<Note> list;
    private static MyItemClickListener mListener;
    private static MyItemLongClickListener mLongClickListener;
    public static boolean isDeleteMode=false;


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




    @Override
    public void onItemMove(int fromPosition, int toPosition) {
//        Collections.swap(list, fromPosition, toPosition);
//        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDissmiss(int position) {
        list.remove(position);
        notifyItemRemoved(position);
//        Note note=list.get(position);
//        DataSupport.delete(Note.class,note.getId());
        //在这个函数中，本来是将某个item删除，但是不建议采用这样的方法，转而使用一个长按弹出checkbox来选定删除
    }
    //这里是item删除和移动的响应函数，这里不采用这种方法，
    // 相关联的文件有ItemToucheHelperAdapter,OnStartDragListener,SimpleItemToucheHelperCallback


    static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder{//内部类ViewHolder
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
        @Override
        public void onItemSelected() {
            //itemView.setBackgroundColor(Color.LTGRAY);
        }
        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
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
                isDeleteMode=true;
                return true;
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isDeleteMode) {//当前页面未处于删除模式，才可以删除
                    int position = viewHolder.getAdapterPosition();
                    mListener.onItemClick(viewHolder.itemView, position);
                }
                else{
//                    Note note=list.get(viewHolder.getAdapterPosition());
//                    if(note.isChecked()){//当前已被选中，点击后更改checkbox以及note的isChecked状态
////                        Log.d("data","true");
//                        viewHolder.checkBox.setChecked(false);
//                        ContentValues contentValues=new ContentValues();
//                        contentValues.put("isChecked",false);
//                        int id=note.getId();
//                        DataSupport.update(Note.class,contentValues,id);
//                        list=DataSupport.findAll(Note.class);
//                    }
//                    else{
////                        Log.d("data","false");
//                        viewHolder.checkBox.setChecked(true);
//                        ContentValues contentValues=new ContentValues();
//                        contentValues.put("isChecked",true);
//                        int id=note.getId();
//                        DataSupport.update(Note.class,contentValues,id);
//                        list=DataSupport.findAll(Note.class);
//                    }
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder( MyAdapter.ViewHolder holder, int position) {//将数据和viewHolder中的子项view进行绑定
        final Note note=list.get(position);
        holder.checkBox.setVisibility(NoteList.checkboxIsVisible?View.VISIBLE:View.GONE);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ContentValues contentValues=new ContentValues();
                contentValues.put("isChecked",isChecked);
                int id=note.getId();
                DataSupport.update(Note.class,contentValues,id);
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
