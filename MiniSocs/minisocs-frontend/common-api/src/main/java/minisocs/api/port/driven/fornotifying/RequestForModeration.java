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

import java.time.Instant;

/**
 * This record implements the concept of requesting moderation for a message
 * posted on a social network. The creation time of the message is used to
 * uniquely identify the message in the social network.
 * 
 * @author Denis Conan, J. Paul Gibson (translation to English)
 * 
 * @param nameSocialNetwork the name of the social network.
 * @param instant           the creation time of the message to be moderated.
 */
public record RequestForModeration(String nameSocialNetwork, Instant instant) {
}
