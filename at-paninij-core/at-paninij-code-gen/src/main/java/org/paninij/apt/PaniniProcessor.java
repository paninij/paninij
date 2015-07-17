/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills, David Johnston, Trey Erenberger
 */
package org.paninij.apt;

import static java.io.File.pathSeparator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.paninij.apt.check.CapsuleChecker;
import org.paninij.apt.check.CapsuleTestChecker;
import org.paninij.apt.check.SignatureChecker;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.SourceFile;
import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleTest;
import org.paninij.lang.Signature;
import org.paninij.model.CapsuleElement;
import org.paninij.model.Procedure;
import org.paninij.runtime.check.Panini$Ownership;
import org.paninij.model.SignatureElement;


/**
 * Used as a service during compilation to make automatically-generated `.java` files from classes
 * annotated with one of the annotations in `org.paninij.lang`.
 */
@SupportedAnnotationTypes({"org.paninij.lang.Capsule",
                           "org.paninij.lang.Signature",
                           "org.paninij.lang.CapsuleTester"})
@SupportedOptions({"ownership.check.method",
                   "panini.soter",
                   "panini.class.path",
                   "panini.class.path.file",
                   "panini.source.path",
                   "panini.class.output",
                   "panini.source.output"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PaniniProcessor extends AbstractProcessor
{
    // Annotation processor options (i.e. `-A` arguments):
    public static Panini$Ownership.CheckMethod ownershipCheckMethod;

    // Annotation processor options (i.e. `-A` arguments):
    protected boolean soterEnabled;
    protected String classPath;
    protected String classPathFile;
    protected String sourcePath;
    protected String classOutput;
    protected String sourceOutput;

    protected JavaCompiler javaCompiler;
    protected StandardJavaFileManager fileManager;

    RoundEnvironment roundEnv;

    @Override
    public void init(ProcessingEnvironment procEnv)
    {
        note("init()");
        super.init(procEnv);

        Map<String, String> options = procEnv.getOptions();
        initOwnershipOptions(options);
        note("Annotation Processor Options: " + options);

        soterEnabled = options.containsKey("panini.soter") ? true : false;
        if (soterEnabled)
        {
            initCompileOptions(options);
            initJavaCompiler();
            initFileManager();
        }
    }

    protected void initOwnershipOptions(Map<String, String> options)
    {
        note("Annotation Processor Options: " + options);
        initOwnershipCheckMethod(options);
    }

    protected void initOwnershipCheckMethod(Map<String, String> options)
    {
        String opt = options.get(Panini$Ownership.CheckMethod.getArgumentKey());
        if (opt == null)
        {
            ownershipCheckMethod = Panini$Ownership.CheckMethod.getDefault();
            note("No `ownership.check.method` annotation processor argument given. Using default.");
        } else {
            // Throws exception if `opt` is invalid:
            ownershipCheckMethod = Panini$Ownership.CheckMethod.fromString(opt);
        }
        note("Using ownership.check.method = " + ownershipCheckMethod);
    }


    protected void initCompileOptions(Map<String, String> options)
    {
        // TODO: Add another option to use maven default locations for compile options.
        initClassPath(options);

        final String[] REMAINING_COMPILE_OPTIONS = {
            "panini.source.path",
            "panini.class.output",
            "panini.source.output"
        };

        // Check that all required options exist.
        for (String opt : REMAINING_COMPILE_OPTIONS)
        {
            String optValue = options.get(opt);
            note(opt + " = " + optValue);
        }

        // If all checks were passed, initialize the remaining compile options.
        sourcePath    = options.get("panini.source.path");
        classOutput   = options.get("panini.class.output");
        sourceOutput  = options.get("panini.source.output");
    }
    
    
    /**
     * Initializes `classPath` using the `panini.class.path` option's value appended with the
     * contents of `panini.class.path.file`.
     */
    private void initClassPath(Map<String, String> options)
    {
        classPath = options.get("panini.class.path");
        String fileOptVal = options.get("panini.class.path.file");
        if (fileOptVal != null)
        {
            try
            {
                Path path = Paths.get(fileOptVal);
                String contents = new String(Files.readAllBytes(path));
                if (classPath == null) {
                    classPath = contents;
                } else {
                    classPath += pathSeparator + contents;
                }
            }
            catch (IOException ex)
            {
                // If the class path file could not be read, then just log it and ignore it.
                String msg ="The given `panini.class.path.file` could not be read: " + fileOptVal;
                throw new IllegalArgumentException(msg);
            }
        }
    }

    protected void initJavaCompiler()
    {
        javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler == null) {
            throw new IllegalStateException("Could not get the system java compiler.");
        }
    }
    
    protected void initFileManager()
    {
        fileManager = javaCompiler.getStandardFileManager(null, null, null);
        if (fileManager == null) {
            throw new IllegalStateException("Could not get the standard file manager.");
        }

        try {
            setFileManagerLocation(fileManager, StandardLocation.CLASS_PATH, classPath);
            setFileManagerLocation(fileManager, StandardLocation.SOURCE_PATH, sourcePath);
            setFileManagerLocation(fileManager, StandardLocation.CLASS_OUTPUT, classOutput);
            setFileManagerLocation(fileManager, StandardLocation.SOURCE_OUTPUT, sourceOutput);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Could not initialize the file manager.");
        }
    }
    
    protected void closeFileManager()
    {
        try {
            fileManager.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param fm     The file manager on which this location is set.
     * @param loc    The location with which the file paths are being associated.
     * @param path  A list of file paths separated by `File.pathSeparator` (i.e. ":" or ";").
     * 
     * @return The number files found on the path.
     * @throws IOException If the given path was somehow invalid.
     */
    private int setFileManagerLocation(StandardJavaFileManager fm, Location loc, String path)
                                                                            throws IOException
    {
        String[] strArr = path.split(File.pathSeparator);
        File[] fileArr = new File[strArr.length];
        for (int idx = 0; idx < strArr.length; idx++)
        {
            File f = new File(strArr[idx]);
            fileArr[idx] = f;
        }
        fm.setLocation(loc, Arrays.asList(fileArr));
        return fileArr.length;
    }



    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        note("Starting a round of processing for annotations: " + annotations.toString());
        this.roundEnv = roundEnv;

        // compileTest();

        // Sets which contain models
        Set<org.paninij.model.Capsule> capsules = new HashSet<org.paninij.model.Capsule>();
        Set<org.paninij.model.Signature> signatures = new HashSet<org.paninij.model.Signature>();
        Set<org.paninij.model.Capsule> capsuleTests = new HashSet<org.paninij.model.Capsule>();

        // Collect all Signature models
        for (Element elem : roundEnv.getElementsAnnotatedWith(Signature.class))
        {
            // Note: `getElementsAnnotatedWith()` even returns elements which inherit `@Signature`.
            //       This includes capsule artifacts generated in a prior round which implement a
            //       user-defined signature.
            if (elem.getAnnotation(Signature.class) != null && SignatureChecker.check(this, elem)) {
                TypeElement template = (TypeElement) elem;
                signatures.add(SignatureElement.make(template));
            }
        }

        // Collect all Capsule models
        for (Element elem : roundEnv.getElementsAnnotatedWith(Capsule.class))
        {
            if (CapsuleChecker.check(this, elem)) {
                TypeElement template = (TypeElement) elem;
                capsules.add(CapsuleElement.make(template));
            }
        }

        // Collect all CapsuleTest capsule models
        for (Element elem : roundEnv.getElementsAnnotatedWith(CapsuleTest.class))
        {
            if (CapsuleTestChecker.check(this, elem)) {
                TypeElement template = (TypeElement) elem;
                capsules.add(CapsuleElement.make(template));
                capsuleTests.add(CapsuleElement.make(template));
            }
        }

        // Artifact factories
        MessageFactory messageFactory = new MessageFactory();
        SignatureFactory signatureFactory = new SignatureFactory();
        CapsuleInterfaceFactory capsuleInterfaceFactory = new CapsuleInterfaceFactory();
        CapsuleDummyFactory capsuleDummyFactory = new CapsuleDummyFactory();
        CapsuleTestFactory capsuleTestFactory = new CapsuleTestFactory();
        CapsuleThreadFactory threadCapsuleFactory = new CapsuleThreadFactory();

        SourceFile sourceFile;  // A temporary variable.
        Set<String> toBeCompiled = new HashSet<String>();
        

        // Generate artifacts from signature model
        for (org.paninij.model.Signature signature : signatures)
        {
            // The original signature needs to be compiled.
            toBeCompiled.add(signature.getQualifiedName());
            
            // Generate Messages
            for (Procedure procedure : signature.getProcedures()) {
                this.createJavaFile(messageFactory.make(procedure));
            }

            // Generate the mangled signature.
            sourceFile = signatureFactory.make(signature);
            this.createJavaFile(sourceFile);
            toBeCompiled.add(sourceFile.qualifiedName);
        }
        
        // Generate capsule artifacts
        for (org.paninij.model.Capsule capsule : capsules)
        {
            // Add the capsule template itself:
            if (capsuleTests.contains(capsule) == false) {
                toBeCompiled.add(capsule.getQualifiedName() + PaniniModelInfo.CAPSULE_TEMPLATE_SUFFIX);
            }
            
            // Generate Messages
            for (Procedure procedure : capsule.getProcedures()) {
                this.createJavaFile(messageFactory.make(procedure));
            }

            // Generate capsule interface
            sourceFile = capsuleInterfaceFactory.make(capsule);
            this.createJavaFile(sourceFile);
            toBeCompiled.add(sourceFile.qualifiedName);
            
            // Generate dummy capsule
            sourceFile = capsuleDummyFactory.make(capsule);
            this.createJavaFile(sourceFile);
            toBeCompiled.add(sourceFile.qualifiedName);
        }
        
        if (soterEnabled)
        {
            try {
                compile(toBeCompiled);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Failed to compile.");
            }
            for (org.paninij.model.Capsule capsule : capsules)
            {
                try
                {
                    //String artifactName = capsule.getQualifiedName() + PaniniModelInfo.CAPSULE_TEMPLATE_SUFFIX;
                    String artifactName = capsule.getQualifiedName();
                    JavaFileObject javaClassFile = fileManager.getJavaFileForInput(
                        StandardLocation.CLASS_PATH,
                        artifactName,
                        Kind.CLASS
                    );
                    if (javaClassFile == null) {
                        throw new NullPointerException("The `fileManager` failed to load " + artifactName);
                    } else {
                        note("Compiled class: " + javaClassFile);
                    }
                } catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        
        for (org.paninij.model.Capsule capsule : capsules)
        {
            // Generate capsule thread profile
            this.createJavaFile(threadCapsuleFactory.make(capsule));

            // TODO Generate other capsule profiles
        }

        // Generate capsule test artifacts
        for (org.paninij.model.Capsule capsule : capsuleTests)
        {
            // Generate Messages
            for (Procedure procedure : capsule.getProcedures()) {
                this.createJavaFile(messageFactory.make(procedure));
            }

            // Generate capsule test artifact
            this.createJavaFile(capsuleTestFactory.make(capsule));
        }

        this.roundEnv = null;  // Release reference, so that the `roundEnv` can potentially be GC'd.
        note("Finished a round of processing.");

        return false;
    }

    protected void compile(Set<String> sourceClasses) throws IOException
    {
        note("compile(): " + sourceClasses);
        if (sourceClasses.isEmpty()) {
            return;
        }

        List<JavaFileObject> sourceFiles = new ArrayList<JavaFileObject>(sourceClasses.size());
        for (String sourceClass : sourceClasses)
        {
            sourceFiles.add(fileManager.getJavaFileForInput(StandardLocation.SOURCE_PATH,
                                                            sourceClass,
                                                            Kind.SOURCE));
        }
        CompilationTask task = javaCompiler.getTask(null, fileManager, null, null, null, sourceFiles);
        task.setProcessors(new ArrayList<Processor>(0));
        task.call();
    }

    void createJavaFile(SourceFile source)
    {
        if (source != null) {
            this.createJavaFile(source.qualifiedName, source.content);
        }
    }

    /**
     * @param cls The fully qualified name of the class that will go in the newly created file.
     * @param src The source to be put in the newly create java file.
     */
    void createJavaFile(String cls, String src)
    {
        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(cls);
            file.openWriter().append(src).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String getPackageOf(TypeElement type) {
        Elements utils = processingEnv.getElementUtils();
        Name pkg = utils.getPackageOf(type).getQualifiedName();
        return pkg.toString();
    }

    String getPackageOf(TypeMirror type) {
        Types utils = processingEnv.getTypeUtils();
        return getPackageOf((TypeElement) utils.asElement(type));
    }

    public void note(String msg) {
        System.out.println("--- PaniniProcessor: " + msg);
    }

    public void warning(String msg) {
        System.out.println("~~~ PaniniProcessor: " + msg);
    }

    public void error(String msg) {
        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR,
                                                 "!!! PaniniProcessor: " + msg);
    }

    public Types getTypeUtils() {
        return processingEnv.getTypeUtils();
    }

    public Elements getElementUtils() {
        return processingEnv.getElementUtils();
    }
}
