package com.example.myapplication.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;
import java.util.Map;


public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder>
        implements View.OnClickListener{
    private OnItemClickListener mOnItemClickListener = null;
    private Context mContext;
    private List<Map<String,Object>> mDataList;
    private LayoutInflater mLayoutInflater;
    public InfoAdapter(Context mContext, List<Map<String,Object>> mDataList){
        this.mContext=mContext;
        this.mDataList=mDataList;
        mLayoutInflater=LayoutInflater.from(mContext);
    }

    public static InfoFragment getInstance() {
        return new InfoFragment();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //这里是去找recyclieview的布局文件
        View v=mLayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycleview_info,viewGroup,false);
//        View v=View.inflate(mContext,R.layout.fragment_detail,null);
        // 给View注册点击事件
        v.setOnClickListener((View.OnClickListener) this);
//        v.findViewById(R.id.news_container).setOnClickListener((View.OnClickListener) this);
//        v.findViewById(R.id.robot_title).setOnClickListener((View.OnClickListener) this);
//        v.findViewById(R.id.robot_img).setOnClickListener((View.OnClickListener) this);
//        v.findViewById(R.id.robot_info).setOnClickListener((View.OnClickListener) this);
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
        holder.info_list_title.setText(entity.get("info_list_title").toString());
        holder.info_list_content.setText(entity.get("info_list_content").toString());
//        holder.info_avatar.setImageResource(Integer.parseInt(entity.get("info_avatar").toString()));
        holder.info_list_title.setTag(position);
        holder.info_list_content.setTag(position);
        if(entity.get("info_list_title").toString().equals("头像：")){
            holder.info_list_title.setVisibility(View.GONE);
            holder.info_list_content.setVisibility(View.GONE);
        }
//        holder.info_avatar.setTag(position);
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
        private TextView info_list_title;
        private TextView info_list_content;
        private ImageView info_avatar;
        private View itemView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            info_list_title=(TextView) itemView.findViewById(R.id.info_list_title);
            info_list_content=(TextView) itemView.findViewById(R.id.info_list_content);
//            info_avatar=(ImageView) itemView.findViewById(R.id.info_avatar);
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
