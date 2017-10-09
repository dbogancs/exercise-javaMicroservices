package cinema.services;

import java.util.ArrayList;
import java.util.List;

//@XmlRootElement(name="movies")
public class MovieIdList {

	//@XmlElement(nillable=true)
	public List<Integer> id;
	
	MovieIdList(){
		id = new ArrayList<>();
	}	
	MovieIdList(List<Integer> ids){
		id = ids;
	}
}
