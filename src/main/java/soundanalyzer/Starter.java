package soundanalyzer;
import java.awt.EventQueue;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import soundanalyzer.gui.MainWindow;

@SpringBootApplication
public class Starter {
	
	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = new SpringApplicationBuilder(Starter.class)
                .headless(false).run(args);

		EventQueue.invokeLater(() -> {
           MainWindow window = new MainWindow();
           window.setVisible(true);
        });
	}
}
