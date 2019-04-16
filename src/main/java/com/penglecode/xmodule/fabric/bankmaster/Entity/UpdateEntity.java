package com.penglecode.xmodule.fabric.bankmaster.Entity;

import java.util.Map;

public class UpdateEntity {
    private String tableName;

    public Map getWhereFields() {
        return whereFields;
    }

    public void setWhereFields(Map whereFields) {
        this.whereFields = whereFields;
    }

    private Map whereFields;
    private  Map setFields;

    public Map getSetFields() {
        return setFields;
    }

    public void setSetFields(Map setFields) {
        this.setFields = setFields;
    }



    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
