package com.penglecode.xmodule.fabric.bankmaster.Entity;

import java.util.List;
import java.util.Map;

public class SelectEntity {
     private  String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(List selectFields) {
        this.selectFields = selectFields;
    }

    public Map getWhereFields() {
        return whereFields;
    }

    public void setWhereFields(Map whereFields) {
        this.whereFields = whereFields;
    }

    private List selectFields;
     private Map whereFields;
}
