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

import com.mmt.classes.GradeListAdapter;
import com.mmt.classes.GradeDbAdapter;
import com.mmt.classes.SubjectDbAdapter;
import com.mmt.classes.TermHelper;
public class GradesActivity extends ListActivity {
    private static final int ACTIVITY_CREATE	= 0;
    private static final int ACTIVITY_EDIT 		= 1;

    private static final int DELETE_ID = Menu.FIRST;
    private static final int CANCEL_ID = Menu.FIRST + 1;

    private GradeDbAdapter gradeAdapter = null;
    private SubjectDbAdapter subjectAdapter = null;
    
    private TextView infoText;
    private TextView siteTitle;
    private ImageView headImage;

    // ---------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        
        gradeAdapter = new GradeDbAdapter(this);
        gradeAdapter.open();        
        
        // -- Design -- //
		siteTitle 	= (TextView)findViewById(R.id.subTitle);
        infoText 	= (TextView)findViewById(R.id.info);
        headImage 	= (ImageView)findViewById(R.id.titleImg);
        
        siteTitle.setText(R.string.title_grade);
        headImage.setBackgroundResource(R.drawable.grade);
        
        // Get Grades
        fillData();        
        registerForContextMenu(getListView());
    }

    // ---------------------------------------------------------
    private void fillData() {
        Cursor notesCursor = gradeAdapter.fetchAllNotes();
        startManagingCursor(notesCursor);
        
        TermHelper th = new TermHelper(this);
		setTitle(th.getCurrentTermTitle() + " - Noten");
        
        // Get Average
        infoText.setText("Durchschnitt: " + gradeAdapter.getAverageGrade(0,0) + "   Anzahl: " + notesCursor.getCount());
        
        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{GradeDbAdapter.KEY_DATE, GradeDbAdapter.KEY_SUBJECTNAME, GradeDbAdapter.KEY_TYPE, GradeDbAdapter.KEY_GRADE};

        // and an array of the fields we want to bind those fields to
        int[] to = new int[]{R.id.text1, R.id.text2,  R.id.text3, R.id.text4};
        
        GradeListAdapter mListAdapter = new GradeListAdapter(this, notesCursor, from, to);//new MyAdapter(this, notesCursor, from, to);
        setListAdapter(mListAdapter);
    }
    
    private void createNote() {    	
    	// First check if there are any subjects
    	subjectAdapter = new SubjectDbAdapter(this);
    	subjectAdapter.open();
    	Cursor subjectCursor = subjectAdapter.fetchAll();
    	if(subjectCursor.getCount() == 0) {
    		subjectAdapter.toastMessage(R.string.noSubjects);
    	// Go to create grade activity
    	} else {
        	Intent i = new Intent(this, GradeEdit.class);
        	startActivityForResult(i, ACTIVITY_CREATE);
    	}
    	subjectCursor.close();
    	subjectAdapter.close();
    }
    
    private void gotoHelp() {
    	 Intent i = new Intent(this, HtmlView.class);
         i.putExtra("htmlSite", R.raw.grade_help);
         startActivity (i);
    }
    
    // ---------------------------------------------------------
    // MENU - Menu Button
	@Override
	// Load Menu by xml resource
    public boolean onCreateOptionsMenu(Menu menu) {
		 MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.menu, menu);
	     return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {    	
    	// Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_insert:
            createNote();
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
    // onMousePressed - list item element
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_remove);
        menu.add(0, CANCEL_ID, 1, R.string.menu_cancel);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();    	
        switch(item.getItemId()) {
            case DELETE_ID:
                gradeAdapter.deleteNote(info.id);
                fillData();
                return true;
            case CANCEL_ID:                
                return true;
        }
        return super.onContextItemSelected(item);
    }

    // ---------------------------------------------------------
    // onClick Item list element
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, GradeEdit.class);
        i.putExtra(GradeDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
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
         
        if (gradeAdapter != null) {
        	gradeAdapter.close();
        }
    }
}
