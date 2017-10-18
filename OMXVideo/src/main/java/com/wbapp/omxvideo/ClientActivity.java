package com.wbapp.omxvideo;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wbapp.suspend.ShowSuspendWindows;

public class ClientActivity extends ListActivity {
    private static ClientActivity instance = null;
    private static boolean winShowStatus = false;
    public static void execHandler(boolean status)
    {
        winShowStatus = status;
        handler.post(updateThread);
    }
    private static Handler handler = new Handler();
    private static Runnable updateThread = new Runnable(){
        public void run(){
            ShowSuspendWindows.setShowStatus(winShowStatus);
       //     handler.postDelayed(updateThread, 300);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("", "client create");
        setContentView(R.layout.client);
        instance = this;
        execHandler(false);

        info = (TextView)findViewById(android.R.id.empty);
        progress = (ProgressBar)findViewById(R.id.client_progress);
        client = new ClientJNI();
        loadSettings();
        if(addr.equals("")) {
            Util.msgBox(this, "请设置远程视频参数");
            showSettings();
            return;
        }
        if(adapter==null) {
            startRefresh();
        }
        else
            bindItems();
    }

    TextView info;
    ProgressBar progress;
    
    ClientJNI client;
    
    // set these to static to allow reusing
    // between orientation switch.
    static List<Resource> items;
    //static ArrayAdapter adapter;
    static TreeViewAdapter adapter;
    static boolean refreshing;
    static Resource current;
    
    String addr;
    int port;
    String user;
    String pswd;
    int latency;
    boolean hardDec;
    
    void loadSettings() {
        Log.i("", "load settings");
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(this);
        String val;
        addr = sp.getString("netclient_addr", getString(R.string.def_netclient_addr));
        val = sp.getString("netclient_port", getString(R.string.def_netclient_port));
        port = Integer.parseInt(val);
        user = sp.getString("netclient_user", getString(R.string.def_netclient_user));
        pswd = sp.getString("netclient_pswd", getString(R.string.def_netclient_pswd));
        val = sp.getString("netclient_latency", getString(R.string.def_netclient_latency));
        latency = Integer.parseInt(val);
        hardDec = sp.getBoolean("hard_dec", false);
    }
    
    void startRefresh() {
        if (refreshing)
            return;
        else
            refreshing = true;
        
        if (adapter!=null) {
            adapter.clear();
        }
        info.setText(String.format("正在连接 %s ..", addr));
        progress.setVisibility(View.VISIBLE);
        new ClientRefreshTask()//.execute();
            .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    
    void bindItems() {
        progress.setVisibility(View.GONE);
        setListAdapter(adapter);
    }
    
    class ClientRefreshTask extends AsyncTask<Void, Void, Boolean>
        implements ClientJNI.ResourceCallback {
        @Override
        protected Boolean doInBackground(Void... arg0) {
            items = new ArrayList<Resource>();
            client.setNetConfig(addr, port, user, pswd, latency);
            client.setResourceCallback(this);
            Log.i("", "client loading");
            boolean r = client.load();
            Log.i("", "client loaded");
            return r;
        }
        Resource root=new Resource(), server, group, cam;
        public void onVisitResource(Resource r) {
            Resource newr = r.clone();
            // setup parents and children
            switch(newr.type) {
                case Resource.RESTYPE_SERVER:
                    root.text = newr.text;
                    newr.parent = root;
                    root.addChild(newr);
                    server = newr;
                    break;
                case Resource.RESTYPE_GROUP:
                    newr.parent = root;
                    root.addChild(newr);
                    group = newr;
                    break;
                case Resource.RESTYPE_CAMERA:
                    newr.parent = group;
                    group.addChild(newr);
                    cam = newr;
                    break;
                case Resource.RESTYPE_CHANNEL:
                    newr.parent = cam;
                    cam.addChild(newr);
                    break;
            }
        }
        protected void onPostExecute(Boolean result) {
            if (result) {
                info.setText("");
                adapter = new TreeViewAdapter(ClientActivity.this, items);
                popNode(root);
                bindItems();
            }
            else {
                progress.setVisibility(View.GONE);
                info.setText("连接失败");
                Util.msgBox(ClientActivity.this, "连接失败！");
            }
            refreshing = false;
        }
    }

    private void popNode(Resource res) {
        adapter.clear();
        List<Resource> children = res.getChildren();
        for (Resource r : children) {
            adapter.add(r);
        }
        current = res;
        setTitle(res.text);
    }

    class TreeViewAdapter extends ArrayAdapter<Resource> {
        public TreeViewAdapter(Context context, List<Resource> objects) {
            super(context, R.layout.item, objects);
            mInflater = LayoutInflater.from(context);
            mlist = objects;
        }

        private LayoutInflater mInflater;
        private List<Resource> mlist;

        public int getCount() {
            return mlist.size();
        }
        public Resource getItem(int position) {
            return mlist.get(position);
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {

            convertView = mInflater.inflate(R.layout.item, null);
            TextView text = (TextView) convertView.findViewById(R.id.text);
            ImageView icon = (ImageView) convertView.findViewById(R.id.icon);

            Resource res = mlist.get(position);
            int level = res.deep;
            icon.setPadding(24 * (level), icon.getPaddingTop(),
                    0, icon.getPaddingBottom());
            text.setText(res.toString());
            icon.setImageResource(res.getImageRes());
            /*if (mfilelist.get(position).isMhasChild()
                    && (mfilelist.get(position).isExpanded() == false)) {
                holder.icon.setImageBitmap(mIconCollapse);
            } else if (mfilelist.get(position).isMhasChild()
                    && (mfilelist.get(position).isExpanded() == true)) {
                holder.icon.setImageBitmap(mIconExpand);
            } else if (!mfilelist.get(position).isMhasChild()){
                holder.icon.setImageBitmap(mIconCollapse);
                holder.icon.setVisibility(View.INVISIBLE);
            }*/

            convertView.setTag(res);
            return convertView;
        }
    }

    @Override
    public void onStart()
    {
        execHandler(false);
        super.onStart();
    }

    @Override
    public void onStop()
    {
        execHandler(true);
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        execHandler(true);
        super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;

        item = menu.add("刷新");
        item.setIcon(android.R.drawable.ic_menu_rotate);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem item) {
                startRefresh();
                return true;
            }
        });

        item = menu.add("设置");
        item.setIcon(android.R.drawable.ic_menu_preferences);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem item) {
                showSettings();
                return true;
            }
        });

        item = menu.add("返回");
        item.setIcon(android.R.drawable.ic_menu_camera);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem item) {
                Intent clientIntent = new Intent(
                        getApplicationContext(), OMXVideoActivity.class);
                startActivity(clientIntent);
                finish();
                return true;
            }
        });
        
        /*
        item = menu.add("退出");
        item.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem item) {
                finish();
                System.exit(0);
                return true;
            }
        });*/

        return true;
    }
    
    final int REQUEST_SETTING = 0;
    final int REQUEST_PLAY = 1;
    
    void showSettings() {
        Intent settingsIntent = new Intent(getApplicationContext(),
                ClientSettingsActivity.class);
        startActivityForResult(settingsIntent, REQUEST_SETTING);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(requestCode == REQUEST_SETTING) {
            String oldAddr = addr;
            int oldPort = port;
            String oldUser = user;
            String oldPass = pswd;
            loadSettings();
            if (!addr.equals(oldAddr) || port != oldPort
                    || !user.equals(oldUser) || !pswd.equals(oldPass)) {
                // refresh if any setting is changed
                startRefresh();
            }
        }
        else if(requestCode == REQUEST_PLAY) {
            client.stopNetVideo();
            Log.i("", "play activity finished");
        }
    }
    
    void startPlayVideo(Resource res) {
        client.startNetVideo(res.id, hardDec);
        Intent playIntent = new Intent(getApplicationContext(),
                PlayActivity.class);
        String resName;
        if (res.type==Resource.RESTYPE_CHANNEL && res.parent!=null) {
            Resource cam = res.parent;
            resName = String.format("%s\n%s", cam.text, res.text);
        }
        else
            resName = res.text;
        playIntent.putExtra("resource_name", resName);
        playIntent.putExtra("hard_dec", hardDec);
        startActivityForResult(playIntent, REQUEST_PLAY);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Resource res = (Resource)v.getTag();
        if(res==null)
            return;
        if (res.getChildren() != null) {
            popNode(res);
        }
        else if (res.getCanPlay()) {
            startPlayVideo(res);
        }
    }
    
    @Override
    public void onBackPressed() {
        if (current!=null && current.parent != null) {
            popNode(current.parent);
        }
    }
    
    public void onMenuClick(View v) {
        openOptionsMenu();
    }

    /**
     * 监听是否点击了home键将客户端推到后台
     */
    public static BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    //表示按了home键,程序到了后台
                    execHandler(true);
                    //instance.finish();
                    //Toast.makeText(OMXVideoActivity.instance.getApplicationContext(), "1111111111111home", Toast.LENGTH_LONG).show();
                }else if(TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG)){
                    //表示长按home键,显示最近使用的程序列表
                }
            }
        }
    };
}
