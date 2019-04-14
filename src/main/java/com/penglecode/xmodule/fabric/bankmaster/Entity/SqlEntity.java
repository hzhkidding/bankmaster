package com.penglecode.xmodule.fabric.bankmaster.Entity;

import java.util.List;
import java.util.Map;

public class SqlEntity {
   private String  sqlType;
   private String   tableName;
   private   List<String> selectFields;
   private Map<Object,Object> whereFields;
   private  Map<String,String>  setFields;
   private int limit;
   private  Class<?> clazz;

    public Map<String, String> getSetFields() {
        return setFields;
    }

    public void setSetFields(Map<String, String> setFields) {
        this.setFields = setFields;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    private  Object object;

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(List<String> selectFields) {
        this.selectFields = selectFields;
    }

    public Map<Object, Object> getWhereFields() {
        return whereFields;
    }

    public void setWhereFields(Map<Object, Object> whereFields) {
        this.whereFields = whereFields;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }


}
