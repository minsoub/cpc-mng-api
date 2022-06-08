package com.bithumbsystems.persistence.mongodb.protection.model.entity.sequence;

import com.bithumbsystems.persistence.mongodb.common.service.ISequenceGeneratorService;
import com.bithumbsystems.persistence.mongodb.protection.model.entity.FraudReport;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FraudReportModelListener extends AbstractMongoEventListener<FraudReport> {

  private ISequenceGeneratorService sequenceGenerator;

  @Autowired
  public FraudReportModelListener(ISequenceGeneratorService sequenceGenerator) {
    this.sequenceGenerator = sequenceGenerator;
  }

  @Override
  public void onBeforeConvert(BeforeConvertEvent<FraudReport> event) {
    try {
      if (event.getSource().getId() == null) {
        event.getSource().setId(sequenceGenerator.generateSequence(FraudReport.SEQUENCE_NAME));
      }
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error:{}", e.getMessage());
    }
  }
}
