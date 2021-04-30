package luminor.homework.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResourceDto {

    @CsvBindByName(column = "amount")
    private Double amount;
    @CsvBindByName(column = "debtorIban")
    private String debtorIban;

}
