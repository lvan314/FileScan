package com.zhch1999.filescan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zhch1999.filescan.R;
import com.zhch1999.filescan.beans.FileBean;
import com.zhch1999.filescan.beans.FileHistoryBean;

import java.util.ArrayList;
import java.util.List;

public class HorizontalListViewAdapter extends BaseAdapter {
    private  Context context;
    private List<FileHistoryBean> filePathList=new ArrayList<>();
    public HorizontalListViewAdapter(List<FileHistoryBean> data, Context context){
        this.context=context;
        this.filePathList=data;
    }
    @Override
    public int getCount() {
        return filePathList.size();
    }

    @Override
    public Object getItem(int i) {
        return filePathList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.filepath_list_item, viewGroup, false);
        TextView textView=view.findViewById(R.id.path_item);
        textView.setText(filePathList.get(i).getFileName()+"/");
        return view;
    }
}
