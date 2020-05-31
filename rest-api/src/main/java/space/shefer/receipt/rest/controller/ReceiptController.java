package space.shefer.receipt.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import space.shefer.receipt.platform.core.entity.Receipt;
import space.shefer.receipt.platform.core.entity.UserProfile;
import space.shefer.receipt.platform.core.service.UserProfileService;
import space.shefer.receipt.rest.converters.ReceiptMetaConverter;
import space.shefer.receipt.rest.dto.ReceiptCreateDto;
import space.shefer.receipt.rest.dto.ReceiptMetaDto;
import space.shefer.receipt.rest.service.ReceiptService;

@Schema(description = "Managing receipts")
@RestController
@RequiredArgsConstructor
public class ReceiptController {

  private final ReceiptService receiptService;
  private final UserProfileService userProfileService;

  @RequestMapping(value = "/create", method = RequestMethod.POST)
  public ReceiptMetaDto create(@RequestBody ReceiptCreateDto query,
                               @Nullable @RequestHeader("Authorization") String authHeader) {
    UserProfile userProfile = null;
    if (authHeader != null) {
      userProfile = userProfileService.getUserByToken(authHeader.substring(authHeader.indexOf(" ") + 1));
    }
    Receipt receipt = receiptService.create(query, userProfile);
    return ReceiptMetaConverter.toDto(receipt);
  }

  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Receipt has been deleted or not found"),
    @ApiResponse(responseCode = "400", description = "Receipt already loaded")
  })
  @Operation(description = "Allows remove receipt if it is stuck in loading")
  @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
  public void delete(
    @Parameter(description = "Receipt identifier", required = true)
    @RequestParam long id
  ) {
    receiptService.deleteReceipt(id);
  }

}
