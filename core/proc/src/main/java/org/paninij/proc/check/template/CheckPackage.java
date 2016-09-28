/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package org.paninij.proc.check.template;

import javax.lang.model.element.TypeElement;

import static org.paninij.proc.check.Check.Result.error;

/**
 * Check that the given template is in an okay package. Currently, the only invalid package is the
 * default package (i.e. no package).
 */
public class CheckPackage implements TemplateCheck {

	private boolean checkInDefault(TypeElement e) {
		return e.getQualifiedName().equals(e.getSimpleName());
	}
	
	@Override
	public Result checkTemplate(TypeElement template, TemplateKind templateKind) {
		if (checkInDefault(template)) {
            String errMsg = "Templates cannot be in the default package.";
			return error(errMsg, CheckPackage.class, template);
		}
		return Result.OK;
	}

}
