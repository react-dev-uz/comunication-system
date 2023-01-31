package uz.pdp.comunicationsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.pdp.comunicationsystem.entity.Owner;
import uz.pdp.comunicationsystem.entity.Packet;
import uz.pdp.comunicationsystem.entity.Role;
import uz.pdp.comunicationsystem.entity.UssdCode;
import uz.pdp.comunicationsystem.entity.enums.PacketType;
import uz.pdp.comunicationsystem.repository.OwnerRepository;
import uz.pdp.comunicationsystem.repository.PacketRepository;
import uz.pdp.comunicationsystem.repository.RoleRepository;
import uz.pdp.comunicationsystem.repository.UssdCodeRepository;

import java.util.List;
import java.util.Set;

import static uz.pdp.comunicationsystem.entity.enums.RoleName.*;

/**
 * @author Dilshod Fayzullayev
 * @version 1.0
 */
@SpringBootApplication
public class ComunicationSystemApplication implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final OwnerRepository ownerRepository;
    private final UssdCodeRepository ussdCodeRepository;
    private final PacketRepository packetRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ComunicationSystemApplication(RoleRepository roleRepository, OwnerRepository ownerRepository, UssdCodeRepository ussdCodeRepository, PacketRepository packetRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.ownerRepository = ownerRepository;
        this.ussdCodeRepository = ussdCodeRepository;
        this.packetRepository = packetRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(ComunicationSystemApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        roleSaveDB();
        ownerSaveDB();
        ussdCodeWithPacketSaveDB();
    }

    public synchronized void roleSaveDB() {
        roleRepository.saveAll(Set.of(Role.build(null, ROLE_DIRECTOR), Role.build(null, ROLE_MANAGER), Role.build(null, ROLE_STAFF), Role.build(null, ROLE_CLIENT)));
    }

    public synchronized void ownerSaveDB() {
        roleRepository.findByName(ROLE_DIRECTOR).ifPresent(role ->
                ownerRepository.save(new Owner("director1919", "John", "Doe", passwordEncoder.encode("root123@"), true, Set.of(role)))
        );
    }

    public synchronized void ussdCodeWithPacketSaveDB() {
        List<UssdCode> ussdCodes = ussdCodeRepository.saveAll(Set.of(
                new UssdCode("*100#", "Joriy hisob holati"),
                new UssdCode("*101#", "Mavjud daqiqalar qoldig'i"),
                new UssdCode("*102#", "Mavjud sms qoldig'i"),
                new UssdCode("*103#", "Mavjud internet traffik qoldig'i"),
                new UssdCode("*555*1*1#", "100MB internet packet"),
                new UssdCode("*555*1*2#", "500MB internet packet"),
                new UssdCode("*555*1*3#", "1000MB internet packet"),
                new UssdCode("*555*1*4#", "1500MB internet packet"),
                new UssdCode("*111*1*1#", "100SMS internet packet"),
                new UssdCode("*111*1*2#", "500SMS sms packet"),
                new UssdCode("*111*1*3#", "1000SMS sms packet"),
                new UssdCode("*111*1*4#", "1500SMS sms packet")
        ));
        packetSaveDB(ussdCodes);
    }

    public void packetSaveDB(List<UssdCode> ussdCodes) {
        packetRepository.saveAll(Set.of(
                new Packet("Internet 100", 100, 3000D, 15, PacketType.MB, getPacket(ussdCodes, "*555*1*1#"), Set.of()),
                new Packet("Internet 500", 500, 5000D, 15, PacketType.MB, getPacket(ussdCodes, "*555*1*2#"), Set.of()),
                new Packet("Internet 1000", 1000, 9_000D, 15, PacketType.MB, getPacket(ussdCodes, "*555*1*3#"), Set.of()),
                new Packet("Internet 1500", 1500, 13_000D, 15, PacketType.MB, getPacket(ussdCodes, "*555*1*4#"), Set.of()),
                new Packet("SMS 100", 100, 1500D, 15, PacketType.SMS, getPacket(ussdCodes, "*111*1*1#"), Set.of()),
                new Packet("SMS 500", 500, 2500D, 15, PacketType.SMS, getPacket(ussdCodes, "*111*1*2#"), Set.of()),
                new Packet("SMS 1000", 1000, 5_000D, 15, PacketType.SMS, getPacket(ussdCodes, "*111*1*3#"), Set.of()),
                new Packet("SMS 1500", 1500, 7_000D, 15, PacketType.SMS, getPacket(ussdCodes, "*111*1*4#"), Set.of())
        ));
    }

    public UssdCode getPacket(List<UssdCode> ussdCodes, String code) {
        return ussdCodes.stream().filter(ussdCode -> ussdCode.getCode().equals(code)).findAny().get();
    }
}
