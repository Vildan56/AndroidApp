package com.example.myapplication;

import java.util.List;

public class Order {
    private String id;
    private Long timestamp;
    private List<Product> items;

    public Order() {
    }

    public Order(String id, Long timestamp, List<Product> items) {
        this.id = id;
        this.timestamp = timestamp;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Product> getItems() {
        return items;
    }

    public void setItems(List<Product> items) {
        this.items = items;
    }
}