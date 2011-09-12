package com.mmt.sap;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mmt.classes.ScheduleDbAdapter;

public class ScheduleDetail extends Activity {

	private static final int ACTIVITY_CREATE	= 0;
	private static final int ACTIVITY_EDIT 		= 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int ACTIVITY_DETAIL = 3;
	
	private Long dayId;
	
    private TextView infoText;
    private TextView siteTitle;
    private ImageView headImage;
	
	private ScheduleDbAdapter scheduleAdapter;
	
	ScrollView sv;
	LinearLayout ll;
	
	// ---------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        
        dayId = (savedInstanceState == null) ? null : (Long) savedInstanceState.getSerializable("dayId");
		if (dayId == null) {
			Bundle extras = getIntent().getExtras();
			dayId = extras != null ? extras.getLong("dayId") : null;
		}
        
        scheduleAdapter = new ScheduleDbAdapter(this);
        scheduleAdapter.open();
		        
        String dayName = getResources().getStringArray(R.array.schedule_array)[dayId.intValue()];        
        
        siteTitle 	= (TextView)findViewById(R.id.subTitle);
        infoText 	= (TextView)findViewById(R.id.info);
        headImage 	= (ImageView)findViewById(R.id.titleImg);
        
        siteTitle.setText(dayName);
        headImage.setBackgroundResource(R.drawable.schedule);

        Cursor dataCursor = scheduleAdapter.fetchSubjectsOfDay(dayId);
        Log.i("Schedule", "Count: " + dataCursor.getCount());
        Map<Integer, String> map = new HashMap<Integer, String>();
        while(dataCursor.moveToNext()) {
        	int g = dataCursor.getInt(dataCursor.getColumnIndex("row_id"));
        	String d = dataCursor.getString(dataCursor.getColumnIndex("subjectName"));
        	map.put(dataCursor.getInt(dataCursor.getColumnIndex("row_id")), dataCursor.getString(dataCursor.getColumnIndex("subjectName")));
        	Log.i("Schedule", "R: " + g + " N: " + d);
        }
        dataCursor.close();

        sv = new ScrollView(this);
        ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);
        
        LinearLayout rowL = null;
        TextView subjectTitle = null;
        TextView row = null;
        
        for(int k = 1; k < 11; k++) {
            rowL = new LinearLayout(this);
            rowL.setOrientation(LinearLayout.HORIZONTAL);
            rowL.setMinimumHeight(40);
            
        	 	row = new TextView(this);
        	 	row.setText(k + ".");
        	 	rowL.addView(row);
        	 	
        	 	subjectTitle = new TextView(this);        	 	
        	 	if(map.containsKey(k))
        	 		subjectTitle.setText(map.get(k).toString());        	 	
        	 	else
        	 		subjectTitle.setText("-");        	 	
        	 	rowL.addView(subjectTitle);
        	 rowL.setTag(k);       	 
        	 rowL.setOnClickListener(mCorkyListener);
        	 ll.addView(rowL);
        }       
        this.setContentView(sv);

        /*     
        if (dayId != null) {                       
            int pos = new Integer(dayId.toString());            

            // Set Title
            siteTitle.setText(getResources().getStringArray(R.array.schedule_array)[pos]);
	        headImage.setBackgroundResource(R.drawable.schedule);
	        infoText.setText("Deine Fächer");
            
	        fillData();
        }
        */
    }
    
    private OnClickListener mCorkyListener = new OnClickListener() {
        public void onClick(View v) {
        	Object id = v.getTag();
        	Toast.makeText(getApplicationContext(), "Toast" + id, Toast.LENGTH_SHORT).show();
        	Integer mp = (Integer)v.getTag();
        	gotoEdit(mp);        	
        }
    };
    
    private void gotoEdit(int rowId) {   
    	Log.i("gotoEdit","id:" + rowId);
    	Intent i = new Intent(this, ScheduleCU.class);
	    i.putExtra("dayId", dayId);
	    i.putExtra("rowId", rowId);
	    startActivityForResult(i, ACTIVITY_CREATE);    	
    }
    
    
    // ---------------------------------------------------------
    /*
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
        	goBackToMenu();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void editSubject() {
    	Intent i = new Intent(this, SubjectEdit.class);
		i.putExtra(SubjectDbAdapter.KEY_ROWID, dayId);
		i.putExtra(SubjectDbAdapter.KEY_ISDIR, 0);
		startActivityForResult(i, ACTIVITY_EDIT);
    }

    private void newGrade() {
    	Intent i = new Intent(this, GradeEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
    }

    
    private void goBackToMenu() {
    	Intent myIntent = new Intent (this, Main.class);
    	startActivity (myIntent);
	}
    */
    // ---------------------------------------------------------
    /*
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
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }
    */
    // ---------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		//fillData();
	}
	  
    // Close DatabaseHelper
    @Override    
    protected void onDestroy() {        
        super.onDestroy();
             
        if (scheduleAdapter != null) {
        	scheduleAdapter.close();
        }      
    }
  
}