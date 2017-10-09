package banking;

import java.io.InputStream;

import javax.ws.rs.core.Response;

import banking.Banking.ChargeCardRequest;
import banking.Banking.ChargeCardResponse;

public class Bank implements IBank {

	
	private boolean isValidCharge(String cardNumber, int amount){
		return (cardNumber.length() % 2 == 0 && amount > 0);
	}
	
	private ChargeCardResponse buildChargeCardResponse(boolean result){
		ChargeCardResponse.Builder builder = ChargeCardResponse.getDefaultInstance().toBuilder();
		builder.setSuccess(result);
		return builder.build();
	}
	
	public ChargeCardResponse ChargeCard(ChargeCardRequest request) {
		System.out.println("ChargeCard starts");
		try {
			// Deserializing the protobuf request:
			String cardNumber = request.getCardNumber();
			int amount = request.getAmount();

			// Serializing the protobuf response:
			ChargeCardResponse response =
					buildChargeCardResponse(isValidCharge(cardNumber, amount));

			return response;
		} catch (Exception e) {
			e.printStackTrace();
		} // Return server error if there was an error:
		return null;
	}

}
