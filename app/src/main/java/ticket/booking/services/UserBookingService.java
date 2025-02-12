package ticket.booking.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import ticket.booking.entites.Train;
import ticket.booking.entites.User;
import ticket.booking.util.UserServiceUtil;

public class UserBookingService{
    
    private User user;
    private List<User> userList;

    private ObjectMapper objectMapper = new ObjectMapper();
    private final String USER_FILE_PATH = "/users.json";
    

    public UserBookingService(User user) throws IOException{
        this.user = user;
        loadUserFromFile();
    }

    public UserBookingService() throws IOException{
        loadUserFromFile();
    }

    public void loadUserFromFile() throws IOException{
         File users = new File(USER_FILE_PATH);
         userList = objectMapper.readValue(users , new TypeReference<List<User>>(){});
//       File users = new File(USER_FILE_PATH);
//       System.out.println("Loading user data from file: " + USER_FILE_PATH);
//
//       if (!users.exists()) {
//           System.out.println("User file does not exist. Initializing empty user list.");
//           userList = new ArrayList<>(); // Initialize empty list
//           return;
//       }
//       try {
//           userList = objectMapper.readValue(users, new TypeReference<List<User>>() {});
//       } catch (IOException e) {
//           System.out.println("Failed to read user data from file. Initializing empty user list.");
//           userList = new ArrayList<>();
//           throw e; // Rethrow the exception for higher-level handling
//       }
    }

    public Boolean userLogin(){
        Optional<User> foundUser = userList.stream().filter(user1 ->{
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(),user1.getHashedPassword());
        }).findFirst();

        return foundUser.isPresent();
    }

    public Boolean signUp(User user1){
        try {
            userList.add(user1);
            saveUserListToFile();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void saveUserListToFile() throws IOException{
        File userFile = new File(USER_FILE_PATH);
        objectMapper.writeValue(userFile,userList);
    }

    public void fetchTickets(){

        Optional<User> userFetched = userList.stream().filter(user1 ->{
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword(),user1.getHashedPassword());
        }).findFirst();
        if(userFetched.isPresent()){
            userFetched.get().printTickets();
        }
    }

    public Boolean cancelTickets(){
        /*Waitforit */
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Ticket Number");
        String ticketId = sc.next();

        if(ticketId==null || ticketId.isEmpty()) {
            System.out.println("Ticket ID cannot be null or empty.");
            return Boolean.FALSE;
        }

        String finalTicketId = ticketId;
        boolean removed = user.getTicketsBooked().removeIf(ticket -> ticket.getTicketId().equalsIgnoreCase(finalTicketId));
        if (removed) {
            System.out.println("Ticket with ID " + ticketId + " has been canceled.");
            return Boolean.TRUE;
        }else{
        System.out.println("No ticket found with ID " + ticketId);
            return Boolean.FALSE;
        }
    }

    public List<Train> geTrain(String src , String dst){
        try {
            TrainService trainService = new TrainService();
            return trainService.searchTrain(src,dst); 
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> fetchSeats(Train train){
        return train.getSeats();
    }
    
    public boolean bookTrainSeats(Train train , int row , int seat){
        try {
            List<List<Integer>> seats = train.getSeats();
            TrainService trainService = new TrainService();
            if(row>=0 && row<seats.size() && seat>=0 && seat<seats.get(row).size()) {
                if(seats.get(row).get(seat) == 0){
                    seats.get(row).set(seat,1);
                    train.setSeats(seats);
                    trainService.updateTrain(train); // ? BRB
                    return true;
                }
                else    
                    return false;
            }
            else    
                return false;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }
}