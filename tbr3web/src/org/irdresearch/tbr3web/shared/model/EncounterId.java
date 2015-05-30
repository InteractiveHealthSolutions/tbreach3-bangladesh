/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.tbr3web.shared.model;

// Generated Jun 12, 2012 4:08:49 PM by Hibernate Tools 3.4.0.CR1

/**
 * EncounterId generated by hbm2java
 */
public class EncounterId implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6042306676698962634L;
	private int					EId;
	private String				pid1;
	private String				pid2;
	private String				encounterType;

	public EncounterId ()
	{
	}

	public EncounterId (int EId, String pid1, String pid2, String encounterType)
	{
		this.EId = EId;
		this.pid1 = pid1;
		this.pid2 = pid2;
		this.encounterType = encounterType;
	}

	public int getEId ()
	{
		return this.EId;
	}

	public void setEId (int EId)
	{
		this.EId = EId;
	}

	public String getPid1 ()
	{
		return this.pid1;
	}

	public void setPid1 (String pid1)
	{
		this.pid1 = pid1;
	}

	public String getPid2 ()
	{
		return this.pid2;
	}

	public void setPid2 (String pid2)
	{
		this.pid2 = pid2;
	}

	public String getEncounterType ()
	{
		return this.encounterType;
	}

	public void setEncounterType (String encounterType)
	{
		this.encounterType = encounterType;
	}

	@Override
	public int hashCode ()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + EId;
		result = prime * result + ((encounterType == null) ? 0 : encounterType.hashCode ());
		result = prime * result + ((pid1 == null) ? 0 : pid1.hashCode ());
		result = prime * result + ((pid2 == null) ? 0 : pid2.hashCode ());
		return result;
	}

	@Override
	public boolean equals (Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass () != obj.getClass ())
			return false;
		EncounterId other = (EncounterId) obj;
		if (EId != other.EId)
			return false;
		if (encounterType == null)
		{
			if (other.encounterType != null)
				return false;
		}
		else if (!encounterType.equals (other.encounterType))
			return false;
		if (pid1 == null)
		{
			if (other.pid1 != null)
				return false;
		}
		else if (!pid1.equals (other.pid1))
			return false;
		if (pid2 == null)
		{
			if (other.pid2 != null)
				return false;
		}
		else if (!pid2.equals (other.pid2))
			return false;
		return true;
	}

	@Override
	public String toString ()
	{
		return EId + ", " + pid1 + ", " + pid2 + ", " + encounterType;
	}

}