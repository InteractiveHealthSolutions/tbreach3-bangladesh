/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Main Menu Composite for TB CONTROL client
 */

package org.irdresearch.tbr3web.client;

import java.util.Date;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MainMenuComposite extends Composite
{
	private static VerticalPanel	mainVerticalPanel;

	private MenuBar					mainMenuBar						= new MenuBar (false);
	private MenuBar					setupMenuBar					= new MenuBar (true);
	private MenuBar					formsMenuBar					= new MenuBar (true);
	private MenuBar					reportingMenuBar				= new MenuBar (true);
	private MenuBar					helpMenuBar						= new MenuBar (true);

	private MenuItem				setupMenuItem					= new MenuItem ("Setup", false, setupMenuBar);
	private MenuItem				formsMenuItem					= new MenuItem ("Forms", false, formsMenuBar);
	private MenuItem				reportingMenuItem				= new MenuItem ("Reporting", false, reportingMenuBar);
	private MenuItem				helpMenuItem					= new MenuItem ("Help", false, helpMenuBar);

	private MenuItem				dashboardMenuItem				= new MenuItem ("Dashboard", false, (Command) null);
	private MenuItem				reportsMenuItem					= new MenuItem ("Reports", false, (Command) null);
	private MenuItem				patientSearchMenuItem			= new MenuItem ("Patient Search", false, (Command) null);
	private MenuItem				logsMenuItem					= new MenuItem ("Logs", false, (Command) null);

	private MenuItem				aboutMenuItem					= new MenuItem ("About Us", false, (Command) null);
	private MenuItem				aboutMeMenuItem					= new MenuItem ("About Me", false, (Command) null);
	private MenuItem				helpContentsMenuItem			= new MenuItem ("Help Contents", false, (Command) null);
	private MenuItem				feedbackMenuItem				= new MenuItem ("Feedback", false, (Command) null);
	private MenuItem				logoutMenuItem					= new MenuItem ("Logout", false, (Command) null);

	@SuppressWarnings("deprecation")
	public MainMenuComposite ()
	{
		VerticalPanel topVerticalPanel = new VerticalPanel ();
		initWidget (topVerticalPanel);
		mainVerticalPanel = new VerticalPanel ();
		mainVerticalPanel.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_CENTER);
		mainVerticalPanel.setSize ("100%", "100%");
		topVerticalPanel.setHorizontalAlignment (HasHorizontalAlignment.ALIGN_CENTER);
		topVerticalPanel.add (mainMenuBar);
		topVerticalPanel.setSize ("400px", "100%");
		topVerticalPanel.add (mainVerticalPanel);
		mainMenuBar.setSize ("100%", "100%");
		mainMenuBar.setAutoOpen (true);
		mainMenuBar.setAnimationEnabled (true);
		setupMenuBar.setAutoOpen (true);
		setupMenuBar.setAnimationEnabled (true);
		formsMenuBar.setAutoOpen (true);
		formsMenuBar.setAnimationEnabled (true);
		reportingMenuBar.setAutoOpen (true);
		reportingMenuBar.setAnimationEnabled (true);
		helpMenuBar.setAutoOpen (true);
		helpMenuBar.setAnimationEnabled (true);

		patientSearchMenuItem.setCommand (new Command ()
		{

			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "DATALOG");
				mainVerticalPanel.add (new Report_PatientComposite ().asWidget ());
			}
		});
		dashboardMenuItem.setCommand (new Command ()
		{

			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "DATALOG");
				mainVerticalPanel.add (new Report_DashboardComposite ().asWidget ());
			}
		});
		reportsMenuItem.setCommand (new Command ()
		{

			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "DATALOG");
				mainVerticalPanel.add (new Report_ReportsComposite ().asWidget ());
			}
		});
		logsMenuItem.setCommand (new Command ()
		{

			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "DATALOG");
				mainVerticalPanel.add (new Report_LogComposite ().asWidget ());
			}
		});
		aboutMeMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				try
				{
					String user = Cookies.getCookie ("UserName");
					Date loginDate = new Date (Long.parseLong (Cookies.getCookie ("LoginTime")));
					int mins = new Date (new Date ().getTime () - loginDate.getTime ()).getMinutes ();
					String str = "CURRENT USER: " + user + "\n" + "LOGIN TIME: " + loginDate.toGMTString ().replace ("GMT", "") + "\n" + "CURRENT SESSION: " + mins + " mins";
					Window.alert (str);
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
		});
		aboutMenuItem.setCommand (new Command ()
		{

			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "About Us");
				mainVerticalPanel.add (new AboutUsComposite ().asWidget ());
			}
		});
		feedbackMenuItem.setCommand (new Command ()
		{

			public void execute ()
			{
				clear ();
				Cookies.setCookie ("CurrentMenu", "FEEDBACK");
				// mainVerticalPanel.add (new FeedbackComposite ().asWidget ());
			}
		});
		logoutMenuItem.setCommand (new Command ()
		{
			public void execute ()
			{
				TBR3Web.logout ();
			}
		});

		mainMenuBar.addItem (setupMenuItem);
		mainMenuBar.addItem (formsMenuItem);
		mainMenuBar.addItem (reportingMenuItem);
		mainMenuBar.addItem (helpMenuItem);
		mainMenuBar.addItem (logoutMenuItem);

		reportingMenuBar.addItem (dashboardMenuItem);
		reportingMenuBar.addItem (reportsMenuItem);
		reportingMenuBar.addItem (patientSearchMenuItem);
		reportingMenuBar.addItem (logsMenuItem);

		helpMenuBar.addItem (aboutMeMenuItem);
		helpMenuBar.addItem (aboutMenuItem);
		helpMenuBar.addItem (helpContentsMenuItem);
	}

	public static void clear ()
	{
		Cookies.setCookie ("CurrentMenu", "");
		mainVerticalPanel.clear ();
	}
}
