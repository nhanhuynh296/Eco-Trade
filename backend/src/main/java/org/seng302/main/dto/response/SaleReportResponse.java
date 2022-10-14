package org.seng302.main.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Sale Report response DTO object
 */
@Getter
@Setter
@NoArgsConstructor
public class SaleReportResponse {

    public String period;
    public LocalDate initialDate;
    public LocalDate finalDate;
    public Integer totalSales;
    public Double totalRevenue;

}
