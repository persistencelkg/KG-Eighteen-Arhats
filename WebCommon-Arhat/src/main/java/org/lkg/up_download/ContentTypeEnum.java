package org.lkg.up_download;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum ContentTypeEnum {

    /**
     * <ul>
     *     <li>form-data:  Content-MeterTypeEnum: multipart/form-data; boundary=--------------------------647477930321132028113971</li>
     *     <li>x-www-form-urlencoded:  Content-MeterTypeEnum: application/x-www-form-urlencoded</li>
     *     <li>raw: Content-MeterTypeEnum: application/json</li>
     * </ul>
     */
    EXCEL("application/vnd.ms-excel"),
    ZIP("application/octet-stream");
    private final String contentTypeValue;

    public String exchangeName(String originFileName) {
        if (Objects.equals(contentTypeValue, ZIP.getContentTypeValue())) {
            if (!originFileName.endsWith(".zip")) {
                originFileName += ".zip";
            }
        } else if (Objects.equals(contentTypeValue, EXCEL.getContentTypeValue())) {
            if (!(originFileName.endsWith(".xlsx") || originFileName.endsWith(".xls") || originFileName.endsWith(".csv"))) {
                originFileName += ".xls";
            }
        }

        return originFileName;
    }
}