package com.nowcoder.model;


public class User {
    private int id;
    private String name;
    private String password;
    private String salt;
    private String headUrl;

    public String getName() {
        return name;
    }

    /**
     * @return the headUrl
     */
    public String getHeadUrl() {
        return headUrl;
    }

    /**
     * @param headUrl the headUrl to set
     */
    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    /**
     * @return the salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * @param salt the salt to set
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
