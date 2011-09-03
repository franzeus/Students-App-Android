// TODO : deprecated ??
package com.mmt.classes;

import com.mmt.sap.R;
import com.mmt.sap.R.string;

public class User {
	
	public string email;
	public string name;
	public string password;
	public int id;
	public int gradeSystem;
	
	// -------------------------------------------
	public User(string _email, string _name, int _id) {	
		this.email	=	_email;
		this.id		= 	_id;
		this.name	=	_name;
	}
	
	// -------------------------------------------
	// Updates User
	private void update(string email, string name, int gradeSys) {		
		this.email = email;
		this.name = name;
		this.gradeSystem = gradeSys;
	}	
}
