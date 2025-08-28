# Hoodlum

Hoodlum is a tiny DSL for stock trading, built with ANTLR4 and Java.

## Build

- Requires Java 17+ and Maven 3.9+
- To build and run tests:

```
mvn -q -DskipTests=false clean test package
```

## Run

Run with an input file (see `examples/quickstart.hlm`):

```
java -jar target/hoodlum-*-SNAPSHOT.jar examples/quickstart.hlm
```

Or run with the built-in demo program (no args):

```
java -jar target/hoodlum-*-SNAPSHOT.jar
```

## Language sketch

```
DEPOSIT 2000;
LET risk = 0.02;
BUY 100 AAPL @ 175.5;
SELL 50 AAPL @ 185.0 if cash > 1000;
IF cash < 1000 THEN DEPOSIT 5000;
PRINT portfolio;
```

- Arithmetic and comparisons: `+ - * / > < >= <= == !=`
- Variables: `LET name = expr;`
- Cash variable: `cash`
- Orders: `BUY|SELL <INT> <SYMBOL> [@ <NUMBER>] [if <expr>]`
- Print: `PRINT portfolio;` or `PRINT <expr>;`

## Notes

- This is a teaching project, not investment advice.
