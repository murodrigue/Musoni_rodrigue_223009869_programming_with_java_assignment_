package billlingmachine;

import java.util.Scanner;

public class BillingMachine {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Ask how many items were bought
        System.out.print("Enter the number of items: ");
        int numItems = sc.nextInt();

        double totalBill = 0;

        // Loop through each item
        for (int i = 0; i < numItems; i++) {
            System.out.println("\nItem " + (i + 1) + ":");

            System.out.print("Item name: ");
            String name = sc.next();

            System.out.print("Price per unit: ");
            double price = sc.nextDouble();

            System.out.print("Quantity purchased: ");
            int quantity = sc.nextInt();

            // Calculate the subtotal for each item
            double subtotal = price * quantity;
            totalBill += subtotal;

            // Print the item details
            System.out.println(name + ": " + quantity + " x " + price + " = " + subtotal);
        }

        // Check for discount
        double discount = 0;
        if (totalBill > 50000) {
            discount = totalBill * 0.05;
        }

        // Final total calculation
        double finalAmount = totalBill - discount;

        // Print the receipt
        System.out.println("\nReceipt:");
        System.out.println("Total before discount: " + totalBill);
        System.out.println("Discount: " + discount);
        System.out.println("Final amount payable: " + finalAmount);

        sc.close();
    }


}
