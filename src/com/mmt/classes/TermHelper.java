package com.mmt.classes;

import android.content.Context;
import android.database.Cursor;

public class TermHelper extends TermDbAdapter{

	public TermHelper(Context ctx) {
		super(ctx);
	}
	
	// Returns the rowId of the activated term
	public int getCurrentTermId() {
		open();
		Cursor cursor = fetchActiveTerm();
		int currentTermId = cursor.getInt(cursor.getColumnIndex(KEY_ROWID));
		cursor.close();
		close();
		return currentTermId;
	}
	
	// Returns the name of the activated term
	public String getCurrentTermTitle() {
		open();
		Cursor cursor = fetchActiveTerm();
		String currentTermId = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
		cursor.close();
		close();
		return currentTermId;
	}
	
}
