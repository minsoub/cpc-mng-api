package com.bithumbsystems.persistence.mongodb.common.service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;

import com.bithumbsystems.persistence.mongodb.common.model.entity.sequence.DatabaseSequence;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SequenceGeneratorService implements ISequenceGeneratorService {

  private ReactiveMongoOperations mongoOperations;

  @Autowired
  public SequenceGeneratorService(ReactiveMongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
  }

  @Override
  public Long generateSequence(final String sequenceName)
      throws InterruptedException, ExecutionException, TimeoutException {
    return mongoOperations.findAndModify(
          new Query(Criteria.where("_id").is(sequenceName)),
          new Update().inc("sequence", 1),
          options().returnNew(true).upsert(true),
          DatabaseSequence.class
        )
        .doOnSuccess(object -> {
          log.debug("databaseSequence is evaluated: {}", object);
        })
        .toFuture().get(3, TimeUnit.SECONDS).getSequence();
  }
}
