package space.shefer.receipt.rest.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "merchant")
@Data
public class Merchant {

  @Id
  @SequenceGenerator(name="merchant_id_seq",sequenceName="merchant_id_seq", allocationSize=1)
  @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="merchant_id_seq")
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "inn", nullable = false)
  private String inn;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "last_update_time", nullable = false)
  private Date lastUpdateTime;

  @Column(name = "address", nullable = false)
  private String address;

}