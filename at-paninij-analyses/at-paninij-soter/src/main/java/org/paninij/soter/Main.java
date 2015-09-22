package org.paninij.soter;

import org.paninij.soter.cli.CLIArguments;
import org.paninij.soter.cli.SoterCommand;

import com.beust.jcommander.JCommander;


public class Main
{
    public static void main(String[] args) throws Exception
    {
        CLIArguments cliArguments = new CLIArguments();
        new JCommander(cliArguments, args);
        SoterCommand cmd = new SoterCommand(cliArguments);
        cmd.perform();
    }
}
