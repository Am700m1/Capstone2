package org.example.flightsbookingmanagementsystem.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.example.flightsbookingmanagementsystem.Api.ApiException;
import org.example.flightsbookingmanagementsystem.Api.EmailApi;
import org.example.flightsbookingmanagementsystem.Model.*;
import org.example.flightsbookingmanagementsystem.Repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final CardRepository cardRepository;
    private final TicketRepository ticketRepository;
    private final EmailApi emailApi;

    public List<Payment> getPayments(){
        List<Payment> payments = paymentRepository.findAll();

        if (payments.isEmpty()){
            throw new ApiException("No payments exist yet!");
        }

        return payments;
    }

    public void addPayment(Payment payment){
        User user = userRepository.findUserById(payment.getUserId());
        Flight flight = flightRepository.findFlightById(payment.getFlightId());
        Card card = cardRepository.findCardByCardNumber(payment.getCardNumber());

        if(user == null) throw new ApiException("User was not found!");
        if(flight == null) throw new ApiException("Flight was not found!");
        if(card == null || !card.getUserId().equals(user.getId())) throw new ApiException("Card was not found!");
        if(flight.getPrice() > card.getBalance()) throw new ApiException("The balance of the card is not enough!");

        paymentRepository.save(payment);
    }

    public void updatePayment(Integer id, Payment payment){
        Payment oldPayment = paymentRepository.findPaymentById(id);
        if(oldPayment == null) throw new ApiException("Payment was not found!");

        oldPayment.setUserId(payment.getUserId());
        oldPayment.setFlightId(payment.getFlightId());
        oldPayment.setCardNumber(payment.getCardNumber());
        oldPayment.setAmount(payment.getAmount());
        oldPayment.setType(payment.getType());

        paymentRepository.save(oldPayment);
    }

    public void deletePayment(Integer id){
        Payment payment = paymentRepository.findPaymentById(id);
        if(payment == null) throw new ApiException("Payment was not found!");
        paymentRepository.delete(payment);
    }

    public void paymentProcessing(Integer ticketId, Long cardNumber){
        Card card = cardRepository.findCardByCardNumber(cardNumber);
        Ticket ticket = ticketRepository.findTicketById(ticketId);
        if(ticket == null) throw new ApiException("Ticket was not found!");

        User user = userRepository.findUserById(ticket.getUserId());
        Flight flight = flightRepository.findFlightById(ticket.getFlightId());

        if(ticket.getCost() > card.getBalance()) throw new ApiException("Card balance is not enough to complete this transaction!");
        if(flight.getSeats() <= 0) throw new ApiException("There is no available seats!");

        card.setBalance(card.getBalance() - ticket.getCost());
        cardRepository.save(card);

        Payment payment = new Payment();
        payment.setUserId(ticket.getUserId());
        payment.setFlightId(ticket.getFlightId());
        payment.setCardNumber(cardNumber);
        payment.setAmount(ticket.getCost());
        payment.setType("PURCHASE");
        paymentRepository.save(payment);

        flight.setSeats(flight.getSeats() - 1);
        flightRepository.save(flight);

        byte[] pdfBytes = generateTransactionPdf(user, payment, card, "PURCHASE RECEIPT");
        String htmlMessage = buildTransactionHtmlEmail(user.getUsername(), payment, flight, "Payment Successful! ✅", "Your payment has been successfully processed. Please find your official receipt attached.");

        emailApi.sendEmailWithSingleAttachment(user.getEmail(), "Payment Receipt: Flight " + flight.getId(), htmlMessage, pdfBytes, "Purchase_Receipt.pdf");
    }

    public void refundProcessing(Integer ticketId, Long cardNumber){
        Card card = cardRepository.findCardByCardNumber(cardNumber);
        Ticket ticket = ticketRepository.findTicketById(ticketId);
        User user = userRepository.findUserById(ticket.getUserId());
        Flight flight = flightRepository.findFlightById(ticket.getFlightId());

        List<Payment> payments = paymentRepository.findPaymentsByUserId(user.getId());
        Payment payment = payments.stream()
                .filter(p -> p.getFlightId().equals(flight.getId()) && p.getType().equals("PURCHASE"))
                .findFirst()
                .orElseThrow(() -> new ApiException("Payment was not found!"));

        if(payment.getType().equalsIgnoreCase("CANCELLED")) {
            throw new ApiException("Payment is already cancelled and refunded!");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departureTime = flight.getDeparture();

        long hoursUntilFlight = Duration.between(now, departureTime).toHours();

        if (hoursUntilFlight < 3) {
            throw new ApiException("Refunds are not permitted less than 3 hours before departure or after the flight has flown.");
        }

        double refundPercentage = 1.0;

        if (hoursUntilFlight <= 48) {
            refundPercentage = 0.60;
        } else if (hoursUntilFlight <= 168) {
            refundPercentage = 0.80;
        }

        Integer refundAmount = (int) (ticket.getCost() * refundPercentage);

        card.setBalance(card.getBalance() + refundAmount);
        cardRepository.save(card);

        payment.setType("CANCELLED");
        payment.setAmount(refundAmount);
        paymentRepository.save(payment);

        flight.setSeats(flight.getSeats() + 1);
        flightRepository.save(flight);

        String policyApplied = refundPercentage == 1.0 ? "100%" : (refundPercentage == 0.80 ? "80%" : "60%");
        String emailBodyText = "Your ticket has been successfully cancelled. According to our cancellation policy timeframe, a "
                + policyApplied + " refund of $" + refundAmount + " has been securely returned to your card.";

        byte[] pdfBytes = generateTransactionPdf(user, payment, card, "REFUND RECEIPT");
        String htmlMessage = buildTransactionHtmlEmail(user.getUsername(), payment, flight, "Refund Processed 🔄", emailBodyText);

        emailApi.sendEmailWithSingleAttachment(user.getEmail(), "Refund Receipt: Flight " + flight.getId(), htmlMessage, pdfBytes, "Refund_Receipt.pdf");
    }

    @Transactional
    public void processAdminMassRefund(Payment payment) {
        Card card = cardRepository.findCardByCardNumber(payment.getCardNumber());
        User user = userRepository.findUserById(payment.getUserId());
        Flight flight = flightRepository.findFlightById(payment.getFlightId());

        if(card == null) throw new ApiException("No card was found!");
        if(payment.getType().equalsIgnoreCase("CANCELLED")) return;

        card.setBalance(card.getBalance() + payment.getAmount());
        cardRepository.save(card);

        payment.setType("CANCELLED");
        paymentRepository.save(payment);

//        PDF and HTML Email message generators
        byte[] pdfBytes = generateTransactionPdf(user, payment, card, "CANCELLATION REFUND RECEIPT");
        String htmlMessage = buildTransactionHtmlEmail(user.getUsername(), payment, flight, "Flight Cancelled ⚠️", "We regret to inform you that your flight was cancelled. A full refund has been issued to your card.");

        emailApi.sendEmailWithSingleAttachment(user.getEmail(), "Flight Cancellation Notice: " + flight.getId(), htmlMessage, pdfBytes, "Cancellation_Refund.pdf");
    }

    public List<Payment> getMyPayments(Integer userId) {
        List<Payment> payments = paymentRepository.findPaymentsByUserId(userId);
        if (payments.isEmpty()) throw new ApiException("No transaction history found.");
        return payments;
    }

//    Helper methods to generate pdf and HTML email messages
    private byte[] generateTransactionPdf(User user, Payment payment, Card card, String documentTitle) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, documentTitle.contains("REFUND") ? Color.RED : Color.BLUE);
            Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK);

            document.add(new Paragraph(documentTitle, titleFont));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("Account Name: " + user.getUsername(), textFont));
            document.add(new Paragraph("Account Email: " + user.getEmail(), textFont));
            document.add(new Paragraph("Card Billed: **** **** **** " + String.valueOf(card.getCardNumber()).substring(12), textFont));
            document.add(new Paragraph("--------------------------------------------------"));
            document.add(new Paragraph("Flight ID: " + payment.getFlightId(), textFont));
            document.add(new Paragraph("Transaction Type: " + payment.getType(), textFont));
            document.add(new Paragraph("\nTOTAL AMOUNT: $" + payment.getAmount(), boldFont));

            document.close();
        } catch (Exception e) {
            throw new ApiException("Error generating transaction PDF");
        }
        return out.toByteArray();
    }

    private String buildTransactionHtmlEmail(String username, Payment payment, Flight flight, String headerTitle, String subMessage) {
        String headerColor = payment.getType().equals("CANCELLED") ? "#ef4444" : "#10b981"; // Red for cancel, Green for purchase

        return "<div style=\"font-family: Arial, sans-serif; background-color: #121212; padding: 40px; color: #ffffff;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; background-color: #1e1e1e; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.5);\">" +
                "  <div style=\"background-color: " + headerColor + "; padding: 30px; text-align: center;\">" +
                "    <h1 style=\"color: white; margin: 0; font-size: 28px;\">" + headerTitle + "</h1>" +
                "  </div>" +
                "  <div style=\"padding: 30px;\">" +
                "    <h3 style=\"color: #e0e0e0; font-size: 20px;\">Hello, " + username + "</h3>" +
                "    <p style=\"color: #a0a0a0; line-height: 1.6;\">" + subMessage + "</p>" +
                "    <div style=\"background-color: #2a2a2a; padding: 20px; border-radius: 8px; margin-top: 25px; border-left: 4px solid " + headerColor + ";\">" +
                "      <h4 style=\"margin-top: 0; color: " + headerColor + ";\">Transaction Details</h4>" +
                "      <p style=\"margin: 5px 0; color: #cccccc;\"><strong>Flight Route:</strong> " + flight.getDepartureCity() + " ➔ " + flight.getDestinationCity() + "</p>" +
                "      <p style=\"margin: 5px 0; color: #cccccc;\"><strong>Amount:</strong> $" + payment.getAmount() + "</p>" +
                "      <p style=\"margin: 5px 0; color: #cccccc;\"><strong>Status:</strong> " + payment.getType() + "</p>" +
                "    </div>" +
                "  </div>" +
                "</div>" +
                "</div>";
    }
}