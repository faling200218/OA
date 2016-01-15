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
        
      
        
        //"�ϴ���ַ��\n"+
        mText2.setText(actionUrl);
        /* ����mButton��onClick�¼����� */    
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
  
      /* �ϴ��ļ���Server�ķ��� */
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
  	          /* ����Input��Output����ʹ��Cache */
  	          con.setDoInput(true);
  	          con.setDoOutput(true);
  	          con.setUseCaches(false);
  	          /* ���ô��͵�method=POST */
  	          con.setRequestMethod("POST");
  	          /* setRequestProperty */
  	          con.setRequestProperty("Connection", "Keep-Alive");
  	          con.setRequestProperty("Charset", "UTF-8");
  	          con.setRequestProperty("Content-Type",
  	                             "multipart/form-data;boundary="+boundary);
  	          /* ����DataOutputStream */
  	          DataOutputStream ds = 
  	            new DataOutputStream(con.getOutputStream());

  	          ds.writeBytes(twoHyphens + boundary + end );
  	        //getBytes("ISO-8859-1"),"UTF-8"
  	          System.out.println(">>>>>>>>>>>>>"+newName);
  	          ds.writeBytes("Content-Disposition: form-data; " +
  	                        "name=\"file1\";filename=\"" +
  	                      newName+"\"" + end);
  	          ds.writeBytes(end);   

  	          /* ȡ���ļ���FileInputStream */
  	          FileInputStream fStream = new FileInputStream(uploadFilePath);
  	          /* ����ÿ��д��1024bytes */
  	          int bufferSize = 2048;
  	          byte[] buffer = new byte[bufferSize];

  	          int length = -1;
  	          /* ���ļ���ȡ������������ */
  	          while((length = fStream.read(buffer)) != -1)
  	          {
  	            /* ������д��DataOutputStream�� */
  	            ds.write(buffer, 0, length);
  	          }
  	          ds.writeBytes(end);
  	          ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

  	          /* close streams */
  	          fStream.close();
  	          ds.flush();

  	          /* ȡ��Response���� */
  	          InputStream is = con.getInputStream();
  	          int ch;
  	          StringBuffer b =new StringBuffer();
  	          while( ( ch = is.read() ) != -1 )
  	          {
  	            b.append( (char)ch );
  	            //is.read(buffer, 0, b.length());
  	          }
  	          //is.read(buffer, byteOffset, byteCount)
  	          /* ��Response��ʾ��Dialog */
  	          //showDialog("�ϴ��ɹ�"+b.toString().trim());
  	          System.out.println("�ϴ��ɹ�"+b.toString().trim());
  	          /* �ر�DataOutputStream */
  	          ds.close();
  	        }
  	        catch(Exception e)
  	        {
  	          //showDialog("�ϴ�ʧ��"+e);
  	        	System.out.println("�ϴ�ʧ��"+e);
  	        }
  			return true;
  		}
  	}
      
      /* ��ʾDialog��method */
      private void showDialog(String mess)
      {
        new AlertDialog.Builder(UploadActivity.this).setTitle("�ϴ����")
         .setMessage(mess)
         .setNegativeButton("ȷ��",new DialogInterface.OnClickListener()
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

