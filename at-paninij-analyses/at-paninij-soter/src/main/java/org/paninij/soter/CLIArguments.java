package org.paninij.soter;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class CLIArguments
{
    private static final String CLASS_OUTPUT_DESCRIPTION =
            "The path to the directory in which the instrumented classes should be placed.";
    
    private static final String CLASS_PATH_DESCRIPTION =
            "The class path needed to compile the given capsule classes. This value will be "
            + "appended to the contents of the `-classPathFile`";
    
    private static final String CLASS_PATH_FILE_DESCRIPTION =
            "A file containing the class path needed to compile the given capsule classes. This "
            + "file's contents will be appended to the `-classPath` value.";
    
    private static final String ANALYSIS_REPORTS_DESCRIPTION =
            "The path to the directory in which the SOTER analysis reports should be placed. "
            + "If this option is not set then no analysis reports will be generated.";
    
    private static final String CALL_GRAPH_PDFS_DESCRIPTION =
            "The path to the directory in which SOTER call graph PDFs should be placed. "
            + "If this option is not set, then no call graph PDFs will be generated.";
    
    private static final String HEAP_GRAPH_PDFS_DESCRIPTION =
            "The path to the directory in which SOTER heap graph PDFs should be placed. "
            + "If this option is not set, then no heap graph PDFs will be generated.";
    
    private static final String ORIG_BYTECODE_DESCRIPTION =
            "The path to the directory in which uninstrumented bytecode should be logged. "
            + "A disassembly code file (generated from `javap -c`) is created for each capsule "
            + "templates. If this option is not set, then no such files are created.";
    
    private static final String NO_INSTRUMENT_DESCRIPTION =
            "Set this flag to disable class file instrumentation (which is enabled by default).";

    private static final String CAPSULE_TEMPLATES_DESCRIPTION =
            "A sequence of fully qualified capsule templates (e.g. `com.example.foo.FooTemplate`) "
            + "to be analyzed and instrumented.";
    
    @Parameter(names = "-classOutput", description = CLASS_OUTPUT_DESCRIPTION)
    public String classOutput;
    
    @Parameter(names = "-classPath", description = CLASS_PATH_DESCRIPTION)
    public String classPath;
    
    @Parameter(names = "-classPathFile", description = CLASS_PATH_FILE_DESCRIPTION)
    public String classPathFile;

    @Parameter(names = "-analysisReports", description = ANALYSIS_REPORTS_DESCRIPTION)
    public String analysisReports;

    @Parameter(names = "-callGraphPDFs", description = CALL_GRAPH_PDFS_DESCRIPTION)
    public String callGraphPDFs;

    @Parameter(names = "-heapGraphPDFs", description = HEAP_GRAPH_PDFS_DESCRIPTION)
    public String heapGraphPDFs;
    
    @Parameter(names = "-origBytecode", description = ORIG_BYTECODE_DESCRIPTION)
    public String origBytecode;

    @Parameter(names = "-noInstrument", description = NO_INSTRUMENT_DESCRIPTION)
    public Boolean noInstrument = false;
    
    @Parameter(description = CAPSULE_TEMPLATES_DESCRIPTION)
    public List<String> capsules = new ArrayList<>();
}
