package validator;

import project.Activity;
import project.Resource;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Esta clase valida las secuencias "S*" que se obtienen, basado en las restricciones que presenta cada
 * actividad, se deben cumplir las restricciones de precedencia y de recursos.
 */

public class Validator {
  private List<Activity> Activities;
  private List<Resource> resources;
  private List<Activity> sequence;
  private List<Activity> activitiesInProgresses;
  private int worldTime;

  /**
   * Contructor validador
   * @param resources Recursos
   * @param sequence Secuencia a validar
   */
  public Validator(Resource[] resources, List<Activity> sequence) {
    this.resources = Arrays.stream(resources)
        .map(Resource::new)
        .collect(Collectors.toList());
    this.sequence = sequence;
    this.activitiesInProgresses = new ArrayList<>();
    this.worldTime = 0;
  }

  /**
   *Metodo para validar las secuencias generadas
   * @return Es valida o no la nueva secuencia basado en las restricciones de
   * precedencia, los recursos y los tiempos de inicio.
   */
  public List<Activity> validate() {
    Activities = new ArrayList<>();
    List<Integer> ids = sequence.stream()
            .map(Activity::getId)
            .collect(Collectors.toList());

    Set<Integer> duplicatedActivities = ids.stream().filter(i -> Collections.frequency(ids, i) >1)
            .collect(Collectors.toSet());

    if(duplicatedActivities.size() > 0) {
      throw new RuntimeException("La secuencia tiene actividades duplicadas");
    }

    while (sequence.size() > 0) {
      if (sequence.get(0).getStartTime() != -1) {
        List<Activity> test = sequence.stream()
            .filter(activity -> {
              return sequence.get(0).getStartTime() == activity.getStartTime()
                  && activity.getStartTime() >= worldTime;
            })
            .collect(Collectors.toList());
        for (Activity activity : test) {
          startActivity(activity);
        }
        pushWorld();
      } else {
        break;
      }
    }
    sequence.forEach(activity -> {
      activity.setStartTime(-1);
      activity.setFinishTime(-1);
    });

    while (sequence.size() > 0) {
      List<Activity> doableActivities = getDoableActivitiesByPredecessors(sequence).stream()
          .filter(doableActivity -> activitiesInProgresses.stream()
              .noneMatch(jip -> jip.getId() == doableActivity.getId()))
          .collect(Collectors.toList());

      List<Activity> sequenceWithoutactivitiesInProgresses = sequence.stream()
          .filter(doableActivity -> activitiesInProgresses.stream()
              .noneMatch(jip -> jip.getId() == doableActivity.getId()))
          .collect(Collectors.toList());

      boolean hasPredecessorsInProgress = false;
      for (int i = 0; i < doableActivities.size(); i++) {
        if (doableActivities.get(i).getId() != sequenceWithoutactivitiesInProgresses.get(i).getId()) {
          List<Activity> nextActivityPredecessors = sequenceWithoutactivitiesInProgresses.get(i).getPredecessors();
          int predecessorsInProgress = nextActivityPredecessors.stream()
              .filter(activitiesInProgresses::contains)
              .toArray().length;

          if( predecessorsInProgress > 0){
            hasPredecessorsInProgress = true;
          } else {
            if (i < 1) {
              throw new RuntimeException("No sirve la secuencia");
            }
          }
          doableActivities = doableActivities.subList(0, i);
        }
      }
      if(!hasPredecessorsInProgress){
        doableActivities = getDoableActivitiesByPredecessorsAndResources(doableActivities);
        while (doableActivities.size() > 0) {
          Activity nextActivity = doableActivities.get(0);
          startActivity(nextActivity);
          doableActivities = getDoableActivitiesByPredecessorsAndResources(doableActivities);
          doableActivities.remove(nextActivity);
        }
        if (activitiesInProgresses.size() == 0) {
          System.out.println();
        }
      }
      pushWorld();
    }
    return Activities;
  }

  /**
   * Metodo que obtiene las actividades que se pueden realizar de acuerdo al predecesor
   * @param activities Actividades
   * @return Listado de actividades validas
   */
  List<Activity> getDoableActivitiesByPredecessors(Collection<Activity> activities) {
    return activities.stream()
        .filter(activity -> activity.getPredecessors().size() == 0)
        .collect(Collectors.toList());
  }

  /**
   *Metodo que obtiene las actividades que se pueden realizar de acuerdo al predecesor
   * y a los recursos
   * @param activities Actividades
   * @return Listado de actividades validas
   */
  List<Activity> getDoableActivitiesByPredecessorsAndResources(Collection<Activity> activities) {
    return activities.stream()
        .filter(activity -> activity.getPredecessors().size() == 0)
        .filter(availableResourcesPredicate)
        .collect(Collectors.toList());
  }

  /**
   *Validador de la cantidad de recursos disponibles para las actividades
   */
  Predicate<Activity> availableResourcesPredicate = activity -> {
    int[] activityResources = activity.getResources();
    for (int j = 0; j < activityResources.length; j++) {
      if (activityResources[j] > resources.get(j).getAmount()) {
        return false;
      }
    }
    return true;
  };

  /**
   * Validador de los predecesores de las actividades
   * @param activity Actividades con sus predecesores
   */
  void startActivity(Activity activity) {
//    System.out.println("StartingActivity: " + activity.getId());
    activity.start(worldTime);
    activitiesInProgresses.add(activity);
    Activities.add(new Activity(activity));
    if (activity.getPredecessors().size() > 0) {
      throw new RuntimeException("El trabajo tiene predecesores");
    }
    /**
     * Validador de recursos disponibles para una actividad
     */
    int[] activityResources = activity.getResources();
    for (int i = 0; i < activityResources.length; i++) {
      int resourceAmount = resources.get(i).getAmount() - activityResources[i];
      if (resourceAmount < 0) {
        throw new RuntimeException("No hay recursos");
      }
      resources.get(i).setAmount(resourceAmount);
    }
  }

  /**
   *Metodo remover predecesores para las actividades asigandas
   * @param completedActivity Actividades finalizadas
   * @return Las actividades finalizadas sin predecesores
   */
  static List<Activity> removePredecessor(Activity completedActivity) {
    for (Activity successor : completedActivity.getSuccessors()) {
      successor.removePredecessor(completedActivity.getId());
    }
    return completedActivity.getSuccessors();
  }

  /**
   *Validador de actividades en progreso
   */
  private void pushWorld() {
    Optional<Activity> nextFinishingActivity = activitiesInProgresses.stream()
        .filter(activity -> activity.getFinishTime() != -1)
        .min(Comparator.comparingInt(Activity::getFinishTime));

    if (nextFinishingActivity.isPresent()) {
      worldTime = nextFinishingActivity.get().getFinishTime();
    } else {
      throw new RuntimeException("No sirve la secuencia");
    }
    List<Activity> finishedActivities = activitiesInProgresses.stream()
        .filter(activity -> activity.getFinishTime() == worldTime)
        .collect(Collectors.toList());
    //finishedActivities.forEach(completeActivity);
    for (Activity activity : finishedActivities) {
      finishActivity(activity);
    }
  }

  /**
   *Metodo para actividad finalizada
   * @param activity Actividad finalizada, tiempo final y recursos finales
   */
  private void finishActivity(Activity activity) {
    int[] activityResources = activity.getResources();
    for (int i = 0; i < activityResources.length; i++) {
      int resourceAmount = resources.get(i).getAmount() + activityResources[i];
      resources.get(i).setAmount(resourceAmount);
    }
    activity.setFinishTime(worldTime);
    sequence.remove(activity);
    activitiesInProgresses.remove(activity);
    removePredecessor(activity);
  }
}
