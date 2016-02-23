package com.jingye.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jingye.main.MyApplication;
import com.jingye.user.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ProcessTemplate extends Activity{

	private static final String TAG = "ProcessTemplate";
	private ListView templateListView;
	private TemplateAdapter adapter;
	private String str;
	List<HashMap<String, String>> listmap;
	HashMap<String, String> map = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_template);
		getjson();
	}
	
	// 获取json数据====================================
		@SuppressWarnings("unchecked")
		public void getjson(){
			MyApplication app = (MyApplication)getApplication();
			String urlString = app.getName()+"Requestflag=listmbgroup&groupstype=1&groupcreatetype=2&createuser=029848";
			FinalHttp fh = new FinalHttp(); 
		     		try {
		     			fh.get( urlString,
		     					new AjaxCallBack() {
		     						@Override
		     						public void onStart() {
		     							super.onStart();
		     							Log.v(TAG,"开始请求");
		     						}

		     						@Override
		     						public void onLoading(long count, long current) {
		     							super.onLoading(count, current);
		     							//Log.showLoadingDialog(DownloadActivity.this);
		     						}

		     						@Override
		     						public void onSuccess(Object result) {
		     							// 访问服务器成功，获得访问结果result
		     							str = (String) result;
		     							Log.v(TAG,"请求结果(json串)" + result);
		     							Log.v(TAG,"str====" + str);
		     							// ++++++++++++解析数据++++++++++++++++++++++++++++++
		     							try {
		     								listmap = new ArrayList<HashMap<String, String>>();
		     								JSONObject jsonObject = new JSONObject(str);

		     								String data = jsonObject.getString("data");

		     								JSONArray jsonArray = new JSONArray(data);
		     								if (jsonArray.length() > 0) {
		     									for (int i = 0; i < jsonArray.length(); i++) {
		     										map = new HashMap<String, String>();
		     										JSONObject jo = jsonArray
		     												.getJSONObject(i);
		     										Log.v(TAG,"i===" + i);
		     										Log.v(TAG,"jo==" + jo);
		     										map.put("gid", jo.getString("gid"));
		     										map.put("groupname", jo.getString("groupname"));
		     										// 将解析出来的款台号加入到动态数组中
		     										listmap.add(map);
		     										Log.v(TAG,jo.getString("groupname"));
		     										System.out.println("listmap-->>" + listmap);
		     									}
		     								} else {
		     									Log.v(TAG,"data无值,为=" + data);
		     								}
		     							} catch (JSONException e) {
		     								e.printStackTrace();
		     							}
		     							// ++++++++++++解析结束++++++++++++++++++++++++++++++++
		     							templateListView = (ListView) findViewById(R.id.lv_template);
		     				            adapter = new TemplateAdapter(ProcessTemplate.this,listmap);
		     				            templateListView.setAdapter(adapter);
		     				            //listview item点击事件
		     				            templateListView.setOnItemClickListener(new OnItemClickListener() {

		     				   			public void onItemClick(AdapterView<?> parent, View view,
		     				   					int position, long id) {

		     				   				if(!TextUtils.isEmpty(listmap.get(position).get("gid"))){
			     				   				Intent intent = new Intent(ProcessTemplate.this,TemplateDetail.class);
			     				   				Log.v(TAG,"gid++++=========="+listmap.get(position).get("gid").toString());
			     				   				intent.putExtra("gid", listmap.get(position).get("gid").toString());
			     				   				intent.putExtra("groupname", listmap.get(position).get("groupname").toString());
			     				   				startActivity(intent);
		     				   				}	
		     				   			}
		     				   		});
		     						}

		     						@Override
		     						public void onFailure(Throwable t, int errorNo,
		     								String strMsg) {
		     							super.onFailure(t, errorNo, strMsg);
		     							Log.v(TAG,"获取数据失败！"+strMsg);
		     						}
		     					});

		     		} catch (Exception e) {
		     			e.printStackTrace();
		     		}
		}
		// end 获取json数据=================================================

}
