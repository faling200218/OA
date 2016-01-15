package com.jingye.upload;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.jingye.user.R;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/***
 * ��Ŀ��ElectronicSignature
 * ������FileActivity
 * ���ܣ�ʵ�ֶ��ֻ��ļ�Ŀ¼����ʾ������
 * ����ʱ�䣺2013-12-10
 * �����ˣ�LXH
 */
public class FileActivity extends ListActivity{
	/**
	 * Ҫ��ʾ���ļ���
	 */
	private ArrayList<String> items;
	/**
	 * ��ʾ�ļ�·��
	 */
	private ArrayList<String> paths;
	/**
	 * ��Ŀ¼
	 */
	private String rootPath = "/";
	private TextView ram_total_tv,sd_total_tv,ram_available_tv,sd_available_tv;
	private MemoryInfo memoryInfo;
	private Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//��ȥ��������Ӧ�ó�������֣� 
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//����һ��Activity����ʾ����
		setContentView(R.layout.file_layout);
		ram_total_tv = (TextView) findViewById(R.id.ram_total_tv);
		sd_total_tv = (TextView) findViewById(R.id.sd_total_tv);
		ram_available_tv = (TextView) findViewById(R.id.ram_available_tv);
		sd_available_tv = (TextView) findViewById(R.id.sd_available_tv);
		getFileDir(rootPath);// ��ȡ�ļ��б�
		/*��̬�����ڴ���Ϣ*/
		handler = new Handler();
		handler.post(thread);
		
	}
	
	Runnable thread = new Runnable() {
		
		public void run() {
			// TODO Auto-generated method stub
			handler.postDelayed(thread, 1000*3);
			getMemoryInfo();//��ȡ�洢��Ϣ
		}
	};
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(thread);
	};
	// List��item�ĵ���¼�
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		File f = new File(paths.get(position));
		
		if (f.isDirectory()) {
		//�ж��ļ����ļ����Ƿ�ɶ������ɶ���ʾ���ɶ�������һĿ¼
			if(f.canRead()){
				getFileDir(paths.get(position));
				}else{
					Toast.makeText(this, "Ȩ�޲��㣡", Toast.LENGTH_SHORT).show();
				}
		}else{
			if(f.canRead()){
				//if((f.getName()).contains(".pdf")){
				//	Toast.makeText(this, "���ڽ���", Toast.LENGTH_SHORT).show();
					String openFile = paths.get(position);
					Uri uri = Uri.parse(openFile);
					Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
					intent.setAction(Intent.ACTION_VIEW);
					intent.setData(uri);
					intent.putExtra("filename", f.getName());
					startActivity(intent);
				//}else{
				//	Toast.makeText(this, "��ѡ��PDF�ļ�", Toast.LENGTH_SHORT).show();
				//}
			}else{
				Toast.makeText(this, "���ɶ��ļ���", Toast.LENGTH_SHORT).show();
			}
		}
	}
	private void getFileDir(String path) {
		// TODO Auto-generated method stub
		//showXPath.setText(path);//��ʾ��ǰ·��
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		
		File presentFile = new  File(path);
		File[] files = presentFile.listFiles();
		// ��ʾ��ǰ·���������ظ�Ŀ¼
		items.add("��ǰ·����"+path+" �ļ�������"+files.length);
		paths.add(rootPath);
		if (! path.equals(rootPath)) {
			// ������һ��Ŀ¼
			items.add("Back");
			paths.add(presentFile.getParent());
		}
		// ��ӵ�ǰ·���µ����е��ļ�����·��
		for (File f : files) {
			items.add(f.getName());
			paths.add(f.getPath());
		}
		// �����б�������
		setListAdapter(new FileListAdapter(FileActivity.this, items, paths));
	}
	private void getMemoryInfo(){
		memoryInfo = new MemoryInfo();
		
		float long1 = memoryInfo.getAvailableInternalMemorySize(); //�ڲ����ô洢�ռ�  
		float long2 = memoryInfo.getTotalInternalMemorySize();  //�ڲ��ܹ��洢�ռ�
		float long3 = memoryInfo.getAvailableExternalMemorySize();  //�ⲿ���ô洢�ռ�
		float long4 = memoryInfo.getTotalExternalMemorySize(); //�ⲿ�ܹ��洢�ռ� 
		if(long2/(1024*1024) >= 1024){
			BigDecimal b2 = new BigDecimal(long2/(1024*1024*1024));
			long2 = b2.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//�����������룬������λС�� 
			ram_total_tv.setText(long2+"G");
		}else{
			BigDecimal b2 = new BigDecimal(long2/(1024*1024));
			long2 = b2.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//�����������룬������λС�� 
			ram_total_tv.setText(long2+"M");
		}
		if (long4/(1024*1024) >= 1024) {
			BigDecimal b4 = new BigDecimal(long4/(1024*1024*1024));
			long4 = b4.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//�����������룬������λС�� 
			sd_total_tv.setText(long4+"G") ;
		}else{
			BigDecimal b4 = new BigDecimal(long2/(1024*1024));
			long4 = b4.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//�����������룬������λС�� 
			sd_total_tv.setText(long4+"M") ;
		}
		if(long1/(1024*1024) >= 1024){
			BigDecimal b1 = new BigDecimal(long1/(1024*1024*1024));
			long1 = b1.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//�����������룬������λС�� 
			ram_available_tv.setText(long1+"G") ;
		}else{
			BigDecimal b1 = new BigDecimal(long2/(1024*1024));
			long1 = b1.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//�����������룬������λС�� 
			ram_available_tv.setText(long1+"M") ;
		}
		if(long3/(1024*1024) >= 0){
			BigDecimal b3 = new BigDecimal(long3/(1024*1024*1024));
			long3 = b3.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//�����������룬������λС�� 
			sd_available_tv.setText(long3+"G");
		}else{
			BigDecimal b3 = new BigDecimal(long2/(1024*1024));
			long3 = b3.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();//�����������룬������λС�� 
			sd_available_tv.setText(long3+"M") ;
		}
	}
	
	@Override
	public void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
	}
}
