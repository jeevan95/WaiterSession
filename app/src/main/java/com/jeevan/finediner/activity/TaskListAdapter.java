package com.jeevan.finediner.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeevan.finediner.Item;
import com.jeevan.finediner.R;
import com.jeevan.finediner.Request;
import com.jeevan.finediner.Session;

import java.util.ArrayList;

public class TaskListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private ArrayList<String> item_list;

    private static TaskListAdapter expAdpTask;

    public void update(ArrayList<String> i)
    {
        this.item_list = i;
        notifyDataSetChanged();

    }
    public ArrayList<String> getList(){
        return item_list;
    }
    public TaskListAdapter(Activity context, ArrayList<String> tasklist) {
        this.context = context;
        this.item_list = tasklist;
        expAdpTask = this;
    }

    public static TaskListAdapter getTaskListAdapter()    {
        return expAdpTask;
    }


    public Object getChild(int groupPosition, int childPosition) {
        return item_list.get(groupPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tasks_child, null);
        }

        final Button btnDone = (Button) convertView.findViewById(R.id.btnDone);

        btnDone.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Session.getSession().sendRequest(new Request(Request.REMOVE_TASK,getGroup(groupPosition)));
            }
        });


        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public Object getGroup(int groupPosition) {
        return item_list.get(groupPosition);
    }

    public int getGroupCount() {
        return item_list.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.tasks_list,
                    null);
        }

        TextView task = (TextView) convertView.findViewById(R.id.taskDetails);
        task.setText(""+getGroup(groupPosition));
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}