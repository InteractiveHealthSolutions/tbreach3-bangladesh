/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Non-Pulmonary Suspect Screening Activity
 */

package org.irdresearch.tbr3mobile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import org.irdresearch.tbr3mobile.custom.MyButton;
import org.irdresearch.tbr3mobile.custom.MyEditText;
import org.irdresearch.tbr3mobile.custom.MyRadioButton;
import org.irdresearch.tbr3mobile.custom.MyRadioGroup;
import org.irdresearch.tbr3mobile.custom.MyTextView;
import org.irdresearch.tbr3mobile.shared.AlertType;
import org.irdresearch.tbr3mobile.util.RegexUtil;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class NonPulmonarySuspectActivity extends AbstractFragmentActivity implements OnEditorActionListener
{
	// Views displayed in pages, sorted w.r.t. appearance on pager
	MyTextView		formDateTextView;
	MyButton		formDateButton;

	MyTextView		ageTextView;
	MyEditText		age;
	MyTextView		genderTextView;
	MyRadioGroup	gender;
	MyRadioButton	male;
	MyRadioButton	female;

	MyTextView		heightTextView;
	MyEditText		height;
	MyTextView		weightTextView;
	MyEditText		weight;
	MyTextView		bmiTextTextView;
	MyTextView		bmiTextView;

	MyTextView		firstNameTextView;
	MyEditText		firstName;
	MyTextView		lastNameTextView;
	MyEditText		lastName;

	MyTextView		patientIdTextView;
	MyEditText		patientId;
	MyButton		scanBarcode;

	/**
	 * Subclass representing Fragment for non-pulmonary suspect form
	 * 
	 * @author owais.hussain@irdresearch.org
	 * 
	 */
	@SuppressLint("ValidFragment")
	class NonPulmonarySuspectFragment extends Fragment
	{
		int	currentPage;

		@Override
		public void onCreate (Bundle savedInstanceState)
		{
			super.onCreate (savedInstanceState);
			Bundle data = getArguments ();
			currentPage = data.getInt ("current_page", 0);
		}

		@Override
		public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			// Return a layout of views from pre-filled ArrayList of groups
			if (currentPage != 0 && groups.size () != 0)
				return groups.get (currentPage - 1);
			else
				return null;
		}
	}

	/**
	 * Subclass for Pager Adapter. Uses NonPulmonarySuspect subclass as items
	 * 
	 * @author owais.hussain@irdresearch.org
	 * 
	 */
	class NonPulmonarySuspectFragmentPagerAdapter extends FragmentPagerAdapter
	{
		/** Constructor of the class */
		public NonPulmonarySuspectFragmentPagerAdapter (FragmentManager fragmentManager)
		{
			super (fragmentManager);
		}

		/** This method will be invoked when a page is requested to create */
		@Override
		public Fragment getItem (int arg0)
		{
			NonPulmonarySuspectFragment fragment = new NonPulmonarySuspectFragment ();
			Bundle data = new Bundle ();
			data.putInt ("current_page", arg0 + 1);
			fragment.setArguments (data);
			return fragment;
		}

		/** Returns the number of pages */
		@Override
		public int getCount ()
		{
			return PAGE_COUNT;
		}
	}

	@Override
	public void createViews (final Context context)
	{
		FORM_NAME = "Non-Pulmonary Suspect";
		TAG = "NonPulmonarySuspectActivity";
		PAGE_COUNT = 3;
		pager = (ViewPager) findViewById (R.template_id.pager);
		navigationSeekbar.setMax (PAGE_COUNT - 1);
		navigatorLayout = (LinearLayout) findViewById (R.template_id.navigatorLayout);
		// If the form consists only of single page, then hide the
		// navigatorLayout
		if (PAGE_COUNT < 2)
		{
			navigatorLayout.setVisibility (View.GONE);
		}
		FragmentManager fragmentManager = getSupportFragmentManager ();
		NonPulmonarySuspectFragmentPagerAdapter pagerAdapter = new NonPulmonarySuspectFragmentPagerAdapter (fragmentManager);
		pager.setAdapter (pagerAdapter);
		pager.setOffscreenPageLimit (PAGE_COUNT);
		// Create views for pages
		formDateTextView = new MyTextView (context, R.style.text, R.string.form_date);
		formDateButton = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.form_date, R.string.form_date);

		ageTextView = new MyTextView (context, R.style.text, R.string.age);
		age = new MyEditText (context, R.string.age, R.string.age_hint, InputType.TYPE_CLASS_NUMBER, R.style.edit, 3, false);
		genderTextView = new MyTextView (context, R.style.text, R.string.gender);
		male = new MyRadioButton (context, R.string.male, R.style.radio, R.string.male);
		female = new MyRadioButton (context, R.string.female, R.style.radio, R.string.female);
		gender = new MyRadioGroup (context, new MyRadioButton[] {male, female}, R.string.gender, R.style.radio, App.isLanguageRTL ());

		heightTextView = new MyTextView (context, R.style.text, R.string.height);
		height = new MyEditText (context, R.string.height, R.string.height_hint, InputType.TYPE_CLASS_NUMBER, R.style.edit, 3, false);
		weightTextView = new MyTextView (context, R.style.text, R.string.weight);
		weight = new MyEditText (context, R.string.weight, R.string.weight_hint, InputType.TYPE_CLASS_NUMBER, R.style.edit, 3, false);
		bmiTextTextView = new MyTextView (context, R.style.text, R.string.bmi);
		bmiTextView = new MyTextView (context, R.style.text, R.string.empty_string);

		firstNameTextView = new MyTextView (context, R.style.text, R.string.first_name);
		firstName = new MyEditText (context, R.string.first_name, R.string.first_name_hint, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 20, false);
		lastNameTextView = new MyTextView (context, R.style.text, R.string.last_name);
		lastName = new MyEditText (context, R.string.last_name, R.string.last_name_hint, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 20, false);

		patientIdTextView = new MyTextView (context, R.style.text, R.string.patient_id);
		patientId = new MyEditText (context, R.string.patient_id, R.string.patient_id_hint, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.idLength, false);
		scanBarcode = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.scan_barcode, R.string.scan_barcode);
		View[][] viewGroups = { {formDateTextView, formDateButton, age, genderTextView, gender}, {heightTextView, height, weightTextView, weight, bmiTextTextView, bmiTextView},
				{firstNameTextView, firstName, lastNameTextView, lastName, patientIdTextView, patientId, scanBarcode}};
		// Create layouts and store in ArrayList
		groups = new ArrayList<ViewGroup> ();
		for (int i = 0; i < PAGE_COUNT; i++)
		{
			LinearLayout layout = new LinearLayout (context);
			layout.setOrientation (LinearLayout.VERTICAL);
			for (int j = 0; j < viewGroups[i].length; j++)
			{
				layout.addView (viewGroups[i][j]);
			}
			ScrollView scrollView = new ScrollView (context);
			scrollView.setLayoutParams (new LayoutParams (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			scrollView.addView (layout);
			groups.add (scrollView);
		}
		// Set event listeners
		formDateButton.setOnClickListener (this);
		firstButton.setOnClickListener (this);
		lastButton.setOnClickListener (this);
		clearButton.setOnClickListener (this);
		saveButton.setOnClickListener (this);
		scanBarcode.setOnClickListener (this);
		navigationSeekbar.setOnSeekBarChangeListener (this);
		height.setOnEditorActionListener (this);
		weight.setOnEditorActionListener (this);
		views = new View[] {age, firstName, lastName, height, weight, patientId};
		for (View v : views)
		{
			if (v instanceof Spinner)
			{
				((Spinner) v).setOnItemSelectedListener (this);
			}
			else if (v instanceof CheckBox)
			{
				((CheckBox) v).setOnCheckedChangeListener (this);
			}
		}
		pager.setOnPageChangeListener (this);
		patientId.setOnLongClickListener (new OnLongClickListener ()
		{
			public boolean onLongClick (View view)
			{
				Intent intent = new Intent (context, PatientSearchActivity.class);
				startActivity (intent);
				return false;
			}
		});
		// Detect RTL language
		if (App.isLanguageRTL ())
		{
			Collections.reverse (groups);
			for (ViewGroup g : groups)
			{
				LinearLayout linearLayout = (LinearLayout) g.getChildAt (0);
				linearLayout.setGravity (Gravity.RIGHT);
			}
			for (View v : views)
			{
				if (v instanceof EditText)
				{
					((EditText) v).setGravity (Gravity.RIGHT);
				}
			}
		}
	}

	@Override
	public void initView (View[] views)
	{
		super.initView (views);
		formDate = Calendar.getInstance ();
		updateDisplay ();
		bmiTextView.setText ("0");
		male.setChecked (true);
	}

	@Override
	public void updateDisplay ()
	{
		formDateButton.setText (DateFormat.format ("dd-MMM-yyyy", formDate));
	}

	@Override
	public boolean validate ()
	{
		boolean valid = true;
		StringBuffer message = new StringBuffer ();
		// Validate mandatory controls
		View[] mandatory = {age, weight, height, firstName, lastName, patientId};
		for (View v : mandatory)
		{
			if (App.get (v).equals (""))
			{
				valid = false;
				message.append (v.getTag ().toString () + ". ");
				((EditText) v).setHintTextColor (getResources ().getColor (R.color.Red));
			}
		}
		if (!valid)
		{
			message.append (getResources ().getString (R.string.empty_data) + "\n");
		}
		if (App.get (patientId).equals (""))
		{
			valid = false;
			message.append (patientId.getTag ().toString () + ": " + getResources ().getString (R.string.empty_data) + "\n");
		}
		// Validate data
		if (valid)
		{
			if (!RegexUtil.isWord (App.get (firstName)))
			{
				valid = false;
				message.append (firstName.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				firstName.setTextColor (getResources ().getColor (R.color.Red));
			}
			if (!RegexUtil.isWord (App.get (lastName)))
			{
				valid = false;
				message.append (lastName.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				lastName.setTextColor (getResources ().getColor (R.color.Red));
			}
			if (!RegexUtil.isNumeric (App.get (age), false))
			{
				valid = false;
				message.append (age.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				age.setTextColor (getResources ().getColor (R.color.Red));
			}
			if (!RegexUtil.isNumeric (App.get (height), false))
			{
				valid = false;
				message.append (height.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				height.setTextColor (getResources ().getColor (R.color.Red));
			}
			if (!RegexUtil.isNumeric (App.get (weight), true))
			{
				valid = false;
				message.append (weight.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				weight.setTextColor (getResources ().getColor (R.color.Red));
			}
		}
		// Validate ranges
		try
		{
			// Form date range
			if (formDate.getTime ().after (Calendar.getInstance ().getTime ()))
			{
				valid = false;
				message.append (formDateButton.getTag () + ": " + getResources ().getString (R.string.invalid_date_or_time) + "\n");
			}
			// Age range
			int a = Integer.parseInt (App.get (age));
			if (a < 0 || a > 110)
			{
				valid = false;
				message.append (age.getTag ().toString () + ": " + getResources ().getString (R.string.out_of_range) + "\n");
			}
			// Height range
			if (!App.get (height).equals (""))
			{
				int h = Integer.parseInt (App.get (height));
				if (h < 30 || h > 250)
				{
					valid = false;
					message.append (height.getTag ().toString () + ": " + getResources ().getString (R.string.out_of_range) + "\n");
				}
			}
			// Weight range
			if (!App.get (weight).equals (""))
			{
				int w = Integer.parseInt (App.get (weight));
				if (w < 1 || w > 250)
				{
					valid = false;
					message.append (weight.getTag ().toString () + ": " + getResources ().getString (R.string.out_of_range) + "\n");
				}
			}
			if (!RegexUtil.isValidId (App.get (patientId)))
			{
				message.append (patientId.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
			}
		}
		catch (NumberFormatException e)
		{
		}
		if (!valid)
		{
			App.getAlertDialog (this, AlertType.ERROR, message.toString ()).show ();
		}
		return valid;
	}

	public boolean submit ()
	{
		if (validate ())
		{
			final ContentValues values = new ContentValues ();
			values.put ("formDate", App.getSqlDate (formDate));
			values.put ("location", App.getCurrentLocation ().getName ());
			values.put ("firstName", App.get (firstName));
			values.put ("lastName", App.get (lastName));
			values.put ("age", App.get (age));
			values.put ("gender", male.isChecked () ? "M" : "F");
			values.put ("patientId", App.get (patientId));
			final ArrayList<String[]> observations = new ArrayList<String[]> ();
			observations.add (new String[] {"Height (cm)", App.get (height)});
			observations.add (new String[] {"Weight (kg)", App.get (weight)});

			AsyncTask<String, String, String> updateTask = new AsyncTask<String, String, String> ()
			{
				@Override
				protected String doInBackground (String... params)
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
					String result = serverService.saveNonPulmonarySuspect (FORM_NAME, values, observations.toArray (new String[][] {}));
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
					loading.dismiss ();
					if (result.equals ("SUCCESS"))
					{
						App.getAlertDialog (NonPulmonarySuspectActivity.this, AlertType.INFO, getResources ().getString (R.string.inserted)).show ();
						initView (views);
					}
					else
					{
						App.getAlertDialog (NonPulmonarySuspectActivity.this, AlertType.ERROR, result).show ();
					}
				}
			};
			updateTask.execute ("");
		}
		return true;
	}

	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult (requestCode, resultCode, data);
		// Retrieve barcode scan results
		if (requestCode == Barcode.BARCODE_RESULT)
		{
			if (resultCode == RESULT_OK)
			{
				String str = data.getStringExtra (Barcode.SCAN_RESULT);
				// Check for valid Id
				if (RegexUtil.isValidId (str) && !RegexUtil.isNumeric (str, false))
				{
					patientId.setText (str);
				}
				else
				{
					App.getAlertDialog (this, AlertType.ERROR, patientId.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data)).show ();
				}
			}
			else if (resultCode == RESULT_CANCELED)
			{
				// Handle cancel
				App.getAlertDialog (this, AlertType.ERROR, getResources ().getString (R.string.operation_cancelled)).show ();
			}
			// Set the locale again, since the Barcode app restores system's locale because of orientation
			Locale.setDefault (App.getCurrentLocale ());
			Configuration config = new Configuration ();
			config.locale = App.getCurrentLocale ();
			getApplicationContext ().getResources ().updateConfiguration (config, null);
		}
	};

	@Override
	public void onClick (View view)
	{
		if (view == formDateButton)
		{
			showDialog (DATE_DIALOG_ID);
		}
		else if (view == firstButton)
		{
			gotoFirstPage ();
		}
		else if (view == lastButton)
		{
			gotoLastPage ();
		}
		else if (view == clearButton)
		{
			initView (views);
		}
		else if (view == saveButton)
		{
			submit ();
		}
		else if (view == scanBarcode)
		{
			Intent intent = new Intent (Barcode.BARCODE_INTENT);
			intent.putExtra (Barcode.SCAN_MODE, Barcode.QR_MODE);
			startActivityForResult (intent, Barcode.BARCODE_RESULT);
		}
	}

	@Override
	public void onCheckedChanged (CompoundButton button, boolean state)
	{
		// Not implemented
		patientId.setEnabled (true);
	}

	@Override
	public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
	{
		// Not implemented
	}

	@Override
	public boolean onLongClick (View v)
	{
		return false;
	}

	@Override
	public boolean onEditorAction (TextView v, int actionId, KeyEvent event)
	{
		if (v == weight || v == height)
		{
			if (!App.get (height).equals ("") && !App.get (weight).equals (""))
			{
				double w = Double.parseDouble (App.get (weight));
				double h = Double.parseDouble (App.get (height)) / 100;
				Long b = Math.round (w / (h * h));
				if (b < 10)
				{
					Toast toast = Toast.makeText (this, getResources ().getString (R.string.bmi_low), App.getDelay ());
					toast.setGravity (Gravity.CENTER, 0, 0);
					toast.show ();
				}
				bmiTextView.setText (String.valueOf (b.intValue ()));
				updateDisplay ();
			}
		}
		return true;
	}
}
