package com.example.youvoice;

public class Compliants {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    private String id;
    private String name;
    private String image;

    public Compliants() {
        // TODO Auto-generated constructor stub
    }

    public Compliants(String id, String name, String image) {
        super();
        this.id = id;
        this.name = name;
        this.image = image;
    }
}
