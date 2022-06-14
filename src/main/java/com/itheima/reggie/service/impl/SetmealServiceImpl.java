package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.entity.dto.SetmealDto;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import com.itheima.reggie.web.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    CategoryService categoryService;


    @Autowired
    SetmealDishService setmealDishService;

    /**
     * 分页条件查询套餐，携带套餐分类名称
     *
     * @param currentPage 页码
     * @param pageSize    页面大小
     * @param name        查询条件
     * @return
     */
    @Override
    public Page<SetmealDto> pageWithCategoryName(Integer currentPage, Integer pageSize, String name) {

        // 封装分页对象
        Page<Setmeal> page = new Page<>(currentPage, pageSize);

        // 封装查询条件对象
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(name), Setmeal::getName, name)
                .orderByDesc(Setmeal::getUpdateTime);

        // 查
        this.page(page, qw);

        // 准备新的分页对象
        Page<SetmealDto> dtoPage = new Page<>();

        // 复制基本数据
        BeanUtils.copyProperties(page, dtoPage, "records");

        // 查询所有分类信息
        List<Category> categories = categoryService.list();

        // 遍历原来分页对象中的records
        List<Setmeal> setmeals = page.getRecords();
        // 准备dto们
        ArrayList<SetmealDto> dtos = new ArrayList<>();
        for (Setmeal setmeal : setmeals) {
            // 从套餐中获取分类id
            Long cid = setmeal.getCategoryId();
            // 获取每个分类对象，通过分类id获取分类名字
            for (Category c : categories) {
                // 如果找到了对应id分类
                if (cid.equals(c.getId())) {
                    SetmealDto dto = new SetmealDto();
                    // 复制套餐基本信息
                    BeanUtils.copyProperties(setmeal, dto);
                    // 获取分类名称，并设置
                    dto.setCategoryName(c.getName());
                    dtos.add(dto);

                    // 跳出本次循环
                    break;
                }

            }
        }

        // 设置回新的分页对象中
        dtoPage.setRecords(dtos);
        return dtoPage;
    }

    /**
     * 新增套餐
     *
     * @param setmealDto 套餐信息，包含了基本信息和套餐中菜品信息
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveWithDish(SetmealDto setmealDto) {

        // 1. 判断套餐名称是否存在，如果存在提示重名
        String name = setmealDto.getName();

        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.eq(StringUtils.isNotBlank(name), Setmeal::getName, name);

        Setmeal setmeal = this.getOne(qw);
        if (setmeal != null) {
            throw new BusinessException("套餐" + name + "已存在");
        }

        // 2. 保存套餐基本信息，返回套餐的id（MP自动完成的）
        boolean saveResult = this.save(setmealDto);
        if (!saveResult) {
            return false;
        }

        // 3. 保存套餐详情

        // 3.1 遍历获取套餐中每个菜品对象
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            // 3.2 设置套餐id
            setmealDish.setSetmealId(setmealDto.getId());
        }

        // 3.3 保存套餐详情
        return setmealDishService.saveBatch(setmealDishes);
    }


}