package com.example.myapplication;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.calculator.DBOpenHelper;
import com.example.myapplication.pojo.User;
import com.example.myapplication.receiver.IncomingSMSReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private DBOpenHelper mDBOpenHelper;
    private CircleImageView circleImageView;
    private TextView btn_login;
    private CheckBox cb_rm;
    private TextView tv_register;
    private EditText et_username;
    private EditText et_password;

    private Toast sToast;
    private Toast fToast;
    private String username_now;
    private String signature_now;
    private String avatar_now;
    private String password_now;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 0:
                    SharedPreferences sharedpreferences = getApplication().getSharedPreferences("state", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("username", username_now);//第一个参数相当于一个Key，后面取出数据时需要用到这个值来索引
                    editor.putString("signature", signature_now);
                    editor.putString("avatar", avatar_now);
                    editor.putString("password", password_now);
                    if(cb_rm.isChecked()){
                        editor.putString("is_rm", "yes");
                    }
                    else {
                        editor.putString("is_rm", "no");
                    }
                    editor.commit();
                    timer.schedule(task,1000);
                    break;
            }
        }
    };
    Timer timer=new Timer();//Timer类是JDK中提供的一个定时器功能，使用时会在主线程之外开启一个单独的线程执行指定任务，任务可以执行一次或者多次
    //TimerTask实现runnable接口，TimerTask类表示在一个指定时间内执行的task
    TimerTask task=new TimerTask() {
        @Override
        public void run() {//跳转主界面的任务代码写在TimerTask的run()方法中
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            //用数据捆传递数据
            Bundle bundle = new Bundle();
            bundle.putString("username", username_now);
            bundle.putString("signature", signature_now);
            bundle.putString("avatar", avatar_now);
            System.out.println("username_now"+username_now);
            System.out.println("signature_now"+signature_now);
            System.out.println("avatar_now"+avatar_now);
            //把数据捆设置改意图
            intent.putExtras(bundle);
            startActivity(intent);
            finish();//销毁此Activity
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            int option = window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_login);
        try {
            initView();
        } catch (IOException e) {
            e.printStackTrace();
        }
        preferences = getSharedPreferences("state", Activity.MODE_PRIVATE);
        username_now=preferences.getString("username", "");
        password_now=preferences.getString("password", "");
        String is_rm=preferences.getString("is_rm", "");
        if(is_rm.equals("yes")){
            if(!username_now.equals("")){
                et_username.setText(username_now);
            }
            if(!password_now.equals("")){
                et_password.setText(password_now);
            }
        }
        //注册一个广播接收者，当另一个app中支付成功或失败会发一条广播
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.paystate.success");
//        intentFilter.addAction("com.paystate.failure");
//        PayStateReceiver payStateChangeReceiver = new PayStateReceiver();
//        registerReceiver(payStateChangeReceiver, intentFilter);

//        //网络状态变化
//        IntentFilter netIntentFilter = new IntentFilter();
//        netIntentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        NetStateReceiver netStateReceiver = new NetStateReceiver();
//        registerReceiver(netStateReceiver, netIntentFilter);
//
        requestPower();
//        //SMS广播
//        final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
//        IntentFilter filter = new IntentFilter(SMS_RECEIVED);
//        filter.setPriority(800);
//        BroadcastReceiver receiver = new IncomingSMSReceiver();
//        registerReceiver(receiver, filter);

        //mDBOpenHelper = new DBOpenHelper(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages((null));
    }

    public void requestPower() {
        //判断是否已经赋予权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS)) {//这里可以写个对话框之类的项向用户解释为什么要申请权限，并在对话框的确认键后续再次申请权限.它在用户选择"不再询问"的情况下返回false
            } else {
                //申请权限，字符串数组内是一个或多个要申请的权限，1是申请权限结果的返回参数，在onRequestPermissionsResult可以得知申请结果
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_SMS,
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,

                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.INTERNET,
                                Manifest.permission.WRITE_SETTINGS}, 1);
            }
        }
    }

//    //支付状态广播接收者
//    class PayStateReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(action.equals("com.paystate.success")){
//                Toast.makeText(context, "收到支付成功的广播", Toast.LENGTH_SHORT).show();
//            }
//            else if(action.equals("com.paystate.failure"))
//            {
//                Toast.makeText(context, "收到支付失败的广播", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//    //网络状态广播接收者
//    class NetStateReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
//                Toast.makeText(context, "网络状态发生变化了", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
    private void initView() throws IOException {
        // 初始化控件
        circleImageView=findViewById(R.id.civ_1);
        btn_login = findViewById(R.id.btn_login);
        cb_rm=findViewById(R.id.cb_rm);
        tv_register=findViewById(R.id.tv_register);
        et_username=findViewById(R.id.et_username);
        et_password=findViewById(R.id.et_password);

        // 设置点击事件监听器
        btn_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);

    }

    public void onClick(View view) {
        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        switch (view.getId()) {
            // 跳转到注册界面
            case R.id.tv_register:
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    sToast=Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT);
                    fToast=Toast.makeText(this, "注册失败", Toast.LENGTH_SHORT);
                    register(username,password);
                } else {
                    Toast.makeText(this, "请输入你的用户名或密码", Toast.LENGTH_SHORT).show();
                }
                break;
//                startActivity(new Intent(this, RegisterActivity.class));
                //finish();
            case R.id.btn_login:
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    sToast=Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT);
                    fToast=Toast.makeText(this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT);
                    login(username,password);
                } else {
                    Toast.makeText(this, "请输入你的用户名或密码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void login(final String username, final String password) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3000, TimeUnit.SECONDS)
                .callTimeout(3000, TimeUnit.SECONDS)
                .build();

        new Thread() {
            @Override
            public void run() {
                super.run();

                // @Headers({"Content-Type:application/json","Accept: application/json"})//需要添加头
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
                String path = "http://192.168.3.2:8080/user/login";
                Request request = new Request.Builder().url(path).post(formBody).build();
                Response response = null;

//                User u=new User(username,password);
//                RequestBody body = new FormBody.Builder()
//                        .add("username", username)
//                        .add("password", password)
//                        .add("type", "user").build();

                // 3 创建请求方式
//                Request request = new Request.Builder().url(path).post(body).build();

//                Request request = new Request.Builder()
//                        .url("http://192.168.3.2:8080/loginServlet?username="+username+"&password="+password+"&type=user")
//                        .build();
                try {
                    //同步请求
                    System.out.println(request);
                    response = okHttpClient.newCall(request).execute();
                    System.out.println(response.isSuccessful());
                    JSONObject resp = new JSONObject(response.body().string());
                    System.out.println(resp);
                    if (response.isSuccessful()) {
                        if (resp.getString("code").equals("1")) {
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = resp.getJSONObject("data");
                            username_now=resp.getJSONObject("data").getString("username");
                            signature_now=resp.getJSONObject("data").getString("signature");
                            avatar_now=resp.getJSONObject("data").getString("avatar");
                            password_now=resp.getJSONObject("data").getString("password");
                            sToast.show();
                            handler.sendMessage(msg);
                        } else {
                            fToast.show();
                            System.out.println("服务器连接失败");
                            Log.d("fail", "失败 ");
                            return;
                        }
                    }
                    return;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return;
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
//                            Message msg = new Message();
//                            msg.what = 1;
//                            msg.obj = "注册成功";
//                            handler.sendMessage(msg);
                            sToast.show();
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

