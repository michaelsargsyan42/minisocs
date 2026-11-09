/**
This file is part of the course CSC5002.

The course material is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

The course material is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the course CSC5002.  If not, see <http://www.gnu.org/licenses/>.

Initial developer(s): Denis Conan
Contributor(s):
*/

package anomalydetection.api.constants;

/**
 * This class contains the constant for ORM EclipseLink.
 * 
 * @author Denis Conan
 * 
 */
public class ORMEclipseLink {
	public static final String PERSISTENCE_UNIT_NAME = "incidents";
	public static final int NETWORKSERVER_PORT = 1621;
	public static final String LOCAL_PATH = "/target/databases";
	public static final String DERBY_CLIENT_DRIVER = "org.apache.derby.jdbc.ClientDriver";
}
