package com.lngstart.easyexcel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author lngstart
 * @date 2023/5/18
 */
@Data
public class InsuranceUpdateModel {
    @ExcelProperty("险种id")
    private Long insuranceId;

    @ExcelProperty("组iD")
    private Long groupId;

    @ExcelProperty("x险种名称")
    private String insuranceName;

    @ExcelProperty("所属机构代码")
    private String companyId;
}
