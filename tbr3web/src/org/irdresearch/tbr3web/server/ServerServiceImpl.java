/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.tbr3web.server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import org.irdresearch.tbr3web.client.ServerService;
import org.irdresearch.tbr3web.server.util.DateTimeUtil;
import org.irdresearch.tbr3web.server.util.HibernateUtil;
import org.irdresearch.tbr3web.server.util.ReportUtil;
import org.irdresearch.tbr3web.shared.Parameter;
import org.irdresearch.tbr3web.shared.TBR3;
import org.irdresearch.tbr3web.shared.model.Defaults;
import org.irdresearch.tbr3web.shared.model.Definition;
import org.irdresearch.tbr3web.shared.model.Sms;
import org.irdresearch.tbr3web.shared.model.User;
import org.irdresearch.tbr3web.shared.model.UserRights;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 * 
 * @author owais.hussain@irdresearch.org
 */
@SuppressWarnings("serial")
public class ServerServiceImpl extends RemoteServiceServlet implements ServerService
{
	private static String	applicationPath		= "";
	private static String	propertiesFilePath	= "";

	public ServerServiceImpl ()
	{
		String currentDirectory = System.getProperty ("user.dir");
		System.out.println ("Current directory:" + currentDirectory);
		if (currentDirectory.startsWith ("/"))
			applicationPath = "/var/lib/tomcat6/webapps/tbr3web/";
		else
			applicationPath = "C:\\workspace\\TBR3\\tbr3web\\war\\";
		propertiesFilePath = applicationPath + "tbr3web.properties";
		setProperties ();
	}

	private String arrangeFilter (String filter)
	{
		if (filter.trim ().equalsIgnoreCase (""))
			return "";
		return (filter.toUpperCase ().contains ("WHERE") ? "" : " where ") + filter;
	}

	/**
	 * Sends a generic SMS
	 * 
	 * @param sms
	 */
	public void sendGenericSMSAlert (Sms[] sms)
	{
		for (Sms s : sms)
			sendGenericSMSAlert (s);
	}

	/**
	 * Sends a generic SMS
	 * 
	 * @param sms
	 */
	public void sendGenericSMSAlert (Sms sms)
	{
		if (!sms.getTargetNumber ().equals (""))
			HibernateUtil.util.save (sms);
	}

	/**
	 * Checks if a user exists in the database
	 * 
	 * @return Boolean
	 */
	public Boolean authenticateUser (String userName)
	{
		if (!UserAuthentication.userExsists (userName))
			return false;
		return true;
	}

	/**
	 * Verifies secret answer against stored secret question
	 * 
	 * @return Boolean
	 */
	public Boolean verifySecretAnswer (String userName, String secretAnswer)
	{
		if (!UserAuthentication.validateSecretAnswer (userName, secretAnswer))
			return false;
		return true;
	}

	/**
	 * Get number of records in a table, given appropriate filter
	 * 
	 * @return Long
	 */
	public Long count (String tableName, String filter)
	{
		Object obj = HibernateUtil.util.selectObject ("select count(*) from " + tableName + " " + arrangeFilter (filter));
		return Long.parseLong (obj.toString ());
	}

	/**
	 * Checks existence of data by counting number of records in a table, given
	 * appropriate filter
	 * 
	 * @return Boolean
	 */
	public Boolean exists (String tableName, String filter)
	{
		long count = count (tableName, filter);
		return count > 0;
	}

	/**
	 * Generates CSV file from query passed along with the filters
	 * 
	 * @param query
	 * @return
	 */
	public String generateCSVfromQuery (String database, String query)
	{
		return ReportUtil.generateCSVfromQuery (database, query, ',');
	}

	/**
	 * Generate report on server side and return the path it was created to
	 * 
	 * @param Path
	 *            of report as String Report parameters as Parameter[] Report to
	 *            be exported in csv format as Boolean
	 * @return String
	 */
	public String generateReport (String fileName, Parameter[] params, boolean export)
	{
		return ReportUtil.generateReport (fileName, params, export);
	}

	/**
	 * Generate report on server side based on the query saved in the Database
	 * against the reportName and return the path it was created to
	 * 
	 * @param reportName
	 * @param params
	 * @param export
	 * @return
	 */
	public String generateReportFromQuery (String database, String reportName, String query, Boolean export)
	{
		return ReportUtil.generateReportFromQuery (database, reportName, query, export);
	}

	public String[] getColumnData (String tableName, String columnName, String filter)
	{
		Object[] data = HibernateUtil.util.selectObjects ("select distinct cast(" + columnName + " as char) from " + tableName + " " + arrangeFilter (filter));
		String[] columnData = new String[data.length];
		for (int i = 0; i < data.length; i++)
			columnData[i] = data[i].toString ();
		return columnData;
	}

	/**
	 * Sets current user name, this is due to a strange GWT bug/feature that
	 * shared variables, set from Client-side appear to be empty on Server-side
	 * code
	 * 
	 * @return
	 */
	public void setCurrentUser (String userName, String role)
	{
		TBR3.setCurrentUserName (userName);
		TBR3.setCurrentRole (role);
	}

	public String getCurrentUserName ()
	{
		return TBR3.getCurrentUserName ();
	}

	/**
	 * Initializes properties by reading from properties file
	 * 
	 * @return true
	 */
	private Boolean setProperties ()
	{
		ArrayList<String> text = new ArrayList<String> ();
		try
		{
			FileInputStream fis = new FileInputStream (propertiesFilePath);
			DataInputStream dis = new DataInputStream (fis);
			BufferedReader br = new BufferedReader (new InputStreamReader (dis));
			String strLine;
			while ((strLine = br.readLine ()) != null)
				text.add (strLine);
			dis.close ();

			/* Initially set defaults */
			TBR3.setProjectTitle ("TB REACH 3");
			TBR3.setDatabaseName ("openmrs_rpt");
			TBR3.setReportingDatabase (TBR3.getDatabaseName ());
			TBR3.setSessionLimit (15 * 60 * 1000);
			TBR3.setHashingAlgorithm ("SHA");
			TBR3.setCurrentVersion ("1.0.0");

			for (String s : text)
			{
				if (s.startsWith ("#"))
					continue;
				String[] parts = s.split ("=");
				if (parts.length < 2)
					continue;
				if (parts[0].equals ("resources_path"))
					TBR3.setResourcesPath (applicationPath);
				else if (parts[0].equals ("current_version"))
					TBR3.setCurrentVersion (parts[1]);
				else if (parts[0].equals ("project_title"))
					TBR3.setProjectTitle (parts[1]);
				else if (parts[0].equals ("database_name"))
					TBR3.setDatabaseName (parts[1]);
				else if (parts[0].equals ("data_warehouse_name"))
					TBR3.setReportingDatabase (parts[1]);
				else if (parts[0].equals ("reports_directory_name"))
					TBR3.setReportsDirectoryName (parts[1]);
				else if (parts[0].equals ("session_limit"))
					TBR3.setSessionLimit (Integer.parseInt (parts[1]) * 1000);
				else if (parts[0].equals ("hashing_algorithm"))
					TBR3.setHashingAlgorithm (parts[1]);
			}
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace ();
			System.out.print ("Unable to read properties file. Make sure that <" + propertiesFilePath + "> exists in the Application root directory and is accessible.");
			System.exit (-1);
			return false;
		}
	}

	public String[][] getSchema ()
	{
		Object[][] data = HibernateUtil.util.selectData ("select TABLE_NAME, COLUMN_NAME, IS_NULLABLE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH from information_schema.columns where TABLE_SCHEMA = '"
				+ TBR3.getDatabaseName () + "'");
		String[][] schema = new String[data.length][];
		for (int i = 0; i < data.length; i++)
		{
			schema[i] = new String[data[i].length];
			for (int j = 0; j < data[i].length; j++)
			{
				if (data[i][j] == null)
					data[i][j] = 0;
				schema[i][j] = data[i][j].toString ();
			}
		}
		return schema;
	}

	/**
	 * Get default values to be used on front-ends
	 */
	public Defaults[] getDefaults ()
	{
		Object[] objs = HibernateUtil.util.findObjects ("from Defaults");
		Defaults[] defaults = new Defaults[objs.length];
		for (int i = 0; i < objs.length; i++)
		{
			Defaults def = (Defaults) objs[i];
			defaults[i] = def;
		}
		return defaults;
	}

	/**
	 * Get all definitions for static data
	 */
	public Definition[] getDefinitions ()
	{
		Object[] objs = HibernateUtil.util.findObjects ("from Definition");
		Definition[] definitions = new Definition[objs.length];
		for (int i = 0; i < objs.length; i++)
		{
			Definition def = (Definition) objs[i];
			definitions[i] = def;
		}
		return definitions;
	}

	public String getObject (String tableName, String columnName, String filter)
	{
		return HibernateUtil.util.selectObject ("select " + columnName + " from " + tableName + arrangeFilter (filter)).toString ();
	}

	public String[] getQueriesResults (String[] queries)
	{
		String[] results = new String[queries.length];
		for (int i = 0; i < results.length; i++)
		{
			try
			{
				results[i] = HibernateUtil.util.selectObject (queries[i]).toString ();
			}
			catch (Exception e)
			{
				results[i] = "";
				e.printStackTrace ();
			}
		}
		return results;
	}

	public String[] getDumpFiles ()
	{
		ArrayList<String> files = new ArrayList<String> ();
		File folder = new File (TBR3.getResourcesPath ());
		for (File f : folder.listFiles ())
		{
			if (f.isFile ())
			{
				String file = f.getPath ();
				if (file.endsWith (".zip") || file.endsWith (".ZIP"))
					files.add (file);
			}
		}
		Collections.sort (files);
		Collections.reverse (files);
		return files.toArray (new String[] {});
	}

	public String[][] getReportsList ()
	{
		return ReportUtil.getReportList ();
	}

	public String[] getRowRecord (String tableName, String[] columnNames, String filter)
	{
		return getTableData (tableName, columnNames, filter)[0];
	}

	public String getSecretQuestion (String userName)
	{
		User user = (User) HibernateUtil.util.findObject ("from User where userName='" + userName + "'");
		return user.getSecretQuestion ();
	}

	@SuppressWarnings("deprecation")
	public String getSnapshotTime ()
	{
		Date dt = new Date ();
		Object obj = HibernateUtil.util.selectObject ("select max(date_end) from encounter where date(date_end) < '" + (dt.getYear () + 1900) + "-" + (dt.getMonth () + 1) + "-" + dt.getDate () + "'");
		return obj.toString ();
	}

	public String[][] getTableData (String tableName, String[] columnNames, String filter)
	{
		StringBuilder columnList = new StringBuilder ();
		for (String s : columnNames)
		{
			columnList.append (s);
			columnList.append (",");
		}
		columnList.deleteCharAt (columnList.length () - 1);
		String query = "select " + columnList.toString () + " from " + tableName + " " + arrangeFilter (filter);
		return getTableData (query);
	}

	public String[][] getTableData (String sqlQuery)
	{
		Object[][] data = HibernateUtil.util.selectData (sqlQuery);
		String[][] stringData = new String[data.length][];
		for (int i = 0; i < data.length; i++)
		{
			stringData[i] = new String[data[i].length];
			for (int j = 0; j < stringData[i].length; j++)
			{
				if (data[i][j] == null)
					data[i][j] = "";
				String str = data[i][j].toString ();
				stringData[i][j] = str;
			}
		}
		return stringData;
	}

	public Boolean[] getUserRgihts (String userName, String role, String menuName)
	{
		if (role.equalsIgnoreCase ("ADMIN"))
		{
			Boolean[] rights = {true, true, true, true, true};
			return rights;
		}
		UserRights userRights = (UserRights) HibernateUtil.util.findObject ("from UserRights where id.userRole='" + role + "' and id.menuName='" + menuName + "'");
		Boolean[] rights = {userRights.isSearchAccess (), userRights.isInsertAccess (), userRights.isUpdateAccess (), userRights.isDeleteAccess (), userRights.isPrintAccess ()};
		return rights;
	}

	public void recordLogin (String userName)
	{
		User user = (User) HibernateUtil.util.findObject ("from User where userName='" + userName + "'");
		HibernateUtil.util.recordLog (LogType.LOGIN, user);
		user.setLoggedIn (true);
		HibernateUtil.util.update (user);
	}

	/**
	 * Update User's Login Status to false and save logout date to last login
	 * record
	 */
	public void recordLogout (String userName)
	{
		User user = (User) HibernateUtil.util.findObject ("from User where userName='" + userName + "'");
		String selectQuery = "select max(login_no) from log_login where user_name='" + userName + "'";
		int num = Integer.parseInt (HibernateUtil.util.selectObject (selectQuery).toString ());
		String updateQuery = "update log_login set date_logout = '" + DateTimeUtil.getSQLDate (new Date ()) + "' where login_no = " + num + "";
		HibernateUtil.util.runCommand (updateQuery);
		user.setLoggedIn (false);
		HibernateUtil.util.update (user);
	}

	public int execute (String query)
	{
		return HibernateUtil.util.runCommand (query);
	}

	public Boolean execute (String[] queries)
	{
		for (String s : queries)
		{
			boolean result = execute (s) >= 0;
			if (!result)
				return false;
		}
		return true;
	}

	public Boolean executeProcedure (String procedure)
	{
		return HibernateUtil.util.runProcedure (procedure);
	}

	public User authenticate (String userName, String password, String role)
	{
		return null;
	}

	public User findUser (String userName)
	{
		return null;
	}
}
