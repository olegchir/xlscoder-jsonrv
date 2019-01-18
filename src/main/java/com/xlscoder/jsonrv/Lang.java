package com.xlscoder.jsonrv;

import org.apache.commons.cli.CommandLine;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class Lang {
    public static String expandPathSimple(String path) {
        return path.replaceFirst("^~", System.getProperty("user.home"));
    }
    public static String expandPathUsingBash(String path) {
        try {
            String command = "ls -d " + path;
            Process shellExec = Runtime.getRuntime().exec(
                    new String[]{"bash", "-c", command});
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(shellExec.getInputStream()));
            String expandedPath = reader.readLine();

            // Only return a new value if expansion worked.
            // We're reading from stdin. If there was a problem, it was written
            // to stderr and our result will be null.
            if (expandedPath != null) {
                path = expandedPath;
            }
        } catch (java.io.IOException ex) {
            // Just consider it unexpandable and return original path.
        }

        return path;
    }

    public static String filenameFromParameter(CommandLine line, String param, String meaning, boolean fileShouldExist) {
        String result = line.getOptionValue(param);
        if (fileShouldExist) {
            result = Lang.expandPathUsingBash(result);
        } else {
            result = Lang.expandPathSimple(result);
        }

        if (!Paths.get(result).toFile().exists()) {
            if (fileShouldExist) {
                System.out.println(String.format("Parameter \"%s\" (--%s) does not exist. This is totally fatal, sorry. The program will exit right now.", meaning, param));
                System.exit(0);
            } else {
                System.out.println(String.format("\"%s\" (--%s) does not exist. It's ok for now.", meaning, param));
            }
        } else {
            System.out.println(String.format("\"%s\" (--%s) detected.", meaning, param));
        }
        return result;
    }
}
