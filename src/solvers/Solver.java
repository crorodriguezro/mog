package solvers;

import java.util.List;
import model.Job;
import model.Schedule;

public interface Solver {
  List<Job> solve(Schedule schedule);
}
