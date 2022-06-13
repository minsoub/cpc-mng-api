package com.bithumbsystems.persistence.mongodb.guide.entity.sequence;

import com.bithumbsystems.persistence.mongodb.guide.entity.News;
import com.bithumbsystems.persistence.mongodb.common.service.ISequenceGeneratorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewsModelListener extends AbstractMongoEventListener<News> {

  private ISequenceGeneratorService sequenceGenerator;

  @Autowired
  public NewsModelListener(ISequenceGeneratorService sequenceGenerator) {
    this.sequenceGenerator = sequenceGenerator;
  }

  @Override
  public void onBeforeConvert(BeforeConvertEvent<News> event) {
    try {
      if (event.getSource().getId() == null) {
        event.getSource().setId(sequenceGenerator.generateSequence(News.SEQUENCE_NAME));
      }
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error:{}", e.getMessage());
    }
  }
}
