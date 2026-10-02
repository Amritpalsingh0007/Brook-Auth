package com.akal.Brook_Auth.eventProducer;

import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfoEvent {
    private int userId;
    private String firstname;
    private String lastname;
    private String emailId;
}
