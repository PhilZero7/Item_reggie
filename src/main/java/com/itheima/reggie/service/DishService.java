package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.dto.DishDto;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;

@Transactional
public interface DishService extends IService<Dish> {
    // 保存包含口味的菜品
    boolean saveWithFlavors(DishDto dishDto) throws SQLIntegrityConstraintViolationException;

    Page<DishDto> pageWithDishName(Integer currentPage, Integer pageSize, String name);

    // 根据id查询菜品，包含口味信息
    DishDto getByIdWithFlavors(Long id);

    // 修改菜品，包含菜品口味
    boolean updateByIdWithFlavors(DishDto dishDto);
}