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
Contributor(s): J. Paul Gibson (translation to English)
*/

package minisocs.api.port.driven.fornotifying;

/**
 * This record implements the concept of user notification. The first argument
 * is used to type the second argument, which is the record that has been
 * serialized into a string.
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 * 
 * @param type    the type of notification.
 * @param content the serialised notification content.
 */
public record Notification(TypeNotification type, String content) {
	/**
	 * builds an "empty" notification. By default, the type is 
	 */
	public Notification() {
		this(TypeNotification.UNKNOWN, "");
	}
}
