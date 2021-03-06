/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.tbr3web.shared.model;

// Generated Jun 13, 2012 3:47:17 PM by Hibernate Tools 3.4.0.CR1

/**
 * UserRights generated by hbm2java
 */
public class UserRights implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4436239869017469059L;
	private UserRightsId	id;
	private boolean			searchAccess;
	private boolean			insertAccess;
	private boolean			updateAccess;
	private boolean			deleteAccess;
	private boolean			printAccess;

	public UserRights ()
	{
	}

	public UserRights (UserRightsId id, boolean searchAccess, boolean insertAccess, boolean updateAccess, boolean deleteAccess, boolean printAccess)
	{
		this.id = id;
		this.searchAccess = searchAccess;
		this.insertAccess = insertAccess;
		this.updateAccess = updateAccess;
		this.deleteAccess = deleteAccess;
		this.printAccess = printAccess;
	}

	public UserRightsId getId ()
	{
		return this.id;
	}

	public void setId (UserRightsId id)
	{
		this.id = id;
	}

	public boolean isSearchAccess ()
	{
		return this.searchAccess;
	}

	public void setSearchAccess (boolean searchAccess)
	{
		this.searchAccess = searchAccess;
	}

	public boolean isInsertAccess ()
	{
		return this.insertAccess;
	}

	public void setInsertAccess (boolean insertAccess)
	{
		this.insertAccess = insertAccess;
	}

	public boolean isUpdateAccess ()
	{
		return this.updateAccess;
	}

	public void setUpdateAccess (boolean updateAccess)
	{
		this.updateAccess = updateAccess;
	}

	public boolean isDeleteAccess ()
	{
		return this.deleteAccess;
	}

	public void setDeleteAccess (boolean deleteAccess)
	{
		this.deleteAccess = deleteAccess;
	}

	public boolean isPrintAccess ()
	{
		return this.printAccess;
	}

	public void setPrintAccess (boolean printAccess)
	{
		this.printAccess = printAccess;
	}

	@Override
	public String toString ()
	{
		return id + ", " + searchAccess + ", " + insertAccess + ", " + updateAccess + ", " + deleteAccess + ", " + printAccess;
	}

}
