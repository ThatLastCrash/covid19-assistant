import com.alibaba.fastjson.JSONObject;
import com.nnu.chatrobot.entity.Message;
import com.nnu.chatrobot.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ChatRobotApplicationTest.class)
public class ChatRobotApplicationTest {

    @Test
    public void test2() {
        RestTemplate restTemplate = new RestTemplate();

        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String url = "http://0.0.0.0:5005/webhooks/rest/webhook";
//        Message mes = new Message("我叫金玉洲");
//        String jsonString = JSONObject.toJSONString();
        String jsonString = "{\"message\":\"你好\"}";
        System.out.println(jsonString);//{"age":10,"name":"sansan"}

        HttpEntity<String> entity = new HttpEntity<String>(jsonString, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
        String user = responseEntity.getBody();//{"msg":"调用成功！","code":1}
        System.out.println(user);

    }

    @Test
    public void PostToRasa() {
        String url = "http://0.0.0.0:5005/webhooks/rest/webhook";
        String mes = "你好";
        RestTemplate restTemplate = new RestTemplate();

        //创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        Message mes = new Message("我叫金玉洲");
//        String jsonString = JSONObject.toJSONString();
        mes = mes.replaceAll("[\\pP‘’“”]", "");
        mes = "{\"message\":\""+mes+"\"}";
        System.out.println(mes);//{"age":10,"name":"sansan"}

        HttpEntity<String> entity = new HttpEntity<String>(mes, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
        String ans = responseEntity.getBody();
        System.out.println(ans);
    }
}
