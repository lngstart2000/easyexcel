package com.lngstart.easyexcel.work;

import cn.hutool.core.lang.Pair;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONObject;
import com.lngstart.easyexcel.dto.DemoData;
import com.lngstart.easyexcel.dto.FormData;
import com.lngstart.easyexcel.model.CaseImgTypeModel;
import com.lngstart.easyexcel.model.CaseKeyToValueModel;
import com.lngstart.easyexcel.model.CompanyModel;
import com.lngstart.easyexcel.model.IDToIDModel;
import org.springframework.util.CollectionUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class FormCompanyCheck {

    public static String basePath = "";
    static {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        File homeDirectory = fileSystemView.getHomeDirectory();
        basePath = homeDirectory.getPath();
    }

    public static Map<Long, Long> idMap = new HashMap<>();

    /**
     * 因为迁移的机构中不存在子集，并且迁移机构没有表头配置，所以这些机构迁移不会影响到其他机构，只需要考虑迁移后是否会被其他机构影响本身
     * 故只需要校验这些迁移机构前后表头和公司相关的配置是否变化即可
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        String companyPath = basePath + "\\sql\\tbl_company.xlsx";
        String companyAfterPath = basePath + "\\sql\\tbl_company_after.xlsx";
        String formPath = basePath + "\\sql\\tbl_case_key_to_value.xlsx";
        String idToIdPath = basePath + "\\sql\\20230320人保财机构迁移-v1.xls";
        String imgTypePath = basePath + "\\sql\\tbl_case_img_type.xlsx";
        String resultFilePath = basePath + "\\迁移前后公司表头对比结果.xlsx";

        final List<CaseKeyToValueModel> caseKeyToValueModelList = new ArrayList<>();
        final List<CompanyModel> companyModelList = new ArrayList<>();
        final List<CompanyModel> companyModelAfterList = new ArrayList<>();
        final List<IDToIDModel> idToIDModelList = new ArrayList<>();
        final List<CaseImgTypeModel> caseImgTypeModelList = new ArrayList<>();

        EasyExcel.read(formPath, CaseKeyToValueModel.class, new PageReadListener<CaseKeyToValueModel>(dataList -> {
            caseKeyToValueModelList.addAll(dataList);
        })).sheet(0).doRead();

        EasyExcel.read(companyPath, CompanyModel.class, new PageReadListener<CompanyModel>(dataList -> {
            companyModelList.addAll(dataList);
        })).sheet(0).doRead();

        EasyExcel.read(companyAfterPath, CompanyModel.class, new PageReadListener<CompanyModel>(dataList -> {
            companyModelAfterList.addAll(dataList);
        })).sheet(0).doRead();

        EasyExcel.read(idToIdPath, IDToIDModel.class, new PageReadListener<IDToIDModel>(dataList -> {
            idToIDModelList.addAll(dataList);
        })).sheet(0).doRead();

        for(IDToIDModel model : idToIDModelList) {
            idMap.put(model.getId(), model.getTarget_id());
        }

        EasyExcel.read(imgTypePath, CaseImgTypeModel.class, new PageReadListener<CaseImgTypeModel>(dataList -> {
            caseImgTypeModelList.addAll(dataList);
        })).sheet(0).doRead();

        Map<Long, CaseImgTypeModel> imgTypeMap = caseImgTypeModelList.stream().collect(Collectors.toMap(CaseImgTypeModel::getId, model -> model));
        Map<Long, CompanyModel> companyMap = companyModelList.stream().collect(Collectors.toMap(CompanyModel::getId, model -> model));
        Map<Long, CompanyModel> companyAfterMap = companyModelAfterList.stream().collect(Collectors.toMap(CompanyModel::getId, model -> model));

        // 校验新老机构迁移之后不同的表头
        Map<Long, Map<Long, List<CaseKeyToValueModel>>> companyToKeyMap = getCompanyToKey(caseKeyToValueModelList, companyModelList, 0);
        Map<Long, Map<Long, List<CaseKeyToValueModel>>> companyToKeyAfterMap = getCompanyToKey(caseKeyToValueModelList, companyModelAfterList, 1);

        Map<Long, Map<Long, Pair<String, String>>> companyToKeyFormMap = new HashMap<>();
        Map<Long, Map<Long, Pair<String, String>>> companyToKeyFormAfterMap = new HashMap<>();


        List<FormData> formDataList = new ArrayList<>();
        for(Long id : companyToKeyMap.keySet()) {
            Map<Long, List<CaseKeyToValueModel>> companyKeyMap = companyToKeyMap.get(id);
            Long targetId = idMap.get(id);
            CompanyModel companyModel = companyMap.get(id);
            CompanyModel companyAfterModel = companyAfterMap.get(targetId);
            Map<Long, List<CaseKeyToValueModel>> companyAfterKeyMap = companyToKeyAfterMap.get(targetId);
            for(Long imgTypeId : companyKeyMap.keySet()) {
                List<CaseKeyToValueModel> caseKeyToValueModels = companyKeyMap.get(imgTypeId);
                List<CaseKeyToValueModel> caseKeyToValueModels1 = companyAfterKeyMap.get(imgTypeId);

                FormData formData = new FormData();
                formData.setCompanyId(id);
                formData.setCompanyTargetId(targetId);
                formData.setCompanyName(companyModel.getCompanyName());
                formData.setCompanyTargetName(companyAfterModel.getCompanyName());
                formData.setBillTypeId(imgTypeId);
                formData.setBillType(imgTypeMap.get(imgTypeId).getName());
                String form = caseKeyToValueModels.stream().sorted((a, b) -> a.getKey().compareTo(b.getKey())).map(CaseKeyToValueModel::getKey).collect(Collectors.joining(","));
                String formAfter = caseKeyToValueModels1.stream().sorted((a, b) -> a.getKey().compareTo(b.getKey())).map(CaseKeyToValueModel::getKey).collect(Collectors.joining(","));
                formData.setForm(form);
                formData.setFormTarget(formAfter);
                formData.setIsCheck(form.equals(formAfter) ? "true" : "false");
                formDataList.add(formData);
            }
        }
//        List<FormData> falseFormDataList = formDataList.stream().filter(form -> "false".equals(form.getIsCheck())).collect(Collectors.toList());
//        formDataList.removeAll(falseFormDataList);
        EasyExcel.write(resultFilePath, FormData.class).sheet("匹配").doWrite(formDataList);
//        WriteSheet sheet1 = EasyExcel.writerSheet(0, "迁移后匹配").build();
//        WriteSheet sheet2 = EasyExcel.writerSheet(1, "迁移后不匹配").build();
//        excelWriter.write(formDataList, sheet1);
//        excelWriter.write(falseFormDataList, sheet2);

    }

    public static Map<Long, Map<Long, List<CaseKeyToValueModel>>> getCompanyToKey(final List<CaseKeyToValueModel> caseKeyToValueModelList, List<CompanyModel> companyModelList, int type) {
        List<CaseKeyToValueModel> caseKeyToValueModels = caseKeyToValueModelList.stream().filter(model -> !"F".equals(model.getShowCompanys())).collect(Collectors.toList());
        List<Long> ids = new ArrayList<>();
        if(type == 0) {
            ids = idMap.keySet().stream().collect(Collectors.toList());
        } else {
            ids = idMap.values().stream().collect(Collectors.toList());
        }
        List<Long> finalIds = ids;

        List<CompanyModel> companyModels = companyModelList.stream().filter(model -> finalIds.contains(model.getId())).collect(Collectors.toList());

//        System.out.println("ids ==> " + ids.size() + "===>" + companyModels.size());
        List<CaseKeyToValueModel> showCompanyModelList = new ArrayList<>();
        List<CaseKeyToValueModel> showSpecialCompanyModel = new ArrayList<>();
        for(CaseKeyToValueModel model : caseKeyToValueModels) {
            if(model.getShowCompanys() == null || model.getShowCompanys().equals("T")) {
                showCompanyModelList.add(model);
            } else {
                showSpecialCompanyModel.add(model);
            }
        }

//        List<String> collect = showSpecialCompanyModel.stream().map(CaseKeyToValueModel::getShowCompanys).collect(Collectors.toList());
//        System.out.println(collect);
//        HashSet<Long> set = new HashSet<>();
//        for(String s : collect) {
//            String[] split = s.split(",");
//            for(String ss : split) {
//                set.add(Long.valueOf(ss));
//            }
//        }
//        System.out.println(set.stream().collect(Collectors.toList()));

        Set<String> companyIdSet = showSpecialCompanyModel.stream().map(model -> model.getShowCompanys().split(",")).flatMap(Arrays::stream).collect(Collectors.toSet());
        Map<String, List<Long>> showCompanyIdMap = new HashMap<>();
        for (String cid : companyIdSet) {
            List<CompanyModel> allCompanyById = getCompanyAndChild(List.of(Long.valueOf(cid)), companyModelList);
            List<Long> companyList = showCompanyIdMap.computeIfAbsent(cid, k-> new ArrayList<>());
            if (allCompanyById != null && allCompanyById.size() > 0) {
                companyList.addAll(allCompanyById.stream().map(CompanyModel::getId).collect(Collectors.toList()));
            }
        }

        Map<Long, Map<Long, List<CaseKeyToValueModel>>> result = new HashMap<>();
        for(CompanyModel companyModel : companyModels) {
            Long companyId = companyModel.getId();

            // 判定当前机构配置的表头信息
            List<CaseKeyToValueModel> showFormList = new ArrayList<>(showCompanyModelList);
            for (CaseKeyToValueModel caseKeyToValueModel : showSpecialCompanyModel) {
                String[] split = caseKeyToValueModel.getShowCompanys().split(",");
                for (String cid : split) {
                    if (showCompanyIdMap.getOrDefault(cid, new ArrayList<>()).contains(companyId)){
                        showFormList.add(caseKeyToValueModel);
                    }
                }
            }

            result.put(companyId, showFormList.stream().collect(Collectors.groupingBy(CaseKeyToValueModel::getCaseModelId)));
        }

        return result;
    }

    public static void getChildCompanyAll(List<Long> ids, List<CompanyModel> result, List<CompanyModel> companyModels) {
        if(!CollectionUtils.isEmpty(ids)) {
            List<Long> finalIds = ids;
            List<CompanyModel> companyModelList = companyModels.stream().filter(model -> model.getPId() != null && finalIds.contains(model.getPId())).collect(Collectors.toList());
            result.addAll(companyModelList);
            ids = companyModelList.stream().map(CompanyModel::getId).collect(Collectors.toList());
            getChildCompanyAll(ids, result, companyModels);
        }
        return ;
    }

    public static List<CompanyModel> getCompanyAndChild(List<Long> ids, List<CompanyModel> companyModels) {
        List<CompanyModel> companyModelList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(ids)) {
            companyModelList = companyModels.stream().filter(model -> ids.contains(model.getId())).collect(Collectors.toList());
            getChildCompanyAll(ids, companyModelList, companyModels);
        }
        return companyModelList;
    }


}
