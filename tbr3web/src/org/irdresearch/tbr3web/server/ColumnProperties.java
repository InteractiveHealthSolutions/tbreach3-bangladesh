/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */
/**
 * 
 */

package org.irdresearch.tbr3web.server;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public final class ColumnProperties
{
	String	columnName;
	String	dataType;
	int		length;
	int		scale;
	int		precision;

	/**
	 * 
	 */
	public ColumnProperties ()
	{
		// Not implemented
	}

	/**
	 * @param columnName
	 * @param dataType
	 * @param length
	 * @param scale
	 * @param precision
	 */
	public ColumnProperties (String columnName, String dataType, int length, int scale, int precision)
	{
		this.columnName = columnName;
		this.dataType = dataType;
		this.length = length;
		this.scale = scale;
		this.precision = precision;
	}

	/**
	 * @return the columnName
	 */
	public String getColumnName ()
	{
		return columnName;
	}

	/**
	 * @param columnName
	 *            the columnName to set
	 */
	public void setColumnName (String columnName)
	{
		this.columnName = columnName;
	}

	/**
	 * @return the dataType
	 */
	public String getDataType ()
	{
		return dataType;
	}

	/**
	 * @param dataType
	 *            the dataType to set
	 */
	public void setDataType (String dataType)
	{
		this.dataType = dataType;
	}

	/**
	 * @return the length
	 */
	public int getLength ()
	{
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength (int length)
	{
		this.length = length;
	}

	/**
	 * @return the scale
	 */
	public int getScale ()
	{
		return scale;
	}

	/**
	 * @param scale
	 *            the scale to set
	 */
	public void setScale (int scale)
	{
		this.scale = scale;
	}

	/**
	 * @return the precision
	 */
	public int getPrecision ()
	{
		return precision;
	}

	/**
	 * @param precision
	 *            the precision to set
	 */
	public void setPrecision (int precision)
	{
		this.precision = precision;
	}

	@Override
	public int hashCode ()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnName == null) ? 0 : columnName.hashCode ());
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode ());
		result = prime * result + length;
		result = prime * result + precision;
		result = prime * result + scale;
		return result;
	}

	@Override
	public boolean equals (Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass () != obj.getClass ())
		{
			return false;
		}
		ColumnProperties other = (ColumnProperties) obj;
		if (columnName == null)
		{
			if (other.columnName != null)
			{
				return false;
			}
		}
		else if (!columnName.equals (other.columnName))
		{
			return false;
		}
		if (dataType == null)
		{
			if (other.dataType != null)
			{
				return false;
			}
		}
		else if (!dataType.equals (other.dataType))
		{
			return false;
		}
		if (length != other.length)
		{
			return false;
		}
		if (precision != other.precision)
		{
			return false;
		}
		if (scale != other.scale)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString ()
	{
		return columnName + ", " + dataType + ", " + length + ", " + scale + ", " + precision;
	}

}
