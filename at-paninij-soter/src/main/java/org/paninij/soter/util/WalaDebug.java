package org.paninij.soter.util;

import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.viz.DotUtil;

public class WalaDebug
{
    private final static String DOT_EXECUTABLE = "/usr/local/bin/dot";
    private final static String DOT_TEMPORARY_FILE = "callgraph.dot";
    
	public static <T> void makeGraphFile(Graph<T> graph, String filename)
	{
	    try
	    {
	        DotUtil.dotify(graph, null, DOT_TEMPORARY_FILE, filename, DOT_EXECUTABLE);
	    }
	    catch (WalaException ex)
	    {
	        String msg = "Failed to dotify the given graph.";
	        throw new IllegalArgumentException(msg);
	    }
	}
}
