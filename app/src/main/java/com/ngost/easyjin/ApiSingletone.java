package com.ngost.easyjin;

/**
 * Created by Jinyoung on 2018-02-07.
 */

public class ApiSingletone {
    private static final ApiSingletone instance = new ApiSingletone("PdOZcYPmajd9ZnBghZ+6RtOd9CeIDY2X93vRaPwxgV1el2ElXqg4WQU4eC0LHG0ejC1bznr2pz6R2hqUrp9w44MgYALeidfWS7XHQvkmr3nw3xel/0Zk0y8UKvySnceArG3nOBrD95CynKRKO6VXHltpyB7TZrsDW+defAhmk2+ObxPSsGlr3XwpuN+qIdZfDmp1F/vT8owveGvWdiO1SSeJuoGsG5hNgVkxFbXKV3LAe/sCjtYYI9OYHCft94l4p7eIHlFBlg+ab8Aw6r5y54Sy3FOGSYBTvJ7mVt8bTXDyo6WEOcZJjDViCcQAifO5caghCQvQR+48QYL4wJwxAyaN5o6L4kDhVnIqSLB6d79qHqMNii7r1YuehLxeDqtmzhy4rtVI3HU/vSw7Ceqx1uKa0oTmZTK0mlv7ZPYuRfm+PQ8mwNHUAHAd02+8O0M6D93m/6TkocCQkd8wJQdXruJNNDxa+oyjtAS3QCk5UQuXiADUHe6MvoWIVKzw8BLEuVo+7IgD1PL+WkvgASlDDW92mIJlj9H6HmSEvZ34z9ARmSUCJi1POg6d7F47Nr4VbQIBkzl+UBhh9OuoDvqfIZcXXC+pMxOVKPp7s7OCJvV4SeUwxd7NzudPu+aW0XVh/emOajKBmq9keNO3gZJwUEqd3qojlIiIvJ25lDVn9sk=");
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
