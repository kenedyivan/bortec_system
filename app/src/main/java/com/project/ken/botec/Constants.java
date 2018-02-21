package com.project.ken.botec;

/**
 * Created by ken on 2/21/18.
 */

public class Constants {

    private static final String HOST = "http://10.0.3.2:8000";

    public static String getHost() {
        return HOST;
    }

    public static String loginUser() {
        return HOST + "/login/";
    }
}
