/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * Provides Authentication funcionality
 */

package org.irdresearch.tbr3web.server;

import org.irdresearch.tbr3web.server.util.HibernateUtil;
import org.irdresearch.tbr3web.server.util.MDHashUtil;
import org.irdresearch.tbr3web.shared.model.User;
import org.irdresearch.tbr3web.shared.model.UserRights;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class UserAuthentication
{
	public UserAuthentication ()
	{
		// Not implemented
	}

	/**
	 * Check if the user of the given name exists in the database
	 * 
	 * @param userName
	 * @return
	 */
	public static boolean userExsists (String userName)
	{
		try
		{
			boolean exists = HibernateUtil.util.count ("select count(*) from User where user_name = '" + userName.toUpperCase () + "'") > 0;
			return exists;
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		return false;
	}

	/**
	 * Checks if the user exists with given password in the database
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	public static boolean validatePassword (String userName, String password)
	{
		try
		{
			String user = HibernateUtil.util.selectObject ("select password from user where user_name = '" + userName.toUpperCase () + "' and current_status='A'").toString ();
			if (MDHashUtil.match (password, user))
				return true;
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		return false;
	}

	/**
	 * Confirm the secret answer for given user in the database
	 * 
	 * @param userName
	 * @param secretAnswer
	 * @return
	 */
	public static boolean validateSecretAnswer (String userName, String secretAnswer)
	{
		try
		{
			User user = (User) HibernateUtil.util.findObject ("from User where userName = '" + userName.toUpperCase () + "'");
			String ansString = user.getSecretAnswer ().toString ();
			if (MDHashUtil.match (secretAnswer, ansString))
				return true;
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		return false;
	}

	/**
	 * Get access rights on a table for given user role
	 * 
	 * @param userRole
	 * @param tableName
	 * @return
	 */
	public static UserRights getRights (String userRole, String tableName)
	{
		return (UserRights) HibernateUtil.util.findObject ("from UserRights where userRole='" + userRole + "' and MenuName='" + tableName + "'");
	}
}
