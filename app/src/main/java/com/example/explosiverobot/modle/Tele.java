package com.example.explosiverobot.modle;

/**
 * Created by zhangyuanyuan on 2017/10/26.
 */

public class Tele {

    private String user;
    private String pwd;
    private String did;

    private String user1 = "admin";
    private String pwd1 = "haifeng567";
    private String did1 = "VSTA347062EGDGD";

    private String user2 = "admin";
    private String pwd2 = "12345678";
    private String did2 = "VSTC900392EUSVZ";

    private String user3 = "";
    private String pwd3 = "";
    private String did3 = "";

    private String user4 = "";
    private String pwd4 = "";
    private String did4 = "";

    private static Tele ourInstance = new Tele();

    public static Tele getInstance() {
        return ourInstance;
    }

    public void setTele1() {
        Tele.getInstance().setUser(user1);
        Tele.getInstance().setPwd(pwd1);
        Tele.getInstance().setDid(did1);
    }

    public void setTele2() {
        Tele.getInstance().setUser(user2);
        Tele.getInstance().setPwd(pwd2);
        Tele.getInstance().setDid(did2);
    }

    public void setTele3() {
        Tele.getInstance().setUser(user3);
        Tele.getInstance().setPwd(pwd3);
        Tele.getInstance().setDid(did3);
    }

    public void setTele4() {
        Tele.getInstance().setUser(user4);
        Tele.getInstance().setPwd(pwd4);
        Tele.getInstance().setDid(did4);
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }


}
