.mode "csv"
.separator ","
.headers off
.import '| tail -n +2 data/Account.csv' Account
.import '| tail -n +2 data/Bus.csv' Bus
.import '| tail -n +2 data/Bus-Seats.csv' BusSeats
.import '| tail -n +2 data/Customer.csv' Customer
.import '| tail -n +2 data/Driver.csv' Driver
.import '| tail -n +2 data/Group.csv' GroupB
.import '| tail -n +2 data/GroupOf.csv' GroupOf
.import '| tail -n +2 data/Route.csv' Route
.import '| tail -n +2 data/Seats.csv' Seats
.import '| tail -n +2 data/Ticket.csv' Ticket
.import '| tail -n +2 data/Ticket-Route.csv' TicketRoute



