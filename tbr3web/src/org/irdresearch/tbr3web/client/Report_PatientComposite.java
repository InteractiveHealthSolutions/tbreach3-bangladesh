/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.tbr3web.client;

import java.util.Date;
import java.util.Iterator;
import org.irdresearch.tbr3web.shared.AccessType;
import org.irdresearch.tbr3web.shared.TBR3;
import org.irdresearch.tbr3web.shared.UserRightsUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;
import com.google.gwt.user.datepicker.client.DatePicker;

public class Report_PatientComposite extends Composite implements IReport, ClickHandler, ChangeHandler, ValueChangeHandler<Boolean>
{
	private static ServerServiceAsync	service							= GWT.create (ServerService.class);
	private static LoadingWidget		loading							= new LoadingWidget ();
	private static final String			menuName						= "DATALOG";
	private UserRightsUtil				rights							= new UserRightsUtil ();
	private boolean						valid;

	private FlexTable					flexTable						= new FlexTable ();
	private FlexTable					topFlexTable					= new FlexTable ();
	private FlexTable					rightFlexTable					= new FlexTable ();

	private Grid						grid							= new Grid (1, 3);

	private HorizontalPanel				genderHorizontalPanel			= new HorizontalPanel ();
	private HorizontalPanel				ageHorizontalPanel				= new HorizontalPanel ();
	private HorizontalPanel				baselineVerticalPanel			= new HorizontalPanel ();
	private HorizontalPanel				followupHorizontalPanel			= new HorizontalPanel ();
	private HorizontalPanel				smearMicroscopyHorizontalPanel	= new HorizontalPanel ();

	private VerticalPanel				sourceVerticalPanel				= new VerticalPanel ();
	private VerticalPanel				statusVerticalPanel				= new VerticalPanel ();

	private Button						exportButton					= new Button ("Export");
	private Button						closeButton						= new Button ("Close");

	private Label						lblTbReachLog					= new Label (TBR3.getProjectTitle () + " Patient Custom Search");
	private Label						lblReportFilters				= new Label ("Report filters:");
	private Label						lblFilterValues					= new Label ("Filter values:");
	private Label						lblBetween						= new Label ("Between:");
	private Label						lblAnd							= new Label ("and");
	private Label						lblTo							= new Label ("to");
	private Label						lblBetween_1					= new Label ("between");
	private Label						lblAnd_1						= new Label ("and");

	private IntegerBox					ageFromTextBox					= new IntegerBox ();
	private IntegerBox					ageToTextBox					= new IntegerBox ();

	private DateBox						baselineStartDateBox			= new DateBox ();
	private DateBox						baselineEndDateBox				= new DateBox ();
	private DateBox						smearMicroscopyStartDateBox		= new DateBox ();
	private DateBox						smearMicroscopyEndDateBox		= new DateBox ();

	private ListBox						monitorComboBox					= new ListBox ();
	private ListBox						gpComboBox						= new ListBox ();
	private ListBox						chwComboBox						= new ListBox ();
	private ListBox						categoryComboBox				= new ListBox ();
	private ListBox						treatmentPhaseComboBox			= new ListBox ();
	private ListBox						regimenComboBox					= new ListBox ();
	private ListBox						followupSelectionComboBox		= new ListBox ();
	private ListBox						followupMonthComboBox			= new ListBox ();
	private ListBox						smearMicroscopyOptionsComboBox	= new ListBox ();

	private RadioButton					maleRadioButton					= new RadioButton ("Gender", "Male");
	private RadioButton					femaleRadioButton				= new RadioButton ("Gender", "Female");

	private CheckBox					monitorCheckBox					= new CheckBox ("Monitor");
	private CheckBox					gpCheckBox						= new CheckBox ("GP");
	private CheckBox					chwCheckBox						= new CheckBox ("CHW");
	private CheckBox					genderCheckBox					= new CheckBox ("Gender");
	private CheckBox					ageCheckBox						= new CheckBox ("Age");
	private CheckBox					sourceCheckBox					= new CheckBox ("Source");
	private CheckBox					smearMicroscopyCheckBox			= new CheckBox ("Smear Microscopy");
	private CheckBox					geneXpertTestCheckBox			= new CheckBox ("Gene Xpert Test");
	private CheckBox					adultDiagnosisCheckBox			= new CheckBox ("Adult Diagnosis");
	private CheckBox					paediatricDiagnosisCheckBox		= new CheckBox ("Paediatric Diagnosis");
	private CheckBox					baselineTreatmentCheckBox		= new CheckBox ("Baseline Treatment");
	private CheckBox					baselineRangeCheckBox			= new CheckBox ("Range:");
	private CheckBox					statusCheckBox					= new CheckBox ("Status");
	private CheckBox					categoryCheckBox				= new CheckBox ("Category");
	private CheckBox					regimenCheckBox					= new CheckBox ("Regimen");
	private CheckBox					treatmentPhaseCheckBox			= new CheckBox ("Treatment Phase");
	private CheckBox					followupsCheckBox				= new CheckBox ("Follow-ups");
	private CheckBox					smearMicroscopyDoneCheckBox		= new CheckBox ("Smear Microscopy");
	private CheckBox					closedCheckBox					= new CheckBox ("CLOSED");
	private CheckBox					gpconfCheckBox					= new CheckBox ("GP_CONF");
	private CheckBox					gpnoconfCheckBox				= new CheckBox ("GP_NO_CONF");
	private CheckBox					suspectCheckBox					= new CheckBox ("SUSPECT");
	private CheckBox					suspendedCheckBox				= new CheckBox ("SUSPENDED");
	private CheckBox					verifiedCheckBox				= new CheckBox ("VERIFIED");

	public Report_PatientComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("440px", "100%");
		flexTable.setWidget (0, 0, topFlexTable);
		lblTbReachLog.setStyleName ("title");
		topFlexTable.setWidget (0, 0, lblTbReachLog);
		flexTable.setWidget (1, 0, rightFlexTable);
		rightFlexTable.setSize ("100%", "100%");
		rightFlexTable.setWidget (0, 0, lblReportFilters);
		rightFlexTable.setWidget (0, 1, lblFilterValues);
		rightFlexTable.setWidget (1, 0, sourceCheckBox);
		rightFlexTable.setWidget (1, 1, sourceVerticalPanel);
		smearMicroscopyCheckBox.setEnabled (false);
		sourceVerticalPanel.add (smearMicroscopyCheckBox);
		geneXpertTestCheckBox.setEnabled (false);
		sourceVerticalPanel.add (geneXpertTestCheckBox);
		adultDiagnosisCheckBox.setEnabled (false);
		sourceVerticalPanel.add (adultDiagnosisCheckBox);
		paediatricDiagnosisCheckBox.setEnabled (false);
		sourceVerticalPanel.add (paediatricDiagnosisCheckBox);
		rightFlexTable.setWidget (2, 0, monitorCheckBox);
		monitorComboBox.setEnabled (false);
		rightFlexTable.setWidget (2, 1, monitorComboBox);
		rightFlexTable.setWidget (3, 0, gpCheckBox);
		gpComboBox.setEnabled (false);
		rightFlexTable.setWidget (3, 1, gpComboBox);
		rightFlexTable.setWidget (4, 0, chwCheckBox);
		chwComboBox.setEnabled (false);
		rightFlexTable.setWidget (4, 1, chwComboBox);
		rightFlexTable.setWidget (5, 0, genderCheckBox);
		rightFlexTable.setWidget (5, 1, genderHorizontalPanel);
		maleRadioButton.setEnabled (false);
		maleRadioButton.setValue (true);
		genderHorizontalPanel.add (maleRadioButton);
		femaleRadioButton.setEnabled (false);
		genderHorizontalPanel.add (femaleRadioButton);
		rightFlexTable.setWidget (6, 0, ageCheckBox);
		rightFlexTable.setWidget (6, 1, ageHorizontalPanel);
		ageHorizontalPanel.add (lblBetween);
		ageFromTextBox.setEnabled (false);
		ageFromTextBox.setText ("0");
		ageFromTextBox.setMaxLength (2);
		ageFromTextBox.setVisibleLength (2);
		ageHorizontalPanel.add (ageFromTextBox);
		ageHorizontalPanel.add (lblAnd);
		ageToTextBox.setEnabled (false);
		ageToTextBox.setText ("100");
		ageToTextBox.setVisibleLength (3);
		ageToTextBox.setMaxLength (3);
		ageHorizontalPanel.add (ageToTextBox);
		rightFlexTable.setWidget (7, 0, statusCheckBox);
		rightFlexTable.setWidget (7, 1, statusVerticalPanel);
		closedCheckBox.setEnabled (false);
		statusVerticalPanel.add (closedCheckBox);
		gpconfCheckBox.setEnabled (false);
		gpconfCheckBox.setValue (true);
		statusVerticalPanel.add (gpconfCheckBox);
		gpnoconfCheckBox.setEnabled (false);
		gpnoconfCheckBox.setValue (true);
		statusVerticalPanel.add (gpnoconfCheckBox);
		suspectCheckBox.setEnabled (false);
		suspectCheckBox.setValue (true);
		statusVerticalPanel.add (suspectCheckBox);
		suspendedCheckBox.setEnabled (false);
		statusVerticalPanel.add (suspendedCheckBox);
		verifiedCheckBox.setEnabled (false);
		verifiedCheckBox.setValue (true);
		statusVerticalPanel.add (verifiedCheckBox);
		rightFlexTable.setWidget (8, 0, baselineTreatmentCheckBox);
		rightFlexTable.setWidget (8, 1, baselineVerticalPanel);
		baselineRangeCheckBox.setEnabled (false);
		baselineVerticalPanel.add (baselineRangeCheckBox);
		baselineStartDateBox.setEnabled (false);
		baselineStartDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("yy-MM-dd")));
		baselineVerticalPanel.add (baselineStartDateBox);
		baselineStartDateBox.setWidth ("100px");
		baselineVerticalPanel.add (lblTo);
		baselineEndDateBox.setEnabled (false);
		baselineEndDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("yy-MM-dd")));
		baselineVerticalPanel.add (baselineEndDateBox);
		baselineEndDateBox.setWidth ("100px");
		rightFlexTable.setWidget (9, 0, categoryCheckBox);
		categoryComboBox.setEnabled (false);
		rightFlexTable.setWidget (9, 1, categoryComboBox);
		rightFlexTable.setWidget (10, 0, treatmentPhaseCheckBox);
		treatmentPhaseComboBox.setEnabled (false);
		rightFlexTable.setWidget (10, 1, treatmentPhaseComboBox);
		rightFlexTable.setWidget (11, 0, regimenCheckBox);
		regimenComboBox.setEnabled (false);
		rightFlexTable.setWidget (11, 1, regimenComboBox);
		rightFlexTable.setWidget (12, 0, followupsCheckBox);
		followupsCheckBox.setWidth ("");
		rightFlexTable.setWidget (12, 1, followupHorizontalPanel);
		followupSelectionComboBox.setEnabled (false);
		followupHorizontalPanel.add (followupSelectionComboBox);
		followupMonthComboBox.setEnabled (false);
		followupHorizontalPanel.add (followupMonthComboBox);
		rightFlexTable.setWidget (13, 0, smearMicroscopyDoneCheckBox);
		smearMicroscopyDoneCheckBox.setWidth ("");
		rightFlexTable.setWidget (13, 1, smearMicroscopyHorizontalPanel);
		smearMicroscopyOptionsComboBox.setEnabled (false);
		smearMicroscopyHorizontalPanel.add (smearMicroscopyOptionsComboBox);
		smearMicroscopyHorizontalPanel.add (lblBetween_1);
		smearMicroscopyStartDateBox.setEnabled (false);
		smearMicroscopyStartDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("yy-MM-dd")));
		smearMicroscopyHorizontalPanel.add (smearMicroscopyStartDateBox);
		smearMicroscopyStartDateBox.setWidth ("100px");
		smearMicroscopyHorizontalPanel.add (lblAnd_1);
		smearMicroscopyEndDateBox.setEnabled (false);
		smearMicroscopyEndDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("yy-MM-dd")));
		smearMicroscopyHorizontalPanel.add (smearMicroscopyEndDateBox);
		smearMicroscopyEndDateBox.setWidth ("100px");
		rightFlexTable.setWidget (14, 1, grid);
		grid.setSize ("100%", "100%");
		exportButton.setEnabled (false);
		exportButton.setText ("Export");
		grid.setWidget (0, 0, exportButton);
		grid.setWidget (0, 2, closeButton);
		flexTable.getRowFormatter ().setVerticalAlign (1, HasVerticalAlignment.ALIGN_TOP);

		sourceCheckBox.addValueChangeHandler (this);
		sourceCheckBox.addValueChangeHandler (this);
		monitorCheckBox.addValueChangeHandler (this);
		gpCheckBox.addValueChangeHandler (this);
		chwCheckBox.addValueChangeHandler (this);
		genderCheckBox.addValueChangeHandler (this);
		ageCheckBox.addValueChangeHandler (this);
		baselineTreatmentCheckBox.addValueChangeHandler (this);
		baselineRangeCheckBox.addValueChangeHandler (this);
		statusCheckBox.addValueChangeHandler (this);
		categoryCheckBox.addValueChangeHandler (this);
		regimenCheckBox.addValueChangeHandler (this);
		treatmentPhaseCheckBox.addValueChangeHandler (this);
		followupsCheckBox.addValueChangeHandler (this);
		smearMicroscopyDoneCheckBox.addValueChangeHandler (this);
		exportButton.addClickHandler (this);
		closeButton.addClickHandler (this);

		refreshList ();
		setRights (menuName);
	}

	private void refreshList ()
	{
		baselineStartDateBox.setValue (new Date ());
		baselineEndDateBox.setValue (new Date ());
		smearMicroscopyStartDateBox.setValue (new Date ());
		smearMicroscopyEndDateBox.setValue (new Date ());
		categoryComboBox.clear ();
		chwComboBox.clear ();
		followupMonthComboBox.clear ();
		followupSelectionComboBox.clear ();
		gpComboBox.clear ();
		monitorComboBox.clear ();
		regimenComboBox.clear ();
		smearMicroscopyOptionsComboBox.clear ();
		treatmentPhaseComboBox.clear ();
		categoryComboBox.addItem ("CAT I");
		categoryComboBox.addItem ("CAT II");
		followupMonthComboBox.addItem ("MONTH 2 FOLLOWUP");
		followupMonthComboBox.addItem ("MONTH 3 FOLLOWUP");
		followupMonthComboBox.addItem ("MONTH 5 FOLLOWUP");
		followupMonthComboBox.addItem ("MONTH 7 FOLLOWUP");
		followupSelectionComboBox.addItem ("HAS");
		followupSelectionComboBox.addItem ("DOES NOT HAVE");
		regimenComboBox.addItem ("EH");
		regimenComboBox.addItem ("RH");
		regimenComboBox.addItem ("RHE");
		regimenComboBox.addItem ("RHZE");
		regimenComboBox.addItem ("RHZES");
		smearMicroscopyOptionsComboBox.addItem ("MISSING");
		smearMicroscopyOptionsComboBox.addItem ("NEGATIVE");
		smearMicroscopyOptionsComboBox.addItem ("PENDING");
		smearMicroscopyOptionsComboBox.addItem ("POSITIVE");
		treatmentPhaseComboBox.addItem ("CONTINUOUS");
		treatmentPhaseComboBox.addItem ("INTENSIVE");
		try
		{
			load (true);
			service.getColumnData ("Monitor", "MonitorID", "", new AsyncCallback<String[]> ()
			{

				public void onSuccess (String[] result)
				{
					for (String s : result)
						monitorComboBox.addItem (s);
					load (false);
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
					load (false);
				}
			});
			load (true);
			service.getColumnData ("GP", "GPID", "", new AsyncCallback<String[]> ()
			{

				public void onSuccess (String[] result)
				{
					for (String s : result)
						gpComboBox.addItem (s);
					load (false);
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
					load (false);
				}
			});
			load (true);
			service.getColumnData ("Worker", "WorkerID", "", new AsyncCallback<String[]> ()
			{

				public void onSuccess (String[] result)
				{
					for (String s : result)
						chwComboBox.addItem (s);
					load (false);
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
					load (false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			load (false);
		}
	}

	/**
	 * Display/Hide main panel and loading widget
	 * 
	 * @param status
	 */
	public void load (boolean status)
	{
		flexTable.setVisible (!status);
		if (status)
			loading.show ();
		else
			loading.hide ();
	}

	public void clearControls (Widget w)
	{
		if (w instanceof Panel)
		{
			Iterator<Widget> iter = ((Panel) w).iterator ();
			while (iter.hasNext ())
				clearControls (iter.next ());
		}
		else if (w instanceof TextBoxBase)
		{
			((TextBoxBase) w).setText ("");
		}
		else if (w instanceof RichTextArea)
		{
			((RichTextArea) w).setText ("");
		}
		else if (w instanceof ListBox)
		{
			((ListBox) w).setSelectedIndex (0);
		}
		else if (w instanceof DatePicker)
		{
			((DatePicker) w).setValue (new Date ());
		}
	}

	public void clearUp ()
	{
		clearControls (flexTable);
	}

	public boolean validate ()
	{
		final StringBuilder errorMessage = new StringBuilder ();
		valid = true;
		/* Validate data-type rules */
		if (!valid)
			Window.alert (errorMessage.toString ());
		return valid;
	}

	public void viewData (boolean export)
	{
		try
		{
			StringBuilder query = new StringBuilder ();
			StringBuilder columns = new StringBuilder ();
			StringBuilder filters = new StringBuilder ();
			// Select columns to display and apply filters
			columns.append ("P.PatientID, P.MRNo, ");
			filters.append (" where P.DiseaseConfirmed = 1 ");
			if (sourceCheckBox.getValue ())
			{
				columns.append ("C.Source, ");
				String sources = "";
				// Iterate for all sources selected
				for (Iterator<Widget> iter = sourceVerticalPanel.iterator (); iter.hasNext ();)
				{
					CheckBox chk = (CheckBox) iter.next ();
					if (chk.getValue ())
						sources += "'" + chk.getText () + "',";
				}
				filters.append ("and C.Source in (" + sources.substring (0, sources.lastIndexOf (',') - 1) + "') ");
			}
			if (monitorCheckBox.getValue ())
			{
				columns.append ("P.MonitorID, ");
				filters.append ("and P.MonitorID = '" + TBRK3lient.get (monitorComboBox) + "' ");
			}
			if (gpCheckBox.getValue ())
			{
				columns.append ("P.GPID, ");
				filters.append ("and P.GPID = '" + TBRK3lient.get (gpComboBox) + "' ");
			}
			if (chwCheckBox.getValue ())
			{
				columns.append ("P.CHWID, ");
				filters.append ("and P.CHWID = '" + TBRK3lient.get (chwComboBox) + "' ");
			}
			if (genderCheckBox.getValue ())
			{
				columns.append ("Pr.Gender, ");
				filters.append ("and Pr.Gender = '" + (maleRadioButton.getValue () ? "M" : "F") + "' ");
			}
			if (ageCheckBox.getValue ())
			{
				columns.append ("year(P.DateRegistered) - year(Pr.DOB) as Age, ");
				filters.append ("and year(P.DateRegistered) - year(Pr.DOB) between " + TBRK3lient.get (ageFromTextBox) + " and " + TBRK3lient.get (ageToTextBox) + " ");
			}
			if (statusCheckBox.getValue ())
			{
				columns.append ("P.PatientStatus, ");
				String statuses = "";
				// Iterate for all sources selected
				for (Iterator<Widget> iter = statusVerticalPanel.iterator (); iter.hasNext ();)
				{
					CheckBox chk = (CheckBox) iter.next ();
					if (chk.getValue ())
						statuses += "'" + chk.getText () + "',";
				}
				filters.append ("and P.PatientStatus in (" + statuses.substring (0, statuses.lastIndexOf (',') - 1) + "') ");
			}
			if (categoryCheckBox.getValue ())
			{
				columns.append ("P.DiseaseCategory, ");
				filters.append ("and P.DiseaseCategory = '" + TBRK3lient.get (categoryComboBox) + "' ");
			}
			if (treatmentPhaseCheckBox.getValue ())
			{
				columns.append ("P.TreatmentPhase, ");
				filters.append ("and P.TreatmentPhase = '" + TBRK3lient.get (treatmentPhaseComboBox) + "' ");
			}
			if (regimenCheckBox.getValue ())
			{
				columns.append ("P.Regimen, ");
				filters.append ("and P.Regimen = '" + TBRK3lient.get (regimenComboBox) + "' ");
			}
			if (baselineTreatmentCheckBox.getValue ())
			{
				columns.append ("B.MaxEnteredDate as BaselineDate, ");
				filters.append ("and B.MaxEnteredDate is not null ");
				if (baselineRangeCheckBox.getValue ())
					filters.append ("and B.MaxEnteredDate between '" + baselineStartDateBox.getTextBox ().getValue () + "' and '" + baselineEndDateBox.getTextBox ().getValue () + "' ");
			}
			if (smearMicroscopyDoneCheckBox.getValue ())
			{
				String option = TBRK3lient.get (smearMicroscopyOptionsComboBox);
				columns.append ("S.DateSubmitted, S.SmearResult, ");
				if (option.equals ("MISSING"))
				{
					filters.append ("and not exists (select * from SputumResults where S.PatientID = P.PatientID and S.Remarks not like 'REJECTED%' and S.DateSubmitted between '"
							+ smearMicroscopyStartDateBox.getTextBox ().getValue () + "' and '" + smearMicroscopyEndDateBox.getTextBox ().getValue () + "') ");
				}
				else if (option.equals ("NEGATIVE"))
				{
					filters.append ("and S.SmearResult = 'NEGATIVE' ");
					filters.append ("and S.DateSubmitted between '" + smearMicroscopyStartDateBox.getTextBox ().getValue () + "' and '" + smearMicroscopyEndDateBox.getTextBox ().getValue () + "' ");
				}
				else if (option.equals ("PENDING"))
				{
					filters.append ("and S.SmearResult is null and S.Remarks not like 'REJECTED%' ");
					filters.append ("and S.DateSubmitted between '" + smearMicroscopyStartDateBox.getTextBox ().getValue () + "' and '" + smearMicroscopyEndDateBox.getTextBox ().getValue () + "' ");
				}
				else if (option.equals ("POSITIVE"))
				{
					filters.append ("and S.SmearResult is not null and S.SmearResult <> 'NEGATIVE' ");
					filters.append ("and S.DateSubmitted between '" + smearMicroscopyStartDateBox.getTextBox ().getValue () + "' and '" + smearMicroscopyEndDateBox.getTextBox ().getValue () + "' ");
				}
			}
			if (followupsCheckBox.getValue ())
			{
				columns.append ("F.EnteredDate as FollowupDate, ");
				String monthCriteria = "";
				if (TBRK3lient.get (followupMonthComboBox).equals ("MONTH 2 FOLLOWUP"))
					monthCriteria = "between 54 and 143";
				else if (TBRK3lient.get (followupMonthComboBox).equals ("MONTH 3 FOLLOWUP"))
					monthCriteria = "between 84 and 143";
				else if (TBRK3lient.get (followupMonthComboBox).equals ("MONTH 5 FOLLOWUP"))
					monthCriteria = "between 144 and 203";
				else if (TBRK3lient.get (followupMonthComboBox).equals ("MONTH 7 FOLLOWUP"))
					monthCriteria = ">= 204";
				if (TBRK3lient.get (followupSelectionComboBox).equals ("HAS"))
					filters.append ("and datediff(F.EnteredDate, B.MaxEnteredDate) " + monthCriteria + " ");
				else
					filters.append ("and not exists (select * from Enc_AD_CLIVIS where PID1 = P.PatientID and datediff(F.EnteredDate, B.MaxEnteredDate) " + monthCriteria + ") ");
			}
			columns.append ("P.DateRegistered");
			query.append ("select " + columns.toString () + " from tbreachkenya_rpt.Patient as P ");
			query.append ("inner join Person as Pr on P.PatientID = Pr.PID ");
			query.append ("left outer join _ConfirmedTBPatients as C on P.PatientID = C.PatientID ");
			query.append ("left outer join tmp_Encounter as B on P.PatientID = B.PID and B.EncounterType = 'BASELINE' ");
			query.append ("left outer join Enc_AD_CLIVIS as F on P.PatientID = F.PID1 ");
			query.append ("left outer join SputumResults as S on P.PatientID = S.PatientID ");
			query.append (filters.toString ());

			service.generateCSVfromQuery ("tbreachkenya_rpt", query.toString (), new AsyncCallback<String> ()
			{

				public void onSuccess (String result)
				{
					Window.open (result, "_blank", "");
					load (false);
				}

				public void onFailure (Throwable caught)
				{
					load (false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			load (false);
		}
	}

	public void setRights (String menuName)
	{
		try
		{
			load (true);
			service.getUserRgihts (TBR3.getCurrentUserName (), TBR3.getCurrentRole (), menuName, new AsyncCallback<Boolean[]> ()
			{

				public void onSuccess (Boolean[] result)
				{
					final Boolean[] userRights = result;
					rights.setRoleRights (TBR3.getCurrentRole (), userRights);
					exportButton.setEnabled (rights.getAccess (AccessType.PRINT));
					load (false);
				}

				public void onFailure (Throwable caught)
				{
					load (false);
				}
			});
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
	}

	public void onClick (ClickEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == exportButton)
		{
			viewData (false);
		}
		else if (sender == closeButton)
		{
			MainMenuComposite.clear ();
		}
	}

	public void onChange (ChangeEvent event)
	{
		// Not implemented
	}

	public void onValueChange (ValueChangeEvent<Boolean> event)
	{
		Widget sender = (Widget) event.getSource ();
		if (sender == sourceCheckBox)
		{
			smearMicroscopyCheckBox.setEnabled (sourceCheckBox.getValue ());
			geneXpertTestCheckBox.setEnabled (sourceCheckBox.getValue ());
			adultDiagnosisCheckBox.setEnabled (sourceCheckBox.getValue ());
			paediatricDiagnosisCheckBox.setEnabled (sourceCheckBox.getValue ());
		}
		else if (sender == monitorCheckBox)
		{
			monitorComboBox.setEnabled (monitorCheckBox.getValue ());
		}
		else if (sender == gpCheckBox)
		{
			gpComboBox.setEnabled (gpCheckBox.getValue ());
		}
		else if (sender == chwCheckBox)
		{
			chwComboBox.setEnabled (chwCheckBox.getValue ());
		}
		else if (sender == genderCheckBox)
		{
			maleRadioButton.setEnabled (genderCheckBox.getValue ());
			femaleRadioButton.setEnabled (genderCheckBox.getValue ());
		}
		else if (sender == ageCheckBox)
		{
			ageFromTextBox.setEnabled (ageCheckBox.getValue ());
			ageToTextBox.setEnabled (ageCheckBox.getValue ());
		}
		else if (sender == baselineTreatmentCheckBox)
		{
			baselineRangeCheckBox.setEnabled (baselineTreatmentCheckBox.getValue ());
		}
		else if (sender == baselineRangeCheckBox)
		{
			baselineStartDateBox.setEnabled (baselineRangeCheckBox.getValue ());
			baselineEndDateBox.setEnabled (baselineRangeCheckBox.getValue ());
		}
		else if (sender == statusCheckBox)
		{
			closedCheckBox.setEnabled (statusCheckBox.getValue ());
			gpconfCheckBox.setEnabled (statusCheckBox.getValue ());
			gpnoconfCheckBox.setEnabled (statusCheckBox.getValue ());
			suspectCheckBox.setEnabled (statusCheckBox.getValue ());
			suspendedCheckBox.setEnabled (statusCheckBox.getValue ());
			verifiedCheckBox.setEnabled (statusCheckBox.getValue ());
		}
		else if (sender == categoryCheckBox)
		{
			categoryComboBox.setEnabled (categoryCheckBox.getValue ());
		}
		else if (sender == regimenCheckBox)
		{
			regimenComboBox.setEnabled (regimenCheckBox.getValue ());
		}
		else if (sender == treatmentPhaseCheckBox)
		{
			treatmentPhaseComboBox.setEnabled (treatmentPhaseCheckBox.getValue ());
		}
		else if (sender == followupsCheckBox)
		{
			followupSelectionComboBox.setEnabled (followupsCheckBox.getValue ());
			followupMonthComboBox.setEnabled (followupsCheckBox.getValue ());
		}
		else if (sender == smearMicroscopyDoneCheckBox)
		{
			smearMicroscopyOptionsComboBox.setEnabled (smearMicroscopyDoneCheckBox.getValue ());
			smearMicroscopyStartDateBox.setEnabled (smearMicroscopyDoneCheckBox.getValue ());
			smearMicroscopyEndDateBox.setEnabled (smearMicroscopyDoneCheckBox.getValue ());
		}
	}
}
