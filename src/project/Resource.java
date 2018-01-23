package project;


/**
 * Esta clase describe los recursos, caracteristicas, la cantidad y el tipo
 */

/**
 * Se definen los tipos de recusos y la letra por la cual seran identificados
 */
public class Resource implements Cloneable
{
    public static final String RENEWABLE = "R";
    public static final String NON_RENEWABLE = "N";
    public static final String DOUBLY_CONSTRAINED = "D";

    private int amount;
    private String resourceType;


    /**
     *Constructor de recursos
     * @param amount Cantidad de recursos
     * @param resourceType Tipo de recursos
     */
    public Resource(int amount, String resourceType)
    {
        this.amount = amount;
        this.resourceType = resourceType;
    }

    /**
     *Constructor de recursos
     * @param resource Recursos
     */
    public Resource(Resource resource)
    {
        this.amount = resource.amount;
        this.resourceType = resource.resourceType;
    }

    /**
     *Constructor de tipo de recursos
     * @param resourceType Tipo de recursos
     */
    public Resource(String resourceType)
    {
        this.resourceType = resourceType;
    }

    /**
     *Metodo para obtener la cantidad de recursos
     * @return Cantidad de recursos
     */
    public int getAmount()
    {
        return amount;
    }

    /**
     *Metodo para asignar la cantidad de recursos
     * @param amount Cantidad de recursos
     */
    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    /**
     *Metodo para obtener el tipo de recursos
     * @return Tipo de recursos
     */
    public String getResourceType()
    {
        return resourceType;
    }

    /**
     *Metodo para asignar el tipo de recursos
     * @param resourceType Tipo de recursos
     */
    public void setResourceType(String resourceType)
    {
        this.resourceType = resourceType;
    }

    /**
     *Metodo para representar los recursos en texto
     * @return Representacion de los recursos
     */
    @Override
    public String toString() {
        return "Resource{" +
            "amount=" + amount +
            ", resourceType='" + resourceType + '\'' +
            '}';
    }
}
