package cinema.services;

//@XmlRootElement(name="movie")
//@XmlAccessorType(XmlAccessType.FIELD)
public class MovieEntity{

	//@XmlTransient
	public int id;
	
	//@XmlElement(nillable=true)
	public String title;
	
	//@XmlElement(nillable=true)
	public Integer year;
	
	//@XmlElement(nillable=true)
	public String director;
	
	//@XmlElement(required=false)
	public String[] actor = {};
}
