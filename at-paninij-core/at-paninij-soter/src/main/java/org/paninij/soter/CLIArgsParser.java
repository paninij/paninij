package org.paninij.soter;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

public class CLIArgsParser
{
    private static final String ANALYSIS_REPORTS_DESCRIPTION =
            "The path to the directory in which the SOTER analysis reports should be placed. "
            + "If this option is not set then no analysis reports will be generated.";
    
    private static final String CALL_GRAPH_PDFS_DESCRIPTION =
            "The path to the directory in which SOTER call graph PDFs should be placed. "
            + "If this option is not set, then no call graph PDFs will be generated.";
    
    private static final String HEAP_GRAPH_PDFS_DESCRIPTION =
            "The path to the directory in which SOTER heap graph PDFs should be placed. "
            + "If this option is not set, then no heap graph PDFs will be generated.";
    
    @Parameter
    private List<String> capsuleTemplates = new ArrayList<>();
 
    @Parameter(names = "-analysisReports", description = ANALYSIS_REPORTS_DESCRIPTION)
    public String analysisReports;

    @Parameter(names = "-callGraphPDFs", description = CALL_GRAPH_PDFS_DESCRIPTION)
    public String callGraphPDFs;

    @Parameter(names = "-heapGraphPDFs", description = HEAP_GRAPH_PDFS_DESCRIPTION)
    public String heapGraphPDFs;
}
