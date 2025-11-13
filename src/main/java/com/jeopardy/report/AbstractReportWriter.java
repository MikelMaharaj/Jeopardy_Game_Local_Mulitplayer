package com.jeopardy.report;

import com.jeopardy.core.GameState;
import java.io.IOException;

public abstract class AbstractReportWriter {
    public final void generate(GameState state, String outputPath) throws IOException {
        startDocument(state);
        writeTurnByTurn(state);
        writeFinalScores(state);
        finish(outputPath);
    }

    protected abstract void startDocument(GameState state) throws IOException;
    protected abstract void writeTurnByTurn(GameState state) throws IOException;
    protected abstract void writeFinalScores(GameState state) throws IOException;
    protected abstract void finish(String outputPath) throws IOException;
}
