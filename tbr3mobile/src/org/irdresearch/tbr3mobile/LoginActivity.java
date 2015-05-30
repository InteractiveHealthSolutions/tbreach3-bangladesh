/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * 
 */

package org.irdresearch.tbr3mobile;

import org.irdresearch.tbr3mobile.shared.AlertType;
import org.irdresearch.tbr3mobile.util.ServerService;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class LoginActivity extends Activity implements IActivity, OnClickListener, OnCheckedChangeListener
{
	private ServerService			serverService;
	protected static ProgressDialog	loading;
	EditText						username;
	EditText						password;
	Button							login;
	CheckBox						offline; 
	View[]							views;
	Animation						alphaAnimation;

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		setTheme (App.getTheme ());
		setContentView (R.layout.login);
		serverService = new ServerService (getApplicationContext ());
		loading = new ProgressDialog (this);
		username = (EditText) findViewById (R.login_id.usernameEditText);
		password = (EditText) findViewById (R.login_id.passwordEditText);
		login = (Button) findViewById (R.login_id.loginButton);
		offline = (CheckBox) findViewById (R.login_id.offlineCheckBox);
		alphaAnimation = AnimationUtils.loadAnimation (this, R.anim.alpha_animation);
		login.setOnClickListener (this);
		offline.setOnCheckedChangeListener (this);
		views = new View[] {username, password, login};
		super.onCreate (savedInstanceState);
		initView (views);
	}

	@Override
	public void initView (View[] views)
	{
		if (!App.getUsername ().equals (""))
		{
			serverService.setCurrentUser (App.get (username));
			Intent intent = new Intent (this, MainMenuActivity.class);
			startActivity (intent);
			finish ();
		}
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		switch (item.getItemId ())
		{
			case R.menu_id.itemPreferences :
				startActivity (new Intent (this, Preferences.class));
				break;
		}
		return true;
	}

	@Override
	protected void onStop ()
	{
		super.onStop ();
		finish ();
	}

	@Override
	public void updateDisplay ()
	{
	}

	@Override
	public boolean validate ()
	{
		boolean valid = true;
		StringBuffer message = new StringBuffer ();
		// Validate mandatory controls
		if (App.get (username).equals (""))
		{
			valid = false;
			message.append (username.getTag () + ". ");
			((EditText) username).setHintTextColor (getResources ().getColor (R.color.Red));
		}
		if (App.get (password).equals (""))
		{
			valid = false;
			message.append (password.getTag () + ". ");
			((EditText) password).setHintTextColor (getResources ().getColor (R.color.Red));
		}
		if (!valid)
		{
			message.append (getResources ().getString (R.string.empty_data) + "\n");
		}
		if (!valid)
		{
			App.getAlertDialog (this, AlertType.ERROR, message.toString ()).show ();
		}
		return valid;
	}

	@Override
	public boolean submit ()
	{
		if (validate ())
		{
			// Authenticate from server
			AsyncTask<String, String, Boolean> authenticationTask = new AsyncTask<String, String, Boolean> ()
			{
				@Override
				protected Boolean doInBackground (String... params)
				{
					runOnUiThread (new Runnable ()
					{
						@Override
						public void run ()
						{
							loading.setIndeterminate (true);
							loading.setCancelable (false);
							loading.setMessage (getResources ().getString (R.string.loading_message));
							loading.show ();
						}
					});
					App.setUsername (App.get (username));
					App.setPassword (App.get (password));
					String defaultUser = getResources ().getString (R.string.default_user);
					String defaultPassword = getResources ().getString (R.string.default_password);
					if ((App.isOfflineMode ()) || (App.get (username).equalsIgnoreCase (defaultUser) && App.get (password).equals (defaultPassword)))
						return true;
					//TODO Uncomment when live
					boolean exists = serverService.authenticate ();
					return exists;
					
					//return true;
				}

				@Override
				protected void onProgressUpdate (String... values)
				{
				};

				@Override
				protected void onPostExecute (Boolean result)
				{
					super.onPostExecute (result);
					loading.dismiss ();
					if (result)
					{
						serverService.setCurrentUser (App.get (username));
						// Save username and password in preferences
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (LoginActivity.this);
						SharedPreferences.Editor editor = preferences.edit ();
						editor.putString (Preferences.USERNAME, App.getUsername ());
						editor.putString (Preferences.PASSWORD, App.getPassword ());
						editor.apply ();
						Intent intent = new Intent (LoginActivity.this, MainMenuActivity.class);
						startActivity (intent);
						finish ();
					}
					else
					{
						App.setUsername ("");
						App.setPassword ("");
						Toast toast = Toast.makeText (LoginActivity.this, getResources ().getString (R.string.authentication_error), App.getDelay ());
						toast.setGravity (Gravity.CENTER, 0, 0);
						toast.show ();
					}
				}
			};
			authenticationTask.execute ("");
		}
		return false;
	}

	@Override
	public void onClick (View view)
	{
		view.startAnimation (alphaAnimation);
		switch (view.getId ())
		{
			case R.login_id.loginButton :
				submit ();
				break;
		}
	}

	@Override
	public void onCheckedChanged (CompoundButton button, boolean state)
	{
		/*switch (button.getId ())
		{
			case R.login_id.offlineCheckBox :
				App.setOfflineMode (offline.isChecked ());
				break;
		}*/
	}
}
