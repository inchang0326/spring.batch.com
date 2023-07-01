package com.example.spring_batch.common;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

public class MyJobParametersValidator implements JobParametersValidator {
    @Override
    public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
        if (jobParameters.getString("date") == null) {
            throw new JobParametersInvalidException("date parameters is not found ");
        }
    }
}
