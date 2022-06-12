package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.dto.DishDto;

import java.sql.SQLIntegrityConstraintViolationException;

public interface DishService extends IService<Dish> {
    // 保存包含口味的菜品
    boolean saveWithFlavors(DishDto dishDto) throws SQLIntegrityConstraintViolationException;
}