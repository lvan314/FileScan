package com.zhch1999.filescan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhch1999.filescan.R;
import com.zhch1999.filescan.beans.FileBean;

import java.util.List;

import static android.view.View.inflate;

public class FileItemAdapter extends BaseAdapter {
    Context context=null;
    List<FileBean> fileBeansList=null;

    public FileItemAdapter(List<FileBean> data,Context context){
        this.context=context;
        this.fileBeansList=data;
    }

    @Override
    public int getCount() {
        return fileBeansList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileBeansList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_filelist_item, parent, false);
        ImageView imageView=convertView.findViewById(R.id.file_image);
        TextView textViewFileName=convertView.findViewById(R.id.file_name);
        TextView textViewFileSize=convertView.findViewById(R.id.file_size);
        TextView textViewLastModifed=convertView.findViewById(R.id.modify_time);
        textViewLastModifed.setText(fileBeansList.get(position).getLastModified());//最后一次修改时间
        Boolean isDirec=fileBeansList.get(position).isDirec();
        CheckBox checkBox=convertView.findViewById(R.id.checkbox_item);
        if(isDirec){
            /**
             * 文件夹默认不可多选
             */
            if(fileBeansList.get(position).enableMoreChoose){
                checkBox.setVisibility(View.INVISIBLE);
            }else{
                checkBox.setVisibility(View.GONE);
            }
            imageView.setImageResource(R.drawable.ic_folder);
            System.out.println(fileBeansList.get(position).getChidFileNumber()+"---------------项");
            textViewFileSize.setText(fileBeansList.get(position).getChidFileNumber()+"项");
        }else{
            /**
             * 文件根据判断来进行筛选
             */
            if(fileBeansList.get(position).enableMoreChoose){
                checkBox.setVisibility(View.VISIBLE);
                if(fileBeansList.get(position).isChecked==false){
                   // checkBox.callOnClick();
                }else{
                    checkBox.setChecked(true);
                }
            }else{
                checkBox.setVisibility(View.GONE);
            }
            imageView.setImageResource(R.drawable.ic_file);
            /**
             * 1073741824==>g
             * 1048576==> m
             * 1024==>k
             */
            double fileSize=fileBeansList.get(position).getFileSize();
            if(fileSize>1073741824){
                textViewFileSize.setText(Double.parseDouble(String.format("%.2f",fileSize/1073741824))+"g");
            }else if(fileSize>=1048576){
                textViewFileSize.setText(Double.parseDouble(String.format("%.2f",fileSize/1048576))+"m");
                //textViewFileSize.setText(fileSize/1048576+"m");
            }else if(fileSize>=1024){
                textViewFileSize.setText(Double.parseDouble(String.format("%.2f",fileSize/1024))+"k");
                //textViewFileSize.setText(fileSize/1024+"k");
            }else if(fileSize>=0&&fileSize<1024){
                textViewFileSize.setText(Double.parseDouble(String.format("%.2f",fileSize))+"b");
                //textViewFileSize.setText(fileSize+"byte");
            }else{
                textViewFileSize.setText("无效文件");
            }

        }
        textViewFileName.setText(fileBeansList.get(position).getFileName());
        return convertView;
    }
}
