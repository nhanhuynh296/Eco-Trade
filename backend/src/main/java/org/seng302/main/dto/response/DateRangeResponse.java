package org.seng302.main.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Sale response date range DTO object
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
public class DateRangeResponse {

    private LocalDate startDate;
    private LocalDate endDate;

}
