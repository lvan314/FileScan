package com.zhch1999.cadmobile;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.zhch1999.cadmobile.adapters.SelfFileAdapter;
import com.zhch1999.cadmobile.model.SelfFile;
import com.zhch1999.cadmobile.utils.SearchFilesUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseFileActivity extends AppCompatActivity {
    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    ListView listViewFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_file);
        listViewFile=findViewById(R.id.fileNameLisList);
        /**
         * 权限申请
         */
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            } else{
                scanRoorStorage();
            }
        }
        final List<SelfFile> selfFiles=scanRoorStorage();
        SelfFileAdapter selfFileAdapter=new SelfFileAdapter(selfFiles,ChooseFileActivity.this);
        listViewFile.setAdapter(selfFileAdapter);
        listViewFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ChooseFileActivity.this,selfFiles.get(position).getFileName()+"",Toast.LENGTH_SHORT).show();
                /**
                 * 跳转至 cad绘制页
                  */
                Intent intent=new Intent(ChooseFileActivity.this,OpenCadActivity.class);
                intent.putExtra("file",selfFiles.get(position).getFilePath());
                startActivity(intent);
            }
        });
    }

    /**
     * 扫描内部存储的根路径 返回对应的dwg 格式
     */
    public ArrayList<SelfFile> scanRoorStorage(){
        ArrayList<SelfFile> arrayList=new ArrayList<>();
        /**
         * 根路径与获取方法
         * Environment.getExternalStorageDirectory().toString()----/storage/emulated/0 存储目录 并非是系统内置的sdk目录
         *System.getenv("EXTERNAL_STORAGE");----/sdcard 内置sd卡路径
         * Environment.getExternalStorageDirectory().getAbsolutePath();----/storage/emulated/0  内置sd卡路径
         *System.getenv("SECONDARY_STORAGE") 外置sd卡路径
         */
        String root=Environment.getExternalStorageDirectory().getPath()+"/1S项目";
       arrayList= new SearchFilesUtils().searchFilesUtils(root,"dwg");
       for(SelfFile file:arrayList){
           Log.e(file.fileName,file.filePath);
       }
       return arrayList;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }
}
