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

package minisocs.common;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import minisocs.api.exceptions.UnfeasibleOperation;
import minisocs.api.port.driving.forrequestingminisocs.returnvalues.ExceptionAsReturnValue;
import minisocs.api.port.driving.forrequestingminisocs.returnvalues.NewMessage;
import minisocs.api.port.driving.forrequestingminisocs.returnvalues.NewMessages;
import minisocs.api.port.driving.forrequestingminisocs.returnvalues.SocialNetworkReturnValue;
import minisocs.api.port.driving.forrequestingminisocs.returnvalues.UserReturnValue;

/**
 * This class contains utility methods to de-serialise JSON return values.
 */
public final class UtilSerialiser {
	/**
	 * the object mapper for serialising and de-serialising objects, including
	 * instants (JSR310).
	 */
	private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
	/**
	 * the date formatter for displaying instants.
	 */
	public static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter.ISO_INSTANT.withLocale(Locale.FRANCE)
			.withZone(ZoneId.systemDefault());

	private UtilSerialiser() {
		// nop
	}

	/**
	 * the instant to serialise using JSR310 using {@link #INSTANT_FORMATTER}.
	 * 
	 * @param instant the instant.
	 * @return the string.
	 */
	public static String formatInstantIntoString(final Instant instant) {
		return INSTANT_FORMATTER.format(instant);
	}

	/**
	 * serialises the object.
	 * 
	 * @param value the object to serialise.
	 * @return the string.
	 */
	public static String writeValueAsString(final Object value) throws JsonProcessingException {
		synchronized (MAPPER) {
			return MAPPER.writeValueAsString(value);
		}
	}

	/**
	 * de-serialises JSON content.
	 *
	 * @param <T>       the type of the object to instantiate.
	 * @param content   the JSON string to de-serialise.
	 * @param valueType the type of the object to instantiate.
	 * @return the object that has been instantiated.
	 */
	public static <T> T readValue(final String content, final Class<T> valueType) throws JsonProcessingException {
		synchronized (MAPPER) {
			return MAPPER.readValue(content, valueType);
		}
	}

	/**
	 * de-serialises an exception return value.
	 * 
	 * @param json the JSON string.
	 * @return the record.
	 * @throws UnfeasibleOperation in case of problem de-serialising.
	 */
	public static ExceptionAsReturnValue exceptionAsReturnValueFromJson(final String json) throws UnfeasibleOperation {
		try {
			synchronized (MAPPER) {
				return MAPPER.readValue(json, ExceptionAsReturnValue.class);
			}
		} catch (JsonProcessingException ex) {
			throw new UnfeasibleOperation("pb deserialize (msg " + ex.getLocalizedMessage() + ", json=" + json + ")");
		}
	}

	/**
	 * de-serialises a user return value.
	 * 
	 * @param json the JSON string.
	 * @return the record.
	 * @throws UnfeasibleOperation in case of problem de-serialising.
	 */
	public static UserReturnValue userReturnValueFromJson(final String json) throws UnfeasibleOperation {
		try {
			synchronized (MAPPER) {
				return MAPPER.readValue(json, UserReturnValue.class);
			}
		} catch (JsonProcessingException ex) {
			throw new UnfeasibleOperation("pb deserialize (msg " + ex.getLocalizedMessage() + ", json=" + json + ")");
		}
	}

	/**
	 * de-serialises a social network return value.
	 * 
	 * @param json the JSON string.
	 * @return the record.
	 * @throws UnfeasibleOperation in case of problem de-serialising.
	 */
	public static SocialNetworkReturnValue socialNetworkReturnValueFromJson(final String json)
			throws UnfeasibleOperation {
		try {
			synchronized (MAPPER) {
				return MAPPER.readValue(json, SocialNetworkReturnValue.class);
			}
		} catch (JsonProcessingException ex) {
			throw new UnfeasibleOperation("pb deserialize (msg " + ex.getLocalizedMessage() + ", json=" + json + ")");
		}
	}

	/**
	 * de-serialises a new message.
	 * 
	 * @param json the JSON string.
	 * @return the record.
	 * @throws UnfeasibleOperation in case of problem de-serialising.
	 */
	public static NewMessage newMessageFromJson(final String json) throws UnfeasibleOperation {
		try {
			synchronized (MAPPER) {
				return MAPPER.readValue(json, NewMessage.class);
			}
		} catch (JsonProcessingException ex) {
			throw new UnfeasibleOperation("pb deserialize (msg " + ex.getLocalizedMessage() + ", json=" + json + ")");
		}
	}

	/**
	 * de-serialises new messages.
	 * 
	 * @param json the JSON string.
	 * @return the record.
	 * @throws UnfeasibleOperation in case of problem de-serialising.
	 */
	public static NewMessages newMessagesFromJson(final String json) throws UnfeasibleOperation {
		try {
			synchronized (MAPPER) {
				return MAPPER.readValue(json, NewMessages.class);
			}
		} catch (JsonProcessingException ex) {
			throw new UnfeasibleOperation("pb deserialize (msg " + ex.getLocalizedMessage() + ", json=" + json + ")");
		}
	}

	/**
	 * de-serialises a list of users.
	 * 
	 * @param json the JSON string.
	 * @return the list of users.
	 * @throws UnfeasibleOperation in case of problem de-serialising.
	 */
	public static List<UserReturnValue> listOfUserReturnValuesFromJson(final String json) throws UnfeasibleOperation {
		if (json == null || json.isEmpty()) {
			return Collections.emptyList();
		}
		try {
			synchronized (MAPPER) {
				return MAPPER.readValue(json,
						MAPPER.getTypeFactory().constructCollectionType(List.class, UserReturnValue.class));
			}
		} catch (JsonProcessingException ex) {
			throw new UnfeasibleOperation("pb deserialize (msg " + ex.getLocalizedMessage() + ", json=" + json + ")");
		}
	}

	/**
	 * de-serialises a list of social networks.
	 * 
	 * @param json the JSON string.
	 * @return the list of social networks.
	 * @throws UnfeasibleOperation in case of problem de-serialising.
	 */
	public static List<SocialNetworkReturnValue> listOfSocialNetworkReturnValuesFromJson(final String json)
			throws UnfeasibleOperation {
		if (json == null || json.isEmpty()) {
			return Collections.emptyList();
		}
		try {
			synchronized (MAPPER) {
				return MAPPER.readValue(json,
						MAPPER.getTypeFactory().constructCollectionType(List.class, SocialNetworkReturnValue.class));
			}
		} catch (JsonProcessingException ex) {
			throw new UnfeasibleOperation("pb deserialize (msg " + ex.getLocalizedMessage() + ", json=" + json + ")");
		}
	}
}
