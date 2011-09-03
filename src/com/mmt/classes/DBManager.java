package com.mmt.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//-------------------------------------------------------------------
class DBManager extends SQLiteOpenHelper {

	/**
	 * Database creation sql statement
	 */
	private static final String DB_CREATE_TERMS =
	    "CREATE TABLE sap_terms " +
	    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	    "title TEXT NOT NULL," +
	    "isActive INTEGER NOT NULL" +
	    ");";
	
	private static final String DB_CREATE_SUBJECTS =
	    "CREATE TABLE sap_subjects " +
	    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	    "title TEXT NOT NULL," +
	    "isDir INTEGER NOT NULL," +
	    "term_id INTEGER," +
	    "FOREIGN KEY(term_id) REFERENCES sap_terms(_id) ON DELETE CASCADE" +
	    ");";

	private static final String DB_CREATE_GRADES =
	    "CREATE TABLE sap_grades " +
	    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	    "grade INTEGER NOT NULL," +
	    "type TEXT NOT NULL," +
	    "date DATE NOT NULL," +
	    "weight INTEGER NOT NULL," +
	    "subject_id INTEGER NOT NULL," +
	    "plusminus TEXT," +
	    "term_id INTEGER NOT NULL," +
	    "FOREIGN KEY(subject_id) REFERENCES sap_subjects(_id) ON DELETE CASCADE," +
	    "FOREIGN KEY(term_id) REFERENCES sap_terms(_id) ON DELETE CASCADE" +
	    ");";
	
	private static final String DB_CREATE_GRADE_SYS =
	    "CREATE TABLE sap_grade_system " +
	    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	    "title TEXT NOT NULL," +
	    "country TEXT NOT NULL," +
	    "min INTEGER NOT NULL," +
	    "max INTEGER NOT NULL," +
	    "isActive INTEGER NOT NULL" +
	    ");";
	
	private static final String DB_CREATE_TODOS =
	    "CREATE TABLE sap_todos " +
	    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	    "title TEXT NOT NULL," +
	    "dir_id INTEGER NOT NULL," +
	    "deadline DATE NOT NULL," +
	    "term_id INTEGER NOT NULL," +
	    "isChecked INTEGER NOT NULL," +
	    "FOREIGN KEY(dir_id) REFERENCES sap_subjects(_id) ON DELETE CASCADE," +
	    "FOREIGN KEY(term_id) REFERENCES sap_terms(_id) ON DELETE CASCADE" +
	    ");";

	private static final String DB_CREATE_SCHEDULE =
	    "CREATE TABLE sap_schedule " +
	    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	    "row_id INTEGER NOT NULL," +
	    "day_id INTEGER NOT NULL," +
	    "subject_id INTEGER NOT NULL," +
	    "term_id INTEGER," +	    
	    "FOREIGN KEY(term_id) REFERENCES sap_terms(_id) ON DELETE CASCADE," +
	    "FOREIGN KEY(subject_id) REFERENCES sap_subjects(_id) ON DELETE CASCADE" +
	    ");";

	
	private static final String DB_CREATE_DAYOFF =
	    "CREATE TABLE sap_dayoffs " +
	    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	    "reason TEXT NOT NULL," +
	    "fromDate DATE NOT NULL," +
	    "toDate DATE NOT NULL," +
	    "isExcused INTEGER NOT NULL," +
	    "term_id INTEGER NOT NULL," +
	    "FOREIGN KEY(term_id) REFERENCES sap_terms(_id) ON DELETE CASCADE" +
	    ");";
	
	private static final String DATABASE_NAME = "SAPdb";
	private static final int DATABASE_VERSION = 12;
	
    DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    	db.beginTransaction();
    	try {    	 
	    	db.execSQL(DB_CREATE_TERMS);
	        db.execSQL(DB_CREATE_SUBJECTS);
	        db.execSQL(DB_CREATE_GRADES);
	        db.execSQL(DB_CREATE_GRADE_SYS);
	        db.execSQL(DB_CREATE_TODOS);
	        db.execSQL(DB_CREATE_DAYOFF);
	        db.execSQL(DB_CREATE_SCHEDULE);
	        
	        /* ADD DATA */        
	        // Add term on first creation
	        db.execSQL("INSERT INTO sap_terms (title, isActive) VALUES('Schuljahr', 1)");
	        // Predefined Grade Systems
	        // DE
	        db.execSQL("INSERT INTO sap_grade_system (title, country, min, max ,isActive) VALUES('1 - 6', 'de' , 1, 6, 1)");
	        db.execSQL("INSERT INTO sap_grade_system (title, country, min, max ,isActive) VALUES('0 - 15', 'de' , 0, 15, 0)");
	        // AT
	        db.execSQL("INSERT INTO sap_grade_system (title, country, min, max ,isActive) VALUES('1 - 5', 'at' , 1, 5, 0)");
	        
	    db.setTransactionSuccessful();
    	} finally {
    		db.endTransaction();
    	}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	db.beginTransaction();
    	try {    	
	    	Log.w("DBManagerClass", "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
	        db.execSQL("DROP TABLE IF EXISTS sap_grades");
	        db.execSQL("DROP TABLE IF EXISTS sap_terms");
	        db.execSQL("DROP TABLE IF EXISTS sap_subjects");
	        db.execSQL("DROP TABLE IF EXISTS sap_todos");
	        db.execSQL("DROP TABLE IF EXISTS sap_dayoffs");
	        db.execSQL("DROP TABLE IF EXISTS sap_schedule");
	        db.execSQL("DROP TABLE IF EXISTS sap_grade_system");
	        onCreate(db);
	        db.setTransactionSuccessful();
    	} finally {
    		db.endTransaction();
    	}
	}
}