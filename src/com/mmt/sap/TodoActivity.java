package com.mmt.sap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.mmt.classes.SubjectDbAdapter;

public class TodoActivity extends ListActivity {
	
		private static final int ACTIVITY_CREATE	= 0;
	    private static final int ACTIVITY_EDIT 	= 1;
	    private static final int ACTIVITY_DETAIL	= 2;

	    private static final int INSERT_ID = Menu.FIRST;
	    private static final int DELETE_ID = Menu.FIRST + 1;
	    private static final int EDIT_ID = Menu.FIRST + 2;

	    private SubjectDbAdapter subjectAdapter;
	    	    
	    private TextView infoText;
	    private TextView siteTitle;
	    private ImageView headImage;

	    // ---------------------------------------------------
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity);
	        
	        subjectAdapter = new SubjectDbAdapter(this);
	        subjectAdapter.open();
	        
	        // ---- DESIGN ---
	        siteTitle 	= (TextView)findViewById(R.id.subTitle);
	        infoText 	= (TextView)findViewById(R.id.info);
	        headImage 	= (ImageView)findViewById(R.id.titleImg);
	        
	        siteTitle.setText(R.string.title_todo);
	        headImage.setBackgroundResource(R.drawable.list);
	        
	        // Get TodoDirs
	        fillData();
	        
	        registerForContextMenu(getListView());
	    }

	    // ---------------------------------------------------------
	    private class MyAdapter extends SimpleCursorAdapter {

	        public MyAdapter(Context context, Cursor cur, String[] from, int[] to) {
	            super(context, R.layout.todo_row, cur, from, to);
	        }

	        @Override
	        public View newView(Context context, Cursor cur, ViewGroup parent) {
	            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            return li.inflate(R.layout.todo_row, parent, false);
	        }

	        @Override
	        public void bindView(View view, Context context, Cursor cur) {
	            
	        	int theId = cur.getInt(cur.getColumnIndex(SubjectDbAdapter.KEY_ROWID));
	        	CharSequence subject = cur.getString(cur.getColumnIndex(SubjectDbAdapter.KEY_TITLE));
	        	int isDir = cur.getInt(cur.getColumnIndex(SubjectDbAdapter.KEY_ISDIR));
	        	
	        	TextView subjectText = (TextView)view.findViewById(R.id.text1);
	        	TextView numText = (TextView)view.findViewById(R.id.info); 	        	
	        	ImageView bgImage = (ImageView)view.findViewById(R.id.folder);
	        	
	        	int numberOfTodos = subjectAdapter.getNumber("sap_todos", "dir_id", theId);
	        	
	        	if(isDir == 1) {
	        		if(numberOfTodos > 0) {
	        			bgImage.setBackgroundResource(R.drawable.folder_green_stuffed);
	        		} else {
	        			bgImage.setBackgroundResource(R.drawable.folder_green);
	        		}
	        	} else {
	        		if(numberOfTodos > 0) {
	        			bgImage.setBackgroundResource(R.drawable.folder_blue_stuffed);
	        		} else {
	        			bgImage.setBackgroundResource(R.drawable.folder);
	        		}
	        	}
	        	
	        	numText.setText("Aufgaben: " + numberOfTodos);
	            subjectText.setText(subject);
	        }
	    }
	    
	    // ---------------------------------------------------------
	    private void fillData() {
	        Cursor notesCursor = subjectAdapter.fetchAllDirs();
	        startManagingCursor(notesCursor);
	        
	        // Get Average-Grade of term
	        infoText.setText("Ordner: " + notesCursor.getCount());
	        
	        
	        // Create an array to specify the fields we want to display in the list
	        String[] from = new String[]{SubjectDbAdapter.KEY_TITLE};

	        // and an array of the fields we want to bind those fields to
	        int[] to = new int[]{R.id.text1};
	        
	        MyAdapter mListAdapter = new MyAdapter(this, notesCursor, from, to);
	        setListAdapter(mListAdapter);
	    }
	    
	    // New Directory
	    private void createDir() {
	    	Intent i = new Intent(this, SubjectEdit.class);	    	
	 	    i.putExtra(SubjectDbAdapter.KEY_ISDIR, 1);
	 	    startActivityForResult(i, ACTIVITY_CREATE);
	    }
	    
   
	    
	    // ---------------------------------------------------------
	    // MENU - Menu Button
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.menu, menu);
	        return true;
	    }
	    
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle item selection
	        switch (item.getItemId()) {
	        case R.id.menu_insert:
	            createDir();
	            return true;
	        case R.id.menu_help:
	            gotoHelp();
	            return true;
	        case R.id.menu_back:
	            finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        }
	    }

	    private void gotoHelp() {
	    	Intent i = new Intent(this, HtmlView.class);
	        i.putExtra("htmlSite", R.raw.todo_help);
	        startActivity (i);			
		}
	    
	    // ---------------------------------------------------------
		@Override
	    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	        super.onCreateContextMenu(menu, v, menuInfo);
	        menu.add(0, DELETE_ID, 0, R.string.menu_remove);
	        menu.add(0, EDIT_ID, 1, R.string.menu_edit);
	    }
	    
	    @Override
	    public boolean onContextItemSelected(MenuItem item) {
	    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	        switch(item.getItemId()) {
	            case DELETE_ID:	                
	                subjectAdapter.delete(info.id);
	                fillData();
	                return true;
	            case EDIT_ID:
	            	Intent i = new Intent(this, SubjectEdit.class);
	        		i.putExtra(SubjectDbAdapter.KEY_ROWID, info.id);
	        		i.putExtra(SubjectDbAdapter.KEY_ISDIR, 1);
	        		startActivityForResult(i, ACTIVITY_EDIT);
	        }
	        return super.onContextItemSelected(item);
	    }
	    
	    // ---------------------------------------------------------
	    // OnClick on a Dir
	    @Override
	    protected void onListItemClick(ListView l, View v, int position, long id) {
	        super.onListItemClick(l, v, position, id);
	        Intent i = new Intent(this, TodoDetail.class);
	        i.putExtra(SubjectDbAdapter.KEY_ROWID, id);
	        startActivityForResult(i, ACTIVITY_DETAIL);
	    }

	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	        super.onActivityResult(requestCode, resultCode, intent);
	        fillData();
	    }
	    
	    // ---------------------------------------------------------
	    // Close DatabaseHelper
	    @Override    
	    protected void onDestroy() {        
	        super.onDestroy();
	             
	        if (subjectAdapter != null) {
	        	subjectAdapter.close();
	        }
	    }
}
