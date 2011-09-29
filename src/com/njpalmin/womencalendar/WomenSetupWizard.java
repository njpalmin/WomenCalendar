package com.njpalmin.womencalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class WomenSetupWizard extends Activity {
	private AlertDialog welcomeDialog;
	private AlertDialog periodSetDialog;
	private AlertDialog daySetDialog;
	private AlertDialog lanSetDialog;
	private AlertDialog endDialog;
	private View periodView;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		initDialog();	
	}
	
	private void initDialog() {
		welcomeDialog = new AlertDialog.Builder(this).setTitle("1/5").setMessage(R.string.first_run_start_1)
			.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					periodSetDialog.show();
				}
			})
			.create();
		welcomeDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {	
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				
				return false;
			}
		});
		
		LayoutInflater factory = LayoutInflater.from(this);
		periodView = factory.inflate(R.layout.period_dialog, null);
		((Button)periodView.findViewById(R.id.btn_cycle_plus))
			.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					EditText editText = (EditText)periodView.findViewById(R.id.text_cycle);
					editText.setText(String.valueOf(Integer.valueOf(editText.getText().toString()) + 1));
				}
			});
		((Button)periodView.findViewById(R.id.btn_cycle_minus))
			.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					EditText editText = (EditText)periodView.findViewById(R.id.text_cycle);
					editText.setText(String.valueOf(Integer.valueOf(editText.getText().toString()) - 1));
				}
			});
		((Button)periodView.findViewById(R.id.btn_length_plus))
			.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					EditText editText = (EditText)periodView.findViewById(R.id.text_length);
					editText.setText(String.valueOf(Integer.valueOf(editText.getText().toString()) + 1));
				}
			});
		((Button)periodView.findViewById(R.id.btn_length_minus))
		.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				EditText editText = (EditText)periodView.findViewById(R.id.text_length);
				editText.setText(String.valueOf(Integer.valueOf(editText.getText().toString()) - 1));
			}
		});
		periodSetDialog = new AlertDialog.Builder(this).setTitle("2/5")
			.setView(periodView)
			.setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					welcomeDialog.show();
				}
			})
			.setNegativeButton(R.string.next, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					daySetDialog.show();
				}
			}).create();
		periodSetDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {	
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				
				return false;
			}
		});
		
		View daySetView = factory.inflate(R.layout.dayset_dialog, null);
		RadioGroup daygroup = (RadioGroup)daySetView.findViewById(R.id.day_group);
		String[] dayStrings = getResources().getStringArray(R.array.dayset_values);
		for (int i = 0; i < dayStrings.length; i++) {
			RadioButton radioBtn = new RadioButton(this);
			radioBtn.setText(dayStrings[i]);
			radioBtn.setId(i);
			daygroup.addView(radioBtn);
		}
		daygroup.check(0);
		daySetDialog = new AlertDialog.Builder(this).setTitle("3/5")
			.setView(daySetView)
			.setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					periodSetDialog.show();
				}
			})
			.setNegativeButton(R.string.next, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					lanSetDialog.show();
				}
			})
			.create();
		daySetDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {	
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				
				return false;
			}
		});
		
		View lanSetView = factory.inflate(R.layout.lanset_dialog, null);
		RadioGroup langroup = (RadioGroup)lanSetView.findViewById(R.id.lan_group);
		String[] lanStrings = getResources().getStringArray(R.array.lanset_values);
		for (int i = 0; i < lanStrings.length; i++) {
			RadioButton radioBtn = new RadioButton(this);
			radioBtn.setText(lanStrings[i]);
			radioBtn.setId(i);
			langroup.addView(radioBtn);
		}
		langroup.check(0);
		lanSetDialog = new AlertDialog.Builder(this).setTitle("4/5")
			.setView(lanSetView)
			.setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					daySetDialog.show();
				}
			})
			.setNegativeButton(R.string.next, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					endDialog.show();
				}
			})
			.create();
		lanSetDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {	
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				
				return false;
			}
		});
		
		endDialog = new AlertDialog.Builder(this).setTitle("5/5").setMessage(R.string.first_run_finish_1)
			.setPositiveButton(R.string.back, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					lanSetDialog.show();
				}
			})
			.setNegativeButton(R.string.start_to_use_calendar, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					SharedPreferences appShared = 
						getApplicationContext().getSharedPreferences(null, MODE_PRIVATE);
					Editor editor = appShared.edit();
					editor.putBoolean("first_start", false);
					editor.commit();
					WomenSetupWizard.this.finish();
				}
			})
			.create();
		endDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {	
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				
				return false;
			}
		});
		
		welcomeDialog.show();
	}
}
