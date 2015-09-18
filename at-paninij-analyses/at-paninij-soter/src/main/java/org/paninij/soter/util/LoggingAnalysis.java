package org.paninij.soter.util;

import javax.json.JsonObject;

public abstract class LoggingAnalysis extends Analysis
{
    @Override
    public void perform()
    {
        if (hasBeenPerformed == false) {
            super.perform();
            logJsonResults();
        }
    }
   
    public abstract JsonObject getJsonResults();

    public abstract String getJsonResultsString();

    public void logJsonResults()
    {
        String analysisName = getClass().getSimpleName();
        Log.logAnalysis(analysisName, getJsonResultsLogFileName(), getJsonResultsString(), false);
    }
    
    protected abstract String getJsonResultsLogFileName();
        

}
