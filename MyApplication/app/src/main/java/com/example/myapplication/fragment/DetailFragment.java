package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.ChatActivity;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class DetailFragment extends Fragment {
    private RecyclerView recyclerview;
    private ChatListAdapter recyclerview_adapter;
    private List<Map<String,Object>> mDataList;

    private ListView news_list;

    public DetailFragment() {
        // Required empty public constructor
    }
    public static DetailFragment newInstance(String label) {
        Bundle args = new Bundle();
        args.putString("label", label);
        DetailFragment fragment = new DetailFragment();
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
        View view=inflater.inflate(R.layout.fragment_detail,container,false);
//        news_list=(ListView) view.findViewById(R.id.news_list);
//
//        SimpleAdapter adapter=new SimpleAdapter(getActivity(),getData(),R.layout.fragment_my,
//                new String[]{"robot_title","robot_info","robot_img"},
//                new int[]{R.id.robot_title,R.id.robot_info,R.id.robot_img});
//        news_list.setAdapter(adapter);

        //获得Recyclerview
        recyclerview = (RecyclerView)view.findViewById(R.id.news_list);
        //创建adapter类的对象
        mDataList=getData();

        //这步骤必须有，这是选择RecylerView的显示方式
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        //recyclerview.setItemAnimator(new DefaultItemAnimator());

        recyclerview_adapter = new ChatListAdapter(getActivity(), mDataList);
        // 设置监听事件
        recyclerview_adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position){
                Intent intent=new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
//                ChatFragment chatFragment = new ChatFragment();
//                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//                transaction.add(R.id.id_chat_layout, chatFragment).commit();

                    // 在这里处理item的点击事件，position就是点击的item
                    // 如点击是删除该项目
//                    Toast.makeText(getContext(), "删除删除", Toast.LENGTH_LONG).show();
//                    recyclerview_adapter.remove(position);

//                System.out.println(view.getId());
//                    switch(view.getId()){
//                        case R.id.robot_img:
//                            Toast.makeText(getContext(), "图片", Toast.LENGTH_LONG).show();
//                            break;
//                        case R.id.robot_title:
//                            Toast.makeText(getContext(), "标题", Toast.LENGTH_LONG).show();
//                            break;
//                        case R.id.robot_info:
//                            Toast.makeText(getContext(), "详细信息", Toast.LENGTH_LONG).show();
//                            break;
//                        case R.id.news_container:
//                            Toast.makeText(getContext(), "container", Toast.LENGTH_LONG).show();
//                            break;
//                    }
                }
            });

        //将对象作为参数通过setAdapter方法设置给recylerview；"
        recyclerview.setAdapter(recyclerview_adapter);
        // Inflate the layout for this fragment
        return view;
    }

    private List<Map<String,Object>> getData(){
        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("robot_title","天气系列");
        map.put("robot_info","在这里您可以询问天气情况");
        map.put("robot_img",R.drawable.img1);
        list.add(map);

        map=new HashMap<String,Object>();
        map.put("robot_title","旅游指南系列");
        map.put("robot_info","在这里您可以询问旅游指南");
        map.put("robot_img",R.drawable.img2);
        list.add(map);

        map=new HashMap<String,Object>();
        map.put("robot_title","新闻系列");
        map.put("robot_info","在这里您可以询问新闻");
        map.put("robot_img",R.drawable.img3);
        list.add(map);

        map=new HashMap<String,Object>();
        map.put("robot_title","闲聊系列");
        map.put("robot_info","在这里您可以随意闲聊");
        map.put("robot_img",R.drawable.img4);
        list.add(map);

        map=new HashMap<String,Object>();
        map.put("robot_title","电影系列");
        map.put("robot_info","在这里您可以询问电影推荐");
        map.put("robot_img",R.drawable.img5);
        list.add(map);

        map=new HashMap<String,Object>();
        map.put("robot_title","动漫系列");
        map.put("robot_info","在这里您可以询问动漫推荐");
        map.put("robot_img",R.drawable.img6);
        list.add(map);

        return list;
    }
}