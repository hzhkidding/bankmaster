package com.penglecode.xmodule.fabric.bankmaster.Entity;

import java.util.Map;

public class InsertEntity {
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    private  String tableName;
    private Map<String,String> params;

}
