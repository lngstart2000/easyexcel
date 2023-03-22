package com.lngstart.easyexcel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

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
public class CaseKeyToValueModel {

    private static final long serialVersionUID = 1L;

    // 自增ID
    @ExcelProperty("id")
    private Long id;

    // 关键字对应表字段
    @ExcelProperty("key")
    private String key;

    // 关键字中文
    @ExcelProperty("title")
    private String title;

    // 票据类型id
    @ExcelProperty("case_model_id")
    private Long caseModelId;

    // OCR返回json对应key值
//    @ExcelProperty("json_key")
//    private String jsonKey;
//
//    // 在运维平台展示 客户前端不展示
//    @ExcelProperty("show_in_management")
//    private Integer showInManagement;

    // 展示的保司id包含其子机构
    @ExcelProperty("show_companys")
    private String showCompanys;

//    // 展示的险种组ID 空值则全部展示，多个之间用,分割
//    @ExcelProperty("insurance_group")
//    private String insuranceGroup;
//
//    // 重点处理
//    private Integer important;
//
//    // 重点处理
//    private Map platformDataSource ;

}
