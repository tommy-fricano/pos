package com.pos.pos;

import com.pos.pos.server.Server;
import com.pos.pos.view.frame.PosFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.swing.*;

@SpringBootApplication()
@EnableJpaRepositories
public class PosApplication extends JFrame{

	public static void main(String... args) {
		ConfigurableApplicationContext context = createApplicationContext(args);
		displayPosFrame(context);

		try{
			Server server = new Server();
			server.start(4040);
		}
		catch (Exception e){
			e.getStackTrace();
		}
	}

	private static ConfigurableApplicationContext createApplicationContext(String... args) {
		return new SpringApplicationBuilder(PosApplication.class)
				.headless(false)
				.run(args);
	}

	private static void displayPosFrame(ConfigurableApplicationContext context) {
		SwingUtilities.invokeLater(() -> {
			PosFrame posFrame = context.getBean(PosFrame.class);
			posFrame.setupFrame();
			posFrame.setVisible(true);
		});
	}

}
