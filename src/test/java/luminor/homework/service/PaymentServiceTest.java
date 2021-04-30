package luminor.homework.service;

import luminor.homework.dto.PaymentResourceDto;
import luminor.homework.exception.InvalidCsvException;
import luminor.homework.exception.InvalidPaymentResourceException;
import luminor.homework.model.PaymentResource;
import luminor.homework.repository.PaymentResourceRepository;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


@SpringBootTest
public class PaymentServiceTest {

    @Autowired
    private PaymentService service;
    @Autowired
    private PaymentResourceRepository repository;

    @Test
    void testSaveSuccessEstonia() {
        PaymentResourceDto dto = service.newDto(15d, "EE401267248726881971");
        PaymentResource pr = service.save(dto, null);
        assertEquals(pr.getAmount(), 15d);
        assertEquals(pr.getDebtorIban(), "EE401267248726881971");
        assertNotNull(pr.getDebtorIban());
        assertNotNull(pr.getCreateAt());
        assertEquals(pr.getCallerCountry(), "Unknown");
    }

    @Test
    void testSaveSuccessLatvia() {
        PaymentResourceDto dto = service.newDto(99d, "LV80BANK0000435195001");
        PaymentResource pr = service.save(dto, null);
        assertEquals(pr.getAmount(), 99d);
        assertEquals(pr.getDebtorIban(), "LV80BANK0000435195001");
        assertNotNull(pr.getDebtorIban());
        assertNotNull(pr.getCreateAt());
        assertEquals(pr.getCallerCountry(), "Unknown");
    }

    @Test
    void testSaveSuccessLithuania() {
        PaymentResourceDto dto = service.newDto(30d, "LT772566531536541423");
        PaymentResource pr = service.save(dto, null);
        assertEquals(pr.getAmount(), 30d);
        assertEquals(pr.getDebtorIban(), "LT772566531536541423");
        assertNotNull(pr.getDebtorIban());
        assertNotNull(pr.getCreateAt());
        assertEquals(pr.getCallerCountry(), "Unknown");
    }

    @Test
    void testSaveFailureAmountIsInvalid() {
        PaymentResourceDto dto = service.newDto(-3d, "EE771291274732756548");
        Exception ex = assertThrows(InvalidPaymentResourceException.class, () -> service.save(dto, null));
        assertEquals("Amount must be greater than 0", ex.getMessage());
    }

    @Test
    void testSaveFailureIbanMissing() {
        PaymentResourceDto dto = service.newDto(15d, "");
        Exception ex = assertThrows(InvalidPaymentResourceException.class, () -> service.save(dto, null));
        assertEquals("IBAN is missing", ex.getMessage());
    }

    @Test
    void testSaveFailureIbanWrongLength() {
        PaymentResourceDto dto = service.newDto(15d, "EE351216988891872359123");
        Exception ex = assertThrows(InvalidPaymentResourceException.class, () -> service.save(dto, null));
        assertEquals("IBAN is of wrong length", ex.getMessage());
    }

    @Test
    void testSaveFailureIbanFailedChecksumValidation() {
        PaymentResourceDto dto = service.newDto(15d, "EE531269366567691230");
        Exception ex = assertThrows(InvalidPaymentResourceException.class, () -> service.save(dto, null));
        assertEquals("IBAN failed checksum validation", ex.getMessage());
    }

    @Test
    void testSaveFailureCountryMustBeBaltic() {
        PaymentResourceDto dto = service.newDto(15d, "HR5424840088578492846");
        Exception ex = assertThrows(InvalidPaymentResourceException.class, () -> service.save(dto, null));
        assertEquals("IBAN must belong to a Baltic country", ex.getMessage());
    }

    @Test
    void testSaveFailureInvalidDto() {
        PaymentResourceDto dto = service.newDto(null, null);
        Exception ex = assertThrows(InvalidPaymentResourceException.class, () -> service.save(dto, null));
        assertEquals("Data transfer object is invalid", ex.getMessage());
    }

    @Test
    void testGetAllNoIban() {
        repository.deleteAll();
        PaymentResourceDto dto1 = service.newDto(10d, "LT081681325556321197");
        PaymentResourceDto dto2 = service.newDto(10d, "LT916454852884863162");
        PaymentResourceDto dto3 = service.newDto(10d, "LT972446973328255187");
        service.save(dto1, null);
        service.save(dto2, null);
        service.save(dto3, null);

        assertEquals(3, service.getAll(null).size());
    }

    @Test
    void testGetAllWithIban() {
        repository.deleteAll();
        PaymentResourceDto dto1 = service.newDto(10d, "EE371298332943239223");
        PaymentResourceDto dto2 = service.newDto(10d, "EE371298332943239223");
        PaymentResourceDto dto3 = service.newDto(10d, "EE911272179238947250");
        PaymentResourceDto dto4 = service.newDto(10d, "EE091218826355614317");
        service.save(dto1, null);
        service.save(dto2, null);
        service.save(dto3, null);
        service.save(dto4, null);

        assertEquals(2, service.getAll("EE371298332943239223").size());
        assertEquals(1, service.getAll("EE911272179238947250").size());
        assertEquals(1, service.getAll("EE091218826355614317").size());
    }


    /**
     *  8 out of 11 rows in the test CSV file will create a PaymentResource
     */
    @Test
    void testSaveFromCsvSuccess() throws IOException {
        File file = new File("src/test/resources/testCSV.csv");
        InputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                "text/csv", input);
        assertEquals(8, service.saveFromCsv(multipartFile, null).size());
    }

    @Test
    void testSaveFromCsvFailureFileEmpty() throws IOException {
        File file = new File("src/test/resources/testEmpty.csv");
        InputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                "text/csv", input);
        Exception ex = assertThrows(InvalidCsvException.class, () -> service.saveFromCsv(multipartFile, null));
        assertEquals("File is empty", ex.getMessage());
    }

    @Test
    void testSaveFromCsvFailureCannotParse() throws IOException {
        File file = new File("src/test/resources/testUnparsable.csv");
        InputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                "text/csv", input);
        Exception ex = assertThrows(InvalidCsvException.class, () -> service.saveFromCsv(multipartFile, null));
        assertTrue(ex.getMessage().contains("File could not be parsed"));
    }
}
