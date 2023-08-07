package com.lngstart.easyexcel.work;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson.JSONObject;
import com.lngstart.easyexcel.dto.OmsUserReasonVO;
import com.lngstart.easyexcel.model.CompanyModel;
import com.lngstart.easyexcel.model.OmsUserModel;
import com.lngstart.easyexcel.model.PayoutUserModel;
import com.lngstart.easyexcel.model.UserRelation;
import com.lngstart.easyexcel.utils.EncryptUtils;
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
    public static String filePath = "//payout_user_test";
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
        String payOutPath = basePath + filePath + "//payout_user.xlsx";
        String omsUserPath = basePath + filePath + "//oms_user.xlsx";
        String resultPath = basePath + filePath + "//result.xlsx";
        String companyPath = basePath + filePath + "//mishu_public_tbl_company.xlsx";
        String userRelationPath = basePath + filePath + "//relation.xlsx";

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
        Map<String, OmsUserModel> userMap = omsUserModelList.stream().collect(Collectors.toMap(OmsUserModel::getAccount, model -> model));

        List<PayoutUserModel> resultList = new ArrayList<>();
        for(PayoutUserModel model : payoutUserModelList) {
            PayoutUserModel result = new PayoutUserModel();
            BeanUtils.copyProperties(model, result);
            String account = model.getAccount();
            Long companyId = model.getCompanyId();
            List<OmsUserReasonVO> voList = new ArrayList<>();
            if(accountList.contains(account)) {
                voList.add(new OmsUserReasonVO(account, "该账号和oms账号account冲突", ""));
                OmsUserModel userModel = userMap.get(account);
                Long id = userModel.getId();
                Long company = userModel.getCompanyId();
                if(company != null) {
                    String companyName = companyMap.getOrDefault(company, "");
                    result.setOmsCompanyName(companyName);
                }
                result.setOmsId(id);
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
                        OmsUserModel userModel1 = userMap.get(userModel.getAccount());
                        Long company = userModel1.getCompanyId();
                        Long id = userModel1.getId();
                        result.setOmsId(id);
                        if(company != null) {
                            String companyName = companyMap.getOrDefault(company, "");
                            result.setOmsCompanyName(companyName);
                        }
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

//        EasyExcel.write(resultPath, PayoutUserModel.class).sheet("数据").doWrite(resultList);

        List<UserRelation> userRelations = new ArrayList<>();
        PayoutUserModel adminUser = null;
        for(PayoutUserModel model : resultList) {
            // 去除小额的admin账号
            String account = model.getAccount();
            if("admin".equals(account)) {
                adminUser = model;
                continue;
            }
            UserRelation relation = new UserRelation();
            Long id = model.getId();
            Long omsId = model.getOmsId();
            relation.setPayoutUser(id);
            relation.setOmsUser(omsId);
            relation.setIsOms("是");
            relation.setDelete(model.getDelete());
            relation.setSignNameUrl(model.getSignNameUrl());
            userRelations.add(relation);
        }

        Long startId = 202307140000L;
        List<Long> isOmsList = resultList.stream().map(PayoutUserModel::getId).collect(Collectors.toList());
        for(PayoutUserModel user : payoutUserModelList) {
            if(isOmsList.contains(user.getId())) continue;

            // 处理小额密码问题
            String password = user.getPassword();
            String omsPassword = EncryptUtils.encryptPassword(password);

            UserRelation relation = new UserRelation();
            relation.setSignNameUrl(user.getSignNameUrl());
            relation.setPayoutUser(user.getId());
            relation.setOmsUser(startId);
            relation.setAccount(user.getAccount());
            relation.setPassword(omsPassword);
            relation.setCompanyId(user.getCompanyId());
            relation.setPhone(user.getAccount());
            relation.setName(user.getUsername());
            relation.setDelete(user.getDelete());
            relation.setIsOms("否");
            startId ++;
            userRelations.add(relation);
        }

        // 小额admin账号给建一个新的
        UserRelation relation = new UserRelation();
        // 处理小额密码问题
        String password = adminUser.getPassword();
        String omsPassword = EncryptUtils.encryptPassword(password);
        relation.setSignNameUrl(adminUser.getSignNameUrl());
        relation.setPayoutUser(adminUser.getId());
        relation.setOmsUser(startId);
        relation.setAccount("payout_admin");
        relation.setPassword(omsPassword);
        relation.setCompanyId(adminUser.getCompanyId());
        relation.setPhone("");
        relation.setName("payout_admin");
        relation.setDelete(adminUser.getDelete());
        relation.setIsOms("否");
        startId ++;
        userRelations.add(relation);

        EasyExcel.write(userRelationPath, UserRelation.class).sheet("数据").doWrite(userRelations);

    }
}
