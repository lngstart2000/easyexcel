package com.lngstart.easyexcel.test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.lngstart.easyexcel.dto.DemoData;

/**
 * @author lngstart
 * @date 2023/3/1
 */
public class ReadTest {
    public static void main(String[] args) {
        String filePath = "C:\\Users\\nenggao.li\\Desktop\\test.xlsx";
        EasyExcel.read(filePath, DemoData.class, new PageReadListener<DemoData>(dataList -> {
            for(DemoData demoData : dataList) {
                System.out.println("记录为：" + demoData.toString());
            }
        })).sheet().doRead();
    }
}
