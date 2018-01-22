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
  private int cMax;
  private double twst;
  private List<Solution> dominatedBy;
  private double dominatedByDistance[];
  private BigDecimal fitness;

  public Solution(List<Activity> sequence, int cMax, double twst) {
    this.sequence = sequence;
    this.cMax = cMax;
    this.twst = twst;
  }

  public List<Activity> getSequence() {
    return sequence;
  }

  public int getcMax() {
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

    if(cMax != solution.cMax || twst != solution.twst) {
      return false;
    }

    for (int i = 0; i < sequence.size(); i++) {
      if(sequence.get(i).getId() != solution.sequence.get(i).getId()){
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int[] intSequence = sequence.stream().mapToInt(Activity::getId).toArray();
    int sequenceHashCode = Arrays.hashCode(intSequence);
    return Objects.hash(sequenceHashCode, cMax, twst);
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
}
