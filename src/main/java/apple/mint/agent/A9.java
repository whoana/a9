package apple.mint.agent;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class A9 {

	private static ConfigurableApplicationContext context;

	public static void restart() {

		ApplicationArguments args = context.getBean(ApplicationArguments.class);

		Thread thread = new Thread(() -> {
			context.close();
			context = SpringApplication.run(A9.class, args.getSourceArgs());
		});

		thread.setDaemon(false);
		thread.start();
	}

	public static void main(String[] args) {

		// context = MySpringApplication.run(A9.class, args);
		context = SpringApplication.run(A9.class, args);
	}

}
