package com.mmt.classes;

/*
 * Students-App Version 1.0
 * made by Francois Weber || MMT 09 || Fh-Salzburg Austria
 * 
 * _DESCRIPTION: 
 * 		This Class defines the basic CRUD operations
 * 		for the sap_grades table (query, create, delete, update)
 * 		 
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mmt.sap.R;

public class TodoDbAdapter {

	private static final String DATABASE_TABLE = "sap_todos";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DIRID = "dir_id";
    public static final String KEY_TERMID = "term_id";
    public static final String KEY_DIRNAME = "dirName";
    public static final String KEY_NUMBER = "num";
    public static final String KEY_ISCHECKED = "isChecked";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DEADLINE = "deadline";
        
    private DBManager mDbHelper;
    private static SQLiteDatabase mDb;
    private final Context mCtx;

    public TodoDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public TodoDbAdapter open() throws SQLException {
    	mDbHelper = new DBManager(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    // -------------------------------------------------------
    // Insert new todo
    public long createTodo(long dir_id, String title,  String deadline, int checkedVal) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_DIRID, dir_id);
        initialValues.put(KEY_ISCHECKED, checkedVal);
        initialValues.put(KEY_DEADLINE, deadline);
        initialValues.put(KEY_TERMID, getActiveTerm());
        
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    // -------------------------------------------------------
    // Delete by ID
    public boolean delete(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    // -------------------------------------------------------
    // Fetch all tasks of dir from database
    public Cursor fetchAllTodos(long dirID) {
    	return mDb.query(DATABASE_TABLE + " LEFT JOIN sap_subjects ON sap_todos.dir_id = sap_subjects._id", 
        				new String[] { 	"sap_todos." + KEY_ROWID,
    									"sap_todos." + KEY_TITLE,
    									"sap_todos." +  KEY_TERMID,
    									"sap_todos." +  KEY_DIRID,
    									"sap_todos." +  KEY_ISCHECKED,
    									"sap_todos." +  KEY_DEADLINE,
    									"sap_subjects.title AS subjectName" },
        				KEY_DIRID + " = " + dirID , // WHERE
        				null, // WHERE PARAM
        				null, // GROUP BY
        				null, // HAVING
        				"sap_todos." + KEY_DEADLINE + " ASC"); // ORDER
    }
      
    // -------------------------------------------------------
    // Fetch certain
    public Cursor fetchSingle(long rowId) throws SQLException {

        Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_TITLE, KEY_TERMID, KEY_DIRID, KEY_DEADLINE}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    // -------------------------------------------------------
    // Update
    public boolean updateTodo(long rowId, String title, String deadline, int checkedVal) {
        ContentValues args = new ContentValues();
        args.put(KEY_TITLE, title);
        args.put(KEY_DEADLINE, deadline);
        args.put(KEY_ISCHECKED, checkedVal);
        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    // Update
    public static void checkTodo(long rowId, int check) {
        ContentValues args = new ContentValues();
        args.put(KEY_ISCHECKED, check);
        mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null);
    }   
    
    // Get Active Term
    private int getActiveTerm() {
    	TermHelper termHelper = new TermHelper(this.mCtx);
        int currentTermId = termHelper.getCurrentTermId();
		return currentTermId;
    }
    
}
