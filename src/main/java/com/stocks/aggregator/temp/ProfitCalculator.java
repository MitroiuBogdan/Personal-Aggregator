package com.stocks.aggregator.temp;

import java.util.Scanner;

public class ProfitCalculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input starting sum
        System.out.print("Enter the starting sum: ");
        double startingSum = scanner.nextDouble();

        // Choose profit type
        System.out.print("Is your daily profit a fixed amount or a percentage? (Enter 'fixed' or 'percent'): ");
        String profitType = scanner.next();

        double profitValue;
        if (profitType.equalsIgnoreCase("fixed")) {
            System.out.print("Enter the fixed daily profit: ");
            profitValue = scanner.nextDouble();
        } else {
            System.out.print("Enter the daily profit percentage (e.g. 5 for 5%): ");
            profitValue = scanner.nextDouble();
        }

        // Choose mode
        System.out.println("Choose mode:");
        System.out.println("1 - Enter target final sum");
        System.out.println("2 - Enter number of days");
        int mode = scanner.nextInt();

        double currentSum = startingSum;
        int days = 0;

        if (mode == 1) {
            // Mode 1: Target Sum Mode
            System.out.print("Enter the target final sum: ");
            double targetSum = scanner.nextDouble();

            while (currentSum < targetSum) {
                if (profitType.equalsIgnoreCase("fixed")) {
                    currentSum += profitValue;
                } else {
                    currentSum += currentSum * (profitValue / 100);
                }
                days++;
            }

            System.out.printf("Final sum reached: %.2f%n", currentSum);
            System.out.println("Days needed: " + days);

        } else if (mode == 2) {
            // Mode 2: Fixed Days Mode
            System.out.print("Enter the number of days: ");
            days = scanner.nextInt();

            for (int i = 0; i < days; i++) {
                if (profitType.equalsIgnoreCase("fixed")) {
                    currentSum += profitValue;
                } else {
                    currentSum += currentSum * (profitValue / 100);
                }
            }

            System.out.printf("Final sum after %d days: %.2f%n", days, currentSum);
        } else {
            System.out.println("Invalid mode selected.");
        }

        scanner.close();
    }
}
