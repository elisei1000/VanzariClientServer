package vanzari.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by elisei on 30.11.2017.
 */
@Entity
@Table(name="VANZARE")
public class Vanzare implements HasId<Integer>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="VANZARE_ID")
    private Integer id = 0;

    @Column(name="DATA")
    private Date date;

    @OneToOne
    @JoinColumn(name="PRODUS_ID")
    private Produs produs;

    @Column(name="CANTITATE")
    private Float cantitate;

    public Vanzare(Integer id, Date date, Produs produs, Float cantitate) {
        this.id = id;
        this.date = date;
        this.produs = produs;
        this.cantitate = cantitate;
    }

    public Vanzare(){}

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Produs getProdus() {
        return produs;
    }

    public void setProdus(Produs produs) {
        this.produs = produs;
    }

    public Float getCantitate() {
        return cantitate;
    }

    public void setCantitate(Float cantitate) {
        this.cantitate = cantitate;
    }
}
