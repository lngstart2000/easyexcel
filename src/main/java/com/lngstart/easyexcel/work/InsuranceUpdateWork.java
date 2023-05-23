package com.lngstart.easyexcel.work;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.lngstart.easyexcel.model.CaseKeyToValueModel;
import com.lngstart.easyexcel.model.InsuranceUpdateModel;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lngstart
 * @date 2023/5/18
 */
public class InsuranceUpdateWork {
    public static String basePath = "";
    static {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        File homeDirectory = fileSystemView.getHomeDirectory();
        basePath = homeDirectory.getPath();
    }

    public static void main(String[] args) throws Exception{
        String path = basePath + "\\险种对应保司-23.5.9.xlsx";

        List<InsuranceUpdateModel> resultList = new ArrayList<>();
        EasyExcel.read(path, InsuranceUpdateModel.class, new PageReadListener<InsuranceUpdateModel>(dataList -> {
            resultList.addAll(dataList);
        })).sheet(0).doRead();

        // 拼接sql接口
        List<String> sqlList = new ArrayList<>();
        String baseSql = "update public.tbl_case_detail_insurance_type set company_id = %d where id = %d";
        for(InsuranceUpdateModel model : resultList) {
            String companyId = model.getCompanyId();
            Long id = strToLong(companyId);
            if(id == null) continue;
            String format = String.format(baseSql, id, model.getInsuranceId());
            sqlList.add(format);
        }

        String sql = sqlList.stream().collect(Collectors.joining(";\n")) + ";";
        System.out.println("===============================");
        System.out.println(sql);

    }

    public static Long strToLong(String num) {
        if(StrUtil.isEmpty(num)) return null;
        try {
            return Long.valueOf(num);
        } catch (Exception e) {
            return null;
        }
    }
}
