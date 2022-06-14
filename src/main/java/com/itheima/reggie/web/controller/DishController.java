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
import java.util.Arrays;
import java.util.List;

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
     *
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


    /**
     * 启售/禁售
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R switchStatus(@PathVariable Integer status, Long[] ids) {
        log.info("批量修改菜品状态，目标状态{}，菜品id们：{}", status == 0 ? "禁售" : "启售", Arrays.toString(ids));

        if (status != null && (status == 0 || status == 1)) {

            boolean ssResult = dishService.switchStatus(status, ids);
            if (ssResult) {
                return R.success("修改状态成功");
            }
            return R.fail("修改状态失败");
        }
        return R.fail("参数有误");
    }


    /**
     * 根据条件查询菜品
     * @param categoryId  菜品分类ID
     * @return
     */
    @GetMapping("/list")
    public R<List<Dish>> listByCondition(Long categoryId,String name) {
        // TODO 菜品条件查询1：handler形参设置为dish类型，兼容更多查询条件
        log.info("根据某个分类下的所有菜品，分类ID{}，查询关键字{}", categoryId,name);
        List<Dish> dishes = dishService.listByCondition(categoryId,name);

        return R.success("查询菜品成功", dishes);
    }
}