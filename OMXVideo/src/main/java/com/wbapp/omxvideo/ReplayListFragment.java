package com.wbapp.omxvideo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ReplayListFragment extends ListFragment {
    
    int filetype;
    static final int ALL   = 0;
    static final int VIDEO = 1;
    static final int AUDIO = 2;
    static final int IMAGE = 3;

    public ReplayListFragment() {
    }
    @SuppressLint("ValidFragment")
    private ReplayListFragment(int type) {
        this.filetype = type;
    }
    
    public static ReplayListFragment createForVideo() {
        return new ReplayListFragment(VIDEO);
    }
    
    public static ReplayListFragment createForAudio() {
        return new ReplayListFragment(AUDIO);
    }
    
    public static ReplayListFragment createForImage() {
        return new ReplayListFragment(IMAGE);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String path = getSavePath();
        curDir = new File(path);
        //refreshList(curDir);
        
        /*getListView().setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
                //menu.setHeaderTitle("");
                menu.add(0, 0, 0, "删除");
            }
        });*/
    }
    
    String getSavePath() {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this.getActivity());
        String val = sp.getString("save_dir", getString(R.string.def_save_dir));
        String save_path = "/mnt/sdcard/" + val;
        return save_path;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.replay_list, container, false);
        Button btn_up = (Button)v.findViewById(R.id.btn_up);
        btn_up.setOnClickListener(mBtnUpOnClickListener);
        Button btn_del = (Button)v.findViewById(R.id.btn_del);
        btn_del.setOnClickListener(mBtnDelOnClickListener);
        return v;
    }
    
    OnClickListener mBtnUpOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v){
            goUp();
        }
    };
    
    OnClickListener mBtnDelOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v){
            onDelClick();
        }
    };
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshList(curDir);
    }
    
    File curDir;
    int level = 0;
    
    final int NORMAL = 0;
    final int DELETE = 1;
    int mode = NORMAL;
    
    class VideoFile {
        public int type; //0-sub dir, 1-file
        public File file;
        public String name;
        public VideoFile(File f, int type) {
            file = f;
            name = f.getName();
            this.type = type;
        }
        public String toString() {
            return name;
        }
    }
    
    List<VideoFile> items;
    ArrayAdapter adapter;
    
    final String[] extens = {null, ".mp4", ".wav", ".jpg"};
    
    final FilenameFilter[] filters = {
            null,
            new FilenameFilter(){
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".mp4");
                }
            },
            new FilenameFilter(){
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".wav");
                }
            },
            new FilenameFilter(){
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.endsWith(".jpg");
                }
            }
    };
    
    class VideoFileFilter implements FilenameFilter
    {
        int filetype;
        public VideoFileFilter(int type) {
            this.filetype = type;
        }
        public boolean accept(File dir, String name) {
            File tmp = new File(dir, name);
            if (tmp.isDirectory()) {
                if (filetype == ALL)
                    return true;
                else {
                    return tmp.listFiles(filters[filetype]).length > 0;
                }
            }
            if (filetype == ALL) {
                return tmp.isDirectory() || name.endsWith(".mp4") ||
                    name.endsWith(".jpg") || name.endsWith(".wav");
            }
            else {
                return name.endsWith(extens[filetype]);
            }
        }
    }

    private void refreshList(File dir) {
        if (!dir.exists())
            return;
        items = new ArrayList<VideoFile>();
        FilenameFilter filter = new VideoFileFilter(filetype);
        File[] files = dir.listFiles(filter);
        for (File f : files) {
            VideoFile vf = null;
            String namelow = f.getName().toLowerCase();
            if (namelow.startsWith("."))
                continue;
            else if (f.isDirectory()) {
                vf = new VideoFile(f, 0);
            }
            else if (namelow.endsWith(".mp4") || namelow.endsWith(".jpg") ||
                    namelow.endsWith(".wav")) {
                vf = new VideoFile(f, 1);
            }
            else
                continue;
            items.add(vf);
        }
        if(mode == NORMAL){
            adapter = new ArrayAdapter<VideoFile>(this.getActivity(), android.R.layout.simple_list_item_1, items);
            getListView().setChoiceMode(ListView.CHOICE_MODE_NONE);
        }
        else if(mode == DELETE){
            adapter = new ArrayAdapter<VideoFile>(this.getActivity(), android.R.layout.simple_list_item_multiple_choice, items);
            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
        setListAdapter(adapter);
        curDir = dir;
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.i("", "list pos "+position);
        if(mode == DELETE)
            // just leave the checkbox
            return;
        
        VideoFile vf = items.get(position);
        
        if (vf.type == 0) {
            level++;
            refreshList(vf.file);
        }
        else if (vf.type == 1) {
            String namelow = vf.file.getName().toLowerCase();
            if(namelow.endsWith(".mp4"))
                playVideoFile(vf.file);
            else if(namelow.endsWith(".jpg"))
                showImage(vf.file);
            else if (namelow.endsWith(".wav"))
                playVideoFile(vf.file);
        }
    }

    private void playVideoFile(File file) {
        Intent playIntent = new Intent(getActivity().getApplicationContext(),
                VideoViewActivity.class);
        playIntent.putExtra("path", file.getPath());
        startActivity(playIntent);
    }
    
    private void showImage(File file){
        Intent imageIntent = new Intent(getActivity().getApplicationContext(),
                ImageViewActivity.class);
        imageIntent.putExtra("path", file.getPath());
        startActivity(imageIntent);
    }
    
    @Override  
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 0){
            int selected = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
            getActivity().setTitle("select "+selected);
        }
        return super.onContextItemSelected(item);
    }
    
    void goUp(){
        if (level == 0)
            return;
        level--;
        refreshList(curDir.getParentFile());
    }
    
    public void onDelClick(){
        if(mode == NORMAL){
            mode = DELETE;
            refreshList(curDir);
        }
        else if(mode == DELETE){
            boolean hasSelect = false;
            ListView lv = getListView();
            for(int i=0; i<lv.getCount(); i++){
                if(lv.isItemChecked(i)){
                    hasSelect = true;
                    break;
                }
            }
            if (!hasSelect) {
                setNormalMode();
                return;
            }
            
            new AlertDialog.Builder(getActivity())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("")
            .setMessage("确定删除这些文件?")
            .setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setNormalMode();
                    }
                })
            .setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSelected();
                        setNormalMode();
                    }
                })
            .show();
        }
    }
    
    void setNormalMode(){
        mode = NORMAL;
        refreshList(curDir);
    }
    
    void deleteSelected(){
        ListView lv = getListView();
        for(int i=0; i<lv.getCount(); i++){
            if(lv.isItemChecked(i))
                deleteAt(i);
        }
    }
    
    void deleteAt(int pos){
        VideoFile vf = items.get(pos);
        Log.i("", "delete "+vf.file.getName());
        deleteFile(vf.file);
    }
    
    // delete file/dir recursively
    void deleteFile(File file){
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
            // delete empty dir
            file.delete();
        }
    }
}
