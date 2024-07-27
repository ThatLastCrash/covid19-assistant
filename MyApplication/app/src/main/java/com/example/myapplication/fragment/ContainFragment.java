package com.example.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ContainFragment extends Fragment {

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

    private String[] tabs = {"疫情新闻", "疫情场所", "疫情问答", "疫情购药","敬请期待","敬请期待","敬请期待"};
    private List<TabFragment> tabFragmentList = new ArrayList<>();
    private TabLayout tabLayout;

    public ContainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_contain,container,false);
        tabLayout = view.findViewById(R.id.tab_layout);
        mViewPager= view.findViewById(R.id.id_viewpager_contain);

        initDatas();
        return view;

    }
    public void initDatas(){
        mFragments=new ArrayList<>();
        tabLayout.addTab(tabLayout.newTab().setText(tabs[0]));
        mFragments.add(NewsFragment.newInstance(tabs[0]));
        for (int i = 1; i < tabs.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabs[i]));
            mFragments.add(DetailFragment.newInstance(tabs[i]));
        }

        mAdapter=new FragmentPagerAdapter(super.getActivity().getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mFragments.size();
            }
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position%mFragments.size());
            }
            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return tabs[position];
            }
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
//                resetImgs();
                selectTab(position%mFragments.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //设置TabLayout和ViewPager联动
        tabLayout.setupWithViewPager(mViewPager,false);
        selectTab(0);
    }
    private void selectTab(int i){
//        switch (i){
//            case 0:
//                mImgWechat.setImageResource(R.mipmap.chat_selected);
//                break;
//            case 1:
//                mImgFriend.setImageResource(R.mipmap.find_selected);
//                break;
//            case 2:
//                mImgContact.setImageResource(R.mipmap.contact_selected);
//                break;
//            case 3:
//                mImgSetting.setImageResource(R.mipmap.mine_selected);
//                break;
//        }
        mViewPager.setCurrentItem(i);
    }
}