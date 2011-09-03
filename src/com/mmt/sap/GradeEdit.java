// TODO : deprecated ??
package com.mmt.sap;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
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

public class GradeEdit extends Activity {

    private Spinner mGradeText;
    private Spinner mTypeText;
    private Spinner mWeightText;
    private Spinner mSubjectSpinner;
    private Long mRowId;
    private GradeDbAdapter gradeAdapter = null;
    private SubjectDbAdapter subAdapter = null;

    private static String[] SUBJECTSPINNER = new String[] {
        SubjectDbAdapter.KEY_ROWID, SubjectDbAdapter.KEY_TITLE
    };
    
    private TextView mDateDisplay;
    private Button mPickDate;
    private int mYear;
    private int mMonth;
    private int mDay;
    private long selectedSubjectId;
    
    static final int DATE_DIALOG_ID = 0;
    
    // ---------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grade_edit);
        setTitle(R.string.edit_note);
             
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
        // -- Grade Spinner
        mGradeText = (Spinner) findViewById(R.id.grade_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.grade_16_string, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGradeText.setAdapter(adapter);
        
        // -- Type Spinner
        mTypeText = (Spinner) findViewById(R.id.grade_type_spinner);
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(this, R.array.grade_type_string,  android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeText.setAdapter(adapterType);
        
        // -- Weight Spinner
        mWeightText = (Spinner) findViewById(R.id.grade_weight_spinner);
        ArrayAdapter<CharSequence> adapterWeight = ArrayAdapter.createFromResource(this, R.array.grade_weight_string, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mWeightText.setAdapter(adapterWeight);
                        
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
        
        gradeAdapter = new GradeDbAdapter(this);
        gradeAdapter.open();
                
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(GradeDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(GradeDbAdapter.KEY_ROWID)
									: null;
		}
		populateFields();
		
        confirmButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                saveState();
        		setResult(RESULT_OK);	// When an activity exits, it can call setResult(int) to return data back to its parent
                finish();
        	}
        });
       
    }
    
    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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
    // Edit Form -> get data of the entry
    private void populateFields() {
        if (mRowId != null) {
            Cursor note = gradeAdapter.fetchNote(mRowId);
            startManagingCursor(note);

            mDateDisplay.setText(note.getString(note.getColumnIndexOrThrow(GradeDbAdapter.KEY_DATE)));
            
            Integer getGradeValue = new Integer(note.getString(note.getColumnIndexOrThrow(GradeDbAdapter.KEY_GRADE))) - 1;
            mGradeText.setSelection(getGradeValue);
            
            Integer getWeightValue = new Integer(note.getString(note.getColumnIndexOrThrow(GradeDbAdapter.KEY_WEIGHT))) - 1;
            mWeightText.setSelection(getWeightValue);
            
            //Integer getTypeValue = new Integer(note.getString(note.getColumnIndexOrThrow(GradeDbAdapter.KEY_TYPE))) - 1;
            //mTypeText.setSelection(getTypeValue);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(GradeDbAdapter.KEY_ROWID, mRowId);
    }

    // onPause() is always called when the Activity ends
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
         
        if (gradeAdapter != null) {
        	gradeAdapter.close();
        }
        
        if (subAdapter != null) {
        	subAdapter.close();
        }
    }
    
    // ---------------------------------------------------
    // Save Entry
    private void saveState() {
       
        // Get Date Value
    	int newMonth = mMonth + 1;
        String date = (new Integer(mYear)).toString() + "-" + (new Integer(newMonth)).toString() + "-" + (new Integer(mDay)).toString();
    	
        // Get Grade Value
        int posGrade = mGradeText.getSelectedItemPosition();
        int gradeArrVal = getResources().getIntArray(R.array.grade_16_int)[posGrade];
       
        // Get Type Value
        int posType = mTypeText.getSelectedItemPosition();
        String typeArrVal = getResources().getStringArray(R.array.grade_type_string)[posType];
        
        // Get Weight Value
        int posWeight = mWeightText.getSelectedItemPosition();
        int weightArrVal = getResources().getIntArray(R.array.grade_weight_int)[posWeight];
        
        // Get SubjectID 
        long subject_id = selectedSubjectId;

        Log.i("onSAVE", "SubjectID " + subject_id + " Type: " + typeArrVal);        
        Log.i("onSAVE", "Date: " + date + " Weight: " + weightArrVal);
            
        // Insert into Database
	    if (mRowId == null) {
	            long id = gradeAdapter.createNote(gradeArrVal, date, typeArrVal, subject_id, weightArrVal );
	            if (id > 0) {
	                mRowId = id;
	            }
	    // Update Entry
	    } else {
	    	gradeAdapter.updateNote(mRowId, gradeArrVal, date, typeArrVal, subject_id, weightArrVal);
	    }
    }
}

