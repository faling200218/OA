package com.jingye.download;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jingye.upload.UploadActivity;
import com.jingye.upload.UploadActivity.Asy_upload;
import com.jingye.user.R;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private ListView bookList;
	private Button btn_download;
	private Button btn_upload;
	private BookAdapter adapter;
	private List<HashMap<String, String>> fileData;
	private final static String SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath().toString()+"/��ҵ�ļ�/";
	private String downloadUrl = "http://61.182.201.194:3931/CustomerSatisfaction/?requestflag=downfiles01&fid=28";
	private String uploadUrl = "http://61.182.201.194:3931/CustomerSatisfaction/?requestflag=upfiles02";
	//private String actionUrl = "http://61.182.203.110:8999/JingYeYunService/download.aspx";
	StringBuffer sb;
	//ArrayList<String> list = new ArrayList<String>();
	protected int local_flag = LOCAL_FILE;
	private final static int WEB_FILE = 0;
	private final static int LOCAL_FILE = 1;
	String newName ="";
	String uploadFilePath = "";
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 10) { // ����ѡ����Ʒ���ܼ�
					newName = (String) msg.obj;
					uploadFilePath = Environment.getExternalStorageDirectory()
					+ "/��ҵ�ļ�/" + newName;
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileData = getData(SDCARD_PATH);
        //�ϴ�
        btn_upload = (Button)findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new OnClickListener() {

 			public void onClick(View v) {

 				/*String fileName = fileData.get(position).get("filename");
 				String filePath = Environment.getExternalStorageDirectory()
 						+ "/��ҵ�ļ�/" + fileData.get(position).get("filename");
 		        Log.v(TAG,fileName);
 		        Uri uri = Uri.parse(filePath);
 				Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
 				intent.setAction(Intent.ACTION_VIEW);
 				intent.setData(uri);
 				intent.putExtra("filename", fileName);
 				startActivity(intent);*/

 				if(uploadFile()){
 					showDialog("�ϴ��ɹ�!");
 				}else {
					showDialog("�ϴ�ʧ�ܣ�");
				}
 			}
 		});
		
        
        //����
        btn_download = (Button)findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new OnClickListener() {

 			public void onClick(View v) {

 				downLoadFile();
 				Log.v(TAG,"pdf���سɹ���");
 			}
 		});
        
        bookList = (ListView) findViewById(R.id.book_directory);
        adapter = new BookAdapter(this,handler,fileData);
		//adapter.setFileData(fileData);
		bookList.setAdapter(adapter);	
		
		LayoutParams params = bookList.getLayoutParams();
		params.height = adapter.fileData.size() * 500;
		bookList.setLayoutParams(params);
		bookList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Log.v(TAG,fileData.get(position).get("filename"));
				String fileName = Environment.getExternalStorageDirectory()
						+ "/��ҵ�ļ�/" + fileData.get(position).get("filename");
				doOpenFile(fileName);
				
			}
		});  
    }
    
    /* ��ʾDialog��method */
    private void showDialog(String mess)
    {
      new AlertDialog.Builder(MainActivity.this).setTitle("�ϴ����")
       .setMessage(mess)
       .setNegativeButton("ȷ��",new DialogInterface.OnClickListener()
       {
         public void onClick(DialogInterface dialog, int which)
         {          
         }
       })
       .show();
    }
    
    /* �ϴ��ļ���Server�ķ��� */
    private boolean uploadFile()
    {
  	        Asy_upload asy = new Asy_upload();
	  		if(asy.execute("") != null){
	  			return true;
	  		}else {
	  			return false;
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
	         
	          URL url =new URL(uploadUrl);
	          HttpURLConnection con=(HttpURLConnection)url.openConnection();
	           //����Input��Output����ʹ��Cache 
	          con.setDoInput(true);
	          con.setDoOutput(true);
	          con.setUseCaches(false);
	           //���ô��͵�method=POST 
	          con.setRequestMethod("POST");
	           //setRequestProperty 
	          con.setRequestProperty("Connection", "Keep-Alive");
	          con.setRequestProperty("Charset", "UTF-8");
	          con.setRequestProperty("Content-Type",
	                             "multipart/form-data;boundary="+boundary);
	           //����DataOutputStream 
	          DataOutputStream ds = 
	            new DataOutputStream(con.getOutputStream());

	          ds.writeBytes(twoHyphens + boundary + end );
	          //getBytes("ISO-8859-1"),"UTF-8"
	          System.out.println("�ϴ��ļ������֣�"+newName);
	          ds.writeBytes("Content-Disposition: form-data; " +
	                        "name=\"file1\";filename=\"" +
	                      newName+"\"" + end);
	          ds.writeBytes(end);   

	           //ȡ���ļ���FileInputStream 
	          FileInputStream fStream = new FileInputStream(uploadFilePath);
	           //����ÿ��д��1024bytes 
	          int bufferSize = 2048;
	          byte[] buffer = new byte[bufferSize];

	          int length = -1;
	          // ���ļ���ȡ������������ 
	          while((length = fStream.read(buffer)) != -1)
	          {
	            // ������д��DataOutputStream�� 
	            ds.write(buffer, 0, length);
	            System.out.println("�����ϴ���"+length);
	          }
	          ds.writeBytes(end);
	          ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

	           //close streams 
	          fStream.close();
	          ds.flush();

	           //ȡ��Response���� 
	          InputStream is = con.getInputStream();
	          int ch;
	          StringBuffer b =new StringBuffer();
	          while( ( ch = is.read() ) != -1 )
	          {
	            b.append( (char)ch );
	            //is.read(buffer, 0, b.length());
	          }
	          //is.read(buffer, byteOffset, byteCount)
	           //��Response��ʾ��Dialog 
	          //showDialog("�ϴ��ɹ�"+b.toString().trim());
	          System.out.println("�ϴ��ɹ�"+b.toString().trim());
	          // �ر�DataOutputStream 
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
    
	public void downLoadFile(){
		Asy_downloadfile asy = new Asy_downloadfile();
  		asy.execute("");
  		try {
  			Thread.sleep(500);
  		} catch (InterruptedException e1) {
  			e1.printStackTrace();
  		}
	}
	
	//�첽�����ļ�
	public class Asy_downloadfile extends AsyncTask<Object, Object, Object> {
  		@Override
  		protected Object doInBackground(Object... params) {
  			
  			// ��ô洢��·�������� �����ļ���Ŀ��·��
  			String dirName = "";
  			dirName = Environment.getExternalStorageDirectory()+"/��ҵ�ļ�/";
  			File f = new File(dirName);
  			if(!f.exists())
  			{
  			    f.mkdir();
  			}

  			try {
  			         // ����URL   
  			         URL url = new URL(downloadUrl);   
	  	  	          HttpURLConnection con=(HttpURLConnection)url.openConnection();
	  	  	          /* ����Input��Output����ʹ��Cache */
	  	  	          con.setDoInput(true);
	  	  	          con.setDoOutput(true);
	  	  	          con.setUseCaches(false);
  			         //����ļ��ĳ���
  			         int contentLength = con.getContentLength();
  			         System.out.println("���� :"+contentLength); 
  			         String nnString = URLDecoder.decode(con.getHeaderField("Content-Disposition"),"UTF-8");
  			        //׼��ƴ���µ��ļ����������ڴ洢������ļ�����
  			         String newFilename = dirName +nnString.substring(nnString.indexOf("=")+1,nnString.length()); 
  		  			File file = new File(newFilename);
  		  			//���Ŀ���ļ��Ѿ����ڣ���ɾ�����������Ǿ��ļ���Ч��
  		  			if(file.exists())
  		  			{
  		  			    file.delete();
  		  			}
  			         System.out.println("++++++++++++++++"+newFilename);
  			         // ������   
  			         final InputStream is = con.getInputStream();  
  			         // 4K�����ݻ���   
  			         byte[] bs = new byte[4096];   
  			         // ������ļ���   
  			         OutputStream os = new FileOutputStream(newFilename); 
		  	          int length = -1;
		  	          /* ���ļ���ȡ������������ */
		  	          while((length = is.read(bs)) != -1)
		  	          {
		  	            /* ������д��DataOutputStream�� */
		  	            os.write(bs, 0, length);
		  	            System.out.println(length);
		  	          } 
  			         // ��ϣ��ر���������   
  			         os.close();  
  			         is.close();
  			            
  			} catch (Exception e) {
  			        e.printStackTrace();
  			}
            return Log.v(TAG,"���سɹ���");
  		}
  	}
	
    private void doOpenFile(String filepath) {
		File file = new File(filepath);
		final Uri uri = Uri.fromFile(file);
		
		Log.v(TAG, "uri = " + uri.getPath());
		final Intent intent = new Intent("android.intent.action.VIEW", uri);
		/*intent.setClassName("com.kinggrid.iapppdf.demo",
				"com.kinggrid.iapppdf.demo.BookShower");*/
    	intent.setClassName(getApplicationContext(),
				"com.jingye.signature.BookShower");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		Log.v(TAG, "username = " + userName);
//		if (userName.getText().toString() == null || sureCopyRight() == null) {
//			return;
//		}
//		
//		intent.putExtra("sUrl", sUrl.getText().toString());
//		intent.putExtra("userName", "test");
//		intent.putExtra("signatureField2", signatureField2.getText().toString());
		intent.putExtra("signatureField2", "");
//		intent.putExtra("copyRight", "SxD/phFsuhBWZSmMVtSjKZmm/c/3zSMrkV2Bbj5tznSkEVZmTwJv0wwMmH/+p6wLiUHbjadYueX9v51H9GgnjUhmNW1xPkB++KQqSv/VKLDSxD/phFsuhBWZSmMVtSjKZmm/c/3zSMrkV2Bbj5tznSkEVZmTwJv0wwMmH/+p6wLiUHbjadYueX9v51H9GgnjUhmNW1xPkB++KQqSv/VKLDsR8V6RvNmv0xyTLOrQoGzAT81iKFYb1SZ/Zera1cjGwQSq79AcI/N/6DgBIfpnlwiEiP2am/4w4+38lfUELaNFry8HbpbpTqV4sqXN1WpeJ7CHHwcDBnMVj8djMthFaapMFm/i6swvGEQ2JoygFU368sLBQG57FhM8Bkq7aPAVrvKeTdxzwi2Wk0Yn+WSxoXx6aD2yiupr6ji7hzsE6/QRx89Izb7etgW5cXVl5PwISjX+xEgRoNggvmgA8zkJXOVWEbHWAH22+t7LdPt+jENUl53rvJabZGBUtMVMHP2J32poSbszHQQyNDZrHtqZuuSgCNRP4FpYjl8hG/IVrYXSo6k2pEkUqzd5hh5kngQSOW8fXpxdRHfEuWC1PB9ruQ=");
//		intent.putExtra("isAnnotProtect", chv_isAnnotProtect.isChecked());
//		intent.putExtra("isSupportEbenT7Mode", sureModes());
//		 //isSupportImmediatelyGetVector������Ҫ���ó�true��false,�˲���Ϊtrueʱʸ��ǩ�������ٶȱȽϿ�
//		intent.putExtra("isSupportImmediatelyGetVector", true);
//		intent.putExtra("saveVector", false);
//		if (chv_fillTemplate.isChecked()) {
//			intent.putExtra("template", sureFillTemplate());
//		}
//		
//		Bundle bundle = new Bundle();
//		bundle.putParcelable("pageViewMode", surePageViewMode());
//		intent.putExtras(bundle);
//		intent.putExtra("local_flag", local_flag);
//		if (local_flag == WEB_FILE) {
//			intent.putExtra("weburl", url.getText().toString());
//		}
//		intent.putExtra("bookRotation", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		intent.putExtra("isUseEbenSDK", (Integer)mode_RadioGroup.getTag() == R.id.eben_sign_mode);
		startActivity(intent);
	}
    
    private List<HashMap<String, String>> getData(String filePath) {
		List<HashMap<String, String>> filedata = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> filehashMap;
		if (local_flag == WEB_FILE) {

		} else {
			File file = new File(filePath);
			if (file.isDirectory()) {
				File[] fileArray = file.listFiles(new FileNameSelector(
						"pdf||.pdf||.PDF"));
				if (null != fileArray && 0 != fileArray.length) {
					for (int i = 0; i < fileArray.length; i++) {
						if (!fileArray[i].isDirectory()) {
							filehashMap = new HashMap<String, String>();
							filehashMap.put("filename", fileArray[i].getName()
									.toString());
							filehashMap.put("url", fileArray[i].getPath());
							Log.v(TAG, "file path = " + fileArray[i].getPath()
									+ " file name = " + fileArray[i].getName());
							filedata.add(filehashMap);
						}
					}
				}
			}
		}

		return filedata;
	}

    public class FileNameSelector implements FilenameFilter {
		String extension = ".";

		public FileNameSelector(String fileExtensionNoDot) {
			extension += fileExtensionNoDot;
		}

		public boolean accept(File dir, String name) {
			return name.endsWith("pdf") || name.endsWith("PDF");
		}
	}
    
    
    
}
