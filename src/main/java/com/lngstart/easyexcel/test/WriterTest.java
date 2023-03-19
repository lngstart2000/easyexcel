package com.lngstart.easyexcel.test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.lngstart.easyexcel.dto.DemoData;

import java.util.Date;
import java.util.List;

public class WriterTest {
    public static void main(String[] args) {
        List<DemoData> list = ListUtils.newArrayList();
        for (int i = 0; i < 10; i++) {
            DemoData data = new DemoData();
            data.setString("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }

        String filePath = "C:\\Users\\Public\\Desktop\\test.xlsx";
        EasyExcel.write(filePath, DemoData.class).sheet("模板").doWrite(list);
    }
}
