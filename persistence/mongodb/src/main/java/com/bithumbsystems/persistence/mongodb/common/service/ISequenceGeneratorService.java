package com.bithumbsystems.persistence.mongodb.common.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ISequenceGeneratorService {
  Long generateSequence(final String sequenceName)
      throws InterruptedException, ExecutionException, TimeoutException;
}
