package com.inflectionPoint.interpop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Environment;
import android.preference.PreferenceManager;

public class FileManager {

	Context context;
	AssetManager assetManager;
	File externalStoragePath;
	
	public FileManager(Context context) {
		this.context = context;
		this.assetManager = context.getAssets();
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			this.externalStoragePath = new File(context.getExternalFilesDir(null), "interpop");
		}
		else 
			this.externalStoragePath = new File(context.getFilesDir(), "interpop");
		
	}
	
	public InputStream openAssets(String fileName) throws IOException {
		return assetManager.open(fileName);
	}
	
	public InputStream openFile(String fileName) throws IOException {
		return new FileInputStream(externalStoragePath);
	}
	
	public OutputStream writeFile(String fileName) throws IOException {
		return new FileOutputStream(externalStoragePath);
	}
	
	public SharedPreferences getPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
}
