package com.penglecode.xmodule.fabric.common.util;

import com.alibaba.fastjson.JSONObject;
import com.penglecode.xmodule.fabric.bankmaster.Entity.InsertEntity;
import com.penglecode.xmodule.fabric.bankmaster.Entity.SelectEntity;
import com.penglecode.xmodule.fabric.bankmaster.Entity.UpdateEntity;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.*;

public class SqlUtils {
    public static Object sqlParser(String sql,String params) throws JSQLParserException {
         /*sql = "insert into table_name (name,id,age) VALUES (?, ?, 主机)";
        params = "{\"1\":\"3\",\"2\":\"哈哈\"}";*/
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
        Object returnSqlObject = CCJSqlParserUtil.parse(sql);


        if(returnSqlObject.getClass() == Select.class){
            SelectEntity selectEntity = new SelectEntity();
            Select select = (Select) returnSqlObject;
            SelectBody selectBody = select.getSelectBody();
            PlainSelect plainSelect = (PlainSelect)selectBody;
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            List<String> result = tablesNamesFinder.getTableList(select);
            String tableName = result.get(0);
            List selectFieldsPre = ((PlainSelect) selectBody).getSelectItems();
            String[] splitSelect = selectFieldsPre.get(0).toString().split(" ");
            List<String> selectFields  = new ArrayList<>();
            for(int i =0;i< splitSelect.length;i++){
                selectFields.add(splitSelect[i]);
            }
            System.out.println(selectFields.get(0));
            if(plainSelect.getWhere()  instanceof AndExpression){
                AndExpression andExpression = (AndExpression) plainSelect.getWhere();
                EqualsTo equalsTo = (EqualsTo) andExpression.getLeftExpression();
                whereFields.put(((Column)equalsTo.getLeftExpression()).getColumnName(),String.valueOf(equalsTo.getRightExpression()));
                equalsTo = (EqualsTo)andExpression.getRightExpression();
                whereFields.put(((Column)equalsTo.getLeftExpression()).getColumnName(),String.valueOf(equalsTo.getRightExpression()));
                //调用test2中进行测试
                // new Test2().select(tableName,selectFields,whereFields);
                selectEntity.setTableName(tableName);
                selectEntity.setWhereFields(whereFields);
                selectEntity.setSelectFields(selectFields);
                System.out.println("hahahahhaahah");
                return  selectEntity;
            }
        }
        if(returnSqlObject.getClass() == Insert.class){
            InsertEntity insertEntity = new InsertEntity();
            Insert insert = (Insert) returnSqlObject;
            List<Column> list = insert.getColumns();
            TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
            List<String> result = tablesNamesFinder.getTableList(insert);

            Iterator iteratorKey = list.iterator();
            ExpressionList expressionList = (ExpressionList) insert.getItemsList();
            int index = 0;
            JSONObject jsonObject = new JSONObject();
            while(iteratorKey.hasNext()){
                Column column = (Column) iteratorKey.next();
                System.out.println(expressionList.getExpressions().get(index));
                jsonObject.put(column.getColumnName(),String.valueOf(expressionList.getExpressions().get(index)));

                //mapParams.put(column.getColumnName(),expressionList.getExpressions().get(index));
                index++;
            }
            System.out.println(jsonObject.toString());

            insertEntity.setTableName(result.get(0));
            insertEntity.setJsonParams(jsonObject);
            return insertEntity;
        }if(returnSqlObject.getClass() == Update.class){
            UpdateEntity updateEntity = new UpdateEntity();
            Update update = (Update) returnSqlObject;
            List list = update.getColumns();

        }
        return  "error";
    }
}