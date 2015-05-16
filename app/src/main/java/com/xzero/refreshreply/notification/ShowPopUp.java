package com.xzero.refreshreply.notification;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.xzero.refreshreply.R;
import com.xzero.refreshreply.models.Ad;

public class ShowPopUp extends Activity implements OnClickListener {

	Button ok;
	Button cancel;
	
	boolean click = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Ad ad = (Ad)getIntent().getSerializableExtra("ad");
		setTitle("You have new sale");
		setContentView(R.layout.popupdialog);
		ok = (Button)findViewById(R.id.popOkB);
		ok.setOnClickListener(this);
		cancel = (Button)findViewById(R.id.popCancelB);
		cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		finish();
	}
}