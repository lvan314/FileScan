package com.zhch1999.filescan.utils;

import android.text.TextUtils;
import android.util.Log;

import com.zhch1999.filescan.beans.FileBean;
import com.zhch1999.filescan.beans.FileHistoryBean;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.net.PasswordAuthentication;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 根据传入的路径查询路径下的所有文件
 * 实现思路：
 * 1.判断传入的路径是否存在
 * 如果不存在 则返回null
 * 2. 路径存在 则判断是否是文件夹
 * 如果不是文件夹，则返回null
 * 3. 如果是文件夹 则对其子文件夹进行list,并遍历子文件
 * 4. 如果子文件是对应的格式 则封装返回 如果不是则移除
 * 5.文件大小设置todo g m k b
 */
public class FileScanUitl {
    /**
     * 扫描路径文件的子一级文件
     *
     * @param filePath 文件路径
     * @param suffix   文件格式
     * @return
     */
    public List<FileBean> fileScan(String filePath, String suffix[]) {
        File file = new File(filePath);
        /**
         * 1.判断文件是否存在
         */
        if (file.exists()) {
            /**
             *2.判断路径是否是文件夹
             */
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    /**
                     * 3 遍历其子文件
                     */
                    List<FileBean> listFileBean = new ArrayList<>();
                    for (File fileItem : files) {
                        /**
                         * 判断是否是所需格式
                         */
                        if (fileItem.isDirectory()) {
                            /**
                             * 文件夹---没有后缀 没有大小
                             * 需要设置子文件个数
                             */
                            long lastTime = fileItem.lastModified();
                            SimpleDateFormat formatter = new
                                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String lastModified = formatter.format(lastTime);
                            long childFileNumber = getChildNumber(fileItem.getAbsolutePath());
                            System.out.println(fileItem.getName()+"----------------->"+childFileNumber);
                            FileBean fileBean = new FileBean();
                            fileBean.fileSize = 0;
                            fileBean.suffix = "";
                            fileBean.fileName = fileItem.getName();
                            fileBean.isDirec = true;
                            fileBean.filePath = fileItem.getAbsolutePath();
                            fileBean.lastModified = lastModified;
                            fileBean.chidFileNumber = childFileNumber;
                            listFileBean.add(fileBean);
                        } else {
                            /**
                             * 是文件，子文件个数木有 修改时间有
                             */
                            if (suffix==null) {
                                /**
                                 * 不带筛选条件
                                 */
                                FileBean fileBean = new FileBean();
                                fileBean.fileSize = fileItem.length();
                                String fileSuffix=getExtensionName(fileItem.getName()); //后缀
                                fileBean.suffix = fileSuffix;
                                fileBean.fileName = fileItem.getName();
                                fileBean.isDirec = false;
                                fileBean.filePath = fileItem.getAbsolutePath();
                                fileBean.chidFileNumber = 0;
                                long lastTime = fileItem.lastModified();
                                SimpleDateFormat formatter = new
                                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String lastModified = formatter.format(lastTime);
                                fileBean.lastModified = lastModified;
                                listFileBean.add(fileBean);
                            } else {
                                /**
                                 * 带筛选条件
                                 */
                                String fileSuffix=getExtensionName(fileItem.getName()); //后缀
                                /**
                                 * 判断是否有后缀
                                 */

                                if(fileSuffix!=""){
                                    /**
                                     * 判断后缀是否是需要的那几种
                                     */
                                    if(Arrays.asList(suffix).contains(fileSuffix)){
                                        FileBean fileBean = new FileBean();
                                        fileBean.fileSize = fileItem.length();
                                        fileBean.suffix = fileSuffix;
                                        fileBean.fileName = fileItem.getName();
                                        fileBean.isDirec = false;
                                        fileBean.filePath = fileItem.getAbsolutePath();
                                        fileBean.chidFileNumber = 0;
                                        long lastTime = fileItem.lastModified();
                                        SimpleDateFormat formatter = new
                                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        String lastModified = formatter.format(lastTime);
                                        fileBean.lastModified = lastModified;
                                        listFileBean.add(fileBean);
                                    }
                                }

                            }


                        }
                    }
                    return listFileBean;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
/**
 * 获取文件扩展名
 * 筛选文件用
 */
public String getExtensionName(String filename) {
    if ((filename != null) && (filename.length() > 0)) {
        int dot = filename.lastIndexOf('.');
        if ((dot >-1) && (dot < (filename.length() - 1))) {
            return filename.substring(dot + 1);
        }
    }
    return filename;
}

    /**
     * 获取文件夹的子文件个数
     *
     * @param path 文件夹路径
     * @return 个数
     */
    public long getChildNumber(String path) {
        File file = new File(path);
        if(file.exists()){
            File[] files=file.listFiles();
            if(files!=null){
                return files.length;
            }else{
                return 0;
            }
        }else{
            file.mkdir(); //如果不存在则创建文件夹
            return getChildNumber(file.getAbsolutePath());
        }
    }
    /**
     * 获取文件的最后一次修改时间
     */
    public String  getLastModified(String path){
        File file=new File(path);
       long lastTime=file.lastModified();
       if(lastTime>0){
           SimpleDateFormat formatter = new
                   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           String lastModified = formatter.format(lastTime);
           return lastModified;
       }else{
        return "未知时间";
       }
    }
}
