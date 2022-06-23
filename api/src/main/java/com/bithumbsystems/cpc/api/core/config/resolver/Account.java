package com.bithumbsystems.cpc.api.core.config.resolver;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Account {
  private final String accountId;

  private final Set<String> roles;
}
