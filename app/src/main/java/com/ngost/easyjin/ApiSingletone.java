package com.ngost.easyjin;

/**
 * Created by Jinyoung on 2018-02-07.
 */

public class ApiSingletone {
    private static final ApiSingletone instance = new ApiSingletone("");
    private String key;
    private ApiSingletone(String key) {
        this.key = key;
    }
    public static final ApiSingletone getInstance(){
        return instance;
    }
    public String getKey(){
        return instance.key;
    }
}
