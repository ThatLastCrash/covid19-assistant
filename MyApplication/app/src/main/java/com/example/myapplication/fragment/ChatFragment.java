package com.example.myapplication.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.ChatActivity;
import com.example.myapplication.R;
import com.example.myapplication.kdxf_asr_demo.ASRModule;
import com.example.myapplication.kdxf_asr_demo.TTSModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    private RecyclerView recyclerview;
    private ChatAdapter recyclerview_adapter;
    private List<Map<String, Object>> mDataList;
    private EditText editText;
    private Button btn_sendMsg;
    private Button btn_volume;
    private ListView news_list;
    private Handler mHandler;

    private String username;
    private String signature;
    private String avatar_url;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private ASRModule asrModule=new ASRModule(getActivity());
    private TTSModule ttsModule=new TTSModule(getActivity());

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String label) {
//        Bundle args = new Bundle();
//        args.putString("label", label);
        ChatFragment fragment = new ChatFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    //在这里设置导航栏
    @Override
    public void onStart() {
        super.onStart();
        preferences = getActivity().getSharedPreferences("state", Activity.MODE_PRIVATE);
        username=preferences.getString("username", "");
        signature=preferences.getString("signature", "");
        avatar_url=preferences.getString("avatar", "");
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
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVolumeMode();
    }

    private void initVolumeMode() {

        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        asrModule.mIat_Init();
        /**/
        ttsModule.mTts_Init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
//        news_list=(ListView) view.findViewById(R.id.news_list);
//
//        SimpleAdapter adapter=new SimpleAdapter(getActivity(),getData(),R.layout.fragment_my,
//                new String[]{"robot_title","robot_info","robot_img"},
//                new int[]{R.id.robot_title,R.id.robot_info,R.id.robot_img});
//        news_list.setAdapter(adapter);

        //获得Recyclerview
        recyclerview = (RecyclerView) view.findViewById(R.id.chat_list);
        //输入框
        editText=view.findViewById(R.id.edit_text);
        //发送消息按钮
        btn_sendMsg=view.findViewById(R.id.btn_sendMsg);
        //语音按钮
        btn_volume=view.findViewById(R.id.btn_volume);
        //创建adapter类的对象
        mDataList = getData();

        //这步骤必须有，这是选择RecylerView的显示方式
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //recyclerview.setItemAnimator(new DefaultItemAnimator());

        recyclerview_adapter = new ChatAdapter(getActivity(), mDataList);
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
                        Toast.makeText(getContext(),s,Toast.LENGTH_SHORT).show();
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
        // Inflate the layout for this fragment
        return view;
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
                    String json="";
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

//        map = new HashMap<String, Object>();
//        map.put("msg_right", "你好你好");
//        map.put("user_img_right", avatar_url);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("msg_left", "憨憨");
//        map.put("user_img_left", R.drawable.lightman);
//        list.add(map);
//
//        map = new HashMap<String, Object>();
//        map.put("msg_right", "你才是憨憨");
//        map.put("user_img_right", avatar_url);
//        list.add(map);
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
                } else {
                    JSONObject qam = answer.getJSONObject("0");
                    String a = qam.getString("answer");
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("msg_left", a);
                    map.put("user_img_left", R.drawable.lightman);
                    recyclerview_adapter.addData(map);
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
                            /*
                            //下面的遍历会报错？好像是等待时间太长卡死了？为什么会这样
                            int cnt = 1;
                            for (Iterator<String> it = answer.keys(); it.hasNext(); ) {
                                if (res.getString("cnt") != "0") {
                                    continue;
                                }
                                String key = it.next();
                                JSONObject qam = answer.getJSONObject(key);
                                //取QA的内容
                                String q = qam.getString("question");
                                String a = qam.getString("answer");
                                String score = qam.getString("score");

                                Map<String, Object> m = new HashMap<String, Object>();
                                m.put("remind_left", String.valueOf(cnt) + ":" + q);
                                m.put("user_img_left", R.drawable.img2);
                                cnt++;
                                recyclerview_adapter.addData(m);
                                recyclerview.scrollToPosition(recyclerview_adapter.getItemCount() - 1);
                            }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
    }

    /**
     * 提示消息
     * @param msg
     */
    private void showMsg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}


