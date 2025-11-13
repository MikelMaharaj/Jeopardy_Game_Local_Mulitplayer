package com.jeopardy.io;

public class LoaderFactory {
    public static BoardLoader fromPath(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".json")) return new JsonBoardLoader(path);
        if (lower.endsWith(".xml")) return new XmlBoardLoader(path);
        if (lower.endsWith(".csv")) return new CsvBoardLoader(path);
        throw new IllegalArgumentException("Unsupported file type: " + path);
    }
}
