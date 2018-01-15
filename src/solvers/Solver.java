package solvers;

import java.util.List;

import model.Activity;
import model.Schedule;

public interface Solver {
  List<Activity> solve(Schedule schedule);
}
