package com.salesforce;

import com.salesforce.entities.Account;

import java.util.ArrayList;
import java.util.Scanner;

public class Menu {

    public static final String PLEASE_CHOSE_A_NUMBER_FROM_THE_ALIGNING_OPTIONS = "Please chose a number from the aligning options";

    public int printMainOptions(Scanner sc){
        int selectedOption = 0;

        System.out.println("\n--------OPTIONS--------");
        System.out.println("1. List accounts");
        System.out.println("2. List contacts");
        System.out.println("3. Get contacts of account");
        System.out.println("4. Exit");
        System.out.println("-----------------------");

        System.out.print("Chosen option: ");
        try {
            selectedOption = sc.nextInt();
        } catch (Exception e){
            System.out.println(PLEASE_CHOSE_A_NUMBER_FROM_THE_ALIGNING_OPTIONS);
        }
        System.out.print("\n********************************************\n");
        return selectedOption;
    }

    public Account printAccountsOptions(Scanner sc, ArrayList<Account> Accounts){
        int selectedOption = -1;
        boolean numberIsIncorrect = true;

        do {
            System.out.println("\n-------ACCOUNTS--------");
            for (int i = 0; i < Accounts.size(); i++) {
                System.out.println((i+1) + ". " + Accounts.get(i).getName());
            }
            System.out.println("-----------------------");

            System.out.print("Chosen option: ");
            try {
                selectedOption = sc.nextInt() - 1;
            } catch (Exception e){
                System.out.println(PLEASE_CHOSE_A_NUMBER_FROM_THE_ALIGNING_OPTIONS);
            }
            if(selectedOption < Accounts.size() && selectedOption >= 0) numberIsIncorrect = false;
        } while (numberIsIncorrect);
        System.out.print("\n********************************************\n");
        return Accounts.get(selectedOption);
    }
}
