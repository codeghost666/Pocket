package com.leaderapps.Cashmyapps;

/**
 * Created by HahnDunst on 10/29/2016.
 */

public class Globals{
    private static Globals instance;

    // Global variable
    private String isClicked;
    private  String clickedBranch;
    private int flag=0;
    private String branchURL;
    // Restrict the constructor from being instantiated
    private Globals(){}
    public void setBranchURL(String str){
        this.branchURL = str;
    }
    public String getBranchURL(){
        return this.branchURL;
    }
    public void setData(String a1, String a2){
        this.isClicked=a1;
        this.clickedBranch=a2;
    }
    public int getData3()
    {
        return this.flag;
    }
    public String getData1(){
        return this.isClicked;
    }
    public String getData2(){
        return this.clickedBranch;
    }
    public void setData3(int a){
        this.flag=a;
    }
    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}