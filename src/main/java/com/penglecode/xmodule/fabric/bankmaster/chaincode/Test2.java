package com.penglecode.xmodule.fabric.bankmaster.chaincode;

import com.alibaba.fastjson.JSONArray;
import com.penglecode.xmodule.fabric.bankmaster.Entity.SqlEntity;
import com.penglecode.xmodule.fabric.common.util.JsonUtils;
import net.sf.json.JSONObject;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.junit.Test;

import java.util.*;

//import com.alibaba.fastjson.JSONObject;

//import net.sf.json.JSONObject;

public class Test2 {
    public void  select(String tableName,List selectFields,Map whereFields) {

        String queryString ="{\"selector\":{\"docType\":\""+tableName+"\"";
        Iterator<Map.Entry<Object, Object>> entries = whereFields.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Object, Object> entry = entries.next();
            queryString  = queryString +  ",\""+entry.getKey()+"\":\""+entry.getValue()+"\"";
            //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        if (selectFields!= null){
            queryString = queryString + "} ,\"Fields\":[\""+selectFields.get(0)+"\"";
            for(int i =1 ;i< selectFields.size();i++){
                queryString = queryString + ",\""+selectFields.get(i)+"\"";
            }
            queryString =queryString+"]}";
        }
        System.out.println(queryString);

        // byte[] bytes = getQueryResultForQueryString(stub, queryString);
        //return newSuccessResponse(String.valueOf(bytes));
    }
    @Test
    public static void main(String[] args) throws JSQLParserException {
        String string = new String("[{\"name\":\"Michael\",\"age\":24},{\"name\":\"Michael\",\"age\":\"25\"}]");
        JSONArray jsonArray = JSONArray.parseArray(string);
        String sql = "insert into table_name (name,id,age) VALUES (?, ?, 主机)";
        String params = "{\"1\":\"3\",\"2\":\"哈哈\"}";
        Map mapParams = new HashMap();
        com.alibaba.fastjson.JSONObject jsonParams = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject.parse(params);
        StringBuffer preSql = new StringBuffer(sql);
        StringBuffer endSql = new StringBuffer();
        for(int i =0,index =1 ;i<preSql.length();i++){
            if(preSql.charAt(i) == '?'){
                endSql.append(jsonParams.getString(String.valueOf(index)));
              //  System.out.println(jsonParams.getString(String.valueOf(index)).getClass());
                index++;
            }
            else  endSql.append(preSql.charAt(i));
        }
        System.out.println(endSql);
        sql = String.valueOf(endSql);
        Map<String,String> whereFields = new HashMap<>();
        Class<? extends Statement> sqlTypeClass = CCJSqlParserUtil.parse(sql).getClass();
        if(sqlTypeClass ==  Select.class){
            Select select = (Select) CCJSqlParserUtil.parse(sql);
            SelectBody selectBody = select.getSelectBody();
            PlainSelect plainSelect = (PlainSelect)selectBody;
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            List<String> result = tablesNamesFinder.getTableList(select);
            String tableName = result.get(0);
            List selectFields = ((PlainSelect) selectBody).getSelectItems();
            if(plainSelect.getWhere()  instanceof  AndExpression){
                AndExpression andExpression = (AndExpression) plainSelect.getWhere();
                EqualsTo equalsTo = (EqualsTo) andExpression.getLeftExpression();
                whereFields.put(((Column)equalsTo.getLeftExpression()).getColumnName(),String.valueOf(equalsTo.getRightExpression()));
                equalsTo = (EqualsTo)andExpression.getRightExpression();
                whereFields.put(((Column)equalsTo.getLeftExpression()).getColumnName(),String.valueOf(equalsTo.getRightExpression()));
                new Test2().select(tableName,selectFields,whereFields);
            }
        }

        if(sqlTypeClass == Insert.class){
            Insert insert = (Insert) CCJSqlParserUtil.parse(sql);
            List<Column> list = insert.getColumns();
            Iterator iteratorKey = list.iterator();
            ExpressionList expressionList = (ExpressionList) insert.getItemsList();
            int index = 0;

            while(iteratorKey.hasNext()){
                Column column = (Column) iteratorKey.next();
                mapParams.put(column.getColumnName(),expressionList.getExpressions().get(index));
            }

            System.out.println(mapParams.toString());
        }
        if(sqlTypeClass == Delete.class){

        }
        if (sqlTypeClass == Update.class){

        }
       /* Select select = (Select) CCJSqlParserUtil.parse(sql);
        SelectBody selectBody = select.getSelectBody();
        PlainSelect plainSelect = (PlainSelect)selectBody;

        Expression where = plainSelect.getWhere();
        ExpressionDeParser expressionDeParser = new ExpressionDeParser();
        plainSelect.getWhere().accept(expressionDeParser);
       if(where  instanceof  AndExpression){
           System.out.println("1");
           AndExpression andExpression = (AndExpression) where;
           Map<String,String> map = new HashMap<>();
           EqualsTo equalsTo = (EqualsTo) andExpression.getLeftExpression();

           System.out.println("Table:"+((Column)equalsTo.getLeftExpression()).getTable());
           System.out.println("Field:"+((Column)equalsTo.getLeftExpression()).getColumnName());
           System.out.println("equal:"+equalsTo.getRightExpression());
           System.out.println("-----------------");
           map.put(((Column)equalsTo.getLeftExpression()).getColumnName(),String.valueOf(equalsTo.getRightExpression()));
           equalsTo = (EqualsTo)andExpression.getRightExpression();

           System.out.println("Table:"+((Column)equalsTo.getLeftExpression()).getTable());
           System.out.println("Field:"+((Column)equalsTo.getLeftExpression()).getColumnName());
           System.out.println("equal:"+equalsTo.getRightExpression());
           map.put(((Column)equalsTo.getLeftExpression()).getColumnName(),String.valueOf(equalsTo.getRightExpression()));
           System.out.println(map.get("name"));
       }
       else {
           EqualsTo equalsTo = (EqualsTo)where;
           System.out.println("Table:"+((Column)equalsTo.getLeftExpression()).getTable());
           System.out.println("Field:"+((Column)equalsTo.getLeftExpression()).getColumnName());
           System.out.println("equal:"+equalsTo.getRightExpression());
       }*/
        String s = jsonArray.getJSONObject(1).getString("age");
        int test = jsonArray.getJSONObject(1).getInteger("age");
        JSONObject jsonObject3 = new JSONObject();
        
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("name",string);
      //  jsonObject1.put("age",age);
        String ss = String.valueOf(jsonObject1);
        System.out.println(ss);
        User user = JsonUtils.json2Object(ss,User.class);
        JSONArray jsonArray1 = JSONArray.parseArray(string);
        String i = jsonArray1.getJSONObject(1).getString("name");
        //统一全部存字符串到couchdb
        //JSON.geth可以自动转型；直接返回数据就可以；NICE
        System.out.println(i);
        JSONObject J = (JSONObject) JSONObject.fromObject(user);
        System.out.println(J);
        Map<Object,Object> map = new HashMap<>();
        //new BankMasterChaincode().start(args);
        SqlEntity sqlEntity = new SqlEntity();
       // Map map = new HashMap();
        map.put("id","2");
        map.put("name","xiaoming");
        //sqlEntity.setWhereFields(map);
        List list = new ArrayList();
        list.add("id");
        list.add("name");
        sqlEntity.setWhereFields(map);
        sqlEntity.setSelectFields(list);
        sqlEntity.setTableName("user");
       // new Test2().select(sqlEntity);
    }
}
