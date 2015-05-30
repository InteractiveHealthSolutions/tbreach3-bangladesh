/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.tbr3web.client;

import java.util.Date;
import org.irdresearch.tbr3web.shared.AccessType;
import org.irdresearch.tbr3web.shared.DataType;
import org.irdresearch.tbr3web.shared.Parameter;
import org.irdresearch.tbr3web.shared.TBR3;
import org.irdresearch.tbr3web.shared.UserRightsUtil;
import org.irdresearch.tbr3web.shared.model.User;
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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DateBox.DefaultFormat;

public class Report_ReportsComposite extends Composite implements IReport, ClickHandler, ChangeHandler, ValueChangeHandler<Boolean>
{
	private static ServerServiceAsync	service							= GWT.create (ServerService.class);
	private static LoadingWidget		loading							= new LoadingWidget ();
	private static final String			menuName						= "DATALOG";
	private String						reportsDB						= "tbreachkenya_rpt";
	private String						filter							= "";
	private String						startDate						= "";
	private String						endDate							= "";
	private String						patientId						= "";
	private String						district						= "";
	private String						facility						= "";
	private String						cmId							= "";
	private String						fcId							= "";
	private UserRightsUtil				rights							= new UserRightsUtil ();

	private FlexTable					flexTable						= new FlexTable ();
	private FlexTable					topFlexTable					= new FlexTable ();
	private FlexTable					rightFlexTable					= new FlexTable ();
	private FlexTable					filterFlexTable					= new FlexTable ();

	private Grid						grid							= new Grid (1, 4);

	private HorizontalPanel				dateRangeHorizontalPanel		= new HorizontalPanel ();
	private HorizontalPanel				timeRangeHorizontalPanel		= new HorizontalPanel ();
	private HorizontalPanel				coughMonitorHorizontalPanel		= new HorizontalPanel ();
	private HorizontalPanel				fieldCoordinatorHorizontalPanel	= new HorizontalPanel ();
	private HorizontalPanel				patientIdHorizontalPanel		= new HorizontalPanel ();

	private Button						viewButton						= new Button ("Save");
	private Button						closeButton						= new Button ("Close");
	private Button						exportButton					= new Button ("Export");

	private Label						lblSelectCategory				= new Label ("Select Category:");
	private Label						lblSnapshot						= new Label ("Snapshot:");
	private Label						snapshotLabel					= new Label ();
	private Label						lblCaution						= new Label ("Some reports may take 5 to 10 minutes to generate. Please wait until report download window appears.");
	private Label						lblTbReachLog					= new Label (TBR3.getProjectTitle () + " Report");
	private Label						lblSelectReport					= new Label ("Select Report:");
	private Label						lblFilter						= new Label ("Filter (Check all that apply):");
	private TextBox						patientIdTextBox				= new TextBox ();
	private TextBox						cmIdTextBox						= new TextBox ();
	private TextBox						fcIdTextBox						= new TextBox ();

	private ListBox						categoryComboBox				= new ListBox ();
	private ListBox						reportsListComboBox				= new ListBox ();
	private ListBox						patientFilterTypeComboBox		= new ListBox ();
	private ListBox						districtComboBox				= new ListBox ();
	private ListBox						facilityComboBox				= new ListBox ();
	private ListBox						cmFilterTypeComboBox			= new ListBox ();
	private ListBox						fcFilterTypeComboBox			= new ListBox ();

	private DateBox						fromDateBox						= new DateBox ();
	private DateBox						toDateBox						= new DateBox ();
	private DateBox						fromTimeDateBox					= new DateBox ();
	private DateBox						toTimeDateBox					= new DateBox ();

	private CheckBox					dateRangeFilterCheckBox			= new CheckBox ("Date Range:");
	private CheckBox					timeRangeFilterCheckBox			= new CheckBox ("Time Range:");
	private CheckBox					patientIdCheckBox				= new CheckBox ("Patient ID:");
	private CheckBox					districtCheckBox				= new CheckBox ("District:");
	private CheckBox					facilityCheckBox				= new CheckBox ("Facility:");
	private CheckBox					fcIdCheckBox					= new CheckBox ("Field Coordinator:");
	private CheckBox					cmIdCheckBox					= new CheckBox ("Cough Monitor:");

	@SuppressWarnings("deprecation")
	public Report_ReportsComposite ()
	{
		initWidget (flexTable);
		flexTable.setSize ("700px", "100%");
		flexTable.setWidget (0, 0, topFlexTable);
		topFlexTable.setSize ("100%", "100%");
		lblTbReachLog.setStyleName ("title");
		topFlexTable.setWidget (0, 0, lblTbReachLog);
		topFlexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		topFlexTable.getRowFormatter ().setVerticalAlign (0, HasVerticalAlignment.ALIGN_MIDDLE);
		topFlexTable.getCellFormatter ().setVerticalAlignment (0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		flexTable.setWidget (1, 0, rightFlexTable);
		rightFlexTable.setSize ("100%", "100%");
		rightFlexTable.setWidget (0, 0, lblSnapshot);
		rightFlexTable.setWidget (0, 1, snapshotLabel);
		rightFlexTable.setWidget (1, 0, lblSelectCategory);
		categoryComboBox.addItem ("-- Select Category --");
		categoryComboBox.addItem ("Reports");
		categoryComboBox.addItem ("Form Dumps");
		rightFlexTable.setWidget (1, 1, categoryComboBox);
		categoryComboBox.setWidth ("100%");
		rightFlexTable.setWidget (2, 0, lblSelectReport);
		rightFlexTable.setWidget (2, 1, reportsListComboBox);
		reportsListComboBox.setWidth ("100%");
		reportsListComboBox.addChangeHandler (this);
		lblCaution.setStyleName ("gwt-MenuItem-selected");
		rightFlexTable.setWidget (3, 1, lblCaution);
		lblCaution.setWidth ("100%");
		rightFlexTable.setWidget (4, 0, lblFilter);
		rightFlexTable.setWidget (4, 1, filterFlexTable);
		filterFlexTable.setWidth ("100%");
		filterFlexTable.setWidget (0, 0, dateRangeFilterCheckBox);
		dateRangeFilterCheckBox.setVisible (true);
		timeRangeFilterCheckBox.setVisible (true);
		dateRangeFilterCheckBox.setWidth ("");
		filterFlexTable.setWidget (0, 1, dateRangeHorizontalPanel);
		dateRangeHorizontalPanel.setSize ("200px", "");
		dateRangeHorizontalPanel.add (fromDateBox);
		fromDateBox.setEnabled (false);
		fromDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("dd-MM-yyyy")));
		fromDateBox.setWidth ("100px");
		dateRangeHorizontalPanel.add (toDateBox);
		toDateBox.setEnabled (false);
		toDateBox.setFormat (new DefaultFormat (DateTimeFormat.getFormat ("dd-MM-yyyy")));
		toDateBox.setWidth ("100px");
		filterFlexTable.setWidget (1, 0, timeRangeFilterCheckBox);
		filterFlexTable.setWidget (1, 1, timeRangeHorizontalPanel);
		timeRangeHorizontalPanel.setWidth ("100px");
		timeRangeHorizontalPanel.add (fromTimeDateBox);
		fromTimeDateBox.setEnabled (false);
		fromTimeDateBox.setValue (new Date (1297969200000L));
		fromTimeDateBox.setFormat (new DefaultFormat (DateTimeFormat.getShortTimeFormat ()));
		fromTimeDateBox.setWidth ("50px");
		timeRangeHorizontalPanel.add (toTimeDateBox);
		toTimeDateBox.setEnabled (false);
		toTimeDateBox.setValue (new Date (1298012400000L));
		toTimeDateBox.setFormat (new DefaultFormat (DateTimeFormat.getShortTimeFormat ()));
		toTimeDateBox.setWidth ("50px");
		filterFlexTable.setWidget (2, 0, districtCheckBox);
		districtComboBox.setEnabled (false);
		filterFlexTable.setWidget (2, 1, districtComboBox);
		districtComboBox.setWidth ("100%");
		filterFlexTable.setWidget (3, 0, facilityCheckBox);
		facilityComboBox.setEnabled (false);
		filterFlexTable.setWidget (3, 1, facilityComboBox);
		facilityComboBox.setWidth ("100%");
		filterFlexTable.setWidget (4, 0, cmIdCheckBox);
		filterFlexTable.setWidget (4, 1, coughMonitorHorizontalPanel);
		coughMonitorHorizontalPanel.setWidth ("100%");
		coughMonitorHorizontalPanel.add (cmFilterTypeComboBox);
		cmFilterTypeComboBox.setEnabled (false);
		cmFilterTypeComboBox.setWidth ("100%");
		coughMonitorHorizontalPanel.add (cmIdTextBox);
		cmIdTextBox.setEnabled (false);
		cmIdTextBox.setVisibleLength (12);
		cmIdTextBox.setMaxLength (12);
		cmIdTextBox.setWidth ("100%");
		filterFlexTable.setWidget (5, 0, fcIdCheckBox);
		filterFlexTable.setWidget (5, 1, fieldCoordinatorHorizontalPanel);
		fieldCoordinatorHorizontalPanel.setWidth ("100%");
		fieldCoordinatorHorizontalPanel.add (fcFilterTypeComboBox);
		fcFilterTypeComboBox.setEnabled (false);
		fcFilterTypeComboBox.setWidth ("100%");
		fieldCoordinatorHorizontalPanel.add (fcIdTextBox);
		fcIdTextBox.setEnabled (false);
		fcIdTextBox.setVisibleLength (12);
		fcIdTextBox.setMaxLength (12);
		fcIdTextBox.setWidth ("100%");
		filterFlexTable.setWidget (6, 0, patientIdCheckBox);
		filterFlexTable.setWidget (6, 1, patientIdHorizontalPanel);
		patientIdHorizontalPanel.setWidth ("100%");
		patientIdHorizontalPanel.add (patientFilterTypeComboBox);
		patientFilterTypeComboBox.setEnabled (false);
		patientFilterTypeComboBox.setWidth ("100%");
		patientIdHorizontalPanel.add (patientIdTextBox);
		patientIdTextBox.setVisibleLength (12);
		patientIdTextBox.setMaxLength (12);
		patientIdTextBox.setEnabled (false);
		patientIdTextBox.setWidth ("100%");
		filterFlexTable.getCellFormatter ().setHorizontalAlignment (1, 1, HasHorizontalAlignment.ALIGN_LEFT);
		rightFlexTable.setWidget (5, 1, grid);
		grid.setSize ("100%", "100%");
		viewButton.setEnabled (false);
		viewButton.setText ("View");
		grid.setWidget (0, 0, viewButton);
		exportButton.setEnabled (false);
		grid.setWidget (0, 1, exportButton);
		grid.setWidget (0, 3, closeButton);
		rightFlexTable.getCellFormatter ().setHorizontalAlignment (4, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flexTable.getRowFormatter ().setVerticalAlign (1, HasVerticalAlignment.ALIGN_TOP);
		rightFlexTable.getRowFormatter ().setVerticalAlign (4, HasVerticalAlignment.ALIGN_TOP);
		flexTable.getCellFormatter ().setHorizontalAlignment (0, 0, HasHorizontalAlignment.ALIGN_CENTER);

		dateRangeFilterCheckBox.addValueChangeHandler (this);
		timeRangeFilterCheckBox.addValueChangeHandler (this);
		districtCheckBox.addValueChangeHandler (this);
		facilityCheckBox.addValueChangeHandler (this);
		fcIdCheckBox.addValueChangeHandler (this);
		cmIdCheckBox.addValueChangeHandler (this);
		patientIdCheckBox.addValueChangeHandler (this);
		categoryComboBox.addChangeHandler (this);
		viewButton.addClickHandler (this);
		exportButton.addClickHandler (this);
		closeButton.addClickHandler (this);

		refreshList ();
		setRights (menuName);
	}

	private void refreshList ()
	{
		String[] filterOptions = {"IS EXACTLY", "STARTS WITH", "ENDS ON", "LOOKS LIKE", "NOT LIKE"};
		for (String s : filterOptions)
		{
			cmFilterTypeComboBox.addItem (s);
			fcFilterTypeComboBox.addItem (s);
			patientFilterTypeComboBox.addItem (s);
		}
		try
		{
			service.getSnapshotTime (new AsyncCallback<String> ()
			{
				public void onSuccess (String result)
				{
					snapshotLabel.setText (result);
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
				}
			});
			service.getTableData ("select location_id, location_name from location where location_type='DISTRICT' order by location_name", new AsyncCallback<String[][]> ()
			{
				public void onSuccess (String[][] result)
				{
					districtComboBox.clear ();
					for (int i = 0; i < result.length; i++)
						districtComboBox.addItem (result[i][1].toString (), result[i][0].toString ());
				}

				public void onFailure (Throwable caught)
				{
					caught.printStackTrace ();
				}
			});
			service.getTableData ("select location_id, location_name from location where location_type='FACILITY' order by location_name", new AsyncCallback<String[][]> ()
			{
				public void onSuccess (String[][] result)
				{
					facilityComboBox.clear ();
					for (int i = 0; i < result.length; i++)
						facilityComboBox.addItem (result[i][1].toString (), result[i][0].toString ());
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

	/**
	 * Creates appropriate filter for given column names
	 * 
	 * @param patientColumnName
	 * @param dateColumnName
	 * @param facilityColumnName
	 * @param fcColumnName
	 * @param cmColumnName
	 */
	@SuppressWarnings("deprecation")
	private String filterData (String dateColumnName, String districtColumnName, String facilityColumnName, String fcColumnName, String cmColumnName, String patientColumnName)
	{
		filter = "";
		startDate = "";
		endDate = "";
		district = "";
		facility = "";
		patientId = "";
		cmId = "";
		fcId = "";

		if (dateRangeFilterCheckBox.getValue ())
		{
			Date start = new Date (fromDateBox.getValue ().getTime ());
			Date end = new Date (toDateBox.getValue ().getTime ());
			StringBuilder startString = new StringBuilder ();
			StringBuilder endString = new StringBuilder ();
			if (timeRangeFilterCheckBox.getValue ())
			{
				start.setHours (fromTimeDateBox.getValue ().getHours ());
				start.setMinutes (fromTimeDateBox.getValue ().getMinutes ());
				end.setHours (toTimeDateBox.getValue ().getHours ());
				end.setMinutes (toTimeDateBox.getValue ().getMinutes ());
			}
			startString.append ((start.getYear () + 1900) + "-" + (start.getMonth () + 1) + "-" + start.getDate () + " " + start.getHours () + ":" + start.getMinutes () + ":00");
			endString.append ((end.getYear () + 1900) + "-" + (end.getMonth () + 1) + "-" + end.getDate () + " " + end.getHours () + ":" + end.getMinutes () + ":00");
			startDate = startString.toString ();
			endDate = endString.toString ();
		}
		if (districtCheckBox.getValue ())
		{
			district = " = '" + TBRK3lient.get (districtComboBox) + "'";
		}
		if (facilityCheckBox.getValue ())
		{
			facility = " = '" + TBRK3lient.get (facilityComboBox) + "'";
		}
		if (fcIdCheckBox.getValue ())
		{
			switch (fcFilterTypeComboBox.getSelectedIndex ())
			{
				case 0 :
					fcId = " = '" + TBRK3lient.get (fcIdTextBox) + "'";
					break;
				case 1 :
					fcId = " LIKE '" + TBRK3lient.get (fcIdTextBox) + "%'";
					break;
				case 2 :
					fcId = " LIKE '%" + TBRK3lient.get (fcIdTextBox) + "'";
					break;
				case 3 :
					fcId = " LIKE '%" + TBRK3lient.get (fcIdTextBox) + "%'";
					break;
				case 4 :
					fcId = " NOT LIKE '%" + TBRK3lient.get (fcIdTextBox) + "%'";
					break;
			}
		}
		if (cmIdCheckBox.getValue ())
		{
			switch (cmFilterTypeComboBox.getSelectedIndex ())
			{
				case 0 :
					cmId = " = '" + TBRK3lient.get (cmIdTextBox) + "'";
					break;
				case 1 :
					cmId = " LIKE '" + TBRK3lient.get (cmIdTextBox) + "%'";
					break;
				case 2 :
					cmId = " LIKE '%" + TBRK3lient.get (cmIdTextBox) + "'";
					break;
				case 3 :
					cmId = " LIKE '%" + TBRK3lient.get (cmIdTextBox) + "%'";
					break;
				case 4 :
					cmId = " NOT LIKE '%" + TBRK3lient.get (cmIdTextBox) + "%'";
					break;
			}
		}
		if (patientIdCheckBox.getValue ())
		{
			switch (patientFilterTypeComboBox.getSelectedIndex ())
			{
				case 0 :
					patientId = " = '" + TBRK3lient.get (patientIdTextBox) + "'";
					break;
				case 1 :
					patientId = " LIKE '" + TBRK3lient.get (patientIdTextBox) + "%'";
					break;
				case 2 :
					patientId = " LIKE '%" + TBRK3lient.get (patientIdTextBox) + "'";
					break;
				case 3 :
					patientId = " LIKE '%" + TBRK3lient.get (patientIdTextBox) + "%'";
					break;
				case 4 :
					patientId = " NOT LIKE '%" + TBRK3lient.get (patientIdTextBox) + "%'";
					break;
			}
		}
		if (dateRangeFilterCheckBox.getValue () && !dateColumnName.equals (""))
			filter += " AND " + dateColumnName + " BETWEEN '" + startDate + "' AND '" + endDate + "'";
		if (districtCheckBox.getValue () && !districtColumnName.equals (""))
			filter += " AND " + districtColumnName + district;
		if (facilityCheckBox.getValue () && !facilityColumnName.equals (""))
			filter += " AND " + facilityColumnName + facility;
		if (patientIdCheckBox.getValue () && !patientColumnName.equals (""))
			filter += " AND " + patientColumnName + patientId;
		if (cmIdCheckBox.getValue () && !cmColumnName.equals (""))
			filter += " AND " + cmColumnName + cmId;
		if (fcIdCheckBox.getValue () && !fcColumnName.equals (""))
			filter += " AND " + fcColumnName + fcId;
		return filter;
	}

	public void clearUp ()
	{
		// Not implemented
	}

	public boolean validate ()
	{
		return true;
	}

	public void viewData (final boolean export)
	{
		String reportSelected = TBRK3lient.get (reportsListComboBox).replace (" ", "");
		String query = "";
		// Change the below tbreachkenya if live reports required
		reportsDB = "tbreachkenya_rpt";
		// Case Detection Reports
		if (TBRK3lient.get (categoryComboBox).equals ("Reports"))
		{
			if (reportSelected.equals ("Facility List"))
			{
				query = "select D.location_id as `District ID`, D.location_name as `District`, L.location_id as `Facility ID`, L.location_name as `Facility Name`, L.state as `Province`, L.latitude as `Latitude`, L.longitude as `Longitude` from tbreachkenya_rpt.location as L "
						+ "left outer join tbreachkenya_rpt.location as D on D.location_id = L.address1 "
						+ "where D.location_type = 'DISTRICT' and L.location_type = 'FACILITY' "
						+ filterData ("", "D.location_id", "L.location_id", "", "", "");
			}
			if (reportSelected.equals ("Cough Monitor List"))
			{
				query = "select U.user_name as `User Name`, D.definition_value as `Current Status`, M.location_id as `Facility ID`, L.location_name as `Facility Name` from tbreachkenya_rpt.user as U "
						+ "inner join tbreachkenya_rpt.person_role as R on U.pid = R.pid "
						+ "inner join tbreachkenya_rpt.definition as D on U.current_status = D.definition_key "
						+ "left outer join tbreachkenya_rpt.user_mapping as M on U.pid = M.user_id "
						+ "left outer join tbreachkenya_rpt.location as L on M.location_id = L.location_id "
						+ "where D.definition_type = 'USER_STATUS' and R.role = 'CM' " + filterData ("", "", "M.location_id", "", "", "");
			}
			if (reportSelected.equals ("Field Coordinator List"))
			{
				query = "select U.user_name as `User Name`, D.definition_value as `Current Status`, M.location_id as `Facility ID`, L.location_name as `Facility Name` from tbreachkenya_rpt.user as U "
						+ "inner join tbreachkenya_rpt.person_role as R on U.pid = R.pid "
						+ "inner join tbreachkenya_rpt.definition as D on U.current_status = D.definition_key "
						+ "left outer join tbreachkenya_rpt.user_mapping as M on U.pid = M.user_id "
						+ "left outer join tbreachkenya_rpt.location as L on M.location_id = L.location_id "
						+ "where D.definition_type = 'USER_STATUS' and R.role = 'FC' " + filterData ("", "", "M.location_id", "", "", "");
			}
			else
			{
				query = "";
			}
		}
		else if (TBRK3lient.get (categoryComboBox).equals ("Form Dumps"))
		{
			query = "select * from Enc_" + TBRK3lient.get (reportsListComboBox) + " where 1 = 1" + filterData ("date_entered", "", "location_id", "", "", "pid1");
		}

		load (true);
		if (query.equals (""))
		{
			try
			{
				service.generateReport (reportSelected, new Parameter[] {new Parameter ("UserName", TBR3.getCurrentUserName (), DataType.STRING)}, export, new AsyncCallback<String> ()
				{
					public void onSuccess (String result)
					{
						Window.open (result, "_blank", "");
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
				load (false);
				e.printStackTrace ();
			}
		}
		else
		{
			if (TBRK3lient.get (categoryComboBox).equals ("Form Dumps"))
			{
				try
				{
					service.generateCSVfromQuery (reportsDB, query, new AsyncCallback<String> ()
					{

						public void onSuccess (String result)
						{
							Window.open (result, "_blank", "");
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
					load (false);
					e.printStackTrace ();
				}
			}
			else
			{
				try
				{
					service.generateReportFromQuery (reportsDB, reportSelected, query, export, new AsyncCallback<String> ()
					{

						public void onSuccess (String result)
						{
							Window.open (result, "_blank", "");
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
					load (false);
					e.printStackTrace ();
				}
			}
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
					try
					{
						service.findUser (TBR3.getCurrentUserName (), new AsyncCallback<User> ()
						{

							public void onSuccess (User result)
							{
								rights.setRoleRights (TBR3.getCurrentRole (), userRights);
								viewButton.setEnabled (rights.getAccess (AccessType.PRINT));
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
		if (sender == viewButton)
		{
			viewData (false);
		}
		else if (sender == exportButton)
		{
			viewData (true);
		}
		else if (sender == closeButton)
		{
			MainMenuComposite.clear ();
		}
	}

	public void onChange (ChangeEvent event)
	{
		Widget sender = (Widget) event.getSource ();
		// Fill report names
		if (sender == categoryComboBox)
		{
			String text = TBRK3lient.get (sender);
			if (text.equals ("Reports"))
			{
				reportsListComboBox.clear ();
				reportsListComboBox.addItem ("Ampath Sites Report");
				reportsListComboBox.addItem ("Comparison Review Report");
				reportsListComboBox.addItem ("Cough Monitor List");
				reportsListComboBox.addItem ("Facility List");
				reportsListComboBox.addItem ("Field Coordinator List");
				reportsListComboBox.addItem ("Lab Review Report");
				reportsListComboBox.addItem ("Screening Report");
				reportsListComboBox.addItem ("Sputum Collection Report");
				reportsListComboBox.addItem ("Treatment Report");
				reportsListComboBox.addItem ("Treatment Review Report");
			}
			else if (text.equals ("Form Dumps"))
			{
				load (true);
				reportsListComboBox.clear ();
				try
				{
					service.getColumnData ("encounter_type", "encounter_type", "", new AsyncCallback<String[]> ()
					{
						public void onSuccess (String[] result)
						{
							for (String s : result)
								reportsListComboBox.addItem (s);
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
		}
		if (sender == reportsListComboBox)
		{
			viewButton.setEnabled (!TBRK3lient.get (reportsListComboBox).equals ("Form Dumps"));
		}
	}

	public void onValueChange (ValueChangeEvent<Boolean> event)
	{
		Widget sender = (Widget) event.getSource ();
		if (sender == dateRangeFilterCheckBox)
		{
			fromDateBox.setEnabled (dateRangeFilterCheckBox.getValue ());
			toDateBox.setEnabled (dateRangeFilterCheckBox.getValue ());
		}
		else if (sender == timeRangeFilterCheckBox)
		{
			fromTimeDateBox.setEnabled (timeRangeFilterCheckBox.getValue ());
			toTimeDateBox.setEnabled (timeRangeFilterCheckBox.getValue ());
		}
		else if (sender == patientIdCheckBox)
		{
			patientFilterTypeComboBox.setEnabled (patientIdCheckBox.getValue ());
			patientIdTextBox.setEnabled (patientIdCheckBox.getValue ());
		}
		else if (sender == districtCheckBox)
		{
			districtComboBox.setEnabled (districtCheckBox.getValue ());
		}
		else if (sender == facilityCheckBox)
		{
			facilityComboBox.setEnabled (facilityCheckBox.getValue ());
		}
		else if (sender == cmIdCheckBox)
		{
			cmFilterTypeComboBox.setEnabled (cmIdCheckBox.getValue ());
			cmIdTextBox.setEnabled (cmIdCheckBox.getValue ());
		}
		else if (sender == fcIdCheckBox)
		{
			fcFilterTypeComboBox.setEnabled (fcIdCheckBox.getValue ());
			fcIdTextBox.setEnabled (fcIdCheckBox.getValue ());
		}
	}
}
