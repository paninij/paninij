package org.paninij.soter.experiments;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.paninij.soter.JavaModel;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.config.AnalysisScopeReader;

public class TestWalaClassLoading
{
    private static final String CLASSPATH = "target/classes:target/test-classes";
    private static final String NORMAL_PATH = "Lorg/paninij/soter/Secret";

    private static final String CAPSULE_TEMPLATE_TYPE = "Lorg/paninij/soter/LeakyServerTemplate";
    private static final String CAPSULE_INTERFACE_TYPE = "Lorg/paninij/soter/LeakyServer";
    private static final String CAPSULE_IMPLEMENTATION_TYPE = "Lorg/paninij/soter/LeakyServer$Thread";
    
    private static final String CAPSULE_IMPLEMENTATION_CLASS_FILE = "target/classes/org/paninij/soter/LeakyServer$Thread.class";
    private static final String CAPSULE_IMPLEMENTATION_SOURCE_FILE = "target/generated-test-sources/test-annotations/org/paninij/soter/LeakyServer$Thread.java";
    
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
        loadTest(CAPSULE_TEMPLATE_TYPE);
    }
    
    @Test
    public void loadCapsuleInterface() {
        loadTest(CAPSULE_INTERFACE_TYPE);
    }

    @Test
    public void loadCapsuleImplementation() {
        loadTest(CAPSULE_IMPLEMENTATION_TYPE);
    }
    
    @Test
    public void addCapsuleImplementationClassFile() throws Throwable
    {
        File implFile = new File(CAPSULE_IMPLEMENTATION_CLASS_FILE);
        assertTrue(implFile.canRead());
        analysisScope.addClassFileToScope(ClassLoaderReference.Application, implFile);

        // Load the class using the updated class hierarchy from the updated `analysisScope`:
        classHierarchy = ClassHierarchy.make(analysisScope);
        @SuppressWarnings("unused") String appClasses = JavaModel.allApplicationClasses(classHierarchy);
        loadTest(CAPSULE_IMPLEMENTATION_TYPE);
    }
    
    @Test
    public void addCapsuleImplementationSourceFile() throws Throwable
    {
        File implFile = new File(CAPSULE_IMPLEMENTATION_SOURCE_FILE);
        assertTrue(implFile.canRead());
        analysisScope.addSourceFileToScope(ClassLoaderReference.Application, implFile, "LeakyServer$Thread.java");
        
        // Load the class using the updated class hierarchy from the updated `analysisScope`:
        classHierarchy = ClassHierarchy.make(analysisScope);
        @SuppressWarnings("unused") String appClasses = JavaModel.allApplicationClasses(classHierarchy);
        loadTest(CAPSULE_IMPLEMENTATION_TYPE);
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
