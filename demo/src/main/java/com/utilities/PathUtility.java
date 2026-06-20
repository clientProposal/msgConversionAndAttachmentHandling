package com.utilities;

public class PathUtility {

    public final String inputPath;
    public final String outputPath;
    public final String resourcePath;
    public final String libPath;

    public PathUtility() {
        String baseDir = System.getProperty("user.dir");
        this.inputPath = baseDir + "/src/main/resources/input/";
        this.outputPath = baseDir + "/src/main/resources/output/";
        this.resourcePath = baseDir + "/Resources/";
        this.libPath = baseDir + "/lib/";
    }

    public String getPath(String pathName) {
        switch (pathName) {
            case "input":
                return inputPath;
            case "output":
                return outputPath;
            case "resource":
                return resourcePath;
            case "lib":
                return libPath;
            default:
                throw new IllegalArgumentException("Unknown path: " + pathName);
        }
    }
}