package com.mmt.classes;

/*
 * Students-App Version 1.0
 * made by Francois Weber || MMT 09 || Fh-Salzburg Austria
 * 
 * _DESCRIPTION: 
 * 		This Class defines the basic CRUD operations
 * 		for the sap_subject table (create, read, update, delete, ...)
 * 		 
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class GradeDbAdapter extends DbAdapter {

	private static final String DATABASE_TABLE 	= "sap_grades";
	
	public static final String KEY_ROWID 		= "_id";
    public static final String KEY_GRADE 		= "grade";
    public static final String KEY_DATE 		= "date";
    public static final String KEY_TYPE 		= "type";
    public static final String KEY_SUBJECTID 	= "subject_id";
    public static final String KEY_TERMID 		= "term_id";
    public static final String KEY_WEIGHT 		= "weight";

    // Dynamic build columns
    public static final String KEY_SUBJECTNAME 	= "subjectName";

    public GradeDbAdapter(Context ctx) {
    	super(ctx);
        this.mCtx = ctx;
    }

    // -------------------------------------------------------
    // Insert new grade
    public long createNote(int grade, String date, String type, long subject_id, int weight) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_GRADE, grade);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_TYPE, type);
        initialValues.put(KEY_WEIGHT, weight);
        initialValues.put(KEY_SUBJECTID, subject_id);	
        initialValues.put(KEY_TERMID, getActiveTerm());
        
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    // -------------------------------------------------------
    // Delete grade by ID
    public boolean deleteNote(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    // -------------------------------------------------------
    // Fetch all grades from database
    public Cursor fetchAllNotes() {
    	return mDb.query(DATABASE_TABLE + " LEFT JOIN sap_subjects ON sap_grades.subject_id = sap_subjects._id", 
        				new String[] { 	"sap_grades." + KEY_ROWID,
    									"sap_grades." + KEY_GRADE,
    									"sap_grades." + KEY_DATE,
    									"sap_grades." +  KEY_TYPE,
    									"sap_grades." +  KEY_WEIGHT,
    									"sap_grades." +  KEY_SUBJECTID,
    									"sap_grades." +  KEY_TERMID,
    									"sap_subjects.title AS subjectName" },
    					"sap_grades.term_id = " + getActiveTerm(), null, null, null,
        				KEY_DATE + " DESC");
    }
    
    // -------------------------------------------------------
    // Fetch all grades of certain subject
    public Cursor fetchSubjectGrades(long rowId) {
    	return mDb.query(DATABASE_TABLE + " LEFT JOIN sap_subjects ON sap_grades.subject_id = sap_subjects._id", 
        				new String[] { 	"sap_grades." + KEY_ROWID,
    									"sap_grades." + KEY_GRADE,
    									"sap_grades." + KEY_DATE,
    									"sap_grades." +  KEY_WEIGHT,
    									"sap_grades." +  KEY_TYPE,
    									"sap_grades." +  KEY_SUBJECTID,
    									"sap_subjects.title AS subjectName" },
        				"sap_grades.subject_id = " + rowId + " AND sap_grades.term_id = " + getActiveTerm(),
        				null,
        				null,
        				null,
        				KEY_DATE + " DESC", null);
    }
    
    // -------------------------------------------------------
    // Fetch certain grade
    public Cursor fetchNote(long rowId) throws SQLException {
        Cursor mCursor = mDb.query(true, DATABASE_TABLE + " LEFT JOIN sap_subjects ON sap_grades.subject_id = sap_subjects._id",
        			new String[] {  "sap_grades." + KEY_ROWID,
        							"sap_grades." + KEY_GRADE,
        							"sap_grades." + KEY_DATE,
        							"sap_grades." + KEY_TYPE,
        							"sap_grades." + KEY_WEIGHT,
        							"sap_grades." + KEY_SUBJECTID,
        							"sap_subjects.title AS subjectName"
        						  },
        			"sap_grades." + KEY_ROWID + "=" + rowId, null,
                    null, null, null, "1");
        
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // -------------------------------------------------------
    // Update
    public boolean updateNote(long rowId, int grade, String date, String type, long subjectId, int weight) {
        ContentValues args = new ContentValues();
        args.put(KEY_GRADE, grade);
        args.put(KEY_DATE, date);
        args.put(KEY_TYPE, type);
        args.put(KEY_WEIGHT, weight);
        args.put(KEY_SUBJECTID, subjectId);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    // Get Active Term
    private int getActiveTerm() {
    	TermHelper termHelper = new TermHelper(this.mCtx);
        int currentTermId = termHelper.getCurrentTermId();
		return currentTermId;
    }
    
    // -------------------------------------------------------
    // Calculate Average Grade
    public String getAverageGrade(long rowID, int termID) throws SQLException {
        
    	int currentTermId = termID;
    	
    	String expandQuery = "";
    	if(rowID > 0) {
    		expandQuery = " AND subject_id = " + rowID;
    	}
    	
    	if(termID > 0) { 
    		expandQuery += " AND term_id = " + termID;
    	} else {
	        currentTermId = getActiveTerm();
    		expandQuery += " AND term_id = " + currentTermId;
    	}
    	
    	// Weight 2
    	Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {"avg( " + KEY_GRADE + ")", KEY_WEIGHT },
            		"weight = 2 " + expandQuery, null,	// WHERE
                    null,	// GROUP BY
                    null,	// HAVING
                    null,	// ORDER BY
                    null);	// LIMIT
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        float averageType1 = mCursor.getFloat(0);
        mCursor.close();
        
        // Weight 1
        Cursor mCursor2 =
            mDb.query(true, DATABASE_TABLE, new String[] {"avg( " + KEY_GRADE + ")", KEY_WEIGHT },
            		"weight = 1" + expandQuery, null, // WHERE
                    null,	// GROUP BY
                    null,	// HAVING
                    null,	// ORDER BY
                    null);	// LIMIT
        if (mCursor2 != null) {
            mCursor2.moveToFirst();
        }
        float averageType2 = mCursor2.getFloat(0);
        mCursor2.close();
                
        // Calculate average grade
        float calculation = 0;
        if(averageType2 == 0) {
        	calculation = averageType1;
        } else if(averageType1 == 0) {
        	calculation = averageType2;
        } else {
        	calculation = ( (averageType1 * 2) + averageType2) / 3;
        }
        
        String average = Float.toString(calculation);
        average = average.substring(0, Math.min( average.length(), 4)); // "round" if necessary
             
        return average;
    }
}
