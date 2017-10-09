package program;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.jaxrs.JAXRSArchive;

import banking.Bank;

public class Program {
	public static void main(String[] args) {

		try { 
			// Instantiate the container
			Swarm swarm = new Swarm(args);
			// Create one or more deployments
			JAXRSArchive deployment = ShrinkWrap.create(JAXRSArchive.class);
			// Add resource to deployment
			deployment.addPackage("banking");
			deployment.addPackage("providers");
			deployment.addAllDependencies();

			// Start the swarm microserver:
			swarm.start();
			swarm.deploy(deployment);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}