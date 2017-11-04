package model;

/**
 * Defines resource in a project. Resource can be assigned to task if it has
 * required skill at no lower than required level. It is also defined by salary.
 * To make design easier, resource is also described by finish field - the time
 * when resource finished its last assigned task.
 */
public class Resource implements Cloneable
{

    private int finish;
    private String resourceType;

    public Resource(int finish, String resourceType) {
        this.finish = finish;
        this.resourceType = resourceType;
    }

    public Resource(String resourceType) {
        this.resourceType = resourceType;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Compare two resources.
     *
     * @param r resource to compare to
     * @return true if this resource is equal to resource r
     */
    @Override
    public boolean equals(Object r) {
        if (!(r instanceof Resource)) {
            return false;
        }
        Resource resource = (Resource) r;
        return resourceType.equals(resource.resourceType);
    }

}
