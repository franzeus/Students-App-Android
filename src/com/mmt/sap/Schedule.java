package com.mmt.sap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Schedule extends ListActivity {
	
		private static final int ACTIVITY_CREATE	= 0;
	    private static final int ACTIVITY_EDIT 		= 1;
	    private static final int ACTIVITY_DETAIL	= 2;

	    private static final int INSERT_ID = Menu.FIRST;
	    private static final int DELETE_ID = Menu.FIRST + 1;
	    
	    private TextView infoText;
	    private TextView siteTitle;
	    private ImageView headImage;
	    
	    // ---------------------------------------------------
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity);
	        setTitle(R.string.title_schedule);
	        
	        siteTitle 	= (TextView)findViewById(R.id.subTitle);
	        infoText 	= (TextView)findViewById(R.id.info);
	        headImage 	= (ImageView)findViewById(R.id.titleImg);
	        
	        siteTitle.setText(R.string.title_schedule);
	        headImage.setBackgroundResource(R.drawable.schedule);
	        infoText.setText("Mo - Fr");
	        	        
	        fillData();
	        //registerForContextMenu(getListView());
	        setContentBasedOnLayout();
	    }
	    
	    // ---------------------------------------------------------
	    private void fillData() {
			ListView lv = getListView();
			lv.setTextFilterEnabled(true);
			
			String[] days = getResources().getStringArray(R.array.schedule_array);
			setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, days));
			
			lv.setOnItemClickListener(new OnItemClickListener() {
			    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {		     
			      showDetails(id);			      
			    }
			});
	    }
	    
	    // ---------------------------------------------------------
	    private void gotoHelp() {
	    	Intent i = new Intent(this, HtmlView.class);
	        i.putExtra("htmlSite", R.raw.todo_help);
	        startActivity (i);	
		}
	    
	    private void showDetails(long id) {
	    	Intent i = new Intent(this, ScheduleDetail.class);
		    i.putExtra("dayId", id);
		    startActivityForResult(i, ACTIVITY_DETAIL);	    	
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

	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	        super.onActivityResult(requestCode, resultCode, intent);
	        fillData();
	    }   
	    
	    private void setContentBasedOnLayout()
	    {
	        WindowManager winMan = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
	        
	        if (winMan != null)
	        {
	            int orientation = winMan.getDefaultDisplay().getOrientation();
	            
	            if (orientation == 0) {
	                // Portrait
	                setContentView(R.layout.activity);
	                Log.i("View", "Portrait");
	            }
	            else if (orientation == 1) {
	                // Landscape
	                setContentView(R.layout.activity);
	                Log.i("View", "Landscape");
	            }            
	        }
	    }
}
