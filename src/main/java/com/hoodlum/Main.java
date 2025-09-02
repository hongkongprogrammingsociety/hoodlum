//package com.hoodlum;
//
//import com.hoodlum.antlr.HoodlumLexer;
//import com.hoodlum.antlr.HoodlumParser;
//import org.antlr.v4.runtime.CharStreams;
//import org.antlr.v4.runtime.CommonTokenStream;
//
//public class Main {
//    public static void main(String[] args) throws Exception {
//        String input;
//        if (args.length == 0) {
//            input = "BUY 100 AAPL @ 175.5;\n" +
//                    "LET risk = 0.02;\n" +
//                    "IF cash < 1000 THEN DEPOSIT 5000;\n" +
//                    "PRINT portfolio;\n";
//        } else {
//            input = java.nio.file.Files.readString(java.nio.file.Path.of(args[0]));
//        }
//
//        var lexer = new HoodlumLexer(CharStreams.fromString(input));
//        var tokens = new CommonTokenStream(lexer);
//        var parser = new HoodlumParser(tokens);
//        var tree = parser.program();
//
//        var runtime = new Runtime();
//        var eval = new Evaluator(runtime);
//        eval.visit(tree);
//    }
//}
