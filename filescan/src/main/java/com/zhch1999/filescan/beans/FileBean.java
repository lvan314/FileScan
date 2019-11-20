package com.zhch1999.filescan.beans;

public class FileBean implements Comparable<FileBean>{
    public String fileName; //文件名
    public String filePath;  //文件路径
    public boolean isDirec; //是否是文件夹
    public String suffix;  //后缀
    public double fileSize; //文件大小
    public String lastModified;//最后修改时间
    public long chidFileNumber;//子文件数量

    public String getLastModified() {
        return lastModified;
    }
    public long getChidFileNumber() {
        return chidFileNumber;
    }
    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isDirec() {
        return isDirec;
    }

    public String getSuffix() {
        return suffix;
    }

    public double getFileSize() {
        return fileSize;
    }

    @Override
    public int compareTo(FileBean fileBean) {
        if(this.isDirec&&!fileBean.isDirec){
            /**
             * 文件夹排前面
             */
            return -1;
        }else if(!this.isDirec&& fileBean.isDirec){
            /**
             * 文件排后面
             */
            return 1;
        }else{
            if(fileBean.fileName.startsWith(".")&&!this.fileName.startsWith(".")){
                /**
                 * 小数点排后面 没有小数点的排前面
                 */
                return -1;
            }else if (!fileBean.fileName.startsWith(".")&&this.fileName.startsWith(".")){
                return 1;
            }else{
                /**
                 * 按字母先后 大写字母先，小写字母后
                 */
                return this.fileName.compareTo(fileBean.fileName);
            }

        }
    }
}
