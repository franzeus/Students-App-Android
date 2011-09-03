package com.mmt.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;

public class TermDbAdapter extends DbAdapter {

    	public static final String KEY_ROWID = "_id";
	 	public static final String KEY_TITLE = "title";
	 	public static final String KEY_ISACTIVE = "isActive";

	    private static final String DATABASE_TABLE = "sap_terms";

	    public TermDbAdapter(Context ctx) {
	    	super(ctx);
	        this.mCtx = ctx;
	    }

	    // -------------------------------------------------------
	    // CREATE
	    public long create(String title, int isActive) {	    	
	    	// Set all terms to inactive (0)
	    	if(isActive > 0) {
	    		ContentValues argsAll = new ContentValues();
		        argsAll.put(KEY_ISACTIVE, 0);
	    		mDb.update(DATABASE_TABLE, argsAll, null, null);	    		
	    	}
	    	if(validateTitle(title)) {
		        ContentValues initialValues = new ContentValues();
		        initialValues.put(KEY_TITLE, title);
		        initialValues.put(KEY_ISACTIVE, isActive);
		        return mDb.insert(DATABASE_TABLE, null, initialValues);
	    	} else {
	    		return 0;
	    	}
	    }

	    // DELETE
	    public boolean delete(long rowId) {	    	

	    	if(this.countTerms() > 1) { // at least one term must always exist
	        	mDb.beginTransaction();
	        	try {	        		
	        		boolean result = mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	        		mDb.setTransactionSuccessful();
	        		return result;
	        	} finally {
	        		mDb.endTransaction();
	        	}	        		
	    	} else {
	    		Toast.makeText(this.mCtx, "Du musst mindestens immer 1 Schuljahr haben", Toast.LENGTH_SHORT).show();
	    		return false;
	    	}
	    }

	    // FETCH ALL
	    public Cursor fetchAll() {
	        return mDb.query(DATABASE_TABLE, 
	        				 new String[] {KEY_ROWID, KEY_TITLE, KEY_ISACTIVE },
	        				 null,
	        				 null,
	        				 null, null, null);
	    }

	    // FETCH SINGLE
	    public Cursor fetchSingle(long rowId) throws SQLException {
	        Cursor mCursor =
	            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE, KEY_ISACTIVE }, KEY_ROWID + "=" + rowId, null,
	                    null, null, null, null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;
	    }

	    // UPDATE
	    public boolean update(long rowId, String title, int isActive) {
	    	
	    	// Set all terms to inactive (0)
	    	if(isActive > 0) {
	    		ContentValues argsAll = new ContentValues();
		        argsAll.put(KEY_ISACTIVE, 0);
	    		mDb.update(DATABASE_TABLE, argsAll, null, null);	    		
	    	}
	    	
	    	if(validateTitle(title)) {
	    		ContentValues args = new ContentValues();
	        	args.put(KEY_TITLE, title);
	        	args.put(KEY_ROWID, rowId);
	        	args.put(KEY_ISACTIVE, isActive);
	        	return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	    	} else {
	    		return false;
	    	}
	    }

	    // FETCH ACTIVE TERM
	    public Cursor fetchActiveTerm() throws SQLException {
	        Cursor mCursor =
	            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE, KEY_ISACTIVE }, KEY_ISACTIVE + "= 1", null,
	                    null, null, null, null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;
	    }
	    
	    private boolean validateTitle(String title) {
			boolean valid = title.length() > 0 ? true : false;
			return valid;	    	
	    }
	    
	    public int countTerms() {
	    	Cursor c = this.fetchAll();
	    	int count = c.getCount();
	    	c.close();
	    	
	    	return count;	    	
	    }
	    
}
