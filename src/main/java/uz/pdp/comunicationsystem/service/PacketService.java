package uz.pdp.comunicationsystem.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.pdp.comunicationsystem.entity.Packet;
import uz.pdp.comunicationsystem.payload.response.PacketType;
import uz.pdp.comunicationsystem.entity.Tariff;
import uz.pdp.comunicationsystem.entity.UssdCode;
import uz.pdp.comunicationsystem.payload.request.PacketDTO;
import uz.pdp.comunicationsystem.repository.PacketRepository;
import uz.pdp.comunicationsystem.repository.TariffRepository;
import uz.pdp.comunicationsystem.repository.UssdCodeRepository;

import java.util.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;
import static uz.pdp.comunicationsystem.entity.enums.PacketType.*;

@Service
public class PacketService {
    private final PacketRepository repository;
    private final UssdCodeRepository ussdCodeRepository;
    private final TariffRepository tariffRepository;
    private Map<Integer, uz.pdp.comunicationsystem.entity.enums.PacketType> types = new HashMap<>();

    @Autowired
    public PacketService(PacketRepository repository, UssdCodeRepository ussdCodeRepository, TariffRepository tariffRepository) {
        this.repository = repository;
        this.ussdCodeRepository = ussdCodeRepository;
        this.tariffRepository = tariffRepository;
        types.put(MB.ordinal(), MB);
        types.put(SMS.ordinal(), SMS);
        types.put(MIN.ordinal(), MIN);
    }

    public ResponseEntity<?> packets(String sort) {
        return ok(repository.findAll(Sort.by(sort)));
    }

    public ResponseEntity<?> packet(Long packetId) {
        return repository.findById(packetId).map(ResponseEntity::ok).orElseGet(() -> notFound().build());
    }

    public ResponseEntity<Set<PacketType>> packetTypes() {
        return ok(Set.of(
                PacketType.build(MIN.ordinal() + 1, MIN),
                PacketType.build(SMS.ordinal() + 1, SMS),
                PacketType.build(MB.ordinal() + 1, MB)
        ));
    }

    public ResponseEntity<?> addPacket(@Valid PacketDTO dto) {
        if (repository.existsByName(dto.getName()))
            return status(UNPROCESSABLE_ENTITY).body("Ushbu paket nomi oldinroq ro'yhatga olingan.");
        if (!types.containsKey(dto.getTypeId() - 1))
            return status(NOT_FOUND).body("Packet type topildmadi. Iltimos tekshirib qayta kiriting :)");
        Optional<UssdCode> optionalUssdCode = ussdCodeRepository.findById(dto.getUssdId());
        if (optionalUssdCode.isEmpty())
            return badRequest().body("Iltimos mavjud ussd kodni tog'ri kiritganligingizga ishonch hosil qilib qayta urunib ko'ring");
        Set<Tariff> tariffs = checkTariff(dto.getTariffsId());
        Packet packet = new Packet(dto.getName(), dto.getAmount(), dto.getPrice(), dto.getActiveDay(), types.get(dto.getTypeId()), optionalUssdCode.get(), null);
        if (tariffs.size() > 0) packet.setAccessTariff(tariffs);
        return status(CREATED).body(repository.save(packet));
    }

    public ResponseEntity<?> editPacket(Long id, @Valid PacketDTO dto) {
        return repository.findById(id).map(packet -> {
            packet.setName(dto.getName());
            packet.setAmount(dto.getAmount());
            packet.setPrice(dto.getPrice());
            packet.setActiveTime(dto.getActiveDay());
            packet.setPacketType(types.containsKey(dto.getTypeId()) ? types.get(dto.getTypeId()) : packet.getPacketType());
            if (!repository.existsByUssdCode_IdAndIdNot(dto.getUssdId(), packet.getId()))
                ussdCodeRepository.findById(dto.getUssdId()).ifPresent(packet::setUssdCode);
            packet.setAccessTariff(checkTariff(dto.getTariffsId()));
            return ok(repository.save(packet));
        }).orElseGet(() -> notFound().build());
    }

    public ResponseEntity<?> deletePacket(Long packetId) {
        return repository.findById(packetId).map(packet -> {
            UssdCode ussdCode = packet.getUssdCode();
            repository.delete(packet);
            ussdCodeRepository.delete(ussdCode);
            return noContent().build();
        }).orElseGet(() -> notFound().build());
    }

    //    ACTIONS
    public Set<Tariff> checkTariff(List<Long> tariffsId) {
        Set<Tariff> tariffs = new HashSet<>();
        tariffsId.forEach(tariffId -> tariffRepository.findById(tariffId).ifPresent(tariffs::add));
        return tariffs;
    }
}
