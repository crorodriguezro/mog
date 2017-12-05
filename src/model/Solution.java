package model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Solution {
  private List<SimpleJob> sequence;
  private int cMax;
  private int twst;

  public Solution(List<SimpleJob> sequence, int cMax, int twst) {
    this.sequence = sequence;
    this.cMax = cMax;
    this.twst = twst;
  }

  public List<SimpleJob> getSequence() {
    return sequence;
  }

  public int getcMax() {
    return cMax;
  }

  public int getTwst() {
    return twst;
  }

  @Override
  public String toString() {
    return "Solution{" +
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
    int[] intSequence = sequence.stream().mapToInt(SimpleJob::getId).toArray();
    int sequenceHashCode = Arrays.hashCode(intSequence);
    return Objects.hash(sequenceHashCode, cMax, twst);
  }
}
