package com.bithumbsystems.cpc.api.core.util.message;

import java.io.IOException;
import javax.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {

  void sendWithFile(MailSenderInfo mailSenderInfo) throws IOException;

  void send(final MailSenderInfo mailSenderInfo) throws MessagingException, IOException;
}
