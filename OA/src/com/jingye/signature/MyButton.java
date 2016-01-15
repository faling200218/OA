/*
 * MyButton.java
 * classes : com.kinggrid.iapppdf.demo.MyButton
 * @author Í¿ï¿½ï¿½Ö®
 * V 1.0.0
 * Create at 2014ï¿½ï¿½5ï¿½ï¿½23ï¿½ï¿½ ï¿½ï¿½ï¿½ï¿½2:51:23
 */
package com.jingye.signature;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;

/**
 * com.kinggrid.iapppdf.demo.MyButton
 * @author æ¶‚åšä¹?<br/>
 * create at 2014å¹?5æœ?23æ—? ä¸‹åˆ2:51:23
 */
public class MyButton extends Button {
  private static final String TAG = "MyButton";
  
  public MyButton(final Context context, final int imageResId, final int textResId) {
    super(context);

    this.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(imageResId), null, null);
    this.setBackgroundResource(android.R.color.transparent);
    this.setTextColor(Color.BLACK);
    this.setText(context.getString(textResId));
    this.setTag(textResId);
    this.setPadding(5, 0, 5, 0);
}
}
