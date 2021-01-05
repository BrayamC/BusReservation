import java.sql.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Bus {
    private Connection c = null;
    private String dbName;
    private boolean isConnected = false;

    private void openConnection(String _dbName) {
        dbName = _dbName;

        if (false == isConnected) {
            //System.out.println("++++++++++++++++++++++++++++++++++");
            //System.out.println("Open database: " + _dbName);

            try {
                // type of database: sql type
                String connStr = new String("jdbc:sqlite:");
                connStr = connStr + _dbName;

                // STEP: Register JDBC driver
                Class.forName("org.sqlite.JDBC");

                // STEP: Open a connection
                c = DriverManager.getConnection(connStr);

                // STEP: Diable auto transactions
                c.setAutoCommit(false);

                isConnected = true;
                //System.out.println("success");
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }

            //System.out.println("++++++++++++++++++++++++++++++++++");
        }
    }

    private void closeConnection() {
        if (true == isConnected) {
            //System.out.println("++++++++++++++++++++++++++++++++++");
            //System.out.println("Close database: " + dbName);

            try {
                // STEP: Close connection
                c.close();

                isConnected = false;
                dbName = "";
                //System.out.println("success");
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }

            //System.out.println("++++++++++++++++++++++++++++++++++");
        }
    }

    /* Helper functions*/

    /* checks if customer ID is inside account table */
    private boolean userExists(int custId){

        try{
            String sql = "SELECT * " +  
                         "FROM Account" ;
    
            PreparedStatement stmt = c.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
    
            while(rs.next()){
                int sqlId = rs.getInt("A_custId");
    
                if(sqlId == custId){
                    stmt.close();
                    rs.close();
                    return true;
                }
            }
            stmt.close();
            rs.close();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

   

    /* checks if route ID is in Route table. */ 
    private boolean routeExists(int _routeId){

        try{
            String sql = "SELECT * " +  
                         "FROM Route" ;
    
            PreparedStatement stmt = c.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
    
            while(rs.next()){
                int routeId = rs.getInt("R_routeId");
    
                if(routeId == _routeId){
                    stmt.close();
                    rs.close();
                    return true;
                }
            }
            stmt.close();
            rs.close();
            
            System.out.println("Route not found");
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /* Returns true if ticket id is in ticket table. False otherwise */
    private boolean ticketExists(int _ticketId){
        boolean ticketExsists = false;
        try{
            String sql = "SELECT * " +
                         "FROM Ticket " +
                         "WHERE T_ticketId = ?";
    
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setInt(1, _ticketId);
            ResultSet rs = stmt.executeQuery();
    
            if(rs.next()){ticketExsists = true;}

            rs.close();
            stmt.close();

            return ticketExsists;

        } catch (Exception e) {
            return false;
        }
    }

    /* createTicketId will attempt to return a new ticket Id. 
       -1 will be returned if it couldn't get a new ticket Id   */
    private int createTicketId(){

        try{
            String sql = "SELECT max(T_ticketId) as max " +
            "FROM Ticket"; 

            PreparedStatement stmt = c.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                int sqlId = rs.getInt("max");
                stmt.close();
                rs.close();
                return sqlId + 1;
            }else{
                stmt.close();
                rs.close();
                return -1;
            }

        }catch (Exception e) {
            return -1;
        }
    }

    /* getDriverId attempts to return the driver's Id that is driving for defined route 
       -1 will return if a dirver id wasnt found */
    private int getDriverId(int routeId){
        try{
            String sql = "SELECT * " +
                         "FROM Bus " +
                         "WHERE B_routeId = ?"; 

            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setInt(1, routeId);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                int driverId = rs.getInt("B_driverID");
                stmt.close();
                rs.close();
                return driverId;
            }else{
                stmt.close();
                rs.close();
                return -1;
            }

        }catch (Exception e) {
            return -1;
        }
    }



    /* returns true if customer is in group */
    private boolean userGroupExists(int _custId, int _groupId){

        try{
            String sql = "SELECT * " +
                         "FROM GroupOf " +
                         "WHERE GO_custId = ?";

            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setInt(1, _custId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                if(rs.getInt("GO_groupId") == _groupId){
                    stmt.close();
                    rs.close();
                    return true;
                }
            }

            stmt.close();
            rs.close();

            System.out.println("Group not found");
            return false;

        }catch (Exception e) {
            return false;
        }
    }

    /* locationExists returns true if location string is in route table. false otherwise */
    private boolean locationExists(String location){

        boolean locationExists = false;
        try{
            String sql = "SELECT R_departureLocation, R_arrivalLocation, R_depatureTime, R_arrivalTime " +
                         "FROM Route " +
                         "WHERE R_departureLocation = ?";
    
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setString(1, location);
            ResultSet rs = stmt.executeQuery();
    
            if(rs.next()){locationExists = true;}

            rs.close();
            stmt.close();

        } catch (Exception e) {
            locationExists =  false;
        }
        return locationExists;
    }
    
    /* returns ticket Id. -1 is returned if ticket couldnt be created */
    private int addTicket(int _custId, int _routeId) {
        //System.out.println("++++++++++++++++++++++++++++++++++");
        //System.out.println("sql 4: Add Ticket");

        int ticket = -1;
        boolean validTicket = true;
        try {
            
            if(!userExists(_custId)){
                System.out.println("Customer does not exist. Try again.");
                validTicket = false;
                ticket = -1;
            }else if(!routeExists(_routeId)){
                System.out.println("Route does not exist. Try again.");
                validTicket = false;
                ticket = -1;
            }

            if(validTicket){
                int ticketId = createTicketId();
                int driverId = getDriverId(_routeId);
                //System.out.println("New ticket id: " + ticketId);

                if(ticketId != -1 && driverId != -1){
                    /* Add ticket into Ticket table */
                    String sql = "INSERT INTO Ticket(T_ticketId, T_price, T_groupId, T_driverId, T_verified, T_custId) " +
                                 "VALUES(?, 0, NULL, ?, ?, ?) ";

                    /* STEP: Execute update statement for Ticket table*/
                    PreparedStatement stmt = c.prepareStatement(sql);
                    stmt.setInt(1, ticketId);
                    stmt.setInt(2, driverId);
                    stmt.setString(3, "FALSE");
                    stmt.setInt(4, _custId);
                    stmt.executeUpdate();
 
                    /* Add ticket into TicketRoute */
                    String sql2 = "INSERT INTO TicketRoute(TR_ticketId, TR_routeID) " +
                                  "VALUES(?, ?)";
      
                    /* STEP: Execute update statement for TicketRoute table */
                    PreparedStatement stmt2 = c.prepareStatement(sql2);
                    stmt2.setInt(1, ticketId);
                    stmt2.setInt(2, _routeId);
                    stmt2.executeUpdate();
      
                    /* STEP: Commit transaction */
                    c.commit();
                    stmt.close();
                    stmt2.close();

                    //System.out.println("Ticket " + ticketId + " has been added.");
                    //System.out.println("SUCCESS");
                    ticket = ticketId;
                }else{
                    ticket = -1;
                }
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            ticket = -1;
        }

        //System.out.println("++++++++++++++++++++++++++++++++++");
        return ticket;
    }

    /* returns ticket id. -1 is returned if ticket was not able to be created */
    private int addGroupTicket(int _custId, int _groupId, int _routeId) {
        //System.out.println("++++++++++++++++++++++++++++++++++");
        //System.out.println("sql 6: Add Group Ticket");

        int ticket = -1;
        boolean validTicket = true;
        try {
            
            if(!userExists(_custId)){
                System.out.println("Customer does not exist. Try again.");
                validTicket = false;
                ticket = -1;
            }else if(!routeExists(_routeId)){
                System.out.println("Route does not exist. Try again.");
                validTicket = false;
                ticket = -1;
            }else if(!userGroupExists(_custId, _groupId)){
                System.out.println("Customer is not in group " + _groupId + ". Try again.");
                validTicket = false;
                ticket = -1;
            }

            if(validTicket){
                int ticketId = createTicketId();
                int driverId = getDriverId(_routeId);
                //System.out.println("New ticket id: " + ticketId);

                if(ticketId != -1 && driverId != -1){
                    /* Add ticket into Ticket table */
                    String sql = "INSERT INTO Ticket(T_ticketId, T_price, T_groupId, T_driverId, T_verified, T_custId) " +
                                 "VALUES(?, 0, ?, ?, ?, ?) ";

                    /* STEP: Execute update statement for Ticket table*/
                    PreparedStatement stmt = c.prepareStatement(sql);
                    stmt.setInt(1, ticketId);
                    stmt.setInt(2, _groupId);
                    stmt.setInt(3, driverId);
                    stmt.setString(4, "FALSE");
                    stmt.setInt(5, _custId);
                    stmt.executeUpdate();
 
                    /* Add ticket into TicketRoute */
                    String sql2 = "INSERT INTO TicketRoute(TR_ticketId, TR_routeID) " +
                                  "VALUES(?, ?)";
      
                    /* STEP: Execute update statement for TicketRoute table */
                    PreparedStatement stmt2 = c.prepareStatement(sql2);
                    stmt2.setInt(1, ticketId);
                    stmt2.setInt(2, _routeId);
                    stmt2.executeUpdate();
      
                    /* STEP: Commit transaction */
                    c.commit();
                    stmt.close();
                    stmt2.close();

                    System.out.println("Ticket " + ticketId + " has been added for group " + _groupId + ".");
                    //System.out.println("SUCCESS");
                    ticket = ticketId;
                }else{
                    ticket = -1;
                }
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            ticket = -1;
        }

        //System.out.println("++++++++++++++++++++++++++++++++++");
        return ticket;
    }

    /* returns true if bus times were displayed. False if something went wrong */
    private boolean displayBusTimes(String location) {

        boolean displayed = false;
        try {

            if(locationExists(location)){
                String sql = "SELECT R_routeId, R_departureLocation, R_arrivalLocation, R_depatureTime, R_arrivalTime " +
                             "FROM Route " +
                             "WHERE R_departureLocation = ?";
    
                // STEP: Execute a query
                PreparedStatement stmt = c.prepareStatement(sql);
                stmt.setString(1, location);
                ResultSet rs = stmt.executeQuery();
    
                // STEP: Extract data from result set
                System.out.printf("%10s %22s %20s %19s %19s\n", "R_routeId", "Departure Location", "Arrival Location", "Depature Time", "Arrival Time");
                System.out.println("-----------------------------------------------------------------------------------------------------------------");
    
                while (rs.next()) {
                    int routeId = rs.getInt("R_routeId");
                    String depatureL = rs.getString("R_departureLocation");
                    String arrivalL = rs.getString("R_arrivalLocation");
                    String depatureT = rs.getString("R_depatureTime");
                    String arrivalT = rs.getString("R_arrivalTime");

                    System.out.printf("%10s %22s %20s %19s %19s\n", routeId, depatureL, arrivalL, depatureT, arrivalT);
                }
                //System.out.println("SUCCESS");
                displayed = true;

                stmt.close();
                rs.close();
            }else{
                System.out.println("Location does not exist. Try Again.");
                displayed = false;
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            displayed = false;
        }

        return displayed;
    }

    // Set price of ticket(also has to update balance of customers) 
    private double ticketprice(int routeId) {
        //System.out.println("++++++++++++++++++++++++++++++++++");
        //System.out.println("SQL 11: ticketPrice");
        boolean displayed = false;
        
        try {
            if(true){
                String sql = "SELECT sq.R_routeId, hour * 10 + minutes * .1 as price, R1.R_departureLocation, R1.R_arrivalLocation " +
                             "FROM " +
                            "(SELECT R_routeId, ABS(strftime('%H', R_depatureTime) - strftime('%H', R_arrivalTime)) as hour, ABS(strftime('%M', R_depatureTime) - strftime('%M', R_arrivalTime)) as minutes " +
                            "FROM Route) sq, Route R1 " +
                            "WHERE " +
                            "R1.R_routeId = sq.R_routeId AND " +
                            "sq.R_routeId = ?";
    
                // STEP: Execute a query
                PreparedStatement stmt = c.prepareStatement(sql);
                stmt.setInt(1, routeId);
                ResultSet rs = stmt.executeQuery();
    
                // STEP: Extract data from result set
                //System.out.printf("%10s\n", "Price");
                //System.out.println("------------------");
    
                double price = 0;
                while (rs.next()) {
                    price = rs.getDouble("price");
                    //System.out.printf("%10s\n", "$" + price);
                }

                stmt.close();
                rs.close();
                return price;
            }else{
                return 0;
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        //System.out.println("SUCCESS");
        //System.out.println("++++++++++++++++++++++++++++++++++");

        return 0;
    }

    //Display ticket 
    private boolean displayTicket(int displayT) {
        //System.out.println("++++++++++++++++++++++++++++++++++");
        //System.out.println("SQL 12: Display Ticket ");
        boolean displayed = false;
        try {
            if(ticketExists(displayT)){
                String sql = "SELECT T_ticketId , T_price, T_groupId, T_driverId, T_verified, T_custId " +
                              "FROM Ticket " +
                              "WHERE T_ticketId = ? ";
                PreparedStatement stmt = c.prepareStatement(sql);
                stmt.setInt(1, displayT);
                ResultSet rs = stmt.executeQuery();
                System.out.println("Displaying Ticket:  " + displayT);
                displayed = true;
            while (rs.next()) {
                String ticketId = rs.getString("T_ticketId");
                String ticketPrice = rs.getString("T_price");
                String ticketGroup = rs.getString("T_groupId");
                String ticketDriver = rs.getString("T_driverId");
                String ticketVerify = rs.getString("T_verified");
                String ticketCustomer = rs.getString("T_custId");

                System.out.printf("%10s %20s %19s %19s %10s %10s\n ", "               T_ticketId ", "T_price ", "T_groupId ","T_driverId ", " T_verified ","T_custId " );
                System.out.println("--------------------------------------------------------------------------------------------------------------");
                System.out.printf("%22s %20s %19s %19s %10s %10s\n", ticketId, ticketPrice, ticketGroup, ticketDriver, ticketVerify, ticketCustomer);
                displayed = true;
            }

            stmt.close();
            rs.close();
        }
            else{
                System.out.println("Ticket does not exist. Please try again");
                displayed = false;
                
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
       // System.out.println("SUCCESS");
       // System.out.println("++++++++++++++++++++++++++++++++++");

        return false;
    }
        
    private int printRoutes(){
        int choice = -1;
        while(choice != 0){
            System.out.println("============================== View Routes and Times ================================================"); 
            System.out.println("     Options: Quit[0] | Go Back[1] | Merced[2] | Modesto[3] | Turlock[4] | Fresno[5] | LA[6]"); 
            System.out.println("=====================================================================================================");
            
            Scanner scan = new Scanner(System.in);
            System.out.print("Enter Route: ");
            choice = scan.nextInt();  
            System.out.println("");

            //System.out.println("You chose: " + choice); 
                   
            final int quit = 0;
            final int goBack = 1;  
            final int Merced = 2;
            final int Modesto = 3;
            final int Turlock =4;
            final int Fresno = 5; 
            final int LA =6;
                
            
            switch(choice){
                case quit:
                    return 0;
                case goBack:
                    return 1;
                case Merced:
                    displayBusTimes("Merced");
                    break;
                case Modesto:
                    displayBusTimes("Modesto");
                    break;
                case Turlock:
                    displayBusTimes("Turlock");
                    break;
                case Fresno:
                    displayBusTimes("Fresno");
                    break;
                case LA:
                    displayBusTimes("LA");  
                    break;
            }
        }

        return 0;
    }

        /* returns customer id */
        private int trylogin(String _username, String _password){

            boolean userNameExists = false;
            boolean passwordExists = false;
            int custId = -1;
            try{
                String sql = "SELECT * " +  
                             "FROM Customer" ;
        
                PreparedStatement stmt = c.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        
                while(rs.next()){

                    String userName = rs.getString("C_username");
                    if(userName.equals(_username)){userNameExists = true;}

                    String password = rs.getString("C_password");
                    if(password.equals(_password)){passwordExists = true;}

                    if(userNameExists && passwordExists){
                        custId = rs.getInt("C_custId");
                        break;
                    }
                }
                System.out.println("Logging in...");
                stmt.close();
                rs.close();
            } catch (Exception e) {
                userNameExists = false;
                passwordExists = false;
            }

            if(userNameExists && passwordExists){
                return custId;
            }
            if(!userNameExists){
                System.out.println("Invalid user name. Try Again!");
            }
            if(userNameExists && !passwordExists){
                System.out.println("Invalid password. Try Again!");
            }

            return custId;
           
        }

    private void viewPurchaseTickets(int _custId){
        
        int ticketId;
        try{
            String sql = "SELECT * " +
                         "FROM Ticket " +
                         "WHERE T_custId = ?";

            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setInt(1, _custId);
            ResultSet rs = stmt.executeQuery();

            boolean ticketsEmpty = false;
            while(rs.next()){
                ticketId = rs.getInt("T_ticketId");
                displayTicket(ticketId);
                System.out.println("\n");
                ticketsEmpty = true;
            }

            if(!ticketsEmpty){
                System.out.println("        You have no tickets purchased!");
            }
            stmt.close();
            rs.close();

            System.out.println("");

        }catch (Exception e) {
            System.out.println("Something went wrong. Try Again");
        }     
    }

    private double getGroupSize(int _groupId){
        double groupSize = 1;
        try{
            String sql = "Select count(DISTINCT GO_custId) as cnt " +
                         "FROM GroupOf " +
                         "WHERE GO_groupId = ?";

            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setInt(1, _groupId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                groupSize = rs.getInt("cnt");
            }

            stmt.close();
            rs.close();

            System.out.println("");

        }catch (Exception e) {
            System.out.println("Something went wrong. Try Again");
        }     

        return groupSize;
    }
    private double subtractGroupBalance(int _custId, int _routeId, int _groupId){

        try{
            double ticketPrice = ticketprice(_routeId);
            double balance = 0;

            String sql = "SELECT A_balance " +
                         "FROM Account " +
                         "where A_custId = ?" ;
            
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setInt(1, _custId) ;
            ResultSet rs = stmt.executeQuery();
    
            if(rs.next()){
                balance  = rs.getInt("A_balance");
                System.out.println("Current Balance: " + balance);
            }

            if(balance >= ticketPrice * getGroupSize(_groupId)){
                double newbalance = balance - ticketPrice * getGroupSize(_groupId);;
                stmt.close();
                rs.close();
                return newbalance;

            }else if (ticketPrice * getGroupSize(_groupId) > balance){
                System.out.println("You do not have enough money in your account. You have $" + balance + ". Try Again ");
            }

            stmt.close();
            rs.close();
        }catch (Exception e) {
            return -1;
        }

        return -1;
    }

    private boolean updateTicketPrice(int _ticketId, double _price){
        try{
            String sql = "Update Ticket " +
                         "SET T_price = ? " +
                         "where T_ticketId = ?";
                         
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setDouble(1, _price) ;
            stmt.setInt(2, _ticketId) ;
            stmt.executeUpdate();
            c.commit();
            stmt.close();
        }catch (Exception e) {
            return false;
        }
        return true;
    }

        /* Returns ticket id*/
        private int buyGroupTicketMenu (int custId){     
            boolean ticketBought = false;
            int ticketId = -1;
    
            while(!ticketBought){
                Scanner scan = new Scanner(System.in);

                System.out.println("================================= Buy Group Ticket ==================================================");  
                System.out.println("                           Options: Please insert Group Id[?]                                        ");
                System.out.println("====================================================================================================="); 
                
                System.out.print("GroupId: ");
                int groupId = scan.nextInt();
                System.out.println("");

                System.out.println("================================= Buy Group Ticket ==================================================");  
                System.out.println("                          Options: Please insert the route ID[?]                      ");
                System.out.println("====================================================================================================="); 
                System.out.print("Route id: ");
                int routeId = scan.nextInt();
                System.out.println("");
    
                boolean groupExists = false;
                groupExists = userGroupExists(custId, groupId);

                boolean routeExists = false;
                routeExists = routeExists(routeId);

                double newBalance = -1;
                double ticketPrice = -1;
                if(groupExists && routeExists){
                    /* check that they have enugh money first */
                    double balance = getBalance(custId);
                    ticketPrice = ticketprice(routeId);
                    ticketPrice = ticketPrice * getGroupSize(groupId);

                    System.out.println("===================================== Checkout Group Ticket ===================================="); 
                    System.out.println("                                     TicketPrice: " + ticketPrice + " Balance: " + balance);
                    System.out.println("                                  Would you like to purchase this ticket?"                           );
                    System.out.println("                                        Options: No[0] | Yes[1]"                                         ); 
                    System.out.println("=====================================================================================================");
                    System.out.print("Enter option: ");
                    int decision = scan.nextInt();
                    System.out.println("");

                    
                    if(decision == 1){
                        ticketId = addGroupTicket(custId, groupId, routeId);
                        newBalance = subtractGroupBalance(custId, routeId, groupId);
                    }


                }

                if(ticketId != -1 && newBalance != -1 && newBalance != -1 && updateBalance(custId, newBalance) && updateTicketPrice(ticketId, ticketPrice)){
                    System.out.print("Ticket bought successfully! This is your ticket Id: " +  ticketId);
                    System.out.println(". New Balance: " + newBalance);
                    ticketBought = true;
                        
                }else{
                    
                    System.out.println("============================== Buy Group Ticket ====================================================="); 
                    System.out.println("                       Options: Leave[0] | Try Again[1]                       "); 
                    System.out.println("====================================================================================================="); 
    
                    System.out.print("Enter option: ");
                    int choice = scan.nextInt();
                    System.out.println("");
    
                    final int leave = 0;
                    final int tryAgain = 1;
    
                    switch(choice){
                        case leave:
                            return -1;
                        case tryAgain:
                            break;
                        default:
                            break;
                    }
                }
            }
    
            return ticketId;
        }
    
             
    private double getBalance(int _custId){
        
        double balance = 0;
        try{
   
            String sql = "SELECT A_balance " +
                         "FROM Account " +
                         "where A_custId = ?" ;
            
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setInt(1, _custId) ;
            ResultSet rs = stmt.executeQuery();
    
            if(rs.next()){
                balance  = rs.getInt("A_balance");
            }

            stmt.close();
            rs.close();
            
        }catch (Exception e) {
            return balance;
        }

        return balance;
    }


    private double subtractBalance(int _custId, int _routeId){
        
        try{
            double ticketPrice = ticketprice(_routeId);
            double balance = 0;

            String sql = "SELECT A_balance " +
                         "FROM Account " +
                         "where A_custId = ?" ;
            
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setInt(1, _custId) ;
            ResultSet rs = stmt.executeQuery();
    
            if(rs.next()){
                balance  = rs.getInt("A_balance");
                //System.out.println("Your balance is: " + balance);
            }

            if(balance >= ticketPrice){
                double newbalance = balance - ticketPrice;
                stmt.close();
                rs.close();
                return newbalance;

            }else if (ticketPrice > balance){
                System.out.println("You do not have enough money in your account. You have $" + balance + ". Try Again ");
            }

            stmt.close();
            rs.close();
        }catch (Exception e) {
            return -1;
        }

        return -1;
    }

    private boolean updateBalance(int _custId, double newBalance){
        
        try{
            String sql = "Update Account " +
                         "SET A_balance = ? " +
                         "where A_custId = ?";
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setDouble(1, newBalance) ;
            stmt.setDouble(2, _custId);  
            stmt.executeUpdate();
            /* STEP: Commit transaction */
            c.commit();
            //System.out.println("Your new balance is: " + newBalance);
            stmt.close();
            return true;
        }catch (Exception e) {
            return false;
        }
    }
    /* Returns ticket id*/
    private int buyIndividualTicketMenu(int custId){

        boolean ticketBought = false;
        int ticketId = -1;

        while(!ticketBought){
            System.out.println("================================== Buy Individual Ticket ============================================"); 
            System.out.println("                       Options: Please insert the route ID[?]                       "); 
            System.out.println("====================================================================================================="); 

            System.out.print("Route id: ");
            Scanner scan = new Scanner(System.in);
            int routeId = scan.nextInt();
            System.out.println("");
            
            boolean routeExists = false;
            routeExists = routeExists(routeId);

            double newBalance = -1;
            double ticketPrice = -1;
            if(routeExists){
                /* check that they have enough money first */
                double balance = getBalance(custId);
                ticketPrice = ticketprice(routeId);

                System.out.println("===================================== Checkout Individual Ticket ===================================="); 
                System.out.println("                                     TicketPrice: " + ticketPrice + " Balance: " + balance);
                System.out.println("                                  Would you like to purchase this ticket?"                           );
                System.out.println("                                        Options: No[0] | Yes[1]"                                         ); 
                System.out.println("=====================================================================================================");
                System.out.print("Enter option: ");
                int decision = scan.nextInt();
                System.out.println("");
                
               
                if(decision == 1 ){
                    ticketId = addTicket(custId, routeId); 
                    newBalance = subtractBalance(custId, routeId); 
                }

            }

                
            if(ticketId != -1 && newBalance != -1 && updateBalance(custId, newBalance) && updateTicketPrice(ticketId, ticketPrice)){
                
                System.out.print("Ticket bought successfully! This is your ticket Id: " +  ticketId);
                System.out.println(". New Balance: " + newBalance);
                ticketBought = true;
                    
            }else{ 
                System.out.println("============================== Buy Individual Ticket ================================================"); 
                System.out.println("                        Options: leave[0] | Try Again?[1]"                            ); 
                System.out.println("=====================================================================================================");

                System.out.print("Enter option: ");
                int choice = scan.nextInt();
                System.out.println("");

                final int leave = 0;
                final int tryAgain = 1;

                switch(choice){
                    case leave:
                        return -1;
                    case tryAgain:
                        break;
                    default:
                        break;
                }
            }
        }

        return ticketId;
    }
    
    // shows the groups the user is part of
    private void displayGroups(int custId){

        try{
            ArrayList<Integer> groups = new ArrayList<Integer>();

            String sql =   "Select * " +
                           "FROM GroupOf " +
                           "WHERE GO_custId = ?";
    
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setInt(1, custId);
            ResultSet rs = stmt.executeQuery();
    
            System.out.println("Your are part of the following groups: \n");
            while(rs.next()){
                int groupId = rs.getInt("GO_groupId");
                System.out.println("Group " + groupId);

                
                String sql2 =   "Select A_accountName, A_balance " +
                                "FROM GroupOf, Account " +
                                "WHERE A_custId = GO_custId and " + 
                                "GO_groupId = ?";

                PreparedStatement stmt2 = c.prepareStatement(sql2);
                stmt2.setInt(1, groupId);
                ResultSet rs2 = stmt2.executeQuery();

                System.out.println("Members: ");
                while(rs2.next()){
                    String name = rs2.getString("A_accountName");
                    System.out.println("        " + name + " ");
                }

                stmt2.close();
                rs2.close();  
            }   
            System.out.println("");
            stmt.close();
            rs.close();

        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private int buyTicket(int custId){
        int choice = -1;
        while(true){
            System.out.println("==================================== Buy Ticket ====================================================="); 
            System.out.println("       Options: Quit[0] | goBack[1] | Buy Individual Ticket[2] | Buy Group Ticket [3] |"); 
            System.out.println("                      View Groups[4] | View Routes and Times [5]"                                       ); 
            System.out.println("======================================================================================================"); 
            
            Scanner scan = new Scanner(System.in);
            System.out.print("Enter Option: ");
            choice = scan.nextInt(); 
            System.out.println("");

            final int quit = 0;
            final int goBack = 1;
            final int buyIndividualTicket = 2;
            final int buyGroupTicket = 3;
            final int viewGroup = 4;
            final int viewRoutes = 5;

            switch (choice){
                case quit:
                    return 0;
                case goBack:
                    return -1;
                case buyIndividualTicket:
                    buyIndividualTicketMenu(custId);
                    break;
                case buyGroupTicket:
                    buyGroupTicketMenu(custId);
                    break;
                case viewGroup:
                    displayGroups(custId);
                    break;
                case viewRoutes:
                    printRoutes();
                    break;
            }        
        }
    }
/* returns true if account information was displayed. False if something went wrong */
private void displayAccountInformation(int custId) {

    try {

        if(userExists(custId)){
            String sql = "SELECT * " +
                         "FROM Account " +
                         "Where A_custId = ?";

            // STEP: Execute a query
            PreparedStatement stmt = c.prepareStatement(sql);
            stmt.setInt(1, custId);
            ResultSet rs = stmt.executeQuery();

            // STEP: Extract data from result set
            System.out.printf("%10s %6s %27s %5s %8s\n", "Id", "Name", "Balance", "Age", "Phone");
            System.out.println("---------------------------------------------------------------------");
            

            while (rs.next()) {
                int id = rs.getInt("A_custId");
                String name = rs.getString("A_accountName");
                int balance = rs.getInt("A_balance");
                int age = rs.getInt("A_age");
                String phone = rs.getString("A_phone");
                System.out.printf("%10s %20s %10s %7s %18s\n", id , name, balance, age, phone);
                //displayGroups(custId);
            }
            //System.out.println("SUCCESS");

            stmt.close();
            rs.close();
        }else{
            System.out.println("Account does not exist. Try Again.");
        }
    } catch (Exception e) {
        System.err.println(e.getClass().getName() + ": " + e.getMessage());
    }
}
    private int homeMenu(int custId){

        int choice = -1;
        while(choice != 0 && choice != 1){
            System.out.println("==================================== Home Menu ======================================================"); 
            System.out.println("Options: Quit[0] | Logout[1] | Buy Ticket[2] | View Purchased Tickets[3] | View Routes and Times [4] "); 
            System.out.println("                       Display Account Information[5] | View Groups[6]");
            System.out.println("===================================================================================================="); 
            
            Scanner scan = new Scanner(System.in);
            System.out.print("Enter Option: ");
            choice = scan.nextInt();  
            System.out.println("");

            final int quit = 0;
            final int logout = 1;
            final int buyTicket =2;
            final int viewPurchaseTicket =3; 
            final int viewRoutes = 4;
            final int displayAccount = 5;
            final int displayGroups = 6;

            switch(choice){
                case quit:
                    return 0;
                case logout:
                    return 1;
                case buyTicket:
                    choice = buyTicket(custId);
                    break;
                case viewPurchaseTicket:
                    viewPurchaseTickets(custId);
                    break;
                case viewRoutes:
                    printRoutes();
                    break;
                case displayAccount:
                    displayAccountInformation(custId);
                    break;
                case displayGroups:
                    displayGroups(custId);
                    break;
            }
        }

        return choice;
    }

    private int loginAccount(){

        System.out.println("===================================== Log in ========================================================"); 
            
        Scanner scan = new Scanner(System.in);
            
        System.out.print("Username: ");
        String username = scan.nextLine();
        System.out.println("");

        System.out.print("Password: ");
        String password = scan.nextLine();
        System.out.println("");

        int custId = trylogin(username , password);

        //System.out.println("custId: " + custId);
        if(custId == -1){
            return 1;
        }else{
            return homeMenu(custId);
        }
    }
  

    private void runTerminal(){

        int choice = -1;
        while(choice != 0){
            System.out.println("================================= Bus Reservation ==================================================="); 
            System.out.println("             Options: Quit[0] | Login[1]  | View Routes and Times[2]"); 
            System.out.println("====================================================================================================="); 

            Scanner scan = new Scanner(System.in);
            System.out.print("Enter Option: ");
            choice = scan.nextInt();  
            System.out.println("");

           // System.out.println("You chose: " + choice); 
            
            final int quit = 0;
            final int login = 1;
            final int viewRoutes = 2;

            switch(choice){
                case quit:
                    break;
                case login:
                    choice = loginAccount();
                    break;
                case viewRoutes:
                    choice = printRoutes();
                    break;
                default:
                    break;
            }
        }
        System.out.println("Goodbye");
    }    

    private void testsql(){
 
        //buyIndividualTicketMenu(5);
        //displayAccountInformation(5);
        //buyGroupTicketMenu(5); 

    }

    public static void main(String args[]) {
        Bus sj = new Bus();
        
        sj.openConnection("sql/data.sqlite");

        //sj.testsql();
        sj.runTerminal();
        
        sj.closeConnection();

    }
}

