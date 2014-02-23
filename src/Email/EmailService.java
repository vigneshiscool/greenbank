package Email;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {

	public String sendEMail(String emailTo, String subject, String body)
			throws AddressException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(
								"greenbankltd@gmail.com", "redCOP123");
					}
				});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("vigneshiscool@gmail.com"));
		message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(emailTo));
		message.setSubject(subject);
		message.setText(body);

		Transport.send(message);

		System.out.println("Done");

		return "Done";
	}
}