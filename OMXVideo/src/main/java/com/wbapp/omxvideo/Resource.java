package com.wbapp.omxvideo;

import java.util.ArrayList;
import java.util.List;

public class Resource {
    public int type;
    public int id;
    public int state;
    public String text;
    public int deep;
    public Resource parent = null;
    
    private List<Resource> children;

    public Resource clone() {
        Resource c = new Resource();
        c.type = type;
        c.id = id;
        c.state = state;
        c.text = text;
        c.deep = deep;
        return c;
    }
    
    public static final int RESTYPE_SERVER = 0;
    public static final int RESTYPE_GROUP  = 1;
    public static final int RESTYPE_CAMERA = 2;
    public static final int RESTYPE_CHANNEL = 3;
    
    static String[] pfx = {"", "  ", "    ", "     ", ""};

    String displayText;
    
    public String toString() {
        if( displayText==null ) {
            //displayText = pfx[deep] + text;
            if (children==null)
                displayText = text;
            else
                displayText = String.format("%s (%s)", text, children.size());
            //if (type==RESTYPE_CAMERA)
            //    displayText += (state==0)? "":" (离线)";
        }
        return displayText;
    }
    
    public int getImageRes() {
        if (type==RESTYPE_CAMERA) {
            return (state==0)?
                    android.R.drawable.presence_online:
                    android.R.drawable.presence_offline;
        }
        else if (type==RESTYPE_CHANNEL) {
            return android.R.drawable.radiobutton_off_background;
        }
        else if (type==RESTYPE_GROUP) {
            return android.R.drawable.ic_input_add;
        }
        else if (type==RESTYPE_SERVER) {
            return android.R.drawable.btn_star_big_on;
        }
        return 0;
    }
    
    public boolean getCanPlay() {
        switch(type) {
        case RESTYPE_SERVER:
        case RESTYPE_GROUP:
            return false;
        case RESTYPE_CAMERA:
        case RESTYPE_CHANNEL:
            return state==0;
        }
        return false;
    }
    
    public List<Resource> getChildren() {
        return children;
    }
    
    public void addChild(Resource child) {
        if (children==null)
            children = new ArrayList<Resource>();
        children.add(child);
    }
}
