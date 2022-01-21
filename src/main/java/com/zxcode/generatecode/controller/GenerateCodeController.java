package com.zxcode.generatecode.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.db.Entity;
import com.zxcode.generatecode.controller.vo.GenerateConfigVO;
import com.zxcode.generatecode.vo.PageVO;
import com.zxcode.generatecode.controller.vo.TableRequestVO;
import com.zxcode.generatecode.service.GenerateCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author zhaoxu
 */
@RestController
@RequestMapping("/generator")
public class GenerateCodeController {

    @Autowired
    private GenerateCodeService codeGenService;

    /**
     * 列表
     *
     * @param request 参数集
     * @return 数据库表
     */
    @GetMapping("/table")
    public PageVO<Entity> listTables(TableRequestVO request) {
        return codeGenService.listTables(request);
    }

    /**
     * 生成代码
     */
    @PostMapping("")
    public void generatorCode(@RequestBody GenerateConfigVO generateConfigVO, HttpServletResponse response) throws IOException, SQLException {
        byte[] data = codeGenService.generatorCode(generateConfigVO);

        response.reset();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s.zip", generateConfigVO.getTableName()));
        response.addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length));
        response.setContentType("application/octet-stream; charset=UTF-8");

        IoUtil.write(response.getOutputStream(), Boolean.TRUE, data);
    }
}
