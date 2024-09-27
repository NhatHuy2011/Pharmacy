package com.project.pharmacy.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateBio {
    String id;
    String username;
    String password;
    String fisrtname;
    String lastname;
    Date dob;
    String sex;
    int phone_number;
    String email;
}
