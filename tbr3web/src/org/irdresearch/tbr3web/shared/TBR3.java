/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * This class contains constants and project-specific methods which will be used throughout the System
 */

package org.irdresearch.tbr3web.shared;

import java.util.ArrayList;
import org.irdresearch.tbr3web.shared.model.Defaults;
import org.irdresearch.tbr3web.shared.model.Definition;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public final class TBR3
{
	private static String		resourcesPath;
	private static String		hashingAlgorithm;
	private static String		projectTitle			= "TB REACH Kenya";
	private static String		databaseName			= "tbreachkenya";
	private static String		reportingDatabase		= "tbreachkenya_rpt";
	private static String		reportsDirectoryName	= "rpt";
	private static int			sessionLimit			= 900000;

	private static String		currentVersion			= "";
	private static String		currentUserName			= "";
	private static String		currentRole				= "";
	private static String		passCode				= "";
	private static String[][]	schema;
	private static Definition[]	definitions;
	private static Defaults[]	defaults;

	public static String getHashingAlgorithm ()
	{
		return hashingAlgorithm;
	}

	public static void setHashingAlgorithm (String hashingAlgorithm)
	{
		TBR3.hashingAlgorithm = hashingAlgorithm;
	}

	public static String getProjectTitle ()
	{
		return projectTitle;
	}

	public static void setProjectTitle (String projectTitle)
	{
		TBR3.projectTitle = projectTitle;
	}

	public static String getDatabaseName ()
	{
		return databaseName;
	}

	public static void setDatabaseName (String databaseName)
	{
		TBR3.databaseName = databaseName;
	}

	public static String getReportingDatabase ()
	{
		return reportingDatabase;
	}

	public static void setReportingDatabase (String reportingDatabase)
	{
		TBR3.reportingDatabase = reportingDatabase;
	}

	public static String getReportsDirectoryName ()
	{
		return reportsDirectoryName;
	}

	public static void setReportsDirectoryName (String reportsDirectoryName)
	{
		TBR3.reportsDirectoryName = reportsDirectoryName;
	}

	public static int getSessionLimit ()
	{
		return sessionLimit;
	}

	public static void setSessionLimit (int sessionLimit)
	{
		TBR3.sessionLimit = sessionLimit;
	}

	public static String[][] getSchema ()
	{
		return schema;
	}

	public static void setSchema (String[][] schema)
	{
		TBR3.schema = schema;
	}

	public static Definition[] getDefinitions ()
	{
		return definitions;
	}

	public static void setDefinitions (Definition[] definitions)
	{
		TBR3.definitions = definitions;
	}

	public static Defaults[] getDefaults ()
	{
		return defaults;
	}

	public static void setDefaults (Defaults[] defaults)
	{
		TBR3.defaults = defaults;
	}

	public static void setResourcesPath (String resourcesPath)
	{
		TBR3.resourcesPath = resourcesPath;
	}

	public static void fillSchema (String[][] schema)
	{
		TBR3.schema = schema;
	}

	public static void fillDefinitions (Definition[] definitions)
	{
		TBR3.definitions = definitions;
	}

	public static void fillDefaults (Defaults[] defaults)
	{
		TBR3.defaults = defaults;
	}

	/**
	 * Get maximum length of a column in a table
	 * 
	 * @param tablename
	 * @param columnName
	 * @return
	 */
	public static int getMaxLength (String tablename, String columnName)
	{
		try
		{
			for (int i = 0; i < schema.length; i++)
			{
				if (schema[i][0].equals (tablename) && schema[i][1].equals (columnName))
					return Integer.parseInt (schema[i][4]);
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace ();
			return 0;
		}
		return 255;
	}

	/**
	 * Concatenate an Array of Strings into single String
	 * 
	 * @param array
	 * @return string
	 */
	public static String concatenateArray (String[] array)
	{
		StringBuilder concatenated = new StringBuilder ();
		for (String s : array)
			concatenated.append (s + ",");
		// Remove additional comma
		concatenated.deleteCharAt (concatenated.length () - 1);
		return concatenated.toString ();
	}

	/**
	 * Get a list of unique definition types
	 * 
	 * @return array
	 */
	public static String[] getDefinitionTypes ()
	{
		ArrayList<String> types = new ArrayList<String> ();
		for (Definition d : definitions)
		{
			boolean exists = false;
			for (int i = 0; i < types.size (); i++)
				if (types.get (i).equals (d.getId ().getDefinitionType ()))
					exists = true;
			if (!exists)
				types.add (d.getId ().getDefinitionType ());
		}
		return types.toArray (new String[] {});
	}

	/**
	 * Get a list of constant values
	 * 
	 * @param definitionType
	 * @return array
	 */
	public static String[] getDefinitionValues (String definitionType)
	{
		ArrayList<String> list = new ArrayList<String> ();
		for (Definition d : definitions)
		{
			if (d.getId ().getDefinitionType ().equals (definitionType))
				list.add (d.getDefinitionValue ());
		}
		if (list.size () == 0)
			list.add ("NO " + definitionType);
		return list.toArray (new String[] {});
	}

	/**
	 * Get definition key for a definition type and fully-qualified value
	 * 
	 * @param definitionType
	 * @param definitionValue
	 * @return
	 */
	public static String getDefinitionKey (String definitionType, String definitionValue)
	{
		for (Definition d : definitions)
		{
			if (d.getId ().getDefinitionType ().equals (definitionType) && d.getDefinitionValue ().equals (definitionValue))
				return d.getId ().getDefinitionKey ();
		}
		return "";
	}

	/**
	 * Get definition value for a definition type and key
	 * 
	 * @param definitionType
	 * @param definitionKey
	 * @return
	 */
	public static String getDefinitionValue (String definitionType, String definitionKey)
	{
		for (Definition d : definitions)
		{
			if (d.getId ().getDefinitionType ().equals (definitionType) && d.getId ().getDefinitionKey ().equals (definitionKey))
				return d.getDefinitionValue ();
		}
		return null;
	}

	/**
	 * Get default value for a definition type
	 * 
	 * @param definitionType
	 * @return
	 */
	public static String getDefaultValue (String definitionType)
	{
		for (Defaults d : defaults)
		{
			if (d.getId ().getDefinitionType ().equals (definitionType))
				return d.getId ().getDefaultDefinitionKey ();
		}
		return "";
	}

	public static String getCurrentVersion ()
	{
		return currentVersion;
	}

	public static void setCurrentVersion (String version)
	{
		TBR3.currentVersion = version;
	}

	/**
	 * Get current User Name (saved in cookies on client-side)
	 * 
	 * @return currentUser
	 */
	public static String getCurrentUserName ()
	{
		return currentUserName;
	}

	/**
	 * Set current user
	 * 
	 * @param userName
	 */
	public static void setCurrentUserName (String userName)
	{
		TBR3.currentUserName = userName;
	}

	public static String getCurrentRole ()
	{
		return currentRole;
	}

	public static void setCurrentRole (String userRole)
	{
		TBR3.currentRole = userRole;
	}

	/**
	 * Get pass code (first 4 characters of User's password)
	 * 
	 * @return passCode
	 */
	public static String getPassCode ()
	{
		return passCode;
	}

	/**
	 * Set pass code for current user
	 * 
	 * @param passcode
	 */
	public static void setPassCode (String passcode)
	{
		TBR3.passCode = passcode;
	}

	/**
	 * @return the reportPath
	 */
	public static String getReportPath ()
	{
		String path = getResourcesPath ();
		char separatorChar = (path.charAt (path.length () - 1));
		return getResourcesPath () + reportsDirectoryName + separatorChar;
	}

	/**
	 * @return the resourcesPath
	 */
	public static String getResourcesPath ()
	{
		return resourcesPath;
	}
}
