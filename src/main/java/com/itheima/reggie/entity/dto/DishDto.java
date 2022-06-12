package com.itheima.reggie.entity.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Vsunks.v
 * @Date 2022/6/12 9:47
 * @Blog blog.sunxiaowei.net/996.mba
 * @Description: 包含口味的菜品
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishDto extends Dish {

    // 菜品口味集合
    private List<DishFlavor> flavors;

}
