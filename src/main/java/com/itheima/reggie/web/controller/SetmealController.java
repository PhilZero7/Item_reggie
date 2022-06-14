package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.dto.SetmealDto;
import com.itheima.reggie.service.SetmealService;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Vsunks.v
 * @Date 2022/6/12 17:16
 * @Blog blog.sunxiaowei.net/996.mba
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {


    @Autowired
    SetmealService setmealService;


    /**
     * 分页条件查询套餐，携带套餐分类名称
     * @param currentPage   页码
     * @param pageSize      页面大小
     * @param name          查询条件
     * @return
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(@RequestParam("page") Integer currentPage, Integer pageSize,
                                    String name) {

        // 参数合理化设置
        if (currentPage == null) {
            currentPage = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        Page<SetmealDto> page = setmealService.pageWithCategoryName(currentPage, pageSize, name);


        return R.success("查询成功", page);

    }
}
