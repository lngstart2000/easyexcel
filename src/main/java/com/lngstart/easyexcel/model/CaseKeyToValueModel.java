package com.lngstart.easyexcel.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CaseKeyToValueModel {

    private static final long serialVersionUID = 1L;

    // 自增ID
    private Long id;

    // 关键字对应表字段
    private String key;

    // 关键字中文
    private String title;

    // 票据类型id
    private Long caseModelId;

    // OCR返回json对应key值
    private String jsonKey;

    // 在运维平台展示 客户前端不展示
    private Integer showInManagement;

    // 展示的保司id包含其子机构
    private String showCompanys;

    // 展示的险种组ID 空值则全部展示，多个之间用,分割
    private String insuranceGroup;

    // 重点处理
    private Integer important;

    // 重点处理
    private Map platformDataSource ;

}
