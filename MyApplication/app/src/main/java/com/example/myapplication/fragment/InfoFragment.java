package com.example.myapplication.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.ChatActivity;
import com.example.myapplication.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoFragment extends Fragment {
    private RecyclerView recyclerview;
    private InfoAdapter recyclerview_adapter;
    private List<Map<String,Object>> mDataList;
    private Button btn_editInfo;
    private Handler mHandler;
    private ListView news_list;
    private String username;
    private String signature;
    private ImageView info_avatar;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        //text.setBackgroundColor(Color.rgb((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //获取当前用的名
        preferences = getActivity().getSharedPreferences("state", Activity.MODE_PRIVATE);
        username=preferences.getString("username", "");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x01) {
            // 选择到图片的uri
            Uri uri = data.getData();

            // 第一种方式，使用文件路径创建图片
            // 文件路径的列
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            // 获取图片的游标
            Cursor cursor = getActivity().getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            // 获取列的指针
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            // 根据指针获取图片路径
            String picturePath = cursor.getString(columnIndex);
            int p=picturePath.lastIndexOf("/");
            cursor.close();
            File file = new File(picturePath.substring(0,p),picturePath.substring(p+1,picturePath.length()));
            System.out.println("file:"+file);
            // 使用地址获取图片
            //Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            //mIvPath.setImageBitmap(bitmap);
            Glide.with(this).load(Uri.fromFile(file)).into(info_avatar);

            //更新titlebar
            ImageView imageView=getActivity().findViewById(R.id.avatar);
            Glide.with(this).load(Uri.fromFile(file)).into(imageView);

            PostAvatar(file);

//            // 第二种方式
//            Bitmap bitmapStream = null;
//            try {
//                // 使用流获取图片
//                bitmapStream = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//            mIvStream.setImageBitmap(bitmapStream);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_info,container,false);
//        news_list=(ListView) view.findViewById(R.id.news_list);
//
//        SimpleAdapter adapter=new SimpleAdapter(getActivity(),getData(),R.layout.fragment_my,
//                new String[]{"robot_title","robot_info","robot_img"},
//                new int[]{R.id.robot_title,R.id.robot_info,R.id.robot_img});
//        news_list.setAdapter(adapter);
        //获得Recyclerview
        recyclerview = (RecyclerView)view.findViewById(R.id.info_list);

        //创建adapter类的对象
        mDataList=getData();

        //这步骤必须有，这是选择RecylerView的显示方式
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        //recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview_adapter = new InfoAdapter(getActivity(), mDataList);
        // 设置监听事件
        recyclerview_adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position){

            }
        });

        //将对象作为参数通过setAdapter方法设置给recylerview；"
        recyclerview.setAdapter(recyclerview_adapter);
        //修改按钮
        btn_editInfo=view.findViewById(R.id.btn_editInfo);
        //个人头像
        info_avatar=view.findViewById(R.id.info_avatar);
        setOnClickListener();

        mHandler=new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        //完成主界面更新,拿到数据
                        try {
                            initData(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            updateData(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        };
        //请求获取个人信息
        getInfo(username);
        return view;
    }

    private void setOnClickListener(){
        //修改个人信息
        btn_editInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("个人信息修改");
                final View v = View.inflate(getContext(), R.layout.edit_info_dialog, null);
                EditText info_list_name = v.findViewById(R.id.info_list_name);
                EditText info_list_sex = v.findViewById(R.id.info_list_sex);
                EditText info_list_signature = v.findViewById(R.id.info_list_signature);
                EditText info_list_birthday = v.findViewById(R.id.info_list_birthday);
                EditText info_list_phone = v.findViewById(R.id.info_list_phone);
                EditText info_list_address = v.findViewById(R.id.info_list_address);

                info_list_name.setText(mDataList.get(0).get("info_list_content").toString());
                info_list_sex.setText(mDataList.get(1).get("info_list_content").toString());
                info_list_signature.setText(mDataList.get(2).get("info_list_content").toString());
                info_list_birthday.setText(mDataList.get(3).get("info_list_content").toString());
                info_list_phone.setText(mDataList.get(4).get("info_list_content").toString());
                info_list_address.setText(mDataList.get(5).get("info_list_content").toString());

                builder.setView(v);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = info_list_name.getText().toString();
                        String sex = info_list_sex.getText().toString();
                        String signature = info_list_signature.getText().toString();
                        String birthday = info_list_birthday.getText().toString();
                        String phone = info_list_phone.getText().toString();
                        String address = info_list_address.getText().toString();
                        mDataList.get(0).put("info_list_content",name);
                        mDataList.get(1).put("info_list_content",sex);
                        mDataList.get(2).put("info_list_content",signature);
                        mDataList.get(3).put("info_list_content",birthday);
                        mDataList.get(4).put("info_list_content",phone);
                        mDataList.get(5).put("info_list_content",address);
                        dialog.cancel();
                        recyclerview_adapter.notifyItemRangeChanged(0,mDataList.size());
                        System.out.println(mDataList);
                        EditInfo();
                        Toast.makeText(getContext(),"修改成功",Toast.LENGTH_SHORT);
                    }

                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        //点击头像选择本地图片
        info_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0x01);
            }
        });
    }
    private List<Map<String,Object>> getData(){
        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("info_list_title","昵称：");
        map.put("info_list_content","懒羊羊");
        list.add(map);

        map=new HashMap<String,Object>();
        map.put("info_list_title","性别：");
        map.put("info_list_content","男");
        list.add(map);

        map=new HashMap<String,Object>();
        map.put("info_list_title","个性签名：");
        map.put("info_list_content","天上白玉京，十二楼五城");
        list.add(map);

        map=new HashMap<String,Object>();
        map.put("info_list_title","生日：");
        map.put("info_list_content","2022-12-18");
        list.add(map);

        map=new HashMap<String,Object>();
        map.put("info_list_title","手机号：");
        map.put("info_list_content","800-820-8820");
        list.add(map);

        map=new HashMap<String,Object>();
        map.put("info_list_title","地址：");
        map.put("info_list_content","青青草原狼堡二楼口径1000mm的大锅");
        list.add(map);

        map=new HashMap<String,Object>();
        map.put("info_list_title","头像：");
        map.put("info_list_content","https://int-mian.oss-cn-hangzhou.aliyuncs.com/android/img/user/1/avatar.jpg");
        list.add(map);
        return list;
    }

    private void PostAvatar(File file) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3000, TimeUnit.SECONDS)
                .callTimeout(3000, TimeUnit.SECONDS)
                .build();

        new Thread(){
            @Override
            public void run() {
                super.run();

                RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
///                .addPart(
//                        Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + fileName + "\""),
//                        RequestBody.create(MEDIA_TYPE_PNG, file))
//                .addPart(
//                        Headers.of("Content-Disposition", "form-data; name=\"imagetype\""),
//                        RequestBody.create(null, imageType))
//                .addPart(
//                        Headers.of("Content-Disposition", "form-data; name=\"userphone\""),
//                        RequestBody.create(null, userPhone))
                        .addFormDataPart("file", "avatar", fileBody)
                        .addFormDataPart("username", username)
                        .build();

                String path = "http://192.168.3.2:8080/test/oss/upload";
                Request request = new Request.Builder()
                        .url(path)
                        .post(requestBody)
                        .build();

                Response response = null;
                try {
                    //同步请求
                    response = okHttpClient.newCall(request).execute();
                    System.out.println(response);
                    JSONObject res = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {
                        String url=res.getString("data");
                        mDataList.get(6).put("info_list_content",url);
                        Log.d("succuss", "上传头像成功 ");
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

    private void EditInfo() {
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
                    json.put("username", mDataList.get(0).get("info_list_content").toString());
                    username=mDataList.get(0).get("info_list_content").toString();

                    json.put("sex", mDataList.get(1).get("info_list_content").toString());

                    json.put("signature", mDataList.get(2).get("info_list_content").toString());
                    signature=mDataList.get(2).get("info_list_content").toString();

                    json.put("birthday", mDataList.get(3).get("info_list_content").toString());
                    json.put("phone", mDataList.get(4).get("info_list_content").toString());
                    json.put("address", mDataList.get(5).get("info_list_content").toString());
                    json.put("avatar", mDataList.get(6).get("info_list_content").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody formBody = RequestBody.create(
                        MediaType.parse("application/json"), String.valueOf(json));
                Message message = new Message();
                String path = "http://192.168.3.2:8080/user";
                Request request = new Request.Builder().url(path).put(formBody).build();
                Response response = null;
                try {
                    //同步请求
                    response = okHttpClient.newCall(request).execute();
                    System.out.println(response);
                    JSONObject res = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {
                        Message msg=new Message();
                        msg.what=1;
                        msg.obj=res;
                        mHandler.sendMessage(msg);
                        Log.d("succuss", "修改信息成功 ");
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
    private void getInfo(final String username) {
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
                        .url("http://192.168.3.2:8080/user?username="+username)
                        .build();
                Response response = null;
                try {
                    //同步请求
                    response = okHttpClient.newCall(request).execute();
                    System.out.println(response);
                    JSONObject res = new JSONObject(response.body().string());
                    System.out.println(res);
                    if (response.isSuccessful()) {
                        Message msg=new Message();
                        msg.what=0;
                        msg.obj=res.getJSONObject("data");
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
    private void initData(Message msg) throws JSONException {
        JSONObject res = (JSONObject) msg.obj;
        System.out.println("res:"+res);
        Iterator it = res.keys();
        String key = null;//键
        int cnt=0;
        while(it.hasNext()) {//遍历JSONObject
            key = (String) it.next().toString();
            if(key.equals("id") || key.equals("password")){
                continue;
            }
            String val= res.getString(key);
            if(key.equals("username")){
                username=val;
            }
            System.out.println("cnt"+cnt+"      key:"+key+"     val:"+val);
            if(!val.equals("null")){
                mDataList.get(cnt).put("info_list_content",val);
            }
            cnt++;
        }
        //加载头像
        String picturePath=mDataList.get(6).get("info_list_content").toString();
        Glide.with(this).load(picturePath).into(info_avatar);

        recyclerview_adapter.notifyItemRangeChanged(0,mDataList.size());
    }
    private void updateData(Message msg) throws JSONException {
        Toast.makeText(getContext(),"信息修改成功",Toast.LENGTH_SHORT).show();
        TextView text_title=getActivity().findViewById(R.id.text_title);
        System.out.println("activity:"+getActivity());
        text_title.setText(username);
        TextView text_info=getActivity().findViewById(R.id.text_info);
        text_info.setText(signature);
        recyclerview_adapter.notifyItemRangeChanged(0,mDataList.size());
    }
}