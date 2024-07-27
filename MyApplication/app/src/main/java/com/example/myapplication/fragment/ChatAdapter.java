package com.example.myapplication.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myapplication.R;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>
        implements View.OnClickListener{
    private OnItemClickListener mOnItemClickListener = null;
    private Context mContext;
    private List<Map<String,Object>> mDataList;
    private LayoutInflater mLayoutInflater;

    private String username;
    private String signature;
    private String avatar_url;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public ChatAdapter(Context mContext, List<Map<String,Object>> mDataList){
        this.mContext=mContext;
        this.mDataList=mDataList;
        mLayoutInflater=LayoutInflater.from(mContext);

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //这里是去找recyclieview的布局文件
        View v=mLayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycleview_chat,viewGroup,false);
//        View v=View.inflate(mContext,R.layout.fragment_detail,null);
//        System.out.println("?????"+this);
        // 给View注册点击事件
        v.setOnClickListener((View.OnClickListener) this);
//        v.findViewById(R.id.news_container).setOnClickListener((View.OnClickListener) this);
//        v.findViewById(R.id.robot_title).setOnClickListener((View.OnClickListener) this);
//        v.findViewById(R.id.robot_img).setOnClickListener((View.OnClickListener) this);
//        v.findViewById(R.id.robot_info).setOnClickListener((View.OnClickListener) this);
        //点击快捷发送
        v.findViewById(R.id.remind_left).setOnClickListener((View.OnClickListener) this);
        return new ViewHolder(v);
    }
    @Override
    public int getItemViewType(int position) {
        // 给每个ItemView指定不同的类型，这样在RecyclerView看来，这些ItemView全是不同的，不能复用
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String,Object> entity=mDataList.get(position);
        if(null==entity){
            return;
        }
        if(entity.get("remind_left")!=null){
            holder.remind_left.setText(entity.get("remind_left").toString());
            holder.remind_left.setTag(position);
            holder.msg_left.setVisibility(View.GONE);
            holder.user_img_left.setImageResource(Integer.parseInt(entity.get("user_img_left").toString()));
//            Glide.with(mContext)
//                    .load(entity.get("user_img_left").toString())
//                    .placeholder(R.drawable.lightman)
//                    .into(holder.user_img_left);

            holder.user_img_left.setTag(position);
            holder.ll_right.setVisibility(View.GONE);
        }
        else if(entity.get("msg_left")!=null){
            holder.msg_left.setText(entity.get("msg_left").toString());
            holder.user_img_left.setImageResource(Integer.parseInt(entity.get("user_img_left").toString()));
//            Glide.with(mContext)
//                    .load(entity.get("user_img_left").toString())
//                    .placeholder(R.drawable.lightman)
//                    .into(holder.user_img_left);

            holder.msg_left.setTag(position);
            holder.user_img_left.setTag(position);
            holder.remind_left.setVisibility(View.GONE);//隐藏提示
            holder.ll_right.setVisibility(View.GONE);
        }
        else{
            holder.msg_right.setText(entity.get("msg_right").toString());
//            holder.user_img_right.setImageResource(Integer.parseInt(entity.get("user_img_right").toString()));
            Glide.with(mContext)
                    .load(entity.get("user_img_right"))
                    .into(holder.user_img_right);

            holder.msg_right.setTag(position);
            holder.user_img_right.setTag(position);
            holder.ll_left.setVisibility(View.GONE);
        }
        // 将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }


    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    //添加一条
    public void addData(Map<String, Object> map){
        mDataList.add(map);
        notifyItemInserted(getItemCount());
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mOnItemClickListener = listener;//（最终listener 就是Activity对象）
    }

    //进行删除方法
    public void remove(int position){
        mDataList.remove(position);
        //notifyDataSetChanged();// 提醒list刷新，没有动画效果
        notifyItemRemoved(position); // 提醒item删除指定数据，这里有RecyclerView的动画效果
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView remind_left;
        private TextView msg_left;
        private TextView msg_right;
        private ImageView user_img_left;
        private ImageView user_img_right;
        private LinearLayout ll_left;
        private LinearLayout ll_right;
        private View itemView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            remind_left=(TextView) itemView.findViewById(R.id.remind_left);
            msg_left=(TextView) itemView.findViewById(R.id.msg_left);
            msg_right=(TextView) itemView.findViewById(R.id.msg_right);
            user_img_left=(ImageView) itemView.findViewById(R.id.user_img_left);
            user_img_right=(ImageView) itemView.findViewById(R.id.user_img_right);
            ll_left=itemView.findViewById(R.id.chat_left);
            ll_right=itemView.findViewById(R.id.chat_right);
            this.itemView=itemView;
//            robot_title.setOnClickListener(ChatListAdapter.this);
//            robot_info.setOnClickListener(ChatListAdapter.this);
//            robot_img.setOnClickListener(ChatListAdapter.this);

//            itemView.findViewById(R.id.news_container).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
        }
    }

}
