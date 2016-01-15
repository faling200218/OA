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
  private static HashMap<Integer, Boolean> isSelected;
  private String filename;
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
    isSelected = new HashMap<Integer, Boolean>();
	//initDate();
  }
  
  //public void setFileData(List<HashMap<String, String>> fileData){
  //  this.fileData = fileData;
  //}
  
//初始化isSelected的数据
	private void initDate() {
		for (int i = 0; i < fileData.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
		BookAdapter.isSelected = isSelected;
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
			getIsSelected().put(position, flag);
			filename = fileData.get(position).get("filename");
			mHandler.sendMessage(mHandler.obtainMessage(10, filename));
			// 如果所有的物品全部被选中，则全选按钮也默认被选中
			//mHandler.sendMessage(mHandler.obtainMessage(11, isAllSelected()));
		}
	}
  
	/*private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) { // 更改商品数量
				//((TextView) msg.obj).setText(String.valueOf(number));
				// 更改商品数量后，通知Activity更新需要付费的总金额
				mHandler.sendMessage(mHandler
						.obtainMessage(10, filename));
			}
		}
	};*/
	
}
