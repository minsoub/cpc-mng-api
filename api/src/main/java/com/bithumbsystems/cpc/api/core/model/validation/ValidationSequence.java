package com.bithumbsystems.cpc.api.core.model.validation;

import com.bithumbsystems.cpc.api.core.model.validation.ValidationGroups.NotEmptyGroup;
import com.bithumbsystems.cpc.api.core.model.validation.ValidationGroups.PatternCheckGroup;
import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, NotEmptyGroup.class, PatternCheckGroup.class})
public interface ValidationSequence {

}
