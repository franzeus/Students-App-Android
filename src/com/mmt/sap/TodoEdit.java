package com.mmt.sap;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mmt.classes.GradeDbAdapter;
import com.mmt.classes.SubjectDbAdapter;

public class TodoEdit extends Activity {
	
	private TextView labelTitle;
	private EditText mTitleText;
    private Long mRowId;
    private SubjectDbAdapter mDbHelper;

                
    // ---------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new SubjectDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.subject_edit);
        setTitle("Neuen Ordner anlegen");

        mTitleText = (EditText) findViewById(R.id.title);
      
        labelTitle = (TextView) findViewById(R.id.labelTitle);
        labelTitle.setText("Name");
        
        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(SubjectDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(SubjectDbAdapter.KEY_ROWID)
									: null;
		}

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
    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchSingle(mRowId);
            startManagingCursor(note);
            
            // mTitleText
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(SubjectDbAdapter.KEY_TITLE)));
                        
            /*
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY))); */
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(SubjectDbAdapter.KEY_ROWID, mRowId);
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

    private void saveState() {
        /*
    	String title = mTitleText.getText().toString();
    	int term_id = 1;
    	
        if (mRowId == null) {
            long id = mDbHelper.createDir(title, term_id );
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.update(mRowId, title, term_id);
        } */
    }

}
