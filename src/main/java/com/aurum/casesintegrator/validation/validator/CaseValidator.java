package com.aurum.casesintegrator.validation.validator;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.aurum.casesintegrator.domain.Case;
import com.aurum.casesintegrator.service.CaseService;
import com.aurum.casesintegrator.validation.constraint.ValidLegalCase;

public class CaseValidator implements ConstraintValidator<ValidLegalCase, String> {

    @Autowired
    private Validator validator;

    @Autowired
    private CaseService caseService;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        final List<Case> cases = this.caseService.getExtractedCasesFrom(s);
        if (CollectionUtils.isEmpty(cases)) {
            return false;
        }

        return cases.stream().noneMatch(singleCase -> {
            final Set<ConstraintViolation<Case>> fieldsConstraintViolations = this.validator.validate(singleCase);
            final boolean thereAreFailedFields = !CollectionUtils.isEmpty(fieldsConstraintViolations);
            if (thereAreFailedFields) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                fieldsConstraintViolations.forEach(violation -> constraintValidatorContext
                        .buildConstraintViolationWithTemplate(violation.getMessageTemplate())
                        .addConstraintViolation()
                );
            }
            return thereAreFailedFields;
        });
    }

}
