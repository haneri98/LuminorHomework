package luminor.homework.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import luminor.homework.dto.PaymentResourceDto;
import luminor.homework.model.PaymentResource;
import luminor.homework.repository.PaymentResourceRepository;
import luminor.homework.service.PaymentService;
import org.apache.logging.log4j.CloseableThreadContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PaymentControllerTest {

    @Autowired
    private PaymentController controller;
    @Autowired
    PaymentResourceRepository repository;
    @Autowired
    PaymentService service;

    @Autowired
    private MockMvc mvc;

    // IP: 43.131.255.255 belongs to Germany
    @Test
    void testCallerCountry() throws Exception {
        PaymentResourceDto dto = service.newDto(15d, "EE591276394863483546");
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String dtoJson = mapper.writeValueAsString(dto);
        PaymentResource pr = mapper.readValue(mvc.perform(MockMvcRequestBuilders.post("/payments")
                .contentType(MediaType.APPLICATION_JSON).content(dtoJson)
                .header("X-Forwarded-For", "43.131.255.255").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn().getResponse()
                .getContentAsString(), PaymentResource.class);
        assertEquals("Germany", pr.getCallerCountry());
    }
}
