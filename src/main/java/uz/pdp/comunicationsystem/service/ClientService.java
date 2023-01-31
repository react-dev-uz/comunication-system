package uz.pdp.comunicationsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.comunicationsystem.entity.Client;
import uz.pdp.comunicationsystem.entity.Detalizatsiya;
import uz.pdp.comunicationsystem.entity.SimCard;
import uz.pdp.comunicationsystem.entity.Tariff;
import uz.pdp.comunicationsystem.entity.enums.Action;
import uz.pdp.comunicationsystem.entity.enums.ClientType;
import uz.pdp.comunicationsystem.entity.enums.RoleName;
import uz.pdp.comunicationsystem.payload.response.CallDTO;
import uz.pdp.comunicationsystem.payload.response.ClientDTO;
import uz.pdp.comunicationsystem.payload.response.NewSimCardDto;
import uz.pdp.comunicationsystem.payload.response.SmsDTO;
import uz.pdp.comunicationsystem.repository.*;

import java.util.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;

@Service
public class ClientService {
    private final ClientRepository repository;
    private final SimCardRepository simCardRepository;
    private final TariffRepository tariffRepository;
    private final RoleRepository roleRepository;
    private final DetalizatsiyaRepository detalizatsiyaRepository;

    @Autowired
    public ClientService(ClientRepository repository, SimCardRepository simCardRepository,
                         TariffRepository tariffRepository,
                         RoleRepository roleRepository,
                         DetalizatsiyaRepository detalizatsiyaRepository) {
        this.repository = repository;
        this.simCardRepository = simCardRepository;
        this.tariffRepository = tariffRepository;
        this.roleRepository = roleRepository;
        this.detalizatsiyaRepository = detalizatsiyaRepository;
    }

    public ResponseEntity<?> getAllClients(Integer page, Integer size, String sort, String order) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? page - 1 : 0, size > 0 ? size : 20, Sort.by(Sort.Direction.fromString(order), sort));
        return ok(repository.findAll(pageRequest));
    }

    public ResponseEntity<?> getOneClient(UUID clientId) {
        return repository.findById(clientId).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<?> addClientAndSimCard(ClientDTO dto) {
        boolean existsByPassportSerialNumber = repository.existsByPassportSerialNumber(dto.getJshir());
        return existsByPassportSerialNumber ? existClient(dto) : notExistClient(dto);
    }

    private ResponseEntity<?> notExistClient(ClientDTO dto) {
        if (simCardRepository.existsByCodeAndNumber(dto.getCode(), dto.getNumber()))
            return status(UNPROCESSABLE_ENTITY).body("Sim card already exists");
        Optional<Tariff> optionalTariff = tariffRepository.findById(dto.getTariffId());
        if (optionalTariff.isEmpty()) return status(NOT_FOUND).body("Tariff not found");
        Tariff tariff = optionalTariff.get();
        SimCard simCard = new SimCard();
        simCard.setCode(dto.getCode());
        simCard.setNumber(dto.getNumber());
        simCard.setName(dto.getSimCardName());
        simCard.setTariff(tariff);
        simCard.setActive(true);
        Client client = new Client();
        if (dto.getAmountBalance() >= tariff.getPrice()) {
            simCard.setBalance(simCard.getBalance() - tariff.getPrice());
            simCard.setAmountMb(tariff.getMb());
            simCard.setAmountMinute(tariff.getMin());
            simCard.setAmountSMS(tariff.getSms());
        } else {
            simCard.setAmountMb(0);
            simCard.setAmountMinute(0);
            simCard.setAmountSMS(0);
        }
        if (Objects.equals(dto.getClientType(), ClientType.USER.name())) {
            client.setClientType(ClientType.USER);
        } else if (Objects.equals(dto.getClientType(), ClientType.COMPANY.name())) {
            client.setClientType(ClientType.COMPANY);
        } else {
            return status(NOT_FOUND).body("Client type not found");
        }
        return roleRepository.findByName(RoleName.ROLE_CLIENT)
                .map(role -> {
                    client.setRoles(Set.of(role));
                    client.setFirstName(dto.getFirstname());
                    client.setLastName(dto.getLastname());
                    client.setSimCards(List.of(simCard));
                    return status(CREATED).body(repository.save(client));
                })
                .orElseGet(() -> status(INTERNAL_SERVER_ERROR).build());
    }

    public ResponseEntity<?> existClient(ClientDTO dto) {
        return repository.findByPassportSerialNumber(dto.getJshir())
                .map(client -> {
                    if (simCardRepository.existsByCodeAndNumber(dto.getCode(), dto.getNumber())) {
                        return status(UNPROCESSABLE_ENTITY).body("Sim card already exists");
                    }
                    Optional<Tariff> optionalTariff = tariffRepository.findById(dto.getTariffId());
                    if (optionalTariff.isEmpty()) return status(NOT_FOUND).body("Tariff not found");
                    Tariff tariff = optionalTariff.get();
                    SimCard simCard = new SimCard();
                    simCard.setCode(dto.getCode());
                    simCard.setName(dto.getSimCardName());
                    simCard.setTariff(tariff);
                    simCard.setActive(true);
                    simCard.setNumber(dto.getNumber());
                    if (dto.getAmountBalance() >= tariff.getPrice()) {
                        simCard.setBalance(simCard.getBalance() - tariff.getPrice());
                        simCard.setAmountMb(tariff.getMb());
                        simCard.setAmountMinute(tariff.getMin());
                        simCard.setAmountSMS(tariff.getSms());
                    } else {
                        simCard.setAmountMb(0);
                        simCard.setAmountMinute(0);
                        simCard.setAmountSMS(0);
                    }

                    List<SimCard> simCards = client.getSimCards();
                    simCards.add(simCard);
                    client.setSimCards(simCards);
                    return status(CREATED).body("Sim card added");
                })
                .orElseGet(() -> notFound().build());
    }


//    public ResponseEntity<?> deleteClient(UUID clientId) {
//        return null;
//    }

    public ResponseEntity<?> getClientType() {
        return ok(ClientType.values());
    }

    public ResponseEntity<?> addSimCardForClient(NewSimCardDto dto) {
        repository.findById(dto.getClientId())
                .map(client -> {
                    if (simCardRepository.existsByCodeAndNumber(dto.getCode(), dto.getNumber()))
                        return status(UNPROCESSABLE_ENTITY).body("Sim card already exists");
                    Optional<Tariff> optionalTariff = tariffRepository.findById(dto.getTariffId());
                    if (optionalTariff.isEmpty()) return status(NOT_FOUND).body("Tariff not found");
                    Tariff tariff = optionalTariff.get();
                    try {
                        simCardRepository.save(new SimCard(
                                dto.getName(),
                                dto.getCode(),
                                dto.getNumber(),
                                true,
                                dto.getPrice(),
                                tariff.getMb(),
                                tariff.getSms(),
                                tariff.getMin(),
                                tariff,
                                client
                        ));
                        return status(CREATED).body("Sim card added");
                    } catch (Exception e) {
                        return badRequest().body(e.getMessage());
                    }
                })
                .orElseGet(() -> status(NOT_FOUND).body("Client not found"));
        return null;
    }

    public ResponseEntity<?> call(CallDTO dto) {
        Optional<SimCard> optionalSimCard = simCardRepository.findByCodeAndNumber(dto.getCode(), dto.getNumber());
        if (optionalSimCard.isEmpty()) return status(NOT_FOUND).body("Sim card not found");
        SimCard simCard;
        try {
            simCard = (SimCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return status(BAD_REQUEST).body("User not authenticated sim card token");
        }
        Tariff tariff = simCard.getTariff();
        double minute = Math.ceil(dto.getSeconds() / 60); // kompaniya holatida 1.5 daqiqa ham 2 daqiqa uchun hisobdan mablag' yechadi
        if (simCard.isActive() && !simCard.isDebit() && (simCard.getAmountMinute() >= minute) || simCard.getBalance() >= tariff.getMinCost() * (minute / 60)) {
            if (simCard.getAmountMinute() >= minute) {
                simCard.setAmountMinute(simCard.getAmountMinute() - minute);
                detalizatsiyaRepository.save(new Detalizatsiya(Action.ACTION_MINUTE, minute, simCard));
            } else if (simCard.getBalance() >= tariff.getMinCost() * minute) {
                simCard.setBalance(simCard.getBalance() - tariff.getMinCost() * minute);
                detalizatsiyaRepository.save(new Detalizatsiya(Action.ACTION_MINUTE, minute, simCard, simCard.getBalance() - tariff.getMinCost() * minute));
            } else {
                simCard.setDebit(true);
                return status(PAYMENT_REQUIRED).body("You don't have enough funds left in your account");
            }
            simCardRepository.save(simCard);
            return ok("Call ended");
        } else {
            return badRequest().body("You don't have enough funds left in your account or not active");
        }
    }

    public ResponseEntity<?> sendSMS(SmsDTO dto) {
        return simCardRepository.findByCodeAndNumber(dto.getCode(), dto.getNumber())
                .map(sim -> {
                    SimCard simCard;
                    try {
                        simCard = (SimCard) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    } catch (Exception e) {
                        return badRequest().build();
                    }
                    Tariff tariff = simCard.getTariff();
                    Double amount = 0D;
                    if (simCard.getAmountSMS() > 0 && !simCard.isDebit() && simCard.isActive()) {
                        simCard.setAmountSMS(simCard.getAmountSMS() - 1);
                    } else if (simCard.getAmountSMS() == 0 && simCard.getBalance() >= tariff.getSmsCost()) {
                        simCard.setBalance(simCard.getBalance() - tariff.getSmsCost());
                        amount = tariff.getSmsCost();
                    } else if (simCard.getBalance() <= 0 && simCard.getAmountSMS() == 0) {
                        return status(BAD_REQUEST).body("Not enough money");
                    }
                    detalizatsiyaRepository.save(new Detalizatsiya(Action.ACTION_SMS, amount, simCard));
                    simCardRepository.save(simCard);
                    return ok("SMS sent");
                })
                .orElseGet(() -> notFound().build());
    }

}
