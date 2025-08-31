grammar Hoodlum;

// A minimal stock trading DSL
// Example program:
//   BUY 100 AAPL @ 175.50;
//   SELL 50 TSLA @ 240.0 if price > 235;
//   LET risk = 0.02;
//   IF cash < 1000 THEN DEPOSIT 5000;
//   PRINT portfolio;

program: (statement ';')* EOF;

statement
  : order                   #orderStmt
  | letStmt                 #letStmtStmt
  | ifStmt                  #ifStmtStmt
  | printStmt               #printStmtStmt
  | depositStmt             #depositStmtStmt
  ;

order
  : (BUY|SELL) INT SYMBOL ('@' amount=NUMBER)? (IF expr)?
  ;

letStmt: LET ID '=' expr;

ifStmt: IF expr THEN statement;

printStmt: PRINT (portfolioKw=PORTFOLIO | expr);

depositStmt: DEPOSIT (NUMBER | INT);

expr
  : '(' expr ')'
  | left=expr op=('*'|'/') right=expr
  | left=expr op=('+'|'-') right=expr
  | left=expr op=('>'|'<'|'>='|'<='|'=='|'!=') right=expr
  | NUMBER
  | INT
  | ID
  | CASH
  ;

BUY: 'BUY' | 'buy';
SELL: 'SELL' | 'sell';
LET: 'LET' | 'let';
IF: 'IF' | 'if';
THEN: 'THEN' | 'then';
PRINT: 'PRINT' | 'print';
PORTFOLIO: 'portfolio' | 'PORTFOLIO';
DEPOSIT: 'DEPOSIT' | 'deposit';
CASH: 'cash' | 'CASH';

SYMBOL: [A-Z]+;
ID: [a-zA-Z_][a-zA-Z_0-9]*;
fragment DIGIT: [0-9];
INT: DIGIT+;
NUMBER: DIGIT+ ('.' DIGIT+)?;
WS: [ \t\r\n]+ -> skip;
COMMENT: '//' ~[\r\n]* -> skip;
