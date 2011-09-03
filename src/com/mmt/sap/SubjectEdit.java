package com.mmt.sap;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.mmt.classes.GradeDbAdapter;
import com.mmt.classes.SubjectDbAdapter;
import com.mmt.classes.TodoDbAdapter;

public class SubjectEdit extends Activity {

    private EditText mTitleText;
    private Long mRowId;
    private Long dirID;
    private SubjectDbAdapter subjectAdapter = null;

    // ---------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_edit);
       
        // ----------------
        // Build Form
        
        mTitleText = (EditText) findViewById(R.id.title);
        Button confirmButton = (Button) findViewById(R.id.confirm);     
        
        // ----------------
        
        subjectAdapter = new SubjectDbAdapter(this);
        subjectAdapter.open();
        
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(SubjectDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(SubjectDbAdapter.KEY_ROWID)
									: null;
		}
		
        dirID = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable("dirID");
		if (dirID == null) {
			Bundle extras = getIntent().getExtras();
			dirID = extras != null ? extras.getLong("dirID") : null;
	        setTitle("Fach");
		} 
		if(dirID != null) { setTitle("Ordner"); }
		
		
		Log.i("SubEdit", "mRowId: " + mRowId + " DirID: " + dirID);
		populateFields();
		
        confirmButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		saveState();
                setResult(RESULT_OK);
                finish();
        	}
        });      
    }
        
    // ---------------------------------------------------
    // Edit Form -> get data of the entry
    private void populateFields() {
        if (mRowId != null && mRowId > 0) {        	
            Cursor note = subjectAdapter.fetchSingle(mRowId);
            startManagingCursor(note);           
            
            String getTitleValue = note.getString(note.getColumnIndexOrThrow(SubjectDbAdapter.KEY_TITLE));
            mTitleText.setText(getTitleValue);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(TodoDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
       // saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    // Close DatabaseHelper
    @Override    
    protected void onDestroy() {        
        super.onDestroy();
         
        if (subjectAdapter != null) {
        	subjectAdapter.close();
        }
    }
    
    // ---------------------------------------------------
    // Save Entry
    private void saveState() {

        // Get Title Value
        String title =  mTitleText.getText().toString();     
        int isDir = dirID == null ? 0 : 1;
        
        Log.i("onSAVE", " title: " + title + " DirID: " + isDir);
        
        // Insert into Database
	    if (mRowId == null || mRowId == 0) {
	            long id = subjectAdapter.create(title, isDir);
	            if (id > 0) {
	                mRowId = id;
	            }
	    // Update Entry
	    } else {
	    	subjectAdapter.update(mRowId, title);
	    }
    }
}