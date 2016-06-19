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
package org.paninij.proc.model;

import java.util.ArrayList;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.util.PaniniModel;
import org.paninij.proc.util.TypeCollector;

//TODO
public class SignatureElement implements Signature
{
    private String simpleName;
    private String qualifiedName;
    private TypeElement element;
    private ArrayList<Procedure> procedures;

    public static Signature make(TypeElement e) {
        SignatureElement signature = new SignatureElement();
        SignatureTemplateVisitor visitor = new SignatureTemplateVisitor();
        e.accept(visitor,  signature);
        return signature;
    }

    private SignatureElement() {
        this.simpleName = "";
        this.qualifiedName = "";
        this.element = null;
        this.procedures = new ArrayList<Procedure>();
    }

    @Override
    public String getSimpleName() {
        return this.simpleName;
    }

    @Override
    public String getQualifiedName() {
        return this.qualifiedName;
    }

    @Override
    public ArrayList<Procedure> getProcedures() {
        return this.procedures;
    }

    @Override
    public Set<String> getImports() {
        return TypeCollector.collect(this.element);
    }

    @Override
    public String getPackage() {
        PackageElement pack = (PackageElement) this.element.getEnclosingElement();
        return pack.getQualifiedName().toString();
    }

    public void setTypeElement(TypeElement e) {
        if (this.element == null) {
            this.element = e;
            this.simpleName = PaniniModel.simpleSignatureName(e);
            this.qualifiedName = PaniniModel.qualifiedSignatureName(e);
        }
    }

    public void addExecutable(ExecutableElement e) {
        if (PaniniModel.isProcedure(e)) {
            this.procedures.add(new ProcedureElement(e));
        }
    }
}
