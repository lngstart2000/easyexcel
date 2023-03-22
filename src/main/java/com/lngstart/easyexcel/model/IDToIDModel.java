package com.lngstart.easyexcel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;

/**
 * @author lngstart
 * @date 2023/3/21
 */

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class IDToIDModel {
    @ExcelProperty("company_id")
    private Long id;

    @ExcelProperty("target_company_id")
    private Long target_id;
}
