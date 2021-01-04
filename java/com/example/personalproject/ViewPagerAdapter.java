package com.example.personalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private int tabnum;
    private ArrayList<String> tabname = new ArrayList<String>();
    private ArrayList<MovieInfo> items = new ArrayList<MovieInfo>();

    private String userfullname;
    public ViewPagerAdapter(FragmentManager fm, int tabnum, ArrayList<MovieInfo> items,String userfullname){
        super(fm);
        this.tabnum = tabnum;
        this.userfullname = userfullname;
        this.items = items;
        tabname.add("Main");
        tabname.add("Post");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MainFrag mainFrag = new MainFrag(items, userfullname);
                return mainFrag;
            case 1:
                PostFrag postFrag  = new PostFrag();
                return postFrag;
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabnum;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) { return tabname.get(position); }

}
