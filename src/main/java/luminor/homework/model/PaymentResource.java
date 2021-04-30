package luminor.homework.model;

import lombok.Getter;
import lombok.Setter;
import luminor.homework.dto.PaymentResourceDto;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Getter
@Entity
public class PaymentResource {

    @Id
    private final String id;
    private Double amount;
    private String debtorIban;
    private final Instant createAt;
    @Setter
    private String callerCountry;

    public PaymentResource() {
        this.id = UUID.randomUUID().toString();
        this.createAt = Instant.now();
    }

    public PaymentResource(PaymentResourceDto dto) {
        this.id = UUID.randomUUID().toString();
        this.createAt = Instant.now();
        this.amount = dto.getAmount();
        this.debtorIban = dto.getDebtorIban();
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof PaymentResource)) {
            return false;
        }
        PaymentResource comparable = (PaymentResource) object;
        return getId().equals(comparable.getId());
    }
}
