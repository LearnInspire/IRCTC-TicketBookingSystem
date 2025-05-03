package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.service.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.util.*;

public class App {

    public static void main(String[] args) {
        try {
            runApp();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred:");
            e.printStackTrace();
        }
    }

    private static void runApp() {
        System.out.println("Running Train Booking System");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        UserBookingService userBookingService;

        try {
            userBookingService = new UserBookingService();
        } catch (IOException ex) {
            System.out.println("There is something wrong initializing the booking service.");
            ex.printStackTrace();
            return;
        }

        Train trainSelectedForBooking = null;

        while (option != 7) {
            System.out.println("\nChoose option");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");

            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // discard invalid input
                continue;
            }

            option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.println("Enter the username to signup");
                    String nameToSignUp = scanner.next();
                    System.out.println("Enter the password to signup");
                    String passwordToSignUp = scanner.next();
                    User userToSignup = new User(
                            nameToSignUp,
                            passwordToSignUp,
                            UserServiceUtil.hashPassword(passwordToSignUp),
                            new ArrayList<>(),
                            UUID.randomUUID().toString()
                    );
                    userBookingService.signUp(userToSignup);
                    break;

                case 2:
                    System.out.println("Enter the username to login");
                    String nameToLogin = scanner.next();
                    System.out.println("Enter the password to login");
                    String passwordToLogin = scanner.next();
                    User userToLogin = new User(
                            nameToLogin,
                            passwordToLogin,
                            UserServiceUtil.hashPassword(passwordToLogin),
                            new ArrayList<>(),
                            UUID.randomUUID().toString()
                    );
                    try {
                        userBookingService = new UserBookingService(userToLogin);
                    } catch (IOException ex) {
                        System.out.println("Login failed. Try again.");
                        return;
                    }
                    break;

                case 3:
                    System.out.println("Fetching your bookings");
                    userBookingService.fetchBookings();
                    break;

                case 4:
                    System.out.println("Type your source station");
                    String source = scanner.next();
                    System.out.println("Type your destination station");
                    String dest = scanner.next();
                    List<Train> trains = userBookingService.getTrains(source, dest);
                    if (trains.isEmpty()) {
                        System.out.println("No trains found.");
                        break;
                    }

                    int index = 1;
                    for (Train t : trains) {
                        System.out.println(index + ". Train ID: " + t.getTrainId());
                        for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                            System.out.println("  Station: " + entry.getKey() + " | Time: " + entry.getValue());
                        }
                        index++;
                    }

                    System.out.println("Select a train by typing 1, 2, 3...");
                    int selectedIndex = scanner.nextInt();
                    if (selectedIndex < 1 || selectedIndex > trains.size()) {
                        System.out.println("Invalid selection.");
                        break;
                    }
                    trainSelectedForBooking = trains.get(selectedIndex - 1);
                    break;

                case 5:
                    if (trainSelectedForBooking == null) {
                        System.out.println("Please select a train first using Option 4.");
                        break;
                    }

                    System.out.println("Available seats:");
                    List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);
                    for (List<Integer> row : seats) {
                        for (Integer seat : row) {
                            System.out.print(seat + " ");
                        }
                        System.out.println();
                    }

                    System.out.println("Enter the row number:");
                    int row = scanner.nextInt();
                    System.out.println("Enter the column number:");
                    int col = scanner.nextInt();

                    System.out.println("Booking your seat...");
                    Boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col);
                    if (Boolean.TRUE.equals(booked)) {
                        System.out.println("Booked! Enjoy your journey.");
                    } else {
                        System.out.println("Can't book this seat. It may already be booked or invalid.");
                    }
                    break;

                case 6:
                    System.out.println("Feature to cancel booking is under construction.");
                    break;

                case 7:
                    System.out.println("Exiting the app. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option. Please select between 1 to 7.");
                    break;
            }
        }
    }
}