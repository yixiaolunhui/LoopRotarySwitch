package com.dalong.library.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 * @author zwl
 * @describe TODO
 * @date on 2020-01-27
 */
public interface ILoopViewAdapter {

    int getCount();

    Object getItem(int position);

    View getView(int position, View convertView, ViewGroup parent);

    void notifyDataSetChanged();
}
