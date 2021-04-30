package luminor.homework.service;

import com.opencsv.CSVParser;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.util.OpencsvUtils;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import luminor.homework.HomeworkApplication;
import luminor.homework.dto.PaymentResourceDto;
import luminor.homework.exception.InvalidCsvException;
import luminor.homework.exception.InvalidPaymentResourceException;
import luminor.homework.model.PaymentResource;
import luminor.homework.repository.PaymentResourceRepository;
import nl.garvelink.iban.IBAN;
import nl.garvelink.iban.Modulo97;
import nl.garvelink.iban.UnknownCountryCodeException;
import nl.garvelink.iban.WrongChecksumException;
import nl.garvelink.iban.WrongLengthException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentResourceRepository paymentRepository;
    @Autowired
    private ApiService apiService;
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeworkApplication.class);
    private final List<String> validCountryCodes = Arrays.asList("LV", "LT", "EE");

    public PaymentResource save(PaymentResourceDto dto, HttpServletRequest request) {
        String country = apiService.getRequestCountry(request);
        dtoValidation(dto);
        PaymentResource pr = new PaymentResource(dto);
        pr.setCallerCountry(country);
        if (pr.getAmount() <= 0) {
            throw new InvalidPaymentResourceException("Amount must be greater than 0");
        }
        ibanValidation(pr);
        return paymentRepository.save(pr);
    }

    public List<PaymentResource> getAll(String iban) {
        if (iban != null) {
            return paymentRepository.findAll().stream().filter(pr -> pr.getDebtorIban().equals(iban)).collect(Collectors.toList());
        }
        return paymentRepository.findAll();
    }

    public List<PaymentResource> saveFromCsv(MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new InvalidCsvException("File is empty");
        }

        try {
            Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            CsvToBean<PaymentResourceDto> csvToBean = new CsvToBeanBuilder<PaymentResourceDto>(reader)
                    .withType(PaymentResourceDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            List<PaymentResourceDto> prDtos = csvToBean.parse();
            List<PaymentResource> prs = new ArrayList<>();
            for (PaymentResourceDto dto : prDtos) {
                try {
                    prs.add(save(dto, request));
                } catch (Exception e) {
                    LOGGER.error("Could not save DTO from CSV, reason: " + e.getMessage());
                }
            }
            return prs;
        } catch (Exception e) {
            throw new InvalidCsvException("File could not be parsed: " + e.getCause().toString());
        }
    }

    private void ibanValidation(PaymentResource pr) {
        try {
            if (pr.getDebtorIban().equals("") || IBAN.valueOf(pr.getDebtorIban()) == null) {
                throw new InvalidPaymentResourceException("IBAN is missing");
            }
        } catch (WrongLengthException wrongLengthException) {
            throw new InvalidPaymentResourceException("IBAN is of wrong length");
        } catch (WrongChecksumException wrongChecksumException) {
            throw new InvalidPaymentResourceException("IBAN failed checksum validation");
        }

        if (!Modulo97.verifyCheckDigits(IBAN.valueOf(pr.getDebtorIban()).toString())) {
            throw new InvalidPaymentResourceException("IBAN check digits are incorrect");
        }

        try {
            if (!validCountryCodes.contains(IBAN.valueOf(pr.getDebtorIban()).getCountryCode())) {
                throw new InvalidPaymentResourceException("IBAN must belong to a Baltic country");
            }
        } catch (UnknownCountryCodeException unknownCountryCodeException) {
            throw new InvalidPaymentResourceException("IBAN country code is unknown");
        }
    }

    private void dtoValidation(PaymentResourceDto dto) {
        if (dto.getAmount() == null || dto.getDebtorIban() == null) {
            throw new InvalidPaymentResourceException("Data transfer object is invalid");
        }
    }

    // Workaround to opencsv instantiation error
    public PaymentResourceDto newDto(Double amount, String debtorIban) {
        PaymentResourceDto dto = new PaymentResourceDto();
        dto.setAmount(amount);
        dto.setDebtorIban(debtorIban);
        return dto;
    }
}
