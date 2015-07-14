package org.paninij.soter.tests;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.paninij.soter.util.JavaModel;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.util.config.AnalysisScopeReader;

public class TestWalaClassLoading
{
    private static final String CLASSPATH = "lib/at-paninij-runtime.jar:target/classes:target/test-classes";

    private static final String NORMAL_TYPE = "Lorg/paninij/soter/tests/Secret";
    private static final String CAPSULE_TEMPLATE_TYPE = "Lorg/paninij/soter/tests/LeakyServerTemplate";
    private static final String CAPSULE_INTERFACE_TYPE = "Lorg/paninij/soter/tests/LeakyServer";
    private static final String CAPSULE_IMPLEMENTATION_TYPE = "Lorg/paninij/soter/tests/LeakyServer$Thread";
    
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
        loadTest(NORMAL_TYPE);
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
    
    private void loadTest(String path)
    {
        @SuppressWarnings("unused") String appClasses = JavaModel.getApplicationClassesString(classHierarchy);

        TypeReference ref = getTypeReference(path);
        assertNotNull(ref);
        IClass clazz = classHierarchy.lookupClass(ref);
        assertNotNull(clazz);
    }
   
    private final TypeReference getTypeReference(String typeName) {
        return TypeReference.findOrCreate(ClassLoaderReference.Application, typeName);
    }
}
