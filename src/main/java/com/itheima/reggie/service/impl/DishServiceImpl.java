package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
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
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    DishFlavorService dishFlavorService;

    @Autowired
    CategoryService categoryService;


    @Autowired
    DishMapper dishMapper;

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

    /**
     * 根据id查询菜品，包含口味信息
     *
     * @param id 查询条件
     * @return
     */
    @Override
    public DishDto getByIdWithFlavors(Long id) {

        // 根据id查询口味的基本信息(Dish)
        Dish dish = this.getById(id);

        // 根据菜品id查询菜品口味
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        qw.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(qw);

        // 封装口味信息到菜品对象(DishDto)
        DishDto dishDto = new DishDto();

        // 复制基本数据
        BeanUtils.copyProperties(dish, dishDto);

        dishDto.setFlavors(flavors);

        // 封装数据返回
        return dishDto;
    }

    /**
     * 修改菜品，包含菜品口味
     *
     * @param dishDto 包含口味的菜品
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByIdWithFlavors(DishDto dishDto) {

        // 修改菜品基本信息
        boolean updateRsult = this.updateById(dishDto);

        if (!updateRsult) {
            return updateRsult;
        }

        // 删除对应菜品的口味信息，条件是菜品id，而非口味id
        LambdaQueryWrapper<DishFlavor> qw = new LambdaQueryWrapper<>();
        Long dishId = dishDto.getId();
        qw.eq(DishFlavor::getDishId, dishId);
        boolean deleteResult = dishFlavorService.remove(qw);
        // 删除失败？也没事，不用额外处理！接下来直接添加即可。

        // 把dishId设置进DishFlavor对象
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }

        // 添加口味信息
        boolean saveBatchResult = dishFlavorService.saveBatch(flavors);
        return saveBatchResult;
    }

    /**
     * 启售/禁售
     *
     * @param status 目标状态，值为0或1
     * @param ids    要被修改的菜品id们
     * @return
     */
    @Override
    public boolean switchStatus(Integer status, Long[] ids) {

        // 禁用
        boolean result = dishMapper.updateStatusByIds(status, ids);

        return result;
    }
}