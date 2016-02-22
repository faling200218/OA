package com.jingye.template;

import java.util.ArrayList;
import java.util.HashMap;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jingye.process.U;
import com.jingye.user.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TemplateDetail extends Activity{

	private static final String TAG = "TemplateDetail";
	private TextView nameTextView;
	private TextView signerTextView;
	private String gidString;
	private String nameString;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.template_detail);
		getjson();
		initView();
	}
	
	// 获取json数据====================================
	@SuppressWarnings("unchecked")
	public void getjson(){
		Intent intent = getIntent();
		gidString = intent.getStringExtra("gid");
		Log.v(TAG,"gidString========="+gidString);
		nameString = intent.getStringExtra("groupname");
		System.out.println("gid=========="+gidString);
		FinalHttp fh = new FinalHttp(); 
	     		try {
	     			fh.get( "http://61.182.203.110:8888/?Requestflag=listmbgroupspr&gid="+gidString,
	     					new AjaxCallBack() {
	     						@Override
	     						public void onStart() {
	     							super.onStart();
	     							U.toast(TemplateDetail.this,"开始请求");
	     						}

	     						@Override
	     						public void onLoading(long count, long current) {
	     							super.onLoading(count, current);
	     							//Log.showLoadingDialog(DownloadActivity.this);
	     						}

	     						@Override
	     						public void onSuccess(Object result) {
	     							// 访问服务器成功，获得访问结果result
	     							String str = (String) result;
	     							Log.v(TAG,"请求结果(json串)" + result);
	     							Log.v(TAG,"str====" + str);
	     							// ++++++++++++解析数据++++++++++++++++++++++++++++++
	     							try {
	     								//listmap = new ArrayList<HashMap<String, String>>();
	     								JSONObject jsonObject = new JSONObject(str);

	     								String data = jsonObject.getString("data");

	     								JSONArray jsonArray = new JSONArray(data);
	     								if (jsonArray.length() > 0) {
	     									String shenpirenString = "";
	     									for (int i = 0; i < jsonArray.length(); i++) {
	     										//map = new HashMap<String, String>();
	     										JSONObject jo = jsonArray
	     												.getJSONObject(i);
	     										Log.v(TAG,"i===" + i);
	     										Log.v(TAG,"jo==" + jo);
	     										//map.put("gid", jo.getString("gid"));
	     										shenpirenString +=jo.getString("shenpiren")+"——";
	     										// 将解析出来的款台号加入到动态数组中
	     										//listmap.add(map);
	     										//Log.v(TAG,jo.getString("groupname"));
	     									}
	     									nameTextView.setText(nameString);
     										signerTextView.setText(shenpirenString);
	     								} else {
	     									Log.v(TAG,"data无值,为=" + data);
	     								}
	     							} catch (JSONException e) {
	     								e.printStackTrace();
	     							}
	     							// ++++++++++++解析结束++++++++++++++++++++++++++++++++
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
	
	public void initView(){
		
		nameTextView = (TextView)findViewById(R.id.tv_name);
		signerTextView = (TextView)findViewById(R.id.tv_signer);
	}

	
}
