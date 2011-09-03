/* Students-App - Android Applikation
 * made by Francois Weber || MMT 09
 * Version: 1.0
 * 
 * _DESCRIPTION: Main class. This class will be called at startup 
 * 
 */

package com.mmt.sap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    // -----------------------------------------
    public void showSubject(View view) {
    	Intent myIntent = new Intent (this, SubjectActivity.class);
    	startActivity (myIntent);
    }
    
    public void showGrades(View view) {
    	Intent myIntent = new Intent (this, GradesActivity.class);
    	startActivity (myIntent);
    }
    
    public void showTodo(View view) {
    	Intent myIntent = new Intent (this, TodoActivity.class);
    	startActivity (myIntent);
    }
    
    public void showDayoff(View view) {
    	Intent myIntent = new Intent (this, DayoffActivity.class);
    	startActivity (myIntent);
    }
    
    public void showTerms(View view) {
    	Intent myIntent = new Intent (this, TermActivity.class);
    	startActivity (myIntent);
    }
    
    public void showSchedule(View view) {
    	Intent myIntent = new Intent (this, Schedule.class);
    	startActivity (myIntent);
    }
    
    // ---------------------------------------------------------
    // MENU - Menu Button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_info:
            gotoInfo();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void gotoInfo() {
   	 	Intent i = new Intent(this, HtmlView.class);
        i.putExtra("htmlSite", R.raw.info);
        startActivity (i);    	
    }    
 }