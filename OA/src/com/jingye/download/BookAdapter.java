/*
 * BookAdapter.java
 * classes : com.kinggrid.iapppdf.demo.BookAdapter
 * @author 涂博之
 * V 1.0.0
 * Create at 2014年5月19日 下午3:45:39
 */
package com.jingye.download;

import java.util.HashMap;
import java.util.List;

import com.jingye.user.R;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * com.kinggrid.iapppdf.demo.BookAdapter
 * @author 涂博之 <br/>
 * create at 2014年5月19日 下午3:45:39
 */
public class BookAdapter extends BaseAdapter {
  private static final String TAG = "BookAdapter";
  private Handler mHandler;
  private ViewHolder holder;
  protected List<HashMap<String, String>> fileData;
  private LayoutInflater inflater;
  protected  class ViewHolder {
    protected TextView fileName;
    protected CheckBox cbfile;
/*    protected TextView fileSize;*/
  }
  
  @SuppressLint("UseSparseArrays")
public BookAdapter(Context context,Handler mHandler,List<HashMap<String, String>> fileData){
    this.inflater = LayoutInflater.from(context);
    this.mHandler = mHandler;
    this.fileData = fileData;
  }

  /* （非 Javadoc）
   * @see android.widget.Adapter#getCount()
   */
  public int getCount() {
    return fileData.size();
  }

  /* （非 Javadoc）
   * @see android.widget.Adapter#getItem(int)
   */
  public Object getItem(int position) {
    return fileData.get(position);
  }

  /* （非 Javadoc）
   * @see android.widget.Adapter#getItemId(int)
   */
  public long getItemId(int position) {
    return position;
  }

  /* （非 Javadoc）
   * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
   */
  public View getView(int position, View convertView, ViewGroup parent) {
    if(convertView == null){
      holder = new ViewHolder();
      convertView = inflater.inflate(R.layout.list_item, null);
      
      holder.fileName = (TextView)convertView.findViewById(R.id.file_name);
      holder.fileName.setTextSize(22.0f);
      holder.cbfile = (CheckBox)convertView.findViewById(R.id.checkBox_file);
/*    holder.fileSize = (TextView)convertView.findViewById(R.id.file_size);
      holder.fileSize.setTextSize(16.0f);*/
      convertView.setTag(holder);
    }else{
      holder = (ViewHolder) convertView.getTag();
    }
    
    holder.fileName.setText(fileData.get(position).get(
        "filename").substring(0,fileData.get(position).get("filename").lastIndexOf(".")).length()>13?fileData.get(position).get("filename").substring(0, 13)+"...":fileData.get(position).get("filename").substring(0, fileData.get(position).get("filename").lastIndexOf(".")));
    holder.cbfile.setTag(position);
    //holder.cbfile.setChecked(getIsSelected().get(position));
    holder.cbfile.setOnCheckedChangeListener(new CheckBoxChangedListener());
    return convertView;
  }
  
    //CheckBox选择改变监听器
	private final class CheckBoxChangedListener implements
			OnCheckedChangeListener {
		public void onCheckedChanged(CompoundButton cb, boolean flag) {
			int position = (Integer) cb.getTag();
			String filename ="";
			if(flag){
				filename = fileData.get(position).get("filename");
				mHandler.sendMessage(mHandler.obtainMessage(10, filename));
			}else {
				filename = fileData.get(position).get("filename");
				mHandler.sendMessage(mHandler.obtainMessage(11, filename));
			}

		}
	}
  
	
}
