import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.mgl.caf.loaniq.rest.health.ApiSocketHealthCheck;
import com.mgl.caf.loaniq.rest.resources.RootResource;
import com.mgl.caf.loaniq.rest.resources.RunWizardResource;
import com.mgl.caf.loaniq.rest.socket.ApiSocketConfiguration;
import com.mgl.caf.loaniq.rest.socket.ApiSocketManaged;
import io.dropwizard.Application;
import com.yunspace.dropwizard.jersey.jackson.xml.JacksonXMLMessageBodyProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class LoanIQAPIService extends Application<LoanIQAPIServiceConfiguration> {

    public static final String API_ABOUT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<!DOCTYPE About SYSTEM \"About.dtd\">\n" +
            "<About/>\n";

    public static void main(String[] args) throws Exception {
        System.out.println("starting LoanIQ API Service");
    	new LoanIQAPIService().run(args);
    }

    @Override
    public String getName() {
        return "loaniq-api-service";
    }

	@Override
	public void initialize(Bootstrap<LoanIQAPIServiceConfiguration> bootstrap) {

	}

	@Override
	public void run(LoanIQAPIServiceConfiguration config, Environment env)
			throws Exception {

        ApiSocketConfiguration apiSocketConfig = config.getLoaniqApi();

        // create new api socket and manage as part of environment lifecycle
        ApiSocketManaged apiSocketManaged = new ApiSocketManaged(apiSocketConfig.getEnvironment(), apiSocketConfig.getHost(), apiSocketConfig.getPort());
        env.lifecycle().manage(apiSocketManaged);

        // add jackson xml provider
        env.jersey().register(new JacksonXMLMessageBodyProvider(new XmlMapper(), env.getValidator()));

        // add health checks
        env.healthChecks().register("socket", new ApiSocketHealthCheck(apiSocketManaged));

        // add REST resources
        env.jersey().register(new RootResource(apiSocketManaged));
        env.jersey().register(new RunWizardResource(apiSocketManaged));

	}

}
