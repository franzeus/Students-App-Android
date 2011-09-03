package com.mmt.sap;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.mmt.classes.GradeDbAdapter;
import com.mmt.classes.SubjectDbAdapter;
import com.mmt.classes.SubjectListAdapter;
import com.mmt.classes.TermHelper;

public class SubjectActivity extends ListActivity {

	private static final int ACTIVITY_CREATE	= 0;
	private static final int ACTIVITY_EDIT 		= 1;
	private static final int ACTIVITY_DETAIL	= 2;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int EDIT_ID = Menu.FIRST + 2;
	private static final int CANCEL_ID = Menu.FIRST + 3;

	private SubjectDbAdapter subjectAdapter = null;
	private GradeDbAdapter gradeAdapter = null;
	
    private TextView infoText;
    private TextView siteTitle;
    private ImageView headImage;
	
	// ------------------------------------------------
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);
		
		TermHelper th = new TermHelper(this);
		setTitle(R.string.average + " " + th.getCurrentTermTitle());
		
		siteTitle 	= (TextView)findViewById(R.id.subTitle);
        infoText 	= (TextView)findViewById(R.id.info);
        headImage 	= (ImageView)findViewById(R.id.titleImg);
		
		subjectAdapter = new SubjectDbAdapter(this);
		subjectAdapter.open();
		
		// Get Average-Grade of subject
		gradeAdapter = new GradeDbAdapter(this);
        gradeAdapter.open();
        
        siteTitle.setText(R.string.title_subject);
        headImage.setBackgroundResource(R.drawable.folder);
        infoText.setText("Durchschnitt: " + gradeAdapter.getAverageGrade(0,0));
		
		fillData();
		registerForContextMenu(getListView());
	}
	
	// ------------------------------------------------
	private void fillData() {
		Cursor subjectCursor = subjectAdapter.fetchAll();
		startManagingCursor(subjectCursor);

		// Create an array to specify the fields we want to display in the list
		String[] from = new String[] { SubjectDbAdapter.KEY_TITLE };

		// and an array of the fields we want to bind those fields to
		int[] to = new int[] { R.id.text1 };

		// Now create a simple cursor adapter and set it to display
		SubjectListAdapter subjectList = new SubjectListAdapter(this, subjectCursor, from, to);
		setListAdapter(subjectList);		
	}

	private void createSubject() {
		Intent i = new Intent(this, SubjectEdit.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}
	
	// ------------------------------------------------
	// onClick Subject
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, SubjectDetail.class);
		i.putExtra(SubjectDbAdapter.KEY_ROWID, id);
		startActivityForResult(i, ACTIVITY_DETAIL);
	}

    private void gotoHelp() {
   	 	Intent i = new Intent(this, HtmlView.class);
        i.putExtra("htmlSite", R.raw.subject_help);
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
            createSubject();
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
    // onMousePressed list item element > show context menu
     @Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_remove);
		menu.add(0, EDIT_ID, 1, R.string.menu_edit);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case DELETE_ID:
			subjectAdapter.delete(info.id);
			fillData();
			return true;
		 case EDIT_ID:
         	Intent i = new Intent(this, SubjectEdit.class);
     		i.putExtra(SubjectDbAdapter.KEY_ROWID, info.id);
     		i.putExtra(SubjectDbAdapter.KEY_ISDIR, 0);
     		startActivityForResult(i, ACTIVITY_EDIT);
		 case CANCEL_ID:               
             return true;
		}
		return super.onContextItemSelected(item);
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
         
        if (gradeAdapter != null) {
        	gradeAdapter.close();
        }
    }
}
