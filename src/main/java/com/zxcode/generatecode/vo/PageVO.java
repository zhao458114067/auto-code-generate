package com.zxcode.generatecode.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author zhaoxu
 */
@Data
@AllArgsConstructor
public class PageVO<T> {
    /**
     * 总条数
     */
    private Long total;

    /**
     * 页码
     */
    private Integer pageNumber;

    /**
     * 每页结果数
     */
    private Integer pageSize;

    /**
     * 结果集
     */
    private List<T> list;
}
