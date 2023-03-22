package com.lngstart.easyexcel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author lngstart
 * @date 2023/3/21
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class FormData {

    @ExcelProperty("原公司id")
    private Long companyId;

    @ExcelProperty("公司名称")
    private String companyName;

    @ExcelProperty("迁移后公司id")
    private Long companyTargetId;

    @ExcelProperty("迁移后公司名称")
    private String companyTargetName;

    @ExcelProperty("票据类型")
    private String billType;

    @ExcelProperty("票据类型id")
    private Long billTypeId;

    @ExcelProperty("迁移前表头")
    private String form;

    @ExcelProperty("迁移后表头")
    private String formTarget;

    @ExcelProperty("表头是否匹配")
    private String isCheck;
}
