package com.jingye.process;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jingye.download.FileActivity;
import com.jingye.user.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreatProcess extends Activity{
	private static final String TAG = "CreatProcess";
	//多文件上传上用到的文件和文件名集合
	Map<String, String> mapParams = new HashMap<String, String>();
	Map<String, File> files = new HashMap<String, File>();
	private EditText topicEditText;
	private EditText contentEditText;
    private TextView selecTextView;
    private TextView fileTextView;
	private Button signButton;
	private Button uploadButton;
	private Button submitButton;
	File uploadFilePath;
	String uploadUrl="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_process);
		
		findview();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				ArrayList<String> leaderList = intent.getStringArrayListExtra("leaders");
				String processidString = intent.getStringExtra("processid");
				ArrayList<String> leaderidList = intent.getStringArrayListExtra("leaderidList");
				String leaderid ="";
				if(leaderList!=null){
					String leaders="";
					for(int i=0;i<leaderList.size();i++){
				    	  leaders += leaderList.get(i).toString()+"-"; 
				    	  leaderid += leaderidList.get(i).toString()+"!";
				        }
					selecTextView.setText(leaders);
				}
				if(processidString!=null){
					uploadUrl="http://61.182.203.110:8888/?Requestflag=postprocess&processid="+processidString+
							"&processname="+topicEditText.getText().toString()+"&processexplain="+contentEditText.getText().toString()
							+"&processpeople="+leaderid+"&postren=001&degree=1";
					System.out.println("链接串："+uploadUrl);
				}
			}
			break;
		case 2:
			if(resultCode == RESULT_OK){
				ArrayList<String> filesList = intent.getStringArrayListExtra("files");
				ArrayList<String> parasList = intent.getStringArrayListExtra("paras");
				if(filesList!=null){
					String filename="";
					for(int i=0;i<filesList.size();i++){
						filename += filesList.get(i).toString()+"-";
						uploadFilePath = new File(Environment.getExternalStorageDirectory()
								+ "/敬业文件/" + filesList.get(i).toString());
						files.put(filesList.get(i).toString(), uploadFilePath);
						
					}
					Log.i(TAG,files.toString());
					fileTextView.setText(filename);
				}
				if(parasList!=null){
					for(int i=0;i<parasList.size();i++){
						mapParams.put(parasList.get(i).toString(), parasList.get(i).toString());
					}
					Log.i(TAG,mapParams.toString());
				}
			}
			break;
		default:
		}
	}
	
	@Override
	public void onBackPressed() {
		ArrayList<String> leaderList = new ArrayList<String>();
		ArrayList<String> leaderidList = new ArrayList<String>();
		ArrayList<String> filesList = new ArrayList<String>();
		ArrayList<String> parasList = new ArrayList<String>();
		Intent intent = getIntent();
		if(intent!=null){
			leaderList = intent.getStringArrayListExtra("leaders");
			leaderidList = intent.getStringArrayListExtra("leaderidList");
			filesList = intent.getStringArrayListExtra("files");
			parasList = intent.getStringArrayListExtra("paras");
			String processidString = intent.getStringExtra("processid");
			String leaderid ="";
			if(leaderList!=null){
				String leaders="";	
				for(int i=0;i<leaderList.size();i++){
			    	  leaders += leaderList.get(i).toString()+"-"; 
			    	  leaderid += leaderidList.get(i).toString()+"!";
			        }
				selecTextView.setText(leaders);
			}
			if(filesList!=null){
				String filename="";
				for(int i=0;i<filesList.size();i++){
					filename += filesList.get(i).toString()+"-";
					uploadFilePath = new File(Environment.getExternalStorageDirectory()
							+ "/敬业文件/" + filesList.get(i).toString());
					files.put(filesList.get(i).toString(), uploadFilePath);
				}
				fileTextView.setText(filename);
			}
			if(parasList!=null){
				for(int i=0;i<parasList.size();i++){
					mapParams.put(parasList.get(i).toString(), parasList.get(i).toString());
				}
			}
			if(processidString!=null){
				uploadUrl="http://61.182.203.110:8888/?Requestflag=postprocess&processid="+processidString+
						"&processname="+topicEditText.getText().toString()+"&processexplain="+contentEditText.getText().toString()
						+"&processpeople="+leaderid+"&postren=001&degree=1";
				System.out.println("链接串："+uploadUrl);
			}
			setResult(RESULT_OK, intent);
			finish();
		}
		
	}
	
	public void findview(){
		topicEditText = (EditText)findViewById(R.id.et_processName);
		contentEditText = (EditText)findViewById(R.id.et_processDes);
		selecTextView =(TextView)findViewById(R.id.tv_sign);
		fileTextView = (TextView)findViewById(R.id.tv_file);
		signButton=(Button)findViewById(R.id.btn_sign);
		uploadButton=(Button)findViewById(R.id.btn_upload_file);
		submitButton=(Button)findViewById(R.id.btn_submit);
		//选择签批人
		signButton.setOnClickListener(new OnClickListener() {

 			public void onClick(View v) {
 				Intent intent = new Intent(CreatProcess.this,Organization.class);
 				startActivityForResult(intent, 1);

 			}
 		});
		//选择附件
		uploadButton.setOnClickListener(new OnClickListener() {

 			public void onClick(View v) {
 				Intent intent = new Intent(CreatProcess.this,FileActivity.class);
 				startActivityForResult(intent, 2);

 			}
 		});
		//提交
		submitButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

				//调用异步上传文件方法
 				Asy_upload asy = new Asy_upload();
 		  		asy.execute("");
 		  		//异步是耗时操作，休眠0.5s（此值可调）
 		  		try {
 		  			Thread.sleep(500);
 		  		} catch (InterruptedException e1) {
 		  			e1.printStackTrace();
 		  		}
			}
		});
	}

	//异步多文件上传方法=======================================================================
    public class Asy_upload extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... params) {     
			
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";

			try{
			URL uri = new URL(uploadUrl);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(20 * 1000); // 缓存的最长时间
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
					+ ";boundary=" + BOUNDARY);

			// 首先组拼文本类型的参数
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : mapParams.entrySet()) {
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINEND);
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"" + LINEND);
				sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
				sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
				sb.append(LINEND);
				sb.append(entry.getValue());
				sb.append(LINEND);
			}
			
	           //设置DataOutputStream 
	          DataOutputStream ds = 
	            new DataOutputStream(conn.getOutputStream());
	          ds.write(sb.toString().getBytes());

	          //发送文件数据
	          if(files!=null){
	        	  for(Map.Entry<String, File>file:files.entrySet()){
	        		StringBuilder sb1 = new StringBuilder();
	        		sb1.append(PREFIX);
	  				sb1.append(BOUNDARY);
	  				sb1.append(LINEND);
	  				sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
	  								+ file.getKey() + "\"" + LINEND);
	  				sb1.append("Content-Type: application/octet-stream; charset="
	  						+ CHARSET + LINEND);
	  				sb1.append(LINEND);
	  				ds.write(sb1.toString().getBytes());
	  				FileInputStream fStream = new FileInputStream(file.getValue());
	  				byte[] buffer = new byte[10240];
					int len = 0;
					while ((len = fStream.read(buffer)) != -1) {
						ds.write(buffer, 0, len);
					}
					
					fStream.close();
					ds.write(LINEND.getBytes());
	        	  }
	        	// 请求结束标志
	      		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
	      		Log.d(TAG,end_data.toString());
	      		ds.write(end_data);
	      		ds.flush();
	      		// 得到响应码
	      		int res = conn.getResponseCode();
	      		if (res == 200) {
	      			InputStream is = conn.getInputStream();
	    			int ch;
	    			StringBuilder sb2 = new StringBuilder();
	    			while ((ch = is.read()) != -1) {
	    				sb2.append((char) ch);
	    			}
	    			System.out.println("上传成功"+sb2.toString().trim());
	    		}
	      		ds.close();
	    		conn.disconnect();
	        }
			}catch(Exception e)
		    {
		        //showDialog("上传失败"+e);
		    	System.out.println("上传失败"+e);
		    }
			return true;
		}
    }
    //end 异步多文件上传方法================================================================================
	
}
