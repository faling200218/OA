package com.jingye.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Loader.ForceLoadContentObserver;
import android.os.Handler;
import android.text.StaticLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingye.user.R;

/**
 * com.kinggrid.iapppdf.demo.BookAdapter
 * @author 涂博之 <br/>
 * create at 2014年5月19日 下午3:45:39
 */
public class OrganizationAdapter extends BaseAdapter {
  private static final String TAG = "OrganizationAdapter";
  private Handler mHandler;
  private ViewHolder holder;
  protected List<HashMap<String, String>> fileData = new ArrayList<HashMap<String,String>>();
  private HashMap<Integer, Boolean> selectCheck = new HashMap<Integer,Boolean>();
  private LayoutInflater inflater;
  private class ViewHolder {
    private TextView organizationName;
    private ImageView imgseparator;
    protected CheckBox cbLeader;
    private ImageView imgGroup;
/*    protected TextView fileSize;*/
  }
  
  @SuppressLint("UseSparseArrays")
public OrganizationAdapter(Context context,Handler mHandler,List<HashMap<String, String>> fileData){
    this.inflater = LayoutInflater.from(context);
    this.mHandler = mHandler;
    this.fileData = fileData;
    for(int i=0;i<fileData.size();i++){
    	selectCheck.put(i, false);
    }
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
      convertView = inflater.inflate(R.layout.address_list_item, null);
      
      holder.organizationName = (TextView)convertView.findViewById(R.id.organization_name);
      holder.organizationName.setTextSize(22.0f);
      holder.imgseparator = (ImageView)convertView.findViewById(R.id.separator);
      holder.cbLeader = (CheckBox)convertView.findViewById(R.id.checkBox_leader);
      holder.imgGroup = (ImageView)convertView.findViewById(R.id.imgGroup);
/*    holder.fileSize = (TextView)convertView.findViewById(R.id.file_size);
      holder.fileSize.setTextSize(16.0f);*/
      convertView.setTag(holder);
    }else{
      holder = (ViewHolder) convertView.getTag();
    }
	//这一句比较长，只是控制文件名的显示，拼接字符串
    holder.organizationName.setText(fileData.get(position).get(
        "fsystem").toString());
    if(fileData.get(position).get("lx").toString().equals("2")){
    	holder.imgseparator.setVisibility(View.INVISIBLE);
    	holder.imgGroup.setVisibility(View.INVISIBLE);
    	holder.cbLeader.setVisibility(View.VISIBLE);
    }else {
    	holder.imgseparator.setVisibility(View.VISIBLE);
    	holder.imgGroup.setVisibility(View.VISIBLE);
		holder.cbLeader.setVisibility(View.INVISIBLE);
	}
    holder.cbLeader.setTag(position);   //复选框标签和文件名位置序号一致，这样后面就可以根据选中复选框来得到文件名
    holder.cbLeader.setChecked(selectCheck.get(position));
    holder.cbLeader.setOnCheckedChangeListener(new CheckBoxChangedListener());
    return convertView;
  }
  
    //CheckBox选择改变监听器
	private final class CheckBoxChangedListener implements
			OnCheckedChangeListener {
		public void onCheckedChanged(CompoundButton cb, boolean flag) {
			int position = (Integer) cb.getTag();
			selectCheck.put(position, flag);
			Log.v(TAG,"位置："+position);
			Log.v(TAG, selectCheck.toString());
			String leadername ="";
			String leaderid ="";
			if(flag){
				leadername = fileData.get(position).get("fsystem");
				leaderid = fileData.get(position).get("fnumber");
				System.out.println("id:"+leaderid);
				Log.v(TAG, leadername);
				mHandler.sendMessage(mHandler.obtainMessage(10, leadername)); 
				mHandler.sendMessage(mHandler.obtainMessage(12, leaderid));//将文件名发送到主界面
			}else {
				leadername = fileData.get(position).get("fsystem");
				leaderid = fileData.get(position).get("fnumber");
				mHandler.sendMessage(mHandler.obtainMessage(11, leadername));
				mHandler.sendMessage(mHandler.obtainMessage(13, leaderid));
			}
			
			
		}
	}
  
	
}


