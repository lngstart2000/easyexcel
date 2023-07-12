package com.lngstart.easyexcel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lngstart
 * @date 2023/7/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OmsUserReasonVO {
    private String account;
    private String reason;
    private String desc;
}
