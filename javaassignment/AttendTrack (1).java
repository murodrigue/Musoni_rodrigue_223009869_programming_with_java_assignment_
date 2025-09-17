package attendtrack;

import java.util.Scanner;

public class AttendTrack {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter total number of students: ");
        int totalStudents = sc.nextInt();
        int[] attendance = new int[30];
        int totalDays = 0, lowAttendanceDays = 0, totalAttendance = 0;

        do {
            System.out.print("Enter present students: ");
            attendance[totalDays] = sc.nextInt();
            if (attendance[totalDays] < totalStudents * 0.5) lowAttendanceDays++;
            totalAttendance += attendance[totalDays];
            totalDays++;
            System.out.print("Enter another day? (yes/no): ");
        } while (sc.next().equalsIgnoreCase("yes") && totalDays < 30);

        double avgAttendance = (double) totalAttendance / totalDays;
        double lowAttendancePercentage = (lowAttendanceDays * 100.0) / totalDays;

        System.out.println("\nAttendance Summary:");
        for (int i = 0; i < totalDays; i++) {
            System.out.println("Day " + (i + 1) + ": " + attendance[i] + " present");
        }
        System.out.println("Avg attendance: " + avgAttendance);
        System.out.println("Low attendance days: " + lowAttendancePercentage + "%");
        sc.close();
    }
}

