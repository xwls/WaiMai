package com.oaec.waimai.entity;

import java.util.List;

/**
 * Created by Kevin on 2016/10/12.
 */
public class Food {
    private String type;
    private List<FoodInfo> foods;

    public Food(String type, List<FoodInfo> foods) {
        this.type = type;
        this.foods = foods;
    }

    public Food() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<FoodInfo> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodInfo> foods) {
        this.foods = foods;
    }

    @Override
    public String toString() {
        return "Food{" +
                "type='" + type + '\'' +
                ", foods=" + foods +
                '}';
    }
}
