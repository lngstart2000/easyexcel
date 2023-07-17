package com.lngstart.easyexcel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author lngstart
 * @date 2023/7/13
 */
@Data
public class UserRelation {
    @ExcelProperty("payout_user")
    private Long payoutUser;

    @ExcelProperty("omsUser")
    private Long omsUser;

    @ExcelProperty("account")
    private String account;

    @ExcelProperty("companyId")
    private Long companyId;

    @ExcelProperty("password")
    private String password;

    @ExcelProperty("name")
    private String name;

    @ExcelProperty("phone")
    private String phone;

    @ExcelProperty("delete")
    private Integer delete;

    @ExcelProperty("isOms")
    private String isOms;

    @ExcelProperty("sign_name_url")
    private String signNameUrl;
}
