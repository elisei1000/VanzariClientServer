package vanzari.domain;

import javax.persistence.*;

/**
 * Created by elisei on 30.11.2017.
 */
@Entity
@Table(name="FACTURA")
public class Factura implements HasId<Integer> {
    public Factura(Integer id, String nume, Vanzare vanzare, Float sumaTotala) {
        this.id = id;
        this.nume = nume;
        this.vanzare = vanzare;
        this.sumaTotala = sumaTotala;
    }

    public Factura(){}

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Vanzare getVanzare() {
        return vanzare;
    }

    public void setVanzare(Vanzare vanzare) {
        this.vanzare = vanzare;
    }

    public Float getSumaTotala() {
        return sumaTotala;
    }

    public void setSumaTotala(Float sumaTotala) {
        this.sumaTotala = sumaTotala;
    }


    @Id
    @Column(name="FACTURA_ID")
    @GeneratedValue
    private Integer id;

    @Column(name="NAME")
    private String nume;

    @OneToOne
    @JoinColumn(name="VANZARE_ID")
    private Vanzare vanzare;

    @Column(name="SUMA_TOTALA")
    private Float sumaTotala;

}
