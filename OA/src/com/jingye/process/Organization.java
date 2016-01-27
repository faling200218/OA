package com.jingye.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jingye.download.DownloadActivity;
import com.jingye.download.DownloadAdapter;
import com.jingye.user.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class Organization extends Activity{

	private static final String TAG = "Organization";
	private ListView addressList;
	private OrganizationAdapter adapter;
	private String str;
	List<HashMap<String, String>> listmap;
	HashMap<String, String> map = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_list);
		
		getjson();
	}

	// 根据接口得到json数据====================================
	@SuppressWarnings("unchecked")
	public void getjson(){
		FinalHttp fh = new FinalHttp(); 
	     		try {
	     			fh.get( "http://61.182.203.110:8888/?requestflag=listfiles",
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
	     										map.put("filename", jo.getString("filename"));
	     										// 将解析出来的数据加入到listmap表中
	     										listmap.add(map);
	     										Log.v(TAG,jo.getString("filename"));
	     										System.out.println("listmap-->>" + listmap);
	     									}
	     									System.out.println("listmap1-->>" + listmap);
	     								} else {
	     									Log.v(TAG,"data无值,为=" + data);
	     								}
	     							} catch (JSONException e) {
	     								e.printStackTrace();
	     							}
	     							// ++++++++++++解析结束++++++++++++++++++++++++++++++++
	     							addressList = (ListView) findViewById(R.id.lv_address_list);
	     				            adapter = new OrganizationAdapter(Organization.this,listmap);
	     				            addressList.setAdapter(adapter);
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
	// end 根据接口得到json数据=================================================
	
}
