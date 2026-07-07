package com.emras.order.client.dto;
public record AddressResponse(
        Long    id,
        String  label,
        String  division,
        String  district,
        String  thana,
        String  area,
        String  houseNumber,
        String  postalCode,
        boolean isDefault
) {
    /** Formats address into a single string for storage on the order */
    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        if (houseNumber != null) sb.append(houseNumber).append(", ");
        if (area != null)        sb.append(area).append(", ");
        if (thana != null)       sb.append(thana).append(", ");
        if (district != null)    sb.append(district).append(", ");
        if (division != null)    sb.append(division);
        if (postalCode != null)  sb.append(" - ").append(postalCode);
        return sb.toString();
    }
}