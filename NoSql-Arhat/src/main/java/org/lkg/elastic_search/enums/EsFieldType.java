package org.lkg.elastic_search.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum EsFieldType {

    KEYWORD("keyword", new Class[]{String.class, Character.class, char.class}),
    TEXT("text", new Class[]{String.class, Character.class, char.class}),
    INTEGER("integer", new Class[]{int.class, Integer.class, Short.class}) ,
    LONG("long", new Class[]{long.class, Long.class}) ,
    DATE("date", new Class[]{LocalDate.class, LocalDateTime.class, Date.class, java.sql.Date.class}) ,
    FLOAT("float", new Class[]{float.class, Float.class, Double.class, double.class}) ,
    SCALE_FLOAT("scaled_float", new Class[]{BigDecimal.class}) ,
    NESTED("nested", new Class[]{EsObject[].class}) ,
    OBJECT("object", new Class[]{EsObject.class}) ,
    ;


    private final String esType;
    private final Class<?>[] aClass;


    @Data
    private static class EsObject {
        private Object properties;
    }


    public static String getEsType(Class<?> classz) {
        Optional<EsFieldType> first = Stream.of(values()).filter(ref -> Stream.of(ref.getAClass()).anyMatch(classz::isAssignableFrom)).findFirst();
        return first.isPresent() ? first.get().getEsType() : TEXT.getEsType();
    }


}
