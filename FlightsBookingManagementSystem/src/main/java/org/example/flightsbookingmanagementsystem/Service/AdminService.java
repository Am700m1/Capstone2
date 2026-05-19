package org.example.flightsbookingmanagementsystem.Service;


import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiException;
import org.example.flightsbookingmanagementsystem.Model.*;
import org.example.flightsbookingmanagementsystem.Repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;
    private final AirplaneRepository airplaneRepository;
    private final PassengerRepository passengerRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final TicketRepository ticketRepository;


    public List<Admin> getAdmins(){
        List<Admin> admins = adminRepository.findAll();

        if(admins.isEmpty()){
            throw new ApiException("No admins exist yet!");
        }

        return admins;
    }



    public void addAdmin(Admin admin){
        adminRepository.save(admin);
    }


    public void updateAdmin(Integer id, Admin admin){
        Admin oldAdmin = adminRepository.findAdminById(id);

        if(oldAdmin == null){
            throw new ApiException("Admin was not found!");
        }

        oldAdmin.setUsername(admin.getUsername());
        oldAdmin.setEmail(admin.getEmail());
        oldAdmin.setPassword(admin.getPassword());
        oldAdmin.setSalary(admin.getSalary());

        adminRepository.save(oldAdmin);
    }



    public void deleteAdmin(Integer id){
        Admin admin = adminRepository.findAdminById(id);


        if(admin == null){
            throw new ApiException("Admin was not found!");
        }

        adminRepository.delete(admin);
    }


    public void addAirline(Integer adminId, Airline airline){
        Admin admin = adminRepository.findAdminById(adminId);

        if(admin == null){
            throw new ApiException("You are not authorized to do this action!");
        }
        airlineRepository.save(airline);
    }



    public void addFlight(Integer adminId, Flight flight){
        Airline airline = airlineRepository.findAirlineById(flight.getAirlineId());
        Airplane airplane = airplaneRepository.findAirplaneById(flight.getAirplaneId());
        Admin admin = adminRepository.findAdminById(adminId);

        if(admin == null){
            throw new ApiException("You are not authorized to do this action!");
        }

        if(airline == null){
            throw new ApiException("Airline was not found!");
        }

        if(airplane == null){
            throw new ApiException("Airplane was not found!");
        }

        flight.setAdminId(adminId);
        flightRepository.save(flight);
    }


    public void changeFlightStatus(Integer adminId, Integer flightId, String status){
        Flight flight = flightRepository.findFlightById(flightId);
        Admin admin = adminRepository.findAdminById(adminId);

        if(admin == null){
            throw new ApiException("You are not authorized to do this action!");
        }

        if(flight == null){
            throw new ApiException("Flight was not found!");
        }
        if(status.equalsIgnoreCase("cancelled")){
            List<Payment> affectedPayments = paymentRepository.findPaymentsByFlightIdAndType(flightId, "PURCHASE");
            List<Ticket> tickets = ticketRepository.findTicketsByFlightId(flightId);
            List<Passenger> passengers = passengerRepository.findPassengersByFlightId(flightId);

            if (affectedPayments.isEmpty()) {
                throw new ApiException("No affected payments were found");
            }

            for (Payment payment : affectedPayments) {
                paymentService.processAdminMassRefund(payment);
            }

            if(tickets.isEmpty()){
                throw new ApiException("No tickets were found");
            }

            if(passengers.isEmpty()){
                throw new ApiException("No passengers were found!");
            }


            ticketRepository.deleteAll(tickets);
            passengerRepository.deleteAll(passengers);

        }
        flight.setStatus(status);
        flightRepository.save(flight);
    }


    public Integer profit(Integer adminId){
        List<Payment> payments = paymentRepository.findPaymentByType("PURCHASE");
        Admin admin = adminRepository.findAdminById(adminId);

        if(admin == null){
            throw new ApiException("You are not authorized to do this action!");
        }

        if(payments.isEmpty()){
            throw new ApiException("No purchase payments were found!");
        }

        Integer sum = 0;

        for(Payment payment: payments){
            sum += payment.getAmount();
        }

        return sum;
    }

}
