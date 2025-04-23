package apiTestCapstone;

import java.io.IOException;

import apiTestCapstone.ApiTests;


public class App {
	public static void main(String[] args) throws IOException, InterruptedException {
		if(args.length == 0) {
			System.out.println("THE BASE URL IS REQUIRED");
			return;
		}
		
		if(args[0].equals(null) || args[0].equals("")) {
			System.out.println("THE BASE URL IS REQUIRED");
			return;
		}
		ApiTests apiTests = new ApiTests();
		
		if(args.length == 2) {
			if(args[1].equals("debug")) {
				apiTests.setDEBUG(true);
			}
		}
		if(args[0].equals("help")) {
			System.out.println("API test program for Capstone budget app.\nArguments: 1: baseurl, 2: debug");
		}
		
		System.out.println(args[0]);
		
		
          // Create an instance of ApiTests
        
        apiTests.setBaseURLString(args[0]);
        
        //
        
        apiTests.createUser();
        
        apiTests.loginToUser();
        
        apiTests.addTransaction();
        apiTests.fetchTransaction();
        
        apiTests.deleteTransaction();
        
        apiTests.addCategory();
        
        apiTests.fetchCategory();
        
        apiTests.deleteCategory();
    }
	

}
