package com.project.ken.botec;

/**
 * Created by ken on 2/21/18.
 */

public class Constants {

    //private static final String HOST = "http://10.0.2.2:8000/api";
    private static final String HOST = "http://192.168.1.100:8000/api";
    private static final String IMAGE_BUCKET = "http://192.168.43.28:8000";

    public static String getHost() {
        return HOST;
    }

    public static String loginUser() {
        return HOST + "/operators/login";
    }

    public static String getProductDetails() {
        return HOST + "/items/show";
    }

    public static String saleProduct() {
        return HOST + "/items/sales";
    }

    public static String receiveProduct() {
        return HOST + "/items/received";
    }

    public static String mySalesLog() {
        return HOST + "/items/operator/sales";
    }

    public static String upload() {
        return HOST + "/operators/upload";
    }

    public static String getUserImage(String name) {
        return IMAGE_BUCKET + "/user_images/" + name;
    }

    public static String saveChangesUri(){
        return HOST + "/operators/edit/save";
    }
}
