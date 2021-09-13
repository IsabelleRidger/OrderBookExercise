package com.example.orderbookexercise;

import java.util.Comparator;

/**
 * Representation of an order. Can be either an ask or a bid.
 */
public class Order implements Comparator<Order>, Comparable<Order> {

    private double volume;
    private final double price;

    public Order(double price, double volume) {
        this.price = price;
        this.volume = volume;
    }

    public double getPrice() {
        return price;
    }

    public double getVolume() {
        return volume;
    }

    public void decreaseVolume(double amount) {
        this.volume = this.volume - amount;
    }

    @Override
    public int compare(Order o1, Order o2) {
        return (int) (o2.getPrice() - o1.getPrice());
    }

    @Override
    public int compareTo(Order o1) {
        return Double.compare(this.price, o1.price);
    }

    @Override
    public String toString() {
        return String.format("%.4f@%.2f", this.getVolume(), this.getPrice());
    }
}
