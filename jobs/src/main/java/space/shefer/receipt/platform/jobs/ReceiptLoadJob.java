package space.shefer.receipt.platform.jobs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import space.shefer.receipt.fnssdk.dto.FnsResponseDto;
import space.shefer.receipt.fnssdk.excepion.AuthorizationFailedException;
import space.shefer.receipt.fnssdk.excepion.ReceiptNotFoundException;
import space.shefer.receipt.fnssdk.service.FnsService;
import space.shefer.receipt.platform.core.dto.ReceiptProvider;
import space.shefer.receipt.platform.core.dto.ReceiptStatus;
import space.shefer.receipt.platform.core.entity.Receipt;
import space.shefer.receipt.platform.core.repository.ReceiptRepository;
import space.shefer.receipt.platform.core.service.FnsReceiptService;
import space.shefer.receipt.platform.jobs.service.ReceiptService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReceiptLoadJob {

  private final ReceiptRepository receiptRepository;
  private final ReceiptService receiptService;
  private final FnsService fnsService;
  private final FnsReceiptService fnsReceiptService;

  @Value("${loader.attempts.limit}")
  private long loadAttemptsLimit;

  @Scheduled(fixedDelay = 10000)
  public void load() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    List<Receipt> receipts = receiptService.getAllIdle();

    long receiptsFalseAttemptLoad = receipts.stream()
      .filter(it -> it.getLoadAttempts() >= loadAttemptsLimit)
      .count();

    List<Receipt> receiptsTrueAttemptLoad = receipts.stream()
      .filter(it -> it.getLoadAttempts() < loadAttemptsLimit)
      .collect(Collectors.toList());

    System.out.println("Starting loading " + receiptsTrueAttemptLoad.size() + " receipts");
    System.out.println("Load attempts exceeded for " + receiptsFalseAttemptLoad + " receipts");

    receiptsTrueAttemptLoad.forEach(receipt -> {
        String receiptUserProfilePhone = null;
        String receiptUserProfilePassword = null;

        if (receipt.getUserProfile() != null) {
          receiptUserProfilePhone = receipt.getUserProfile().getPhone();
          receiptUserProfilePassword = receipt.getUserProfile().getPassword();
        }

        try {
          String rawReceipt = fnsService.getReceiptExists(
            receipt.getFn(),
            receipt.getFd(),
            receipt.getFp(),
            receipt.getDate().format(dateTimeFormatter),
            receipt.getSum().floatValue(),
            receiptUserProfilePhone,
            receiptUserProfilePassword
          );

          if (rawReceipt != null) {
            fnsReceiptService.update(
              FnsResponseDto.fromString(rawReceipt).document.receipt,
              receipt,
              ReceiptProvider.NALOG.name()
            );
          }
          else {
            receipt.setStatus(ReceiptStatus.FAILED);
          }
        }
        catch (AuthorizationFailedException e) {
          e.printStackTrace();
        }
        catch (ReceiptNotFoundException e) {
          receipt.setStatus(ReceiptStatus.FAILED);
          e.printStackTrace();
        }
        catch (Exception e) {
          receipt.setStatus(ReceiptStatus.IDLE);
          e.printStackTrace();
        }
        finally {
          receipt.setLoadAttempts(receipt.getLoadAttempts() + 1);
          receiptRepository.save(receipt);
        }
      }
    );
  }

}
