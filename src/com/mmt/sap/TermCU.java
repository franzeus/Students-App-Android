package com.mmt.sap;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mmt.classes.TermDbAdapter;

public class TermCU extends Activity {

	private Long mRowId;
    private TermDbAdapter termAdapter = null;
	
	private EditText mTitleText;
	private Spinner activeSpinner;
    
    // ---------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.term_edit);
        setTitle("Schuljahre");
       
        // ----------------
        // Build Form
        mTitleText = (EditText) findViewById(R.id.title);
        activeSpinner = (Spinner) findViewById(R.id.active_spinner);
 
        Button confirmButton = (Button) findViewById(R.id.confirm);
        
        // ----------------
        termAdapter = new TermDbAdapter(this);
        termAdapter.open();
        
        if(termAdapter.countTerms() == 1) {
        	activeSpinner.setVisibility(0);
    	}
        
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(termAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(termAdapter.KEY_ROWID)
									: null;
		}
		
        populateFields();
		
        confirmButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
                setResult(RESULT_OK);	// When an activity exits, it can call setResult(int) to return data back to its parent
                finish();
        	}
        });      
    }

    
    // ---------------------------------------------------
    // Edit Form -> get data of the entry
    private void populateFields() {
        if (mRowId != null) {
            Cursor note = termAdapter.fetchSingle(mRowId);
            startManagingCursor(note);
            
            String getTitleValue = note.getString(note.getColumnIndexOrThrow(TermDbAdapter.KEY_TITLE));
            mTitleText.setText(getTitleValue);
            
            if(termAdapter.countTerms() == 1) {
            	activeSpinner.setVisibility(8);
            	TextView labelActiveSpinner = (TextView) findViewById(R.id.activeText);
            	labelActiveSpinner.setVisibility(8);
        	} else {            
        		Integer getActiveValue = new Integer(note.getString(note.getColumnIndexOrThrow(TermDbAdapter.KEY_ISACTIVE)));
        		activeSpinner.setSelection(getActiveValue);
        	}
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(TermDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
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
         
        if (termAdapter != null) {
        	termAdapter.close();
        }
    }
    
    // ---------------------------------------------------
    // Save Entry
    private void saveState() {
       
        // Get Title Value
        String title =  mTitleText.getText().toString();
        
        // Get active Value
        int posActive = activeSpinner.getSelectedItemPosition();
        int isActive = getResources().getIntArray(R.array.isactive_int)[posActive];
        
        if(title.length() > 0) {        	
        	Toast.makeText(this, "Schuljahr gespeichert", Toast.LENGTH_SHORT).show();
        
        	// Insert into Database
    	    if (mRowId == null) {
    	            long id = termAdapter.create(title, isActive);
    	            if (id > 0) {
    	                mRowId = id;
    	            }
    	    // Update Entry
    	    } else {
    	    	// inactive can only be set if term.count higher than one 
    	    	if(termAdapter.countTerms() == 1 && isActive == 0) {
    	    		Toast.makeText(this, "Schuljahr gespeichert - aber aktiv", Toast.LENGTH_SHORT).show();
    	    		isActive = 1;
    	    	}
    	    	termAdapter.update(mRowId, title, isActive);
    	    }
        }
    }
}
