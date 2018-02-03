package model;

import project.Activity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Se alamacena secuencias "S" obtenidas, con el Cmax y el TWST de cada una.
 */

public class Solution {
  private List<Activity> sequence;
  private double cMax;
  private double twst;
  private List<Solution> dominatedBy;
  private double dominatedByDistance[];
  private BigDecimal fitness;

  public Solution(List<Activity> sequence, double cMax, double twst) {
    this.sequence = sequence;
    this.cMax = cMax;
    this.twst = twst;
  }

  /**
   * Ingresa la secuencia "S" para obtener el TWST y el Cmax.
   * @param sequence
   * @return sequence con el Cmax y el TWST
   */
  public Solution (List<Activity> sequence) {
    int cMax = sequence.get(sequence.size() - 1).getFinishTime();
    double twst = sequence.stream()
            .mapToDouble(activity -> {
              if(activity.getStartTime() == 0) return 0;
              return (double) activity.getWeight() / activity.getStartTime();
            })
            .sum();
    this.cMax = cMax;
    this.twst = Math.round(twst * 1000) /1000d;
    this.sequence = sequence;
  }

  public List<Activity> getSequence() {
    return sequence;
  }

  public double getcMax() {
    return cMax;
  }

  public double getTwst() {
    return twst;
  }

  @Override
  public String toString() {
    return "Solution{" +
        "sequence=" + sequence +
        ", cMax=" + cMax +
        ", twst=" + twst +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Solution)) {
      return false;
    }
    Solution solution = (Solution) o;

    return cMax == solution.cMax && twst == solution.twst;
  }

  @Override
  public int hashCode() {
    return Objects.hash(cMax, twst);
  }

  public List<Solution> getDominatedBy() {
    return dominatedBy;
  }

  public void setDominatedBy(List<Solution> dominatedBy) {
    this.dominatedBy = dominatedBy;
  }

  public double[] getDominatedByDistance() {
    return dominatedByDistance;
  }

  public void setDominatedByDistance(double[] dominatedByDistance) {
    this.dominatedByDistance = dominatedByDistance;
  }

  public BigDecimal getFitness() {
    return fitness;
  }

  public void setFitness(BigDecimal fitness) {
    this.fitness = fitness;
  }

  public void printSequence() {
    for (Activity activity : sequence) {
      System.out.print(activity.getId() + " ");
    }
    System.out.println();
  }
}
