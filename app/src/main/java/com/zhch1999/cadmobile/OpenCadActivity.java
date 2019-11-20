package com.zhch1999.cadmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.MxDraw.McGePoint3d;
import com.MxDraw.MrxDbgUiPrPoint;
import com.MxDraw.MxDrawActivity;
import com.MxDraw.MxDrawDragEntity;
import com.MxDraw.MxFunction;
import com.MxDraw.MxLibDraw;

import org.cocos2dx.lib.Cocos2dxEditBox;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.ResizeLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.MxDraw.MxFunction.initMxDraw;
import static com.MxDraw.MxFunction.setGridColor;

public class OpenCadActivity extends MxDrawActivity implements View.OnClickListener {
    protected boolean m_isLoadAndroidLayoutUi = false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        m_isLoadAndroidLayoutUi = true;
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String mFile = extras.getString("file");
            if (!mFile.isEmpty()) {
                MxFunction.initMxDraw(false, "Null", "Null", "Null", "Null");
                //MxFunction.addUpButton("Mx_Menu");
                // MxFunction.setShowUpToolBar(true);
                MxFunction.openFile(mFile);
            }
        }
    }

    /**
     * 手机按键按下
     *
     * @param iKeyCode
     */
    @Override
    public void onKeyReleased(int iKeyCode) {
        Log.e("keyCode", iKeyCode + "");
        //6 -返回
        if (iKeyCode == 6) {
            /**
             * 开始执行命令 命令名称为string类型
             */
            finish();
        }
    }

    /**
     * 打开cad文件结束
     *
     * @param isOk
     */
    @Override
    public void openComplete(boolean isOk) {
        super.openComplete(isOk);
    }

    /**
     * 触摸事件
     *
     * @param iType 触摸类型
     *              单击事件----0
     * @param dX    位置x坐标
     * @param dY    位置y坐标
     * @return
     */
    @Override
    public int touchesEvent(final int iType, double dX, double dY) {
        return super.touchesEvent(iType, dX, dY);
    }

    @Override
    public boolean createInterfaceLayout() {
        if (!m_isLoadAndroidLayoutUi)
            return false;
        setContentView(R.layout.activity_open_cad);
        ResizeLayout mFrameLayout = (ResizeLayout) this.findViewById(R.id.my_frame);
        Cocos2dxGLSurfaceView mGLSurfaceView = (Cocos2dxGLSurfaceView) this.findViewById(R.id.view_cad);
        Cocos2dxEditBox edittext = (Cocos2dxEditBox) this.findViewById(R.id.my_edittext);
        initInterfaceLayout(mFrameLayout, edittext, mGLSurfaceView);
        Button buttonAddPointsFile = findViewById(R.id.add_pointsfile);
        buttonAddPointsFile.setOnClickListener(this);
        Button buttonChangeResource = findViewById(R.id.changedwg);
        buttonChangeResource.setOnClickListener(this);
        Button buttonChangeColor = findViewById(R.id.changecolor);
        buttonChangeColor.setOnClickListener(this);
        Button buttonSave = findViewById(R.id.save);
        buttonSave.setOnClickListener(this);
        return true;
    }

    @Override
    public void mcrxEntryPoint(int iCode) {
        long[] rgb=new long[3];
        rgb[0]=255;
        rgb[1]=0;
        rgb[2]=0;
        MxLibDraw.setDrawColor(rgb);
        MxLibDraw.setLineWidth(0.5f);
        MxFunction.setSysVarLong("PMODE",35);
       // MxLibDraw.drawPoint(100,100,100);
        MxFunction.setViewColor(255,255,255);
        MxFunction.setAutoRegen(true);
        super.mcrxEntryPoint(iCode);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /**
             * 加载点库
             */
            case R.id.add_pointsfile:
                int permission = ActivityCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    String[] needRequest = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions((Activity) getContext(), needRequest, 1);
                } else {
                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/1S项目/测试放样速度.txt");
                    List<Object> pointsList = new ArrayList<>();
                    if (file.exists() && file.isFile()) {
                        /**
                         * 按行读取文件
                         */
                        try {
                            InputStream inputStream = new FileInputStream(file);
                            //if (inputStream != null) {
                            FileReader inputreader = new FileReader(file);
                            byte[] head = new byte[3];
                            inputStream.read(head);

                            String code = "gb2312";
                            if (head[0] == -1 && head[1] == -2)
                                code = "UTF-16";
                            if (head[0] == -2 && head[1] == -1)
                                code = "Unicode";
                            if (head[0] == -17 && head[1] == -69 && head[2] == -65)
                                code = "UTF-8";
                            Log.e("编码------------------->",code);
                            BufferedReader buffreader = new BufferedReader(
                                    new InputStreamReader(inputStream, code));
                            FileReader fr = null;
                            BufferedReader br = null;
                            String line;
                            //分行读取
                            while ((line = buffreader.readLine()) != null) {
                                String[] item = new String[3];
//                                item[0] = String.valueOf(Long.parseLong(line.toString().split(",")[-3]));
//                                item[1] = String.valueOf(Long.parseLong(line.toString().split(",")[-2]));
//                                item[2] = String.valueOf(Long.parseLong(line.toString().split(",")[-1]));
                                Log.e("point-->", line.split(",")[2]+ "-"+line.split(",")[3]+"-"+line.split(",")[4]);
                                /**
                                 * N,E,H,
                                 * -3,-2,-1
                                 */
                                MxLibDraw.drawPoint(Double.parseDouble(line.split(",")[2]),
                                        Double.parseDouble(line.split(",")[3]),
                                        Double.parseDouble(line.split(",")[4]));
                                //pointsList.add(item);
                            }
                            inputreader.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getContext(),"加载点库",Toast.LENGTH_SHORT).show();
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ThreadPoolExecutor executor = new ThreadPoolExecutor(
//                                        3,
//                                        30,
//                                        1,
//                                        TimeUnit.SECONDS,
//                                        new LinkedBlockingQueue<Runnable>(128));
//                                File file=new File(Environment.getExternalStorageDirectory().getPath()+"/1S项目/测试放样速度.txt");
//                                List<Object> pointsList=new ArrayList<>();
//                                if(file.exists()){
//                                    /**
//                                     * 按行读取文件
//                                     */
//                                    try {
//                                        InputStream inputStream = new FileInputStream(file);
//                                        if (inputStream != null)
//                                        {
//                                            InputStreamReader inputreader = new InputStreamReader(inputStream);
//                                            BufferedReader buffreader = new BufferedReader(inputreader);
//                                            String line;
//                                            //分行读取
//                                            while (( line = buffreader.readLine()) != null) {
//                                                long[] item=new long[3];
//                                                item[0]= Long.parseLong(line.toString().split(",")[-3]);
//                                                item[1]= Long.parseLong(line.toString().split(",")[-2]);
//                                                item[2]= Long.parseLong(line.toString().split(",")[-1]);
//                                                Log.e("point-->",item[0]+"");
//                                                /**
//                                                 * N,E,H,
//                                                 * -3,-2,-1
//                                                 */
//                                                pointsList.add(item);
//                                            }
//                                            inputStream.close();
//                                        }
//
//                                    } catch (FileNotFoundException e) {
//                                        e.printStackTrace();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                    for (int i = 0; i < 30; i++) {
//                                        final int index = i;
//                                        Runnable runnable = new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                try {
//                                                    String threadName = Thread.currentThread().getName();
//                                                    Log.v("---->", "线程："+threadName+",正在执行第" + index + "个任务");
//                                                    Thread.currentThread().sleep(1);
//                                                } catch (InterruptedException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        };
//                                        executor.execute(runnable);
//                                    }
//                                }else{
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Toast.makeText(getContext(),"点库文件不存在",Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            }
//                        }).run();
//                    }
//                });
                }
                break;
            case R.id.changecolor:
                runOnGLThread(new Runnable() {
                    @Override
                    public void run() {
                       // Toast.makeText(getContext(), "改变颜色", Toast.LENGTH_SHORT).show();
                        MxLibDraw.drawPoint(100,100,100);
                        MxFunction.zoomAll();
                    }
                });
                break;
            case R.id.save:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "保存", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.changedwg:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "换源", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }
}
