package com.mmt.sap;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.mmt.classes.GradeDbAdapter;
import com.mmt.classes.ScheduleDbAdapter;
import com.mmt.classes.SubjectDbAdapter;
import com.mmt.classes.TermDbAdapter;

public class ScheduleCU extends Activity {

	private Integer mRowId;
	private Long dayId;
    
	private ScheduleDbAdapter scheduleAdapter = null;
    private SubjectDbAdapter subAdapter = null;
    
	private EditText mTitleText;
	private Spinner mSubjectSpinner;
	private long selectedSubjectId;
    // ---------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_edit);
        setTitle(R.string.title_schedule);
        
        // ----------------
        // Build Form
        // -- Subject Spinner
        subAdapter = new SubjectDbAdapter(this);
        subAdapter.open();
        Cursor getSubjectsCursor = subAdapter.fetchAll();
        startManagingCursor(getSubjectsCursor);
        
        mSubjectSpinner = (Spinner) findViewById(R.id.subject_spinner);       
        SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(this,
        	    android.R.layout.simple_spinner_item, // Use a template
        	                                          // that displays a
        	                                          // text view
        	    getSubjectsCursor, // Give the cursor to the list adapter
        	    new String[] {SubjectDbAdapter.KEY_TITLE}, // Map the NAME column in the
        	                                         // database to...
        	    new int[] {android.R.id.text1}); // The "text1" view defined in
        	                                     // the XML template
        	                                         
        	adapter2.setDropDownViewResource(android.R.layout.simple_list_item_1); //simple_spinner_dropdown_item
        	mSubjectSpinner.setAdapter(adapter2);

        	// onSelect Subject Spinner
        	mSubjectSpinner.setOnItemSelectedListener(
                    new OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectedSubjectId = id;
                        }
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
        subAdapter.close();
        Button confirmButton = (Button) findViewById(R.id.confirm);
        
        // ----------------
       
        mRowId = (savedInstanceState == null) ? null : (Integer) savedInstanceState.getSerializable("rowId");
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getInt("rowId") : null;
		}
		
		dayId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable("dayId");
			if (dayId == null) {
				Bundle extras = getIntent().getExtras();
				dayId = extras != null ? extras.getLong("dayId") : null;
		}
		Log.i("ScheduleCU"," dayId: " + dayId + " rowID: " + mRowId);
        confirmButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		saveState();
                setResult(RESULT_OK);	// When an activity exits, it can call setResult(int) to return data back to its parent
                finish();
        	}
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable("rowId", mRowId);
        outState.putSerializable("dayId", dayId);
    }

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
         
        if (scheduleAdapter != null) {
        	scheduleAdapter.close();
        }
    }
    
    // ---------------------------------------------------
    // Save Entry
    private void saveState() {
    	// Get SubjectID 
        long subject_id = selectedSubjectId;
        Log.i("SaveSchedule"," SID:" + subject_id + " dayId: " + dayId + " rowID: " + mRowId);
        	
    	scheduleAdapter.create(subject_id, dayId, mRowId);
    	/*if (id > 0) {
    		mRowId = id
        }*/
    }
}
