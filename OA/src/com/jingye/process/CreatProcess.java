package com.jingye.process;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jingye.download.FileActivity;
import com.jingye.user.R;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class CreatProcess extends Activity{
	private static final String TAG = "CreatProcess";
	//多文件上传上用到的文件和文件名集合
	Map<String, String> mapParams = new HashMap<String, String>();
	Map<String, File> files = new HashMap<String, File>();
	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private EditText topicEditText;
	private EditText contentEditText;
    private EditText selecEditText;
    private EditText fileEditText;
	private Button submitButton;
	private Button saveButton;
	private Spinner levelSpinner;
	File uploadFilePath;
	String uploadUrl="";
    String topicString= "";
    String contentString = "";
    String levelId;
    String leaderid ="";
    String newTopic ="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_process);
		getData();
		findview();
	}
	
	public void getData(){
		Map<String,String> maplist1= new HashMap<String, String>();
		maplist1.put("levelNum", "1");
		maplist1.put("levelName", "一般");
		list.add(maplist1);
		Map<String,String> maplist2= new HashMap<String, String>();
		maplist2.put("levelNum", "2");
		maplist2.put("levelName", "紧急");
		list.add(maplist2);
		Map<String,String> maplist3= new HashMap<String, String>();
		maplist3.put("levelNum", "3");
		maplist3.put("levelName", "非常紧急");
		list.add(maplist3);
		System.out.println("list======="+list);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case 1:
			if (resultCode == RESULT_OK) {
				ArrayList<String> leaderList = intent.getStringArrayListExtra("leaders");
				String processidString = intent.getStringExtra("processid");
				ArrayList<String> leaderidList = intent.getStringArrayListExtra("leaderidList");
				
				if(leaderList!=null){
					String leaders="";
					for(int i=0;i<leaderList.size();i++){
				    	  leaders += leaderList.get(i).toString()+"-"; 
				    	  leaderid += leaderidList.get(i).toString()+"!";
				        }
					selecEditText.setText(leaders);
				}
				if(processidString!=null){
					topicEditText = (EditText)findViewById(R.id.et_processName);
					contentEditText = (EditText)findViewById(R.id.et_processDes);
					topicString = topicEditText.getText().toString();
					contentString = contentEditText.getText().toString();
					try {
						newTopic = URLEncoder.encode(topicString,"UTF-8");  //编码，防中文乱码
						String newContent = URLEncoder.encode(contentString, "UTF-8");
						uploadUrl="http://61.182.203.110:8888/?Requestflag=postprocess&processid="+processidString+
								"&processname="+newTopic+"&processexplain="+newContent
								+"&processpeople="+leaderid+"&postren=029848&degree="+levelId;
						System.out.println("链接串："+uploadUrl);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
					fileEditText.setText(filename);
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

			if(leaderList!=null){
				String leaders="";	
				for(int i=0;i<leaderList.size();i++){
			    	  leaders += leaderList.get(i).toString()+"-"; 
			    	  leaderid += leaderidList.get(i).toString()+"!";
			        }
				selecEditText.setText(leaders);
			}
			if(filesList!=null){
				String filename="";
				for(int i=0;i<filesList.size();i++){
					filename += filesList.get(i).toString()+"-";
					uploadFilePath = new File(Environment.getExternalStorageDirectory()
							+ "/敬业文件/" + filesList.get(i).toString());
					files.put(filesList.get(i).toString(), uploadFilePath);
				}
				fileEditText.setText(filename);
			}
			if(parasList!=null){
				for(int i=0;i<parasList.size();i++){
					mapParams.put(parasList.get(i).toString(), parasList.get(i).toString());
				}
			}
			if(processidString!=null){
				topicEditText = (EditText)findViewById(R.id.et_processName);
				contentEditText = (EditText)findViewById(R.id.et_processDes);
				topicString = topicEditText.getText().toString();
				contentString = contentEditText.getText().toString();
				try {
					newTopic = URLEncoder.encode(topicString,"UTF-8");  //编码，防中文乱码
					String newContent = URLEncoder.encode(contentString, "UTF-8");
					uploadUrl="http://61.182.203.110:8888/?Requestflag=postprocess&processid="+processidString+
							"&processname="+newTopic+"&processexplain="+newContent
							+"&processpeople="+leaderid+"&postren=029848&degree="+levelId;
					System.out.println("链接串："+uploadUrl);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			setResult(RESULT_OK, intent);
			finish();
		}
		
	}
	
	public void findview(){
		levelSpinner = (Spinner)findViewById(R.id.spinner_level);
		selecEditText =(EditText)findViewById(R.id.et_sign);
		fileEditText = (EditText)findViewById(R.id.et_file);
		submitButton=(Button)findViewById(R.id.btn_submit);
		saveButton = (Button)findViewById(R.id.btn_saveProcess);
		SimpleAdapter adapter = new SimpleAdapter(this, list,
				R.layout.select_item, new String[] { "levelNum",
						"levelName" }, new int[] { R.id.value,
						R.id.lable });
		levelSpinner.setAdapter(adapter);
		levelSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long id) {
				levelId = ((TextView) CreatProcess.this.findViewById(R.id.value))
						.getText().toString();
				System.out.println("levelID=========="+levelId);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
		//选择签批人
		selecEditText.setOnTouchListener(new View.OnTouchListener() {
 			//按住和松开的标识
			int touch_flag=0;
			public boolean onTouch(View v, MotionEvent event) {
				touch_flag++;
				if(touch_flag==2){
					Intent intent = new Intent(CreatProcess.this,Organization.class);
	 				startActivityForResult(intent, 1);
				}
				return false;
			}
 		});
		//选择附件
		fileEditText.setOnTouchListener(new View.OnTouchListener() {
 			//按住和松开的标识
			int touch_flag=0;
			public boolean onTouch(View v, MotionEvent event) {
				touch_flag++;
				if(touch_flag==2){
					Intent intent = new Intent(CreatProcess.this,FileActivity.class);
	 				startActivityForResult(intent, 2);
				}
				return false;
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
		//保存流程
		saveButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

				getjson();
			}
		});
	}
	
	// 根据接口得到json数据====================================
		@SuppressWarnings("unchecked")
		public void getjson(){
			FinalHttp fh = new FinalHttp(); 
		     		try {
		     			fh.get( "http://61.182.203.110:8888/?Requestflag=creategroup&grouptype=1&groupcreatetype=2&createuser=029848&shenpiren="+leaderid+"&groupname="+newTopic,
		     					new AjaxCallBack() {
		     						@Override
		     						public void onStart() {
		     							super.onStart();
		     							Log.v(TAG,"开始请求");
		     						}

		     						@Override
		     						public void onLoading(long count, long current) {
		     							super.onLoading(count, current);
		     							U.toast(CreatProcess.this,"正在加载");
		     						}

		     						@Override
		     						public void onSuccess(Object result) {
		     							// 访问服务器成功，获得访问结果result
		     							String str = (String) result;
		     							//Log.v(TAG,"请求结果(json串)" + result);
		     							//Log.v(TAG,"str====" + str);
		     							// ++++++++++++解析数据++++++++++++++++++++++++++++++
		     							try {
		     								//listmap = new ArrayList<HashMap<String, String>>();
		     								JSONObject jsonObject = new JSONObject(str);

		     								String data = jsonObject.getString("ret");
		     								if(data=="1001"){
		     									U.toast(CreatProcess.this, "保存流程成功！");
		     								} else {
		     									Log.v(TAG,"data无值,为=" + data);
		     								}
		     							} catch (JSONException e) {
		     								e.printStackTrace();
		     							}

		     						}

		     						@Override
		     						public void onFailure(Throwable t, int errorNo,
		     								String strMsg) {
		     							super.onFailure(t, errorNo, strMsg);
		     							Log.v(TAG,"解析失败"+strMsg);
		     							//U.closeLoadingDialog();
		     							//U.showNetErr(SelectActivity.this);
		     						}
		     					});

		     		} catch (Exception e) {
		     			e.printStackTrace();
		     		}		
		}
		//

	//异步多文件上传方法=======================================================================
    public class Asy_upload extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... params) {     
			
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			System.out.println("UUID:"+BOUNDARY);
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
			   System.out.println("sb===="+sb);
			   System.out.println("conn==="+conn.getOutputStream());
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
	  				System.out.println("file.value======="+file.getValue());
	  				byte[] buffer = new byte[10240];
					int len = 0;
					while ((len = fStream.read(buffer)) != -1) {
						ds.write(buffer, 0, len);
					}
					
					System.out.println("文件输入流："+fStream);
					System.out.println("sb1===="+sb1);
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
