package com.penglecode.xmodule.fabric.bankmaster.Entity;

public class TestField {
    private String docType;
    private  int id;
    private  String name;
    private  String password;

  public  TestField(){

  }

    public   TestField (String docType,int id,String name,String password){
        this.docType = docType;
        this.id = id;
        this.name = name;
        this.password = password;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }




    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
