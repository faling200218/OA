package com.jingye.process;

import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.jingye.user.R;

/**
 * com.kinggrid.iapppdf.demo.BookAdapter
 * @author 涂博之 <br/>
 * create at 2014年5月19日 下午3:45:39
 */
public class OrganizationAdapter extends BaseAdapter {
  private static final String TAG = "OrganizationAdapter";
  //private Handler mHandler;
  private ViewHolder holder;
  protected List<HashMap<String, String>> fileData;
  private LayoutInflater inflater;
  protected  class ViewHolder {
    protected TextView organizationName;
    //protected CheckBox cbfile;
/*    protected TextView fileSize;*/
  }
  
  @SuppressLint("UseSparseArrays")
public OrganizationAdapter(Context context,List<HashMap<String, String>> fileData){
    this.inflater = LayoutInflater.from(context);
    //this.mHandler = mHandler;
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
      convertView = inflater.inflate(R.layout.address_list_item, null);
      
      holder.organizationName = (TextView)convertView.findViewById(R.id.organization_name);
      holder.organizationName.setTextSize(22.0f);
      //holder.cbfile = (CheckBox)convertView.findViewById(R.id.checkBox_file);
/*    holder.fileSize = (TextView)convertView.findViewById(R.id.file_size);
      holder.fileSize.setTextSize(16.0f);*/
      convertView.setTag(holder);
    }else{
      holder = (ViewHolder) convertView.getTag();
    }
    //这一句比较长，只是控制文件名的显示，拼接字符串
    holder.organizationName.setText(fileData.get(position).get(
        "filename").substring(0,fileData.get(position).get("filename").lastIndexOf(".")).length()>11?fileData.get(position).get("filename").substring(0, 11)+"...":fileData.get(position).get("filename").substring(0, fileData.get(position).get("filename").lastIndexOf(".")));
    //holder.cbfile.setTag(position);   //复选框标签和文件名位置序号一致，这样后面就可以根据选中复选框来得到文件名
    //holder.cbfile.setChecked(getIsSelected().get(position));
    //holder.cbfile.setOnCheckedChangeListener(new CheckBoxChangedListener());
    return convertView;
  }
  
    //CheckBox选择改变监听器
	/*private final class CheckBoxChangedListener implements
			OnCheckedChangeListener {
		public void onCheckedChanged(CompoundButton cb, boolean flag) {
			int position = (Integer) cb.getTag();
			String filename ="";
			if(flag){
				filename = fileData.get(position).get("filename");
				mHandler.sendMessage(mHandler.obtainMessage(10, filename));  //将文件名发送到主界面
			}else {
				filename = fileData.get(position).get("filename");
				mHandler.sendMessage(mHandler.obtainMessage(11, filename));
			}

		}
	}*/
  
	
}


