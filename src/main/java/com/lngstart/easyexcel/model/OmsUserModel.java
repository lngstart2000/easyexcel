package com.lngstart.easyexcel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lngstart
 * @date 2023/7/5
 */
@Data
public class OmsUserModel {
    @ExcelProperty("id")
    private Long id;

    @ExcelProperty("name")
    private String name;

    @ExcelProperty("phone")
    private String phone;

    @ExcelProperty("account")
    private String account;

    @ExcelProperty("company_id")
    private Long companyId;

    @ExcelProperty("deleted")
    private Short deleted;
}
