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

package anomalydetection.api.port.driving.fordetecting.events;

import java.time.Instant;

/**
 * This record embodies the concept of a newly posted message to be analyse in
 * order to detect an anomaly. The message creation time serves to uniquely
 * identify the message within the social network. The content is a string.
 * 
 * <p>
 * By definition, messages are uniquely identified by their creation instant,
 * and, for convenience, they are comparable using their creation instants.
 * 
 * @author Denis Conan
 * 
 * @param nameSocialNetwork the name of the social network.
 * @param userPseudo        the user's pseudo.
 * @param instant           the moment the message was created.
 * @param content           the content of the message.
 */
public record Message(String nameSocialNetwork, String userPseudo, Instant instant, String content)
		implements Comparable<Message> {
	@Override
	public String toString() {
		return "Message [nameSocialNetwork=" + nameSocialNetwork + ", userPseudo=" + userPseudo + ", instant=" + instant
				+ ", content=" + content + "]";
	}

	@Override
	public int compareTo(final Message other) {
		return instant.compareTo(other.instant());
	}

}
