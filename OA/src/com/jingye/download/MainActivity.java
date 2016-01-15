package com.jingye.download;

import java.io.File;
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
import com.jingye.user.R;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private TextView tvTemp;
	private ListView bookList;
	private Button btn_download;
	private Button btn_upload;
	private BookAdapter adapter;
	private List<HashMap<String, String>> fileData;
	private final static String SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath().toString()+"/敬业文件/";
	private String actionUrl = "http://61.182.201.194:3931/CustomerSatisfaction/?requestflag=downfiles01&fid=28";
	//private String actionUrl = "http://61.182.203.110:8999/JingYeYunService/download.aspx";
	StringBuffer sb;
	protected int local_flag = LOCAL_FILE;
	private final static int WEB_FILE = 0;
	private final static int LOCAL_FILE = 1;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 10) { // 更改选中商品的总价格
				String price = (String) msg.obj;
				if (price !=null) {
					tvTemp.setText("文件名：" + price);
					// layout.setVisibility(View.VISIBLE);
				} else {
					// layout.setVisibility(View.GONE);
					tvTemp.setText("传值失败！");
				}
			}
		}
	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileData = getData(SDCARD_PATH);
        tvTemp = (TextView)findViewById(R.id.tv_temp);
        //上传
        btn_upload = (Button)findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(new OnClickListener() {

 			public void onClick(View v) {

 				/*String fileName = fileData.get(position).get("filename");
 				String filePath = Environment.getExternalStorageDirectory()
 						+ "/敬业文件/" + fileData.get(position).get("filename");
 		        Log.v(TAG,fileName);
 		        Uri uri = Uri.parse(filePath);
 				Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
 				intent.setAction(Intent.ACTION_VIEW);
 				intent.setData(uri);
 				intent.putExtra("filename", fileName);
 				startActivity(intent);
 				Log.v(TAG,"pdf上传成功！");*/
 			}
 		});
		
        
        //下载
        btn_download = (Button)findViewById(R.id.btn_download);
        btn_download.setOnClickListener(new OnClickListener() {

 			public void onClick(View v) {

 				downLoadFile();
 				Log.v(TAG,"pdf下载成功！");
 			}
 		});
        
        bookList = (ListView) findViewById(R.id.book_directory);
        adapter = new BookAdapter(this,handler,fileData);
		
		Log.v(TAG, SDCARD_PATH);
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
						+ "/敬业文件/" + fileData.get(position).get("filename");
				doOpenFile(fileName);
				
			}
		});  
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
	
	//异步下载文件
	public class Asy_downloadfile extends AsyncTask<Object, Object, Object> {
  		@Override
  		protected Object doInBackground(Object... params) {
  			
  			// 获得存储卡路径，构成 保存文件的目标路径
  			String dirName = "";
  			dirName = Environment.getExternalStorageDirectory()+"/敬业文件/";
  			File f = new File(dirName);
  			if(!f.exists())
  			{
  			    f.mkdir();
  			}

  			try {
  			         // 构造URL   
  			         URL url = new URL(actionUrl);   
	  	  	          HttpURLConnection con=(HttpURLConnection)url.openConnection();
	  	  	          /* 允许Input、Output，不使用Cache */
	  	  	          con.setDoInput(true);
	  	  	          con.setDoOutput(true);
	  	  	          con.setUseCaches(false);
  			         //获得文件的长度
  			         int contentLength = con.getContentLength();
  			         System.out.println("长度 :"+contentLength); 
  			         String nnString = URLDecoder.decode(con.getHeaderField("Content-Disposition"),"UTF-8");
  			        //准备拼接新的文件名（保存在存储卡后的文件名）
  			         String newFilename = dirName +nnString.substring(nnString.indexOf("=")+1,nnString.length()); 
  		  			File file = new File(newFilename);
  		  			//如果目标文件已经存在，则删除。产生覆盖旧文件的效果
  		  			if(file.exists())
  		  			{
  		  			    file.delete();
  		  			}
  			         System.out.println("++++++++++++++++"+newFilename);
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
  			            
  			} catch (Exception e) {
  			        e.printStackTrace();
  			}
            return Log.v(TAG,"下载成功！");
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
