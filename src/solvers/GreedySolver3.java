package solvers;

import model.Job;
import model.Resource;
import model.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GreedySolver3 {

    private List<Job> availableJobs = new ArrayList<>();

    private List<Job> doableJobs = new ArrayList<>();

    private List<Job> inProgressJobs = new ArrayList<>();

    private Resource[] resources;

    private int t = 1;

    List<Integer> path = new ArrayList<>();

    public List<Integer> solve(Schedule schedule) {
        resources = schedule.getResources();
        availableJobs = getDoableJobs(schedule.getJobs()[0].getSuccessors());
        doableJobs = availableJobs;
        while (availableJobs.size() > 0) {
            while (doableJobs.size() > 0) {
                startJob(selectOneJob(doableJobs));
            }
            lapse();
        }
        return path;
    }

    private List<Job> getDoableJobs(List<Job> jobs) {
        List<Job> doableJobs = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            int[] jobResources = jobs.get(i).getResources();
            boolean doable = true;
            for (int j = 0; j < jobResources.length; j++) {
                if (jobResources[j] > resources[j].getAmount()) {
                    doable = false;
                }
            }
            if (doable) {
                doableJobs.add(jobs.get(i));
            }
        }
        return doableJobs;
    }

    private Job selectOneJob(List<Job> availableJobs) {
        Random random = new Random();
//        Job selected = availableJobs.get(random.nextInt(availableJobs.size()));
        Job selected = availableJobs.get(0);
        //path.add(selected.getId());
        return selected;
    }

    private void startJob(Job job) {
        System.out.println(job.getId());
        inProgressJobs.add(job);
        availableJobs.remove(job);
        job.start(t);
        int[] jobResources = job.getResources();
        for (int i = 0; i < jobResources.length; i++) {
            int resourceAmount = resources[i].getAmount() - jobResources[i];
            resources[i].setAmount(resourceAmount);
        }
        doableJobs = new ArrayList<>();
        doableJobs = getDoableJobs(availableJobs);
    }

    private void lapse() {
        int nextT = Integer.MAX_VALUE;
        Job finishedJob = null;
        for (int i = 0; i < inProgressJobs.size(); i++) {
            if (inProgressJobs.get(i).getFinishTime() < nextT) {
                nextT = inProgressJobs.get(i).getFinishTime();
                finishedJob = inProgressJobs.get(i);
            }
        }
        t = nextT;
        int[] jobResources = finishedJob.getResources();
        for (int i = 0; i < jobResources.length; i++) {
            int resourceAmount = resources[i].getAmount() + jobResources[i];
            resources[i].setAmount(resourceAmount);
        }
        inProgressJobs.remove(finishedJob);
        availableJobs.addAll(finishedJob.getSuccessors());
        doableJobs = new ArrayList<>();
        doableJobs.addAll(getDoableJobs(availableJobs));
    }
}
