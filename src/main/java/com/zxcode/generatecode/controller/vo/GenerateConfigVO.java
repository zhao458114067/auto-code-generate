package com.zxcode.generatecode.controller.vo;

import lombok.Data;

/**
 * @author : zhaoxu
 */
@Data
public class GenerateConfigVO {
    /**
     * 请求参数
     */
    private TableRequestVO request;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 作者
     */
    private String author;
    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 表前缀
     */
    private String tablePrefix;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 表备注
     */
    private String comments;
}

