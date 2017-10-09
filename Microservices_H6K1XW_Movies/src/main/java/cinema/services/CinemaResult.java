package cinema.services;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="result")
public class CinemaResult {
	
	@XmlElement(nillable=true)
	public int id;
	
	public CinemaResult() {
		
	}

	public CinemaResult(int resultId) {
		id = resultId;
	}
}
