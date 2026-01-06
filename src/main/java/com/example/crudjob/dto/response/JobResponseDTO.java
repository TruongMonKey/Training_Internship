package com.example.crudjob.dto.response;

import com.example.crudjob.entity.enums.EJobStatus;
import com.example.crudjob.entity.enums.EJobType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobResponseDTO {

    private Long id;
    private String title;
    private String company;
    private String location;
    private Integer salary;
    private EJobType type;
    private EJobStatus status;
    private String description;

}
