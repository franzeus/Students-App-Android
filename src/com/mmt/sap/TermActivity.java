package com.mmt.sap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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

import com.mmt.classes.GradeDbAdapter;
import com.mmt.classes.TermDbAdapter;

public class TermActivity extends ListActivity {
	
		private static final int ACTIVITY_CREATE	= 0;
	    private static final int ACTIVITY_EDIT 		= 1;
	    private static final int ACTIVITY_DETAIL	= 2;

	    private static final int INSERT_ID = Menu.FIRST;
	    private static final int DELETE_ID = Menu.FIRST + 1;

	    private TermDbAdapter termAdapter = null;
	    private GradeDbAdapter gradeAdapter = null;

	    private TextView infoText;
	    private TextView siteTitle;
	    private ImageView headImage;
	    
	    // ---------------------------------------------------
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity);
	        setTitle("StudentsApp - Schuljahre");
	        
	        // -- Design -- //
			siteTitle 	= (TextView)findViewById(R.id.subTitle);
	        infoText 	= (TextView)findViewById(R.id.info);
	        headImage 	= (ImageView)findViewById(R.id.titleImg);
	        
	        siteTitle.setText(R.string.title_term);
	        headImage.setBackgroundResource(R.drawable.moleskine);
	        // --- //
	        
	        termAdapter = new TermDbAdapter(this);
	        termAdapter.open();
	        
	        gradeAdapter = new GradeDbAdapter(this);
	        
	        fillData();	        
	        registerForContextMenu(getListView());
	    }

	    // ---------------------------------------------------------
	    private class TaskListAdapter extends SimpleCursorAdapter {

	        public TaskListAdapter(Context context, Cursor cur, String[] from, int[] to) {
	            super(context, R.layout.term_row, cur, from, to);
	        }

	        @Override
	        public View newView(Context context, Cursor cur, ViewGroup parent) {
	            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            return li.inflate(R.layout.term_row, parent, false);
	        }

	        @Override
	        public void bindView(View view, Context context, Cursor cur) {
	            
	        	int theID = cur.getInt(cur.getColumnIndex(termAdapter.KEY_ROWID));
	        	gradeAdapter.open();
	        	String average = gradeAdapter.getAverageGrade(0, theID);
	        	gradeAdapter.close();
	        	
	        	CharSequence subject = cur.getString(cur.getColumnIndex(termAdapter.KEY_TITLE));
	        	TextView subjectText = (TextView)view.findViewById(R.id.text1);
	        	
	        	TextView infoText = (TextView)view.findViewById(R.id.info);
	        	infoText.setText("Durchschnitt: " + average);
	        	
	        	subjectText.setText(subject);
	        	
	        	int isActive = cur.getInt(cur.getColumnIndex(termAdapter.KEY_ISACTIVE));
	        	ImageView bgImage = (ImageView)view.findViewById(R.id.folder);
	        	bgImage.setImageResource(R.drawable.subject);
	        	if(isActive == 0) {
	        		subjectText.setTextColor(getResources().getColor(R.color.inactive));
	        		infoText.setTextColor(getResources().getColor(R.color.inactive));
	        		bgImage.setAlpha(100);
	        	} else {
	        		subjectText.setTextColor(getResources().getColor(R.color.darkgrey));
	        		infoText.setTextColor(getResources().getColor(R.color.darkgrey));
	        		bgImage.setAlpha(255);
	        	}
	        }
	    }
	    
	    // ---------------------------------------------------------
	    private void fillData() {
	        Cursor notesCursor = termAdapter.fetchAll();
	        startManagingCursor(notesCursor);

	        Integer amount = notesCursor.getCount();
        	infoText.setText("Anzahl: " + amount.toString());
	        
	        // Create an array to specify the fields we want to display in the list
	        String[] from = new String[]{termAdapter.KEY_TITLE};

	        // and an array of the fields we want to bind those fields to
	        int[] to = new int[]{R.id.text1};
	        
	        TaskListAdapter mListAdapter = new TaskListAdapter(this, notesCursor, from, to);
	        setListAdapter(mListAdapter);
	    }
	    
	    // New
	    private void createTerm() {
	        Intent i = new Intent(this, TermCU.class);
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
	            createTerm();
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
	        i.putExtra("htmlSite", R.raw.term_help);
	        startActivity (i);			
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
	                termAdapter.delete(info.id);
	                fillData();
	                return true;
	        }
	        return super.onContextItemSelected(item);
	    }
	    
	    // ---------------------------------------------------------
	    // OnClick on a Term
	    @Override
	    protected void onListItemClick(ListView l, View v, int position, long id) {
	        super.onListItemClick(l, v, position, id);
	        Intent i = new Intent(this, TermCU.class);
	        i.putExtra(termAdapter.KEY_ROWID, id);
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
	             
	        if (termAdapter != null) {
	        	termAdapter.close();
	        }
	    }
}
