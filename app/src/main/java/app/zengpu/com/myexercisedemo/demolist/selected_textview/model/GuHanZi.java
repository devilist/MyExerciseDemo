package app.zengpu.com.myexercisedemo.demolist.selected_textview.model;

/**
 * Created by zengpu on 2016/11/21.
 */

public class GuHanZi {

    private int ID;
    private String hanzi;
    private String yinjie;  // 音节
    private String bushou;  // 部首
    private int bushoubihuashu; // 部首笔画数
    private int zongbihuashu; // 总笔画数
    private String bishun; // 笔顺 2121343413434521234123452134
    private String shiyi; // 释义

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getHanzi() {
        return hanzi;
    }

    public void setHanzi(String hanzi) {
        this.hanzi = hanzi;
    }

    public String getYinjie() {
        return yinjie;
    }

    public void setYinjie(String yinjie) {
        this.yinjie = yinjie;
    }

    public String getBushou() {
        return bushou;
    }

    public void setBushou(String bushou) {
        this.bushou = bushou;
    }

    public int getBushoubihuashu() {
        return bushoubihuashu;
    }

    public void setBushoubihuashu(int bushoubihuashu) {
        this.bushoubihuashu = bushoubihuashu;
    }

    public int getZongbihuashu() {
        return zongbihuashu;
    }

    public void setZongbihuashu(int zongbihuashu) {
        this.zongbihuashu = zongbihuashu;
    }

    public String getBishun() {
        return bishun;
    }

    public void setBishun(String bishun) {
        this.bishun = bishun;
    }

    public String getShiyi() {
        return shiyi;
    }

    public void setShiyi(String shiyi) {
        this.shiyi = shiyi;
    }
}
