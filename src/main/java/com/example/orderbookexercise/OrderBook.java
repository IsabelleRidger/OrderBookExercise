package com.example.orderbookexercise;

import java.io.PrintStream;
import java.util.Collections;
import java.util.ArrayList;
import com.google.common.base.Strings;


/**
 * Representation of an order book. Order books maintain a list of orders to record the
 * interest of buyers and sellers for a particular financial instrument. An order book
 * has two for buyers and sellers, and orders for each side are tracked independently.
 */
public class OrderBook {
    private final ArrayList<Order> bids;
    private final ArrayList<Order> asks;
    private final int level;

    public OrderBook(int level) {
        bids = new ArrayList<>();
        asks = new ArrayList<>();
        this.level = level;
    }

    /**
     * Attempts to match an ask against all available bids. If one exists, process the transaction
     * and remove all fulfilled bids. If it's not possible to fulfill the ask, it must be added
     * to the list of ask and the list be re-sorted.
     */
    public void processAsk(Order order) {
        for (Order bid : bids) {
            if (order.getVolume() != 0 && bid.getPrice() > order.getPrice()) {
                double contractVolume = Math.min(order.getVolume(), bid.getVolume());
                order.decreaseVolume(contractVolume);
                bid.decreaseVolume(contractVolume);
            }
        }
        bids.removeIf(o -> o.getVolume() == 0L);
        updateOrderList(order, asks);
    }


    /**
     * Attempts to match a bid against all available asks. If one exists, process the transaction
     * and remove all fulfilled asks. If it's not possible to fulfill the bid, it must be added
     * to the list of bids and the list be re-sorted.
     */
    public void processBid(Order order) {
        for (Order ask : asks) {
            if (order.getVolume() != 0 && ask.getPrice() < order.getPrice()) {
                double contractVolume = Math.min(order.getVolume(), ask.getVolume());
                order.decreaseVolume(contractVolume);
                ask.decreaseVolume(contractVolume);
            }
        }
        asks.removeIf(o -> o.getVolume() == 0L);
        updateOrderList(order, bids);
    }

    private void updateOrderList(Order order, ArrayList<Order> orders) {
        if (order.getVolume() > 0) {
            orders.add(order);
            orders.sort(Collections.reverseOrder());
        }
    }


    /**
     * Display order book up to a maximum number of levels in human-readable format.
     * Bids are displayed on LHS and asks on the RHS. With each order displayed as volume@price
     */
    public void printOrderBook(PrintStream out) {
        int lineWidth = 60;
        int columnWidth = lineWidth/2;

        out.println(Strings.padStart("Bids -", columnWidth, ' ') + Strings.padEnd("- Asks", columnWidth, ' '));
        out.println(Strings.repeat("=", lineWidth));

        int rows = Math.min(this.level, Math.max(bids.size(), asks.size()));
        for (int i = 0; i < rows; i++) {
            out.println(
                    Strings.padStart(getOrderAsString(bids, i) + " -", columnWidth, ' ') +
                            Strings.padEnd("- " + getOrderAsString(asks, i), columnWidth, ' '));
        }
    }

    private String getOrderAsString(ArrayList<Order> orders, int index) {
        String orderAsString = "";
        if (index >= 0 && index < orders.size()) {
            orderAsString = orders.get(index).toString();
        }
        return orderAsString;
    }
}

