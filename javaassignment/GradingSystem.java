package gradingsystem;

import java.util.Scanner;

public class GradingSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int totalStudents = 0, passes = 0, fails = 0;
        while (true) {
            System.out.print("Enter student's marks (0-100) or -1 to stop: ");
            int marks = sc.nextInt();

            if (marks == -1) break; // Exit condition

            totalStudents++;

            // Assign grade
            if (marks >= 80) System.out.println("Grade: A");
            else if (marks >= 70) System.out.println("Grade: B");
            else if (marks >= 60) System.out.println("Grade: C");
            else if (marks >= 50) System.out.println("Grade: D");
            else {
                System.out.println("Grade: F");
                fails++;
                continue;
            }

            // Count pass/fail
            if (marks >= 50) passes++;
        }

        // Print summary
        if (totalStudents > 0) {
            double passRate = (passes * 100.0) / totalStudents;
            System.out.println("\nSummary Report:");
            System.out.println("Total students: " + totalStudents);
            System.out.println("Passed: " + passes + ", Failed: " + fails);
            System.out.println("Pass rate: " + passRate + "%");
        }

        sc.close();
    }
}
