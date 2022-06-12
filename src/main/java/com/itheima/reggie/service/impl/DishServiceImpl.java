package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    @Transactional(rollbackFor = Exception.class)
    public boolean saveWithFlavors(DishDto dishDto) throws SQLIntegrityConstraintViolationException {

        // 0. 判断菜品名称是否存在
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(Dish::getName, dishDto.getName());
        Dish dish = getOne(qw);
        if (dish == null) {
            throw new RuntimeException("菜品名称不可重复");
        }


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
        // 事务 运行时异常（受检异常）  非运行时异常（非受检异常）
        // Spring的事务只会在受检异常发生时才会整体回滚；
        // 非受检异常发生时，其所在事务内异常前之前的数据库操作不会被回滚

        /*if (true) {
            throw new SQLIntegrityConstraintViolationException();
        }*/

        // 4. 调用DishFlavorService保存口味信息
        dishFlavorService.saveBatch(flavors);

        // 5. 组织结果并返回
        return true;
    }
}