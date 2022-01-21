package com.zxcode.generatecode.utils;

import com.zxcode.generatecode.controller.vo.TableRequestVO;
import com.zaxxer.hikari.HikariDataSource;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhaoxu
 */
@Slf4j
@UtilityClass
public class DataBaseUtil {
    public HikariDataSource createDataSource(TableRequestVO request) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(request.getPrepend() + request.getUrl());
        dataSource.setUsername(request.getUsername());
        dataSource.setPassword(request.getPassword());
        return dataSource;
    }
}
