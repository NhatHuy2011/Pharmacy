package com.project.pharmacy.mapper;

import com.project.pharmacy.dto.request.notification.CreateNotificationRequest;
import com.project.pharmacy.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NotificationMapper {
    CreateNotificationRequest toNotificationRequest(Notification notification);
}
