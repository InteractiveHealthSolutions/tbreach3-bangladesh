/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Paediatric Suspect Screening Activity
 */

package org.irdresearch.tbr3mobile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import org.irdresearch.tbr3mobile.custom.MyButton;
import org.irdresearch.tbr3mobile.custom.MyCheckBox;
import org.irdresearch.tbr3mobile.custom.MyEditText;
import org.irdresearch.tbr3mobile.custom.MyRadioButton;
import org.irdresearch.tbr3mobile.custom.MyRadioGroup;
import org.irdresearch.tbr3mobile.custom.MySpinner;
import org.irdresearch.tbr3mobile.custom.MyTextView;
import org.irdresearch.tbr3mobile.shared.AlertType;
import org.irdresearch.tbr3mobile.shared.FormType;
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
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class PaediatricScreeningActivity extends AbstractFragmentActivity implements OnEditorActionListener
{
	// Views displayed in pages, sorted w.r.t. appearance on pager
	MyTextView		formDateTextView;
	MyButton		formDateButton;

	MyTextView		ageTextView;
	MyEditText		age;
	MySpinner		ageModifier;
	MyTextView		dobTextView;
	DatePicker		dobPicker;
	Calendar		dob;

	MyTextView		genderTextView;
	MyRadioGroup	gender;
	MyRadioButton	male;
	MyRadioButton	female;

	MyTextView		suspectTypeTextView;
	MyRadioGroup	suspectType;
	MyRadioButton	pulmonarySuspect;
	MyRadioButton	nonPulmonarySuspect;

	MyTextView		respirationRateTextView;
	MyEditText		respirationRate;

	MyTextView		coughTextView;
	MySpinner		cough;
	MyTextView		coughDurationTextView;
	MySpinner		coughDuration;

	MyTextView		feverTextView;
	MySpinner		fever;
	MyTextView		feverDurationTextView;
	MySpinner		feverDuration;

	MyTextView		familyTbTextView;
	MySpinner		familyTb;

	MyTextView		pneumoniaConclusionTextView;
	MyCheckBox		pneumoniaConclusion;

	MyTextView		tbConclusionTextView;
	MyCheckBox		tbConclusion;

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
		FORM_NAME = "Paediatric Screening";
		TAG = "PaediatricScreeningActivity";
		PAGE_COUNT = 8;
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
		age = new MyEditText (context, R.string.age, R.string.age_hint, InputType.TYPE_CLASS_NUMBER, R.style.edit, 2, false);
		ageModifier = new MySpinner (context, getResources ().getStringArray (R.array.duration_modifiers), R.string.age, R.string.option_hint);
		dobTextView = new MyTextView (context, R.style.text, R.string.dob);
		dobPicker = new DatePicker (context);
		ArrayList<View> touchables = dobPicker.getTouchables ();
		for (int i = 0; i < touchables.size (); i++)
		{
			if (i == 2 || i == 5 || i == 8)
				touchables.get (i).setBackgroundResource (R.drawable.numberpicker_down_normal);
			else if (i == 0 || i == 3 || i == 6)
				touchables.get (i).setBackgroundResource (R.drawable.numberpicker_up_normal);
			else
				touchables.get (i).setBackgroundResource (R.drawable.custom_button_beige);
		}
		dob = Calendar.getInstance ();

		genderTextView = new MyTextView (context, R.style.text, R.string.gender);
		male = new MyRadioButton (context, R.string.male, R.style.radio, R.string.male);
		female = new MyRadioButton (context, R.string.female, R.style.radio, R.string.female);
		gender = new MyRadioGroup (context, new MyRadioButton[] {male, female}, R.string.gender, R.style.radio, App.isLanguageRTL ());

		suspectTypeTextView = new MyTextView (context, R.style.text, R.string.suspect_type);
		pulmonarySuspect = new MyRadioButton (context, R.string.pulmonary, R.style.radio, R.string.pulmonary);
		nonPulmonarySuspect = new MyRadioButton (context, R.string.non_pulmonary, R.style.radio, R.string.non_pulmonary);
		suspectType = new MyRadioGroup (context, new MyRadioButton[] {pulmonarySuspect, nonPulmonarySuspect}, R.string.suspect_type, R.style.radio, App.isLanguageRTL ());

		respirationRateTextView = new MyTextView (context, R.style.text, R.string.respiratory_rate);
		respirationRate = new MyEditText (context, R.string.respiratory_rate, R.string.respiratory_rate_hint, InputType.TYPE_CLASS_NUMBER, R.style.edit, 3, false);

		coughTextView = new MyTextView (context, R.style.text, R.string.child_cough);
		cough = new MySpinner (context, getResources ().getStringArray (R.array.options), R.string.child_cough, R.string.option_hint);
		coughDurationTextView = new MyTextView (context, R.style.text, R.string.child_cough_duration);
		coughDuration = new MySpinner (context, getResources ().getStringArray (R.array.child_cough_durations), R.string.cough_duration, R.string.option_hint);

		feverTextView = new MyTextView (context, R.style.text, R.string.child_fever);
		fever = new MySpinner (context, getResources ().getStringArray (R.array.options), R.string.child_fever, R.string.option_hint);
		feverDurationTextView = new MyTextView (context, R.style.text, R.string.child_fever_duration);
		feverDuration = new MySpinner (context, getResources ().getStringArray (R.array.child_fever_durations), R.string.child_fever_duration, R.string.option_hint);

		familyTbTextView = new MyTextView (context, R.style.text, R.string.family_tb);
		familyTb = new MySpinner (context, getResources ().getStringArray (R.array.options), R.string.family_tb, R.string.option_hint);

		pneumoniaConclusionTextView = new MyTextView (context, R.style.text, R.string.pneumonia_conclusion);
		pneumoniaConclusion = new MyCheckBox (context, R.string.pneumonia_conclusion, R.style.edit, R.string.pneumonia_conclusion, false);
		pneumoniaConclusion.setClickable (false);

		tbConclusionTextView = new MyTextView (context, R.style.text, R.string.tb_conclusion);
		tbConclusion = new MyCheckBox (context, R.string.tb_conclusion, R.style.edit, R.string.tb_conclusion, false);
		tbConclusion.setClickable (false);

		firstNameTextView = new MyTextView (context, R.style.text, R.string.first_name);
		firstName = new MyEditText (context, R.string.first_name, R.string.first_name_hint, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 20, false);
		lastNameTextView = new MyTextView (context, R.style.text, R.string.last_name);
		lastName = new MyEditText (context, R.string.last_name, R.string.last_name_hint, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 20, false);

		patientIdTextView = new MyTextView (context, R.style.text, R.string.patient_id);
		patientId = new MyEditText (context, R.string.patient_id, R.string.patient_id_hint, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.idLength, false);
		scanBarcode = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.scan_barcode, R.string.scan_barcode);
		View[][] viewGroups = { {formDateTextView, formDateButton, ageTextView, age, ageModifier, genderTextView, gender}, {dobTextView, dobPicker}, {suspectTypeTextView, suspectType},
				{respirationRateTextView, respirationRate}, {coughTextView, cough, coughDurationTextView, coughDuration, feverTextView, fever, feverDurationTextView, feverDuration},
				{familyTbTextView, familyTb}, {pneumoniaConclusionTextView, pneumoniaConclusion, tbConclusionTextView, tbConclusion},
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
		pulmonarySuspect.setOnCheckedChangeListener (this);
		nonPulmonarySuspect.setOnCheckedChangeListener (this);
		age.setOnEditorActionListener (this);
		respirationRate.setOnEditorActionListener (this);
		views = new View[] {age, ageModifier, suspectType, respirationRate, cough, coughDuration, fever, feverDuration, familyTb, pneumoniaConclusion, tbConclusion, firstName, lastName, patientId};
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
		age.setOnFocusChangeListener (new OnFocusChangeListener ()
		{
			public void onFocusChange (View view, boolean state)
			{
				if (!state)
				{
					updateDob ();
				}
			}
		});
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
		dob.setTime (new Date ());
		male.setChecked (true);
		pulmonarySuspect.setChecked (true);
		pneumoniaConclusion.setChecked (false);
		tbConclusion.setChecked (false);
		firstNameTextView.setEnabled (false);
		firstName.setEnabled (false);
		lastNameTextView.setEnabled (false);
		lastName.setEnabled (false);
		patientIdTextView.setEnabled (false);
		patientId.setEnabled (false);
		scanBarcode.setEnabled (false);
		dobPicker.updateDate (dob.get (Calendar.YEAR), dob.get (Calendar.MONTH), dob.get (Calendar.DAY_OF_MONTH));
	}

	@Override
	public void updateDisplay ()
	{
		formDateButton.setText (DateFormat.format ("dd-MMM-yyyy", formDate));
		// Auto-populate suspect logic
		int respiratoryRate = 0;
		pneumoniaConclusion.setChecked (false);
		tbConclusion.setChecked (false);
		if (!"".equals (App.get (respirationRate)))
		{
			respiratoryRate = Integer.parseInt (App.get (respirationRate));
			Calendar now = Calendar.getInstance ();
			Double diff = Double.valueOf ((now.getTime ().getTime () - dob.getTime ().getTime ()) / 86400000);
			int ageInMonths = diff.intValue () / 30;
			// Pneumonia suspect logic
			boolean condition1 = ageInMonths < 2 && respiratoryRate >= 60;
			boolean condition2 = ageInMonths >= 2 && ageInMonths < 12 && respiratoryRate >= 50;
			boolean condition3 = ageInMonths >= 12 && respiratoryRate >= 40;
			boolean condition4 = cough.getSelectedItemPosition () == 1 && coughDuration.getSelectedItemPosition () == 0;
			if (condition1 | condition2 | condition3 | condition4)
			{
				pneumoniaConclusion.setChecked (true);
			}
		}
		if (!pneumoniaConclusion.isChecked () && (cough.getSelectedItemPosition () == 1 || fever.getSelectedItemPosition () == 1))
		{
			boolean hasCough = cough.getSelectedItemPosition () == 1 && (coughDuration.getSelectedItemPosition () == 1 || coughDuration.getSelectedItemPosition () == 2);
			boolean hasFever = fever.getSelectedItemPosition () == 1 && (feverDuration.getSelectedItemPosition () == 1 || feverDuration.getSelectedItemPosition () == 2);
			boolean hasFamilyTb = familyTb.getSelectedItemPosition () == 1;
			// TB suspect logic
			if ((hasCough & hasFever) || (hasCough & hasFamilyTb) || (hasFever & hasFamilyTb))
			{
				tbConclusion.setChecked (true);
			}
		}
	}

	/**
	 * Updates the DOB picker date
	 */
	private void updateDob ()
	{
		// Calculate dob by subtracting age in days from dob object
		if (!"".equals (App.get (age)))
		{
			int index = ageModifier.getSelectedItemPosition ();
			int multiplier = index == 0 ? 1 : index == 1 ? 7 : index == 2 ? 30 : index == 3 ? 365 : 0;
			int a = Integer.parseInt (App.get (age)) * multiplier;
			dob = Calendar.getInstance ();
			dob.add (Calendar.DAY_OF_YEAR, -a);
			dobPicker.updateDate (dob.get (Calendar.YEAR), dob.get (Calendar.MONTH), dob.get (Calendar.DAY_OF_MONTH));
		}
	}

	@Override
	public boolean validate ()
	{
		boolean valid = true;
		StringBuffer message = new StringBuffer ();
		// Validate mandatory controls
		View[] mandatory = {firstName, lastName, age, respirationRate};
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
		if (tbConclusion.isChecked () && App.get (patientId).equals (""))
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
			if (!RegexUtil.isValidId (App.get (patientId)))
			{
				valid = false;
				message.append (patientId.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				patientId.setTextColor (getResources ().getColor (R.color.Red));
			}
			if (!RegexUtil.isNumeric (App.get (age), false))
			{
				valid = false;
				message.append (age.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				age.setTextColor (getResources ().getColor (R.color.Red));
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
			if (Calendar.getInstance ().get (Calendar.YEAR) - dob.get (Calendar.YEAR) > 5)
			{
				valid = false;
				message.append (age.getTag ().toString () + ": " + getResources ().getString (R.string.out_of_range) + "\n");
			}
			if (!App.get (respirationRate).equals (""))
			{
				int d = Integer.parseInt (App.get (respirationRate));
				if (d < 10 || d > 120)
				{
					valid = false;
					message.append (respirationRate.getTag ().toString () + ": " + getResources ().getString (R.string.out_of_range) + "\n");
				}
			}
			if ((tbConclusion.isChecked () || pneumoniaConclusion.isChecked ()) && !RegexUtil.isValidId (App.get (patientId)))
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
			values.put ("location", App.getLocation ());
			values.put ("firstName", App.get (firstName));
			values.put ("lastName", App.get (lastName));
			int ageInYears = Calendar.getInstance ().get (Calendar.YEAR) - dob.get (Calendar.YEAR);
			values.put ("age", ageInYears);
			values.put ("dob", App.getSqlDate (dob));
			values.put ("gender", male.isChecked () ? "M" : "F");
			values.put ("conclusion", tbConclusion.isChecked () ? "Yes" : "No");
			values.put ("patientId", App.get (patientId));
			final ArrayList<String[]> observations = new ArrayList<String[]> ();
			observations.add (new String[] {"Pulmonary Suspect", pulmonarySuspect.isChecked () ? "Yes" : "No"});
			if (pulmonarySuspect.isChecked ())
			{
				observations.add (new String[] {"Cough", App.get (cough)});
				observations.add (new String[] {"Child Cough Duration", coughDuration.isEnabled () ? App.get (coughDuration) : ""});
				observations.add (new String[] {"Fever", App.get (fever)});
				observations.add (new String[] {"Fever Duration", feverDuration.isEnabled () ? App.get (feverDuration) : ""});
				observations.add (new String[] {"Family TB", App.get (familyTb)});
				observations.add (new String[] {"Respiratory Rate", App.get (respirationRate)});
				observations.add (new String[] {"Pneumonia", pneumoniaConclusion.isChecked () ? "Yes" : "No"});
				observations.add (new String[] {"Tuberculosis", tbConclusion.isChecked () ? "Yes" : "No"});
			}

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
					String result = serverService.saveScreening (FormType.PAEDIATRIC_SCREENING, values, observations.toArray (new String[][] {}));
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
						App.getAlertDialog (PaediatricScreeningActivity.this, AlertType.INFO, getResources ().getString (R.string.inserted)).show ();
						initView (views);
					}
					else
					{
						App.getAlertDialog (PaediatricScreeningActivity.this, AlertType.ERROR, result).show ();
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
		if (state)
		{
			if (button == pulmonarySuspect)
			{
				nonPulmonarySuspect.setChecked (!state);
			}
			else if (button == nonPulmonarySuspect)
			{
				pulmonarySuspect.setChecked (!state);
			}
		}
		if (button == tbConclusion || button == pneumoniaConclusion || button == nonPulmonarySuspect)
		{
			firstNameTextView.setEnabled (state);
			firstName.setEnabled (state);
			lastNameTextView.setEnabled (state);
			lastName.setEnabled (state);
			patientId.setEnabled (state);
			scanBarcode.setEnabled (state);
		}
	}

	@Override
	public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
	{
		MySpinner spinner = (MySpinner) parent;
		boolean visible = spinner.getSelectedItemPosition () == 1;
		if (parent == cough)
		{
			coughDurationTextView.setEnabled (visible);
			coughDuration.setEnabled (visible);
		}
		else if (parent == fever)
		{
			feverDurationTextView.setEnabled (visible);
			feverDuration.setEnabled (visible);
		}
		else if (parent == ageModifier)
		{
			if (!"".equals (App.get (age)))
			{
				updateDob ();
			}
		}
		updateDisplay ();
	}

	@Override
	public boolean onLongClick (View v)
	{
		return false;
	}

	@Override
	public void onPageSelected (int pageNo)
	{
		int _pageNo = pageNo;
		// For Non-Pulmonary suspect, skip pages 4-7
		if (nonPulmonarySuspect.isChecked ())
		{
			if (pageNo >= 3 && pageNo != PAGE_COUNT)
			{
				_pageNo = PAGE_COUNT;
			}
		}
		gotoPage (_pageNo);
	}

	@Override
	public boolean onEditorAction (TextView v, int actionId, KeyEvent event)
	{
		if (v == age || v == respirationRate)
		{
			updateDisplay ();
		}
		return true;
	}
}
