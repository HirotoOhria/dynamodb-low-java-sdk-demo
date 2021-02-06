package com.example.demo;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

@SpringBootTest
class DemoApplicationTests {

  DynamoDbClient client() {
    AwsCredentials credentials = AwsBasicCredentials.create("access-key", "secret-key");
    AwsCredentialsProvider provider = StaticCredentialsProvider.create(credentials);
    return DynamoDbClient.builder()
        .region(Region.of("ap-northeast-1"))
        .credentialsProvider(provider)
        .endpointOverride(URI.create("http://localhost:8000"))
        .build();
  }

  @Test
  void get() {
    Map<String, AttributeValue> key = new HashMap<>();
    key.put("id", AttributeValue.builder().n("1").build());
    key.put("email", AttributeValue.builder().s("foo@example.com").build());

    GetItemRequest request = GetItemRequest.builder()
        .key(key)  // プライマリキーを指定
        .tableName("foos")
        .build();

    Map<String, AttributeValue> item = client().getItem(request).item();

    System.out.println("item = " + item);
    System.out.println("id = " + item.get("id").n());  // "1"
    System.out.println("s of id = " + item.get("id").s());  // null
  }

  @Test
  void put() {
    Map<String, AttributeValue> item = new HashMap<>();
    item.put("id", AttributeValue.builder().n("2").build());
    item.put("email", AttributeValue.builder().s("email 2").build());
    item.put("age", AttributeValue.builder().n("23").build());

    PutItemRequest request = PutItemRequest.builder()
        .item(item)
        .tableName("foos")
        .returnValues(ReturnValue.ALL_OLD)
        .build();

    Map<String, AttributeValue> attributes = client().putItem(request)
        .attributes();  // PutItemRequest#returnValuesに設定したパラメータに応じる表示結果を取得
    System.out.println(attributes);  // {}
  }

  @Test
  void update() {
    Map<String, AttributeValue> key = new HashMap<>();
    key.put("id", AttributeValue.builder().n("1").build());
    key.put("email", AttributeValue.builder().s("foo@example.com").build());

    Map<String, String> expressionAttributeNames = new HashMap<>();
    expressionAttributeNames.put("#A", "name");
    Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
    expressionAttributeValues.put(":value", AttributeValue.builder().s("new name").build());

    UpdateItemRequest request = UpdateItemRequest.builder()
        .key(key)  // プライマリキーを指定
        .tableName("foos")
        .updateExpression("SET #A = :value")
        .expressionAttributeNames(expressionAttributeNames)
        .expressionAttributeValues(expressionAttributeValues)
        .returnValues(ReturnValue.ALL_NEW)
        .build();

    Map<String, AttributeValue> attributes = client().updateItem(request).attributes();
    System.out.println(attributes);
    //　attributes = {name=AttributeValue(S=new name), id=AttributeValue(N=1), email=AttributeValue(S=foo@example.com)}
  }

  @Test
  void delete() {
    Map<String, AttributeValue> key = new HashMap<>();
    key.put("id", AttributeValue.builder().n("1").build());
    key.put("email", AttributeValue.builder().s("foo@example.com").build());

    DeleteItemRequest request = DeleteItemRequest.builder()
        .key(key)  // プライマリキーを指定
        .tableName("foos")
        .returnValues(ReturnValue.ALL_OLD)
        .build();

    Map<String, AttributeValue> attributes = client().deleteItem(request).attributes();
    System.out.println(attributes);
  }

}
