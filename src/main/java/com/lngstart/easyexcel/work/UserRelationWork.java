package com.lngstart.easyexcel.work;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

/**
 * @author lngstart
 * @date 2023/7/5
 */
public class UserRelationWork {

    public static String basePath = "";
    static {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        File homeDirectory = fileSystemView.getHomeDirectory();
        basePath = homeDirectory.getPath();
    }

    /**
     * 小额用户和oms迁移用户关系记录表
     * @param args
     */
    public static void main(String[] args) {

    }

}
