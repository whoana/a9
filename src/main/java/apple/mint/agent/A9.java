package apple.mint.agent;
import java.io.File;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class A9 {

	private static ConfigurableApplicationContext context;

	public static void restart() {
  
		ApplicationArguments args = context.getBean(ApplicationArguments.class);
		context.close();
		context = SpringApplication.run(A9.class, args.getSourceArgs());

		// Thread thread = new Thread(() -> {
		// 	context.close();
		// 	context = SpringApplication.run(A9.class, args.getSourceArgs());
		// });		

		// thread.setDaemon(false);
		// thread.start();
	} 

	public static void main(String[] args) {		
		// context = SpringApplication.run(A9.class, args);
		// context = sab.run(args);
		context = iwannadie().run(args);
	}

	public static SpringApplicationBuilder iwannadie(){
		SpringApplicationBuilder sab = new SpringApplicationBuilder(A9.class);
		String iwannadie = System.getProperty("apple.mint.home", ".")
								 .concat(File.separator)
								 .concat("iwannadie.pid");
		
		// try {
		// 	System.out.println("Iwannadie:" + iwannadie);
		// 	Thread.sleep(5000);
		// } catch (InterruptedException e) {
		// }								 

		sab.build().addListeners(new ApplicationPidFileWriter(iwannadie));
		return sab;
	}
}
