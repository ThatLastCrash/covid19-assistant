package com.nnu.chatrobot.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nnu.chatrobot.entity.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.*;

@RestController
@Slf4j
public class ChatController {

    @GetMapping("/chat")
    public Object chat(@RequestParam("question")String question) throws UnsupportedEncodingException {

        String link="http://0.0.0.0:5005/webhooks/rest/webhook";
        MultiValueMap<String, String> stringMultiValueMap = new LinkedMultiValueMap<>();
//        stringMultiValueMap.add("message", question);
//        stringMultiValueMap.add("sender", question);
        System.out.println(question);
        String robotanswer = PostToRasa(link,question);
//        String robotanswer = this.sendPOSTRequest(link, stringMultiValueMap);

//        String robotanswer = getContent(link,"UTF-8");
        //判断User
        System.out.println(robotanswer);
        robotanswer = robotanswer.substring(1,robotanswer.length()-1);
        JSONObject jsonObject = JSON.parseObject(robotanswer);
        System.out.println(jsonObject);
        System.out.println(String.valueOf(jsonObject));
        //神之一手
        return JSON.parse(robotanswer);
    }
    public String getContent(String link,String encoding)
    {
        //定义接口地址
        //String link="http://api.qingyunke.com/api.php?key=free&appid=0&msg=你好";
        //String link="https://www.qq.com";
        String text;
        StringBuilder sb=new StringBuilder();


        String str = this.sendPOSTRequest(link, null).toString();
//        try {
            //建立与API接口的网络连接
//            String line_utf8=new String(link.getBytes(StandardCharsets.UTF_8));
//            URL url=new URL(line_utf8);
//            //打开链接
//            URLConnection uc=url.openConnection();
//            //读取内容
//            InputStream inputStream=uc.getInputStream();
//            //创建缓冲流
////            InputStreamReader inputStreamReader=new InputStreamReader(inputStream, StandardCharsets.UTF_8);
//            InputStreamReader inputStreamReader=new InputStreamReader(inputStream, StandardCharsets.UTF_8);
//            BufferedReader bf=new BufferedReader(inputStreamReader);
//
//            //正式读取内容
//            while((text=bf.readLine())!=null)
//            {
//                sb.append(text);
//            }
//            bf.close();

//            str= sb.toString();
//            System.out.println(str);
//            String strlist[]=str.split(":");
//            str=strlist[strlist.length-1];
//            str=str.substring(1,str.length()-2);
//            inputStreamReader.close();
//            inputStream.close();
//            System.out.println(str);

//        } catch (MalformedURLException e)
//        {
//            System.out.println("您访问的网页不存在，请检查");
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return str;
    }
    public static String sendPOSTRequest(String url, MultiValueMap<String, String> params) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        // 执行HTTP请求，将返回的结构使用String类格式化
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        return response.getBody().toString();
    }
    public String PostToRasa(String url, String mes) {
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
        return ans;
    }
}
