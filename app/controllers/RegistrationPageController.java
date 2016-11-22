package controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import database.Database;
import database.DatabaseJDBC;
import models.User;
import play.mvc.Controller;
import repository.UserRepository;
import repository.Impl.UserRepositoryImpl;

public class RegistrationPageController extends Controller {
	
	private static Database database = DatabaseJDBC.getInstance();
	private static Connection conn = database.getConnection();
	
	public static void registrationPage() {
		if ( Security.isConnected() )
			Application.index();
		
    	String error = flash.get("error");
        render(error);
    }
	
	public static void register(String nickname, String password, String repeat_password, String name, String email, String promocode, String avatar) {
    	
		if (nickname == null || password == null || repeat_password == null || nickname.isEmpty() || password.isEmpty() || repeat_password.isEmpty()) {
    		flash.put("error", "You did not fill required fields completely!!!");
    		registrationPage();
    	}
		
		if ( !password.equals(repeat_password) ) {
			flash.put("error", "Passwords are different!!!");
			registrationPage();
		}
    	
    	UserRepository ur = new UserRepositoryImpl();
    	User user = ur.getUser(nickname);

		String type = "user";

		if(promocode.equals("nueditor")){
			type="editor";
		}

    	if ( user != null ) {
    		flash.put("error", "User with this nickname is already registered!");
    		registrationPage();
    	}
    	
    	if ( conn == null ) {
    		flash.put("error", "The connection to the database is null");
    		registrationPage();
    	}
    	
    	if (name == null)
    		name = "Smith";
    	else
    		System.out.println(nickname + " " + name);
    	
    	if ( ur.createUser(nickname, password, type, name) ) {
    		if ( !Security.authenticate(nickname, password) ) {
    			flash.put("error", "Some error occured! Please check connection and try to log in again");
        		LogInPageController.loginPage();
    		}
    		Application.index();
    	} else {
    		flash.put("error", "Ooops, some error occured! Please, check the connection and try again :) ");
    		registrationPage();
    	}
    		
    }
}
