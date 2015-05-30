/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.tbr3mobile;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import org.irdresearch.tbr3mobile.shared.AlertType;
import org.irdresearch.tbr3mobile.util.DatabaseUtil;
import org.irdresearch.tbr3mobile.util.ServerService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;

public class MainActivity extends Activity
{
	public static final String	TAG	= "MainActivity";
	private static DatabaseUtil	dbUtil;
	public static ServerService	serverService;
	Button						screening;
	Button						screeningLog;

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		MainActivity.resetPreferences (this);
		// Set theme
		setTheme (App.getTheme ());
		super.onCreate (savedInstanceState);
		serverService = new ServerService (this);
		try
		{
			dbUtil = new DatabaseUtil (this);
			dbUtil.buildDatabase (false);
		}
		catch (Exception e)
		{
			Log.e (TAG, e.getMessage ());
		}
		// Check connection with server
		if (!serverService.checkInternetConnection ())
		{
			AlertDialog alertDialog = App.getAlertDialog (this, AlertType.ERROR, getResources ().getString (R.string.data_connection_error));
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
			// runTests ();
			Intent intent = new Intent (this, LoginActivity.class);
			startActivity (intent);
			finish ();
		}
	}
	
	/**
	 * Reads preferences from application preferences and loads into App class
	 * members
	 */
	public static void resetPreferences (Context context)
	{
		PreferenceManager.setDefaultValues (context, R.xml.preferences, false);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences (context);
		App.setServer (preferences.getString (Preferences.SERVER, ""));
		App.setUseSsl (preferences.getBoolean (Preferences.USE_SSL, true));
		App.setUsername (preferences.getString (Preferences.USERNAME, ""));
		App.setPassword (preferences.getString (Preferences.PASSWORD, ""));
		App.setSupportContact (preferences.getString (Preferences.SUPPORT_CONTACT, ""));
		App.setCity (preferences.getString (Preferences.CITY, ""));
		App.setCountry (preferences.getString (Preferences.COUNTRY, ""));
		String locations = preferences.getString (Preferences.LOCATIONS, "");
		App.setLocations (locations.split (","));
		App.setDelay (Integer.parseInt (preferences.getString (Preferences.DELAY, "30000")));
		Locale locale = new Locale (preferences.getString (Preferences.LANGUAGE, "en").substring (0, 2));
		Locale.setDefault (locale);
		Configuration config = new Configuration ();
		config.locale = locale;
		context.getApplicationContext ().getResources ().updateConfiguration (config, null);
		App.setCurrentLocale (locale);
		String version = "0";
		try
		{
			version = context.getPackageManager ().getPackageInfo (context.getPackageName (), 0).versionName;
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace ();
		}
		App.setVersion (version);
	}

	public void runTests ()
	{
		final String TAG = "Test";
		Log.d (TAG, "Running tests");
		// Testing ServerService methods
		if (!serverService.getIdentifierTypeUuid ().equals ("OpenMRS Identification Number"))
			Log.d (TAG, "ServerService.getIdentifierTypeUuid() failed");
		if (serverService.getLocations ().length < 1)
			Log.d (TAG, "ServerService.getLocations() returned 0 results");
		// Setting required variables
		serverService.setCurrentUser ("owais");
		serverService.setCurrentLocation ("IRD");
		// Test Screening form
		{
			String patientId = "01130700002-3";
			String contactId = "021306130022-3";
			final ContentValues values = new ContentValues ();
			values.put ("formDate", App.getSqlDate (new Date ()));
			values.put ("location", App.getCurrentLocation ().getName ());
			values.put ("firstName", "Cedric");
			values.put ("lastName", "Diggory");
			values.put ("age", "20");
			values.put ("gender", "M");
			values.put ("patientId", patientId);
			final ArrayList<String[]> observations = new ArrayList<String[]> ();
			observations.add (new String[] {"Height (cm)", "130"});
			observations.add (new String[] {"Weight (kg)", "68.5"});
			observations.add (new String[] {"Cough", "Yes"});
			observations.add (new String[] {"Cough Duration", "5"});
			observations.add (new String[] {"Cough Duration Modifier", "Days"});
			observations.add (new String[] {"Fever", "Yes"});
			observations.add (new String[] {"Haemoptysis", "Yes"});
			observations.add (new String[] {"Night Sweats", "Yes"});
			observations.add (new String[] {"Weight Loss", "Yes"});
			observations.add (new String[] {"Fatigue", "Yes"});
			observations.add (new String[] {"Appetite Loss", "Yes"});
			observations.add (new String[] {"Diabetes", "Yes"});
			observations.add (new String[] {"Family Diabetes", "Yes"});
			observations.add (new String[] {"Hypertension", "Yes"});
			observations.add (new String[] {"Breathing Shortness", "Yes"});
			observations.add (new String[] {"Active Breathing Shortness", "Yes"});
			observations.add (new String[] {"Wheezing", "Yes"});
			observations.add (new String[] {"Smoking", "Yes"});
			observations.add (new String[] {"Smoking History", "Yes"});
			observations.add (new String[] {"CXR", "Yes"});
			observations.add (new String[] {"Blood Glucose", "Yes"});
			observations.add (new String[] {"GXP", "Yes"});
			observations.add (new String[] {"Spirometry", "Yes"});
			observations.add (new String[] {"Referral Voucher", "Yes"});
			observations.add (new String[] {"Voucher Type", "Contact"});
			observations.add (new String[] {"Contact Patient ID", contactId});
			AsyncTask<String, String, String> updateTask = new AsyncTask<String, String, String> ()
			{
				@Override
				protected String doInBackground (String... params)
				{
					String result = serverService.saveScreening ("Screening", values, observations.toArray (new String[][] {}));
					return result;
				}

				@Override
				protected void onProgressUpdate (String... values)
				{
				};

				@Override
				protected void onPostExecute (String result)
				{
					super.onPostExecute (result);
					if (!result.equals ("SUCCESS"))
						Log.d (TAG, "ServerService.saveScreening() failed with message: " + result);
				}
			};
			updateTask.execute ("");
			String patientUuid = serverService.getPatientUuid (patientId);
			Log.d (TAG, patientUuid);
		}

		// Test Client Info form
		{
			String patientId = "01130700002-3";
			final ContentValues values = new ContentValues ();
			values.put ("formDate", App.getSqlDate (new Date ()));
			values.put ("location", App.getCurrentLocation ().getName ());
			values.put ("patientId", patientId);
			values.put ("phone1", "00923453174588");
			values.put ("phone1Owner", "");
			values.put ("phone2", "+92213699568");
			values.put ("phone2Owner", "Home");
			values.put ("address1", "E-310, Emerald Street");
			values.put ("town", "Saddar Town");
			values.put ("city", App.getCity ());
			values.put ("country", App.getCountry ());
			AsyncTask<String, String, String> updateTask = new AsyncTask<String, String, String> ()
			{
				@Override
				protected String doInBackground (String... params)
				{
					String result = serverService.saveCustomerInfo ("Customer Information", values);
					return result;
				}

				@Override
				protected void onProgressUpdate (String... values)
				{
				};

				@Override
				protected void onPostExecute (String result)
				{
					super.onPostExecute (result);
					if (!result.equals ("SUCCESS"))
						Log.d (TAG, "ServerService.saveScreening() failed with message: " + result);
				}
			};
			updateTask.execute ("");
			String patientUuid = serverService.getPatientUuid (patientId);
			Log.d (TAG, patientUuid);
		}

		// Test Non-Pulmonary Screening form
		{
			String patientId = "011307000034-6";
			final ContentValues values = new ContentValues ();
			values.put ("formDate", App.getSqlDate (new Date ()));
			values.put ("location", App.getCurrentLocation ().getName ());
			values.put ("firstName", "Minerva");
			values.put ("lastName", "McGonagall");
			values.put ("age", "55");
			values.put ("gender", "F");
			values.put ("patientId", patientId);
			final ArrayList<String[]> observations = new ArrayList<String[]> ();
			observations.add (new String[] {"Height (cm)", "138"});
			observations.add (new String[] {"Weight (kg)", "54"});
			AsyncTask<String, String, String> updateTask = new AsyncTask<String, String, String> ()
			{
				@Override
				protected String doInBackground (String... params)
				{
					String result = serverService.saveScreening ("Non-Pulmonary Suspect", values, observations.toArray (new String[][] {}));
					return result;
				}

				@Override
				protected void onProgressUpdate (String... values)
				{
				};

				@Override
				protected void onPostExecute (String result)
				{
					super.onPostExecute (result);
					if (!result.equals ("SUCCESS"))
						Log.d (TAG, "ServerService.saveScreening() failed with message: " + result);
				}
			};
			updateTask.execute ("");
			String patientUuid = serverService.getPatientUuid (patientId);
			Log.d (TAG, patientUuid);
		}

		// Test Test Indication form
	}
}
