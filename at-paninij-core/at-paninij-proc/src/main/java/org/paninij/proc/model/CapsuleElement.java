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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.lang.Root;
import org.paninij.proc.util.PaniniModel;
import org.paninij.proc.util.TypeCollector;

public class CapsuleElement implements Capsule
{
    private String simpleName;
    private String qualifiedName;
    private TypeElement element;

    private ArrayList<Procedure> procedures;
    private ArrayList<Variable> localFields;
    private ArrayList<Variable> importFields;
    private ArrayList<Variable> state;

    private Set<String> imports;

    private boolean hasInitDecl;
    private boolean hasRunDecl;
    private boolean hasDesignDecl;

    /*
     * Generate a Capsule from a TypeElement. The TypeElement should already be checked for
     * any errors. The TypeElement should be annotated with @Capsule and should represent
     * a CapsuleTemplate
     */
    public static Capsule make(TypeElement e) {
        CapsuleElement capsule = new CapsuleElement();
        CapsuleTemplateVisitor visitor = new CapsuleTemplateVisitor();
        e.accept(visitor, capsule);
        return capsule;
    }

    private CapsuleElement() {
        this.simpleName = "";
        this.qualifiedName = "";
        this.element = null;
        this.procedures = new ArrayList<Procedure>();
        this.localFields = new ArrayList<Variable>();
        this.importFields = new ArrayList<Variable>();
        this.state = new ArrayList<Variable>();
        this.imports = new HashSet<String>();
        this.hasInitDecl = false;
        this.hasRunDecl = false;
        this.hasDesignDecl = false;
    }

    @Override
    public List<Variable> getLocalFields() {
        return new ArrayList<Variable>(this.localFields);
    }

    @Override
    public List<Variable> getImportFields() {
        return new ArrayList<Variable>(this.importFields);
    }

    @Override
    public List<Variable> getStateFields() {
        return new ArrayList<Variable>(this.state);
    }

    public void addLocals(Variable v) {
        this.localFields.add(v);
    }

    public void addImportDecl(Variable v) {
        this.importFields.add(v);
    }

    public void addState(Variable v) {
        this.state.add(v);
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
    public List<Procedure> getProcedures() {
        return this.procedures;
    }

    @Override
    public Set<String> getImports() {
        return this.imports;
    }

    @Override
    public String getPackage() {
        PackageElement pack = (PackageElement) this.element.getEnclosingElement();
        return pack.getQualifiedName().toString();
    }

    @Override
    public ArrayList<String> getSignatures() {
        ArrayList<String> sigs = new ArrayList<String>();

        for (TypeMirror i : this.element.getInterfaces()) {
            String name = i.toString();
            assert(name.endsWith(PaniniModel.SIGNATURE_TEMPLATE_SUFFIX));
            name = name.substring(0, name.length() - PaniniModel.SIGNATURE_TEMPLATE_SUFFIX.length());
            sigs.add(name);
        }
        return sigs;
    }
    
    @Override
    public boolean isRoot() {
        return this.element.getAnnotation(Root.class) != null;
    }

    @Override
    public boolean hasInit() {
        return this.hasInitDecl;
    }

    @Override
    public boolean hasRun() {
        return this.hasRunDecl;
    }

    @Override
    public boolean hasDesign() {
        return this.hasDesignDecl;
    }

    @Override
    public boolean isActive() {
        return this.hasRunDecl;
    }

    @Override
    public boolean hasActiveAncestor() {
        // TODO
        return false;
    }

    public void addExecutable(ExecutableElement e) {
        if (PaniniModel.isProcedure(e)) {
            this.procedures.add(new ProcedureElement(e));
        } else {
            String name = e.getSimpleName().toString();

            if (name.equals("init")) {
                this.hasInitDecl = true;
            } else if (name.equals("run")) {
                this.hasRunDecl = true;
            } else if (name.equals("design")) {
                this.hasDesignDecl = true;
            }

        }
    }

    public void setTypeElement(TypeElement e) {
        if (this.element == null) {
            this.element = e;
            this.imports = TypeCollector.collect(this.element);
            this.simpleName = PaniniModel.simpleCapsuleName(e);
            this.qualifiedName = PaniniModel.qualifiedCapsuleName(e);
        }
    }
}
