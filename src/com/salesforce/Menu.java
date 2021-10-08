package com.salesforce;

import java.util.Scanner;

public class Menu {

    public int printMainOptions(){
        int selection = 5;
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--------OPTIONS--------");
        System.out.println("1. List accounts");
        System.out.println("2. List contacts");
        System.out.println("3. List contacts by account");
        System.out.println("4. Exit");
        System.out.println("-----------------------");

        System.out.print("Chosen option: ");
        try {
            selection = sc.nextInt();
        } catch (Exception e){
            System.out.println("Please chose a number from the aligning options");
        }

        return selection;
    }
}
