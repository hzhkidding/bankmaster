package com.penglecode.xmodule.fabric.bankmaster.Entity;

import java.util.Map;

public class DeleteEntity {
    private  String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getWhereFields() {
        return whereFields;
    }

    public void setWhereFields(Map<String, String> whereFields) {
        this.whereFields = whereFields;
    }

    private Map<String,String> whereFields;
}
