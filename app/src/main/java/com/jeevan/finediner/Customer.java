package com.jeevan.finediner;

import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Created by JEEVAN on 12/03/2016.
 */
public class Customer implements Serializable{

    private ArrayList<Item> tempOrder = new ArrayList<>();
    private ArrayList<Item> placedOrder;

    public String tablecode;

    private long totaltime;



    private CountDownTimer countTimer;
    public static ProgressBar srk;
    public  TextView time;

    public Customer(ArrayList<Item> order, String code){
        placedOrder = order;
        tablecode = code;
    }

    public CountDownTimer getTimer(){
        return countTimer;
    }
    public void setTimer(CountDownTimer d){
        countTimer=d;
    }

    public ArrayList<Item> getTempOrder(){
        return tempOrder;
    }
    public ArrayList<Item> getPlacedOrder(){
        return placedOrder;
    }
    public void addItemtoOrder(Item it) {
        for (int i=0; i<tempOrder.size();i++) {
            if (it.getName().equals( tempOrder.get(i).getName())) {
                tempOrder.get(i).setQuantity(it.getQuantity());
                return;
            }
        }
        tempOrder.add(it);
    }
    public void setPlacedOrder(ArrayList<Item> it) {
        placedOrder = it;
    }
    public void removeItemOrder(int it) {
        it = it - placedOrder.size();
        tempOrder.remove(it);
    }
    public void changeProgress(long ee, long e2){

        if(getTimer()!=null){
            getTimer().cancel();
        }
        srk.setProgress((int) ee);
        srk.setMax((int) e2);

        setTimer(new CountDownTimer(srk.getMax() - srk.getProgress(), 100) {
            public void onTick(long millisUntilFinished) {
                srk.setProgress(srk.getProgress() + 100);
                time.setText("" + (srk.getMax() - srk.getProgress()) / 60000 + "m");

            }

            public void onFinish() {
                setTimer(null);
                time.setText("");

                //   srk.setProgress(0);
            }
        }.start());
    }
    public void setOrder(){
/*
        long remaining;
        if(getTimer()==null) {
            remaining = compareTime(tempOrder, 0);
        }
        else {
            getTimer().cancel();
            remaining = compareTime(tempOrder, srk.getMax() - srk.getProgress());
        }
        srk.setMax(srk.getProgress()+(int)remaining);

*/
        placedOrder.addAll(tempOrder);
        tempOrder = new ArrayList<>();
/*
        setTimer(new CountDownTimer(remaining, 100) {
            public void onTick(long millisUntilFinished) {
                srk.setProgress(srk.getProgress() + 100);
            }
            public void onFinish() {
                setTimer(null);
                srk.setProgress(0);
            }
        }.start());
*/
    }
    public double getTotal() {
        double total = 0;
        for (Item item: placedOrder)
            total+=item.getPrice()*item.getQuantity();
        return total;
    }
    public String getFormattedDouble(double d) {

        DecimalFormat df=new DecimalFormat("0.00");
        return df.format(d);
    }
    public double getNewTotal() {
        double total = getTotal();
        for (Item item: tempOrder)
            total+=item.getPrice()*item.getQuantity();
        return total;
    }
    public long compareTime(ArrayList<Item> a, long total) {
        for (Item item: a) {
            if (total < item.getTime()) {
                total = item.getTime();
            }
        }

        return total;
    }
    public long compareTimeNewOrder(){
        long remaining;
        if( getTimer()==null) {
            remaining =  compareTime( getTempOrder(), 0);
        }
        else {
            remaining =  compareTime(getTempOrder(), srk.getMax() - srk.getProgress());
        }
        return remaining;
    }

}
