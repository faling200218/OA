package com.jingye.process;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.jingye.user.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class U {

	public static String STAFF_ID = "";// 员工编号
	public static String STAFF_NAME = "";// 员工名称
	public static String ORG_NAME = "";// 部门
	public static String POS_NAME = "";// 职称

	// 私有的进度条对话框
	private static ProgressDialog progressDialog;
	// httpUtils
	public static HttpUtils httpUtils;

	static {
		// 全局静态成员初始化
		httpUtils = new HttpUtils(3000);
		httpUtils.configSoTimeout(3000);
		httpUtils.configTimeout(3000);
	}

	/**
	 * 快捷toast提示
	 * 
	 * @param c
	 * @param msg
	 */
	public static void toast(Context c, String msg) {
		Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * @param content
	 *            i方法 LogUtil.i(content)打印日志信息
	 */
	public static void i(String content) {
		LogUtils.i(content);
	}

	/**
	 * @return 返回版本号
	 */
	public static int getVersionCode(Context c) {
		PackageManager pm = c.getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(c.getPackageName(), 0);
			int versionCode = packageInfo.versionCode;
			U.i(versionCode + "");
			return versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @return 返回版本名称
	 */
	public static String getVersionName(Context c) {
		PackageManager pm = c.getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageInfo(c.getPackageName(), 0);
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 消息提示框
	 * @param context
	 */
	public static void alertDialog(Context context){
		new AlertDialog.Builder(context).setTitle("提示").setMessage("无法获取当前位置，请稍后再试！").setNegativeButton("确定", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
			
			}
		}).show();
	}

	/**
	 * 显示加载对话框
	 */
	public static void showLoadingDialog(Context context) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage("正在加载.....");
			progressDialog.show();
		}
		
	}

	/**
	 * 关闭加载加载对话框
	 */
	public static void closeLoadingDialog() {
		if (progressDialog != null) {
			progressDialog.cancel();
		}
	}

	/**
	 * 关闭对话框
	 */
	public static void closeDialog() {
		progressDialog.dismiss();
	}

	/**
	 * @param url
	 *            获取数据的url
	 * @param requestCallBack
	 *            回调接口
	 */
	public static void get(String url, RequestCallBack<String> requestCallBack) {
		httpUtils.send(HttpMethod.GET, url, requestCallBack);
	}

	/**
	 * @param c
	 *            显示网络连接失败的提示
	 */
	public static void showNetErr(Context c) {
		toast(c, c.getString(R.string.net_err));
	}

	/**
	 * @param context
	 *            上下文
	 * @param view
	 *            隐藏键盘 当前活动窗口 通常都是使用 Activity.getCurrentFocus();
	 */
	public static void closeKeyBoard(Context context, View view) {
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

//	/**
//	 * @return 时间格式转换
//	 */
//	public static String t2t(String time) {
//		String result = "";
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		try {
//			Date date = sdf.parse(time);
//			int y = date.getYear() + 1900;
//			int m = date.getMonth() + 1;
//			String sm = m > 10 ? "" + m : "0" + m;
//			int d = date.getDate();
//			String sd = d > 10 ? "" + d : "0" + d;
//			int h = date.getHours();
//			String sh = h > 10 ? h + "" : "0" + h;
//			int mm = date.getMinutes();
//			String smm = mm > 10 ? mm + "" : "0" + mm;
//			String w = intToStr(date.getDay());
//			result = y + "年" + sm + "月" + sd + "日" + "  " + "周" + w + "  " + sh
//					+ "时" + smm + "分";
//		} catch (Exception e) {
//		}
//		return result;
//	}

	public static String intToStr(int num) {
		switch (num) {
		case 1:
			return "一";
		case 2:
			return "二";
		case 3:
			return "三";
		case 4:
			return "四";
		case 5:
			return "五";
		case 6:
			return "六";
		case 0:
			return "日";
		default:
			return num + "";
		}
	}

	/**
	 * 获取系统时间
	 * @param time1
	 * @return
	 */
	public static String refreshTime(long time1) {
		String result = "";
		time1 = System.currentTimeMillis();// 获得系统当前时间
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置时间格式
		Date date = new Date(time1);
		int y = date.getYear() + 1900;
		int m = date.getMonth() + 1;
		String sm = m >= 10 ? "" + m : "0" + m;
		int d = date.getDate();
		String sd = d >= 10 ? "" + d : "0" + d;
		int h = date.getHours();
		String sh = h >=10 ? "" + h : "0" + h;
		int mm = date.getMinutes();
		String smm = mm >= 10 ? mm + "" : "0" + mm;
		result = y + "年" + sm + "月" + sd+"日";// 将获得到的时间组装(仅组装的年月日)
		return result;
	}
	
	/**
	 * 获取系统是星期几
	 * @param time2
	 * @return
	 */
	public static String weekTime(long time2){
		String result="";
		time2=System.currentTimeMillis();
//		SimpleDateFormat format=new SimpleDateFormat();
		Date date=new Date(time2);
		String w = intToStr(date.getDay());
		result=w;
		return result;
	}
}
