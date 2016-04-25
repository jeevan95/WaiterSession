package com.jeevan.finediner.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jeevan.finediner.R;
import com.jeevan.finediner.Session;
import com.jeevan.finediner.fragments.MenuTab;
import com.jeevan.finediner.fragments.OrderTab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TasksActivity extends AppCompatActivity {

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ExpandableListView exl = (ExpandableListView) findViewById(R.id.expandableListView);
        exl.setAdapter(new TaskListAdapter(this, Session.getSession().getTasks()));



    }




}
