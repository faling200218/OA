package com.jingye.download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.exception.AfinalException;
import net.tsz.afinal.http.AjaxCallBack;

import com.jingye.user.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

public class DownloadActivity extends Activity{
	private static final String TAG = "DownloadActivity";
	private ListView bookList;
	private DownloadAdapter adapter;
	private String str;
	List<HashMap<String, String>> listmap;
	HashMap<String, String> map = null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down);
        
        getjson();
	}
	
	// 获取json数据====================================
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
	     										// 将解析出来的款台号加入到动态数组中
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
	     							bookList = (ListView) findViewById(R.id.lv_download);
	     				            adapter = new DownloadAdapter(DownloadActivity.this,listmap);
	     				    		bookList.setAdapter(adapter);
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
