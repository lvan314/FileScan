package com.zhch1999.filescan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrinterId;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.zhch1999.filescan.adapters.FileItemAdapter;
import com.zhch1999.filescan.adapters.HorizontalListViewAdapter;
import com.zhch1999.filescan.beans.FileBean;
import com.zhch1999.filescan.beans.FileHistoryBean;
import com.zhch1999.filescan.utils.FileScanUitl;
import com.zhch1999.filescan.views.HorizontalListView;
import com.zyq.easypermission.EasyPermission;
import com.zyq.easypermission.EasyPermissionResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Spinner spinnerFileType;//文件类型选择器
    private Button buttonEnsure;//确定
    private ListView listViewFile; //文件列表
    private RadioButton radioButtonPhoneStorage;
    private RadioButton radioButtonNetReceived;
    private ImageView imageViewUpImage;
    private HorizontalListView mHorizontalListView; //横向文件路径列表
    private HorizontalListViewAdapter mHorizontalListViewAdapter; //横向文件路径列表适配器
    private List<FileHistoryBean> fileHistoryList = new ArrayList<>(); //横向文件路径列表对应的数据
    private Button buttonSearch;//搜索按钮
    private Button buttonOrder; //排序按钮
    private final static int FILE_SCAN_SUCCESS = 512;//获取选择文件成功
    private final static int FILE_SCAN_FAILURE = 314;//获取文件路径失败
    private final static int FILES_SCAN_SUCCESS = 222;//获取多个文件成功
    private final static int RE_EDIT_FORMAT = 444;//格式编辑 返回
    private int W_R_REQUEST_CODE = 111;//读写内存权限申请码
    private int SEARCH_ACTVITY_CODE = 112;//跳转到搜索页面请求码
    List<FileBean> fileBeanList = new ArrayList<>(); //适配器对应的list
    FileItemAdapter fileItemAdapter; //文件列表适配器
    private String rootPath = Environment.getExternalStorageDirectory().getPath();//默认的手机内存的根路径
    private String weixinFilePath = rootPath + "/tencent/MicroMsg";//微信接收文件根路径
    private String qqFilePath = rootPath + "/tencent/QQfile_recv"; //qq接收文件根路径
    private String qqEmailFilePath = rootPath + "/Download/QQMail/"; //qq邮箱接收文件根路径
    private String bluetoothFilePath = rootPath + "/bluetooth"; //蓝牙接收文件根路径
    private String[] shaixuanType = null;//用户的筛选类型 默认为空 显示所有文件 不进行筛选
    private String suffixSpinner = null;//筛选类型下拉框的选择的单种格式
    private String operateType = "single"; //single，急速模式 单击就开 double，配置模式 点击选中，再打开文件 more多选后按确定
    private int checkedView = 0;//暂存当前选中的listview的item的hashcode
    private View viewGetFocus = null; //暂存当前选中的view 方便改变选中的view的背景色
    private int checkedPosition = -1; //暂存当前选中的position 操作类型为double时有用
    private String nowFilePath = "";//暂存当前选择文件的路径
    private List<FileBean> filesChoosedList = new ArrayList<>(); //如果是多选状态，则暂存用户选择的文件信息
    private List<View> viewsChooosedList = new ArrayList<>();//多选状态，则暂存用户点击的view
    private String fileOrderType="no"; //文件排序类型
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_filescan);
        Intent oldIntent = getIntent();
        Bundle bundle = oldIntent.getExtras();
        try {
            String[] receiveFileType = bundle.getStringArray("fileTypes");
            operateType = bundle.getString("operateType"); //操作模式赋值
            if (receiveFileType.length == 0) {
                shaixuanType = null;
            } else {
                shaixuanType = receiveFileType;
            }
        } catch (NullPointerException e) {
            TastyToast.makeText(MainActivity.this, "请求参数错误", Toast.LENGTH_LONG, TastyToast.ERROR).show();
            Log.e("-----------", "出错");
            //finish();
        }
/**
 * 搜索
 */
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("op", operateType);//传入选择类型
                startActivityForResult(intent, SEARCH_ACTVITY_CODE);

            }
        });
        /**
         * 排序
         */
        buttonOrder=findViewById(R.id.buttonOrder);
        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
        /**
         * 下拉框数据初始化
         */
        spinnerFileType = findViewById(R.id.file_type_spinner);
        final String[] arrFileType;//下拉框显示的数据
        if (shaixuanType != null) {
            arrFileType = new String[shaixuanType.length + 1];//shaixuanType;
            for (int i = 0; i < shaixuanType.length; i++) {
                if (i == 0) {
                    arrFileType[0] = Arrays.asList(shaixuanType).toString().replace("]", "").replace("[", "");
                } else {
                    arrFileType[i] = shaixuanType[i - 1];
                }
            }
        } else {
            arrFileType = new String[2];
            arrFileType[0] = "全部";//{"CAD图", "*dwg", "*.dxf","*.txt","*.dat"};
        }
        arrFileType[arrFileType.length - 1] = "格式编辑";
        //创建ArrayAdapter对象
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrFileType);
        spinnerFileType.setAdapter(adapterSpinner);
        spinnerFileType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == arrFileType.length - 1) {
                    /**
                     * 最后一位 为格式编辑
                     */
                    Intent intent = getIntent();
                    setResult(RE_EDIT_FORMAT, intent);
                    finish();
                } else if (i == 0) {
                    /**
                     * 多种类型筛选
                     */
                    suffixSpinner = null; //换为null模式 即为全选
                } else {
                    suffixSpinner = arrFileType[i];
                }
                File file = new File(nowFilePath);
                if (file.getAbsolutePath() != null) {
                    searchFile(file.getAbsolutePath());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        /**
         * 确认选择
         */
        buttonEnsure = findViewById(R.id.ensure);
        buttonEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                if (operateType.equals("single")) {
                    intent.putExtra("path", "--------->");
                    setResult(FILE_SCAN_FAILURE, intent);
                    finish();
                } else if (operateType.equals("double")) {
                    Log.e("-----------", checkedPosition + "");
                    if (checkedPosition == -1) {
                        /**
                         * double时的确定事件---未选中
                         */
                        setResult(FILE_SCAN_FAILURE, intent);
                        finish();
                    } else {
                        intent.putExtra("path", fileBeanList.get(checkedPosition).getFilePath());
                        intent.putExtra("fileName", fileBeanList.get(checkedPosition).getFileName());
                        intent.putExtra("suffix", fileBeanList.get(checkedPosition).getSuffix());
                        setResult(FILE_SCAN_SUCCESS, intent);
                        finish();
                    }
                } else if (operateType.equals("more")) {
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
                }
            }
        });
        /**
         *
         */
        listViewFile = findViewById(R.id.filelist);
        fileItemAdapter = new FileItemAdapter(fileBeanList, this);
        listViewFile.setAdapter(fileItemAdapter);
        listViewFile.setItemsCanFocus(true);//让listview的item获取到焦点
        listViewFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (fileBeanList.get(position).isDirec) {
                    /**
                     * 文件夹，继续深入
                     */
                    String newPath = fileBeanList.get(position).getFilePath();
                    fileBeanList.clear();
                    searchFile(newPath);
                } else {
                    if (operateType.equals("double")) {
                        if (checkedView == (view.hashCode())) {
                            Intent intent = getIntent();
                            /**
                             * 返回码 512 代表选择文件成功  314代表未选择文件
                             * path是返回文件的全路径
                             * suffix 是选择文件的后缀
                             * fileName 文件名
                             */
                            intent.putExtra("path", fileBeanList.get(position).getFilePath());
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
                            checkedView = view.hashCode();
                            viewGetFocus = view;
                            view.setBackgroundColor(Color.CYAN);
                        }
                    } else if (operateType.equals("more")) {
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

                    } else {
                        Intent intent = getIntent();
                        /**
                         * 返回码 512 代表选择文件成功  314代表未选择文件
                         * path是返回文件的全路径
                         * suffix 是选择文件的后缀
                         * fileName 文件名
                         */
                        intent.putExtra("path", fileBeanList.get(position).getFilePath());
                        intent.putExtra("fileName", fileBeanList.get(position).getFileName());
                        intent.putExtra("suffix", fileBeanList.get(position).getSuffix());
                        setResult(FILE_SCAN_SUCCESS, intent);
                        finish();
                    }


                }
            }
        });

        /**
         * 向上按钮的点击事件
         */
        imageViewUpImage = findViewById(R.id.upImage);
        imageViewUpImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 返回到上一页 如果上一页没有了 则提示没法向上了
                 */

                if (radioButtonNetReceived.isChecked()) {
                    /**
                     * 网络接收模式下 返回到根路径时应返回到radiobutton刚点击时的页面
                     */
                    if (nowFilePath.equals(qqEmailFilePath) ||
                            nowFilePath.equals(bluetoothFilePath) ||
                            nowFilePath.equals(weixinFilePath) ||
                            nowFilePath.equals(qqFilePath)) {
                        radioButtonNetReceived.callOnClick();
                    } else {
                        /**
                         * 返回上一页操作
                         */
                        File file;
                        if (fileBeanList.size() == 0) {
                            /**
                             * 当前目录为空
                             */
                            file = new File(nowFilePath + "/ttt");
                        } else {
                            file = new File(fileBeanList.get(0).getFilePath());
                        }
                        if (nowFilePath.equals(rootPath)) {
                            /**
                             * 已到达根路径
                             */
                            Toast.makeText(MainActivity.this, "已到达根路径了", Toast.LENGTH_SHORT).show();
                        } else {
                            /**
                             * 未到达根路径 返回上一页
                             */
                            if (file.getParentFile() != null) {
                                File grandFile = new File(file.getParentFile().getAbsolutePath());
                                if (grandFile.exists()) {
                                    searchFile(grandFile.getParentFile().getAbsolutePath());
                                } else {
                                    Intent intent = getIntent();
                                    Log.e("----------", "--------------");
                                    setResult(FILE_SCAN_FAILURE, intent);
                                    TastyToast.makeText(MainActivity.this, "ddd", Toast.LENGTH_LONG, TastyToast.INFO).show();
                                    finish();
                                }
                            } else {
                                /**
                                 * 不存在 那么返回
                                 */
                                Toast.makeText(MainActivity.this, "不存在的文件路径" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    File file;
                    if (fileBeanList.size() == 0) {
                        /**
                         * 当前目录为空
                         */
                        file = new File(nowFilePath + "/ttt");
                    } else {
                        file = new File(fileBeanList.get(0).getFilePath());
                    }
                    if (nowFilePath.equals(rootPath)) {
                        /**
                         * 已到达根路径
                         */
                        Toast.makeText(MainActivity.this, "已到达根路径了", Toast.LENGTH_SHORT).show();
                    } else {
                        /**
                         * 未到达根路径 返回上一页
                         */
                        if (file.getParentFile() != null) {
                            File grandFile = new File(file.getParentFile().getAbsolutePath());
                            if (grandFile.exists()) {
                                searchFile(grandFile.getParentFile().getAbsolutePath());
                            } else {
                                Intent intent = getIntent();
                                Log.e("----------", "--------------");
                                setResult(FILE_SCAN_FAILURE, intent);
                                TastyToast.makeText(MainActivity.this, "ddd", Toast.LENGTH_LONG, TastyToast.INFO).show();
                                finish();
                            }
                        } else {
                            /**
                             * 不存在 那么返回
                             */
                            Toast.makeText(MainActivity.this, "不存在的文件路径" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

        /**
         * 横向文件路径列表
         */
        mHorizontalListView = findViewById(R.id.horizontal_lv);
//        final FileHistoryBean fileHistoryBean = new FileHistoryBean();
//        fileHistoryBean.fileName = "根路径";
//        fileHistoryBean.filePath = rootPath;
//        fileHistoryList.add(fileHistoryBean);
        mHorizontalListViewAdapter = new HorizontalListViewAdapter(fileHistoryList, MainActivity.this);
        mHorizontalListView.setAdapter(mHorizontalListViewAdapter);
        fileItemAdapter.notifyDataSetChanged();
        mHorizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MainActivity.this,fileHistoryList.get(i).fileName,Toast.LENGTH_SHORT).show();
                //nowFilePath.substring(nowFilePath.indexOf(fileHistoryList.get(i).fileName)+1);
                //Toast.makeText(MainActivity.this,nowFilePath.substring(nowFilePath.indexOf(fileHistoryList.get(i).fileName)),Toast.LENGTH_SHORT).show();
                String subStringOfPath = nowFilePath.substring(nowFilePath.indexOf(fileHistoryList.get(i).fileName));//点击item路径的后面部分
                searchFile(nowFilePath.replace(subStringOfPath, "") + fileHistoryList.get(i).fileName);
            }
        });
        /**
         * Radiobutton点击事件
         * 手机内存
         * 网络接收
         */
        radioButtonNetReceived = findViewById(R.id.net_received);
        radioButtonNetReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 网络接收
                 */
                fileBeanList.clear();
                FileBean fileBeanWeiXin = new FileBean();
                fileBeanWeiXin.filePath = weixinFilePath;
                fileBeanWeiXin.chidFileNumber = new FileScanUitl().getChildNumber(weixinFilePath);
                fileBeanWeiXin.isDirec = true;
                fileBeanWeiXin.fileName = "微信接收文件";
                fileBeanWeiXin.lastModified = new FileScanUitl().getLastModified(weixinFilePath);
                fileBeanList.add(fileBeanWeiXin);

                FileBean fileBeanQQReceived = new FileBean();
                fileBeanQQReceived.filePath = qqFilePath;
                fileBeanQQReceived.chidFileNumber = new FileScanUitl().getChildNumber(qqFilePath);
                fileBeanQQReceived.isDirec = true;
                fileBeanQQReceived.fileName = "QQ接收文件";
                fileBeanQQReceived.lastModified = new FileScanUitl().getLastModified(qqFilePath);
                fileBeanList.add(fileBeanQQReceived);

                FileBean fileBeanQQMailReceived = new FileBean();
                fileBeanQQMailReceived.filePath = qqEmailFilePath;
                fileBeanQQMailReceived.chidFileNumber = new FileScanUitl().getChildNumber(qqEmailFilePath);
                fileBeanQQMailReceived.isDirec = true;
                fileBeanQQMailReceived.fileName = "QQ邮箱接收文件";
                fileBeanQQMailReceived.lastModified = new FileScanUitl().getLastModified(qqEmailFilePath);
                fileBeanList.add(fileBeanQQMailReceived);

                FileBean fileBeanBluetooth = new FileBean();
                fileBeanBluetooth.filePath = bluetoothFilePath;
                fileBeanBluetooth.chidFileNumber = new FileScanUitl().getChildNumber(bluetoothFilePath);
                fileBeanBluetooth.isDirec = true;
                fileBeanBluetooth.fileName = "蓝牙";
                fileBeanBluetooth.lastModified = new FileScanUitl().getLastModified(bluetoothFilePath);
                fileBeanList.add(fileBeanBluetooth);

                fileItemAdapter.notifyDataSetChanged();
            }
        });
        radioButtonPhoneStorage = findViewById(R.id.phone_storage);
        radioButtonPhoneStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 手机内存
                 */
                searchFile(rootPath);
            }
        });
        /**
         * 获取根路径下的文件
         */
        radioButtonPhoneStorage.callOnClick();
    }

    /**
     * 初始化页面数据//根据路径获取数据
     */
    public void searchFile(String path) {
        /**
         * 取消多选之前的选择 --多选模式生效
         */
        if (operateType.equals("more") && viewsChooosedList != null) {
            viewsChooosedList.clear();
            filesChoosedList.clear();
        }
        nowFilePath = path;
        String[] permission = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        /**
         * 读取权限判断
         */
        if (EasyPermission.build().hasPermission(this, permission)) {
            //fileBeanList.addAll(new FileScanUitl().fileScan(path,""));
            String[] seflSuffix;
            if(shaixuanType==null){
                /**
                 * 用户未传筛选类型的情况
                 */
                seflSuffix = new String[1];
                seflSuffix[0]="";
            }else{
                seflSuffix = new String[shaixuanType.length];
            }
//            String[] seflSuffix = new String[shaixuanType.length];
            if (suffixSpinner != null) {
                /**
                 * 用户筛选的单种类型
                 */
                seflSuffix[0] = suffixSpinner;
            } else {
                seflSuffix = shaixuanType;
            }
            List<FileBean> fileBeanList2 = new FileScanUitl().fileScan(path, seflSuffix);
            String[] temArr = path.split("/");
            if (temArr != null) {
                /**
                 * 拆分当前路径 显示到页面
                 */
                fileHistoryList.clear();
                for (int i = 0; i < temArr.length; i++) {
                    /**
                     * 把前三省了 前三为全路径的根路径
                     */
                    if (i <= 3) {

                    } else {
                        FileHistoryBean fileHistoryBean = new FileHistoryBean();
                        fileHistoryBean.fileName = temArr[i];
                        fileHistoryList.add(fileHistoryBean);
                    }
                }
                mHorizontalListViewAdapter.notifyDataSetChanged();
            } else {

            }
            if (fileBeanList2 != null) {
                if(fileOrderType.equals("no")){
                    /**
                     * 默认的排序方式
                     */
                    fileBeanList.clear();
                    Collections.sort(fileBeanList2);
                    for (FileBean fileBean : fileBeanList2) {
                        fileBeanList.add(fileBean);
                    }
                    fileItemAdapter.notifyDataSetChanged();
                }else if(fileOrderType.equals("time")){
                    /**
                     * 按时间排序
                     */
                    fileBeanList.clear();
                    Collections.sort(fileBeanList2, new Comparator<FileBean>() {
                        @Override
                        public int compare(FileBean fileBean1, FileBean fileBean2) {
                            return fileBean1.lastModified.compareTo(fileBean2.lastModified)*(-1);//不乘以-1 为升序 否则为倒序
                        }
                    });
                    for (FileBean fileBean : fileBeanList2) {
                        fileBeanList.add(fileBean);
                    }
                    fileItemAdapter.notifyDataSetChanged();
                }
                else if(fileOrderType.equals("size")){
                    /**
                     * 按大小排序
                     */
                    fileBeanList.clear();
                    Collections.sort(fileBeanList2, new Comparator<FileBean>() {
                        @Override
                        public int compare(FileBean fileBean1, FileBean fileBean2) {
                            if(fileBean1.fileSize>fileBean2.fileSize){
                                return -1;
                            }else if(fileBean1.fileSize<fileBean2.fileSize){
                                return 1;
                            }else{
                                return 0;
                            }
                        }
                    });
                    for (FileBean fileBean : fileBeanList2) {
                        fileBeanList.add(fileBean);
                    }
                    fileItemAdapter.notifyDataSetChanged();
                }
                else if(fileOrderType.equals("type")){
                    /**
                     * 按类型排序
                     */
                    fileBeanList.clear();
                    Collections.sort(fileBeanList2, new Comparator<FileBean>() {
                        @Override
                        public int compare(FileBean fileBean1, FileBean fileBean2) {
                            return fileBean1.suffix.compareTo(fileBean2.suffix);
                        }
                    });
                    for (FileBean fileBean : fileBeanList2) {
                        fileBeanList.add(fileBean);
                    }
                    fileItemAdapter.notifyDataSetChanged();
                }
                else if(fileOrderType.equals("name")){
                    /**
                     * 按名称排序
                     */
                    fileBeanList.clear();
                    Collections.sort(fileBeanList2, new Comparator<FileBean>() {
                        @Override
                        public int compare(FileBean fileBean1, FileBean fileBean2) {
                            return fileBean1.fileName.compareTo(fileBean2.fileName);
                        }
                    });
                    for (FileBean fileBean : fileBeanList2) {
                        fileBeanList.add(fileBean);
                    }
                    fileItemAdapter.notifyDataSetChanged();
                }
            }

        } else {
            TastyToast.makeText(getApplicationContext(), "哇，没有权限", TastyToast.LENGTH_LONG, TastyToast.INFO);
            EasyPermission easyPermission = EasyPermission.build()
                    .mRequestCode(W_R_REQUEST_CODE)
                    .mContext(MainActivity.this)
                    .mPerms(permission)
                    .mResult(new EasyPermissionResult() {
                        @Override
                        public void onPermissionsAccess(int requestCode) {
                            super.onPermissionsAccess(requestCode);
                            //获取权限成功,重新刷新数据
                            searchFile(rootPath);
                        }

                        @Override
                        public void onPermissionsDismiss(int requestCode, @NonNull List<String> permissions) {
                            super.onPermissionsDismiss(requestCode, permissions);
                            /**
                             *  权限被用户拒绝
                             *  再重新申请
                             */
                            searchFile(rootPath);
                        }

                        @Override
                        public boolean onDismissAsk(int requestCode, @NonNull List<String> permissions) {
                            //你的权限被用户禁止了并且不能请求了你怎么办
                            // easyPermission.openAppDetails(MainActivity.this, "Call Phone - Give me the permission to dial the number for you");
                            TastyToast.makeText(getApplicationContext(), "取消了授权，不能使用此模块", TastyToast.LENGTH_LONG, TastyToast.INFO);
                            return true;
                        }
                    });
            easyPermission.requestPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (EasyPermission.APP_SETTINGS_RC == requestCode) {
            //从设置界面返回
            //Result from system setting
            if (EasyPermission.build().hasPermission(MainActivity.this)) {
                searchFile(rootPath);
            } else {
                //从设置回来还是没给你权限
                TastyToast.makeText(getApplicationContext(), "还是没给我权限呀", TastyToast.LENGTH_LONG, TastyToast.WARNING);
            }
        } else if (requestCode == SEARCH_ACTVITY_CODE) {
            switch (resultCode) {
                case FILE_SCAN_SUCCESS:
                    Intent intent = data;
                    Bundle bundle = intent.getExtras();
                    String filePath = bundle.getString("file");
                    File file = new File(filePath);
                    Intent intent1 = getIntent();
                    intent1.putExtra("path", filePath);
                    intent1.putExtra("fileName", file.getName());
                    intent1.putExtra("suffix", new FileScanUitl().getExtensionName(filePath));
                    setResult(FILE_SCAN_SUCCESS, intent1);
                    finish();
                    break;
                case FILES_SCAN_SUCCESS:
                    Intent intent3 = data;
                    Bundle bundle3 = intent3.getExtras();
                    ArrayList<String> list = bundle3.getStringArrayList("fileList");
                    Intent intent2 = getIntent();
                    Bundle bundle2 = new Bundle();
                    bundle2.putStringArrayList("fileList", list);
                    intent2.putExtras(bundle2);
                    setResult(FILES_SCAN_SUCCESS, intent2);
                    finish();
                    break;
                case FILE_SCAN_FAILURE:
//                    Intent intent4 = getIntent();
//                    intent4.putExtra("file", "");
//                    setResult(FILE_SCAN_FAILURE, intent4);
//                    finish();
                    Toast.makeText(MainActivity.this,"未选择文件",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            File file;
            if (fileBeanList.size() == 0) {
                /**
                 * 当前目录为空
                 */
                file = new File(nowFilePath + "/ttt"); //如果不存在子文件夹 就假装创建一下 这样获取到的父目录就和真正需要的一样
            } else {
                file = new File(fileBeanList.get(0).getFilePath());
            }
            if (fileHistoryList.size() == 0) {
                Intent intent = getIntent();
                intent.putExtra("file", "");
                setResult(FILE_SCAN_FAILURE, intent);
                finish();
            } else {
                if (file.getParentFile() != null) {
                    File grandFile = new File(file.getParentFile().getAbsolutePath());
                    if (grandFile.exists()) {
                        fileHistoryList.remove(fileHistoryList.size() - 1);
                        mHorizontalListViewAdapter.notifyDataSetChanged();
                        searchFile(grandFile.getParentFile().getAbsolutePath());
                    } else {
                        Intent intent = getIntent();
                        Log.e("----------", "--------------");
                        setResult(FILE_SCAN_FAILURE, intent);
                        TastyToast.makeText(MainActivity.this, "ddd", Toast.LENGTH_LONG, TastyToast.INFO).show();
                        finish();
                    }
                } else {
                    /**
                     * 不存在 那么返回
                     */
                    Intent intent = getIntent();
                    intent.putExtra("file", "");
                    setResult(FILE_SCAN_FAILURE, intent);
                    finish();
                }
            }

        } else {
            //TastyToast.makeText(MainActivity.this, "ttt", Toast.LENGTH_LONG, TastyToast.INFO).show();
        }
        return true;
    }
    /**
     * 排序popmenu
     */
    private void showPopupMenu(View view) {
        // 这里的view代表popupMenu需要依附的view
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        // 获取布局文件
        popupMenu.getMenuInflater().inflate(R.menu.file_order, popupMenu.getMenu());
        popupMenu.show();
        // 通过上面这几行代码，就可以把控件显示出来了
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 控件每一个item的点击事件
               if(item.getItemId()==R.id.time){
                   /**
                    * 按时间排序
                    */
                   fileOrderType="time";
               }else if(item.getItemId()==R.id.type){
                   /**
                    * 按文件类型排序
                    */
                   fileOrderType="type";
               }else if(item.getItemId()==R.id.name){
                   /**
                    * 按名称排序
                    */
                   fileOrderType="name";
               }else if(item.getItemId()==R.id.size){
                   /**
                    * 按文件大小排序
                    */
                   fileOrderType="size";
               }else if(item.getItemId()==R.id.no){
                   /**
                    * 默认排序
                    */
                   fileOrderType="no";
               }
               searchFile(nowFilePath);
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // 控件消失时的事件
            }
        });

    }
}
