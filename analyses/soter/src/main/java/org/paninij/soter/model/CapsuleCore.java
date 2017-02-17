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
package org.paninij.soter.model;

import org.paninij.runtime.util.IdentitySet;
import org.paninij.soter.util.PaniniModel;
import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.types.TypeName;

class CapsuleCore
{
    protected TypeName typeName;
    protected String qualifiedName;

    // Panini Model Elements:
    protected IClass core;
    protected IMethod initDecl;
    protected IMethod designDecl;
    protected IMethod runDecl;
    protected IdentitySet<IMethod> procedures = new IdentitySet<IMethod>();
    protected IdentitySet<IMethod> methods = new IdentitySet<IMethod>();
    
    /**
     * @param core A capsule core class.
     */
    public CapsuleCore(IClass core)
    {
        if (PaniniModel.isCapsuleCore(core) == false) {
            String msg = "The `core` argument is not a capsule core: " + core;
            throw new IllegalArgumentException(msg);
        }

        typeName = core.getName();
        qualifiedName = toQualifiedName(typeName);
        
        initModelElements(core);
    }
    
    private void initModelElements(IClass coreClass)
    {
        core = coreClass;
        for (IMethod method : core.getDeclaredMethods())
        {
            switch (method.getName().toString())
            {
            case "init":
                initDecl = method;
                continue;
            case "design":
                designDecl = method;
                continue;
            case "run":
                runDecl = method;
                continue;
            default:
                if (method.isClinit() || method.isInit() || method.isStatic() || method.isNative())
                    continue;
                if (PaniniModel.isProcedure(method)) {
                    procedures.add(method);
                } else {
                    methods.add(method);
                }
            }
        }        
    }

    public IClass getCoreClass()
    {
        return core;
    }
    
    public String getWalaPath()
    {
        return WalaUtil.fromQualifiedNameToWalaPath(qualifiedName);
    }

    /**
     * @param qualifiedName A fully-qualified capsule name of the form "org.paninij.examples.pi.Pi".
     */
    public String getQualifiedName()
    {
        return qualifiedName;
    }
    
    private static String toQualifiedName(TypeName typeName)
    {
        String packageName = new String(typeName.getPackage().getValArray());
        String className = new String(typeName.getClassName().getValArray());
        if (packageName.isEmpty()) {
            return className;
        } else {
            return packageName + "/" + className;
        }
    }
}
