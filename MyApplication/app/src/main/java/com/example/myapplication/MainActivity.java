package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.myapplication.fragment.ContainFragment;
import com.example.myapplication.fragment.InfoAdapter;
import com.example.myapplication.fragment.InfoFragment;
import com.example.myapplication.fragment.NewsFragment;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity{
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;

    private LinearLayout mTabWechat;
    private LinearLayout mTabFriend;
    private LinearLayout mTabContact;
    private LinearLayout mTabSetting;

    private ImageButton mImgWechat;
    private ImageButton mImgFriend;
    private ImageButton mImgContact;
    private ImageButton mImgSetting;

    private TextView text_title;
    private TextView text_info;
    private CircleImageView circleImageView;
//    private String[] tabs = {"疫情新闻", "疫情场所", "疫情问答", "疫情购药","敬请期待","敬请期待","敬请期待"};
//    private List<TabFragment> tabFragmentList = new ArrayList<>();
//    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏全透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        //"state"是存储的文件名，Context.MODE_PRIVATE表示该文件只能被创建他的应用访问
        SharedPreferences sharedpreferences = getApplication().getSharedPreferences("state", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("isLogin", true);//第一个参数相当于一个Key，后面取出数据时需要用到这个值来索引
        editor.commit();

        setContentView(R.layout.activity_main);
        initViews();
        initEvents();
        initDatas();

        Intent intent = getIntent();
        String username=intent.getStringExtra("username");
        String signature=intent.getStringExtra("signature");
        String avatar=intent.getStringExtra("avatar");
        System.out.println("username"+username);
        System.out.println("signature"+signature);
        System.out.println("avatar"+avatar);
        text_title.setText(username);
        text_info.setText(signature);
//        String url = "https://int-mian.oss-cn-hangzhou.aliyuncs.com/android/img/user/1/avatar.jpg";
        Glide.with(this).load(avatar).into(circleImageView);
    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        SharedPreferences preferences;
//        preferences = getSharedPreferences("state", Activity.MODE_PRIVATE);
//        String is_rm=preferences.getString("is_rm", "");
//        if(is_rm.equals("no")){
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.clear();
//            editor.commit();
//        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        InfoFragment fragment = InfoAdapter.getInstance();
        fragment.onActivityResult(requestCode,resultCode,data);
    }
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.id_tab_wechat:
                    selectTab(0);
                    break;
                case R.id.id_tab_friend:
                    selectTab(1);
                    break;
                case R.id.id_tab_contact:
                    selectTab(2);
                    break;
                case R.id.id_tab_setting:
                    selectTab(3);
                    break;
            }
        }
    };
    private void initViews(){
//        tabLayout = findViewById(R.id.tab_layout);
        mViewPager=findViewById(R.id.id_viewpager_main);

        mTabWechat=findViewById(R.id.id_tab_wechat);
        mTabFriend=findViewById(R.id.id_tab_friend);
        mTabContact=findViewById(R.id.id_tab_contact);
        mTabSetting=findViewById(R.id.id_tab_setting);

        mImgWechat=findViewById(R.id.id_tab_wechat_img);
        mImgFriend=findViewById(R.id.id_tab_friend_img);
        mImgContact=findViewById(R.id.id_tab_contact_img);
        mImgSetting=findViewById(R.id.id_tab_setting_img);


        text_title=findViewById(R.id.text_title);
        text_info=findViewById(R.id.text_info);
        circleImageView=findViewById(R.id.avatar);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0x01);
            }
        });
    }
    public void initEvents(){
        mTabWechat.setOnClickListener(onClickListener);
        mTabFriend.setOnClickListener(onClickListener);
        mTabContact.setOnClickListener(onClickListener);
        mTabSetting.setOnClickListener(onClickListener);

    }
    public void initDatas(){
        mFragments=new ArrayList<>();
        mFragments.add(new ContainFragment());
        mFragments.add(new InfoFragment());
        mFragments.add(new InfoFragment());
        mFragments.add(new InfoFragment());
//        for (int i = 0; i < tabs.length; i++) {
//            tabLayout.addTab(tabLayout.newTab().setText(tabs[i]));
//            mFragments.add(DetailFragment.newInstance(tabs[i]));
//        }

        mAdapter=new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position%mFragments.size());
            }
//            @Nullable
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return tabs[position];
//            }
        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mFragments.size());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("position"+position);
                mViewPager.setCurrentItem(position%mFragments.size());
                resetImgs();
                selectTab(position%mFragments.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    //设置TabLayout和ViewPager联动
//        tabLayout.setupWithViewPager(mViewPager,false);
        selectTab(0);
    }

    private void selectTab(int i){
        switch (i){
            case 0:
                mImgWechat.setImageResource(R.mipmap.chat_selected);
                break;
            case 1:
                mImgFriend.setImageResource(R.mipmap.find_selected);
                break;
            case 2:
                mImgContact.setImageResource(R.mipmap.contact_selected);
                break;
            case 3:
                mImgSetting.setImageResource(R.mipmap.mine_selected);
                break;
        }
        mViewPager.setCurrentItem(i);


    }
    private void resetImgs(){
        mImgWechat.setImageResource(R.mipmap.chat);
        mImgFriend.setImageResource(R.mipmap.find);
        mImgContact.setImageResource(R.mipmap.contact);
        mImgSetting.setImageResource(R.mipmap.mine);
    }
}