package com.oaec.waimai.entity;

/**
 * Created by Kevin on 2016/10/12.
 */
public class FoodInfo {
    private int id;
    private int tid;
    private String img;
    private String name;
    private int xl;
    private float price;
    private float hpl;

    public FoodInfo(int id, int tid, String img, String name, int xl, float price, float hpl) {
        this.id = id;
        this.tid = tid;
        this.img = img;
        this.name = name;
        this.xl = xl;
        this.price = price;
        this.hpl = hpl;
    }

    public FoodInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getXl() {
        return xl;
    }

    public void setXl(int xl) {
        this.xl = xl;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getHpl() {
        return hpl;
    }

    public void setHpl(float hpl) {
        this.hpl = hpl;
    }

    @Override
    public String toString() {
        return "FoodInfo{" +
                "id=" + id +
                ", tid=" + tid +
                ", img='" + img + '\'' +
                ", name='" + name + '\'' +
                ", xl=" + xl +
                ", price=" + price +
                ", hpl=" + hpl +
                '}';
    }
}