package project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Esta clase describe cada actividad con todas sus caracteristicas, id, duracion, tiempo de inicio, tiempo de finalizacion,
 * recursos, peso, sucesores y predecesores
 */
public class Activity {
    private int id;
    private int duration;
    private int startTime;
    private int finishTime;
    private int[] resources;
    private int weight;
    private List<Activity> predecessors = new ArrayList<>();
    private List<Activity> successors = new ArrayList<>();
    public Activity(int id) {
        this.id = id;
//        weight = random.nextInt(1000);
    }
    private static Random random = new Random();

    /**
     *Constructor de la actividad
     * @param id Identificacion de la actividad
     * @param duration Duracion de la actividad
     * @param startTime Tiempo de inicio de la actividad
     * @param resources Recursos para la actividad
     */
    public Activity(int id, int duration, int startTime, int[] resources) {
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        this.resources = resources;
//        weight = random.nextInt(1000);
    }

    /**
     *Contructor de la actividad
     * @param activity Retorna la actividad con la informacion de duracion
     *                 tiempo de inicio, tiempo final, recursos y peso.
     */
    public Activity(Activity activity) {
        this.id = activity.id;
        this.duration = activity.duration;
        this.startTime = activity.startTime;
        this.finishTime = activity.finishTime;
        this.resources = activity.resources;
        this.weight = activity.weight;
    }

    /**
     *Metodo tiempo
     * @param t El tiempo que se va a utilizar para los tiempo de inicio y fin
     *          de las actividades
     */
    public void start(int t) {
        this.startTime = t;
        this.finishTime = t + duration;
    }

    /**
     *Metodo del tiempo final
     * @return Se obtiene el tiempo final de las actividades
     */
    public int getFinishTime() {
        return finishTime;
    }

    /**
     *Metodo del ID
     * @return Se obtiene el ID de la actividad
     */
    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    /**
     *Metodo obtener duracion
     * @return Se obtiene la duracion de la actividad
     */
    public int getDuration() {
        return duration;
    }

    /**
     *Metodo asignar duracion
     * @param duration Se modifican o asignan las duraciones de las actividades
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     *Metodo obtener peso
     * @return Se obtiene el peso de la actividad
     */
    public int getWeight() {
        return weight;
    }

    /**
     *Metodo obtener recursos
     * @return Se obtienen los recursos de la actividad
     */
    public int[] getResources() {
        return resources;
    }

    /**
     *Metodo asignar recursos
     * @param resources Se asignan lo recursos a la actividad
     */
    public void setResources(int[] resources) {
        this.resources = resources;
    }

    /**
     *Metodo obtener predecesores
     * @return Se obtienen los predecesores de las actividades
     */
    public List<Activity> getPredecessors() {
        return predecessors;
    }

    public List<Activity> getSuccessors() {
        return successors;
    }

    /**
     *Metodo adiciona sucesores
     * @param successor Se adicionan los sucesores a la actividad
     */
    public void addSuccessor(Activity successor) {
        successors.add(successor);
        successor.predecessors.add(this);
    }

    /**
     *Metodo asignar tiempo final
     * @param finishTime Se le asigna el tiempo final a la actividad
     */
    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    /**
     *Metodo remover predecesor
     * @param idPredecessor Se elimina el predecesor para no generar problemas
     *                      con la secuencia una vez fue asignada la actividad.
     */
    public void removePredecessor(int idPredecessor) {
        for (int i = 0; i < predecessors.size(); i++) {
            if (predecessors.get(i).getId() == idPredecessor) {
                predecessors.remove(predecessors.get(i));
            }
        }
    }

    /**
     *Metodo obtener tiempo inicial
     * @return Se obtiene el tiempo inicial de la actividad
     */
    public int getStartTime() {
        return startTime;
    }

    /**
     *Metodo asignar tiempo de inicio
     * @param startTime Se asigna el tiempo de inicio a la actividad
     */
    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    /**
     *Metodo asignar peso
     * @param weight Se asgina el peso a la actividad
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     *Metodo para retornar la informacion del tiempo inicial y tiempo final
     * @return Retorna en pantalla los tiempo inicial y final de la actividad
     */
    @Override
    public String toString() {
        return "Activity{" +
            "id=" + id +
            ", startTime=" + startTime +
            ", finishTime=" + finishTime +
            '}';
    }

    /**
     *Metodo de comparacion de dos objetos bajo criterios
     * @param o Dos objetos
     * @return Retorna verdadero o falso si los objetos son iguales o no
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Activity activity = (Activity) o;
        return id == activity.id;
    }

    /**
     *Metodo para comparar dos objetos mediante el ID obtenido por el hash
     * @return Los hash de los objetos para ser comparados
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
