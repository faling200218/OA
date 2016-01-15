package com.jingye.upload;

import java.io.File;
import java.util.List;

import com.jingye.user.R;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/***
 * ��Ŀ��ElectronicSignature
 * ������FileListAdapter
 * ���ܣ��ļ�Ŀ¼������
 * ����ʱ�䣺2013-12-10
 * �����ˣ�LXH
 */
public class FileListAdapter extends BaseAdapter{

	private List<String> items;
	private List<String> paths;
	private LayoutInflater inflater;//��������XML�ļ�����
	private Bitmap rootIcon;
	private Bitmap prevIcon;
	private Bitmap docIcon;
	private Bitmap NotReaddocIcon;
	private Bitmap folderIcon;
	private Bitmap NotReadfolderIcon;
	private Bitmap PdfFileIcon;
	private Bitmap APKFileIcon;
	private Bitmap MP3FileIcon;
	private Bitmap PicFileIcon;
	private Bitmap wordFileIcon;
	public FileListAdapter(Context context, List<String> items,
			List<String> paths) {
		// ��context��ȡһ�����ּ�����
		inflater = LayoutInflater.from(context);
		this.items = items;
		this.paths = paths;
		// ��ʼ������ͼ��
		rootIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.sym_keyboard_shift);
		prevIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.sym_keyboard_return);//������һ���˵�
		docIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.ic_gesturebuilder);//��ʾ�ı��ļ�
		NotReaddocIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.ic_gesturebuilder_notread);//��ʾ�ı��ļ�
		folderIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.live_folder_notes);//��ʾ�ļ���
		NotReadfolderIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.live_folder_cnntread);//��ʾ���ɶ��ļ���
		PdfFileIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.ico_pdf);
		APKFileIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.apk_icon);
		MP3FileIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.mp3);
		PicFileIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.picture);
		wordFileIcon = BitmapFactory.decodeResource(context.getResources(), 
				R.drawable.ico_word);
	}
	public int getCount() {
		// �������ݵļ�¼����
		return items.size();
	}

	public Object getItem(int position) {
		// �����б��е�����
		return items.get(position);
	}

	public long getItemId(int position) {
		// ����item��idֵ
		return position;
	}
	/* ��ȡListView�е�item�������*/
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null ;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.file_list_item_layout, null);
			// �������������
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.fileNameText);
			holder.imageIcon = (ImageView) convertView.findViewById(R.id.imageIcon);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		String fileName = items.get(position);
		getFileIco(holder,fileName,position);
		holder.text.setText(items.get(position));
		return convertView;
	}
	
	/**
	 * ���ܣ��ж��ļ����ͣ���������ӦͼƬ
	 */
	private void getFileIco(ViewHolder holder,String fileName,int position) {
		//String end = fileName.substring(fileName.indexOf(".") + 1).toLowerCase();
		File f = new File(paths.get(position));
		//���f��ʾ����һ��Ŀ¼�򷵻�true
		if(f.isDirectory()){
			if(fileName.contains("��ǰ·��")){
				holder.imageIcon.setImageBitmap(rootIcon); 
			}else if(fileName.equals("Back")){
		    	holder.imageIcon.setImageBitmap(prevIcon); 
		    }else if(f.canRead()){
				holder.imageIcon.setImageBitmap(folderIcon); 					
			}else{
				holder.imageIcon.setImageBitmap(NotReadfolderIcon); 					
			}
		}else{
			if(f.canRead()){
				//�ж��ļ�����
				if(fileName.contains(".pdf")){
					holder.imageIcon.setImageBitmap(PdfFileIcon);
				}else if(fileName.contains(".doc")||fileName.contains(".docx")){
					holder.imageIcon.setImageBitmap(wordFileIcon);
				}else if(fileName.contains(".apk")){
					holder.imageIcon.setImageBitmap(APKFileIcon);
				}else if(fileName.contains(".mp3") || fileName.contains(".ape") || fileName.contains(".flac")){
					holder.imageIcon.setImageBitmap(MP3FileIcon);
				}else if(fileName.contains(".png") || fileName.contains(".jpg") || fileName.contains(".bmp")){
					holder.imageIcon.setImageBitmap(PicFileIcon);
				}else{
					holder.imageIcon.setImageBitmap(docIcon); 
				}						
			}else{
				holder.imageIcon.setImageBitmap(NotReaddocIcon);			
			}
		}

	}
	/***
	 * ��Ŀ��ElectronicSignature
	 * ������ViewHolder
	 * ���ܣ����ڴ�ſؼ����ڲ�������
	 * ����ʱ�䣺2013-12-10
	 * �����ˣ�LXH
	 */
	private class ViewHolder {
		TextView text;
		ImageView imageIcon;
	}
}
