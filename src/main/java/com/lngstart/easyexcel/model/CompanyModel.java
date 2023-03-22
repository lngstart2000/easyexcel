package com.lngstart.easyexcel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author bin.wang
 * @since 2019-12-29
 */

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CompanyModel {

    private static final long serialVersionUID = 1L;

    @ExcelProperty("id")
    private Long id;

    // 公司名
    @ExcelProperty("company_name")
    private String companyName;

    // 父公司ID
    @ExcelProperty("p_id")
    private Long pId;

    // 公司分组
    @ExcelProperty("company_group")
    private Integer companyGroup;

//    // 关键词匹配比例组
//    private Integer nameKeyToRateGroup;
//
//    // 关键词匹配比例组
//    private Integer showLimit;
//
//    // 关键词匹配比例组
//    private Integer insuranceTypeGroup;
//
//    // 关键词匹配比例组
//    private Integer useInsuranceType;

//    // 展示页面类型 默认0 1:泰康模板
//    private Integer showResultType;
//
//    // 省份
//    private String province;
//
//    // 下载类型 0 DOC 1 PDF
//    private Integer downLoadType;
//
//
//    // 城市
//    private String city;
//
//    // 城市编码
//    private String cityCode;
//
//    // 省份名称
//    private String provinceName;
//
//    // 城市名称
//    private String cityName;
//
//    // 省份_药品类型
//    private String drugProvinceType;
//
//    // 替换用药权限
//    private Integer alternativesPermission;
//
//    // 默认药品类型
//    private String defaultDrugType;
//
//
//    // 判断历史相同票据用分组
//    private Integer sameImgCheckGroup;
//
//    // 关键表头核对用分组
//    private Integer formKeyCheckGroup;
//
//    // 是否表头核对
//    private Integer checkForm;
//
//    // 可以使用优先级
//    private Integer usePriority;
//
//    // 公司的类别 0-普通客户 1-特殊客户 2-付费客户
//    private Integer special;
//
//    // 公司的优先级别 越大优先级越高
//    private Integer priority;
//
//    // 查询列表排序优先级 越大优先级越高
//    private Integer orderPriority;
//
//    // 是否同步至COP 0-否 1-是
//    private Integer sync;
//
//    // 该机构是否有诉讼金额
//    private Integer isLitigation;
//
//    // 保险公司
//    private String companyI;
//
//    private Integer autoCommit;
//
//    /**
//     * 结果修改限制 0-不允许修改结果 1-允许修改结果
//     */
//    private Integer forbidenResultModify;
//
//    private Integer pageVersion;
//
//    private Integer appletPageVersion;
//
//    private Integer submitType;
//
//    private Integer showOtherPay;
//
//    // api推送地址id
//    private Long apiPushId;
}
