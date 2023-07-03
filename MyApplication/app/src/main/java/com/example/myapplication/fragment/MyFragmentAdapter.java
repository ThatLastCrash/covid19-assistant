package com.example.myapplication.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.LinkedList;
import java.util.List;

public class MyFragmentAdapter extends FragmentStateAdapter {
    List<Fragment> fragmentList = new LinkedList<>();
    public MyFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<Fragment> listFragment) {
        super(fragmentManager, lifecycle);
        this.fragmentList = listFragment;

    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}

