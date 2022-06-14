package com.itheima.reggie.entity.dto;

import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private String categoryName;
    private List<SetmealDish> setmealDishes;
}