/*
 * BookShower.java
 * classes : com.kinggrid.iapppdf.demo.BookShower
 * @author 涂博之
 * V 1.0.0
 * Create at 2014年5月20日 下午4:35:00
 */
package com.jingye.signature;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jingye.user.R;
import com.kinggrid.iapppdf.common.settings.SettingsManager;
import com.kinggrid.iapppdf.ui.viewer.IAppPDFActivity;
import com.kinggrid.iapppdf.ui.viewer.ViewerActivityController.LoadPageFinishedListener;
import com.kinggrid.kinggridsign.KingGridView;
import com.kinggrid.pdfservice.Annotation;
import com.kinggrid.pdfservice.Field;
import com.kinggrid.pdfservice.SignaturePosition;
import com.kinggrid.signature.KingGridSignatureBase;
import com.kinggrid.signature.KingGridSignatureBase.OnSignatureListener;
import com.kinggrid.signature.Signature_KINGGRIDSIGN;

/**
 * com.kinggrid.iapppdf.demo.BookShower 
 * 
 * @author 涂博之 <br/>
 *         create at 2014年5月20日 下午4:35:00
 */
@SuppressLint("NewApi")
public class BookShower extends IAppPDFActivity {

	private static final String TAG = "BookShower";

	private FrameLayout frameLayout;
	private LinearLayout toolbar;
	private HorizontalScrollView hsv;

	private SparseArray<MyButton> viewMap;
	// private Context context;

	final static int KEY_DOCUMENT_SAVE = 0;
	final static int KEY_SINGER = 1;
	final static int KEY_SINGER_DEL = 2;
	final static int KEY_FULL_SINGER = 3;
	final static int KEY_DEL_FULL_SINGER = 4;// 删除全文批注
	final static int KEY_TEXT_NOTE = 5;
	final static int KEY_DEL_TEXT_NOTE = 6;// 删除文字注释
	final static int KEY_SOUND_NOTE = 7;// 语音批注
	final static int KEY_DEL_SOUND_NOTE = 8;// 删除语音批注
	final static int KEY_NOTE_LIST = 9; //

	final static int KEY_TEXT_LIST = 10;// 注释列表
	final static int KEY_SOUND_LIST = 11; //语音批注列表
	final static int KEY_BOOKMARK_LIST = 12;// 大纲
	final static int KEY_CAMERA = 13;// 证件拍照
	final static int KEY_SAVEAS = 16; // 另存
	final static int KEY_DIGITAL_SIGNATURE = 14;//数字签名
	final static int KEY_VERIFY = 15;//验证
	//final static int KEY_SEARCH = 21;//搜索
	final static int KEY_ABOUT = 22; // 关于界面
	final static int KEY_SAVE_PAGES = 17; // 保存页面图片
	final static int KEY_FIELD_CONTENT = 18; // 获取全部域内容
	final static int KEY_AREA = 19; //区域签批
	final static int KEY_DUPLEX = 21;//同步签批
	final static int KEY_LOCAL_DIGITAL_SIGNATURE = 20;//数字签名

/*	final static int TYPE_ANNOT_HANDWRITE = 1;*/
	final static int TYPE_ANNOT_STAMP = 1;
	final static int TYPE_ANNOT_TEXT = 2;
	final static int TYPE_ANNOT_SIGNATURE = 3;
	final static int TYPE_ANNOT_SOUND = 4;
	

	private Intent intent;
	private int templateId;
	private boolean loadField = true;
	private ArrayList<Field> fieldList;
	private int annotType;

	private Uri systemPhotoUri; // 照片在媒体库中的路径
	private String imagePath; // 照片文件在文件系统中的路径

	private boolean hasLoaded = false;
	
	/*private final static String url = "http://192.168.0.48:8888/Demo/DefaulServlet";*/
	
	private String url;
	private final static String signatruePdfUrl = "pdf/isignature/template.pdf";
	private final static String verifyPdfUrl = "pdf/verify/template.pdf";
	private final static String IMAGE_PATH = Environment
			.getExternalStorageDirectory().getPath().toString() + "/testSignature.jpg";
	private ArrayList<String> fieldNameList = null;
	private final String SAVESIGNFINISH_ACTION = "com.kinggrid.iapppdf.broadcast.savesignfinish";
	
	private ArrayList<SignaturePosition> signpos_list = new ArrayList<SignaturePosition>();
	@Override
	protected void onCreateImpl(Bundle savedInstanceState) {
		super.onCreateImpl(savedInstanceState);
		/* context = this; */
		setContentView(R.layout.book);
		frameLayout = (FrameLayout) findViewById(R.id.book_frame);
		hsv = (HorizontalScrollView) findViewById(R.id.toptitle_item_layout);
		toolbar = (LinearLayout) findViewById(R.id.toolbar);
		//hsv.setVisibility(View.GONE);
		//金格科技iApp试用许可A02    过期时间=2016-03-22
		copyRight = "SxD/phFsuhBWZSmMVtSjKZmm/c/3zSMrkV2Bbj5tznSkEVZmTwJv0wwMmH/+p6wLiUHbjadYueX9v51H9GgnjUhmNW1xPkB++KQqSv/VKLDsR8V6RvNmv0xyTLOrQoGzAT81iKFYb1SZ/Zera1cjGwQSq79AcI/N/6DgBIfpnlwiEiP2am/4w4+38lfUELaNFry8HbpbpTqV4sqXN1WpeJ7CHHwcDBnMVj8djMthFaapMFm/i6swvGEQ2JoygFU368sLBQG57FhM8Bkq7aPAVrvKeTdxzwi2Wk0Yn+WSxoXx6aD2yiupr6ji7hzsE6/QRx89Izb7etgW5cXVl5PwISjX+xEgRoNggvmgA8zkJXOVWEbHWAH22+t7LdPt+jENUl53rvJabZGBUtMVMHP2J32poSbszHQQyNDZrHtqZuuSgCNRP4FpYjl8hG/IVrYXSo6k2pEkUqzd5hh5kngQSOW8fXpxdRHfEuWC1PB9ruQ=";
		
		//Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0);
		initPDFView(frameLayout);
		initViewMap();
		initToolBar();
		//openSignature();
		
		intent = getIntent();
		if(isUseEbenSDK){
			viewMap.get(KEY_FULL_SINGER).setVisibility(View.GONE);
		}
		url = intent.getStringExtra("sUrl");
		Log.v("tbz","url = " + url);
		setLoadingText(R.string.msg_loading_tip);
		//setPenInfo(10, Color.BLACK, KingGridView.TYPE_BALLPEN);	
		getController().setLoadPageFinishedListener(
				new LoadPageFinishedListener() {

					public void onLoadPageFinished() {
						if (!hasLoaded) {
							loadFieldTemplates();					
							hasLoaded = true;
						}
					}
				});

		super.setOnKinggridSignListener(new OnKinggridSignListener() {

			public void onSaveSign() {
				Log.i(TAG, "onSaveSign");
			}

			public void onCloseSign() {
				Log.i(TAG, "onCloseSign");
			}

			public void onClearSign() {
				Log.i(TAG, "onClearSign");
			}
		});

		super.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageChange(String currentPage) {
				Log.i(TAG, "onPageChange:" + currentPage);
			}
		});
		
		IntentFilter filter = new IntentFilter(
				"com.kinggrid.pages.bmp.save");
		registerReceiver(receiver, filter);
		
		IntentFilter save_filter = new IntentFilter(SAVESIGNFINISH_ACTION);
		registerReceiver(receiver, save_filter);
	}

	@Override
	protected void loadFieldTemplates() {
		super.loadFieldTemplates();
		if (intent.hasExtra("template")) {
			templateId = intent.getExtras().getInt("template");
			Thread thread = new Thread(new LoadAnnots());
			thread.start();
		}
	}

	
	
	/*@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}*/

	@Override
	protected void onStartImpl() {
		// TODO Auto-generated method stub
		super.onStartImpl();
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//setScreenPort();
	}

	@Override
	protected void onDestroyImpl(boolean finishing) {
		super.onDestroyImpl(finishing);
		System.exit(0);
	}



	public class LoadAnnots implements Runnable {

		/*
		 * ���� Javadoc��
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			if (loadField) {
				fieldNameList = null;
				switch (templateId) {
				case 1:
					fieldNameList = new ArrayList<String>();
					fieldList = new ArrayList<Field>();
					Field field = new Field();
					field.setFieldName("ajmc");
					field.setFieldContent("抢劫");
					fieldList.add(field);
					fieldNameList.add("ajmc");
					Field field1 = new Field();
					field1.setFieldName("fasj");
					field1.setFieldContent("2014-01-01");
					fieldList.add(field1);
					fieldNameList.add("fasj");
					Field field2 = new Field();
					field2.setFieldName("ajwh");
					field2.setFieldContent("G10001");
					fieldList.add(field2);
					fieldNameList.add("ajwh");
					Field field3 = new Field();
					field3.setFieldName("fzxyrxm");
					field3.setFieldContent("张三");
					fieldList.add(field3);
					fieldNameList.add("fzxyrxm");
					Field field4 = new Field();
					field4.setFieldName("fzxyrxb");
					field4.setFieldContent("男");
					fieldList.add(field4);
					fieldNameList.add("fzxyrxb");
					Field field5 = new Field();
					field5.setFieldName("mz");
					field5.setFieldContent("汉");
					fieldList.add(field5);
					fieldNameList.add("mz");
					Field field6 = new Field();
					field6.setFieldName("fzxyrcsny");
					field6.setFieldContent("1970-01-01");
					fieldList.add(field6);
					fieldNameList.add("fzxyrcsny");
					Field field7 = new Field();
					field7.setFieldName("whcd");
					field7.setFieldContent("初中");
					fieldList.add(field7);
					fieldNameList.add("whcd");
					Field field8 = new Field();
					field8.setFieldName("zjzljhm");
					field8.setFieldContent("320361197001010001");
					fieldList.add(field8);
					fieldNameList.add("zjzljhm");
					Field field9 = new Field();
					field9.setFieldName("zz");
					field9.setFieldContent("北京某街道10#5楼1号");
					fieldList.add(field9);
					fieldNameList.add("zz");
					Field field10 = new Field();
					field10.setFieldName("hjd");
					field10.setFieldContent("北京某街道10#5楼1号");
					fieldList.add(field10);
					fieldNameList.add("hjd");
					Field field11 = new Field();
					field11.setFieldName("gzdw");
					field11.setFieldContent("无业");
					fieldList.add(field11);
					fieldNameList.add("gzdw");
					Field field12 = new Field();
					field12.setFieldName("wfjl");
					field12.setFieldContent("无");
					fieldList.add(field12);
					fieldNameList.add("wfjl");
					Field field13 = new Field();
					field13.setFieldName("dwmc");
					field13.setFieldContent("无");
					fieldList.add(field13);
					fieldNameList.add("dwmc");
					Field field14 = new Field();
					field14.setFieldName("frdtb");
					field14.setFieldContent("李四");
					fieldList.add(field14);
					fieldNameList.add("frdtb");
					Field field15 = new Field();
					field15.setFieldName("dwdz");
					field15.setFieldContent("北京某街道10#5楼1号");
					fieldList.add(field15);
					fieldNameList.add("dwdz");
					Field field16 = new Field();
					field16.setFieldName("taqtr");
					field16.setFieldContent("王五");
					fieldList.add(field16);
					fieldNameList.add("taqtr");
					Field field17 = new Field();
					field17.setFieldName("wfssjzj");
					field17.setFieldContent("省略。。。");
					fieldList.add(field17);
					fieldNameList.add("wfssjzj");
					break;
				case 2:
					fieldList = new ArrayList<Field>();
					fieldNameList = new ArrayList<String>();
					Field field_year = new Field();
					field_year.setFieldName("Year");
					field_year.setFieldContent("2012");
					fieldList.add(field_year);
					fieldNameList.add("Year");
					Field field_month = new Field();
					field_month.setFieldName("Month");
					field_month.setFieldContent("05");
					fieldList.add(field_month);
					fieldNameList.add("Month");
					Field field_day = new Field();
					field_day.setFieldName("Day");
					field_day.setFieldContent("11");
					fieldList.add(field_day);
					fieldNameList.add("Day");
					Field field_phone = new Field();
					field_phone.setFieldName("Phone");
					field_phone.setFieldContent("13888888888");
					fieldList.add(field_phone);
					fieldNameList.add("Phone");
					Field field_billno = new Field();
					field_billno.setFieldName("BillNo");
					field_billno.setFieldContent("20120511164722312");
					fieldList.add(field_billno);
					fieldNameList.add("BillNo");
					Field field_clientname = new Field();
					field_clientname.setFieldName("ClientName");
					field_clientname.setFieldContent("中国移动-深圳");
					fieldList.add(field_clientname);
					fieldNameList.add("ClientName");
					Field field_imagesystemid = new Field();
					field_imagesystemid.setFieldName("ImageSystemID");
					field_imagesystemid.setFieldContent("20120511164722312");
					fieldList.add(field_imagesystemid);
					fieldNameList.add("ImageSystemID");
					Field field_content0 = new Field();
					field_content0.setFieldName("Content0");
					field_content0
							.setFieldContent("经办人：张三"
									+ "\n\r"
									+ "证件号码：4405899888752011"
									+ "\n\r"
									+ "证件类型：身份证"
									+ "\n\r"
									+ "业务类型：产品转换"
									+ "\n\r"
									+ "实收费用：0.00"
									+ "\n\r"
									+ "鉴权方式：不验证"
									+ "\n\r"
									+ "新增营销方案：新砖卡服务1-1688——(营销方案说明：套餐月租费1688元：含基本月租50元和国内及香港(需选香"
									+ "\n\r"
									+ "港万众一卡多号)被叫免费；包本地/省内/国际主叫及香港主叫(需选香港万众一卡多号)8000分钟和价值71元的数据"
									+ "\n\r"
									+ "业务包。超出部分资费：1、在归属地拨打本地电话：0.29元/分钟，2、在归属地直拨国内长途：0.39元/分钟能够"
									+ "\n\r"
									+ "全包，3、省内、省际漫游：0.39元/分钟全包。以上资费使用范围仅限中国移动广东公司全球通客户，国内被叫免"
									+ "\n\r"
									+ "费范围不含台港澳；国内长途通话费不含台港澳长途电话：客户使用17951IP电话的通话时长不计入套餐内；其他"
									+ "\n\r"
									+ "资费现行全球通标准资费执行。)生效时间：2010-05-01 00：00：00 取消营销方案：98新商旅套餐(200608：捆绑"
									+ "\n\r"
									+ "一年，到期延续)"
									+ "\n\r"
									+ ". . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . ."
									+ "\n\r"
									+ ". . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .");
					fieldList.add(field_content0);
					fieldNameList.add("Content0");
					Field field_signature1 = new Field();
					field_signature1.setFieldName("Signature1");
					field_signature1.setFieldContent("10086");
					fieldList.add(field_signature1);
					fieldNameList.add("Signature1");
					Field field_idcard = new Field();
					field_idcard.setFieldName("IdCard");
					field_idcard.setFieldContent("430528201101018888");
					fieldList.add(field_idcard);
					fieldNameList.add("IdCard");
					Field field_operator = new Field();
					field_operator.setFieldName("Operator");
					field_operator.setFieldContent("10086");
					fieldList.add(field_operator);
					fieldNameList.add("Operator");
					break;
				}
				if (fieldList.size() > 0) {
					for (int i = 0; i < fieldList.size(); i++) {
						setFieldContent(fieldList.get(i).getFieldName(),
								fieldList.get(i).getFieldContent());
					}
				}
				loadField = false;
				refreshDocument();
			}
		}

	}

	private void initToolBar() {
		if (viewMap != null && viewMap.size() > 0) {
			for (int i = 0; i < viewMap.size(); i++) {
				addBtnView(viewMap.get(i));
			}
		}
	}
	
	private void setSignaturePosition(){
		for(int i = 0; i < getPageCount(); i++){
			SignaturePosition sp = new SignaturePosition();
			sp.setSignature_page_num(String.valueOf(i));
			sp.setSignature_x(100.0f);
			sp.setSignature_y(100.0f);
			signpos_list.add(sp);
			
			SignaturePosition sp2 = new SignaturePosition();
			sp2.setSignature_page_num(String.valueOf(i));
			sp2.setSignature_x(300.0f);
			sp2.setSignature_y(300.0f);
			signpos_list.add(sp2);
		}
	}

	private void addBtnView(final MyButton btn) {

		toolbar.addView(btn);

		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isLocked) {
					return;
				}
				switch ((Integer) btn.getTag()) {
				case R.string.signature:
					//setScreenLand();//强制横屏
					//openSignature();
					LayoutInflater inflater=getLayoutInflater();
					//ViewHolder holder;
					  //class ViewHolder {
					    final EditText etname;
					/*    protected TextView fileSize;*/
					//  }
					View layout=inflater.inflate(R.layout.dialog, (ViewGroup)findViewById(R.id.dialog));
					//holder = new ViewHolder();
				      
				      etname = (EditText)layout.findViewById(R.id.etname);
					AlertDialog.Builder builder1 = new Builder(context);
					builder1.setTitle("请设置签字栏").setView(layout)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							signatureField2=etname.getText().toString();
						}
					})
					.setNegativeButton("取消", null).show();
					
					openSignature(0, 900, 750 ,Color.YELLOW, SIGNATURE_LOCATION_MODE_FIELD);
					mSignatureWindows.setOnWindowDismissListener(new OnDismissListener(){

						public void onDismiss() {
							// TODO Auto-generated method stub
							setScreenPort();//强制竖屏
						}
						
					});
					
					
					/*setSignaturePosition();
					openSignatureByPos(signpos_list, 1.0f, 900, 1350, 50);*/
					/*setSignaturePosition();
					Log.v("tbz","signpos_list size = " + signpos_list.size());
					openSignatureByPos(signpos_list, 1.0f, 700, 750, 10);*/
					break;
				case R.string.signature_delete:
					/* deleteAllAnnotations(TYPE_ANNOT_SIGNATURE); */
					createDialog(TYPE_ANNOT_SIGNATURE);
					break;
				case R.string.full_signature:
					openHandwriteAnnotation();
					break;
				case R.string.note_list:
					openAnnotationList(TYPE_ANNOT_STAMP);
					break;
				case R.string.bookmark_list:
					openOutlineList();
					break;
				case R.string.text_note_list:
					openAnnotationList(TYPE_ANNOT_TEXT);
					break;
				case R.string.annot_sound_list:
					openAnnotationList(TYPE_ANNOT_SOUND);
					break;
				case R.string.text_note:
					openTextAnnotation();
					break;
				case R.string.sound_note:
					openSoundAnnotation();
					break;
				case R.string.delete_sound_note:
					createDialog(TYPE_ANNOT_SOUND);
					break;
				case R.string.full_signature_delete:
					/* deleteAllAnnotations(TYPE_ANNOT_HANDWRITE); */
					createDialog(TYPE_ANNOT_STAMP);
					break;
				case R.string.note_delete:
					/* deleteAllAnnotations(TYPE_ANNOT_TEXT); */
					createDialog(TYPE_ANNOT_TEXT);
					break;
				case R.string.camera:
					//takePicture();
					insertSignatureInField(IMAGE_PATH,1.0f);
					break;
				case R.string.duplex:
					openDuplexHandwrite(200, 400, 150);
					doSettingDuplexHandwriteDelayTime(2000);
					//doSettingDuplexHandwriteInfo(50);
					break;
				case R.string.about:
					//openAbout();
					//SettingsManager.toggleNightMode(getController().getBookSettings());
					insertTextAnnotation (1, 80, 80, "测试文字批注", "admin", "2014-05-29 11:10:10","20140529111010");
					addBlankPage(0);
					saveDocument();
					//final Uri uri = intent.getData();
					String path = getIntent().getData().getPath();
					final Uri uri = Uri.parse(path);
					Log.v("tbz","uri = " + uri);
					reopenDocument(BookShower.this, uri, "");
					clearDocumentInfo();
					clearCacheInfo();
					break;
				case R.string.document_save:
					clearPDFReadSettings();
					final AlertDialog.Builder builder = new Builder(context);
					builder.setMessage(getString(R.string.dialog_message_save));
					builder.setTitle(getString(R.string.dialog_title));
					builder.setPositiveButton(getString(R.string.ok),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									Log.v(TAG,
											"" + intent.hasExtra("annotType"));
									if (intent.hasExtra("annotType")) {
										annotType = intent.getExtras().getInt(
												"annotType");
										List<Annotation> annotList = new ArrayList<Annotation>();
										annotList = getAnnotationList(annotType);
										if (annotList.size() != 0) {
											for (int i = 0; i < annotList
													.size(); i++) {
												Annotation annot = new Annotation();
												annot = annotList.get(i);
												Log.v(TAG,
														"sign path = "
																+ annot.getAnnoContent());
												Log.v(TAG,"sign name = "  + annot.getUnType());
											}
										}
									}
									if(isUseEbenSDK){
										saveAllSignAndDocument();
									} else {
										getVectorData();
										setAllAnnotationsOnlyRead(isAnnotProtect);
										saveDocument();
										finish();
									}
								}
							});
					builder.setNegativeButton(getString(R.string.cancel),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

					final Dialog dialog1 = builder.create();
					dialog1.setCancelable(false);
					dialog1.show();
					break;
				case R.string.digital_signature:
					
					openSignature(3, 750, 900 ,Color.WHITE, 0);
//					if(url == null){
//						Toast.makeText(getApplicationContext(), "服务器地址为空", Toast.LENGTH_LONG).show();
//					}else{
//						new Thread(new Runnable(){
//
//							@Override
//							public void run() {
//								// TODO Auto-generated method stub
//								upload(url, IMAGE_PATH, 1, signatureField1);
//							}
//							
//						}).start();
//					}
//					setAllAnnotationsVisible(false);
//					setAnnotationsVisible(false, "admin");
//					refreshDocument();
					break;
				case R.string.local_digital_signature:
					//数字签名
					openSignature(SIGNATURE_CLIENT, 700, 750 ,Color.TRANSPARENT, SIGNATURE_LOCATION_MODE_FIELD);
					break;
				case R.string.verify:
//					setAllAnnotationsVisible(true);
//					setAnnotationsVisible(true, "admin");
//					refreshDocument();
					if(url == null){
						Toast.makeText(getApplicationContext(), "服务器地址为空", Toast.LENGTH_LONG).show();
					}else{
						new Thread(new Runnable(){

							public void run() {
								// TODO Auto-generated method stub
								upload(url,null,2,null);
							}
							
						}).start();
					}
					
					break;
				case R.string.area:
					openAreaHandwrite((int)screenHeight / 4, false, false);
					break;
				case R.string.document_saveas:
					final EditText input = new EditText(context);    //定义一个EditText
					input.setText("xxx.pdf");
					final AlertDialog.Builder saveAsBuilder = new Builder(context);
					saveAsBuilder.setTitle("设置文件名");
			        saveAsBuilder.setView(input);       //将EditText添加到builder中
			        saveAsBuilder.setPositiveButton(getString(R.string.save),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

									saveAsDocument(FILEDIR_PATH + "/"+ input.getText());
								}
			        });
			        saveAsBuilder.setNegativeButton(getString(R.string.cancel),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});

					final Dialog saveAsDlg = saveAsBuilder.create();
					saveAsDlg.setCancelable(false);
					saveAsDlg.show();
					
					break;
				case R.string.save_pages:
					getPagesImage(FILEDIR_PATH + "/pagebmp/");
					break;
				case R.string.save_field_content:
					if (fieldNameList != null) {
						if (fieldNameList.size() > 0) {
							try {
								FileOutputStream fos = new FileOutputStream(
										FILEDIR_PATH + "/fieldcontents.txt");
								String values = "";
								for (int i = 0; i < fieldNameList.size(); i++) {
									String key = fieldNameList.get(i);
									values = values + key + ": " + getFieldContent(key) + "\r\n";
								}
								fos.write(values.getBytes("UTF-8"));
								fos.close();
								Toast.makeText(getApplicationContext(), "保存文本域内容完毕", Toast.LENGTH_SHORT).show();
							} catch (FileNotFoundException e) {
								Log.e("", e.toString());
							} catch (UnsupportedEncodingException e) {
								Log.e("", e.toString());
							} catch (IOException e) {
								Log.e("", e.toString());
							}

						}else{
							Toast.makeText(getApplicationContext(), "无文本域内容", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "无文本域内容", Toast.LENGTH_SHORT).show();
					}
					break;
				}
			}
		});
	}
	
	private void initViewMap() {
		viewMap = new SparseArray<MyButton>();
		viewMap.put(KEY_SINGER, new MyButton(context,
				R.drawable.kinggrid_signature, R.string.signature));
		viewMap.put(KEY_SINGER_DEL, new MyButton(context,
				R.drawable.kinggrid_signature_del, R.string.signature_delete));
		viewMap.put(KEY_FULL_SINGER, new MyButton(context,
				R.drawable.kinggrid_signer_full, R.string.full_signature));
		viewMap.put(KEY_NOTE_LIST, new MyButton(context,
				R.drawable.kinggrid_annot_postil_list, R.string.note_list));
		viewMap.put(KEY_BOOKMARK_LIST, new MyButton(context,
				R.drawable.kinggrid_outline, R.string.bookmark_list));
		
		viewMap.put(KEY_SOUND_LIST, new MyButton(context,
				R.drawable.kinggrid_annot_sound_list, R.string.annot_sound_list));
		
		viewMap.put(KEY_TEXT_NOTE, new MyButton(context,
				R.drawable.kinggrid_annot, R.string.text_note));
		viewMap.put(KEY_DEL_FULL_SINGER, new MyButton(context,
				R.drawable.kinggrid_signer_delete,
				R.string.full_signature_delete));
		viewMap.put(KEY_DEL_TEXT_NOTE, new MyButton(context,
				R.drawable.kinggrid_annot_delete, R.string.note_delete));
		viewMap.put(KEY_DOCUMENT_SAVE, new MyButton(context,
				R.drawable.kinggrid_pdf_save_item, R.string.document_save));
		viewMap.put(KEY_SOUND_NOTE, new MyButton(context,
				R.drawable.kinggrid_annot_sound, R.string.sound_note));
		viewMap.put(KEY_DEL_SOUND_NOTE,
				new MyButton(context, R.drawable.kinggrid_annot_sound_del,
						R.string.delete_sound_note));
		viewMap.put(KEY_TEXT_LIST, new MyButton(context,
				R.drawable.kinggrid_annot_text_list, R.string.text_note_list));
		viewMap.put(KEY_CAMERA, new MyButton(context,
				R.drawable.kinggrid_camera, R.string.camera));
		viewMap.put(KEY_ABOUT, new MyButton(context,
				R.drawable.kinggrid_about, R.string.about));
		viewMap.put(KEY_DIGITAL_SIGNATURE, new MyButton(context,
				R.drawable.kinggrid_signature_add, R.string.digital_signature));
		viewMap.put(KEY_LOCAL_DIGITAL_SIGNATURE, new MyButton(context,
				R.drawable.kinggrid_signature_add, R.string.local_digital_signature));
		viewMap.put(KEY_VERIFY, new MyButton(context,
				R.drawable.kinggrid_signature_verify, R.string.verify));
		
		viewMap.put(KEY_SAVEAS, new MyButton(context,
				R.drawable.kinggrid_pdf_save_item, R.string.document_saveas));
		viewMap.put(KEY_SAVE_PAGES, new MyButton(context,
				R.drawable.kinggrid_pdf_save_item, R.string.save_pages));
		viewMap.put(KEY_FIELD_CONTENT, new MyButton(context,
				R.drawable.kinggrid_pdf_save_item, R.string.save_field_content));
		viewMap.put(KEY_AREA, new MyButton(context,
				R.drawable.kinggrid_camera, R.string.area));
		viewMap.put(KEY_DUPLEX, new MyButton(context,
				R.drawable.kinggrid_signature, R.string.duplex));
		
		
	}

	private void createDialog(final int type) {
		final AlertDialog.Builder builder = new Builder(context);
		switch (type) {
		case TYPE_ANNOT_SIGNATURE:
			builder.setMessage(getString(R.string.dialog_message_annot_signature));
			break;
		case TYPE_ANNOT_STAMP:
			builder.setMessage(getString(R.string.dialog_message_annot_hardwrite));
			break;
		case TYPE_ANNOT_TEXT:
			builder.setMessage(getString(R.string.dialog_message_annot_text));
			break;
		case TYPE_ANNOT_SOUND:
			builder.setMessage(getString(R.string.dialog_message_annot_sound));
		}
		builder.setTitle(getString(R.string.dialog_title));
		builder.setPositiveButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						deleteAllAnnotations(type);
					}
				});
		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		final Dialog dialog1 = builder.create();
		dialog1.setCancelable(false);
		dialog1.show();
	}

	// 拍照
	private void takePicture() {
		systemPhotoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		imagePath = FILEDIR_PATH + "/tempphotos/cameraphoto"
				+ System.currentTimeMillis() + ".jpg";
		final ContentResolver resolver = getContentResolver();
		final Cursor cursor = resolver.query(systemPhotoUri, null, null, null,
				null);
		String lastPhotoPath;
		if (!cursor.moveToFirst()) {
			cursor.moveToLast();
			lastPhotoPath = cursor.getString(1);
		} else {
			lastPhotoPath = "";
		}
		final SharedPreferences sPreferences = getSharedPreferences(
				"photo_info", MODE_PRIVATE);
		sPreferences.edit().putString("photoPath", lastPhotoPath).commit();
		cursor.close();
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		final File imageFile = new File(imagePath);
		final Uri fileUri = Uri.fromFile(imageFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		startActivityForResult(intent, REQUESTCODE_PHOTOS_TAKE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		systemPhotoUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		final ContentResolver resolver = getContentResolver();
		if (requestCode == REQUESTCODE_PHOTOS_TAKE) {
			if (resultCode == RESULT_OK) {
				final Cursor cursor = resolver.query(systemPhotoUri, null,
						null, null, null);
				String lastPhotoPath;
				String lastPhotoPath_old;
				if (!cursor.moveToFirst()) {
					cursor.moveToLast();
					lastPhotoPath = cursor.getString(1);
					final SharedPreferences sPreferences = getSharedPreferences(
							"photo_info", MODE_PRIVATE);
					lastPhotoPath_old = sPreferences.getString("photoPath", "");
					if (!lastPhotoPath.equals(lastPhotoPath_old)) {
						final File file = new File(cursor.getString(1));
						if (file.exists()) {
							file.delete();
						}
					}
				}
				cursor.close();
				insertPhotoIntoPDF(imagePath, photo);
			}
		}
	}

	private void getVectorData() {
		
		ArrayList<String> list = getVectorData(userName);
		if (list != null && list.size() > 0) {
			Log.v("tbz", "list isn't null");
			for (int i = 0; i < list.size(); i++) {
				String[] annotinfo = list.get(i).split(";");
				Log.d("debug", "annotinfo " + i + " : " + annotinfo[4]);
				// saveString2File(annotinfo[4], FILEDIR_PATH + "/vector_info("
				// + i + ").txt");
			}
		}
	}

	@SuppressWarnings("unused")
	private void saveString2File(String info, String filePath) {
		try {
			File saveFile = new File(filePath);
			if (saveFile.exists()) {
				saveFile.delete();
			}
			File dir = new File(saveFile.getParent());
			dir.mkdirs();
			saveFile.createNewFile();

			FileOutputStream outStream = new FileOutputStream(saveFile);
			outStream.write(info.getBytes());
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Handler handler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        Bundle data = msg.getData();
	        String val = data.getString("value");
	        if(val.equals("iSignature Successed")){
	        	insertSignatureInField(IMAGE_PATH,1.0f);
	        	return;
	        }
	        Toast.makeText(getApplicationContext(), val, Toast.LENGTH_LONG).show();
	    }
	};
	
	private void showSaveDialog(Context context){
		View v = View.inflate(context,R.layout.save_dialog_layout, null);
		final Button cancel = (Button) v.findViewById(R.id.btn_cancel);
		final Button no_save = (Button) v.findViewById(R.id.btn_no_save);
		final Button save = (Button) v.findViewById(R.id.btn_save);
		
		final Dialog dialog = new Dialog(context,R.style.MyDialog);
		cancel.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
			
		});
		
		no_save.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
			
		});
		
		save.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if(isUseEbenSDK){
					saveAllSignAndDocument();
				} else {
					saveDocument();
					finish();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			}
			
		});	
		
		dialog.setContentView(v);
		dialog.setCancelable(false);
		dialog.show();		
		
		/*WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();	
		lp.y = -150;
		lp.width = 400; 
        lp.height = 200; 
        dialog.getWindow() .setAttributes(lp);*/
		getController().setDialogWindowSize(dialog);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if((keyCode == KeyEvent.KEYCODE_BACK)){
			if(!isDocumentModified()){
				final AlertDialog.Builder builder = new Builder(context);
				builder.setTitle(getString(R.string.dialog_title));
				builder.setMessage(getString(R.string.close_doc_title));
				builder.setPositiveButton(getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								finish();
								android.os.Process.killProcess(android.os.Process
										.myPid());
							}
						});
				builder.setNegativeButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});

				final Dialog dialog1 = builder.create();
				dialog1.setCancelable(false);
				dialog1.show();
			}else{
				showSaveDialog(context);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void sendMsgToHandler(String message, boolean isShowVerify){
		Message msg = new Message();
		Bundle data = new Bundle();
		if(message.equals("-1")){
			data.putString("value",getString(R.string.result_no_exist_sign));
		}/*else if(message.equals("0")){
			data.putString("value",getString(R.string.result_invalid_sign));
		}else if(message.equals("1")){
			data.putString("value",getString(R.string.result_valid_sign));
		}else if(message.equals("2")){
			data.putString("value",getString(R.string.result_add_content));
		}*/else if(message.equals("3")){
			data.putString("value",getString(R.string.result_sign_unusual));
		}/*else if(isShowVerify){
			msg.obj = 99;
			data.putString("value", message);
		}*/else{
			data.putString("value", message);
		}
		
        msg.setData(data);
        handler.sendMessage(msg);
	}
	
	protected String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bytesOut = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytesOut = is.read(bufferOut)) != -1) {
            baos.write(bufferOut, 0, bytesOut);
        }
        String resstrString = new String();
        resstrString = baos.toString("UTF-8");
        baos.flush();
        baos.close();
        return resstrString;
    }
	
	private String getJsonData(List<SignPosition> positions){
		JSONObject data = new JSONObject();
		JSONArray array = new JSONArray();
		try{
			for(int i=0; i<positions.size(); i++){
				JSONObject jo = new JSONObject();
				jo.put("pageno", positions.get(i).pageno);
				jo.put("x", (positions.get(i).rect[0] + positions.get(i).rect[2]) / 2);
				jo.put("y", positions.get(i).height - (positions.get(i).rect[1] + positions.get(i).rect[3]) / 2);
				array.put(jo);
			}
			
			data.put("positions", array);
		}catch(JSONException joe){
			joe.printStackTrace();
		}
		
		return data.toString();
	}
	
	private void upload(String url, String imagePath, int type, String name){
		HttpURLConnection conn = null;
		OutputStream out = null;
		try{
			URL serviceUrl = new URL(url);
			conn = (HttpURLConnection) serviceUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            /*conn.setRequestProperty("enctype", "multipart/form-data");
            conn.setRequestProperty("contentType", "charset=UTF-8");*/
            conn.setRequestProperty("connection", "close");
            conn.setRequestMethod("POST");

            
            DataInputStream disImage = null;
            String imageOrder = null;
            String json = null;
            BitmapFactory.Options options = new  BitmapFactory.Options();
            if(type == 1){
            	File image = new File(imagePath);
            	long imageSize = image.length();
            	Log.v("tbz","imageSize = " + imageSize);
                disImage = new DataInputStream(new FileInputStream(image));
                imageOrder = "fileSize="+ imageSize + ",fileName=IMAGE";
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imagePath, options);
                options.inJustDecodeBounds = false;
                
                List<SignPosition> positions = getSignPositionInField(name);/*getSignPositionInText(name);*/
                json = getJsonData(positions);
            }
            
            
            
            
            out = conn.getOutputStream();
            int bytesOut = 0;
            byte[] bufferOut = new byte[1024 * 4];
            Log.v("tbz","outputStream start transform");
            if(type == 1){
            	out.write("type=1".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write("debug=0".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                /*out.write("pageno=1".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write("x=100".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write("y=100".getBytes("UTF-8"));*/
                out.write(("position=" + json).getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write(("w="+options.outWidth).getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write(("h="+options.outHeight).getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write("imagetype=jpg".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write(("pdfPath="+signatruePdfUrl).getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                out.write(imageOrder.getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
                while (((bytesOut = disImage.read(bufferOut, 0, 1024)) != -1)) {
                  out.write(bufferOut, 0, bytesOut);
                }

            }else if(type == 2){
            	out.write("type=2".getBytes("UTF-8"));
            	out.write("\r\n".getBytes("UTF-8"));
            	out.write("debug=0".getBytes("UTF-8"));
                out.write("\r\n".getBytes("UTF-8"));
            	out.write(("pdfPath="+verifyPdfUrl).getBytes("UTF-8"));
            	out.write("\r\n".getBytes("UTF-8"));
            }
            
            /*out.write("\r\n".getBytes("UTF-8"));
            out.write(pdfOrder.getBytes("UTF-8"));
            out.write("\r\n".getBytes("UTF-8"));*/
            /*while (((bytesOut = disPDF.read(bufferOut, 0, 1024)) != -1)) {
                out.write(bufferOut, 0, bytesOut);
            }*/
            
            out.flush();
            out.close();
            Log.v("tbz","outputStream transform end");
            
            InputStream isResult = conn.getInputStream();
            Log.v("tbz","isResult = " + isResult.toString());
            String resultString = inputStream2String(isResult);
            Log.v("tbz","Result = " + resultString);
            if(type == 1){
            	sendMsgToHandler(resultString, false);
            }else if(type == 2){
            	/*if(resultString.length() == 1){
            		sendMsgToHandler(resultString, false);
            	}else{
            		sendMsgToHandler(resultString, true);
            	}*/
            	sendMsgToHandler(resultString, false);
            }
            
		}catch(Exception e){
			Log.v("tbz","exception found");
			sendMsgToHandler(getString(R.string.exception_tip), false);
			e.printStackTrace();
		}finally {
			if(conn != null){
				Log.v("tbz","final step");
				conn.disconnect();
				conn = null;
			}
		}
		
		
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();			
			if (action.equals("com.kinggrid.pages.bmp.save")) {
				Toast.makeText(context, "保存页面图片完毕", Toast.LENGTH_SHORT).show();
			}
			if (action.equals(SAVESIGNFINISH_ACTION)) {
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}
	};
}
