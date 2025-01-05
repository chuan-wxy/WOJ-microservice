package com.chuan.wojcommon.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * SQL工具
 * @Author: chuan-wxy
 * @Date: 2024/9/9 12:19
 * @Description:
 */
public class SqlUtil {
    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
