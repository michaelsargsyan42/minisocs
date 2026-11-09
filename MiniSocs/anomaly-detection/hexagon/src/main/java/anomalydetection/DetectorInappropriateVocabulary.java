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

package anomalydetection;

import java.util.Arrays;
import java.util.List;

import anomalydetection.api.constants.AnomalyType;

/**
 * This class realises the detector of inappropriate vocabulary. The collection
 * of "inappropriate words" is fixed.
 * 
 * @author Denis Conan
 */
public final class DetectorInappropriateVocabulary implements AnomalyDetector {
	/**
	 * the collection of inappropriate words.
	 */
	private static final List<String> INAPPROPRIATE_WORDS = Arrays.asList("inappropriate", "unsuitable", "inadequate",
			"improper");

	/**
	 * empty constructor.
	 */
	public DetectorInappropriateVocabulary() {
		super();
	}
	
	@Override
	public AnomalyType type() {
		return AnomalyType.INAPPROPRIATE_VOCABULARY;
	}

	@Override
	public boolean check(final String content) {
		return INAPPROPRIATE_WORDS.stream().anyMatch(w -> content.toLowerCase().contains(w));
	}

	@Override
	public String toString() {
		return "DetectorInappropriateVocabulary []";
	}
}
