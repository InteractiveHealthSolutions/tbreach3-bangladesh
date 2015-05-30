/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * This class provides different rights and access that a User has in the System
 */

package org.irdresearch.tbr3web.shared;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class UserRightsUtil
{
	private boolean	deleteAccess;
	private boolean	insertAccess;
	private boolean	printAccess;
	private boolean	searchAccess;
	private boolean	updateAccess;

	public UserRightsUtil ()
	{
		searchAccess = insertAccess = updateAccess = deleteAccess = printAccess = false;
	}

	public UserRightsUtil (boolean searchAccess, boolean insertAccess, boolean updateAccess, boolean deleteAccess,
			boolean printAccess)
	{
		this.searchAccess = searchAccess;
		this.insertAccess = insertAccess;
		this.updateAccess = updateAccess;
		this.deleteAccess = deleteAccess;
		this.printAccess = printAccess;
	}

	/**
	 * 
	 * @param userRole
	 * @param rights
	 */
	public void setRoleRights (String userRole, Boolean[] rights)
	{
		try
		{
			searchAccess = rights[0];
			insertAccess = rights[1];
			updateAccess = rights[2];
			deleteAccess = rights[3];
			printAccess = rights[4];
		}
		catch (Exception e)
		{
			searchAccess = insertAccess = updateAccess = deleteAccess = printAccess = false;
		}
	}

	public boolean getAccess (AccessType accessType)
	{
		boolean access = false;
		switch (accessType)
		{
			case SELECT :
				return searchAccess;
			case INSERT :
				return insertAccess;
			case UPDATE :
				return updateAccess;
			case DELETE :
				return deleteAccess;
			case PRINT :
				return printAccess;
		}
		return access;
	}
}
