package com.jeopardy.io;

import com.jeopardy.core.Category;
import java.io.IOException;
import java.util.List;

public interface BoardLoader {
    List<Category> load() throws IOException;
    String getSourceDescription();
}
