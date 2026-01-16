package com.example.crudjob.controller;

import com.example.crudjob.dto.response.DecryptedTransferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.crudjob.dto.EncryptedTransferCommand;
import com.example.crudjob.dto.request.PlainTransferRequest;
import com.example.crudjob.service.TransferService;
import com.example.crudjob.utils.SecureLogUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "Transfer API",
        description = """
                API xử lý giao dịch chuyển tiền.
                Bao gồm:
                - API encrypt/decrypt dùng cho DEV/TEST
                - API transfer dùng cho PRODUCTION
                ⚠️ Dữ liệu nhạy cảm đã được RSA/AES bảo vệ
                """
)
public class TransferController {

    private final TransferService transferService;

    /**
     * =================================================
     * API ENCRYPT (DEV / TEST ONLY)
     * =================================================
     */
    @Operation(
            summary = "Encrypt transfer request (DEV/TEST)",
            description = """
                    Nhận dữ liệu PLAINTEXT và mã hoá RSA toàn bộ payload.
                    API này chỉ dùng để test Postman hoặc debug integration.
                    
                    ❌ KHÔNG DÙNG TRONG PRODUCTION
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Encrypt thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EncryptedTransferCommand.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    @PostMapping(
            value = "/encrypt",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<EncryptedTransferCommand> encryptForTest(
            @Valid
            @RequestBody
            @Parameter(
                    description = "Plain transfer request (PLAINTEXT)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = PlainTransferRequest.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "transactionId": "TXN-001",
                                              "sourceAccount": "0981234567",
                                              "targetAccount": "0978889999",
                                              "inDebt": 100000,
                                              "have": 100000,
                                              "time": "2026-01-16T10:00:00"
                                            }
                                            """
                            )
                    )
            )
            PlainTransferRequest request) {

        log.info(
                "TRANSFER_ENCRYPT_TEST | {}",
                SecureLogUtil.mask(
                        String.format(
                                "transactionId=%s | sourceAccount=%s | targetAccount=%s | inDebt=%s | have=%s | time=%s",
                                request.getTransactionId(),
                                request.getSourceAccount(),
                                request.getTargetAccount(),
                                request.getInDebt(),
                                request.getHave(),
                                request.getTime())));

        EncryptedTransferCommand encrypted = transferService.encryptTransferCommand(
                request.getTransactionId(),
                request.getSourceAccount(),
                request.getTargetAccount(),
                request.getInDebt(),
                request.getHave(),
                request.getTime());

        return ResponseEntity.ok(encrypted);
    }

    /**
     * =================================================
     * API TRANSFER (PRODUCTION)
     * =================================================
     */
    @Operation(
            summary = "Execute transfer (PRODUCTION)",
            description = """
                    Thực hiện giao dịch chuyển tiền.
                    
                    ⚠️ Yêu cầu:
                    - TẤT CẢ field phải được RSA encrypted
                    - Dữ liệu account sẽ được AES tự động khi lưu DB
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transfer thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "409", description = "Lỗi nghiệp vụ / validation"),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    @PostMapping
    public ResponseEntity<Void> transfer(
            @Valid
            @RequestBody
            @Parameter(
                    description = "Encrypted transfer command (RSA)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = EncryptedTransferCommand.class)
                    )
            )
            EncryptedTransferCommand command) {

        log.info(
                "TRANSFER_REQUEST_RECEIVED | {}",
                SecureLogUtil.mask(
                        String.format(
                                "transactionId=%s | sourceAccount=%s | targetAccount=%s | inDebt=%s | have=%s | time=%s",
                                command.getTransactionId(),
                                command.getSourceAccount(),
                                command.getTargetAccount(),
                                command.getEncryptedInDebt(),
                                command.getEncryptedHave(),
                                command.getTime())));

        transferService.transfer(command);
        return ResponseEntity.ok().build();
    }

    /**
     * =================================================
     * API DECRYPT (DEV / TEST ONLY)
     * =================================================
     */
    @Operation(
            summary = "Decrypt transfer command (DEV/TEST)",
            description = """
                    Nhận EncryptedTransferCommand và giải mã RSA trả về PLAINTEXT.
                    
                    ❌ KHÔNG DÙNG TRONG PRODUCTION
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Decrypt thành công",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = DecryptedTransferResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi giải mã")
    })
    @PostMapping(
            value = "/decrypt",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DecryptedTransferResponse> decryptForTest(
            @Valid
            @RequestBody
            @Parameter(
                    description = "Encrypted transfer command (RSA)",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = EncryptedTransferCommand.class)
                    )
            )
            EncryptedTransferCommand command) {

        log.info(
                "TRANSFER_DECRYPT_TEST | {}",
                SecureLogUtil.mask(
                        String.format(
                                "transactionId=%s | sourceAccount=%s | targetAccount=%s | inDebt=%s | have=%s | time=%s",
                                command.getTransactionId(),
                                command.getSourceAccount(),
                                command.getTargetAccount(),
                                command.getEncryptedInDebt(),
                                command.getEncryptedHave(),
                                command.getTime())));

        DecryptedTransferResponse decrypted =
                transferService.decryptTransferCommand(command);

        return ResponseEntity.ok(decrypted);
    }
}
