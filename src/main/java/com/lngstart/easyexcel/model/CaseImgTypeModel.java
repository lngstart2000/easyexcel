package com.lngstart.easyexcel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;

/**
 * <p>
 * 
 * </p>
 *
 * @author bin.wang
 * @since 2019-11-19
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CaseImgTypeModel {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("id")
    private Long id;

    @ExcelProperty("name")
    private String name;

    @ExcelProperty("hospital_id")
    private Integer hospitalId;

    @ExcelProperty("bill_type_id")
    private Integer billTypeId;

    // 计算类型 0 门诊 1 住院 2其他 3诊断
    @ExcelProperty("cal_type")
    private Integer calType;


    // 计算类型 0 门诊 1 住院 2其他
    @ExcelProperty("ocr_type")
    private String ocrType;

    @ExcelProperty("cut_before_ocr")
    private Integer cutBeforeOcr;

    // 单据类型 1-发票 2-清单 3-其他
    @ExcelProperty("invoice_type")
    private Integer invoiceType;

    // 单据二级类型 1-报销结算单 2-其他
    @ExcelProperty("invoice_second_type")
    private Integer invoiceSecondType;

}
