

# 1、效果

**方便看效果，新建一个全新的工程：**

 ![在这里插入图片描述](https://img-blog.csdnimg.cn/17f8b7949e49456e9a02147a3b7c97ba.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5pet5petMTk5OA==,size_20,color_FFFFFF,t_70,g_se,x_16)


**引入依赖：** 
![在这里插入图片描述](https://img-blog.csdnimg.cn/fae046f0954b443095f380d281c2a65e.png)

 **然后编写一个可执行的main方法，指定数据库驱动以及数据库地址等配置信息点击执行：**
![在这里插入图片描述](https://img-blog.csdnimg.cn/c039a5cf99c94e28ab5865d24ef2dbcb.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5pet5petMTk5OA==,size_20,color_FFFFFF,t_70,g_se,x_16)

 

```bash
CodeGenerateUtil.startAutoGenerateCode("jdbc:postgresql://", "localhost:5432/oms_tank_info", "tank_info",
        "postgres", "123456", "zhaoxu", "tankInfomodule", "com.supcon.mare",
        "oms");
```


**执行之后看到多了一些文件，这些就是自动编写好的代码，包括前端的ts请求文件，以及自动生成的接口**
![在这里插入图片描述](https://img-blog.csdnimg.cn/e97f753286104203a0f7d5e1bc2c4fae.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5pet5petMTk5OA==,size_20,color_FFFFFF,t_70,g_se,x_16)

 

**如果想生成某个数据库下所有表（表最少需要有一个字段）：**

```bash
static String userName = "root";
static String password = "123456";
static String url = "localhost:3306/nacos?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";
static String prepend = "jdbc:mysql://";
static String tableName = "";
static String auther = "zhaoxu";
static String moduleName = "testmodule";
static String packageName = "com.supcon.mare";
static String mainPath = "oms";

public static void main(String[] args) throws SQLException, NoSuchFieldException, IllegalAccessException {
    //设置请求参数
    TableRequestVO tableRequestVO = new TableRequestVO();
    tableRequestVO.setUsername(userName);
    tableRequestVO.setUrl(url);
    tableRequestVO.setPrepend(prepend);
    tableRequestVO.setPassword(password);
    tableRequestVO.setTablename(tableName);
    tableRequestVO.setCurrentPage(1);
    DynamicVO dynamicVO = DynamicVO.parseMap(new GenerateCodeServiceImpl().queryTable(tableRequestVO));
    //查询对应数据库下所有的表
    List<Entity> tables = dynamicVO.getToObject("tables" );
    tables.forEach(item -> {
        try {
            //生成所有表
            CodeGenerateUtil.startAutoGenerateCode(prepend, url, item.getStr("tableName" ),
                    userName, password, auther, moduleName, packageName, mainPath);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    });
}
```

 **这是naccos数据库下所有的表**
![在这里插入图片描述](https://img-blog.csdnimg.cn/5582f9aca57244d881bb715abeeda7ba.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5pet5petMTk5OA==,size_20,color_FFFFFF,t_70,g_se,x_16)

 

**然后使用springboot启动起来，还会有许多自动生成的接口比如查users表所有信息：**
![在这里插入图片描述](https://img-blog.csdnimg.cn/ed9ba10fa0fe47b3b255e4d94fa82387.png)

 

# 2、自动生成的接口示例

## 2.1 通过属性条件组合查询

```bash
http://localhost:18276/oms/tankInfo/findAll?tankName=T8&tankCode=5401&tankType=2&tankArea.id=707
```

**可作为条件查询的属性为实体类的所有属性字段，默认所有字符串为模糊查询，可使用excludeAttr采用精确查询**
 
![在这里插入图片描述](https://img-blog.csdnimg.cn/51521bdb59984f8ab26f3b365eeb4eed.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5pet5petMTk5OA==,size_20,color_FFFFFF,t_70,g_se,x_16)
**也有排序功能，比如传参数sorter={'tankAreaId':'desc'}，等号后需要进行ecodingurl转换**

## 2.2新增

**post请求，默认寻找valid属性，没有valid属性跳过保存**
 ![在这里插入图片描述](https://img-blog.csdnimg.cn/46c0fd21e36b437c87baa1d5670358a6.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5pet5petMTk5OA==,size_20,color_FFFFFF,t_70,g_se,x_16)

## 2.3通过valid属性假删

**http://localhost:18276/oms/tankInfo/80182**

**delete请求，80182为主键id，多个用逗号隔开**

## 2.4修改

**http://localhost:18276/oms/tankInfo**

**和新增一样，put请求**

## 2.5 通过属性查询某一个实体信息

**http://localhost:18276/oms/tankInfo/tankName/T8105
可作为条件查询的属性为实体类的所有属性字段**

## 2.6分页查询

**http://localhost:18276/oms/tankInfo/findByPage?current=1&pageSize=2&tankName=T8&tankCode=5401&tankType=2&tankArea.id=707**

 ![在这里插入图片描述](https://img-blog.csdnimg.cn/d7379b8ef728410da25ff3145b88e302.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5pet5petMTk5OA==,size_20,color_FFFFFF,t_70,g_se,x_16)



**可作为条件查询的属性为实体类的所有属性字段，默认所有字符串为模糊查询，可使用excludeAttr采用精确查询**

**也有排序功能，比如传参数sorter={'tankAreaId':'desc'}，等号后需要进行ecodingurl转换**

## 2.6通过某个属性值查询实体，返回为列表

**http://localhost:18276/oms/tankInfo/list/tankName/T8201**

**查询多个条件值用逗号隔开，属性加conditionType=list**

**http://localhost:18276/oms/tankInfo/list/tankName/T8201,T8202?conditionType=list**
![在这里插入图片描述](https://img-blog.csdnimg.cn/67b5a941e69e4ae4b8118766b7a0a127.png?x-oss-process=image/watermark,type_d3F5LXplbmhlaQ,shadow_50,text_Q1NETiBA5pet5petMTk5OA==,size_20,color_FFFFFF,t_70,g_se,x_16)

 

