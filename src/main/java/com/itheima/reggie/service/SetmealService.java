package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.dto.SetmealDto;

public interface SetmealService extends IService<Setmeal> {
    // 分页条件查询套餐，携带套餐分类名称
    Page<SetmealDto> pageWithCategoryName(Integer currentPage, Integer pageSize, String name);
}