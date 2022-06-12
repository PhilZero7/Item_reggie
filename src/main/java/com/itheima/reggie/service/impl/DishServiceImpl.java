package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
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

    @Autowired
    CategoryService categoryService;

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

    @Override
    public Page<DishDto> pageWithDishName(Integer currentPage, Integer pageSize, String name) {

        // 检查并设置分页参数的合理性
        if (currentPage == null) {
            currentPage = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        // 条件查询
        Page<Dish> page = new Page<>(currentPage, pageSize);

        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.like(StringUtils.isNotBlank(name), Dish::getName, name)
                .orderByAsc(Dish::getSort)
                .orderByDesc(Dish::getUpdateTime);

        // 查询出来的page的records里面的每个dish只包含分类id，无分类名称
        this.page(page, qw);

        // 创建新的page对象，内含DishDto
        Page<DishDto> dishDtopage = new Page<DishDto>();

        // 复制基本信息
        BeanUtils.copyProperties(page, dishDtopage, "records");

        // 查询所有(菜品)分类
        List<Category> categories = categoryService.list();

        // 处理records中的dishDto对象的分类名称
        // 使用集合接收，集合的泛型为DishDto
        List<DishDto> dishDtos = page.getRecords().stream()
                // 获取每一个dish对象
                .map((dish) -> {
                    // 准备一个可以保存分类名称的dish的子类DishDto对象
                    DishDto dishDto = new DishDto();

                    // 复制dish的基本数据
                    BeanUtils.copyProperties(dish, dishDto);

                    // 获取dish中的分类id
                    Long categoryId = dish.getCategoryId();

                    // 遍历所有分类集合，根据id查找名字
                    for (Category category : categories) {
                        if (categoryId.equals(category.getId())) {
                            // 找到对应的分类，设置到dishDto的categoryName
                            dishDto.setCategoryName(category.getName());
                        }
                    }
                    // 返回dishDto
                    return dishDto;
                    // 收集到集合中
                }).collect(Collectors.toList());

        // 将所有的dishDto设置到分页对象中
        dishDtopage.setRecords(dishDtos);

        return dishDtopage;
    }


}