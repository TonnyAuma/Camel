package CamelProject.CamelProject;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class App {
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		try {
			context.addComponent("activemq",
					ActiveMQComponent.activeMQComponent("vm://localhost?broker.persistent=false"));
			context.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from("activemq:queue:test.queue").to("stream:out");

				}
			});
			// let me see if this comment is tracked
			// This is a timer for the that prints a message
			context.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from("timer://myTimer?period=2000").setBody()
							.simple("Hello World Camel fired at ${header.firedTime}").to("stream:out");
				}
			});
			context.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from("file:C:\\datafiles\\input?noop=false") // &recursive=true" if there are sub directories
							// .marshal(new PdfTextDataFormat())
							.to("file:C:\\datafiles\\output");
				}
			});

			/*
			 * context.addRoutes(new RouteBuilder() {
			 * 
			 * @Override public void configure() throws Exception { // TODO Auto-generated
			 * method stub
			 * rest("/say").get("http://dummy.restapiexample.com/api/v1/employee/22356").to(
			 * "direct:hello") .consumes("application/json")
			 * .to("file:C:\\datafiles\\output\\Camel File.txt");
			 * 
			 * } });
			 */
			ProducerTemplate template = context.createProducerTemplate();
			context.start();
			template.sendBody("activemq:test.queue", "Example Thread that works after 1 second as scheduled");
			Thread.sleep(10000);

		} finally {
			context.stop();
		}
	}
}
