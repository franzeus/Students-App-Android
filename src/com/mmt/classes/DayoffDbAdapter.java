package com.mmt.classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.mmt.sap.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;

public class DayoffDbAdapter extends DbAdapter {

    	public static final String KEY_ROWID 		= "_id";
	 	public static final String KEY_REASON 		= "reason";
	 	public static final String KEY_TERMID 		= "term_id";
	 	public static final String KEY_FROM 		= "fromDate";
	 	public static final String KEY_TO 			= "toDate";
	 	public static final String KEY_ISEXCUSED 	= "isExcused";	 	
 	
	    private static final String DATABASE_TABLE = "sap_dayoffs";

	    public DayoffDbAdapter(Context ctx) {
	    	super(ctx);
	        //this.mCtx = ctx;
	    }

	    // -------------------------------------------------------
	    // CREATE
	    public long create(String reason, String fromDate, String toDate, int isExcused) {
	    	
	    	if(validateReason(reason)) {
		        ContentValues initialValues = new ContentValues();
		        initialValues.put(KEY_REASON, reason);
		        initialValues.put(KEY_FROM, fromDate);
		        initialValues.put(KEY_TO, toDate);
		        initialValues.put(KEY_ISEXCUSED, isExcused);
		        initialValues.put(KEY_TERMID, getActiveTerm());
		        return mDb.insert(DATABASE_TABLE, null, initialValues);
	    	} else {
	    		toastMessage(R.string.missingReason);
	    		return 0;
	    	}
	    }

	    // DELETE
	    public boolean delete(long rowId) {
	    		Toast.makeText(this.mCtx, "Fehltag gelöscht", Toast.LENGTH_SHORT).show();
	    		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	    }

	    // FETCH ALL
	    public Cursor fetchAll() {
	        return mDb.query(DATABASE_TABLE, 
	        				 new String[] {KEY_ROWID, KEY_REASON, KEY_TO, KEY_FROM, KEY_ISEXCUSED, KEY_TERMID },
	        				 KEY_TERMID + " = " + getActiveTerm(),
	        				 null,
	        				 null, null, null);
	    }

	    // FETCH SINGLE
	    public Cursor fetchSingle(long rowId) throws SQLException {
	        Cursor mCursor =
	            mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_REASON, KEY_TO, KEY_FROM, KEY_ISEXCUSED, KEY_TERMID }, KEY_ROWID + "=" + rowId, null,
	                    null, null, null, null);
	        if (mCursor != null) {
	            mCursor.moveToFirst();
	        }
	        return mCursor;
	    }

	    // UPDATE
	    public boolean update(long rowId, String reason, String dateFrom, String dateTo, int isExcused) {
	    	if(validateReason(reason)) {
	    		ContentValues args = new ContentValues();
	        	args.put(KEY_REASON, reason);
	        	args.put(KEY_ROWID, rowId);
	        	args.put(KEY_ISEXCUSED, isExcused);
	        	args.put(KEY_FROM, dateFrom);
	        	args.put(KEY_TO, dateTo);
	        	return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	    	} else {
	    		toastMessage(R.string.missingReason);
	    		return false;
	    	}
	    }
	    
	    // -------------------------------------------------------    
	    private boolean validateReason(String param) {
			boolean valid = param.length() > 0 ? true : false;
			return valid;
	    }
	    
	    // Get Active Term
	    private int getActiveTerm() {
	    	TermHelper termHelper = new TermHelper(this.mCtx);
	        int currentTermId = termHelper.getCurrentTermId();
			return currentTermId;
	    }
	    
	    // Get Total Amount of days off
	    public long getTotalDaysOff() {
	    
	    	long total = 0;
	    	
	    	Cursor cur = this.fetchAll();  	
	    	if(cur.getCount() > 0) {
	    		while (cur.moveToNext()) {
	    			CharSequence dateTo = cur.getString(cur.getColumnIndex(KEY_TO));
			       	CharSequence dateFrom = cur.getString(cur.getColumnIndex(KEY_FROM));
			       	total += this.getDateDifference(dateFrom.toString(), dateTo.toString());
	    	    }
	    	}
	    	cur.close();
	    	
	    	return total;	    	
	    }
	    
    	// Calculate difference between to dates in days
	    public long getDateDifference(String dateFrom, String dateTo) {
        	SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd"); //HH:mm:ss
        	Date startDate = null;
        	Date endDate = null;
        	try {
				startDate = outputFormat.parse(dateFrom.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
        	try {
				endDate = outputFormat.parse(dateTo.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			long diffInSec = TimeUnit.MILLISECONDS.toSeconds((endDate.getTime() - startDate.getTime()) / 86400) + 1;
			return diffInSec;
	    }
	    
}
