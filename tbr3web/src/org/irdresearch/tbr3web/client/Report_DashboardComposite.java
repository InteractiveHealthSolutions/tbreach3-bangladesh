/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.tbr3web.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.irdresearch.tbr3web.shared.AccessType;
import org.irdresearch.tbr3web.shared.TBR3;
import org.irdresearch.tbr3web.shared.UserRightsUtil;
import org.irdresearch.tbr3web.shared.model.User;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextBoxBase;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class Report_DashboardComposite extends Composite implements IForm, ClickHandler, ChangeHandler, SelectionHandler<Integer>
{
	private static ServerServiceAsync	service						= GWT.create (ServerService.class);
	private static LoadingWidget		loading						= new LoadingWidget ();
	private static final String			menuName					= "DATALOG";

	private UserRightsUtil				rights						= new UserRightsUtil ();

	private FlexTable					flexTable					= new FlexTable ();
	private FlexTable					topFlexTable				= new FlexTable ();

	private DecoratedTabPanel			decoratedTabPanel			= new DecoratedTabPanel ();

	private Grid						usersGrid					= new Grid (5, 2);
	private Grid						formsGrid					= new Grid (6, 2);
	private Grid						patientsGrid				= new Grid (5, 2);
	private Grid						testsGrid					= new Grid (6, 2);

	private Button						refreshButton				= new Button ("Refresh");
	private Button						closeButton					= new Button ("Close");

	private Label						topLabel					= new Label (TBR3.getProjectTitle () + " Dashboard");
	private Label						lblTotalUsers				= new Label ("Total Users:");
	private Label						lblActiveUsers				= new Label ("Active Users:");
	private Label						lblUsersLoggedIn			= new Label ("Users Logged In:");
	private Label						lblLastLogin				= new Label ("Last Login:");
	private Label						lblAverageSessionTime		= new Label ("Average Session Time (minutes):");
	private Label						lblTotalFormsFilled			= new Label ("Total Forms Filled:");
	private Label						lblMaximumFilled			= new Label ("Maximum Filled:");
	private Label						lblMinimumFilled			= new Label ("Minimum Filled:");
	private Label						lblAverageTimePer			= new Label ("Average Filling Time (minutes):");
	private Label						lblLastFilled				= new Label ("Last Filled:");
	private Label						lblScreened					= new Label ("Screening:");
	private Label						lblConfirmedPatients		= new Label ("Confirmed Cases:");
	private Label						lblClosedCases				= new Label ("Closed Cases:");
	private Label						lblTotalTests				= new Label ("Total Tests:");
	private Label						lblPendingResults			= new Label ("Pending Results:");
	private Label						lblIndicatingTb				= new Label ("Indicating TB:");
	private Label						lblNonindicating			= new Label ("Non-Indicating:");
	private Label						lblOtherResults				= new Label ("Other Results:");

	private TextBox						totalUsersTextBox			= new TextBox ();
	private TextBox						activeUsersTextBox			= new TextBox ();
	private TextBox						usersLoggedInTextBox		= new TextBox ();
	private TextBox						lastLoginTextBox			= new TextBox ();
	private TextBox						averageSessionTextBox		= new TextBox ();
	private TextBox						totalFormsFilledTextBox		= new TextBox ();
	private TextBox						maximumFilledTextBox		= new TextBox ();
	private TextBox						minimumFilledTextBox		= new TextBox ();
	private TextBox						averageFillTimeTextBox		= new TextBox ();
	private TextBox						lastFilledTextBox			= new TextBox ();
	private TextBox						totalTestsTextBox			= new TextBox ();
	private TextBox						pendingResultsTextBox		= new TextBox ();
	private TextBox						positiveResultsTextBox		= new TextBox ();
	private TextBox						negativeResultsTextBox		= new TextBox ();
	private TextBox						otherResultsTextBox			= new TextBox ();

	private TextArea					screeningTextArea			= new TextArea ();
	private TextArea					confirmedCasesTextArea		= new TextArea ();
	private TextArea					closedCasesTextArea			= new TextArea ();

	private CheckBox					specifyFormTypeCheckBox		= new CheckBox ("Specify Form Type:");
	private CheckBox					patientOptionsCheckBox		= new CheckBox ("More Options");
	private CheckBox					testTypeCheckBox			= new CheckBox ("Test Type");

	private ListBox						encounterTypeComboBox		= new ListBox ();
	private ListBox						patientOptionsComboBox		= new ListBox ();
	private ListBox						patientOptionsListComboBox	= new ListBox ();
	private ListBox						textTypeComboBox			= new ListBox ();

	public Report_DashboardComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("80%", "100%");
		flexTable.setWidget (0, 0, topFlexTable);
		topFlexTable.setSize ("100%", "100%");
		topLabel.setWordWrap (false);
		topLabel.setStyleName ("title");
		topFlexTable.setWidget (0, 0, topLabel);
		topFlexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getRowFormatter ().setVerticalAlign (3, HasVerticalAlignment.ALIGN_TOP);
		flexTable.setWidget (1, 0, decoratedTabPanel);
		decoratedTabPanel.setSize ("100%", "100%");
		usersGrid.setStyleName ("grid");
		decoratedTabPanel.add (usersGrid, "Users", false);
		usersGrid.setSize ("100%", "100%");
		lblTotalUsers.setWordWrap (false);
		usersGrid.setWidget (0, 0, lblTotalUsers);
		totalUsersTextBox.setName ("select count(user_name) as Value from user as u");
		totalUsersTextBox.setMaxLength (4);
		totalUsersTextBox.setVisibleLength (4);
		totalUsersTextBox.setReadOnly (true);
		usersGrid.setWidget (0, 1, totalUsersTextBox);
		lblActiveUsers.setWordWrap (false);
		usersGrid.setWidget (1, 0, lblActiveUsers);
		activeUsersTextBox.setName ("select count(user_name) as Value from user as u where current_status='A'");
		activeUsersTextBox.setVisibleLength (4);
		activeUsersTextBox.setReadOnly (true);
		activeUsersTextBox.setMaxLength (4);
		usersGrid.setWidget (1, 1, activeUsersTextBox);
		lblUsersLoggedIn.setWordWrap (false);
		usersGrid.setWidget (2, 0, lblUsersLoggedIn);
		usersLoggedInTextBox.setName ("select count(user_name) as Value from user as u where current_status='A' and logged_in = 1");
		usersLoggedInTextBox.setVisibleLength (4);
		usersLoggedInTextBox.setReadOnly (true);
		usersLoggedInTextBox.setMaxLength (4);
		usersGrid.setWidget (2, 1, usersLoggedInTextBox);
		lblLastLogin.setWordWrap (false);
		usersGrid.setWidget (3, 0, lblLastLogin);
		lastLoginTextBox.setName ("select cast(concat(user_name, ' ON ', date_login) as char) as Value from log_login order by date_login desc limit 1");
		lastLoginTextBox.setVisibleLength (35);
		lastLoginTextBox.setReadOnly (true);
		lastLoginTextBox.setMaxLength (35);
		usersGrid.setWidget (3, 1, lastLoginTextBox);
		lblAverageSessionTime.setWordWrap (false);
		usersGrid.setWidget (4, 0, lblAverageSessionTime);
		averageSessionTextBox.setName ("select round(avg(minute(timediff(date_logout, date_login)))) as avg from log_login where date_login is not null and date_logout is not null");
		averageSessionTextBox.setVisibleLength (4);
		averageSessionTextBox.setReadOnly (true);
		averageSessionTextBox.setMaxLength (4);
		usersGrid.setWidget (4, 1, averageSessionTextBox);
		decoratedTabPanel.add (formsGrid, "Forms", false);
		formsGrid.setSize ("100%", "100%");
		specifyFormTypeCheckBox.setWordWrap (false);
		formsGrid.setWidget (0, 0, specifyFormTypeCheckBox);
		encounterTypeComboBox.setEnabled (false);
		formsGrid.setWidget (0, 1, encounterTypeComboBox);
		lblTotalFormsFilled.setWordWrap (false);
		formsGrid.setWidget (1, 0, lblTotalFormsFilled);
		totalFormsFilledTextBox.setName ("select count(e_id) from encounter where encounter_type like '%%'");
		totalFormsFilledTextBox.setVisibleLength (4);
		totalFormsFilledTextBox.setReadOnly (true);
		totalFormsFilledTextBox.setMaxLength (4);
		formsGrid.setWidget (1, 1, totalFormsFilledTextBox);
		lblMaximumFilled.setWordWrap (false);
		formsGrid.setWidget (2, 0, lblMaximumFilled);
		maximumFilledTextBox.setVisibleLength (35);
		maximumFilledTextBox.setReadOnly (true);
		maximumFilledTextBox
				.setName ("select et.description from (select encounter_type, count(*) as total from encounter where encounter_type like '%%' group by encounter_type order by count(*) desc) as A inner join encounter_type as et using (encounter_type) limit 1");
		maximumFilledTextBox.setMaxLength (35);
		formsGrid.setWidget (2, 1, maximumFilledTextBox);
		lblMinimumFilled.setWordWrap (false);
		formsGrid.setWidget (3, 0, lblMinimumFilled);
		minimumFilledTextBox.setVisibleLength (35);
		minimumFilledTextBox.setReadOnly (true);
		minimumFilledTextBox
				.setName ("select et.description from (select encounter_type, count(*) as total from encounter where encounter_type like '%%' group by encounter_type order by count(*)) as A inner join encounter_type as et using (encounter_type) limit 1");
		minimumFilledTextBox.setMaxLength (35);
		formsGrid.setWidget (3, 1, minimumFilledTextBox);
		lblAverageTimePer.setWordWrap (false);
		formsGrid.setWidget (4, 0, lblAverageTimePer);
		averageFillTimeTextBox.setVisibleLength (4);
		averageFillTimeTextBox.setReadOnly (true);
		averageFillTimeTextBox
				.setName ("select round(avg(minute(timediff(date_end, date_start)))) as avg from encounter where encounter_type like '%%' and date_start is not null and date_end is not null");
		averageFillTimeTextBox.setMaxLength (4);
		formsGrid.setWidget (4, 1, averageFillTimeTextBox);
		formsGrid.setWidget (5, 0, lblLastFilled);
		lastFilledTextBox.setVisibleLength (65);
		lastFilledTextBox.setReadOnly (true);
		lastFilledTextBox
				.setName ("select cast(concat(et.description, ' BY ', u.user_name, ' ON ', e.date_end) as char) as Value from encounter as e inner join user as u on e.pid2 = u.pid inner join encounter_type as et using (encounter_type) order by e.date_end desc limit 1");
		lastFilledTextBox.setMaxLength (65);
		formsGrid.setWidget (5, 1, lastFilledTextBox);
		decoratedTabPanel.add (patientsGrid, "Patients", false);
		patientsGrid.setSize ("100%", "100%");
		patientOptionsCheckBox.setWordWrap (false);
		patientsGrid.setWidget (0, 0, patientOptionsCheckBox);
		patientOptionsComboBox.addItem ("-- Select --");
		patientOptionsComboBox.addItem ("AGE");
		patientOptionsComboBox.addItem ("GENDER");
		patientOptionsComboBox.setEnabled (false);
		patientsGrid.setWidget (0, 1, patientOptionsComboBox);
		patientOptionsListComboBox.setEnabled (false);
		patientsGrid.setWidget (1, 1, patientOptionsListComboBox);
		lblScreened.setWordWrap (false);
		patientsGrid.setWidget (2, 0, lblScreened);
		screeningTextArea.setCharacterWidth (40);
		screeningTextArea.setReadOnly (true);
		patientsGrid.setWidget (2, 1, screeningTextArea);
		lblConfirmedPatients.setWordWrap (false);
		patientsGrid.setWidget (3, 0, lblConfirmedPatients);
		confirmedCasesTextArea.setCharacterWidth (40);
		confirmedCasesTextArea.setReadOnly (true);
		patientsGrid.setWidget (3, 1, confirmedCasesTextArea);
		lblClosedCases.setWordWrap (false);
		patientsGrid.setWidget (4, 0, lblClosedCases);
		closedCasesTextArea.setCharacterWidth (40);
		closedCasesTextArea.setVisibleLines (5);
		closedCasesTextArea.setReadOnly (true);
		patientsGrid.setWidget (4, 1, closedCasesTextArea);
		decoratedTabPanel.add (testsGrid, "Lab Tests", false);
		testsGrid.setSize ("100%", "100%");
		testsGrid.setWidget (0, 0, testTypeCheckBox);
		textTypeComboBox.addItem ("Smear Microscopy");
		textTypeComboBox.addItem ("Gene Xpert Test");
		textTypeComboBox.addItem ("Chest X-Ray");
		textTypeComboBox.addItem ("Mantoux Screening Test");
		textTypeComboBox.setEnabled (false);
		testsGrid.setWidget (0, 1, textTypeComboBox);
		testsGrid.setWidget (1, 0, lblTotalTests);
		totalTestsTextBox.setMaxLength (4);
		totalTestsTextBox.setVisibleLength (4);
		totalTestsTextBox.setReadOnly (true);
		testsGrid.setWidget (1, 1, totalTestsTextBox);
		testsGrid.setWidget (2, 0, lblPendingResults);
		pendingResultsTextBox.setMaxLength (4);
		pendingResultsTextBox.setVisibleLength (4);
		pendingResultsTextBox.setReadOnly (true);
		testsGrid.setWidget (2, 1, pendingResultsTextBox);
		testsGrid.setWidget (3, 0, lblIndicatingTb);
		positiveResultsTextBox.setMaxLength (4);
		positiveResultsTextBox.setVisibleLength (4);
		positiveResultsTextBox.setReadOnly (true);
		testsGrid.setWidget (3, 1, positiveResultsTextBox);
		testsGrid.setWidget (4, 0, lblNonindicating);
		negativeResultsTextBox.setMaxLength (4);
		negativeResultsTextBox.setVisibleLength (4);
		negativeResultsTextBox.setReadOnly (true);
		testsGrid.setWidget (4, 1, negativeResultsTextBox);
		testsGrid.setWidget (5, 0, lblOtherResults);
		otherResultsTextBox.setMaxLength (4);
		otherResultsTextBox.setVisibleLength (4);
		otherResultsTextBox.setReadOnly (true);
		testsGrid.setWidget (5, 1, otherResultsTextBox);
		flexTable.setWidget (2, 0, refreshButton);
		refreshButton.setWidth ("100%");
		refreshButton.setText ("Refresh");
		refreshButton.setEnabled (false);
		flexTable.setWidget (3, 0, closeButton);
		flexTable.getCellFormatter ().setHorizontalAlignment (3, 0, HasHorizontalAlignment.ALIGN_CENTER);

		specifyFormTypeCheckBox.addClickHandler (this);
		patientOptionsCheckBox.addClickHandler (this);
		patientOptionsComboBox.addChangeHandler (this);
		refreshButton.addClickHandler (this);
		closeButton.addClickHandler (this);

		refresh (flexTable);
		try
		{
			load (true);
			service.getTableData ("encounter_type", new String[] {"encounter_type", "description"}, "", new AsyncCallback<String[][]> ()
			{
				public void onSuccess (String[][] result)
				{
					encounterTypeComboBox.clear ();
					for (int i = 0; i < result.length; i++)
						encounterTypeComboBox.addItem (result[i][1], result[i][0]);
					setRights (menuName);
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
		setRights (menuName);
		displaySummary ();
	}

	private void displaySummary ()
	{
		final ArrayList<String> sqlQueries = new ArrayList<String> ();
		try
		{
			/* Fill summaries for Users grid */
			sqlQueries.clear ();
			for (Iterator<Widget> iter = usersGrid.iterator (); iter.hasNext ();)
			{
				Widget w = iter.next ();
				if (w instanceof TextBox)
				{
					TextBox textBox = (TextBox) w;
					sqlQueries.add (textBox.getName ());
				}
			}
			service.getQueriesResults (sqlQueries.toArray (new String[] {}), new AsyncCallback<String[]> ()
			{
				public void onSuccess (String[] result)
				{
					int cnt = 0;
					for (Iterator<Widget> iter = usersGrid.iterator (); iter.hasNext ();)
					{
						Widget w = iter.next ();
						if (w instanceof TextBox)
							((TextBox) w).setText (result[cnt++]);
					}
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
				}
			});

			/* Fill summaries for Forms grid */
			sqlQueries.clear ();
			for (Iterator<Widget> iter = formsGrid.iterator (); iter.hasNext ();)
			{
				Widget w = iter.next ();
				String encounter = specifyFormTypeCheckBox.getValue () ? TBRK3lient.get (encounterTypeComboBox) : "";
				if (w instanceof TextBox)
				{
					TextBox textBox = (TextBox) w;
					sqlQueries.add (textBox.getName ().replaceAll ("%%", "%" + encounter + "%"));
				}
			}
			service.getQueriesResults (sqlQueries.toArray (new String[] {}), new AsyncCallback<String[]> ()
			{
				public void onSuccess (String[] result)
				{
					int cnt = 0;
					for (Iterator<Widget> iter = formsGrid.iterator (); iter.hasNext ();)
					{
						Widget w = iter.next ();
						if (w instanceof TextBox)
							((TextBox) w).setText (result[cnt++]);
					}
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
				}
			});

			/* Fill summaries for Patients grid */
			String filter = "";
			if (patientOptionsCheckBox.getValue ())
			{
				String option = TBRK3lient.get (patientOptionsComboBox);
				String selection = TBRK3lient.get (patientOptionsListComboBox);
				if (option.equals ("AGE"))
					filter = " and person.approximate_age between " + selection;
				else if (option.equals ("GENDER"))
					filter = " and person.gender = '" + selection + "'";
			}
			// Fill Screening
			sqlQueries.clear ();
			sqlQueries.add ("select count(*) from patient inner join person using (pid) where 1 = 1 " + filter);
			sqlQueries.add ("select count(*) from patient inner join person using (pid) where disease_suspected = 1 " + filter);
			service.getQueriesResults (sqlQueries.toArray (new String[] {}), new AsyncCallback<String[]> ()
			{
				public void onSuccess (String[] result)
				{
					StringBuilder builder = new StringBuilder ();
					builder.append ("Total: " + result[0] + "\n");
					builder.append ("Suspects: " + result[1]);
					screeningTextArea.setText (builder.toString ());
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
				}
			});
			// Fill Confirmed Cases
			sqlQueries.clear ();
			sqlQueries.add ("select count(*) from patient inner join person using (pid) where disease_confirmed = 1 " + filter);
			sqlQueries.add ("select count(*) from patient inner join person using (pid) where disease_suspected = 1 and date_baseline is not null " + filter);
			service.getQueriesResults (sqlQueries.toArray (new String[] {}), new AsyncCallback<String[]> ()
			{
				public void onSuccess (String[] result)
				{
					StringBuilder builder = new StringBuilder ();
					builder.append ("Total: " + result[0] + "\n");
					builder.append ("On Treatment: " + result[1]);
					confirmedCasesTextArea.setText (builder.toString ());
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
				}
			});
			// Fill Closed Cases
			sqlQueries.clear ();
			sqlQueries.add ("select count(*) from patient inner join person using (pid) where patient_status = 'CLOSED' " + filter);
			sqlQueries.add ("select count(*) from patient inner join person using (pid) where patient_status = 'CLOSED' and treatment_outcome = 'CURED' " + filter);
			sqlQueries.add ("select count(*) from patient inner join person using (pid) where patient_status = 'CLOSED' and treatment_outcome = 'TX_COMP' " + filter);
			sqlQueries.add ("select count(*) from patient inner join person using (pid) where patient_status = 'CLOSED' and treatment_outcome = 'TX_FAIL' " + filter);
			sqlQueries.add ("select count(*) from patient inner join person using (pid) where patient_status = 'CLOSED' and treatment_outcome = 'DIED' " + filter);
			sqlQueries.add ("select count(*) from patient inner join person using (pid) where patient_status = 'CLOSED' and treatment_outcome = 'REFERRED' " + filter);
			service.getQueriesResults (sqlQueries.toArray (new String[] {}), new AsyncCallback<String[]> ()
			{
				public void onSuccess (String[] result)
				{
					StringBuilder builder = new StringBuilder ();
					builder.append ("Total: " + result[0] + "\n");
					builder.append ("Cured: " + result[1] + "\n");
					builder.append ("Treatment Completed: " + result[2] + "\n");
					builder.append ("Treatment Failed: " + result[3] + "\n");
					builder.append ("Died: " + result[4] + "\n");
					builder.append ("Referred: " + result[5]);
					closedCasesTextArea.setText (builder.toString ());
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
				}
			});
			/* Fill summaries for Lab Results grid */
			filter = "";
			sqlQueries.clear ();
			service.getQueriesResults (sqlQueries.toArray (new String[] {}), new AsyncCallback<String[]> ()
			{
				public void onSuccess (String[] result)
				{
					StringBuilder builder = new StringBuilder ();
					closedCasesTextArea.setText (builder.toString ());
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
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
	 * This method refreshes data inside a widget recursively. If the widget is
	 * a List box and the "name" property of the list box is set, then the
	 * method searches the value in "name" (e.g. MARITAL_STATUS) in definitions
	 * and loads into the list. Otherwise if the "name" property is not set, the
	 * list box is left untouched. If the widget is text-type, and the "name"
	 * property is set, then the method sets max length to the allowed length in
	 * table meta data. (The format of name property has to be in the format:
	 * table_name;column_name)
	 * 
	 * @param widget
	 */
	public void refresh (Widget widget)
	{
		if (widget instanceof FlexTable)
		{
			Iterator<Widget> iter = ((FlexTable) widget).iterator ();
			while (iter.hasNext ())
				refresh (iter.next ());
		}
		else if (widget instanceof Panel)
		{
			Iterator<Widget> iter = ((Panel) widget).iterator ();
			while (iter.hasNext ())
				refresh (iter.next ());
		}
		else if (widget instanceof TextBox)
		{
			TextBox text = (TextBox) widget;
			String name = text.getName ();
			if (!name.equals (""))
			{
				String[] parts = name.split (";");
				if (parts.length == 2)
					text.setMaxLength (TBR3.getMaxLength (parts[0], parts[1]));
			}
		}
		else if (widget instanceof ListBox)
		{
			if (!((ListBox) widget).getName ().equals (""))
				widget = TBRK3lient.fillList (widget);
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

	public void fillData ()
	{
		try
		{
			load (false);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			load (false);
		}
	}

	public void clearUp ()
	{
		clearControls (flexTable);
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
					try
					{
						service.findUser (TBR3.getCurrentUserName (), new AsyncCallback<User> ()
						{
							public void onSuccess (User result)
							{
								rights.setRoleRights (TBR3.getCurrentRole (), userRights);
								refreshButton.setEnabled (rights.getAccess (AccessType.DELETE));
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

	public void onClick (ClickEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == specifyFormTypeCheckBox)
		{
			encounterTypeComboBox.setEnabled (specifyFormTypeCheckBox.getValue ());
			load (false);
		}
		else if (sender == patientOptionsCheckBox)
		{
			patientOptionsComboBox.setEnabled (patientOptionsCheckBox.getValue ());
			patientOptionsListComboBox.setEnabled (patientOptionsCheckBox.getValue ());
			load (false);
		}
		else if (sender == refreshButton)
		{
			displaySummary ();
			load (false);
		}
		else if (sender == closeButton)
		{
			MainMenuComposite.clear ();
		}
	}

	public void onChange (ChangeEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == patientOptionsComboBox)
		{
			patientOptionsListComboBox.clear ();
			if (TBRK3lient.get (patientOptionsComboBox).equals ("AGE"))
			{
				patientOptionsListComboBox.addItem ("0 TO 15", "0 and 15");
				patientOptionsListComboBox.addItem ("16 TO 24", "16 and 24");
				patientOptionsListComboBox.addItem ("25 TO 40", "24 and 40");
				patientOptionsListComboBox.addItem ("MORE THAN 40", "41 and 999");
				load (false);
			}
			else if (TBRK3lient.get (patientOptionsComboBox).equals ("GENDER"))
			{
				patientOptionsListComboBox.addItem ("MALE", "M");
				patientOptionsListComboBox.addItem ("FEMALE", "F");
				load (false);
			}
			else
				load (false);
		}
	}

	public void onSelection (SelectionEvent<Integer> event)
	{
		Widget sender = (Widget) event.getSource ();
		load (true);
		if (sender == decoratedTabPanel)
		{
			load (false);
		}
	}

	public void setCurrent ()
	{
	}

	public boolean validate ()
	{
		return false;
	}

	public void saveData ()
	{
	}

	public void updateData ()
	{
	}

	public void deleteData ()
	{
	}
}
