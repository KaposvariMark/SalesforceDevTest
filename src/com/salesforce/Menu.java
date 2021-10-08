package com.salesforce;

import java.util.Scanner;

public class Menu {

    public static int printOptions(){
        int selection;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--------OPTIONS--------");
        System.out.println("1. Liveness check & authorization");
        System.out.println("2. List accounts");
        System.out.println("3. List contacts");
        System.out.println("-----------------------");

        System.out.print("Chosen option: ");
        selection = sc.nextInt();
        return selection;
    }
}
