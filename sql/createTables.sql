Create TABLE Customer(

    C_custId Decimal(25) PRIMARY KEY,
    C_username char(32),
    C_password char(32)
);


Create TABLE Bus(

    B_busId Decimal(25) PRIMARY KEY,
    B_routeId Decimal(25),
    B_driverID Decimal(25),
    B_busName char(32)
);

Create TABLE Driver(

    D_driverId Decimal(25) PRIMARY KEY,
    D_age Decimal(25),
    D_phone char(32),
    D_shift char(32),
    D_driverName char(32),
    D_busId Decimal(25)
);

--DROP TABLE Ticket;
Create TABLE Ticket(

    T_ticketId Decimal(25) PRIMARY KEY,
    T_price Decimal(25),
    T_groupId Decimal(25),
    T_driverId Decimal(25),
    T_verified char(32),
    T_custId Decimal(25)
);

Create TABLE GroupOf(

    GO_custId Decimal(25) PRIMARY KEY,
    GO_groupId Decimal(25)
);

Create TABLE GroupB(

    G_groupId Decimal(25) PRIMARY KEY,
    G_children Decimal(25),
    G_groupSize Decimal(25),
    G_adults Decimal(25)
);

CREATE TABLE Account(
    A_custId Decimal(25) PRIMARY KEY,
    A_accountName char(32),
    A_balance Decimal(25),
    A_age Decimal(25), 
    A_phone char(32)
);

CREATE TABLE Seats(
    S_seatId Decimal(25) PRIMARY KEY
);


CREATE TABLE BusSeats(
    BS_busId Decimal(25),
    BS_seatId Decimal(25),
    BS_ticketId Decimal(25)
);

--DROP TABLE Route;
CREATE TABLE Route(
    R_routeId Decimal(25) PRIMARY KEY,
    R_depatureTime Time(0) NOT NULL,
    R_departureLocation char(32),
    R_arrivalTime Time(0) NOT NULL,
    R_arrivalLocation char(32),
    R_availableSeats Decimal(25)

);

CREATE TABLE TicketRoute (
    TR_ticketId Decimal(25),
    TR_routeID Decimal(25)
);
