package com.jingye.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.jingye.download.MainActivity;

public class LoginActivity extends Activity {

	private Button btnLogin;
	private Button btnRegister;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnRegister = (Button) findViewById(R.id.btnRegister);
       
		// ��¼
		btnLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				EditText etPassword = (EditText) findViewById(R.id.etPassword);
				String Password = etPassword.getText().toString();
				
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
			}
		});

		// ע��
		btnRegister.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//setContentView(R.layout.activity_register);
				//Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
				//startActivity(intent);
			}
		});
	}

}