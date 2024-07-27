package com.example.myapplication.welcome;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;


public class SplashActivity extends Activity {

    ViewPager vp;
    RadioGroup rg;
    Button btn;

    LinearLayout ll;
    RelativeLayout rr;
    //把小圆点存到集合中
    List<ImageView> listDoc;

    List<ImageView> list;
    int[] imgArray = { R.drawable.hello_1, R.drawable.hello_2, R.drawable.hello_3};

    int count = 0;
    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            count++;
            vp.setCurrentItem(count);
            sendEmptyMessageDelayed(0, 1000);
        };
    };

    SharedPreferences preferences;
    Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_splash);

        //当该acitivty启动的时候，先从SharedPreferences中获取登录状态的值，如果为true，那么直接调转到主页
//        preferences = getSharedPreferences("state", MODE_PRIVATE);
//        if(preferences.getBoolean("isLogin", false)){
//            Intent intent = new Intent(SplashActivity.this, VolumeActivity.class);
//            startActivity(intent);
//            finish();
//        }

        //rg = (RadioGroup) findViewById(R.id.rg);
        btn = (Button) findViewById(R.id.btn);

        // 1、初始化控件
        vp = (ViewPager) findViewById(R.id.vp);
        // 2、初始化数据
        initData();
        // 3、创建apdater对象
        MyPagerAdapter adapter = new MyPagerAdapter();
        // 4、绑定
        vp.setAdapter(adapter);

        //添加小圆点的方法
        addDoc();

        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        handler.sendEmptyMessageDelayed(0, 1000);

        vp.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // 通过vp的选中位置，来确定小圆点的联动
                int index = arg0 % list.size();
                for (int i = 0; i < imgArray.length; i++) {
                    if(index == i){
                        listDoc.get(i).setImageResource(R.drawable.white);
                    }else{
                        listDoc.get(i).setImageResource(R.drawable.black);
                    }
                }
                //当index == 3的时候 说明显示的是第四张图片
                if(index == imgArray.length - 1){
                    btn.setVisibility(View.VISIBLE);
                }
//                else{
//                    btn.setVisibility(View.GONE);
//                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    /**
     * 把小圆点按照导航图片的数量初始化出来
     */

    private void addDoc() {

        listDoc = new ArrayList<ImageView>();
        rr = (RelativeLayout) findViewById(R.id.rv);

        //根据图片的数量创建小圆点对象
        for (int i = 0; i < imgArray.length; i++) {
            ImageView iv = new ImageView(SplashActivity.this);
            if(i == 0){
                iv.setImageResource(R.drawable.white);
            }else{
                iv.setImageResource(R.drawable.black);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(400,
                    400);
            iv.setLayoutParams(params);

            ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(iv.getLayoutParams());

            margin.leftMargin=410+i*100;
            margin.topMargin=2000;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
            layoutParams.height = 50;//设置图片的高度
            layoutParams.width = 50; //设置图片的宽度
            iv.setLayoutParams(layoutParams);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);//使图片充满控件大小

            listDoc.add(iv);
            rr.addView(iv);
        }
    }


    @Override
    protected void onStop() {
        //重写stop声明周期方法
        //当点击按钮执行页面跳转时，欢迎页会被关闭，那么handler的消息不会随着acitivy的关闭而停止，所以需要手动移除消息
        if(handler != null){
            handler.removeMessages(0);
        }
        super.onStop();
    }


    private void initData() {
        list = new ArrayList<ImageView>();
        for (int i = 0; i < imgArray.length; i++) {
            ImageView iv = new ImageView(SplashActivity.this);
            iv.setImageResource(imgArray[i]);
            iv.setScaleType(ScaleType.FIT_XY);

            list.add(iv);
        }
    }

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // 因为是无限轮播 所以position参数会一直增长
            // 那么我需要对list集合的长度取余数得到一个index下标
            int index = position % list.size();
            container.addView(list.get(index));
            return list.get(index);
        }

    }

}
