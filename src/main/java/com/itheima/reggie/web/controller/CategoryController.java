package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;

/**
 * @Author Vsunks.v
 * @Date 2022/6/11 9:44
 * @Blog blog.sunxiaowei.net/996.mba
 * @Description: 分类控制器，包含了菜品分类和套餐分类
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /**
     * 保存分类
     * @param category 分类信息
     * @return
     *//*
    @PostMapping
    public R add(@RequestBody Category category) {

        log.info("新增分类，信息：{}", category);

        boolean saveRsult = categoryService.save(category);

        if (saveRsult) {
            return R.success("新增成功");
        }
        return R.fail("新增失败");

    }*/

    /**
     * 保存分类，并在保存前检查分类名称
     *
     * @param category 分类信息
     * @return
     */
    @PostMapping
    public R add(@RequestBody Category category) {

        log.info("新增分类，信息：{}", category);

        boolean saveRsult = categoryService.saveWithNameCheck(category);

        if (saveRsult) {
            return R.success("新增成功");
        }
        return R.fail("新增失败");
    }


    /**
     * 分类查询
     * 排序条件：主要条件是sort，次要条件是updateTime
     *
     * @param currentPage 当前页
     * @param pageSize    页面大小
     * @return
     */
    @GetMapping("/page")
    public R<Page<Category>> page(@RequestParam("page") Integer currentPage, Integer pageSize) {
        log.info("所有分类分页查询，第{}页，每页{}条", currentPage, pageSize);

        // TODO 下面的逻辑代码，应该写在service层

        // 0. 请求数据合理化设置
        if (currentPage == null) {
            currentPage = 1;
        }
        if (pageSize == null) {
            pageSize = 5;
        }

        // 1. 创建分页查询的page对象
        Page<Category> page = new Page<>(currentPage, pageSize);

        // 2. 创建条件对象，并设置隐含条件
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        qw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        // 3. 查询，数据自动封装进Page对象
        categoryService.page(page, qw);

        return R.success("查询成功", page);
    }


    /**
     * 按照id删除
     *
     * @param id
     * @return
     */
    /*@DeleteMapping
    public R deleteById(Long id) {
        log.info("按照id删除，id为：{}", id);

        // 删除
        boolean deleteResut = categoryService.removeById(id);

        if (deleteResut) {

            return R.success("删除成功");
        }
        return R.fail("删除失败");

    }*/


    /**
     * 按照id删除
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R deleteById(Long id) {
        log.info("按照id删除，id为：{}", id);

        // 删除
        boolean deleteResut = categoryService.removeByIdWithoutUsersd(id);

        if (deleteResut) {

            return R.success("删除成功");
        }
        return R.fail("删除失败");

    }


    /**
     * 修改，必须要携带id
     *
     * @param category
     * @return
     */
    @PutMapping
    public R update(@RequestBody Category category) {
        log.info("分类编辑，新的数据：{}", category);
        if (category.getId() != null) {

            boolean updateResult = categoryService.updateById(category);

            if (updateResult) {
                return R.success("修改成功");
            }
            return R.fail("修改失败");
        }
        return R.fail("参数异常");
    }


    /**
     * 按照条件查询所有的分类数据。
     * 排序主要条件：sort升序
     * 排序次要条件：updateTime 降序
     *
     * @param category 查询条件，SpringMVC帮我们创建的对象，不可能为null。其属性值可能为null
     * @return
     */
    @GetMapping("list")
    public R<List<Category>> listByCondition(Category category) {
        log.info("查询分类，查询条件：{}", category);


        // 1. 条件构造器，设置分类类型
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();

        // 2. 条件构造器，设置主次排序条件
        Integer type = category.getType();
        String name = category.getName();
        // 如果存在类型，就按照类型查
        qw.eq(type != null, Category::getType, type)
                // 如果存在名称，就按照名称查
                .like(StringUtils.isNotBlank(name), Category::getName, name)
                // 隐含的排序条件
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);

        // 3. 调用`service`方法查询
        List<Category> categories = categoryService.list(qw);

        if (categories != null && categories.size() > 0) {
            // 4. 组装结果数据，响应
            return R.success("查询分类成功", categories);
        }
        return R.fail("没有这种分类");
    }

    /**
     * 按照【分类类型】条件查询所有的分类数据。
     * 排序主要条件：sort升序
     * 排序次要条件：updateTime 降序
     *
     * @param type 查询
     * @return
     */
    //@GetMapping("list")
    public R<List<Category>> listByType(Long type) {
        log.info("查询分类，分类类型id：{}", type);

        if (type != null) {

            // 1. 条件构造器，设置分类类型
            LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();

            // 2. 条件构造器，设置主次排序条件
            qw.eq(Category::getType, type)
                    .orderByAsc(Category::getSort)
                    .orderByDesc(Category::getUpdateTime);

            // 3. 调用`service`方法查询
            List<Category> categories = categoryService.list(qw);

            if (categories != null && categories.size() > 0) {
                // 4. 组装结果数据，响应
                return R.success("查询分类成功", categories);
            }
            return R.fail("没有这种分类");
        }

        return R.fail("参数异常");
    }
}
