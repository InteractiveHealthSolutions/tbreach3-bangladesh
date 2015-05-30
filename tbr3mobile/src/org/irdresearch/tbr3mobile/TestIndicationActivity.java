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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import org.irdresearch.tbr3mobile.custom.MyButton;
import org.irdresearch.tbr3mobile.custom.MyCheckBox;
import org.irdresearch.tbr3mobile.custom.MyEditText;
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

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class TestIndicationActivity extends AbstractFragmentActivity
{
	static final int	GET_PATIENT_ID	= 1;
	// Views displayed in pages, sorted w.r.t. appearance on pager
	MyTextView			formDateTextView;
	MyButton			formDateButton;

	MyTextView			patientIdMyTextView;
	MyEditText			patientId;
	MyButton			scanBarcode;

	MyTextView			tests;
	MyCheckBox			cxr;
	MyCheckBox			oxr;
	MyCheckBox			gxp;
	//MyCheckBox			mantoux;
	MyCheckBox			smearMicroscopy;
	MyCheckBox			bloodGlucose;
	MyCheckBox			hba1c;
	MyCheckBox			spirometry;
	//MyCheckBox			oximetry;

	/**
	 * Subclass representing Fragment for test indication form
	 * 
	 * @author owais.hussain@irdresearch.org
	 * 
	 */
	@SuppressLint("ValidFragment")
	class TestIndicationFragment extends Fragment
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
	 * Subclass for Pager Adapter. Uses TestIndicationFragment subclass as items
	 * 
	 * @author owais.hussain@irdresearch.org
	 * 
	 */
	class TestIndicationFragmentPagerAdapter extends FragmentPagerAdapter
	{
		/** Constructor of the class */
		public TestIndicationFragmentPagerAdapter (FragmentManager fragmentManager)
		{
			super (fragmentManager);
		}

		/** This method will be invoked when a page is requested to create */
		@Override
		public Fragment getItem (int arg0)
		{
			TestIndicationFragment fragment = new TestIndicationFragment ();
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
		FORM_NAME = "Test Indication";
		TAG = "TestIndicationActivity";
		PAGE_COUNT = 2;
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
		TestIndicationFragmentPagerAdapter pagerAdapter = new TestIndicationFragmentPagerAdapter (fragmentManager);
		pager.setAdapter (pagerAdapter);
		pager.setOffscreenPageLimit (PAGE_COUNT);
		// Create views for pages
		formDateTextView = new MyTextView (context, R.style.text, R.string.form_date);
		formDateButton = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.form_date, R.string.form_date);

		tests = new MyTextView (context, R.style.text, R.string.test_indication);
		tests.setText (R.string.test_indication_check);
		cxr = new MyCheckBox (context, R.string.cxr, R.style.edit, R.string.cxr, false);
		oxr = new MyCheckBox (context, R.string.oxr, R.style.edit, R.string.oxr, false);
		gxp = new MyCheckBox (context, R.string.gxp, R.style.edit, R.string.gxp, false);
		smearMicroscopy = new MyCheckBox (context, R.string.smear_microscopy, R.style.edit, R.string.smear_microscopy, false);
		//mantoux = new MyCheckBox (context, R.string.mantoux, R.style.edit, R.string.mantoux, false);
		bloodGlucose = new MyCheckBox (context, R.string.blood_glucose, R.style.edit, R.string.blood_glucose, false);
		hba1c = new MyCheckBox (context, R.string.hba1c, R.style.edit, R.string.hba1c, false);
		spirometry = new MyCheckBox (context, R.string.spirometry, R.style.edit, R.string.spirometry, false);
		//oximetry = new MyCheckBox (context, R.string.oximetry, R.style.edit, R.string.oximetry, false);

		patientIdMyTextView = new MyTextView (context, R.style.text, R.string.patient_id);
		patientId = new MyEditText (context, R.string.patient_id, R.string.patient_id_hint, InputType.TYPE_CLASS_TEXT, R.style.edit, RegexUtil.idLength, false);
		scanBarcode = new MyButton (context, R.style.button, R.drawable.custom_button_beige, R.string.scan_barcode, R.string.scan_barcode);
		View[][] viewGroups = { {formDateTextView, formDateButton, patientIdMyTextView, patientId, scanBarcode}, {cxr, oxr, gxp, smearMicroscopy, /*mantoux,*/ bloodGlucose, hba1c, spirometry/*, oximetry*/}};
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
		views = new View[] {cxr, oxr, gxp, smearMicroscopy/*, mantoux*/, bloodGlucose, hba1c, spirometry,/* oximetry,*/ patientId};
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
		patientId.setOnLongClickListener (this);
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
		cxr.setChecked (false);
		oxr.setChecked (false);
		gxp.setChecked (false);
		smearMicroscopy.setChecked (false);
		//mantoux.setChecked (false);
		bloodGlucose.setChecked (false);
		hba1c.setChecked (false);
		spirometry.setChecked (false);
		//oximetry.setChecked (false);
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
		View[] mandatory = {patientId};
		for (View v : mandatory)
		{
			if (App.get (v).equals (""))
			{
				valid = false;
				message.append (v.getTag () + ". ");
				((EditText) v).setHintTextColor (getResources ().getColor (R.color.Red));
			}
		}
		if (!(cxr.isChecked () || oxr.isChecked () || gxp.isChecked () || /*mantoux.isChecked () */ smearMicroscopy.isChecked () || bloodGlucose.isChecked () || hba1c.isChecked () || spirometry.isChecked () /*|| oximetry.isChecked ()*/))
		{
			valid = false;
			message.append (tests.getText () + ": " +  getResources ().getString (R.string.empty_selection) + "\n");
		}
		if (!valid)
		{
			message.append (getResources ().getString (R.string.empty_data) + "\n");
		}
		// Validate data
		if (valid)
		{
			if (!RegexUtil.isValidId (App.get (patientId)))
			{
				valid = false;
				message.append (patientId.getTag () + ": " + getResources ().getString (R.string.invalid_data) + "\n");
				patientId.setTextColor (getResources ().getColor (R.color.Red));
			}
		}
		// Validate range
		if (formDate.getTime ().after (new Date ()))
		{
			valid = false;
			message.append (formDateButton.getTag () + ": " + getResources ().getString (R.string.invalid_date_or_time) + "\n");
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
			values.put ("location", App.getLocation());
			values.put ("patientId", App.get (patientId));
			final ArrayList<String[]> observations = new ArrayList<String[]> ();
			observations.add (new String[] {"CXR", cxr.isChecked () ? "Yes" : "No"});
			observations.add (new String[] {"OXR", oxr.isChecked () ? "Yes" : "No"});
			observations.add (new String[] {"GXP", gxp.isChecked () ? "Yes" : "No"});
			observations.add (new String[] {"Smear Microscopy", smearMicroscopy.isChecked () ? "Yes" : "No"});
			//observations.add (new String[] {"Mantoux", mantoux.isChecked () ? "Yes" : "No"});
			observations.add (new String[] {"Blood Glucose", bloodGlucose.isChecked () ? "Yes" : "No"});
			observations.add (new String[] {"HbA1c", hba1c.isChecked () ? "Yes" : "No"});
			observations.add (new String[] {"Spirometry", spirometry.isChecked () ? "Yes" : "No"});
			//observations.add (new String[] {"Oximetry", oximetry.isChecked () ? "Yes" : "No"});
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
					//TODO Uncomment when live
					String result = serverService.saveTestIndication (FormType.TEST_INDICATION, values, observations.toArray (new String[][] {}));
					return result;
					//return "SUCCESS";
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
						App.getAlertDialog (TestIndicationActivity.this, AlertType.INFO, getResources ().getString (R.string.inserted)).show ();
						initView (views);
					}
					else
					{
						App.getAlertDialog (TestIndicationActivity.this, AlertType.ERROR, result).show ();
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
		// Retrieve barcode scan results or Search for ID
		if (requestCode == Barcode.BARCODE_RESULT || requestCode == GET_PATIENT_ID)
		{
			if (resultCode == RESULT_OK)
			{
				String str = "";
				if (requestCode == Barcode.BARCODE_RESULT)
					str = data.getStringExtra (Barcode.SCAN_RESULT);
				else if (requestCode == GET_PATIENT_ID)
					str = data.getStringExtra (PatientSearchActivity.SEARCH_RESULT);
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
	}

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
	public void onCheckedChanged (CompoundButton button, boolean state)
	{
		// Not implemented
	}

	@Override
	public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
	{
		// Not implemented
	}

	@Override
	public boolean onLongClick (View view)
	{
		if (view == patientId)
		{
			Intent intent = new Intent (view.getContext (), PatientSearchActivity.class);
			intent.putExtra (PatientSearchActivity.SEARCH_RESULT, "");
			startActivityForResult (intent, GET_PATIENT_ID);
			return true;
		}
		return false;
	}
}
