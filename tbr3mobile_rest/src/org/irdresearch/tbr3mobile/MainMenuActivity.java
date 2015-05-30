/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Main menu Activity
 */

package org.irdresearch.tbr3mobile;

import java.util.ArrayList;
import java.util.List;
import org.irdresearch.tbr3mobile.model.Location;
import org.irdresearch.tbr3mobile.shared.AlertType;
import org.irdresearch.tbr3mobile.util.DatabaseUtil;
import org.irdresearch.tbr3mobile.util.MetadataUtil;
import org.irdresearch.tbr3mobile.util.ServerService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class MainMenuActivity extends Activity implements IActivity, OnClickListener, OnItemSelectedListener
{
	private static final String		TAG					= "MainMenuActivity";
	private static final int		LOCATIONS_DIALOG	= 1;
	private static final int		FORMS_DIALOG	= 2;
	private static ServerService	serverService;

	static ProgressDialog			loading;
	Spinner							location;
	Button							selectLocationsButton;
	Button							screening;
	Button							paediatricScreening;
	Button							nonPulmonarySuspect;
	Button							customerInfoButton;
	Button							testIndication;
	Button							feedback;
	Animation						alphaAnimation;

	Location[]						locations;
	View[]							views;

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		setTheme (App.getTheme ());
		super.onCreate (savedInstanceState);
		setContentView (R.layout.main_menu);
		Configuration config = new Configuration ();
		config.locale = App.getCurrentLocale ();
		getApplicationContext ().getResources ().updateConfiguration (config, null);
		serverService = new ServerService (getApplicationContext ());
		loading = new ProgressDialog (this);

		location = (Spinner) findViewById (R.main_id.locationSpinner);
		selectLocationsButton = (Button) findViewById (R.main_id.selectLocationsButton);
		screening = (Button) findViewById (R.main_id.screeningButton);
		paediatricScreening = (Button) findViewById (R.main_id.paediatricButton);
		nonPulmonarySuspect = (Button) findViewById (R.main_id.nonPulmonarySuspectButton);
		customerInfoButton = (Button) findViewById (R.main_id.customerInfoButton);
		testIndication = (Button) findViewById (R.main_id.testIndicationButton);
		feedback = (Button) findViewById (R.main_id.feedbackButton);
		alphaAnimation = AnimationUtils.loadAnimation (this, R.anim.alpha_animation);
		
		// Disable all forms that cannot be filled offline
		if (App.isOfflineMode ())
		{
			customerInfoButton.setEnabled (false);
			testIndication.setEnabled (false);
		}
		else
		{
			//TODO: Not Yet! Offline forms!
			// Check if there are any forms to be submitted
		/*	String[][] localForms = serverService.getLocalForms ();
			try{
				if (localForms.length > 0)
				{
					showDialog (FORMS_DIALOG);
				}
			}
			catch(Exception e){
				Log.e ("localForms", "No forms avaliable!", e);
			} */
			
		}
		views = new View[] {location, selectLocationsButton, screening, paediatricScreening, nonPulmonarySuspect, customerInfoButton, testIndication, feedback};
		for (View v : views)
		{
			if (v instanceof Spinner)
			{
				((Spinner) v).setOnItemSelectedListener (this);
			}
			else if (v instanceof Button)
			{
				((Button) v).setOnClickListener (this);
			}
		}
		initView (views);
	}

	public void initView (View[] views)
	{
		locations = serverService.getLocations ();
		List<String> list = new ArrayList<String> ();
		// If locations are selected, fill them. Otherwise, fill all locations
		if (App.getLocations ().length > 0)
		{
			for (String s : App.getLocations ())
				if (!s.equals (""))
					list.add (s);
		}
		if (list.size () == 0)
		{
			list.clear ();
			for (Location l : locations)
				list.add (l.getName ());
		}
		location.setAdapter (App.getAdapter (this, list.toArray (new String[] {})));
	}

	public void updateDisplay ()
	{
	}

	public boolean validate ()
	{
		boolean valid = true;
		StringBuffer message = new StringBuffer ();
		// Validate mandatory controls
		if (location.getChildCount () == 0 || App.get (location).equals (""))
		{
			valid = false;
			message.append (location.getTag () + ":" + getResources ().getString (R.string.empty_selection));
		}
		if (!valid)
		{
			App.getAlertDialog (this, AlertType.ERROR, message.toString ()).show ();
		}
		return valid;
	}

	public boolean submit ()
	{
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater ().inflate (R.menu.update_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		switch (item.getItemId ())
		{
			case R.menu_id.updateMetadataService :
				AlertDialog confirmationDialog = new AlertDialog.Builder (this).create ();
				confirmationDialog.setTitle ("Update Primary data?");
				confirmationDialog.setMessage (getResources ().getString (R.string.update_metadata));
				confirmationDialog.setButton (AlertDialog.BUTTON_POSITIVE, "Yes", new AlertDialog.OnClickListener ()
				{
					@Override
					public void onClick (DialogInterface dialog, int which)
					{
						AsyncTask<String, String, String> updateTask = new AsyncTask<String, String, String> ()
						{
							@Override
							protected String doInBackground (String... params)
							{
								try
								{
									if (!serverService.checkInternetConnection ())
									{
										AlertDialog alertDialog = App.getAlertDialog (MainMenuActivity.this, AlertType.ERROR, getResources ().getString (R.string.data_connection_error));
										alertDialog.setTitle (getResources ().getString (R.string.error_title));
										alertDialog.setButton (AlertDialog.BUTTON_POSITIVE, "OK", new AlertDialog.OnClickListener ()
										{
											@Override
											public void onClick (DialogInterface dialog, int which)
											{
												finish ();
											}
										});
										alertDialog.show ();
									}
									else
									{
										// Operations on UI elements can be
										// performed only in UI threads. Damn!
										// WHY?
										runOnUiThread (new Runnable ()
										{
											@Override
											public void run ()
											{
												loading.setIndeterminate (true);
												loading.setCancelable (false);
												loading.show ();
											}
										});
										// Update database
										DatabaseUtil util = new DatabaseUtil (MainMenuActivity.this);
										publishProgress ("Saving trees...");
										util.buildDatabase (true);
										// Update metadata
										
										 MetadataUtil metadata = new MetadataUtil (MainMenuActivity.this);
										 publishProgress ("Updating attributes and identifiers...");
										 metadata.fillPersonAttributesMetadata(); 
										 metadata.fillIdentifiersMetadata (); 
										 publishProgress ("Finding friends...");
										 metadata.fillUsersMetadata ();
										 metadata.fillProvidersMetadata ();
										 publishProgress ("Locating sites...");
										 metadata.fillLocationsMetadata ();
										 publishProgress ("Updating forms...");
										 metadata.fillEncounterTypesMetadata(); 
										 publishProgress ("Updating concepts...");
										 metadata.fillConceptsMetadata ();
										 
									}
								}
								catch (Exception e)
								{
									Log.e (TAG, e.getMessage ());
								}
								return "SUCCESS";
							}

							@Override
							protected void onProgressUpdate (String... values)
							{
								loading.setMessage (values[0]);
							};

							@Override
							protected void onPostExecute (String result)
							{
								super.onPostExecute (result);
								if (result.equals ("SUCCESS"))
								{
									loading.dismiss ();
									App.getAlertDialog (MainMenuActivity.this, AlertType.INFO, "Local database updated successfully.").show ();
									initView (views);
								}
							}
						};
						updateTask.execute ("");
					}
				});
				confirmationDialog.setButton (AlertDialog.BUTTON_NEGATIVE, "Cancel", new AlertDialog.OnClickListener ()
				{
					@Override
					public void onClick (DialogInterface dialog, int which)
					{
						// Do nothing
					}
				});
				confirmationDialog.show ();
				break;
		}
		return true;
	}

	@Override
	protected Dialog onCreateDialog (int id)
	{
		Dialog dialog = super.onCreateDialog (id);
		AlertDialog.Builder builder = new AlertDialog.Builder (this);
		final ArrayList<String> selectedItems = new ArrayList<String> ();
		switch (id)
		{
		// Show a list of all locations to choose. This is to limit the
		// locations displayed on site spinner
			case LOCATIONS_DIALOG :
				builder.setTitle (getResources ().getString (R.string.multi_select_hint));
				Location[] locationsList = serverService.getLocations ();
				final ArrayList<CharSequence> locations = new ArrayList<CharSequence> ();
				for (Location location : locationsList)
					locations.add (location.getName ());
				final EditText locationText = new EditText (this);
				locationText.setTag ("Location");
				locationText.setHint (R.string.location_hint);
				builder.setView (locationText);
				builder.setPositiveButton (R.string.save, new DialogInterface.OnClickListener ()
				{
					@Override
					public void onClick (DialogInterface dialogInterface, int i)
					{
						String selected = App.get (locationText);
						if (selected.equals (""))
						{
							Toast toast = Toast.makeText (MainMenuActivity.this, "", App.getDelay ());
							toast.setText (R.string.empty_data);
							toast.setGravity (Gravity.CENTER, 0, 0);
							toast.show ();
							return;
						}
						// Try to fetch from local DB or Server
						Location location = serverService.getLocation (selected);
						if (location == null)
						{
							App.getAlertDialog (MainMenuActivity.this, AlertType.ERROR, getResources ().getString (R.string.item_not_found)).show ();
							return;
						}
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (MainMenuActivity.this);
						SharedPreferences.Editor editor = preferences.edit ();
						editor.putString (Preferences.LOCATIONS, location.getName ());
						editor.apply ();
						// Filhaal, we are using only single location
						App.setLocations (new String[] {location.getName ()});
						initView (views);
					}
				});
				builder.setNegativeButton (R.string.cancel, null);
				dialog = builder.create ();
				break;
			case FORMS_DIALOG:
				builder.setTitle (getResources ().getString (R.string.multi_select_hint));
				builder.setIcon (R.drawable.question);
				builder.setMessage (this.getResources ().getString (R.string.submit_forms));
				String[][] savedForms = serverService.getSavedForms ();
				final ArrayList<String> forms = new ArrayList<String> ();
				for (int i = 0; i < savedForms.length; i++)
				{
					forms.add (savedForms[i][0] + "(" + savedForms[i][1] + ")");
				}
				builder.setMultiChoiceItems (forms.toArray (new CharSequence[] {}), null, new DialogInterface.OnMultiChoiceClickListener ()
				{
					@Override
					public void onClick (DialogInterface dialog, int index, boolean isChecked)
					{
						if (isChecked)
						{
							selectedItems.add (forms.get (index).toString ());
						}
						else if (selectedItems.contains (index))
						{
							selectedItems.remove (Integer.valueOf (index));
						}
					}
				});
				builder.setPositiveButton (R.string.save, new DialogInterface.OnClickListener ()
				{
					@Override
					public void onClick (DialogInterface dialogInterface, int i)
					{
						// TODO: Try to submit all the forms checked
						
						// TODO: Maintain a log of forms that failed to submit and report to user at the end
					}
				});
				builder.setNegativeButton (R.string.cancel, null);
				dialog = builder.create ();
				break;
		}
		return dialog;
	}

	/**
	 * Shows options to Exit and Log out
	 */
	@Override
	public void onBackPressed ()
	{
		AlertDialog confirmationDialog = new AlertDialog.Builder (this).create ();
		confirmationDialog.setTitle (getResources ().getString (R.string.exit_application));
		confirmationDialog.setMessage (getResources ().getString (R.string.exit_operation));
		confirmationDialog.setButton (AlertDialog.BUTTON_NEGATIVE, getResources ().getString (R.string.exit), new AlertDialog.OnClickListener ()
		{
			@Override
			public void onClick (DialogInterface dialog, int which)
			{
				finish ();
			}
		});
		confirmationDialog.setButton (AlertDialog.BUTTON_POSITIVE, getResources ().getString (R.string.logout), new AlertDialog.OnClickListener ()
		{
			@Override
			public void onClick (DialogInterface dialog, int which)
			{
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (MainMenuActivity.this);
				SharedPreferences.Editor editor = preferences.edit ();
				editor.putString (Preferences.USERNAME, "");
				editor.putString (Preferences.PASSWORD, "");
				editor.apply ();
				finish ();
			}
		});
		confirmationDialog.setButton (AlertDialog.BUTTON_NEUTRAL, getResources ().getString (R.string.cancel), new AlertDialog.OnClickListener ()
		{
			@Override
			public void onClick (DialogInterface dialog, int which)
			{
			}
		});
		confirmationDialog.show ();
	}

	@Override
	protected void onStop ()
	{
		super.onStop ();
		finish ();
	}

	@Override
	public void onClick (View view)
	{
		// Return if trying to open any form without selecting location
//		if (view.getId () != R.main_id.selectLocationsButton && !validate ())
//		{
//			return;
//		}
		Toast toast = Toast.makeText (view.getContext (), "", App.getDelay ());
		toast.setGravity (Gravity.CENTER, 0, 0);
		view.startAnimation (alphaAnimation);
		switch (view.getId ())
		{
			case R.main_id.selectLocationsButton :
				showDialog (LOCATIONS_DIALOG);
				break;
			case R.main_id.screeningButton :
				Intent screeningIntent = new Intent (this, ScreeningActivity.class);
				startActivity (screeningIntent);
				break;
			case R.main_id.paediatricButton :
				Intent paediatricScreeningIntent = new Intent (this, PaediatricScreeningActivity.class);
				startActivity (paediatricScreeningIntent);
				break;
			case R.main_id.nonPulmonarySuspectButton :
				Intent nonPulmonarySuspectIntent = new Intent (this, NonPulmonarySuspectActivity.class);
				startActivity (nonPulmonarySuspectIntent);
				break;
			case R.main_id.customerInfoButton :
				Intent customerInfoIntent = new Intent (this, CustomerInfoActivity.class);
				startActivity (customerInfoIntent);
				break;
			case R.main_id.testIndicationButton :
				Intent testIndicationIntent = new Intent (this, TestIndicationActivity.class);
				startActivity (testIndicationIntent);
				break;
			case R.main_id.feedbackButton :
				Intent feedbackIntent = new Intent (this, FeedbackActivity.class);
				startActivity (feedbackIntent);
				break;
			default :
				toast.setText (getResources ().getString (R.string.form_unavailable));
				toast.show ();
		}
	}

	@Override
	public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
	{
		Spinner spinner = (Spinner) parent;
		if (parent == location)
		{
			String selection = spinner.getSelectedItem ().toString ();
			serverService.setCurrentLocation (selection);
		}
	}

	@Override
	public void onNothingSelected (AdapterView<?> arg0)
	{
	}
}
