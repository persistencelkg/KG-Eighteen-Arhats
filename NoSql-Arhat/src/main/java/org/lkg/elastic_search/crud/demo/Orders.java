package org.lkg.elastic_search.crud.demo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Orders implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;

    private String name;

    private long age;

    private BigDecimal fee;

    private Date startTime;

}
