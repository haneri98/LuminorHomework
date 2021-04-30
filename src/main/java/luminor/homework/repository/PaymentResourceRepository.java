package luminor.homework.repository;

import luminor.homework.model.PaymentResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentResourceRepository extends JpaRepository<PaymentResource, String> {
}
