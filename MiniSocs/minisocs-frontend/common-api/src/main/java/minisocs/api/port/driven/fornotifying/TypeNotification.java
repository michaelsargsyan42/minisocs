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
 * This enumerated type models the type of notification to the user.
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 */
public enum TypeNotification {
	/**
	 * moderation request.
	 */
	MODERATION_REQUEST("moderation request"),
	/**
	 * new messages.
	 */
	NEW_MESSAGES("new messages"),
	/**
	 * new message.
	 */
	NEW_MESSAGE("new message"),
	/**
	 * unknown. This instance is used when de-serialising an empty notification.
	 */
	UNKNOWN("unknown");

	/**
	 * the name of the notification type to display.
	 */
	private String name;

	/**
	 * constructs an enumerator.
	 * 
	 * @param name the name of the notification type.
	 */
	TypeNotification(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
