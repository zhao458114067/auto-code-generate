package com.zxcode.generatecode.constants;

import jdk.nashorn.internal.scripts.JS;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaoxu
 */
public interface Constants {
    /**
     * 签名
     */
    String SIGNATURE = "";

    /**
     * mysql
     */
    String MYSQL = "mysql";

    /**
     * postgresql
     */
    String POSTGRESQL = "postgresql";

    /**
     * 生成java类
     */
    String GENERATOR_STRING = "#\\u4EE3\\u7801\\u751F\\u6210\\u5668\\uFF0C\\u914D\\u7F6E\\u4FE1\\u606F\n" +
            "mainPath=\n" +
            "#\\u5305\\u540D\n" +
            "package=com.supcon.mare\n" +
            "moduleName=tankinfo\n" +
            "#\\u4F5C\\u8005\n" +
            "author=zhaoxu\n" +
            "#\\u8868\\u524D\\u7F00(\\u7C7B\\u540D\\u4E0D\\u4F1A\\u5305\\u542B\\u8868\\u524D\\u7F00)\n" +
            "tablePrefix=tb_\n" +
            "#\\u7C7B\\u578B\\u8F6C\\u6362\\uFF0C\\u914D\\u7F6E\\u4FE1\\u606F\n" +
            "tinyint=Integer\n" +
            "smallint=Integer\n" +
            "mediumint=Integer\n" +
            "int=Integer\n" +
            "integer=Integer\n" +
            "bigint=Long\n" +
            "float=Float\n" +
            "double=Double\n" +
            "decimal=BigDecimal\n" +
            "bit=Boolean\n" +
            "char=String\n" +
            "varchar=String\n" +
            "varchar(255)=String\n" +
            "tinytext=String\n" +
            "text=String\n" +
            "mediumtext=String\n" +
            "longtext=String\n" +
            "date=LocalDateTime\n" +
            "datetime=LocalDateTime\n" +
            "character\\u0020varying=String\n" +
            "timestamp=LocalDateTime\n";

    /**
     * 生成jdbc配置
     */
    String JDBC_TYPE_STRING = "tinyint=TINYINT\n" +
            "smallint=SMALLINT\n" +
            "mediumint=MEDIUMINT\n" +
            "int=INTEGER\n" +
            "integer=INTEGER\n" +
            "bigint=BIGINT\n" +
            "float=FLOAT\n" +
            "double=DOUBLE\n" +
            "decimal=DECIMAL\n" +
            "bit=BIT\n" +
            "char=CHAR\n" +
            "varchar=VARCHAR\n" +
            "tinytext=VARCHAR\n" +
            "text=VARCHAR\n" +
            "mediumtext=VARCHAR\n" +
            "longtext=VARCHAR\n" +
            "date=DATE\n" +
            "datetime=DATETIME\n" +
            "timestamp=TIMESTAMP\n" +
            "character\\u0020varying=VARCHAR\n" +
            "blob=BLOB\n" +
            "longblob=LONGBLOB\n";

    HashMap<String, String> GENERATE_FFILE_MAP = new HashMap() {
        {
            put("api.ts.vm", "import request from '@/router/axios'\n" +
                    "\n" +
                    "/**\n" +
                    " * 新增$!{comments}\n" +
                    " * @param obj $!{comments}\n" +
                    " */\n" +
                    "export function add$!{className}($!{lowerAttrName}) {\n" +
                    "    return request({\n" +
                    "        url: '/$!{mainPath}/$!{lowerAttrName}',\n" +
                    "        method: 'post',\n" +
                    "        data: $!{lowerAttrName}\n" +
                    "    })\n" +
                    "}\n" +
                    "\n" +
                    "/**\n" +
                    " * 通过id删除$!{comments}\n" +
                    " * @param ids 主键,多个逗号隔开\n" +
                    " */\n" +
                    "export function del$!{className}(ids) {\n" +
                    "    return request({\n" +
                    "        url: '/$!{mainPath}/$!{lowerAttrName}/' + ids,\n" +
                    "        method: 'delete'\n" +
                    "    })\n" +
                    "}\n" +
                    "\n" +
                    "/**\n" +
                    " * 修改$!{comments}\n" +
                    " * @param obj $!{comments}\n" +
                    " */\n" +
                    "export function put$!{className}($!{lowerAttrName}) {\n" +
                    "    return request({\n" +
                    "        url: '/$!{mainPath}/$!{lowerAttrName}',\n" +
                    "        method: 'put',\n" +
                    "        data: $!{lowerAttrName}\n" +
                    "    })\n" +
                    "}\n" +
                    "\n" +
                    "/**\n" +
                    " * 分页查询$!{comments}\n" +
                    " * @param query 分页查询条件\n" +
                    " */\n" +
                    "export function findByPage(params) {\n" +
                    "    return request({\n" +
                    "        url: '/$!{mainPath}/$!{lowerAttrName}/findByPage',\n" +
                    "        method: 'get',\n" +
                    "        params: params\n" +
                    "    })\n" +
                    "}\n" +
                    "\n" +
                    "/**\n" +
                    " * 全属性条件查询$!{comments}\n" +
                    " * @param id 主键\n" +
                    " */\n" +
                    "export function findAllByConditions(params) {\n" +
                    "    return request({\n" +
                    "        url: '/$!{mainPath}/$!{lowerAttrName}/findAll',\n" +
                    "        method: 'get',\n" +
                    "        params: params\n" +
                    "    })\n" +
                    "}\n" +
                    "\n" +
                    "/**\n" +
                    " * 通过条件查询一个对象$!{comments}\n" +
                    " */\n" +
                    "export function findByAttr(attr, condition) {\n" +
                    "    return request({\n" +
                    "        url: '/$!{mainPath}/$!{lowerAttrName}/' + attr + '/' + condition,\n" +
                    "        method: 'get'\n" +
                    "    })\n" +
                    "}\n" +
                    "\n" +
                    "/**\n" +
                    " * 通过条件查询多个对象$!{comments}\n" +
                    " */\n" +
                    "export function findByAttrs(attr, condition, conditionType) {\n" +
                    "    return request({\n" +
                    "        url: '/$!{mainPath}/$!{lowerAttrName}/list/' + attr + '/' + condition + '?conditionType=' + conditionType,\n" +
                    "        method: 'get'\n" +
                    "    })\n" +
                    "}\n" +
                    "\n" );
            put("BaseJpaRepositoryConfig.java.vm", "package $!{package}.$!{moduleName}.config;\n" +
                    "\n" +
                    "import com.zx.util.factory.BaseJpaRepositoryFactoryBean;\n" +
                    "import com.zx.util.mvc.BaseRequestMappingHandlerMapping;\n" +
                    "import org.springframework.context.annotation.Bean;\n" +
                    "import org.springframework.context.annotation.Configuration;\n" +
                    "import org.springframework.data.jpa.repository.config.EnableJpaRepositories;\n" +
                    "import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;\n" +
                    "import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;\n" +
                    "\n" +
                    "@Configuration\n" +
                    "/**\n" +
                    " * @author : zhaoxu\n" +
                    " * 这个是自定义repository工厂\n" +
                    " */\n" +
                    "@EnableJpaRepositories(basePackages = {\"$!{package}\"}, repositoryFactoryBeanClass = BaseJpaRepositoryFactoryBean.class)\n" +
                    "public class BaseJpaRepositoryConfig extends WebMvcConfigurationSupport {\n" +
                    "    @Override\n" +
                    "    @Bean\n" +
                    "    public RequestMappingHandlerMapping requestMappingHandlerMapping() {\n" +
                    "        return new BaseRequestMappingHandlerMapping();\n" +
                    "    }\n" +
                    "}" );
            put("Controller.java.vm", "package $!{package}.$!{moduleName}.controller;\n" +
                    "\n" +
                    "import com.zx.util.controller.BaseControllerModel;\n" +
                    "import org.springframework.web.bind.annotation.RequestMapping;\n" +
                    "import org.springframework.web.bind.annotation.RestController;\n" +
                    "import io.swagger.annotations.Api;\n" +
                    "import lombok.extern.slf4j.Slf4j;\n" +
                    "import $!{package}.$!{moduleName}.entity.$!{className}Entity;\n" +
                    "\n" +
                    "/**\n" +
                    " * <p>\n" +
                    " * $!{comments}\n" +
                    " * </p>\n" +
                    " *\n" +
                    " * @author $!{author}\n" +
                    " * @date Created in $!{datetime}\n" +
                    " */\n" +
                    "@Slf4j\n" +
                    "@RestController\n" +
                    "@RequestMapping(\"/$!{mainPath}\")\n" +
                    "@Api(description = \"$!{className}Controller\", tags = {\"$!{comments}\"})\n" +
                    "public class $!{className}Controller extends BaseControllerModel<$!{className}Entity, $!{className}Entity> {\n" +
                    "\n" +
                    "}" );
            put("Entity.java.vm", "package $!{package}.$!{moduleName}.entity;\n" +
                    "\n" +
                    "#if($!{hasBigDecimal})\n" +
                    "import java.math.BigDecimal;\n" +
                    "#end\n" +
                    "import java.time.LocalDateTime;\n" +
                    "import java.io.Serializable;\n" +
                    "import java.util.Date;\n" +
                    "import javax.persistence.*;\n" +
                    "import io.swagger.annotations.ApiModelProperty;\n" +
                    "import org.springframework.data.annotation.CreatedDate;\n" +
                    "import org.springframework.data.annotation.LastModifiedDate;\n" +
                    "import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\n" +
                    "import lombok.Data;\n" +
                    "import lombok.NoArgsConstructor;\n" +
                    "/**\n" +
                    " * <p>\n" +
                    " * $!{comments}\n" +
                    " * </p>\n" +
                    " *\n" +
                    " * @author $!{author}\n" +
                    " * @date Created in $!{datetime}\n" +
                    " */\n" +
                    "@Entity\n" +
                    "@Table(name = \"$!{tableName}\")\n" +
                    "@JsonIgnoreProperties(ignoreUnknown = true)\n" +
                    "@Data\n" +
                    "@NoArgsConstructor\n" +
                    "public class $!{className}Entity implements Serializable {\n" +
                    "    private static final long serialVersionUID = 1L;\n" +
                    "\n" +
                    "  #foreach ($!column in $!columns)\n" +
                    "    /**\n" +
                    "     * $!column.comments\n" +
                    "     */\n" +
                    "    @ApiModelProperty(value = \"$!column.comments\")\n" +
                    "    #if($!column.columnName == $!pk.columnName)\n" +
                    "    @Id\n" +
                    "    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
                    "    private Long $!column.lowerAttrName;\n" +
                    "    #else\n" +
                    "    @Column(name = \"$!{column.columnName}\", nullable = $!{column.nullAbled})\n" +
                    "    private $!column.attrType $!column.lowerAttrName;\n" +
                    "    #end\n" +
                    "\n" +
                    "  #end\n" +
                    "}\n" );
            put("Repository.java.vm", "package $!{package}.$!{moduleName}.repository;\n" +
                    "\n" +
                    "import $!{package}.$!{moduleName}.entity.$!{className}Entity;\n" +
                    "import com.zx.util.service.BaseRepository;\n" +
                    "import org.springframework.stereotype.Repository;\n" +
                    "\n" +
                    "/**\n" +
                    " * <p>\n" +
                    " * $!{comments}\n" +
                    " * </p>\n" +
                    " *\n" +
                    " * @author $!{author}\n" +
                    " * @date Created in $!{datetime}\n" +
                    " */\n" +
                    "@Repository\n" +
                    "public interface $!{className}Repository extends BaseRepository<$!{className}Entity, Long> {\n" +
                    "\n" +
                    "}\n" );
        }
    };
}
