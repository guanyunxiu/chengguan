package com.wbapp.omxvideo;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class VideoSettingsActivity extends PreferenceActivity
	implements OnSharedPreferenceChangeListener {

    public static List<Size> videoSizes;
    public static List<Size> picSizes;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		
        sp.registerOnSharedPreferenceChangeListener(this);
		
		Preference server_addr = findPreference("server_addr");
		server_addr.setSummary(getServerAddrString(sp));
		
		Preference cmd_port = findPreference("cmd_port");
		cmd_port.setSummary(getCmdPortString(sp));
		
		Preference data_port = findPreference("data_port");
		data_port.setSummary(getDataPortString(sp));
		
		Preference size = findPreference("size");
		setVideoSizeItems((ListPreference) size);
		size.setSummary(getSizeString(sp));
		
		Preference fps = findPreference("fps");
		fps.setSummary(getFpsString(sp));
		
		Preference bitrate = findPreference("bitrate");
		bitrate.setSummary(getBitrateString(sp));
		
		Preference gop = findPreference("gop");
		gop.setSummary(getGopString(sp));

        Preference picsize = findPreference("pic_size");
        setPicSizeItems((ListPreference) picsize);
        
        Preference savesize = findPreference("save_size");
        setVideoSizeItems((ListPreference) savesize);
	}
	
    private void setVideoSizeItems(ListPreference sizelist) {
        String ents[];
        if (videoSizes == null){
            ents = new String[]{"640x480"};
        }
        else{
            Iterator<Size> it = videoSizes.iterator();
            LinkedList<String> entlist = new LinkedList<String>();
            while (it.hasNext()) {
                Size s = it.next();
                int h = getHeight(s.width);
                if (h != 0) { //there may be nonstandard heights..
                    entlist.add(String.format("%dx%d", s.width, s.height));
                }
            }
            ents = new String[entlist.size()];
            entlist.toArray(ents);
        }
        sizelist.setEntries(ents);
        sizelist.setEntryValues(ents);
    }

    private void setPicSizeItems(ListPreference sizelist) {
        String ents[], vals[];
        if (picSizes == null){
            ents = new String[]{"640x480"};
            vals = ents;
        }
        else{
            LinkedList<String> entlist = new LinkedList<String>();
            LinkedList<String> vallist = new LinkedList<String>();
            int max_width = 0, max_height = 0;
            for (Size s : picSizes) {
                Log.i("", String.format("pic size %dx%d", s.width, s.height));
                entlist.add(String.format("%dx%d", s.width, s.height));
                vallist.add(entlist.getLast());
                if (max_width < s.width && max_height < s.height) {
                    max_width = s.width;
                    max_height = s.height;
                }
            }
            entlist.add("8M");
            vallist.add(String.format("%dx%dx8M", max_width, max_height));
            entlist.add("16M");
            vallist.add(String.format("%dx%dx16M", max_width, max_height));
            entlist.add("24M");
            vallist.add(String.format("%dx%dx24M", max_width, max_height));
            entlist.add("32M");
            vallist.add(String.format("%dx%dx32M", max_width, max_height));
            ents = new String[entlist.size()];
            entlist.toArray(ents);
            vals = new String[vallist.size()];
            vallist.toArray(vals);
        }
        sizelist.setEntries(ents);
        sizelist.setEntryValues(vals);
	}

	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		Preference pref = findPreference(key);
		if(key.equals("server_addr")){
			pref.setSummary(getServerAddrString(sp));
		}
		else if(key.equals("cmd_port")){
			pref.setSummary(getCmdPortString(sp));
		}
		else if(key.equals("data_port")){
			pref.setSummary(getDataPortString(sp));
		}
		else if(key.equals("size")){
			pref.setSummary(getSizeString(sp));
		}
		else if(key.equals("fps")){
			pref.setSummary(getFpsString(sp));
		}
		else if(key.equals("bitrate")){
			pref.setSummary(getBitrateString(sp));
		}
		else if(key.equals("gop")){
			pref.setSummary(getGopString(sp));
		}
	}
	
	public static int getHeight(int width) {
		switch(width)
		{
		case 1920: return 1080;
		case 1280: return 720;
		case 704: return 576;
		case 640: return 480;
		case 352: return 288;
		case 320: return 240;
		case 176: return 144;
		default: return 0;
		}
	}
	
	private String getServerAddrString(SharedPreferences sp){
		return sp.getString("server_addr", getString(R.string.def_server_addr));
	}
	
	private String getCmdPortString(SharedPreferences sp){
		return sp.getString("cmd_port", getString(R.string.def_cmd_port));
	}
	
	private String getDataPortString(SharedPreferences sp){
		return sp.getString("data_port", getString(R.string.def_data_port));
	}
	
	private String getSizeString(SharedPreferences sp) {
		return sp.getString("size", getString(R.string.def_size));
	}
	
	private String getFpsString(SharedPreferences sp){
		return sp.getString("fps", getString(R.string.def_fps));
	}
	
	private String getBitrateString(SharedPreferences sp) {
		return sp.getString("bitrate", getString(R.string.def_bitrate)) + "kbps";
	}
	
	private String getGopString(SharedPreferences sp) {
		return sp.getString("gop", getString(R.string.def_gop));
	}
}
