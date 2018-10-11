package com.xiaogang.framework.controller;

import com.xiaogang.framework.basic.annotations.Json;
import com.xiaogang.framework.basic.annotations.ResponseJson;
import com.xiaogang.framework.domain.QueryRequestVo;
import com.xiaogang.framework.domain.QueryResponseVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: zhuganggang
 * @Date: 2018/10/11 10:42
 * @Description:
 */
@Controller
@RequestMapping("/xiaogang")
public class MyController {

    @ResponseJson
    @RequestMapping("/query")
    public QueryResponseVo query(@Json QueryRequestVo queryRequestVo) {
        QueryResponseVo queryResponseVo = new QueryResponseVo();
        queryResponseVo.setAge(29);
        queryResponseVo.setName("朱罡罡");
        return queryResponseVo;
    }
}
