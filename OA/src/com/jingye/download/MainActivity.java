package com.jingye.download;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.jingye.user.LoginActivity;
import com.jingye.user.R;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private ListView bookList;
	private Button btn_download;
	private Button btn_upload;
	private BookAdapter adapter;
	private List<HashMap<String, String>> fileData;
	private final static String SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath().toString()+"/敬业文件/";
	private String downloadUrl = "http://61.182.203.110:8888/?Requestflag=downfiles&fid=20160122143237";
	private String uploadUrl = "http://61.182.203.110:8888/?requestflag=upfiles02&shrshu=9";
	//private String downloadUrl = "http://61.182.203.110:8999/JingYeYunService/download.aspx";
	//private String uploadUrl = "http://61.182.203.110:8999/JingYeYunService/upload.aspx";
	
	protected int local_flag = LOCAL_FILE;
	private final static int WEB_FILE = 0;
	private final static int LOCAL_FILE = 1;
	String newName ="";
	File uploadFilePath;
	
	//多文件上传上用到的文件和文件名集合
	Map<String, String> mapParams = new HashMap<String, String>();
	Map<String, File> files = new HashMap<String, File>();
	
	//接收适配器页面传送过来的文件名=============================================
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 10) {         //适配器页面复选框选中时通知主界面将选中文件添加进集合
				newName = (String) msg.obj;
				uploadFilePath = new File(Environment.getExternalStorageDirectory()
						+ "/敬业文件/" + newName);
				files.put(newName, uploadFilePath);
				try {
					String urlName = URLEncoder.encode(newName,"UTF-8");  //编码，防中文乱码
					mapParams.put(urlName, urlName);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}else if(msg.what == 11){     //适配器页面复选框取消选中时通知主界面将反选中文件从集合中移除
				newName = (String) msg.obj;
				uploadFilePath = new File(Environment.getExternalStorageDirectory()
						+ "/敬业文件/" + newName);
				files.remove(newName);
				try {
					String urlName = URLEncoder.encode(newName,"UTF-8"); //编码，防中文乱码
					mapParams.remove(urlName);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	};
	//end 接收适配器页面传送过来的文件名===========================================
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        findview();
    }

    public void findview(){
    	
    	//listview显示===============================================================
    	fileData = getData(SDCARD_PATH);   //bookList的数据源
        bookList = (ListView) findViewById(R.id.book_directory);
        adapter = new BookAdapter(this,handler,fileData);
		//adapter.setFileData(fileData);
		bookList.setAdapter(adapter);	
		
		LayoutParams params = bookList.getLayoutParams(); //这三句控制bookList高度，是金格demo的原代码，
		params.height = adapter.fileData.size() * 500;    //不知为什么要这样设置，我在后面乘以500增加了高度。
		bookList.setLayoutParams(params);
		//listview item点击事件
		bookList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Log.v(TAG,fileData.get(position).get("filename"));
				String fileName = Environment.getExternalStorageDirectory()
						+ "/敬业文件/" + fileData.get(position).get("filename");
				doOpenFile(fileName);
				
			}
		});
		//end listview显示==================================================================
		
		//上传单击事件========================================================================
        btn_upload = (Button)findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new OnClickListener() {

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
      //end 上传单击事件==================================================================
        
        
        
      //下载单击事件======================================================================
        btn_download = (Button)findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new OnClickListener() {

 			public void onClick(View v) {
 				//调用异步下载文件方法
 				/*Asy_downloadfile asy = new Asy_downloadfile();
 		  		asy.execute("");
 		  		//异步耗时0.5s（此值可调）
 		  		try {
 		  			Thread.sleep(500);
 		  		} catch (InterruptedException e1) {
 		  			e1.printStackTrace();
 		  		}

 				//下载过后重新加载达到页面刷新效果
 				fileData = getData(SDCARD_PATH);
 				adapter = new BookAdapter(MainActivity.this,handler,fileData);
 				//adapter.setFileData(fileData);
 				bookList.setAdapter(adapter);*/
 				
 				Intent intent = new Intent(MainActivity.this,
						DownloadActivity.class);
				startActivity(intent);
 			}
 		});
      //end 下载单击事件==================================================================   
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
    
	//异步下载文件方法==============================================================================
	public class Asy_downloadfile extends AsyncTask<Object, Object, Object> {
  		@Override
  		protected Object doInBackground(Object... params) {	
  			// 获得存储卡路径，构成 保存文件的目标路径
  			String dirName = "";
  			dirName = Environment.getExternalStorageDirectory()+"/敬业文件/";
  			File f = new File(dirName);
  			//如果路径不存在就新建
  			if(!f.exists())
  			{
  			    f.mkdir();
  			}

  			try {
  			         // 构造URL   
  			         URL url = new URL(downloadUrl);   
	  	  	          HttpURLConnection con=(HttpURLConnection)url.openConnection();
	  	  	          /* 允许Input、Output，不使用Cache */
	  	  	          con.setDoInput(true);
	  	  	          con.setDoOutput(true);
	  	  	          con.setUseCaches(false);
  			         //获得文件的长度
  			         int contentLength = con.getContentLength();
  			         System.out.println("长度 :"+contentLength); 
  			         //解码获得服务端文件名字段，要获得正确中文文件名，需要解码并拼接字符串
  			         String nnString = URLDecoder.decode(con.getHeaderField("Content-Disposition"),"UTF-8");
  			        //拼接新的文件名（保存在存储卡后的文件名）
  			         String newFilename = dirName +nnString.substring(nnString.indexOf("=")+1,nnString.length()); 
  		  			File file = new File(newFilename);
  		  			//如果目标文件已经存在，则删除。产生覆盖旧文件的效果
  		  			if(file.exists())
  		  			{
  		  			    file.delete();
  		  			}
  			         // 输入流   
  			         final InputStream is = con.getInputStream();  
  			         // 4K的数据缓冲   
  			         byte[] bs = new byte[4096];   
  			         // 输出的文件流   
  			         OutputStream os = new FileOutputStream(newFilename); 
		  	          int length = -1;
		  	          /* 从文件读取数据至缓冲区 */
		  	          while((length = is.read(bs)) != -1)
		  	          {
		  	            /* 将资料写入DataOutputStream中 */
		  	            os.write(bs, 0, length);
		  	            System.out.println(length);
		  	          } 
  			         // 完毕，关闭所有链接   
  			         os.close();  
  			         is.close(); 
  			         System.out.println("下载成功！");
  			} catch (Exception e) {
  			        e.printStackTrace();
  			}
            return true;
  		}
  	}
	//end 异步下载文件方法====================================================================
	
	  /* 显示Dialog的method ,暂时没有使用*/
    private void showDialog(String mess)
    {
      new AlertDialog.Builder(MainActivity.this).setTitle("上传结果")
       .setMessage(mess)
       .setNegativeButton("确定",new DialogInterface.OnClickListener()
       {
         public void onClick(DialogInterface dialog, int which)
         {          
         }
       })
       .show();
    }
	
	//============================================================================
	/* 以下是金格demo原代码，用于打开pdf文档
	 * 顺便说一下，整理过后的项目，主要有三个包：
	 * 1.上传下载包com.jingye.download;
	 * 2.金格手签包com.jingye.signature;
	 * 3.用户登录包com.jingye.user;
	 * 佳荣你主要负责第1个包和第3个包，第2个包我来负责。
	 * 因为金格源码还没有对我们公开，所以我目前在做第1个包，随后我会交给你。*/
    private void doOpenFile(String filepath) {
		File file = new File(filepath);
		final Uri uri = Uri.fromFile(file);
		
		Log.v(TAG, "uri = " + uri.getPath());
		final Intent intent = new Intent("android.intent.action.VIEW", uri);
		/*intent.setClassName("com.kinggrid.iapppdf.demo",
				"com.kinggrid.iapppdf.demo.BookShower");*/
		//注意：此处跳转到金格手签包
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
//		 //isSupportImmediatelyGetVector根据需要设置成true或false,此参设为true时矢量签批保存速度比较快
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
