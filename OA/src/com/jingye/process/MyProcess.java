package com.jingye.process;

import com.jingye.download.MainActivity.Asy_upload;
import com.jingye.user.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author 张周强
 *
 */
public class MyProcess extends Activity {

	private Button btnCreat;
	private Button btnMyTemple;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process);
		
		btnCreat=(Button)findViewById(R.id.btn_creat);
		btnMyTemple=(Button)findViewById(R.id.btn_template);
		btnCreat.setOnClickListener(new OnClickListener() {

 			public void onClick(View v) {
 				Intent intent = new Intent(MyProcess.this,Organization.class);
 				startActivity(intent);
	
 			}
 		});
	}
	

}
