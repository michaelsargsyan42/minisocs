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

package minisocs.api.port.driving.forrequestingminisocs.returnvalues;

import java.time.Instant;

import minisocs.common.UtilSerialiser;

/**
 * This record embodies the concept of a newly posted message. The message
 * creation time serves to uniquely identify the message within the social
 * network. The content is a string.
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 * 
 * @param nameSocialNetwork the name of the social network.
 * @param instant           the moment the message was created.
 * @param content           the content of the message.
 */
public record NewMessage(String nameSocialNetwork, Instant instant, String content) {
	/**
	 * constructs a new message with preconditions on the arguments.
	 * 
	 * @param nameSocialNetwork the name of the social network.
	 * @param instant           the moment the message was created.
	 * @param content           the content of the message.
	 */
	public NewMessage {
		if (nameSocialNetwork == null || nameSocialNetwork.isBlank()) {
			throw new IllegalArgumentException("nameSocialNetwork cannot be null or empty");
		}
		if (instant == null) {
			throw new IllegalArgumentException("instant cannot be empty");
		}
		if (content == null || content.isBlank()) {
			throw new IllegalArgumentException("content cannot be null or empty");
		}
	}

	@Override
	public String toString() {
		return "NewMessage [nameSocialNetwork=" + nameSocialNetwork + ", instant="
				+ UtilSerialiser.formatInstantIntoString(instant) + ", content=" + content + "]";
	}

}
