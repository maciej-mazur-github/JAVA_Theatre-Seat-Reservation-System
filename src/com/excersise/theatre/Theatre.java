package com.excersise.theatre;


import java.util.*;

public class Theatre {
    private final String theatreName;
    private final int rowNumber;
    private final int seatsPerRow;
    private final Scanner scanner = new Scanner(System.in);
    private boolean areSeatsSorted = true;

    private List<Seat> seats = new ArrayList<>();

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

            if(choice < 0 || choice > 7) {
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
                case 5:
                    shuffleSeats();
                    break;
                case 6:
                    sortSeatsByNumber();
                    break;
                case 7:
                    sortSeatsByPriceOrder();
                    break;
            }
        }
    }

    private void sortSeatsByPriceOrder() {
        Comparator<Seat> PRICE_ORDER = new Comparator<Seat>() {
            @Override
            public int compare(Seat seat1, Seat seat2) {
                if(seat1.getPrice() > seat2.getPrice()) {
                    return 1;
                } else if(seat1.getPrice() < seat2.getPrice()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };

        Collections.sort(seats, PRICE_ORDER);
        System.out.println("Seats sorted by price as requested");
    }

    private void printActions() {
        System.out.println();
        System.out.println("Available actions:");
        System.out.println("\t0   -   to quit the program");
        System.out.println("\t1   -   to show current reservation status");
        System.out.println("\t2   -   to reserve a seat");
        System.out.println("\t3   -   to cancel reservation for a given seat");
        System.out.println("\t4   -   to print available actions");
        System.out.println("\t5   -   to shuffle seats");
        System.out.println("\t6   -   to sort seats by seat number");
        System.out.println("\t7   -   to sort seats by price");
    }

    private void createSeats(int rowNumber, int seatsPerRow) {
        int rowMargin = rowNumber / 4;
        int seatMargin = seatsPerRow / 4;


        for(char row = 'A'; row <= ('A' + rowNumber - 1); row++) {
            for(int seatNumber = 1; seatNumber <= seatsPerRow; seatNumber++) {
                double price = 12.0;

                if(row < ('A' + rowMargin) && seatNumber > seatMargin && seatNumber < (seatsPerRow - seatMargin)) {
                    price = 14.0;
                } else if (row >= ('A' + rowNumber - rowMargin) && (seatNumber < seatMargin || seatNumber > seatsPerRow - seatMargin)) {
                    price = 7.0;
                }

                seats.add(new Seat(row + String.format("%02d", seatNumber), price));
            }
        }
    }

    private void shuffleSeats() {
        Collections.shuffle(seats);
        areSeatsSorted = false;
        System.out.println("Seats shuffled as requested");
    }

    private void sortSeatsByNumber() {
        for(int i = 0; i < seats.size() - 1; i++) {
            for(int j = i + 1; j < seats.size(); j++) {
                if(seats.get(i).compareTo(seats.get(j)) > 0) {
                    Collections.swap(seats, i, j);
                }
            }
        }

        System.out.println("Seats now ordered as requested");
        areSeatsSorted = true;
    }

    private void showSeats() {
        int counter = 0;

        System.out.println("Theatre " + theatreName + " current reservation status:\n");

        String builder;

        for(Seat s : seats) {
            builder = s.getSeatNumber() + " $" + s.getPrice();

            if(s.isReserved()) {
                builder += " (R)";
            }


            counter++;

            builder = String.format("%-16s", builder);

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

    private Seat findSeat(String verifiedSeatNumber, int searchType) {
        Seat foundSeat;

        if(searchType == 1) {
            foundSeat = findSeatBruteForce(verifiedSeatNumber);
        } else if(searchType == 2) {
            foundSeat = findSeatCollectionsBinarySearch(verifiedSeatNumber);
        } else {
            foundSeat = findSeatManualBinarySearch(verifiedSeatNumber);
        }

        return foundSeat;
    }

    private int chooseSearchType() {
        int choice = 1;

        if(areSeatsSorted) {
            System.out.println("\tChoose search type. Enter:");
            System.out.println("\t\t1 - for bruteforce search");
            System.out.println("\t\t2 - for Collections.binarySearch");
            System.out.println("\t\t3 - for binary search manually implemented");
            System.out.print("\tWhat is your choice?\t");
            choice = scanner.nextInt();
            scanner.nextLine();

            if(choice < 1 || choice > 3) {
                choice = -1;
                System.out.println("Invalid choice. Please try again...");
            }
        } else {
            System.out.println("Seat will be searched using BruteForce method");
        }

        return choice;
    }

    private void reserveSeat() {
        System.out.println("Seat reservation");
        String verifiedSeatNumber = verifySeat();

        if(verifiedSeatNumber == null) {
            return;
        }

        int searchType = chooseSearchType();

        if(searchType < 0) {
            return;
        }

        Seat foundSeat = findSeat(verifiedSeatNumber, searchType);

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

        int searchType = chooseSearchType();

        if(searchType < 0) {
            return;
        }

        Seat foundSeat = findSeat(verifiedSeatNumber, searchType);

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

    private Seat findSeatManualBinarySearch(String verifiedSeatNumber) {
        int lowIndex = 0;
        int highIndex = seats.size() - 1;
        int comparison;
        int middleIndex;

        while(lowIndex <= highIndex) {
            System.out.print(".");
            middleIndex = (highIndex + lowIndex) / 2;
            comparison = seats.get(middleIndex).getSeatNumber().compareTo(verifiedSeatNumber);

            if(comparison > 0) {
                highIndex = middleIndex - 1;
            } else if(comparison < 0) {
                lowIndex = middleIndex + 1;
            } else {
                return seats.get(middleIndex);
            }
        }

        return null;
    }

    private Seat findSeatCollectionsBinarySearch(String verifiedSeatNumber) {
        int foundSeatPosition = Collections.binarySearch(seats, new Seat(verifiedSeatNumber, 0), null);

        if(foundSeatPosition >= 0) {
            return seats.get(foundSeatPosition);
        } else {
            return null;
        }
    }

    private Seat findSeatBruteForce(String seatNumber) {
        for(Seat s : seats) {
            System.out.print(".");
            if(s.getSeatNumber().equals(seatNumber)) {
                return s;
            }
        }

        return null;
    }



    private static class Seat implements Comparable<Seat> {
        private final String seatNumber;
        private boolean reserved = false;
        private double price;

        public Seat(String seatNumber, double price) {
            this.seatNumber = seatNumber;
            this.price = price;
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

        @Override
        public int compareTo(Seat seat) {
            return seatNumber.compareTo(seat.getSeatNumber());
        }

        public double getPrice() {
            return price;
        }
    }
}
