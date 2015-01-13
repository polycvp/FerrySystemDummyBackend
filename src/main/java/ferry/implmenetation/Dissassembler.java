
package ferry.implmenetation;

import ferry.dto.AccountDetail;
import ferry.entity.Person;
import java.math.BigDecimal;


public class Dissassembler {

    public static Person createPerson(AccountDetail accDetail) {
        Person p = new Person(accDetail.getId(), accDetail.getName(), accDetail.getEmail(), accDetail.getPassword(), accDetail.getCprNo(), accDetail.getAddress(),'N');
        return p;
    }
    
}
