// TODO : deprecated ??
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

public class TaskCU extends Activity {

    private EditText mTitleText;
    private Long mRowId;
    private Long dirID;
    private Spinner checkedSpinner;
    private TodoDbAdapter todoAdapter = null;
       
    private TextView mDateDisplay;
    private Button mPickDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    
    static final int DATE_DIALOG_ID = 0;
    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {    	
                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };
            
    // ---------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_edit);
        setTitle("Task");
       
        // ----------------
        // Build Form
        mTitleText = (EditText) findViewById(R.id.title);
        // -- Date
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        mPickDate = (Button) findViewById(R.id.pickDate);
     	// add a click listener to the button
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        // Get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
                
        Button confirmButton = (Button) findViewById(R.id.confirm);
        
        // ----------------
        todoAdapter = new TodoDbAdapter(this);
        todoAdapter.open();
        
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(SubjectDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(SubjectDbAdapter.KEY_ROWID)
									: null;
		}
		
        dirID = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable("dirID");
		if (dirID == null) {
			Bundle extras = getIntent().getExtras();
			dirID = extras != null ? extras.getLong("dirID")
									: null;
		}
		
		// -- Checked Spinner
        checkedSpinner = (Spinner) findViewById(R.id.task_checked_spinner);
        /*
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.task_checked_string, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        checkedSpinner.setAdapter(adapter);
		*/
		populateFields();
		
        confirmButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		saveState();
                setResult(RESULT_OK);	// When an activity exits, it can call setResult(int) to return data back to its parent
                finish();
        	}
        });      
    }
    
    // ---------------------------------------------------
    // DATEPICKER
    // updates the date in the TextView
    private void updateDisplay() {
        mDateDisplay.setText(
            new StringBuilder()
                    // Month is 0 based so add 1
            		.append(mDay).append("-")
                    .append(mMonth + 1).append("-")
                    .append(mYear).append(" "));
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }
    
    
    // ---------------------------------------------------
    // Edit Form -> get data of the entry
    private void populateFields() {
        if (mRowId != 0) { //mRowId != null ||
        	
        	Log.i("TaskCUPopField", "mRowId: " + mRowId);
        	
            Cursor note = todoAdapter.fetchSingle(mRowId);
            startManagingCursor(note);

            mDateDisplay.setText(note.getString(note.getColumnIndexOrThrow(TodoDbAdapter.KEY_DEADLINE)));
            
            Integer getTitleValue = new Integer(note.getString(note.getColumnIndexOrThrow(TodoDbAdapter.KEY_TITLE)));
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
         
        if (todoAdapter != null) {
        	todoAdapter.close();
        }
    }
    
    // ---------------------------------------------------
    // Save Entry
    private void saveState() {
       
        // Get Date Value
    	int newMonth = mMonth + 1;
        String date = (new Integer(mYear)).toString() + "-" + (new Integer(newMonth)).toString() + "-" + (new Integer(mDay)).toString(); // mDateDisplay.getText().toString();  	

        // Get Grade Value
        int posChecked = checkedSpinner.getSelectedItemPosition();
        int checkedVal = getResources().getIntArray(R.array.task_checked_int)[posChecked];
               
        // Get Title Value
        String title =  mTitleText.getText().toString();
               
        Log.i("onSAVE", "Date: " + date + " title: " + title + " DirID: " + dirID);
            
        // Insert into Database
	    if (mRowId == 0) {
	            long id = todoAdapter.createTodo(dirID, title, date, checkedVal);
	            if (id > 0) {
	                mRowId = id;
	            }
	    // Update Entry
	    } else {
	    	todoAdapter.updateTodo(mRowId, title, date, checkedVal);
	    }
    }
}

