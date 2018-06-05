package com.example.item;

public class ItemCategory {

    private int CategoryId;
    private String CategoryName;
    private String CategoryImageUrl;

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int id) {
        this.CategoryId = id;
    }


    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String name) {
        this.CategoryName = name;
    }

    public String getCategoryImage() {
        return CategoryImageUrl;

    }

    public void setCategoryImage(String image) {
        this.CategoryImageUrl = image;
    }

}
