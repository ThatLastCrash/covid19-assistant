package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.fragment.ChatAdapter;
import com.example.myapplication.fragment.DetailFragment;
import com.example.myapplication.fragment.OnItemClickListener;
import com.example.myapplication.fragment.TabFragment;
import com.example.myapplication.kdxf_asr_demo.ASRModule;
import com.example.myapplication.kdxf_asr_demo.TTSModule;
import com.google.android.material.tabs.TabLayout;
import com.iflytek.cloud.SpeechConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class ChatActivity extends AppCompatActivity{
    private List<Fragment> mFragments;

    private TextView text_title;
    private TextView text_info;
    private CircleImageView circleImageView;
    private String username;
    private String signature;
    private String avatar_url;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private RecyclerView recyclerview;
    private ChatAdapter recyclerview_adapter;
    private List<Map<String, Object>> mDataList;
    private EditText editText;
    private Button btn_sendMsg;
    private Button btn_volume;
    private ListView news_list;
    private Handler mHandler;

    private ASRModule asrModule=new ASRModule(this);
    private TTSModule ttsModule=new TTSModule(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //状态栏全透明
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        }
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
        initVolumeMode();

        //"state"是存储的文件名，Context.MODE_PRIVATE表示该文件只能被创建他的应用访问
        SharedPreferences sharedpreferences = getApplication().getSharedPreferences("state", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("isLogin", true);//第一个参数相当于一个Key，后面取出数据时需要用到这个值来索引
        editor.commit();
        preferences = getSharedPreferences("state", Activity.MODE_PRIVATE);
        username=preferences.getString("username", "");
        signature=preferences.getString("signature", "");
        avatar_url=preferences.getString("avatar", "");
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setContentView(R.layout.activity_chat);
        Toast.makeText(this,"我是chat",Toast.LENGTH_SHORT);
        initViews();
        initEvents();
        initDatas();
        text_title.setText(username);
        text_info.setText(signature);
//        String url = "https://int-mian.oss-cn-hangzhou.aliyuncs.com/android/img/user/1/avatar.jpg";
        Glide.with(this).load(avatar_url).into(circleImageView);
    }

    private void initViews(){
        text_title=findViewById(R.id.text_title);
        text_info=findViewById(R.id.text_info);
        circleImageView=findViewById(R.id.avatar);
    }
    public void initEvents(){
    }
    public void initDatas(){
//        mFragments=new ArrayList<>();
//        mFragments.add(new WelcomeFragment());
//        mFragments.add(new NewsFragment());
//        mFragments.add(new DetailFragment());
//        mFragments.add(new WelcomeFragment());
    }
    //在这里设置导航栏
    @Override
    public void onStart() {
        super.onStart();
        preferences = this.getSharedPreferences("state", Activity.MODE_PRIVATE);
        username=preferences.getString("username", "");
        signature=preferences.getString("signature", "");
        avatar_url=preferences.getString("avatar", "");
        onCreateView();
//        String label = getArguments().getString("label");
//        TextView text = getView().findViewById(R.id.tv_tab);
//        text.setText(label);
        //text.setBackgroundColor(Color.rgb((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != asrModule.mIat) {
            // 退出时释放连接
            asrModule.mIat.cancel();
            asrModule.mIat.destroy();
        }
        if(null!=ttsModule.mTts){
            // 退出时释放连接
            ttsModule.mTts.destroy();
        }
    }

    private void initVolumeMode() {

        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        asrModule.mIat_Init();
        /**/
        ttsModule.mTts_Init();
    }

    public void onCreateView() {

        //获得Recyclerview
        recyclerview = (RecyclerView)findViewById(R.id.chat_list);
        //输入框
        editText=findViewById(R.id.edit_text);
        //发送消息按钮
        btn_sendMsg=findViewById(R.id.btn_sendMsg);
        //语音按钮
        btn_volume=findViewById(R.id.btn_volume);
        //创建adapter类的对象
        mDataList = getData();

        //这步骤必须有，这是选择RecylerView的显示方式
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //recyclerview.setItemAnimator(new DefaultItemAnimator());

        recyclerview_adapter = new ChatAdapter(this, mDataList);
        // 设置监听事件
        recyclerview_adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // 在这里处理item的点击事件，position就是点击的item
                // 如点击是删除该项目
//                    Toast.makeText(getContext(), "删除删除", Toast.LENGTH_LONG).show();
//                    recyclerview_adapter.remove(position);
//                System.out.println(view.getId());
                switch (view.getId()){
                    case R.id.remind_left:
                        TextView textView=(TextView) view;
                        String s=textView.getText().toString();
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("msg_right",s);
                        map.put("user_img_right",avatar_url);
                        recyclerview_adapter.addData(map);
                        sendMsg(s);
                        recyclerview.scrollToPosition(recyclerview_adapter.getItemCount()-1);
                        Toast.makeText(ChatActivity.this,s,Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        //将对象作为参数通过setAdapter方法设置给recylerview；"
        recyclerview.setAdapter(recyclerview_adapter);
        initEvent();

//        mHandler = new Handler();
        mHandler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        //完成主界面更新,拿到数据
                        updateData(msg);
                        break;
                }
            }
        };
    }


    public void initEvent(){
        btn_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asrModule.solve_onClick();
                Handler handler=new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        editText.setText(asrModule.getASR_Result());
                    }
                },4000);
            }
        });
        btn_sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("msg_right",editText.getText());
                map.put("user_img_right",avatar_url);
                recyclerview_adapter.addData(map);
                sendMsg(editText.getText().toString());
                recyclerview.scrollToPosition(recyclerview_adapter.getItemCount()-1);
                editText.setText("");
            }
        });
    }

    private void sendMsg(final String question) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3000, TimeUnit.SECONDS)
                .callTimeout(3000, TimeUnit.SECONDS)
                .build();

        new Thread(){
            @Override
            public void run() {
                super.run();
                Request request = new Request.Builder()
//                        .url("http://192.168.43.83:8080/tomcat/loginServlet?username="+username+"&password="+password+"&type=user")
                        .url("http://192.168.3.2:8080/chat?question="+question)
                        .build();
                Response response = null;
                try {
                    //同步请求
                    response = okHttpClient.newCall(request).execute();
                    System.out.println(response);
//                    System.out.println(response.body().string());

                    String json="{\"data\":{\"0\":{\"score\":0.2773500981126146,\"question\":\"进入工作场所前做好哪些准备\",\"answer\":\"进入工作场所前正确佩戴口罩并自觉接受体温检测，体温正常可入内工作。若出现发热等症状，请勿进入工作场所，并回家休息观察，根据身体状况及时就诊，期间向单位报告有关情况。\"},\"1\":{\"score\":0.2672612419124244,\"question\":\"乘坐火车、飞机时如何做好防护\",\"answer\":\"全程正确佩戴口罩，最好备两副口罩，一旦口罩变湿、脏污和变形，需及时更换。乘车时可戴眼镜和帽子，女士长头发可扎起可用一次性消毒湿巾擦拭小桌板或公共物品后再使用。上车后或在机舱内尽量不要走动。如果是短途，尽量不要进食，以免摘除口罩。如果需要进食和用餐，请注意饮食卫生和手卫生。注意手卫生。途中避免用手直接触摸口鼻眼，如果确需用手，要先洗手或用消毒湿巾、手消毒液对手清洁后方可进行。注意咳嗽和喷嚏礼仪。咳嗽和喷嚏时，请用纸巾或手肘捂住口鼻，并尽快洗手。上完厕所后，盖上马桶盖再冲水，并及时洗手。\"},\"2\":{\"score\":0.242535625036333,\"question\":\"乘坐私家车上下班，如何做好个人防护\",\"answer\":\"自驾上下班，要适当增加开窗通风次数。如果多人乘坐私家车，建议全程佩戴口罩。私家车内部及车把手建议每日用75%的酒精或消毒湿巾擦拭一次。\"},\"3\":{\"score\":0.22941573387056172,\"question\":\"骑自行车或走路上下班，如何做好个人防护\",\"answer\":\"上下班途中要全程佩戴口罩，并注意尽可能与其他行人保持1米以上安全距离。\"},\"4\":{\"score\":0.21821789023599236,\"question\":\"乘坐出租车、网约车时，如何做好个人防护\",\"answer\":\"务必全程佩戴口罩，尽量坐在后排，避免与司机交谈。上车前尽量用消毒湿巾擦拭扶手、 车把手等可能身体接触到的物品，尽量减少用手触摸车上其他物品 上车后立即将车窗打开， 保持通风。避免用手直接触摸口鼻眼。注意咳嗽和喷嚏礼仪。到达目的地后及时洗手。\"}},\"cnt\":0}";
                    System.out.println(json);
                    String str=response.body().string();
                    System.out.println(str);
                    JSONObject res = new JSONObject(str);
                    if (response.isSuccessful()) {
                        Message msg=new Message();
                        msg.what=0;
                        msg.obj=res;
                        mHandler.sendMessage(msg);
                        Log.d("succuss", "后端收到消息并回复 ");
                    } else{
                        System.out.println("服务器连接失败");
                        Log.d("fail", "失败 ");
                        return;
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
                return;
            }
        }.start();
        return;
    }
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("msg_left", "哈喽哈喽");
        map.put("user_img_left", R.drawable.lightman);
        list.add(map);
        return list;
    }
    private void updateData(Message msg) {
        JSONObject res = (JSONObject) msg.obj;
        System.out.println("res:"+res);
        try {
            JSONObject answer = res.getJSONObject("data");
            if (res.getString("cnt").equals("0")) {
                String reply = "人家也不知道捏，来和我一起扩充知识库吧！";
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("msg_left", reply);
                map.put("user_img_left", R.drawable.lightman);
                recyclerview_adapter.addData(map);

                /*测试更换合成语言内容*/
                ttsModule.setContent(reply);
                ttsModule.solve_onClick();
            } else {
                JSONObject qam = answer.getJSONObject("0");
                String a = qam.getString("answer");
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("msg_left", a);
                map.put("user_img_left", R.drawable.lightman);
                recyclerview_adapter.addData(map);

                /*测试更换合成语言内容*/
                ttsModule.setContent(a);
                ttsModule.solve_onClick();
            }
            String q = "你可能想问的问题：";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("remind_left", q);
            map.put("user_img_left", R.drawable.lightman);
            recyclerview_adapter.addData(map);
            Iterator it = answer.keys();
            String key = null;//键
            int cnt=1;
            while(it.hasNext()) {//遍历JSONObject
                key = (String) it.next().toString();
                JSONObject val= answer.getJSONObject(key);
                System.out.println("key:"+key+"     val:"+val.getString("question"));
                map = new HashMap<String, Object>();
                map.put("remind_left", String.valueOf(cnt)+":"+val.getString("question"));
                map.put("user_img_left", R.drawable.lightman);
                recyclerview_adapter.addData(map);
                cnt++;
            }
            recyclerview.scrollToPosition(recyclerview_adapter.getItemCount() - 1);
            //遍历QA对

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 提示消息
     * @param msg
     */
    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}


//public class ChatActivity extends AppCompatActivity{
//    private List<Fragment> mFragments;
//
//    private TextView text_title;
//    private TextView text_info;
//    private CircleImageView circleImageView;
//    private String username;
//    private String signature;
//    private String avatar_url;
//    SharedPreferences preferences;
//    SharedPreferences.Editor editor;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //状态栏全透明
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        }
//        //"state"是存储的文件名，Context.MODE_PRIVATE表示该文件只能被创建他的应用访问
//        SharedPreferences sharedpreferences = getApplication().getSharedPreferences("state", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.putBoolean("isLogin", true);//第一个参数相当于一个Key，后面取出数据时需要用到这个值来索引
//        editor.commit();
//        preferences = getSharedPreferences("state", Activity.MODE_PRIVATE);
//        username=preferences.getString("username", "");
//        signature=preferences.getString("signature", "");
//        avatar_url=preferences.getString("avatar", "");
////        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//
//        setContentView(R.layout.activity_chat);
//        Toast.makeText(this,"我是chat",Toast.LENGTH_SHORT);
//        initViews();
//        initEvents();
//        initDatas();
//        text_title.setText(username);
//        text_info.setText(signature);
////        String url = "https://int-mian.oss-cn-hangzhou.aliyuncs.com/android/img/user/1/avatar.jpg";
//        Glide.with(this).load(avatar_url).into(circleImageView);
//    }
//
//    private void initViews(){
//        text_title=findViewById(R.id.text_title);
//        text_info=findViewById(R.id.text_info);
//        circleImageView=findViewById(R.id.avatar);
//    }
//    public void initEvents(){
//    }
//    public void initDatas(){
////        mFragments=new ArrayList<>();
////        mFragments.add(new WelcomeFragment());
////        mFragments.add(new NewsFragment());
////        mFragments.add(new DetailFragment());
////        mFragments.add(new WelcomeFragment());
//    }
//
//}