package com.zhch1999.cadmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhch1999.cadmobile.R;
import com.zhch1999.cadmobile.model.SelfFile;

import java.util.List;

public class SelfFileAdapter  extends BaseAdapter {
    private List<SelfFile> selfFileList;
    private Context mContext;

    public SelfFileAdapter(List<SelfFile> datas, Context mContext) {
        selfFileList=datas;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return selfFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return selfFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(mContext).inflate(R.layout.self_file_list_item,viewGroup,false);
        TextView textView=view.findViewById(R.id.fileName);
        textView.setText(selfFileList.get(i).fileName);
        return view;
    }
}
