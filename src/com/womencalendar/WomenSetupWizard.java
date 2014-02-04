package com.womencalendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.womencalendar.utils.Utils;

public class WomenSetupWizard extends Activity {
	private final static String TAG = "WomenSetupWizard";
    private AlertDialog welcomeDialog;
	private AlertDialog periodSetDialog;
	private AlertDialog daySetDialog;
	private AlertDialog localeSetDialog;
	private AlertDialog endDialog;
	private View periodView;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setRequestedOrientation(LinearLayout.VERTICAL);
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
					EditText editText = (EditText)periodView.findViewById(R.id.cycle_length);
					editText.setText(String.valueOf(Integer.valueOf(editText.getText().toString()) + 1));
				}
			});
		((Button)periodView.findViewById(R.id.btn_cycle_minus))
			.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					EditText editText = (EditText)periodView.findViewById(R.id.cycle_length);
					editText.setText(String.valueOf(Integer.valueOf(editText.getText().toString()) - 1));
				}
			});
		((Button)periodView.findViewById(R.id.btn_length_plus))
			.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					EditText editText = (EditText)periodView.findViewById(R.id.period_length);
					editText.setText(String.valueOf(Integer.valueOf(editText.getText().toString()) + 1));
				}
			});
		((Button)periodView.findViewById(R.id.btn_length_minus))
		.setOnClickListener(new View.OnClickListener() {				
			@Override
			public void onClick(View v) {
				EditText editText = (EditText)periodView.findViewById(R.id.period_length);
				editText.setText(String.valueOf(Integer.valueOf(editText.getText().toString()) - 1));
			}
		});
		periodSetDialog = new AlertDialog.Builder(this).setTitle("2/5")
			.setView(periodView)
			.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					welcomeDialog.show();
				}
			})
			.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					SharedPreferences appShared = 
	                        getApplicationContext().getSharedPreferences(null, MODE_PRIVATE);
					Editor editor = appShared.edit();
					EditText cycleLength = (EditText)periodView.findViewById(R.id.cycle_length);
					EditText periodLength = (EditText)periodView.findViewById(R.id.period_length);
					editor.putInt(Utils.SHARED_PREF_CYCLE_LENGTH,Integer.valueOf(cycleLength.getText().toString()));
					editor.putInt(Utils.SHARED_PREF_PERIOD_LENGTH,Integer.valueOf(periodLength.getText().toString()));
					editor.commit();
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
		final RadioGroup daygroup = (RadioGroup)daySetView.findViewById(R.id.day_group);
		
		String[] dayStrings = getResources().getStringArray(R.array.dayset_values);
		for (int i = 0; i < dayStrings.length; i++) {
			RadioButton radioBtn = new RadioButton(this);
			radioBtn.setText(dayStrings[i]);
			radioBtn.setId(i);
			daygroup.addView(radioBtn);
		}
		daygroup.check(0);
		daygroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                SharedPreferences appShared = 
                        getApplicationContext().getSharedPreferences(null, MODE_PRIVATE);
                Editor editor = appShared.edit();
                editor.putInt(Utils.SHARED_PREF_START_DAY,checkedId);
                editor.commit();                
                
            }});
		daySetDialog = new AlertDialog.Builder(this).setTitle("3/5")
			.setView(daySetView)
			.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					periodSetDialog.show();
				}
			})
			.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
	                
	                if(daygroup.getCheckedRadioButtonId() == 0) {
	                    SharedPreferences appShared = 
	                            getApplicationContext().getSharedPreferences(null, MODE_PRIVATE);
	                    Editor editor = appShared.edit();
	                    editor.putInt(Utils.SHARED_PREF_START_DAY,Calendar.getInstance().getFirstDayOfWeek());
	                    editor.commit(); 
	                }
	                localeSetDialog.show();
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
		final RadioGroup localeGroup = (RadioGroup)lanSetView.findViewById(R.id.lan_group);
        ArrayList<String> localeList = new ArrayList<String>(Arrays.asList(Resources.getSystem().getAssets().getLocales()));
		final String[] locales = getAssets().getLocales();
		for (int i = 1; i < localeList.size(); i++) {
			RadioButton radioBtn = new RadioButton(this);
			Locale locale = new Locale(localeList.get(i));
			radioBtn.setText(locale.getDisplayLanguage());
			radioBtn.setId(i);
			localeGroup.addView(radioBtn);
		}
		localeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                SharedPreferences appShared = 
                        getApplicationContext().getSharedPreferences(null, MODE_PRIVATE);
                Editor editor = appShared.edit();
                editor.putString(Utils.SHARED_PREF_LOCALE,locales[checkedId]);
                editor.commit();                   
            }
		});
		localeSetDialog = new AlertDialog.Builder(this).setTitle("4/5")
			.setView(lanSetView)
			.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					daySetDialog.show();
				}
			})
			.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					endDialog.show();
				}
			})
			.create();
		localeSetDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {	
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					return true;
				}
				
				return false;
			}
		});
		
		endDialog = new AlertDialog.Builder(this).setTitle("5/5").setMessage(R.string.first_run_finish_1)
			.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					localeSetDialog.show();
				}
			})
			.setPositiveButton(R.string.start_to_use_calendar, new DialogInterface.OnClickListener() {				
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
