package org.paninij.soter;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class CLIArguments
{
    private static final String CLASS_OUTPUT_DESCRIPTION =
            "The path to the directory in which the instrumented classes should be placed.";
    
    private static final String COMPILER_CLASS_PATH_DESCRIPTION =
            "The class path needed to compile the given capsule template classes.";
    
    private static final String ANALYSIS_REPORTS_DESCRIPTION =
            "The path to the directory in which the SOTER analysis reports should be placed. "
            + "If this option is not set then no analysis reports will be generated.";
    
    private static final String CALL_GRAPH_PDFS_DESCRIPTION =
            "The path to the directory in which SOTER call graph PDFs should be placed. "
            + "If this option is not set, then no call graph PDFs will be generated.";
    
    private static final String HEAP_GRAPH_PDFS_DESCRIPTION =
            "The path to the directory in which SOTER heap graph PDFs should be placed. "
            + "If this option is not set, then no heap graph PDFs will be generated.";
    
    private static final String CAPSULE_TEMPLATES_DESCRIPTION =
            "A sequence of fully qualified capsule templates (e.g. `com.example.foo.FooTemplate`) "
            + "to be analyzed and instrumented.";
    
    @Parameter(names = "-classOutput", description = CLASS_OUTPUT_DESCRIPTION)
    public String classOutput;
    
    @Parameter(names = "-compilerClassPath", description = COMPILER_CLASS_PATH_DESCRIPTION)
    public String compilerClassPath;

    @Parameter(names = "-analysisReports", description = ANALYSIS_REPORTS_DESCRIPTION)
    public String analysisReports;

    @Parameter(names = "-callGraphPDFs", description = CALL_GRAPH_PDFS_DESCRIPTION)
    public String callGraphPDFs;

    @Parameter(names = "-heapGraphPDFs", description = HEAP_GRAPH_PDFS_DESCRIPTION)
    public String heapGraphPDFs;
    
    @Parameter(description = CAPSULE_TEMPLATES_DESCRIPTION)
    public List<String> capsuleTemplates = new ArrayList<>();
}
