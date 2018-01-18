package project;


/**
 * Esta clase guarda los recursos y sus caracteristicas, la cantidad y el tipo
 */
public class Resource implements Cloneable
{
    public static final String RENEWABLE = "R";
    public static final String NON_RENEWABLE = "N";
    public static final String DOUBLY_CONSTRAINED = "D";

    private int amount;
    private String resourceType;

    public Resource(int amount, String resourceType)
    {
        this.amount = amount;
        this.resourceType = resourceType;
    }

    public Resource(Resource resource)
    {
        this.amount = resource.amount;
        this.resourceType = resource.resourceType;
    }

    public Resource(String resourceType)
    {
        this.resourceType = resourceType;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public String getResourceType()
    {
        return resourceType;
    }

    public void setResourceType(String resourceType)
    {
        this.resourceType = resourceType;
    }

    @Override
    public String toString() {
        return "Resource{" +
            "amount=" + amount +
            ", resourceType='" + resourceType + '\'' +
            '}';
    }
}
