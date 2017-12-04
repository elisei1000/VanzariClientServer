package vanzari.domain;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by elisei on 30.11.2017.
 */
@Entity
@Table(name="STOC")
public class Stoc implements HasId<Integer>, Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name="PRODUS_ID", referencedColumnName = "PRODUS_ID", unique = true)
    private Produs produs;

    @Column(name="CANTITATE")
    private float cantitate;

    public Stoc(){}

    public Stoc(Integer id, Produs produs, float cantitate){
        this.id = id;
        this.produs = produs;
        this.cantitate = cantitate;
    }

    public Produs getProdus() {
        return produs;
    }

    public void setProdus(Produs produs) {
        this.produs = produs;
    }

    public float getCantitate() {
        return cantitate;
    }

    public void setCantitate(float cantitate) {
        this.cantitate = cantitate;
    }

    public Integer getId(){
        return id;
    }

    public void setId(Integer id){
        this.id = id;
    }


}
