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

import com.mmt.classes.DayoffDbAdapter;
import com.mmt.classes.GradeDbAdapter;
import com.mmt.classes.SubjectDbAdapter;
import com.mmt.classes.TodoDbAdapter;

public class DayoffCU extends Activity {


    private Long mRowId;
    private DayoffDbAdapter dayoffAdapter = null;

    private EditText mReasonText;
    private TextView mDateFrom;
    private TextView mDateTo;
    private Button mPickDateFrom;
    private Button mPickDateTo;
    
    private int mYear;
    private int mMonth;
    private int mDay;
    
    private int mYearTo;
    private int mMonthTo;
    private int mDayTo;
    
    static final int DATE_DIALOG_ID = 0;
	static final int DATE_DIALOG_ID2 = 1;  
            
    // ---------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dayoff_edit);
        setTitle("Fehltag");
       
        // ----------------
        // Build Form
        mReasonText 		= (EditText) findViewById(R.id.title);
        // -- Date
        mDateFrom 		= (TextView) findViewById(R.id.dateFrom);
        mDateTo 		= (TextView) findViewById(R.id.dateTo);
        
        mPickDateFrom 	= (Button) findViewById(R.id.pickDateFrom);
        mPickDateTo 	= (Button) findViewById(R.id.pickDateTo);
     	
        // add a click listener to the button
        mPickDateFrom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        
        mPickDateTo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID2);
            }
        });
        
        // Get the current date
        final Calendar cal = Calendar.getInstance();
        mYear 	= mYearTo 	= cal.get(Calendar.YEAR);
        mMonth 	= mMonthTo 	= cal.get(Calendar.MONTH);
        mDay 	= mDayTo 	= cal.get(Calendar.DAY_OF_MONTH);
        updateDisplay();
        updateDisplayTo();
        
		populateFields();
        
        Button confirmButton = (Button) findViewById(R.id.confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		if (mReasonText.getText().toString().length() == 0) {
        			dayoffAdapter.toastMessage(R.string.missingReason);
        		} else {
        			saveState();
        			setResult(RESULT_OK);
        			finish();
        		}
        	}
        });      
        // ----------------
        dayoffAdapter = new DayoffDbAdapter(this);
        dayoffAdapter.open();
        
        mRowId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable(SubjectDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(SubjectDbAdapter.KEY_ROWID) : null;
		}
    }
    
    // ---------------------------------------------------
    // DATEPICKER
    // updates the date in the TextView
    private void updateDisplay() {
        StringBuilder d = 
            new StringBuilder()
                    // Month is 0 based so add 1
        			.append(mYear).append("-")
                    .append(mMonth + 1).append("-")
                    .append(mDay);                    
        mDateFrom.setText(d);
    }
    
    private void updateDisplayTo() {
        StringBuilder d = 
            new StringBuilder()
                    // Month is 0 based so add 1
        			.append(mYearTo).append("-")
                    .append(mMonthTo + 1).append("-")
                    .append(mDayTo);                    
        mDateTo.setText(d);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        case DATE_DIALOG_ID2:
            return new DatePickerDialog(this,
                        mDateSetListenerTo,
                        mYearTo, mMonthTo, mDayTo);
        }
        return null;
    }
    
    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {    	
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
    };
    
    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListenerTo =
            new DatePickerDialog.OnDateSetListener() {    	
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYearTo = year;
                    mMonthTo = monthOfYear;
                    mDayTo = dayOfMonth;
                    updateDisplayTo();
                }
    };
    
    // ---------------------------------------------------
    // Edit Form -> get data of the entry
    private void populateFields() {
        if (mRowId != null) {
        	      	
            Cursor note = dayoffAdapter.fetchSingle(mRowId);
            startManagingCursor(note);
            
            mReasonText.setText(note.getString(note.getColumnIndexOrThrow(dayoffAdapter.KEY_REASON)));
            mDateTo.setText(note.getString(note.getColumnIndexOrThrow(dayoffAdapter.KEY_TO)));
            mDateFrom.setText(note.getString(note.getColumnIndexOrThrow(dayoffAdapter.KEY_FROM)));            
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(DayoffDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //saveState();
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
         
        if (dayoffAdapter != null) {
        	dayoffAdapter.close();
        }
    }
    
    // ---------------------------------------------------
    // Save Entry
    private void saveState() {
       
        // Get Date Value
    	int newMonth = mMonth + 1;
        String dateFrom = mDateFrom.getText().toString(); // (new Integer(mYear)).toString() + "-" + (new Integer(newMonth)).toString() + "-" + (new Integer(mDay)).toString(); // mDateDisplay.getText().toString();  	
        String dateTo 	= mDateTo.getText().toString();

        String reason =  mReasonText.getText().toString();
        
        Log.i("onSAVE", "Date: " + dateFrom + " title: " + reason);
            
        // Insert into Database
	    if (mRowId == null) {
	            long id = dayoffAdapter.create(reason, dateFrom, dateTo, 0);
	            if (id > 0) {
	                mRowId = id;
	            }
	    // Update Entry
	    } else {
	    	dayoffAdapter.update(mRowId, reason, dateFrom, dateTo, 0);
	    }
    }
}

