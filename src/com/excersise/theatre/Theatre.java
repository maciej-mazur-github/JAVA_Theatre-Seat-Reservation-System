package com.excersise.theatre;

import java.util.*;

public class Theatre {
    private final String theatreName;
    private final int rowNumber;
    private final int seatsPerRow;
    private final Scanner scanner = new Scanner(System.in);

    Collection<Seat> seats = new LinkedHashSet<>();

    public Theatre(String name, int rowNumber, int seatsPerRow) {
        this.theatreName = name;
        this.rowNumber = rowNumber;
        this.seatsPerRow = seatsPerRow;
        createSeats(rowNumber, seatsPerRow);
    }

    public void run() {
        System.out.println("Welcome to theatre " + theatreName + ".");
        System.out.println();
        showSeats();
        boolean quit = false;
        int choice;
        printActions();

        while(!quit) {
            System.out.println();
            System.out.print("What action do you choose? (4 to print available actions)\t");

            if(!scanner.hasNextInt()) {
                System.out.println("Integer numbers allowed only. Please try again");
                scanner.nextLine();
                continue;
            }

            choice = scanner.nextInt();
            scanner.nextLine();

            if(choice < 0 || choice > 4) {
                System.out.println("Numbers in range 0-4 allowed only. Please see the available options");
            }

            switch (choice) {
                case 0:
                    System.out.println("You have decided to quit the program...");
                    quit = true;
                    break;
                case 1:
                    showSeats();
                    break;
                case 2:
                    reserveSeat();
                    break;
                case 3:
                    cancelSeat();
                    break;
                case 4:
                    printActions();
                    break;
            }
        }
    }

    private void printActions() {
        System.out.println();
        System.out.println("Available actions:");
        System.out.println("\t0   -   to quit the program");
        System.out.println("\t1   -   to show current reservation status");
        System.out.println("\t2   -   to reserve a seat");
        System.out.println("\t3   -   to cancel reservation for a given seat");
        System.out.println("\t4   -   to print available actions");
    }

    private void createSeats(int rowNumber, int seatsPerRow) {
        for(char row = 'A'; row <= ('A' + rowNumber - 1); row++) {
            for(int seatNumber = 1; seatNumber <= seatsPerRow; seatNumber++) {
                seats.add(new Seat(row + String.format("%02d", seatNumber)));
            }
        }
    }

    private void showSeats() {
        int counter = 0;

        System.out.println("Theatre " + theatreName + " current reservation status:\n");

        String builder;

        for(Seat s : seats) {
            builder = s.getSeatNumber();

            if(s.isReserved()) {
                builder += "(R)";
            }

            counter++;

            builder = String.format("%-9s", builder);

            if(counter % seatsPerRow == 0) {
                builder += "\n";
            }

            System.out.print(builder);
        }
    }

    private String verifySeat() {
        System.out.print("Enter seat number: ");
        String chosenSeat = scanner.nextLine().toUpperCase();
        char chosenRow = (chosenSeat.substring(0, 1)).charAt(0);
        int seatNumberInRow = Integer.parseInt(chosenSeat.substring(1));

        if(chosenRow < 'A' || chosenRow > ('A' + rowNumber - 1)) {
            System.out.println("There is no row " + chosenRow);
            return null;
        }

        if(seatNumberInRow < 1 || seatNumberInRow > seatsPerRow) {
            System.out.println("There is no seat " + seatNumberInRow + " in row " + chosenRow);
            return null;
        }

        if(seatNumberInRow < 10) {
            chosenSeat = chosenRow + "0" + seatNumberInRow;
        }

        return chosenSeat;
    }

    private void reserveSeat() {
        System.out.println("Seat reservation");
        String verifiedSeatNumber = verifySeat();

        if(verifiedSeatNumber == null) {
            return;
        }

        Seat foundSeat = findSeat(verifiedSeatNumber);

        if(foundSeat == null) {
            System.out.println("There is no seat " + verifiedSeatNumber);
            return;
        }

        if(foundSeat.reserve()) {
            System.out.println("Seat " + verifiedSeatNumber + " reserved successfully");
        } else {
            System.out.println("Seat " + verifiedSeatNumber + " has already been taken. Try again with another seat");
        }
    }

    private void cancelSeat() {
        System.out.println("Seat cancellation");
        String verifiedSeatNumber = verifySeat();

        if(verifiedSeatNumber == null) {
            return;
        }

        Seat foundSeat = findSeat(verifiedSeatNumber);

        if(foundSeat == null) {
            System.out.println("There is no seat " + verifiedSeatNumber);
            return;
        }

        if(foundSeat.cancel()) {
            System.out.println("Seat " + verifiedSeatNumber + " reservation cancelled successfully");
        } else {
            System.out.println("Seat " + verifiedSeatNumber + " was not taken. Nothing to cancel here then");
        }
    }

    private Seat findSeat(String seatNumber) {
        for(Seat s : seats) {
            if(s.getSeatNumber().equals(seatNumber)) {
                return s;
            }
        }

        return null;
    }



    private static class Seat {
        private final String seatNumber;
        private boolean reserved = false;

        public Seat(String seatNumber) {
            this.seatNumber = seatNumber;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public boolean reserve() {
            if(!reserved) {
                reserved = true;
                return true;
            } else {
                return false;
            }
        }

        public boolean cancel() {
            if(reserved) {
                reserved = false;
                return true;
            } else {
                return false;
            }
        }

        public boolean isReserved() {
            return reserved;
        }
    }
}
