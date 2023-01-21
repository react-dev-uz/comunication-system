package uz.pdp.comunicationsystem.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.pdp.comunicationsystem.entity.Payment;
import uz.pdp.comunicationsystem.entity.enums.PaymentType;
import uz.pdp.comunicationsystem.payload.request.PayDTO;
import uz.pdp.comunicationsystem.payload.response.PayType;
import uz.pdp.comunicationsystem.repository.PaymentRepository;
import uz.pdp.comunicationsystem.repository.SimCardRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.*;
import static uz.pdp.comunicationsystem.entity.enums.PaymentType.*;

@Service
@Valid
public class PaymentService {
    private final PaymentRepository repository;
    private final SimCardRepository simCardRepository;
    private Map<Integer, PaymentType> types = new HashMap<>();

    @Autowired
    public PaymentService(PaymentRepository repository, SimCardRepository simCardRepository) {
        this.repository = repository;
        this.simCardRepository = simCardRepository;
        types.put(PAYMENT_CASH.ordinal(), PAYMENT_CASH);
        types.put(PAYMENT_TRANSFER_CLICK.ordinal(), PAYMENT_TRANSFER_CLICK);
        types.put(PAYMENT_TRANSFER_PAYME.ordinal(), PAYMENT_TRANSFER_PAYME);
    }

    public ResponseEntity<?> getAll(Integer page, Integer size, String sort, String filter) {
        PageRequest pageRequest = PageRequest.of(page > 1 ? page - 1 : 0, size > 1 ? size : 10, filter == "desc" ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return ok(repository.findAll(pageRequest));
    }

    public ResponseEntity<?> getSimCardHistory(@NotBlank String code, @NotBlank String number) {
        return repository.findAllBySimCard_CodeAndSimCard_Number(code, number).map(ResponseEntity::ok).orElseGet(()-> notFound().build());
    }

    public ResponseEntity<?> getPayTypes() {
        return ok(Set.of(
                PayType.build(PAYMENT_CASH.ordinal() + 1, PAYMENT_CASH),
                PayType.build(PAYMENT_TRANSFER_CLICK.ordinal() + 1, PAYMENT_TRANSFER_CLICK),
                PayType.build(PAYMENT_TRANSFER_PAYME.ordinal() + 1, PAYMENT_TRANSFER_PAYME)
        ));
    }

    public ResponseEntity<?> addNewPay(PayDTO dto) {
        return simCardRepository.findByCodeAndNumber(dto.getCode(), dto.getNumber()).map(simCard -> {
            if (!types.containsKey(dto.getPayId()))
                return status(NOT_FOUND).body("Berilgan qiymatli to'lov turi mavjud emas");
            simCard.setBalance(simCard.getBalance() + dto.getAmount());
            simCardRepository.save(simCard);
            Payment payment = new Payment(null, dto.getAmount(), dto.getPayerFullName(), types.get(dto.getPayId() - 1), simCard);
            return ok(repository.save(payment));
        }).orElseGet(() -> notFound().build());
    }
}
