package org.paninij.apt;

import static java.io.File.pathSeparator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.paninij.apt.check.StaticOwnershipTransfer;
import org.paninij.runtime.check.DynamicOwnershipTransfer;

public class ProcessorOptions
{
    protected Map<String, String> options;
    
    // Annotation processor options (i.e. `-A` arguments):
    public final List<File> classPath;
    public final List<File> sourcePath;
    public final File classOutput;
    public final File sourceOutput;
    
    public final File classPathFile;
    public final List<File> effectiveClassPath;
    public final String effectiveClassPathString;

    public final DynamicOwnershipTransfer.Kind dynamicOwnershipTransferKind;
    public final StaticOwnershipTransfer.Kind staticOwnershipTransferKind;
    
    public final File analysisReports;
    
    /**
     * If null, then no call graph PDFs are generated. Otherwise, this is a path to a directory in
     * which the PDFs will be placed.
     */
    public final File callGraphPDFs;
    
    /**
     * If null, then no heap graph PDFs are generated. Otherwise, this is a path to a directory in
     * which the PDFs will be placed.
     */
    public final File heapGraphPDFs;

    
    public ProcessorOptions(Map<String, String> options)
    {
        this.options = options;

        // Initialize the compiler options.
        classPath = makePathFromOption(options, "panini.classPath");
        sourcePath = makePathFromOption(options, "panini.sourcePath");
        classOutput = makeFileFromOption(options, "panini.classOutput");
        sourceOutput = makeFileFromOption(options, "panini.sourceOutput");
        
        classPathFile = makeFileFromOption(options, "panini.classPathFile");
        effectiveClassPath = makeEffectiveClassPath(options.get("panini.classPath"),
                                                    options.get("panini.classPathFile"));
        effectiveClassPathString = makeEffectiveClassPathString(options.get("panini.classPath"),
                                                                options.get("panini.classPathFile"));

        // Initialize the ownership transfer kinds.
        String opt;
        opt = options.get(DynamicOwnershipTransfer.ARGUMENT_KEY);
        dynamicOwnershipTransferKind = DynamicOwnershipTransfer.Kind.fromString(opt);
        opt = options.get(StaticOwnershipTransfer.ARGUMENT_KEY);
        staticOwnershipTransferKind = StaticOwnershipTransfer.Kind.fromString(opt);
        
        // Initialize all options related to SOTER (i.e. `-Apanini.soter.*`) if the static ownership
        // transfer kind is set to SOTER. Otherwise, ignore any SOTER options.
        if (staticOwnershipTransferKind == StaticOwnershipTransfer.Kind.SOTER)
        {
            analysisReports = makeFileFromOption(options, "panini.soter.analysisReports");
            callGraphPDFs = makeFileFromOption(options, "panini.soter.callGraphPDFs");
            heapGraphPDFs = makeFileFromOption(options, "panini.soter.heapGraphPDFs");
        }
        else
        {
            analysisReports = null;
            callGraphPDFs = null;
            heapGraphPDFs = null;
        }
    }
    
    
    public String get(String key)
    {
        return options.get(key);
    }
    
    
    private static List<File> makeEffectiveClassPath(String classPath, String classPathFile)
    {
        return makePathFromString(makeEffectiveClassPathString(classPath, classPathFile));
    }


    /**
     * @return A files representing the file found the the options map under the given
     *         key. If nothing was found when looking up the key, then `null` will be returned.
     */
    private static File makeFileFromOption(Map<String, String> options, String key)
    {
        String option = options.get(key);
        return (option == null) ? null : new File(option);
    }
    
    
    /**
     * @return A list of files representing the file path found the the options map under the given
     *         key. Elements of the file path are expected to be separated with the
     *         `File.pathSeparator`. If nothing was found when looking up the key, then `null` will
     *         be returned.
     */
    private static List<File> makePathFromOption(Map<String, String> options, String key)
    {
        String option = options.get(key);
        if (option == null || option.equals("")) {
            return null;
        } else {
            return makePathFromString(option);
        }
    }
    
    // TODO: Move this somewhere better.
    public static List<File> makePathFromString(String option)
    {
        if (option == null) {
            return null;
        }
        
        String[] strArray = option.split(File.pathSeparator);
        if (strArray.length == 0)
        {
            String msg = "The given option cannot be interpreted as a path: " + option;
            throw new IllegalArgumentException(msg);
        }

        List<File> path = new ArrayList<File>(strArray.length);
        for (String str : strArray)
        {
            path.add(new File(str));
        }
        return path;   
    }
    
    public static void assertWellFormed(ProcessorOptions options)
    {
        throw new UnsupportedOperationException("TODO");
    }


    /**
     * Appends the contents of the `classPathFile` to the given `classPath`. Either argument can
     * be `null`.
     * 
     * @throws IllegalArgumentException if the `classPathFile` could not be read.
     */
    public static String makeEffectiveClassPathString(String classPath, String classPathFile)
    {
        if (classPathFile == null || classPathFile == "") {
            return classPath;
        }
    
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(classPathFile));
            String contents = new String(bytes, "UTF-8");
            if (! contents.isEmpty()) {
                classPath = (classPath.equals("")) ? contents : classPath+pathSeparator+contents;
            }
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Could not read `classPathFile`: " + classPathFile);
        }
    
        return classPath;
    }
    
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ProcessorOptions {\n")
               .append("    options = ").append(options).append('\n')
               .append("    classPath = ").append(classPath).append('\n')
               .append("    sourcePath = ").append(sourcePath).append('\n')
               .append("    classOutput = ").append(classOutput).append('\n')
               .append("    sourceOutput = ").append(sourceOutput).append('\n')
               .append("    classPathFile = ").append(classPathFile).append('\n')
               .append("    effectiveClassPath = ").append(effectiveClassPath).append('\n')
               .append("    effectiveClassPathString = ").append(effectiveClassPathString).append('\n')
               .append("    dynamicOwnershipTransferKind = ").append(dynamicOwnershipTransferKind).append('\n')
               .append("    staticOwnershipTransferKind = ").append(staticOwnershipTransferKind).append('\n')
               .append("    analysisReports = ").append(analysisReports).append('\n')
               .append("    callGraphPDFs = ").append(callGraphPDFs).append('\n')
               .append("    heapGraphPDFs = ").append(heapGraphPDFs).append('\n')
               .append("}");

        return builder.toString();
    }
}
