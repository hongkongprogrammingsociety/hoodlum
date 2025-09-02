package com.hoodlum;

import com.hoodlum.antlr.HoodlumBaseVisitor;
import com.hoodlum.antlr.HoodlumParser;

public class Evaluator extends HoodlumBaseVisitor<Double> {
	private final Runtime rt;

	public Evaluator(Runtime rt) {
		this.rt = rt;
	}

	@Override
	public Double visitOrderStmt(HoodlumParser.OrderStmtContext ctx) {
		var o = ctx.order();
		String symbol = o.SYMBOL().getText();
		int qty = Integer.parseInt(o.INT().getText());
		Double price = o.amount != null ? Double.parseDouble(o.amount.getText()) : null;
		boolean isBuy = o.BUY() != null;
		if (o.IF() != null) {
			double cond = visit(o.expr());
			if (cond == 0)
				return 0.0;
		}
		if (isBuy)
			rt.buy(symbol, qty, price);
		else
			rt.sell(symbol, qty, price);
		return 0.0;
	}

	@Override
	public Double visitLetStmtStmt(HoodlumParser.LetStmtStmtContext ctx) {
		String id = ctx.letStmt().ID().getText();
		double v = visit(ctx.letStmt().expr());
		rt.setVar(id, v);
		return 0.0;
	}

	@Override
	public Double visitIfStmtStmt(HoodlumParser.IfStmtStmtContext ctx) {
		double cond = visit(ctx.ifStmt().expr());
		if (cond != 0)
			visit(ctx.ifStmt().statement());
		return 0.0;
	}

	@Override
	public Double visitPrintStmtStmt(HoodlumParser.PrintStmtStmtContext ctx) {
		var ps = ctx.printStmt();
		if (ps.portfolioKw != null) {
			rt.print("portfolio");
		} else {
			double v = visit(ps.expr());
			rt.print(v);
		}
		return 0.0;
	}

	@Override
	public Double visitForStmtStmt(HoodlumParser.ForStmtStmtContext ctx) {
		System.out.println("Evaluator::visitForStmtStmt");
		var forStmt = ctx.forStmt();
		String varName = forStmt.ID().getText();
		double start = visit(forStmt.expr(0));
		double end = visit(forStmt.expr(1));
		for (double i = start; i <= end; i++) {
			rt.setVar(varName, i);
			if (forStmt.forBody() != null) {
				for (var stmtCtx : forStmt.forBody().statement()) {
					visit(stmtCtx);
				}
			}
		}
		return 0.0;
	}

	@Override
	public Double visitDepositStmtStmt(HoodlumParser.DepositStmtStmtContext ctx) {
		var d = ctx.depositStmt();
		String num = d.NUMBER() != null ? d.NUMBER().getText() : d.INT().getText();
		double amt = Double.parseDouble(num);
		rt.deposit(amt);
		return 0.0;
	}

	// Expressions
	@Override
	public Double visitExpr(HoodlumParser.ExprContext ctx) {
		System.out.println("visitExpr=" + ctx.getText());
		if (ctx.NUMBER() != null)
			return Double.parseDouble(ctx.NUMBER().getText());
		if (ctx.INT() != null)
			return Double.parseDouble(ctx.INT().getText());
		if (ctx.ID() != null)
			return rt.getVar(ctx.ID().getText());
		if (ctx.CASH() != null)
			return rt.getCash();
		if (ctx.getChildCount() == 3) {
			if ("(".equals(ctx.getChild(0).getText()))
				return visit(ctx.expr(0));
			double l = visit(ctx.left);
			double r = visit(ctx.right);
			String op = ctx.op.getText();
			return switch (op) {
				case "+" -> l + r;
				case "-" -> l - r;
				case "*" -> l * r;
				case "/" -> l / r;
				case ">" -> l > r ? 1.0 : 0.0;
				case "<" -> l < r ? 1.0 : 0.0;
				case ">=" -> l >= r ? 1.0 : 0.0;
				case "<=" -> l <= r ? 1.0 : 0.0;
				case "==" -> l == r ? 1.0 : 0.0;
				case "!=" -> l != r ? 1.0 : 0.0;
				default -> throw new RuntimeException("Unknown op: " + op);
			};
		}
		return super.visitExpr(ctx);
	}
}
