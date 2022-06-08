package com.bithumbsystems.persistence.mongodb.care.entity.sequence;

import com.bithumbsystems.persistence.mongodb.care.entity.LegalCounseling;
import com.bithumbsystems.persistence.mongodb.common.service.ISequenceGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LegalCounselingModelListener extends AbstractMongoEventListener<LegalCounseling> {

  private ISequenceGeneratorService sequenceGenerator;

  @Autowired
  public LegalCounselingModelListener(ISequenceGeneratorService sequenceGenerator) {
    this.sequenceGenerator = sequenceGenerator;
  }

//  @Override
//  public void onBeforeConvert(BeforeConvertEvent<LegalCounseling> event) {
//    try {
//      if (event.getSource().getId() == null) {
//        event.getSource().setId(sequenceGenerator.generateSequence(LegalCounseling.SEQUENCE_NAME));
//      }
//    } catch (InterruptedException | ExecutionException | TimeoutException e) {
//      log.error("Error:{}", e.getMessage());
//    }
//  }
}
