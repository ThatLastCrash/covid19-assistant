package com.example.myapplication.fragment;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.List;
import java.util.Map;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>
        implements View.OnClickListener{
    private OnItemClickListener mOnItemClickListener = null;
    private Context mContext;
    private List<Map<String,Object>> mDataList;
    private LayoutInflater mLayoutInflater;
    public ChatListAdapter(Context mContext, List<Map<String,Object>> mDataList){
        this.mContext=mContext;
        this.mDataList=mDataList;
        mLayoutInflater=LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //这里是去找recyclieview的布局文件
        View v=mLayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycleview_chatlist,viewGroup,false);
//        View v=View.inflate(mContext,R.layout.fragment_detail,null);
//        System.out.println("?????"+this);
        // 给View注册点击事件
        v.setOnClickListener((View.OnClickListener) this);
//        v.findViewById(R.id.news_container).setOnClickListener((View.OnClickListener) this);
//        v.findViewById(R.id.robot_title).setOnClickListener((View.OnClickListener) this);
//        v.findViewById(R.id.robot_img).setOnClickListener((View.OnClickListener) this);
//        v.findViewById(R.id.robot_info).setOnClickListener((View.OnClickListener) this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String,Object> entity=mDataList.get(position);
        if(null==entity){
            return;
        }
        holder.robot_title.setText(entity.get("robot_title").toString());
        holder.robot_info.setText(entity.get("robot_info").toString());
        holder.robot_img.setImageResource(Integer.parseInt(entity.get("robot_img").toString()));
        // 将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
        holder.robot_title.setTag(position);
        holder.robot_info.setTag(position);
        holder.robot_img.setTag(position);
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
        private TextView robot_title;
        private ImageView robot_img;
        private TextView robot_info;
        private View itemView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            robot_title=(TextView) itemView.findViewById(R.id.robot_title);
            robot_info=(TextView) itemView.findViewById(R.id.robot_info);
            robot_img=(ImageView) itemView.findViewById(R.id.robot_img);
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
