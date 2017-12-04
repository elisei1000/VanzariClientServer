package vanzari.domain;

import java.io.Serializable;

/**
 * Created by elisei on 30.11.2017.
 */
public interface ICloneableHasId<E, ID extends Serializable>
        extends ICloneable<E> ,  HasId<ID>{
}
