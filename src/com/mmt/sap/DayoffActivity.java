package com.mmt.sap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

import com.mmt.classes.DayoffDbAdapter;
import com.mmt.classes.GradeDbAdapter;
import com.mmt.classes.Helper;
import com.mmt.classes.TermDbAdapter;

public class DayoffActivity extends ListActivity {
	
		private static final int ACTIVITY_CREATE	= 0;
	    private static final int ACTIVITY_EDIT 		= 1;
	    private static final int ACTIVITY_DETAIL	= 2;

	    private static final int DELETE_ID = Menu.FIRST;

	    private long totalDays = 0;
	    
	    private TermDbAdapter termAdapter = null;
	    private DayoffDbAdapter dayoffAdapter = null;
   
	    private TextView infoText;
	    private TextView siteTitle;
	    private ImageView headImage;
	    
	    // ---------------------------------------------------
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity);
	        
	        dayoffAdapter = new DayoffDbAdapter(this);
	        dayoffAdapter.open();

	        // ---- DESIGN ---
	        siteTitle 	= (TextView)findViewById(R.id.subTitle);
	        infoText 	= (TextView)findViewById(R.id.info);
	        headImage 	= (ImageView)findViewById(R.id.titleImg);
	        
	        siteTitle.setText(R.string.title_dayoff);
	        headImage.setBackgroundResource(R.drawable.dayoff);
	        	       
	        fillData();
	        registerForContextMenu(getListView());
	    }

	    // ---------------------------------------------------------
	    private class DayoffListAdapter extends SimpleCursorAdapter {

	        public DayoffListAdapter(Context context, Cursor cur, String[] from, int[] to) {
	            super(context, R.layout.dayoff_row, cur, from, to);
	        }

	        @Override
	        public View newView(Context context, Cursor cur, ViewGroup parent) {
	            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            return li.inflate(R.layout.dayoff_row, parent, false);
	        }

	        @Override
	        public void bindView(View view, Context context, Cursor cur) {
	            
	        	int theID 			= cur.getInt(cur.getColumnIndex(dayoffAdapter.KEY_ROWID));
	        	int isExcused 		= cur.getInt(cur.getColumnIndex(dayoffAdapter.KEY_ISEXCUSED));        	
	        	CharSequence reason = cur.getString(cur.getColumnIndex(dayoffAdapter.KEY_REASON));
	        	CharSequence dateTo = cur.getString(cur.getColumnIndex(dayoffAdapter.KEY_TO));
	        	CharSequence dateFrom = cur.getString(cur.getColumnIndex(dayoffAdapter.KEY_FROM));
	        		        	
	        	TextView reasonText = (TextView)view.findViewById(R.id.reason);
	        	TextView dateText = (TextView)view.findViewById(R.id.dateRange);	        	
	        	
	        	ImageView bgImage = (ImageView)view.findViewById(R.id.status);
	        	if(isExcused == 1) {
	        		bgImage.setBackgroundResource(R.drawable.checkmark);
	        	} else {
	        		bgImage.setBackgroundResource(R.drawable.no);  
	        	}
	        	
				long dateDifference = dayoffAdapter.getDateDifference(dateFrom.toString(), dateTo.toString());
				Helper helper = new Helper();
				reasonText.setText(reason);
	            dateText.setText(helper.formatDate((String) dateFrom) + " - " + helper.formatDate((String) dateTo) + " (" + dateDifference + ")");
	        }
	    }
	    
	    // ---------------------------------------------------------
	    private void fillData() {
	        Cursor cursor = dayoffAdapter.fetchAll();
	        startManagingCursor(cursor);
	        
	        // Create an array to specify the fields we want to display in the list
	        String[] from = new String[]{dayoffAdapter.KEY_REASON};

	        // and an array of the fields we want to bind those fields to
	        int[] to = new int[]{R.id.reason};
	        
	        DayoffListAdapter mListAdapter = new DayoffListAdapter(this, cursor, from, to);
	        setListAdapter(mListAdapter);
			infoText.setText("Fehltage: " + dayoffAdapter.getTotalDaysOff());
	    }
	    
	    private void create() {
	        Intent i = new Intent(this, DayoffCU.class);
	        startActivityForResult(i, ACTIVITY_CREATE);
	    }	    
   
	    private void gotoHelp() {
	    	Intent i = new Intent(this, HtmlView.class);
	        i.putExtra("htmlSite", R.raw.dayoff_help);
	        startActivity (i);			
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
	            create();
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
	                dayoffAdapter.delete(info.id);
	                fillData();
	                return true;
	        }
	        return super.onContextItemSelected(item);
	    }
	    
	    // ---------------------------------------------------------
	    // OnClick on a list element
	    @Override
	    protected void onListItemClick(ListView l, View v, int position, long id) {
	        super.onListItemClick(l, v, position, id);
	        Intent i = new Intent(this, DayoffCU.class);
	        i.putExtra(dayoffAdapter.KEY_ROWID, id);
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
	        
	        if (dayoffAdapter != null) {
	        	dayoffAdapter.close();
	        }
	    }
}
