package com.jeevan.finediner.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jeevan.finediner.Item;
import com.jeevan.finediner.R;
import com.jeevan.finediner.Request;
import com.jeevan.finediner.Customer;
import com.jeevan.finediner.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Button btnSimpleTabs, btnCreateTab,btnReqItems,btnTasks;


    private EditText edUname;
    private EditText edPass;
    private View layy;

    Spinner spinner ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        edUname = (EditText) findViewById(R.id.edID);
        edPass = (EditText) findViewById(R.id.edPass);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        btnSimpleTabs = (Button) findViewById(R.id.btnSimpleTabs);
        btnSimpleTabs.setOnClickListener(this);
        btnReqItems = (Button) findViewById(R.id.btnReqItems);
        btnReqItems.setOnClickListener(this);
        Button btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(this);
        btnCreateTab = (Button) findViewById(R.id.btncreateTable);
        btnCreateTab.setOnClickListener(this);
        btnCreateTab.setEnabled(false);
        btnTasks = (Button) findViewById(R.id.btnTasks);
        btnTasks.setOnClickListener(this);
        btnTasks.setEnabled(false);

        layy = findViewById(R.id.relativeLayoutmain);

        spinner = (Spinner) findViewById(R.id.spinner);

        setViewAndChildrenEnabled(layy, false);
        spinner.setEnabled(false);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSimpleTabs:
                Intent in = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(in);
                break;
            case R.id.btnTasks:
                Intent in2 = new Intent(MainActivity.this, TasksActivity.class);
                startActivity(in2);
                break;

            case R.id.btnConnect:
                Session.getSession(String.valueOf(edUname.getText()),String.valueOf(edPass.getText()), MainActivity.this);
                if(Session.getSession()!=null) {
                    view.setEnabled(false);
                    btnCreateTab.setEnabled(true);
                    btnTasks.setEnabled(true);
                    new Listen().start();
                    Session.getSession().sendRequest(new Request(Request.INITIALIZE_WAITER,""));

                }
                break;
            case R.id.btncreateTable:


                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Get the layout inflater
                final LayoutInflater inflater = this.getLayoutInflater();
                final View dialogview = inflater.inflate(R.layout.create_table_dialog,null);
                builder.setView(dialogview)

                        .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText nogus = (EditText) dialogview.findViewById(R.id.nogus);
                                EditText tbno = (EditText) dialogview.findViewById(R.id.tbnp);
                                if (nogus.getText().equals(null) || tbno.getText().equals(null)) {
                                    Toast.makeText(getApplicationContext(), "Please enter the table details and try again", Toast.LENGTH_SHORT).show();
                                } else
                                    Session.getSession().sendRequest(new Request(Request.CREATE_TABLE, "" + nogus.getText(), "" + tbno.getText()));
                            }
                        });

                builder.create().show();
                break;
            case R.id.btnReqItems:
                final String[] myList = Session.getSession().getReqItems().toArray(new String[Session.getSession().getReqItems().size()]);

                AlertDialog.Builder buildern = new AlertDialog.Builder(MainActivity.this);
                buildern.setTitle("Request")
                        .setItems(myList, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                Session.getSession().sendRequest(new Request(Request.REQUEST_ITEM, Session.getSession().getSelectedCustomer().tablecode, myList[which]));
                            }
                        });
                buildern.create().show();



        }
    }
    private  void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }

        }
    }
    public class Listen extends Thread {

        public void run(){
            while(true){
                final Request rr = (Request)  Session.getSession().receive();
                switch (rr.getType()) {
                    case Request.ERROR_MESSAGE:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),(String)rr.getContent(),Toast.LENGTH_SHORT).show();

                            }
                        });
                        break;

                    case Request.MENU:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Session.getSession().setMenu((ArrayList<Item>) rr.getContent());
                            }
                        });
                        break;

                    case Request.CUSTOMER_INFO:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<Item> order = (ArrayList<Item>) rr.getContent();
                                Session.getSession().selected = new Customer(order, (String) rr.getSecondContent());
                                setViewAndChildrenEnabled(layy, true);
                            }
                        });
                        break;
                    case Request.REQUESTABLE_ITEMS:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               Session.getSession().setReqItems((ArrayList<String>) rr.getContent());

                            }
                        });

                        break;
                    case Request.TASK_LIST:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<String> ss =(ArrayList<String>) rr.getContent();
                                String outp = "";
                                for (String s:ss){
                                    outp = outp + "\n" + s;
                                }

                                Toast.makeText(getApplicationContext(), outp, Toast.LENGTH_SHORT).show();

                                Session.getSession().setTasks(ss);
                                if(TaskListAdapter.getTaskListAdapter()!=null){

                                    TaskListAdapter.getTaskListAdapter().update(ss);
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), "adp null", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                        break;
                    case Request.TABLE_LIST:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                spinner.setEnabled(true);
                                // Spinner click listener
                                AdapterView.OnItemSelectedListener ois = new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String item = parent.getItemAtPosition(position).toString();
                                        Session.getSession().sendRequest(new Request(Request.CUSTOMER_INFO, item));
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                };
                                spinner.setOnItemSelectedListener(ois);


                                // Spinner Drop down elements
                                ArrayList<String> categories = (ArrayList<String>) rr.getContent();

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, categories);

                                // Drop down layout style - list view with radio button
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                // attaching data adapter to spinner
                                spinner.setAdapter(dataAdapter);

                            }});
                        break;
                    case Request.PROGRESS_UPDATE_WAITER:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "" + (int) rr.getContent() + " " + (int) rr.getSecondContent() + " ", Toast.LENGTH_SHORT).show();
                                if(Session.getSession().getSelectedCustomer().srk !=null){
                                    Session.getSession().getSelectedCustomer().changeProgress(Long.valueOf((int) rr.getContent()), Long.valueOf((int) rr.getSecondContent()));
                                }

                            }
                        });
                        break;

                }
            }
        }
    }
}
