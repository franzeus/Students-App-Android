package com.mmt.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class SubjectDbAdapter extends DbAdapter {

    	public static final String KEY_ROWID = "_id";
	 	public static final String KEY_TITLE = "title";
	    public static final String KEY_TERMID = "term_id";
	    public static final String KEY_ISDIR = "isDir";

	    private static final String DATABASE_TABLE = "sap_subjects";

	    public SubjectDbAdapter(Context ctx) {
	    	super(ctx);
	        this.mCtx = ctx;
	    }

	    // -------------------------------------------------------
	    // CREATE
	    public long create(String title, int isDir) {
	        ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_TITLE, title);
	        initialValues.put(KEY_TERMID, getActiveTerm());
	        initialValues.put(KEY_ISDIR, isDir);
	        	        
	        return mDb.insert(DATABASE_TABLE, null, initialValues);
	    }

	    // DELETE
	    public boolean delete(long rowId) {
	        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	    }

	    // FETCH ALL SUBJECTS
	    public Cursor fetchAll() {
	        return mDb.query(DATABASE_TABLE, 
	        				 new String[] {KEY_ROWID, KEY_TITLE, KEY_TERMID, KEY_ISDIR },
	        				 "isDir = ? AND term_id = " + getActiveTerm(),
	        				 new String [] { "0" },
	        				 null, null, KEY_TITLE, null);
	    }

	    // FETCH SINGLE SUBJECT 
	    public Cursor fetchSingle(long rowId) throws SQLException {
	        Cursor mCursor =
	            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID, KEY_TITLE, KEY_TERMID}, KEY_ROWID + "=" + rowId, null,
	                    null, null, null, null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;
	    }

	    // UPDATE SUBJECT
	    public boolean update(long rowId, String title) {	    		    	
	        ContentValues args = new ContentValues();
	        args.put(KEY_TITLE, title);
	        args.put(KEY_ROWID, rowId);
	        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	    }

	    // GET SUBJECTS AS WELL AS TODODIRS
		public Cursor fetchAllDirs() {
			 return mDb.query(DATABASE_TABLE,
					 new String[] {KEY_ROWID, KEY_TITLE, KEY_TERMID, KEY_ISDIR },
    				 "term_id = " + getActiveTerm(),
    				 null,
    				 null, null, KEY_TITLE, null);
		}	    
		
	    // Get Active Term
	    private int getActiveTerm() {	    
	        TermHelper termHelper = new TermHelper(this.mCtx);
	        int currentTermId = termHelper.getCurrentTermId();
			return currentTermId;
	    }
}
