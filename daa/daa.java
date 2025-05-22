import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static int[][] hungarianAlgorithm(int[][] cost) {
        int n = cost.length;
        int[] labelByWorker = new int[n];
        int[] labelByJob = new int[n];
        int[] minSlackWorkerByJob = new int[n];
        int[] minSlackValueByJob = new int[n];
        int[] matchJobByWorker = new int[n];
        int[] matchWorkerByJob = new int[n];
        Arrays.fill(matchJobByWorker, -1);
        Arrays.fill(matchWorkerByJob, -1);

        for (int i = 0; i < n; i++) {
            labelByWorker[i] = Arrays.stream(cost[i]).min().getAsInt();
        }

        for (int j = 0; j < n; j++) {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                min = Math.min(min, cost[i][j] - labelByWorker[i]);
            }
            labelByJob[j] = min;
        }

        for (int w = 0; w < n; w++) {
            boolean[] committedWorkers = new boolean[n];
            int[] parentWorkerByCommittedJob = new int[n];
            Arrays.fill(parentWorkerByCommittedJob, -1);
            Arrays.fill(minSlackValueByJob, Integer.MAX_VALUE);
            Arrays.fill(minSlackWorkerByJob, -1);

            int committedWorker = w;

            while (true) {
                committedWorkers[committedWorker] = true;
                for (int j = 0; j < n; j++) {
                    if (parentWorkerByCommittedJob[j] == -1) {
                        int slack = cost[committedWorker][j] - labelByWorker[committedWorker] - labelByJob[j];
                        if (slack < minSlackValueByJob[j]) {
                            minSlackValueByJob[j] = slack;
                            minSlackWorkerByJob[j] = committedWorker;
                        }
                    }
                }

                int minSlack = Integer.MAX_VALUE;
                int minSlackJob = -1;
                for (int j = 0; j < n; j++) {
                    if (parentWorkerByCommittedJob[j] == -1 && minSlackValueByJob[j] < minSlack) {
                        minSlack = minSlackValueByJob[j];
                        minSlackJob = j;
                    }
                }

                for (int i = 0; i < n; i++) {
                    if (committedWorkers[i]) labelByWorker[i] += minSlack;
                }
                for (int j = 0; j < n; j++) {
                    if (parentWorkerByCommittedJob[j] != -1) labelByJob[j] -= minSlack;
                    else minSlackValueByJob[j] -= minSlack;
                }

                parentWorkerByCommittedJob[minSlackJob] = minSlackWorkerByJob[minSlackJob];
                if (matchWorkerByJob[minSlackJob] == -1) {
                    int committedJob = minSlackJob;
                    int parentWorker = parentWorkerByCommittedJob[committedJob];
                    while (true) {
                        int temp = matchJobByWorker[parentWorker];
                        matchJobByWorker[parentWorker] = committedJob;
                        matchWorkerByJob[committedJob] = parentWorker;
                        committedJob = temp;
                        if (committedJob == -1) break;
                        parentWorker = parentWorkerByCommittedJob[committedJob];
                    }
                    break;
                } else {
                    committedWorker = matchWorkerByJob[minSlackJob];
                }
            }
        }

        int[][] result = new int[n][2];
        for (int i = 0; i < n; i++) {
            result[i][0] = i;
            result[i][1] = matchJobByWorker[i];
        }
        return result;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of people/jobs (n): ");
        int n = sc.nextInt();

        int[][] cost = new int[n][n];
        System.out.println("Enter the cost matrix (n x n):");
        for (int i = 0; i < n; i++) {
            System.out.println("Enter costs for person " + i + ":");
            for (int j = 0; j < n; j++) {
                cost[i][j] = sc.nextInt();
            }
        }

        int[][] assignment = hungarianAlgorithm(cost);
        int totalCost = 0;
        System.out.println("\nOptimal Assignment:");
        for (int[] pair : assignment) {
            int person = pair[0];
            int job = pair[1];
            int costValue = cost[person][job];
            totalCost += costValue;
            System.out.println("Person " + person + " → Job " + job + " (Cost: " + costValue + ")");
        }
        System.out.println("Total Minimum Cost: " + totalCost);
    }
}
