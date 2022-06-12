package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Dish;
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


   /* @GetMapping("/{id}")
    public R<Dish> getById(@PathVariable Long id) {
        log.info("根据菜品id查询菜品信息，id为：{}", id);

        // id非空判断

        if (id != null) {
            Dish dish = dishService.getById(id);
            if (dish != null) {
                return R.success("查询成功", dish);
            }
            return R.fail("查询失败");

        }
        return R.fail("参数有误");

    }*/

    /**
     * 根据id查询菜品，包含口味信息
     *
     * @param id 查询条件
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getByIdWithFlavors(@PathVariable Long id) {
        log.info("根据菜品id查询菜品信息，id为：{}", id);

        // id非空判断

        if (id != null) {
            DishDto dishDto = dishService.getByIdWithFlavors(id);
            if (dishDto != null) {
                return R.success("查询成功", dishDto);
            }

            return R.fail("查询失败");
        }
        return R.fail("参数有误");

    }


    /**
     * 修改菜品，包含菜品口味
     * @param dishDto 包含口味的菜品
     * @return
     */
    @PutMapping
    public R update(@RequestBody DishDto dishDto) {
        log.info("修改菜品，数据：{}", dishDto);

        // id非空校验
        Long dishId = dishDto.getId();

        if (dishId != null) {

            boolean updateResult = dishService.updateByIdWithFlavors(dishDto);
            if (updateResult) {
                return R.success("修改成功");
            }
            return R.fail("修改失败");
        }


        return R.fail("参数有误");
    }
}