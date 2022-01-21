package com.zxcode.generatecode.entity;

import lombok.Data;

/**
 * @author zhaoxu
 */
@Data
public class ColumnEntity {
    /**
     * 列表
     */
    private String columnName;
    /**
     * 数据类型
     */
    private String dataType;
    /**
     * 备注
     */
    private String comments;
    /**
     * 驼峰属性
     */
    private String caseAttrName;
    /**
     * 普通属性
     */
    private String lowerAttrName;
    /**
     * 属性类型
     */
    private String attrType;
    /**
     * jdbc类型
     */
    private String jdbcType;
    /**
     * 其他信息
     */
    private String extra;
}
