package com.example.mapcalc;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class EmissionsCalculator {

    private static class EmissionsData {
        private static final Map<String, Double> emissionsData = new HashMap<>();

        static {
            emissionsData.put("Passenger Cars and Light SUVs", 146.5);
            emissionsData.put("Heavy SUVs and Light Commercial Vehicles", 212.5);
        }

        public static double getEmissionsIntensity(String vehicleType) {
            return emissionsData.getOrDefault(vehicleType, 0.0);
        }
    }

    public double calculateEmissions(String vehicleType, double distanceKm) {
        double emissionsIntensity = EmissionsData.getEmissionsIntensity(vehicleType);
        return emissionsIntensity * distanceKm;
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EmissionsCalculator calculator = new EmissionsCalculator();

        System.out.println("Enter the vehicle type\n[A] for Passenger Cars and Light SUVs\n[B] for Heavy SUVs and Light Commercial Vehicles:");
        System.out.print("Input Data: ");
        String choice = scanner.nextLine().toUpperCase();

        String vehicleType;
        switch (choice) {
            case "A":
                vehicleType = "Passenger Cars and Light SUVs";
                break;
            case "B":
                vehicleType = "Heavy SUVs and Light Commercial Vehicles";
                break;
            default:
                System.out.println("Invalid choice. Exiting.");
                scanner.close();
                return;
        }

        System.out.println("Enter the distance traveled in kilometers:");
        double distanceKm = scanner.nextDouble();

        double emissions = calculator.calculateEmissions(vehicleType, distanceKm);
        System.out.println("Total emissions for " + distanceKm + " km: " + emissions + " grams of CO2");

        scanner.close();
    }
}
