package org.paninij.soter;

import org.paninij.soter.cli.CLIArguments;
import org.paninij.soter.cli.SoterCommand;

import com.beust.jcommander.JCommander;


public class Main
{
    public static void main(String[] args) throws Exception
    {
        CLIArguments cliArgs = new CLIArguments();
        new JCommander(cliArgs, args);
        SoterCommand cmd = new SoterCommand(cliArgs);
        cmd.perform();
    }
}
