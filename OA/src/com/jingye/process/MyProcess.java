package com.jingye.process;

import com.jingye.template.CreatTemplate;
import com.jingye.template.ProcessTemplate;
import com.jingye.user.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 
 * @author Administrator
 *
 */
public class MyProcess extends MyBaseActivity {

	private Button btnCreat;// 发起审批流程
	private Button btnMyTemple;// 流程模板维护
	private Button btnManage;// 审批流程管理
	private long exitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_process);

		initView();
		/**
		 * 发起审批流程
		 */
		 btnCreat.setOnClickListener(new OnClickListener() {
		
		 public void onClick(View v) {
				 Intent intent = new Intent(MyProcess.this,CreatProcess.class);
				 startActivity(intent);
			 }
		 });
		 /**
		 * 流程模板维护
		 */
		 btnMyTemple.setOnClickListener(new OnClickListener() {
		
		 public void onClick(View v) {
				 Intent intent = new Intent(MyProcess.this,
				 CreatTemplate.class);
				 startActivity(intent);
			 }
		 });
		/**
		 * 审批流程管理
		 */
//		btnManage.setOnClickListener(new OnClickListener() {
//
//			public void onClick(View v) {
//				//Intent intent = new Intent(MyProcess.this, MyProcessManageActivity.class);
//				//startActivity(intent);
//			}
//		});
	}
	
	private void initView(){
		btnCreat = (Button) findViewById(R.id.btn_creat);
		btnMyTemple = (Button) findViewById(R.id.btn_template);
		btnManage = (Button) findViewById(R.id.btn_manage);
	}
	
	// 连续按两次返回键 提示并退出程序
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				if ((System.currentTimeMillis() - exitTime) > 2000) // System.currentTimeMillis()无论何时调用，肯定大于2000
				{
					U.toast(MyProcess.this, "再按一次退出程序");
					exitTime = System.currentTimeMillis();
				} else {
					finish();
					System.exit(0);
				}
				return true;
			}
			return super.onKeyDown(keyCode, event);
		}

}
