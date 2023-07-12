package com.lngstart.easyexcel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.lngstart.easyexcel.dto.OmsUserReasonVO;
import lombok.Data;

import java.util.List;

/**
 * @author lngstart
 * @date 2023/7/5
 */
@Data
public class PayoutUserModel {
    @ExcelProperty("id")
    private Long id;

    @ExcelProperty("account")
    private String account;

    @ExcelProperty("company_id")
    private Long companyId;

    @ExcelProperty("delete")
    private Integer delete;

    @ExcelProperty("username")
    private String username;

    @ExcelProperty("小额账号公司")
    private String companyName;

    /**
     * oms冲突用户名极其原因
     */
    @ExcelProperty("冲突账户及原因")
    private String omsAccountReason;

}
