package com.xlscoder.jsonrv;

import org.apache.commons.cli.*;

public class POpts {
    private String schemaFile;
    private String jsonFile;
    private String excelFile;
    private String outputJSONFile;

    public String getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(String excelFile) {
        this.excelFile = excelFile;
    }

    public String getSchemaFile() {
        return schemaFile;
    }

    public void setSchemaFile(String schemaFile) {
        this.schemaFile = schemaFile;
    }

    public String getJsonFile() {
        return jsonFile;
    }

    public void setJsonFile(String jsonFile) {
        this.jsonFile = jsonFile;
    }

    public String getOutputJSONFile() {
        return outputJSONFile;
    }

    public void setOutputJSONFile(String outputJSONFile) {
        this.outputJSONFile = outputJSONFile;
    }

    public POpts(String[] args) {
        Options options = new Options();
        options.addOption("s", "schema",true,
                "CSV schema file. Line: filename,conferenceName,list,of,personal,data,fields");
        options.addOption("j", "json",true,
                "JSON file.");
        options.addOption("e", "excel",true,
                "Directory with Excel files.");
        options.addOption("o", "out",true,
                "Output JSON file.");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse( options, args );
            schemaFile = Lang.filenameFromParameter(line, "schema", "Schema file", true);
            jsonFile = Lang.filenameFromParameter(line, "json", "JSON file", true);
            excelFile = Lang.filenameFromParameter(line, "excel", "Directory with Excel files", true);
            outputJSONFile = Lang.filenameFromParameter(line, "out", "Output JSON file", false);
        }
        catch( ParseException exp ) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        }
    }
}
