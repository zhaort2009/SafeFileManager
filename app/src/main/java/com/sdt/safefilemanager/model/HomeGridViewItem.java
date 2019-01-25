package com.sdt.safefilemanager.model;

/**
 * @author:
 * @date: 2018/11/8
 * @describe: GridItem实体
 */
public class HomeGridViewItem {
    Integer imageResourceID;
    String imageName;
    String capacity;
    Integer imagePressedID;

    public HomeGridViewItem(Integer imageResourceID, String imageName, String capacity, Integer imagePressedID) {
        this.imageResourceID = imageResourceID;
        this.imageName = imageName;
        this.capacity = capacity;
        this.imagePressedID = imagePressedID;
    }

    public Integer getImageResourceID() {
        return imageResourceID;
    }

    public void setImageResourceID(Integer imageResourceID) {
        this.imageResourceID = imageResourceID;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public Integer getImagePressedID() {
        return imagePressedID;
    }

    public void setImagePressedID(Integer imagePressedID) {
        this.imagePressedID = imagePressedID;
    }
}
