package com.jingye.upload;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

	public class MemoryInfo{
	/*@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//getAvailaleSize();
		//getAllSize();
		Log.i("info","�ڲ����ô洢�ռ� �ǣ�"+Long.toString(getAvailableInternalMemorySize()/(1024*1024)));  
		Log.i("info","�ڲ��ܹ��洢�ռ��ǣ�"+Long.toString(getTotalInternalMemorySize()/(1024*1024)));  
		Log.i("info","�ⲿ���ô洢�ռ��ǣ�"+Long.toString(getAvailableExternalMemorySize()/(1024*1024)));  
		Log.i("info","�ⲿ�ܹ��洢�ռ��ǣ�"+Long.toString(getTotalExternalMemorySize()/(1024*1024)));  
	}*/
	public long getAvailableInternalMemorySize(){           
		File path = Environment.getDataDirectory();  //��ȡ����Ŀ¼           
		StatFs stat = new StatFs(path.getPath());           
		long blockSize = stat.getBlockSize();           
		long availableBlocks = stat.getAvailableBlocks();           
		return availableBlocks*blockSize;  
		} 
	public long getTotalInternalMemorySize(){  
        File path = Environment.getDataDirectory(); 
         StatFs stat = new StatFs(path.getPath());  
         long blockSize = stat.getBlockSize();  
         long totalBlocks = stat.getBlockCount();  
        return totalBlocks*blockSize;  
		} 
	public boolean externalMemoryAvailable(){  
         return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);  
		}

 

	public long getAvailableExternalMemorySize(){  
         if(externalMemoryAvailable()){   
             File path = Environment.getExternalStorageDirectory();  
             StatFs stat = new StatFs(path.getPath());  
             long blockSize = stat.getBlockSize();   
             long availableBlocks = stat.getAvailableBlocks();  
             return availableBlocks*blockSize;  
         }  
         else{   
             return -1;  
         }  
 	} 
 	public long getTotalExternalMemorySize(){  
	 if(externalMemoryAvailable()){   
             File path = Environment.getExternalStorageDirectory();  
             StatFs stat = new StatFs(path.getPath());  
             long blockSize = stat.getBlockSize();  
             long totalBlocks = stat.getBlockCount();  
             return totalBlocks*blockSize;  
         }  
         else{   
             return -1;  
         }  
 	} 
 
}
