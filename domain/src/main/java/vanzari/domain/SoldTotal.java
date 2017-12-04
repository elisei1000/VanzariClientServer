package vanzari.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by elisei on 30.11.2017.
 */
@Table(name="SOLD_TOTAL")
@Entity
public class SoldTotal implements HasId<Integer>, Serializable {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name="SUM")
    private float totalSum;

    public SoldTotal(){}

    public SoldTotal(Integer id, float totalSum) {
        this.id = id;
        this.totalSum = totalSum;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public float getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(float totalSum) {
        this.totalSum = totalSum;
    }

}
