package org.paninij.soter.cli;

import static java.io.File.pathSeparator;
import static org.paninij.soter.util.Log.note;
import static org.paninij.soter.util.Log.warning;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Some simple static utility methods to enable some functionality of the `SoterCommand`.
 * 
 * TODO: This functionality should probably be moved elsewhere.
 */
class Util
{

    /**
     * Appends the contents of the `classPathFile` to the given `classPath`. Either argument can
     * be `null`.
     * 
     * @throws IllegalArgumentException if the `classPathFile` could not be read.
     */
    // TODO: Move this helper method to somewhere else.
    static String makeEffectiveClassPath(String classPath, String classPathFile)
    {
        if (classPath == null) {
            classPath = "";
        }
        if (classPathFile == null || classPathFile == "") {
            return classPath;
        }
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(classPathFile));
            String contents = new String(bytes, "UTF-8");
            if (! contents.isEmpty()) {
                classPath = (classPath.equals("")) ? contents : classPath + pathSeparator + contents;
            }
        }
        catch (IOException ex) {
            throw new IllegalArgumentException("Could not read `classPathFile`: " + classPathFile);
        }
    
        return classPath;
    }

    // TODO: Move this helper method to somewhere else.
    static void logAllTemplatesDisassembledBytecode(List<String> capsules, String classpath,
                                                    String origBytecodeDir)
                                                    throws IOException, InterruptedException
    {
        for (String capsuleTemplate: capsules) {
            logDisassembledBytecode(capsuleTemplate + "Template", classpath, origBytecodeDir);
        } 
    }

    // TODO: Move this helper method to somewhere else.
    static void logDisassembledBytecode(String qualifiedClassName, String classpath,
                                                  String directory)
                                                  throws IOException, InterruptedException
    {
        // TODO: BUG: Handle the case that `classpath` contains spaces!
        note("Logging Original Bytecode: " + qualifiedClassName);
        String cmd = "javap -c -classpath " + classpath + " " + qualifiedClassName;

        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(cmd);
        Path path = Paths.get(directory, qualifiedClassName);

        proc.waitFor();
        if (proc.exitValue() > 0)
        {
            warning("Attempt to obtain disassembled bytecode failed: " + qualifiedClassName);
            InputStream err = proc.getErrorStream();
            Files.copy(err, path, StandardCopyOption.REPLACE_EXISTING);
        }
        else
        {
            InputStream in = proc.getInputStream();
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
