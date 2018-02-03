package performancemesures;

import model.Solution;

import java.util.*;
import java.util.stream.Collectors;

public class DistanceMetrics {
    public static double measurePerformance(List<Solution> solutions) {
        if(solutions.size() == 1) return 1;
        solutions.sort(Comparator.comparing(solution -> -solution.getTwst()));

        double cMaxMax = solutions.get(solutions.size() - 1).getcMax();
        double cMaxMin = solutions.get(0).getcMax();
        double twstMax = solutions.get(0).getTwst();
        double twstMin = solutions.get(solutions.size() - 1).getTwst();
        List<Solution> normalizedSolutions = solutions.stream()
                .map(solution -> {
                    double cMaxN = (solution.getcMax() - cMaxMin) / (cMaxMax - cMaxMin);
                    double twstN = (solution.getTwst() - twstMin) / (twstMax - twstMin);
                    return new Solution(solution.getSequence(), cMaxN, twstN);
                })
                .collect(Collectors.toList());

        double area = 0;
        double biggerCmax = normalizedSolutions.get(solutions.size() - 1).getcMax();
        for (int i = 0; i < normalizedSolutions.size() - 1; i++) {
            area += (biggerCmax - normalizedSolutions.get(i).getcMax()) * (normalizedSolutions.get(i).getTwst() - normalizedSolutions.get(i + 1).getTwst());
        }
        return area;
    }
    public static double measurePerformance(List<Solution> solutions, double cMaxMaxP, double twstMaxP) {
        if(solutions.size() == 1) return 1;
        solutions.sort(Comparator.comparing(solution -> -solution.getTwst()));

        double cMaxMax = cMaxMaxP == -1 ? solutions.get(solutions.size() - 1).getcMax() : cMaxMaxP;
        double cMaxMin = solutions.get(0).getcMax();
        double twstMax = twstMaxP == -1 ? solutions.get(0).getTwst() : twstMaxP;
        double twstMin = solutions.get(solutions.size() - 1).getTwst();
        List<Solution> normalizedSolutions = solutions.stream()
                .map(solution -> {
                    double cMaxN = (solution.getcMax() - cMaxMin) / (cMaxMax - cMaxMin);
                    double twstN = (solution.getTwst() - twstMin) / (twstMax - twstMin);
                    return new Solution(solution.getSequence(), cMaxN, twstN);
                })
                .collect(Collectors.toList());

        double area = 0;
        double biggerCmax = normalizedSolutions.get(solutions.size() - 1).getcMax();
        for (int i = 0; i < normalizedSolutions.size() - 1; i++) {
            area += (biggerCmax - normalizedSolutions.get(i).getcMax()) * (normalizedSolutions.get(i).getTwst() - normalizedSolutions.get(i + 1).getTwst());
        }
        return area;
    }
}
