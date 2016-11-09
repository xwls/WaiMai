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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cart cart = (Cart) o;

        if (id != cart.id) return false;
        if (fid != cart.fid) return false;
        if (Float.compare(cart.price, price) != 0) return false;
        if (count != cart.count) return false;
        return name != null ? name.equals(cart.name) : cart.name == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + fid;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (price != +0.0f ? Float.floatToIntBits(price) : 0);
        result = 31 * result + count;
        return result;
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
