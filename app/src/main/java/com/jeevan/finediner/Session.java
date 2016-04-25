package com.jeevan.finediner;

import android.app.Activity;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by JEEVAN on 22/04/2016.
 */
public class Session {

    private ArrayList<Item> menuItems;
    private ArrayList<String> reqItems;
    private ArrayList<String> tasklist;

    public Customer selected;

    private static Session instance;
    private Socket soc;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    private Session(String uname, String pass, Activity a){
        try{
            soc = new Socket("10.0.3.2", 3223);
            outStream = new ObjectOutputStream(soc.getOutputStream());
            inStream = new ObjectInputStream(soc.getInputStream());
            sendRequest(new Request(Request.NEW_WAITER, uname, pass));
            Request req = receive();
            if (req.getType()==Request.ERROR_MESSAGE){
                Toast.makeText(a, "" + req.getContent(), Toast.LENGTH_SHORT).show();
                instance = null;
            }else if (req.getType()==Request.INITIALIZE_WAITER){
                Toast.makeText(a, "Logged in successfully", Toast.LENGTH_SHORT).show();
                instance = this;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Request receive(){
        try {
            return (Request) inStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void sendRequest(Request req) {
        try {
            outStream.writeObject(req);
            outStream.flush();
            outStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Customer getSelectedCustomer(){

        return selected;
    }

    public static Session getSession(){return instance;}

    public static Session getSession(String un, String pas, Activity a){
        return new Session(un,pas,a); }
    public void setMenu(ArrayList<Item> ai){ menuItems = ai; }
    public void setReqItems(ArrayList<String> ai){ reqItems = ai; }
    public void setTasks(ArrayList<String> ai){ tasklist = ai; }
    public ArrayList<Item> getMenu(){ return menuItems; }
    public ArrayList<String> getReqItems(){ return reqItems; }
    public ArrayList<String> getTasks(){ return tasklist; }

}
