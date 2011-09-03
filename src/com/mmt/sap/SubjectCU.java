/*
 * SubjectCU.java Class
 * made by Francois Weber MMT09
 * 
 * _DESCRIPTION: loads subjectCU.html to create or update a Subject.
 */

package com.mmt.sap;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.mmt.classes.SubjectDbAdapter;

public class SubjectCU extends Activity {

	private SubjectDbAdapter subjectAdapter;
	private Long mRowId;
	private int isDir;
	
	private static final int ACTIVITY_DETAIL	= 2;
	
	// ---------------------------------------------------
	public class JavaScriptInterface {
	    Context mContext;
	    String name;
	    Long rID;
	    int setDir;
	    
	    /** Instantiate the interface and set the context */
	    JavaScriptInterface(Context c, Long rowID, int isdir) {
	        mContext = c;
	        rID 	 = rowID;
	        setDir 	 = isdir;

	        // Edit Mode
	        if(rID != null) {
	        	Cursor subjectCursor = subjectAdapter.fetchSingle(rID);
	        	startManagingCursor(subjectCursor);
	                 
	        	Log.i("SCU-JS","rID:" + rID + " isDir" + setDir);
	        	name  = subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(SubjectDbAdapter.KEY_TITLE));
	        } else {
	        	this.name = "";
	        	this.rID = null;
	        }
	    }
	           
	    public String getTitle() {
	    	return this.name;
	    }
	    
	    public int getIsDir() {
	    	return this.setDir;
	    }
	    
	    public Long getID() {
	    	return this.rID;
	    }
	    
	    /** Save Data **/
	    public void saveData(String titleVal, long idVal, int dirIs) {
	    	this.name = titleVal;
	    	this.rID = idVal;
	    	this.setDir = dirIs;
	    	
	        if(this.name.length() > 0) {
	        	save(this.name, this.rID, this.setDir);
	        	Toast.makeText(mContext, this.name + " gespeichert", Toast.LENGTH_SHORT).show();
	        } else {
	        	Toast.makeText(mContext, "Name fehlt !", Toast.LENGTH_SHORT).show();
	        }
	    }
	}	
	
	// ---------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        
        // Subject
        subjectAdapter = new SubjectDbAdapter(this);
        subjectAdapter.open();
        
        // GET ID	
		Bundle extras = getIntent().getExtras(); 
		if(extras != null) {
			mRowId = extras.getLong(SubjectDbAdapter.KEY_ROWID);
			if(mRowId == 0) { mRowId = null; }
			isDir = extras.getInt(SubjectDbAdapter.KEY_ISDIR, 0);
		} else {
			mRowId = null;
			isDir = 0;
		}
		
        Log.i("SCU","mRowId: " + mRowId + " isDIR" + isDir);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true); // Enable JS
        
        // Access to interface
        webView.addJavascriptInterface(new JavaScriptInterface(this, mRowId, isDir), "Android");
        
        initWebKit(webView, this);
        //webView.bringToFront();        
    }
    
    // ---------------------------------------------------
    private void initWebKit(WebView view, Context context) {
    	final String mimeType = "text/html";
    	final String encoding = "UTF-8";
    	String htmldata;

    	InputStream is = context.getResources().openRawResource(R.raw.subject_form);
    	
    	try {
    		if(is != null && is.available() > 0) {
    			final byte[] bytes = new byte[is.available()];
    			is.read(bytes);
    			htmldata = new String(bytes);
    			view.loadDataWithBaseURL("file:///android-res/raw/", htmldata, mimeType, encoding, null);
    		}
    	} catch (IOException e) {}
    }    
    
    // ---------------------------------------------------
    // Save Data
    private void save(String title, Long rID, int dirVal) {   	
    	Log.i("SCU.Save","rID: " + rID);
    	
    	// Create
    	if(rID == 0) {
    		rID = subjectAdapter.create(title, dirVal );
    	}
    	// Update
    	else {
    		subjectAdapter.update(rID, title);
    		rID = mRowId;
    	}

    	// Redirect
    	Intent i;
    	
    	// isDirectory
    	if(dirVal > 0) {
    		i = new Intent(this, TodoActivity.class);
    	}
    	// isSubject
    	else {
    		i = new Intent(this, SubjectDetail.class);
    		i.putExtra(SubjectDbAdapter.KEY_ROWID, rID);
    	}
    	
		startActivity(i);
		setResult(RESULT_OK);
    	this.finish();
    }
    
    // --------------
    /*
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SubjectDbAdapter.KEY_ROWID, mRowId);
    }
	*/
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    // Close DatabaseHelper
    @Override    
    protected void onDestroy() {        
        super.onDestroy();
             
        if (subjectAdapter != null) {
        	subjectAdapter.close();
        }
    }
    
}
