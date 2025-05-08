package com.project.pharmacy.dto.response.statistic;

import java.time.LocalDateTime;

public interface DaylyStatisticResponse {
    LocalDateTime getTime();
    Long getMoney();
}
