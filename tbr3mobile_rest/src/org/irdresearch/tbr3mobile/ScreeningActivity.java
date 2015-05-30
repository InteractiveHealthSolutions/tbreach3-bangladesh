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
public class ScreeningActivity extends AbstractFragmentActivity implements OnEditorActionListener
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

	MyTextView		coughTextView;
	MySpinner		cough;
	MyTextView		coughDurationTextView;
	MyEditText		coughDuration;
	MyTextView		coughDurationModifierTextView;
	MySpinner		coughDurationModifier;

	MyTextView		symptomsTextView;
	MyCheckBox		fever;
	MyCheckBox		haemoptysis;
	MyCheckBox		nightSweats;
	MyCheckBox		weightLoss;
	MyCheckBox		fatigue;
	MyCheckBox		appetiteLoss;

	MyTextView		diabetesTextView;
	MySpinner		diabetes;
	MyTextView		familyDiabetesTextView;
	MySpinner		familyDiabetes;

	MyTextView		hypertensionTextView;
	MySpinner		hypertension;

	MyTextView		breathingShortnessTextView;
	MySpinner		breathingShortness;
	MyTextView		physicalActivityTextView;
	MySpinner		activeBreathingShortness;
	MyTextView		wheezingTextView;
	MySpinner		wheezing;

	MyTextView		tobaccoCurrentTextView;
	MySpinner		tobaccoCurrent;
	MyTextView		tobaccoPastTextView;
	MySpinner		tobaccoPast;

	MyCheckBox		contactReferral;
	MyTextView		contactVoucherTypeTextView;
	MySpinner		contactVoucherType;
	MyTextView		contactIdTextView;
	MyEditText		contactId;

	MyTextView		conclusionTextView;
	MyCheckBox		cxrRifIndication;
	MyCheckBox		bloodGlucoseIndication;
	MyCheckBox		cxrSpirometryIndication;

	MyTextView		firstNameTextView;
	MyEditText		firstName;
	MyTextView		lastNameTextView;
	MyEditText		lastName;

	MyTextView		patientIdTextView;
	MyEditText		patientId;
	MyButton		scanBarcode;

	boolean			isSuspect;

	/**
	 * Subclass representing Fragment for screening form
	 * 
	 * @author owais.hussain@irdresearch.org
	 * 
	 */
	@SuppressLint("ValidFragment")
	class ScreeningFragment extends Fragment
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
	 * Subclass for Pager Adapter. Uses Screening subclass as items
	 * 
	 * @author owais.hussain@irdresearch.org
	 */
	class ScreeningPagerAdapter extends FragmentPagerAdapter
	{
		/** Constructor of the class */
		public ScreeningPagerAdapter (FragmentManager fragmentManager)
		{
			super (fragmentManager);
		}

		/** This method will be invoked when a page is requested to create */
		@Override
		public Fragment getItem (int arg0)
		{
			ScreeningFragment fragment = new ScreeningFragment ();
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
	public void createViews (Context context)
	{
		FORM_NAME = "Screening";
		TAG = "ScreeningActivity";
		PAGE_COUNT = 11;
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
		ScreeningPagerAdapter pagerAdapter = new ScreeningPagerAdapter (fragmentManager);
		pager.setAdapter (pagerAdapter);
		pager.setOffscreenPageLimit (PAGE_COUNT);
		// Create views for pages
		formDateTextView = new MyTextView (context, R.style.text, R.string.form_date);
		formDateButton = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.form_date, R.string.form_date);

		// Demographics
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

		// Symptoms
		coughTextView = new MyTextView (context, R.style.text, R.string.cough);
		cough = new MySpinner (context, getResources ().getStringArray (R.array.options), R.string.cough, R.string.option_hint);
		coughDurationTextView = new MyTextView (context, R.style.text, R.string.cough_duration);
		coughDuration = new MyEditText (context, R.string.cough_duration, R.string.cough_duration_hint, InputType.TYPE_CLASS_NUMBER, R.style.edit, 2, false);
		coughDurationModifierTextView = new MyTextView (context, R.style.text, R.string.cough_duration_modifier);
		coughDurationModifier = new MySpinner (context, getResources ().getStringArray (R.array.duration_modifiers), R.string.cough_duration_modifier, R.string.option_hint);

		symptomsTextView = new MyTextView (context, R.style.text, R.string.symptoms);
		fever = new MyCheckBox (context, R.string.fever_symptom, R.style.edit, R.string.fever_symptom, false);
		haemoptysis = new MyCheckBox (context, R.string.haemoptysis_symptom, R.style.edit, R.string.haemoptysis_symptom, false);
		nightSweats = new MyCheckBox (context, R.string.night_sweats_symptom, R.style.edit, R.string.night_sweats_symptom, false);
		weightLoss = new MyCheckBox (context, R.string.weight_loss_symptom, R.style.edit, R.string.weight_loss_symptom, false);
		fatigue = new MyCheckBox (context, R.string.fatigue_symptom, R.style.edit, R.string.fatigue_symptom, false);
		appetiteLoss = new MyCheckBox (context, R.string.appetite_loss_symptom, R.style.edit, R.string.appetite_loss_symptom, false);

		diabetesTextView = new MyTextView (context, R.style.text, R.string.diabetes);
		diabetes = new MySpinner (context, getResources ().getStringArray (R.array.options), R.string.diabetes, R.string.option_hint);
		familyDiabetesTextView = new MyTextView (context, R.style.text, R.string.family_diabetes);
		familyDiabetes = new MySpinner (context, getResources ().getStringArray (R.array.options), R.string.family_diabetes, R.string.option_hint);

		hypertensionTextView = new MyTextView (context, R.style.text, R.string.hypertension);
		hypertension = new MySpinner (context, getResources ().getStringArray (R.array.options), R.string.hypertension, R.string.option_hint);

		breathingShortnessTextView = new MyTextView (context, R.style.text, R.string.breathing_shortness);
		breathingShortness = new MySpinner (context, getResources ().getStringArray (R.array.options), R.string.breathing_shortness, R.string.option_hint);
		physicalActivityTextView = new MyTextView (context, R.style.text, R.string.active_breathing_shortness);
		activeBreathingShortness = new MySpinner (context, getResources ().getStringArray (R.array.options), R.string.active_breathing_shortness, R.string.option_hint);
		wheezingTextView = new MyTextView (context, R.style.text, R.string.wheezing);
		wheezing = new MySpinner (context, getResources ().getStringArray (R.array.options), R.string.wheezing, R.string.option_hint);

		tobaccoCurrentTextView = new MyTextView (context, R.style.text, R.string.tobacco_current);
		tobaccoCurrent = new MySpinner (context, getResources ().getStringArray (R.array.tobacco_durations), R.string.tobacco_current, R.string.option_hint);
		tobaccoPastTextView = new MyTextView (context, R.style.text, R.string.tobacco_past);
		tobaccoPast = new MySpinner (context, getResources ().getStringArray (R.array.tobacco_durations), R.string.tobacco_past, R.string.option_hint);

		// Contact Referral info..
		contactReferral = new MyCheckBox (context, R.string.contact_referral, R.style.edit, R.string.contact_referral, false);
		contactVoucherTypeTextView = new MyTextView (context, R.style.text, R.string.voucher_type);
		contactVoucherType = new MySpinner (context, getResources ().getStringArray (R.array.voucher_type_options), R.string.voucher_type, R.string.option_hint);
		contactIdTextView = new MyTextView (context, R.style.text, R.string.contact_referral_id);
		contactId = new MyEditText (context, R.string.contact_referral_id, R.string.patient_id_hint, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.idLength, false);

		// Conclusion
		conclusionTextView = new MyTextView (context, R.style.text, R.string.test_indication);
		cxrRifIndication = new MyCheckBox (context, R.string.cxr_rif_indication, R.style.text, R.string.cxr_rif_indication, false);
		cxrRifIndication.setClickable (false);
		bloodGlucoseIndication = new MyCheckBox (context, R.string.blood_sugar_indication, R.style.text, R.string.blood_sugar_indication, false);
		bloodGlucoseIndication.setClickable (false);
		cxrSpirometryIndication = new MyCheckBox (context, R.string.cxr_spirometry_indication, R.style.text, R.string.cxr_spirometry_indication, false);
		cxrSpirometryIndication.setClickable (false);

		// Patient's Name
		firstNameTextView = new MyTextView (context, R.style.text, R.string.first_name);
		firstName = new MyEditText (context, R.string.first_name, R.string.first_name_hint, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 20, false);
		lastNameTextView = new MyTextView (context, R.style.text, R.string.last_name);
		lastName = new MyEditText (context, R.string.last_name, R.string.last_name_hint, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, R.style.edit, 20, false);

		patientIdTextView = new MyTextView (context, R.style.text, R.string.patient_id);
		patientId = new MyEditText (context, R.string.patient_id, R.string.patient_id_hint, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.idLength, false);
		scanBarcode = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.scan_barcode, R.string.scan_barcode);

		View[][] viewGroups = { {formDateTextView, formDateButton, ageTextView, age, genderTextView, gender}, {heightTextView, height, weightTextView, weight, bmiTextTextView, bmiTextView},
				{coughTextView, cough, coughDurationTextView, coughDuration, coughDurationModifierTextView, coughDurationModifier},
				{symptomsTextView, fever, haemoptysis, nightSweats, weightLoss, fatigue, appetiteLoss}, {diabetesTextView, diabetes, familyDiabetesTextView, familyDiabetes},
				{hypertensionTextView, hypertension}, {breathingShortnessTextView, breathingShortness, physicalActivityTextView, activeBreathingShortness, wheezingTextView, wheezing},
				{tobaccoCurrentTextView, tobaccoCurrent, tobaccoPastTextView, tobaccoPast}, {contactReferral, contactVoucherTypeTextView, contactVoucherType, contactIdTextView, contactId},
				{conclusionTextView, cxrRifIndication, bloodGlucoseIndication, cxrSpirometryIndication},
				{firstNameTextView, firstName, lastNameTextView, lastName, patientIdTextView, patientId, scanBarcode}};
		// Create layouts and store in ArrayList
		groups = new ArrayList<ViewGroup> ();
		for (int i = 0; i < PAGE_COUNT; i++)
		{
			LinearLayout layout = new LinearLayout (context);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			params.setMargins (0, 5, 0, 5);
			layout.setLayoutParams (params);
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
		views = new View[] {age, firstName, lastName, height, weight, cough, coughDuration, coughDurationModifier, fever, haemoptysis, nightSweats, weightLoss, fatigue, appetiteLoss, diabetes,
				familyDiabetes, hypertension, breathingShortness, activeBreathingShortness, wheezing, tobaccoCurrentTextView, tobaccoCurrent, tobaccoPast, contactReferral, contactVoucherType,
				contactId, cxrRifIndication, bloodGlucoseIndication, cxrSpirometryIndication, patientId};
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
		male.setChecked (true);
		cxrRifIndication.setChecked (false);
		contactReferral.setChecked (false);
		contactVoucherTypeTextView.setEnabled (false);
		contactVoucherType.setEnabled (false);
		contactIdTextView.setEnabled (false);
		contactId.setEnabled (false);
		firstNameTextView.setEnabled (false);
		firstName.setEnabled (false);
		lastNameTextView.setEnabled (false);
		lastName.setEnabled (false);
		patientIdTextView.setEnabled (false);
		patientId.setEnabled (false);
		scanBarcode.setEnabled (false);
		// TODO: Remove the default values for weight, height and bmi afterwards
		//height.setText ("150");
		//weight.setText ("45");
		//bmiTextView.setText ("20");
	}

	@Override
	public void updateDisplay ()
	{
		formDateButton.setText (DateFormat.format ("dd-MMM-yyyy", formDate));
		boolean hasCough = cough.getSelectedItemPosition () == 1;
		boolean hasFever = fever.isChecked ();
		boolean hasHaemoptysis = haemoptysis.isChecked ();
		boolean hasNightSweats = nightSweats.isChecked ();
		boolean hasWeightLioss = weightLoss.isChecked ();
		boolean hasFatigue = fatigue.isChecked ();
		boolean hasAppetiteLoss = appetiteLoss.isChecked ();
		boolean hasBmiUnder = false;
		boolean hasBmiOver = false;
		try
		{
			double bmi = Double.parseDouble (App.get (bmiTextView));
			hasBmiUnder = (bmi < 18.5) & (bmi > 0);
			hasBmiOver = bmi > 23;
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace ();
		}
		boolean isSmoker = tobaccoCurrent.getSelectedItemPosition () == 1 | tobaccoCurrent.getSelectedItemPosition () == 2;
		boolean wasSmoker = tobaccoPast.getSelectedItemPosition () == 1 | tobaccoPast.getSelectedItemPosition () == 2;
		boolean hasDiabetes = diabetes.getSelectedItemPosition () == 1;
		boolean hasFamilyDiabetes = familyDiabetes.getSelectedItemPosition () == 1;
		boolean hasHypertension = hypertension.getSelectedItemPosition () == 1;
		boolean isAgedEnoughForDiabetic = false;
		try
		{
			isAgedEnoughForDiabetic = Integer.parseInt (App.get (age)) > 45;
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		boolean hasBreathingShortness = breathingShortness.getSelectedItemPosition () == 1;
		boolean hasPhysicalActivityBreathingShortness = activeBreathingShortness.getSelectedItemPosition () == 1;
		boolean hasWheezing = wheezing.getSelectedItemPosition () == 1;
		// CXR and RIF test indication logic
		if (hasCough | hasFever | hasHaemoptysis | hasNightSweats | hasWeightLioss | hasFatigue | hasAppetiteLoss | hasBmiUnder | isSmoker | wasSmoker | contactReferral.isChecked ())
		{
			cxrRifIndication.setChecked (true);
		}
		else
		{
			cxrRifIndication.setChecked (false);
		}
		// Blood Sugar test indication logic
		if (hasDiabetes | hasFamilyDiabetes | hasHypertension | isAgedEnoughForDiabetic | hasBmiOver)
		{
			bloodGlucoseIndication.setChecked (true);
		}
		else
		{
			bloodGlucoseIndication.setChecked (false);
		}
		// CXR and Spirometry test indication logic
		if (hasBreathingShortness | hasPhysicalActivityBreathingShortness | hasWheezing | isSmoker | wasSmoker)
		{
			cxrSpirometryIndication.setChecked (true);
		}
		else
		{
			cxrSpirometryIndication.setChecked (false);
		}
	}

	@Override
	public boolean validate ()
	{
		boolean valid = true;
		StringBuffer message = new StringBuffer ();
		// Validate mandatory controls
		View[] mandatory = {firstName, lastName, age, height, weight};
		for (View v : mandatory)
		{
			if (!isSuspect && (v == firstName || v == lastName))
			{
				continue;
			}
			if (App.get (v).equals (""))
			{
				valid = false;
				message.append (v.getTag ().toString () + ". ");
				((EditText) v).setHintTextColor (getResources ().getColor (R.color.Red));
			}
		}
		if (cough.getSelectedItemPosition () == 1 && App.get (coughDuration).equals (""))
		{
			valid = false;
			message.append (coughDuration.getTag ().toString () + ". ");
			coughDuration.setHintTextColor (getResources ().getColor (R.color.Red));
		}
		if (!valid)
		{
			message.append (getResources ().getString (R.string.empty_data) + "\n");
		}
		if (isSuspect && App.get (patientId).equals (""))
		{
			valid = false;
			message.append (patientId.getTag ().toString () + ": " + getResources ().getString (R.string.empty_data) + "\n");
		}
		if (contactReferral.isChecked () && App.get (contactId).equals (""))
		{
			valid = false;
			message.append (contactId.getTag ().toString () + ": " + getResources ().getString (R.string.empty_data) + "\n");
		}
		// Validate data
		if (valid)
		{
			if (isSuspect)
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
			}
			if (!RegexUtil.isNumeric (App.get (age), false))
			{
				valid = false;
				message.append (age.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				age.setTextColor (getResources ().getColor (R.color.Red));
			}
			if (!App.get (height).equals (""))
			{
				if (!RegexUtil.isNumeric (App.get (height), false))
				{
					valid = false;
					message.append (height.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
					height.setTextColor (getResources ().getColor (R.color.Red));
				}
			}
			if (!App.get (weight).equals (""))
			{
				if (!RegexUtil.isNumeric (App.get (weight), true))
				{
					valid = false;
					message.append (weight.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
					weight.setTextColor (getResources ().getColor (R.color.Red));
				}
			}
		}
		// Validate ranges
		try
		{
			// Form date range
			if (formDate.getTime ().after (new Date ()))
			{
				valid = false;
				message.append (formDateButton.getTag () + ": " + getResources ().getString (R.string.invalid_date_or_time) + "\n");
			}
			// Age range
			int a = Integer.parseInt (App.get (age));
			if (a < 10)
			{
				valid = false;
				message.append (age.getTag ().toString () + ": " + getResources ().getString (R.string.out_of_range) + "\n");
			}
			if (a > 110)
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
			if (isSuspect && !RegexUtil.isValidId (App.get (patientId)))
			{
				valid = false;
				message.append (patientId.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				patientId.setTextColor (getResources ().getColor (R.color.Red));
			}
			if (contactReferral.isChecked () && !RegexUtil.isValidId (App.get (contactId)))
			{
				valid = false;
				message.append (contactId.getTag ().toString () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				contactId.setTextColor (getResources ().getColor (R.color.Red));
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

	@Override
	public boolean submit ()
	{
		if (validate ())
		{
			final ContentValues values = new ContentValues ();
			final ArrayList<String[]> observations = new ArrayList<String[]> ();
			// If not a suspect, then post to separate DB
			if (!isSuspect)
			{
				values.put ("formDate", App.getSqlDate (formDate));
				values.put ("location", App.getCurrentLocation ().getName ());
				values.put ("age", App.get (age));
				values.put ("gender", male.isChecked () ? "M" : "F");
				values.put ("weight", App.get (weight));
				values.put ("height", App.get (height));
			}
			else
			{
				values.put ("formDate", App.getSqlDate (formDate));
				values.put ("location", App.getCurrentLocation ().getName ());
				values.put ("firstName", App.get (firstName));
				values.put ("lastName", App.get (lastName));
				values.put ("age", App.get (age));
				values.put ("gender", male.isChecked () ? "M" : "F");
				values.put ("patientId", App.get (patientId));
				observations.add (new String[] {"Height (cm)", App.get (height)});
				observations.add (new String[] {"Weight (kg)", App.get (weight)});
				observations.add (new String[] {"Cough", App.get (cough)});
				observations.add (new String[] {"Cough Duration", App.get (coughDuration)});
				observations.add (new String[] {"Cough Duration Modifier", App.get (coughDurationModifier)});
				observations.add (new String[] {"Fever", fever.isChecked () ? "Yes" : "No"});
				observations.add (new String[] {"Haemoptysis", haemoptysis.isChecked () ? "Yes" : "No"});
				observations.add (new String[] {"Night Sweats", nightSweats.isChecked () ? "Yes" : "No"});
				observations.add (new String[] {"Weight Loss", weightLoss.isChecked () ? "Yes" : "No"});
				observations.add (new String[] {"Fatigue", fatigue.isChecked () ? "Yes" : "No"});
				observations.add (new String[] {"Appetite Loss", appetiteLoss.isChecked () ? "Yes" : "No"});
				observations.add (new String[] {"Diabetes", App.get (diabetes)});
				observations.add (new String[] {"Family Diabetes", App.get (familyDiabetes)});
				observations.add (new String[] {"Hypertension", App.get (hypertension)});
				observations.add (new String[] {"Breathing Shortness", App.get (breathingShortness)});
				observations.add (new String[] {"Active Breathing Shortness", App.get (activeBreathingShortness)});
				observations.add (new String[] {"Wheezing", App.get (wheezing)});
				observations.add (new String[] {"Smoking", App.get (tobaccoCurrent)});
				observations.add (new String[] {"Smoking History", App.get (tobaccoPast)});
				observations.add (new String[] {"CXR", (cxrRifIndication.isChecked () || cxrSpirometryIndication.isChecked ()) ? "Yes" : "No"});
				observations.add (new String[] {"Blood Glucose", bloodGlucoseIndication.isChecked () ? "Yes" : "No"});
				observations.add (new String[] {"GXP", cxrRifIndication.isChecked () ? "Yes" : "No"});
				observations.add (new String[] {"Spirometry", cxrSpirometryIndication.isChecked () ? "Yes" : "No"});
				observations.add (new String[] {"Referral Voucher", contactReferral.isChecked () ? "Yes" : "No"});
				if (contactReferral.isChecked ())
				{
					observations.add (new String[] {"Voucher Type", App.get (contactVoucherType)});
					if (contactVoucherType.getSelectedItemPosition () == 0)
					{
						observations.add (new String[] {"Contact Patient ID", App.get (contactId)});
					}
				}
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
					if (contactId.isEnabled ())
					{
						App.setThreadSafety (false);
						try
						{
							String[] searchPatient = serverService.searchPatient (App.get (contactId));
							if (searchPatient.length == 0)
								return contactIdTextView.getText () + ":" + getResources ().getString (R.string.patient_id_missing);
						}
						catch (UnsupportedEncodingException e)
						{
							e.printStackTrace ();
						}
					}
					App.setThreadSafety (true);
					String result = "";
					if (isSuspect)
						result = serverService.saveScreening (FORM_NAME, values, observations.toArray (new String[][] {}));
					else
						result = serverService.saveNonSuspect (FORM_NAME, values);
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
						App.getAlertDialog (ScreeningActivity.this, AlertType.INFO, getResources ().getString (R.string.inserted)).show ();
						initView (views);
					}
					else
					{
						App.getAlertDialog (ScreeningActivity.this, AlertType.ERROR, result).show ();
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
		view.startAnimation (alphaAnimation);
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
	public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
	{
		MySpinner spinner = (MySpinner) parent;
		boolean visible = spinner.getSelectedItemPosition () == 1;
		if (parent == cough)
		{
			coughDurationTextView.setEnabled (visible);
			coughDuration.setEnabled (visible);
			coughDurationModifierTextView.setEnabled (visible);
			coughDurationModifier.setEnabled (visible);
		}
		updateDisplay ();
	}

	@Override
	public void onCheckedChanged (CompoundButton button, boolean state)
	{
		if (button == contactReferral)
		{
			if (button.isChecked ())
			{
				contactVoucherTypeTextView.setEnabled (true);
				contactVoucherType.setEnabled (true);
				contactIdTextView.setEnabled (true);
				contactId.setEnabled (true);
				updateDisplay ();
			}
			else
			{
				contactVoucherTypeTextView.setEnabled (false);
				contactVoucherType.setEnabled (false);
				contactIdTextView.setEnabled (false);
				contactId.setEnabled (false);
			}
		}
		else if (button == fever || button == haemoptysis || button == nightSweats || button == weightLoss || button == fatigue || button == appetiteLoss)
		{
			updateDisplay ();
		}
		else if (button == cxrRifIndication || button == bloodGlucoseIndication || button == cxrSpirometryIndication)
		{
			boolean check = cxrRifIndication.isChecked () | bloodGlucoseIndication.isChecked () | cxrSpirometryIndication.isChecked ();
			firstNameTextView.setEnabled (check);
			firstName.setEnabled (check);
			lastNameTextView.setEnabled (check);
			lastName.setEnabled (check);
			patientIdTextView.setEnabled (check);
			patientId.setEnabled (check);
			scanBarcode.setEnabled (check);
			isSuspect = check;
		}
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
