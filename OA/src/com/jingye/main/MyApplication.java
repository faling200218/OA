package com.jingye.main;

import android.app.Application;

public class MyApplication extends Application{
   private String name;
   private static final String NAME = "http://61.182.203.110:8888/?";
   @Override
   public void onCreate(){
	   super.onCreate();
	   setName(NAME);
   }
   
   public String getName(){
	   return name;
   } 
   public void setName(String name){
	   this.name = name;
   }
 
}
