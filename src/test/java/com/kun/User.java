package com.kun;

/**
 * 用户实体类（测试用）。
 */
public class User {

    private String name;

    /**
     * 构造用户对象。
     *
     * @param name 用户名
     */
    public User(String name) {
        this.name = name;
    }

    /**
     * 获取用户名。
     *
     * @return 用户名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置用户名。
     *
     * @param name 用户名
     */
    public void setName(String name) {
        this.name = name;
    }
}
