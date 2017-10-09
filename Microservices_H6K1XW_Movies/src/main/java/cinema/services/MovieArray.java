package cinema.services;

import java.util.ArrayList;
import java.util.List;

//@XmlRootElement(name="movies")
public class MovieArray {

	//@XmlElement(nillable=true)
	public List<MovieEntity> movie;
	
	public MovieArray(){
		movie = new ArrayList<MovieEntity>();
	}
	
	public MovieArray(List<MovieEntity> list){
		movie = list;
	}
}
