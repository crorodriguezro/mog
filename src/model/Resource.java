package model;

public class Resource implements Cloneable
{
    private int amount;
    private String resourceType;

    public Resource(int amount, String resourceType) {
        this.amount = amount;
        this.resourceType = resourceType;
    }

    public Resource(String resourceType) {
        this.resourceType = resourceType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
