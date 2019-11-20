package com.zhch1999.cadmobile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zhch1999.filescan.beans.FileBean;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Edited  by Lvan
 * 2019-11-17
 */

public class MainActivity extends AppCompatActivity {
    /**
     * Button定义 最近 选择文件 文件浏览
     */
    private Button buttonLatest, buttonChooseFile, buttonFileScan;
    //返回码 启动文件扫描返回码
    private final static int FILE_SCAN_REQUEST_CODE = 111; //请求扫描文件请求码
    private final static int FILE_SCAN_SUCCESS = 512;//获取选择文件成功
    private final static int FILE_SCAN_FAILURE = 314;//获取文件路径失败
    private final static int FILES_SCAN_SUCCESS=222;//获取文件列表成功
    private Button buttonAddSuffix;
    private EditText editTextSuffix;
    private Button buttonSingle,buttonDouble,buttonMore;
    private List<String> suffixList=new ArrayList<>();
    private TextView textViewShowSuffix,textViewShowResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonAddSuffix=findViewById(R.id.addsuffix);
        editTextSuffix=findViewById(R.id.edt_suffx);
        buttonSingle=findViewById(R.id.single);
        buttonDouble=findViewById(R.id.doubler);
        buttonMore=findViewById(R.id.more);
        textViewShowSuffix=findViewById(R.id.showSuffix);
        textViewShowResult=findViewById(R.id.showResult);
        buttonAddSuffix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String suffixItem=editTextSuffix.getText().toString();
                if(suffixItem.equals("")){
                    Toast.makeText(MainActivity.this,"后缀不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    suffixList.add(suffixItem);
                    String temp="";
                    for(int i=0;i<suffixList.size();i++){
                        temp+=suffixList.get(i)+",";
                    }
                    Toast.makeText(MainActivity.this,temp,Toast.LENGTH_SHORT).show();
                    textViewShowSuffix.setText(temp);
                }
            }
        });
        buttonSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.zhch1999.filescan.MainActivity.class);
                //String[] arrFileType ={"CAD图", "dwg", "dxf","txt","dat"};
                String[] arrFileType=new String[suffixList.size()];
                for(int i=0;i<suffixList.size();i++){
                    if(suffixList.get(i).equals("")){
                    }else{
                        arrFileType[i]=suffixList.get(i);
                    }
                }
                for(int i=0;i<arrFileType.length;i++){
                    Log.e(i+"",arrFileType[i]+"");
                }
                intent.putExtra("fileTypes",arrFileType);
                intent.putExtra("operateType","single");
                startActivityForResult(intent, FILE_SCAN_REQUEST_CODE);
            }
        });
        buttonDouble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.zhch1999.filescan.MainActivity.class);
                //String[] arrFileType ={"CAD图", "dwg", "dxf","txt","dat"};
                String[] arrFileType=new String[suffixList.size()];
                for(int i=0;i<suffixList.size();i++){
                    if(suffixList.get(i).equals("")){
                    }else{
                        arrFileType[i]=suffixList.get(i);
                    }
                }
                for(int i=0;i<arrFileType.length;i++){
                    Log.e(i+"",arrFileType[i]+"");
                }
                intent.putExtra("fileTypes",arrFileType);
                intent.putExtra("operateType","double");
                startActivityForResult(intent, FILE_SCAN_REQUEST_CODE);
            }
        });
        buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.zhch1999.filescan.MainActivity.class);
                //String[] arrFileType ={"CAD图", "dwg", "dxf","txt","dat"};
                String[] arrFileType=new String[suffixList.size()];
                for(int i=0;i<suffixList.size();i++){
                    if(suffixList.get(i).equals("")){
                    }else{
                        arrFileType[i]=suffixList.get(i);
                    }
                }
                for(int i=0;i<arrFileType.length;i++){
                    Log.e(i+"",arrFileType[i]+"");
                }
                intent.putExtra("fileTypes",arrFileType);
                intent.putExtra("operateType","more");
                startActivityForResult(intent, FILE_SCAN_REQUEST_CODE);
            }
        });







        //-------------------------------unsefull--------------------------------------------------
        buttonLatest = findViewById(R.id.latest);
        buttonLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LatestActivity.class);
                startActivity(intent);
            }
        });
        buttonChooseFile = findViewById(R.id.choosefile);
        buttonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChooseFileActivity.class));
            }
        });
        buttonFileScan = findViewById(R.id.filescan);
        buttonFileScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 进去浏览文件的activity
                 */
                Intent intent = new Intent(MainActivity.this, com.zhch1999.filescan.MainActivity.class);
                //String[] arrFileType ={"CAD图", "dwg", "dxf","txt","dat"};
                String[] arrFileType ={"dwg", "dxf","txt","dat"};
                intent.putExtra("fileTypes",arrFileType);
                intent.putExtra("operateType","more");
                startActivityForResult(intent, FILE_SCAN_REQUEST_CODE);
            }
        });
    }

    /**
     * 启动activities后返回接受
     *
     * @param requestCode 请求码
     * @param resultCode  返回码
     * @param intentOut   上一个页面返回的intent数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intentOut) {
        /**
         * 1.启动activities时有请求码 这个可用于目标Activity确认启动的actitvty对象
         * 2.返回码，目标Activity返回时设置的result_code,可先确认requestcode，再确认resultCode
         */
        switch (requestCode) {
            case FILE_SCAN_REQUEST_CODE: //
                /**
                 * 对选择文件activity返回结果的判定
                 */
                switch (resultCode) {
                    case FILE_SCAN_SUCCESS:
                        /**
                         * 浏览文件返回
                         */
                        Intent intent = intentOut;
                        Bundle bundle = intent.getExtras();
                        //Toast.makeText(MainActivity.this, bundle.getString("path"), Toast.LENGTH_SHORT).show();
//                        Intent intentOpenCad=new Intent(this,OpenCadActivity.class);
//                        intentOpenCad.putExtra("file",bundle.getString("path"));
                        //startActivity(intentOpenCad);
                       // Toast.makeText(MainActivity.this,bundle.getString("path"),Toast.LENGTH_LONG).show();
                        textViewShowResult.setText(bundle.getString("path"));
                        break;
                    case FILE_SCAN_FAILURE:
                        Toast.makeText(MainActivity.this, "没有选择文件", Toast.LENGTH_SHORT).show();
                        textViewShowResult.setText("没有选择文件");
                        break;
                    case FILES_SCAN_SUCCESS:
                        Intent intent1 = intentOut;
                        Bundle bundle1 = intent1.getExtras();
                        ArrayList<String> list= bundle1.getStringArrayList("fileList");
                        //Toast.makeText(MainActivity.this,list.toString(),Toast.LENGTH_LONG).show();
                        textViewShowResult.setText(list.toString());
                        break;
                    case 444:
                        Toast.makeText(MainActivity.this,"格式编辑请求",Toast.LENGTH_LONG).show();
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, intentOut);
    }
}
