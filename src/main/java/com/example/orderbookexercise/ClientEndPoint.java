package com.example.orderbookexercise;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Representation of a client endpoint for a given websocket container. Upon receiving a message
 * from websocket, the message string is transformed to a list of new orders that are processed
 * and managed by the order book.
 */
@ClientEndpoint
public class ClientEndPoint {

    private Session userSession = null;
    private OrderBook orderBook;

    public ClientEndPoint(String endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            System.out.println("Establishing connection...");
            container.connectToServer(this, URI.create(endpointURI));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Determine whether message received is either a snapshot or l2update, and process
     * message accordingly and print updated order book to System.out.
     */
    @OnMessage
    public void onMessage(String message, boolean messageNotNull) {
        if (messageNotNull) {
            JSONObject jsonMsg = new JSONObject(message);
            String type = (String) jsonMsg.get("type");
            switch (type) {
                case "snapshot" -> {
                    processSnapshot(jsonMsg);
                    this.orderBook.printOrderBook(System.out);
                }
                case "l2update" -> {
                    processL2Update(jsonMsg);
                    this.orderBook.printOrderBook(System.out);
                }
                case "error" -> throw new InvalidRequestException((String) jsonMsg.get("reason"));
            }

        }
    }

    @OnOpen
    public void newConnection(Session session) {
        this.userSession = session;
        this.orderBook = new OrderBook(10);
        System.out.println("Successfully connected.");
    }

    @OnClose
    public void disconnection() {
        this.userSession = null;
        System.out.println("The connection has been closed.");
    }

    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    private void processSnapshot(JSONObject jsonMsg) {
        for (Object ask : (JSONArray) jsonMsg.get("asks")) {
            this.orderBook.processAsk(createOrderFromEntry((JSONArray) ask));
        }
        for (Object bid : (JSONArray) jsonMsg.get("bids")) {
            this.orderBook.processBid(createOrderFromEntry((JSONArray) bid));
        }
    }

    private void processL2Update(JSONObject jsonMsg) {
        for (Object change : (JSONArray) jsonMsg.get("changes")) {
            JSONArray changeEntry = (JSONArray) change;
            Object entryType = changeEntry.remove(0);
            if (entryType.equals("buy")){
                this.orderBook.processBid(createOrderFromEntry(changeEntry));
            } else {
                this.orderBook.processAsk(createOrderFromEntry(changeEntry));
            }
        }
    }

    private Order createOrderFromEntry(JSONArray entry) {
        double price = Double.parseDouble(entry.get(0).toString());
        double volume = Double.parseDouble(entry.get(1).toString());
        return new Order(price, volume);
    }

    @OnError
    public void onError(Throwable throwable) throws IOException {
        System.out.println(throwable.getMessage());
        userSession.close();
        System.exit(1);
    }

}
