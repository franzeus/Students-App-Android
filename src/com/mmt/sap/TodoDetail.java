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

import com.mmt.classes.SubjectDbAdapter;
import com.mmt.classes.TaskListAdapter;
import com.mmt.classes.TodoDbAdapter;

public class TodoDetail extends ListActivity {

	private static final int ACTIVITY_CREATE	= 0;
	private static final int ACTIVITY_EDIT 		= 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	
	private TextView txtTitle;
	private TextView txtAverage;
	private Long dirId;
	private Long mRowId;
	
	private TodoDbAdapter todoAdapter;
	private SubjectDbAdapter subjectAdapter;
	private TaskListAdapter mListAdapter;
	
	// ---------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.todo_detail);
        
        subjectAdapter = new SubjectDbAdapter(this);
        subjectAdapter.open();
        
        dirId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(SubjectDbAdapter.KEY_ROWID);
		if (dirId == null) {
			Bundle extras = getIntent().getExtras();
			dirId = extras != null ? extras.getLong(SubjectDbAdapter.KEY_ROWID)
									: null;
		}
        
        if (dirId != null) {
            todoAdapter = new TodoDbAdapter(this);
	        todoAdapter.open();
        	
        	// Fetch Dir Data
            Cursor subjectCursor = subjectAdapter.fetchSingle(dirId);
            startManagingCursor(subjectCursor);
        
            // Set Title
            txtTitle = (TextView) findViewById(R.id.subTitle);
            txtTitle.setText(subjectCursor.getString(subjectCursor.getColumnIndexOrThrow(subjectAdapter.KEY_TITLE)));        	        

	        txtAverage = (TextView) findViewById(R.id.info);
	
	        // List tasks
	        fillData();
        }
        registerForContextMenu(getListView());
    }
        
    // ---------------------------------------------------------
    private void fillData() {
        Cursor cursor = todoAdapter.fetchAllTodos(dirId);
        startManagingCursor(cursor);
        
        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{TodoDbAdapter.KEY_TITLE};

        // and an array of the fields we want to bind those fields to
        int[] to = new int[]{R.id.text1};
        
        mListAdapter = new  TaskListAdapter(this, cursor, from, to);
        setListAdapter(mListAdapter);
        
        txtAverage.setText("Aufgaben: " + cursor.getCount());        
    }
    
    // ---------------------------------------------------------
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
    	TodoDbAdapter.checkTodo(id);
    	//mListAdapter.notifyDataSetChanged();
    	fillData();    	
    	//openOptionsMenu();
    }
        
	// ---------------------------------------------------------
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_todo_detail, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	
     	 // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_insert:
            newTask();
            return true;
        case R.id.menu_edit:
            editTodo();
            return true;
        case R.id.menu_back:
            goBackToMenu();
            return true;
        case R.id.menu_remove:
        	deleteDir();
            return true;
        case R.id.menu_clear:
        	clearDir();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void goBackToOverview() {    	
    	Intent myIntent = new Intent (this, TodoActivity.class);
    	startActivity (myIntent);
	}
    
    private void deleteDir() {
   	 new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle("Ordner loeschen?")
        .setMessage("Wirklich loeschen?")
        .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {     
            	subjectAdapter.delete(dirId);            	
            	goBackToOverview();
            }
         })
        .setNegativeButton("Nein", null)
        .show();   	
   }
    
    private void clearDir() {
    	todoAdapter.removeCheckedTasks(dirId);
    	fillData();
    }

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
                todoAdapter.delete(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void editTodo() {
    	Intent i = new Intent(this, SubjectEdit.class);
		i.putExtra(SubjectDbAdapter.KEY_ROWID, dirId);
		i.putExtra(SubjectDbAdapter.KEY_ISDIR, 1);
		startActivityForResult(i, ACTIVITY_EDIT);
    }

    private void newTask() {
    	Intent i = new Intent(this, TaskCU.class);
    	i.putExtra(SubjectDbAdapter.KEY_ROWID, mRowId);
    	i.putExtra("dirID", dirId);
		startActivityForResult(i, ACTIVITY_CREATE);
    }

    
    private void goBackToMenu() {
    	Intent myIntent = new Intent (this, Main.class);
    	startActivity (myIntent);
	}
   
    
    // ---------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
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
         
        if (todoAdapter != null) {
        	todoAdapter.close();
        }
    }
}
