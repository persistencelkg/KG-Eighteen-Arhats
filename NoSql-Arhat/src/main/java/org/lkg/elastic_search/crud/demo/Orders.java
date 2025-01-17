package org.lkg.elastic_search.crud.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.lkg.elastic_search.enums.EsDoc;
import org.lkg.elastic_search.enums.TextIndex;
import org.lkg.utils.DateTimeUtils;

import java.io.Serializable;
import java.util.Date;

@Data
@EsDoc(uniqueKey = "id")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Orders implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private Long age;

    @JsonFormat(pattern = DateTimeUtils.YYYY_MM_DD_HH_MM_SS_SSS, timezone = DateTimeUtils.TIME_ZONE)
    private Date startTime;

    @TextIndex
    private String text;
}
