package com.lngstart.easyexcel.work;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.lngstart.easyexcel.model.UserRelation;

import javax.swing.filechooser.FileSystemView;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lngstart
 * @date 2023/7/5
 */
public class UserRelationWork {

    public static String basePath = "";
    public static String filePath = "//payout_user_test";
    public static Long payoutRole = 44L;
    static {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        File homeDirectory = fileSystemView.getHomeDirectory();
        basePath = homeDirectory.getPath();
    }

    /**
     * 小额用户和oms迁移用户关系记录表
     * @param args
     */
    public static void main(String[] args) throws Exception{
        String userRelationPath = basePath + filePath + "//relation.xlsx";
        String resultPath = basePath + filePath + "//执行sql.md";

        List<UserRelation> userRelationList = new ArrayList<>();
        EasyExcel.read(userRelationPath, UserRelation.class, new PageReadListener<UserRelation>(dataList -> {
            userRelationList.addAll(dataList);
        })).sheet(0).doRead();

        StringBuilder sb = new StringBuilder();

        // 拼接sql
        // 小额用户和oms用户映射关系表
        String sqlRelation = "insert into mishu_quick_payout.oms_payout_user_relation (oms_user_id, payout_user_id) values ";
        List<String> sqlRelationList = new ArrayList<>();
        for(UserRelation user : userRelationList) {
            String sql = "(" + user.getOmsUser() + ", " + user.getPayoutUser() + ")";
            sqlRelationList.add(sql);
        }
        String insertSql = sqlRelationList.stream().collect(Collectors.joining(",\n"));
        sqlRelation += insertSql + ";";

        System.out.println("-- oms和小额用户关系映射");
        System.out.println(sqlRelation);
        sb.append("-- oms和小额用户关系映射\n" + sqlRelation + "\n");

        // 用户添加添加小额权限
        List<String> omsUserSqlList = new ArrayList<>();
        String sqlTemplate = "insert into public.tbl_user_permission (user_id, permission_id) values ";
        String template = "(%d, %d)";
        for(UserRelation user : userRelationList) {
            String sql = String.format(template, user.getOmsUser(), payoutRole);
            omsUserSqlList.add(sql);
        }
        String omsUserSql = omsUserSqlList.stream().collect(Collectors.joining(",\n"));
        omsUserSql = sqlTemplate + omsUserSql + ";";
        System.out.println("-- 用户添加小额权限");
        System.out.println(omsUserSql);
        sb.append("-- 用户添加小额权限\n" + omsUserSql + "\n");

        // 兼容用户协议书sql
        String signSql = "insert into mishu_quick_payout.tbl_user_attribute (user_id, sign_name_url) values ";
        String signSqlTemplate = "(%d, '%s')";
        List<String> signSqlList = new ArrayList<>();
        for(UserRelation user : userRelationList) {
            String signNameUrl = user.getSignNameUrl();
            if(StrUtil.isEmpty(signNameUrl)) continue;
            String sql = String.format(signSqlTemplate, user.getOmsUser(), user.getSignNameUrl());
            signSqlList.add(sql);
        }
        String signSqlValues = signSqlList.stream().collect(Collectors.joining(",\n"));
        signSql += signSqlValues + ";";
        System.out.println("-- 用户协议书表迁移sql");
        System.out.println(signSql);
        sb.append("-- 用户协议书表迁移sql\n" + signSql + "\n");


        List<UserRelation> userRelations = userRelationList.stream().filter(user -> "是".equals(user.getIsOms())).collect(Collectors.toList());
        userRelationList.removeAll(userRelations);

        // 小额用户添加到oms的sql
        String sqlAddOmsTemplate = "insert into public.tbl_user (id, name, account, company_id, password, phone, deleted, is_car_insurance) values ";
        String sqlOmsTemplate = "(%d, '%s', '%s', %d, '%s', '%s', %d, %d)";
        List<String> sqlAddOmsList = new ArrayList<>();
        for(UserRelation user : userRelationList) {
            String sql = String.format(sqlOmsTemplate, user.getOmsUser(), user.getName(), user.getAccount(),
                    user.getCompanyId(), user.getPassword(), user.getPhone(), user.getDelete(), 2);
            sqlAddOmsList.add(sql);
        }
        String collect = sqlAddOmsList.stream().collect(Collectors.joining(",\n"));
        sqlAddOmsTemplate += collect + ";";

        System.out.println("-- 未冲突小额用户导入oms");
        System.out.println(sqlAddOmsTemplate);
        sb.append("-- 未冲突小额用户导入oms\n" + sqlAddOmsTemplate + "\n");

        // 新导入小额用户赋权
        List<Long> permissionList = List.of(2L, 3L, 4L, 5L, 6L, 12L);
        sqlTemplate = "insert into public.tbl_user_permission (user_id, permission_id) values ";
        template = "(%d, %d)";
        List<String> permissionSqlList = new ArrayList<>();
        for(UserRelation user : userRelationList) {
            for(Long id : permissionList) {
                String sql = String.format(template, user.getOmsUser(), id);
                permissionSqlList.add(sql);
            }
        }
        String collect1 = permissionSqlList.stream().collect(Collectors.joining(",\n"));
        sqlTemplate += collect1 + ";";
        System.out.println("-- 新增用户数据权限添加");
        System.out.println(sqlTemplate);
        sb.append("-- 新增用户数据权限添加\n" + sqlTemplate + "\n");

        String result = sb.toString();
        byte[] bytes = result.getBytes(StandardCharsets.UTF_8);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             FileOutputStream fos = new FileOutputStream(resultPath)) {
            byte[] buff = new byte[2048];
            int len = -1;
            while((len = bais.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
        }

    }

}
