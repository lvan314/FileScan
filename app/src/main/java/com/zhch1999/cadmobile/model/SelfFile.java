package com.zhch1999.cadmobile.model;

/**
 * 自定义文件模型
 * 包含文件名 和所在路径
 */
public class SelfFile {
    public String fileName;
    public String filePath;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }
}
