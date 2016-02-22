package com.jingye.process;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jingye.download.DownloadActivity;
import com.jingye.download.DownloadAdapter;
import com.jingye.user.R;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Organization extends Activity{
    private String strWhere = "wherestr=";     //查询条件
    private String strFloor = "";     //查询层数
	private static final String TAG = "Organization";
	private ListView addressList;
	private OrganizationAdapter adapter;
	private String str;
	List<HashMap<String, String>> listmap;
	HashMap<String, String> map = null;
	private Button btn_select;
	private String processidString="";
	private String backUrl="";
	
	//将适配器页面传过来的领导名字存起来
	ArrayList<String> leaders = new ArrayList<String>();
	ArrayList<String> leaderList = new ArrayList<String>();
	
	//接收适配器页面传送过来的文件名=============================================
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String leaderName ="";
			String leadersid = "";
			if (msg.what == 10) {         //适配器页面复选框选中时通知主界面将选中名字添加进集合
				leaderName = (String) msg.obj;
				leaders.add(leaderName);
				Log.v(TAG,"添加审批人员："+leaders.toString());
			}else if(msg.what == 11){     //适配器页面复选框取消选中时通知主界面将反选中名字从集合中移除
				leaderName = (String) msg.obj;
				leaders.remove(leaderName);
				Log.v(TAG,"减去审批人员："+leaders.toString());
			}else if(msg.what == 12){     //适配器页面复选框取消选中时通知主界面将反选中名字从集合中移除
				leadersid = (String) msg.obj;
				leaderList.add(leadersid);
				Log.v(TAG,"添加审批人员id："+leaderList.toString());
			}else if(msg.what == 13){     //适配器页面复选框取消选中时通知主界面将反选中名字从集合中移除
				leadersid = (String) msg.obj;
				leaderList.remove(leadersid);
				Log.v(TAG,"减去审批人员id："+leaderList.toString());
			}
		}
	};
	//end 接收适配器页面传送过来的文件名===========================================

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_list);
		
		String strUrl = "http://61.182.203.110:8888/?Requestflag=newprocess";
		getjson(strUrl);
		findview();
	}

	@Override
	public void onBackPressed() {
//		if (currentLevel == LEVEL_COUNTY) {
//		   queryCities();
//		} else if (currentLevel == LEVEL_CITY) {
//		   queryProvinces();
//		} else {
//		   finish();
//		}
		
		strWhere = strWhere.substring(0,strWhere.length()-1);  //去掉一个"！"
		System.out.println("========="+strWhere);
		if(strWhere.contains("!")){
			strWhere = strWhere.substring(0,strWhere.lastIndexOf("!")+1);  //前面去掉了一个，就成了截取倒数第二个！前面的串
			int i = count(strWhere);
			Log.v(TAG, "层参数："+i);
		    strFloor = "floorn="+i;
		    backUrl="http://61.182.203.110:8888/?Requestflag=Lookingsomeone";
		    backUrl += "&"+strFloor+"&"+strWhere;
		    Log.v(TAG+"返回键得到的：",backUrl);
			getjson(backUrl);
		}else if(!strWhere.contains("!")&&!strWhere.equals("wherestr")){
			backUrl = "http://61.182.203.110:8888/?Requestflag=newprocess";
			strWhere="wherestr=";    //重置查询条件
			Log.v(TAG+"返回键得到的：",backUrl);
			getjson(backUrl);
		} 
		else if (strWhere.equals("wherestr")) {
			finish();
		}
		
	}
	
	public void findview(){
		btn_select=(Button)findViewById(R.id.btn_select);
		btn_select.setOnClickListener(new OnClickListener() {

 			public void onClick(View v) {
 				Intent intent = new Intent(Organization.this,CreatProcess.class);
 				intent.putStringArrayListExtra("leaders", leaders);
 				intent.putExtra("processid", processidString);
 				intent.putStringArrayListExtra("leaderidList", leaderList);
 				setResult(RESULT_OK,intent);
	            finish();
 			}
 		});
	}
	
	// 根据接口得到json数据====================================
	@SuppressWarnings("unchecked")
	public void getjson(String strUrl){
		FinalHttp fh = new FinalHttp(); 
	     		try {
	     			fh.get( strUrl,
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
	     							//Log.v(TAG,"请求结果(json串)" + result);
	     							//Log.v(TAG,"str====" + str);
	     							// ++++++++++++解析数据++++++++++++++++++++++++++++++
	     							try {
	     								listmap = new ArrayList<HashMap<String, String>>();
	     								JSONObject jsonObject = new JSONObject(str);

	     								String data = jsonObject.getString("data");
	     								if(processidString==""){
	     									processidString = jsonObject.getString("processid");
	     								}
	     								JSONArray jsonArray = new JSONArray(data);
	     								if (jsonArray.length() > 0) {
	     									for (int i = 0; i < jsonArray.length(); i++) {
	     										map = new HashMap<String, String>();
	     										JSONObject jo = jsonArray
	     												.getJSONObject(i);
	     										//Log.v(TAG,"i===" + i);
	     										//Log.v(TAG,"jo==" + jo);
	     										map.put("fsystem", jo.getString("fsystem"));
	     										map.put("fnumber", jo.getString("fnumber"));
	     										map.put("lx", jo.getString("lx"));
	     										// 将解析出来的数据加入到listmap表中
	     										listmap.add(map);
	     										//Log.v(TAG,jo.getString("fsystem"));
	     										//System.out.println("listmap-->>" + listmap);
	     									}
	     									//System.out.println("listmap1-->>" + listmap);
	     								} else {
	     									Log.v(TAG,"data无值,为=" + data);
	     								}
	     							} catch (JSONException e) {
	     								e.printStackTrace();
	     							}
	     							// ++++++++++++解析结束++++++++++++++++++++++++++++++++
	     							addressList = (ListView) findViewById(R.id.lv_address_list);
	     				            adapter = new OrganizationAdapter(Organization.this,handler,listmap);
	     				            addressList.setAdapter(adapter);
	     				            //listview item点击事件
	     				            addressList.setOnItemClickListener(new OnItemClickListener() {

	     				   			public void onItemClick(AdapterView<?> parent, View view,
	     				   					int position, long id) {

	     				   				//Log.v(TAG,"机构名："+listmap.get(position).get("fsystem"));
	     				   				if(listmap.get(position).get("lx").toString().equals("1")){
		     				   				strWhere += listmap.get(position).get("fsystem")+"!";
		     				   			    Log.v(TAG,"查询条件参数："+strWhere);
		     				   			    int i = count(strWhere);
		     				   		        Log.v(TAG, "层参数："+i);
		     				   			    strFloor = "floorn="+i;
		     				   			    String Url="http://61.182.203.110:8888/?Requestflag=Lookingsomeone";
		     				   			    Url += "&"+strFloor+"&"+strWhere;
		     				   			    
		     				   				Log.v(TAG, "拼接好的链接："+Url);
	     				   					getjson(Url);
	     				   				}	
	     				   			}
	     				   		});
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
	
	/**
	 *param s 需要判断的字符串
	 **/
	public int count(String s){
		int num = 1;
		for(int i =0;i<s.length();i++){
			char c =s.charAt(i);
			if(c=='!'){
				num++;
			}
		}
        return num;
	}

	
}
