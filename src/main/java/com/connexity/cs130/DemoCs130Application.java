package com.connexity.cs130;

import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.connexity.cs130.dao.UserDAO;
import com.connexity.cs130.model.User;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoCs130Application {

	public static void main(String[] args) {
		SpringApplication.run(DemoCs130Application.class, args);
		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");

		/*UserDAO userDAO = (UserDAO) context.getBean("userDAO");
		User user = new User("kevin2", "asd", 28);
		userDAO.insert(user);

		User user1 = userDAO.findByUserID("kevin");
		System.out.println(user1);*/
	}
}
