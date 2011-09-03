package com.mmt.sap;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mmt.classes.SubjectDbAdapter;
import com.mmt.classes.TodoDbAdapter;

public class TaskEdit extends Activity {
	
	private EditText mTitleText;

    private Long mRowId;
    private TodoDbAdapter mDbHelper;

                
    // ---------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new TodoDbAdapter(this);
        mDbHelper.open();

        setContentView(R.layout.subject_edit);
        setTitle("Neue Aufgabe");

        mTitleText = (EditText) findViewById(R.id.title);

        
        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(TodoDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(TodoDbAdapter.KEY_ROWID)
									: null;
		}

		populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
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
                    note.getColumnIndexOrThrow(TodoDbAdapter.KEY_TITLE)));
                        
            /*
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY))); */
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
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        
    	String title = mTitleText.getText().toString();
    	int term_id = 1;
    	/*
        if (mRowId == null) {
            long id = mDbHelper.createTodo(title, mRowId );
            if (id > 0) {
                mRowId = id;
            }
        } else {
            mDbHelper.updateTodo(mRowId, title);
        }
        */
    }

}
