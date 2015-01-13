package ferry.implmenetation;

import ferry.contract.FerryContract;
import ferry.dto.*;
import ferry.entity.*;
import ferry.eto.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class FerryManager implements FerryContract {

    private int i = 30;
    private Assembler asm = new Assembler();
    private Dissassembler dsm = new Dissassembler();
    @PersistenceContext(unitName = "ferry_ls-FerryMavenBackend_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;

    public FerryManager() {

    }
    
    @Override
    public Collection<TrafficSummary> getTrafficInformation(TrafficDetail trafficDetail) throws InvalidRouteException, NoFerriesFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TravelSummary getTravelSummary(TravelDetail travelDetail) throws NoScheduleException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReservationSummary makeReservation(ReservationDetail resDetail) throws NoSuchReservationException {
        try {
            Reservation r = new Reservation();
            Person pers = em.find(Person.class, resDetail.getReserver().getId());
            //increment every time based on previous ID
            r.setId(new BigDecimal(i)); i++;
            r.setBookerId(pers);
            r.setHasArrived('N');
            r.setReservationNumber(resDetail.getReservationSerialNumber());
            r.setTotalprice(resDetail.getTotalPrice());

            Collection<PassengerDTO> passengers = resDetail.getPassengers();
            Collection<AbstractVehicle> vehicles = resDetail.getVehicles();
            Collection<ReservationTravelingEntity> colRte = new ArrayList<ReservationTravelingEntity>();
            for (PassengerDTO p : passengers) {
                Passanger pas = new Passanger();
                pas.setPassangerName(p.getName());
                em.persist(pas);
                ReservationTravelingEntity rte = new ReservationTravelingEntity();
                rte.setId(new BigDecimal(i)); i++;
                rte.setReservationId(r);
                TravelingEntity te = new TravelingEntity();
                te.setId(new BigDecimal(i)); i++;
                te.setIsresident(pers.getIsresident());
                te.setPassangerreference(pas.getId().toBigInteger());
                rte.setTravelingEntityId(te);
                em.persist(te);
                em.persist(rte);
                colRte.add(rte);
            }
            for (AbstractVehicle v : vehicles) {
                Vehicle veh = new Vehicle();
                veh.setRegno(v.getLicensePlate());
                em.persist(veh);
                ReservationTravelingEntity rte = new ReservationTravelingEntity();
                rte.setId(new BigDecimal(i)); i++;
                rte.setReservationId(r);
                TravelingEntity te = new TravelingEntity();
                te.setId(new BigDecimal(i)); i++;
                te.setIsresident(pers.getIsresident());
                te.setVehiclereference(veh.getId().toBigInteger());
                rte.setTravelingEntityId(te);
                em.persist(te);
                em.persist(rte);
                colRte.add(rte);
            }
            r.setReservationTravelingEntityCollection(colRte);

            String departurePort = resDetail.getDeparturePort();
            Date departureDate = resDetail.getDepartureTime();
            String destinationPort = resDetail.getDestinationPort();
            Harbour harb = (Harbour) em.createNamedQuery("Harbour.findByName").setParameter("name", departurePort).getSingleResult();
            for (Route route : harb.getRouteCollection()) {
                if (route.getIdDestination().getName().equals(destinationPort)) {
                    for (Departure d : route.getDepartureCollection()) {
                        if (d.getDepartureDate().equals(departureDate)) {
                            r.setDepartureId(d);
                        }
                    }
                }
            }
            em.persist(r);
            return asm.createReservationSummary(r);
        } catch (Exception e) {
            throw new NoSuchReservationException(resDetail.getReserver().getId(), "Error making the reservation");
        }
    }

    @Override
    public boolean deleteReservation(int reservationId) throws NoSuchReservationException {
        try {
            Reservation r = em.find(Reservation.class, reservationId);
            Collection<ReservationTravelingEntity> crte = em.createNamedQuery("ReservationTravelingEntity.findByReservationId").setParameter("rid", reservationId).getResultList();
            for (ReservationTravelingEntity rte : crte) {
                TravelingEntity te = em.find(TravelingEntity.class, rte.getTravelingEntityId());
                if (te.getPassangerreference().longValue() >= 0) {
                    Passanger passanger = em.find(Passanger.class, te.getPassangerreference());
                    em.remove(passanger);
                } else {
                    if (te.getVehiclereference().longValue() >= 0) {
                        Vehicle veh = em.find(Vehicle.class, te.getVehiclereference());
                        em.remove(veh);
                    }
                }
                em.remove(rte);
            }
            em.remove(r);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public ReservationSummary editReservation(ReservationDetail resDetail) throws NoSuchReservationException {
        Reservation r = (Reservation) em.createNamedQuery("Reservation.findByReservationNumber").setParameter("reservationNumber", resDetail.getReservationSerialNumber()).getSingleResult();
        if (!resDetail.getDepartureTime().equals(r.getDepartureId().getDepartureDate())) {

        }
        Collection<ReservationTravelingEntity> crte = em.createNamedQuery("ReservationTravelingEntity.findByReservationId").setParameter("rid", r.getId().longValue()).getResultList();
        for (ReservationTravelingEntity rte : crte) {
            TravelingEntity te = em.find(TravelingEntity.class, rte.getTravelingEntityId());
            if (te.getPassangerreference().longValue() >= 0) {
                Passanger passanger = em.find(Passanger.class, te.getPassangerreference());
                boolean control = false;
                for (PassengerDTO pdto : resDetail.getPassengers()) {
                    if (passanger.getId().equals(pdto.getId())) {
                        control = true;
                        if (!passanger.getPassangerName().equals(pdto.getName())) {
                            passanger.setPassangerName(pdto.getName());
                        }
                    }
                }
                if (!control) {
                    //save the new passanger into the reservation
                }
            } else {
                if (te.getVehiclereference().longValue() >= 0) {
                    Vehicle veh = em.find(Vehicle.class, te.getVehiclereference());
                    //edit vehicle and save new vehicles in the db
                }
            }
            em.remove(rte);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isUserResident(AbstractAccount accDTO) {
        Person p = em.find(Person.class, accDTO.getId());
        return p.getIsresident().equals('Y');
    }

    @Override
    public boolean makeAccount(AccountDetail accDetail) throws InvalidAccountException {
        int result = em.createNamedQuery("Person.findByEmail").setParameter("email", accDetail.getEmail()).getFirstResult();
        if (result == 0) {
            em.persist(dsm.createPerson(accDetail));
            return true;
        } else {
            throw new InvalidAccountException("Email address already exists");
        }
    }

    @Override
    public AccountDetail login(String email, String password) throws NoSuchAccountException {
        try {
            Person p = (Person) em.createNamedQuery("Person.findByEmail").setParameter("email", email).getSingleResult();
            if (p.getPassword().equalsIgnoreCase(password)) {
                return asm.createAccountDetail(p);
            } else {
                throw new NoSuchAccountException("Login failed");
            }
        } catch (Exception e) {
            throw new NoSuchAccountException("Login failed");
        }
    }

    @Override
    public AccountSummary deleteAccount(AccountDetail accDetail) throws NoSuchAccountException {
        try {
            Person p = em.find(Person.class, accDetail.getId());
            em.remove(p);
            return asm.createAccountSummary(p);
        } catch (Exception e) {
            throw new NoSuchAccountException("Login failed");
        }
    }

    @Override
    public boolean editAccount(AccountDetail accDetail) throws NoSuchAccountException {
        try {
            Person p = em.find(Person.class, accDetail.getId());
            p.setAddress(accDetail.getAddress());
            p.setCpr(accDetail.getCprNo());
            p.setEmail(accDetail.getEmail());
            p.setPersonName(accDetail.getName());

            em.merge(p);
            return true;
        } catch (Exception e) {
            throw new NoSuchAccountException("Login failed");
        }
    }

    @Override
    public AccountDetail showAccount(AbstractAccount acc) throws NoSuchAccountException {
        try {
            Person p = em.find(Person.class, acc.getId());
            return asm.createAccountDetail(p);
        } catch (Exception e) {
            throw new NoSuchAccountException("Login failed");
        }
    }

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    public void persist(Object object) {
        try {
            utx.begin();
            em.persist(object);
            utx.commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }
    }

    public void persist1(Object object) {
        try {
            utx.begin();
            em.persist(object);
            utx.commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }
    }

    public void persist2(Object object) {
        try {
            utx.begin();
            em.persist(object);
            utx.commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }
    }
}
