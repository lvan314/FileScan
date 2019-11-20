package com.zhch1999.cadmobile.utils;

import android.util.Log;

import com.zhch1999.cadmobile.model.SelfFile;

import java.io.File;
import java.util.ArrayList;

/**
 * 查询文件工具 根据传入的文件夹路径和后缀名 返回该文件夹内部所有文件的文件名,和对应的文件路径
 */
public class SearchFilesUtils {
    private ArrayList<SelfFile> filesList=new ArrayList<>();
    /**
     *
     * @param strPath 查询路径
     * @param suffix 后缀格式
     * @return
     */
    public ArrayList<SelfFile> searchFilesUtils(String strPath,String suffix ) {
        String filename;//文件名
        String suf;//文件后缀
        File dir = new File(strPath);//文件夹dir
        File[] files = dir.listFiles();//文件夹下的所有文件或文件
        if (files == null){

            return null;
        }else {

            for (int i = 0; i < files.length; i++) {

                if (files[i].isDirectory()) {
                    System.out.println("---" + files[i].getAbsolutePath());
                    searchFilesUtils(files[i].getAbsolutePath(), suffix);    //递归文件夹！！！
                } else {
                    filename = files[i].getName();

                    int j = filename.lastIndexOf(".");
                    suf = filename.substring(j + 1);//得到文件后缀
                    if (suf.equalsIgnoreCase(suffix))    //判断是不是需要查询的文件格式
                    {
                        String strFileName = files[i].getAbsolutePath().toLowerCase();
                        SelfFile selfFile = new SelfFile();
                        selfFile.setFileName(filename);
                        selfFile.setFilePath(files[i].getAbsolutePath());
                        filesList.add(selfFile);//对于文件才把它的路径加到filelist中
                    }else{

                    }
                }
            }
        }
        return filesList;
    }
}