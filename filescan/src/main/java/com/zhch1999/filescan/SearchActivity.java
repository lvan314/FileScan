package com.zhch1999.filescan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhch1999.filescan.adapters.FileItemAdapter;
import com.zhch1999.filescan.beans.FileBean;
import com.zhch1999.filescan.utils.FileScanUitl;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText editTextSearch; //搜索框
    private ListView listViewSearch; //搜索内容显示的listview
    private Button buttonSearch,buttonEnsureChoose; // 搜索按钮 确定选择按钮
    private TextView textViewSearchPath;//当前搜索路径
    private String operateType; //操作类型
    private List<FileBean> fileBeanList=new ArrayList<>(); //listview适配的数据list
    private FileItemAdapter fileItemAdapter=new FileItemAdapter(fileBeanList,SearchActivity.this); //listview 的适配器
    private int FILE_SCAN_SUCCESS=512; //选择单个文件成功
    private int FILE_SCAN_FAILURE=314;//选择文件失败
    private final static int FILES_SCAN_SUCCESS = 222;//获取多个文件成功
    /**
     * 双击和多选的参数
     */
    private int checkedViewHashCode= 0;//暂存当前选中的listview的item的hashcode
    private View viewGetFocus = null; //暂存当前选中的view 方便改变选中的view的背景色
    private int checkedPosition = -1; //暂存当前选中的position 操作类型为double时有用
    private List<FileBean> filesChoosedList = new ArrayList<>(); //如果是多选状态，则暂存用户选择的文件信息
    private List<View> viewsChooosedList = new ArrayList<>();//多选状态，则暂存用户点击的view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        editTextSearch=findViewById(R.id.searchEdit);
        listViewSearch=findViewById(R.id.searchListView);
        listViewSearch.setAdapter(fileItemAdapter);

        buttonSearch=findViewById(R.id.searchButton);
        textViewSearchPath=findViewById(R.id.searcbPath);

        final Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        operateType=bundle.getString("op");//获取操作类型 single单击进入 double单机选择 双击进入 more多选
        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(operateType.equals("single")){
                    /**
                     * 单击选择文件，选择后返回
                     */
                    intent.putExtra("file",fileBeanList.get(position).filePath);
                    setResult(FILE_SCAN_SUCCESS,intent);
                    finish();
                }else if(operateType.equals("double")){
                    /**
                     * 第一次选中 第二次确定
                     */
                    checkedPosition=position;//点击确定时使用
                    if (checkedViewHashCode == (view.hashCode())) {
                        Intent intent = getIntent();
                        /**
                         * 返回码 512 代表选择文件成功  314代表未选择文件
                         * path是返回文件的全路径
                         * suffix 是选择文件的后缀
                         * fileName 文件名
                         */
                        intent.putExtra("file", fileBeanList.get(position).getFilePath());
                        intent.putExtra("fileName", fileBeanList.get(position).getFileName());
                        intent.putExtra("suffix", fileBeanList.get(position).getSuffix());
                        setResult(FILE_SCAN_SUCCESS, intent);
                        finish();
                    } else {
                        checkedPosition = position;
                        //listViewFile.setSelection(position);
                        //fileItemAdapter.notifyDataSetChanged();//刷新组件去除上一个选中的颜色
                        if (viewGetFocus != null) {
                            viewGetFocus.setBackgroundColor(Color.TRANSPARENT);
                        }
                        checkedViewHashCode = view.hashCode();
                        viewGetFocus = view;
                        view.setBackgroundColor(Color.CYAN);
                    }
                }else if(operateType.equals("more")){
                    /**
                     * 多选 第一次选中 第二次取消选中
                     */
                    if (viewsChooosedList != null) {
                        if (viewsChooosedList.contains(view)) {
                            viewsChooosedList.remove(view);
                            view.setBackgroundColor(Color.TRANSPARENT);
                            filesChoosedList.remove(fileBeanList.get(position));
                        } else {
                            viewsChooosedList.add(view);
                            view.setBackgroundColor(Color.CYAN);
                            filesChoosedList.add(fileBeanList.get(position));
                        }
                    } else {
                        viewsChooosedList.add(view);
                        view.setBackgroundColor(Color.CYAN);
                        filesChoosedList.add(fileBeanList.get(position));
                    }
                }
            }
        });
        /**
         * 搜索按钮
         */
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edtContent=editTextSearch.getText().toString(); //用户输入的内容
                Toast.makeText(SearchActivity.this,edtContent,Toast.LENGTH_SHORT).show();
                SearchTask searchTask=new SearchTask();
                searchTask.execute(edtContent);
            }
        });
        /**
         * 确定选择
         */
        buttonEnsureChoose=findViewById(R.id.ensureChoose);
        buttonEnsureChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (operateType){
                    case "single":
                        intent.putExtra("file", "--------->");
                        setResult(FILE_SCAN_FAILURE, intent);
                        finish();
                        break;
                    case "double":
                        if (checkedPosition == -1) {
                            /**
                             * double时的确定事件---未选中
                             */
                            setResult(FILE_SCAN_FAILURE, intent);
                            finish();
                        } else {
                            intent.putExtra("file", fileBeanList.get(checkedPosition).getFilePath());
                            intent.putExtra("fileName", fileBeanList.get(checkedPosition).getFileName());
                            intent.putExtra("suffix", fileBeanList.get(checkedPosition).getSuffix());
                            setResult(FILE_SCAN_SUCCESS, intent);
                            finish();
                        }
                        break;
                    case "more":
                        ArrayList<String> resultList = new ArrayList<>();
                        if (filesChoosedList.size() != 0) {
                            for (FileBean fileBean : filesChoosedList) {
                                resultList.add(fileBean.getFilePath());
                            }
                            Intent intent1 = getIntent();
                            //intent1.putExtra("fileList", (Parcelable) resultList);
                            Bundle bundle1 = new Bundle();
                            bundle1.putStringArrayList("fileList", resultList);
                            intent1.putExtras(bundle1);
                            setResult(FILES_SCAN_SUCCESS, intent1);
                            finish();
                        } else {
                            /**
                             * 未选中任何文件
                             */
                            intent.putExtra("path", "--------->");
                            setResult(FILE_SCAN_FAILURE, intent);
                            finish();
                        }
                        break;
                }
            }
        });
    }

    public class SearchTask extends AsyncTask {
            @Override
            protected void onPreExecute() {
                /**
                 * 执行异步任务前操作
                 */
                buttonSearch.setText("搜索中");
                super.onPreExecute();
            }

        @Override
        protected List<FileBean>  doInBackground(Object[] objects) {
            /**
             * 工作线程执行的方法 不能调用ui操作
             */
            String searchContent=objects[0].toString();
            Log.e("搜索内容",searchContent);
            String rootPath= Environment.getExternalStorageDirectory().getPath();//默认的手机内存的根路径
            File rootFile=new File(rootPath);
            List<FileBean> searchRes=new ArrayList<>();
            File[] listRootFiles=rootFile.listFiles();
            for(File file:listRootFiles){
                publishProgress(file.getAbsolutePath());
                if(file.isFile()){
                    if(file.getName().indexOf(searchContent)!=-1){
                        FileBean fileBean=new FileBean();
                        fileBean.filePath=file.getAbsolutePath();
                        fileBean.fileName=file.getName();
                        fileBean.lastModified=new FileScanUitl().getLastModified(file.getAbsolutePath());
                        fileBean.chidFileNumber=0;
                        fileBean.isDirec=false;
                        fileBean.fileSize=file.length();
                        fileBean.suffix="";
                        searchRes.add(fileBean);
                    }
                }else{
                    if(searchFile(file.getAbsolutePath(), searchContent)!=null){
                        searchRes.addAll(searchFile(file.getAbsolutePath(), searchContent));
                    }

                }
            }

            //File rootFile=new File(rootPath); //根路径不肯不存在吧。。。额遇到了再说
            //List<FileBean> searchRes = searchFile(rootPath, searchContent);
            Log.e("异步搜索结果", Arrays.asList(searchRes).toString());
            return searchRes;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            /**
             * 可以在此方法内更新操作进度 需要调用publishProgress 方法才会执行
             */
            String temp=values[0].toString().split("/")[4];
            textViewSearchPath.setText("正在搜索路径:"+temp);


            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Object o) {
            /**
             * 接收任务执行结果 进行ui操作
             * 工作在主线程，所以可以更新ui操作
             * doInBackground()的返回数据存储在 ojbect o 里面
             */
            List<FileBean> fileBeanList1= (List<FileBean>) o;
            if(fileBeanList1.size()!=0){
                textViewSearchPath.setText("搜索完成");
                fileBeanList.clear();
                fileBeanList.addAll(fileBeanList1);
                fileItemAdapter.notifyDataSetChanged();
            }else{
                textViewSearchPath.setText("没有搜索到文件");
            }
            buttonSearch.setText("搜索"); //异步搜索执行完成
            super.onPostExecute(o);
        }
    }
    /**
     * 遍历文件夹 搜索匹配的文件名
     */
    private List<FileBean> searchFile(String path, String content){
//        Log.e("搜索路径",path);
//        Log.e("搜索内容",content);
        List<FileBean> fileBeanList=new ArrayList<>();
        File file=new File(path);
        if(file!=null){
            File[] listFiles=file.listFiles();
            if(listFiles!=null){
                for(File fileItem:listFiles){
                    if(fileItem.isFile()){
                        if(fileItem.getName().indexOf(content)!=-1){
                            FileBean fileBean=new FileBean();
                            fileBean.filePath=fileItem.getAbsolutePath();
                            fileBean.fileName=fileItem.getName();
                            fileBean.lastModified=new FileScanUitl().getLastModified(fileItem.getAbsolutePath());
                            fileBean.chidFileNumber=0;
                            fileBean.isDirec=false;
                            fileBean.fileSize=fileItem.length();
                            fileBean.suffix=new FileScanUitl().getExtensionName(fileItem.getAbsolutePath());
                            fileBeanList.add(fileBean);
                        }
                    }else{
                        if(searchFile(fileItem.getAbsolutePath(),content)!=null){
                            fileBeanList.addAll(searchFile(fileItem.getAbsolutePath(),content));
                        }
                    }
                }
                if(fileBeanList.size()==0){
                    return null;
                }else{
                    return fileBeanList;
                }
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    @Override
    public void onBackPressed() {
       Intent intent=getIntent();
       intent.putExtra("file","");
       setResult(FILE_SCAN_FAILURE,intent);
       finish();
    }
}
