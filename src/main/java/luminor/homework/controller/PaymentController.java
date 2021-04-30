package luminor.homework.controller;

import luminor.homework.dto.PaymentResourceDto;
import luminor.homework.model.PaymentResource;
import luminor.homework.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    /**
     * @param iban Optional filter parameter
     * @return All payment resources or all filtered payment resources if iban provided
     */
    @GetMapping("payments")
    public List<PaymentResource> getPayments(@RequestParam(value = "debtorIban", required = false) String iban) {
        return paymentService.getAll(iban);
    }

    /**
     * @param dto DTO of PaymentResource, which contains only amount and debtorIban
     * @param request Request information, which is used to find the caller's IP if possible
     * @return Created PaymentResource
     */
    @PostMapping("payments")
    public PaymentResource save(@RequestBody PaymentResourceDto dto, HttpServletRequest request) {
        return paymentService.save(dto, request);
    }

    /**
     * @param file CSV file headers "amount" and "debtorIban"
     * @param request Request information, which is used to find the caller's IP if possible
     * @return List of all successfully created PaymentResources
     */
    @PostMapping("payment-files")
    public List<PaymentResource> saveFromCsv(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return paymentService.saveFromCsv(file, request);
    }

}
