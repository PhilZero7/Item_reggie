package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 保存包含口味的菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R saveWithFlavors(@RequestBody DishDto dishDto) throws SQLIntegrityConstraintViolationException {
        log.info("保存菜品，内容：{}", dishDto);

        boolean saveResult = dishService.saveWithFlavors(dishDto);

        if (saveResult) {
            return R.success("保存成功");
        }

        return R.fail("保存失败");
    }


    @GetMapping("/page")
    public R<Page<DishDto>> page(@RequestParam("page") Integer currentPage, Integer pageSize,
                              String name) {

        Page<DishDto> dishDtoPage = dishService.pageWithDishName(currentPage, pageSize, name);

        // 组织数据并响应
        return R.success("查询成功", dishDtoPage);
    }
}    