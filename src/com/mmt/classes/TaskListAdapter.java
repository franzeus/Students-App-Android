package com.mmt.classes;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.mmt.sap.R;

public class TaskListAdapter extends SimpleCursorAdapter {

    private static Context context;

	public TaskListAdapter(Context ctx, Cursor cur, String[] from, int[] to) {
        super(ctx, R.layout.task_row, cur, from, to);
        context = ctx;
    }

    @Override
    public View newView(Context context, Cursor cur, ViewGroup parent) {
    	final LayoutInflater li = LayoutInflater.from(context);
        return li.inflate(R.layout.task_row, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cur) {

    	final int theId = cur.getInt(cur.getColumnIndex(TodoDbAdapter.KEY_ROWID));
    	CharSequence todoTitle = cur.getString(cur.getColumnIndex(TodoDbAdapter.KEY_TITLE));
    	int isChecked = cur.getInt(cur.getColumnIndex(TodoDbAdapter.KEY_ISCHECKED));
    	
    	TextView titleText = (TextView)view.findViewById(R.id.text1);
    	CheckBox checkbox = (CheckBox) view.findViewById(R.id.list_checkbox);
    	// TODO: try to get this refactored!
    	
    	checkbox.setOnClickListener(new OnClickListener() {
    	    public void onClick(View v) {
    	        int status = ((CheckBox) v).isChecked() ? 1 : 0 ;
    	    	   	        
            	TodoDbAdapter.checkTodo(theId, status);
    	    }
    	});
    	
    	if(isChecked == 1) {    		
    		checkbox.setChecked(true);
    	}
        titleText.setText(todoTitle);
    }
}