package com.penglecode.xmodule.fabric.bankmaster.Entity;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public class InsertEntity {
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }





    private  String tableName;

    public JSONObject getJsonParams() {
        return jsonParams;
    }

    public void setJsonParams(JSONObject jsonParams) {
        this.jsonParams = jsonParams;
    }

    private JSONObject jsonParams;

}
