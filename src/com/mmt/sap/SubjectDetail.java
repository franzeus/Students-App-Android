package com.mmt.sap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.mmt.classes.GradeDbAdapter;
import com.mmt.classes.GradeListAdapter;
import com.mmt.classes.SubjectDbAdapter;
import com.mmt.classes.TaskListAdapter;
import com.mmt.classes.TodoDbAdapter;

public class SubjectDetail extends ListActivity {

	private static final int ACTIVITY_CREATE	= 0;
	private static final int ACTIVITY_EDIT 		= 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	
	private TextView txtTitle;
	private TextView txtAverage;
	private Long mRowId;
	
	private GradeDbAdapter gradeAdapter;
	private SubjectDbAdapter subjectAdapter;
	private TodoDbAdapter todoAdapter;
	
	// ---------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_detail);
        
        txtAverage = (TextView) findViewById(R.id.info);
        
        subjectAdapter = new SubjectDbAdapter(this);
        subjectAdapter.open();
        
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(SubjectDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(SubjectDbAdapter.KEY_ROWID)
									: null;
		}
        
        if (mRowId != null) {
            Cursor subjectCursor = subjectAdapter.fetchSingle(mRowId);
            startManagingCursor(subjectCursor);

            // Set Title
            txtTitle = (TextView) findViewById(R.id.subTitle);
            txtTitle.setText(subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(subjectAdapter.KEY_TITLE)));
        
            gradeAdapter = new GradeDbAdapter(this);
	        gradeAdapter.open();
	
	        // List grades
	        fillData();
	        fillTaskList();
        }
    }
       
    // ---------------------------------------------------------
    private void fillData() {
        Cursor notesCursor = gradeAdapter.fetchSubjectGrades(mRowId);
        startManagingCursor(notesCursor);
    
        // Get Average-Grade of term
        txtAverage.setText("Durchschnitt: " + gradeAdapter.getAverageGrade(mRowId, 0) + "   #" + notesCursor.getCount());
        
        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{GradeDbAdapter.KEY_DATE, GradeDbAdapter.KEY_SUBJECTNAME, GradeDbAdapter.KEY_TYPE, GradeDbAdapter.KEY_GRADE};

        // and an array of the fields we want to bind those fields to
        int[] to = new int[]{R.id.text1, R.id.text2,  R.id.text3, R.id.text4};
        
        GradeListAdapter mListAdapter = new GradeListAdapter(this, notesCursor, from, to);
        setListAdapter(mListAdapter);
    }
    
    // ---------------------------------------------------------
    private void fillTaskList() {
    	Log.i("FillTaskList", "mRowId: " + mRowId);
    	
    	todoAdapter = new TodoDbAdapter(this);
	    todoAdapter.open();
    	
        Cursor cursor = todoAdapter.fetchAllTodos(mRowId);
        startManagingCursor(cursor);
        
        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{TodoDbAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to
        int[] to = new int[]{R.id.text1};
        
        TaskListAdapter mListAdapter = new  TaskListAdapter(this, cursor, from, to);
        ListView listView2 = (ListView) findViewById(R.id.list2);
        listView2.setAdapter(mListAdapter);
    }
    
    // ---------------------------------------------------------
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, GradeEdit.class);
        i.putExtra(GradeDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    // ---------------------------------------------------------
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	
     	 // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_insert:
            newGrade();
            return true;
        case R.id.menu_edit:
            editSubject();
            return true;
        case R.id.menu_back:
            goBackToMenu();
            return true;
        case R.id.menu_remove:
        	deleteSubject();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void deleteSubject() {    	
    	
    	 new AlertDialog.Builder(this)
         .setIcon(android.R.drawable.ic_dialog_alert)
         .setTitle("Fach loeschen?")
         .setMessage("Fach und seine Noten wirklich loeschen?")
         .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {     
             	subjectAdapter.delete(mRowId);
             	goBackToOverview();
             }
          })
         .setNegativeButton("Nein", null)
         .show();   	
    }
    
    private void editSubject() {
    	Intent i = new Intent(this, SubjectEdit.class);
		i.putExtra(SubjectDbAdapter.KEY_ROWID, mRowId);
		i.putExtra(SubjectDbAdapter.KEY_ISDIR, 0);
		startActivityForResult(i, ACTIVITY_EDIT);
    }

    private void newGrade() {
    	Intent i = new Intent(this, GradeEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    private void goBackToOverview() {
    	Intent myIntent = new Intent (this, SubjectActivity.class);
    	startActivity (myIntent);
	}
    
    private void goBackToMenu() {
    	Intent myIntent = new Intent (this, Main.class);
    	startActivity (myIntent);
	}
    
    // ---------------------------------------------------------
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_remove);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                gradeAdapter.deleteNote(info.id);
                Log.i("Do", "exec");
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }
    
    // ---------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
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
        
        if (todoAdapter != null) {
        	todoAdapter.close();
        }
    }
}