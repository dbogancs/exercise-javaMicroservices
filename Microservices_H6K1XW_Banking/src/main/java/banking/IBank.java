package banking;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import banking.Banking.ChargeCardRequest;
import banking.Banking.ChargeCardResponse;

@Path("banking")
@Consumes({MediaType.APPLICATION_JSON, "application/x-protobuf"})
@Produces({MediaType.APPLICATION_JSON, "application/x-protobuf"})
public interface IBank {

	@POST
	@Path("ChargeCard")
	ChargeCardResponse ChargeCard(ChargeCardRequest request);
}
