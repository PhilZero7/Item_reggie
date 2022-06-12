package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;

    /**
     * 保存包含口味的菜品
     *
     * @param dishDto 包含口味的菜品
     * @return
     */
    @Override
    public boolean saveWithFlavors(DishDto dishDto)  {
        // 1. 保存菜品的基本信息
        // 使用mp新增数据，他会自动将对一个记录的id设置会实体对象
        this.save(dishDto);

        // 2. 获取菜品id
        Long dishId = dishDto.getId();

        // 3. 菜品口味设置菜品id
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((flavor) -> {
            flavor.setDishId(dishId);
            return flavor;

        }).collect(Collectors.toList());


        // 4. 调用DishFlavorService保存口味信息
        dishFlavorService.saveBatch(flavors);

        // 5. 组织结果并返回
        return true;
    }
}