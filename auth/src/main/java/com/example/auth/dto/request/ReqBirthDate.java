package com.example.auth.dto.request;

import com.example.auth.validation.BirthDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReqBirthDate {
    @JsonFormat(pattern="yyyy-MM-dd")
    @BirthDate(message = "The birth date must be greater or equal than 18")
    private Date dateOfBirth;
}
