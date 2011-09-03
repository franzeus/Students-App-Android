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

import com.mmt.classes.GradeDbAdapter;
import com.mmt.classes.SubjectDbAdapter;

public class GradeCU extends Activity {

	private GradeDbAdapter gradeAdapter;
	private SubjectDbAdapter subjectAdapter;
	private Long mRowId;
	
	private static final int ACTIVITY_DETAIL	= 2;
	
	// ---------------------------------------------------
	public class JavaScriptInterface {
	    Context mContext;
	    
	    Long rID;
	    int grade;
	    int subject_id;
	    String type;
	    String date;	  
	    int weight;
	    String subject_name;
	    String subject_list;
	    	    
	    /** Instantiate the interface and set the context */
	    JavaScriptInterface(Context c, Long rowID) {
	        mContext = c;
	        rID = rowID;
	        
	        // Get Subjects
	        subjectAdapter = new SubjectDbAdapter(mContext);
	        subjectAdapter.open();

	        Cursor c1 = subjectAdapter.fetchAll();
	        while(c1.moveToNext()) {
	        	subject_list += "<option value=\"" + c1.getInt(c1.getColumnIndexOrThrow(SubjectDbAdapter.KEY_ROWID)) +"\">" + c1.getString(c1.getColumnIndexOrThrow(SubjectDbAdapter.KEY_TITLE)) + "</option>";
	        }
	        c1.close();
	        	        
	        // Edit Mode
	        if(rID != null) {	        		        	
	        	// Fetch Data from Database
	        	Cursor gradeCursor = gradeAdapter.fetchNote(rID);
	        	startManagingCursor(gradeCursor);
	                 
	        	grade  = gradeCursor.getInt(gradeCursor.getColumnIndexOrThrow(GradeDbAdapter.KEY_GRADE));
	        	subject_id  = gradeCursor.getInt(gradeCursor.getColumnIndexOrThrow(GradeDbAdapter.KEY_SUBJECTID));
	        	subject_name= gradeCursor.getString(gradeCursor.getColumnIndexOrThrow(GradeDbAdapter.KEY_SUBJECTNAME));
	        	type = gradeCursor.getString(gradeCursor.getColumnIndexOrThrow(GradeDbAdapter.KEY_TYPE));
	        	date = gradeCursor.getString(gradeCursor.getColumnIndexOrThrow(GradeDbAdapter.KEY_DATE));
	        	weight = gradeCursor.getInt(gradeCursor.getColumnIndexOrThrow(GradeDbAdapter.KEY_WEIGHT));
	        } else {
	        	this.rID = null;
	        }
	    }
	           
	    public int getGrade() {
	    	return this.grade;
	    }
	    
	    public int getSubjectId() {
	    	return this.subject_id;
	    }
	    
	    public String getSubjectName() {
	    	return this.subject_name;
	    }
	    
	    public String getSubjectList() {
	    	return this.subject_list;
	    }
	    
	    public int getWeight() {
	    	return this.weight;
	    }
	    
	    public String getType() {
	    	return this.type;
	    }
	    
	    public String getDate() {
	    	return this.date;
	    }
	    
	    public Long getID() {
	    	return this.rID;
	    }
	    
	    /** Save Data **/
	    public void saveData(long idVal, int gradeVal, int subjectIdVal, String typeVal, String dateVal, int weight) {
	    	this.rID = idVal;
	    	this.grade = gradeVal;	    	
	    	this.subject_id = subjectIdVal;
	    	this.type = typeVal;
	    	this.date = dateVal;
	    	this.weight = weight;
	    	
	    	//Log.i("GCU-SAVE","rID:" + idVal + " Grade: " + grade + " SubjectID: " + subjectIdVal);
	    	
	        if(this.subject_id > 0) {
	        	save(this.rID, this.grade, this.date, this.type, this.subject_id, this.weight);
	        	Toast.makeText(mContext, "Note gespeichert", Toast.LENGTH_SHORT).show();
	        } else {
	        	Toast.makeText(mContext, "Fehler: SID(" + subjectIdVal + ") GE("+ gradeVal +")", Toast.LENGTH_SHORT).show();
	        }
	    }
	}	
	
	// ---------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        
        gradeAdapter = new GradeDbAdapter(this);
        gradeAdapter.open();
        
		Bundle extras = getIntent().getExtras(); 
		if(extras != null) {
			mRowId = extras.getLong(SubjectDbAdapter.KEY_ROWID);
			if(mRowId == 0) { mRowId = null; }
		} else {
			mRowId = null;
		}
		
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true); // Enable JS
        
        // Access to interface
        webView.addJavascriptInterface(new JavaScriptInterface(this, mRowId), "Android");
        
        initWebKit(webView, this);
        webView.bringToFront();        
    }
    
    // ---------------------------------------------------
    private void initWebKit(WebView view, Context context) {
    	final String mimeType = "text/html";
    	final String encoding = "UTF-8";
    	String htmldata;

    	InputStream is = context.getResources().openRawResource(R.raw.grade_form);
    	
    	try {
    		if(is != null && is.available() > 0) {
    			final byte[] bytes = new byte[is.available()];
    			is.read(bytes);
    			htmldata = new String(bytes);
    			view.loadDataWithBaseURL("file:///android_res/raw/", htmldata, mimeType, encoding, null);
    		}
    	} catch (IOException e) {}
    }
    
    // ---------------------------------------------------
    // Save Data
    // this.rID, this.grade, this.date, this.type, this.subject_id
    private void save(Long rID, int gradeVal, String dateVal, String typeVal, int subjectIdVal, int weight) {
    	
    	// Create
    	if(rID == 0) {
    		rID = gradeAdapter.createNote(gradeVal, dateVal, typeVal, subjectIdVal, weight );
    	}
    	// Update
    	else {
    		gradeAdapter.updateNote(rID, gradeVal, dateVal, typeVal, subjectIdVal, weight );
    	}

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
        
        if (gradeAdapter != null) {
        	gradeAdapter.close();
        }
    }
    
}
