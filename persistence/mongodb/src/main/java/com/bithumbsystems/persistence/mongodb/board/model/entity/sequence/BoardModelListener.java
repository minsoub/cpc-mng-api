package com.bithumbsystems.persistence.mongodb.board.model.entity.sequence;

import com.bithumbsystems.persistence.mongodb.board.model.entity.Board;
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
public class BoardModelListener extends AbstractMongoEventListener<Board> {

  private ISequenceGeneratorService sequenceGenerator;

  @Autowired
  public BoardModelListener(ISequenceGeneratorService sequenceGenerator) {
    this.sequenceGenerator = sequenceGenerator;
  }

  @Override
  public void onBeforeConvert(BeforeConvertEvent<Board> event) {
    try {
      if (event.getSource().getId() == null) {
        event.getSource().setId(sequenceGenerator.generateSequence(Board.SEQUENCE_NAME));
      }
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      log.error("Error:{}", e.getMessage());
    }
  }
}
