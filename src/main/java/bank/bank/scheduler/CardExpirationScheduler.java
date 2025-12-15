package bank.bank.scheduler;

import java.util.Date;
import java.util.List;
import bank.bank.entity.*;
import java.math.BigDecimal;
import bank.bank.repository.*;
import bank.bank.entity.enums.*;
import lombok.RequiredArgsConstructor;
import bank.bank.service.IEmailService;
import bank.bank.util.EmailTemplateUtil;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

@Component
@RequiredArgsConstructor
public class CardExpirationScheduler {

        private final CardRepository cardRepository;
        private final TransactionHistoryRepository historyRepository;
        private final IEmailService emailService;

        @Scheduled(cron = "0 0 2 * * ?")
        public void checkExpiredCards() {

                List<Card> expiredCards = cardRepository.findByExpirationDateBeforeAndStatus(
                                (java.sql.Date) new Date(), CardStatus.ACTIVE);

                for (Card expired : expiredCards) {

                        expired.setStatus(CardStatus.DEACTIVE);

                        Customer customer = expired.getCustomer();

                        BigDecimal balance = expired.getBalance();

                        List<Card> targetCards = cardRepository.findByCustomerAndStatusAndCardTypeNot(
                                        customer, CardStatus.ACTIVE, CardType.CASHBACK);

                        if (!targetCards.isEmpty() && balance.compareTo(BigDecimal.ZERO) > 0) {

                                Card target = targetCards.get(0);

                                target.setBalance(target.getBalance().add(balance));
                                expired.setBalance(BigDecimal.ZERO);

                                cardRepository.save(target);

                                TransactionHistory h = new TransactionHistory();
                                h.setOwnerCardId(expired.getId());
                                h.setFromCardNumber(expired.getCardNumber());
                                h.setToCardNumber(target.getCardNumber());
                                h.setFromCustomerName(customer.getFullName());
                                h.setToCustomerName(customer.getFullName());
                                h.setAmount(balance);
                                h.setConvertedAmount(balance);
                                h.setType("CARD_EXPIRED_AUTO_TRANSFER");
                                historyRepository.save(h);

                                String mail = "Dear " + customer.getFullName() + ",<br><br>"
                                                + "Your card has expired and has been deactivated for security reasons.<br>"
                                                + "The existing balance on your card has been automatically transferred to your other active card.<br><br>"
                                                + "<b>Deactivated card:</b> " + expired.getCardNumber() + "<br>"
                                                + "<b>Transferred amount:</b> " + balance + "<br><br>"
                                                + "Thank you for using our services.<br>"
                                                + "Sincerely,<br>"
                                                + "NexusBank Service";

                                emailService.send(
                                                customer.getEmail(),
                                                "Card Expired – Balance Transferred",
                                                EmailTemplateUtil.getFormattedEmail(mail));

                        } else {

                                String mail = "Dear " + customer.getFullName() + ",<br><br>"
                                                + "Your card has expired and has been deactivated for security reasons.<br>"
                                                + "Since you do not have another active card, the balance could not be transferred automatically.<br><br>"
                                                + "<b>Deactivated card:</b> " + expired.getCardNumber() + "<br>"
                                                + "<b>Card balance:</b> " + balance + "<br><br>"
                                                + "To retrieve the remaining funds on your card, please visit your nearest bank branch with your ID.<br><br>"
                                                + "Sincerely,<br>"
                                                + "NexusBank Service";

                                emailService.send(
                                                customer.getEmail(),
                                                "Card Expired – Balance Retained",
                                                EmailTemplateUtil.getFormattedEmail(mail));
                        }

                        cardRepository.save(expired);
                }
        }
}
