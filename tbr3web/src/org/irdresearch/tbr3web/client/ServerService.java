/* Copyright(C) 2015 Interactive Health Solutions, Pvt. Ltd.

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
published by the Free Software Foundation; either version 3 of the License (GPLv3), or any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program; if not, write to the Interactive Health Solutions, info@ihsinformatics.com
You can also access the license on the internet at the address: http://www.gnu.org/licenses/gpl-3.0.html

Interactive Health Solutions, hereby disclaims all copyright interest in this program written by the contributors. */

package org.irdresearch.tbr3web.client;

import org.irdresearch.tbr3web.shared.Parameter;
import org.irdresearch.tbr3web.shared.model.Defaults;
import org.irdresearch.tbr3web.shared.model.Definition;
import org.irdresearch.tbr3web.shared.model.Sms;
import org.irdresearch.tbr3web.shared.model.User;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * 
 * @author owais.hussain@irdresearch.org
 */
@RemoteServiceRelativePath("greet")
public interface ServerService extends RemoteService
{
	/* Authentication methods */
	User authenticate (String userName, String password, String role) throws Exception;

	Boolean authenticateUser (String text) throws Exception;

	/* Other methods */
	Long count (String tableName, String filter) throws Exception;

	Boolean verifySecretAnswer (String userName, String secretAnswer) throws Exception;

	String generateCSVfromQuery (String database, String query) throws Exception;

	String generateReport (String fileName, Parameter[] params, boolean export) throws Exception;

	String generateReportFromQuery (String database, String reportName, String query, Boolean export) throws Exception;

	String[] getColumnData (String tableName, String columnName, String filter) throws Exception;

	String getCurrentUserName () throws Exception;
	
	String[][] getSchema() throws Exception;

	Definition[] getDefinitions () throws Exception;
	
	Defaults[] getDefaults() throws Exception;

	String[] getDumpFiles () throws Exception;

	String[][] getReportsList () throws Exception;

	String[] getRowRecord (String tableName, String[] columnNames, String filter) throws Exception;

	String getObject (String tableName, String columnName, String filter) throws Exception;

	String[] getQueriesResults (String[] queries) throws Exception;

	String getSecretQuestion (String userName) throws Exception;

	String getSnapshotTime () throws Exception;

	String[][] getTableData (String tableName, String[] columnNames, String filter) throws Exception;

	String[][] getTableData (String sqlQuery) throws Exception;

	Boolean[] getUserRgihts (String userName, String role, String menuName) throws Exception;

	Boolean exists (String tableName, String filter) throws Exception;

	int execute (String query) throws Exception;

	Boolean execute (String[] queries) throws Exception;

	Boolean executeProcedure (String procedure) throws Exception;

	void recordLogin (String userName) throws Exception;

	void recordLogout (String userName) throws Exception;

	void sendGenericSMSAlert (Sms sms) throws Exception;

	void sendGenericSMSAlert (Sms[] sms) throws Exception;

	void setCurrentUser (String userName, String role) throws Exception;

	User findUser (String currentUserName)  throws Exception;
}
