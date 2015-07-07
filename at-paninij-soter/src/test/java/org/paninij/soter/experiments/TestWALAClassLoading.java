package org.paninij.soter.experiments;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.paninij.soter.JavaModel;

import static org.junit.Assert.assertNotNull;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.config.AnalysisScopeReader;

public class TestWALAClassLoading
{
    private static final String CLASSPATH = "target/classes:target/test-classes";
    private static final String NORMAL_PATH = "Lorg/paninij/soter/Secret";
    private static final String CAPSULE_TEMPLATE_PATH = "Lorg/paninij/soter/LeakyServerTemplate";
    private static final String CAPSULE_INTERFACE_PATH = "Lorg/paninij/soter/LeakyServer";
    private static final String CAPSULE_IMPLEMENTATION_PATH = "Lorg/paninij/soter/LeakyServer$Thread";
    
    private AnalysisScope analysisScope;
    private ClassHierarchy classHierarchy;

    
    @Before
    public void setup() throws Throwable
    {
        File exclusions = new File("Exclusions.txt");
        analysisScope = AnalysisScopeReader.makeJavaBinaryAnalysisScope(CLASSPATH, exclusions);
        classHierarchy = ClassHierarchy.make(analysisScope);
    }
    
    
    @Test
    public void loadNormal() {
        loadTest(NORMAL_PATH);
    }
    
    @Test
    public void loadCapsuleTemplate() {
        loadTest(CAPSULE_TEMPLATE_PATH);
    }
    
    @Test
    public void loadCapsuleInterface() {
        loadTest(CAPSULE_INTERFACE_PATH);
    }

    @Test
    public void loadCapsuleImplementation() {
        loadTest(CAPSULE_IMPLEMENTATION_PATH);
    }
    
    @Test
    public void addCapsuleImplementationFile() throws Throwable
    {
        File implFile = new File("target/classes/org/paninij/soter/LeakyServer$Thread.class");
        analysisScope.addClassFileToScope(ClassLoaderReference.Application, implFile);

        // Make class hierarchy again using updated `analysisScope`:
        classHierarchy = ClassHierarchy.make(analysisScope);
        String appClasses = JavaModel.allApplicationClasses(classHierarchy);

        // Try to load the capsule implementation.
        loadTest(CAPSULE_IMPLEMENTATION_PATH);
    }

    private void loadTest(String path)
    {
        TypeReference ref = getTypeReference(path);
        assertNotNull(ref);
        IClass clazz = classHierarchy.lookupClass(ref);
        assertNotNull(clazz);
    }
   
    private final TypeReference getTypeReference(String typeName) {
        return TypeReference.findOrCreate(ClassLoaderReference.Application, typeName);
    }
}
