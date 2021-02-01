package edu.uw.cs.checks.readability.mainisaconcisesummary;

public class InputMainIsAConciseSummary {
    public static void main(String[] args) {
        // Compliant Code
        System.out.println();   // OK
        System.out.printf();    // OK
        System.out.print();     // OK
        
        // Uncompliant Code
        System.out.println("Hello, World!");    // Violation
        System.out.printf("Hello, World!");     // Violation
        System.out.print("Hello, World!");      // Violation
        
        System.out.println("Hello, " + "World!");   // Violation
        System.out.printf("Hello, " + "World!");    // Violation
        System.out.print("Hello, " + "World!");     // Violation
                
        String hello = "Hello, World!";
        
        System.out.println(hello);  // Violation
        System.out.printf(hello);   // Violation
        System.out.print(hello);    // Violation
    }
}
