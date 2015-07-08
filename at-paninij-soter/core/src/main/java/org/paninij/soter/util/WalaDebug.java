package org.paninij.soter.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.graph.Graph;
import com.ibm.wala.viz.DotUtil;

public class WalaDebug
{
    private final static String DOT_EXECUTABLE = "/usr/local/bin/dot";
    private final static String DOT_TEMPORARY_FILE = "callgraph_temp_file.dot";
    
	public static <T> void makeGraphFile(Graph<T> graph, String filename)
	{

	    try
	    {
	        DotUtil.dotify(graph, null, DOT_TEMPORARY_FILE, filename, DOT_EXECUTABLE);
	        Path path = FileSystems.getDefault().getPath(DOT_TEMPORARY_FILE);
	        Files.delete(path);
	    }
	    catch (WalaException ex)
	    {
	        String msg = "Failed to dotify the given graph.";
	        throw new IllegalArgumentException(msg);
	    }
	    catch (IOException ex)
	    {
	        String msg = "Could not delete the `dot` temporary file.";
	        throw new IllegalArgumentException(msg);
	    }
	}
}
