package com.penglecode.xmodule.fabric.bankmaster.chaincode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.primitives.Bytes;
import com.penglecode.xmodule.fabric.bankmaster.Entity.DeleteEntity;
import com.penglecode.xmodule.fabric.bankmaster.Entity.InsertEntity;
import com.penglecode.xmodule.fabric.bankmaster.Entity.SelectEntity;
import com.penglecode.xmodule.fabric.bankmaster.Entity.UpdateEntity;
import com.penglecode.xmodule.fabric.common.util.JsonUtils;
import com.penglecode.xmodule.fabric.common.util.SqlUtils;
import com.sun.corba.se.impl.ior.ByteBuffer;
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
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BankMasterChaincode extends   ChaincodeBase {

    //表名和id的映射集合
    private static Map<String, Integer> tableNameToId = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(BankMasterChaincode.class);

    @Override
    public Response init(ChaincodeStub stub) {
        List<String> parameters = stub.getParameters();
        return newSuccessResponse("初始化智能合约成功!");


    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        String function = stub.getFunction();
        List<String> args = stub.getParameters();
        String sql = "insert into table_name (name,id,age) VALUES (?, ?, 主机)";
        String sql2 = "select id age from table_name where id=哈哈 and name =3";
        String params =  "{\"1\":\"3\",\"2\":\"哈哈\"}";
        if(args.get(0) == "1"){
            args.add(0,sql);
            args.add(1,params);
        }else{
            args.add(0,sql2);
            args.add(1,params);
        }

        String reponse = null;
            try {
                Object object = SqlUtils.sqlParser(args.get(0),args.get(1));
               if(object.getClass() == InsertEntity.class){
                   InsertEntity insertEntity = (InsertEntity) object;
                   insert(stub,insertEntity);
               } else  if(object.getClass() == SelectEntity.class){
                   SelectEntity selectEntity = (SelectEntity) object;
                  reponse =  select(stub,selectEntity);
               }else  if(object.getClass() == DeleteEntity.class) {
                   DeleteEntity deleteEntity = (DeleteEntity)object;
                   delete(stub, deleteEntity);
               }else  if(object.getClass() == UpdateEntity.class) {
                   UpdateEntity updateEntity = (UpdateEntity) object;
                   update(stub, updateEntity);
               }
            } catch (JSQLParserException e) {
                e.printStackTrace();
            }

        LOGGER.info(">>> 调用智能合约开始，function = {}, args = {}", function, args);
        //return  newSuccessResponse(stub.getStringState("table_name0"));
        return  newSuccessResponse("哈哈哈哈",reponse.getBytes( StandardCharsets.UTF_8));

    }

    public Response insert(ChaincodeStub stub, InsertEntity insertEntity ) {
        String tableName = insertEntity.getTableName();
        JSONObject jsonParams = insertEntity.getJsonParams();
        if (tableNameToId.get(tableName) == null) {
            tableNameToId.put(tableName, -1);
        }
              LOGGER.info(jsonParams.toString());
            jsonParams.put("docType",tableName);
           LOGGER.info(jsonParams.toJSONString());
            int docId = tableNameToId.get(tableName) + 1;
            String idIndex = tableName + docId;
            tableNameToId.put(tableName, tableNameToId.get(tableName) + 1);
            stub.putStringState(idIndex,jsonParams.toJSONString());
            return newSuccessResponse("数据插入成功");
    }

    public String select( ChaincodeStub stub,SelectEntity selectEntity) {
        System.out.println("测试点2");
        String tableName = selectEntity.getTableName();
        Map whereFields = selectEntity.getWhereFields();
        List selectFields = selectEntity.getSelectFields();
        String queryString ="{\"selector\":{\"docType\":\""+tableName+"\"";
        Iterator<Map.Entry<Object, Object>> entries = whereFields.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Object, Object> entry = entries.next();
            queryString  = queryString +  ",\""+entry.getKey()+"\":\""+entry.getValue()+"\"";
        }
        if (selectFields!= null ){
            if(selectFields.get(0)!="*") {
                queryString = queryString + "}, \"fields\":[\"" + selectFields.get(0) + "\"";
                for (int i = 1; i < selectFields.size(); i++) {
                    queryString = queryString + ",\"" + selectFields.get(i) + "\"";
                }
                queryString = queryString + "]}";
            }
        } else {
            queryString = queryString+"}";
        }
       String returnString = getQueryResultForQueryString(stub, queryString);
       // return  newSuccessResponse("hahahahahahhahahahaha",returnString.getBytes());
        LOGGER.info("最终returnString测试点"+returnString);
        JSONArray jsonArray = JSONArray.parseArray(returnString);
        LOGGER.info("jsonArray测试点"+jsonArray.toJSONString());
        if(selectFields.get(0)=="*") {
            JSONArray returnArrray = new JSONArray();
            for(int i =0;i<jsonArray.size();i++){
                JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(i).remove("docType");
                returnArrray.add(jsonObject);
            }
            LOGGER.info(returnArrray.toJSONString());
            return  returnArrray.toJSONString();
        }
        return  jsonArray.toJSONString();
    }
    public  Response update(ChaincodeStub stub, UpdateEntity updateEntity){
        SelectEntity selectEntity = new SelectEntity();
        selectEntity.setTableName(updateEntity.getTableName());
        selectEntity.setWhereFields(updateEntity.getWhereFields());
        String returnString = String.valueOf(select(stub,selectEntity));
        JSONArray jsonArray = JSONArray.parseArray(returnString);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        Iterator<Map.Entry<Object, Object>> entries2 = updateEntity.getSetFields().entrySet().iterator();
        while (entries2.hasNext()) {
            Map.Entry<Object, Object> entry = entries2.next();
            jsonObject.put(String.valueOf(entry.getKey()),entry.getValue());
        }
        String id = updateEntity.getTableName()+jsonObject.getString("id");
        stub.putStringState(id,jsonObject.toJSONString());
         return  newSuccessResponse("update success");
    }

    public  Response delete(ChaincodeStub stub, DeleteEntity deleteEntity){
        SelectEntity selectEntity = new SelectEntity();
        List<String> list = new LinkedList();
        list.add("id");
        selectEntity.setTableName(deleteEntity.getTableName());
        selectEntity.setWhereFields(deleteEntity.getWhereFields());
        selectEntity.setSelectFields(list);
        String returnString = String.valueOf(select(stub,selectEntity));
        JSONArray jsonArray = JSONArray.parseArray(returnString);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String id = deleteEntity.getTableName()+jsonObject.getString("id");
        stub.delState(id);
        return  newSuccessResponse("delete success");
    }

    @Test
    public  void test2() throws JSQLParserException {
        /* String sql = "insert into table_name (name,id,age) VALUES (?, ?, 主机)";*/
        String sql1 = "delete  from table_name where id=? and name = ?";
        String sql2 = "select id age from table_name where id=哈哈 ";
        String sql3 = "select id  from table_name where id=哈哈33 and name=2";
        String params =  "{\"1\":\"3\",\"2\":\"哈哈\"}";
        test(sql1,params);
        //test(sql2,params);
    }

    public void test(String sql,String params) throws JSQLParserException {
        Object object = SqlUtils.sqlParser(sql,params);
        if(object.getClass() == InsertEntity.class){
            InsertEntity insertEntity = (InsertEntity) object;
            System.out.println(insertEntity.getJsonParams().toJSONString());
            //insert(stub,insertEntity);
        } else  if(object.getClass() == SelectEntity.class){
            SelectEntity selectEntity = (SelectEntity) object;
            System.out.println(selectEntity.getWhereFields().toString());
            System.out.println(selectEntity.getSelectFields().toString());
            // select2(selectEntity);
        } else  if(object.getClass() == DeleteEntity.class){
            DeleteEntity deleteEntity = (DeleteEntity) object;
            System.out.println(deleteEntity.getWhereFields().toString()+"hahahha");
            delete2(deleteEntity);
        }
    }

    public String select2( SelectEntity selectEntity) {
        System.out.println("测试点2");
        String tableName = selectEntity.getTableName();
        Map whereFields = selectEntity.getWhereFields();
        List selectFields = selectEntity.getSelectFields();
        String queryString ="{\"selector\":{\"docType\":\""+tableName+"\"";
        Iterator<Map.Entry<Object, Object>> entries = whereFields.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Object, Object> entry = entries.next();
            queryString  = queryString +  ",\""+entry.getKey()+"\":\""+entry.getValue()+"\"";
        }
        if (selectFields!= null ){
            if(selectFields.get(0)!="*") {
                queryString = queryString + "}, \"fields\":[\"" + selectFields.get(0) + "\"";
                for (int i = 1; i < selectFields.size(); i++) {
                    queryString = queryString + ",\"" + selectFields.get(i) + "\"";
                }
                queryString = queryString + "]}";
            }
        } else {
            queryString = queryString+"}";
        }
        System.out.println(queryString);
        return queryString;

    }
    public  void delete2(DeleteEntity deleteEntity){
        SelectEntity selectEntity = new SelectEntity();
        List<String> list = new LinkedList();
        list.add("id");
        selectEntity.setTableName(deleteEntity.getTableName());
        selectEntity.setWhereFields(deleteEntity.getWhereFields());
        selectEntity.setSelectFields(list);
        String returnString = select2(selectEntity);
        JSONArray jsonArray = JSONArray.parseArray(returnString);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        String id = deleteEntity.getTableName()+jsonObject.getString("id");
        // stub.delState(id);

    }
    public  Response update2(UpdateEntity updateEntity){
        SelectEntity selectEntity = new SelectEntity();
        selectEntity.setTableName(updateEntity.getTableName());
        selectEntity.setWhereFields(updateEntity.getWhereFields());
        String returnString = String.valueOf(select2(selectEntity));
        JSONArray jsonArray = JSONArray.parseArray(returnString);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        Iterator<Map.Entry<Object, Object>> entries2 = updateEntity.getSetFields().entrySet().iterator();
        while (entries2.hasNext()) {
            Map.Entry<Object, Object> entry = entries2.next();
            jsonObject.put(String.valueOf(entry.getKey()),entry.getValue());
        }
        String id = updateEntity.getTableName()+jsonObject.getString("id");
        //stub.putStringState(id,jsonObject.toJSONString());
        return  newSuccessResponse("update success");
    }
    public String getQueryResultForQueryString(ChaincodeStub stub, String queryString) {
         LOGGER.info("VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV");
         QueryResultsIterator<KeyValue> queryResultsIterator = stub.getQueryResult(queryString);
         LOGGER.info("NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
         String returnString = constructQueryResponseFromIterator(queryResultsIterator);
         return returnString;
    }
    private String constructQueryResponseFromIterator(QueryResultsIterator resultsIterator) {
         StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
        LOGGER.info("ccccccccccccccccccccccccccccccc");
         Iterator<KeyValue> iterator = resultsIterator.iterator();
        LOGGER.info("dddddddddddddddddddddddddddddd");
        while (iterator.hasNext()) {
            KeyValue keyValue = iterator.next();
            //stringBuffer.append(",");
            LOGGER.info("fffffffffffffffffff"+keyValue.getStringValue());
            stringBuffer.append(keyValue.getStringValue());
            stringBuffer.append(",");
        }
        LOGGER.info("fffffffffffffffffff");
        stringBuffer.deleteCharAt(stringBuffer.length()-1);
        stringBuffer.append("]");
        LOGGER.info("gggggggggggggggggggggg"+stringBuffer.toString());
        return stringBuffer.toString();
    }
    public static void main(String[] args) {
        LOGGER.info(">>> Chaincode starting, args = {}", Arrays.toString(args));
        new BankMasterChaincode().start(args);
    }
}

   /* public Response insert(ChaincodeStub stub, String tableName, Map map) {

        if (tableNameToId.get(tableName) == null) {
            tableNameToId.put(tableName, -1);
        }
        String jsonObject = JsonUtils.object2Json(map);
        System.out.println(tableNameToId.get(tableName));
        try {
            Class<?> clazz = Class.forName("com.suke.czx.entity." + tableName);
            //tableObject用来传递给第二句 tableObject.getClass
            Object tableObject = clazz.newInstance();
            //创建泛型对象
            Object T = JsonUtils.json2Object(requestBody, tableObject.getClass());
            //获取方法设置doctype
            Method method1 = T.getClass().getMethod("setDocType", String.class);
            method1.invoke(T, tableName);
            //获取方法设置id
            Method method2 = T.getClass().getMethod("setId", int.class);
            //文档里的ID+1
            int docId = tableNameToId.get(tableName) + 1;
            method2.invoke(T, docId);
            Field[] fields = clazz.getDeclaredFields();
            String jsonAccount = JsonUtils.object2Json(T);
            //有前戳的ID
            String idIndex = tableName + docId;
            //转成字节码
            byte[] bytes = jsonAccount.getBytes();
            //将表名映射ID增加1
            tableNameToId.put(tableName, docId);
            stub.putState(idIndex,bytes);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return newErrorResponse(String.format("不存在的智能合约方法名: %s"));
    }*/
  /*  func getQueryResultForQueryString(stub shim.ChaincodeStubInterface, queryString string) ([]byte, error) {

        fmt.Printf("- getQueryResultForQueryString queryString:\n%s\n", queryString)

        resultsIterator, err := stub.GetQueryResult(queryString)
        if err != nil {
            return nil, err
        }
        defer resultsIterator.Close()

        buffer, err := constructQueryResponseFromIterator(resultsIterator)
        if err != nil {
            return nil, err
        }

        fmt.Printf("- getQueryResultForQueryString queryResult:\n%s\n", buffer.String())

        return buffer.Bytes(), nil
    }

    func constructQueryResponseFromIterator(resultsIterator shim.StateQueryIteratorInterface) (*bytes.Buffer, error) {
        // buffer is a JSON array containing QueryResults
        var buffer bytes.Buffer
        var sum = 0
        buffer.WriteString("[")

        bArrayMemberAlreadyWritten := false
        for resultsIterator.HasNext() {
            queryResponse, err := resultsIterator.Next()
            if err != nil {
                return nil, err
            }
            // Add a comma before array members, suppress it for the first array member
            if bArrayMemberAlreadyWritten == true {
                buffer.WriteString(",")
            }
            buffer.WriteString("{\"Key\":")
            buffer.WriteString("\"")
            buffer.WriteString(queryResponse.Key)
            buffer.WriteString("\"")

            buffer.WriteString(", \"Record\":")
            sum++
            // Record is a JSON object, so we write as-is
            buffer.WriteString(string(queryResponse.Value))
            buffer.WriteString("}")
            bArrayMemberAlreadyWritten = true
        }
        buffer.WriteString("]")

        return &buffer, nil
    }*/

