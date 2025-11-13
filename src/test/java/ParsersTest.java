package com.jeopardy;

import com.jeopardy.io.*;
import com.jeopardy.core.Category;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ParsersTest {

    @Test
    public void jsonLoaderParses() throws Exception {
        String p = Path.of("src","main","resources","board","sample_game.json").toString();
        BoardLoader loader = new JsonBoardLoader(p);
        List<Category> cats = loader.load();
        assertFalse(cats.isEmpty());
    }

    @Test
    public void xmlLoaderParses() throws Exception {
        String p = Path.of("src","main","resources","board","sample_game.xml").toString();
        BoardLoader loader = new XmlBoardLoader(p);
        List<Category> cats = loader.load();
        assertFalse(cats.isEmpty());
    }
}
