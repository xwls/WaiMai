package com.oaec.waimai.entity;

/**
 * Created by Kevin on 2016/10/11.
 */
public class Merchant {
    private int id;
    private String name;
    private String img;
    private float grade;
    private float qs;
    private int xl;
    private float psf;
    private float distance;

    public Merchant() {
    }

    public Merchant(int id, String name, String img, float grade, float qs, int xl, float psf, float distance) {
        this.id = id;
        this.name = name;
        this.img = img;
        this.grade = grade;
        this.qs = qs;
        this.xl = xl;
        this.psf = psf;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }

    public float getQs() {
        return qs;
    }

    public void setQs(float qs) {
        this.qs = qs;
    }

    public int getXl() {
        return xl;
    }

    public void setXl(int xl) {
        this.xl = xl;
    }

    public float getPsf() {
        return psf;
    }

    public void setPsf(float psf) {
        this.psf = psf;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Merchant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", img='" + img + '\'' +
                ", grade=" + grade +
                ", qs=" + qs +
                ", xl=" + xl +
                ", psf=" + psf +
                ", distance=" + distance +
                '}';
    }
}
