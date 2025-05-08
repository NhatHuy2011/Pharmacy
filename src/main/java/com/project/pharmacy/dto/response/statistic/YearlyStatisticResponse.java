package com.project.pharmacy.dto.response.statistic;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YearlyStatisticResponse {
    Integer month;
    Long money;
}
