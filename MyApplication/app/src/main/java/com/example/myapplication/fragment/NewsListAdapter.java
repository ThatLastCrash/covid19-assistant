package com.example.myapplication.fragment;

import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;
import java.util.Map;
import java.util.Random;


public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder>
        implements View.OnClickListener{
    private OnItemClickListener mOnItemClickListener = null;
    private Context mContext;
    private Display display;
    private List<Map<String,Object>> mDataList;
    private LayoutInflater mLayoutInflater;
    public NewsListAdapter(Context mContext, List<Map<String,Object>> mDataList, Display d){
        this.mContext=mContext;
        this.mDataList=mDataList;
        this.display=d;
        mLayoutInflater=LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //这里是去找recyclieview的布局文件
        View v=mLayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycleview_news,viewGroup,false);
//        View v=View.inflate(mContext,R.layout.fragment_detail,null);
//        System.out.println("?????"+this);
        // 给View注册点击事件
        v.setOnClickListener((View.OnClickListener) this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String,Object> entity=mDataList.get(position);
        if(null==entity){
            return;
        }
        holder.news_title.setText(entity.get("news_title").toString());
        holder.news_data.setText(entity.get("news_data").toString());
        holder.news_last.setText(entity.get("news_last").toString());
        Random random = new Random();
        String color[]=new String[]{"#FF4500","#FF8C00","#006400","#00CED1",
                "#00BFFF","#7B68EE","#EE82EE","#DB7093","#DAA520"};
        holder.ll_contain_data.setBackgroundColor(Color.parseColor(color[random.nextInt(color.length)]));

        ViewGroup.LayoutParams l = holder.news_title.getLayoutParams();
        l.width=display.getWidth()/4;
        holder.news_title.setLayoutParams(l);

        // 将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
        holder.news_title.setTag(position);
        holder.news_data.setTag(position);
        holder.news_last.setTag(position);
        holder.ll_contain_data.setTag(position);
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
    //添加一条
    public void addData(Map<String, Object> map){
        mDataList.add(map);
        notifyItemInserted(getItemCount());
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView news_title;
        private TextView news_data;
        private TextView news_last;
        private LinearLayout ll_contain_data;
        private View itemView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            news_title=(TextView) itemView.findViewById(R.id.news_title);
            news_data=(TextView) itemView.findViewById(R.id.news_data);
            news_last=(TextView) itemView.findViewById(R.id.news_last);
            ll_contain_data=itemView.findViewById(R.id.ll_contain_data);
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
