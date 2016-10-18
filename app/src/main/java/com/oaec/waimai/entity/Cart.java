package com.oaec.waimai.entity;

/**
 * Created by Kevin on 2016/10/16.
 */
public class Cart {
    private int id;
    private int fid;
    private String name;
    private float price;
    private int count;

    public Cart(int fid, String name, float price, int count) {
        this.fid = fid;
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public Cart(int id, int fid, String name, float price, int count) {
        this.id = id;
        this.fid = fid;
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public Cart() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", fid=" + fid +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", count=" + count +
                '}';
    }
}
