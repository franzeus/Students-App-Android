package com.mmt.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class ScheduleDbAdapter extends DbAdapter {
		
		private static final String DATABASE_TABLE = "sap_schedule";
		/*
    	public static final String KEY_ROWID 	= DATABASE_TABLE + "._id";
    	public static final String KEY_DAY		= DATABASE_TABLE + ".day_id";
	 	public static final String KEY_SUBJECTID= DATABASE_TABLE + ".subject_id";
	    public static final String KEY_PLACEID 	= DATABASE_TABLE + ".row_id";
	    public static final String KEY_TERMID 	= DATABASE_TABLE + ".term_id";
	    */
		public static final String KEY_ROWID 	= "_id";
    	public static final String KEY_DAY		= "day_id";
	 	public static final String KEY_SUBJECTID= "subject_id";
	    public static final String KEY_PLACEID 	= "row_id";
	    public static final String KEY_TERMID 	= "term_id";
		
	    // Dynamic columns
	    public static final String KEY_SUBJECTNAME = "subjectName";
	    
	    public ScheduleDbAdapter(Context ctx) {
	    	super(ctx);
	        this.mCtx = ctx;
	    }

    
	    // -------------------------------------------------------
	    // CREATE
	    public void create(long subjectId, long dayId, long posId) throws SQLException {
	    	int termId = getActiveTerm();
	    	
	    	this.open();
	    	if(!isPlaceEmpty(dayId, posId, termId)) {
	    		delete(dayId, posId, termId);
	    	}

	        ContentValues initialValues = new ContentValues();
	        initialValues.put(KEY_SUBJECTID, subjectId);
	        initialValues.put(KEY_PLACEID, posId);
	        initialValues.put(KEY_DAY, dayId);
	        initialValues.put(KEY_TERMID, termId);
	        
	        if(mDb.isOpen())
	        	mDb.insert(DATABASE_TABLE, null, initialValues);
	        this.close();
	    }

	    // DELETE where dayID = ? and placeId = ? and term_id = ?
	    public boolean delete(long dayId, long posId, int termId) {
	        return mDb.delete(DATABASE_TABLE, KEY_PLACEID + "=" + posId + " AND " + KEY_DAY + "=" + dayId + " AND " + KEY_TERMID + "=" + termId, null) > 0;
	    }

	    // FETCH SUBJECT OF DAY 
	    public Cursor fetchSubjectsOfDay(long dayId) throws SQLException {
	        Cursor mCursor =
	            mDb.query(true, DATABASE_TABLE + " LEFT JOIN sap_subjects ON sap_subjects._id = sap_schedule.subject_id",
	            		new String[] {DATABASE_TABLE + "." + KEY_ROWID, DATABASE_TABLE + "." + KEY_SUBJECTID, DATABASE_TABLE + "." + KEY_PLACEID, DATABASE_TABLE + "." + KEY_TERMID, "sap_subjects.title AS " + KEY_SUBJECTNAME},
	            		DATABASE_TABLE + "." + KEY_DAY + "=" + dayId,
	            		null,
	                    null, null, DATABASE_TABLE + "." + KEY_PLACEID, null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;
	    }
	    	    
	    private boolean isPlaceEmpty(long dayId, long rowId, int termId) {
	    	Cursor mCursor = mDb.query(true, DATABASE_TABLE, 
	    							new String[] {KEY_PLACEID, KEY_DAY, KEY_TERMID}, 
	    							KEY_PLACEID + "=" + rowId + " AND " + KEY_DAY + "=" + dayId + " AND " + KEY_TERMID + "=" + termId, 
	    							null, null, null, null, null);
	    	
	    	return mCursor == null ? true : false;	    	
	    }
	
	    // Get Active Term
	    private int getActiveTerm() {
	        TermHelper termHelper = new TermHelper(this.mCtx);
	        int currentTermId = termHelper.getCurrentTermId();
			return currentTermId;
	    }
	    
}
