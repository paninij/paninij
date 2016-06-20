package org.paninij.jdt.plugin;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;

public class Plugin extends CompilationParticipant {

    @Override
    public int aboutToBuild(IJavaProject project) {
        log("aboutToBuild");
        return READY_FOR_BUILD;
    }
    
    @Override
    public void buildFinished(IJavaProject project) {
        log("buildFinished");
    }
    
    @Override
    public void buildStarting(BuildContext[] files, boolean isBatch) {
        log("buildStarting");
    }
    
    @Override
    public void cleanStarting(IJavaProject project) {
        log("cleanStarting");
    }
    
    @Override
    public boolean isActive(IJavaProject project) {
        log("isActive");
        try { return project.findType("org.panini.lang.Capsule") != null; }
        catch (JavaModelException ex) { return false; }
    }
    
    @Override
    public void reconcile(ReconcileContext context) {
        log("reconcile");
    }
    
    private void log(Object obj) {
        System.out.println(obj);
    }
}
