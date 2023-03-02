package com.lngstart.easyexcel.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * @author lngstart
 * @date 2023/3/1
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DemoData {
    @ExcelProperty(index = 1)
    private String string;

    @ExcelProperty(index = 2)
    private Date date;

    @ExcelProperty(index = 3)
    private Double doubleData;
}
