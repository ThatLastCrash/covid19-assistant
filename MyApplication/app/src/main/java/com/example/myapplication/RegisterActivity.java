package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.calculator.DBOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private String realCode;
    private DBOpenHelper mDBOpenHelper;
    private Button mBtRegisteractivityRegister;
    private RelativeLayout mRlRegisteractivityTop;
    private ImageView mIvRegisteractivityBack;
    private LinearLayout mLlRegisteractivityBody;
    private EditText mEtRegisteractivityUsername;
    private EditText mEtRegisteractivityPassword1;
    private EditText mEtRegisteractivityPassword2;
    private EditText mEtRegisteractivityPhonecodes;
    private ImageView mIvRegisteractivityShowcode;
    private RelativeLayout mRlRegisteractivityBottom;
    boolean registerflag=false;
    private Toast sToast;
    private Toast fToast;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    timer.schedule(task,2000);
                    break;
            }
        }
    };
    Timer timer=new Timer();//Timer类是JDK中提供的一个定时器功能，使用时会在主线程之外开启一个单独的线程执行指定任务，任务可以执行一次或者多次
    //TimerTask实现runnable接口，TimerTask类表示在一个指定时间内执行的task
    TimerTask task=new TimerTask() {
        @Override
        public void run() {//跳转主界面的任务代码写在TimerTask的run()方法中
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();//销毁此Activity
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        mDBOpenHelper = new DBOpenHelper(this);


    }

    private void initView(){
        mBtRegisteractivityRegister = findViewById(R.id.bt_registeractivity_register);
        mRlRegisteractivityTop = findViewById(R.id.rl_registeractivity_top);
        mIvRegisteractivityBack = findViewById(R.id.iv_registeractivity_back);
        mLlRegisteractivityBody = findViewById(R.id.ll_registeractivity_body);
        mEtRegisteractivityUsername = findViewById(R.id.et_registeractivity_username);
        mEtRegisteractivityPassword1 = findViewById(R.id.et_registeractivity_password1);
        mEtRegisteractivityPassword2 = findViewById(R.id.et_registeractivity_password2);

        mIvRegisteractivityBack.setOnClickListener(this);

        mBtRegisteractivityRegister.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_registeractivity_back: //返回登录页面
                Intent intent1 = new Intent(this, LoginActivity.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.bt_registeractivity_register:    //注册按钮
                //获取用户输入的用户名、密码、验证码
                String username = mEtRegisteractivityUsername.getText().toString().trim();
                String password = mEtRegisteractivityPassword1.getText().toString().trim();
                //注册验证
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    sToast=Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT);
                    fToast=Toast.makeText(this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT);
                    register(username,password);
                } else {
                    Toast.makeText(this, "请输入你的用户名或密码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void register(final String username, final String password) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3000, TimeUnit.SECONDS)
                .callTimeout(3000, TimeUnit.SECONDS)
                .build();

        new Thread(){
            @Override
            public void run() {
                super.run();
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JSONObject json = new JSONObject();
                try {
                    json.put("username", username);
                    json.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody formBody = RequestBody.create(
                        MediaType.parse("application/json"), String.valueOf(json));


                Message message = new Message();

//                HashMap<String,String> paramsMap=new HashMap<>();
//                paramsMap.put("username",username);
//                paramsMap.put("password",password);
//                FormBody.Builder builder = new FormBody.Builder();
//                for (String key : paramsMap.keySet()) {
//                    //追加表单信息
//                    builder.add(key, paramsMap.get(key));
//                }
//                RequestBody formBody=builder.build();
                String path = "http://192.168.3.2:8080/user/register";
                Request request = new Request.Builder().url(path).post(formBody).build();
                Response response = null;
                try {
                    //同步请求
                    response = okHttpClient.newCall(request).execute();
                    System.out.println(response.isSuccessful());
                    String str = response.body().string();
                    str = str.substring(1, str.length() - 1);
                    System.out.println(str);
                    System.out.println(str.equals("success"));
                    if (response.isSuccessful()) {
                        if (str.equals("success")) {
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = "注册成功";
                            sToast.show();
                            handler.sendMessage(msg);
                        } else {
                            fToast.show();
                            System.out.println("服务器连接失败");
                            Log.d("fail", "失败 ");
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }.start();
        return;
    }
}

