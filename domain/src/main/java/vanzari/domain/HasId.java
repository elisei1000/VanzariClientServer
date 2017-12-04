package vanzari.domain;

import java.io.Serializable;

/**
 * Created by elisei on 30.11.2017.
 */
public interface HasId<ID extends Serializable>{
    ID getId();
    void setId(ID id);
}
