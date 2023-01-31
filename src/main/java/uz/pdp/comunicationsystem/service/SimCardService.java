package uz.pdp.comunicationsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.comunicationsystem.entity.Detalizatsiya;
import uz.pdp.comunicationsystem.entity.Packet;
import uz.pdp.comunicationsystem.entity.SimCard;
import uz.pdp.comunicationsystem.entity.Tariff;
import uz.pdp.comunicationsystem.entity.enums.Action;
import uz.pdp.comunicationsystem.repository.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.*;

@Service
public class SimCardService {
    private final SimCardRepository repository;
    private final UssdCodeRepository ussdCodeRepository;
    private final PacketRepository packetRepository;
    private final DetalizatsiyaRepository detalizatsiyaRepository;
    private final TariffRepository tariffRepository;

    @Autowired
    public SimCardService(SimCardRepository repository, UssdCodeRepository ussdCodeRepository, PacketRepository packetRepository,
                          DetalizatsiyaRepository detalizatsiyaRepository,
                          TariffRepository tariffRepository) {
        this.repository = repository;
        this.ussdCodeRepository = ussdCodeRepository;
        this.packetRepository = packetRepository;
        this.detalizatsiyaRepository = detalizatsiyaRepository;
        this.tariffRepository = tariffRepository;
    }

    public ResponseEntity<?> getAll(int page, int pageSize) {
        return ok(repository.findAll(PageRequest.of(page >= 1 ? page - 1 : 0, pageSize > 1 ? pageSize : 20, Sort.by("id").descending())));
    }

    public ResponseEntity<?> getOne(UUID id) {
        return repository.findById(id).map(ResponseEntity::ok).orElseGet(() -> notFound().build());
    }

    @Transactional
    public ResponseEntity<?> simUssdCodeService(String ussdCode) {
        SimCard simCard = (SimCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return switch (ussdCode) {
            case "*100#" -> ok(format("Joriy hisob: %s", simCard.getBalance()));
            case "*101#" -> ok(format("Qoldiq SMSlar: %s", simCard.getAmountSMS()));
            case "*102#" -> ok(format("Qoldiq daqiqalar: %s", simCard.getAmountMinute()));
            case "*103#" -> ok(format("Qoldiq internet trafik: %s", simCard.getAmountMb()));
            case "*555*1*1#", "*555*1*3#", "*555*1*4#", "*555*1*2#" -> internetPacketActivate(ussdCode, simCard);
            case "*111*1*1#", "*111*1*3#", "*111*1*4#", "*111*1*2#" -> smsPacketActivate(ussdCode, simCard);
            default -> badRequest().body("Invalid USSD code");
        };
    }

    public ResponseEntity<?> internetPacketActivate(String ussdCode, SimCard simCard) {
        Optional<Packet> optionalPacket = packetRepository.findByUssdCode_Code(ussdCode);
        if (optionalPacket.isEmpty()) return badRequest().body("Packet not found");
        Packet packet = optionalPacket.get();
        boolean accessThisSimCard = false;
        for (Tariff tariff : packet.getAccessTariff()) {
            if (tariff.equals(simCard.getTariff())) {
                accessThisSimCard = true;
                break;
            }
        }
        if (!accessThisSimCard) return badRequest().body("You don't have access to this");
        if (simCard.getBalance() < packet.getPrice()) return badRequest().body("Not enough money");
        simCard.setBalance(simCard.getBalance() - packet.getPrice());
        simCard.setAmountMb(simCard.getAmountMb() + packet.getAmount());
        SimCard save = repository.save(simCard);
        List<SimCard> simCards = packet.getSimCards();
        simCards.add(simCard);
        packet.setSimCards(simCards);
        packetRepository.save(packet);
        detalizatsiyaRepository.save(new Detalizatsiya(Action.ACTION_PACKET, Double.valueOf(packet.getAmount()), save, packet.getPrice()));
        return ok("Packet activated");
    }

    public ResponseEntity<?> smsPacketActivate(String ussdCode, SimCard simCard) {
        Optional<Packet> optionalPacket = packetRepository.findByUssdCode_Code(ussdCode);
        if (optionalPacket.isEmpty()) return badRequest().body("Packet not found");
        Packet packet = optionalPacket.get();
        boolean accessThisSimCard = false;
        for (Tariff tariff : packet.getAccessTariff()) {
            if (tariff.equals(simCard.getTariff())) {
                accessThisSimCard = true;
                break;
            }
        }
        if (!accessThisSimCard) return badRequest().body("You don't have access to this");
        if (simCard.getBalance() < packet.getPrice()) return badRequest().body("Not enough money");
        simCard.setBalance(simCard.getBalance() - packet.getPrice());
        simCard.setAmountSMS(simCard.getAmountSMS() + packet.getAmount());
        SimCard save = repository.save(simCard);
        List<SimCard> simCards = packet.getSimCards();
        simCards.add(simCard);
        packet.setSimCards(simCards);
        packetRepository.save(packet);
        detalizatsiyaRepository.save(new Detalizatsiya(Action.ACTION_PACKET, Double.valueOf(packet.getAmount()), save, packet.getPrice()));
        return ok("Packet activated");
    }

    public ResponseEntity<?> changeTariff(Long tariffId) {
        tariffRepository.findById(tariffId)
                .map(tariff -> {
                    SimCard simCard = (SimCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    if (Objects.equals(tariff.getId(), simCard.getTariff().getId()))
                        return badRequest().body("You are already connected to this tariff");
                    if (simCard.getBalance() < (tariff.getPrice() + tariff.getTransferPrice()))
                        return badRequest().body("Not enough money");
                    if (!simCard.isActive()) return badRequest().body("You are not active");
                    simCard.setTariff(tariff);
                    simCard.setActive(true);
                    simCard.setAmountSMS(tariff.getSms());
                    simCard.setAmountMb(tariff.getMb());
                    simCard.setAmountMinute(tariff.getMin());
                    simCard.setBalance(simCard.getBalance() - (tariff.getPrice() + tariff.getTransferPrice()));
                    SimCard saved = repository.save(simCard);
                    detalizatsiyaRepository.save(new Detalizatsiya(Action.ACTION_TARIFF, 0D, saved, (tariff.getTransferPrice() + tariff.getPrice())));
                    return ok("Tariff changed");
                })
                .orElseGet(() -> status(NOT_FOUND).body("Tariff not found"));
        return null;
    }
}
