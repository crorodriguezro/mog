package performancemesures;

import model.Solution;

import java.util.ArrayList;
import java.util.List;

public class DistanceMetrics {
    public static void measurePerformance(List<Solution> mogSolutions, List<Solution> spea2Solutions) {
        double distances[] = new double[mogSolutions.size() * spea2Solutions.size()];
        List<double[]> middlePoints = new ArrayList<>();
        for (int i = 0; i < mogSolutions.size(); i++) {
            for (int j = 0; j < spea2Solutions.size(); j++) {
                Solution mogSolution = mogSolutions.get(i);
                Solution spea2Solution = spea2Solutions.get(j);
                double cMaxSquare = Math.pow(mogSolution.getcMax() - spea2Solution.getcMax(), 2);
                double twstSquare = Math.pow(mogSolution.getTwst() - spea2Solution.getTwst(), 2);
                distances[i+j] = Math.pow(cMaxSquare + twstSquare, 0.5);

                double[] middlePoint = new double[2];
                middlePoint[0] = (mogSolution.getcMax() + spea2Solution.getcMax())/2;
                middlePoint[1] = (mogSolution.getTwst() + spea2Solution.getTwst())/2;
                middlePoints.add(middlePoint);
            }
        }
        //double area = area(middlePoints);
        System.out.println();
    }

    public static double area(List<double[]> middlePoints) {
        double sum = 0.0;
        for (int i = 0; i < middlePoints.size(); i++) {
            sum = sum + (middlePoints.get(i)[0] * middlePoints.get(i+1)[1]) - (middlePoints.get(i)[1]* middlePoints.get(i+1)[0]);
        }
        return 0.5 * sum;
    }

}
