package com.zxcode.generatecode.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.zaxxer.hikari.HikariDataSource;
import com.zx.util.util.DynamicVO;
import com.zxcode.generatecode.constants.Constants;
import com.zxcode.generatecode.controller.vo.GenerateConfigVO;
import com.zxcode.generatecode.controller.vo.TableRequestVO;
import com.zxcode.generatecode.service.GenerateCodeService;
import com.zxcode.generatecode.utils.CodeGenerateUtil;
import com.zxcode.generatecode.utils.DataBaseUtil;
import com.zxcode.generatecode.vo.PageVO;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * @author zhaoxu
 */
@Service
public class GenerateCodeServiceImpl implements GenerateCodeService {

    public static final String COUNT_SQL_TEMPLATE = "select count(1) from (%s)tmp";

    public static final String PAGE_SQL_TEMPLATE = "limit ? offset ? ";

    /**
     * 分页查询表信息
     *
     * @param request 请求参数
     * @return 表名分页信息
     */
    @Override
    @SneakyThrows
    public PageVO<Entity> listTables(TableRequestVO request) {
        DynamicVO dynamicVO = DynamicVO.parseMap(this.queryTable(request));
        List<Entity> tables = dynamicVO.getToObject("tables");
        BigDecimal count = dynamicVO.getToObject("tablesCount");
        PageVO<Entity> pageResult = new PageVO<>(count.longValue(), request.getCurrentPage() - 1, request.getPageSize(), tables);
        return pageResult;
    }

    /**
     * 生成代码
     *
     * @param generateConfigVO 生成配置
     * @return 代码压缩文件
     */
    @Override
    public byte[] generatorCode(GenerateConfigVO generateConfigVO) throws SQLException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

        //查询表信息
        List<Entity> tables = null;
        try {
            tables = DynamicVO.parseMap(queryTable(generateConfigVO.getRequest())).getToObject("tables");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //查询列信息
        List<Entity> columns = queryColumns(generateConfigVO.getRequest());
        //生成代码
        CodeGenerateUtil.generatorCode(generateConfigVO, tables.get(0), columns, zip);
        IoUtil.close(zip);
        return outputStream.toByteArray();
    }

    public Map<String, Object> queryTable(TableRequestVO request) throws SQLException {
        //查找参数
        List<Object> params = new ArrayList<>();
        List<Object> countParams = new ArrayList<>();

        String paramSql = "";
        Map<String, Object> result = new HashMap<>(2);

        String schemaName = request.getUrl().split("/")[1].split("\\?")[0];

        //创建datasource
        HikariDataSource dataSource = DataBaseUtil.createDataSource(request);
        Db db = new Db(dataSource);
        //分页条件
        Integer currentPage = request.getCurrentPage();
        Integer pageSize = request.getPageSize();

        //过滤条件
        if (StrUtil.isNotBlank(request.getTablename())) {
            paramSql = "and tableName='" + request.getTablename() + "'";
        }
        String queryTableSql = null;

        String dataBaseType = request.getPrepend().split(":")[1];
        switch (dataBaseType) {
            //Mysql
            case Constants.MYSQL:
                queryTableSql = "select * from (select table_name tableName, engine, table_comment tableComment, create_time createTime from information_schema.tables " +
                        "where table_schema = ?)as A where 1=1 ";
                params.add(schemaName);
                countParams.add(schemaName);
                break;

            //Postgresql
            case Constants.POSTGRESQL:
                queryTableSql = "select * from (SELECT relname tableName, cast( obj_description ( relfilenode, 'pg_class' ) AS VARCHAR ) AS tableComment FROM pg_class c  WHERE" +
                        " relkind = 'r' AND relname NOT LIKE 'pg_%' AND relname NOT LIKE 'sql_%' )as A where 1=1 ";
                break;
            default:
                break;
        }

        String sql = queryTableSql + paramSql;
        String countSql = String.format(COUNT_SQL_TEMPLATE, sql);
        List<Entity> query;
        BigDecimal count;

        //查询数据库表
        if (currentPage != null && pageSize != null) {
            params.add(pageSize);
            params.add((currentPage - 1) * pageSize);
            sql += PAGE_SQL_TEMPLATE;
        }
        query = db.query(sql, params.toArray());
        count = (BigDecimal) db.queryNumber(countSql, countParams.toArray());
        result.put("tables", query);
        result.put("tablesCount", count);
        dataSource.close();
        return result;
    }

    public List<Entity> queryColumns(TableRequestVO request) throws SQLException {
        HikariDataSource dataSource = DataBaseUtil.createDataSource(request);
        Db db = new Db(dataSource);

        //数据库名字
        String schemaName = request.getUrl().split("/")[1].split("\\?")[0];

        //表的名字
        String tableName = request.getTablename();

        List<Object> params = new ArrayList<>();
        //数据库类型
        String dataBaseType = request.getPrepend().split(":")[1];
        String queryColumnSql = null;
        switch (dataBaseType) {
            //Mysql
            case Constants.MYSQL:
                queryColumnSql = "select\n" +
                        "\tcolumn_name columnName,\n" +
                        "\tdata_type datatype,\n" +
                        "\tcolumn_comment columnComment,\n" +
                        "\tcolumn_key columnKey,\n" +
                        "\textra\n" +
                        "from\n" +
                        "\tinformation_schema.columns\n" +
                        "where\n" +
                        "\ttable_name = ?\n" +
                        "\tand table_schema = ?";
                params.add(tableName);
                params.add(schemaName);
                break;

            //Postgresql
            case Constants.POSTGRESQL:
                queryColumnSql = "select\n" +
                        "\ta.attname columnName,\t\n" +
                        "\tconcat_ws('', t.typname, SUBSTRING(format_type(a.atttypid, a.atttypmod) from '\\(.*\\)')) as dataType,\n" +
                        "\td.description columnComment,\n" +
                        "\tcase\n" +
                        "\t\twhen a.attname=f.attname then 'PRI'\n" +
                        "\t\telse ''\n" +
                        "\tend as columnKey\n" +
                        "from\n" +
                        "\tpg_class c,\n" +
                        "\tpg_attribute a,\n" +
                        "\tpg_type t,\n" +
                        "\tpg_description d\n" +
                        "left join (\n" +
                        "\tselect\n" +
                        "\t\tpg_attribute.attname\n" +
                        "\tfrom\n" +
                        "\t\tpg_index,\n" +
                        "\t\tpg_class,\n" +
                        "\t\tpg_attribute\n" +
                        "\twhere\n" +
                        "\t\tpg_class.oid = ? :: regclass\n" +
                        "\t\tand pg_index.indrelid = pg_class.oid\n" +
                        "\t\tand pg_attribute.attrelid = pg_class.oid\n" +
                        "\t\tand pg_attribute.attnum = any (pg_index.indkey)\n" +
                        ") f on\n" +
                        "\t1=1\n" +
                        "where\n" +
                        "\tc.relname = ?\n" +
                        "\tand a.attnum>0\n" +
                        "\tand a.attrelid = c.oid\n" +
                        "\tand a.atttypid = t.oid\n" +
                        "\tand d.objoid = a.attrelid\n" +
                        "\tand d.objsubid = a.attnum";
                params.add(tableName);
                params.add(tableName);
                break;
            default:
                break;
        }

        List<Entity> query = db.query(queryColumnSql, params.toArray());

        dataSource.close();
        return query;
    }

}
