package com.lngstart.easyexcel.work;

import javax.swing.filechooser.FileSystemView;
import java.io.File;

public class FormCompanyCheck {

    public static String basePath = "";
    static {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        File homeDirectory = fileSystemView.getHomeDirectory();
        basePath = homeDirectory.getPath();
    }
    public static void main(String[] args) {
        System.out.println(basePath);
    }
}
