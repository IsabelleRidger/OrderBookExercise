package com.example.orderbookexercise;

import org.json.JSONArray;
import org.json.JSONObject;
import sun.misc.Signal;
import java.util.Scanner;

/**
 * Driver class to run the order book application. The user must initially supply the
 * product ID for a financial instrument, then the application established a connection
 * to a websocket belonging to Coinbase Pro API, where the given product ID is wrapped
 * in a request string and transmitted. All incoming responses to the request
 * are processed into an order book that is displayed to the console.
 */
public class OrderBookApplication {

    private final static String uri = "wss://ws-feed-public.sandbox.pro.coinbase.com/";

    public static void main(String[] args) throws InterruptedException {

        Signal.handle(new Signal("INT"), sig -> {
            System.out.println( "\nShutting down application.");
            System.exit(0);
        });

        System.out.print("Please enter a Product ID: ");
        Scanner scanner = new Scanner(System.in);

        String product_id = "";
        if (scanner.hasNext()) {
            product_id = scanner.next();
        }

        ClientEndPoint clientEndPoint = new ClientEndPoint(uri);
        System.out.println("Fetching data for product: " + product_id);

        clientEndPoint.sendMessage(getRequestString(product_id));

        Thread.sleep(60000);
    }

    private static String getRequestString(String productID)  {
        JSONArray product_ids = new JSONArray();
        product_ids.put(productID);

        JSONObject level2 = new JSONObject();
        level2.put("name", "ticker");
        level2.put("product_ids", product_ids);

        JSONArray channels = new JSONArray();
        channels.put("level2");
        channels.put(level2);

        JSONObject request = new JSONObject();
        request.put("type", "subscribe");
        request.put("product_ids", product_ids);
        request.put("channels", channels);

        return request.toString();
    }

}
