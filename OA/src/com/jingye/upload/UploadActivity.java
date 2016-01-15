package com.jingye.upload;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.jingye.user.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UploadActivity extends Activity {

	    
	
	    String newName ;
	    String uploadFilePath ;
	    //String UPDATE = "updateTime";
	    //private String actionUrl = "http://61.182.203.110:8999/JingYeYunService/upload.aspx";
	    private String actionUrl = "http://61.182.201.194:3931/CustomerSatisfaction/?requestflag=upfiles02";
	    
	    private TextView mText1;
	    private TextView mText2;
	 
	    private Button mButton;
	    private Button select;
	    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        
        mText1 = (TextView) findViewById(R.id.textView2);
        mText2 = (TextView) findViewById(R.id.textView4);
        
      
        
        //"上传网址：\n"+
        mText2.setText(actionUrl);
        /* 设置mButton的onClick事件处理 */    
        mButton = (Button) findViewById(R.id.button1);
        mButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View v)
          {
            uploadFile();
          }
        });
        
        select = (Button)findViewById(R.id.button2);
        select.setOnClickListener(new View.OnClickListener() {
			
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				Intent intent = getIntent();
				if (Intent.ACTION_VIEW.equals(intent.getAction())) {
					Uri uri = intent.getData();
					if (uri.toString().startsWith("content://media/external/file")) {
						Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
						if (cursor.moveToFirst()) {
							uri = Uri.parse(cursor.getString(0));
						}
					}
					uploadFilePath = Uri.decode(uri.getEncodedPath());
					mText1.setText(Uri.decode(uri.getEncodedPath()));
				}
				newName = intent.getStringExtra("filename");
				try {
					newName = URLEncoder.encode(newName,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
       
       
      }
  
      /* 上传文件至Server的方法 */
      private void uploadFile()
      {
    	    Asy_upload asy = new Asy_upload();
	  		asy.execute("");
	  		try {
	  			Thread.sleep(500);
	  		} catch (InterruptedException e1) {
	  			e1.printStackTrace();
	  		}
      }

      public class Asy_upload extends AsyncTask<Object, Object, Object> {
  		@Override
  		protected Object doInBackground(Object... params) {
  			String end = "\r\n";
  	        String twoHyphens = "--";
  	        String boundary = "*****";
  	     
  	        try
  	        {
  	         
  	          URL url =new URL(actionUrl);
  	          HttpURLConnection con=(HttpURLConnection)url.openConnection();
  	          /* 允许Input、Output，不使用Cache */
  	          con.setDoInput(true);
  	          con.setDoOutput(true);
  	          con.setUseCaches(false);
  	          /* 设置传送的method=POST */
  	          con.setRequestMethod("POST");
  	          /* setRequestProperty */
  	          con.setRequestProperty("Connection", "Keep-Alive");
  	          con.setRequestProperty("Charset", "UTF-8");
  	          con.setRequestProperty("Content-Type",
  	                             "multipart/form-data;boundary="+boundary);
  	          /* 设置DataOutputStream */
  	          DataOutputStream ds = 
  	            new DataOutputStream(con.getOutputStream());

  	          ds.writeBytes(twoHyphens + boundary + end );
  	        //getBytes("ISO-8859-1"),"UTF-8"
  	          System.out.println(">>>>>>>>>>>>>"+newName);
  	          ds.writeBytes("Content-Disposition: form-data; " +
  	                        "name=\"file1\";filename=\"" +
  	                      newName+"\"" + end);
  	          ds.writeBytes(end);   

  	          /* 取得文件的FileInputStream */
  	          FileInputStream fStream = new FileInputStream(uploadFilePath);
  	          /* 设置每次写入1024bytes */
  	          int bufferSize = 2048;
  	          byte[] buffer = new byte[bufferSize];

  	          int length = -1;
  	          /* 从文件读取数据至缓冲区 */
  	          while((length = fStream.read(buffer)) != -1)
  	          {
  	            /* 将资料写入DataOutputStream中 */
  	            ds.write(buffer, 0, length);
  	          }
  	          ds.writeBytes(end);
  	          ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

  	          /* close streams */
  	          fStream.close();
  	          ds.flush();

  	          /* 取得Response内容 */
  	          InputStream is = con.getInputStream();
  	          int ch;
  	          StringBuffer b =new StringBuffer();
  	          while( ( ch = is.read() ) != -1 )
  	          {
  	            b.append( (char)ch );
  	            //is.read(buffer, 0, b.length());
  	          }
  	          //is.read(buffer, byteOffset, byteCount)
  	          /* 将Response显示于Dialog */
  	          //showDialog("上传成功"+b.toString().trim());
  	          System.out.println("上传成功"+b.toString().trim());
  	          /* 关闭DataOutputStream */
  	          ds.close();
  	        }
  	        catch(Exception e)
  	        {
  	          //showDialog("上传失败"+e);
  	        	System.out.println("上传失败"+e);
  	        }
  			return true;
  		}
  	}
      
      /* 显示Dialog的method */
      private void showDialog(String mess)
      {
        new AlertDialog.Builder(UploadActivity.this).setTitle("上传结果")
         .setMessage(mess)
         .setNegativeButton("确定",new DialogInterface.OnClickListener()
         {
           public void onClick(DialogInterface dialog, int which)
           {          
           }
         })
         .show();
      }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
}

