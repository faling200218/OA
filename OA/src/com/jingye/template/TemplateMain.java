package com.jingye.template;

import com.jingye.main.MyProcess;
import com.jingye.user.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TemplateMain extends Activity{
	private Button creatButton;
    private Button sysButton;
    private Button myButton;
    private Button groupButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_template);
		
		findView();
	}
	
	public void findView(){
		creatButton = (Button)findViewById(R.id.btn_creat);
		sysButton = (Button)findViewById(R.id.btn_system);
		myButton = (Button)findViewById(R.id.btn_my);
		groupButton = (Button)findViewById(R.id.btn_group);
		creatButton.setOnClickListener(new OnClickListener() {
			
			 public void onClick(View v) {
					 Intent intent = new Intent(TemplateMain.this,CreatTemplate.class);
					 startActivity(intent);
				 }
			 });
		myButton.setOnClickListener(new OnClickListener() {
			
			 public void onClick(View v) {
					 Intent intent = new Intent(TemplateMain.this,ProcessTemplate.class);
					 startActivity(intent);
				 }
			 });
	}

}
