package com.example.notepad;

/**
 * Created by 司维 on 2017/5/5.
 */

public interface ItemToucheHelperAdapter {
    //数据交换
    void onItemMove(int fromPosition,int toPosition);
    //数据删除
    void onItemDissmiss(int position);

}
