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
package org.paninij.soter.cga;

import org.paninij.soter.model.CapsuleTemplate;
import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.cha.IClassHierarchy;

public class CallGraphAnalysisFactory
{
    protected final IClassHierarchy cha;
    protected final AnalysisOptions options;
    
    public CallGraphAnalysisFactory(IClassHierarchy cha, AnalysisOptions options)
    {
        this.cha = cha;
        this.options = options;
    }

    public CallGraphAnalysis make(CapsuleTemplate template)
    {
        return new CallGraphAnalysis(template, cha, options);
    }
    
    /**
     * A helper method for making a call graph analysis and performing the build in the default way.
     * This is useful for building a single call for a template. However, if call graphs for
     * multiple templates are needed, it is recommended (for performance reasons) separate
     * `CallGraphAnalyses` and to call perform on each with resources shared across all of the
     * call graph analyses (e.g. the class * hierarchy analysis).
     * 
     * @param templateName  The name of the template to be analyzed. Should be something of the form
     *                      `-Lorg/paninij/soter/FooTemplate`.
     * @param classPath     A colon-separated list of file system locations in which WALA should
     *                      look for application classes.
     */
    public static CallGraphAnalysis performStandardAnalysis(String templateName, String classPath)
    {
        IClassHierarchy cha = WalaUtil.makeClassHierarchy(classPath);
        AnalysisOptions options = WalaUtil.makeAnalysisOptions(cha);
        IClass templateClass = WalaUtil.loadTemplateClass(templateName, cha);
        CapsuleTemplate template = new CapsuleTemplate(templateClass);

        CallGraphAnalysis cga = new CallGraphAnalysis(template, cha, options);
        cga.perform();
        return cga;
    }
}
