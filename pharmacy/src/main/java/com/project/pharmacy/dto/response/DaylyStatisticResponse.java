package com.project.pharmacy.dto.response;

import java.time.LocalDateTime;

public interface DaylyStatisticResponse {
    LocalDateTime getTime();
    Long getMoney();
}
