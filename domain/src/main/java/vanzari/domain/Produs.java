package vanzari.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by elisei on 30.11.2017.
 */
@Entity
@Table(name="PRODUS")
public class Produs implements HasId<Integer>, Serializable, ICloneable {
    @Id
    @GeneratedValue
    @Column(name = "PRODUS_ID")
    private Integer id;

    @Column(name="NUME_PRODUS")
    private String nume;

    @Column(name="PRET_UNITAR")
    private Float pretUnitar;

    @Column(name="UNITATE_DE_MASURA")
    private String unitateDeMasura;

    public Produs(){}

    public Produs(int id, String nume, float pretUnitar, String unitateDeMasura){
        this.id = id;
        this.nume = nume;
        this.pretUnitar = pretUnitar;
        this.unitateDeMasura = unitateDeMasura;
    }

    public Produs(Produs produs) {
        this.id = produs.getId();
        this.nume = produs.getNume();
        this.pretUnitar = produs.getPretUnitar();
        this.unitateDeMasura = produs.getUnitateDeMasura();
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer integer) {
        this.id = integer;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Float getPretUnitar() {
        return pretUnitar;
    }

    public void setPretUnitar(Float pretUnitar) {
        this.pretUnitar = pretUnitar;
    }

    public String getUnitateDeMasura() {
        return unitateDeMasura;
    }

    public void setUnitateDeMasura(String unitateDeMasura) {
        this.unitateDeMasura = unitateDeMasura;
    }

    public Object clone(){
        return new Produs(this);
    }
}
