package com.hoodlum;

import java.util.*;

public class Runtime {
    public static class Position { public final String symbol; public int qty; public double price; Position(String s,int q,double p){symbol=s;qty=q;price=p;} }
    private final Map<String, Double> vars = new HashMap<>();
    private final Map<String, Position> portfolio = new LinkedHashMap<>();
    private double cash = 10000.0;

    public void deposit(double amount) { cash += amount; log("Deposited $"+amount+", cash="+cash); }

    public void buy(String symbol, int qty, Double priceOpt){
		System.out.println("buy");
        double price = priceOpt != null ? priceOpt : 0.0;
        double cost = qty * price;
        if (priceOpt != null && cash < cost) throw new RuntimeException("Insufficient cash");
        cash -= cost;
        var pos = portfolio.get(symbol);
        if (pos == null) pos = new Position(symbol, 0, 0);
        double newQty = pos.qty + qty;
        pos.price = newQty == 0 ? 0 : (pos.price * pos.qty + cost) / newQty;
        pos.qty += qty;
        portfolio.put(symbol, pos);
        log("BUY "+qty+" "+symbol+" @ "+price+" -> cash="+cash);
    }

    public void sell(String symbol, int qty, Double priceOpt){
        var pos = portfolio.get(symbol);
        if (pos == null || pos.qty < qty) throw new RuntimeException("Not enough shares to sell");
        double price = priceOpt != null ? priceOpt : pos.price;
        double proceeds = qty * price;
        pos.qty -= qty;
        cash += proceeds;
        if (pos.qty == 0) portfolio.remove(symbol); else portfolio.put(symbol, pos);
        log("SELL "+qty+" "+symbol+" @ "+price+" -> cash="+cash);
    }

    public void setVar(String id, double value){ vars.put(id, value); }
    public double getVar(String id){
        if ("cash".equalsIgnoreCase(id)) return cash;
        return Optional.ofNullable(vars.get(id)).orElseThrow(() -> new RuntimeException("Unknown variable: "+id));
    }

    public double getCash(){ return cash; }

    public void print(Object o){
        if (o instanceof String s && s.equalsIgnoreCase("portfolio")) {
            log("PORTFOLIO:");
            for (var e : portfolio.values()) {
                log(" - "+e.symbol+": qty="+e.qty+", avgPrice="+e.price);
            }
            log("Cash: "+cash);
        } else {
            log(String.valueOf(o));
        }
    }

    private void log(String s){ System.out.println(s); }
}
