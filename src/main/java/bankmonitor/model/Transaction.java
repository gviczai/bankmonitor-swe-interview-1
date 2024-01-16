package bankmonitor.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*
 TODO: Using hybernate and JPA in a performance-sensitive transactional microservice is not the best selection.
 Hybernate (and all other persistence implementations) are slow, because of their complexity.
 Using a plain simple ORM framework (e.g. MyBatis) is a much better option.
 Not to mention that SQL hints can not be added to a SQL-less framework...
 https://www.postgresql.org/about/news/pg_hint_plan-v160-released-2712/
 https://docs.oracle.com/en/database/oracle/oracle-database/19/tgsql/influencing-the-optimizer.html#GUID-C558F7CF-446E-4078-B045-0B3BB026CB3C
 Of course one can add NamedNativeQuery annotations at any time to custom methods, but if once sql tweaking is necessary why even bother with JPA
 at the first place?
 BTW, JPA and MyBatis can be used simultaneously.
 They both use Spring's transaction mechanism, so they can share the same transaction and jdbc connection instance.
 http://mybatis.org/spring/
*/
@Entity
@Table(name = "transaction")
public class Transaction {

  public static final String REFERENCE_KEY = "reference";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  /*
  TODO: Timestamps should be stored as UTC - if one uses localdatetime,
  Europe/Budapest times will be stored in db, which will cause malfunction
  around the time skips at daylight saving changes because the daylight
  information is lost. (The time range 02:00-03:00 is repeated twice
  in the autumn, so for example a 02:13 database value is ambiguous.)
  Other systems may also have problems when interpreting data read from
  database.
  If localdatetime is stored in the db, an additional column is required to
  store whether it is 02:13 CEST or 02:13 CET.
  However, storing UTC is straightforward, simply use OffsetDateTime as the
  datatype, the db driver will handle the necessary conversion.
  Probably zoneddatetime is sufficient enough for the driver, but
  offsetdatetime is the de facto standard preferred both by json and xml,
  so it is the best to stick to that everywhere.

     private OffsetDateTime timestamp; // timeStamp = Instant.now().atZone(ZoneId.of("Europe/Budapest")).toOffsetDateTime();
  */
  @Column(name = "created_at")
  private LocalDateTime timestamp;

  @Column(name = "data")
  private String data;

  public int getId() {
      return this.id;
  }

  public void setId(int id) {
      this.id = id;
  }

  public LocalDateTime getTimestamp() {
      return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
      this.timestamp = timestamp;
  }

  public String getData() {
      return this.data;
  }

  public void setData(String data) {
      this.data = data;
  }

  public Transaction data(String data) {
      this.data=data;
      return this;
  }

}