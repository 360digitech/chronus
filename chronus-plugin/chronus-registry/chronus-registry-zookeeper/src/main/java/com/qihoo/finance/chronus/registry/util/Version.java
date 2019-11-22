package com.qihoo.finance.chronus.registry.util;

public class Version {
	
   public final static String VERSION="chronus-1.0.0";
   
   public static String getVersion(){
	   return VERSION;
   }
   public static boolean isCompatible(String dataVersion){
	  if(VERSION.compareTo(dataVersion)>=0){
		  return true;
	  }else{
		  return false;
	  }
   }
   
}
