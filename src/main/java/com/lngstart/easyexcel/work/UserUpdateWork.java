package com.lngstart.easyexcel.work;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson.JSONObject;
import com.lngstart.easyexcel.dto.FormData;
import com.lngstart.easyexcel.dto.OmsUserReasonVO;
import com.lngstart.easyexcel.model.CompanyModel;
import com.lngstart.easyexcel.model.OmsUserModel;
import com.lngstart.easyexcel.model.PayoutUserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lngstart
 * @date 2023/7/5
 */
public class UserUpdateWork {

    public static String basePath = "";
    static {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        File homeDirectory = fileSystemView.getHomeDirectory();
        basePath = homeDirectory.getPath();
    }

    /**
     * 处理oms用户和小额用户冲突校验和迁移sql脚本生成
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String payOutPath = basePath + "//payout_user//payout_user.xlsx";
        String omsUserPath = basePath + "//payout_user//oms_user.xlsx";
        String resultPath = basePath + "//payout_user//result.xlsx";
        String companyPath = basePath + "//payout_user//mishu_public_tbl_company.xlsx";

        List<PayoutUserModel> payoutUserModelList = new ArrayList<>();
        EasyExcel.read(payOutPath, PayoutUserModel.class, new PageReadListener<PayoutUserModel>(dataList -> {
            payoutUserModelList.addAll(dataList);
        })).sheet(0).doRead();

        List<OmsUserModel> omsUserModelList = new ArrayList<>();
        EasyExcel.read(omsUserPath, OmsUserModel.class, new PageReadListener<OmsUserModel>(dataList -> {
            omsUserModelList.addAll(dataList);
        })).sheet(0).doRead();

        List<CompanyModel> companyModelList = new ArrayList<>();
        EasyExcel.read(companyPath, CompanyModel.class, new PageReadListener<CompanyModel>(dataList -> {
            companyModelList.addAll(dataList);
        })).sheet(0).doRead();
        Map<Long, String> companyMap = companyModelList.stream().collect(Collectors.toMap(CompanyModel::getId, model -> model.getCompanyName()));

        List<String> accountList = omsUserModelList.stream().map(OmsUserModel::getAccount).collect(Collectors.toList());
        Map<String, List<OmsUserModel>> omsPhoneMap = omsUserModelList.stream().filter(model -> StrUtil.isNotEmpty(model.getPhone())).collect(Collectors.groupingBy(OmsUserModel::getPhone));

        List<PayoutUserModel> resultList = new ArrayList<>();
        for(PayoutUserModel model : payoutUserModelList) {
            PayoutUserModel result = new PayoutUserModel();
            BeanUtils.copyProperties(model, result);
            String account = model.getAccount();
            Long companyId = model.getCompanyId();
            List<OmsUserReasonVO> voList = new ArrayList<>();
            if(accountList.contains(account)) {
                voList.add(new OmsUserReasonVO(account, "该账号和oms账号account冲突", ""));
            }
            if(omsPhoneMap.containsKey(account)) {
                List<OmsUserModel> omsUserModels = omsPhoneMap.get(account);
                if(!CollectionUtils.isEmpty(omsUserModels)) {
                    for(OmsUserModel userModel : omsUserModels) {
                        String desc = "公司id相同";
                        if (!userModel.getCompanyId().equals(companyId)) {
                            desc = "公司id不同";
                        }
                        voList.add(new OmsUserReasonVO(userModel.getAccount(), "该账号和oms账号电话号码冲突", desc));
                    }
                }
            }
            String companyName = companyMap.getOrDefault(result.getCompanyId(), "");
            result.setCompanyName(companyName);
            if(!CollectionUtils.isEmpty(voList)) {
                String reason = JSONObject.toJSONString(voList);
                result.setOmsAccountReason(reason);
                resultList.add(result);
            }
        }

        EasyExcel.write(resultPath, PayoutUserModel.class).sheet("数据").doWrite(resultList);
    }
}
