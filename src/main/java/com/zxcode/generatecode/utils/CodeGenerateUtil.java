package com.zxcode.generatecode.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Entity;
import cn.hutool.setting.dialect.Props;
import com.google.common.collect.Lists;
import com.zx.util.util.FileUtil;
import com.zxcode.generatecode.constants.Constants;
import com.zxcode.generatecode.controller.vo.GenerateConfigVO;
import com.zxcode.generatecode.controller.vo.TableRequestVO;
import com.zxcode.generatecode.entity.ColumnEntity;
import com.zxcode.generatecode.entity.TableEntity;
import com.zxcode.generatecode.service.impl.GenerateCodeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author zhaoxu
 */
@Slf4j
public class CodeGenerateUtil {

    private final static String ENTITY_JAVA_VM = "Entity";
    private final static String CONFIG_JAVA_VM = "Config";
    private final static String REPOSITORY_JAVA_VM = "Repository";
    private final static String CONTROLLER_JAVA_VM = "Controller";
    private final static String API_JS_VM = "api.ts.vm";
    private final static Pattern PATTERN = Pattern.compile("[^\\\\].*" );

    /**
     * 开始自动生成代码
     *
     * @param prepend          数据库驱动
     * @param url              地址
     * @param tableName        表名字
     * @param dataBaseUserName 数据库用户名
     * @param password         数据库密码
     * @param author           作者
     * @throws SQLException
     */
    public static void startAutoGenerateCode(String prepend, String url, String tableName, String dataBaseUserName,
                                             String password, String author, String moduleName, String packageName, String mainPath) throws SQLException {
        GenerateConfigVO generateConfigVO = new GenerateConfigVO();
        TableRequestVO tableRequestVO = new TableRequestVO();
        tableRequestVO.setPassword(password);
        tableRequestVO.setUsername(dataBaseUserName);
        tableRequestVO.setPrepend(prepend);
        tableRequestVO.setTablename(tableName);
        tableRequestVO.setUrl(url);
        generateConfigVO.setAuthor(author);
        generateConfigVO.setTableName(tableName);
        generateConfigVO.setRequest(tableRequestVO);
        generateConfigVO.setModuleName(moduleName);
        generateConfigVO.setPackageName(packageName);
        generateConfigVO.setMainPath(mainPath);
        new GenerateCodeServiceImpl().generatorCode(generateConfigVO);
    }

    public List<String> getTemplates() {
        return FileUtil.listFile(getResourcesFile("template" )).stream().map(item -> item.getName()).collect(Collectors.toList());
    }

    /**
     * 生成代码
     */
    public void generatorCode(GenerateConfigVO generateConfigVO, Entity table, List<Entity> columns, ZipOutputStream zip) {
        //配置信息
        Props propsDb2Java = new Props();
        Props propssDb2Jdbc = new Props();

        try {
            propsDb2Java.load(new ByteArrayInputStream(Constants.GENERATOR_STRING.getBytes()));
            propssDb2Jdbc.load(new ByteArrayInputStream(Constants.JDBC_TYPE_STRING.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean hasBigDecimal = false;
        //表信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.getStr("tableName" ));

        if (StrUtil.isNotBlank(generateConfigVO.getComments())) {
            tableEntity.setComments(generateConfigVO.getComments());
        } else {
            tableEntity.setComments(table.getStr("tableComment" ));
        }

        String tablePrefix;
        if (StrUtil.isNotBlank(generateConfigVO.getTablePrefix())) {
            tablePrefix = generateConfigVO.getTablePrefix();
        } else {
            tablePrefix = propsDb2Java.getStr("tablePrefix" );
        }

        //表名转换成Java类名
        String className = tableToJava(tableEntity.getTableName(), tablePrefix);
        tableEntity.setCaseClassName(className);
        tableEntity.setLowerClassName(StrUtil.lowerFirst(className));

        //列信息
        List<ColumnEntity> columnList = Lists.newArrayList();
        String jdbcType = null;
        for (Entity column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.getStr("columnName" ));
            columnEntity.setDataType(column.getStr("dataType" ));
            columnEntity.setComments(column.getStr("columnComment" ));
            columnEntity.setExtra(column.getStr("extra" ));
            columnEntity.setNullAbled(column.getStr("nullAbled" ));

            //列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setCaseAttrName(attrName);
            columnEntity.setLowerAttrName(StrUtil.lowerFirst(attrName));

            //列的数据类型，转换成Java类型
            String attrType = columnEntity.getDataType().contains("varchar" ) ||
                    columnEntity.getDataType().contains("character" ) ?
                    "String" : propsDb2Java.getStr(columnEntity.getDataType(), "unknownType" );

            columnEntity.setAttrType(attrType);
            jdbcType = columnEntity.getDataType();
            columnEntity.setJdbcType(jdbcType);
            if (!hasBigDecimal && "BigDecimal".equals(attrType)) {
                hasBigDecimal = true;
            }
            //是否主键
            if ("PRI".equalsIgnoreCase(column.getStr("columnKey" )) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }

            columnList.add(columnEntity);
        }
        tableEntity.setColumns(columnList);

        //没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );
        Velocity.init(prop);
        //封装模板数据
        Map<String, Object> map = new HashMap<>(16);
        map.put("tableName", tableEntity.getTableName());
        map.put("pk", tableEntity.getPk());
        map.put("className", tableEntity.getCaseClassName());
        map.put("classname", tableEntity.getLowerClassName());
        map.put("lowerAttrName", tableEntity.getLowerClassName());
        map.put("pathName", tableEntity.getLowerClassName().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("datetime", DateUtil.now());
        map.put("year", DateUtil.year(new Date()));

        if (StrUtil.isNotBlank(generateConfigVO.getComments())) {
            map.put("comments", generateConfigVO.getComments());
        } else {
            map.put("comments", tableEntity.getComments());
        }

        if (StrUtil.isNotBlank(generateConfigVO.getAuthor())) {
            map.put("author", generateConfigVO.getAuthor());
        } else {
            map.put("author", propsDb2Java.getStr("author" ));
        }

        if (StrUtil.isNotBlank(generateConfigVO.getModuleName())) {
            map.put("moduleName", generateConfigVO.getModuleName());
        } else {
            map.put("moduleName", propsDb2Java.getStr("moduleName" ));
        }

        if (StrUtil.isNotBlank(generateConfigVO.getPackageName())) {
            map.put("package", generateConfigVO.getPackageName());
        } else {
            map.put("package", propsDb2Java.getStr("package" ));
        }

        if (StrUtil.isNotBlank(generateConfigVO.getMainPath())) {
            map.put("mainPath", generateConfigVO.getMainPath());
        } else {
            map.put("mainPath", propsDb2Java.getStr("mainPath" ));
        }
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate("template/" + template, CharsetUtil.UTF_8);
            tpl.merge(context, sw);
            String fileName = getFileName(template, tableEntity.getCaseClassName(), map.get("package" ).toString(), map.get("moduleName" ).toString());
            try {
                //添加到zip
                Matcher matcher = PATTERN.matcher(fileName);
                String writePath = "";
                if (matcher.find()) {
                    writePath = matcher.group();
                    File file = new File(writePath);
                    if (!file.exists()) {
                        FileUtil.createFiles(writePath);
                        FileUtil.write(file, sw.toString(), "UTF-8" );
                    }
                }

                zip.putNextEntry(new ZipEntry(Objects.requireNonNull(fileName)));
                IoUtil.write(zip, StandardCharsets.UTF_8, false, sw.toString());
                IoUtil.close(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException("渲染模板失败，表名：" + tableEntity.getTableName(), e);
            }
        }
    }


    /**
     * 列名转换成Java属性名
     */
    private String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "" );
    }

    /**
     * 表名转换成Java类名
     */
    private String tableToJava(String tableName, String tablePrefix) {
        if (StrUtil.isNotBlank(tablePrefix)) {
            tableName = tableName.replaceFirst(tablePrefix, "" );
        }
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     */
    private Props getConfig(String fileName) {
        Props props = new Props(getResourcesFile(fileName).getPath());
        props.autoLoad(true);
        return props;
    }

    public static File getResourcesFile(String fileName) {
        try {
            File file = new File(new URI(CodeGenerateUtil.class.getResource("/" ) + "/" + fileName));
            return file;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件名
     */
    private String getFileName(String template, String className, String packageName, String moduleName) {
        // 包路径
        String packagePath = Constants.SIGNATURE + File.separator + "src" + File.separator + "main" + File.separator + "java" + File.separator;
        // 资源路径
        String resourcePath = Constants.SIGNATURE + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;
        // api路径
        String apiPath = Constants.SIGNATURE + "api" + File.separator;

        if (StrUtil.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + moduleName + File.separator;
        }

        if (template.contains(ENTITY_JAVA_VM)) {
            return packagePath + "entity" + File.separator + className + "Entity.java";
        }

        if (template.contains(CONFIG_JAVA_VM)) {
            return packagePath + "config" + File.separator + template.split("\\." )[0] + ".java";
        }

        if (template.contains(REPOSITORY_JAVA_VM)) {
            return packagePath + "repository" + File.separator + className + "Repository.java";
        }

        if (template.contains(CONTROLLER_JAVA_VM)) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains(API_JS_VM)) {
            return packagePath + apiPath + className.toLowerCase() + ".ts";
        }

        return null;
    }
}
