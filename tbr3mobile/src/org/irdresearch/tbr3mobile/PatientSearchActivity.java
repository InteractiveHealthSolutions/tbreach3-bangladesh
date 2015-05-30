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

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;
import org.irdresearch.tbr3mobile.shared.AlertType;
import org.irdresearch.tbr3mobile.util.RegexUtil;
import org.irdresearch.tbr3mobile.util.ServerService;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class PatientSearchActivity extends Activity
{
	public static final String			TAG				= "PatientSearchActivity";
	public static final String			SEARCH_RESULT	= "SEARCH_RESULT";
	private static final AtomicInteger	counter			= new AtomicInteger ();
	public static ServerService			serverService;
	public static ProgressDialog		loading;

	TableLayout							searchLayout;
	RadioGroup							genderGroup;
	RadioButton							male;
	RadioButton							female;
	
	Spinner								ageGroup;
	EditText							firstName;
	EditText							lastName;
	
	Button								search;
	Button								searchAgain;
	
	ScrollView							searchResultsScrollView;
	LinearLayout						searchResultsLayout;
	RadioGroup							patientsRadioGroup;
	Animation							alphaAnimation;

	protected void onCreate (Bundle savedInstanceState)
	{
		setTheme (App.getTheme ());
		setContentView (R.layout.patient_search);
		super.onCreate (savedInstanceState);
		serverService = new ServerService (this);
		loading = new ProgressDialog (this);
		searchLayout = (TableLayout) findViewById (R.search_id.searchLayout);
		firstName = (EditText) findViewById (R.search_id.firstNameEditText);
		lastName = (EditText) findViewById (R.search_id.lastNameEditText);
		genderGroup = (RadioGroup) findViewById (R.search_id.genderRadioGroup);
		male = (RadioButton) findViewById (R.search_id.maleRadioButton);
		female = (RadioButton) findViewById (R.search_id.femaleRadioButton);
		ageGroup = (Spinner) findViewById (R.search_id.ageGroupSpinner);
		search = (Button) findViewById (R.search_id.searchButton);
		searchAgain = (Button) findViewById (R.search_id.searchAgainButton);
		searchResultsScrollView = (ScrollView) findViewById (R.search_id.resultsScrollView);
		searchResultsLayout = (LinearLayout) findViewById (R.search_id.searchResultsLayout);
		patientsRadioGroup = (RadioGroup) findViewById (R.search_id.patientsRadioGroup);
		alphaAnimation  = AnimationUtils.loadAnimation(this, R.anim.alpha_animation);
		patientsRadioGroup.setOnCheckedChangeListener (new OnCheckedChangeListener ()
		{
			public void onCheckedChanged (RadioGroup radioGroup, int checkedId)
			{
				RadioButton radio = (RadioButton) findViewById (radioGroup.getCheckedRadioButtonId ());
				if (radio.isChecked ())
				{
					String patientId = radio.getTag ().toString ();
					Intent intent = new Intent ();
					intent.putExtra (PatientSearchActivity.SEARCH_RESULT, patientId);
					setResult (RESULT_OK, intent);
				}
				finish ();
			}
		});
		searchAgain.setOnClickListener (new OnClickListener ()
		{
			public void onClick (View view)
			{
				view.startAnimation (alphaAnimation);
				searchLayout.setVisibility (View.VISIBLE);
				searchResultsScrollView.setVisibility (View.GONE);
			}
		});
		search.setOnClickListener (new OnClickListener ()
		{
			public void onClick (View view)
			{
				final String gender = male.isChecked () ? "M" : "F";
				final String[] ages = App.get (ageGroup).replaceAll (" ", "").split ("-");
				final int ageStart = Integer.parseInt (ages[0]);
				final int ageEnd = Integer.parseInt (ages[1]);
				final String first = App.get (firstName);
				final String last = App.get (lastName);
				// Check if the names are provided
				if ("".equals (first) && "".equals (last))
				{
					App.getAlertDialog (PatientSearchActivity.this, AlertType.ERROR, getResources ().getString (R.string.empty_data)).show ();
					return;
				}
				AsyncTask<String, String, Object> searchTask = new AsyncTask<String, String, Object> ()
				{
					protected Object doInBackground (String... params)
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
						String[] patientList = null;
						try
						{
							patientList = serverService.searchPatient (first + " " + last, gender, ageStart, ageEnd);
						}
						catch (UnsupportedEncodingException e)
						{
							e.printStackTrace ();
						}
						return patientList;
					}

					protected void onPostExecute (Object result)
					{
						super.onPostExecute (result);
						loading.dismiss ();
						String[] patientList = (String[]) result;
						// Display a message if no results were found
						if (patientList == null || patientList.length == 0)
						{
							App.getAlertDialog (PatientSearchActivity.this, AlertType.INFO, getResources ().getString (R.string.patients_not_found)).show ();
						}
						else
						{
							patientsRadioGroup.removeAllViews ();
							for (String str : patientList)
							{
								RadioButton radioButton = new RadioButton (PatientSearchActivity.this);
								radioButton.setId (counter.getAndIncrement ());
								radioButton.setText (str);
								if (str.length () > RegexUtil.idLength)
								{
									radioButton.setTag (str.substring (0, RegexUtil.idLength));
								}
								patientsRadioGroup.addView (radioButton);
							}
							searchLayout.setVisibility (View.GONE);
							searchResultsScrollView.setVisibility (View.VISIBLE);
						}
					}
				};
				searchTask.execute ("");
			}
		});
	}
}
