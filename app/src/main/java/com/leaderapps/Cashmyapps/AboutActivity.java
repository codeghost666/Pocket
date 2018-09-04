
/**
 * About Activity
 * 
 * @author DroidOXY
 */

package com.leaderapps.Cashmyapps;

import android.view.MenuItem;
import android.view.Menu;
import android.net.Uri;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;

import com.leaderapps.Cashmyapps.app.App;


public class AboutActivity extends BaseActivity {
	Button btnLogout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		getSupportActionBar().setIcon(R.drawable.ic_back_icon);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("About");

		btnLogout = (Button) findViewById(R.id.btnLogout);

		btnLogout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				App.getInstance().logout();

				Intent i = new Intent(getApplicationContext(), AppActivity.class);
				startActivity(i);

				ActivityCompat.finishAffinity(AboutActivity.this);
			}
		});

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_about, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				this.finish();
				break;

			case R.id.share:
                shareURL();       
                return true;
				
			case R.id.buy:
				openyash();
				return true;

			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private void openyash() {
		Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.leaderapps.Cashmyapps");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	

    // sharing
    public void shareURL() {
		try
		{ Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("text/plain");
			i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
			String sAux = Config.share_text+"\n";
			sAux = sAux + Config.share_link+"\n";
			i.putExtra(Intent.EXTRA_TEXT, sAux);
			startActivity(Intent.createChooser(i, "choose one"));
		}
		catch(Exception e)
		{ //e.toString();
		}
    }
	
}