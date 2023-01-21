package uz.pdp.comunicationsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.comunicationsystem.entity.SimCard;
import uz.pdp.comunicationsystem.repository.PacketRepository;
import uz.pdp.comunicationsystem.repository.SimCardRepository;
import uz.pdp.comunicationsystem.repository.UssdCodeRepository;

import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.ResponseEntity.*;

@Service
public class SimCardService {
    private final SimCardRepository repository;
    private final UssdCodeRepository ussdCodeRepository;
    private final PacketRepository packetRepository;

    @Autowired
    public SimCardService(SimCardRepository repository, UssdCodeRepository ussdCodeRepository, PacketRepository packetRepository) {
        this.repository = repository;
        this.ussdCodeRepository = ussdCodeRepository;
        this.packetRepository = packetRepository;
    }

    public ResponseEntity<?> getAll(int page, int pageSize) {
        return ok(repository.findAll(PageRequest.of(page >= 1 ? page - 1 : 0, pageSize > 1 ? pageSize : 20, Sort.by("id").descending())));
    }

    public ResponseEntity<?> getOne(UUID id) {
        return repository.findById(id).map(ResponseEntity::ok).orElseGet(() -> notFound().build());
    }

    public ResponseEntity<?> simUssdCodeService(String ussdCode) {
        SimCard simCard = (SimCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return switch (ussdCode) {
            case "*100#" -> ok(format("Joriy hisob: %s", simCard.getBalance()));
            case "*101#" -> ok(format("Qoldiq SMSlar: %s", simCard.getAmountSMS()));
            case "*102#" -> ok(format("Qoldiq daqiqalar: %s",simCard.getAmountMinute()));
            case "*103#" -> ok(format("Qoldiq internet trafik: %s", simCard.getAmountMb()));
            case "*104#" -> ok(null);
            case "*555*1*1#", "*555*1*3#", "*555*1*4#", "*555*1*2#" -> internetPacketActivate(ussdCode, simCard);
            case "*111*1*1#", "*111*1*3#", "*111*1*4#", "*111*1*2#" -> smsPacketActivate(ussdCode, simCard);
            default -> badRequest().build();
        };
    }

    public ResponseEntity<?> internetPacketActivate(String ussdCode, SimCard simCard) {
        return ok("");
    }

    public ResponseEntity<?> smsPacketActivate(String ussdCode, SimCard simCard) {
        return ok("");
    }
}
