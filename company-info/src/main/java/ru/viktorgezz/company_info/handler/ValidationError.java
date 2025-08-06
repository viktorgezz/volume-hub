package ru.viktorgezz.company_info.handler;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ValidationError {
    private String field;
    private String message;
    private String code;
}
