package com.hoodlum;

import org.junit.jupiter.api.Test;

import com.hoodlum.antlr.HoodlumLexer;
import com.hoodlum.antlr.HoodlumParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import static org.junit.jupiter.api.Assertions.*;

public class HoodlumTest {
    @Test
    void parsesAndRunsQuickstart() throws Exception {
        String src = java.nio.file.Files.readString(java.nio.file.Path.of("examples/quickstart.hlm"));
        var lexer = new HoodlumLexer(CharStreams.fromString(src));
        var tokens = new CommonTokenStream(lexer);
        var parser = new HoodlumParser(tokens);
        var tree = parser.program();
        assertEquals(0, parser.getNumberOfSyntaxErrors());

        var rt = new Runtime();
        var ev = new Evaluator(rt);
        ev.visit(tree);

        assertTrue(rt.getCash() >= 0);
    }
}
