package com.penglecode.xmodule.fabric.bankmaster.chaincode;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.penglecode.xmodule.fabric.bankmaster.Entity.InsertEntity;
import com.penglecode.xmodule.fabric.bankmaster.Entity.SelectEntity;
import com.penglecode.xmodule.fabric.common.util.JsonUtils;
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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
public class BankMasterChaincode extends   ChaincodeBase {

    //表名和id的映射集合
    private static Map<String, Integer> tableNameToId = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(BankMasterChaincode.class);

    @Override
    public Response init(ChaincodeStub chaincodeStub) {
        return null;
    }

    @Override
    public Response invoke(ChaincodeStub stub) {
        String function = stub.getFunction();
        List<String> args = stub.getParameters();
        if(args.size() == 2){
            try {
               Class<?> sqlClass =  sqlParser(args.get(0),args.get(1)).getClass();
               if(sqlClass == InsertEntity.class){
                   InsertEntity insertEntity = (InsertEntity) sqlClass.newInstance();
                   insert(stub,insertEntity);
               } else  if(sqlClass == SelectEntity.class){
                   SelectEntity selectEntity = (SelectEntity) sqlClass.newInstance();
                   select(stub,selectEntity);
               }
            } catch (JSQLParserException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info(">>> 调用智能合约开始，function = {}, args = {}", function, args);
        Response response = null;
        return  newSuccessResponse("成功");
    }
    @Test
    public void test() throws JSQLParserException {
        String sql = "insert into table_name (name,id,age) VALUES (?, ?, 主机)";
        String sql2 = "select id name from teacher where id=? and name =?";
        String params =  "{\"1\":\"3\",\"2\":\"哈哈\"}";
        Object object = sqlParser(sql2,params);

        if(object.getClass() == InsertEntity.class){
            InsertEntity insertEntity = (InsertEntity) object;
            System.out.println(insertEntity.getTableName()+"sadfsadfasdfds");
            //insert(stub,insertEntity);
        } else  if(object.getClass() == SelectEntity.class){
            SelectEntity selectEntity = (SelectEntity) object;
            System.out.println(selectEntity.getTableName()+"dsfadsfadsfasd");
        }
    }

    /**
     * //sql解析
     * @param sql
     * @param params
     * @return  object (sqlEntity)
     * @throws JSQLParserException
     */

    public  Object sqlParser(String sql,String params) throws JSQLParserException {
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
            List selectFields = ((PlainSelect) selectBody).getSelectItems();
            if(plainSelect.getWhere()  instanceof AndExpression){
                AndExpression andExpression = (AndExpression) plainSelect.getWhere();
                EqualsTo equalsTo = (EqualsTo) andExpression.getLeftExpression();
                whereFields.put(((Column)equalsTo.getLeftExpression()).getColumnName(),String.valueOf(equalsTo.getRightExpression()));
                equalsTo = (EqualsTo)andExpression.getRightExpression();
                whereFields.put(((Column)equalsTo.getLeftExpression()).getColumnName(),String.valueOf(equalsTo.getRightExpression()));
              //调用test2中进行测试
                new Test2().select(tableName,selectFields,whereFields);
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

            while(iteratorKey.hasNext()){
                Column column = (Column) iteratorKey.next();
                mapParams.put(column.getColumnName(),expressionList.getExpressions().get(index));
            }
            System.out.println(mapParams.toString());

            insertEntity.setTableName(result.get(0));
            insertEntity.setParams(mapParams);
            return insertEntity;
        }
       return  "error";
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
    public Response insert(ChaincodeStub stub, InsertEntity insertEntity ) {
        String tableName = insertEntity.getTableName();
        Map params = insertEntity.getParams();
        if (tableNameToId.get(tableName) == null) {
            tableNameToId.put(tableName, -1);
        }

            params.put("tableName",tableName);
            String jsonParams = JsonUtils.object2Json(params);
            int docId = tableNameToId.get(tableName) + 1;
            String idIndex = tableName + docId;
            tableNameToId.put(tableName, tableNameToId.get(tableName) + 1);
            stub.putStringState(idIndex,jsonParams);
            return newSuccessResponse(String.format("数据插入成功"));
    }
    public   Response delete(ChaincodeStub stub,String tableName,Map whereFields){
        SelectEntity selectEntity = new SelectEntity();
        selectEntity.setTableName(tableName);
        selectEntity.setWhereFields(whereFields);
        selectEntity.setSelectFields(null);
        String bytes = String.valueOf(select(stub,selectEntity).getPayload());
        JSONArray jsonArray = JSONArray.parseArray(bytes);
        String id = tableName+jsonArray.getJSONObject(0).getString("id");
        stub.delState(id);
        return  newSuccessResponse("数据删除成功");
    }
    public Response select( ChaincodeStub stub,SelectEntity selectEntity) {
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
        if (selectFields!= null){
            queryString = queryString + "}, \"Fields\":["+selectFields.get(0)+"\"";
            for(int i= 1;i<selectFields.size();i++){
                queryString = queryString + ",\""+selectFields.get(i)+"\"";
            }
            queryString =queryString+"]}";
        }
        byte[] bytes = getQueryResultForQueryString(stub, queryString);
        JSONArray jsonArray = JSONArray.parseArray(String.valueOf(bytes));
        JSONArray returnAaaray = new JSONArray();
        for(int i =0;i<jsonArray.size();i++){
            JSONObject jsonObject = (JSONObject) jsonArray.getJSONObject(i).remove("docType");
            returnAaaray.add(jsonObject);
        }
        return newSuccessResponse("success",returnAaaray.toJSONString().getBytes());
    }
    public  Response update(ChaincodeStub stub,String tableName,Map updateFields,Map whereFields){
        SelectEntity selectEntity = new SelectEntity();
        selectEntity.setTableName(tableName);
        selectEntity.setSelectFields(null);
        selectEntity.setWhereFields(updateFields);
        String bytes = String.valueOf(select(stub,selectEntity).getPayload());
        JSONArray jsonArray = JSONArray.parseArray(bytes);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        Iterator<Map.Entry<Object, Object>> entries2 = whereFields.entrySet().iterator();
        while (entries2.hasNext()) {
            Map.Entry<Object, Object> entry = entries2.next();
            jsonObject.put(String.valueOf(entry.getKey()),entry.getValue());
        }
        String id = jsonObject.getString("id");
        stub.putStringState(tableName+id,String.valueOf(jsonObject));
         return  newSuccessResponse("success");
    }
    public byte[] getQueryResultForQueryString(ChaincodeStub stub, String queryString) {
         QueryResultsIterator<KeyValue> queryResultsIterator = stub.getQueryResult(queryString);
         byte[] bytes = constructQueryResponseFromIterator(queryResultsIterator);
         return bytes;
    }
    private byte[] constructQueryResponseFromIterator(QueryResultsIterator resultsIterator) {
         StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("[");
         Iterator<KeyValue> iterator = resultsIterator.iterator();
        while (iterator.hasNext()) {
            KeyValue keyValue = iterator.next();
            //stringBuffer.append(",");
            stringBuffer.append(keyValue.getValue());
            stringBuffer.append(",");
        }
        stringBuffer.deleteCharAt(stringBuffer.length()-1);
        stringBuffer.append("]");
        System.out.println(stringBuffer.toString());
        return stringBuffer.toString().getBytes();
    }
    public static void main(String[] args) {

        new BankMasterChaincode().start(args);
    }
}
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

