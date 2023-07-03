package com.example.myapplication.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.myapplication.ChatActivity;
import com.example.myapplication.R;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment implements ViewSwitcher.ViewFactory, View.OnTouchListener{
    private RecyclerView recyclerview;
    private NewsListAdapter recyclerview_adapter;
    private List<Map<String,Object>> mDataList;
    private Handler mHandler;
    private Button btn_left;
    private Button btn_right;
    private TextSwitcher textSwitcher;
    private TextView tv_update_time;
    private String[] strs = new String[] {
            "各省疫情"
    };
    /**
     * 当前选中的文字id序号
     */
    private int currentPosition;
    /**
     * 按下点的X坐标
     */
    private float downX;

    public NewsFragment() {
        // Required empty public constructor
    }

    public static NewsFragment newInstance(String label) {
        Bundle args = new Bundle();
        args.putString("label", label);
        NewsFragment fragment = new NewsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    //在这里设置导航栏
    @Override
    public void onStart() {
        super.onStart();
        String label = getArguments().getString("label");
        TextView text = getView().findViewById(R.id.tv_tab);
        text.setText(label);

        //text.setBackgroundColor(Color.rgb((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_news, container, false);
        //获得Recyclerview
        recyclerview = (RecyclerView)view.findViewById(R.id.news_list);
        //创建adapter类的对象
        mDataList=getData();


        FlexboxLayoutManager manager = new FlexboxLayoutManager();
        //设置主轴排列方式
        manager.setFlexDirection(FlexDirection.ROW);
//        //设置是否换行
//        manager.setFlexWrap(FlexWrap.WRAP);
//        manager.setAlignItems(AlignItems.STRETCH);

        //这步骤必须有，这是选择RecylerView的显示方式
        recyclerview.setLayoutManager(manager);
        //recyclerview.setItemAnimator(new DefaultItemAnimator());

        WindowManager wm = getActivity().getWindowManager();
        Display d = wm.getDefaultDisplay();
        recyclerview_adapter = new NewsListAdapter(getActivity(), mDataList,d);
        // 设置监听事件
        recyclerview_adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position){

            }
        });
        //将对象作为参数通过setAdapter方法设置给recylerview；"
        recyclerview.setAdapter(recyclerview_adapter);

        tv_update_time=view.findViewById(R.id.tv_update_time);


        btn_left=view.findViewById(R.id.btn_left);
        btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPosition > 0){
                    currentPosition --;
                    textSwitcher.setCurrentText(strs[currentPosition]);
                }else{
                    Toast.makeText(getContext(), "已经是第一", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_right=view.findViewById(R.id.btn_right);
        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPosition < strs.length - 1){
                    currentPosition ++ ;
                    textSwitcher.setCurrentText(strs[currentPosition]);

                }else{
                    Toast.makeText(getContext(), "到了最后", Toast.LENGTH_SHORT).show();
                }
            }
        });

        textSwitcher=view.findViewById(R.id.textSwitcher);
        textSwitcher.setFactory(this);
        textSwitcher.setOnTouchListener(this);
        textSwitcher.setInAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.message_in_anim));
        textSwitcher.setInAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.message_out_anim));


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
        sendMsg();
        return view;
    }
    private void sendMsg() {
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
                        .url("http://192.168.3.2:9090/crawler")
                        .build();
                Response response = null;
                try {
                    //同步请求
                    response = okHttpClient.newCall(request).execute();
                    System.out.println(response);
//                    System.out.println(response.body().string());

                    String json="";
                    String str=response.body().string();
                    System.out.println(str);
                    JSONObject res = new JSONObject(str);
                    if (response.isSuccessful()) {
                        Message msg=new Message();
                        msg.what=0;
                        msg.obj=res;
                        mHandler.sendMessage(msg);
                        Log.d("succuss", "后端开始爬取");
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
    private void updateData(Message msg) {
        JSONObject res = (JSONObject) msg.obj;
        System.out.println("res:"+res);
        try {
            JSONObject answer = res.getJSONObject("total_data");
            Map<String, Object> map = new HashMap<String, Object>();
            Iterator it = answer.keys();
            String key = null;//键
            int cnt=1;
            while(it.hasNext()) {//遍历JSONObject
                key = (String) it.next().toString();
                JSONObject val= answer.getJSONObject(key);
                System.out.println("key:"+key+"     val:"+val.getString("title"));
                map = new HashMap<String, Object>();
                map.put("news_title", val.getString("title"));
                map.put("news_data", val.getString("data"));
                map.put("news_last", val.getString("last"));
                recyclerview_adapter.addData(map);
                cnt++;
            }

            JSONArray each = res.getJSONArray("each_data");
            String s=each.toString();
            strs=s.substring(1,s.length()-1).split(",");
            for(int i=0;i<strs.length;i++)
            {
                strs[i]=strs[i].substring(1,strs[i].length()-1);
            }
            System.out.println("strs:"+strs);

            tv_update_time.setText(res.getString("time"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<Map<String,Object>> getData(){
        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
        Map<String,Object> map=new HashMap<String,Object>();
//        map.put("news_title","全国累计确诊");
//        map.put("news_data","100");
//        map.put("news_last","50");
//        list.add(map);
//
//        map=new HashMap<String,Object>();
//        map.put("news_title","香港累计确诊");
//        map.put("news_data","50");
//        map.put("news_last","20");
//        list.add(map);
//
//        map=new HashMap<String,Object>();
//        map.put("news_title","全国累计确诊");
//        map.put("news_data","100");
//        map.put("news_last","50");
//        list.add(map);
//
//        map=new HashMap<String,Object>();
//        map.put("news_title","全国累计确诊");
//        map.put("news_data","100");
//        map.put("news_last","50");
//        list.add(map);
//
//        map=new HashMap<String,Object>();
//        map.put("news_title","全国累计确诊");
//        map.put("news_data","100");
//        map.put("news_last","50");
//        list.add(map);
//
//        map=new HashMap<String,Object>();
//        map.put("news_title","全国累计确诊");
//        map.put("news_data","100");
//        map.put("news_last","50");
//        list.add(map);

        return list;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:{
                //手指按下的X坐标
                downX = event.getX();
                break;
            }
            case MotionEvent.ACTION_UP:{
                float lastX = event.getX();
                //抬起的时候的X坐标大于按下的时候移动
                if(lastX > downX){
                    if(currentPosition > 0){
                        currentPosition --;
                        textSwitcher.setCurrentText(strs[currentPosition]);
                    }else{
                        Toast.makeText(getContext(), "已经是第一", Toast.LENGTH_SHORT).show();
                    }
                }

                if(lastX < downX){
                    if(currentPosition < strs.length - 1){
                        currentPosition ++ ;
                        textSwitcher.setCurrentText(strs[currentPosition]);

                    }else{
                        Toast.makeText(getContext(), "到了最后", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            break;
        }

        return true;
    }

    @Override
    public View makeView() {
        final TextView textView = new TextView(getContext());
        textView.setTextSize(30);
        textView.setLayoutParams(new TextSwitcher.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        textView.setTextColor(Color.rgb(255, 0, 0));
        textView.setText(strs[currentPosition]);
        return textView;
    }
}