
package ferry.implmenetation;

import ferry.dto.AccountDetail;
import ferry.dto.AccountSummary;
import ferry.dto.ReservationSummary;
import ferry.entity.Person;
import ferry.entity.Reservation;


public class Assembler {

    public static AccountDetail createAccountDetail(Person p) {
        AccountDetail acc = new AccountDetail(p.getId(), p.getCpr(), p.getPassword(), p.getPersonName(), p.getEmail(), p.getAddress());
        return acc;
    }

    public static AccountSummary createAccountSummary(Person p) {
        AccountSummary acc = new AccountSummary(p.getId().intValue(), p.getCpr(),p.getPersonName(), p.getEmail(), p.getAddress());
        return acc;
    }

    public static ReservationSummary createReservationSummary(Reservation r) {
        ReservationSummary res = new ReservationSummary(r.getReservationNumber(), null, r.getTotalprice().toString());
        return res;
    }
    
}
